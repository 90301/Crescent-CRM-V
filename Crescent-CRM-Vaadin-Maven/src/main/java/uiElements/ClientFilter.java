package uiElements;

import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.TextField;

import ccrmV.CrmUI;
import clientInfo.Client;
import clientInfo.Group;
import clientInfo.Location;
import clientInfo.Status;
import dbUtils.InhalerUtils;
import debugging.DebugObject;
import debugging.Debugging;
import debugging.profiling.ProfilingTimer;

public class ClientFilter extends HorizontalLayout implements Filter {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean DEFAULT_FILTER_SHOW = true;

	private static final boolean MORE_EFFICIENT_FILTERING = true;

	CrmUI crmUi = null;
	
	// filtering
	HorizontalLayout filterLayout = new HorizontalLayout();
	Button filterShowFilter = new Button(">>",e -> showFilterClick());
	Button filterHideFilter = new Button("<<", e -> hideFilterClick());
	Boolean filterShowing = false;
	
	ComboBox<Status> filterStatus  = new ComboBox<>("Status");
	ComboBox<Location> filterLocation = new ComboBox<>("Location");
	ComboBox<Group> filterGroup = new ComboBox<>("Group");
	Button filterButton = new Button("Filter", event -> this.updateFilter());
	Button resetFilterButton = new Button("Reset", event -> this.resetFilterClick());;
	TextField filterClientTextField = new TextField(" Name ");
	TextField filterClientNotesField = new TextField("Notes Include:");
	Label filterLabel = new Label("Filter :");
	CheckBox filterContactNowCheckBox = new CheckBox("Contact Now Only");

	//Determine if Filter Has Changed
	Boolean filterHasChanged = false;
	
	
	{
		filterClientTextField.addValueChangeListener(e -> updateFilter());
		filterStatus.addValueChangeListener(e -> updateFilter());
		filterLocation.addValueChangeListener(e -> updateFilter());
		filterGroup.addValueChangeListener(e -> updateFilter());
		filterClientNotesField.addValueChangeListener(e -> updateFilter());
		filterContactNowCheckBox.addValueChangeListener(e -> updateFilter());
	}
	
	/**
	 * Pulls in the crmUi to be used
	 * @param crmUi
	 */
	public ClientFilter(CrmUI crmUi) {
		this.crmUi = crmUi;
		this.generateLayout();
	}
	
	/*
	 * Click Events
	 */
	
	private void hideFilterClick() {
		setFilterShow(false);
	}

	private void showFilterClick() {
		setFilterShow(true);
	}

	
	public void resetFilterClick() {
		filterStatus.setValue(null);
		filterLocation.setValue(null);
		filterGroup.setValue(null);
		filterClientTextField.setValue("");
		filterClientNotesField.setValue("");
		filterContactNowCheckBox.setValue(false);
		updateFilter();
	}
	
	public void updateAllComboBoxes() {
		filterStatus.clear();
		filterLocation.clear();
		filterGroup.clear();
		
		crmUi.fillComboBox(filterStatus, crmUi.masterUi.userDataHolder.getAllStatus());
		crmUi.fillComboBox(filterLocation, crmUi.masterUi.userDataHolder.getAllLocations());
		crmUi.fillComboBox(filterGroup, crmUi.masterUi.userDataHolder.getAllGroups());
	}
	
	public void setFilterShow(boolean showFilter) {
		this.filterShowing = showFilter;
		
		filterStatus.setVisible(showFilter);
		filterLocation.setVisible(showFilter);
		filterGroup.setVisible(showFilter);
		
		filterButton.setVisible(showFilter);
		
		resetFilterButton.setVisible(showFilter);
		filterClientTextField.setVisible(showFilter);
		filterClientNotesField.setVisible(showFilter);
		filterContactNowCheckBox.setVisible(showFilter);
		
		filterShowFilter.setVisible(!showFilter);
		filterHideFilter.setVisible(showFilter);
	}
	
	/**
	 * Adds all the components related to the filter layout
	 */
	public void generateLayout() {
		
		this.setSpacing(true);
		this.setMargin(false);
		//this.addStyleName("filterBorder");
		//this.setColumns(8);
		this.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
		this.addComponent(filterLabel);
		
		

		//filterClientTextField 
		this.addComponent(filterShowFilter);
		
		this.addComponent(filterClientTextField);
		//filterStatus
		this.addComponent(filterStatus);
		//filterLocation 
		this.addComponent(filterLocation);
		//filterGroup 
		this.addComponent(filterGroup);
		// filter notes

		this.addComponent(filterClientNotesField);
		
		this.addComponent(filterContactNowCheckBox);

		//filterButton
		this.addComponent(filterButton);
		//resetFilterButton 
		this.addComponent(resetFilterButton);
		
		this.addComponent(filterHideFilter);
		
		setFilterShow(DEFAULT_FILTER_SHOW);
		
	}
	
