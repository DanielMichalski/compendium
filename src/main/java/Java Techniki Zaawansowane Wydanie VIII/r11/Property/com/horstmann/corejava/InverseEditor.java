package com.horstmann.corejava;

import java.awt.*;
import java.beans.*;
import javax.swing.*;

/**
 * Edytor w³aœciwoœci inverse ziarnka ChartBean.
 * W³aœciwoœæ inverse umo¿liwia wybór kolorowych s³upków
 * wykresu lub kolorowego t³a.
 * @version 1.30 2007-10-03
 * @author Cay Horstmann
 */
public class InverseEditor extends PropertyEditorSupport
{
   public Component getCustomEditor()
   {
      return new InverseEditorPanel(this);
   }

   public boolean supportsCustomEditor()
   {
      return true;
   }

   public boolean isPaintable()
   {
      return true;
   }

   public String getAsText()
   {
      return null;
   }

   public String getJavaInitializationString()
   {
      return "(" + getValue() + ")";
   }

   public void paintValue(Graphics g, Rectangle bounds)
   {
      ImageIcon icon = (Boolean) getValue() ? inverseIcon : normalIcon;
      int x = bounds.x + (bounds.width - icon.getIconWidth()) / 2;
      int y = bounds.y + (bounds.height - icon.getIconHeight()) / 2;
      g.drawImage(icon.getImage(), x, y, null);
   }

   private ImageIcon inverseIcon = new ImageIcon(getClass().getResource(
         "ChartBean_INVERSE_16x16.gif"));
   private ImageIcon normalIcon = new ImageIcon(getClass().getResource("ChartBean_MONO_16x16.gif"));
}
