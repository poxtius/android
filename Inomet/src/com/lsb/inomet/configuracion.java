package com.lsb.inomet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class configuracion extends Activity{

	protected static final int OK_RESULT_CODE = 1;
	private Button dirOKestacion;
	private EditText direccionWeb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configuracion);
		
		direccionWeb = (EditText) findViewById(R.id.editText1);
		direccionWeb.setText("10.123.5.43");

		dirOKestacion = (Button) findViewById(R.id.button1);
		dirOKestacion.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String prueba = null;
				prueba = direccionWeb.getText().toString(); 
				Log.d( "Config va a devolver: ", prueba);
				Intent intent = new Intent(configuracion.this, Inomet.class);
				intent.putExtra("DirEstacion", prueba);
				setResult(OK_RESULT_CODE, intent);
			    finish();			
			}
		});
		
	}
}
