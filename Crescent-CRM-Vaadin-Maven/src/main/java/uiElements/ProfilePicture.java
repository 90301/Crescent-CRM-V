package uiElements;

import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.http.conn.scheme.HostNameResolver;
import org.imgscalr.Scalr;

import uiElements.UploadProfilePicture;
import clientInfo.Client;
import clientInfo.DataHolder;
import dbUtils.InhalerUtils;
import debugging.Debugging;

//Check if there is an image link for the client. If there isn't, go to UploadProfilePhoto
public class ProfilePicture extends HorizontalLayout{

	//TODO Will need to change the path to something else later on

	//Resource res = new ThemeResource("DefaultImage.jpg");
	//Image defaultImage = new Image(null, res);
	public FileResource DEFAULT_PROFILE_PICTURE;
	public static String PROFILE_PICTURE_FOLDER = System.getProperty("user.home")+"/ClientPictures/";
	public Link link;
	public Image profilePicture, defaultPicture;
	public int DEFAULT_IMAGE_SIZE = 128;
	public UploadProfilePicture uPP = new UploadProfilePicture();

	//TODO 
	public void loadprofilePictureField(Client c) {
		this.removeAllComponents();

		if(c ==  null){
			Debugging.output("There is no image to upload because the client is null", Debugging.UPLOAD_IMAGE);
			return;
		}

		//If template is selected, load default image
		if (c.getName().contains(DataHolder.TEMPLATE_STRING)) {
			//defaultPicture = new Image("Default Picture", DEFAULT_PROFILE_PICTURE);
			//defaultPicture.setWidth("128px");
			//defaultPicture.setHeight("128px");
			String pictureLink = c.getProfilePicture();
			Debugging.output("Template Image Link is: " + pictureLink, Debugging.UPLOAD_IMAGE);
			FileResource DEFAULT_PROFILE_PICTURE = new FileResource(new File(pictureLink));
			profilePicture = new Image("Default Picture", DEFAULT_PROFILE_PICTURE);
			this.removeAllComponents();
			this.addComponent(profilePicture);
		}
		else{
			//Check if client has a profile picture stored and if so, show it for the client.

			//Need to grab the link of the clients photo
			String pictureLink = c.getProfilePicture();
			//If no link, then add the upload photo functionality
			if(InhalerUtils.stringNullCheck(pictureLink)){
				if(DEFAULT_PROFILE_PICTURE != null){
					defaultPicture = new Image("Default Picture", DEFAULT_PROFILE_PICTURE);
					defaultPicture.setWidth("128px");
					defaultPicture.setHeight("128px");
					this.addComponent(defaultPicture);
				}
				uPP.addUploadUI();
			}
			//If there is an image saved for the client, grab the link and show it.
			else{
				Debugging.output("pictureLink: " + pictureLink, Debugging.UPLOAD_IMAGE);
				FileResource clientPhoto = new FileResource(new File(pictureLink));
				profilePicture = new Image("", clientPhoto);
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
