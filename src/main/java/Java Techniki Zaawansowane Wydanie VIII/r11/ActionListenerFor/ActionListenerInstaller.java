import java.awt.event.*;
import java.lang.reflect.*;

/**
 * @version 1.00 2004-08-17
 * @author Cay Horstmann
 */
public class ActionListenerInstaller
{
   /**
    * Przetwarza wszystkie adnotacje ActionListenerFor danego obiektu.
    * @param obj obiekt, którego metody mog¹ posiadaæ adnotacje ActionListenerFor
    */
   public static void processAnnotations(Object obj)
   {
      try
      {
         Class<?> cl = obj.getClass();
         for (Method m : cl.getDeclaredMethods())
         {
            ActionListenerFor a = m.getAnnotation(ActionListenerFor.class);
            if (a != null)
            {
               Field f = cl.getDeclaredField(a.source());
               f.setAccessible(true);
               addListener(f.get(obj), obj, m);
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Dodaje obiekt nas³uchuj¹cy wywo³uj¹cy dan¹ metodê.
    * @param source Ÿród³o zdarzeñ, dla którego instalowany jest obiekt nas³uchuj¹cy
    * @param param niejawny parametr metody wywo³ywanej przez obiekt nas³uchuj¹cy
    * @param m metoda wywo³ywana przez obiekt nas³uchuj¹cy
    */
   public static void addListener(Object source, final Object param, final Method m)
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
   {
      InvocationHandler handler = new InvocationHandler()
         {
            public Object invoke(Object proxy, Method mm, Object[] args) throws Throwable
            {
               return m.invoke(param);
            }
         };

      Object listener = Proxy.newProxyInstance(null,
            new Class[] { java.awt.event.ActionListener.class }, handler);
      Method adder = source.getClass().getMethod("addActionListener", ActionListener.class);
      adder.invoke(source, listener);
   }
}

