package uiElements;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
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
import dbUtils.InhalerUtils;
import debugging.Debugging;

public class ClientEditor extends VerticalLayout {

	public static final int MAX_NOTE_ROWS = 15;


	public static final String NOTE_WIDTH = "650px";


	//A reference to the CRM UI, useful to calling methods in the parent UI
	CrmUI crmUi;
	//IMPORTANT VARIABLES in CRM UI
	//SelectedClient - the currently selected client


	//VerticalLayout this = new VerticalLayout();
	HorizontalLayout clientEditorNameLayout = new HorizontalLayout();
	HorizontalLayout clientEditorMetaLayout = new HorizontalLayout();
	HorizontalLayout clientEditorActionLayout = new HorizontalLayout();
	HorizontalLayout uploadProfileLayout = new HorizontalLayout();
	HorizontalLayout noteHistoryLayout = new HorizontalLayout();

	// Current Client Editing
	TextArea clientNoteBox  = new TextArea("Client Notes");
	ComboBox<Status> clientStatus = new ComboBox<Status>("Status"); 
	ComboBox<Location> clientLocation = new ComboBox<Location>("Location");
	ComboBox<Group> clientGroup = new ComboBox<Group>("Group");
	Button clientUpdateButton = new Button("Update", event -> this.updateClientClick());
	Button clientArchiveButton = new Button("Archive", e-> this.archiveClick());
	Label clientNameLabel = new Label("Client Name");
	Label clientLastUpdate = new Label("Last Updated: --/--/----");
	CheckBox clientContactNowCheckBox = new CheckBox("Contact Now");
	
	//Rename clients
	Button clientRenameButton = new Button("Rename", e-> this.renameClick());
	TextField clientRenameTextField = new TextField("New Name");
	//ComboBox clientContactFrequency = new ComboBox("Contact Frequency");

	//Custom Fields
	TemplateEditor templateEditor = new TemplateEditor();
	CustomFieldEditor customFieldEditor = new CustomFieldEditor();

	//Profile Picture
	ProfilePicture pPicture = new ProfilePicture();
	UploadProfilePicture uploadProfilePicture = new UploadProfilePicture();
	
	//Note History
	ComboBox noteHistoryComboBox = new ComboBox("Note History");
	Button noteHistoryPreviewButton = new Button("Preview",e ->this.noteHistoryPreviewClick());
	Button noteHistoryLoadButton = new Button("Load",e -> this.noteHistoryLoadClick());
	TextArea notePreviewBox = new TextArea("Preview");

	public ClientEditor(CrmUI crmUi) {
		this.crmUi = crmUi;
		genClientEditor();
	}

	/**
	 * Adds all the components for the clientEditor
	 */
	private void genClientEditor() {


		//clientNameLabel 
		clientEditorNameLayout.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);
		
		clientEditorNameLayout.addComponent(clientNameLabel);
		clientEditorNameLayout.addComponent(clientRenameTextField);
		
		clientEditorNameLayout.addComponent(clientRenameButton);

		//clientNoteBox
		clientNoteBox.setSizeFull();
		clientNoteBox.setWidth(NOTE_WIDTH);
		clientNoteBox.setResponsive(true);

		clientLastUpdate.setSizeFull();
		
		notePreviewBox.setWidth(NOTE_WIDTH);
		notePreviewBox.setEnabled(false);
		notePreviewBox.setVisible(false);
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
		//clientEditorActionLayout.addComponent(clientContactFrequency);

