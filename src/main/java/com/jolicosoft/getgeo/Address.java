package com.jolicosoft.getgeo;

public class Address {
	String name;
	String addr1;
	String addr2;
	String zip;
	double lat;
	double lon;
	public Address(String name, String addr1, String addr2,
			String zip, double lat, double lon){
		this.name = name;
		this.addr1 = addr1;
		this.addr2 = addr2;
		this.zip = zip;
		this.lat = lat;
		this.lon = lon;
	}
	
	public String getName(){
		return name;
	}
	
	public String getAddr1(){
		return addr1;
	}
	
	public String getAddr2(){
		return addr2;
	}
	
	public String getZip(){
		return zip;
	}
	
	public double getLat(){
		return lat;
	}
	
	public double getLon(){
		return lon;
	}
}

