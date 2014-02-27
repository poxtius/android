package com.example.intenciones;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button pgWeb = (Button) findViewById(R.id.button1);

        pgWeb.setOnClickListener(new OnClickListener() {

        	public void onClick(View view) {

        		pgWeb(view);
        	}
        }); 
        
        Button llamadaTelefono = (Button) findViewById(R.id.button2);
        llamadaTelefono.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				llamadaTelefono(v);
			}
		});
        
        Button googleMaps = (Button) findViewById(R.id.button3);
        googleMaps.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				googleMaps(v);				
			}
		});
        
        Button tomarFoto = (Button) findViewById(R.id.button4);
        tomarFoto.setOnClickListener(new OnClickListener() {

        	public void onClick(View v) {
        		tomarFoto(v);
        	}
        }); 
        
        Button mandarCorreo = (Button) findViewById(R.id.button5);
        mandarCorreo.setOnClickListener(new OnClickListener() {

        	public void onClick(View v) {
        		mandarCorreo(v);
        	}
        }); 
        
    }
    
public void pgWeb(View view) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.androidcurso.com/"));
        startActivity(intent);
 }

 public void llamadaTelefono(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:962849347"));
        startActivity(intent);
 }
 
 public void googleMaps(View view) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:41.656313,-0.877351"));
        startActivity(intent);
 }
  
 public void tomarFoto(View view) {

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivity(intent);
 }

 public void mandarCorreo(View view) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "asunto");
        intent.putExtra(Intent.EXTRA_TEXT, "texto del correo");
        intent.putExtra(Intent.EXTRA_EMAIL,  new String[] {" jtomas@upv.es" });
        startActivity(intent);
 }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
