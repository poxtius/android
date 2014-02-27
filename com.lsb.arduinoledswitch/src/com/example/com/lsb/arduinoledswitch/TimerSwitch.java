package com.example.com.lsb.arduinoledswitch;

import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class TimerSwitch extends TimerTask{
	
	Handler mHandler;
	
	public TimerSwitch (Handler handler){
		if (handler == null)
			throw new NullPointerException();
		mHandler = handler;
	}

	@Override
	public void run() {
		
		RoomControler rm = new RoomControler();
        Boolean res;
        
        try {
			res = rm.getStatusSwitch();
			Log.d("MAIN", String.valueOf(res));
		} catch (Exception e) {
			Log.d("MAIN", "Esto va mal");
			return;
		}
        
        if (mHandler != null)
        	{
        	 Message msg = new Message();
        	 msg.what = (res == true)?1:0; //esta l’nea es igual que lo que he puesto debajo en comentarios.
        
        	 /*if (res == true)
        	  *   msg.what(1);
        	  *else
        	  *	msg.what(0);
        	  */
        	 mHandler.sendMessage(msg);
        	}
	}

}
