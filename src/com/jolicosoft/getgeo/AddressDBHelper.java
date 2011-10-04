package com.jolicosoft.getgeo;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;





public class AddressDBHelper {
	private final static String DATABASE_NAME = "addresses.db";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "address_data";
	
	private Context context;
	private SQLiteDatabase db;

	
	private SQLiteStatement insertStmt;
	private static final String INSERT = "insert into " 
		+ TABLE_NAME + "(name,addr_line_one,addr_line_two,zip,lat,long) values (?,?,?,?,?,?)";
	
	private static class OpenHelper extends SQLiteOpenHelper{
		OpenHelper(Context context){
			super(context,DATABASE_NAME,null,DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db){
			db.execSQL("CREATE TABLE " + TABLE_NAME + 
					"( id INTEGER PRIMARY KEY, name VARCHAR(255)," + 
					"addr_line_one VARCHAR(255),addr_line_two VARCHAR(255)," +
					"zip VARCHAR(255), lat DOUBLE, long DOUBLE)");
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
		
		
	}
	
	
	
	public AddressDBHelper(Context context){
		this.context = context;
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		this.insertStmt = this.db.compileStatement(INSERT);
		
	}
	
	public long insert(Address addr ){
		this.insertStmt.bindString(1, addr.getName());
		this.insertStmt.bindString(2, addr.getAddr1());
		this.insertStmt.bindString(3, addr.getAddr2());
		this.insertStmt.bindString(4, addr.getZip());
		this.insertStmt.bindDouble(5,addr.getLat());
		this.insertStmt.bindDouble(6,addr.getLon());
		return this.insertStmt.executeInsert();
	}
	
	public void deleteAll(){
		this.db.delete(TABLE_NAME, null, null);
	}
	
	public void deleteAddr(Address addr){
		String query = "name = '" + addr.getName() + "' AND addr_line_one = '" + addr.getAddr1()
		+ "' AND addr_line_two = '" + addr.getAddr2() + "' AND zip = '" + addr.getZip() + "'";
		this.db.delete(TABLE_NAME, query, null);
		
	}
	
	public ArrayList<Address> getAll(){
		ArrayList<Address> list = new ArrayList<Address>();
		Cursor cursor = this.db.query(TABLE_NAME, 
				new String [] {"name",
				"addr_line_one", 
				"addr_line_two",
				"zip", "lat", "long"},
				null, null, null, null, "name desc");
		while(cursor.moveToNext()){
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String addr1 = cursor.getString(cursor.getColumnIndex("addr_line_one"));
			String addr2 = cursor.getString(cursor.getColumnIndex("addr_line_two"));
			String zip = cursor.getString(cursor.getColumnIndex("zip"));
			double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
			double lon = cursor.getDouble(cursor.getColumnIndex("long"));
			Address addr = new Address(name,addr1,addr2,zip, lat ,lon);
			list.add(addr);
		}
		if(cursor != null && !cursor.isClosed()){
			cursor.close();
		}
		return list;
	}
	
	public ArrayList<Address> findByAddress(String addr1, String addr2,String zip){
		ArrayList<Address> list = new ArrayList<Address>();
		String query = "addr_line_one = '" + addr1
		+ "' AND addr_line_two = '" + addr2 + "' AND zip = '" + zip + "'";
		Cursor cursor = this.db.query(TABLE_NAME, 
				new String [] {"name",
				"addr_line_one", 
				"addr_line_two",
				"zip", "lat", "long"},
				query, null, null, null, "name desc");
		while(cursor.moveToNext()){
			String instName = cursor.getString(cursor.getColumnIndex("name"));
			String instAddr1 = cursor.getString(cursor.getColumnIndex("addr_line_one"));
			String instAddr2 = cursor.getString(cursor.getColumnIndex("addr_line_two"));
			String instZip = cursor.getString(cursor.getColumnIndex("zip"));
			double instLat = cursor.getDouble(cursor.getColumnIndex("lat"));
			double instLon = cursor.getDouble(cursor.getColumnIndex("long"));
			Address addr = new Address(instName,instAddr1,instAddr2,instZip, instLat ,instLon);
			list.add(addr);
		}
		if(cursor != null && !cursor.isClosed()){
			cursor.close();
		}
		return list;
	}
	
	
	public void close(){
		this.insertStmt.close();
		db.close();
	}
	
	

}
