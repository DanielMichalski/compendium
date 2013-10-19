import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Program ³¹cz¹cy siê z lokalizacj¹ okreœlon¹ przez URL 
 * i wyœwietlaj¹cy dane nag³ówka odpowiedzi oraz pierwszych
 * 10 wierszy danych ¿¹danego zasobu.
 * 
 * Parametrem programu jest adres URL oraz, opcjonalnie,
 * nazwa i has³o s³u¿¹ce uwierzytelnieniu u¿ytkownika
 * w protokole HTTP.
 * @version 1.11 2007-06-26
 * @author Cay Horstmann
 */
public class URLConnectionTest
{
   public static void main(String[] args)
   {
      try
      {
         String urlName;
         if (args.length > 0) urlName = args[0];
         else urlName = "http://java.sun.com";

         URL url = new URL(urlName);
         URLConnection connection = url.openConnection();

         // wykorzystuje nazwê u¿ytkownika i has³o, jeœli podane w linii poleceñ

         if (args.length > 2)
         {
            String username = args[1];
            String password = args[2];
            String input = username + ":" + password;
            String encoding = base64Encode(input);
            connection.setRequestProperty("Authorization", "Basic " + encoding);
         }

         connection.connect();

         // wyœwietla pola nag³ówka

         Map<String, List<String>> headers = connection.getHeaderFields();
         for (Map.Entry<String, List<String>> entry : headers.entrySet())
         {
            String key = entry.getKey();
            for (String value : entry.getValue())
               System.out.println(key + ": " + value);
         }

         // wyœwietla rezultaty dzia³ania metod dostêpu do wartoœci pól nag³ówka

         System.out.println("----------");
         System.out.println("getContentType: " + connection.getContentType());
         System.out.println("getContentLength: " + connection.getContentLength());
         System.out.println("getContentEncoding: " + connection.getContentEncoding());
         System.out.println("getDate: " + connection.getDate());
         System.out.println("getExpiration: " + connection.getExpiration());
         System.out.println("getLastModifed: " + connection.getLastModified());
         System.out.println("----------");

         Scanner in = new Scanner(connection.getInputStream());

         // wyœwietla pierwszych 10 wierszy danych zasobu

         for (int n = 1; in.hasNextLine() && n <= 10; n++)
            System.out.println(in.nextLine());
         if (in.hasNextLine()) System.out.println(". . .");
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Wyznacza kodowanie Base64 ³añcucha znaków
    * @param s ³añcuch znaków
    * @return ³añcuch zakodowany za pomoc¹ Base64
    */
   public static String base64Encode(String s)
   {
      ByteArrayOutputStream bOut = new ByteArrayOutputStream();
      Base64OutputStream out = new Base64OutputStream(bOut);
      try
      {
         out.write(s.getBytes());
         out.flush();
      }
      catch (IOException e)
      {
      }
      return bOut.toString();
   }
}

/**
 * Filtr strumienia przekszta³caj¹cy strumieñ bajtów na kod Base64.
 *
 * Base64 koduje 3 bajty za pomoc¹ 4 znaków.
 * |11111122|22223333|33444444|
 * Ka¿de 6 bitów kodowane jest wed³ug mapy Base64. 
 * Jeœli liczba bajtów nie jest wielokrotnoœci¹ 3, to ostatnia 
 * grupa 4 znaków uzupe³niana za pomoc¹ jednego lub dwu
 * znaków =. Ka¿da wiersz zawiera co najwy¿ej 76 znaków.
 */
class Base64OutputStream extends FilterOutputStream
{
   /**
    * Tworzy filtr strumienia.
    * @param out filtrowany strumieñ
    */
   public Base64OutputStream(OutputStream out)
   {
      super(out);
   }

   public void write(int c) throws IOException
   {
      inbuf[i] = c;
      i++;
      if (i == 3)
      {
         super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
         super.write(toBase64[((inbuf[0] & 0x03) << 4) | ((inbuf[1] & 0xF0) >> 4)]);
         super.write(toBase64[((inbuf[1] & 0x0F) << 2) | ((inbuf[2] & 0xC0) >> 6)]);
         super.write(toBase64[inbuf[2] & 0x3F]);
         col += 4;
         i = 0;
         if (col >= 76)
         {
            super.write('\n');
            col = 0;
         }
      }
   }

   public void flush() throws IOException
   {
      if (i == 1)
      {
         super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
         super.write(toBase64[(inbuf[0] & 0x03) << 4]);
         super.write('=');
         super.write('=');
      }
      else if (i == 2)
      {
         super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
         super.write(toBase64[((inbuf[0] & 0x03) << 4) | ((inbuf[1] & 0xF0) >> 4)]);
         super.write(toBase64[(inbuf[1] & 0x0F) << 2]);
         super.write('=');
      }
      if (col > 0)
      {
         super.write('\n');
         col = 0;
      }
   }

   private static char[] toBase64 = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
         'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
         'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
         'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

   private int col = 0;
   private int i = 0;
   private int[] inbuf = new int[3];
}
