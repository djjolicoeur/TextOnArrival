package com.jolicosoft.getgeo;

//import com.jolicosoft.getgeo.TextService.LocalBinder;

import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMAdView.MMAdListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SendSMS extends Activity {
	TextService ts;
	ProgressDialog pd;
	boolean isBound = false;
	boolean deletedGci = false;
	GeoContactInfo gci = null;

	private ServiceConnection tsConnection = new ServiceConnection() {

		@SuppressWarnings("unchecked")
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			// LocalBinder binder = (LocalBinder) service;
			ts = ((LocalBinder<TextService>) service).getService();
			// ts = binder.getService();
			isBound = true;
			Log.d("NOTIFY: ", "SendSMS is Bound");
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			isBound = false;
		}
	};

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		if (isBound) {
			/*
			 * if(! deletedGci){ ts.stopProximityMonitor(gci); deletedGci =
			 * true; }
			 */
			ts.killIfEmpty();
			unbindService(tsConnection);
			isBound = false;
		}
	}

	public void onSaveInstanceState(Bundle b) {

		b.clear();

	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("NOTIFY", "Sending Text From SendSMS");
		Intent intent = new Intent(this, TextService.class);
		bindService(intent, tsConnection, Context.BIND_AUTO_CREATE);
		String msgBdy = "";
		String phoneNumber = "";
		String addrLine1 = "";
		Double latDub = 0.0;
		Double lonDub = 0.0;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			msgBdy = extras.getString("message");
			phoneNumber = extras.getString("pNum");
			addrLine1 = extras.getString("addr");
			latDub = extras.getDouble("lat");
			lonDub = extras.getDouble("lon");
			gci = new GeoContactInfo(phoneNumber, addrLine1, msgBdy);
		}
		// gci = new GeoContactInfo(userName,phoneNumber,addrLine1);

		Log.d("SendSMS msgBdy:", msgBdy);
		Log.d("SendSMS phoneNumber: ", phoneNumber);
		Log.d("SendSMS Addr: ", addrLine1);
		if ((!phoneNumber.equals("")) && (!addrLine1.equals(""))) {
			SmsManager sm = SmsManager.getDefault();
			String message = "";
			if (msgBdy.equals("")) {
				message = "I have Arrived at " + addrLine1;
			} else {
				message = msgBdy;
			}

			message = message
					+ "\n\nHere's a Google map of where I am:\nhttp://maps.google.com/maps?q="
					+ latDub + "," + lonDub + "\n\nSent using TextOnArrival";

			sm.sendTextMessage(phoneNumber, null, message, null, null);
		}

		// GeoContactInfo gci = new
		// GeoContactInfo(userName,phoneNumber,addrLine1);
		Log.d("NOTIFY: ", "SendSMS checking for Service");
		if (isBound && gci != null) {
			Log.d("NOTIFY", "SendSMS found service");
			// Log.d("NOTIFY: ", "Removing Proximity Monitor");
			// ts.stopProximityMonitor(gci);
			// deletedGci = true;
		}

		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setBackgroundColor(Color.argb(255, 75, 96, 122));
		TextView message = new TextView(this);
		message.setPadding(1, 100, 1, 100);
		message.setTextSize(24);
		message.setText("A message has been sent to: " + phoneNumber
				+ " that you have arrived at " + addrLine1);
		ll.addView(message);

		final MMAdView adview = new MMAdView(this, "28911",
				MMAdView.FULLSCREEN_AD_TRANSITION, -1);
		adview.setId(20);
		AdListener al = new AdListener();
		adview.setListener(al);

		Button button = new Button(this);
		button.setText("OK");
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isBound) {
					Log.d("SMS BUTTON", "IS BOUND");
					if (!deletedGci) {
						Log.d("SMS BUTTON", "Calling StopProximityMonitor");
						// ts.stopProximityMonitor(gci);
						// deletedGci = true;
					}
					Log.d("SMS BUTTON", "Unbinding Service");
					unbindService(tsConnection);
					isBound = false;
				}
				// TODO Auto-generated method stub
				adview.callForAd();
				pd = ProgressDialog.show((Activity) SendSMS.this, "",
						"Please wait for a word from our sponsers...", true);

			}
		});

		ll.addView(button);
		setContentView(ll);
		// this.finish();

	}

	public class AdListener implements MMAdListener {

		@Override
		public void MMAdClickedToNewBrowser(MMAdView arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void MMAdClickedToOverlay(MMAdView arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void MMAdFailed(MMAdView arg0) {
			pd.dismiss();
			SendSMS.this.finish();

		}

		@Override
		public void MMAdOverlayLaunched(MMAdView arg0) {
			pd.dismiss();
			SendSMS.this.finish();

		}

		@Override
		public void MMAdRequestIsCaching(MMAdView arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void MMAdReturned(MMAdView arg0) {
			// TODO Auto-generated method stub

		}

	}

}
