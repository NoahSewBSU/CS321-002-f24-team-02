package cs321.search;

import cs321.btree.BTree;
import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SSHSearchDatabase
{
      public static void main(String[] args)
      {
        if (args.length < 3) {
            System.out.println("Usage: java -jar build/libs/SSHSearchDatabase.jar --database=<database.db> --type=<table-type> --top-frequency=<top frequency>\n");
            return;
        }

        String database = null;
        String type = null;
        int topFrequency = 0;

        for (String arg : args) {
            if (arg.startsWith("--database=")) {
                database = arg.substring("--database=".length());
            } else if (arg.startsWith("--type=")) {
                type = arg.substring("--type=".length());
                type = type.replaceAll("-","_");
            } else if (arg.startsWith("--top-frequency=")) {
                topFrequency = Integer.parseInt(arg.substring("--top-frequency=".length()));
            }
        }

        if (database == null || type == null || topFrequency <= 0) {
            System.out.println("Invalid arguments. Try --database=<database> --type=<table-type> --topfrequency=<10/25/50>\n");
            return;
        }

        /* Smoke test to view results from args */
        // System.out.println(database+"\n");
        // System.out.println(type+"\n");
        // System.out.println(topFrequency+"\n");

        int frequencyCounter = 0;

        String orderTable = "SELECT Key, Frequency FROM " + type +
                            " ORDER BY Frequency DESC LIMIT " + topFrequency;

        String url = "jdbc:sqlite:" + database;

        try (Connection conn = DriverManager.getConnection(url)) {

          if(conn != null) {

            try(PreparedStatement pstmt = conn.prepareStatement(orderTable);

              ResultSet rs = pstmt.executeQuery()) {
              

              System.out.println("Top " + topFrequency + "highest frequencies:");
              while (rs.next()) {
                String key = rs.getString("Key");
                int frequency = rs.getInt("Frequency");
                System.out.println("Key: " + key + ", Frequency: " + frequency);
              }



              }
          }
          // // create a database connection
          // connection = DriverManager.getConnection(url);
          // Statement statement = connection.createStatement();
          // statement.setQueryTimeout(30);  // set timeout to 30 sec.
        
          // ResultSet rs = statement.executeQuery(orderTable);

          


          // while(rs.next())
          // {
          //   // read the result set
          //   System.out.println("Key: " + rs.getString("Key"));
          //   System.out.println("Frequency: " + rs.getInt("Frequency"));
          //   frequencyCounter++;
          // }
        }
        catch(SQLException e)
        {
          // if the error message is "out of memory",
          // it probably means no database file is found
          System.err.println(e.getMessage());
        }
      }
    }

