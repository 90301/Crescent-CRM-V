package debugging;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RichTextArea;

public class DebuggingVaadinRichTextConsole extends HorizontalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	RichTextArea console = new RichTextArea();
	
	public void genConsole() {
		console.clear();
		this.addComponent(console);
	}
	
	public void setValue(String text) {
		console.setValue(text);
	}
}
