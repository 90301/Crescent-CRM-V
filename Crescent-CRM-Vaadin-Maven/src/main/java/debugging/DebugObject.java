package debugging;

import java.util.ArrayList;

import org.omg.CORBA.Environment;

import ccrmV.MasterUI;

public class DebugObject {

	private static final boolean DOUBLE_SPACING = true;
	public String debugStyle;
	public Boolean enabled = true;

	Boolean logEnabled = true;
	private String debugLog = "";
	String outputPreface = "";//adds this text to the beginning out output
	//DOES NOT do this in the log.
	ArrayList<String> blocks = new ArrayList<String>();

	String currentBlock = "";

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
				debugLog += outString + System.getProperty("line.separator");
				currentBlock += outString + System.getProperty("line.separator");
			}

			Debugging.output(this.outputPreface + outString, debugStyle, enabled);
		}
	}

	public void nextBlock() {
		blocks.add(currentBlock);
		currentBlock = "";
	}

	public String getOutput() {
		if (blocks.size() <= 1) {
			return this.debugLog;
		} else {
			return this.outputBlocks();
		}
	}

	public void outputLog() {
		if (blocks.size() <= 1) {
			Debugging.output(debugLog, debugStyle, enabled);
		} else {
			Debugging.output(outputBlocks(), debugStyle, enabled);
		}
	}

	public String outputBlocks() {
		String output = Debugging.LINE + System.getProperty("line.separator");
		for (String s : blocks) {
			output += s;
			output += Debugging.LINE + System.getProperty("line.separator");
			if (DOUBLE_SPACING) {
				output+= System.getProperty("line.separator");
			}
		}
		
		output += currentBlock;
		return output;

	}

	public String getName() {
		return outputPreface;
	}
}
