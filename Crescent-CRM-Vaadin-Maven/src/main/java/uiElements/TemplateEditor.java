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
	//Build an ArrayList of TemplateRowUI's
	ArrayList<TemplateRowUI> fieldArrayList = new ArrayList<TemplateRowUI>();
	
	//Create an instance of udh
	/*
	UserDataHolder udh;
	
	public UserDataHolder getUdh() {
		return this.udh;
	}

	public void setUdh(UserDataHolder udh) {
		this.udh = udh;
	}
	*/
	
	Button Add = new Button("Add New Field", event -> this.addRowClick());
	
	
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
		//udh.remove(udh.getMap(TemplateField.class).get(templateRowUI.getTemplateFieldName()), TemplateEditor.class);
		
		updateUI();
	}

	public void updateTemplates(UserDataHolder userDataHolder) {
		
		//for efficieny, lets store a cache and fill it up
		ArrayList<String> fieldNameCache = new ArrayList<String>();
		for(TemplateRowUI row :fieldArrayList){
			
			TemplateField tF = row.genTemplateField();
			
			fieldNameCache.add(tF.getFieldName());
			
			userDataHolder.store(tF, TemplateField.class);
		}
		
		ArrayList<TemplateField> removeList = new ArrayList<TemplateField>();
		//Remove items no longer in the fieldArrayList (utilizing the cache)
		for (TemplateField tf : userDataHolder.getMap(TemplateField.class).values()) {
			
			//check to see if the template field is in the cached arraylist
			if (!fieldNameCache.contains(tf.getFieldName())) {
				removeList.add(tf);
			}
			
		}
		
		//loop through the remove list, and remove it from the user data holder
		for (TemplateField tf : removeList) {
			//maybe I should have called it remove
			//either that or make it deleteList 
			//remind me to refactor  later
			userDataHolder.delete(tf, TemplateField.class);
		}
	}
	
	public void loadTemplateRows(UserDataHolder userDataHolder) {
		//this.removeAllComponents();
		fieldArrayList.clear();
		
		for (TemplateField tf : userDataHolder.getMap(TemplateField.class).values()) {
			TemplateRowUI loadedRow = new TemplateRowUI(this);
			loadedRow.load(tf);
			fieldArrayList.add(loadedRow);
		}
		updateUI();
	}
	
}
