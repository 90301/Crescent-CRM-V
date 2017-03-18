package uiElements;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import ccrmV.CrmUI;
import clientInfo.Client;
import clientInfo.DataHolder;
import clientInfo.Group;
import clientInfo.Location;
import clientInfo.Status;
import clientInfo.UserDataHolder;
import debugging.Debugging;

public class ClientEditor extends VerticalLayout {

	public static final int MAX_NOTE_ROWS = 15;


	public static final String NOTE_WIDTH = "650px";


	//A reference to the CRM UI, useful to calling methods in the parent UI
	CrmUI crmUi;
	//IMPORTANT VARIABLES in CRM UI
	//SelectedClient - the currently selected client


	//VerticalLayout this = new VerticalLayout();
	HorizontalLayout clientEditorMetaLayout = new HorizontalLayout();
	HorizontalLayout clientEditorActionLayout = new HorizontalLayout();
	HorizontalLayout uploadProfileLayout = new HorizontalLayout();

	// Current Client Editing
	TextArea clientNoteBox  = new TextArea("Client Notes");
	ComboBox clientStatus = new ComboBox("Status"); 
	ComboBox clientLocation = new ComboBox("Location");
	ComboBox clientGroup = new ComboBox("Group");
	Button clientUpdateButton = new Button("Update", event -> this.updateClientClick());
	Button clientArchiveButton = new Button("Archive");
	Label clientNameLabel = new Label("Client Name");
	Label clientLastUpdate = new Label("Last Updated: --/--/----");
	CheckBox clientContactNowCheckBox = new CheckBox("Contact Now");
	ComboBox clientContactFrequency = new ComboBox("Contact Frequency");

	//Custom Fields
	TemplateEditor templateEditor = new TemplateEditor();
	CustomFieldEditor customFieldEditor = new CustomFieldEditor();

	//Profile Picture
	ProfilePicture pPicture = new ProfilePicture();
	UploadProfilePicture uploadProfilePicture = new UploadProfilePicture();

	public ClientEditor(CrmUI crmUi) {
		this.crmUi = crmUi;
		genClientEditor();
	}

	/**
	 * Adds all the components for the clientEditor
	 */
	private void genClientEditor() {


		//clientNameLabel 

		//clientNoteBox
		clientNoteBox.setSizeFull();
		clientNoteBox.setWidth(NOTE_WIDTH);
		clientNoteBox.setResponsive(true);

		//clientLocation 
		clientLocation.setNullSelectionAllowed(false);
		clientLocation.setInvalidAllowed(false);

		//clientGroup 
		clientGroup.setNullSelectionAllowed(false);
		clientGroup.setInvalidAllowed(false);

		//clientStatus 
		clientStatus.setNullSelectionAllowed(false);
		clientStatus.setInvalidAllowed(false);

		clientLastUpdate.setSizeFull();
		// client editing events

		//Editor for the client meta data (location status group)
		clientEditorMetaLayout.setSpacing(true);
		clientEditorMetaLayout.setSizeFull();

		clientEditorMetaLayout.addComponent(clientLocation);
		clientEditorMetaLayout.addComponent(clientStatus);
		clientEditorMetaLayout.addComponent(clientGroup);

		//Editor actions (archive update ect)
		clientEditorActionLayout.setSpacing(true);

		clientEditorActionLayout.addComponent(clientLastUpdate);
		clientEditorActionLayout.addComponent(clientUpdateButton);
		clientEditorActionLayout.addComponent(clientArchiveButton);
		clientEditorActionLayout.addComponent(clientContactNowCheckBox);
		clientEditorActionLayout.addComponent(clientContactFrequency);

		//Profile Picture Horizontal Layout
		uploadProfileLayout.setSpacing(true);
		uploadProfilePicture.addUploadUI();
		uploadProfileLayout.addComponent(pPicture);
		uploadProfileLayout.addComponent(uploadProfilePicture);
		
		
		
		
		//Template editor
		templateEditor.updateUI();
		templateEditor.setVisible(false);

		//templateEditor.setUdh(crmUi.masterUi.userDataHolder);

		//Custom Field Editor
		//This is buggy and has been replaced
		//customFieldEditor.setUserDataHolder(crmUi.masterUi.userDataHolder);

		//holds the client editor
		this.setSpacing(true);

		this.addComponent(clientNameLabel);

		this.addComponent(clientEditorMetaLayout);
		
		this.addComponent(uploadProfileLayout);

		this.addComponent(templateEditor);
		this.addComponent(customFieldEditor);

		this.addComponent(clientNoteBox);
		this.addComponent(clientEditorActionLayout);
	}

