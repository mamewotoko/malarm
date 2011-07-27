
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Vector;
import javax.bluetooth.*;
import java.util.Enumeration;
import javax.obex.*;
import javax.microedition.io.*;

/**
 * Minimal Device Discovery example.
 */
public class MacBluetoothMain {
    public static final Vector/*<RemoteDevice>*/ devicesDiscovered = new Vector();

    public static void mainDiscovery(String[] args) throws IOException, InterruptedException {

        final Object inquiryCompletedEvent = new Object();

        devicesDiscovered.clear();

        DiscoveryListener listener = new DiscoveryListener() {

            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                System.out.println("Device " + btDevice.getBluetoothAddress() + " found");
                devicesDiscovered.addElement(btDevice);
                try {
                    System.out.println("     name " + btDevice.getFriendlyName(false));
                } catch (IOException cantGetDeviceName) {
                }
            }

            public void inquiryCompleted(int discType) {
                System.out.println("Device Inquiry completed!");
                synchronized(inquiryCompletedEvent){
                    inquiryCompletedEvent.notifyAll();
                }
            }

            public void serviceSearchCompleted(int transID, int respCode) {
            }

            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
            }
        };

        synchronized(inquiryCompletedEvent) {
            boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
            if (started) {
                System.out.println("wait for device inquiry to complete...");
                inquiryCompletedEvent.wait();
                System.out.println(devicesDiscovered.size() +  " device(s) found");
            }
        }
    }
    static final UUID OBEX_FILE_TRANSFER = new UUID(0x1106);

    public static final Vector/*<String>*/ serviceFound = new Vector();

    public static void mainServiceSearch(String[] args) throws IOException, InterruptedException {

        // First run RemoteDeviceDiscovery and use discoved device
        mainDiscovery(null);

        serviceFound.clear();

        UUID serviceUUID = OBEX_FILE_TRANSFER;
        if ((args != null) && (args.length > 0)) {
            serviceUUID = new UUID(args[0], false);
        }

        final Object serviceSearchCompletedEvent = new Object();

        DiscoveryListener listener = new DiscoveryListener() {

            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
            }

            public void inquiryCompleted(int discType) {
            }

            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
                for (int i = 0; i < servRecord.length; i++) {
                    String url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                    if (url == null) {
                        continue;
                    }
                    serviceFound.add(url);
                    DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
                    if (serviceName != null) {
                        System.out.println("service " + serviceName.getValue() + " found " + url);
                    } else {
                        System.out.println("service found " + url);
                    }
                }
            }

            public void serviceSearchCompleted(int transID, int respCode) {
                System.out.println("service search completed!");
                synchronized(serviceSearchCompletedEvent){
                    serviceSearchCompletedEvent.notifyAll();
                }
            }

        };

        UUID[] searchUuidSet = new UUID[] { serviceUUID };
        int[] attrIDs =  new int[] {
                0x0100 // Service name
        };

        for(Enumeration en = devicesDiscovered.elements(); en.hasMoreElements(); ) {
            RemoteDevice btDevice = (RemoteDevice)en.nextElement();

            synchronized(serviceSearchCompletedEvent) {
                System.out.println("search services on " + btDevice.getBluetoothAddress() + " " + btDevice.getFriendlyName(false));
                LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, btDevice, listener);
                serviceSearchCompletedEvent.wait();
            }
        }
    }
    
    private static void printAsHex (byte[] b, int len) {
    	for (int i = 0; i < len; i++) {
    		System.out.printf("0x%02X ", b[i]);
    	}
    	System.out.println("");
    }
    
    public static void main(String argv[]) {
    	boolean done = false;
    	byte[] buffer = new byte[1024];

    	try {
        	//mainServiceSearch(null);
    		LocalDevice local = LocalDevice.getLocalDevice();
    		local.setDiscoverable(DiscoveryAgent.GIAC);
    		System.out.println("before wait ‚Ü‚Á‚Ä‚é‚º‚Á‚Æ");
    		//btspp
    		//TODO: discover insecure UUID from device! (copied from BluetoothChatService.java)
    		StreamConnectionNotifier cn = (StreamConnectionNotifier) Connector.open("btspp://localhost:8ce255c0200a11e0ac640800200c9a66;name=BluetoothChatInsecure");
			StreamConnection sock = null;
			sock = cn.acceptAndOpen();
    		System.out.println("accept! ");
   			InputStream is = sock.openInputStream();
   		    while (! done) {
    			int len = is.read(buffer);
    			printAsHex(buffer, len);
    			System.out.printf("received message(%d): %s\n", len, new String(buffer, 0, len, Charset.forName("UTF-8")));
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}