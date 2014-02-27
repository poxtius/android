package org.example.asteroides;

import java.util.Vector;

public class AlmacenPuntuacionArray implements AlmacenPuntuaciones{
	
	 private Vector<String> puntuaciones;

     public AlmacenPuntuacionArray() {

          puntuaciones= new Vector<String>();

          puntuaciones.add("123000 Pepito Domingez");

          puntuaciones.add("111000 Pedro Martinez");

          puntuaciones.add("011000 Paco PŽrez");

     }

	public void guardarPuntuacion(int puntos, String nombre, long fecha) {
		// TODO Auto-generated method stub
		puntuaciones.add(0, puntos + " "+ nombre);
		
	}

	public Vector<String> listaPuntuaciones(int cantidad) {
		// TODO Auto-generated method stub
		return puntuaciones;
	}

}
