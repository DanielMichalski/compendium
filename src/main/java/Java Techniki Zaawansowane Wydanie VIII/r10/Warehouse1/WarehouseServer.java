import java.rmi.*;
import javax.naming.*;

/**
 * Program tworzy instancj� zdalnego obiektu
 * reprezentuj�cego dom towarowy, 
 * rejestruje j� w serwisie nazw i oczekuje wywo�a� metod obiektu przez klient�w.
 * @version 1.12 2007-10-09
 * @author Cay Horstmann
 */
public class WarehouseServer
{
   public static void main(String[] args) throws RemoteException, NamingException
   {
      System.out.println("Constructing server implementation...");
      WarehouseImpl centralWarehouse = new WarehouseImpl();

      System.out.println("Binding server implementation to registry...");
      Context namingContext = new InitialContext();
      namingContext.bind("rmi:central_warehouse", centralWarehouse);

      System.out.println("Waiting for invocations from clients...");
   }
}
