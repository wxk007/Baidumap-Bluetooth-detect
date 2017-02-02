package com.whh.beaconsdk;

import java.util.Arrays;

import publicdata.PublicData;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whh.beaconsdk.R;
import com.lef.scanner.BeaconConnection;




public class ChangePasswdActivity extends Activity {
	private TextView defaultPasswdView;
	private TextView changePasswdView;
	
	private static final int EMPTYVALUE = 1;
	private static final int INVALIDVALUE = 2;
	private static final int SETSUCCEED = 3;
	private static final int SETFAILURE = 4;
	private static final int SETNEWPWDSUCCEED = 5;
	private static final int SETNEWPWDFAILURE = 6;
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case EMPTYVALUE:
				Toast.makeText(ChangePasswdActivity.this, "不能为空", Toast.LENGTH_SHORT)
						.show();
				break;
			case SETFAILURE:
				Toast.makeText(ChangePasswdActivity.this, "密码错误",
						Toast.LENGTH_SHORT).show();
				break;
			case SETSUCCEED:
				Toast.makeText(ChangePasswdActivity.this, "原始密码匹配成功", Toast.LENGTH_SHORT)
						.show();
				break;
			case SETNEWPWDSUCCEED:
				Toast.makeText(ChangePasswdActivity.this, "新密码设置成功", Toast.LENGTH_SHORT)
						.show();
				break;
			case SETNEWPWDFAILURE:
				Toast.makeText(ChangePasswdActivity.this, "新密码设置失败", Toast.LENGTH_SHORT)
						.show();
				break;
			case INVALIDVALUE:
				Toast.makeText(ChangePasswdActivity.this, "不合理值，请输入6位数字", Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				break;
			}
		};
	};
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_passwd);
        
        defaultPasswdView = (TextView) findViewById(R.id.default_passwd_modify);
        changePasswdView = (TextView) findViewById(R.id.change_passwd_modify);       
       
        defaultPasswdView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(
						R.layout.attr_modify_dialog, null);
				final EditText attrValue = (EditText) ll
						.findViewById(R.id.attr_value);
				// TODO Auto-generated method stub
				new AlertDialog.Builder(ChangePasswdActivity.this)
						.setIcon(R.drawable.title_bar_menu)
						.setTitle("请输入原密码")
						.setView(ll)
						.setPositiveButton("保存",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										if (!attrValue.getText().toString()
												.equals("")) {
											setDefaultPasswd(attrValue
													.getText().toString());
										} else {
											handler.sendEmptyMessage(EMPTYVALUE);
										}

									}
								}).setNegativeButton("取消", null).create()
						.show();
			}
		});
        
        changePasswdView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(
						R.layout.attr_modify_dialog, null);
				final EditText attrValue = (EditText) ll
						.findViewById(R.id.attr_value);
				// TODO Auto-generated method stub
				new AlertDialog.Builder(ChangePasswdActivity.this)
						.setIcon(R.drawable.title_bar_menu)
						.setTitle("请输入新密码")
						.setView(ll)
						.setPositiveButton("保存",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										if (!attrValue.getText().toString()
												.equals("")) {
											setNewPasswd(attrValue
													.getText().toString());
										} else {
											handler.sendEmptyMessage(EMPTYVALUE);
										}

									}
								}).setNegativeButton("取消", null).create()
						.show();
			}
		});
        
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.finish();
	}	
	private void setDefaultPasswd(String text){
		if(text.length()!=6) handler.sendEmptyMessage(INVALIDVALUE);		
		if(isNumber(text)){						 
			if(Arrays.equals(PublicData.getInstance().passwd,str2byte(text))){
				PublicData.getInstance().chagepwd = str2byte(text);
				handler.sendEmptyMessage(SETSUCCEED);
			}
			else
				handler.sendEmptyMessage(SETFAILURE);
		}
	}
	SharedPreferencesHelper sp;
	private void setNewPasswd(String text){
		if(text.length()!=6) handler.sendEmptyMessage(INVALIDVALUE);
		if(isNumber(text)){
		if(Arrays.equals(PublicData.getInstance().passwd,PublicData.getInstance().chagepwd)){			
			BeaconConnection.setCHGEPWD(str2byte(text));
			BeaconConnection.setNewPWD();
			
			PublicData.getInstance().passwd=str2byte(text);		
			sp = new SharedPreferencesHelper(this, "contacts"); 
			sp.putValue("tmp_passwd", text);
			handler.sendEmptyMessage(SETNEWPWDSUCCEED);
//			if(PublicData.getInstance().chagepwd_falg == 0){}else {
//				BeaconConnection.setCHGEPWD(PublicData.getInstance().passwd);
//				PublicData.getInstance().chagepwd_falg = 0;
//				handler.sendEmptyMessage(SETNEWPWDFAILURE);
//			}
		}
		else
			handler.sendEmptyMessage(SETFAILURE);		
		}
	}
	
	public boolean isNumber(String text) {
		for(int i=0;i<text.length();i++){
			if(text.charAt(i)<'0' && text.charAt(i)>'9'){
				handler.sendEmptyMessage(INVALIDVALUE);
				return false;
			}			
		}		        
				return true;		
	}
	public byte[] str2byte(String text){
        byte[] b = new byte[text.length()];
        for (int i =0;i < text.length();i++){
            b[i] = invert(text.charAt(i));
        }
        return b;
     }

    private byte invert(char ch){
        switch (ch)
        {
            case '0':return 0x00;
            case '1':return 0x01;
            case '2':return 0x02;
            case '3':return 0x03;
            case '4':return 0x04;
            case '5':return 0x05;
            case '6':return 0x06;
            case '7':return 0x07;
            case '8':return 0x08;
            case '9':return 0x09;
            default:
        }
        return (byte)0xFF;
    }
}

