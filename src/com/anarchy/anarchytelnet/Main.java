package com.anarchy.anarchytelnet;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Main extends Activity implements OnClickListener {
	//thread qui tourne en fond pour accepter les donnees entrantres
	AnarchyTelnet telnet;
	
	//classe qui gere l'envoi des messages et la creation de notifications
	EnvoieMessage mess;

	//les autres classes utilisent le handler de la classe principale pour gerer leurs threads
	public Handler mHandler;

	//gestionnaire de notifications
	public NotificationManager notificationManager;

	//vues du layout principal
	Button envoyer;
	public TextView text;
	public EditText editText;

	/*on repere l'etat de pause pour n'afficher les notifs que lorsque l'appli est en pause. Meilleur systeme : definir l'appli comme un service : 
	http://developer.android.com/guide/components/services.html*/
	public boolean onPause;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		System.out.println("create");
		onPause = false;

		/*s'il la meme activity est deja lancee, on kill celle la 
		Le click sur les notifs recree une activity c'est un pb a resoudre
		peut etre avec un service et/ou un fichier xml qui sauvegarde les conversations*/
		if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
			finish();
			return;
		}

		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// declarations diverses et variées
		mHandler = new Handler();
		text = (TextView) findViewById(R.id.text);
		editText = (EditText) findViewById(R.id.editText1);
		// le texte de la conversation est scrollable
		text.setMovementMethod(new ScrollingMovementMethod());
		envoyer = (Button) findViewById(R.id.button1);
		// la methode OnClick est appelee au clic sur le bouton car on implemente un listener
		envoyer.setOnClickListener(this);

		// declarer le thread apres les declarations des vues pour quil puisse y acceder
		telnet = new AnarchyTelnet(this);
		telnet.start();

		mess = new EnvoieMessage(this);
	}

	protected void onPause() {
		super.onPause();
		onPause = true;
	}

	protected void onResume() {
		super.onResume();
		onPause = false;
		System.out.println("resume");
	}

	public void onClick(View v) {
		// envoie le message (commentaire bien utile)
		mess.envoie();
	}

	// methode que le thread AnarchyTelnet apelle quand il recoit des donnees et que lappli est en pause
	public void notifyMessage(Notification n) {
		// instructions pour ajouter une notification
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// le 0 est un id. On oeut avoir differents types de.notifications
		// par exemple un id lorsque qqun se connecte a lapli et un autre pour les messages
		notificationManager.notify(0, n);
	}

}
