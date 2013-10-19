import java.sql.*;
import java.io.*;
import java.util.*;

/**
 * Program sprawdzaj¹cy poprawnoœæ konfiguracji
 * bazy danych i sterownika JDBC.
 * @version 1.01 2004-09-24
 * @author Cay Horstmann
 */
class TestDB
{
   public static void main(String args[])
   {
      try
      {
         runTest();
      }
      catch (SQLException ex)
      {
         for (Throwable t : ex)
            t.printStackTrace();
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
      }
   }

   /**
    * Wykonuje test polegaj¹cy na utworzeniu tabeli, wstawieniu do niej wartoœci, 
    * prezentacji zawartoœci, usuniêciu tabeli.
    */
   public static void runTest() throws SQLException, IOException
   {
      Connection conn = getConnection();
      try
      {
         Statement stat = conn.createStatement();

         stat.executeUpdate("CREATE TABLE Greetings (Message CHAR(20))");
         stat.executeUpdate("INSERT INTO Greetings VALUES ('Hello, World!')");

         ResultSet result = stat.executeQuery("SELECT * FROM Greetings");
         if (result.next())
            System.out.println(result.getString(1));
         result.close();
         stat.executeUpdate("DROP TABLE Greetings");
      }
      finally
      {
         conn.close();
      }
   }

   /**
    * Nawi¹zuje po³¹czenie, korzystaj¹c
    * z w³aœciwoœci w pliku database.properties
    * @return po³¹czenie do bazy danych
    */
   public static Connection getConnection() throws SQLException, IOException
   {
      Properties props = new Properties();
      FileInputStream in = new FileInputStream("database.properties");
      props.load(in);
      in.close();

      String drivers = props.getProperty("jdbc.drivers");
      if (drivers != null) System.setProperty("jdbc.drivers", drivers);
      String url = props.getProperty("jdbc.url");
      String username = props.getProperty("jdbc.username");
      String password = props.getProperty("jdbc.password");

      return DriverManager.getConnection(url, username, password);
   }
}
