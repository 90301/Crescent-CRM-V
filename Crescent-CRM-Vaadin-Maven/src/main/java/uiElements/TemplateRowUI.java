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

import clientInfo.TemplateField;
import dbUtils.InhalerUtils;

public class TemplateRowUI extends HorizontalLayout{

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
		
		for(String DataType:TemplateField.DataTypes){
			fieldType.addItem(DataType);
		}
		fieldType.select(TemplateField.DATA_TYPE_TEXT);
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

	public TemplateField genTemplateField() {
		
		String fieldName = fieldNameTextField.getValue();
		String modFieldName = InhalerUtils.replaceStringSpaces(fieldName);
		fieldNameTextField.setValue(modFieldName);
		
		// TODO Auto-generated method stub
		TemplateField tF = new TemplateField();
		tF.setDataType(fieldType.getValue().toString());
		tF.setFieldName(modFieldName);
		tF.genDefaultValue();
		return tF;
		
	}
	
	public void load(TemplateField tf) {
		this.initializeUI();
		
		fieldNameTextField.setValue(tf.getFieldName());
		fieldType.setValue(tf.getDataType());
		
	}
}
