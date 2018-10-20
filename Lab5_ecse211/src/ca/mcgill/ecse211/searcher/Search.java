package ca.mcgill.ecse211.searcher;

import ca.mcgill.ecse211.lab5.Display;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import lejos.hardware.Sound;




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
	
	private Navigation navigator;
	private Odometer odo;
	
	private static int waypoints[][];
	
	
	
	
	
	
	/**
	 * Constructor
	 */
	public Search(Navigation navigator) throws OdometerExceptions {
		this.navigator = navigator;
		this.odo = Odometer.getOdometer();
	}
	
	
	public void run() {
		//navigate to start of search region
		navigator.travelToCoordinate(LL_X, LL_Y);
		while(!navigator.hasArrived(LL_X, LL_Y)) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {}
		}
		Sound.beep();
		
		waypoints = createWayPoints(LL_X, LL_Y, UR_X, UR_Y);
		
		//move to next point
		int[] nextXY = new int[2];
		for (int i = 0; i < waypoints.length; i++) {
			nextXY = waypoints[i];
			navigator.travelToCoordinate(nextXY[0], nextXY[1]);
			
			
		}
		//if there is a ring detect and analyze, then avoid according to the position i.e.
		//		if this is not a second time in a row avoidance, then turn left and right 45 deg to see which has bigger object
		//			so that avoiding is easier with that route
		//		if the ring is at the end of a row then avoid using different technique to set up for next row
		//if there is another ring detect and analyze, then avoid again
		
		
		
		
		
		
		
		
		
		
		Display.objectDetected();
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
