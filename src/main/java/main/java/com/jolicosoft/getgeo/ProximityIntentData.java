package main.java.com.jolicosoft.getgeo;

//import android.app.PendingIntent;
//import android.content.Intent;
import android.os.Bundle;

public class ProximityIntentData {

    GeoContactInfo proximityGci;
    //PendingIntent proximityPi;
    //Intent intent;
    Bundle extras;

    public ProximityIntentData(GeoContactInfo gci, Bundle ext) {
        this.proximityGci = gci;
        //this.proximityPi = pi;
        //this.intent = pi;
        this.extras = ext;
    }

    public GeoContactInfo getGci() {
        return proximityGci;
    }

    /*	public PendingIntent getPi(){
     return proximityPi;
     }*/
    /*	public Intent getIntent(){
     return intent;
     }*/
    public Bundle getBundle() {
        return extras;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.proximityGci != null ? this.proximityGci.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProximityIntentData other = (ProximityIntentData) obj;
        if (this.proximityGci != other.proximityGci && (this.proximityGci == null || !this.proximityGci.equals(other.proximityGci))) {
            return false;
        }
        return true;
    }
}
