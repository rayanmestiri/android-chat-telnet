package com.anarchy.anarchytelnet;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class EnvoieMessage {
	Main main;
	AnarchyTelnet telnet;
	String myIp;

	EnvoieMessage(Main m) {
		main = m;
		telnet = main.telnet;
		
		try {
			myIp = ip()+"";
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		main.editText.setOnEditorActionListener(new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        boolean handled = false;
		        if (actionId == EditorInfo.IME_ACTION_SEND) {
		            envoie();
		            handled = true;
		        }
		        return handled;
		    }
		});
	}

	public void envoie() {
		main.mHandler.post(new Runnable() {

			public void run() {

				telnet.sb.append(myIp+ " : " + main.editText.getText()+"\n\r");
				main.text.setText(telnet.sb.toString());

				telnet.out.println(main.editText.getText().toString());
				telnet.out.flush();
				
				main.editText.setText("");
				
				
			}
		});
	}
	
	
	static InetAddress ip() throws SocketException {
	    Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
	    NetworkInterface ni;
	    while (nis.hasMoreElements()) {
	        ni = nis.nextElement();
	        if (!ni.isLoopback()/*not loopback*/ && ni.isUp()/*it works now*/) {
	            for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
	                //filter for ipv4/ipv6
	                if (ia.getAddress().getAddress().length == 4) {
	                    //4 for ipv4, 16 for ipv6
	                    return ia.getAddress();
	                }
	            }
	        }
	    }
	    return null;
	}
	
}
