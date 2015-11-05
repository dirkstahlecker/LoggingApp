package menu;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import model.Constants.FileAction;
import model.Constants;
import model.FileDialogClass;
import model.Globals;
import model.LoggingException;
import model.OutputLogDisplay;
import model.PopupDialog;

/**
 * Performs open, save, and new menu operations
 * @author Dirk
 *
 */
public class PerformFileAction implements ActionListener {
	
	private final BlockingQueue<String[]> performSaveQueue;
	private final BlockingQueue<String[]> audioQueue;
	private final JTextPane log;
	private final JFrame frame;
	private final FileAction action;
	private final OutputLogDisplay outputLogDisplay;
	private final AtomicReference<String> audioFilePathReference;
	private final PopupDialog popupDialog;
	
	public PerformFileAction(JFrame frame, BlockingQueue<String[]> performSaveQueue, JTextPane log, FileAction action, 
			BlockingQueue<String[]> audioQueue, OutputLogDisplay outputLogDisplay, AtomicReference<String> audioFilePathReference) {
		this.log = log;
		this.performSaveQueue = performSaveQueue;
		this.audioQueue = audioQueue;
		this.frame = frame;
		this.action = action;
		this.outputLogDisplay = outputLogDisplay;
		this.audioFilePathReference = audioFilePathReference;
		this.popupDialog = new PopupDialog(frame);
	}
	
	private synchronized String getSaveFile() {
		System.setProperty("apple.awt.fileDialogForDirectories", "true");
		FileDialog fileDialog = new FileDialog(frame, "Choose a folder to save to", FileDialog.SAVE);
		fileDialog.setFile("*.txt");
		fileDialog.setVisible(true);
		
		String saveFilePath = null;
		if (fileDialog.getFile() != null) { //user clicked okay and not cancel 
			saveFilePath = fileDialog.getDirectory() + fileDialog.getFile();
		}
		return saveFilePath;
	}
	
	/**
	 * Saves all relevant information to a text file
	 * @param saveFilePath path to output save data to
	 */
	private synchronized void performSave(String saveFilePath) {
		Globals.log("Saving file");
		String out = "";
		//store stuff in a text file
		/*
		 * audio file path\n
		 * playback position\n
		 * full log text
		 */
		String audioPath = "";
		int position = 0;
		String[] message = null;
		
		message = performSaveQueue.poll();
		if (message != null) {
			audioPath = message[0];
			audioPath = audioPath.replace(" ", "%20");
			Globals.log("audioPath to save: " + audioPath);

			position = (int)Double.parseDouble(message[1]);
		}
		else {
			audioPath = Constants.nullPath;
			Globals.log("Message empty", true);
		}

		out += audioPath + '\n';
		out += String.valueOf(position) + '\n';
		
		out += log.getText();
		
		if (saveFilePath != null) {
			audioFilePathReference.set(saveFilePath);
			Globals.log("Updating audioFilePathReference: " + audioFilePathReference);
			File file = new File(saveFilePath);

			PrintWriter fileWriter;
			try {
				fileWriter = new PrintWriter(file,"UTF-8");
				fileWriter.write(out);
				fileWriter.close();
				popupDialog.showMessage("File saved");
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				JOptionPane.showMessageDialog(frame, "Error creating file","Error",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Open a previously saved file
	 */
	private synchronized void performOpen() {
		Globals.log("--------------------\n--------------------");
		Globals.log("Opening file:");
		File filePath;
		try {
			filePath = FileDialogClass.showDialog(frame, FileAction.OPEN, "*.txt", false);
			Globals.log("filePath: " + filePath);
		}
		catch (LoggingException e) {
			filePath = null;
			Globals.log("Exception: file path is null", true);
		}

		if (filePath != null) { //user clicked okay and not cancel 
			audioFilePathReference.set(filePath.toString()); //allow us to save without save as later
			//String filePath = fileDialog.getDirectory() + fileDialog.getFile();
			String audioFilePath = null;
			String playbackPosition = "";
			List<String> logText = new ArrayList<String>();
			boolean validFile = true;
			
			FileReader fileReader;
			try {
				fileReader = new FileReader(filePath);
				BufferedReader reader = new BufferedReader(fileReader);
				String line;
				int count = 0; //check lines in the order they are supposed to be in
				while ((line = reader.readLine()) != null) {
					switch (count) {
					case 0:
						Globals.log(line);
						if (line.matches("^\\s*file:/[\\w|%20|/]+\\.[\\w]+")) { //TODO: allow for spaces in filepath
							audioFilePath = line;
							Globals.log("audioFilePath: " +  audioFilePath);
						}
						else if (line.equals(Constants.nullPath)) {
							audioFilePath = "";
							Globals.log("audioFilePath: No file selected"); 
							JOptionPane.showMessageDialog(frame, "No audio file saved with project","Warning",JOptionPane.WARNING_MESSAGE);
							//TODO: broke here - didn't load any text and froze
						}
						else {
							validFile = false;
						}
						break;
					case 1:
						if (!line.matches("\\d+")) { //TODO: make more robust
							validFile = false;
							Globals.log("Invalid file! Breaking in case 1");
							break;
						}
						try {
							playbackPosition = line;
						}
						catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(frame,"Error loading file","Error",JOptionPane.ERROR_MESSAGE);
							Globals.log("Number format exception in case 1 when opening");
						}
						break;
					case 2:
						/*if (!line.matches("\\d+\\s*:\\s*\\d+\\s*:\\s*(.*|\n)")) {
							validFile = false;
							break;
						}*/ //TODO: add this logic back in eventually, updated for html
						//don't break here, because we want it to get into default to add the line
					default:
						logText.add(line);
					}
					count++;
				}
				reader.close();
				fileReader.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Globals.log("audioFilePath: " + audioFilePath);
			Globals.log("playbackPosition: " + playbackPosition);
			Globals.log("logText: " + logText);

			
			if (!validFile) {
				PopupDialog.showError(frame,"Invalid save file","Error");
				Globals.log("Invalid file!", true);
				return;
			}
			
			audioFilePath = audioFilePath.trim();
			//give this info to wherever it needs to go
			audioQueue.add(new String[]{"init",audioFilePath,playbackPosition});
			//outputLogDisplay.rewriteField(logText);
			outputLogDisplay.enterTextNoAdditionalMarkup(logText); //enter text directly
		}
	}
	
	/**
	 * Creates a new file
	 */
	private synchronized void performNew() {
		Globals.log("--------------------\n--------------------");
		Globals.log("Creating new file");
		audioQueue.add(new String[]{"init",""});
		outputLogDisplay.rewriteField(new ArrayList<String>());
		audioFilePathReference.set(null);
	}

	@Override
	public synchronized void actionPerformed(ActionEvent e) {
		String path = audioFilePathReference.get();
		if (action == FileAction.SAVE) {
			if (path == null) { //not already saved
				String filePath = getSaveFile();
				performSave(filePath);
			}
			else {
				performSave(path);
			}
		}
		else if (action == FileAction.OPEN)
			performOpen();
		else if (action == FileAction.SAVE_AS) {
			String filePath = getSaveFile();
			performSave(filePath);
		}
		else if (action == FileAction.NEW) {
			performNew();
		}
		else if (action == FileAction.UPDATE_PREFS) {
			//PopupDialog.showInputBox(frame, "", "");
			//Preferences.exportLineNumbers = true;
		}
	}
}
