package org.example.ejemplograficos;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

public class EjemploGraficosActivity extends Activity {
  

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(new EjemploView(this));


    }

     public class EjemploView extends View {

         private Drawable miImagen;
         
            public EjemploView (Context context) {   
                super(context);
                Resources res = context.getResources();
                miImagen = res.getDrawable(R.drawable.mi_imagen);
                miImagen.setBounds(30,30,200,200);

            }

        @Override

            protected void onDraw(Canvas canvas) {

            //Dibujar aqu’
        	miImagen.draw(canvas);
        }

    }

} 