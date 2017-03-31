/**
 * Author: Andrew Dorsett
 * Last Modified: 2/10/17
 */
package uiElements;

import java.awt.Color;
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

	public Boolean hasUploaded;
	public File recentUpload;
	public String link;
	//Find home folder that contains all Client Photos
	public static String PROFILE_PICTURE_FOLDER = System.getProperty("user.home")+"/ClientPictures/";
	
	BufferedImage originalImage;
	Client c;
	FileResource resource;
	String fileName = "";
	Upload uploadPhoto = new Upload("Upload Image", this);
	
	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		// TODO Auto-generated method stub
		makeDirectory();
		FileOutputStream fos = null;

		//If upload button is selected and there isn't a file, break from method

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

	public Boolean getHasUploaded() {
		return hasUploaded;
	}

	public void setHasUploaded(Boolean hasUploaded) {
		this.hasUploaded = hasUploaded;
	}

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
	}

	//Constructor
	public UploadProfilePicture(){

	}

	//TODO When update is clicked. Test for null on resource and then for the link
	//Currently trying to figure out if I need the link to be MaxField<String>
	public String updateProfilePicture(){

		String photoLink = getLink();
		Debugging.output("Photo Link: " + photoLink, Debugging.UPLOAD_IMAGE);
		//setLink(null);
		
		setHasUploaded(false);

		if(photoLink == null){
			Debugging.output("Photo Link (null): " + photoLink, Debugging.UPLOAD_IMAGE);
			return null;
		}
		else{
			Debugging.output("Photo Link: " + photoLink, Debugging.UPLOAD_IMAGE);
			return photoLink;
		}
	}

	public void renameProfilePicture(){

	}

	public File convertPNGtoJPEG(File imageFile){
		BufferedImage bufferedImage;

		String imageName = "";

		int i = fileName.lastIndexOf('.');
		if (i >= 0) {
			imageName = fileName.substring(0, i-1); //Grabs the whole front part of the name of the file
		}
		try {

		  //read image file
		  bufferedImage = ImageIO.read(imageFile);

		  // create a blank, RGB, same width and height, and a white background
		  BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
				bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		  newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
		  //Change the.png extension to .jpg
		  File imageFile2 = new File(PROFILE_PICTURE_FOLDER + imageName +".jpg");
		  // write to jpeg file
		  imageFile.renameTo(imageFile2);
		  ImageIO.write(newBufferedImage, "jpg", imageFile);

		  System.out.println("Done");

		} catch (IOException e) {

		  e.printStackTrace();

		}

	   

		return imageFile;
	}
	
	//TODO Need this done ASAP
	public String resizeImage(FileResource resource){

		File imageFile = resource.getSourceFile().getAbsoluteFile();
		
		//Copy of the File that will be deleted
		File imageToDelete = resource.getSourceFile().getAbsoluteFile();

		Debugging.output("ImageFile: " + imageFile, Debugging.UPLOAD_IMAGE);
		
		if(imageFile.getName().contains("png")){
			imageFile = convertPNGtoJPEG(imageFile);
		}
		
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
		BufferedImage scaledImage = Scalr.resize(originalImage, 256);
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
		resource = new FileResource(new File(scaledImageLocation));

		//Set link to new image file that has been scaled.
		link = resource.getSourceFile().getAbsoluteFile().toString();

		//Delete original image file. 
		Boolean deletion = imageToDelete.delete();
		Debugging.output("Original Image deleted? " + deletion, Debugging.UPLOAD_IMAGE);
		
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

		this.link = resizeImage(resource);
		Debugging.output("Photo Link: " + this.link, Debugging.UPLOAD_IMAGE);
		//setLink(link);
		//Debugging.output("Resource: " + resource, Debugging.UPLOAD_IMAGE);
		setHasUploaded(true);
	}


	/**
	 * Adding the label and buttons using this method. Will need some editing.
	 */
	public void addUploadUI(){
		setHasUploaded(false);
		this.setSpacing(true);
		//this.removeAllComponents();
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
		Debugging.output("File has started to Upload: ",  Debugging.UPLOAD_IMAGE);
		Debugging.output("File Type in UploadStarted: " + fileName,  Debugging.UPLOAD_IMAGE);
		
			if(fileName.contains("image")){
				
			}
			else{
				Notification.show("Error", "\nFile Types Allowed: .jpg and .png" + "\nClick on error message to dismiss.", Type.ERROR_MESSAGE);
				uploadPhoto.interruptUpload();
			}
		
	}

	public void makeDirectory() {
		File f = new File(PROFILE_PICTURE_FOLDER);
		Boolean worked = f.mkdirs();
		Debugging.output("Created new directory: " + worked,  Debugging.UPLOAD_IMAGE);
	}
}