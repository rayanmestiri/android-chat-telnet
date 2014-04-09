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
	// reference sur l'activite principale
	Main main;
	// l'objet telnet
	AnarchyTelnet telnet;
	// notre IP
	String myIp;

	EnvoieMessage(Main m) {
		// on recupere l'activite
		main = m;
		// on recupere le telnet
		telnet = main.telnet;
		
		try {
			// affectation de l'ip grace a la methode ip() ecrite plus bas
			myIp = ip()+"";
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* methode appelee lorsque le clavier est sorti 
		c'est juste pour faire d'avoir que le bouton "entree" soit un bouton "envoyer" 
		mais c'est pas forcemment necessaire et ca empeche de faire des sauts de lignes */
		main.editText.setOnEditorActionListener(new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        boolean handled = false;
		        // si l'action c'est "envoyer"
		        if (actionId == EditorInfo.IME_ACTION_SEND) {
		      		// on evoie et puis voilà 
		      		envoie();
		      		handled = true;
		        }
		        return handled;
		    }
		});
	}

	// methode d'envoi d'un message
	public void envoie() {
		/* on doit faire un nouveau thread pour ne pas bloquer la reception des messages
		et comme on modifie l'interface de l'activite principale, c'est mieux de faire un nouveau thread */
		main.mHandler.post(new Runnable() {
			public void run() {
				// on ajoute le texte dans le StringBuffer du telnet
				telnet.sb.append(myIp+ " : " + main.editText.getText()+"\n\r");
				// on change le texte dans l'interface
				main.text.setText(telnet.sb.toString());
				// on ecrit dans le flux sortant
				telnet.out.println(main.editText.getText().toString());
				// on vide le flux sortant
				telnet.out.flush();
				// on efface la zone de saisie
				main.editText.setText("");
				
				
			}
		});
	}
	
	// methode de recuperation de l'ip
	
	// c'est un peu galere mais faut eviter de prendre l'adresse de boucle locale, l'ipv6 etc.
	static InetAddress ip() throws SocketException {
		// on recupere toutes les interfaces
		Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
	    	NetworkInterface ni;
	    	while (nis.hasMoreElements()) {
	        	ni = nis.nextElement();
			if (!ni.isLoopback()/*pas la boucle locale*/ && ni.isUp()/*et active*/) {
	            		for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
	                	//filtre ipv4/ipv6
	                		if (ia.getAddress().getAddress().length == 4) {
	                    		//4 pour ipv4, 16 pour ipv6
	                    			return ia.getAddress();
	                		}
	            		}
	        	}
	    	}
	    return null;
	}
	
}
