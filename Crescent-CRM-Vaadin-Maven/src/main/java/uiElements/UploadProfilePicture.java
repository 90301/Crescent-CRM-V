/**
 * Author: Andrew Dorsett
 * Last Modified: 2/10/17
 */
package uiElements;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

//Not sure if this is needed
import clientInfo.Client;

//Need debugging
import debugging.Debugging;

import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;


public class UploadProfilePicture extends HorizontalLayout implements Upload.StartedListener, 
Upload.ProgressListener, Upload.FailedListener, Upload.SucceededListener,
Upload.FinishedListener, Receiver{

	Label uploadLabel = new Label("Profile Picture");
	Label state = new Label();
	//Upload(null, Receiver) may need a new class for receiver based off demo source code
	Upload uploadPhoto = new Upload(null, this);
	public File recentUpload;
	FileResource resource;
	public String link;
	Client c;
	BufferedImage originalImage;

	String fileName = "";

	public static String PROFILE_PICTURE_FOLDER = System.getProperty("user.home")+"/ClientPictures/";

	//uploadPhoto.setImmediate(false);
	//uploadPhoto.setButtonCaption("Upload File");

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Client getC() {
		return c;
	}

	public void setC(Client c) {
		this.c = c;
	}

	{
		uploadPhoto.addFinishedListener(e->isFinished(e));
	}
	/*
	uploadPhoto.addStartedListener(new StartedListener() {
		@Override
		public void uploadStarted(final StartedEvent event) {
			if (uploadInfoWindow.getParent() == null) {
				UI.getCurrent().addWindow(uploadInfoWindow);
			}
			uploadInfoWindow.setClosable(false);
		}

		uploadPhoto.addFinishedListener(new Upload.FinishedListener() {
			@Override
			public void uploadFinished(final FinishedEvent event) {
				uploadInfoWindow.setClosable(true);
			}
		}
	 */
	//uploadInfoWindow = new UploadInfoWindow(uploadPhoto, lineBreakCounter);

	//Constructor
	public UploadProfilePicture(){

	}

	//TODO When update is clicked. Test for null on resource and then for the link
	//Currently trying to figure out if I need the link to be MaxField<String>
	public String updateProfilePicture(){

		String photoLink = getLink();

		if(photoLink.equals(null)){
			Debugging.output("Photo Link: " + photoLink, Debugging.UPLOAD_IMAGE);
			return null;
		}
		else{
			Debugging.output("Photo Link: " + photoLink, Debugging.UPLOAD_IMAGE);
			return photoLink;
		}
	}
	
	public void renameProfilePicture(){
		
	}
	
	//TODO
	public String resizeImage(FinishedEvent event){
		
		File imageFile = new File(event.getFilename());
		try {
			BufferedImage originalImage = ImageIO.read(imageFile) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedImage scaledImage = Scalr.resize(originalImage, 128);

		FileOutputStream fos = null;
		try {

			fos = new FileOutputStream(PROFILE_PICTURE_FOLDER + "Scaled" + scaledImage);

		} catch (final java.io.FileNotFoundException e){
			new Notification("Couldn't open the file", e.getMessage(), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
		}

		resource = new FileResource(new File(PROFILE_PICTURE_FOLDER + "Scaled" + scaledImage));

		link = resource.getSourceFile().getAbsoluteFile().toString();
		
		return link;
		
	}
	
	private void isFinished(FinishedEvent event) {
		// TODO Auto-generated method stub
		String fileType = event.getMIMEType();

		Debugging.output("File Type: " + fileType, Debugging.UPLOAD_IMAGE);
		//this.removeAllComponents();

		/*
		if(fileType.equals("image/jpeg")){
			resource = new FileResource(new File(PROFILE_PICTURE_FOLDER + event.getFilename()+ this.fileName));
		}
		else{
			resource = new FileResource(new File("C:/Users/Boogy/Pictures/VaadinTest/TopTwenty.png"));
		}
		 */
		//resizeImage(event);
		
		String extension = "";

		int i = event.getFilename().lastIndexOf('.');
		if (i > 0) {
		    extension = fileName.substring(i);
		}
		
		String originalName = event.getFilename();
		UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
		String newName = randomUUIDString + extension;
		
		File oldFileName = new File(PROFILE_PICTURE_FOLDER + originalName);
		File newFileName = new File(PROFILE_PICTURE_FOLDER + newName);
		oldFileName.renameTo(newFileName);
		
		resource = new FileResource(newFileName);
		
		//TODO need to get resizeImage() to work properly
		//link = resizeImage(event);
		
		link = resource.getSourceFile().getAbsoluteFile().toString();
		// Show the image in the application
		//Image image = new Image("Profile Picture", resource);
		//image.setWidth("240px");
		//image.setHeight("160px");
		Debugging.output("Resource: " + resource, Debugging.UPLOAD_IMAGE);
		//Get selected client and add link to photo to the clients profilePicture
		//Same comment as above this method
		//c.setProfilePicture(newLink);
		//this.addComponent(image);
	}


	/**
	 * Adding the label and buttons using this method. Will need some editing.
	 */
	public void addUploadUI(){


		this.setSpacing(true);
		this.removeAllComponents();
		this.addComponent(uploadLabel);
		this.addComponent(uploadPhoto);

		makeDirectory();
	}

	//Add labels for each event e.g. result, fileName, Progress, state

	public void uploadFinished(final FinishedEvent event) {
		state.setValue("Upload Finished");
		this.addComponent(state);
	}
	@Override
	public void uploadSucceeded(SucceededEvent event) {
		// TODO Auto-generated method stub

	}
	@Override
	public void uploadFailed(FailedEvent event) {
		// TODO Auto-generated method stub

	}
	@Override
	public void updateProgress(long readBytes, long contentLength) {
		// TODO Auto-generated method stub

	}
	@Override
	public void uploadStarted(StartedEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		// TODO Auto-generated method stub
		makeDirectory();
		FileOutputStream fos = null;
		try {
			recentUpload = new File(filename);

			fos = new FileOutputStream(PROFILE_PICTURE_FOLDER + recentUpload);


			this.fileName = recentUpload.getName();
		} catch (final java.io.FileNotFoundException e){
			new Notification("Couldn't open the file", e.getMessage(), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());

			return null;
		}
		return fos;
	}

	public void makeDirectory() {
		File f = new File(PROFILE_PICTURE_FOLDER);
		Boolean worked = f.mkdirs();
		Debugging.output("Created new directory: " + worked,  Debugging.UPLOAD_IMAGE);
	}
}
