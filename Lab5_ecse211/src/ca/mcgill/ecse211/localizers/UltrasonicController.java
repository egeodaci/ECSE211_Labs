package ca.mcgill.ecse211.localizers;

public interface UltrasonicController {

  public void processUSData(int distance);

  public int readUSDistance();
}
