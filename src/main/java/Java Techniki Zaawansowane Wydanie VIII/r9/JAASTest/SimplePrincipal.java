import java.security.*;

/**
 * Nadzorca (na przyk³ad "role=HR" lub "username=harry").
 * @version 1.0 2004-09-14
 * @author Cay Horstmann
 */
public class SimplePrincipal implements Principal
{
   /**
    * Tworzy obiekt SimplePrincipal przechowuj¹cy opis i wartoœæ.
    * @param roleName nazwa roli
    */
   public SimplePrincipal(String descr, String value)
   {
      this.descr = descr;
      this.value = value;
   }

   /**
    * Zwraca nazwê roli
    * @return nazwa roli
    */
   public String getName()
   {
      return descr + "=" + value;
   }

   public boolean equals(Object otherObject)
   {
      if (this == otherObject) return true;
      if (otherObject == null) return false;
      if (getClass() != otherObject.getClass()) return false;
      SimplePrincipal other = (SimplePrincipal) otherObject;
      return getName().equals(other.getName());
   }

   public int hashCode()
   {
      return getName().hashCode();
   }

   private String descr;
   private String value;
}

