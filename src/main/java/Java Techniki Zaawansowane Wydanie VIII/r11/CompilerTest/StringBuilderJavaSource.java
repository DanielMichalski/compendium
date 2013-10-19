import java.net.*;
import javax.tools.*;

/**
 * Umieszcza kod w obiekcie typu StringBuilder.
 * @version 1.00 2007-11-02
 * @author Cay Horstmann
 */
public class StringBuilderJavaSource extends SimpleJavaFileObject
{
   /**
    * Tworzy nowy obiekt StringBuilderJavaSource
    * @param name nazwa pliku Ÿród³owego reprezentowanego przez ten obiekt
    */
   public StringBuilderJavaSource(String name)
   {
      super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), 
         Kind.SOURCE);
      code = new StringBuilder();
   }

   public CharSequence getCharContent(boolean ignoreEncodingErrors)
   {
      return code;
   }

   public void append(String str)
   {
      code.append(str);
      code.append('\n');
   }

   private StringBuilder code;
}

