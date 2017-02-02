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
 * 用于回调绑定服务的consumer
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.lef.ibeacon.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
/**
 * Service回调，回调IBeaconIntentProcessor，实现beacon探测信息的通知<br>
 * IBeaconIntentProcessor实现了过滤，如果要通知，需要添加过滤信息，{@code intentForNew.setComponent(new ComponentName(intentPackageName, "com.lef.ibeacon.IBeaconIntentProcessor"));}
 * 否则无法收到通知，在这里出现过错误
 * @author lief
 *
 */
public class Callback {
	private String TAG = "Callback";
//	private Messenger messenger;
	private Intent intentForNew;
	private Intent intentForUpdate;
	private Intent intentForGone;
	private Intent intentForFind;
	public Callback(String intentPackageName) {
		if (intentPackageName != null) {
			intentForNew = new Intent();
			intentForNew.setComponent(new ComponentName(intentPackageName, "com.lef.ibeacon.IBeaconIntentProcessor"));
			intentForUpdate = new Intent();
			intentForUpdate.setComponent(new ComponentName(intentPackageName, "com.lef.ibeacon.IBeaconIntentProcessor"));
			intentForGone = new Intent();
			intentForGone.setComponent(new ComponentName(intentPackageName, "com.lef.ibeacon.IBeaconIntentProcessor"));
			intentForFind = new Intent();
			intentForFind.setComponent(new ComponentName(intentPackageName, "com.lef.ibeacon.IBeaconIntentProcessor"));
        }
	}
//	public Intent getIntent() {
//		return intent;
//	}
//	public void setIntent(Intent intent) {
//		this.intent = intent;
//	}
	/**
	 * Tries making the callback, first via messenger, then via intent
	 * 
	 * @param context
	 * @param dataName
	 * @param data
	 * @return false if it callback cannot be made
	 */
	public boolean call(Context context, String dataName, Parcelable data) {
		if (intentForFind != null) {
			Log.d(TAG, "attempting callback via intent: "+intentForFind.getComponent());
			intentForFind.putExtra(dataName, data);
			context.startService(intentForFind);		
			return true;			
		}
		return false;
	}
	/**
	 * 当检测到区域中新的beacon时，回调
	 * @param context
	 * @param dataName
	 * @param data
	 * @return
	 */
	public boolean callForNewBeacon(Context context, String dataName, Parcelable data) {
		if (intentForNew != null) {
			Log.d(TAG, "attempting callback via intent: "+intentForNew.getComponent());
			intentForNew.putExtra(dataName, data);
			context.startService(intentForNew);		
			return true;			
		}
		return false;
	}
	/**
	 * 当检测到有beacon消失时，回调
	 * @param context
	 * @param dataName
	 * @param data
	 * @return
	 */
	public boolean callForGoneBeacon(Context context, String dataName, Parcelable data) {
		if (intentForUpdate != null) {
			Log.d(TAG, "attempting callback via intent: "+intentForUpdate.getComponent());
			intentForUpdate.putExtra(dataName, data);
			context.startService(intentForUpdate);		
			return true;			
		}
		return false;
	}
	/**
	 * 更新beacon数据
	 * @param context
	 * @param dataName
	 * @param data
	 * @return
	 */
	public boolean callForUpdateBeacons(Context context, String dataName, Parcelable data) {
		if (intentForFind != null) {
			Log.d(TAG, "attempting callback via intent: "+intentForFind.getComponent());
			intentForFind.putExtra(dataName, data);
			context.startService(intentForFind);		
			return true;			
		}
		return false;
	}
}
