package model;

import java.awt.FileDialog;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import model.Constants.FileAction;

/**
 * Used for creating load and save file dialogs
 * @author Dirk
 *
 */
public class FileDialogClass {

	//helper method
	private synchronized static File performShowDialog(JFrame frame, FileAction action, String fileType, boolean forDirectories) throws LoggingException {
		//remove forDirectories, or change it
		
		FileDialog fileDialog = null;
		if (action == FileAction.SAVE) {
			fileDialog = new FileDialog(frame, "Choose a folder to save to", FileDialog.SAVE);
		}
		else if (action == FileAction.OPEN) {
			fileDialog = new FileDialog(frame, "Choose a file to open", FileDialog.LOAD);
		}
		else {
			throw new LoggingException();
		}
		
		if (fileType != "" && fileType != null) {
			fileDialog.setFile(fileType);
			
		}
		fileDialog.setVisible(true);

		if (fileDialog.getFile() != null) { //user clicked okay and not cancel 
			File outputFilePath = new File(fileDialog.getDirectory(), fileDialog.getFile());
			Globals.log("Trace in File Dialog Class:");
			Globals.log("outputFilePath: " + outputFilePath);
			Globals.log("outputFilePath.toURI(): " + outputFilePath.toURI());
			Globals.log("outputFilePath.toURI().toString(): " + outputFilePath.toURI().toString());
			return outputFilePath; //this method fixes issues of opening from other directories
		}
		else {
			return null;
		}
	}
	
	public synchronized static String showChooser(JFrame frame, FileAction action, boolean forDirectories) throws LoggingException {
		//TODO: add file filtering
		System.setProperty("apple.awt.fileDialogForDirectories", "true");
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setVisible(true);
		
		if (forDirectories) {
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		}
		
		int r = -1;
		if (action == FileAction.OPEN) {
			r = fileChooser.showOpenDialog(frame);
		}
		else if (action == FileAction.SAVE) {
			r = fileChooser.showSaveDialog(frame);
		}

		if (r == JFileChooser.APPROVE_OPTION) { //user clicked okay and not cancel 
			File myFile = fileChooser.getSelectedFile();
			return myFile.getAbsolutePath();
		}
		else {
			return null;
		}
	}
	
	/**
	 * Shows a file dialog for the specified action
	 * @param frame frame to display dialog within
	 * @param action shows either a save or load dialog
	 * @return path to selected file or directory
	 * @throws LoggingException if incorrect file action or fatal error
	 */
	public synchronized static File showDialog(JFrame frame, FileAction action) throws LoggingException {
		return performShowDialog(frame,action,"",true);
	}
	
	/**
	 * Shows a file dialog for the specified action
	 * @param frameframe to display dialog within
	 * @param action shows either a save or load dialog
	 * @param fileType extension of file to choose
	 * @return path to selected file or directory
	 * @throws LoggingException if incorrect file action or fatal error
	 */
	public synchronized static File showDialog(JFrame frame, FileAction action, String fileType) throws LoggingException {
		return performShowDialog(frame,action,fileType,true);
	}
	
	/**
	 * 
	 * Shows a file dialog for the specified action
	 * @param frameframe to display dialog within
	 * @param action shows either a save or load dialog
	 * @param fileType extension of file to choose
	 * @param forDirectories true allows selection of directories
	 * @return path to selected file or directory
	 * @throws LoggingException if incorrect file action or fatal error
	 */
	public synchronized static File showDialog(JFrame frame, FileAction action, String fileType, boolean forDirectories) throws LoggingException {
		return performShowDialog(frame,action,fileType,forDirectories);
	}
	
	/**
	 * Shows a confirmation dialog
	 * @param msg message to be displayed to the user
	 * @param title title on the dialog
	 * @return true if user clicked yes
	 */
	public synchronized static boolean showConfirmation(String msg, String title) {
		int dialogButton = JOptionPane.YES_NO_OPTION;
		int dialogResult = JOptionPane.showConfirmDialog(null, msg, title, dialogButton);
		return dialogResult == 0;
	}
}
