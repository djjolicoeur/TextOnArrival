package main.java.com.jolicosoft.getgeo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.util.Log;
import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.LinearLayout;
//import android.widget.ScrollView;
import android.widget.TabHost;
//import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import main.java.com.jolicosoft.getgeo.R;

public class GetGeo extends Activity {
	/** Called when the activity is first created. */
	String MAPS_API_KEY = "ABQIAAAA48BI78Soj4VFX04fnMegnBQaN_mYiTSBF_D2THREJRwAFpLrGxR5d2acchZ0HJ2Kz40X4Vi0Jdp-SA";
	String MAPS_URL = "http://maps.google.com/maps/geo?output=csv&key="
			+ MAPS_API_KEY + "&q=";
	TextService ts;
	String message = "";
	boolean isBound = false;
	private AddressDBHelper db;
	private boolean dbConnected = false;

	@Override
	public void onStart() {
		super.onStart();
		// Bind to LocalService
		// Intent intent = new Intent(this, TextService.class);
		// startService(intent);
		Intent intent = new Intent(TextService.ACTION_FOREGROUND);
		intent.setClass(GetGeo.this, TextService.class);
		startService(intent);
		bindService(intent, tsConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (dbConnected) {
			db.close();
			dbConnected = false;
		}
		// Unbind from the service
		if (isBound) {
			ts.killIfEmpty();
			unbindService(tsConnection);
			isBound = false;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!dbConnected) {
			db = new AddressDBHelper(this);
			dbConnected = true;
		}
		if (!isBound) {
			Intent intent = new Intent(TextService.ACTION_FOREGROUND);
			intent.setClass(GetGeo.this, TextService.class);
			startService(intent);
			bindService(intent, tsConnection, Context.BIND_AUTO_CREATE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (isBound) {
			ts.killIfEmpty();
			unbindService(tsConnection);
			isBound = false;
		}
		if (dbConnected) {
			db.close();
			dbConnected = false;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (isBound) {
			ts.killIfEmpty();
		}
	}

	private ServiceConnection tsConnection = new ServiceConnection() {

		@SuppressWarnings("unchecked")
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			// LocalBinder<TextService> binder = new LocalBinder(this);
			ts = ((LocalBinder<TextService>) service).getService();
			isBound = true;
			Log.d("NOTIFY: ", "GetGeo is Bound");
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			isBound = false;
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);
		Bundle extras = getIntent().getExtras();
		if (!dbConnected) {
			db = new AddressDBHelper(this);
			dbConnected = true;
		}

		setContentView(R.layout.get_geo);

		final TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();
		tabHost.setBackgroundColor(Color.argb(255, 75, 96, 122));

		TabSpec spec1 = tabHost.newTabSpec("Tab 1");

		spec1.setContent(R.id.tab1);
		spec1.setIndicator("Contact (req)");

		TabSpec spec2 = tabHost.newTabSpec("Tab 2");
		spec2.setContent(R.id.tab2);
		spec2.setIndicator("Address (req)");

		TabSpec spec3 = tabHost.newTabSpec("Tab 3");
		spec3.setIndicator("Message/Submit");
		spec3.setContent(R.id.tab3);

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);

		final EditText phoneNumber = (EditText) findViewById(R.id.phonedata);// new
																				// EditText(this);
		phoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
		if (extras != null) {
			phoneNumber.setText(extras.getString("phone"));
		}

		Button contacts = (Button) findViewById(R.id.phonebutton);// new
																	// Button(this);
		contacts.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent getContact = new Intent(GetGeo.this, GetContact.class);
				startActivity(getContact);
				GetGeo.this.finish();
			}
		});

		final EditText nameData = (EditText) findViewById(R.id.namedata);// new
																			// EditText(this);
		if (extras != null) {
			nameData.setText(extras.getString("name"));
		}

		final EditText line1 = (EditText) findViewById(R.id.sadata);// new
																	// EditText(this);
		if (extras != null) {
			line1.setText(extras.getString("streetAddr"));
		}

		final EditText line2 = (EditText) findViewById(R.id.csdata);// new
																	// EditText(this);
		if (extras != null) {
			line2.setText(extras.getString("cityState"));
		} else {
			line2.setHint("City,State");
		}

		final EditText zip = (EditText) findViewById(R.id.zipdata);// new
																	// EditText(this);
		zip.setInputType(InputType.TYPE_CLASS_PHONE);
		if (extras != null) {
			zip.setText(extras.getString("zip"));
		}

