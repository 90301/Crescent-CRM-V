/*
 * Author: Andrew Dorsett
 * Last Edited: 11/22/2016
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
	//Dates, Text, Numbers
	
	//Use this for date selection in Vaadin
	// new PopupDateField("");
	ArrayList<HorizontalLayout> z = new ArrayList<HorizontalLayout>();
	//HorizontalLayout x = new HorizontalLayout();
	VerticalLayout y = new VerticalLayout();
	
	//Need to make a list of all Field Types: Numbers, Dates, URLs, etc.
	ComboBox FieldType = new ComboBox("Field Type");    
	TextField TF = new TextField("Text Field");
	Button Add = new Button("Add New Line", event -> this.addRowClick());
	Button Remove = new Button("Remove", event -> this.removeRowClick());
	Label Warning= new Label("Are you sure you would like to remove this row? (Press again to remove the row)");
	
	//Make an ArrayList to keep it all organized and ordered :D
	public Layout generateTemplateRow(){
		HorizontalLayout x = new HorizontalLayout();
		z.add(x);
		//Need to make sure this method creates a new horizontal layout every time it is called
		//and that it doesn't add onto 
		x.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
		//If FieldType is null or a type that we don't have, then do not allow them to add it.
		x.addComponent(FieldType);
		x.addComponent(TF);
		//Possibly make red :D and also warning when selecting	
		x.addComponent(Remove);
		return x;
	}
	
	public Layout addNewField(){
		
		generateTemplateRow();
		y.removeAllComponents();
		for(HorizontalLayout h:z){
			y.addComponent(h);
		}
		y.addComponent(Add);
		
		
		return y;
	}
	
	private void addRowClick(){
		// TODO Auto-generated method stub
		
		addNewField();
		
	}
	
	private void removeRowClick() {
		// TODO Auto-generated method stub
		//eventually add Warning Label to this
		
	}
	
	//TODO
	//Need to add click event on the add new line button which will generate a new row.
	
}
