import java.io.*;

/**
 * @version 1.00 05 Sep 1997
 * @author Gary Cornell
 */
public class FindDirectories
{
   public static void main(String[] args)
   {
      // jeœli brak parametru wywo³ania, 
      // rozpoczyna od katalogu nadrzêdnego
      if (args.length == 0) args = new String[] { ".." };

      try
      {
         File pathName = new File(args[0]);
         String[] fileNames = pathName.list();

         // wyœwietla wszystkie pliki w katalogu
         for (int i = 0; i < fileNames.length; i++)
         {
            File f = new File(pathName.getPath(), fileNames[i]);

            // jeœli kolejny katalog, 
            // wywo³uje rekurencyjnie metodê main
            if (f.isDirectory())
            {
               System.out.println(f.getCanonicalPath());
               main(new String[] { f.getPath() });
            }
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}
