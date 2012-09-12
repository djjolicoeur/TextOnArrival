package main.java.com.jolicosoft.getgeo;

//import android.app.PendingIntent;
//import android.content.Intent;
import android.os.Bundle;

public class ProximityIntentData {
	GeoContactInfo proximityGci;
	//PendingIntent proximityPi;
	//Intent intent;
	Bundle extras;
	
	public ProximityIntentData(GeoContactInfo gci, Bundle ext){
		this.proximityGci = gci;
		//this.proximityPi = pi;
		//this.intent = pi;
		this.extras = ext;
	}
	
	public GeoContactInfo getGci(){
		return proximityGci;
	}
	
/*	public PendingIntent getPi(){
		return proximityPi;
	}*/
	
/*	public Intent getIntent(){
		return intent;
	}*/
	
	public Bundle getBundle(){
		return extras;
	}

}
