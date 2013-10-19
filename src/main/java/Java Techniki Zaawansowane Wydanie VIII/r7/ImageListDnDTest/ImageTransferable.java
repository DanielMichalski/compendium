import java.awt.*;
import java.awt.datatransfer.*;

/**
 * Klasa obudowuj¹ca przekazywane obiekty obrazków.
 */
public class ImageTransferable implements Transferable
{
   /**
    * Konstruktor.
    * @param image obrazek
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

