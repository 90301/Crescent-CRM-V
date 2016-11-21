package uiElements;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class TemplateEditor extends VerticalLayout{
	
	
	//Use this for date selection in Vaadin
	// new PopupDateField("");
	HorizontalLayout x;
	VerticalLayout y = new VerticalLayout();
	
	//Need to make a list of all Field Types
	ComboBox FieldType = new ComboBox("Field Type");
	TextField TF = new TextField();
	Button Add = new Button("Add New Line", event -> this.addRowClick());
	Button Remove = new Button("Remove", event -> this.removeRowClick());
	Label Warning= new Label("Are you sure you would like to remove this row? (Press again to remove the row)");
	
	public Layout generateTemplateRow(){
		//Need to make sure this method creates a new horizontal layout every time it is called
		//and that it doesn't add onto 
		HorizontalLayout x = new HorizontalLayout();
		//If FieldType is null or a type that we don't have, then do not allow them to add it.
		x.addComponent(FieldType);
		x.addComponent(TF);
		//Possibly make red :D and also warning when selecting
		x.addComponent(Remove);
		return x;
	}
	
	public Layout addNewLine(){
		y.addComponent(x);
		y.addComponent(Add);
		
		
		return y;
	}
	
	private void addRowClick() {
		// TODO Auto-generated method stub
		generateTemplateRow();
	}
	
	private void removeRowClick() {
		// TODO Auto-generated method stub
		//eventually add Warning Label to this
	}
	
	//TODO
	//Need to add click event on the add new line button which will generate a new row.
	
}
