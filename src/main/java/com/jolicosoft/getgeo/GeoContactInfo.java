package main.java.com.jolicosoft.getgeo;

import java.io.Serializable;

import android.util.Log;

public class GeoContactInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String phoneNumber;
	//private String userName;
	private String addrLine1;
	private String message;
	private double lat;
	private double lon;
	private float distance;
	
	public GeoContactInfo(String pNumber, String addr, String message,double lat,double lon){
		this.phoneNumber = pNumber;
		//this.userName = uName;
		this.message = message;
		this.lat = lat;
		this.lon = lon;
		this.addrLine1 = addr;
		this.distance = 0.0f;
	}
	
	public GeoContactInfo(String pNumber,String addr,String message){
		this.phoneNumber = pNumber;
		this.message = message;
		this.lat = 0;
		this.lon = 0;
		this.addrLine1 = addr;
		this.distance = 0.0f;
	}
	
	public String getPhoneNum(){
		return phoneNumber;
	}
	
	public void setDistance(float d){
		this.distance = d;
	}
	
	public float getDistance(){
		return distance;
	}
	
	public double getLat(){
		return lat;
	}
	
	public double getLon(){
		return lon;
	}
	
	public String getAddr(){
		return addrLine1;
	}
	
	public String getMessage(){
		return message;
	}
	
	public boolean equals(GeoContactInfo oGci){
		boolean result = true;
		if(!this.message.equals(oGci.getMessage())){
			Log.d("GeoContactInfo.equals", "FAILED on MESSAGE");
			return false;
		}
		if(!this.phoneNumber.equals(oGci.getPhoneNum())){
			Log.d("GeoContactInfo.equals", "FAILED on PHONE");
			return false;
		}
		if(!this.addrLine1.equals(oGci.getAddr())){
			Log.d("GeoContactInfo.equals", "FAILED on ADDR");
			return false;
		}
		Log.d("GeoContactInfo", "IsEqual");
		return result;
	}
}
