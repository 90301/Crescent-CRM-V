package debugging.profiling;

import java.text.NumberFormat;
import java.util.ArrayList;

import dbUtils.InhalerUtils;

public class MetaProfilingTimer {
	String id = "";//This is the id of the timers in the collection
	//it is also the KEY for any datastructures holding the meta Timer
	
	ArrayList<ProfilingTimer> timers = new ArrayList<ProfilingTimer>();
	
	long avgRunTime = 0;
	long totalRunTime = 0;
	long avgDeviation = 0;
	
	long minRun = 0;
	long maxRun = 0;
	
	public MetaProfilingTimer(String id) {
		this.id = id;
	}
	
	public void addTimer(ProfilingTimer t) {
		timers.add(t);
	}
	
	public void processTimings() {
		
		if (timers.size() <2) {
			return;
			//not enough data to process.
		}
		
		totalRunTime = 0;
		minRun = 0;
		maxRun = 0;
		
		for (ProfilingTimer timer : timers) {
			long time = timer.elapsedTime;
			
			totalRunTime += timer.elapsedTime;
					
			if (time < this.minRun || this.minRun==0) {
				this.minRun = time;
			}
			if (time > this.maxRun ) {
				this.maxRun = time;
			}
		}
		
		avgRunTime = totalRunTime / timers.size();
		
		long totalDeviation = 0;
		for (ProfilingTimer timer : timers) {
			totalDeviation = Math.abs(timer.elapsedTime - avgRunTime);
		}
		avgDeviation = totalDeviation / timers.size();
		
	}
	
	
	public String formattedOutput() {
		
		processTimings();
		
		if (timers.size() < 2) {
			return "Not Enough Values for: " + id;
		}
		
		String output = "";
		
		String preBox = "";
		preBox += "Meta Stats for: " + id + System.lineSeparator();
		preBox += "Avg Time: " + avgRunTime + " | Avg Deviation: " + avgDeviation + System.lineSeparator();
		
		preBox += "Range: " +  NumberFormat.getInstance().format(minRun) + " - " + 
				NumberFormat.getInstance().format(maxRun) + System.lineSeparator();
		
		
		//Output the First value, mid value and last value
		
		preBox += formatNum(timers.get(0).elapsedTime) + " | ";
		preBox += formatNum(timers.get(timers.size()/2).elapsedTime) + " | ";
		preBox += formatNum(timers.get(timers.size()-1).elapsedTime) + System.lineSeparator();
		
		output  = InhalerUtils.boxString(preBox);
		
		return output;
	}
	
	public String formatNum(long number) {
		return NumberFormat.getInstance().format(number);
	}
	
	
	
	public String getID() {
		return id;
	}

}
