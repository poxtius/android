package com.proiektua.tknikbot;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements SurfaceHolder.Callback, IVideoPlayer, SensorEventListener {

	
	//Asynstask-arekin zerikusia duen aldagaia
	private final String TAG = getClass().getSimpleName();
		
	//Lehenengo activity-an kokatutako Widget guztiak definitzen ditugu
	
	private ImageView  picamera_argazkia, ps_argazkia, xtiond,xtionc,xtioni;
	private Button start_sentsoreak, aginduak, azelerometroAginduak;
	private TextView sentsore_temperatura;
	private SurfaceView surface_pi, surface_ps;
	private ImageButton aurrera, atzera, eskuinera, ezkerrera, stop, stop_1;
	
	//Raspberry Pi-ari GPIO portuan konektatu diogun tenperatura sentsoaren irakurketa TimerTask
	//baten bitartez egingo da. Honetarako SensorearenTimerTask class-ea sortu da beste fitxategi batean.
	
	private int argazki_widgeta = 0;
	private int surface_widgeta = 0;
	private boolean piStreamingStart, psStreamingStart;
	int primerPaso=0; //START SENTSOREAK botoiari sakatu zaion jakiteko.
	private final Integer TIMER_TASK_DELAY = 2000;
	
	private Timer sensorTimer= null;
    private SensorearenTimerTask sensorTimerTask= null;
    
    //Surface-k erabiltzen dituen aldagaiak
    
    public final static String VTAG = "streamingVLC/VideoActivity";

    private String mFilePath;

    private SurfaceHolder holder, holder_1;
    

    private LibVLC libvlc, libvlc_1;
    private int mVideoWidth;
    private int mVideoHeight;
    private final static int VideoSizeChanged = -1;
    private static Context mContext;
    
    ////////////////////////////////////////////////
    //
    // TexToSpeech ekin zerikusia duten aldagiak
    //
	////////////////////////////////////////////////
    
    private String AURRERA_COMMAND="adelante";
	private String GELDITU_COMMAND="stop";
	private String EZKERRA_COMMAND="izquierda";
	private String ESKUBIRA_COMMAND="derecha";
	
	protected static final int RESULT_SPEECH = 1;
	
	////////////////////////////////////////////////
	//
	//  Azelerometroekin zerikusia duten aldagiak
	//
	////////////////////////////////////////////////
	
	SensorManager sensorManager = null; 
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
	//ONCREATE
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Quitar la restricci—n de utilizar conectividad en el thread
        // principal.
		
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// obtenemos el gestor de
																		// sensores
																		// del
																		// dispositivo
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);
		
		//lehenengo pantaila edo activity-a bistaratzen dugu
		setContentView(R.layout.activity_main);
		
		//Lehenengo activity-an kokatutako Widget guztiak erreferentziatzen ditugu
		setupWidgets();
						
	}//onCreate-ren bukaera
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//ONRESUME
	//Tenperatura sentsoaren irakurketa TimerTask baten bitartez egiten dugunez kontuan hartu behar dugu
    //activity-aren bizi-zikloa, egoeraz aldatzen denean task-a geratzeko, martxan jartzeko e.a.
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	
    	super.onResume();
    	
    	if(primerPaso != 0)
    		configTimer(); //primerPaso=0 bada oraindik START SENTSOREAK botoiari ez zaiola sakatu esan nahi du,
    					  //eta ondorioz primerpaso inkrementatzen dugu onResumetik pasatzen den hurrengoan
    					  // 0 izan ez dedin 	    	                 	
    }//OnResume-ren bukaera
    ///////////////////////////////////////////////////////////////////////////////////////////////////
	
    
	////////////////////////////////////////////////////////////////////////////////////////////////////
	//ONPAUSE
	//Tenperatura sentsoaren irakurketa TimerTask baten bitartez egiten dugunez kontuan hartu behar dugu
    //activity-aren bizi-zikloa, egoeraz aldatzen denean task-a geratzeko e.a.
	@Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	
    	//Paramos la tarea
    	if (sensorTimerTask != null)
    		sensorTimerTask.cancel();
    	
    	//Purgamos las planificaciones que hemos puesto en marcha
    	if (sensorTimer != null)
    		sensorTimer.purge();
    	
    	//Streaming gelditzeko eta player-a suntsitzeko 
    	stopStreaming();
    	releasePlayer();     	
    }//onPause-ren bukaera
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	//ONDESTROY
    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyPlayer();
    }//onDestroy-en bukarea

    
	/////////////////////////////////////////////////////////////////////////////////////////////////
	//SETUPWIDGET
	//Lehengo activity-an kokatutako Widget-ak erreferentziatzeko funtzioa
	
	private void setupWidgets ()
	{
		sentsore_temperatura = (TextView) this.findViewById(R.id.sentsore_temperatura);
		
		picamera_argazkia = (ImageView) this.findViewById(R.id.piCamera_argazkia);
		
		//Robota Bideo ImageView sakatzean argazkia atera eta ikuskatuko da.
		picamera_argazkia.setOnClickListener( new OnClickListener() {		
			@Override
			public void onClick(View v) {
				
				Boolean wifi; 
				
				argazki_widgeta = 1;
				
				wifi = wifi_edo_3G();//internetera wifi bitartez konektatzen den ziurtatu
				
				if (wifi){ //wifia konektaturik badago argazkia ateratzeko prozesua martxan jartzen da!
								
					//Asyntask bat erabiliz robotaren kamarara sartuko gara. Asyntask-ak CapturePicture izena du eta beherago definitzen da.
					final CapturePicture task = new CapturePicture();				                
	                try {
	                	
	                	//Asyntask-a martxan jartzen dugu Raspberry Pi-ko ApiRest-eko helbidea pasaz
	                	task.execute(new URI("http://10.15.180.34:8000/camera/piCameraP/"), null, null);
	                	
	                } catch (URISyntaxException e) {
						Log.d(TAG, e.getMessage());
					}
				}
			}
		});
		
	//Ps Webcam2-tik argaztia ateratzea.
		ps_argazkia = (ImageView) this.findViewById(R.id.ps_argazkia);		
		ps_argazkia.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Boolean wifi;
				
				argazki_widgeta= 2;
				
				wifi = wifi_edo_3G();//internetera wifi bitartez konektatzen den ziurtatu
				
				if (wifi){ //wifia konektaturik badago argazkia ateratzeko prozesua martxan jartzen da!
								
					//Asyntask bat erabiliz robotaren kamarara sartuko gara. Asyntask-ak CapturePicture izena du eta beherago definitzen da.
					final CapturePicture task = new CapturePicture();				                
	                try {
	                	
	                	//Asyntask-a martxan jartzen dugu Raspberry Pi-ko ApiRest-eko helbidea pasaz
	                	task.execute(new URI("http://10.15.180.34:8000/camera/psEyeWebcamP/?cam=1"), null, null);
	                	
	                } catch (URISyntaxException e) {
						Log.d(TAG, e.getMessage());
					}
				}
			}
		});
		
		xtiond = (ImageView) this.findViewById(R.id.xtiond);
		xtiond.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Boolean wifi;
				
				argazki_widgeta= 3;
				
				wifi = wifi_edo_3G();//internetera wifi bitartez konektatzen den ziurtatu
				
				if (wifi){ //wifia konektaturik badago argazkia ateratzeko prozesua martxan jartzen da!
								
					//Asyntask bat erabiliz robotaren kamarara sartuko gara. Asyntask-ak CapturePicture izena du eta beherago definitzen da.
					final CapturePicture task = new CapturePicture();				                
	                try {
	                	
	                	//Asyntask-a martxan jartzen dugu Raspberry Pi-ko ApiRest-eko helbidea pasaz
	                	task.execute(new URI("http://10.15.180.34:8000/camera/xtion/?type=d"), null, null);
	                	
	                } catch (URISyntaxException e) {
						Log.d(TAG, e.getMessage());
					}
				}
				
			}
		});
		
		xtionc = (ImageView) this.findViewById(R.id.xtionc);
		xtionc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Boolean wifi;
				
				argazki_widgeta= 4;
				
				wifi = wifi_edo_3G();//internetera wifi bitartez konektatzen den ziurtatu
				
				if (wifi){ //wifia konektaturik badago argazkia ateratzeko prozesua martxan jartzen da!
								
					//Asyntask bat erabiliz robotaren kamarara sartuko gara. Asyntask-ak CapturePicture izena du eta beherago definitzen da.
					final CapturePicture task = new CapturePicture();				                
	                try {
	                	
	                	//Asyntask-a martxan jartzen dugu Raspberry Pi-ko ApiRest-eko helbidea pasaz
	                	task.execute(new URI("http://10.15.180.34:8000/camera/xtion/?type=c"), null, null);
	                	
	                } catch (URISyntaxException e) {
						Log.d(TAG, e.getMessage());
					}
				}
				
			}
		});
		
		xtioni = (ImageView) this.findViewById(R.id.xtioni);
		xtioni.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Boolean wifi;
				
				argazki_widgeta= 5;
				
				wifi = wifi_edo_3G();//internetera wifi bitartez konektatzen den ziurtatu
				
				if (wifi){ //wifia konektaturik badago argazkia ateratzeko prozesua martxan jartzen da!
								
					//Asyntask bat erabiliz robotaren kamarara sartuko gara. Asyntask-ak CapturePicture izena du eta beherago definitzen da.
					final CapturePicture task = new CapturePicture();				                
	                try {
	                	
	                	//Asyntask-a martxan jartzen dugu Raspberry Pi-ko ApiRest-eko helbidea pasaz
	                	task.execute(new URI("http://10.15.180.34:8000/camera/xtion/?type=i"), null, null);
	                	
	                } catch (URISyntaxException e) {
						Log.d(TAG, e.getMessage());
					}
				}
				
			}
		});
		
		 //mFilePath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/VID_20131226_195102.mp4";
        mContext = this;
        
      
        surface_pi = (SurfaceView) this.findViewById(R.id.surface_pi);
        holder = surface_pi.getHolder();
        holder.addCallback(this);
        
        surface_pi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				surface_widgeta = 1;
				piStreamingStart=true;
				MyTask task = new MyTask();
				task.execute();
			}
		});
        surface_pi.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				surface_widgeta = 1;
				piStreamingStart=false;
				stopStreaming();
                releasePlayer(); // para terminar de liberar cosas de
                                    // VLC y surface
                return true;
			}
		});
        
        surface_ps = (SurfaceView) this.findViewById(R.id.surface_ps);
        holder_1 = surface_ps.getHolder();
        holder_1.addCallback(this);
        
        surface_ps.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyTask task = new MyTask();
				psStreamingStart = true;
				surface_widgeta = 2;
				task.execute();
			}
		});
        surface_ps.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				surface_widgeta = 2;
				psStreamingStart  = false;
				stopStreaming();
                releasePlayer(); // para terminar de liberar cosas de
                                    // VLC y surface
                return true;
			}
		});
		
		//START SENTSOREAK botoiari ematean hasiko gara Tenperatura ikusten.
		start_sentsoreak = (Button) this.findViewById(R.id.start_sentsoreak);
		start_sentsoreak.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				configTimer(); //Tenperatura sensorearen Timer-a konfiguratzeko funtzioari deitzen zaio.
				primerPaso++;  //primerPaso inkrementazen da 0-ren ezberdina izan dadin eta onResume-tik pasako
							   //balitz aktibo zegoela jakiteko.
			}
		});
		
		aurrera = (ImageButton) this.findViewById(R.id.aurrera);
		aurrera.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
			doGetPetition("http://10.15.180.34:8000/robota/mugimenduak/?state=au");
			}
		});
		
		atzera = (ImageButton) this.findViewById(R.id.atzera);
		atzera.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
			doGetPetition("http://10.15.180.34:8000/robota/mugimenduak/?state=at");
				
			}
		});
		
		eskuinera = (ImageButton) this.findViewById(R.id.eskuinera);
		eskuinera.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
			doGetPetition("http://10.15.180.34:8000/robota/mugimenduak/?state=es");
				
			}
		});
		
		ezkerrera = (ImageButton) this.findViewById(R.id.ezkerrera);
		ezkerrera.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				doGetPetition("http://10.15.180.34:8000/robota/mugimenduak/?state=ez");
				
			}
		});
		
		stop = (ImageButton) this.findViewById(R.id.stop);
		stop.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
			doGetPetition("http://10.15.180.34:8000/robota/mugimenduak/?state=g");
				
			}
		});
		stop_1 = (ImageButton) this.findViewById(R.id.stop_1);
		stop_1.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
			doGetPetition("http://10.15.180.34:8000/robota/mugimenduak/?state=g");
				
			}
		});
		
		aginduak = (Button) this.findViewById(R.id.aginduak);
		aginduak.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//el reconocimiento de voz se hará mediante un intent 
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");//definir lenguaje de reconocimiento

                try {                	
                	
                    startActivityForResult(intent, RESULT_SPEECH);//lanzamos el intent que hemos configurado para RECOGNIZE_SPEECH
                   
                } catch (ActivityNotFoundException a) {
                	
                	
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
			}
		});

		//Aelerometroa martxan jartzeko registratu behar da!!!!
		azelerometroAginduak = (Button) this.findViewById(R.id.azelerometroAginduak);
		azelerometroAginduak.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				registratuAzelerometroa();
			}
		});
		
		azelerometroAginduak.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				
				desregistratuAzelerometroa();
				return true;
			}
		});
		
	}//setupWidgets-en bukaera
	/////////////////////////////////////////////////////////////////////////////////////////////////////	



	////////////////////////////////////////////////////////////////////////////////////////////////////
	//WIFI_EDO_3G
	//Irudiak eta bideoak transmititu behar ditugunez komenigarria da gailua internetera Wifi edo 3G bitartez
	//konektatzen den jakitea. 3G bada abixua pasako zaio erabiltzaileari wifia aktibatzeko.
    private boolean wifi_edo_3G(){

    	final ConnectivityManager conMan = (ConnectivityManager) 
    			getSystemService(Context.CONNECTIVITY_SERVICE);
    	
    	final State wifi = conMan.getNetworkInfo(1).getState();
    	    	    	
    	if (wifi != NetworkInfo.State.CONNECTED){
    		
    		//Alertdialog baten bitartez abixatu!
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Informazioa");
            builder.setMessage("Aukera hau erabiltzeko Wifi-ra konektatu mesedez!");
            builder.setPositiveButton("OK",null);
            builder.create();
            builder.show();
            
            return false;
    	}
    	else
    		return true;
    }//wifi_edo_3G-ren bukaera
    /////////////////////////////////////////////////////////////////////////////////////////////////////
	
    
        
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	//CAPTUREPICTURE ASYNCTASK
	//Raspberry Pi-aren kameratik argazkia ateratzeko asynctask-a
	//ApiRest batera konektatu behar dugunez Asynctask-aren lehenengo argumentua
	//URI motakoa da; bigarrena Int motakoa progressDialog-ari Int motatako datuak
	//pasa behar dizkiogulako progresoa ikusteko; eta azkenik DoInbackground-ek Bitmap motako
	//aldagaia bueltatu behar duenez kamararen irudiarekin hirugarren argumentua Bitmap motakoa da. Modu honetan onPostexecute-k
	//result bere argumentuan jaso dezake irudia eta UI-ra bidali. Gogoratu DoInBackground-ek ezin duela UI-arekin elkarreragin, bestek bai.
	
	public class CapturePicture extends AsyncTask<URI, Integer, Bitmap>{

		
		ProgressDialog pd = null;
		int progress=0;
		
		private final String TAG = getClass().getSimpleName();
		
		private String urlApiRest;
		
		protected void onPreExecute()
		{
			// Argazkia ateratzen ari den bitartean progressDialog bat erakutsiko dugu
			pd = new ProgressDialog(MainActivity.this);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setTitle("ProgressDialog");
			pd.setMessage("Argazkia ateratzen ...");
			pd.show();
		}
		
		//ApiRest-aren konektatuko gara,argazkia atera eta bukaeran Bitmap motako aldagaia bultatuko dugu
		//lortutako irudiarekin. progressDialog-a 10 aldiz eguneratuko dugu 0%, 10%, 20%,...100% ikusteko
		@Override
		protected Bitmap doInBackground(final URI... uris) {
			
			//progessDialog-a eguneratzen joateko funtzioa honi deitzen zaio. Honek era berean
	        //onProgressUpdate-i deituko dio.
			//Hemen %10 erakutsiko da
			progress=progress+10;
			publishProgress(progress);
			
			urlApiRest = uris[0].toString();
			
			//progessDialog-an %20 erakutsi
			progress=progress+10;
			publishProgress(progress);
			
			Log.d(TAG, urlApiRest);
			
			try{
				DefaultHttpClient httpclient = new DefaultHttpClient();
				
				//progessDialog-an %30 erakutsi
				progress=progress+10;
				publishProgress(progress);
				
				HttpGet  httpGet = null;
				
				//progessDialog-an %40 erakutsi
				progress=progress+10;
				publishProgress(progress);
				
				httpGet = new HttpGet(urlApiRest);
				
				//progessDialog-an %50 erakutsi
				progress=progress+10;
				publishProgress(progress);
				
				HttpResponse response = httpclient.execute(httpGet);
				
				//progessDialog-an %60 erakutsi
				progress=progress+10;
				publishProgress(progress);
				
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				{
					//progessDialog-an %70 erakutsi
					progress=progress+10;
					publishProgress(progress);
					
					InputStream inputStream = response.getEntity().getContent(); //lo pasamos a tipo de objeto InpuStrean
					
					//progessDialog-an %80 erakutsio
					progress=progress+10;
					publishProgress(progress);
					
					Bitmap argazkia = BitmapFactory.decodeStream(inputStream); //lo convierto a tipo Bitmap
					
					//progessDialog-an %90 erakutsi
					progress=progress+10;
					publishProgress(progress);
					
					return argazkia; //lortutako irudia bueltatzen dugu
					
				}
				else
					return null;
			}
			catch (IOException e) {
				Log.e("Main",e.getMessage());
				return null;
			}
		}

		@Override
		protected void onProgressUpdate(final Integer... progress) {
	       
			pd.setProgress(progress[0]); //progessDialog-a eguneratu
	        
	    }
		
		@Override
	    protected void onPostExecute(Bitmap result)
	    {
	    	//doInbackground-ek bueltatzen duen Bitmap motako aldagaia jasotzen dugu argazkia aldagai berrian
	    	Bitmap argazkia=result;
	    	
	    	//progessDialog-an %100 erakutsi
			progress=progress+10;
			publishProgress(progress);
	    	
	    	//irudia ikusiko den widget-era bidali baino lehen, ziurtatu egiten dugu irudia lortu
			//dugula, bestela Toast baten bitartez errore bat egon dela azaltzen dugu
	    	if (argazkia!= null){
	    		if (argazki_widgeta == 1)
	    			picamera_argazkia.setImageBitmap(argazkia);
	    		if (argazki_widgeta == 2)
	    			ps_argazkia.setImageBitmap(argazkia);
	    		if (argazki_widgeta == 3)
	    			xtiond.setImageBitmap(argazkia);
	    		if (argazki_widgeta == 4)
	    			xtionc.setImageBitmap(argazkia);
	    		if (argazki_widgeta == 5)
	    			xtioni.setImageBitmap(argazkia);
	    		pd.dismiss();//progessDialog-a ezabatzen dugu.
		    	Toast.makeText(MainActivity.this,"Irudia jeitsita", Toast.LENGTH_LONG).show();
	    	}
	    	else
				Toast.makeText(MainActivity.this, "Error while image is loaded", Toast.LENGTH_SHORT).show();
	    }
			
	}//Asynctask-aren bukaera
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	//CONFIGTIMER
	//Sensorearen irakurketa konfiguratuko duen funtzioa
	
	private void configTimer(){
		
		if (sensorTimer == null && sensorTimerTask == null){
	    	
			sensorTimer = new Timer();//creamos el objeto Timer para poder programar la taeea
            
			sensorTimerTask = new SensorearenTimerTask();//creamos un objeto que hemos definido en la clase miTimertask
            
			sensorTimerTask.setContext(this);//le pasamos el contexto de la actividad
            
			sensorTimer.schedule(sensorTimerTask, TIMER_TASK_DELAY, SensorearenTimerTask.TIMER_TASK_PERIOD);
		}
		
	}//configTimer funtzioaren bukaera
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	//SENSOREARENTIMERTASK TIMERTASK-a
	
	public class SensorearenTimerTask extends TimerTask{

		public static final long TIMER_TASK_PERIOD = 5000;
		Context mContext;
		
		//necesitamos el contexto de la actividad principal
		public void setContext (Context ctx){
			
			mContext= ctx;
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		//RUN
		
		@Override
		public void run() {
			
			//con el metodo runOnUiThread conseguimos que podamos pintar en la actividad principal, en este caso un toast
			((Activity) mContext).runOnUiThread(new Runnable() {
				
				public void run() {
					
					//////////////////////////////////////////////////////////////////////////
					//LECTURA DEL SENSOR EXTERNO que hemos conectado a la raspberry
					
					//getresponse funtzioari esker tenperaturaren balio lortzen dugu string batean
					String strJSON = getResponse("http://10.15.180.34:1111/sensors/temp/ds/");
					
					try {
					JSONObject json = new JSONObject(strJSON);
					
					if (json.has("temp")) //miramos si en el objeto json si hay una clave que se llama temp, que es como lo hemos definido en el API Rest
					{
					String temp=json.getString("temp");
					sentsore_temperatura.setText("Temp sensor exterior: " + temp + "ºC");
					}
					}
					catch (JSONException e){
					e.printStackTrace();
					}
					//Toast.makeText(mContext,"Kaixo",Toast.LENGTH_SHORT).show();	
				}
			});
			
		}//Run funtzioaren bukaera
		////////////////////////////////////////////////////////////////////////////////////////

		////////////////////////////////////////////////////////////////////////////////////////
		//GETRESPONSE FUNTZIOA
		//Funtzio honek tenperatura sensorearen balioa bueltatuko digu.
		
		private String getResponse (String url){
			
			try{
				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpGet  httpGet = null;
				
				httpGet = new HttpGet(url);
				HttpResponse response = httpclient.execute(httpGet);
				
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				{
					return EntityUtils.toString(response.getEntity());
				}
				else
					return null;
			}
			catch (IOException e) {
				Log.e("Main",e.getMessage());
				return null;
			}
			
		}//Getresponse funtzioaren bukaera
		////////////////////////////////////////////////////////////////////////////////////////

	}//SensorearenTimerTask class-aren bukaera
	////////////////////////////////////////////////////////////////////////////////////////////////////////

/***************************************************************
 * 
 * Surfacerekin zerikusia duten funtzioak hemendik aurrera joango dira
 * 
 ***************************************************************/
	
	   public class MyTask extends AsyncTask<Void, Void, Void>
	    {
	    	ProgressDialog pd;
	    	String mStringUrl;
	    	
	    	@Override
	    	protected void onPreExecute() {
	    		// TODO Auto-generated method stub
	    		super.onPreExecute();
	    		
	    		 pd = ProgressDialog.show(mContext, "Start Streaming", "Start Streaming");
	    	}

			@Override
			protected Void doInBackground(Void... params) {
				if (surface_widgeta==1)
					{
					String jsonStr = doGetPetition("http://10.15.180.34:8000/camera/piCameraS/start");
					try {
						JSONObject json = new JSONObject(jsonStr);
						if (json.has("streaming_url"))
							mStringUrl = json.getString("streaming_url");
					
						Thread.sleep(6000);
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					surface_widgeta = 0;
					}
				if (surface_widgeta==2)
					{
					String jsonStr = doGetPetition("http://10.15.180.34:8000/camera/psEyeWebcamS0/?state=on");
					try {
						JSONObject json = new JSONObject(jsonStr);
						if (json.has("streaming_url"))
							mStringUrl = json.getString("streaming_url");
						
						Thread.sleep(8000);
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					surface_widgeta = 0;
					}
				return null;
			}
	    	
			@Override
			protected void onPostExecute(Void result) {
				if (pd.isShowing())
					pd.cancel();
				
				if (piStreamingStart)
					createPlayer(LibVLC.PathToURI(mStringUrl));
				if (psStreamingStart)
					createPlayer_2(LibVLC.PathToURI(mStringUrl));
				super.onPostExecute(result);
			}
	    	
	    	
	    }
	    
	    
	    private void stopStreaming() {
	    	
			 Thread th = new Thread(new Runnable() {
				
				@Override
				public void run() {
					if (surface_widgeta == 1)
						doGetPetition("http://10.15.180.34:8000/camera/piCameraS/stop/");	
					if (surface_widgeta == 2)
						doGetPetition("http://10.15.180.34:8000/camera/psEyeWebcamS0/?state=off");
					surface_widgeta = 0;
				}
			});
			 
			 th.start();
			 
			 try {
				th.join(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	    
	    
	    private String doGetPetition (String url)
	    {        
	            try
	            {
	                    DefaultHttpClient httpclient = new DefaultHttpClient();
	                    HttpGet httpGet = null;

	                    httpGet = new HttpGet(url);
	                    HttpResponse response = httpclient.execute(httpGet);
	                    HttpEntity entity = response.getEntity();

	                    String str = EntityUtils.toString(entity);
	                   
	                    return str;

	            }catch (IOException e) {
	                    Log.e("doGet",e.getMessage());
	                    return null;
	            }      
	    }
	    
	    

	    @Override
	    public void onConfigurationChanged(Configuration newConfig) {  //es la funci—n que se llama cuando cambia de 
	    	//orientaci—n se llama a setSize que es la funci—n que se encarga de cambiar el tama–o.
	        super.onConfigurationChanged(newConfig);
	        setSize(mVideoWidth, mVideoHeight);
	    }
	    
/*************
* Surface
*************/
	    
	    
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub		
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder surfaceholder, int format,
            int width, int height) {
        if (libvlc != null)
        	libvlc.attachSurface(holder.getSurface(), this);
        if (libvlc_1 != null)
         	libvlc_1 .attachSurface(holder_1.getSurface(), this);   
     }
        


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub		
	}
	
	private void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        // get screen size
        int w = getWindow().getDecorView().getWidth();
        int h = getWindow().getDecorView().getHeight();

        // getWindow().getDecorView() doesn't always take orientation into
        // account, we have to correct the values
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        // force surface buffer size
        holder.setFixedSize(mVideoWidth, mVideoHeight);
        holder_1.setFixedSize(mVideoWidth, mVideoHeight);
        // set display size
        if (piStreamingStart){
        	LayoutParams lp = surface_pi.getLayoutParams();
        	lp.width = w;
        	lp.height = h;
        	///GOGORATU HAU BEIDATZEAZ EZ DAGO ONDO
        	surface_pi.setLayoutParams(lp);
        	surface_pi.invalidate();
        }
        if (psStreamingStart){
        	LayoutParams lp = surface_ps.getLayoutParams();
        	lp.width = w;
        	lp.height = h;
        	///GOGORATU HAU BEIDATZEAZ EZ DAGO ONDO
        	surface_ps.setLayoutParams(lp);
        	surface_ps.invalidate();
        }
	}
	
	@Override
    public void setSurfaceSize(int width, int height, int visible_width,
            int visible_height, int sar_num, int sar_den) {
        Message msg = Message.obtain(mHandler, VideoSizeChanged, width, height);
        msg.sendToTarget();
    }
	
    /*************
     * Player
     *************/

    private void createPlayer(String media) {
        //releasePlayer();
        

        if (media.length() > 0) {
        	Toast toast = Toast.makeText(this, media, Toast.LENGTH_LONG);
        	toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
        			0);
        	toast.show();
        }

        // Create a new media player
        try {
        	libvlc = LibVLC.getInstance();
        } catch (LibVlcException e) {
        	Toast.makeText(this, "Can't create player", Toast.LENGTH_LONG).show();
        	return;
        }
        libvlc.setIomx(false);
        libvlc.setSubtitlesEncoding("");
        libvlc.setAout(LibVLC.AOUT_OPENSLES);
        libvlc.setTimeStretching(true);
        libvlc.setChroma("RV32");
        libvlc.setVerboseMode(true);
        LibVLC.restart(this);
        EventHandler.getInstance().addHandler(mHandler);
        if (holder == null)
       		Log.d(VTAG, "holder==null");
       	holder.setFormat(PixelFormat.RGBX_8888);
       	holder.setKeepScreenOn(true);
        MediaList list = libvlc.getMediaList();
        list.clear();
        list.add(new Media(libvlc, LibVLC.PathToURI(media)), false);
        libvlc.playIndex(0);

    }
    
    private void createPlayer_2(String media) {
        //releasePlayer();
        

        if (media.length() > 0) {
        	Toast toast = Toast.makeText(this, media, Toast.LENGTH_LONG);
        	toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
        			0);
        	toast.show();
        }

        // Create a new media player
        try {
        	libvlc_1 = LibVLC.getInstance();
        } catch (LibVlcException e) {
        	Toast.makeText(this, "Can't create player", Toast.LENGTH_LONG).show();
        	return;
        }
        libvlc_1.setIomx(false);
        libvlc_1.setSubtitlesEncoding("");
        libvlc_1.setAout(LibVLC.AOUT_OPENSLES);
        libvlc_1.setTimeStretching(true);
        libvlc_1.setChroma("RV32");
        libvlc_1.setVerboseMode(true);
        LibVLC.restart(this);
        EventHandler.getInstance().addHandler(mHandler_1);
        if (holder_1 == null)
       		Log.d(VTAG, "holder_1==null");
       	holder_1.setFormat(PixelFormat.RGBX_8888);
       	holder_1.setKeepScreenOn(true);
        MediaList list = libvlc_1.getMediaList();
        list.clear();
        list.add(new Media(libvlc_1, LibVLC.PathToURI(media)), false);
        libvlc_1.playIndex(0);

    }
 
    private void releasePlayer() {  //esto para el vlc a nivel local de la aplicaci—n.
        if (libvlc == null && libvlc_1 == null)
            return;
        if (!piStreamingStart){
        	EventHandler.getInstance().removeHandler(mHandler);
        	libvlc.stop();
        	}
        if (!psStreamingStart){
        	EventHandler.getInstance().removeHandler(mHandler_1);
        	libvlc_1.stop();
        	}
    }
    
    private void destroyPlayer(){    //destruye el vlc cuando salimos de la aplicaci—n.
  	  if (libvlc == null && libvlc_1 == null)
            return;
  	  if (libvlc!=null){  //honek libvlc-a akatzen du guztiz
  		  EventHandler.getInstance().removeHandler(mHandler);
  		  libvlc.stop();
  		  libvlc.detachSurface();
  		  holder = null;
  		  libvlc.closeAout();
  		  libvlc.destroy();
  		  libvlc = null;

  		  mVideoWidth = 0;
  		  mVideoHeight = 0;
  	  	}
   
   	 if (libvlc_1!=null){   //honek libvlc_1-a akatzen du guztiz
        EventHandler.getInstance().removeHandler(mHandler_1);
        libvlc_1.stop();
   	    libvlc_1.detachSurface();
        holder_1 =null;
        libvlc_1.closeAout();
        libvlc_1.destroy();
        libvlc_1 = null;

        mVideoWidth = 0;
        mVideoHeight = 0;
   	  }
    }

    /*************
     * Events
     *************/

    private Handler mHandler = new MyHandler(this);
    private Handler mHandler_1 = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> mOwner;

        public MyHandler(MainActivity owner) {
            mOwner = new WeakReference<MainActivity>(owner);
        }

        @Override
        public void handleMessage(Message msg) {
        	MainActivity player = mOwner.get();

        	// SamplePlayer events
        	if (msg.what == VideoSizeChanged) {
        		player.setSize(msg.arg1, msg.arg2);
        		return;
        	}

        	// Libvlc events
        	Bundle b = msg.getData();
        	switch (b.getInt("event")) {
        	
        	case EventHandler.MediaPlayerEndReached:
        		player.releasePlayer();
        		break;
        		
        	case EventHandler.MediaPlayerPlaying:        	
        	case EventHandler.MediaPlayerPaused:
        	case EventHandler.MediaPlayerStopped:
        	default:
        		break;
        	}
        }
    }

