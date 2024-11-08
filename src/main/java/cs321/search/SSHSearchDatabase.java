package cs321.search;

// import java.sql.DriverManager;
// import java.sql.SQLException;
import java.sql.*;
// import cs321.btree.BTree;
// import cs321.common.ParseArgumentException;
// import cs321.common.ParseArgumentUtils;


/**
 * The driver class for searching a Database of a B-Tree.
 */

public class SSHSearchDatabase
{

/* Method to connect to given database; this is being set to FakeData.db for smoke testing purposes */
    // public static void connect() {
    //     // connection string 
    //     var url = "jdbc:sqlite:fakedata.db";

    //     try (var conn = DriverManager.getConnection(url)) {
    //         System.out.println("Connection to SQLite has been established.");
    //     } catch (SQLException e) {
    //         System.out.println(e.getMessage());
    //     }
    // }
	
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java -jar build/libs/SSHSearchDatabase.jar --database=<database.db> --type=<table-type> --top-frequency=<max num items>\n");
            return;
        }

        String database = null;
        String type = null;
        int topFrequency = 0;

        for (String arg : args) {
            if (arg.startsWith("--database=")) {
                database = arg.substring("--database=".length());
            } else if (arg.startsWith("--type=")) {
                type = arg.substring("-type=".length());
            } else if (arg.startsWith("--top-frequency=")) {
                topFrequency = Integer.parseInt(arg.substring("--top-frequency=".length()));
            }
        }

        if (database == null || type == null || topFrequency <= 0) {
            System.out.println("Invalid arguments.\n");
            return;
        }

        String url = "jdbc:sqlite:" + database;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Connected to the database successfully!");

                String query = "SELECT * FROM your_table WHERE type = '" + type + "' ORDER BY frequency DESC LIMIT " + topFrequency;
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Frequency: " + rs.getInt("frequency"));

                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

       //connect();
    }

}
