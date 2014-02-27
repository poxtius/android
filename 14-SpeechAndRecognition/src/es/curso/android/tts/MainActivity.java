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

//Para que el reconocimiento de voz funcione el dispositivo debe tener instalado Google Voice. CUIDADO CON LOS MÓVILES CHINOS!!
//No pasa lo mismo con el TextToSpeech ya que lo integra el SDK de Android

package es.curso.android.tts;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//Implementamos OnInitListener para utilizar Text TO Speech
public class MainActivity extends Activity implements TextToSpeech.OnInitListener {

	private EditText etTTS;
	private TextToSpeech mTTS;
	private Button btTTS, btVoice;
	private TextView tvResults;
	
	private String START_VIDEO_COMMAND="comenzar";
	private String STOP_VIDEO_COMMAND="parar";
	
	protected static final int RESULT_SPEECH = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mTTS = new TextToSpeech(this, this); //instanciamos o creamos el objeto de TextToSpeech
		
		setupWidgets();
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		if (mTTS != null)
		{
			mTTS.stop();//paramos el dictado de voz
					
		}
	}


	@Override
	protected void onDestroy() {
		
		if (mTTS != null)
		{
			mTTS.stop();//paramos el dictado de voz
			mTTS.shutdown();//liberamos los recursos de memoria			
		}					
		
		super.onDestroy();
				
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void setupWidgets()
	{
		
		tvResults = (TextView) this.findViewById(R.id.tvResults);
		etTTS = (EditText) this.findViewById(R.id.etText);
		
		btTTS = (Button) this.findViewById(R.id.btTTS);
		btTTS.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String text = etTTS.getText().toString();				 
		        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);//comenzamos
		        												 //con queue_flush si coincide que viene un nuevo texto para reproducir
		        												 //se dejara de reproducir lo actual y se empeará con lo nuevo.
				
			}
		});		
		
		btVoice = (Button) this.findViewById(R.id.btVoice);
		
				btVoice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//el reconocimiento de voz se hará mediante un intent 
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");//definir lenguaje de reconocimiento

                try {
                	
                	tvResults.setText("");//cada vez que hagamos un nuevo reconocimiento de voz vaciamos el textview
                						  //donde escribimos las distintas posiblidades de interpretación de android.
                	
                	
                    startActivityForResult(intent, RESULT_SPEECH);//lanzamos el intent que hemos configurado para RECOGNIZE_SPEECH
                   
                } catch (ActivityNotFoundException a) {
                	
                	
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
				
			}
		});
		
		
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//TEXTTOSPEECH
	//metodo que nos obligan a implementar por implemetar TextToSpeech.OnInitListener en la activity.
	//Esta función se ejecuta cuando se hace un New de la clase TextToSpeech que ofrece el SDK de android.
	//Se utiliza para definir el lenguaje que se va a utilizar.
	@Override
	public void onInit(int status) {
		
		if (status == TextToSpeech.SUCCESS) {
			 
            int result = mTTS.setLanguage(new Locale("spa"));//definimos lenguaje
 
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            	                
                Toast.makeText(this, "TTS: Language is not supported", Toast.LENGTH_SHORT).show();
                
            } 
            else
            {
            	Toast.makeText(this, "TTS: Initilization OK!", Toast.LENGTH_SHORT).show();
            	btTTS.setEnabled(true);
            }
 
        } else {
            Log.d("TTS", "Initilization Failed!");
            Toast.makeText(this, "TTS: Initilization Failed!", Toast.LENGTH_SHORT).show();
        }
		
	}
	
	//Recogemos el resultado del intent que será un array con las frases reconocidas	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode) {
        case RESULT_SPEECH: {
            if (resultCode == RESULT_OK && null != data) {
 
                ArrayList<String> results = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
 
                
                showResults(results);
                detectCommand(results);
            }
            break;
        }
 
        }
    }
	
	//Pasamos al textview los  datos o frases obtenidas en el array.
	private void showResults (ArrayList<String> results)
	{
		for (String sentence : results) 			
			tvResults.setText( tvResults.getText() + sentence + "\n");
		
	}
	
	//función que hemos definido para gestionar lo que devuelve el reconocedor
	 private void detectCommand(ArrayList<String> commands){
		 
		 /*Estructura For each, mucho más rápido y eficiente
		  * for (String command : commands){}
		  * como necesitamos saber la posición del array usamos el metodo clasico
		  * y solo consideraremos las 3 primeras posiciones, y lo demás como si fuera ruido
		 */
		 boolean isFound = false;
		 
		 for (int i=0; i<commands.size(); i++)
		 {
			 if (i>2)//si es la posición 4 del array salimos
				 break;
			 
			 String command = commands.get(i);
			 
			 if (command.equalsIgnoreCase(START_VIDEO_COMMAND)){
				 Toast.makeText(this, "Start Streaming!", Toast.LENGTH_SHORT).show();
				 isFound = true;
				 break;
			 }
			 
			 if (command.equalsIgnoreCase(STOP_VIDEO_COMMAND)){
				 Toast.makeText(this, "Stop Streaming!", Toast.LENGTH_SHORT).show();
				 isFound = true;
				 break;
			 }
		 }
		 
		 if (isFound)
			 Log.d("Main","Se ha reconocido el comando");
		 else
			 Log.d("Main","No se ha reconocido el comando");
	 }
}
