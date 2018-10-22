package ca.mcgill.ecse211.lab5;

import java.util.ArrayList;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Search extends Thread{

	private static int waypoints[][];
	
	private static final int RING_INBOUND = 13;
	private static final int LARGEST_RADIUS = 6;
	private static final int CENTER_TO_SENSOR = 6; //distance from center of wheels to ring light sensor,  
													//should be slightly higher than actual value
	private static final int US_TO_LIGHT = 5; //distance from us sensor to ring light sensor
	private static final int SLOW_SPEED = 40;
	private static final int ULTRA_SLOW_SPEED = 20;
	private static final int RING_WIDTH = 3;
	
	private Navigation navigator;
	private Odometer odo;
  	private EV3LargeRegulatedMotor leftMotor;
  	private EV3LargeRegulatedMotor rightMotor;
	

	/**
	 * Constructor
	 */
	public Search(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) 
			throws OdometerExceptions {
		this.odo = Odometer.getOdometer();
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}
	
	
	@SuppressWarnings("unused")
	public void run() {		
		//use sensors on both sides to correct for straight passing
		//	can turn on this thread only when correcting
		//	can either have correction thread running algorithm or correct every so and so
		
		waypoints = createWayPoints(Lab5.LLx, Lab5.LLy, Lab5.URx, Lab5.URy);	// create waypoints to travel to
	}
	
	/**
	 * stops the motors 
	 */
	public void stopMotors(EV3LargeRegulatedMotor left, EV3LargeRegulatedMotor right) {
		left.flt();
		right.flt();
		left.setAcceleration(1000);
		right.setAcceleration(1000);
	}
	
	/**
	 * This method takes in the coordinates of search region and 
	 * creates the points the robot will travel to, in the shape of a zig zag
	 * so we loop through the points and go to each of them
	 * @param lx
	 * @param ly
	 * @param ux
	 * @param uy
	 */
	public static int[][] createWayPoints(int lx, int ly, int ux, int uy) {
		int numberOfWaypoints = (ux - lx + 1) * (uy - ly + 1);
		int[][] waypoints = new int[numberOfWaypoints][2];
		boolean east = true;
		int j = 0;
		for(int y = ly; y < uy + 1; y++) {
			if (east) {
				for(int x = lx; x < ux + 1; x++, j++) {
					waypoints[j][0] = x;
					waypoints[j][1] = y;
				}
				east = false;
			} else {
				for(int x = ux; x > lx - 1; x--, j++) {
					waypoints[j][0] = x;
					waypoints[j][1] = y;
				}
				east = true;
			}
		}
		return waypoints;
	}
	
}
