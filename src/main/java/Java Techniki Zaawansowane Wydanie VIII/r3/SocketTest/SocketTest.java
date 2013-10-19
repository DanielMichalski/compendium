import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Program ��cz�cy si� do zegara atomowego 
 * w Boulder, stan Colorado i wy�wietlaj�cy uzyskany czas.
 * @version 1.20 2004-08-03
 * @author Cay Horstmann
 */
public class SocketTest
{
   public static void main(String[] args)
   {
      try
      {
         Socket s = new Socket("time-A.timefreq.bldrdoc.gov", 13);
         try
         {
            InputStream inStream = s.getInputStream();
            Scanner in = new Scanner(inStream);

            while (in.hasNextLine())
            {
               String line = in.nextLine();
               System.out.println(line);
            }
         }
         finally
         {
            s.close();
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}
