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

import java.util.Collection;

/**
 * 回调接口，需要实现回调接口中的方法，以接收扫描到的自定义的region{@link Region}中的beacon
 * This interface is implemented by classes that receive iBeacon ranging notifications
 * 
 * @see IBeaconManager#setRangeNotifier(RangeNotifier notifier)
 * @see IBeaconManager#startRangingBeaconsInRegion(Region region)
 * @see Region
 * @see IBeacon
 * 
 *
 */
public interface RangeNotifier {
	/**
	 * 按自定义的扫描周期回调显示所有扫描到的beacon
	 * Called once per second to give an estimate of the distance to visible iBeacons
	 * @param iBeacons a collection of <code>IBeacon<code> objects that have been seen in the past second
	 * @param region the <code>Region</code> object that defines the criteria for the ranged iBeacons
	 */
	public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region);
	/**
	 * 回调显示region中新扫描到的beacon
	 * @param iBeacons
	 * @param region
	 */
	public void onNewBeacons(Collection<IBeacon> iBeacons, Region region);
	/**
	 * 回调显示region中消失的beacon
	 * @param iBeacons
	 * @param region
	 */
	public void onGoneBeacons(Collection<IBeacon> iBeacons, Region region);
	/**
	 * 回调更新region中beacon的信息
	 * @param iBeacons
	 * @param region
	 */
	public void onUpdateBeacon(Collection<IBeacon> iBeacons, Region region);
	
}
