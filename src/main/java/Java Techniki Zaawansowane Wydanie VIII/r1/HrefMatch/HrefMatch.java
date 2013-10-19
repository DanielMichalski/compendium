import java.io.*;
import java.net.*;
import java.util.regex.*;

/**
 * Program wyœwietlaj¹cy wszystkie adresy URL na stronie WWW
 * poprzez dopasowanie wyra¿enia regularnego 
 * opisuj¹cego znacznik <a href=...> jêzyka HTML.
 * Uruchamianie: java HrefMatch adresURL
 * @version 1.01 2004-06-04
 * @author Cay Horstmann
 */
public class HrefMatch
{
   public static void main(String[] args)
   {
      try
      {
         // pobiera URL z wiersza poleceñ lub u¿ywa domyœlnego
         String urlString;
         if (args.length > 0) urlString = args[0];
         else urlString = "http://java.sun.com";

         // otwiera InputStreamReader dla podanego URL
         InputStreamReader in = new InputStreamReader(new URL(urlString).openStream());

         // wczytuje zawartoœæ do obiektu StringBuilder
         StringBuilder input = new StringBuilder();
         int ch;
         while ((ch = in.read()) != -1)
            input.append((char) ch);

         // poszukuje wszystkich wyst¹pieñ wzorca
         String patternString = "<a\\s+href\\s*=\\s*(\"[^\"]*\"|[^\\s>])\\s*>";
         Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
         Matcher matcher = pattern.matcher(input);

         while (matcher.find())
         {
            int start = matcher.start();
            int end = matcher.end();
            String match = input.substring(start, end);
            System.out.println(match);
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (PatternSyntaxException e)
      {
         e.printStackTrace();
      }
   }
}