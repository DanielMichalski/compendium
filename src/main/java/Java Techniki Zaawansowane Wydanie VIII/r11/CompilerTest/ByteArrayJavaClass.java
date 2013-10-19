import java.io.*;
import java.net.*;
import javax.tools.*;

/**
 * Klasa przechowuj¹ca kod bajtowy w tablicy bajtów.
 * @version 1.00 2007-11-02
 * @author Cay Horstmann
 */
public class ByteArrayJavaClass extends SimpleJavaFileObject
{
   /**
    * Tworzy nowy obiekt ByteArrayJavaClass
    * @param name nazwa pliku klasy reprezentowanego przez ten obiekt
    */
   public ByteArrayJavaClass(String name)
   {
      super(URI.create("bytes:///" + name), Kind.CLASS);
      stream = new ByteArrayOutputStream();
   }

   public OutputStream openOutputStream() throws IOException
   {
      return stream;
   }
   
   public byte[] getBytes()
   {
      return stream.toByteArray();
   }

   private ByteArrayOutputStream stream;
}

