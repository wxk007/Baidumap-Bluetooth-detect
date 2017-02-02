package com.whh.beaconsdk;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import publicdata.PublicData;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.whh.beaconsdk.R;
import com.lef.scanner.BeaconConnection;
import com.lef.scanner.IBeacon;
import com.lef.scanner.IBeaconData;
import com.lef.scanner.IBeaconManager;
import com.lef.scanner.MonitorNotifier;
import com.lef.scanner.RangeNotifier;
import com.lef.scanner.Region;

import org.json.JSONArray;
import org.json.JSONException;  
import org.json.JSONObject;  
import org.json.JSONStringer;  
//import org.junit.Test;  
  

public class MainActivity extends Activity implements
		com.lef.scanner.IBeaconConsumer {
	// 启动蓝牙请求码
	protected static final int BLUTETOOTH = 1;
	private IBeaconManager iBeaconManager;
	private ListView beaconListView;
	private LocationManager locationManager;
	private String provider;
	private MyDatabaseHelper dbHelper;
	
	/**
	 * 数据更新
	 */
	private ArrayList<IBeacon> beaconDataListA = new ArrayList<IBeacon>();
	/**
	 * UI数据
	 */
	private ArrayList<IBeacon> beaconDataListB = new ArrayList<IBeacon>();

	// 云子数据Adapter
	private BeaconAdapter beaconListAdapter;
	// 数据常量
	private static final int UPDATEUI = 1;
	private static final int PROGRESSBARGONE = 2;
	private static final int CLICKTOAST = 3;
	
	//
	Handler UIHandler = new Handler();
	// 开始扫描时使用ProgressBar
	private ProgressBar progressScan;
	private TextView progressScanTextView;
	private boolean ProgressBarVisibile = true;
	


	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATEUI:
				beaconDataListB.clear();
				beaconDataListB.addAll(beaconDataListA);
				beaconListAdapter.notifyDataSetChanged();
				break;
			case PROGRESSBARGONE:
				progressScan.setVisibility(TextView.GONE);
				progressScanTextView.setVisibility(TextView.GONE);
				ProgressBarVisibile = false;
				break;
			case CLICKTOAST:
				Toast.makeText(MainActivity.this, "请设置beacon部署模式",
				Toast.LENGTH_SHORT).show();
			default:
				break;
			}
		};
	};	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 设置名称
		ActionBar mainBar = getActionBar();
		mainBar.setLogo(R.drawable.ic_list);
		mainBar.setTitle(R.string.ibeacon_list);
		dbHelper=new MyDatabaseHelper(this,"Beacon.db",null,1);
		//dbHelper=new MyDatabaseHelper(this,"Beacon.db",null,1);
	  
		
		
		iBeaconManager = IBeaconManager.getInstanceForApplication(this);
        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        List<String>providerList=locationManager.getProviders(true);		// 获取View
        if(providerList.contains(LocationManager.GPS_PROVIDER)){
        	provider=LocationManager.GPS_PROVIDER;
        	Log.d("MainActivity","yunxingle1");
        }
