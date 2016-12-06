package debugging;

import ccrmV.MasterUI;

public class DebugObject {

	public String debugStyle;
	public Boolean enabled = true;

	Boolean logEnabled = true;
	String debugLog = "";
	String outputPreface = "";//adds this text to the beginning out output
	//DOES NOT do this in the log.

	public DebugObject(String debugStyle, Boolean enabled, Boolean logEnabled) {
		this.debugStyle = debugStyle;
		this.enabled = enabled;
		this.logEnabled = logEnabled;
	}
	
	public DebugObject(String debugStyle, Boolean enabled, Boolean logEnabled, String outputPreface) {
		this.debugStyle = debugStyle;
		this.enabled = enabled;
		this.logEnabled = logEnabled;
		this.outputPreface = outputPreface;
	}

	public void output(String outString) {
		if (MasterUI.DEVELOPER_MODE) {

			if (logEnabled) {
				debugLog += outString;
			}
			
			Debugging.output(this.outputPreface + outString, debugStyle, enabled);
		}
	}
	
	public void outputLog() {
		Debugging.output(debugLog, debugStyle, enabled);
	}
}
