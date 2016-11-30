package dbUtils.Conversions;

import clientInfo.UserDataHolder;

public abstract class MaxConversion <USE,STORE> {
	//Variables that will probably always need to be used
	public UserDataHolder userDataHolder;
	public Class<STORE> storeRef;
	
	public void setUserDataHolder(UserDataHolder userDataHolder) {
		this.userDataHolder = userDataHolder;
	}
	
	public void setStoreRef(Class<STORE> ref) {
		storeRef = ref;
	}
	
	public abstract STORE convertToStore(USE input);
	
	public abstract USE convertToUse(STORE input);

	public Class<STORE> getStoreClass() {
		return storeRef;
	}
	
	
}
