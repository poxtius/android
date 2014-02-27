/*
 *
 *  Copyright (C) Roberto Calvo Palomino
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/. 
 *
 *  Author : Roberto Calvo Palomino <rocapal at gmail dot com>
 *
 */

package es.curso.android.sensors;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {

	String TAG = getClass().getSimpleName();
	
	SensorManager sensorManager = null;   //es el registrador de los eventos
	
	TextView tvAcc, tvLight, tvProximity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); //aqui le decimos al sistemas que todo
		//lo que los sistemas lo meta a registrador.
		
		setContentView(R.layout.activity_main);
		
		tvAcc = (TextView) this.findViewById(R.id.tv_acceleration);		
		tvLight= (TextView) this.findViewById(R.id.tv_light);
		tvProximity = (TextView) this.findViewById(R.id.tv_proximity);
	}
	
	@Override
	protected void onResume() {		
		super.onResume();
	    sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_GAME);	    
	    sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), sensorManager.SENSOR_DELAY_NORMAL);
	    
	    //en el onResume le decimos que registre los sensores que queremos y los mete al registrador sensorManager
	    
	    if(sensorManager.getSensorList(Sensor.TYPE_PROXIMITY) != null)
	    	sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), sensorManager.SENSOR_DELAY_NORMAL);
	    
	    //Obtener lista de sensores
	    List<Sensor> sensor = sensorManager.getSensorList(Sensor.TYPE_ALL);
	    
	    for(Sensor s: sensor){
	    	
	    	Log.d("Main", s.getName()); //Printeara en el Log los sensores que puede implementar...
	    }
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
				
	    sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));	  
	    sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT));
	    sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
	    //si salimos de la aplicaci—n hay que desregistrar los sensores
	}
    

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {  //es una funci—n que se implementa a partir de de la 
		//versi—n 4.0 y es para generar el menu contextual de la aplicaci—n.
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {  //esta funci—n se ejecuta cuando hay algœn cambio en los
		//sensores o cuando el delay se cumple
		synchronized (this) {
			
			switch (event.sensor.getType()){
			case Sensor.TYPE_ACCELEROMETER:
				tvAcc.setText("");
				tvAcc.setText("x:"+Float.toString(event.values[0]) + "\n");
				tvAcc.setText(tvAcc.getText() + "y:"+Float.toString(event.values[1]) + "\n");
				tvAcc.setText(tvAcc.getText() +  "z:"+Float.toString(event.values[2]));
				//toma los datos del aceler—metro para presentarlos en el textview
				
				float accelationSquareRoot = (event.values[0] * event.values[0] + 
											  event.values[1] * event.values[1] + 
											  event.values[2] * event.values[2])											  
											  / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
				//funci—n que se utiliza para la detecci—n de los G.
				
				if (accelationSquareRoot >= 4) //en este caso detecta mayor de 4G y muestra un toast.
				{									
					Log.d(TAG, Float.toString(event.values[0]) + " " + Float.toString(event.values[1]) + " " + Float.toString(event.values[2]));
					Toast.makeText(this, "Movimiento fuerte en smartphone ", Toast.LENGTH_SHORT).show();
				}
				      
				      
				break;							
				
			case Sensor.TYPE_LIGHT:
				tvLight.setText(Float.toString(event.values[0]));
				break;
				//saca el valor en un textView
			case Sensor.TYPE_PROXIMITY:
				tvProximity.setText(Float.toString(event.values[0]));
				break;
				//saca el valor en un textView
			}
	    }
		
	}

}
