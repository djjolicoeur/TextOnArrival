package main.java.com.jolicosoft.getgeo;

//import com.jolicosoft.getgeo.TextService.LocalBinder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMAdView.MMAdListener;

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
		
		
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		ll.setOrientation(LinearLayout.VERTICAL);
		
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
                adview.fetch();

		Button button = new Button(this);
		button.setText("OK");
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				adview.display();
				pd = ProgressDialog.show((Activity) SendSMS.this, "",
						"Please wait for a word from our sponsers...", true);

			}
		});

		ll.addView(button);
		setContentView(ll);
		

	}

	public class AdListener implements MMAdListener {

		//@Override
		//public void MMAdClickedToNewBrowser(MMAdView arg0) {
			// TODO Auto-generated method stub

		//}

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
                
                /*
		@Override
		public void MMAdRequestIsCaching(MMAdView arg0) {
			// TODO Auto-generated method stub

		}*/

		@Override
		public void MMAdReturned(MMAdView arg0) {
			// TODO Auto-generated method stub

		}

        @Override
        public void MMAdRequestIsCaching(MMAdView mmav) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void MMAdCachingCompleted(MMAdView mmav, boolean bln) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

	}

}
