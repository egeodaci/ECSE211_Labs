package ca.mcgill.ecse211.lab5;

import ca.mcgill.ecse211.localizers.*;
import ca.mcgill.ecse211.odometer.*;
import ca.mcgill.ecse211.searcher.Navigation;
import ca.mcgill.ecse211.searcher.Search;
import ca.mcgill.ecse211.sensors.UltrasonicPoller;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;


public class lab5 {
	
	 // Motor objects and robot parameters
	private static final Port usPort = LocalEV3.get().getPort("S1");
	private static final EV3LargeRegulatedMotor leftMotor =
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor =
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

 
	public static final double WHEEL_RAD = 2.2;
	public static final double TRACK = 11.3;
	
	
	 
	public static void main(String[] args) throws OdometerExceptions {
		
		Odometer odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD); 
		Navigation navigator = new Navigation(leftMotor, rightMotor);
		Search tracker = new Search(navigator, leftMotor, rightMotor);
		UltrasonicLocalizer uLocalizer = new UltrasonicLocalizer(leftMotor, rightMotor);
		LightLocalizer lLocalizer = new LightLocalizer(leftMotor, rightMotor);
		 
		@SuppressWarnings("resource")
		SensorModes usSensor = new EV3UltrasonicSensor(usPort); 
		SampleProvider usDistance = usSensor.getMode("Distance");
		float[] usData = new float[usDistance.sampleSize()];
		
		

		
		//Start odometer and sensor threads
		Thread odoThread = new Thread(odometer);
		odoThread.start();
		Thread usPoller = new UltrasonicPoller(usDistance, usData, uLocalizer);
		usPoller.start();
		
		
		//Start field trial
		Button.waitForAnyPress();
		
		//Start ultrasonic localizer thread and wait for it to finish
		uLocalizer.start();
		try {
			uLocalizer.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Start light localizer and wait for it to finish
		lLocalizer.start();
		try {
			lLocalizer.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		tracker.start();
		
		
		 
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);	 
		
		
	 }
}
