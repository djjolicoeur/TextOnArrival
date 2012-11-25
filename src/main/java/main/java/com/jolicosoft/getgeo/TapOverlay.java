/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.jolicosoft.getgeo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author djolicoeur
 */
public class TapOverlay extends Overlay {

    public GeoPoint lastTap = null;
    String phone;
    private Context context;
    private boolean isPinch;

    public TapOverlay(Context c, String phoneNumber) {
        this.context = c;
        this.phone = phoneNumber;
    }

    @Override
    public boolean onTap(GeoPoint p, MapView mapView) {
        if (isPinch) {
            return false;
        }

        if (p != null) {
            lastTap = p;
            mapView.getController().animateTo(p);

            //strCalle = sb.toString(); //from geocoder


            handleResult();
            return true;
        } else {
            return false;
        }
    }

    private void handleResult() {


        final MapActivity ma = (MapActivity) context;
        //Intent intent = new Intent(ma, GetGeo.class);
        //Bundle b = new Bundle();
        Integer latE6 = Integer.valueOf(lastTap.getLatitudeE6());
        Integer lonE6 = Integer.valueOf(lastTap.getLongitudeE6());
        double lat = latE6.doubleValue() / 1E6;
        double lon = lonE6.doubleValue() / 1E6;
        Geocoder geo = new Geocoder(context, Locale.getDefault());
        List<android.location.Address> addresses = null; //= geo.getFromLocation(lat, lon, 1); 
        try {
            addresses = geo.getFromLocation(lat, lon, 1);
        } catch (Exception e) {
            Log.d("TapOverlay",e.getStackTrace().toString());
        }
        //b.putInt("dlat", lastTap.getLatitudeE6() / 1E6);
        //b.putInt("dlng", lastTap.getLongitudeE6());
        if (addresses != null && addresses.size() > 0) {
            android.location.Address adr = addresses.get(0);
            final Address address = new Address();
            int maxAddrLine = adr.getMaxAddressLineIndex();
            address.setLat(lat);
            address.setLon(lon);
            if (adr.getAddressLine(0) != null && 0 <= maxAddrLine) {
                address.setAddr1(adr.getAddressLine(0));
                address.setName(adr.getAddressLine(0));
            }
            if (adr.getAddressLine(1) != null && 1 <= maxAddrLine) {
                address.setAddr2(adr.getAddressLine(1));
            }
            address.setZip(adr.getPostalCode());
            
            AlertDialog validate = addressDialog(address, ma);
            
            validate.show();

            
            /*
            intent.putExtra("phone", phone);
            intent.putExtra("name", address.getName());
            intent.putExtra("streetAddr", address.getAddr1());
            intent.putExtra("cityState", address.getAddr2());
            intent.putExtra("zip", address.getZip());
            intent.putExtra("lat", address.getLat());
            intent.putExtra("lon", address.getLon());
            intent.putExtra("tabIndex", 2);

            */
            

        } 
    }

    @Override
    public boolean onTouchEvent(MotionEvent e, MapView mapView) {
        int fingers = e.getPointerCount();
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            isPinch = false;  // Touch DOWN, don't know if it's a pinch yet
        }
        if (e.getAction() == MotionEvent.ACTION_MOVE && fingers == 2) {
            isPinch = true;   // Two fingers, def a pinch
        }
        return super.onTouchEvent(e, mapView);
    }

    public AlertDialog addressDialog(final Address address, final MapActivity ma) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ma);
        builder.setMessage(address.toString() + "Is This Correct?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(ma, GetGeo.class);
                intent.putExtra("phone", phone);
                intent.putExtra("name", address.getName());
                intent.putExtra("streetAddr", address.getAddr1());
                intent.putExtra("cityState", address.getAddr2());
                intent.putExtra("zip", address.getZip());
                intent.putExtra("lat", address.getLat());
                intent.putExtra("lon", address.getLon());
                intent.putExtra("tabIndex", 2);
                
                ma.startActivity(intent);
                ma.finish();
                
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                //GetLocation.this.finish();
            }
        });
        AlertDialog alert = builder.create();
        return alert;
    }
}
