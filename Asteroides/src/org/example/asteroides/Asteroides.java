package org.example.asteroides;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Asteroides extends Activity {

	public static AlmacenPuntuaciones almacen = new AlmacenPuntuacionArray();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button bConfigurar = (Button) findViewById(R.id.button2);
        bConfigurar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				lanzarPreferencias(null);
			}
		});
        
        Button bAcercaDe = (Button) findViewById(R.id.button3);
        bAcercaDe.setOnClickListener(new OnClickListener() {

        	public void onClick(View view) {

        		lanzarAcercaDe(null);
        	}
        }); 
        
        Button bPuntuar = (Button) findViewById(R.id.button4);
        bPuntuar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				lanzarPuntuaciones(null);
			}
		});
        
        Button bSalir = (Button) findViewById(R.id.button5);
        bSalir.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
        Button bJuego = (Button) findViewById(R.id.button1);
        bJuego.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				lanzarJuego(null);
			}
		});
    }

    public void lanzarJuego(View view){

    	Intent i = new Intent(this, Juego.class);
    	startActivity(i);
    }
    
    public void lanzarAcercaDe(View view){

    	Intent i = new Intent(this, AcercaDe.class);
    	startActivity(i);
    }
    
    public void lanzarPreferencias(View view){

    	Intent i = new Intent(this, Preferencias.class);
    	startActivity(i);
    }
    
    public void lanzarPuntuaciones(View view) {

    	Intent i = new Intent(this, Puntuaciones.class);
    	startActivity(i);

    }
    
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true; /** true -> el menœ ya est‡ visible */
     }
    
     @Override public boolean onOptionsItemSelected(MenuItem item) {
              switch (item.getItemId()) {
              case R.id.acercaDe:
                     lanzarAcercaDe(null);
                     break;
              case R.id.config:
            	  	lanzarPreferencias(null);
            	  	break;
              }
              return true; /** true -> consumimos el item, no se propaga*/
     }
}
