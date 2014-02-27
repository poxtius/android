package com.example.com.lsb.arduinoledswitch;

import java.util.Timer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	TimerSwitch mTimerS;
	Timer mt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        StrictMode.ThreadPolicy policy = new
        		StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        		StrictMode.setThreadPolicy(policy);
        
        final TextView tvStatus = (TextView) findViewById(R.id.textView1);
        
        Button btOn = (Button) findViewById(R.id.btOn);
        Button btOff = (Button) findViewById(R.id.btOff);
        
          
        Handler myHandler = new Handler(new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				
				tvStatus.setText("Switch= "+ String.valueOf(msg.what));
				// TODO Auto-generated method stub
				return true;
			}
		});
        
        mTimerS = new TimerSwitch(myHandler);
        mt = new Timer();
        mt.schedule(mTimerS, 0, 3000);
        
        btOn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Thread th = new Thread(new Runnable() {
					
					@Override
					public void run() {
						RoomControler rm = new RoomControler();
						rm.setLed(true);
						
					}
				});
				th.start();
			}
		});
        
        btOff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Thread th = new Thread(new Runnable() {
					
					@Override
					public void run() {
						RoomControler rm = new RoomControler();
						rm.setLed(false);
						
					}
				});
				th.start();
			}
		});
        
  
    }
    
    @Override
    protected void onDestroy() {
   
    	super.onDestroy();
    	
    	if(mTimerS != null)
    		mTimerS.cancel();
    	if (mt != null)
    		mt.purge();
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
