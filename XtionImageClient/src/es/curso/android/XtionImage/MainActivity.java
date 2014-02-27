package es.curso.android.XtionImage;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private String TAG = getClass().getSimpleName();
	
	private Button btColor, btDepth, btIR;
	private ImageView ivXtion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btColor = (Button)this.findViewById(R.id.btColor);
		btDepth = (Button)this.findViewById(R.id.btDepth);
		btIR = (Button)this.findViewById(R.id.btIr);
		ivXtion = (ImageView) this.findViewById(R.id.ivXtion);

		btColor.setOnClickListener(getListenerImage("c"));
		btDepth.setOnClickListener(getListenerImage("d"));
		btIR.setOnClickListener(getListenerImage("i"));

	}

	private OnClickListener getListenerImage (final String mode)
	{
		
		return new OnClickListener() {
			
			Bitmap bm;
			
			@Override
			public void onClick(View v) {
				
				Thread th = new Thread(new Runnable() {
					
					@Override
					public void run() {
						 bm = getImage("http://10.15.181.26:8888/camera/xtion/?type=" + mode);
						
					}
				});
				
				th.start();
				try {
					th.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				ivXtion.setImageBitmap(bm);
				
			
			}
		};
	}
	
	
	
	
	private Bitmap getImage (String url)
	{

		try
		{
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpGet = null;

			httpGet = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpGet);

            InputStream inputStream;

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{				                                               
				inputStream = response.getEntity().getContent();
				return BitmapFactory.decodeStream(inputStream);
			}
			return null;

		}catch (IOException e) {
			Log.e(TAG,e.getMessage());
			return null;
		}
	}

	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
