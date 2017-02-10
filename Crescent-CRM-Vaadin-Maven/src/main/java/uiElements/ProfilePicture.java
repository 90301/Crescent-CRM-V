package uiElements;

import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.server.FileResource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import java.io.File;

import org.apache.http.conn.scheme.HostNameResolver;

import uiElements.UploadProfilePicture;
import clientInfo.Client;
import clientInfo.DataHolder;
import clientInfo.UserDataHolder;
import dbUtils.InhalerUtils;
import debugging.Debugging;

//Check if there is an image link for the client. If there isn't, go to UploadProfilePhoto
public class ProfilePicture extends HorizontalLayout{

	public FileResource DEFAULT_PROFILE_PICTURE = new FileResource(new File("C:/Users/Boogy/Pictures/MarkDefault.jpg"));
	public Link link;
	public Panel panelPicture = new Panel("Profile Picture");
	public Image profilePicture, defaultPicture;


	//TODO 
	public void loadprofilePictureField(Client c) {
		this.removeAllComponents();
		//Debug the issue with no default image on the page for clients.
		if(c ==  null){
			Debugging.output("There is no image to upload because the client is null", Debugging.UPLOAD_IMAGE);
			return;
		}
		//If template is selected, load default image
		if (c.getName().contains(DataHolder.TEMPLATE_STRING)) {
			defaultPicture = new Image("Default Picture", DEFAULT_PROFILE_PICTURE);
			defaultPicture.setWidth("128px");
			defaultPicture.setHeight("128px");
			this.addComponent(defaultPicture);
		}
		else{
			//Check if client has a profile picture stored and if so, show it for the client.
			UploadProfilePicture uPP = new UploadProfilePicture();

			//Need to grab the link of the clients photo
			String pictureLink = c.getProfilePicture();
			//If no link, then add the upload photo functionality
			if(InhalerUtils.stringNullCheck(pictureLink)){
				uPP.addUploadUI();
			}
			//If there is an image saved for the client, grab the link and show it.
			else{
				Debugging.output("pictureLink: " + pictureLink, Debugging.UPLOAD_IMAGE);
				FileResource clientPhoto = new FileResource(new File(pictureLink));
				profilePicture = new Image("Profile Picture", clientPhoto);
				this.removeAllComponents();
				this.addComponent(profilePicture);
			}
		}
	}


	public Link getLink() {
		//Find the link to the profile picture that the client currently has if they have one.
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public Image getProfilePicture() {
		//Who is the selected client? Find their picture if they have one. Otherwise ask for a picture.
		return profilePicture;
	}

	public void setProfilePicture(Image profilePicture) {
		this.profilePicture = profilePicture;
	}

	public ProfilePicture() {
		// TODO Auto-generated constructor stub

	}




}
