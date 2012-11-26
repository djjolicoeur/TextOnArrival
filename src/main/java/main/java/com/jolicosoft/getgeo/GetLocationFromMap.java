/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.jolicosoft.getgeo;

//import com.google.
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import java.util.List;

/**
 *
 * @author djolicoeur
 */
public class GetLocationFromMap extends MapActivity {

    private String phoneNumber;
    private MapView mapView;
    private static final int ZOOM_LEVEL = 14;

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service

    }

    @Override
    public void onBackPressed() {
        Intent getGeo = new Intent(GetLocationFromMap.this, GetGeo.class);
        getGeo.putExtra("phone", phoneNumber);
        getGeo.putExtra("tabIndex", 1);
        startActivity(getGeo);
    }

    @Override
    public void onSaveInstanceState(Bundle b) {

        b.clear();

    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setSatellite(true);

        LocationManager locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria cr = new Criteria();
        cr.setAccuracy(Criteria.ACCURACY_COARSE);
        String provider = locMan.getBestProvider(cr, true);
        Location loc = locMan.getLastKnownLocation(provider);
        
        if(loc != null){
            Double lat = loc.getLatitude();
            Double lon = loc.getLongitude();
            
            lat = lat * 1E6;
            lon = lon * 1E6;
            
            Integer latIntValue = lat.intValue();
            Integer lonIntValue = lon.intValue();
            
            GeoPoint current = new GeoPoint(latIntValue,lonIntValue);
            
            
            
            mapView.getController().setZoom(ZOOM_LEVEL);
            mapView.getController().setCenter(current);
        }
        
        


        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            phoneNumber = extras.getString("pNum");
        } else {
            phoneNumber = "";
        }



        TapOverlay tap = new TapOverlay(this, phoneNumber);
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.clear();
        listOfOverlays.add(tap);

        mapView.invalidate();
    }
}
