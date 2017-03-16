package debugging.profiling;

import java.util.ArrayList;

import debugging.Debugging;

/**
 * Holds all timers created
 * 
 * Also a home for static timers similar to Debugging class
 * 
 * @author Joshua Benton
 * (c) 2017 Joshua Benton All Rights Reserved
 *
 */
public class MasterTimer {
	static ArrayList<ProfilingTimer> allTimers = new ArrayList<ProfilingTimer>();
	
	public static void addTimer(ProfilingTimer timer) {
		allTimers.add(timer);
	}
	
	public static void outputAllTimers() {
		for (ProfilingTimer timer : allTimers) {
			Debugging.output(timer.formattedOutput(), Debugging.PROFILING);
		}
	}
	
}
