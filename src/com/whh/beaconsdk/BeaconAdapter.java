package com.whh.beaconsdk;

import java.util.List;

import publicdata.PublicData;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.whh.beaconsdk.R;
import com.lef.scanner.IBeacon;

public class BeaconAdapter extends BaseAdapter{
	private List<IBeacon> beaconList;
	private LayoutInflater layoutInflater = null;
    private ViewHolder holder = null;
	public BeaconAdapter(Context context,List<IBeacon> beaconList){
		this.beaconList = beaconList;
		layoutInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return beaconList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return beaconList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	static int 		rssi_new,rssi_old;
	static int  	first_rssi,get_rssi_count=0,rssi_count =0;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
		// TODO Auto-generated method stub
		if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.beaconadapter, parent, false);
            holder = new ViewHolder();
            holder.majorTextView = (TextView) convertView.findViewById(R.id.major);
            holder.minorTextView = (TextView) convertView.findViewById(R.id.minor);
            holder.rssiTextView = (TextView) convertView.findViewById(R.id.rssi);
            holder.IDTextView = (TextView) convertView.findViewById(R.id.ID);
            holder.distanceTextView = (TextView)convertView.findViewById(R.id.distance);
            holder.canBeConnectedView = (ImageView) convertView.findViewById(R.id.canbeconnected);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
		if (beaconList.size()>0) {
			holder.majorTextView.setText(String.valueOf(beaconList.get(position).getMajor1()));
			holder.minorTextView.setText(String.valueOf(beaconList.get(position).getMinor1()));
			holder.IDTextView.setText(beaconList.get(position)
					.getBluetoothAddress());
			holder.distanceTextView.setText(getDistance(beaconList.get(position)));		
			holder.canBeConnectedView
					.setBackgroundResource(getConnectionView(beaconList
							.get(position)));
			//rssi Optimization	
			get_rssi_count++;
			if(PublicData.getInstance().flag == 0){
				first_rssi = beaconList.get(position).getRssi();
				rssi_new   = first_rssi;
				PublicData.getInstance().flag = 1;
				Log.w("first_rssi: ","");
			}else{
				  rssi_new = beaconList.get(position).getRssi();
				  int count = 100;
					for(int i = count;i>=0;i--)				  
						rssi_new = (15*rssi_new + rssi_old) >> 4;												
			}
			rssi_count+=rssi_new;					
			Log.w("rssi_new: ",""+rssi_new);
			rssi_old = rssi_new;	
			holder.rssiTextView.setText(String.valueOf(beaconList.get(position).getRssi()));
		}
			if(get_rssi_count == 10){				
				PublicData.getInstance().flag = 0;
				get_rssi_count				  =	0;
				rssi_count					  = 0;
			}			
//		}
		return convertView;
	}
	private int getConnectionView(IBeacon beacon){
		if (beacon.isCanBeConnected()){
			return R.drawable.conn;
		}else{
			return R.drawable.conn;
		}
	}
	public static String getDistance(IBeacon beacon){
		return String.valueOf(beacon.getName());
	}
	class ViewHolder {
        private TextView  majorTextView	     = null;
        private TextView  minorTextView      = null;
        private TextView  rssiTextView       = null;
        private TextView  distanceTextView 	 = null;
        private TextView  IDTextView 		 = null;
        private ImageView canBeConnectedView = null;
    }
}
