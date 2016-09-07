package utgardPoc;

import java.util.concurrent.Executors;
import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.AccessBase;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.SyncAccess;

public class UtgardTutorial1 {
	
	public static void main(String[] args) throws Exception {
        // create connection information
        final ConnectionInformation ci = new ConnectionInformation();
        //Details about OPC server.
        ci.setHost("IP");             
        ci.setUser("User Name");
        ci.setPassword("Password");
        
        /*ci.setDomain("localhost");*/
        //ci.setProgId("SWToolbox.TOPServer.V5");
        ci.setClsid("F8582CF2-88FB-11D0-B850-00C0F0104305"); // if ProgId is not working, try it using the Clsid instead
        final String itemId = "Random.Boolean";
        // create a new server
        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
         
        try {
            // connect to server
        	
            server.connect();
            // add sync access, poll every 500 ms
            
            final AccessBase access = new SyncAccess(server, 500);
            access.addItem(itemId, new DataExtension());
            
            // start reading
            access.bind();
            // wait a little bit
            Thread.sleep(10 * 1000);
            // stop reading
            access.unbind();
        } catch (final JIException e) {
            System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        }
    }

}

class DataExtension implements DataCallback 
{
	public void changed(Item item, ItemState itemState) {
		 System.out.println(itemState);
	}
	
}
