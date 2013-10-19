import java.rmi.*;
import javax.naming.*;

/**
 * Program serwera tworzy instancjê zdalnego obiektu, 
 * umieszcza go w rejestrze i oczekuje na wywo³ania klientów.
 * @version 1.12 2007-10-09
 * @author Cay Horstmann
 */
public class WarehouseServer
{
   public static void main(String[] args) throws RemoteException, NamingException
   {
      System.setProperty("java.security.policy", "server.policy");
      System.setSecurityManager(new SecurityManager());
      
      System.out.println("Constructing server implementation...");
      WarehouseImpl backupWarehouse = new WarehouseImpl(null);
      WarehouseImpl centralWarehouse = new WarehouseImpl(backupWarehouse);
      
      centralWarehouse.add("toaster", new Product("Blackwell Toaster", 23.95));
      backupWarehouse.add("java", new Book("Core Java vol. 2", "0132354799", 44.95));

      System.out.println("Binding server implementation to registry...");
      Context namingContext = new InitialContext();
      namingContext.bind("rmi:central_warehouse", centralWarehouse);

      System.out.println("Waiting for invocations from clients...");
   }
}
