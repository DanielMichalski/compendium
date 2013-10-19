import java.io.*;
import java.lang.reflect.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Program demonstruj¹cy wykorzystanie w³asnej procedury ³aduj¹cej
 * zaszyfrowane pliki klas.
 * @version 1.22 2007-10-05
 * @author Cay Horstmann
 */
public class ClassLoaderTest
{
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {

               JFrame frame = new ClassLoaderFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
}

/**
 * Ramka zawieraj¹ca dwa pola tekstowe 
 * umo¿liwiaj¹ce wprowadzenie nazwy ³adowanej klasy
 * i klucza szyfrowania.
 */
class ClassLoaderFrame extends JFrame
{
   public ClassLoaderFrame()
   {
      setTitle("ClassLoaderTest");
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
      setLayout(new GridBagLayout());
      add(new JLabel("Class"), new GBC(0, 0).setAnchor(GBC.EAST));
      add(nameField, new GBC(1, 0).setWeight(100, 0).setAnchor(GBC.WEST));
      add(new JLabel("Key"), new GBC(0, 1).setAnchor(GBC.EAST));
      add(keyField, new GBC(1, 1).setWeight(100, 0).setAnchor(GBC.WEST));
      JButton loadButton = new JButton("Load");
      add(loadButton, new GBC(0, 2, 2, 1));
      loadButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               runClass(nameField.getText(), keyField.getText());
            }
         });
      pack();
   }

   /**
    * Wywo³uje metodê main danej klasy.
    * @param name nazwa klasy
    * @param key klucz szyfrowania pliku klasy
    */
   public void runClass(String name, String key)
   {
      try
      {
         ClassLoader loader = new CryptoClassLoader(Integer.parseInt(key));
         Class<?> c = loader.loadClass(name);
         Method m = c.getMethod("main", String[].class);
         m.invoke(null, (Object) new String[] {});
      }
      catch (Throwable e)
      {
         JOptionPane.showMessageDialog(this, e);
      }
   }

   private JTextField keyField = new JTextField("3", 4);
   private JTextField nameField = new JTextField("Calculator", 30);
   private static final int DEFAULT_WIDTH = 300;
   private static final int DEFAULT_HEIGHT = 200;
}

/**
 * Klasa procedury ³aduj¹cej zaszyfrowane pliki klas.
 */
class CryptoClassLoader extends ClassLoader
{
   /**
    * Tworzy obiekt klasy CryptoClassLoader.
    * @param k klucz szyfrowania
    */
   public CryptoClassLoader(int k)
   {
      key = k;
   }

   protected Class<?> findClass(String name) throws ClassNotFoundException
   {
      byte[] classBytes = null;
      try
      {
         classBytes = loadClassBytes(name);
      }
      catch (IOException e)
      {
         throw new ClassNotFoundException(name);
      }

      Class<?> cl = defineClass(name, classBytes, 0, classBytes.length);
      if (cl == null) throw new ClassNotFoundException(name);
      return cl;
   }

   /**
    * £aduje i odszyfrowuje kod klasy.
    * @param name nazwa klasy
    * @return tablica zawieraj¹ca kod klasy
    */
   private byte[] loadClassBytes(String name) throws IOException
   {
      String cname = name.replace('.', '/') + ".caesar";
      FileInputStream in = null;
      in = new FileInputStream(cname);
      try
      {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         int ch;
         while ((ch = in.read()) != -1)
         {
            byte b = (byte) (ch - key);
            buffer.write(b);
         }
         in.close();
         return buffer.toByteArray();
      }
      finally
      {
         in.close();
      }
   }

   private int key;
}
