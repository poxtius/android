package com.example.com.lsb.arduinoledswitch;

public class RoomControler {
	
	public RoomControler(){
		
	}

	public Boolean getStatusSwitch() throws Exception{
	
		JSONParser parser = new JSONParser();
		
		Integer value = parser.getSwitch(); //cogemos el valor del switch en le JSONParser se har‡ la descarga y parseo
											//del json que hemos descargado
		if (value == -1)					//Si es -1 levantamos excepcion
			throw new Exception("switch");
		return (value == 1);				//si no devolvemos el valor de la comparaci—n true o false
		
	}
	
	public void setLed (Boolean status){
		
		JSONParser parser = new JSONParser();
		parser.setLed(status);
		
	}
}
