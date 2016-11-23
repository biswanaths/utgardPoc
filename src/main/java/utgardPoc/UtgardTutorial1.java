package utgardPoc;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
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
        ci.setHost("localhost");             
        ci.setUser(""); //System user name
        ci.setPassword(""); //password for the same
        
        /*ci.setDomain("localhost");*/
        //ci.setProgId("SWToolbox.TOPServer.V5");
        ci.setClsid("F8582CF2-88FB-11D0-B850-00C0F0104305"); // if ProgId is not working, try it using the Clsid instead
        final String itemId = "Random.Boolean";
        final String itemId1 = "Random.ArrayOfString";
        final String itemId2 = "Random.Int1";
        // create a new server
        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
         
        try {
            // connect to server
        	
            server.connect();
            // add sync access, poll every 500 ms
            
            final AccessBase access = new SyncAccess(server, 500);
            //access.addItem(itemId, new DataExtension());
            access.addItem(itemId1, new DataExtension());
            
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
	public void changed(Item item, ItemState state) {
        // Building a JSON string with value recieved
    	
        String record;
		try {
			record = "[ {" +"\""+"name"+"\" :\""+" itemId1" + "\",\""+"timestamp"+"\" :"+ state.getTimestamp().getTime().getTime()+ ",\""+"value"+"\" : "
							+convertToNativeType(state.getValue())
							//+value.replace("[", "").replace("]", "")
							+ ", \"group\": " + item.getGroup().getName();
			
			System.out.println(" record is " + record);
		} catch (JIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Object convertToNativeType(final JIVariant type) {
		Object defaultObj = null;
	    try {
	    	
	        if (type.isArray()) {
	            final ArrayList<Object> objs = new ArrayList<Object>();
	            final Object [] array = (Object[])type.getObjectAsArray().getArrayInstance();

	            for (final Object element : array) {
	            	//System.out.println("Class is : " + element.getClass());
	            	if(element.getClass().toString().contains("JIString"))
	            	{
	            		String temp = element.toString();	            		
	            		temp = temp.replace("[Type: 1 , [", "").replace("]]", "");	            		
	            		objs.add(temp);
	            	}
	            		
	            }
	            
	            return objs;
	        }	       
	        

	        switch (type.getType()) {
	            case JIVariant.VT_NULL:
	                return null;
	            case JIVariant.VT_BSTR:
	                return type.getObjectAsString().getString();
	            case JIVariant.VT_I2: // sint16
	                return type.getObjectAsShort();
	            case JIVariant.VT_I4:
	                return type.getObjectAsInt();
	            case JIVariant.VT_UI1: // uint8 (convert to Java Number)
	                return type.getObjectAsUnsigned().getValue();
	            case JIVariant.VT_BOOL:
	                return type.getObjectAsBoolean();
	            case JIVariant.VT_DECIMAL:
	                return type.getObjectAsFloat();
	            case JIVariant.VT_DATE:
	                return type.getObjectAsDate();
	            default:
	               System.out.println("Unknown type presented (" + type.getType() + "), defaulting to Object: " + type.toString());
	               return defaultObj;
	        }
	    } catch (final JIException e) {
	        System.out.println("Failed to conver WMI type to native object: " + e.getMessage());
	    }
	    return defaultObj;
	}
	
}
