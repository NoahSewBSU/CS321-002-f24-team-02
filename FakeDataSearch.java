package cs321.search;

import java.sql.DriverManager;
import java.sql.SQLException;
// import cs321.btree.BTree;
// import cs321.common.ParseArgumentException;
// import cs321.common.ParseArgumentUtils;


/**
 * The driver class for searching a Database of a B-Tree.
 */

public class FakeDataSearch
{

/* Method to connect to given database; this is being set to FakeData.db for smoke testing purposes */
    public static void connect() {
        // connection string 
        var url = "jdbc:sqlite:/workspaces/CS321-002-f24-team-02/fakedata.db";

        try (var conn = DriverManager.getConnection(url)) {
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	
    public static void main(String[] args) {
       connect();
    }

}
