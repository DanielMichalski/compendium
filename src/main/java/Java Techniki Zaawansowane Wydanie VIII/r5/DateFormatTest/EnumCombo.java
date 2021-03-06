import java.util.*;
import javax.swing.*;

/**
 * Lista rozwijalna pozwalaj�ca u�ytkownikowi wybra�
 * jedn� spo�r�d warto�ci p�l statycznych,
 * kt�rych nazwy zosta�y przekazane konstruktorowi.
 * @version 1.13 2007-07-25
 * @author Cay Horstmann
 */
public class EnumCombo extends JComboBox
{ 
   /**
    * Tworzy EnumCombo.
    * @param cl klasa
    * @param labels tablica nazw p�l statycznych klasy cl
    */
   public EnumCombo(Class<?> cl, String[] labels)
   {  
      for (String label : labels)
      {  
         String name = label.toUpperCase().replace(' ', '_');
         int value = 0;
         try
         {  
            java.lang.reflect.Field f = cl.getField(name);
            value = f.getInt(cl);
         }
         catch (Exception e)
         {  
            label = "(" + label + ")";
         }
         table.put(label, value);
         addItem(label);
      }
      setSelectedItem(labels[0]);
   }

   /**
    * Zwraca warto�� pola wybranego przez u�ytkownika.
    * @return warto�� pola statycznego
    */
   public int getValue()
   {  
      return table.get(getSelectedItem());
   }

   private Map<String, Integer> table = new TreeMap<String, Integer>();
}

