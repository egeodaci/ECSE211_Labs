package ca.mcgill.ecse211.lab5;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class Display {
	
	private static final TextLCD lcd = LocalEV3.get().getTextLCD();
	
	/**
	 * This is the constructor
	 * 
	 */
	public Display() {
	}
	/**
	 * This method is used to display the color of the ring detected
	 */
	public static void objectDetected() {
		lcd.clear();
	}
	
}
