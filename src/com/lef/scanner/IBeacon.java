/**
 * Radius Networks, Inc.
 * http://www.radiusnetworks.com
 * 
 * @author David G. Young
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.lef.scanner;

import java.util.Date;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.lef.client.IBeaconDataFactory;
import com.lef.client.NullIBeaconDataFactory;
import com.lef.ibeacon.IBeaconDataNotifier;
import com.lef.ibeacon.service.ScannerServiceParser;

/**
* The <code>IBeacon</code> class represents a single hardware iBeacon detected by 
* an Android device.
* 
* <pre>An iBeacon is identified by a three part identifier based on the fields
* proximityUUID - a string UUID typically identifying the owner of a
*                 number of ibeacons
* major - a 16 bit integer indicating a group of iBeacons
* minor - a 16 bit integer identifying a single iBeacon</pre>
*
* An iBeacon sends a Bluetooth Low Energy (BLE) advertisement that contains these
* three identifiers, along with the calibrated tx power (in RSSI) of the 
* iBeacon's Bluetooth transmitter.  
* 
* This class may only be instantiated from a BLE packet, and an RSSI measurement for
* the packet.  The class parses out the three part identifier, along with the calibrated
* tx power.  It then uses the measured RSSI and calibrated tx power to do a rough
* distance measurement (the accuracy field) and group it into a more reliable buckets of 
* distance (the proximity field.)
* 
* @see     Region#matchesIBeacon(IBeacon iBeacon)
*/
public class IBeacon { 
//	/**
//	 * Less than half a meter away
//	 */
//	public static final int PROXIMITY_IMMEDIATE = 1;
//	/**
//	 * More than half a meter away, but less than four meters away
//	 */
//	public static final int PROXIMITY_NEAR = 2;
//	/**
//	 * More than four meters away
//	 */
//	public static final int PROXIMITY_FAR = 3;
//	/**
//	 * No distance estimate was possible due to a bad RSSI value or measured TX power
//	 */
//	public static final int PROXIMITY_UNKNOWN = 0;

    final private static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
	private static final String TAG = "IBeacon";	
	protected String name;
		
    /**
     * A 16 byte UUID that typically represents the company owning a number of iBeacons
     * Example: E2C56DB5-DFFB-48D2-B060-D0F5A71096E0 
     */
	protected String proximityUuid1;
	protected String proximityUuid2;
	/**
	 * A 16 bit integer typically used to represent a group of iBeacons
	 */
	protected int major1;
	protected int major2;
	/**
	 * A 16 bit integer that identifies a specific iBeacon within a group 
	 */
	protected int minor1;
	protected int minor2;
	/**
	 * An integer with four possible values representing a general idea of how far the iBeacon is away
	 * @see #PROXIMITY_IMMEDIATE
	 * @see #PROXIMITY_NEAR
	 * @see #PROXIMITY_FAR
	 * @see #PROXIMITY_UNKNOWN
	 */
	protected Integer proximity;
	/**
	 * A double that is an estimate of how far the iBeacon is away in meters.  This name is confusing, but is copied from
	 * the iOS7 SDK terminology.   Note that this number fluctuates quite a bit with RSSI, so despite the name, it is not
	 * super accurate.   It is recommended to instead use the proximity field, or your own bucketization of this value. 
	 */
	protected Double accuracy;
	/**
	 * The measured signal strength of the Bluetooth packet that led do this iBeacon detection.
	 */
	protected int rssi;
	/**
	 * The calibrated measured Tx power of the iBeacon in RSSI
	 * This value is baked into an iBeacon when it is manufactured, and
	 * it is transmitted with each packet to aid in the distance estimate
	 */
	protected int txPower;
	
	//reset：记录一键休眠的状态
	//0:正常状态   1:休眠状态
	protected int reset;
	
	//timer:广播切换的定时器
	//Beacon固件默认设为 500ms
	protected int timer;
	/**
	 * beacon����ʱ��
	 */
	protected long updateTime;
	/**
	 * ����Ƶ��
	 */
	protected int advertisingInterval;
	/**
	 * ���书��
	 */
	protected int transmitPower;
	/**
	 * beacon �Ļ������ã��������书�ʺ�Ƶ��
	 */
	protected BaseSettings baseSettings;
	