		//Profile Picture Horizontal Layout
		uploadProfileLayout.setSpacing(true);
		uploadProfilePicture.addUploadUI();
		uploadProfileLayout.addComponent(pPicture);
		uploadProfileLayout.addComponent(uploadProfilePicture);
		
		
		noteHistoryLayout.setSpacing(true);
		noteHistoryLayout.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);
		noteHistoryLayout.addComponent(noteHistoryComboBox);
		noteHistoryLayout.addComponent(noteHistoryPreviewButton);
		noteHistoryLayout.addComponent(noteHistoryLoadButton);
		
		
		
		//Template editor
		templateEditor.updateUI();
		templateEditor.setVisible(false);

		//templateEditor.setUdh(crmUi.masterUi.userDataHolder);

		//Custom Field Editor
		//This is buggy and has been replaced
		//customFieldEditor.setUserDataHolder(crmUi.masterUi.userDataHolder);

		//holds the client editor
		this.setSpacing(true);

		this.addComponent(clientEditorNameLayout);

		this.addComponent(clientEditorMetaLayout);
		
		//TODO
		this.addComponent(uploadProfileLayout);
		uploadProfileLayout.setVisible(false);
		
		this.addComponent(templateEditor);
		this.addComponent(customFieldEditor);

		this.addComponent(clientNoteBox);
		this.addComponent(notePreviewBox);
		
		this.addComponent(noteHistoryLayout);
		
		this.addComponent(clientEditorActionLayout);
		
		
		this.toggleRename(false);
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
			Group g = clientGroup.getValue();
			if (g!=null) {
			Group cGroup = crmUi.masterUi.userDataHolder.getGroup(g.getPrimaryKey());
			
			if (cGroup != null) {
				crmUi.selectedClient.setGroup(cGroup);
			}
			}
			// Resolve field
			
			Location l = clientLocation.getValue();
			if (l!=null) {
			Location cLocation = crmUi.masterUi.userDataHolder.getLocation(l.getPrimaryKey());
			// if valid, set the field
			if (cLocation != null) {
				crmUi.selectedClient.setLocation(cLocation);
			}
			}
			// Resolve field
			Status s = clientStatus.getValue();
			if (s!=null) {
			Status cStatus = crmUi.masterUi.userDataHolder.getStatus(s.getPrimaryKey());
			// if valid, set the field
			if (cStatus != null) {
				crmUi.selectedClient.setStatus(cStatus);
			}
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
	
	public void noteHistoryLoadClick() {
		if (noteHistoryComboBox.getValue()!=null && crmUi.selectedClient!=null) {
			String key = "" + noteHistoryComboBox.getValue();
			this.clientNoteBox.setValue(crmUi.selectedClient.getNoteHistory().get(key));
		}
	}

	public void noteHistoryPreviewClick() {
		if (noteHistoryComboBox.getValue()!=null && crmUi.selectedClient!=null) {
			String key = "" + noteHistoryComboBox.getValue();
			this.notePreviewBox.setValue(crmUi.selectedClient.getNoteHistory().get(key));
			this.notePreviewBox.setVisible(true);
		}
	}
	
	public void archiveClick() {
		UserDataHolder udh = crmUi.masterUi.userDataHolder;
		if (crmUi.selectedClient!=null) {
			udh.archiveClient(crmUi.selectedClient);
		}
		crmUi.selectClient(null);
		crmUi.updateClientGrid();
		
	}
	
	public void renameClick() {
		//TODO write unit test for this
		
		//if not showing textbox, show textbox
		if (!renameOn) {
			toggleRename(true);
		} else {
			//if showing textbox, and name is different but not blank, attemot to change the name.
			String updatedName = clientRenameTextField.getValue();
			if (!InhalerUtils.stringNullCheck(updatedName) && this.crmUi.selectedClient!=null && !updatedName.contentEquals(this.crmUi.selectedClient.getName())) {
				boolean clientRenamed = this.crmUi.masterUi.userDataHolder.renameClient(this.crmUi.selectedClient, updatedName);
				 
				if (!clientRenamed) {
					//show error message
					Notification n = new Notification("Client with that name already exists.");
					n.setDelayMsec(200);
					n.show(UI.getCurrent().getPage());
					
				} else {
					//update the grid
					this.crmUi.updateClientGrid();
					this.toggleRename(false);
					
					Notification n = new Notification("Client renamed to: " + updatedName);
					n.setDelayMsec(200);
					n.show(UI.getCurrent().getPage());
				}
			} else {
				Notification n = new Notification("Client name is not valid or not changed.");
				n.setDelayMsec(200);
				n.show(UI.getCurrent().getPage());
			}
		}
		
	}
	
	boolean renameOn = false;
	
	public void toggleRename(Boolean renameOn) {

			this.clientNameLabel.setVisible(!renameOn);
			this.clientRenameTextField.setVisible(renameOn);
			if (this.crmUi.selectedClient!=null) {
				this.clientNameLabel.setValue(this.crmUi.selectedClient.getName());
				this.clientRenameTextField.setValue(this.crmUi.selectedClient.getName());
			}
			if (renameOn) {
				this.clientRenameButton.setCaption("Set Name");
			} else {
				this.clientRenameButton.setCaption("Edit Name");
			}
			//TODO make these constants
			this.renameOn = renameOn;
	}
	

	public void updateAllComboBoxes() {
		clientStatus.setValue(clientStatus.getEmptyValue());
		clientLocation.setValue(clientLocation.getEmptyValue());
		clientGroup.setValue(clientGroup.getEmptyValue());

		// Client editor

		clientStatus.setItems(crmUi.masterUi.userDataHolder.getAllStatus());
		clientLocation.setItems(crmUi.masterUi.userDataHolder.getAllLocations());
		clientGroup.setItems(crmUi.masterUi.userDataHolder.getAllGroups());
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
		// LOAD INFORMATION
		clientNameLabel.setValue(c.getName());
		clientStatus.setValue(c.getStatus());
		clientLocation.setValue(c.getLocation());
		clientGroup.setValue(c.getGroup());

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
		
		//note history
		noteHistoryComboBox.setItems(
				InhalerUtils.reverseList(
						c.getNoteHistory().keySet()));
		
		this.notePreviewBox.setVisible(false);
		this.toggleRename(false);
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
