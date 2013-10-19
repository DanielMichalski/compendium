import java.rmi.*;
import java.util.*;

/**
   Zdalny interfejs prostego domu towarowego.
   @version 1.0 2007-10-09
   @author Cay Horstmann
*/
public interface Warehouse extends Remote
{  
   double getPrice(String description) throws RemoteException;
   Product getProduct(List<String> keywords) throws RemoteException;
}
