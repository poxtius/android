/**********************************************************
 *Copyright (C) Josema Fernandez LSB Andoain
 *
 *	INOMET (Jon Fernandez, Mikel Jimenez, Ion Arbizu)
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
 *  Author : Jose Manuel Fernandez <fernandezjm at lasalleberrozpe dot com>
 *
 * 
 *******************************/

package com.lsb.inomet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Inomet extends Activity {
	
	private static final int DIR_WEB = 1;
	private boolean primera_vez;
	private String direccionWeb;

	//Asynstask-arekin zerikusia duen aldagaia
	private final String TAG = getClass().getSimpleName();
	
	private ImageView logoInomet, lluviaInomet;
	private TextView humedadText, contaText, vientoText, tempeText, presionText, luminosidadText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inomet);
	
		setup_widget();
		primera_vez = true;
		datuak_irakurri(); 
		
	}

	private void setup_widget() {
		
		logoInomet = (ImageView) findViewById(R.id.logoInomet);
		logoInomet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				datuak_irakurri();
			}
		});
		
		lluviaInomet = (ImageView) findViewById(R.id.lluviaInomet);		
		humedadText = (TextView) findViewById(R.id.humedadText);
		contaText = (TextView) findViewById(R.id.contaminacionText);
		vientoText = (TextView) findViewById(R.id.vientoText);
		tempeText = (TextView) findViewById(R.id.tempeText);
		presionText = (TextView) findViewById(R.id.presionText);
		luminosidadText = (TextView) findViewById(R.id.luminosidadText);
	}
	

	protected void datuak_irakurri() { //Funtzio honekin Asynctask-a marchan jartzen dugu datuak irakurtzeko
		
		final captureData task = new captureData();
		try {
			if (primera_vez)
				task.execute(new URI("http://10.123.5.43 "), null, null);
			else{
				task.execute(new URI("http://"+ direccionWeb), null, null);
			}
		} catch (URISyntaxException e) {
			Toast.makeText(this, "Direcci—n "+ direccionWeb + " incorrecta."  , Toast.LENGTH_LONG).show();
			Log.d(TAG, e.getMessage());
		}	
	}

	
	public class captureData extends AsyncTask<URI, Integer, String>{

		private String url;
		
		private ProgressDialog pd;
		
		protected void onPreExecute()
		{
			// Argazkia ateratzen ari den bitartean progressDialog bat erakutsiko dugu
			pd = new ProgressDialog(Inomet.this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setTitle("Inomet");
			pd.setMessage("Datuak irakurtzen ...");
			pd.show();
		}
		
		@Override
		protected String doInBackground(final URI... uris) {
			// TODO Auto-generated method stub
			
			url = uris[0].toString();
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
					Log.e("Fallo Lectura",e.getMessage());
					return null;
				}
			
		}
		
		protected void onPostExecute(String result)
	    {
			
			if (pd.isShowing())
				pd.cancel();
			
	    	if (result!=null){
	    		
	    		JSONObject json;
	    		try {
					json = new JSONObject(result);
					String contaaire;
					if (json.has("Aire")){
						  contaaire = json.getString("Aire");
						  contaText.setText(contaaire + " %");
					}
					if (json.has("Temperatura")){
						  String tempe = json.getString("Temperatura");
						  tempeText.setText(tempe + " ¼C");
					}
					if (json.has("Humedad")){
						  String humedad = json.getString("Humedad");
						  humedadText.setText(humedad+ " %");
					}
					if (json.has("Presion")){
						  String presion = json.getString("Presion");
						  presionText.setText(presion+" mbar");
					}
					if (json.has("Viento")){
						  String vientoV = json.getString("Viento");
						  vientoText.setText(vientoV + " Km/h");
					}
					if (json.has("Luminosidad")){
						  String luminosidad = json.getString("Luminosidad");
						  luminosidadText.setText(luminosidad + " %");
					}
					if (json.has("Lluvia")){
						  int lluvia = json.getInt("Lluvia");
						  if (lluvia == 0){
							  lluviaInomet.setImageResource(R.drawable.lluvia);						
						  }
						  if (lluvia == 1){
							  lluviaInomet.setImageResource(R.drawable.sol);
						  }
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}			
	    }
	}//AsyncTask-aren bukaera.
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.inomet, menu);
		return true;
	}
	 @Override public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
         case R.id.config:
                lanzarConfig(null);
                break;
         }
         return true; /** true -> consumimos el item, no se propaga*/
}

	private void lanzarConfig(Object object) {
		Intent i = new Intent(this, configuracion.class);
    	startActivityForResult(i, DIR_WEB);
	}
	
	@Override
	   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	      super.onActivityResult(requestCode, resultCode, data);
	      primera_vez= false;
	      if (requestCode == DIR_WEB) {
	         // cogemos el valor devuelto por la otra actividad
	         direccionWeb = data.getStringExtra("DirEstacion");
	         // ense–amos al usuario el resultado
	         //Toast.makeText(this, "Config devolvi—: " + result, Toast.LENGTH_LONG).show();
	         datuak_irakurri();
	      }
	   }
}//


