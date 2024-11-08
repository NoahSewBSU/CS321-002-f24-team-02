import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FakeData
{
      public static void main(String[] args)
      {
        Connection connection = null;
        try
        {
          // create a database connection
          connection = DriverManager.getConnection("jdbc:sqlite:fakedata.db");
          Statement statement = connection.createStatement();
          statement.setQueryTimeout(30);  // set timeout to 30 sec.

          statement.executeUpdate("drop table if exists acceptedtime");
          statement.executeUpdate("create table acceptedtime (Key string, Frequency integer)");
          statement.executeUpdate("insert into acceptedtime values('Accepted-14:18', 2)");
          statement.executeUpdate("insert into acceptedtime values('Accepted-22:11', 1)");
          statement.executeUpdate("insert into acceptedtime values('Accepted-08:32', 3)");
          statement.executeUpdate("insert into acceptedtime values('Accepted-09:21', 3)");
          statement.executeUpdate("insert into acceptedtime values('Accepted-06:14', 4)");
          statement.executeUpdate("insert into acceptedtime values('Accepted-23:56', 2)");
          statement.executeUpdate("insert into acceptedtime values('Accepted-15:01', 1)");
          ResultSet rs = statement.executeQuery("select * from acceptedtime");
          while(rs.next())
          {
            // read the result set
            System.out.println("Key: " + rs.getString("Key"));
            System.out.println("Frequency: " + rs.getInt("Frequency"));
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

