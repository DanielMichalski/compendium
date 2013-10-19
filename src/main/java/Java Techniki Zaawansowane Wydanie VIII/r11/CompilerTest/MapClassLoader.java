import java.util.*;

/**
 * Klasa implementuj�ca procedur� �adowania klas
 * umieszczonych na mapie, kt�rej kluczami s� nazwy klas,
 * a warto�ciami tablice kodu bajtowego.
 * @version 1.00 2007-11-02
 * @author Cay Horstmann
 */
public class MapClassLoader extends ClassLoader
{
   public MapClassLoader(Map<String, byte[]> classes)
   {
      this.classes = classes;
   }

   protected Class<?> findClass(String name) throws ClassNotFoundException
   {
      byte[] classBytes = classes.get(name);
      if (classBytes == null) throw new ClassNotFoundException(name);
      Class<?> cl = defineClass(name, classBytes, 0, classBytes.length);
      if (cl == null) throw new ClassNotFoundException(name);
      return cl;
   }

   private Map<String, byte[]> classes;
}

