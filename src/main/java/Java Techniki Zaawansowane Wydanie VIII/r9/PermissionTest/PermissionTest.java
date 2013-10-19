import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Demonstruje wykorzystanie 
 * w³asnej klasy pozwoleñ WordCheckPermission.
 * @version 1.03 2007-10-06
 * @author Cay Horstmann
 */
public class PermissionTest
{
   public static void main(String[] args)
   {
      System.setProperty("java.security.policy", "PermissionTest.policy");      
      System.setSecurityManager(new SecurityManager());
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {
               JFrame frame = new PermissionTestFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
}

/**
 * Ramka zawieraj¹ca pole tekstowe umo¿liwiaj¹ce
 * dodawanie tekstu do obszaru tekstowego pod warunkiem,
 * ¿e nie zawiera on zabronionych s³ów.
 */
class PermissionTestFrame extends JFrame
{
   public PermissionTestFrame()
   {
      setTitle("PermissionTest");
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      textField = new JTextField(20);
      JPanel panel = new JPanel();
      panel.add(textField);
      JButton openButton = new JButton("Insert");
      panel.add(openButton);
      openButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               insertWords(textField.getText());
            }
         });

      add(panel, BorderLayout.NORTH);

      textArea = new WordCheckTextArea();
      add(new JScrollPane(textArea), BorderLayout.CENTER);
   }

   /**
    * Próbuje dodaæ s³owa do obszaru tekstowego.
    * Wyœwietla okno dialogowe, jeœli próba nie powiedzie siê.
    * @param words wstawiane s³owa
    */
   public void insertWords(String words)
   {
      try
      {
         textArea.append(words + "\n");
      }
      catch (SecurityException e)
      {
         JOptionPane.showMessageDialog(this, "I am sorry, but I cannot do that.");
      }
   }

   private JTextField textField;
   private WordCheckTextArea textArea;
   private static final int DEFAULT_WIDTH = 400;
   private static final int DEFAULT_HEIGHT = 300;
}

/**
 * Klasa obszaru tekstowego, której metoda append
 * sprawdza, czy dodawany tekst nie zawiera zabronionych s³ów.
 */
class WordCheckTextArea extends JTextArea
{
   public void append(String text)
   {
      WordCheckPermission p = new WordCheckPermission(text, "insert");
      SecurityManager manager = System.getSecurityManager();
      if (manager != null) manager.checkPermission(p);
      super.append(text);
   }
}
