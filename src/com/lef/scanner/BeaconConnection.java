package com.lef.scanner;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import publicdata.PublicData;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.lef.ibeacon.service.BeaconScanner;
import com.lef.ibeacon.service.ScannerListener;
import com.lef.ibeacon.service.UpdateService;

/**
 * * An class for an Android <code>Activity</code> that wants to interact with
 * iBeacons.<br>
 * 连接beacon时用到，连接成功后可以设置beacon的uuid{@link BeaconConnection#setUUID(String)}
 * 、major、minor、发射功率和频率
 */
public class BeaconConnection implements ScannerListener {
	/**
	 * 默认密码为01<br>
	 * 如果修改过密码，则需要在连接之前，先{@link BeaconConnection@#setPASSWORD(String)};返回再连接
	 */
	public static byte[] PASSWORD = {0x01,0x02,0x03,0x04,0x05,0x06};
	public static byte[] CHGEPWD  = {0x01,0x02,0x03,0x04,0x05,0x06};
	/**
	 * 属性设置成功
	 */
	public static final int SUCCESS = 1;
	/**
	 * 属性设置失败
	 */
	public static final int FAILURE = 2;
	/**
	 * 设置时采用了不合理的值
	 */
	public static final int INVALIDVALUE = 3;
	/**
	 * 正在连接beacon
	 */
	public static final int CONNECTING = 4;
	/**
	 * 连接beacon成功
	 */
	public static final int CONNECTED = 5;
	/**
	 * 断开beacon连接
	 */
	public static final int DISCONNECTED = 6;
	/**
	 * 正在断开beacon连接
	 */
	public static final int DISCONNECTTING = 7;

	private static UpdateService.ServiceBinder mBinder;
	private boolean mBinded;
	private Activity mContext;
	private IBeacon mcurrentBeacon;
	private BeaconConnectionCallback mConnectionCallback;
	private volatile boolean isConnection;
	private BeaconScanner bs;
	private BluetoothDevice bluetoothDevice;
	IntentFilter filter;

	/**
	 * 构造函数，需要实现监听程序BeaconConnectionCallback
	 * 
	 * @param context
	 *            -连接beacon的Activity
	 * @param beacon
	 *            -需要连接的beacon
	 * @param callback
	 *            -实现BeaconConnectionCallback接口，用于连接成功后对beacon进行设置
	 */
	public BeaconConnection(Activity context, IBeacon beacon,
			BeaconConnectionCallback callback) {
	
		this.mContext = context;
		this.mcurrentBeacon = beacon;
		this.mConnectionCallback = callback;
		IntentFilter filter = new IntentFilter();
		filter.addAction(UpdateService.ACTION_STATE_CHANGED);
		filter.addAction(UpdateService.ACTION_DONE);
		filter.addAction(UpdateService.ACTION_GATT_ERROR);

		filter.addAction(UpdateService.ACTION_UUID1_WRITE_READY);
		filter.addAction(UpdateService.ACTION_MAJOR1_WRITE_READY);
		filter.addAction(UpdateService.ACTION_MINOR1_WRITE_READY);
		filter.addAction(UpdateService.ACTION_UUID2_WRITE_READY);
		filter.addAction(UpdateService.ACTION_MAJOR2_WRITE_READY);
		filter.addAction(UpdateService.ACTION_MINOR2_WRITE_READY);	
		
		filter.addAction(UpdateService.ACTION_RSSI_WRITE_READY);
		filter.addAction(UpdateService.ACTION_ADVERTISINGINTERVAL_WRITE_READY);
		filter.addAction(UpdateService.ACTION_TRANSMITPOWER_WRITE_READY);
		filter.addAction(UpdateService.ACTION_TIMER_WRITE_READY);
		filter.addAction(UpdateService.ACTION_RESET_WRITE_READY);
		
		filter.addAction(UpdateService.ACTION_RSSI_UPDATE_READY);
		

		filter.addAction(UpdateService.ACTION_UUID1_READ_READY);
		filter.addAction(UpdateService.ACTION_MAJOR1_READ_READY);
		filter.addAction(UpdateService.ACTION_MINOR1_READ_READY);
		filter.addAction(UpdateService.ACTION_UUID2_READ_READY);
		filter.addAction(UpdateService.ACTION_MAJOR2_READ_READY);
		filter.addAction(UpdateService.ACTION_MINOR2_READ_READY);
		
		filter.addAction(UpdateService.ACTION_RSSI_READ_READY);
		filter.addAction(UpdateService.ACTION_ADVERTISINGINTERVAL_READ_READY);
		filter.addAction(UpdateService.ACTION_TRANSMITPOWER_READ_READY);
		filter.addAction(UpdateService.ACTION_TIMER_READ_READY);
		filter.addAction(UpdateService.ACTION_RESET_READ_READY);
				
		this.filter = filter;
	}

	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(final ComponentName name,
				final IBinder service) {
			isConnection = true;
			final UpdateService.ServiceBinder binder = mBinder = (UpdateService.ServiceBinder) service;
			final int state = binder.getState();
			switch (state) {
			case UpdateService.STATE_DISCONNECTED:
				binder.connect();
				break;
			case UpdateService.STATE_CONNECTED:
				break;
			}
		}

