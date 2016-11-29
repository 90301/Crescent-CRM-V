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
import com.vaadin.ui.Layout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import clientInfo.TemplateField;
import clientInfo.UserDataHolder;

public class TemplateEditor extends VerticalLayout{
	
	/* Use for replacing space with hyphen in Inhaler.utils
	 * 
	 * String replaceText = "AT AT";
	 * replaceText = replaceText.replace(' ', '-');
	 *
	 */
	
	
	//TODO
	//Flags
	int removeFlag = 0;
	//Dates, Text, Numbers
	
	//Use this for date selection in Vaadin
	// new PopupDateField("");
	ArrayList<TemplateRowUI> fieldArrayList = new ArrayList<TemplateRowUI>();
	//HorizontalLayout x = new HorizontalLayout();
	
	//Need to make a list of all Field Types: Numbers, Dates, URLs, etc.
	
	Button Add = new Button("Add New Field", event -> this.addRowClick());
	
	//UserDataHolder userDataHolder;
	
	public TemplateEditor() {
		
		updateUI();
	}
	
	public void updateUI(){
		
		this.setSpacing(true);
		
		this.removeAllComponents();
		
		for(HorizontalLayout h:fieldArrayList){
			this.addComponent(h);
		}
		this.addComponent(Add);
	}
	
	public void addNewField(){
		//Call TemplateRowUI
		TemplateRowUI newRow = new TemplateRowUI(this);
		fieldArrayList.add(newRow);
		updateUI();
		
	}
	
	private void addRowClick(){
		//Add new instance of TemplateRowUI here
		addNewField();
	}
	

	
	public void removeRow(TemplateRowUI templateRowUI) {
		// TODO Auto-generated method stub
		fieldArrayList.remove(templateRowUI);
		updateUI();
	}

	public void updateTemplates(UserDataHolder userDataHolder) {
		for(TemplateRowUI row :fieldArrayList){
			
			TemplateField tF = row.genTemplateField();
			userDataHolder.store(tF, TemplateField.class);
		}
	}
	
}
