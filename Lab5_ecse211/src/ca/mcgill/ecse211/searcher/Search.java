package ca.mcgill.ecse211.searcher;

import ca.mcgill.ecse211.lab5.Display;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;




/**
 * This class will be using Navigation to 
 * 
 *
 */
public class Search extends Thread {

	// Coordinates of search region
	public static final int LL_X = 3;
	public static final int LL_Y = 3;
	public static final int UR_X = 7;
	public static final int UR_Y = 7;
	// 1-blue ; 2-green ; 3-yellow ; 4-orange
	public static final int TR = 2;
	public static final int SC = 0;
	
	private static final int THRESHOLD = 15;
	
	private Navigation navigator;
	private Odometer odo;
  	private EV3LargeRegulatedMotor leftMotor;
  	private EV3LargeRegulatedMotor rightMotor;
	
	private static int waypoints[][];
	
	
	
	
	
	
	/**
	 * Constructor
	 */
	public Search(Navigation navigator, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) 
			throws OdometerExceptions {
		this.navigator = navigator;
		this.odo = Odometer.getOdometer();
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}
	
	
	@SuppressWarnings("unused")
	public void run() {
		
		moveToSearchRegion();	// go to start of search region
		waypoints = createWayPoints(LL_X, LL_Y, UR_X, UR_Y);	// create waypoints to travel to
		
		//define variables
		int[] nextXY = new int[2];
		int d;
		boolean isNotConsecutiveRing = true;
		
		//move to next point
		for (int i = 0; i < waypoints.length; i++) {
			nextXY = waypoints[i];
			navigator.travelToCoordinate(nextXY[0], nextXY[1]);
			while(true) {
				d = odo.getD();
				if (d < THRESHOLD) {	//ring detected
					//stop motors
					stopMotors(leftMotor, rightMotor);
					//detect what color it is (maybe have to move close or back)
					detectRingColor();
					//avoid
					if (isNotConsecutiveRing) {
						
					}
				}
			}
			
			
		}
		//if there is a ring detect and analyze, then avoid according to the position i.e.
		//		if this is not a second time in a row avoidance, then turn left and right 45 deg to see which has bigger object
		//			so that avoiding is easier with that route
		//		if the ring is at the end of a row then avoid using different technique to set up for next row
		//if there is another ring detect and analyze, then avoid again
		
		
		
		
		
		
		
		
		
		
		Display.objectDetected();
	}
	
	
	private void detectRingColor() {
		//might have to adjust distance to ring
		
		
	}


	public void stopMotors(EV3LargeRegulatedMotor left, EV3LargeRegulatedMotor right) {
		left.stop();
		left.setAcceleration(1000);
		right.stop();
		right.setAcceleration(1000);
	}
	
	

	
	/**
	 * This method moves the robot to the start of the search region
	 */
	private void moveToSearchRegion() {
		navigator.travelToCoordinate(LL_X, LL_Y);
		while(!navigator.hasArrived(LL_X, LL_Y)) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {}
		}
		Sound.beep();
	}


	// this method is for avoiding objects
	// do standard avoidance by curving around ring touching
	// on the center of the tile you are crossing and a bit towards the ring
	// because you can slightly nudge it 
	public void avoidRing() {}
	
	
	/**
	 * This method takes in the coordinates of search region and 
	 * creates the points the robot will travel to, in the shape of a zig zag
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
