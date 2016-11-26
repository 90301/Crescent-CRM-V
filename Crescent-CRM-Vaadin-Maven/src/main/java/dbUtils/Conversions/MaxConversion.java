package dbUtils.Conversions;

public interface MaxConversion <USE,STORE> {
	public STORE convertToStore(USE input);
	
	public USE convertToUse(STORE input);
}
