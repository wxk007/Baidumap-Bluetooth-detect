package com.lef.ibeacon.service;

import java.util.UUID;



import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;
/**
 * 扫描周围蓝牙，寻找需要连接的beacon
 * @author lief
 *
 */
public class BeaconScanner {

	private final static String TAG = "BeaconScanner";
	private BluetoothAdapter mBluetoothAdapter;
	private Context mContext;
	private static final UUID BEACON_CONFIG_UUID = UUID
			.fromString("955A1523-0FE2-F5AA-0A094-84B8D4F3E8AD");
	private ScannerListener mListener;
	
	private UUID mUuid;
	private boolean mIsScanning = false;
	private String bluetoothAdress;

	public BeaconScanner(Context context, ScannerListener listener ,String bluetoothAdress) {
		super();
		this.mContext = context;
		this.mListener = listener;
		this.bluetoothAdress = bluetoothAdress;
		mUuid = BEACON_CONFIG_UUID;
		BluetoothManager manager = (BluetoothManager) mContext
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = manager.getAdapter();
	}

	/**
	 * 开始扫描
	 */
	public void startScan() {
		if (!mIsScanning) {
			mBluetoothAdapter.startLeScan(mLEScanCallback);
			Log.i("Debug", "开始扫描"+bluetoothAdress);
			mIsScanning = true;
		}
	}

	/**
	 * ֹͣ停止扫描
	 */
	public void stopScan() {
		if (mIsScanning) {
			mBluetoothAdapter.stopLeScan(mLEScanCallback);
			mIsScanning = false;
		}
	}

	/**
	 * Callback for scanned devices class {@link ScannerServiceParser} will be
	 * used to filter devices with custom BLE service UUID then the device will
	 * be added in a list
	 */
	private BluetoothAdapter.LeScanCallback mLEScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			if (device != null) {
				try {
					//原版
//					if (ScannerServiceParser.decodeDeviceAdvData(scanRecord,
//							mUuid)&&device.getAddress().equals(bluetoothAdress)) {
					//新版本
					if (device.getAddress().equals(bluetoothAdress)) {
						// On some devices device.getName() is always null. We
						// have to parse the name manually :(
						// This bug has been found on Sony Xperia Z1 (C6903)
						// with Android 4.3.
						// https://devzone.nordicsemi.com/index.php/cannot-see-device-name-in-sony-z1
						mListener.onDeviceSelected(device, ScannerServiceParser
								.decodeDeviceName(scanRecord));
						Log.i("Debug", "连接beacon"+bluetoothAdress);
//						BeaconDevice temp = new BeaconDevice(device, ScannerServiceParser
//								.decodeDeviceName(scanRecord), rssi);
//						if(beacons.contains(temp)){
//							mListener.onUpdateBeacon(temp);
//						}else{
//							mListener.onNewBeacon(temp);
//						}
					}
				} catch (Exception e) {
					Log.w(TAG,
							"Invalid data in Advertisement packet "
									+ e.toString());
				}
			}
		}
	};

	public boolean ismIsScanning() {
		return mIsScanning;
	}

	public void setmIsScanning(boolean mIsScanning) {
		this.mIsScanning = mIsScanning;
	}
}
