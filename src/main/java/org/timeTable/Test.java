package org.timeTable;

import org.timeTable.models.Lesson;
import org.timeTable.models.Year;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws IOException {
        LiteSQL.connect();

        ResultSet set = LiteSQL.onQuery("SELECT name, COUNT(*) c from course GROUP BY name HAVING COUNT(*) > 1");

        try {
            while (set.next()) {
                System.out.println(set.getString("name") + " " + set.getString("c"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }

    }

}
