package com.whh.beaconsdk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper{
public static final String CREATE_BEACON="create table Beacon("
+"latitude real,"
+"longitude real,"
+"major text,"
+"minor text,"
+"rssi real)";
private static final String CREATE_IF_NECESSARY = null;
/*public static final String CREATE_CATEGORY="create table Category("
+"id integer primary key autoincrement,"
+"category_name text,"
+"categoty_code integer)";*/
 private Context mContext;
 public MyDatabaseHelper(Context context,String name,CursorFactory factory, int version){
	 super(new CustomPathDatabaseContext(context, getDirPath()), name, factory, version);
	 mContext=context;
 }
 private static String getDirPath(){
     //TODO 这里返回存放db的文件夹的绝对路径
	 String dbPath = "/sdcard/xxk/Beacon.db";
     return dbPath;
}
 @Override
 public void onCreate(SQLiteDatabase db){
	 Toast.makeText(mContext,"Create succeeded", Toast.LENGTH_SHORT).show();
	db.execSQL(CREATE_BEACON);
	// String Path="/DCIM/BEACON.db";
	//public static SQLiteDatabase openOrCreateDatabase("/DCIM/BEACON.db", CursorFactory factory);
 }

 @Override
 public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
	 
 }
}
