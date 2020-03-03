package net.dashflight.data.helpers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.dashflight.data.postgres.PostgresFactory;

public class FetchCheckins extends CacheableFetcher<Integer, Integer> {

    public FetchCheckins() {
        super("checkin-count");
    }

    @Override
    protected CacheableResult<Integer> fetchResult(Integer input) throws SQLException {
        try (Connection conn = PostgresFactory.withDefaults().getConnection()) {
            String SQL = "select count(*) as total from check_ins\n"
                    + "where check_in_date > '2020-03-02 00:00:00.000000' and\n"
                    + "      check_in_date < '2020-03-03 00:00:00.000000'";

            ResultSet res = conn.prepareStatement(SQL).executeQuery();
            res.next();
            return CacheableResult.of(res.getInt("total"), 10);
        }
    }
}
