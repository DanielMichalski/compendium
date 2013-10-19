import java.rmi.*;

/**
   Zdalny interfejs prostego magazynu.
   @version 1.0 2007-10-09
   @author Cay Horstmann
*/
public interface Warehouse extends Remote
{  
   double getPrice(String description) throws RemoteException;
}