///////////////////////////////////////////////////////
//
//  Ahots komandoekin lan egitko.
//
//////////////////////////////////////////////////////    
    
	//Recogemos el resultado del intent que será un array con las frases reconocidas	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode) {
        case RESULT_SPEECH: {
            if (resultCode == RESULT_OK && null != data) {
 
                ArrayList<String> results = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
 
                detectCommand(results);
            }
            break;
        }
 
        }
    }

	//funci—n que hemos definido para gestionar lo que devuelve el reconocedor
	private void detectCommand(ArrayList<String> commands) {
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
			 
			 if (command.equalsIgnoreCase(AURRERA_COMMAND)){
				 
				 boolean wifi = wifi_edo_3G();
				 if (wifi){
					 doGetPetition("http://10.15.180.34:8000/robota/mugimenduak/?state=au");
				 }
				 Log.d("Mugimendua", "aurrera");
				 isFound = true;
				 break;
			 }
			 
			 if (command.equalsIgnoreCase(GELDITU_COMMAND)){
				 boolean wifi = wifi_edo_3G();
				 if (wifi){
					 doGetPetition("http://10.15.180.34:8000/robota/mugimenduak/?state=g");
				 }
				 Log.d("Mugimendua", "aurrera");
				 isFound = true;
				 break;
			 }
			 
			 if (command.equalsIgnoreCase(ESKUBIRA_COMMAND)){
				 boolean wifi = wifi_edo_3G();
				 if (wifi){
					 doGetPetition("http://10.15.180.34:8000/robota/mugimenduak/?state=es");
				 }
				 Log.d("Mugimendua", "aurrera");
				 isFound = true;
				 break;
			 }
			 
			 if (command.equalsIgnoreCase(EZKERRA_COMMAND)){
				 boolean wifi = wifi_edo_3G();
				 if (wifi){
					 doGetPetition("http://10.15.180.34:8000/robota/mugimenduak/?state=ez");
				 }
				 Log.d("Mugimendua", "aurrera");
				 isFound = true;
				 break;
			 }
		 }
		 
		 if (isFound)
			 Log.d("Main","Se ha reconocido el comando");
		 else
			 Log.d("Main","No se ha reconocido el comando");
	 }
	
	
	//Azelerometroarekin zerikusia duten funtzioak!!!!
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {

			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:

				// AURRERA
				// aurrera x=0 y=0 z=10 (x 0 inguruan dagoela, z 10erantz eta y
				// 0rantz doazela azeleratzen da) => geratzeko baldintza -2<x<2,
				// y<4, z>7
				if (event.values[0] > -2 && event.values[0] < 2
						&& event.values[1] < 4 && event.values[2] > 7) {
					Boolean wifi;

					wifi = wifi_edo_3G();

					if (wifi) {
						Log.d(TAG, "aurrera");
						doGetPetition("http://10.15.180.34:8000/robota/mugimenduak/?state=au");
					}
				}
				// ATZERA
				// atzera x=0 y=0 z=-9 (x 0 inguruan dagoela, z -10erantz eta y
				// 0rantz doazela azeleratzen da) => geratzeko baldintza -2<x<2,
				// y<4, z<-7
				if (event.values[0] > -2 && event.values[0] < 2
						&& event.values[1] < 4 && event.values[2] < -7) {
					Boolean wifi;

					wifi = wifi_edo_3G();

					if (wifi) {
						Log.d(TAG, "atzera");
						doGetPetition("http://10.15.180.34:8000/robota/mugimenduak/?state=at");
					}
				}
				// ESKUBIRA
				// eskubira x=-10 y=0 z=0 (z 0 inguruan dagoela, x -10erantz eta
				// y 0rantz doazela azeleratzen da) => geratzeko baldintza x<-7,
				// y<4, -2<z<2
				if (event.values[0] < -7 && event.values[1] < 4
						&& event.values[2] > -2 && event.values[2] < 2) {
					Boolean wifi;

					wifi = wifi_edo_3G();

					if (wifi) {
						Log.d(TAG, "eskubira");
						doGetPetition("http://10.15.180.34:8000/robota/mugimenduak/?state=es");
					}
				}
				// EZKERRERA
				// ezkerrera x=10 y=0 z=0 (z 0 inguruan dagoela, x 10erantz eta
				// y 0rantz doazela azeleratzen da) => geratzeko baldintza x>7,
				// y<4, -2<z<2
				if (event.values[0] > 7 && event.values[1] < 4
						&& event.values[2] > -2 && event.values[2] < 2) {
					Boolean wifi;

					wifi = wifi_edo_3G();

					if (wifi) {
						Log.d(TAG, "ezkerrera");
						doGetPetition("http://10.15.180.34:8000/robota/mugimenduak/?state=ez");
					}
				}
				// GELDITU
				// geratzeko baldintza -2<x<2, y>8, -2<z<2
				if (event.values[0] > -2 && event.values[0] < 2
						&& event.values[1] > 8 && event.values[2] > -2
						&& event.values[2] < 2) {
					Boolean wifi;

					wifi = wifi_edo_3G();

					if (wifi) {
						Log.d(TAG, "gelditu");
						doGetPetition("http://10.15.180.34:8000/robota/mugimenduak/?state=g");
					}
				}

				break;

			}
		}
	}   
	
	protected void registratuAzelerometroa() {	
		Log.d("Azelerometroa", "Ha llegado");
		if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER) != null){
			Log.d("Azelerometroa", "Ha llegado_1");
			sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL); //resistramos el aceler—metro. A partir de ahora los eventos
														//que se den en el aceler—metro nos llevaran a la funcion
														//onSensorChanged
			Toast toast = Toast.makeText(this, "Azelerometroa registratuta", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
		}else{
			Toast toast = Toast.makeText(this, "Azelerometrorik ez", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
		}
	}
	
	protected void desregistratuAzelerometroa() {
		if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)!=null){
			sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
			Toast toast = Toast.makeText(this, "Azelerometroa desregistratuta", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
		}
	}

 
}//Activity-aren bukaera
	

