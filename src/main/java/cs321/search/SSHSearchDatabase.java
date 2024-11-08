package cs321.search;

import cs321.btree.BTree;
import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;

import java.sql.Connection;
import java.sql.DriverManager;
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
                type = type.replaceAll("-","");
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
        Connection connection = null;

        try {
          // create a database connection
          connection = DriverManager.getConnection("jdbc:sqlite:" + database);
          Statement statement = connection.createStatement();
          statement.setQueryTimeout(30);  // set timeout to 30 sec.

        //   statement.executeUpdate("drop table if exists " + type);
        //   statement.executeUpdate("create table " + type + " (Key string, Frequency integer)");
        //   statement.executeUpdate("insert into " + type + " values('Accepted-14:18', 2)");
        //   statement.executeUpdate("insert into " + type + " values('Accepted-22:11', 1)");
        //   statement.executeUpdate("insert into " + type + " values('Accepted-08:32', 3)");
        //   statement.executeUpdate("insert into " + type + " values('Accepted-09:21', 3)");
        //   statement.executeUpdate("insert into " + type + " values('Accepted-06:14', 4)");
        //   statement.executeUpdate("insert into " + type + " values('Accepted-23:56', 2)");
        //   statement.executeUpdate("insert into " + type + " values('Accepted-15:01', 1)");
          ResultSet rs = statement.executeQuery("select * from " + type);
          while(rs.next() && frequencyCounter < topFrequency)
          {
            // read the result set
            System.out.println("Key: " + rs.getString("Key"));
            System.out.println("Frequency: " + rs.getInt("Frequency"));
            frequencyCounter++;
          }
        }
        catch(SQLException e)
        {
          // if the error message is "out of memory",
          // it probably means no database file is found
          System.err.println(e.getMessage());
        }
        finally
        {
          try
          {
            if(connection != null)
              connection.close();
          }
          catch(SQLException e)
          {
            // connection close failed.
            System.err.println(e.getMessage());
          }
        }
      }
    }

