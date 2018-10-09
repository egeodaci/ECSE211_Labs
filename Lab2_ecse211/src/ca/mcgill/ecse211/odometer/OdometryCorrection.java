/*
 * OdometryCorrection.java
 */
package ca.mcgill.ecse211.odometer;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class OdometryCorrection implements Runnable {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	
	
	private static final SensorModes colorSensor = new EV3ColorSensor(LocalEV3.get().getPort("S1"));
	SampleProvider colorSample = colorSensor.getMode("Red");
	static float[] colorData = new float[colorSensor.sampleSize()];

	/**
	 * This is the default class constructor. An existing instance of the odometer
	 * is used. This is to ensure thread safety.
	 * 
	 * @throws OdometerExceptions
	 */
	public OdometryCorrection() throws OdometerExceptions {
		this.odometer = Odometer.getOdometer();
	}

	/////////////////////////// TODO ///////////////////////////
	////////////////////////////////////////////////////////////

	// implement the correction design, so for example when you run across a line
	// it changes the value of for example the y so that it is at that value again.
	/**
	 * Here is where the odometer correction code should be run.
	 * 
	 * @throws OdometerExceptions
	 */
	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		int nLines = 0;
		boolean onTopOfLine = false;




		while (true) {
			correctionStart = System.currentTimeMillis();
			colorSample.fetchSample(colorData, 0);

			// TODO Trigger correction (When do I have information to correct?)

			if (colorData[0] < .18 && !onTopOfLine) {
				Sound.beep();
				nLines++;
				onTopOfLine = true;
				double X, Y, Theta;

				// TODO Calculate new (accurate) robot position
				if (0 < nLines && nLines <= 3) {
					X = 0;
					Y = ((nLines - 0) * 30.48) - 15.24;
					Theta = 0;
				} else if (3 < nLines && nLines <= 6) {
					X = ((nLines - 3) * 30.48) - 15.24;
					Y = 91.44;
					Theta = 90;
				} else if (6 < nLines && nLines <= 9) {
					X = 91.44;
					Y = (91.44 - (nLines - 7) * 30.48) - 15.24;
					Theta = 180;
				} else {
					X = (91.44 - (nLines - 10) * 30.48) - 15.24;
					Y = 0;
					Theta = 270;
				}					

				// TODO Update odometer with new calculated (and more accurate) vales
				odometer.setXYT(X, Y, Theta);
			} else {
				onTopOfLine = false;
			}

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD - (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here
				}
			}
		}
	}
}