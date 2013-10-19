import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

/**
 * Program demonstruj¹cy przekazywanie tekstu
 * miêdzy aplikacj¹ Java i schowkiem systemowym.
 * @version 1.13 2007-08-16
 * @author Cay Horstmann
 */
public class TextTransferTest
{
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {
               JFrame frame = new TextTransferFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
}

/**
 * Ramka zawieraj¹ca obszar tekstowy 
 * oraz przyciski umo¿liwiaj¹ce kopiowanie do schowka
 * i wklejanie ze schowka.
 */
class TextTransferFrame extends JFrame
{
   public TextTransferFrame()
   {
      setTitle("TextTransferTest");
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      textArea = new JTextArea();
      add(new JScrollPane(textArea), BorderLayout.CENTER);
      JPanel panel = new JPanel();

      JButton copyButton = new JButton("Copy");
      panel.add(copyButton);
      copyButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               copy();
            }
         });

      JButton pasteButton = new JButton("Paste");
      panel.add(pasteButton);
      pasteButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               paste();
            }
         });

      add(panel, BorderLayout.SOUTH);
   }

   /**
    * Kopiuje wybrany tekst do schowka systemowego.
    */
   private void copy()
   {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      String text = textArea.getSelectedText();
      if (text == null) text = textArea.getText();
      StringSelection selection = new StringSelection(text);
      clipboard.setContents(selection, null);
   }

   /**
    * Wkleja tekst ze schowka systemowego.
    */
   private void paste()
   {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      DataFlavor flavor = DataFlavor.stringFlavor;
      if (clipboard.isDataFlavorAvailable(flavor))
      {
         try
         {
            String text = (String) clipboard.getData(flavor);
            textArea.replaceSelection(text);
         }
         catch (UnsupportedFlavorException e)
         {
            JOptionPane.showMessageDialog(this, e);
         }
         catch (IOException e)
         {
            JOptionPane.showMessageDialog(this, e);
         }
      }
   }

   private JTextArea textArea;

   private static final int DEFAULT_WIDTH = 300;
   private static final int DEFAULT_HEIGHT = 300;
}
