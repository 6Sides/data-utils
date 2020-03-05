package net.dashflight.data.helpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import net.dashflight.data.postgres.PostgresFactory;

public class FetchCheckins extends RefreshAheadCacheableFetcher<Integer, Map<Integer, Integer>> {

    public FetchCheckins() {
        super("checkin-count");
    }

    @Override
    protected void initialize() {
        registerClass(TreeMap.class, 12);

        for (int i = 2; i <= 24; i++) {
            this.get(i);
        }
    }

    private static final String SQL = "select total as visits, count(total) as total from (\n"
            + "   select count(*) as total\n"
            + "\n"
            + "   FROM (\n"
            + "            SELECT (current_date::date - offs)::date AS d\n"
            + "            FROM generate_series(0, (? * 7)-1, 7) AS offs\n"
            + "        ) dates\n"
            + "\n"
            + "            LEFT OUTER JOIN\n"
            + "        check_ins ON dates.d = check_ins.check_in_date_local\n"
            + "            inner join\n"
            + "        members m on check_ins.data_exchange_id = m.data_exchange_id\n"
            + "\n"
            + "   where m.membership_type != 'Club Fit Staff'\n"
            + "\n"
            + "   group by check_ins.data_exchange_id\n"
            + ") sub\n"
            + "\n"
            + "group by visits";

    @Override
    protected CacheableResult<Map<Integer, Integer>> fetchResult(Integer input) throws SQLException {
        try (Connection conn = PostgresFactory.withDefaults().getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(SQL);
            stmt.setInt(1, input);

            ResultSet res = stmt.executeQuery();

            Map<Integer, Integer> result = new TreeMap<>();
            while (res.next()) {
                result.put(res.getInt("visits"), res.getInt("total"));
            }
            return CacheableResult.of(result);
        }
    }
}
