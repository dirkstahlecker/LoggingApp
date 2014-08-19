package model;

import java.awt.FileDialog;
import javax.swing.JFrame;

import model.Constants.FileAction;

/**
 * Used for creating load and save file dialogs
 * @author Dirk
 *
 */
public class FileDialogClass {

	public static String performShowDialog(JFrame frame, FileAction action, String fileType, boolean forDirectories) throws LoggingException {
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
		
		if (!forDirectories) {
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
		}
		
		if (fileType != "" && fileType != null) {
			fileDialog.setFile(fileType);
		}
		fileDialog.setVisible(true);

		if (fileDialog.getFile() != null) { //user clicked okay and not cancel 
			String outputFilePath = fileDialog.getDirectory() + fileDialog.getFile();
			return outputFilePath;
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
	public static String showDialog(JFrame frame, FileAction action) throws LoggingException {
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
	public static String showDialog(JFrame frame, FileAction action, String fileType) throws LoggingException {
		return performShowDialog(frame,action,fileType,true);
	}
	
	/**
	 * 
	 * Shows a file dialog for the specified action
	 * @param frameframe to display dialog within
	 * @param action shows either a save or load dialog
	 * @param fileType extension of file to choose
	 * @param forDirectories true allows slection of directories
	 * @return path to selected file or directory
	 * @throws LoggingException if incorrect file action or fatal error
	 */
	public static String showDialog(JFrame frame, FileAction action, String fileType, boolean forDirectories) throws LoggingException {
		return performShowDialog(frame,action,fileType,forDirectories);
	}
}