	/**
	 * Method called when updating a client with the latest information
	 * Fired when clicked
	 */
	private void updateClientClick() {



		// UPDATE fields in client

		crmUi.selectedClient.setNotes(clientNoteBox.getValue());

		// Resolve field

		// Template code
		if (crmUi.selectedClient.getName().contains(DataHolder.TEMPLATE_STRING)) {

			//Group tGroup = crmUi.masterUi.userDataHolder.getGroup(DataHolder.TEMPLATE_STRING);
			//Location tLocation = crmUi.masterUi.userDataHolder.getLocation(DataHolder.TEMPLATE_STRING);
			//Status tStatus = crmUi.masterUi.userDataHolder.getStatus(DataHolder.TEMPLATE_STRING);
			crmUi.selectedClient.setGroup(DataHolder.TEMPLATE_STRING);
			crmUi.selectedClient.setLocation(DataHolder.TEMPLATE_STRING);
			crmUi.selectedClient.setStatus(DataHolder.TEMPLATE_STRING);

			Debugging.output("Setting Template Values for status / location / group.",Debugging.TEMPLATE_DEBUG);

			//Update the fields
			templateEditor.updateTemplates(crmUi.masterUi.userDataHolder);

		} else {
			// normal client creation
			// if valid, set the field
			Group cGroup = crmUi.masterUi.userDataHolder.getGroup((String) clientGroup.getValue());
			if (cGroup != null) {
				crmUi.selectedClient.setGroup(cGroup);
			}
			// Resolve field
			Location cLocation = crmUi.masterUi.userDataHolder.getLocation((String) clientLocation.getValue());
			// if valid, set the field
			if (cLocation != null) {
				crmUi.selectedClient.setLocation(cLocation);
			}
			// Resolve field
			Status cStatus = crmUi.masterUi.userDataHolder.getStatus((String) clientStatus.getValue());
			// if valid, set the field
			if (cStatus != null) {
				crmUi.selectedClient.setStatus(cStatus);
			}

		}
		crmUi.selectedClient.setLastUpdatedToNow();

		crmUi.selectedClient = customFieldEditor.updateClient(crmUi.selectedClient);

		crmUi.selectedClient.setContactNow(clientContactNowCheckBox.getValue());

		String uPP = uploadProfilePicture.updateProfilePicture();
		if(uPP != null){
		crmUi.selectedClient.setProfilePicture(uPP);
		
		uploadProfilePicture.setLink(null);
		}
		
		
		crmUi.masterUi.userDataHolder.store(crmUi.selectedClient, Client.class);

		crmUi.updateClientGrid();
		//reset the filter if the selected client doesn't meet the current filter
		if (!crmUi.clientFilter.checkClientMeetsFilter(crmUi.selectedClient)) {
			crmUi.clientFilter.resetFilterClick();
		}
		crmUi.selectClient(crmUi.selectedClient);

		Debugging.TEMPLATE_DEBUG.outputLog();
	}

	public void updateAllComboBoxes() {
		clientStatus.clear();
		clientLocation.clear();
		clientGroup.clear();

		clientStatus.removeAllItems();
		clientLocation.removeAllItems();
		clientGroup.removeAllItems();

		// Client editor
		crmUi.fillComboBox(clientStatus, crmUi.masterUi.userDataHolder.getAllStatus());
		crmUi.fillComboBox(clientLocation, crmUi.masterUi.userDataHolder.getAllLocations());
		crmUi.fillComboBox(clientGroup, crmUi.masterUi.userDataHolder.getAllGroups());

	}

	public void selectClient(Client c) {

		if (c != null) {

		} else {
			Debugging.output("Null value made it to selectClient: " + c,Debugging.OLD_OUTPUT);
			return;
		}
		//Set the user data holder
		c.setUserDataHolder(crmUi.masterUi.userDataHolder);
		//clientTable.select(c.getName());
		crmUi.selectedClient = c;
		//Show template editor
		if (c.getName().contains(DataHolder.TEMPLATE_STRING)) {
			templateEditor.setVisible(true);
			customFieldEditor.setVisible(false);
			templateEditor.loadTemplateRows(crmUi.masterUi.userDataHolder);
		} else {
			templateEditor.setVisible(false);
			customFieldEditor.loadCustomFields(c,crmUi.masterUi.userDataHolder);
			customFieldEditor.setVisible(true);
		}

		Debugging.output("showing client information for: " + c,Debugging.OLD_OUTPUT);
		// TODO: load information into the ui.
		// LOAD INFORMATION
		clientNameLabel.setValue(c.getName());
		clientStatus.setValue(c.getStatusName());
		clientLocation.setValue(c.getLocationName());
		clientGroup.setValue(c.getGroupName());

		clientNoteBox.setValue(c.getNotes());
		//clientNoteBox.setRows(Math.min(c.getNotes().split("\\r?\\n").length+2,MAX_NOTE_ROWS));
		clientNoteBox.setRows(MAX_NOTE_ROWS);
		// set last updated
		if (c.getLastUpdated() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("MMMM-dd-yy h:mm a");
			Date resultdate = c.getLastUpdated();

			clientLastUpdate.setValue(sdf.format(resultdate));
		} else {
			clientLastUpdate.setValue("Never updated");
		}

		clientContactNowCheckBox.setValue(c.getContactNow());

		//Profile picture
		pPicture.loadprofilePictureField(c);

	}

	/**
	 * Checks to see if the client has been updated
	 * @return TRUE is client has been updated
	 */
	public boolean checkUpdate() {
		//check notes
		Boolean rtrn = false;
		if (crmUi.selectedClient != null) {
			if (!crmUi.selectedClient.getNotes().equals(clientNoteBox.getValue())) {
				//notes have changed
				rtrn = true;
			} else if (crmUi.selectedClient.getContactNow() != clientContactNowCheckBox.getValue()) {
				//contact now has changed
				rtrn = true;
			} else if(uploadProfilePicture.getHasUploaded()){
				rtrn = true;
			}
		}
		return rtrn;
	}

}
