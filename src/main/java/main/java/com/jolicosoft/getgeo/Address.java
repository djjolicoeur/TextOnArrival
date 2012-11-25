package main.java.com.jolicosoft.getgeo;

public class Address {

    private String name;
    private String addr1;
    private String addr2;
    private String zip;
    private double lat;
    private double lon;

    public Address(String name, String addr1, String addr2,
            String zip, double lat, double lon) {
        this.name = name;
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.zip = zip;
        this.lat = lat;
        this.lon = lon;
    }

    public Address() {
    }

    public String getName() {
        return name;
    }

    public String getAddr1() {
        return addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    public String getZip() {
        return zip;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
    
    @Override
    public String toString(){
        String string = 
                addr1 + "\n" 
              + addr2 + "\n"
              + zip   + "\n";
        return string;
    }
}
