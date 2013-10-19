import java.io.*;
import java.security.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Program wyznaczaj¹cy skrót zawartoœci pliku lub obszaru tekstowego.
 * @version 1.13 2007-10-06
 * @author Cay Horstmann
 */
public class MessageDigestTest
{
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {
               JFrame frame = new MessageDigestFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
}

/**
 * Ramka posiadaj¹ca menu umo¿liwiaj¹ce wyznaczanie skrótu pliku
 * lub tekstu w obszarze tekstowym, przyciski wyboru algorytmu skrótu
 * SHA-1 lub MD5, obszar tekstowy oraz pole tekstowe prezentuj¹ce
 * wyznaczony skrót.
 */
class MessageDigestFrame extends JFrame
{
   public MessageDigestFrame()
   {
      setTitle("MessageDigestTest");
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      JPanel panel = new JPanel();
      ButtonGroup group = new ButtonGroup();
      addRadioButton(panel, "SHA-1", group);
      addRadioButton(panel, "MD5", group);

      add(panel, BorderLayout.NORTH);
      add(new JScrollPane(message), BorderLayout.CENTER);
      add(digest, BorderLayout.SOUTH);
      digest.setFont(new Font("Monospaced", Font.PLAIN, 12));

      setAlgorithm("SHA-1");

      JMenuBar menuBar = new JMenuBar();
      JMenu menu = new JMenu("File");
      JMenuItem fileDigestItem = new JMenuItem("File digest");
      fileDigestItem.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               loadFile();
            }
         });
      menu.add(fileDigestItem);
      JMenuItem textDigestItem = new JMenuItem("Text area digest");
      textDigestItem.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               String m = message.getText();
               computeDigest(m.getBytes());
            }
         });
      menu.add(textDigestItem);
      menuBar.add(menu);
      setJMenuBar(menuBar);
   }

   /**
    * Dodaje przycisk wyboru algorytmu.
    * @param c kontener, do którego dodawany jest przycisk
    * @param name nazwa algorytmu
    * @param g grupa przycisków
    */
   public void addRadioButton(Container c, final String name, ButtonGroup g)
   {
      ActionListener listener = new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               setAlgorithm(name);
            }
         };
      JRadioButton b = new JRadioButton(name, g.getButtonCount() == 0);
      c.add(b);
      g.add(b);
      b.addActionListener(listener);
   }

   /**
    * Wybiera algorytm skrótu.
    * @param alg nazwa algorytmu
    */
   public void setAlgorithm(String alg)
   {
      try
      {
         currentAlgorithm = MessageDigest.getInstance(alg);
         digest.setText("");
      }
      catch (NoSuchAlgorithmException e)
      {
         digest.setText("" + e);
      }
   }

   /**
    * £aduje plik i wyznacza jego skrót.
    */
   public void loadFile()
   {
      JFileChooser chooser = new JFileChooser();
      chooser.setCurrentDirectory(new File("."));

      int r = chooser.showOpenDialog(this);
      if (r == JFileChooser.APPROVE_OPTION)
      {
         try
         {
            String name = chooser.getSelectedFile().getAbsolutePath();
            computeDigest(loadBytes(name));
         }
         catch (IOException e)
         {
            JOptionPane.showMessageDialog(null, e);
         }
      }
   }

   /**
    * £aduje bajty z pliku.
    * @param name nazwa pliku
    * @return tablica bajtów pliku
    */
   public byte[] loadBytes(String name) throws IOException
   {
      FileInputStream in = null;

      in = new FileInputStream(name);
      try
      {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         int ch;
         while ((ch = in.read()) != -1)
            buffer.write(ch);
         return buffer.toByteArray();
      }
      finally
      {
         in.close();
      }
   }

   /**
    * Wyznacza skrót tablicy bajtów i prezentuje go w polu tekstowym.
    * @param b tablica bajtów, dla których wyznaczany jest skrót.
    */
   public void computeDigest(byte[] b)
   {
      currentAlgorithm.reset();
      currentAlgorithm.update(b);
      byte[] hash = currentAlgorithm.digest();
      String d = "";
      for (int i = 0; i < hash.length; i++)
      {
         int v = hash[i] & 0xFF;
         if (v < 16) d += "0";
         d += Integer.toString(v, 16).toUpperCase() + " ";
      }
      digest.setText(d);
   }

   private JTextArea message = new JTextArea();
   private JTextField digest = new JTextField();
   private MessageDigest currentAlgorithm;
   private static final int DEFAULT_WIDTH = 400;
   private static final int DEFAULT_HEIGHT = 300;
}
