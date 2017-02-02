/*******************************************************************************
 * Copyright (c) 2014 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package com.lef.ibeacon.service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import publicdata.PublicData;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.whh.beaconsdk.BuildConfig;
import com.lef.scanner.BeaconConnection;

public class UpdateService extends Service {
	private static final String TAG = "Debug";
	/**
	 * 定义action的名字
	 */
	/**
	 * 连接状态改变，连接成功
	 */
	public final static String ACTION_STATE_CHANGED = "no.nordicsemi.android.nrfbeacon.ACTION_STATE_CHANGED";
	public final static String ACTION_GATT_ERROR = "no.nordicsemi.android.nrfbeacon.ACTION_GATT_ERROR";
	/**
	 * 发现蓝牙service
	 */
	public final static String ACTION_DONE = "no.nordicsemi.android.nrfbeacon.ACTION_DONE";
	public final static String ACTION_SOFTREBOOT_READY = "no.nordicsemi.android.nrfbeacon.ACTION_SOFTREBOOT_READY";

	public final static String ACTION_UUID1_READ_READY = "no.nordicsemi.android.nrfbeacon.ACTION_UUID1_READ_READY";
	public final static String ACTION_MAJOR1_READ_READY = "no.nordicsemi.android.nrfbeacon.ACTION_MAJOR1_READ_READY";
	public final static String ACTION_MINOR1_READ_READY = "no.nordicsemi.android.nrfbeacon.ACTION_MINOR1_READ_READY";
	public final static String ACTION_UUID2_READ_READY = "no.nordicsemi.android.nrfbeacon.ACTION_UUID2_READ_READY";
	public final static String ACTION_MAJOR2_READ_READY = "no.nordicsemi.android.nrfbeacon.ACTION_MAJOR2_READ_READY";
	public final static String ACTION_MINOR2_READ_READY = "no.nordicsemi.android.nrfbeacon.ACTION_MINOR2_READ_READY";
	
	public final static String ACTION_RSSI_READ_READY = "no.nordicsemi.android.nrfbeacon.ACTION_RSSI_READ_READY";
	
	public final static String ACTION_ADVERTISINGINTERVAL_READ_READY = "no.nordicsemi.android.nrfbeacon.ACTION_ADVERTISINGINTERVAL_READ_READY";
	public final static String ACTION_TRANSMITPOWER_READ_READY = "no.nordicsemi.android.nrfbeacon.ACTION_TRANSMITPOWER_READ_READY";
	public final static String ACTION_TIMER_READ_READY ="no.nordicsemi.android.nrfbeacon.ACTION_TIMER_READ_READY";
	public final static String ACTION_RESET_READ_READY ="no.nordicsemi.android.nrfbeacon.ACTION_RESET_READ_READY";

	public final static String ACTION_RSSI_UPDATE_READY = "no.nordicsemi.android.nrfbeacon.ACTION_RSSI_UPDATE_READY";
	
	public final static String ACTION_UUID1_WRITE_READY = "no.nordicsemi.android.nrfbeacon.ACTION_UUID1_WRITE_READY";
	public final static String ACTION_MAJOR1_WRITE_READY = "no.nordicsemi.android.nrfbeacon.ACTION_MAJOR1_WRITE_READY";
	public final static String ACTION_MINOR1_WRITE_READY = "no.nordicsemi.android.nrfbeacon.ACTION_MINOR1_WRITE_READY";
	public final static String ACTION_UUID2_WRITE_READY = "no.nordicsemi.android.nrfbeacon.ACTION_UUID2_WRITE_READY";
	public final static String ACTION_MAJOR2_WRITE_READY = "no.nordicsemi.android.nrfbeacon.ACTION_MAJOR2_WRITE_READY";
	public final static String ACTION_MINOR2_WRITE_READY = "no.nordicsemi.android.nrfbeacon.ACTION_MINOR2_WRITE_READY";
	
	public final static String ACTION_RSSI_WRITE_READY = "no.nordicsemi.android.nrfbeacon.ACTION_RSSI_WRITE_READY";
	
	public final static String ACTION_ADVERTISINGINTERVAL_WRITE_READY = "no.nordicsemi.android.nrfbeacon.ACTION_ADVERTISINGINTERVAL_WRITE_READY";
	public final static String ACTION_TRANSMITPOWER_WRITE_READY = "no.nordicsemi.android.nrfbeacon.ACTION_TRANSMITPOWER_WRITE_READY";
	public final static String ACTION_TIMER_WRITE_READY ="no.nordicsemi.android.nrfbeacon.ACTION_TIMER_WRITE_READY";
	public final static String ACTION_RESET_WRITE_READY ="no.nordicsemi.android.nrfbeacon.ACTION_RESET_WRITE_READY";


	public final static String EXTRA_DATA = "no.nordicsemi.android.nrfbeacon.EXTRA_DATA";
	public final static String EXTRA_DATA1 = "no.nordicsemi.android.nrfbeacon.EXTRA_DATA1";
	public final static String EXTRA_DATA2 = "no.nordicsemi.android.nrfbeacon.EXTRA_DATA2";
	
	public final static String EXTRA_MAJOR1 = "no.nordicsemi.android.nrfbeacon.EXTRA_MAJOR1";
	public final static String EXTRA_MINOR1 = "no.nordicsemi.android.nrfbeacon.EXTRA_MINOR1";
	public final static String EXTRA_MAJOR2 = "no.nordicsemi.android.nrfbeacon.EXTRA_MAJOR2";
	public final static String EXTRA_MINOR2 = "no.nordicsemi.android.nrfbeacon.EXTRA_MINOR2";
	
	
	public final static String EXTRA_ADVERTISINGINTERVAL = "no.nordicsemi.android.nrfbeacon.EXTRA_ADVERTISINGINTERVAL";
	public final static String EXTRA_TRANSMITPOWER = "no.nordicsemi.android.nrfbeacon.EXTRA_TRANSMITPOWER";
	public final static String EXTRA_RESET = "no.nordicsemi.android.nrfbeacon.EXTRA_RESETL";
	public final static String EXTRA_TIMER = "no.nordicsemi.android.nrfbeacon.EXTRA_TIMER";
	

	public final static int ERROR_UNSUPPORTED_DEVICE = -1;

	private int mConnectionState;
	public final static int STATE_DISCONNECTED = 0;
	public final static int STATE_CONNECTING = 1;
	public final static int STATE_DISCOVERING_SERVICES = 2;
	public final static int STATE_CONNECTED = 3;
	public final static int STATE_DISCONNECTING = 4;

	public final static int SERVICE_UUID = 1;
	public final static int SERVICE_MAJOR_MINOR = 2;
	public final static int SERVICE_CALIBRATION = 3;
	/**
	 * 蓝牙服务的uuid
	 */
	//访问服务：
	//1、密码 特征值   2、密码修改 特征值
	private static final UUID ACCESS_SERVICE_UUID = new UUID(
			0X000015231212efdel,0x1523785feabcd123l);
	private static final UUID ACCESS_PWD_CHARACTERISTIC_UUID = new UUID(
			0X000015241212efdel,0x1523785feabcd123l);
	private static final UUID ACCESS_CHGEPWD_CHARACTERISTIC_UUID = new UUID(
			0X000015251212efdel,0x1523785feabcd123l);
	
	//1m时RSSI校准服务：
	//1、RSSI 特征值
	private static final UUID DIS_SERVICE_UUID = new UUID(
			0X4432000276655332l,0x4573161452247644l);
	private static final UUID DIS_RSSI_CHARACTERISTIC_UUID = new UUID(
			0X0000000100001000l,0x800000805f9b34fbl);
	
	//为厂家Ali提供Beacon基本信息服务：
	//1、major 2、minor 3、uuid	
	private static final UUID BEACON1_SERVICE_UUID = new UUID(
			0X1122123133445566l,0x7788990077666564l);
	private static final UUID CONFIG1_MAJOR_CHARACTERISTIC_UUID = new UUID(
			0x0000001200001000l, 0X800000805f9b34fbl);
	private static final UUID CONFIG1_MINOR_CHARACTERISTIC_UUID = new UUID(
			0x0000001300001000l, 0X800000805f9b34fbl);
	private static final UUID CONFIG1_UUID_CHARACTERISTIC_UUID = new UUID(
			0x0000001100001000l, 0X800000805f9b34fbl);
	
	//为厂家Baidu提供Beacon基本信息服务：
		//1、major 2、minor 3、uuid	
		private static final UUID BEACON2_SERVICE_UUID = new UUID(
				0X1122123233445566l,0x7438110077665444l);
		private static final UUID CONFIG2_MAJOR_CHARACTERISTIC_UUID = new UUID(
				0x0000002200001000l, 0X800000805f9b34fbl);
		private static final UUID CONFIG2_MINOR_CHARACTERISTIC_UUID = new UUID(
				0x0000002300001000l, 0X800000805f9b34fbl);
		private static final UUID CONFIG2_UUID_CHARACTERISTIC_UUID = new UUID(
				0x0000002100001000l, 0X800000805f9b34fbl);
	
	//发射功率和广播频率可配置服务：
		private static final UUID TPS_SERVICE_UUID = new UUID(
				0X4527000388773343l,0x6742368453362545l);
		private static final UUID TPS_TXPOWER_CHARACTERISTIC_UUID = new UUID(
				0x0000003200001000l, 0X800000805f9b34fbl);		
		private static final UUID TPS_ADVERTISINGINTERVAL_CHARACTERISTIC_UUID = new UUID(
				0x0000003100001000l, 0X800000805f9b34fbl);
	
	//一键置位服务：
		private static final UUID KEYSET_SERVICE_UUID = new UUID(
				0X000044231212efdel,0x1523785feabcd111l);
		private static final UUID KEYSET_RESET_CHARACTERISTIC_UUID = new UUID(
				0x000044241212efdel, 0X1523785feabcd111l);
		
	//设置广播切换间隔服务：
		private static final UUID TIMER_SERVICE_UUID = new UUID(
				0X0000112312433276l,0x1523212132534121l);
		private static final UUID TIMER_SET_CHARACTERISTIC_UUID = new UUID(
				0X0000112412433276l,0x1523212132534121l);
	
	//Beacon追踪
		private static final UUID TRACKING_SERVICE_UUID = new UUID(
				0X2232324114862346l,0x2535714636125332l);
		private static final UUID TRACKING_RSSI_CHARACTERISTIC_UUID = new UUID(
				0X2232324214862346l,0x2535714636125332l);
		protected static final UUID RSSI_DESCRIPTER_UUID = new UUID(
				0X0000290200001000l,0x800000805f9b34fbl);

	private BluetoothAdapter mAdapter;
	private BluetoothDevice mBluetoothDevice;
	private BluetoothGatt mBluetoothGatt;
	
	//下面是特征值的汇总声明定义！！！
	private BluetoothGattCharacteristic mPwdCharacteristic;
	private BluetoothGattCharacteristic mChgepwdCharacteristic;
	
	private BluetoothGattCharacteristic mUuid1Characteristic;
	private BluetoothGattCharacteristic mMajor1Characteristic;
	private BluetoothGattCharacteristic mMinor1Characteristic;
	
	private BluetoothGattCharacteristic mUuid2Characteristic;
	private BluetoothGattCharacteristic mMajor2Characteristic;
	private BluetoothGattCharacteristic mMinor2Characteristic;
	
	private BluetoothGattCharacteristic mRssiCharacteristic;
	
	private BluetoothGattCharacteristic mAdvertisingintervalCharacteristic;
	private BluetoothGattCharacteristic mTransmitpowerCharacteristic;
	
	private BluetoothGattCharacteristic mKeysetCharacteristic;
	
	private BluetoothGattCharacteristic mTimerCharacteristic;
	
	private BluetoothGattCharacteristic mTrackRssiCharacteristic;

	private Handler mHandler;
	private volatile boolean errorSign = false;
	Timer timer = new Timer();
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(final BluetoothGatt gatt,
				final int status, final int newState) {
			if (status != BluetoothGatt.GATT_SUCCESS) {
//				logw("Connection state change error: " + status);
//				Log.w(TAG, status + "");
				broadcastError(status);
				return;
			}

			if (newState == BluetoothProfile.STATE_CONNECTED) {
				setState(STATE_DISCOVERING_SERVICES);

				// Attempts to discover services after successful connection.
				gatt.discoverServices();
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				setState(STATE_DISCONNECTED);
				if (gatt != null) {
					refreshDeviceCache(gatt);
					gatt.close();
				}
				mBluetoothGatt = null;
				stopSelf();
			}
		}
		public void onCharacteristicChanged (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) 
		{
//			logw("onCharacteristicChanged RSSI: " + characteristic.getIntValue(
//					BluetoothGattCharacteristic.FORMAT_SINT8, 0));
			if(TRACKING_RSSI_CHARACTERISTIC_UUID.equals(characteristic.getUuid())){
					
				PublicData.getInstance().phone_rssi = characteristic.getIntValue(
						BluetoothGattCharacteristic.FORMAT_SINT8, 0);
				broadcastRssiUpdate();
			}
		 
		}
		// 服务发现回调，目前有8大服务
		@Override
		public void onServicesDiscovered(final BluetoothGatt gatt,
				final int status) {
			if (status != BluetoothGatt.GATT_SUCCESS) {
//				logw("Service discovery error: " + status);
				broadcastError(status);
				return;
			}
			// 筛选！！！
			List<BluetoothGattService> ListService =gatt.getServices();
			if(ListService == null){
				broadcastError(ERROR_UNSUPPORTED_DEVICE);
				setState(STATE_DISCONNECTING);
				gatt.disconnect();
				return;
			}			
			if(ListService.contains(gatt.getService(ACCESS_SERVICE_UUID)))
			{
				final BluetoothGattService accessService = gatt
						.getService(ACCESS_SERVICE_UUID);
				mPwdCharacteristic = accessService
						.getCharacteristic(ACCESS_PWD_CHARACTERISTIC_UUID);
				mChgepwdCharacteristic = accessService
						.getCharacteristic(ACCESS_CHGEPWD_CHARACTERISTIC_UUID);
				if (mPwdCharacteristic != null || mChgepwdCharacteristic != null){
//					logw("ACCESS Service discovery success: ");
					broadcastOperationCompleted();
				}

				if (mPwdCharacteristic == null && mChgepwdCharacteristic == null) {
					// Config characteristics is not present
					broadcastError(ERROR_UNSUPPORTED_DEVICE);
					setState(STATE_DISCONNECTING);
					gatt.disconnect();
					return;
				}			
			}
			if(ListService.contains(gatt.getService(DIS_SERVICE_UUID)))
			{
				final BluetoothGattService disService = gatt
						.getService(DIS_SERVICE_UUID);
				mRssiCharacteristic = disService
						.getCharacteristic(DIS_RSSI_CHARACTERISTIC_UUID);				
				if (mRssiCharacteristic != null){
//					logw("DIS Service discovery success: ");
					broadcastOperationCompleted();
				}

				if (mRssiCharacteristic == null) {
					// Config characteristics is not present
					broadcastError(ERROR_UNSUPPORTED_DEVICE);
					setState(STATE_DISCONNECTING);
					gatt.disconnect();
					return;
				}
			}
			if(ListService.contains(gatt.getService(KEYSET_SERVICE_UUID)))
			{
				final BluetoothGattService keysetService = gatt
						.getService(KEYSET_SERVICE_UUID);
				mKeysetCharacteristic = keysetService
						.getCharacteristic(KEYSET_RESET_CHARACTERISTIC_UUID);				
				if (mKeysetCharacteristic != null){
//					logw("KEYSET Service discovery success: ");
					broadcastOperationCompleted();
				}

				if (mKeysetCharacteristic == null) {
					// Config characteristics is not present
					broadcastError(ERROR_UNSUPPORTED_DEVICE);
					setState(STATE_DISCONNECTING);
					gatt.disconnect();
					return;
				}
			}
			if(ListService.contains(gatt.getService(TIMER_SERVICE_UUID)))
			{
				final BluetoothGattService keysetService = gatt
						.getService(TIMER_SERVICE_UUID);
				mTimerCharacteristic = keysetService
						.getCharacteristic(TIMER_SET_CHARACTERISTIC_UUID);				
				if (mTimerCharacteristic != null){
//					logw("TIMER Service discovery success: ");
					broadcastOperationCompleted();
				}

				if (mTimerCharacteristic == null) {
					// Config characteristics is not present
					broadcastError(ERROR_UNSUPPORTED_DEVICE);
					setState(STATE_DISCONNECTING);
					gatt.disconnect();
					return;
				}
			}
			if(ListService.contains(gatt.getService(TRACKING_SERVICE_UUID)))
			{
				final BluetoothGattService trackingService = gatt
						.getService(TRACKING_SERVICE_UUID);
				mTrackRssiCharacteristic = trackingService
						.getCharacteristic(TRACKING_RSSI_CHARACTERISTIC_UUID);				
				if (mTrackRssiCharacteristic != null){
					logw("mTrackRssiCharacteristic Service discovery success: ");
					broadcastOperationCompleted();
				}

				if (mTrackRssiCharacteristic == null) {
					// Config characteristics is not present
					broadcastError(ERROR_UNSUPPORTED_DEVICE);
					setState(STATE_DISCONNECTING);
					gatt.disconnect();
					return;
				}					
			}
			if(ListService.contains(gatt.getService(BEACON1_SERVICE_UUID)))
			{
				final BluetoothGattService beacon1Service = gatt
						.getService(BEACON1_SERVICE_UUID);
				mMajor1Characteristic = beacon1Service
						.getCharacteristic(CONFIG1_MAJOR_CHARACTERISTIC_UUID);				
				mMinor1Characteristic = beacon1Service
						.getCharacteristic(CONFIG1_MINOR_CHARACTERISTIC_UUID);				
				mUuid1Characteristic = beacon1Service
						.getCharacteristic(CONFIG1_UUID_CHARACTERISTIC_UUID);							
						
				if (mMajor1Characteristic != null && mMinor1Characteristic != null && mUuid1Characteristic != null){
//					logw("BEACON1 Service discovery success: ");
					broadcastOperationCompleted();
				}
					
				if (mMajor1Characteristic == null || mMinor1Characteristic == null ||mUuid1Characteristic == null) {		
					//logw("Service discovery BEACON1: " + status);
					broadcastError(ERROR_UNSUPPORTED_DEVICE);
					setState(STATE_DISCONNECTING);
					gatt.disconnect();
					return;
				}
			}
			
			if(ListService.contains(gatt.getService(BEACON2_SERVICE_UUID)))
			{
				final BluetoothGattService beacon2Service = gatt
						.getService(BEACON2_SERVICE_UUID);
				mMajor2Characteristic = beacon2Service
						.getCharacteristic(CONFIG2_MAJOR_CHARACTERISTIC_UUID);				
				mMinor2Characteristic = beacon2Service
						.getCharacteristic(CONFIG2_MINOR_CHARACTERISTIC_UUID);				
				mUuid2Characteristic = beacon2Service
						.getCharacteristic(CONFIG2_UUID_CHARACTERISTIC_UUID);							
						
				if (mMajor2Characteristic != null && mMinor2Characteristic != null && mUuid2Characteristic != null){
//					logw("BEACON2 Service discovery success: ");
					broadcastOperationCompleted();
				}
					
				if (mMajor2Characteristic == null || mMinor2Characteristic == null ||mUuid2Characteristic == null) {		
					//logw("Service discovery BEACON1: " + status);
					broadcastError(ERROR_UNSUPPORTED_DEVICE);
					setState(STATE_DISCONNECTING);
					gatt.disconnect();
					return;
				}
			}
			if(ListService.contains(gatt.getService(TPS_SERVICE_UUID)))
			{
				final BluetoothGattService tpsService = gatt
						.getService(TPS_SERVICE_UUID);
				mTransmitpowerCharacteristic = tpsService
						.getCharacteristic(TPS_TXPOWER_CHARACTERISTIC_UUID);				
				mAdvertisingintervalCharacteristic = tpsService
						.getCharacteristic(TPS_ADVERTISINGINTERVAL_CHARACTERISTIC_UUID);														
				
				if (mTransmitpowerCharacteristic != null && mAdvertisingintervalCharacteristic != null){
//					logw("TPS Service discovery success: ");
					broadcastOperationCompleted();
				}

				if (mTransmitpowerCharacteristic == null || mAdvertisingintervalCharacteristic == null) {
					// Config characteristics is not present
					broadcastError(ERROR_UNSUPPORTED_DEVICE);
					setState(STATE_DISCONNECTING);
					gatt.disconnect();
					return;
				}
			}						
		}

		@Override
		public void onCharacteristicWrite(final BluetoothGatt gatt,
				final BluetoothGattCharacteristic characteristic,
				final int status) {
			if (status != BluetoothGatt.GATT_SUCCESS) {
//				logw("Characteristic write error: " + status);
				broadcastError(status);
				return;
			}
			
			//修改密码
			if(ACCESS_CHGEPWD_CHARACTERISTIC_UUID.equals(characteristic.getUuid())){
//				logw("chgepass: " + characteristic.getValue()[0]+characteristic.getValue()[1]);
				
			}
			// 写入鉴权值之后开始读取各个参数，与之前的逻辑不同了
			if (ACCESS_PWD_CHARACTERISTIC_UUID.equals(characteristic
					.getUuid())) {
//				logw("pass: " + characteristic.getValue()[0]+characteristic.getValue()[1]);								
				if (mTransmitpowerCharacteristic != null) {
					mBluetoothGatt.readCharacteristic(mTransmitpowerCharacteristic);					
				}
			}
			if(CONFIG1_UUID_CHARACTERISTIC_UUID
					.equals(characteristic.getUuid())){
				 UUID uuid1 = decodeBeaconUUID(characteristic);
				 broadcastUuid1Write(uuid1);
			}
			else if (CONFIG1_MAJOR_CHARACTERISTIC_UUID.equals(characteristic
					.getUuid())) {
//				Log.d("CONFIG1_MAJOR_CHARACTERISTIC_UUID", "XXXXXXXXX");
				final int major1 = decodeUInt16(characteristic, 0);
				broadcastMajor1Write(major1);
//				Log.d("Debug", "XXXXXXXXX");
			} else if (CONFIG1_MINOR_CHARACTERISTIC_UUID.equals(characteristic
					.getUuid())) {
				final int minor1 = decodeUInt16(characteristic, 0);
				broadcastMinor1Write(minor1);
			}
			if(CONFIG2_UUID_CHARACTERISTIC_UUID
					.equals(characteristic.getUuid())){
				 UUID uuid2 = decodeBeaconUUID(characteristic);
				 broadcastUuid2Write(uuid2);
			}
			else if (CONFIG2_MAJOR_CHARACTERISTIC_UUID.equals(characteristic
					.getUuid())) {
//				Log.d("CONFIG2_MAJOR_CHARACTERISTIC_UUID", "XXXXXXXXX");
				final int major2 = decodeUInt16(characteristic, 0);
				broadcastMajor2Write(major2);
//				Log.d("Debug", "XXXXXXXXX");
			} else if (CONFIG2_MINOR_CHARACTERISTIC_UUID.equals(characteristic
					.getUuid())) {
				final int minor2 = decodeUInt16(characteristic, 0);
				broadcastMinor2Write(minor2);
			}
			else if (DIS_RSSI_CHARACTERISTIC_UUID.equals(characteristic
					.getUuid())) {
				final int rssi = characteristic.getIntValue(
						BluetoothGattCharacteristic.FORMAT_SINT8, 0);
//				logw("rssi write: " + rssi);
				broadcastRssiWrite(rssi);
			}else if (KEYSET_RESET_CHARACTERISTIC_UUID.equals(characteristic
					.getUuid())) {
				final int reset = characteristic.getIntValue(
						BluetoothGattCharacteristic.FORMAT_UINT8, 0);
//				logw("reset write: " + reset);
				broadcastResetWrite(reset);
			}else if (TIMER_SET_CHARACTERISTIC_UUID.equals(characteristic
					.getUuid())) {
				final int timer = characteristic.getIntValue(
						BluetoothGattCharacteristic.FORMAT_UINT8, 0);
//				logw("timer write: " + timer);
				broadcastTimerWrite(timer);
			} else if (TPS_ADVERTISINGINTERVAL_CHARACTERISTIC_UUID
					.equals(characteristic.getUuid())) {
				final int advertisinginterval = characteristic.getIntValue(
						BluetoothGattCharacteristic.FORMAT_UINT8, 0);
				broadcastAdvertisingintervalWrite(advertisinginterval);
			} else if (TPS_TXPOWER_CHARACTERISTIC_UUID
					.equals(characteristic.getUuid())) {
				final int Transmitpower = characteristic.getIntValue(
						BluetoothGattCharacteristic.FORMAT_SINT8, 0);
				int t=Transmitpower;
				if(Transmitpower==14)t=-4;if(Transmitpower==18)t=-8;
				if(Transmitpower==22)t=-12;if(Transmitpower==26)t=-16;
				if(Transmitpower==30)t=-20;
				logw("Transmitpower write: " + t);				
				broadcastTransmitpowerWrite(t);
				// // 所有数据读取完成后设置成功，这样返回的mcurrnentBeacon所有配置就被设置了				
			}			
		}

		@Override
		public void onCharacteristicRead(final BluetoothGatt gatt,
				final BluetoothGattCharacteristic characteristic,
				final int status) {
			if (status != BluetoothGatt.GATT_SUCCESS) {				
//				logw("Characteristic read error: " + status);
				broadcastError(status);
				return;
			}

//			if (CONFIG1_UUID_CHARACTERISTIC_UUID
//					.equals(characteristic.getUuid())) {
//				logw("uuid1 Characteristic read ok: ");				
//				final UUID uuid1 = decodeBeaconUUID(characteristic);
//				broadcastUuid1Read(uuid1);
//				if (mMajor1Characteristic.getValue() == null)
//					gatt.readCharacteristic(mMajor1Characteristic);				
//			} else if (CONFIG1_MAJOR_CHARACTERISTIC_UUID.equals(characteristic
//					.getUuid())) {
//				logw("Major1 Characteristic read ok: ");
//				final int major1 = decodeUInt16(characteristic, 0);
//				broadcastMajor1Read(major1);
//				if (mMinor1Characteristic.getValue() == null)
//					gatt.readCharacteristic(mMinor1Characteristic);
//			} else if (CONFIG1_MINOR_CHARACTERISTIC_UUID.equals(characteristic
//					.getUuid())) {
//				logw("Minor1 Characteristic read ok: ");
//				final int minor1 = decodeUInt16(characteristic, 0);
//				broadcastMinor1Read(minor1);
//				if (mUuid2Characteristic.getValue() == null)
//					gatt.readCharacteristic(mUuid2Characteristic);
//			} 
//			if (CONFIG2_UUID_CHARACTERISTIC_UUID
//					.equals(characteristic.getUuid())) {
//				logw("uuid2 Characteristic read ok: ");				
//				final UUID uuid2 = decodeBeaconUUID(characteristic);
//				broadcastUuid2Read(uuid2);
//				if (mMajor2Characteristic.getValue() == null)
//					gatt.readCharacteristic(mMajor2Characteristic);				
//			} else if (CONFIG2_MAJOR_CHARACTERISTIC_UUID.equals(characteristic
//					.getUuid())) {
//				logw("Major2 Characteristic read ok: ");
//				final int major2 = decodeUInt16(characteristic, 0);
//				broadcastMajor2Read(major2);
//				if (mMinor2Characteristic.getValue() == null)
//					gatt.readCharacteristic(mMinor2Characteristic);
//			} else if (CONFIG2_MINOR_CHARACTERISTIC_UUID.equals(characteristic
//					.getUuid())) {
//				logw("Minor2 Characteristic read ok: ");
//				final int minor2 = decodeUInt16(characteristic, 0);
//				broadcastMinor2Read(minor2);
//				if (mRssiCharacteristic.getValue() == null)
//					logw("RSSI Characteristic read start: ");
//					gatt.readCharacteristic(mRssiCharacteristic);
//			}
//			else if (DIS_RSSI_CHARACTERISTIC_UUID.equals(characteristic
//					.getUuid())) {			
//				final int rssi = characteristic.getIntValue(
//						BluetoothGattCharacteristic.FORMAT_SINT8, 0);
//				logw("RSSI Characteristic read ok: " + rssi);
//				broadcastRssiRead(rssi);
//				if (mAdvertisingintervalCharacteristic.getValue() == null) {
//					gatt.readCharacteristic(mAdvertisingintervalCharacteristic);
//				}
//			} else if (TPS_ADVERTISINGINTERVAL_CHARACTERISTIC_UUID
//					.equals(characteristic.getUuid())) {
//				logw("ADVERTISINGINTERVAL Characteristic read ok: ");
//				final int advertisinginterval = characteristic.getIntValue(
//						BluetoothGattCharacteristic.FORMAT_UINT8, 0);
//				logw("ADVERTISINGINTERVAL: " + advertisinginterval);
//				broadcastAdvertisingintervalRead(advertisinginterval);
//				if (mTimerCharacteristic.getValue() == null) {
//					gatt.readCharacteristic(mTimerCharacteristic);
//				}
//			} else if (TIMER_SET_CHARACTERISTIC_UUID
//					.equals(characteristic.getUuid())) {
//				logw("TIMER Characteristic read ok: ");
//				final int timer = characteristic.getIntValue(
//						BluetoothGattCharacteristic.FORMAT_SINT8, 0);
//				logw("TIMER: " + timer);
//				broadcastTimerRead(timer);
//				if (mKeysetCharacteristic.getValue() == null) {
//					gatt.readCharacteristic(mKeysetCharacteristic);
//				}
//			} else if (KEYSET_RESET_CHARACTERISTIC_UUID
//					.equals(characteristic.getUuid())) {
//				logw("KEYSET_RESET Characteristic read ok: ");
//				final int reset = characteristic.getIntValue(
//						BluetoothGattCharacteristic.FORMAT_UINT8, 0);
//				logw("KEYSET_RESET: " + reset);
//				broadcastResetRead(reset);
//				if (mTransmitpowerCharacteristic.getValue() == null) {
//					gatt.readCharacteristic(mTransmitpowerCharacteristic);
//				}
//			}else 
			if (TPS_TXPOWER_CHARACTERISTIC_UUID
					.equals(characteristic.getUuid())) {
				logw("TXPOWER Characteristic read ok: ");
				final int Transmitpower = characteristic.getIntValue(
						BluetoothGattCharacteristic.FORMAT_SINT8, 0);
				logw("TXPOWER: " + Transmitpower);
//				broadcastTransmitpowerRead(Transmitpower);
				// 所有数据读取完成后设置成功，这样返回的mcurrnentBeacon所有配置就被设置了
				setState(STATE_CONNECTED);
				if(mBluetoothGatt.setCharacteristicNotification(mTrackRssiCharacteristic, true)){
					logw("setCharacteristicNotification success!!!! ");
				}else
					logw("setCharacteristicNotification failed!!!! ");
				BluetoothGattDescriptor desc = mTrackRssiCharacteristic.getDescriptor(RSSI_DESCRIPTER_UUID);
				if(desc == null)
					logw("desc + failed!!!! ");
				boolean test;
				test = desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);  // return value = true
//				test = gatt.readDescriptor(desc);  // return value = true
				test = gatt.writeDescriptor(desc);  // return value = true
				
			}
		}
	};

	public class ServiceBinder extends Binder {
		/**
		 * Connects to the service. The bluetooth device must have been passed
		 * during binding to the service in {@link UpdateService#EXTRA_DATA}
		 * field.
		 * 
		 * @return <code>true</code> if connection process has been initiated
		 */
		public boolean connect() {
			if (mAdapter == null) {
//				logw("BluetoothAdapter not initialized or unspecified address.");
				return false;
			}

			if (mBluetoothDevice == null) {
//				logw("Target device not specified. Start service with the BluetoothDevice set in EXTRA_DATA field.");
				return false;
			}

			// the device may be already connected
			if (mConnectionState == STATE_CONNECTED) {
//				logw("已经连接，不能重复连接");
				return true;
			}

			setState(STATE_CONNECTING);
			mBluetoothGatt = mBluetoothDevice.connectGatt(UpdateService.this,
					false, mGattCallback);
//			logw("服务开始连接");
			return true;
		}

		/**
		 * Disconnects from the device and closes the Bluetooth GATT object
		 * afterwards.
		 */
		public void disconnectAndClose() {
			// This sometimes happen when called from
			// UpdateService.ACTION_GATT_ERROR event receiver in UpdateFragment.
			if (mBluetoothGatt == null)
				return;

			setState(STATE_DISCONNECTING);
			mBluetoothGatt.disconnect();

			// Sometimes the connection gets error 129 or 133. Calling
			// disconnect() method does not really disconnect... sometimes the
			// connection is already broken.
			// Here we have a security check that notifies UI about
			// disconnection even if onConnectionStateChange(...) has not been
			// called.
			// 1500后自动回调断开信息
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mConnectionState == STATE_DISCONNECTING)
						mGattCallback.onConnectionStateChange(mBluetoothGatt,
								BluetoothGatt.GATT_SUCCESS,
								BluetoothProfile.STATE_DISCONNECTED);
				}
			}, 1500);

		}

		/**
		 * Reads all the values from the device, one by one.
		 * 
		 * @return <code>true</code> if at least one required characteristic has
		 *         been found on the beacon.
		 */
		public boolean read() {
			if (mBluetoothGatt == null)
				return false;

			if (mUuid1Characteristic != null) {
				mBluetoothGatt.readCharacteristic(mUuid1Characteristic);
				return true;
			} else if (mMajor1Characteristic != null) {
				mBluetoothGatt.readCharacteristic(mMajor1Characteristic);
				return true;
			} else if (mMinor1Characteristic != null) {
				mBluetoothGatt.readCharacteristic(mMinor1Characteristic);
				return true;
			} else if (mRssiCharacteristic != null) {
				mBluetoothGatt.readCharacteristic(mRssiCharacteristic);
				return true;
			} else if (mAdvertisingintervalCharacteristic != null) {
				mBluetoothGatt
						.readCharacteristic(mAdvertisingintervalCharacteristic);
				return true;
			} else if (mTransmitpowerCharacteristic != null) {
				mBluetoothGatt.readCharacteristic(mTransmitpowerCharacteristic);
				return true;
			}
			return false;
		}

		/**
		 * 发送鉴权码
		 */
		public void sendConnectAuthenticate() {
			if (mBluetoothGatt == null)
				return;

//			Log.w("PublicData.getInstance().passwd:", " "+PublicData.getInstance().passwd[0]+ " "+
//			PublicData.getInstance().passwd[1]+" "+PublicData.getInstance().passwd[2]
//					+PublicData.getInstance().passwd[3]+PublicData.getInstance().passwd[4]
//							+PublicData.getInstance().passwd[5]);
			/**
			 * 连接成功后，发送密码			 			
			*/			
			mPwdCharacteristic.setValue(BeaconConnection.PASSWORD);
			logw("DEBUG" + BeaconConnection.PASSWORD[0] +BeaconConnection.PASSWORD[1]);
			mBluetoothGatt.writeCharacteristic(mPwdCharacteristic);								
		}
		
		//发送更改之后的密码
		public boolean sendChangePwd() {
			if (mBluetoothGatt == null)
				return false;

		//	Log.w("PublicData.getInstance().chagepwd:", " "+PublicData.getInstance().chagepwd[0]+ " "+PublicData.getInstance().chagepwd[1]);
			/**
			 * 连接成功后，发送密码			 			
			*/
			mChgepwdCharacteristic.setValue(BeaconConnection.CHGEPWD);
//			Log.w("CHGEPWD:", " "+BeaconConnection.CHGEPWD[0]+ " "+
//					BeaconConnection.CHGEPWD[1]+" "+BeaconConnection.CHGEPWD[2]
//							+BeaconConnection.CHGEPWD[3]+BeaconConnection.CHGEPWD[4]
//									+BeaconConnection.CHGEPWD[5]);
			if(mBluetoothGatt.writeCharacteristic(mChgepwdCharacteristic)){
				Log.w("sendChangePwd"," success");
				return true;
			}
			
			else return false;		
		}

		/**
		 * Overwrites the beacon service UUID1.
		 * 
		 * @param uuid
		 *            the new UUID1
		 * @return <code>true</code> if altering UUID1 is supported (required
		 *         characteristic exists)
		 */
		public boolean setBeaconUuid1(final UUID uuid) {
			if (mUuid1Characteristic == null || uuid == null)
				return false;

			final byte[] data = new byte[16];
			for (int i = 0; i < 8; ++i)
				data[i] = (byte) ((uuid.getMostSignificantBits() >>> (56 - i * 8)) & 0xFF);
			for (int i = 8; i < 16; ++i)
				data[i] = (byte) ((uuid.getLeastSignificantBits() >>> (56 - i * 8)) & 0xFF);
			mUuid1Characteristic.setValue(data);
			if(mBluetoothGatt != null)
				mBluetoothGatt.writeCharacteristic(mUuid1Characteristic);
			else 
				return false;			
			return true;
		}
		/**
		 * Overwrites the beacon service UUID2.
		 * 
		 * @param uuid
		 *            the new UUID2
		 * @return <code>true</code> if altering UUID2 is supported (required
		 *         characteristic exists)
		 */
		public boolean setBeaconUuid2(final UUID uuid) {
			if (mUuid1Characteristic == null || uuid == null)
				return false;

			final byte[] data = new byte[16];
			for (int i = 0; i < 8; ++i)
				data[i] = (byte) ((uuid.getMostSignificantBits() >>> (56 - i * 8)) & 0xFF);
			for (int i = 8; i < 16; ++i)
				data[i] = (byte) ((uuid.getLeastSignificantBits() >>> (56 - i * 8)) & 0xFF);
			mUuid2Characteristic.setValue(data);
			if(mBluetoothGatt != null)
				mBluetoothGatt.writeCharacteristic(mUuid2Characteristic);
			else 
				return false;			
			return true;
		}

		/**
		 * Returns the current UUID value. This reads the value from the local
		 * cache. The {@link #read()} method must be invoked before to read the
		 * current value from the device.
		 * 
		 * @return the beacon service UUID
		 */
		public UUID getBeaconUuid1() {
			final BluetoothGattCharacteristic characteristic = mUuid1Characteristic;
			if (characteristic != null) {
				final byte[] data = characteristic.getValue();
				if (data == null || data.length != 16)
					return null;
				return decodeBeaconUUID(characteristic);
			}
			return null;
		}
		
		public UUID getBeaconUuid2() {
			final BluetoothGattCharacteristic characteristic = mUuid2Characteristic;
			if (characteristic != null) {
				final byte[] data = characteristic.getValue();
				if (data == null || data.length != 16)
					return null;
				return decodeBeaconUUID(characteristic);
			}
			return null;
		}

		/**
		 * Overwrites the beacon major and minor numbers.
		 * 
		 * @param major
		 *            the major number (0-65535)
		 * @param minor
		 *            the minor number (0-65535)
		 * @return <code>true</code> if altering major and minor is supported
		 *         (required characteristic exists)
		 */
		public boolean setMajor1(final int major) {
			if (mMajor1Characteristic == null)
				return false;

			if (major < 0 || major > 0xFFFF)
				return false;

			final int majorInverted = (major & 0xFF) << 8
					| ((major >> 8) & 0xFF);
			mMajor1Characteristic.setValue(majorInverted,
					BluetoothGattCharacteristic.FORMAT_UINT16, 0);
			if(mBluetoothGatt != null)
				mBluetoothGatt.writeCharacteristic(mMajor1Characteristic);
			else 
				return false;
			return true;
		}
		
		public boolean setMajor2(final int major) {
			if (mMajor2Characteristic == null)
				return false;

			if (major < 0 || major > 0xFFFF)
				return false;

			final int majorInverted = (major & 0xFF) << 8
					| ((major >> 8) & 0xFF);
			mMajor2Characteristic.setValue(majorInverted,
					BluetoothGattCharacteristic.FORMAT_UINT16, 0);
			if(mBluetoothGatt != null)
				mBluetoothGatt.writeCharacteristic(mMajor2Characteristic);
			else 
				return false;
			return true;
		}

		/**
		 * Overwrites the beacon major and minor numbers.
		 * 
		 * @param major
		 *            the major number (0-65535)
		 * @param minor
		 *            the minor number (0-65535)
		 * @return <code>true</code> if altering major and minor is supported
		 *         (required characteristic exists)
		 */
		public boolean setMinor1(final int minor) {
			if (mMinor1Characteristic == null)
				return false;
			if (minor < 0 || minor > 0xFFFF)
				return false;
			final int minorInverted = (minor & 0xFF) << 8
					| ((minor >> 8) & 0xFF);
			mMinor1Characteristic.setValue(minorInverted,
					BluetoothGattCharacteristic.FORMAT_UINT16, 0);
			if(mBluetoothGatt != null)
				mBluetoothGatt.writeCharacteristic(mMinor1Characteristic);
			else 
				return false;
			return true;
		}
		
		public boolean setMinor2(final int minor) {
			if (mMinor2Characteristic == null)
				return false;
			if (minor < 0 || minor > 0xFFFF)
				return false;
			final int minorInverted = (minor & 0xFF) << 8
					| ((minor >> 8) & 0xFF);
			mMinor2Characteristic.setValue(minorInverted,
					BluetoothGattCharacteristic.FORMAT_UINT16, 0);
			if(mBluetoothGatt != null)
				mBluetoothGatt.writeCharacteristic(mMinor2Characteristic);
			else 
				return false;
			return true;
		}

		/**
		 * 发送密码
		 * 
		 * @param passWord
		 * @return
		 */
		public boolean sendPassword(String passWord) {
			if (mPwdCharacteristic == null) {
				return false;
			}
			mPwdCharacteristic.setValue(passWord);
			if(mBluetoothGatt != null)
				mBluetoothGatt.writeCharacteristic(mPwdCharacteristic);
			return true;
		}

		/**
		 * 修改密码
		 * 
		 * @param passWord
		 * @return
		 */
		public boolean setPassword(String passWord) {
			// TODO Auto-generated method stub
			if (mPwdCharacteristic == null) {
				return false;
			}
			mPwdCharacteristic.setValue(passWord);
			if(mBluetoothGatt != null)
				mBluetoothGatt.writeCharacteristic(mPwdCharacteristic);
			return true;
		}

		/**
		 * Returns the pair of current Major and Minor values. This reads the
		 * value from the local cache. The {@link #read()} method must be
		 * invoked before to read the current value from the device.
		 * 
		 * @return the pair where the first value is the major number and the
		 *         second is the minor value
		 */
		public int getMajor1() {
			final BluetoothGattCharacteristic characteristic = mMajor1Characteristic;
			if (characteristic != null) {
				final byte[] data = characteristic.getValue();
				if (data == null || data.length != 2)
					return -1;
				final int major = decodeUInt16(characteristic, 0);
				return major;
			}
			return -1;
		}
		public int getMajor2() {
			final BluetoothGattCharacteristic characteristic = mMajor2Characteristic;
			if (characteristic != null) {
				final byte[] data = characteristic.getValue();
				if (data == null || data.length != 2)
					return -1;
				final int major = decodeUInt16(characteristic, 0);
				return major;
			}
			return -1;
		}

		/**
		 * Returns the pair of current Major and Minor values. This reads the
		 * value from the local cache. The {@link #read()} method must be
		 * invoked before to read the current value from the device.
		 * 
		 * @return the pair where the first value is the major number and the
		 *         second is the minor value
		 */
		public int getMinor1() {
			final BluetoothGattCharacteristic characteristic = mMinor1Characteristic;
			if (characteristic != null) {
				final byte[] data = characteristic.getValue();
				if (data == null || data.length != 2)
					return -1;
				final int minor = decodeUInt16(characteristic, 0);
				return minor;
			}
			return -1;
		}
		public int getMinor2() {
			final BluetoothGattCharacteristic characteristic = mMinor2Characteristic;
			if (characteristic != null) {
				final byte[] data = characteristic.getValue();
				if (data == null || data.length != 2)
					return -1;
				final int minor = decodeUInt16(characteristic, 0);
				return minor;
			}
			return -1;
		}

		/**
		 * Overwrites the beacon calibration RSSI value.
		 * 
		 * @param rssi
		 *            the RSSI value calculated at 1m distance from the beacon
		 *            using iPhone 5S.
		 * @return <code>true</code> if altering major and minor is supported
		 *         (required characteristic exists)
		 */
		public boolean setCalibratedRssi(final int rssi) {
			if (mRssiCharacteristic == null)
				return false;
			mRssiCharacteristic.setValue(rssi,
					BluetoothGattCharacteristic.FORMAT_SINT8, 0);
			if(mBluetoothGatt != null)
			mBluetoothGatt.writeCharacteristic(mRssiCharacteristic);
			return true;
		}
		
		/**
		 * Overwrites the beacon Reset value.
		 * 
		 * @param reset
		 *            the reset value can be 0 or 1, if it is 1 the beacon will be
		 *            in the low energy state
		 * @return <code>true</code> if altering reset is supported
		 *         (required characteristic exists)
		 */
		public boolean setReset(final int reset) {
			if (mKeysetCharacteristic == null)
				return false;
			mKeysetCharacteristic.setValue(reset,
					BluetoothGattCharacteristic.FORMAT_UINT8, 0);
			if(mBluetoothGatt != null)
			mBluetoothGatt.writeCharacteristic(mKeysetCharacteristic);
			return true;
		}
		
		/**
		 * Overwrites the beacon Timer value.
		 * 
		 * @param timer
		 *            timer can control the 2 different advertising packet,and the 
		 *            advertising interval may be not important  
		 * @return <code>true</code> if altering timer is supported
		 *         (required characteristic exists)
		 */
		public boolean setTimer(final int timer) {
			if (mTimerCharacteristic == null)
				return false;
			mTimerCharacteristic.setValue(timer,
					BluetoothGattCharacteristic.FORMAT_UINT8, 0);
			if(mBluetoothGatt != null)
			mBluetoothGatt.writeCharacteristic(mTimerCharacteristic);
			return true;
		}

		/**
		 * 重写beacon的发射频率
		 * 
		 * @param advertisingInterval
		 * @return
		 */
		public boolean setAdvertisingInterval(final int advertisingInterval) {
			if (mAdvertisingintervalCharacteristic == null)
				return false;

			mAdvertisingintervalCharacteristic.setValue(advertisingInterval,
					BluetoothGattCharacteristic.FORMAT_UINT8, 0);
			if(mBluetoothGatt != null)
			mBluetoothGatt
					.writeCharacteristic(mAdvertisingintervalCharacteristic);
			return true;
		}

		/**
		 * 重写beacon的发射功率
		 * 
		 * @param transmitPower
		 * @return
		 */
		public boolean setTransmitPower(final int transmitPower) {
			if (mTransmitpowerCharacteristic == null)
				return false;
			int t = transmitPower;
			if(transmitPower == -4)t=14;
			if(transmitPower == -8)t=18;
			if(transmitPower == -12)t=22;
			if(transmitPower == -16)t=26;
			if(transmitPower == -20)t=30;
//			Log.w("setTransmitPower"," "+transmitPower);
			mTransmitpowerCharacteristic.setValue(t,
					BluetoothGattCharacteristic.FORMAT_UINT8, 0);
			if(mBluetoothGatt != null)
			mBluetoothGatt.writeCharacteristic(mTransmitpowerCharacteristic);
			return true;
		}

		/**
		 * Obtains the cached value of the AdvertisingInterval characteristic.
		 * If the value has not been obtained yet using {@link #read()}, or the
		 * characteristic has not been found on the beacon, <code>null</code> is
		 * returned.
		 * 
		 * @return the RSSI value or <code>null</code>
		 */
		public Integer getAdvertisingInterval() {
			final BluetoothGattCharacteristic characteristic = mAdvertisingintervalCharacteristic;
			if (characteristic != null) {
				final byte[] data = characteristic.getValue();
				if (data == null || data.length < 1)
					return null;
				return characteristic.getIntValue(
						BluetoothGattCharacteristic.FORMAT_UINT8, 0);
			}
			return null;
		}

		/**
		 * Obtains the cached value of the AdvertisingInterval characteristic.
		 * If the value has not been obtained yet using {@link #read()}, or the
		 * characteristic has not been found on the beacon, <code>null</code> is
		 * returned.
		 * 
		 * @return the RSSI value or <code>null</code>
		 */
		public Integer getTransmitPower() {
			final BluetoothGattCharacteristic characteristic = mTransmitpowerCharacteristic;
			if (characteristic != null) {
				final byte[] data = characteristic.getValue();
				if (data == null || data.length < 1)
					return null;
				return characteristic.getIntValue(
						BluetoothGattCharacteristic.FORMAT_SINT8, 0);
			}
			return null;
		}

		/**
		 * Obtains the cached value of the RSSI characteristic. If the value has
		 * not been obtained yet using {@link #read()}, or the characteristic
		 * has not been found on the beacon, <code>null</code> is returned.
		 * 
		 * @return the RSSI value or <code>null</code>
		 */
		public Integer getCalibratedRssi() {
			final BluetoothGattCharacteristic characteristic = mRssiCharacteristic;
			if (characteristic != null) {
				final byte[] data = characteristic.getValue();
				if (data == null || data.length < 1)
					return null;
				return characteristic.getIntValue(
						BluetoothGattCharacteristic.FORMAT_SINT8, 0);
			}
			return null;
		}

		public int getState() {
			return mConnectionState;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
//		Log.i("Debug", "服务oncreate");
		initialize();
		mHandler = new Handler();
		mConnectionState = STATE_DISCONNECTED;
		//连接蓝牙是有时会连不上，则不会有状态回到，则发起连接
		//12秒后设置为超时，通知重新连接
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mConnectionState == STATE_DISCONNECTED||mConnectionState == STATE_CONNECTING){
					outOfTime(1);
				}
			}
		}, 12000);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mBluetoothGatt != null)
			mBluetoothGatt.disconnect();
		mHandler = null;
		mBluetoothDevice = null;
		timer.cancel();
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return new ServiceBinder();
	}

	@Override
	public boolean onUnbind(final Intent intent) {
		// We want to allow rebinding
		return true;
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags,
			final int startId) {
		mBluetoothDevice = intent.getParcelableExtra(EXTRA_DATA);
		return START_NOT_STICKY;
	}
	

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 */
	private void initialize() {
		errorSign = false;
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
		mAdapter = bluetoothManager.getAdapter();
	}

	private void setState(final int state) {
		mConnectionState = state;
		final Intent intent = new Intent(ACTION_STATE_CHANGED);
		intent.putExtra(EXTRA_DATA, state);
//		Log.w(TAG, "广播连接" + state);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastRssiUpdate() {
		Log.w("broadcastRssiUpdate", " ");
		final Intent intent = new Intent(ACTION_RSSI_UPDATE_READY);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	private void broadcastUuid1Write(final UUID uuid) {
		final Intent intent = new Intent(ACTION_UUID1_WRITE_READY);
		intent.putExtra(EXTRA_DATA1, new ParcelUuid(uuid));
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastMajor1Write(final int major) {
		final Intent intent = new Intent(ACTION_MAJOR1_WRITE_READY);
		intent.putExtra(EXTRA_MAJOR1, major);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastMinor1Write(final int minor) {
		final Intent intent = new Intent(ACTION_MINOR1_WRITE_READY);
		intent.putExtra(EXTRA_MINOR1, minor);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	
	private void broadcastUuid2Write(final UUID uuid) {
		final Intent intent = new Intent(ACTION_UUID2_WRITE_READY);
		intent.putExtra(EXTRA_DATA2, new ParcelUuid(uuid));
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastMajor2Write(final int major) {
		final Intent intent = new Intent(ACTION_MAJOR2_WRITE_READY);
		intent.putExtra(EXTRA_MAJOR2, major);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastMinor2Write(final int minor) {
		final Intent intent = new Intent(ACTION_MINOR2_WRITE_READY);
		intent.putExtra(EXTRA_MINOR2, minor);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastRssiWrite(final int rssi) {
		final Intent intent = new Intent(ACTION_RSSI_WRITE_READY);
		intent.putExtra(EXTRA_DATA, rssi);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	
	private void broadcastResetWrite(final int reset) {
		final Intent intent = new Intent(ACTION_RESET_WRITE_READY);
		intent.putExtra(EXTRA_RESET, reset);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	
	private void broadcastTimerWrite(final int timer) {
		final Intent intent = new Intent(ACTION_TIMER_WRITE_READY);
		intent.putExtra(EXTRA_TIMER, timer);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastAdvertisingintervalWrite(final int advertisinginterval) {
		final Intent intent = new Intent(ACTION_ADVERTISINGINTERVAL_WRITE_READY);
		intent.putExtra(EXTRA_ADVERTISINGINTERVAL, advertisinginterval);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastTransmitpowerWrite(final int transmitpower) {
		final Intent intent = new Intent(ACTION_TRANSMITPOWER_WRITE_READY);
		intent.putExtra(EXTRA_TRANSMITPOWER, transmitpower);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastUuid1Read(final UUID uuid) {
//		logw("broadcastUuid1Read: " + uuid.toString());
		final Intent intent = new Intent(ACTION_UUID1_READ_READY);
		intent.putExtra(EXTRA_DATA1, new ParcelUuid(uuid));
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastMajor1Read(final int major) {
		final Intent intent = new Intent(ACTION_MAJOR1_READ_READY);
		intent.putExtra(EXTRA_MAJOR1, major);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastMinor1Read(final int minor) {
		final Intent intent = new Intent(ACTION_MINOR1_READ_READY);
		intent.putExtra(EXTRA_MINOR1, minor);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	
	private void broadcastUuid2Read(final UUID uuid) {
		final Intent intent = new Intent(ACTION_UUID2_READ_READY);
		intent.putExtra(EXTRA_DATA2, new ParcelUuid(uuid));
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastMajor2Read(final int major) {
		final Intent intent = new Intent(ACTION_MAJOR2_READ_READY);
		intent.putExtra(EXTRA_MAJOR2, major);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastMinor2Read(final int minor) {
		final Intent intent = new Intent(ACTION_MINOR2_READ_READY);
		intent.putExtra(EXTRA_MINOR2, minor);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastRssiRead(final int rssi) {
		final Intent intent = new Intent(ACTION_RSSI_READ_READY);
		intent.putExtra(EXTRA_DATA, rssi);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastAdvertisingintervalRead(final int advertisinginterval) {
		final Intent intent = new Intent(ACTION_ADVERTISINGINTERVAL_READ_READY);
		intent.putExtra(EXTRA_ADVERTISINGINTERVAL, advertisinginterval);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastTransmitpowerRead(final int transmitpower) {
		final Intent intent = new Intent(ACTION_TRANSMITPOWER_READ_READY);
		intent.putExtra(EXTRA_TRANSMITPOWER, transmitpower);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	
	private void broadcastTimerRead(final int timer) {
//		Log.w("broadcastTimerRead","timer:" + timer);
		final Intent intent = new Intent(ACTION_TIMER_READ_READY);
		intent.putExtra(EXTRA_TIMER, timer);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastResetRead(final int reset) {
		final Intent intent = new Intent(ACTION_RESET_READ_READY);
		intent.putExtra(EXTRA_RESET, reset);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastSoftReboot() {
		final Intent intent = new Intent(ACTION_SOFTREBOOT_READY);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastOperationCompleted() {
		final Intent intent = new Intent(ACTION_DONE);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void outOfTime(final int error){
//		Log.w(TAG, "超时重连");
		final Intent intent = new Intent(ACTION_GATT_ERROR);
		intent.putExtra(EXTRA_DATA, error);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	private void broadcastError(final int error) {
		//如果已经广播过一次错误，则已经开始重连，不再广播第二次，同时也取消超时重连
		if (!this.errorSign){
//			Log.w("Debug", "有效错误广播");
			this.errorSign = true;
			timer.cancel();
		}
		else {
//			Log.w("Debug", "无效错误广播");
			return;
		}
		final Intent intent = new Intent(ACTION_GATT_ERROR);
		intent.putExtra(EXTRA_DATA, error);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		
	}

	/**
	 * Clears the device cache.
	 * <p>
	 * CAUTION:<br />
	 * It is very unsafe to call the refresh() method. First of all it's hidden
	 * so it may be removed in the future release of Android. We do it because
	 * Nordic Beacon may advertise as a beacon, as Beacon Config or DFU. Android
	 * does not clear cache then device is disconnected unless manually
	 * restarted Bluetooth Adapter. To do this in the code we need to call
	 * {@link BluetoothGatt#refresh()} method. However is may cause a lot of
	 * troubles. Ideally it should be called before connection attempt but we
	 * get 'gatt' object by calling connectGatt method so when the connection
	 * already has been started. Calling refresh() afterwards causes errors 129
	 * and 133 to pop up from time to time when refresh takes place actually
	 * during service discovery. It seems to be asynchronous method. Therefore
	 * we are refreshing the device after disconnecting from it, before closing
	 * gatt. Sometimes you may obtain services from cache, not the actual values
	 * so reconnection is required.
	 * 
	 * @param gatt
	 *            the Bluetooth GATT object to refresh.
	 */
	private boolean refreshDeviceCache(final BluetoothGatt gatt) {
		/*
		 * There is a refresh() method in BluetoothGatt class but for now it's
		 * hidden. We will call it using reflections.
		 */
		try {
			final Method refresh = gatt.getClass().getMethod("refresh");
			if (refresh != null) {
				return (Boolean) refresh.invoke(gatt);
			}
		} catch (final Exception e) {
			loge("An exception occured while refreshing device");
		}
		return false;
	}

	private void loge(final String message) {
		if (BuildConfig.DEBUG)
		Log.e(TAG, message);
	}

	private void logw(final String message) {
		if (BuildConfig.DEBUG)
			Log.w(TAG, message);
	}

	public static int decodeUInt16(
			final BluetoothGattCharacteristic characteristic, final int offset) {
		final byte[] data = characteristic.getValue();
		return (unsignedByteToInt(data[offset]) << 8)
				| unsignedByteToInt(data[offset + 1]);
	}
//static  
	public UUID decodeBeaconUUID(
			final BluetoothGattCharacteristic characteristic) {
		final byte[] data = characteristic.getValue();
//		logw("Characteristic get value: " + data.length);				
		final long mostSigBits = (unsignedByteToLong(data[0]) << 56)
				+ (unsignedByteToLong(data[1]) << 48)
				+ (unsignedByteToLong(data[2]) << 40)
				+ (unsignedByteToLong(data[3]) << 32)
				+ (unsignedByteToLong(data[4]) << 24)
				+ (unsignedByteToLong(data[5]) << 16)
				+ (unsignedByteToLong(data[6]) << 8)
				+ unsignedByteToLong(data[7]);
//		logw("mostSigBits value: " + mostSigBits);
		final long leastSigBits = (unsignedByteToLong(data[8]) << 56)
				+ (unsignedByteToLong(data[9]) << 48)
				+ (unsignedByteToLong(data[10]) << 40)
				+ (unsignedByteToLong(data[11]) << 32)
				+ (unsignedByteToLong(data[12]) << 24)
				+ (unsignedByteToLong(data[13]) << 16)
				+ (unsignedByteToLong(data[14]) << 8)
				+ unsignedByteToLong(data[15]);
//		logw("leastSigBits value: " + leastSigBits);
		
		UUID uuid = new UUID(mostSigBits, leastSigBits); 
//		logw("UUID: " + uuid.toString());		
		return uuid;		
	}

	/**
	 * Convert a signed byte to an unsigned long.
	 */
	public static long unsignedByteToLong(byte b) {
		return b & 0xFF;
	}

	/**
	 * Convert a signed byte to an unsigned int.
	 */
	public static int unsignedByteToInt(int b) {
		return b & 0xFF;
	}
}