	public void updateFilter() {
		ProfilingTimer updateFilterTime = new ProfilingTimer("Update Filter Time");
		//TODO, may be able to make this more efficent by directly adding the filter to the grid
		//instead of doing a full refresh
		if (MORE_EFFICIENT_FILTERING) {
			crmUi.clients.removeAllContainerFilters();
			//ensure not everything is null
			if (!isBlankFilter()) {
				crmUi.clients.addContainerFilter(this);
			}
		} else {
			this.filterHasChanged = true;
			crmUi.updateClientGrid();
		}
		
		updateFilterTime.stopTimer();
	}
	
	/**
	 * Tests to see if the filter is blank (no filter)
	 * @return if the filter is default (no filter) (true)
	 */
	public boolean isBlankFilter() {
		if (filterGroup.getValue()!=null
				&& filterStatus.getValue()!=null
				&& filterLocation.getValue()!=null
				&& filterClientTextField.getValue()!=null
				&& filterClientNotesField.getValue()!=null
				&& filterContactNowCheckBox.getValue()!=false
				) {
		return true;	
		}
		
		return false;
	}

	/**
	 * Checks to see if a client meets the current filter
	 * if it does, return true.
	 * Intended for use with reseting the filter, and in the future
	 * this may be used to actually replace the current filter code.
	 * 
	 * @param c the client to check if it meets the filter
	 * @return if the client should be show with the current filter.
	 */
	public boolean checkClientMeetsFilter(Client c) {
		
		
		Status filterStatusTest = null;
		if (filterStatus.getValue() != null)
			filterStatusTest = crmUi.masterUi.userDataHolder.getStatus(filterStatus.getValue().toString());
		Location filterLocationTest = null;
		if (filterLocation.getValue() != null)
			filterLocationTest = crmUi.masterUi.userDataHolder.getLocation(filterLocation.getValue().toString());
		Group filterGroupTest = null;
		if (filterGroup.getValue() != null)
			filterGroupTest = crmUi.masterUi.userDataHolder.getGroup(filterGroup.getValue().toString());

		String filterNameTest = null;
		if (filterClientTextField.getValue() != null && !InhalerUtils.stringNullCheck(filterClientTextField.getValue()))
			filterNameTest = filterClientTextField.getValue().toString();
		String[] filterNotesTests = null;
		if (filterClientNotesField.getValue() != null && !InhalerUtils.stringNullCheck(filterClientNotesField.getValue()))
			filterNotesTests = filterClientNotesField.getValue().toLowerCase().split("\\s+");
		
		Boolean contactNowOnly = filterContactNowCheckBox.getValue();

		// filter settings

		if ((filterStatusTest == c.getStatus() || filterStatusTest == null)
				&& (filterLocationTest == c.getLocation() || filterLocationTest == null)
				&& (filterGroupTest == c.getGroup() || filterGroupTest == null)
				&& (filterNameTest == null || c.getName().toLowerCase().contains(filterNameTest.toLowerCase()))
				&& (!contactNowOnly || c.getContactNow())) {

			boolean noteQueryFound = true;
			// Make sure the notes contain all the terms
			if (filterNotesTests != null)
				for (String noteKeyword : filterNotesTests) {
					if (!c.getNotes().toLowerCase().contains(noteKeyword)) {
						noteQueryFound = false;
					}
				}
			if (noteQueryFound == true) {
				//meets the conditions, return true
				return true;
				
			}
		}
		//does not meet all the conditions, return false
		return false;
	}

	/**
	 * Filters clients, Note that itemId is the actual client.
	 */
	@Override
	public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
		if (itemId != null && item != null) {
			
			Debugging.output("ItemID: " + itemId, Debugging.FILTER2);
			
			Client c = (Client) itemId;
			
			Debugging.output("client: " + c, Debugging.FILTER2);
			
			if (c!=null && checkClientMeetsFilter(c)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * No clue what this does yet -Josh
	 * TODO Implement this
	 */
	@Override
	public boolean appliesToProperty(Object propertyId) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Boolean getFilterHasChanged() {
		return filterHasChanged;
	}
	
	public void setFilterHasChanged(Boolean changed) {
		this.filterHasChanged = changed;
	}
}
