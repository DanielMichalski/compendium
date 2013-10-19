import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Program implementuj¹cy wielow¹tkowy serwer
 * nas³uchuj¹cy na porcie 8189 
 * i wysy³aj¹cy echo informacji otrzymanej od klientów.
 * @author Cay Horstmann
 * @version 1.20 2004-08-03
 */
public class ThreadedEchoServer
{  
   public static void main(String[] args )
   {  
      try
      {  
         int i = 1;
         ServerSocket s = new ServerSocket(8189);

         while (true)
         {  
            Socket incoming = s.accept();
            System.out.println("Spawning " + i);
            Runnable r = new ThreadedEchoHandler(incoming);
            Thread t = new Thread(r);
            t.start();
            i++;
         }
      }
      catch (IOException e)
      {  
         e.printStackTrace();
      }
   }
}

/**
   Klasa obs³uguj¹ca komunikacjê z pojedynczym klientem. 
*/
class ThreadedEchoHandler implements Runnable
{ 
   /**
      Tworzy obiekt obs³ugi.
      @param i gniazdko wejœciowe
      @param c licznik obiektów (wykorzystywany przez ³añcuch zachêty)
   */
   public ThreadedEchoHandler(Socket i)
   { 
      incoming = i; 
   }

   public void run()
   {  
      try
      {  
         try
         {
            InputStream inStream = incoming.getInputStream();
            OutputStream outStream = incoming.getOutputStream();
            
            Scanner in = new Scanner(inStream);         
            PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);
            
            out.println( "Hello! Enter BYE to exit." );
            
            // wyœwietla echo
            boolean done = false;
            while (!done && in.hasNextLine())
            {  
               String line = in.nextLine();            
               out.println("Echo: " + line);            
               if (line.trim().equals("BYE"))
                  done = true;
            }
         }
         finally
         {
            incoming.close();
         }
      }
      catch (IOException e)
      {  
         e.printStackTrace();
      }
   }

   private Socket incoming;
}

