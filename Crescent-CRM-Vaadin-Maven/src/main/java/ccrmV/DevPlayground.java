package ccrmV;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

import integrations.SuperRest;

public class DevPlayground extends CrescentView {

	VerticalLayout testBtns = new VerticalLayout();
	
	Button firebaseGetTokenBtn = new Button("getToken",e -> firebaseGetTokenClick());
	
	
	@Override
	public void enterView(ViewChangeEvent event) {
		testBtns.setSpacing(true);
		
		testBtns.addComponent(firebaseGetTokenBtn);
		
		this.addComponent(testBtns);
		
	}


	private void firebaseGetTokenClick() {
		SuperRest.getToken();
	}

}
