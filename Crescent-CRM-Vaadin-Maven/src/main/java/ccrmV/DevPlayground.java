package ccrmV;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import clientInfo.DataHolder;
import integrations.SuperRest;

public class DevPlayground extends CrescentView {

	VerticalLayout testBtns = new VerticalLayout();
	
	Button firebaseGetTokenBtn = new Button("getToken",e -> firebaseGetTokenClick());
	
	//push bullet
	TextField pushBulletKeyTextField = new TextField("Push Bullet Key: ");
	Button pushBulletBtn = new Button("set Push Bullet key",e -> setPushBulletKeyClick());
	Button pushBulletTestBtn = new Button("Push Bullet Test",e -> SuperRest.connectToPushBullet(masterUi.getUser()));
	
	@Override
	public void enterView(ViewChangeEvent event) {
		testBtns.setSpacing(true);
		
		testBtns.addComponent(firebaseGetTokenBtn);
		
		testBtns.addComponent(pushBulletKeyTextField);
		testBtns.addComponent(pushBulletBtn);
		testBtns.addComponent(pushBulletTestBtn);
		
		this.addComponent(testBtns);
		
	}


	private void setPushBulletKeyClick() {
		this.masterUi.getUser().setPushBulletKey(pushBulletKeyTextField.getValue());
		users.User u = this.masterUi.getUser();
		DataHolder.store(u,users.User.class);
	}


	private void firebaseGetTokenClick() {
		SuperRest.regServiceWorker();
		
		SuperRest.requestPermission();
		SuperRest.getToken();
	}

}
