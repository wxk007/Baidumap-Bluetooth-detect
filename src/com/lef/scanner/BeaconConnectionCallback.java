package com.lef.scanner;


/**
 *  An interface for an Android <code>Activity</code> 
 * that wants to interact with iBeacons.  
 * @author lief
 *
 */
public interface BeaconConnectionCallback {
	/**
	 * 返回连接状态
	 * 
	 * @param beacon
	 *            当前连接的beacon
	 * @param status
	 *            返回连接状态 {@link}UpdateService.STATE_CONNECTED 和 {@link}
	 *            UpdateService.STATE_DISCONNECTED
	 */
	public void onConnectedState(IBeacon beacon, int status);

	
//	/**
//	 * 返回已连接设备的uuid
//	 * 
//	 * @param proximityUuid
//	 */
//	public void onGetUUID(String proximityUuid);
//
//	/**
//	 * 返回已连接设备的major
//	 * 
//	 * @param major
//	 */
//	public void onGetMajor(int major);
//
//	/**
//	 * 返回已连接设备的minor
//	 * 
//	 * @param minor
//	 */
//	public void onGetMinor(int minor);
//
	/**
	 * 返回已连接设备的计算Rssi
	 * 
	 * @param rssi
	 */
	public void onGetCalRssi();
}
