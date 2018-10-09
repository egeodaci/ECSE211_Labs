package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {

  /* Constants */
  private static final int MOTOR_SPEED = 200;
  private static final int FILTER_OUT = 20;
  private static final double HIGH_CORRECTION = 2;
  private static final double LOW_CORRECTION = 0.5;

  private final int bandCenter;
  private final int bandWidth;
  private int distance;
  private int filterControl;

  public PController(int bandCenter, int bandwidth) {
    this.bandCenter = bandCenter;
    this.bandWidth = bandwidth;
    this.filterControl = 0;

    WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED); // Initalize motor rolling forward
    WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
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

    // TODO: process a movement based on the us distance passed in (P style)
    // P-Style: Correction should be proportional to the magnitude of error.
    int distError = bandCenter - distance;
    int motorHigh= (int) (MOTOR_SPEED * HIGH_CORRECTION * distError);
    int motorLow = (int) (MOTOR_SPEED * LOW_CORRECTION * distError);
    
    if (Math.abs(distError) <= bandWidth) {
        move(motorHigh, motorHigh);
      }
      else if (distError >= (bandCenter - 12)) {
        WallFollowingLab.leftMotor.setSpeed(75); 
        WallFollowingLab.rightMotor.setSpeed(150); 
        WallFollowingLab.leftMotor.backward(); 
        WallFollowingLab.rightMotor.backward();
      }
      else if ((distError > 0) && (distError < (bandCenter - 10))) { // too close 
        move(motorHigh, motorLow);
      }
      else if (distError < 0) { // too far 
        move(motorLow, motorHigh);
      } 
  }
  
  public static void move(int leftSpeed, int rightSpeed) {
	    WallFollowingLab.leftMotor.setSpeed(leftSpeed); 
	    WallFollowingLab.rightMotor.setSpeed(rightSpeed); 
	    WallFollowingLab.leftMotor.forward(); 
	    WallFollowingLab.rightMotor.forward();
	  }


  @Override
  public int readUSDistance() {
    return this.distance;
  }

}
