package ca.mcgill.ecse211.sensors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ca.mcgill.ecse211.odometer.OdometerExceptions;

public class DataController {

	
	// Sensor variables
	private volatile double lightGrid;	//volatile because more than one thread will access it
	private volatile float[] lightRing;
	private volatile double distance;
	
	//Class control
	private volatile static int numberOfInstances = 0; //static so that everyone looks at same value
	private static final int MAX_INSTANCES = 1;	//only one controller allowed
	
	
	//Thread control
	private static Lock lock = new ReentrantLock(true); // Fair lock for concurrent writing
	private volatile boolean isReseting = false;	// Indicates if a thread is
      												// trying to reset any
													// position parameters
	private Condition doneReseting = lock.newCondition();	// Let other threads
															// know that a reset
															// operation is
															// over.
	private static DataController dataCont = null;
	
	/**
	 * Default constructor. The constructor is private. A factory is used instead such that only one
	 * instance of this class is ever created.
	 */
	protected DataController() {
		this.lightGrid = 0.8;
		this.lightRing = new float[] {0, 0, 0};
	    this.distance = 100;
	}
	
	/**
	 * DataController factory. Returns a DataController instance and makes sure that only one instance is
	 * ever created. If the user tries to instantiate multiple objects, the method throws a
	 * MultipleOdometerDataException.
	 * 
	 * @return A DataController object
	 * @throws OdometerExceptions
	 */
	public synchronized static DataController getSensorData() throws OdometerExceptions {
		if (dataCont != null) { // Return existing object
			return dataCont;
		} else if (numberOfInstances < MAX_INSTANCES) { // create object and
			// return it
			dataCont = new DataController();
			numberOfInstances += 1;
			return dataCont;
		} else {
			throw new OdometerExceptions("Only one intance of the DataController can be created.");
		}

	}
	
	/**
	 * Returns the lightGrid and distance data
	 * 
	 * @return the sensor data as a double[2]
	 */
	public double[] getDL() {
		double[] data = new double[2];
		lock.lock();
		try {
			while (isReseting) { // If a reset operation is being executed, wait
				// until it is over.
				doneReseting.await(); // Using await() is lighter on the CPU
				// than simple busy wait.
			}

			data[0] = lightGrid;
			data[1] = distance;

		} catch (InterruptedException e) {
			// Print exception to screen
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		
		return data;
	}
	
	/**
	 * Return the Sensor data.
	 * 
	 * @return the sensor data.
	 */
	public double[] getRGB() {
		double[] data = new double[3];
		lock.lock();
		try {
			while (isReseting) { // If a reset operation is being executed, wait
				// until it is over.
				doneReseting.await(); // Using await() is lighter on the CPU
				// than simple busy wait.
			}
			data[0] = lightRing[0] * 100;
			data[1] = lightRing[1] * 100;
			data[2] = lightRing[2] * 100;

		} catch (InterruptedException e) {
			// Print exception to screen
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return data;
	}

	/**
	 * Sets distance of ultrasonic sensor
	 * 
	 * @param d the value of d
	 */
	public void setD(double d) {
		lock.lock();
		isReseting = true;
		try {
			this.distance = d;
			isReseting = false; // Done reseting
			doneReseting.signalAll(); // Let the other threads know that you are
			// done reseting
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Sets light intensity for grid
	 * 
	 * @param l the value of l
	 */
	public void setL(double intensity) {
		lock.lock();
		isReseting = true;
		try {
			this.lightGrid = intensity;
			isReseting = false; // Done reseting
			doneReseting.signalAll(); // Let the other threads know that you are
			// done reseting
		} finally {
			lock.unlock();
		}
	}
	
	
	/**
	 * Sets rgb of lightRing array
	 * 
	 * @param l the value of l
	 */
	public void setRGB(float[] rgb) {
		lock.lock();
		isReseting = true;
		try {
			this.lightRing = rgb;
			isReseting = false; // Done reseting
			doneReseting.signalAll(); // Let the other threads know that you are
			// done reseting
		} finally {
			lock.unlock();
		}
	}
	
}
