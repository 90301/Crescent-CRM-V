package debugging.profiling;

import java.text.NumberFormat;

public class ProfilingTimer {
	
	private static final int OUTPUT_LENGTH = 20;

	String timerName = "";
	
	long startTime = 0;
	long endTime = 0;
	
	long elapsedTime = 0;
	
	
	/**
	 * Automatically starts the timer. If you need another start time, call startTimer
	 * when you desire to start timing.
	 * @param timerName - the name of the timer
	 */
	public ProfilingTimer(String timerName) {
		this.timerName = timerName;
		MasterTimer.addTimer(this);
		startTimer();
	}
	
	
	/**
	 * Sets the startTime to current nano time.
	 */
	public void startTimer() {
		startTime = System.nanoTime();
	}
	
	/**
	 * Sets the stop time and computes elapsed time
	 */
	public void stopTimer() {
		endTime = System.nanoTime();
		
		elapsedTime = endTime - startTime;
	}
	
	
	/**
	 * Gets the nanoseconds of execution
	 * @return nano-seconds  elapsed between startTimer and stopTimer
	 */
	public long getElapsedTime() {
		return elapsedTime;
	}
	
	/**
	 * Makes an evenly spaced output that's intended to be easy to read.
	 * @return an easy to read String representation of the timer
	 */
	public String formattedOutput() {
		String output = "";
		
		output += timerName + ": ";
		
		while (output.length() < OUTPUT_LENGTH) {
			output += " ";
		}
		output += " | ";
		
		//format the long to include commas.
		String elapsedTimeFormatted = NumberFormat.getInstance().format(elapsedTime);
		
		output += elapsedTimeFormatted;
		
		return output;
	}
	
	
	
	
	
	

}
