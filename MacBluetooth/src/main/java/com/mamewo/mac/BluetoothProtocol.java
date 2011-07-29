package com.mamewo.mac;

/** 
 * Sample code of bluecove in following site
 * http://www.bluecove.org/bluecove/apidocs/overview-summary.html
 */

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

import com.intel.bluetooth.BluetoothConsts;

public class BluetoothProtocol {

//	static final UUID OBEX_FILE_TRANSFER = new UUID(0x1106);
//	static final UUID RFCOMM = new UUID(0x0003);
	
	public static Vector discoverDevices() throws IOException,
			InterruptedException {
		// RemoteDevice
		final Vector devicesDiscovered = new Vector();
		final Object inquiryCompletedEvent = new Object();

		System.out.println("discoverDevices start");
		DiscoveryListener listener = new DiscoveryListener() {

			public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
				System.out.println("Device " + btDevice.getBluetoothAddress()
						+ " found");
				devicesDiscovered.addElement(btDevice);
				try {
					System.out.println("     name "
							+ btDevice.getFriendlyName(false));
				} catch (IOException cantGetDeviceName) {
				}
			}

			public void inquiryCompleted(int discType) {
				System.out.println("Device Inquiry completed!");
				synchronized (inquiryCompletedEvent) {
					inquiryCompletedEvent.notifyAll();
				}
			}

			public void serviceSearchCompleted(int transID, int respCode) {
			}

			public void servicesDiscovered(int transID,
					ServiceRecord[] servRecord) {
			}
		};

		synchronized (inquiryCompletedEvent) {
			boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent()
					.startInquiry(DiscoveryAgent.GIAC, listener);
			if (started) {
				System.out.println("wait for device inquiry to complete...");
				inquiryCompletedEvent.wait();
				System.out.println(devicesDiscovered.size()
						+ " device(s) found");
			}
		}
		return devicesDiscovered;
	}

	public static class ServiceDesc {
		final String _name;
		final String _url;
		protected ServiceDesc(String name, String url) {
			_name = name;
			_url = url;
		}
		public String getName() {
			return _name;
		}
		public String getUrl() {
			return _url;
		}
	}
	
	// show services of remote devices
	public static Vector discoverServices(Vector devices) throws IOException,
			InterruptedException {
		final Vector/* <String> */serviceFound = new Vector();

		// TODO change UUID
		UUID serviceUUID = BluetoothConsts.RFCOMM_PROTOCOL_UUID;

		final Object serviceSearchCompletedEvent = new Object();

		DiscoveryListener listener = new DiscoveryListener() {

			public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
			}

			public void inquiryCompleted(int discType) {
			}

			public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
				for (int i = 0; i < servRecord.length; i++) {
					String url = servRecord[i].getConnectionURL(
							ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
					if (url == null) {
						continue;
					}
					DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
					String name = null;
					if (serviceName != null) {
						name = (String)serviceName.getValue();
					}
					//DataElement serviceID = servRecord[i].getAttributeValue(0x0003);
					serviceFound.add(new ServiceDesc(name, url));
					
					if (serviceName != null) {
						System.out.printf("service(0x%X) %s found: %s\n", transID, name, url);
					} else {
						System.out.println("service found " + url);
					}
				}
			}

			public void serviceSearchCompleted(int transID, int respCode) {
				System.out.printf("service search completed!: 0x%X\n", transID);
				switch (respCode) {
				case SERVICE_SEARCH_COMPLETED:
					System.out.println ("COMPLETED");
					break;
				case SERVICE_SEARCH_TERMINATED:
					System.out.println ("TERMINATED");
					break;
				case SERVICE_SEARCH_ERROR:
					System.out.println ("ERROR");
					break;
				case SERVICE_SEARCH_NO_RECORDS:
					System.out.println ("NORECORDS");
					break;
				case SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
					System.out.println ("NOT_REACHABLE");
					break;
				default:
					System.out.println ("Unknown case");
					break;
				}
				synchronized (serviceSearchCompletedEvent) {
					serviceSearchCompletedEvent.notifyAll();
				}
			}
		};

		//TODO: Parameterize Protocol
		UUID[] searchUuidSet = new UUID[] { BluetoothConsts.RFCOMM_PROTOCOL_UUID };
//				BluetoothConsts.SERIAL_PORT_UUID};
//					BluetoothConsts.L2CAP_PROTOCOL_UUID,
		//					BluetoothConsts.OBEX_PROTOCOL_UUID,
		int[] attrIDs = new int[] { 0x0100 }; //Service name?

		for (Enumeration en = devices.elements(); en.hasMoreElements();) {
			RemoteDevice btDevice = (RemoteDevice) en.nextElement();

			synchronized (serviceSearchCompletedEvent) {
				System.out.println("search services on "
						+ btDevice.getBluetoothAddress() + " "
						+ btDevice.getFriendlyName(false));
				LocalDevice
						.getLocalDevice()
						.getDiscoveryAgent()
						.searchServices(attrIDs, searchUuidSet, btDevice,
								listener);
				serviceSearchCompletedEvent.wait();
			}
		}
		return serviceFound;
	}
}
