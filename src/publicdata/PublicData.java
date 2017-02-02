package publicdata;
import com.whh.beaconsdk.SharedPreferencesHelper;

import android.app.Application;
import android.location.Location;
import android.util.Log;

public class PublicData extends Application{
	public  byte[] passwd = new byte[]{0x01,0x02,0x03,0x04,0x05,0x06}; 
	public  byte[] chagepwd;
	public 	int    chagepwd_falg=0; 
	public 	int  	flag = 0;
	private static PublicData self;
	//手机端蓝牙rssi
	public  int    phone_rssi;
	
	public static PublicData getInstance(){
        return self;
    }   
	SharedPreferencesHelper sp;
    @Override  
    public void onCreate(){    
        super.onCreate();  
        self = this;
      //存储文件的名字是contacts.xml，在eclipse中的DDMS的fileexplore页下的data/data/包名/shared_prefs下
        sp = new SharedPreferencesHelper(this, "contacts");     
        
        //1. to store some value
//        
             
        //2. to fetch the value
        String name = sp.getValue("tmp_passwd");
        if(name == null){
        	sp.putValue("tmp_passwd", "123456");
        	name = sp.getValue("tmp_passwd");
        }
        
        passwd = str2byte(name);
        Log.w("passwd", " "+passwd[0]);
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
    
    public double rss_to_distance(){
    	int A = 45;
    	double n = 3.25;
    	
    	double d=Math.pow(10, (Math.abs(phone_rssi) -A)/(10*n));    	      	   	
    	return d;
    }
    public double gps_longitude(double a){
    	
		return a;
    	
    }
    public double gps_latitude(Location location){
		return phone_rssi;
    	
    }
}
