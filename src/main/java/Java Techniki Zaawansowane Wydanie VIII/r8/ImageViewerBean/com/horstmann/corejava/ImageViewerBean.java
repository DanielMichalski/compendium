package com.horstmann.corejava;

import java.awt.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

/**
 * Ziarnko wy�wietlania plik�w graficznych.
 * @version 1.21 2001-08-15
 * @author Cay Horstmann
 */
public class ImageViewerBean extends JLabel
{

   public ImageViewerBean()
   {
      setBorder(BorderFactory.createEtchedBorder());
   }

   /**
    * Nadaje warto�� w�a�ciwo�ci fileName.
    * @param fileName nazwa pliku graficznego
    */
   public void setFileName(String fileName)
   {
      try
      {
         file = new File(fileName);
         setIcon(new ImageIcon(ImageIO.read(file)));
      }
      catch (IOException e)
      {
         file = null;
         setIcon(null);
      }
   }

   /**
    * Zwraca warto�� w�a�ciwo�ci fileName.
    * @return nazwa pliku graficznego
    */
   public String getFileName()
   {
      if (file == null) return "";
      else return file.getPath();
   }

   public Dimension getPreferredSize()
   {
      return new Dimension(XPREFSIZE, YPREFSIZE);
   }

   private File file = null;
   private static final int XPREFSIZE = 200;
   private static final int YPREFSIZE = 200;
}
