import java.io.*;
import java.util.*;
import java.sql.*;

/**
 * Program wykonuje polecenia jêzyka SQL zapisane w pliku.
 * Uruchomienie programu:
 * java -classpath œcie¿kaSterownika:. ExecSQL plikPoleceñ
 * @version 1.30 2004-08-05
 * @author Cay Horstmann
 */
class ExecSQL
{
   public static void main(String args[])
   {
      try
      {
         Scanner in;
         if (args.length == 0) in = new Scanner(System.in);
         else in = new Scanner(new File(args[0]));

         Connection conn = getConnection();
         try
         {
            Statement stat = conn.createStatement();

            while (true)
            {
               if (args.length == 0) System.out.println("Enter command or EXIT to exit:");

               if (!in.hasNextLine()) return;

               String line = in.nextLine();
               if (line.equalsIgnoreCase("EXIT")) return;
               if (line.trim().endsWith(";")) // usuwa œrednik koñcz¹cy polecenie
                  line = line.trim();
                  line = line.substring(0, line.length() - 1);
               }
               try
               {
                  boolean hasResultSet = stat.execute(line);
                  if (hasResultSet) showResultSet(stat);
               }
               catch (SQLException ex)
               {                  
                  for (Throwable e : ex)
                     e.printStackTrace();
               }
            }
         }
         finally
         {
            conn.close();
         }
      }
      catch (SQLException e)
      {
         for (Throwable t : e)
            t.printStackTrace();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Nawi¹zuje po³¹czenie z baz¹ danych,
    * korzystaj¹c z w³aœciwoœci
    * zapisanych w pliku database.properties
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

   /**
    * Wyœwietla wynik zapytania.
    * @param stat polecenie, którego wynik jest wyœwietlany
    */
   public static void showResultSet(Statement stat) throws SQLException
   {
      ResultSet result = stat.getResultSet();
      ResultSetMetaData metaData = result.getMetaData();
      int columnCount = metaData.getColumnCount();

      for (int i = 1; i <= columnCount; i++)
      {
         if (i > 1) System.out.print(", ");
         System.out.print(metaData.getColumnLabel(i));
      }
      System.out.println();

      while (result.next())
      {
         for (int i = 1; i <= columnCount; i++)
         {
            if (i > 1) System.out.print(", ");
            System.out.print(result.getString(i));
         }
         System.out.println();
      }
      result.close();
   }
}
