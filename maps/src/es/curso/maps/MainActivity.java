package es.curso.maps;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {

	//Google Map
	private GoogleMap googleMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        
        googleMap.setMyLocationEnabled(true);  //para dar permimos que vamos a querer nuestra localización
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        //Y esta segunda nos visualiza el boton de pedir la localización del dispositivo.
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//hay que sobre escribir este menú para que cuando pinchemos en el menú funcione.
	
	@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	// TODO Auto-generated method stub
    	//al detectar la pulsación del menu mira en este switch case.
		//para que esto funcione hay que generar el xml de menu que está en menu dentro de res
		//hay que modificar ese XML para tener las referencias de los menus dentro del menu
		
    	switch (item.getItemId())
    	{
    	case R.id.map_normal:
    		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    		return true;
    		
    	case R.id.map_hybrid:
    		googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    		return true;    
    		
    	case R.id.map_sattelite:
    		googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    		return true;
    		
    	case R.id.map_terrain:
    		googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    		return true;
    		
    	case R.id.my_robot:
    		
    		double latitude = 40.3223400;
    		double longitude = -3.8649600;
    		
    		//este primer comando genera una posición y luego tenemos que animarlo para que se vea en el mapa
    		CameraPosition cameraPosition = new CameraPosition.Builder().target(
    				new LatLng(latitude, longitude)).zoom(12).build();
    		//este segundo comando hace que el mapa se mueva a la posición anterior.
    		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    		//CameraUpdateFactory.newCameraPosition(cameraPosition) es un método que se hace que cameraPosition
    		//cambie a cameraUpdate que es lo que necesita el animateCamera
    
    		//Creamos el markador diciendole la latitud y la longitud y el texto que queremos que nos aparezca
    		MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Mi Robot");
    		//asignamos al marker el bitmap que va a representar y el color.
    		marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
    		//pintamos el marker en le mapa.
    		googleMap.addMarker(marker);
    		
    		return true;
    	}
    	
    	return super.onMenuItemSelected(featureId, item);
    }

}
