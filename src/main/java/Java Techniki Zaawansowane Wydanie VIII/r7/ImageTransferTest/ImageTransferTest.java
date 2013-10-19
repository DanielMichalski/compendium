import java.io.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * Program demonstruj¹cy przekazywanie obrazów
 * miêdzy aplikacj¹ w jêzyku Java a schowkiem systemowym.
 * @version 1.22 2007-08-16
 * @author Cay Horstmann
 */
public class ImageTransferTest
{
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {
               JFrame frame = new ImageTransferFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
}

/**
 * Ramka zawieraj¹ca etykietê wyœwietlaj¹c¹ obraz
 * i przyciski umo¿liwiaj¹ce jego kopiowanie i wklejanie
 * za poœrednictwem schowka systemowego.
 */
class ImageTransferFrame extends JFrame
{
   public ImageTransferFrame()
   {
      setTitle("ImageTransferTest");
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      label = new JLabel();
      image = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_ARGB);
      Graphics g = image.getGraphics();
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
      g.setColor(Color.RED);
      g.fillOval(DEFAULT_WIDTH / 4, DEFAULT_WIDTH / 4, DEFAULT_WIDTH / 2, DEFAULT_HEIGHT / 2);

      label.setIcon(new ImageIcon(image));
      add(new JScrollPane(label), BorderLayout.CENTER);
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
    * Kopiuje bie¿¹cy obraz do schowka systemowego.
    */
   private void copy()
   {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      ImageTransferable selection = new ImageTransferable(image);
      clipboard.setContents(selection, null);
   }

   /**
    * Wkleja obraz ze schowka systemowego.
    */
   private void paste()
   {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      DataFlavor flavor = DataFlavor.imageFlavor;
      if (clipboard.isDataFlavorAvailable(flavor))
      {
         try
         {
            image = (Image) clipboard.getData(flavor);
            label.setIcon(new ImageIcon(image));
         }
         catch (UnsupportedFlavorException exception)
         {
            JOptionPane.showMessageDialog(this, exception);
         }
         catch (IOException exception)
         {
            JOptionPane.showMessageDialog(this, exception);
         }
      }
   }

   private JLabel label;
   private Image image;

   private static final int DEFAULT_WIDTH = 300;
   private static final int DEFAULT_HEIGHT = 300;
}

/**
 * Klasa obudowuj¹ca obrazy przekazywane przez schowek.
 */
class ImageTransferable implements Transferable
{
   /**
    * Tworzy obiekt ImageTransferable.
    * @param image obraz
    */
   public ImageTransferable(Image image)
   {
      theImage = image;
   }

   public DataFlavor[] getTransferDataFlavors()
   {
      return new DataFlavor[] { DataFlavor.imageFlavor };
   }

   public boolean isDataFlavorSupported(DataFlavor flavor)
   {
      return flavor.equals(DataFlavor.imageFlavor);
   }

   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
   {
      if (flavor.equals(DataFlavor.imageFlavor))
      {
         return theImage;
      }
      else
      {
         throw new UnsupportedFlavorException(flavor);
      }
   }

   private Image theImage;
}