//        else if(providerList.contains(LocationManager.NETWORK_PROVIDER)){
//        	provider=LocationManager.NETWORK_PROVIDER;
//        	Log.d("MainActivity","yunxingle2");
//        }
        else{
        	Toast.makeText(this, "No provider avaliable", Toast.LENGTH_SHORT).show();
        	Log.d("MainActivity","yunxingle3");
        	return;	
        }
        //Location location=locationManager.getLastKnownLocation(provider);
		beaconListView = (ListView) findViewById(R.id.yunzilist);
		progressScan = (ProgressBar) findViewById(R.id.progressScan);
		progressScanTextView = (TextView) findViewById(R.id.progressScantext);
		
		beaconListAdapter = new BeaconAdapter(this, beaconDataListB);
		beaconListView.setAdapter(beaconListAdapter);		
		BeaconConnection.setPASSWORD(PublicData.getInstance().passwd);
		beaconListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {	
				Intent mintent = new Intent(MainActivity.this,
						BeaconModify.class);
				//需要把IBeacon类型封装成IBeacon，以方便在intent传输
				mintent.putExtra("beacon",
						new IBeaconData(beaconDataListB.get(position)));
				startActivity(mintent);
				if (iBeaconManager != null && iBeaconManager.isBound(MainActivity.this)) {
					iBeaconManager.unBind(MainActivity.this);
				}
			}
		});
		locationManager.requestLocationUpdates(provider, 100, (float)0.1, locationListener);
	}

	 LocationListener locationListener=new LocationListener(){
	    	@Override
	    	public void onStatusChanged(String provider,int status,Bundle extras){}
	    	@Override
	    	public void onProviderEnabled(String provider){}
	    	@Override
	    	public void onProviderDisabled(String provider){}
	    	@Override
	    	public void onLocationChanged(Location location){
	    		//showLocation(location);
	    		Log.e("test","1111");
	    		Log.d("gps",""+location.getLatitude()+":"+location.getLongitude());
	    		SharedPreferences.Editor editor= getSharedPreferences("gps", 
	    				MODE_PRIVATE).edit(); 
	    		editor.putFloat("logitude",(float)location.getLongitude());
	    		editor.putFloat("latitude",(float)location.getLatitude());
	    		editor.apply();
	  
	    		
	    		
	    	}
	    	
	    };
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		BeaconConnection.setPASSWORD(PublicData.getInstance().passwd);
		if (iBeaconManager!=null&&!iBeaconManager.isBound(this)) {
			if(beaconDataListA.size()>0){
				beaconDataListA.clear();
			}
			// 蓝牙dialog
			initBluetooth();
		}
		PublicData.getInstance().flag = 0;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (iBeaconManager.isBound(this)) {
			iBeaconManager.unBind(this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (iBeaconManager != null && iBeaconManager.isBound(this)) {
			iBeaconManager.unBind(this);
		}
		dbHelper.close();
		if(locationManager!=null){
    		locationManager.removeUpdates(locationListener);
    	}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "修改密码");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == 0) {//batch			
			Intent intent = new Intent(MainActivity.this,
					ChangePasswdActivity.class);
			MainActivity.this.startActivity(intent);
		}

		return true;
	}

	/**
	 * 请求打开蓝牙，如果不打开，将退出程序
	 */
	private void initBluetooth() {
		final BluetoothAdapter blueToothEable = BluetoothAdapter
				.getDefaultAdapter();
		if (!blueToothEable.isEnabled()) {
			new AlertDialog.Builder(MainActivity.this)
					.setIcon(R.drawable.title_bar_menu).setTitle("蓝牙开启")
					.setMessage("云子配置需要开启蓝牙").setCancelable(false)
					.setPositiveButton("开启", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							blueToothEable.enable();
							iBeaconManager.bind(MainActivity.this);
						}
					}).setNegativeButton("退出", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							MainActivity.this.finish();
						}
					}).create().show();
		} else {
			BluetoothAdapter pt;
			Class<BluetoothAdapter> clazz = BluetoothAdapter.class;        
			try {
				pt = BluetoothAdapter.class.newInstance();
				Field field = clazz.getDeclaredField("DBG");        
		        field.setAccessible(true);        
		        field.set(pt, false);        
		        field.setAccessible(false);  
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}   
            	                	        
			iBeaconManager.setForegroundScanPeriod(500);
			iBeaconManager.bind(this);
		}
	}


	@Override
	public void onIBeaconServiceConnect() {
		// 启动Range服务
		iBeaconManager.setRangeNotifier(new RangeNotifier() {
			public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons,
					Region region) {
				if (ProgressBarVisibile) {
					handler.sendEmptyMessage(PROGRESSBARGONE);
				}
			}
			@Override
			public void onNewBeacons(Collection<IBeacon> iBeacons, Region region) {
				//dbHelper=new MyDatabaseHelper(this,"Beacon.db",null,1);
				
				//String PATH="/DCIM/Beacon.db";
				 //db=SQLiteDatabase.openOrCreateDatabase(PATH, null);
				java.util.Iterator<IBeacon> iterator = iBeacons.iterator();
				while (iterator.hasNext()) {
					IBeacon temp = iterator.next();
					if (!beaconDataListA.contains(temp)) {												
//						  if(BeaconAdapter.getDistance(temp).equals("XiangBeacon"))							  
//							if(beaconDataListA.size() < 10)
						if(temp.getRssi()>-100){
							beaconDataListA.add(temp);
							
						}
							
					}
					
					handler.sendEmptyMessage(UPDATEUI);
				}
			}
			

			@Override
			public void onGoneBeacons(Collection<IBeacon> iBeacons,
					Region region) {
				java.util.Iterator<IBeacon> iterator = iBeacons.iterator();
				while (iterator.hasNext()) {
					IBeacon temp = iterator.next();
					if (beaconDataListA.contains(temp)) {
						beaconDataListA.remove(temp);
					}
					handler.sendEmptyMessage(UPDATEUI);
				}
			}

			@Override
			public void onUpdateBeacon(Collection<IBeacon> iBeacons,
					Region region) {
				java.util.Iterator<IBeacon> iterator = iBeacons.iterator();
				while (iterator.hasNext()) {
					IBeacon temp = iterator.next();
					if (beaconDataListA.contains(temp)) {
						beaconDataListA.set(beaconDataListA.indexOf(temp), temp);
						//Location location=locationManager.getLastKnownLocation(provider);
						
						SQLiteDatabase db=dbHelper.getWritableDatabase();
//						if(location!=null){
//							ContentValues values=new ContentValues();
//							values.put("latitude", location.getLatitude());
//							values.put("longitude",location.getLongitude() );
//							values.clear();
//						}
							SharedPreferences gps=getSharedPreferences("gps",MODE_PRIVATE);
							Double a=(double)gps.getFloat("latitude", 0);
							Double b=(double)gps.getFloat("logitude", 0);
							String c= temp.getBluetoothAddress();
							Double d=(double) temp.getMinor1();
							Double e=(double) temp.getRssi();
				
							ContentValues values=new ContentValues();
							
							values.put("latitude", a);
							values.put("longitude",b);
							values.put("major",c);
							values.put("minor", String.valueOf(d));
							values.put("rssi", e);
							db.insert("Beacon", null, values);
							JSONArray array=new JSONArray();
				            JSONObject object = new JSONObject();  
							JSONObject beaconJSONObject = new JSONObject();
							 try {  
							        beaconJSONObject.put("lng", b);  
						            beaconJSONObject.put("lat", a);  
						            beaconJSONObject.put("id", c);
						            array.put(beaconJSONObject);
						            object.put("stones", array);  
						        } 
							 catch (JSONException e1) {  
						            e1.printStackTrace();  
						        }  
							
						//if(location!=null){}
					}
					handler.sendEmptyMessage(UPDATEUI);
				}
			}			
		});
		iBeaconManager.setMonitorNotifier(new MonitorNotifier() {
			@Override
			public void didExitRegion(Region region) {
				// TODO Auto-generated method stub
			}

			@Override
			public void didEnterRegion(Region region) {
				// TODO Auto-generated method stub
			}

			@Override
			public void didDetermineStateForRegion(int state, Region region) {
				// TODO Auto-generated method stub
			}
		});
		try {
			Region myRegion = new Region("myRangingUniqueId", null, null, null);
			iBeaconManager.startRangingBeaconsInRegion(myRegion);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}


}
