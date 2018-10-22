package ca.mcgill.ecse211.lab5;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class RingDetection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	public static final TextLCD lcd = LocalEV3.get().getTextLCD();
	private static float[][] rgbMeansArray = { { 0, 0, 1 }, { 0, 1, 0 }, { 1, 0, 0 }, { 1, 1, 0 }};
 // { { Mean-rgb for Blue }, { Mean-rgb for Green }, { Mean-rgb for Yellow }, { Mean-rgb for Orange }}

	Port portRing = LocalEV3.get().getPort("S4");
	SensorModes colorSensor = new EV3ColorSensor(portRing);
	SampleProvider colorSample = colorSensor.getMode("RGB");
	float[] colorData = new float[colorSensor.sampleSize()];

	public RingDetection() {

	}


	// run method (required for Thread)
	public void run() {
		long sampleStart, sampleEnd;

		while (true) {
			sampleStart = System.currentTimeMillis();
			
			float[] rgbValues = new float[3]; // need to figure out how to get the RGB values from light sensor
			colorSample.fetchSample(rgbValues, 0);
			//---------
			float A=rgbValues[0]*100;
			float B=rgbValues[1]*100;
			float C=rgbValues[2]*100;
			//-----
			
			
			
			
			lcd.drawString("R: " + A, 0, 2);
			lcd.drawString("G: " + B, 0, 3);
			lcd.drawString("B: " + C, 0, 4);

			lcd.drawString("Color: " + colorDetection(rgbValues), 0, 5); //display the Color detected

			// this ensure the odometry correction occurs only once every period
			sampleEnd = System.currentTimeMillis();
			if (sampleEnd - sampleStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD - (sampleEnd - sampleStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here
				}
			}
		}
	}

	public static String colorDetection(float[] rgb) {
		float[] colorsDistances = new float[5];

		// This could be implemented with a for loop but to understand how it works, I
		// have decided to separated the 4 colorsDistances

		// if this distance is the minimum then, no Color/Object is detected
		colorsDistances[0] = 1000; // to modify
		
		// distance from Blue
		colorsDistances[1] = (float) distance(rgbMeansArray[0], rgb);

		// distance from Green
		colorsDistances[2] = (float) distance(rgbMeansArray[1], rgb);

		// distance from Yellow
		colorsDistances[3] = (float) distance(rgbMeansArray[2], rgb);

		// distance from Orange
		colorsDistances[4] = (float) distance(rgbMeansArray[3], rgb);
		
		int minimum = getMinIndex(colorsDistances);
		lcd.drawString("min d: " + colorsDistances[minimum], 0, 6);
		String color;
		
		switch (minimum) {
		case 1:
			color = "Blue";
			break;
		case 2:
			color = "Green";
			break;
			
		case 3:
			color = "Yellow";
			break;
			
		case 4:
			color = "Orange";
			break;
		default:
			color = "None";
		}
		

		return color;
	}

	public static double distance(float[] rgbMean, float[] rgbValues) {

		return Math.sqrt(Math.pow(rgbMean[0] - rgbValues[0], 2) + Math.pow(rgbMean[1] - rgbValues[1], 2)
				+ Math.pow(rgbMean[2] - rgbValues[2], 2));

	}

	private static int getMinIndex(float[] inputArray) {
		double minValue = inputArray[0];
		int minIndex = 0;
		for (int i = 0; i < inputArray.length; i++) {
			if (inputArray[i] < minValue) {
				minValue = inputArray[i];
				minIndex = i;
			}
		}
		return minIndex;
	}

	public static int detectColor(double[] rgb) {
		// yellow
		if (rgb[0] >= 6 && rgb[0] <= 9 &&

				rgb[1] >= 4 && rgb[1] <= 7 &&

				rgb[2] >= 0 && rgb[2] <= 2)
			return 3;

		// blue
		if (rgb[0] >= 0 && rgb[0] <= 3 &&

				rgb[1] >= 3 && rgb[1] <= 8 &&

				rgb[2] >= 1 && rgb[2] <= 5)
			return 1;

		// orange
		if (rgb[0] >= 2 && rgb[0] <= 6 &&

				rgb[1] >= 0 && rgb[1] <= 3 &&

				rgb[2] >= 0 && rgb[2] <= 1)
			return 4;

		// green
		if (rgb[0] >= 0 && rgb[0] <= 4 &&

				rgb[1] >= 2 && rgb[1] <= 8 &&

				rgb[2] >= 0 && rgb[2] <= 2)
			return 2;

		return 0;
	}
}
