import java.awt.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * Program demonstruj¹cy wykorzystanie w³asnego modelu drzewa. 
 * Wyœwietla pola obiektów.
 * @version 1.03 2007-08-01
 * @author Cay Horstmann
 */
public class ObjectInspectorTest
{
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {
               JFrame frame = new ObjectInspectorFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
}

/**
 *  Ramka zawieraj¹ca drzewo.
 */
class ObjectInspectorFrame extends JFrame
{
   public ObjectInspectorFrame()
   {
      setTitle("ObjectInspectorTest");
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      // jako pierwszy inspekcji poddany jest obiekt ramki

      Variable v = new Variable(getClass(), "this", this);
      ObjectTreeModel model = new ObjectTreeModel();
      model.setRoot(v);

      // tworzy i prezentuje drzewo

      tree = new JTree(model);
      add(new JScrollPane(tree), BorderLayout.CENTER);
   }

   private JTree tree;
   private static final int DEFAULT_WIDTH = 400;
   private static final int DEFAULT_HEIGHT = 300;
}

/**
 * Model drzewa opisuj¹cego strukturê powi¹zañ obiektów w jêzyku Java.
 * Wêz³y podrzêdne reprezentuj¹ sk³adowe obiektu.
 */
class ObjectTreeModel implements TreeModel
{
   /**
    * Tworzy puste drzewo.
    */
   public ObjectTreeModel()
   {
      root = null;
   }

   /**
    * Umieszcza zmienn¹ w korzeniu drzewa.
    * @param v zmienna opisywana przez drzewo
    */
   public void setRoot(Variable v)
   {
      Variable oldRoot = v;
      root = v;
      fireTreeStructureChanged(oldRoot);
   }

   public Object getRoot()
   {
      return root;
   }

   public int getChildCount(Object parent)
   {
      return ((Variable) parent).getFields().size();
   }

   public Object getChild(Object parent, int index)
   {
      ArrayList<Field> fields = ((Variable) parent).getFields();
      Field f = (Field) fields.get(index);
      Object parentValue = ((Variable) parent).getValue();
      try
      {
         return new Variable(f.getType(), f.getName(), f.get(parentValue));
      }
      catch (IllegalAccessException e)
      {
         return null;
      }
   }

   public int getIndexOfChild(Object parent, Object child)
   {
      int n = getChildCount(parent);
      for (int i = 0; i < n; i++)
         if (getChild(parent, i).equals(child)) return i;
      return -1;
   }

   public boolean isLeaf(Object node)
   {
      return getChildCount(node) == 0;
   }

   public void valueForPathChanged(TreePath path, Object newValue)
   {
   }

   public void addTreeModelListener(TreeModelListener l)
   {
      listenerList.add(TreeModelListener.class, l);
   }

   public void removeTreeModelListener(TreeModelListener l)
   {
      listenerList.remove(TreeModelListener.class, l);
   }

   protected void fireTreeStructureChanged(Object oldRoot)
   {
      TreeModelEvent event = new TreeModelEvent(this, new Object[] { oldRoot });
      EventListener[] listeners = listenerList.getListeners(TreeModelListener.class);
      for (int i = 0; i < listeners.length; i++)
         ((TreeModelListener) listeners[i]).treeStructureChanged(event);
   }

   private Variable root;
   private EventListenerList listenerList = new EventListenerList();
}

/**
 * Klasa reprezentuj¹ca zmienn¹ posiadaj¹c¹ typ, nazwê i wartoœæ.
 */
class Variable
{
   /**
    * Tworzy obiekt reprezentuj¹cy zmienn¹.
    * @param aType typ zmiennej
    * @param aName nazwa zmiennej
    * @param aValue wartoœæ zmiennej
    */
   public Variable(Class<?> aType, String aName, Object aValue)
   {
      type = aType;
      name = aName;
      value = aValue;
      fields = new ArrayList<Field>();

      // znajduje wszystkie pola, jeœli zmienna jest typu klasy,
      // nie rozwija jedynie ³añcuchów znaków i wartoœci null

      if (!type.isPrimitive() && !type.isArray() && !type.equals(String.class) && value != null)
      {
         // pobiera pola klasy i pola wszystkich jej klas bazowych
         for (Class<?> c = value.getClass(); c != null; c = c.getSuperclass())
         {
            Field[] fs = c.getDeclaredFields();
            AccessibleObject.setAccessible(fs, true);

            // pobiera wszystkie pola, które nie s¹ statyczne
            for (Field f : fs)
               if ((f.getModifiers() & Modifier.STATIC) == 0) fields.add(f);
         }
      }
   }

   /**
    * Zwraca wartoœæ zmiennej.
    * @return wartoœæ
    */
   public Object getValue()
   {
      return value;
   }

   /**
    * Zwraca wszystkie pola zmiennej, które nie s¹ statyczne.
    * @return tablica zmiennych opisuj¹cych pola
    */
   public ArrayList<Field> getFields()
   {
      return fields;
   }

   public String toString()
   {
      String r = type + " " + name;
      if (type.isPrimitive()) r += "=" + value;
      else if (type.equals(String.class)) r += "=" + value;
      else if (value == null) r += "=null";
      return r;
   }

   private Class<?> type;
   private String name;
   private Object value;
   private ArrayList<Field> fields;
}
