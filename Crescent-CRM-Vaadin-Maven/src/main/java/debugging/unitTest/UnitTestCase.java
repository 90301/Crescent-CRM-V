package debugging.unitTest;

import java.text.NumberFormat;

import debugging.Debugging;

/**
 * Data holder for the result of a test
 * Should also contain utility functions for outputting data.
 * @author Josh Benton with help from Mark Layton
 *
 */
public class UnitTestCase {
	public static final String TEST_SUCCESS_STRING = "[?]";
	public static final String TEST_FAIL_STRING = "[X]";
	public static final String DELIMITER = " | ";
	//Test type constants
	
	public static final String TEST_TYPE_OBJECT = "object";
	public static final String TEST_TYPE_STRING_CONTAINS = "string contains";
	public static final String TEST_TYPE_NOT_NULL = "not null";
	public static final String TEST_TYPE_NOT_EQUAL = "not equal";
	
	
	private String testName = "";
	private String testMessage = "";

	
	private Object expectedResult;
	private Object actualResult;
	
	
	private Boolean testResult = false;
	//Choosing tests
	
	private String testType = "";
	
	//profiling
	
	long startTime = 0;
	long stopTime = 0;
	
	private boolean outputChanges = true;
	
	
	
	
	{
		UnitTestSuite.addTestCase(this);
	}
	
	
	
	/**
	 * This is designed to be used to set up a number of tests without
	 * entering the actual result.
	 * @param testName
	 * @param testMessage
	 * @param expectedResult
	 * @param testType
	 */
	public UnitTestCase(String testName, String testMessage, Object expectedResult, String testType) {
		super();
		this.testName = testName;
		this.testMessage = testMessage;
		this.expectedResult = expectedResult;
		this.testType = testType;
	}


	/**
	 * This Constructor handles everything and simply needs to have the result output
	 * @param testName
	 * @param testMessage
	 * @param expectedResult
	 * @param actualResult
	 * @param testType
	 */
	public UnitTestCase(String testName, String testMessage, Object expectedResult,
			Object actualResult, String testType) {
		super();
		this.testMessage = testMessage;
		this.testName = testName;
		this.expectedResult = expectedResult;
		this.actualResult = actualResult;
		this.testType = testType;
		
		this.checkTest();
	}
	
	
	
	public UnitTestCase(String testName, String testMessage, Object expectedResult, Object actualResult,
			String testType, TestSuiteExecutor testSuiteExecutor) {
		super();
		this.testMessage = testMessage;
		this.testName = testName;
		this.expectedResult = expectedResult;
		this.actualResult = actualResult;
		this.testType = testType;
		
		testSuiteExecutor.testCases.add(this);
		this.checkTest();
	}
	
	public UnitTestCase(String testName, String testMessage, Object expectedResult,
			String testType, TestSuiteExecutor testSuiteExecutor) {
		super();
		this.testMessage = testMessage;
		this.testName = testName;
		this.expectedResult = expectedResult;
		this.testType = testType;
		
		testSuiteExecutor.testCases.add(this);
	}


	public Boolean getTestResult() {
		return testResult;
	}
	public void setTestResult(Boolean testSuccess) {
		this.testResult = testSuccess;
	}
	public String getTestMessage() {
		return testMessage;
	}
	public void setTestMessage(String testMessage) {
		this.testMessage = testMessage;
	}
	
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
		public Object getExpectedResult() {
		return expectedResult;
	}
	public void setExpectedResult(Object expectedResult) {
		this.expectedResult = expectedResult;
		
		if (outputChanges ) {
			outputResult("Expected Result",expectedResult);
		}
		
	}
	public Object getActualResult() {
		return actualResult;
	}
	
	
	
	public boolean isOutputChanges() {
		return outputChanges;
	}


	public void setOutputChanges(boolean outputChanges) {
		this.outputChanges = outputChanges;
	}


	/**
	 * This adds the actual result to the test case and then checks the result.
	 * @param actualResult
	 */
	public void setActualResult(Object actualResult) {
		this.actualResult = actualResult;
		if (outputChanges ) {
			outputResult("Actual Result",actualResult);
		}
		
		if (startTime!=0 && stopTime ==0) {
			stopProfiling();
		}
		
		this.checkTest();
	}
	
	/*
	 * Check results
	 */
	



	/**
	 * This determines the test to use, and runs that method
	 * to set the testResult.
	 * if nothing is set, it defaults to the object test
	 */
	public void checkTest() {
		if (testType.equals(TEST_TYPE_STRING_CONTAINS)) {
			checkStringContains();
		} else if (testType.equals(TEST_TYPE_OBJECT)) {
			checkObjectResult();
		} else if (testType.equals(TEST_TYPE_NOT_EQUAL)) {
			checkNotEqual();
		}  else if (testType.equals(TEST_TYPE_NOT_NULL)) {
			checkNotNull();
		} else {
			checkObjectResult();
		}
	}
	
	
	/**
	 * Checks the results of any object that "Equals" will work for.
	 */
	public void checkObjectResult() {
		if (expectedResult.equals(actualResult)) {
			this.testResult = true;
		} else {
			this.testResult = false;
		}
	}
	
	public void checkStringContains() {
		if (((String) actualResult).contains(((String)expectedResult))) {
			this.testResult = true;
		} else {
			this.testResult = false;
		}
	}
	
	public void checkNotNull() {
		if (actualResult!=null) {
			this.testResult = true;
		} else {
			this.testResult = false;
		}
	}
	
	public void checkNotEqual() {
		if (!actualResult.equals(expectedResult)) {
			this.testResult = true;
		} else {
			this.testResult = false;
		}
	}
	
	/*
	 * Profiling
	 */
	
	public void startProfiling() {
		startTime = System.nanoTime();
	}
	
	public void stopProfiling() {
		stopTime = System.nanoTime();
	}
	
	/*
	 * OUTPUT CODE
	 */
	public String toString() {
		String output = "";
		if (testResult) {
			output += TEST_SUCCESS_STRING;
		} else {
			output += TEST_FAIL_STRING;
		}
		
		output += testName + DELIMITER + testMessage + " Result: " + actualResult + " Expected: " + expectedResult;
		return output;
		
	}
	
	public void outputProfiling() {
		if (startTime ==0 && stopTime==0) {
			return;
		}
		
		long runTime = stopTime-startTime;
		String runTimeOutput = NumberFormat.getInstance().format(runTime);
		String profileOutput = testName + " | run-time: "  + runTimeOutput;
		Debugging.output(profileOutput, Debugging.PROFILING);
	}

	private void outputResult(String preface, Object result) {
		
		Debugging.output(testName + " | " + preface + " : " + result, Debugging.UNIT_TEST_TRACK_CHANGES);
		
	}
}
