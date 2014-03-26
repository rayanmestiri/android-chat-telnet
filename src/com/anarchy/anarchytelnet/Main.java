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
	AnarchyTelnet telnet;
	Button envoyer;
	public Handler mHandler;

	EnvoieMessage mess;

	public TextView text;
	public EditText editText;

	public NotificationManager notificationManager;

	public boolean onPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		System.out.println("create");
		onPause = false;

		if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
			finish();
			return;
		}

		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		mHandler = new Handler();

		text = (TextView) findViewById(R.id.text);
		editText = (EditText) findViewById(R.id.editText1);

		text.setMovementMethod(new ScrollingMovementMethod());

		envoyer = (Button) findViewById(R.id.button1);
		envoyer.setOnClickListener(this);

		telnet = new AnarchyTelnet(this);
		telnet.start();

		mess = new EnvoieMessage(this);

		// prepare intent which is triggered if the
		// notification is selected

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

	@Override
	public void onClick(View v) {
		mess.envoie();
	}

	public void notifyMessage(Notification n) {
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(0, n);

	}

}