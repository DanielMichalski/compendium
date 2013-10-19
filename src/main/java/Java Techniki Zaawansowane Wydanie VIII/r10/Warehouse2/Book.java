/**
 * Reprezentuje ksi¹¿kê posiadaj¹c¹ numer ISBN.
 * @version 1.0 2007-10-09
 * @author Cay Horstmann
 */
public class Book extends Product
{
   public Book(String title, String isbn, double price)
   {
      super(title, price);
      this.isbn = isbn;
   }
   
   public String getDescription()
   {
      return super.getDescription() + " " + isbn;
   }
   
   private String isbn;
}

