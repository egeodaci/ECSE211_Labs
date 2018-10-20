package ca.mcgill.ecse211.searcher;

import ca.mcgill.ecse211.lab5.lab5;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class is used to navigate the robot on the demo floor.
 */
public class Navigation {
	
	private static final int FORWARD_SPEED = 180;
  	private static final int ROTATE_SPEED = 150;
  	private EV3LargeRegulatedMotor leftMotor;
  	private EV3LargeRegulatedMotor rightMotor;
  	
  	private Odometer odo;
  	private static boolean isNavigating;

  	

    /**
     * Class constructor
     * 
     * @param leftMotor
     * @param rightMotor
     * @throws OdometerExceptions 
     */
    public Navigation(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) throws OdometerExceptions {
    	this.odo = Odometer.getOdometer();
    	this.leftMotor = leftMotor;
    	this.rightMotor = rightMotor;
    }
    
    /**
     * This drives the robot to a specified point on the grid
     * 
     * @param x
     * @param y
     */
	public void travelTo(double x, double y) {
		isNavigating = false;
		
		while(!hasArrived(x, y)) {
		    double distance = odo.getXYTD()[3];
			
			if(distance < 20 && isNotABorderWall()) {
				isNavigating = false;
				avoidRing();
			} else if(!isNavigating){
				isNavigating = true;
				travelToCoordinate(x, y);
			}
		}
	}
	
    public boolean isNotABorderWall() {
		// TODO Auto-generated method stub
		return false;
	}


	public void avoidRing() {
		// TODO Auto-generated method stub
		
	}


	/**
     * This drives the robot to a way point
     * 
     * @param x
     * @param y
     */
	public void travelToCoordinate(double x, double y) {
		double dX = x - odo.getXYTD()[0];
		double dY = y - odo.getXYTD()[1];
		double theta = Math.atan(dX/dY); 
		if (dY < 0 && theta < Math.PI) theta += Math.PI; 
		double distance = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));

		turnTo(Math.toDegrees(theta));
		
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);

		leftMotor.rotate(convertDistance(lab5.WHEEL_RAD, distance), true);
		rightMotor.rotate(convertDistance(lab5.WHEEL_RAD, distance), true);
	}
	
    /**
     * This turns the robot to face an angle in regards to the x y axis
     * 
     * @param theta
     */
	public void turnTo(double theta) {
		double dTheta = theta - odo.getXYTD()[2];
		if(dTheta < 0) dTheta += 360;
		// turn right
		if (dTheta > 180) {
			leftMotor.setSpeed(ROTATE_SPEED);
			rightMotor.setSpeed(ROTATE_SPEED);
			leftMotor.rotate(-convertAngle(lab5.WHEEL_RAD, lab5.TRACK,360 - dTheta), true);
			rightMotor.rotate(convertAngle(lab5.WHEEL_RAD, lab5.TRACK, 360 - dTheta), false);
		}
		// turn left
		else { 
			leftMotor.setSpeed(ROTATE_SPEED);
			rightMotor.setSpeed(ROTATE_SPEED);
			leftMotor.rotate(convertAngle(lab5.WHEEL_RAD, lab5.TRACK, dTheta), true);
			rightMotor.rotate(-convertAngle(lab5.WHEEL_RAD, lab5.TRACK, dTheta), false);
		}
	}
	
	
	
    /**
     * Checks if robot reached its destination
     */
	public boolean hasArrived(double xDest, double yDest) {
		double xCurr = odo.getXYTD()[0];
		double yCurr = odo.getXYTD()[1];
		double cErr = Math.hypot(xCurr - xDest, yCurr - yDest);
		return cErr < 4;
	}
	
	
	/**
	 * This method changes distance to the equivalent wheel rotation
	 * 
	 * @param radius
	 * @param distance
	 */
	public static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	public static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	public static boolean isNavigating() {
		return isNavigating;
	}

	public void processUSData(int distance) {
		odo.setD(distance);
	}
}