		@Override
		public void onServiceDisconnected(final ComponentName name) {
			mBinder = null;
		}
	};						
	
	//更换密码
	public static void setNewPWD(){   
		if(mBinder==null) PublicData.getInstance().chagepwd_falg = 1;
		if (mBinder!=null && !mBinder.sendChangePwd()) {
			PublicData.getInstance().chagepwd_falg = 1;
		}
	}		
	
	/**
	 * 启动连接Beacon
	 * 
	 * @param device
	 */
	public void connect() {
		bs = new BeaconScanner(mContext, this,
				mcurrentBeacon.getBluetoothAddress());
		bs.startScan();
	}

	/**
	 * 保存设置并断开连接beacon
	 */
	public void rebootAndDisConnect() {
	}

	/**
	 * 断开连接beacon
	 */
	public void disConnect() {

		if (mContext != null) {
			if(isConnection)
			mContext.unbindService(mServiceConnection);
			final Intent service = new Intent(mContext, UpdateService.class);
			mContext.stopService(service);
			LocalBroadcastManager.getInstance(mContext).unregisterReceiver(
					mServiceBroadcastReceiver);
			Log.w("Debug", "停止服务");
			mBinder = null;
			mBinded = false;
			isConnection = false;
			if(bs!=null){
				if(bs.ismIsScanning())
				bs.stopScan();
				bs=null;
			}
		}

	}

	/** 用于延迟加载的handler **/
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

		}
	};

	/**
	 * 判断beacon是否已经连接
	 * 
	 * @return isConnection
	 */
	public boolean isConnection() {
		return isConnection;
	}

	/**
	 * 连接次数计数
	 */
	private int connectCount = 0;
	boolean isReconn = false;
	private BroadcastReceiver mServiceBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();

			if (UpdateService.ACTION_STATE_CHANGED.equals(action)) {
				final int state = intent.getIntExtra(UpdateService.EXTRA_DATA,
						UpdateService.STATE_DISCONNECTED);

				Log.w("ACTION_STATE_CHANGED", "断开回调");
				switch (state) {
				case UpdateService.STATE_DISCONNECTED:
					Log.w("Debug", "断开回调");
					if(!isReconn)
						mConnectionCallback.onConnectedState(mcurrentBeacon,
							DISCONNECTED);

					break;
				case UpdateService.STATE_CONNECTED:
					isConnection = true;
					isReconn = false;
					connectCount = -1;
					mConnectionCallback.onConnectedState(mcurrentBeacon,
							CONNECTED);
					break;
				case UpdateService.STATE_DISCONNECTING:
					mConnectionCallback.onConnectedState(mcurrentBeacon,
							DISCONNECTTING);
					break;
				case UpdateService.STATE_CONNECTING:
					mConnectionCallback.onConnectedState(mcurrentBeacon,
							CONNECTING);
					break;
				}
			}  else if (UpdateService.ACTION_DONE.equals(action)) {
				// 通讯之前的逻辑是先读取各个参数值，然后再写密码，现在连接成功后，立马发送鉴权值，鉴权值写入成功再读取各个参数。
				if (mBinder != null) {
					mBinder.sendConnectAuthenticate();
				}

			} else if (UpdateService.ACTION_RSSI_UPDATE_READY.equals(action)) {				
				Log.w("ACTION_RSSI_UPDATE_READY", " ");
				mConnectionCallback.onGetCalRssi();			
			}else if (UpdateService.ACTION_SOFTREBOOT_READY.equals(action)) {				

				if (mContext != null) {

					mContext.unbindService(mServiceConnection);
					final Intent service = new Intent(mContext,
							UpdateService.class);
					mContext.stopService(service);
					LocalBroadcastManager.getInstance(mContext)
							.unregisterReceiver(mServiceBroadcastReceiver);
					Log.w("Debug", "停止服务");
					mBinder = null;
					mBinded = false;
					isConnection = false;
					if(!isReconn)
						mConnectionCallback.onConnectedState(mcurrentBeacon,
							DISCONNECTED);
				}
			} else if (UpdateService.ACTION_GATT_ERROR.equals(action)) {
				final int error = intent.getIntExtra(UpdateService.EXTRA_DATA,
						0);

				if (mBinded && mBinder != null) {
					mBinder.disconnectAndClose();
				}
				switch (error) {
				case UpdateService.ERROR_UNSUPPORTED_DEVICE:
					Toast.makeText(mContext, "非低功耗蓝牙", Toast.LENGTH_SHORT)
							.show();
					// 出错时候也要先停止服务，在finish
					Intent service = new Intent(mContext, UpdateService.class);
					mContext.unbindService(mServiceConnection);
					mContext.stopService(service);
					Log.w("Debug", "非低功耗蓝牙");
					mContext.finish();
					break;
				default:
					//这里是关键，出现错误时，需要重新连接，首先需要断开蓝牙，其次关闭服务，重新开始扫描蓝牙、启动服务、连接beacon，注意执行次序。设置为两秒
					//蓝牙断开还需要一段时间。
					Timer timer = new Timer();
					isReconn = true;
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Log.w("Debug", "重连");
							if(isConnection){
								Log.w("Debug", "开始重连");							
								disConnect();
								connect();
							}else{
								Log.w("Debug", "无效重连");
							}							
						}
					}, 4000);										
				}

			}
		}
	};

	/**
	 * SDK自动回调，开发者不需要调用
	 * 
	 * @param BluetoothDevice
	 * @param BluetoothDeviceName
	 */
	@Override
	public void onDeviceSelected(BluetoothDevice device, String name) {
		
		// This will connect to the service only if it's already running
		bs.stopScan();
		bluetoothDevice = device;
		final Activity activity = mContext;
		final Intent service = new Intent(activity, UpdateService.class);
		service.putExtra(UpdateService.EXTRA_DATA, device);
		activity.startService(service);
		mBinded = true;
		activity.bindService(service, mServiceConnection, 0);
		LocalBroadcastManager.getInstance(mContext).registerReceiver(
				mServiceBroadcastReceiver, filter);
	}

	public static byte[] getPASSWORD() {
		return PASSWORD;
	}

	public static void setPASSWORD(byte[] pASSWORD) {
		PASSWORD = pASSWORD;
	}
	
	public static byte[] getCHGEPWD() {
		return CHGEPWD;
	}

	public static void setCHGEPWD(byte[] pCHGEPWD) {
		CHGEPWD = pCHGEPWD;
	}
}
