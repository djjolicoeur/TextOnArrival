package com.jolicosoft.getgeo;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

public class GetLocation extends Activity {
		private AddressDBHelper db;
		String phoneNumber = "";
		
	@Override
	public void onBackPressed(){
		Intent getGeo = new Intent(GetLocation.this,GetGeo.class);
        getGeo.putExtra("phone", phoneNumber);
        getGeo.putExtra("tabIndex", 1);
        startActivity(getGeo);
        db.close();
	}
	
	public void onStop(){
		super.onStop();
		db.close();
	}
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Bundle extras = this.getIntent().getExtras();
        if(extras != null){
        	phoneNumber = extras.getString("pNum");
        }
        else{
        	phoneNumber = "";
        }
        
        
        
        ScrollView sv = new ScrollView(this);
        sv.setBackgroundColor(Color.argb(255,75,96,122));
        LinearLayout ll = new LinearLayout(this);
        LayoutParams rulerParams = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
        rulerParams.height = 2;
        ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        ll.setOrientation(LinearLayout.VERTICAL);
        
        db = new AddressDBHelper(this);
        ArrayList<Address> list = db.getAll();
        if (list.isEmpty()){
        	AlertDialog ad = emptyDialog();
        	ad.show();
        }
        for (final Address addr : list){
        	String info = "Name: " + addr.getName() + "\n"
        	+ "Street Address: " + addr.getAddr1() + "\n"
        	+ "City,State: " + addr.getAddr2() + "\n"
        	+ "Zip: " + addr.getZip();
        	TableRow tr = new TableRow(this);
        	TextView tv = new TextView(this);
        	tv.setText(info);
        	tv.setBackgroundColor(Color.argb(255,75,96,122));
        	tr.addView(tv);
        	ll.addView(tr);
        	View ruler = new View(this);
        	ruler.setLayoutParams(rulerParams);
   			ruler.setBackgroundColor(0xD0D0D0D0);
   			ll.addView(ruler);
   			tv.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(GetLocation.this,GetGeo.class);
					intent.putExtra("phone", phoneNumber);
					intent.putExtra("name", addr.getName());
					intent.putExtra("streetAddr", addr.getAddr1());
					intent.putExtra("cityState", addr.getAddr2());
					intent.putExtra("zip", addr.getZip());
					intent.putExtra("lat", addr.getLat());
					intent.putExtra("lon", addr.getLon());
					intent.putExtra("tabIndex", 2);
					startActivity(intent);
					GetLocation.this.finish();
				}
			});
   			
   			tv.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					AlertDialog ad = deleteDialog(addr);
					ad.show();
					return false;
				}
   				
   			});
        }
       
        sv.addView(ll);
        setContentView(sv);
    }
    
    public AlertDialog emptyDialog(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("No Saved Addresses Found.")
           .setCancelable(false)
           .setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                    Intent getGeo = new Intent(GetLocation.this,GetGeo.class);
                    getGeo.putExtra("phone", phoneNumber);
                    getGeo.putExtra("tabIndex", 1);
                    startActivity(getGeo);
               }
           })
           .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    GetLocation.this.finish();
               }
           });
    	AlertDialog alert = builder.create();
    	return alert;
    }
    
    public AlertDialog deleteDialog(final Address addr){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Delete Saved Address?.")
           .setCancelable(false)
           .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                    db.deleteAddr(addr);
               }
           })
           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
               }
           });
    	AlertDialog alert = builder.create();
    	return alert;
    }

}
