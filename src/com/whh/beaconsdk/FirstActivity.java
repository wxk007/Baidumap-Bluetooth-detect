package com.whh.beaconsdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FirstActivity extends Activity{
@Override
protected void onCreate(Bundle savedInstanceState){
	super.onCreate(savedInstanceState);
	setContentView(R.layout.first_layout);
	Button button1=(Button)findViewById(R.id.button_1);
	Button button2=(Button)findViewById(R.id.button_2);
	button1.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v){
			Intent intent=new Intent(FirstActivity.this,MainActivity.class);
			startActivity(intent);
		}
	});
	button2.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v){
		Intent intent=new Intent(FirstActivity.this,HeatMapDemo.class);
		startActivity(intent);
		}
	});
	
}
}
