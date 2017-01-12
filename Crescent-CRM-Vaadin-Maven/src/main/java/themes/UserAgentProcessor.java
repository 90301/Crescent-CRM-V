/*
 * (c) 2017 Joshua Benton. All rights reserved
 */

package themes;

import debugging.Debugging;

/**
 * A class for determining what the user's device is and returning if it's a mobile OS
 * or a Desktop OS.
 * @author Josh Benton
 *
 */
public class UserAgentProcessor {
	
	
	
	public static Boolean isAgentMobile(String userAgent) {
		
		//start off with mobility factor 0
		
		int mobilityFactor = 0;
		//likely mobile factors:
		if (userAgent.toLowerCase().contains("android")) {
			mobilityFactor +=5;
			Debugging.output("User agent contains android. ", Debugging.MOBILE_DEBUG);
		}
		if (userAgent.toLowerCase().contains("iphone")) {
			mobilityFactor +=1;
			Debugging.output("User agent contains iPhone. ", Debugging.MOBILE_DEBUG);
		}
		
		//likely Desktop factors:
		if (userAgent.toLowerCase().contains("windows")) {
			mobilityFactor -=1;
			Debugging.output("User agent contains windows. ", Debugging.MOBILE_DEBUG);
		}
		
		
		if (mobilityFactor >= 1) {
			Debugging.output("User agent identified as mobile.", Debugging.MOBILE_DEBUG);
			return true;
		} else {
			Debugging.output("User agent identified as desktop.", Debugging.MOBILE_DEBUG);
			return false;
		}
	}

}
