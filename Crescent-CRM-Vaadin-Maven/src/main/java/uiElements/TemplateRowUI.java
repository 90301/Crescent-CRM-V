/*
 * Author: Andrew Dorsett
 * Last Edited: 11/23/2016
 */

package uiElements;

import java.util.ArrayList;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class TemplateRowUI extends HorizontalLayout{

	//Constants
	public static final String DATA_TYPE_NUMBER = "Number";
	public static final String DATA_TYPE_DATE = "Date";
	public static final String DATA_TYPE_TEXT = "Text";
	public static final String[] DataTypes = {DATA_TYPE_NUMBER,DATA_TYPE_DATE, DATA_TYPE_TEXT};
	
	int removeFlag = 0;
	Label Warning= new Label("Are you sure? (Press remove again if you are)");
	TemplateEditor templateEditor;
	
	public TemplateRowUI() {
		// TODO Auto-generated constructor stub
		initializeUI();
		
	}

	public TemplateRowUI(TemplateEditor reference) {
		// TODO Auto-generated constructor stub
		this.templateEditor = reference;
		initializeUI();
		
	}
	
	ComboBox fieldType = new ComboBox("Field Type");    
	TextField fieldNameTextField = new TextField("Field Name");
	Button removeButton = new Button("Remove", event -> this.removeRowClick());
	
	public void initializeUI(){
		fieldType.setNullSelectionAllowed(false); 
		this.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);
		this.setSpacing(true);
		this.addComponent(fieldType);
		this.addComponent(fieldNameTextField);
		this.addComponent(removeButton);
		populateComboBoxes();
	}
	
	public void populateComboBoxes(){
		
		for(String DataType:DataTypes){
			fieldType.addItem(DataType);
		}
		fieldType.select(DATA_TYPE_TEXT);
	}
	
	private void removeRowClick() {
		// TODO Auto-generated method stub
		if(removeFlag == 0){
			addComponent(Warning);
			removeFlag++;
		}
		else{
			removeComponent(Warning);
			//Remove row process
			this.templateEditor.removeRow(this);
			removeFlag = 0;
		}
		
	}
}
