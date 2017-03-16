package debugging.profiling;

import java.text.NumberFormat;
import java.util.ArrayList;

import dbUtils.InhalerUtils;

/**
 * A timer designed for use in loops / or for taking a lot of measurements
 * 
 * @author Joshua Benton
 * (c) 2017 Joshua Benton All Rights Reserved.
 *
 */
public class RapidProfilingTimer extends ProfilingTimer {

	
	private static final int MAX_OUTPUT_LINES = 50;

	ArrayList<Long> timeReadings = new ArrayList<Long>();
	
	//Processed values
	ArrayList<Long> timeElapsed = new ArrayList<Long>();
	long totalTime = 0;
	long averageTime = 0;
	long avgDeviation = 0;
	long largestDeviation  = 0;
	long maxRun = 0;
	long minRun = 0;
	
	public RapidProfilingTimer(String timerName) {
		super(timerName);
	}
	
	/**
	 * Logs the current time, can be used constantly
	 */
	public void logTime() {
		timeReadings.add(System.nanoTime());
	}
	
	/**
	 * Processes all the readings for use with output
	 * If there are a number of readings, note that this may take O(C*N) time
	 */
	public void processReadings() {
		if (timeReadings.size() < 2) {
			return;
			//Not enough values to process.
		}
		
		for (int i=1;i<timeReadings.size();i++) {
			long time1 = timeReadings.get(i-1);
			long time2 = timeReadings.get(i);
			
			long elapsedTime = time2 - time1;
			
			timeElapsed.add(elapsedTime);
		}
		
		this.totalTime = 0;
		for (long time : this.timeElapsed) {
			this.totalTime += time;
			
		}
		this.averageTime = this.totalTime / this.timeElapsed.size();
		
		//compute deviations
		this.largestDeviation = 0;
		long totalDeviation = 0;
		for (long time : this.timeElapsed) {
			long deviation = Math.abs(time - this.averageTime);
			
			totalDeviation += deviation;
			if (deviation > largestDeviation || largestDeviation == 0) {
				largestDeviation = deviation;
			}
			
			//min / max values
			
			if (time < this.minRun || this.minRun==0) {
				this.minRun = time;
			}
			if (time > this.maxRun || this.maxRun==0) {
				this.maxRun = time;
			}
			
			
			
		}
		
		this.avgDeviation = totalDeviation/this.timeElapsed.size();
		
		
		this.startTime = timeReadings.get(0);
		this.endTime = timeReadings.get(timeReadings.size()-1);
		this.elapsedTime = endTime-startTime;
	}
	
	@Override
	public String formattedOutput() {
		
		if (this.maxRun==0) {
			processReadings();
		}
		
		String output = "";
		
		String preBox = "___ " + timerName + " ___" + System.lineSeparator();
		
		preBox += "--- Stats ---"+ System.lineSeparator();
		
		preBox += "Average Time: " + NumberFormat.getInstance().format(averageTime) + " | ";
		preBox += " Avg Deviation: " + NumberFormat.getInstance().format(avgDeviation) + System.lineSeparator();
		
		preBox += "Range: " +  NumberFormat.getInstance().format(minRun) + " - " + 
				NumberFormat.getInstance().format(maxRun) + System.lineSeparator();
		
		preBox += "Number of Intervals: " + timeElapsed.size() + System.lineSeparator();
		
		preBox += "Total Time: " + NumberFormat.getInstance().format(this.elapsedTime) + System.lineSeparator();
		
		int i=0;
		//sample first N values
		preBox += "--- Data ---" + System.lineSeparator();
		
		for (long time : timeElapsed) {
			preBox += i+": " + NumberFormat.getInstance().format(time) + System.lineSeparator();
			i++;
			
			if (i>MAX_OUTPUT_LINES) {
				break;
			}
		}
		
		output = InhalerUtils.boxString(preBox);
		
		return output;
	}
	

}
