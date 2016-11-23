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

public class TemplateEditor extends VerticalLayout{
	
	
	//TODO
	//Flags
	int removeFlag = 0;
	//Dates, Text, Numbers
	
	//Use this for date selection in Vaadin
	// new PopupDateField("");
	ArrayList<HorizontalLayout> fieldArrayList = new ArrayList<HorizontalLayout>();
	//HorizontalLayout x = new HorizontalLayout();
	
	//Need to make a list of all Field Types: Numbers, Dates, URLs, etc.
	
	Button Add = new Button("Add New Field", event -> this.addRowClick());
	
	
	
	public TemplateEditor() {
		
		updateUI();
	}

	//Make an ArrayList to keep it all organized and ordered :D
	public Layout generateTemplateRow(){
		//fieldArrayList.addComponent(TemplateRowUI());
		/*
		ComboBox FieldType = new ComboBox("Field Type");    
		TextField TF = new TextField("Text Field");
		Button Remove = new Button("Remove", event -> this.removeRowClick());
		HorizontalLayout x = new HorizontalLayout();
		
		//Need to make sure this method creates a new horizontal layout every time it is called
		//and that it doesn't add onto 
		x.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
		//If FieldType is null or a type that we don't have, then do not allow them to add it.
		x.addComponent(FieldType);
		x.addComponent(TF);
		//Possibly make red :D and also warning when selecting	
		x.addComponent(Remove);
		*/
		return null;
		
	}
	
	public void updateUI(){
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
		// TODO Auto-generated method stub
		//Add new instance of TemplateRowUI here
		addNewField();
	}
	


	public void removeRow(TemplateRowUI templateRowUI) {
		// TODO Auto-generated method stub
		fieldArrayList.remove(templateRowUI);
		updateUI();
	}
	
	//TODO
	//Need to add click event on the add new line button which will generate a new row.
	
}
