package com.horstmann.corejava;

import java.beans.*;
import java.util.*;

/**
 * Niestandardowy edytor w³aœciwoœci titlePosition ziarnka 
 * ChartBean. Umo¿liwia u¿ytkownikowi wybór wartoœci
 * Left, Center i Right.
 * @version 1.20 2007-12-14
 * @author Cay Horstmann
 */
public class TitlePositionEditor extends PropertyEditorSupport
{
   public String[] getTags()
   {
      return tags;
   }

   public String getJavaInitializationString()
   {
      return ChartBean.Position.class.getName().replace('$', '.') + "." + getValue();
   }

   public String getAsText()
   {
      int index = ((ChartBean.Position) getValue()).ordinal();
      return tags[index];
   }

   public void setAsText(String s)
   {
      int index = Arrays.asList(tags).indexOf(s);
      if (index >= 0) setValue(ChartBean.Position.values()[index]);
   }

   private String[] tags = { "Left", "Center", "Right" };
}
