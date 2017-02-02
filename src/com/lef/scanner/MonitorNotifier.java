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


/**
 * 回调接口，需要实现回调接口中的方法，触发进入，离开某个自定义的region{@link Region}
 * This interface is implemented by classes that receive iBeacon monitoring notifications
 * 
 * @see IBeaconManager#setMonitorNotifier(MonitorNotifier notifier)
 * @see IBeaconManager#startMonitoringBeaconsInRegion(Region region)
 * @see Region
 * 
 */
public interface MonitorNotifier {
	/**
	 * 指示智能终端进入了自定义的region<br>
	 * Indicates the Android device is inside the Region of iBeacons
	 */
	public static final int INSIDE = 1;
	/**
	 * 指示智能终端离开了自定义的region<br>
	 * Indicates the Android device is outside the Region of iBeacons
	 */
	public static final int OUTSIDE = 0;
	
	/**
	 * 当进入自定义的region时回调
	 * Called when at least one iBeacon in a <code>Region</code> is visible.
	 * @param region a Region that defines the criteria of iBeacons to look for
	 */
	public void didEnterRegion(Region region);

	/**
	 * 当离开自定义的region时回调
	 * Called when no iBeacons in a <code>Region</code> are visible.
	 * @param region a Region that defines the criteria of iBeacons to look for
	 */
	public void didExitRegion(Region region);
	
	/**
	 * 当进入离开region时状态发生变化时回调
	 * Called with a state value of MonitorNotifier.INSIDE when at least one iBeacon in a <code>Region</code> is visible.
	 * Called with a state value of MonitorNotifier.OUTSIDE when no iBeacons in a <code>Region</code> are visible.
	 * @param state either MonitorNotifier.INSIDE or MonitorNotifier.OUTSIDE
	 * @param region a Region that defines the criteria of iBeacons to look for
	 */
	public void didDetermineStateForRegion(int state, Region region);
}
