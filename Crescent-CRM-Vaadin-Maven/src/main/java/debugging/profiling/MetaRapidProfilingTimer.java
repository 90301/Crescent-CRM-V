package debugging.profiling;

import java.text.NumberFormat;
import java.util.ArrayList;

import dbUtils.InhalerUtils;

/**
 * Holds multiple of the same timer, can compute meta stats
 * and present the data in a meaningful way.
 * 
 * @author Joshua Benton
 * (c) 2017 Joshua Benton All Rights Reserved.
 */
public class MetaRapidProfilingTimer {
	
	String id = "";//This is the id of the timers in the collection
	//it is also the KEY for any datastructures holding the meta Timer
	
	
	long avgRunTime = 0;
	long totalRunTime = 0;
	long avgDeviation = 0;
	
	long avgCycleTime = 0;
	long totalCycles = 0;
	
	long minRun = 0;
	long maxRun = 0;
	
	ArrayList<RapidProfilingTimer> timers = new ArrayList<RapidProfilingTimer>(); 
	
	public MetaRapidProfilingTimer(String id) {
		this.id = id;
	}
	
	public void addTimer(RapidProfilingTimer t) {
		timers.add(t);
	}
	
	
	public void processTimings() {
		
		for (RapidProfilingTimer timer : timers) {
			timer.processReadings();
		}
		
		if (timers.size() <2) {
			return;
			//not enough data to process.
		}
		
		totalRunTime = 0;
		minRun = 0;
		maxRun = 0;
		
		totalCycles = 0;
		
		for (RapidProfilingTimer timer : timers) {
			long time = timer.elapsedTime;
			
			
			
			totalRunTime += timer.elapsedTime;
					
			if (time < this.minRun || this.minRun==0) {
				this.minRun = time;
			}
			if (time > this.maxRun ) {
				this.maxRun = time;
			}
			
			totalCycles += timer.timeElapsed.size();
		}
		
		avgCycleTime = totalRunTime / totalCycles;
		
		avgRunTime = totalRunTime / timers.size();
		
		long totalDeviation = 0;
		for (RapidProfilingTimer timer : timers) {
			totalDeviation = Math.abs(timer.elapsedTime - avgRunTime);
		}
		avgDeviation = totalDeviation / timers.size();
		
	}
	
	public String formattedOutput() {
		//TODO improve the qualitiy of this info.
		processTimings();
		
		if (timers.size() < 2) {
			return "Not Enough Values for: " + id;
		}
		
		String output = "";
		
		String preBox = "";
		preBox += InhalerUtils.SPACING_FORMAT_CENTERED + id + System.lineSeparator();
		preBox += "Meta Rapid Stats for: " + id + System.lineSeparator();
		preBox += "Avg Time: " + formatNum(avgRunTime) + " | Avg Deviation: " + formatNum(avgDeviation) + System.lineSeparator();
		
		preBox += "Avg Cycle: " + formatNum(avgCycleTime) + " | Cycles: " + formatNum(totalCycles) + System.lineSeparator();
		
		preBox += "Total Time: " + formatNum(totalRunTime) + System.lineSeparator();
		
		preBox += "Range: " +  formatNum(minRun) + " - " + 
				formatNum(maxRun) + System.lineSeparator();
		
		preBox += InhalerUtils.SPACING_FORMAT_BLANK_LINE + System.lineSeparator();
		//Output the First value, mid value and last value
		preBox += InhalerUtils.SPACING_FORMAT_CENTERED;
		preBox += "   " + formatNum(timers.get(0).elapsedTime) + " | ";
		preBox += formatNum(timers.get(timers.size()/2).elapsedTime) + " | ";
		preBox += formatNum(timers.get(timers.size()-1).elapsedTime) + "  " + System.lineSeparator();
		
		output  = InhalerUtils.boxString(preBox);
		
		return output;
	}
	
	static final long NANO_TO_SEC = 1000000000;
	
	public String formatNum(long number) {
		return NumberFormat.getInstance().format(number);
	}
	
	
	public String getID() {
		return id;
	}
	

}
