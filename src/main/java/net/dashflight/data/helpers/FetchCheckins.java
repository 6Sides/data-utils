package net.dashflight.data.helpers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.dashflight.data.postgres.PostgresFactory;

public class FetchCheckins extends CacheableLookup<Integer, Integer> {

    public FetchCheckins() {
        super("checkin-count");
    }

    @Override
    protected CacheableLookupResult<Integer> fetchResult(Integer input) throws SQLException {
        try (Connection conn = PostgresFactory.withDefaults().getConnection()) {
            String SQL = "select count(*) as total from check_ins\n"
                    + "where check_in_date > '2020-03-02 00:00:00.000000' and\n"
                    + "      check_in_date < '2020-03-03 00:00:00.000000'";

            ResultSet res = conn.prepareStatement(SQL).executeQuery();
            res.next();
            return CacheableLookupResult.of(res.getInt("total"), 10);
        }
    }
}
