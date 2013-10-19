import javax.security.auth.callback.*;

/**
 * Prosty obiekt obs³ugi prezentuj¹cy nazwê u¿ytkownika i jego has³o.
 * @version 1.0 2004-09-14
 * @author Cay Horstmann
 */
public class SimpleCallbackHandler implements CallbackHandler
{
   /**
    * Konstuktor.
    * @param username nazwa u¿ytkownika
    * @param password tablica znaków zawieraj¹ca has³o    
    */
   public SimpleCallbackHandler(String username, char[] password)
   {
      this.username = username;
      this.password = password;
   }

   public void handle(Callback[] callbacks)
   {
      for (Callback callback : callbacks)
      {
         if (callback instanceof NameCallback)
         {
            ((NameCallback) callback).setName(username);
         }
         else if (callback instanceof PasswordCallback)
         {
            ((PasswordCallback) callback).setPassword(password);
         }
      }
   }

   private String username;
   private char[] password;
}

