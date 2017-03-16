package debugging.profiling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
	private static final int REQUIRED_ENTRIES_FOR_META = 2;//must be at least 2

	private static final int REQUIRED_ENTRIES_FOR_META_RAPID = 2;//must be at least 2

	static ArrayList<ProfilingTimer> allTimers = new ArrayList<ProfilingTimer>();
	
	static ArrayList<RapidProfilingTimer> allRapidTimers = new ArrayList<RapidProfilingTimer>();
	
	static HashMap<String,MetaProfilingTimer> metaProfilingTimers = new HashMap<String,MetaProfilingTimer>();
	
	static HashMap<String,MetaRapidProfilingTimer> metaRapidProfilingTimers = new HashMap<String,MetaRapidProfilingTimer>();
	
	public static void addTimer(ProfilingTimer timer) {
		allTimers.add(timer);
		
		//if no profiling timer exists add one
		if (!metaProfilingTimers.containsKey(timer.timerName)) {
			// create the meta profiling timer
			MetaProfilingTimer metaTimer = new MetaProfilingTimer(timer.timerName);
			metaProfilingTimers.put(timer.timerName, metaTimer);
		}
		metaProfilingTimers.get(timer.timerName).addTimer(timer);
	}
	
	
	public static void addTimer(RapidProfilingTimer timer) {
		allRapidTimers.add(timer);
		
		//if no profiling timer exists add one meta profiling timer collection
		if (!metaRapidProfilingTimers.containsKey(timer.timerName)) {
			// create the meta profiling timer
			MetaRapidProfilingTimer metaTimer = new MetaRapidProfilingTimer(timer.timerName);
			metaRapidProfilingTimers.put(timer.timerName, metaTimer);
		}
		metaRapidProfilingTimers.get(timer.timerName).addTimer(timer);
	}
	
	public static void outputAllTimers() {
		for (ProfilingTimer timer : allTimers) {
			if (metaProfilingTimers.get(timer.timerName).timers.size() < REQUIRED_ENTRIES_FOR_META) {
				Debugging.output(timer.formattedOutput(), Debugging.PROFILING);
			}
		}
		
		for (RapidProfilingTimer timer : allRapidTimers) {
			if (metaRapidProfilingTimers.get(timer.timerName).timers.size() < REQUIRED_ENTRIES_FOR_META_RAPID) {
				Debugging.output(timer.formattedOutput(), Debugging.PROFILING);
			}
		}
		
		//Meta Timers
		
		for (MetaProfilingTimer metaTimer : metaProfilingTimers.values()) {
			if (metaTimer.timers.size() > REQUIRED_ENTRIES_FOR_META) {
				Debugging.output(metaTimer.formattedOutput(), Debugging.PROFILING);
			}
		}
		
		for (MetaRapidProfilingTimer metaTimer : metaRapidProfilingTimers.values()) {
			if (metaTimer.timers.size() > REQUIRED_ENTRIES_FOR_META_RAPID) {
				Debugging.output(metaTimer.formattedOutput(), Debugging.PROFILING);
			}
		}
		
		
		
	}
	
}
