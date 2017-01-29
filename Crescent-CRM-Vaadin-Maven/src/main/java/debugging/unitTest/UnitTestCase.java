package debugging.unitTest;

/**
 * Data holder for the result of a test
 * Should also contain utility functions for outputting data.
 * @author Josh Benton with help from Mark Layton
 *
 */
public class UnitTestCase {
	public static final String TEST_SUCCESS_STRING = "[✔]";
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
	}
	public Object getActualResult() {
		return actualResult;
	}
	
	/**
	 * This adds the actual result to the test case and then checks the result.
	 * @param actualResult
	 */
	public void setActualResult(Object actualResult) {
		this.actualResult = actualResult;
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

	
}
