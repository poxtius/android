package com.example.com.lsb.arduinoledswitch;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {
	
	private final String URL = "http://10.15.181.21/";
	
	public JSONParser (){
		
		
	}
	
	public int getSwitch(){
		
		String str = doGetPetition( URL + "status/");
		JSONObject json;
		try {
			
			json = new JSONObject(str);
			if (json.has("switch"))
			   return json.getInt("switch");
		
		} catch (JSONException e1) {
			return -1;
		}
		
		return -1;
	}
	
	public void setLed(Boolean status){
		
		if (status)
			doGetPetition(URL +"led/on" );
		else
			doGetPetition(URL+ "led/off");
		
	}
	
	 private String doGetPetition (String url) //Esta funci—n solo sirve para conseguir texto plano
	    {
	    	try
	    	{
	    		DefaultHttpClient httpclient = new DefaultHttpClient(); //Creamos el objeto
	    		HttpGet httpGet = null;   //Inicianizamos la variable
	    		httpGet = new HttpGet(url);  //Creamos una una petici—n Get para recibir el texto en este caso un JSON
	    		HttpResponse response = httpclient.execute(httpGet);
	    		HttpEntity entity = response.getEntity();
	    		String str = EntityUtils.toString(entity);
	    		
	    		Log.d("JSON", str); //Presentamos esto en el LOG
	    		
	    		return str;
	    		
	    	}
	    	catch (IOException e) {
	    		Log.e("doGet",e.getMessage());
	    		return null;
	    	}
	    }

}
