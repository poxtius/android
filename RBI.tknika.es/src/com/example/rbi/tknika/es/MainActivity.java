package com.example.rbi.tknika.es;


import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private ImageView mImageView;
	private Button btOn, btOff;
	private TextView tvInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mImageView = (ImageView) this.findViewById(R.id.imPicture);
        btOn = (Button) this.findViewById(R.id.btOn);
        btOff = (Button) this.findViewById(R.id.btOff);
        tvInfo = (TextView) this.findViewById(R.id.tvInfo);
        
        Bitmap bmImage = getImage("http://10.15.181.27:8888/sensors/camera/picture/");
        if (bmImage != null)
        	mImageView.setImageBitmap(bmImage);
        else
        	Toast.makeText(this, "Error while image is load", Toast.LENGTH_LONG).show();
        
        String strJSON= getResponse("http://10.15.181.27:8888/sensors/temp/board/");
        
        try {
			JSONObject json = new JSONObject(strJSON);
			
			if (json.has("temp"))
			{
				String temp = json.getString("temp");
				tvInfo.setText("Temp. Board:" + temp + "¼C");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        strJSON= getResponse("http://10.15.181.27:8888/sensors/temp/ds/");  //Pido el json de esta url y lo guardo en un
        																	//string
        try {
			JSONObject json = new JSONObject(strJSON);   //creamos el objeto Json  pasandole el string que hemos descargado antes
			
			if (json.has("temp"))   //busco la clave temp y si est‡
			{
				String temp = json.getString("temp");      //cojo el valor que tiene esa clave
				tvInfo.setText(tvInfo.getText() + "\n" +
								"Temp Sensor: " + temp + "¼C");   //lo represento en un textView
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        btOn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			  getResponse("http://10.15.181.27:8888/sensors/led/?state=on");	
			}
		});
        
        btOff.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			  getResponse("http://10.15.181.27:8888/sensors/led/?state=off");	
			}
		});
    }
    
    private Bitmap getImage(String url){       //esta funci—n permite descargar la imagen desde una 
    										// url 
    	try
    	{
    	
    	DefaultHttpClient httpclient =new DefaultHttpClient();
    	HttpGet httpGet = null;
    	
    	httpGet = new HttpGet(url);
    	HttpResponse response = httpclient.execute(httpGet);
    	
    	if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
    	{
    		InputStream inputStream = response.getEntity().getContent();
    		return BitmapFactory.decodeStream(inputStream);
    		
    	}
    	else
    		return null;
    	}
    	catch (IOException e){
    		Log.e("Main", e.getMessage());
    		return null;
    	}
    }
    
    private String getResponse (String url)   //getResponse permite descargar cualquier json mediante una peticion
    {										  //http y devuelve un string que en otra funci—n podr‡ parsear
    	try
    	{
    	
    	DefaultHttpClient httpclient =new DefaultHttpClient();
    	HttpGet httpGet = null;
    	
    	httpGet = new HttpGet(url);
    	HttpResponse response = httpclient.execute(httpGet);
    	
    	if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
    	{
    		return EntityUtils.toString(response.getEntity());
    		
    	}
    	else
    		return null;
    	}
    	catch (IOException e){
    		Log.e("Main", e.getMessage());
    		return null;
    	}	
    	
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
