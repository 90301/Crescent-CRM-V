package dbUtils.Conversions;

import dbUtils.MaxObject;
import debugging.DebugObject;
import debugging.Debugging;

public class UdhMaxObjectToString<T extends MaxObject> extends MaxConversion<T,String> {

	private Class<T> ref;

	@Override
	public String convertToStore(T input) {
		// TODO Auto-generated method stub
		if (input!=null) {
		
		return input.getPrimaryKey();
		} else {
			Debugging.output("Null value attempting to store. ", Debugging.CONVERSION_DEBUG2);
			return "";
		}
	}

	@Override
	public T convertToUse(String input) {
		// TODO Auto-generated method stub
		return userDataHolder.getMap(ref).get(input);
	}
	
	/**
	 * MUST CALL THIS METHOD BEFORE ATTEMPTING TO USE THE CONVERSION!!!
	 * @param ref
	 */
	public void setRef(Class<T> ref) {
		this.ref = ref;
	}

}
