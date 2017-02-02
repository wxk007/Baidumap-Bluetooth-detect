package com.whh.beaconsdk;

import org.json.JSONException;  
import org.json.JSONObject;  
import org.json.JSONTokener;  
//import org.junit.Test;  
  
public class getjson {  
  
    //@Test  
    public void test() {  
        System.out.println(getJSONContent());  
    }  
      
    private static String JSONText = "{\"id\":20130001,\"phone\":\"13579246810\",\"name\":\"Jason\"}";  
      
    private static String getJSONContent(){  
        JSONTokener jsonTokener = new JSONTokener(JSONText);   
        JSONObject beaconJSONObject;  
        String name = null;  
        int id = 0;  
        String phone = null;  
        try {  
            beaconJSONObject = (JSONObject) jsonTokener.nextValue();  
            name = beaconJSONObject.getString("name");  
            id = beaconJSONObject.getInt("id");  
            phone = beaconJSONObject.getString("phone");  
              
        } catch (JSONException e) {  
            e.printStackTrace();  
        }  
        return name + "  " + id + "   " + phone;  
    }  
}  