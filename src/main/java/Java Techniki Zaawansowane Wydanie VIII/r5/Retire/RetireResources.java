import java.awt.*;

/**
 * Zasoby dla j�zyka angielskiego, 
 * kt�re nie s� �a�cuchami znak�w.
 * @version 1.21 2001-08-27
 * @author Cay Horstmann
 */
public class RetireResources extends java.util.ListResourceBundle
{
   public Object[][] getContents()
   {
      return contents;
   }

   static final Object[][] contents = {
   // BEGIN LOCALIZE
         { "colorPre", Color.blue }, { "colorGain", Color.white }, { "colorLoss", Color.red }
   // END LOCALIZE
   };
}
