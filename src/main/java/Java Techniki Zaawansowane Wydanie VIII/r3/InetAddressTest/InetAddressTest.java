import java.net.*;

/**
 * Program demonstruj�cy zastosowanie klasy InetAddress.
 * W wierszu polece� przekazujemy mu jako parametr nazw� hosta.
 * W przeciwnym razie uzyskamy adres lokalnego hosta.
 * @version 1.01 2001-06-26
 * @author Cay Horstmann
 */
public class InetAddressTest
{
   public static void main(String[] args)
   {
      try
      {
         if (args.length > 0)
         {
            String host = args[0];
            InetAddress[] addresses = InetAddress.getAllByName(host);
            for (InetAddress a : addresses)
               System.out.println(a);
         }
         else
         {
            InetAddress localHostAddress = InetAddress.getLocalHost();
            System.out.println(localHostAddress);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