	/**
	 * canBeConnected
	 */
	protected boolean canBeConnected;
    /**
     * The bluetooth mac address
     */
    protected String bluetoothAddress;
    /**
     * The bluetoothDevice
     */
	protected BluetoothDevice bluetoothDevice;
	/**
	 * If multiple RSSI samples were available, this is the running average
	 */
	protected Double runningAverageRssi = null;
	
	/**
	 * Used to attach data to individual iBeacons, either locally or in the cloud
	 */
	protected static IBeaconDataFactory iBeaconDataFactory = new NullIBeaconDataFactory();
	
	/**
	 * @see #accuracy
	 * @return accuracy
	 */
	
	public double getAccuracy() {
		if (accuracy == null) {
			accuracy = calculateAccuracy(txPower, runningAverageRssi != null ? runningAverageRssi : rssi );		
		}
		return accuracy;
	}
	/**
	 * @see #major
	 * @return major
	 */
	public int getMajor1() {
		return major1;
	}
	public int getMajor2() {
		return major2;
	}
	/**
	 * @see #minor
	 * @return minor
	 */
	public int getMinor1() {
		return minor1;
	}
	public int getMinor2() {
		return minor2;
	}
//	/**
//	 * @see #proximity
//	 * @return proximity
//	 */
//	public int getProximity() {
//		if (proximity == null) {
//			proximity = calculateProximity(getAccuracy());		
//		}
//		return proximity;		
//	}
	protected void setProximityUuid1(String proximityUuid) {		
		Log.w("setProximityUuid1", proximityUuid);
		this.proximityUuid1 = proximityUuid;
	}
	protected void setMajor1(int major) {
		this.major1 = major;
	}
	protected void setMinor1(int minor) {
		this.minor1 = minor;
	}
	protected void setProximityUuid2(String proximityUuid) {		
		Log.w("setProximityUuid2", proximityUuid);
		this.proximityUuid2 = proximityUuid;
	}
	protected void setMajor2(int major) {
		this.major2 = major;
	}
	protected void setMinor2(int minor) {
		this.minor2 = minor;
	}
	protected void setTxPower(int power){
		this.txPower = power;
	}
	protected void setReset(int reset){
		this.reset = reset;
	}
	protected void setTimer(int timer){
		this.timer = timer;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	protected void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	/**
	 * @see #rssi
	 * @return rssi
	 */
	public int getRssi() {
		return rssi;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @see #txPower
	 * @return txPowwer
	 */
	public int getTxPower() {
		return txPower;
	}

	/**
	 * @see #proximityUuid
	 * @return proximityUuid
	 */
	public String getProximityUuid1() {
		return proximityUuid1;
	}
	public String getProximityUuid2() {
		return proximityUuid2;
	}
    /**
     * @see #bluetoothAddress
     * @return bluetoothAddress
     */
    public String getBluetoothAddress() {
        return bluetoothAddress;
    }


	public BluetoothDevice getBluetoothDevice() {
		return bluetoothDevice;
	}
	public int getAdvertisingInterval() {
		return advertisingInterval;
	}
	protected int getTransmitPower() {
		return transmitPower;
	}
	public int getReset() {
		return reset;
	}
	public int getTimer() {
		return timer;
	}	
	public BaseSettings getBaseSettings() {
		return baseSettings;
	}
	protected void setBaseSettings(BaseSettings baseSettings) {
		this.baseSettings = baseSettings;
	}
	protected void setAdvertisingInterval(int advertisingInterval) {
		this.advertisingInterval = advertisingInterval;
		this.baseSettings.setaInterval(BaseSettings.getAdvertisingIntervalByInt(advertisingInterval));
	}
	protected void setTransmitPower(int transmitPower) {
		this.transmitPower = transmitPower;
		this.baseSettings.settPower(BaseSettings.getTransmitPowerByInt(transmitPower));
	}
	public boolean isCanBeConnected() {
		return canBeConnected;
	}
	@Override
	public int hashCode() {
		return minor1;
	}
	
	/**
	 * Two detected iBeacons are considered equal if they share the same three identifiers, regardless of their distance or RSSI.
	 */
	@Override
	public boolean equals(Object that) {
		if (!(that instanceof IBeacon)) {
			return false;
		}
		IBeacon thatIBeacon = (IBeacon) that;		
		return (thatIBeacon.bluetoothAddress.equals( this.bluetoothAddress));
	}

    /**
     * Construct an iBeacon from a Bluetooth LE packet collected by Android's Bluetooth APIs
     *
     * @param scanData The actual packet bytes
     * @param rssi The measured signal strength of the packet
     * @return An instance of an <code>IBeacon</code>
     */
    public static IBeacon fromScanData(byte[] scanData, int rssi) {
        return fromScanData(scanData, rssi, null,false);
    }

	/**
	 * SDK�Զ����ã��������������<br>
	 * Construct an iBeacon from a Bluetooth LE packet collected by Android's Bluetooth APIs,
     * including the raw bluetooth device info
	 * @param scanData The actual packet bytes
	 * @param rssi The measured signal strength of the packet
     * @param device The bluetooth device that was detected
	 * @return An instance of an <code>IBeacon</code>
	 */
    @TargetApi(5)
    //处理收到的广播包，非常重要！！！
	public static IBeacon fromScanData(byte[] scanData, int rssi, BluetoothDevice device ,boolean canBeConnected) {
		int startByte = 2;
		boolean patternFound = false;
		while (startByte <= 5) {
			if (((int)scanData[startByte+2] & 0xff) == 0x02 &&
				((int)scanData[startByte+3] & 0xff) == 0x15) {			
				// yes!  This is an iBeacon	
				patternFound = true;
				break;
			}
			else if (((int)scanData[startByte] & 0xff) == 0x2d &&
					((int)scanData[startByte+1] & 0xff) == 0x24 &&
					((int)scanData[startByte+2] & 0xff) == 0xbf &&
					((int)scanData[startByte+3] & 0xff) == 0x16) {
                if (IBeaconManager.debug) Log.d(TAG, "This is a proprietary Estimote beacon advertisement that does not meet the iBeacon standard.  Identifiers cannot be read.");
                IBeacon iBeacon = new IBeacon();
				iBeacon.major1 = 0;
				iBeacon.minor1 = 0;
				iBeacon.proximityUuid1 = "00000000-0000-0000-0000-000000000000";
				iBeacon.txPower = -55;
				return iBeacon;
			}
            else if (((int)scanData[startByte] & 0xff) == 0xad &&
                     ((int)scanData[startByte+1] & 0xff) == 0x77 &&
                     ((int)scanData[startByte+2] & 0xff) == 0x00 &&
                     ((int)scanData[startByte+3] & 0xff) == 0xc6) {
                    if (IBeaconManager.debug) Log.d(TAG, "This is a proprietary Gimbal beacon advertisement that does not meet the iBeacon standard.  Identifiers cannot be read.");
                    IBeacon iBeacon = new IBeacon();
                    iBeacon.major1 = 0;
                    iBeacon.minor1 = 0;
                    iBeacon.proximityUuid1 = "00000000-0000-0000-0000-000000000000";
                    iBeacon.txPower = -55;
                    return iBeacon;
            }
			startByte++;
		}
		if(canBeConnected){
			patternFound = true;
		}
		if (patternFound == false) {
			// This is not an iBeacon
			if (IBeaconManager.debug) Log.d(TAG, "This is not an iBeacon advertisment (no 0215 seen in bytes 4-7).  The bytes I see are: "+bytesToHex(scanData));
			return null;
		}
								
		final IBeacon iBeacon = new IBeacon();
		if (!canBeConnected) {
			iBeacon.major1 = (scanData[startByte + 20] & 0xff) * 0x100
					+ (scanData[startByte + 21] & 0xff);
			iBeacon.minor1 = (scanData[startByte + 22] & 0xff) * 0x100
					+ (scanData[startByte + 23] & 0xff);
			iBeacon.txPower = (int) scanData[startByte + 24]; // this one is signed
			
		}else{
			iBeacon.major1 = -1;
			iBeacon.minor1 = -1;
			iBeacon.txPower = -1; // this one is signed
		}
		iBeacon.rssi = rssi;
				
		// AirLocate:
		// 02 01 1a 1a ff 4c 00 02 15  # Apple's fixed iBeacon advertising prefix
		// e2 c5 6d b5 df fb 48 d2 b0 60 d0 f5 a7 10 96 e0 # iBeacon profile uuid
		// 00 00 # major 
		// 00 00 # minor 
		// c5 # The 2's complement of the calibrated Tx Power

		// Estimote:		
		// 02 01 1a 11 07 2d 24 bf 16 
		// 394b31ba3f486415ab376e5c0f09457374696d6f7465426561636f6e00000000000000000000000000000000000000000000000000

		byte[] proximityUuidBytes = new byte[16];
		System.arraycopy(scanData, startByte+4, proximityUuidBytes, 0, 16); 
		String hexString = bytesToHex(proximityUuidBytes);
		StringBuilder sb = new StringBuilder();
		sb.append(hexString.substring(0,8));
		sb.append("-");
		sb.append(hexString.substring(8,12));
		sb.append("-");
		sb.append(hexString.substring(12,16));
		sb.append("-");
		sb.append(hexString.substring(16,20));
		sb.append("-");
		sb.append(hexString.substring(20,32));
		iBeacon.proximityUuid1 = sb.toString();

        if (device != null) {
        	iBeacon.name = ScannerServiceParser
					.decodeDeviceName(scanData);
            iBeacon.bluetoothAddress = device.getAddress();
            iBeacon.bluetoothDevice = device;
            iBeacon.canBeConnected = canBeConnected;
            iBeacon.updateTime = new Date().getTime();
            iBeacon.baseSettings = new BaseSettings();
            iBeacon.advertisingInterval = BaseSettings.AdvertisingInterval.UNKNOWN.aValue;
            iBeacon.transmitPower = BaseSettings.AdvertisingInterval.UNKNOWN.aValue;
        }
		return iBeacon;
	}
	
	public void requestData(IBeaconDataNotifier notifier) {
		iBeaconDataFactory.requestIBeaconData(this, notifier);
	}

	protected IBeacon(IBeacon otherIBeacon) {
		this.major1 = otherIBeacon.major1;
		this.minor1 = otherIBeacon.minor1;
		this.accuracy = otherIBeacon.accuracy;
		this.proximity = otherIBeacon.proximity;
		this.rssi = otherIBeacon.rssi;
		this.proximityUuid1= otherIBeacon.proximityUuid1;
		this.txPower = otherIBeacon.txPower;
        this.bluetoothAddress = otherIBeacon.bluetoothAddress;
        this.name = otherIBeacon.name;
        this.bluetoothDevice = otherIBeacon.bluetoothDevice;
        this.canBeConnected = otherIBeacon.canBeConnected;
        this.updateTime = otherIBeacon.updateTime;
        this.baseSettings = otherIBeacon.baseSettings;
	}
	
	protected IBeacon() {
		
	}

	@SuppressLint("DefaultLocale")
	protected IBeacon(String proximityUuid, int major, int minor, int txPower, int rssi) {
		this.proximityUuid1 = proximityUuid.toLowerCase();
		this.major1 = major;
		this.minor1 = minor;
		this.rssi = rssi;
		this.txPower = txPower;
	}
	
	public IBeacon(String proximityUuid, int major, int minor) {
		this.proximityUuid1 = proximityUuid.toLowerCase();
		this.major1 = major;
		this.minor1 = minor;
		this.txPower = -59;
		this.rssi = 0;
	}

	protected static double calculateAccuracy(int txPower, double rssi) {
		if (rssi == 0) {
			return -1.0; // if we cannot determine accuracy, return -1.
		}
		
		if (IBeaconManager.debug) Log.d(TAG, "calculating accuracy based on rssi of "+rssi);


		double ratio = rssi*1.0/txPower;
		if (ratio < 1.0) {
			return Math.pow(ratio,10);
		}
		else {
			double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;	
			if (IBeaconManager.debug) Log.d(TAG, " avg rssi: "+rssi+" accuracy: "+accuracy);
			return accuracy;
		}
	}	
	
//	protected static int calculateProximity(double accuracy) {
//		if (accuracy < 0) {
//			return PROXIMITY_UNKNOWN;	 
//			// is this correct?  does proximity only show unknown when accuracy is negative?  I have seen cases where it returns unknown when
//			// accuracy is -1;
//		}
//		if (accuracy < 0.5 ) {
//			return IBeacon.PROXIMITY_IMMEDIATE;
//		}
//		// forums say 3.0 is the near/far threshold, but it looks to be based on experience that this is 4.0
//		if (accuracy <= 4.0) { 
//			return IBeacon.PROXIMITY_NEAR;
//		}
//		// if it is > 4.0 meters, call it far
//		return IBeacon.PROXIMITY_FAR;
//
//	}

	private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    } 
}
