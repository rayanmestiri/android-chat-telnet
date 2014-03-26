package com.anarchy.anarchytelnet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.SocketException;

import org.apache.commons.net.telnet.TelnetClient;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

public class AnarchyTelnet extends Thread implements Runnable {

	public PrintStream out;
	public InputStream in;

	int len;
	String s;
	String nouveauMessage;

	Main main;

	public StringBuffer sb;

	boolean envoieMessage;

	public TelnetClient telnet;

	AnarchyTelnet(Main m) {
		main = m;

	}

	public void run() {
		telnet = new TelnetClient();

		sb = new StringBuffer();

		try {
			telnet.connect("88.161.45.180", 8080);

			telnet.setKeepAlive(true);
			in = telnet.getInputStream();
			out = new PrintStream(telnet.getOutputStream());

			while (true) {

				len = in.read();

				s = Character.toString((char) len);
				if (main.onPause) {
					nouveauMessage += s;
				}
				sb.append(s);
				main.mHandler.post(new Runnable() {

					public void run() {
						main.text.getText();
						main.text.setText(sb.toString());
						if (main.onPause) {
							notifyMessage(nouveauMessage);
						}
					}
				});

			}

		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void disconnect() {
		try {
			in.close();
			out.close();
			telnet.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void notifyMessage(String message) {
		Intent intent = new Intent(main, Main.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       
        
		PendingIntent pIntent = PendingIntent.getActivity(main, 0, intent, 0);

		// build notification
		// the addAction re-use the same intent to keep the example short
		Notification n = new Notification.Builder(main)
				.setContentTitle("Nouveau message").setContentText(message)
				.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent)
				.setAutoCancel(true)
				// .addAction(R.drawable.ic_launcher, "Call", pIntent)
				.setStyle(new Notification.BigTextStyle().bigText(message))
				.build();

		main.notifyMessage(n);

	}

}
