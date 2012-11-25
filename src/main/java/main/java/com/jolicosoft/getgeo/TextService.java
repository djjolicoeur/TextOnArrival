package main.java.com.jolicosoft.getgeo;

//import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
//import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import main.java.com.jolicosoft.getgeo.R;

public class TextService extends Service {
	static final String TAG = "TOA-TextService";
	static final String ACTION_FOREGROUND = "main.java.com.jolicosoft.geteo.TextService.FOREGROUND";
	static final String ACTION_BACKGROUND = "main.java.com.jolicosoft.geteo.TextService.BACKGROUND";
	static final float RADIUS = 150.0f;

	private static final Class<?>[] mSetForegroundSignature = new Class[] { boolean.class };
	private static final Class<?>[] mStartForegroundSignature = new Class[] {
			int.class, Notification.class };
	private static final Class<?>[] mStopForegroundSignature = new Class[] { boolean.class };

	private NotificationManager mNM;
	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];

	private LocationManager locMan = null;
	String provider = null;
	static ArrayList<ProximityIntentData> proxSessions = null;
	private final IBinder sBinder = new LocalBinder<TextService>(this);
	private TOALocationListener listener = null;

	Timer timer = new Timer();
	final Handler handler = new Handler();

	TimerTask requestUpdates = new TimerTask() {

		@Override
		public void run() {
			handler.post(new Runnable() {
				public void run() {
					if (locMan != null && provider != null) {
						// Log.d(TAG,"NOT NULL");
						Log.d(TAG, locMan.getLastKnownLocation(provider)
								.toString() + " from  " + this.toString());
					}

				}

			});

		}
	};

	void invokeMethod(Method method, Object[] args) {
		try {
			mStartForeground.invoke(this, mStartForegroundArgs);
		} catch (InvocationTargetException e) {
			// Should not happen.
			Log.w(TAG, "Unable to invoke method", e);
		} catch (IllegalAccessException e) {
			// Should not happen.
			Log.w(TAG, "Unable to invoke method", e);
		}
	}

	void startForegroundCompat(int id, Notification notification) {
		// If we have the new startForeground API, then use it.
		if (mStartForeground != null) {
			mStartForegroundArgs[0] = Integer.valueOf(id);
			mStartForegroundArgs[1] = notification;
			invokeMethod(mStartForeground, mStartForegroundArgs);
			return;
		}

		// Fall back on the old API.
		mSetForegroundArgs[0] = Boolean.TRUE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
		mNM.notify(id, notification);
	}

	void stopForegroundCompat(int id) {
		// If we have the new stopForeground API, then use it.
		if (mStopForeground != null) {
			mStopForegroundArgs[0] = Boolean.TRUE;
			try {
				mStopForeground.invoke(this, mStopForegroundArgs);
			} catch (InvocationTargetException e) {
				// Should not happen.
				Log.w("TOA-SERVICE", "Unable to invoke stopForeground", e);
			} catch (IllegalAccessException e) {
				// Should not happen.
				Log.w("TOA_SERVICE", "Unable to invoke stopForeground", e);
			}
			return;
		}

		// Fall back on the old API. Note to cancel BEFORE changing the
		// foreground state, since we could be killed at that point.
		mNM.cancel(id);
		mSetForegroundArgs[0] = Boolean.FALSE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
	}

	@Override
	public void onCreate() {

		// super.onCreate();

		listener = new TOALocationListener();

		Log.d(TAG, "Instance " + this.toString() + " being created");

		locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria cr = new Criteria();
		cr.setAccuracy(Criteria.ACCURACY_FINE);
		provider = locMan.getBestProvider(cr, true);
		locMan.requestLocationUpdates(provider, 30000, 5, listener,
				this.getMainLooper());

		timer.schedule(requestUpdates, 300, 10000);

		proxSessions = makeList();

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		try {
			mStartForeground = getClass().getMethod("startForeground",
					mStartForegroundSignature);
			mStopForeground = getClass().getMethod("stopForeground",
					mStopForegroundSignature);
		} catch (NoSuchMethodException e) {
			// Running on an older platform.
			mStartForeground = mStopForeground = null;
			return;
		}
		try {
			mSetForeground = getClass().getMethod("setForeground",
					mSetForegroundSignature);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(
					"OS doesn't have Service.startForeground OR Service.setForeground!");
		}
	}

	/*
	 * @Override public void onStart(Intent intent, int startId) {
	 * handleCommand(intent); }
	 */

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleCommand(intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// Make sure our notification is gone.
		timer.cancel();
		timer.purge();
		Log.d(TAG, "Service being Destroyed");
		locMan.removeUpdates(listener);
		stopForegroundCompat(R.string.foreground_service_started);
	}

	void handleCommand(Intent intent) {
		if (ACTION_FOREGROUND.equals(intent.getAction())) {
			// In this sample, we'll use the same text for the ticker and the
			// expanded notification
			CharSequence text = getText(R.string.foreground_service_started);

			// Set the icon, scrolling text and timestamp
			Notification notification = new Notification(
					R.drawable.icon_notification_bubble, text,
					System.currentTimeMillis());

			// The PendingIntent to launch our activity if the user selects this
			// notification
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					new Intent(this, CurrentAlerts.class), 0);

			// Set the info for the views that show in the notification panel.
			notification.setLatestEventInfo(this,
					getText(R.string.local_service_label), text, contentIntent);

			startForegroundCompat(R.string.foreground_service_started,
					notification);

		} else if (ACTION_BACKGROUND.equals(intent.getAction())) {
			stopForegroundCompat(R.string.foreground_service_started);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {

		return sBinder;
	}

	synchronized ArrayList<ProximityIntentData> makeList() {
		return new ArrayList<ProximityIntentData>();
	}

	synchronized void startProximityMonitor(GeoContactInfo gci) {
		Location curr = locMan.getLastKnownLocation(provider);
		Location dest = new Location(provider);
		dest.setLatitude(gci.getLat());
		dest.setLongitude(gci.getLon());
		float dist = dest.distanceTo(curr);
		gci.setDistance(dist);
		// Intent sendText = new Intent(this,SendSMS.class);
		Bundle extras = new Bundle();
		extras.putString("message", gci.getMessage());
		Log.d("TS ADDING message: ", gci.getMessage());
		extras.putString("pNum", gci.getPhoneNum());
		Log.d("TS ADDING PNUM: ", gci.getPhoneNum());
		extras.putString("addr", gci.getAddr());
		Log.d("TS ADDING ADDR: ", gci.getAddr());
		extras.putDouble("lat", gci.getLat());
		extras.putDouble("lon", gci.getLon());

		proxSessions.add(new ProximityIntentData(gci, extras));

	}

	synchronized void stopProximityMonitor(GeoContactInfo gci) {

		if (!proxSessions.isEmpty()) {
			for (ProximityIntentData pi : proxSessions) {
				Log.d("NOTIFY: ", "Checking against GCI "
						+ pi.getGci().getMessage());
				Log.d("NOTIFY: ", "Checking against GCI "
						+ pi.getGci().getPhoneNum());
				Log.d("NOTIFY: ", "Checking against GCI "
						+ pi.getGci().getAddr());
				if (pi.getGci().equals(gci)) {
					Log.d("TextService: ",
							"Removing ProximityIntent for" + gci.getAddr());
					proxSessions.remove(pi);
				}
			}
		}

		killIfEmpty();
	}

	public synchronized boolean hasRunning() {
		return !proxSessions.isEmpty();
	}

	public void killIfEmpty() {
		if (!hasRunning()) {
			locMan.removeUpdates(listener);
			this.stopSelf();
		}
	}

	public synchronized ArrayList<GeoContactInfo> getRunning() {
		ArrayList<GeoContactInfo> list = new ArrayList<GeoContactInfo>();
		for (ProximityIntentData pid : proxSessions) {
			list.add(pid.getGci());
		}
		return list;
	}

	public Location getCurrentLocation() {
		Location current = locMan.getLastKnownLocation(provider);
		return current;
	}

	private synchronized ArrayList<ProximityIntentData> copyProxList() {
		ArrayList<ProximityIntentData> copy = makeList();
		for (ProximityIntentData pid : proxSessions) {
			copy.add(pid);
		}

		return copy;

	}

	private synchronized void onLocationChangedOperation(Location current) {

		ArrayList<ProximityIntentData> copy = copyProxList();

		for (ProximityIntentData pid : copy) {
			Location dest = new Location(provider);
			dest.setLatitude(pid.getGci().getLat());
			dest.setLongitude(pid.getGci().getLon());
			Log.d(TAG, "Checking against Lat: " + dest.getLatitude() + " Lon: "
					+ dest.getLongitude());
			Log.d(TAG, "Distance from current: " + current.distanceTo(dest));
			float dist = current.distanceTo(dest);
			pid.getGci().setDistance(dist);
			if (dist <= RADIUS) {
				Intent sendText = new Intent(TextService.this, SendSMS.class);
				sendText.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				sendText.putExtras(pid.getBundle());
				startActivity(sendText);
				boolean wasDeleted = proxSessions.remove(pid);
                                if(wasDeleted){
                                    Log.d(TAG, "[!!] " + pid.proximityGci.getAddr() + " WAS DELETED[!!]");
                                }else{
                                    Log.d(TAG, "[!!] " + pid.proximityGci.getAddr() + " WAS NOT DELETED[!!]");
                                }
                          
                                
			}
		}

	}

	class TOALocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {

			Location current = getCurrentLocation();
			Log.d(TAG,
					"Location changed to Lat: " + current.getLatitude()
							+ " Lon: " + current.getLongitude() + "From "
							+ this.toString());
			onLocationChangedOperation(current);

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	}

}
