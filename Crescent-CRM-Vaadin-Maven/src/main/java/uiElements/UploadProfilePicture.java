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
import java.util.ArrayList;
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
import com.vaadin.ui.Notification.Type;
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

	//Upload(null, Receiver) may need a new class for receiver based off demo source code
	Upload uploadPhoto = new Upload("Upload Image", this);
	public File recentUpload;
	FileResource resource;
	public String link;
	Client c;
	BufferedImage originalImage;
	String fileName = "";
	ArrayList<String> allowedMimeTypes;

	//Find home folder that contains all Client Photos
	public static String PROFILE_PICTURE_FOLDER = System.getProperty("user.home")+"/ClientPictures/";

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
		uploadPhoto.addStartedListener(e->uploadStarted(e));
		ArrayList<String> allowedMimeTypes = new ArrayList<String>();
		allowedMimeTypes.add("image/jpeg");
		allowedMimeTypes.add("image/png");
	}

	//Constructor
	public UploadProfilePicture(){

	}

	//TODO When update is clicked. Test for null on resource and then for the link
	//Currently trying to figure out if I need the link to be MaxField<String>
	public String updateProfilePicture(){

		String photoLink = getLink();

		if(photoLink == null){
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

	//TODO Need this done ASAP
	public String resizeImage(FileResource resource){

		File imageFile = resource.getSourceFile().getAbsoluteFile();

		Debugging.output("ImageFile: " + imageFile, Debugging.UPLOAD_IMAGE);
		BufferedImage originalImage = null;
		try {
			originalImage = ImageIO.read(imageFile);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(originalImage == null){
			return null;
		}
		Debugging.output("BufferedImage: " + originalImage, Debugging.UPLOAD_IMAGE);
		BufferedImage scaledImage = Scalr.resize(originalImage, 128);
		Debugging.output("ScaledImage: " + scaledImage, Debugging.UPLOAD_IMAGE);

		File scaledImageOutput = null;

		String scaledImageLocation = PROFILE_PICTURE_FOLDER + "Scaled" + imageFile.getName();
		scaledImageOutput = new File(scaledImageLocation);

		Debugging.output("ScaledOutput: " + scaledImageOutput, Debugging.UPLOAD_IMAGE);
		try {
			//Debugging.output("Writing scaled file starting. File: " + scaledImageOutput.getAbsolutePath(), Debugging.UPLOAD_IMAGE);
			ImageIO.write(scaledImage, "jpg", scaledImageOutput);
			//Debugging.output("Writing scaled file finished. File: " + scaledImageOutput.getAbsolutePath(), Debugging.UPLOAD_IMAGE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Debugging.output("ScaledOutput: " + scaledImageOutput, Debugging.UPLOAD_IMAGE);

		resource = new FileResource(new File(scaledImageLocation));
		//Debugging.output("Resource: " + resource, Debugging.UPLOAD_IMAGE);

		link = resource.getSourceFile().getAbsoluteFile().toString();
		//Debugging.output("Link: " + link, Debugging.UPLOAD_IMAGE);

		return link;

	}

	private void isFinished(FinishedEvent event) {
		// TODO Auto-generated method stub
		String fileType = event.getMIMEType();

		Debugging.output("File Type: " + fileType, Debugging.UPLOAD_IMAGE);

		//Find the extension for the file
		String extension = getFileExtension(event.getFilename());

		String originalName = event.getFilename();
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		String newName = randomUUIDString + extension;

		//Rename image so there won't be any duplicates
		File oldFileName = new File(PROFILE_PICTURE_FOLDER + originalName);
		File newFileName = new File(PROFILE_PICTURE_FOLDER + newName);
		oldFileName.renameTo(newFileName);

		resource = new FileResource(newFileName);

		link = resizeImage(resource);
		//Debugging.output("Resource: " + resource, Debugging.UPLOAD_IMAGE);
	}


	/**
	 * Adding the label and buttons using this method. Will need some editing.
	 */
	public void addUploadUI(){

		this.setSpacing(true);
		this.removeAllComponents();
		this.addComponent(uploadPhoto);
		makeDirectory();
	}

	public String getFileExtension(String fileName){
		String extension = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i);
		}
		return extension;
	}

	//Add labels for each event if needed
	public void uploadFinished(final FinishedEvent event) {

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
	//TODO Fix this bulls
	public void uploadStarted(StartedEvent event) {
		// TODO Auto-generated method stub
		
		String fileName = event.getMIMEType();
		Debugging.output("File Type in UploadStarted: " + fileName,  Debugging.UPLOAD_IMAGE);
		
			if(fileName.contains("image")){
				
			}
			else{
				Notification.show("Error", "\nFile Types Allowed: .jpg and .png" + "\nClick on error message to dismiss.", Type.ERROR_MESSAGE);
				uploadPhoto.interruptUpload();
			}
		
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		// TODO Auto-generated method stub
		makeDirectory();
		FileOutputStream fos = null;

		//If upload button is selected amd there isn'ta file, break from method

		try {
			recentUpload = new File(filename);
			//if(recentUpload == null){
			//	return null;
			//}

			fos = new FileOutputStream(PROFILE_PICTURE_FOLDER + recentUpload);

			this.fileName = recentUpload.getName();
		} catch (final java.io.FileNotFoundException e){
			new Notification("Couldn't open the file, please refresh the page", e.getMessage(), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());

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