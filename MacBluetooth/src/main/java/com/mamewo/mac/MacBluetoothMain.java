/*
 * Takashi Masuyama <mamewotoko@gmail.com>
 */

package com.mamewo.mac;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    
    private static void printAsHex (byte[] b, int len) {
    	for (int i = 0; i < len; i++) {
    		System.out.printf("0x%02X ", b[i]);
    	}
    	System.out.println("");
    }
    
    private static class OutputThread extends Thread {
    	OutputStream _os;
    	boolean _done = false;
    	
    	public OutputThread(OutputStream os) {
    		_os = os;
    	}
    	public void run() {
    		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    		while (! _done) {
	    		try {
	    			String line = br.readLine();
	    			if (line.length() > 0) {
	    				_os.write(line.getBytes(Charset.forName("UTF-8")));
	    			}
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
    		}
    	}
    	public void cancel() {
    		_done = true;
    	}
    }
    
    private static String INSECURE_URL = "btspp://localhost:8ce255c0200a11e0ac640800200c9a66;name=BluetoothChatInsecure";
    private static String SECURE_URL = "btspp://localhost:fa87c0d0afac11de8a390800200c9a66;name=BluetoothChatSecure";

    public static void main(String argv[]) {
    	boolean done = false;
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
	    	try {
	    		//mainServiceSearch(null);
	    		//btspp
	    		//TODO: discover insecure UUID from device! (copied from BluetoothChatService.java)
	    		System.out.println("before wait: default charset = " + Charset.defaultCharset());
				sock = cn.acceptAndOpen();
	    		System.out.println("accept!");
	   			InputStream is = sock.openInputStream();
	   			OutputStream os = sock.openOutputStream ();

	   			t = new MacBluetoothMain.OutputThread (os);
	   			t.start();
	   			
	   		    while (! done) {
	    			int len = is.read(buffer);
	    			//printAsHex(buffer, len);
	    			if (len > 0) {
	    				System.out.printf("received message(%d): %s\n", len, new String(buffer, 0, len, Charset.forName("UTF-8")));
	    			}
	    		}
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	} finally {
	    		t.interrupt();
	    		t = null;
	    		try {
	    			sock.close();
	    		} catch (IOException e) {
	    		//
	    		}
	    	}
    	}
    }
}