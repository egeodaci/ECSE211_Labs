package ca.mcgill.ecse211.wallfollowing;


import lejos.hardware.Button;
//import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController {

  private final int bandCenter;
  private final int bandwidth;
  private final int motorLow;
  private final int motorHigh;
  private int distance;   //sensor measured distance
  private int filterControl;
  private int distError;
  
  public static final int DELTASPD = 100; 
  public static final int SLEEPINT = 50; 
  private static final int FILTER_OUT = 9;

  public BangBangController(int bandCenter, int bandwidth, int motorLow, int motorHigh) {
    this.bandCenter = bandCenter;
    this.bandwidth = bandwidth;
    this.motorLow = motorLow;
    this.motorHigh = motorHigh;
    WallFollowingLab.leftMotor.setSpeed(motorHigh); // Start robot moving forward
    WallFollowingLab.rightMotor.setSpeed(motorHigh);
    WallFollowingLab.leftMotor.forward();
    WallFollowingLab.rightMotor.forward();
  }

  @Override
  public void processUSData(int distance) {  
    if (distance >= 255 && filterControl < FILTER_OUT) {
        filterControl++;
      } else if (distance >= 255) {
        this.distance = distance;
      } else {
        filterControl = 0;
        this.distance = distance;
      }
	

	  distError = bandCenter - distance; 
	  
	  // 25 +- 3 so 22-28
	  if (Math.abs(distError) <= bandwidth) {
		  moveForward(170, 170);
	  }
	  // 0-7
	  else if (distance < 8) {
		  moveBackward(60, 200);
	  }
	  // 8-18
	  else if (distance<=22 && distance >=8) {  
		  moveForward(150, 100);
	  }
	  else if (distance > 28 && distance < 70) {
		  moveForward(150, 150);
	  } else {
		  moveForward(100, 160);
	  }
  }
  

  public static void moveForward(int leftSpeed, int rightSpeed) {
    WallFollowingLab.leftMotor.setSpeed(leftSpeed); 
    WallFollowingLab.rightMotor.setSpeed(rightSpeed); 
    WallFollowingLab.leftMotor.forward(); 
    WallFollowingLab.rightMotor.forward();
  }
  public static void moveBackward(int leftSpeed, int rightSpeed) {
    WallFollowingLab.leftMotor.setSpeed(leftSpeed); 
    WallFollowingLab.rightMotor.setSpeed(rightSpeed); 
    WallFollowingLab.leftMotor.backward(); 
    WallFollowingLab.rightMotor.backward();
  }
  public static void concaveCurve(int leftSpeed, int rightSpeed) {
    WallFollowingLab.leftMotor.setSpeed(leftSpeed); 
    WallFollowingLab.rightMotor.setSpeed(rightSpeed); 
    WallFollowingLab.leftMotor.forward(); 
    WallFollowingLab.rightMotor.backward();
  }

  @Override
  public int readUSDistance() {
    return this.distance;
  }
}
