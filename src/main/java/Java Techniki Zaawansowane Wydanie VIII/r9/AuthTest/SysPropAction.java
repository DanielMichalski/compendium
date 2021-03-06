import java.security.*;

/**
 * Wyszukuje właściwość systemową.
 * @version 1.01 2007-10-06
 * @author Cay Horstmann
 */
public class SysPropAction implements PrivilegedAction<String>
{
   /**
    * Konstruktor.
    * @param propertyName nazwa właściwości (na przykład "user.home")
    */
   public SysPropAction(String propertyName)
   {
      this.propertyName = propertyName;
   }

   public String run()
   {
      return System.getProperty(propertyName);
   }

   private String propertyName;
}


