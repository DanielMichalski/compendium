import java.applet.*;
import java.awt.*;

/**
 * Program demonstruj¹cy dzia³anie weryfikatora kodu maszyny
 * wirtualnej. Jeœli zmodyfikujemy plik klasy, korzystaj¹c
 * z edytora kodu szesnastkowego, zostanie to wykryte przez weryfikator
 * podczas próby uruchomienia programu.
 * @version 1.00 1997-09-10
 * @author Cay Horstmann
 */
public class VerifierTest extends Applet
{
   public static void main(String[] args)
   {
      System.out.println("1 + 2 == " + fun());
   }

   /**
    * Metoda wyznaczaj¹ca wynik dzia³ania 1 + 2
    * @return 3, jeœli kod metody fun nie zosta³ zmodyfikowany
    */
   public static int fun()
   {
      int m;
      int n;
      m = 1;
      n = 2;
      // powy¿sz¹ instrukcjê nale¿y zmieniæ w pliku klasy 
      // na "m = 2", korzystaj¹c z edytora kodu szesnastkowego
      int r = m + n;
      return r;
   }

   public void paint(Graphics g)
   {
      g.drawString("1 + 2 == " + fun(), 20, 20);
   }
}
