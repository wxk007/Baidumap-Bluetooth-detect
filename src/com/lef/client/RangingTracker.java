package com.lef.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;

import com.lef.scanner.IBeacon;
import com.lef.scanner.IBeaconManager;

public class RangingTracker {
	private static String TAG = "RangingTracker";	
	private Map<IBeacon,RangedIBeacon> rangedIBeacons = new HashMap<IBeacon,RangedIBeacon>();
	public void addIBeacon(IBeacon iBeacon) {
		if (rangedIBeacons.containsKey(iBeacon)) {
			RangedIBeacon rangedIBeacon = rangedIBeacons.get(iBeacon);
			if (IBeaconManager.debug) Log.d(TAG, "adding "+iBeacon.getProximityUuid1()+" to existing range for: "+rangedIBeacon.getProximityUuid1() );
			rangedIBeacon.addRangeMeasurement(iBeacon.getRssi());
		}
		else {
			if (IBeaconManager.debug) Log.d(TAG, "adding "+iBeacon.getProximityUuid1()+" to new rangedIBeacon");
			rangedIBeacons.put(iBeacon, new RangedIBeacon(iBeacon));
		}
	}
	public synchronized Collection<IBeacon> getIBeacons() {	
		ArrayList<IBeacon> iBeacons = new ArrayList<IBeacon>();		
		Iterator<RangedIBeacon> iterator = rangedIBeacons.values().iterator();
		while (iterator.hasNext()) {
			RangedIBeacon rangedIBeacon = iterator.next();
			if (!rangedIBeacon.allMeasurementsExpired()) {
				iBeacons.add(rangedIBeacon);				
			}
		}
		return iBeacons;
	}
	

}
