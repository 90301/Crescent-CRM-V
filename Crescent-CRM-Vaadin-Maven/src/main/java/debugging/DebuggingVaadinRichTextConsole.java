package debugging;

import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.RichTextArea;

public class DebuggingVaadinRichTextConsole extends HorizontalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	RichTextArea console = new RichTextArea();
	
	public void genConsole() {
		console.clear();
		
		console.setSizeFull();
		console.setWidth("500px");
		console.setHeight("800px");
		this.addComponent(console);
	}
	
	public void setValue(String text) {
		console.setValue(text);
	}
}
