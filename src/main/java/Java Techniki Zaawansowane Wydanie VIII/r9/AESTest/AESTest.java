import java.io.*;
import java.security.*;
import javax.crypto.*;

/**
 * Program testuj¹cy szyfr AES. Uruchamianie:
 * java AESTest -genkey keyfile
 * java AESTest -encrypt plaintext encrypted keyfile
 * java AESTest -decrypt encrypted decrypted keyfile
 * @author Cay Horstmann
 * @version 1.0 2004-09-14
 */
public class AESTest
{
   public static void main(String[] args)
   {
      try
      {
         if (args[0].equals("-genkey"))
         {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecureRandom random = new SecureRandom();
            keygen.init(random);
            SecretKey key = keygen.generateKey();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(args[1]));
            out.writeObject(key);
            out.close();
         }
         else
         {
            int mode;
            if (args[0].equals("-encrypt")) mode = Cipher.ENCRYPT_MODE;
            else mode = Cipher.DECRYPT_MODE;

            ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream(args[3]));
            Key key = (Key) keyIn.readObject();
            keyIn.close();

            InputStream in = new FileInputStream(args[1]);
            OutputStream out = new FileOutputStream(args[2]);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(mode, key);

            crypt(in, out, cipher);
            in.close();
            out.close();
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (GeneralSecurityException e)
      {
         e.printStackTrace();
      }
      catch (ClassNotFoundException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Szyfruje bajty strumienia wejœciowego i wysy³a je do strumienia wyjœciowego.
    * @param in strumieñ wejœciowy
    * @param out the strumieñ wyjœciowy
    * @param cipher algorytm szyfrowania
    */
   public static void crypt(InputStream in, OutputStream out, Cipher cipher) throws IOException,
         GeneralSecurityException
   {
      int blockSize = cipher.getBlockSize();
      int outputSize = cipher.getOutputSize(blockSize);
      byte[] inBytes = new byte[blockSize];
      byte[] outBytes = new byte[outputSize];

      int inLength = 0;
      boolean more = true;
      while (more)
      {
         inLength = in.read(inBytes);
         if (inLength == blockSize)
         {
            int outLength = cipher.update(inBytes, 0, blockSize, outBytes);
            out.write(outBytes, 0, outLength);
         }
         else more = false;
      }
      if (inLength > 0) outBytes = cipher.doFinal(inBytes, 0, inLength);
      else outBytes = cipher.doFinal();
      out.write(outBytes);
   }
}
