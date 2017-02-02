package com.whh.beaconsdk;

import com.whh.beaconsdk.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RssiAdapter extends BaseAdapter{
	private int phone_rssi;
	private LayoutInflater layoutInflater = null;
    private ViewHolder holder = null;
	public RssiAdapter(Context context,int phone_rssi){
		this.phone_rssi = phone_rssi;
		layoutInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
		// TODO Auto-generated method stub
		if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.rssiadapter, parent, false);
            holder = new ViewHolder();
            holder.rssiTextView = (TextView) convertView.findViewById(R.id.phone_rssi);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
			
		holder.rssiTextView.setText(phone_rssi + "");						
		
		return convertView;
	}

	class ViewHolder {    
        TextView  rssiTextView       = null;
    }
}
