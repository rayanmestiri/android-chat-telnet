package com.anarchy.anarchytelnet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.SocketException;
import org.apache.commons.net.telnet.TelnetClient;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

// classe qui gère la réception de messages

public class AnarchyTelnet extends Thread implements Runnable {
	// reference sur l'activite principale
	Main main;
		
	// flux de sortie
	public PrintStream out;
	// flux d'entree
	public InputStream in;
	
	// donnees recues
	int data;
	// donnees converties en string
	String stringData;
	// le message est constitue de l'ensemble des donnees recues
	String nouveauMessage;

	/* le string buffer contient tous les messages (les donnees recues) 
	il est prevu pour etre modifier par plusieurs threads sans problemes
	c'est comme ca que les messages s'ajoutent au fur et a mesure*/
	
	public StringBuffer sb;
	
	// le string nouveauMessage sert pour les notifications car il est initialise seulement quand l'appli est en pause

	/* TelnetClient est l'objet principal pour gerer les communications telnet.
	On peut aussi le faire en TCP classique mais parmis les differentes methodes existantes, 
	j'ai trouve que celle-ci fonctionne le mieux car c'est un objet programme pour */
	public TelnetClient telnet;

	AnarchyTelnet(Main m) {
		// on recupere l'activite principale
		main = m;
	}

	public void run() {
		telnet = new TelnetClient();
		// il n'y a qu'un seul StringBuffer qui contient tous les messages
		sb = new StringBuffer();
		
		try {
			// le thread se connecte à anarchy.rayanmestiri.com
			telnet.connect("88.161.45.180", 8080);
			// on etabli une connexion persistante
			telnet.setKeepAlive(true);
			// on recupere le flux entrant
			in = telnet.getInputStream();
			// et le flux sortant
			out = new PrintStream(telnet.getOutputStream());
			
			while (true) {
				// on lit les donnees du flux entrant
				data = in.read();
				// on cast cela en string
				stringData = Character.toString((char) data);
				// on prepare un nouveau message
				if (main.onPause) {
					nouveauMessage += stringData;
				}
				// on ajoute ces donnees dans le StringBuffer
				sb.append(s);
				/* on lance un nouveau thread pour modifier le texte dans l'appli
				c'est indispensable car sinon on ne peut pas modifier l'interface
				de l'activite de maniere asynchrone  et ca ferait tout ramer*/
				main.mHandler.post(new Runnable() {
					public void run() {
						// ne sert a rien (j'ai oublie de l'enlever je crois)
						main.text.getText();
						// on change le texte de l'interface que l'on recupere depuis le StringBuffer (pas de problemes entre les threads)
						main.text.setText(sb.toString());
						if (main.onPause) {
							// si l'app est en pause, on notifie
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
	
	// methode de deconnexion non utilisee pour le moment

	public void disconnect() {
		try {
			// on ferme les flux
			in.close();
			out.close();
			// on deconnecte
			telnet.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// methode de notification
	public void notifyMessage(String message) {
		// l'intent qui sera effectue au clic de la notif
		Intent intent = new Intent(main, Main.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       		
       		// l'intent fait parti d'un PendingIntent car c'est ce que les notifs utilisent
		PendingIntent pIntent = PendingIntent.getActivity(main, 0, intent, 0);

		// creation de la notification
		Notification n = new Notification.Builder(main)
				.setContentTitle("Nouveau message").setContentText(message)
				// c'est la qu'on donne l'intent
				.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent)
				.setAutoCancel(true)
				// style qui permet d'agrandir la notif
				.setStyle(new Notification.BigTextStyle().bigText(message))
				.build();
		// on apelle la methode pour que la notification soit envoye au systeme
		main.notifyMessage(n);

	}

}
