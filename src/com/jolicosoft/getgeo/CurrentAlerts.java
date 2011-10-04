package com.jolicosoft.getgeo;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

public class CurrentAlerts extends Activity {
	TextService ts;
	boolean isBound = false;
	boolean deletedGci = false;
	final GeoContactInfo gci = null;
	ArrayList<GeoContactInfo> list = null;
	ProgressDialog pd = null;
	ScrollView sv = null;
	LinearLayout ll2 = null;
	LayoutParams rulerParams = new LayoutParams(LayoutParams.FILL_PARENT,
			LayoutParams.WRAP_CONTENT);

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
			list = ts.getRunning();
			rulerParams.height = 2;
			if (pd != null) {
				pd.dismiss();
			}
			if (list != null) {
				for (final GeoContactInfo gci : list) {
					float miles = gci.getDistance() * 0.000621371192f;
					String info = "Address: " + gci.getAddr() + "\nPhone:"
							+ gci.getPhoneNum() + "\nMiles To Destination: "
							+ miles;
					TableRow tr = new TableRow(CurrentAlerts.this);
					TextView tv = new TextView(CurrentAlerts.this);
					tv.setTextSize(18);
					tv.setText(info);
					tv.setClickable(true);
					tv.setBackgroundColor(Color.argb(255, 75, 96, 122));
					tr.addView(tv);
					ll2.addView(tr);
					View ruler = new View(CurrentAlerts.this);
					ruler.setLayoutParams(rulerParams);
					ruler.setBackgroundColor(0xD0D0D0D0);
					ll2.addView(ruler);
					tv.setOnLongClickListener(new View.OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {
							// TODO Auto-generated method stub
							AlertDialog ad = deleteDialog(gci);
							ad.show();
							return false;
						}
					});
				}
			} else {
				TableRow tr = new TableRow(CurrentAlerts.this);
				TextView tv = new TextView(CurrentAlerts.this);
				tv.setText("No Current Alerts Are Pending.");
				tr.addView(tv);
				ll2.addView(tr);
			}
			sv.addView(ll2);
			setContentView(sv);
			Log.d("NOTIFY: ", "CurrentAlerts is Bound");
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
			if (!deletedGci && gci != null) {
				ts.stopProximityMonitor(gci);
				deletedGci = true;
			} else {
				ts.killIfEmpty();
			}
			unbindService(tsConnection);
			isBound = false;
		}
	}

	@Override
	public void onBackPressed() {

		Intent getGeo = new Intent(CurrentAlerts.this, GetGeo.class);
		startActivity(getGeo);
		CurrentAlerts.this.finish();

	}

	@Override
	public void onPause() {
		super.onPause();
		CurrentAlerts.this.finish();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Log.d("NOTIFY", "Sending Text From SendSMS");
		Intent intent = new Intent(CurrentAlerts.this, TextService.class);
		bindService(intent, tsConnection, Context.BIND_AUTO_CREATE);
		ll2 = new LinearLayout(this);
		ll2.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		ll2.setOrientation(LinearLayout.VERTICAL);
		TextView directions = new TextView(this);
		directions.setBackgroundColor(Color.LTGRAY);
		directions.setTextColor(Color.BLACK);
		directions.setTextSize(24);
		directions.setText("Long Click To Remove");
		ll2.addView(directions);
		sv = new ScrollView(this);
		sv.setBackgroundColor(Color.argb(255, 75, 96, 122));
		pd = ProgressDialog
				.show(CurrentAlerts.this, "", "Loading, Stand by...");
		pd.show();
		Log.d("CurrentAlerts:", "GETS HERE");
		// list = ts.getRunning();
		// LayoutParams rulerParams = new
		// LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		// rulerParams.height = 2;
		// LinearLayout ll1 = new LinearLayout(this);
		// ll2 = new LinearLayout(this);
		// sv = new ScrollView(this);
		// TextView tv1 = new TextView(this);
		// tv1.setTextSize(28);
		// tv1.setText("Pending Alerts");
		// TextView tv2 = new TextView(this);
		// tv2.setText("Tap Address to Cancel Alert");
		// ll1.addView(tv1);
		// ll1.addView(tv2);
		/*
		 * if (list != null){ for (GeoContactInfo gci:list){ String info =
		 * "Address: " + gci.getAddr() + "\nPhone:" + gci.getPhoneNum();
		 * TableRow tr = new TableRow(this); TextView tv = new TextView(this);
		 * tv.setText(info); tr.addView(tv); ll2.addView(tr); View ruler = new
		 * View(this); ruler.setLayoutParams(rulerParams);
		 * ruler.setBackgroundColor(0xD0D0D0D0); ll2.addView(ruler); } } else{
		 * TableRow tr = new TableRow(this); TextView tv = new TextView(this);
		 * tv.setText("This is the Problem"); tr.addView(tv); ll2.addView(tr); }
		 * 
		 * sv.addView(ll2); //ll1.addView(sv); setContentView(sv);
		 */
	}

	public AlertDialog deleteDialog(final GeoContactInfo gci) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Cancel Pending Alert?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (isBound) {
									ts.stopProximityMonitor(gci);
									deletedGci = true;
								}
								Intent restart = new Intent(CurrentAlerts.this,
										GetGeo.class);
								dialog.cancel();
								startActivity(restart);
								CurrentAlerts.this.finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent getGeo = new Intent(CurrentAlerts.this,
								GetGeo.class);
						startActivity(getGeo);
						CurrentAlerts.this.finish();
					}
				});
		AlertDialog alert = builder.create();
		return alert;
	}
}
