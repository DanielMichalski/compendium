import java.rmi.*;
import javax.naming.*;

/**
 * Program tworzy instancjê zdalnego obiektu
 * reprezentuj¹cego dom towarowy, 
 * rejestruje j¹ w serwisie nazw i oczekuje wywo³añ metod obiektu przez klientów.
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
