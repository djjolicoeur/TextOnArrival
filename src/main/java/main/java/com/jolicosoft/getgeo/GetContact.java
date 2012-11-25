package main.java.com.jolicosoft.getgeo;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
//import android.widget.Toast;

public class GetContact extends Activity {
    /** Called when the activity is first created. */
	String chooseNumber = "";
	String chooseName = "";
	ProgressDialog pd;
	LinearLayout ll;
	ScrollView sv;
	ArrayList<ContactInfo> contacts;
	
	final Handler mHandler = new Handler();

    // Create runnable for posting
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateResultsInUi();
        }
    };
    
    private class ContactInfo{
    	String name;
    	ArrayList<String> pnums;
    	ContactInfo(String name,ArrayList<String> pnums){
    		this.name = name;
    		this.pnums = pnums;
    	}
    	
    	String getName(){
    		return name;
    	}
    	
    	ArrayList<String> getPnums(){
    		return pnums;
    	}
    }
    
    private void updateResultsInUi() {
    	LayoutParams rulerParams = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
        rulerParams.height = 2;
    	for (final ContactInfo contact:contacts){
    		Log.d("GetContacts", "Adding " + contact.getName());
    		final ArrayList<String> pNums = contact.getPnums();
    		if (pNums.size() > 0){
    			TableRow tr = new TableRow(this);
    	   	
    			TextView tv = new TextView(this);
    			tv.setClickable(true);
    			tv.setTextSize(30);
    			//tv.setBackgroundColor(Color.argb(255,75,96,122));
    			tv.setText(contact.getName());
    			tr.addView(tv);
    			ll.addView(tr);
    			View ruler = new View(this);
    			ruler.setLayoutParams(rulerParams);
    			ruler.setBackgroundColor(0xD0D0D0D0);
    			ll.addView(ruler);
    			tv.setOnClickListener(new View.OnClickListener(){
	 	        	@Override
	 	        	public void onClick(View view) {
	 	        		setName(contact.getName());
	 	        		if(pNums.size() > 1){
	 	        			AlertDialog alert = makeAlert(pNums);
	 	        			alert.show();
	 	        		}
	 	        		else{
	 	        			setNumber(pNums.get(0));
	 	        			Log.d("TestContacts::onClick: ", "NAME: " + chooseName + " PHONE: " + chooseNumber);
	 	        			Intent getGeo = new Intent(GetContact.this,GetGeo.class);
	 	        			getGeo.putExtra("phone", chooseNumber);
	 	        			getGeo.putExtra("tabIndex", 1);
	 	        			startActivity(getGeo);
	 	        			GetContact.this.finish();
	 	        		}
	 	        	}
	 	        });
    		}
    	}
    	sv.addView(ll);
    	setContentView(sv);
    	pd.dismiss();

        // Back in the UI thread -- update our UI elements based on the data in mResults
        
    }
    
    protected void startLongRunningOperation() {

        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread t = new Thread() {
            public void run() {
                contacts = getContacts();
                mHandler.post(mUpdateResults);
            }
        };
        t.start();
    }
    
    protected ArrayList<ContactInfo> getContacts(){
    	ArrayList<ContactInfo> results = new ArrayList<ContactInfo>();
    	ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE NOCASE ASC");
        if (cur.getCount() > 0) {
        	while (cur.moveToNext()) {
        		String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
        		final String name = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        		
	       if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
	    	   	
       			final ArrayList<String> pNums = new ArrayList<String>();
	        	Cursor pCur = cr.query(
 		 		    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
 		 		    null, 
 		 		    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = " + id , 
 		 		    null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE NOCASE ASC");
 		 	        while (pCur.moveToNext()) {
 		 	        	String phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
 		 	        	pNums.add(phoneNumber);
 		 	        }
 		 	        
 		 	        pCur.close();
 		 	        results.add(new ContactInfo(name,pNums));
 		 	      
	       		}
	    	}
        }
        cur.close();
        return results;
    }

	
	@Override
	public void onBackPressed(){
		Intent getGeo = new Intent(this,GetGeo.class);
		startActivity(getGeo);
		this.finish();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sv = new ScrollView(this);
        ll = new LinearLayout(this);
        LayoutParams rulerParams = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
        rulerParams.height = 2;
        ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        ll.setOrientation(LinearLayout.VERTICAL);
        startLongRunningOperation();
        pd = ProgressDialog.show(this, "", "Loading Contacts...");
     
    }
    
    public AlertDialog makeAlert(ArrayList<String> pNum){
    	final String [] items = new String [pNum.size()];
    	for(int i = 0; i < items.length;i++){
    		items[i] = pNum.get(i);
    	}
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Pick a Number");
    	builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	    	setNumber(items[item]);
    	    	Log.d("TestContacts::onClick: ", "NAME: " + chooseName + " PHONE: " + chooseNumber);
    	    	Intent getGeo = new Intent(GetContact.this,GetGeo.class);
    	    	getGeo.putExtra("phone", chooseNumber);
    	    	getGeo.putExtra("tabIndex", 1);
    	    	dialog.dismiss();
    	    	startActivity(getGeo);
    	    	GetContact.this.finish();
    	    }
    	});
    	AlertDialog alert = builder.create();
    	return alert;
    }
    
    public void setName(String name){
    	chooseName = name;
    }
    
    public void setNumber(String number){
    	number = number.replaceAll("[^0-9]", "");
    	chooseNumber = number;
    }
    
}
