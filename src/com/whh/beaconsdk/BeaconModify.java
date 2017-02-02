package com.whh.beaconsdk;

import java.util.ArrayList;

import publicdata.PublicData;
import android.widget.Spinner;
import android.widget.AdapterView;

import com.whh.beaconsdk.R;
import com.lef.scanner.BeaconConnection;
import com.lef.scanner.BeaconConnectionCallback;
import com.lef.scanner.IBeacon;
import com.lef.scanner.BaseSettings;

import android.os.Bundle;
import android.os.Handler;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

public class BeaconModify extends Activity implements BeaconConnectionCallback {

	private IBeacon  currentBeacon;
	private TextView rssiTextView;	
//	private RssiAdapter rssiAdapter;
	private BeaconConnection beaconConnection;
	private static final int CONNECTION_S = 4;
	private static final int CONNECTION_F = 5;
	private static final int UPDATERSSI = 8;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CONNECTION_S:
				Toast.makeText(BeaconModify.this, "连接成功", Toast.LENGTH_SHORT)
						.show();								
					rssiTextView.setText(PublicData.getInstance().phone_rssi + "");											
				break;
			case CONNECTION_F:
				Toast.makeText(BeaconModify.this, "连接断开,快点重连", Toast.LENGTH_SHORT)
						.show();
				break;
			case UPDATERSSI:
				rssiTextView.setText(PublicData.getInstance().rss_to_distance() + "");
				break;
			default:
				break;

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beacon_modify);
		
		ActionBar mainBar = getActionBar();
			mainBar.setLogo(R.drawable.ic_setting);
			mainBar.setTitle(R.string.ibeacon_attribute);
		rssiTextView = (TextView) findViewById(R.id.rssi_modify);	
		Intent intent = getIntent();
		currentBeacon = intent.getParcelableExtra("beacon");

		beaconConnection = new BeaconConnection(this, currentBeacon, this);
		beaconConnection.connect();
		
//		rssiAdapter = new RssiAdapter(this, PublicData.getInstance().phone_rssi);	
																			
	}
											
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 无需在这里判断是否连接成功，disconnect中会判断，所以只要connection不为空，就应该dis，
		// 否则在未连接成功时则不dis，会出现错误
		if (beaconConnection != null) {
			beaconConnection.disConnect();
		}
		this.finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		menu.add(0, 0, 0, "修改密码");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == 0) {//change passwd	
			
			Intent intent = new Intent(BeaconModify.this,
					ChangePasswdActivity.class);
			BeaconModify.this.startActivity(intent);
		}
		return true;
	}

	@Override
	public void onConnectedState(IBeacon beacon, int status) {
		// TODO Auto-generated method stub
		switch (status) {
		case BeaconConnection.CONNECTED:
				Log.w("BeaconConnection.CONNECTED: " ,beacon.getProximityUuid1());
				currentBeacon = beacon;
				handler.sendEmptyMessage(CONNECTION_S);
				Log.i("Debug", "状态改变，连接成功");
			break;
		case BeaconConnection.DISCONNECTED:
			handler.sendEmptyMessage(CONNECTION_F);
			break;
		case BeaconConnection.CONNECTING:
		case BeaconConnection.DISCONNECTTING:
		default:
			break;
		}
	}
	
	@Override
	public void onGetCalRssi() {					
		handler.sendEmptyMessage(UPDATERSSI);	
		Log.w("123","UPDATERSSI");
	}
	
}
