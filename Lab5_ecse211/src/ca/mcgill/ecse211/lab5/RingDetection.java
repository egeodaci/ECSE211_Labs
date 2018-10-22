package ca.mcgill.ecse211.lab5;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class RingDetection {
	public static SensorModes myColor = new EV3ColorSensor(Lab5.portColor);
	public static SampleProvider myColorSample = myColor.getMode("RGB");
	static float[] sampleColor = new float[myColor.sampleSize()];
}
