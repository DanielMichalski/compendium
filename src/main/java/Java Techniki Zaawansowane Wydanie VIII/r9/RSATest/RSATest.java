import java.io.*;
import java.security.*;
import javax.crypto.*;

/**
 * Program wykorzystuj¹cy algorytm szyfrowania RSA.
 * Uruchamianie:
 * java RSATest -genkey public private
 * java RSATest -encrypt plaintext encrypted public
 * java RSATest -decrypt encrypted decrypted private
 * @author Cay Horstmann
 * @version 1.0 2004-09-14 
 */
public class RSATest
{
   public static void main(String[] args)
   {
      try
      {
         if (args[0].equals("-genkey"))
         {
            KeyPairGenerator pairgen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = new SecureRandom();
            pairgen.initialize(KEYSIZE, random);
            KeyPair keyPair = pairgen.generateKeyPair();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(args[1]));
            out.writeObject(keyPair.getPublic());
            out.close();
            out = new ObjectOutputStream(new FileOutputStream(args[2]));
            out.writeObject(keyPair.getPrivate());
            out.close();
         }
         else if (args[0].equals("-encrypt"))
         {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecureRandom random = new SecureRandom();
            keygen.init(random);
            SecretKey key = keygen.generateKey();

            // szyfruje klucz DES za pomoc¹ klucza publicznego RSA
            ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream(args[3]));
            Key publicKey = (Key) keyIn.readObject();
            keyIn.close();

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.WRAP_MODE, publicKey);
            byte[] wrappedKey = cipher.wrap(key);
            DataOutputStream out = new DataOutputStream(new FileOutputStream(args[2]));
            out.writeInt(wrappedKey.length);
            out.write(wrappedKey);

            InputStream in = new FileInputStream(args[1]);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            crypt(in, out, cipher);
            in.close();
            out.close();
         }
         else
         {
            DataInputStream in = new DataInputStream(new FileInputStream(args[1]));
            int length = in.readInt();
            byte[] wrappedKey = new byte[length];
            in.read(wrappedKey, 0, length);

            // odszyfrowuje klucz DES za pomoc¹ klucza prywatnego RSA
            ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream(args[3]));
            Key privateKey = (Key) keyIn.readObject();
            keyIn.close();

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.UNWRAP_MODE, privateKey);
            Key key = cipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);

            OutputStream out = new FileOutputStream(args[2]);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);

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
      ;
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

   private static final int KEYSIZE = 512;
}
