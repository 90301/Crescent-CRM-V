package debugging;

import ccrmV.MasterUI;

public class DebugObject {

	public String debugStyle;
	public Boolean enabled = true;

	Boolean logEnabled = true;
	String debugLog = "";

	public DebugObject(String debugStyle, Boolean enabled, Boolean logEnabled) {
		this.debugStyle = debugStyle;
		this.enabled = enabled;
		this.logEnabled = logEnabled;
	}

	public void output(String outString) {
		if (MasterUI.DEVELOPER_MODE) {

			if (logEnabled) {
				debugLog += outString;
			}
			
			Debugging.output(outString, debugStyle, enabled);
		}
	}
	
	public void outputLog() {
		Debugging.output(debugLog, debugStyle, enabled);
	}
}
