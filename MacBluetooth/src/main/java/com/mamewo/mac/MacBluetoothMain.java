/**
 * Bluetooth chat using RFCOMM for Mac/Windows PC (not tested..)
 * Takashi Masuyama <mamewotoko@gmail.com>
 */

package com.mamewo.mac;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Vector;
import javax.bluetooth.*;
import javax.microedition.io.*;

import com.mamewo.mac.BluetoothProtocol.ServiceDesc;

/**
 * Minimal Device Discovery example.
 */
public class MacBluetoothMain {
//	private static final Charset _UTF8 = Charset.forName("UTF-8");

	//Android chat
	private static String INSECURE_URL = "btspp://localhost:8ce255c0200a11e0ac640800200c9a66;name=BluetoothChatInsecure";
	private static String SECURE_URL = "btspp://localhost:fa87c0d0afac11de8a390800200c9a66;name=BluetoothChatSecure";
	
	private static void printAsHex(byte[] b, int len) {
		for (int i = 0; i < len; i++) {
			System.out.printf("0x%02X ", b[i]);
		}
		System.out.println("");
	}

	private static class OutputThread extends Thread {
		final Writer _writer;
		final BufferedReader _br;
		boolean _done = false;
		
		public OutputThread(OutputStream os) {
			_writer= new BufferedWriter(new OutputStreamWriter(os));
			_br = new BufferedReader(new InputStreamReader(System.in));
		}

		public void run() {
			try {
				while (!_done) {
					String line = _br.readLine();
					if (line.length() > 0) {
						_writer.write(line);
						_writer.flush();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void cancel() {
			_done = true;
		}
	}

	//TODO: support multiple user?
	public static void startChatServer() {
		byte[] buffer = new byte[1024];
		StreamConnectionNotifier cn = null;
		try {
			LocalDevice local = LocalDevice.getLocalDevice();
			local.setDiscoverable(DiscoveryAgent.GIAC);
			cn = (StreamConnectionNotifier) Connector.open(INSECURE_URL);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			OutputThread t = null;
			StreamConnection sock = null;
			InputStream is = null;
			OutputStream os = null;
			try {
				System.out.println("accepting connection:");
				sock = cn.acceptAndOpen();
				System.out.println("accept!: " + Charset.defaultCharset());
				is = sock.openInputStream();
				os = sock.openOutputStream();

				t = new OutputThread(os);
				t.start();

				while (t.isAlive()) {
					//TODO: use timeout read?
					int len = is.read(buffer);
					// printAsHex(buffer, len);
					if (len > 0) {
						System.out.printf("received message(%d): %s\n", len, new String(buffer, 0, len));
					}
				}
				//TODO: check connection is live or not,
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				t.interrupt();
				t = null;
				try {
					sock.close();
					cn.close();
				} catch (IOException e) {
					//
				}
			}
		}
	}
	
	/**
	 * 
	 * @return Vector of ServiceDesc; pair of service name and URL
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static Vector discoverServices() throws IOException, InterruptedException {
		Vector v = BluetoothProtocol.discoverDevices();
		return BluetoothProtocol.discoverServices(v);
	}
	public static final String INSECURE_CHAT_SERVICENAME = "BluetoothChatInsecure";
	
	//RFCOMM
	public static void startChatClient() throws IOException, InterruptedException {
		Vector service = discoverServices();
		//find InsecureChat
		String servicename = INSECURE_CHAT_SERVICENAME;
		String url = null;
		for (int i = 0; i < service.size(); i++) {
			ServiceDesc desc = (ServiceDesc)service.get(i);
			if (desc.getName().equals(servicename)) {
				url = desc.getUrl();
				break;
			}
		}
		if (url == null) {
			System.out.println("startChatClient: cannot find chat service:" + servicename);
			return;
		}

		OutputStream os = null;
		InputStream is = null;
		byte[] buffer = new byte[1024];

		// RFCOMM
		// TODO: finalize con
		StreamConnection con = (StreamConnection) Connector.open(url);
		System.out.println("connected as client(RFCOMM)");
		os = con.openOutputStream();
		is = con.openInputStream();

		//BufferedReader br = new BufferedReader(new InputStreamReader(is));
		OutputThread t = new OutputThread(os);
		t.start();

		//TODO: define InputThread
		try {
			while (t.isAlive()) {
				int len = is.read(buffer);
				// printAsHex(buffer, len);
				if (len > 0) {
					System.out.printf("received message(%d): %s\n", len, new String(buffer, 0, len));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (t.isAlive()) {
				t.interrupt();
			}
			t = null;
			try {
				con.close();
			} catch (IOException e) {
				//
			}
		}
	}

	public static void main(String argv[]) {
		
		try {
			if (! LocalDevice.isPowerOn()) {
				System.err.println ("Please turn on bluetooth");
				return;
			}
			//To get address, device power must be on
			LocalDevice local = LocalDevice.getLocalDevice();
			System.out.println ("\tlocal bluetooth address: " + local.getBluetoothAddress());

			while (true) {
				if (argv.length > 0 && argv[0].equals("--client")) {
					System.out.println ("start as client");
					startChatClient();
				} else {
					System.out.println ("start as server");
					startChatServer();
				}
				//TODO: add obex?
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}