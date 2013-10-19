import java.security.*;
import java.util.*;

/**
 * Pozwolenie kontroluj¹ce wyst¹pienie okreœlonych s³ów.
 * @version 1.00 1999-10-23
 * @author Cay Horstmann
 */
public class WordCheckPermission extends Permission
{
   /**
    * Tworzy obiekt pozwolenia
    * @param target lista s³ów oddzielonych przecinkami
    * @param anAction "insert" lub "avoid"
    */
   public WordCheckPermission(String target, String anAction)
   {
      super(target);
      action = anAction;
   }

   public String getActions()
   {
      return action;
   }

   public boolean equals(Object other)
   {
      if (other == null) return false;
      if (!getClass().equals(other.getClass())) return false;
      WordCheckPermission b = (WordCheckPermission) other;
      if (!action.equals(b.action)) return false;
      if (action.equals("insert")) return getName().equals(b.getName());
      else if (action.equals("avoid")) return badWordSet().equals(b.badWordSet());
      else return false;
   }

   public int hashCode()
   {
      return getName().hashCode() + action.hashCode();
   }

   public boolean implies(Permission other)
   {
      if (!(other instanceof WordCheckPermission)) return false;
      WordCheckPermission b = (WordCheckPermission) other;
      if (action.equals("insert"))
      {
         return b.action.equals("insert") && getName().indexOf(b.getName()) >= 0;
      }
      else if (action.equals("avoid"))
      {
         if (b.action.equals("avoid")) return b.badWordSet().containsAll(badWordSet());
         else if (b.action.equals("insert"))
         {
            for (String badWord : badWordSet())
               if (b.getName().indexOf(badWord) >= 0) return false;
            return true;
         }
         else return false;
      }
      else return false;
   }

   /**
    * Pobiera s³owa okreœlone przez pozwolenie.
    * @return zbiór zabronionych s³ów
    */
   public Set<String> badWordSet()
   {
      Set<String> set = new HashSet<String>();
      set.addAll(Arrays.asList(getName().split(",")));
      return set;
   }

   private String action;
}