		Button locationButton = (Button) findViewById(R.id.locationbutton);// new
																			// Button(this);
		locationButton.setText("Choose Saved Location");
		locationButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String pNum = phoneNumber.getText().toString().trim();
				if (pNum.equals("")) {
					AlertDialog ad = invalidPhoneDialog();
					ad.show();
					return;
				}
				Intent getLocation = new Intent(GetGeo.this, GetLocation.class);
				getLocation.putExtra("pNum", phoneNumber.getText().toString()
						.trim());
				startActivity(getLocation);
				GetGeo.this.finish();
			}

		});

		final EditText message = (EditText) findViewById(R.id.msgbody);// new
																		// EditText(this);

		Button button = (Button) findViewById(R.id.submitbutton);// new
																	// Button(this);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Intent intent = new Intent(TextService.ACTION_FOREGROUND);
				// intent.setClass(GetGeo.this, TextService.class);
				// startService(intent);
				// bindService(intent, tsConnection, Context.BIND_AUTO_CREATE);
				String locationName = nameData.getText().toString().trim();
				String l1 = line1.getText().toString().trim();
				String l2 = line2.getText().toString().trim();
				String zipcode = zip.getText().toString().trim();
				String toEncode = l1 + "," + l2 + "," + zipcode;
				StringBuilder contents = new StringBuilder();
				ArrayList<Address> addrList = db.findByAddress(l1, l2, zipcode);
				String pNum = phoneNumber.getText().toString().trim();
				// String addr = line1.getText().toString().trim();
				String messageBdy = message.getText().toString().trim();
				if (pNum.equals("") || zipcode.equals("") || l1.equals("")
						|| l2.equals("") || locationName.equals("")) {
					AlertDialog empty = emptyFieldDialog();
					empty.show();
					return;
				}
				GeoContactInfo gci = null;
				/*
				 * while(!isBound){ try{ Thread.sleep(50);
				 * Log.d("GetGeo - BINDING", "Sleeping 50ms until bound");
				 * }catch(InterruptedException e){ e.printStackTrace(); } }
				 */
				if (addrList.isEmpty()) {
					try {
						String encoded = URLEncoder.encode(toEncode, "UTF-8");
						String url = MAPS_URL + encoded;
						URL geoUrl = new URL(url);

						InputStream in = geoUrl.openStream();
						InputStreamReader isr = new InputStreamReader(in);
						BufferedReader br = new BufferedReader(isr);
						String line;
						while ((line = br.readLine()) != null) {
							contents.append(line);
						}

						String[] array = contents.toString().split(",");
						double lat = Double.parseDouble(array[2]);
						double lon = Double.parseDouble(array[3]);
						Log.d("GEO DATA", contents.toString());

						gci = new GeoContactInfo(pNum, l1, messageBdy, lat, lon);
						final Address newAddr = new Address(locationName, l1,
								l2, zipcode, lat, lon);

						if (isBound) {
							AlertDialog ad = endSaveDialog(gci, newAddr);
							ad.show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					gci = new GeoContactInfo(pNum, l1, messageBdy, addrList
							.get(0).getLat(), addrList.get(0).getLon());
					if (isBound && gci != null) {
						AlertDialog ad = endDialog(gci);
						ad.show();
					}
				}
			}

		});

		Button curr = (Button) findViewById(R.id.currentbutton);
		curr.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent currIntent = new Intent(GetGeo.this, CurrentAlerts.class);
				startActivity(currIntent);

			}

		});

		if (extras != null) {
			tabHost.setCurrentTab(extras.getInt("tabIndex"));
		}

		Button tab1Next = (Button) findViewById(R.id.tab1next);
		tab1Next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tabHost.setCurrentTab(1);
			}
		});

		Button tab2Next = (Button) findViewById(R.id.tab2next);
		tab2Next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tabHost.setCurrentTab(2);
			}
		});

		Button tab2Back = (Button) findViewById(R.id.tab2back);
		tab2Back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tabHost.setCurrentTab(0);
			}
		});

		Button tab3Back = (Button) findViewById(R.id.tab3back);
		tab3Back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tabHost.setCurrentTab(1);
			}
		});

	}

	public AlertDialog endDialog(final GeoContactInfo gci) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Send Alert to " + gci.getPhoneNum() + " Upon Arrival @ "
						+ gci.getAddr() + "?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								ts.startProximityMonitor(gci);
								GetGeo.this.finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		return alert;
	}

	public AlertDialog endSaveDialog(final GeoContactInfo gci,
			final Address addr) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Send Alert to " + gci.getPhoneNum() + " Upon Arrival @ "
						+ gci.getAddr() + "?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								ts.startProximityMonitor(gci);
								AlertDialog saved = saveDialog(addr);
								saved.show();
								// GetGeo.this.finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		return alert;
	}

	public AlertDialog saveDialog(final Address addr) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Save New Address? ")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								db.insert(addr);
								GetGeo.this.finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						GetGeo.this.finish();
					}
				});
		AlertDialog alert = builder.create();
		return alert;
	}

	public AlertDialog emptyFieldDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Empty Fields Detected, Press Yes to try again or no to Exit")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						GetGeo.this.finish();
					}
				});
		AlertDialog alert = builder.create();
		return alert;
	}

	public AlertDialog invalidPhoneDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Enter Valid Phone Number, Press Yes to try again or no to Exit")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						GetGeo.this.finish();
					}
				});
		AlertDialog alert = builder.create();
		return alert;
	}
}