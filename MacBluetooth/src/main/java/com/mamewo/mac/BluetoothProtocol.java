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

public class BluetoothProtocol {

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

	static final UUID OBEX_FILE_TRANSFER = new UUID(0x1106);

	// show services of remote devices
	public static Vector discoverServices(Vector devices) throws IOException,
			InterruptedException {
		final Vector/* <String> */serviceFound = new Vector();

		// TODO change UUID
		UUID serviceUUID = OBEX_FILE_TRANSFER;
		// if ((args != null) && (args.length > 0)) {
		// serviceUUID = new UUID(args[0], false);
		// }

		final Object serviceSearchCompletedEvent = new Object();

		DiscoveryListener listener = new DiscoveryListener() {

			public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
			}

			public void inquiryCompleted(int discType) {
			}

			public void servicesDiscovered(int transID,
					ServiceRecord[] servRecord) {
				for (int i = 0; i < servRecord.length; i++) {
					String url = servRecord[i].getConnectionURL(
							ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
					if (url == null) {
						continue;
					}
					serviceFound.add(url);
					DataElement serviceName = servRecord[i]
							.getAttributeValue(0x0100);
					if (serviceName != null) {
						System.out.println("service " + serviceName.getValue()
								+ " found " + url);
					} else {
						System.out.println("service found " + url);
					}
				}
			}

			public void serviceSearchCompleted(int transID, int respCode) {
				System.out.println("service search completed!");
				synchronized (serviceSearchCompletedEvent) {
					serviceSearchCompletedEvent.notifyAll();
				}
			}

		};

		UUID[] searchUuidSet = new UUID[] { serviceUUID };
		int[] attrIDs = new int[] { 0x0100 // Service name
		};

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
