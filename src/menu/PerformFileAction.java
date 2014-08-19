package menu;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import sun.misc.IOUtils;
import view.LoggingGUI;
import model.Constants.FileAction;
import model.OutputLogDisplay;
import model.PopupDialog;

/**
 * Performs open and save operations
 * @author Dirk
 *
 */
public class PerformFileAction implements ActionListener {
	
	private final BlockingQueue<String[]> performSaveQueue;
	private final BlockingQueue<String[]> audioQueue;
	private final JTextArea log;
	private final JFrame frame;
	private final FileAction action;
	private final OutputLogDisplay outputLogDisplay;
	private String saveFilePath = "";
	private final AtomicReference<String> audioFilePathReference;
	private final PopupDialog popupDialog;
	
	public PerformFileAction(JFrame frame, BlockingQueue<String[]> performSaveQueue, JTextArea log, FileAction action, 
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
	
	private String getSaveFile() {
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
	private void performSave(String saveFilePath) {
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
			position = (int)Double.parseDouble(message[1]);
		}
		else {
			//TODO: raise error
			System.err.println("Message empty");
		}

		out += audioPath + '\n';
		out += String.valueOf(position) + '\n';
		
		out += log.getText();
		
		if (saveFilePath != null) {
			audioFilePathReference.set(saveFilePath);
			File file = new File(saveFilePath);

			PrintWriter fileWriter;
			try {
				fileWriter = new PrintWriter(file,"UTF-8");
				fileWriter.write(out);
				fileWriter.close();
				popupDialog.showMessage("File created");
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				JOptionPane.showMessageDialog(frame, "Error creating file","Error",JOptionPane.ERROR_MESSAGE);
			}
		}

	}
	
	/**
	 * Open a previously saved file
	 */
	private void performOpen() {
		//System.setProperty("apple.awt.fileDialogForDirectories", "true");
		FileDialog fileDialog = new FileDialog(frame, "Choose a file to open", FileDialog.LOAD);
		fileDialog.setFile("*.txt");
		fileDialog.setVisible(true);

		if (fileDialog.getFile() != null) { //user clicked okay and not cancel 
			String filePath = fileDialog.getDirectory() + fileDialog.getFile();
			String audioFilePath = null;
			String playbackPosition = "";
			List<String> logText = new ArrayList<String>();
			String fullFileText = "";
			boolean validFile = true;
			
			FileReader fileReader;
			try {
				fileReader = new FileReader(filePath);
				BufferedReader reader = new BufferedReader(fileReader);
				String line;
				int count = 0;
				while ((line = reader.readLine()) != null) {
					fullFileText += line;
					switch (count) {
					case 0:
						if (line.matches("\\s*file:///[^\\s]+?\\.[0-9a-zA-Z]+")) { //TODO: allow no audio file to be specfied
							audioFilePath = line;
						}
						else {
							validFile = false;
						}
						break;
					case 1:
						if (!line.matches("\\d+")) { //TODO: make more robust
							validFile = false;
							break;
						}
						try {
							playbackPosition = line;
						}
						catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(frame, "Error loading file","Error",JOptionPane.ERROR_MESSAGE);
						}
						break;
					case 2:
						if (!line.matches("\\d+\\s*:\\s*\\d+\\s*:\\s*.*|\n")) {
							validFile = false;
							break;
						}
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
			
			if (!validFile) {
				PopupDialog.showError(frame,"Invalid save file","Error");
				return;
			}
			else
				System.out.println("successfully passed regex");
			
			audioFilePath = audioFilePath.trim();
			//give this info to wherever it needs to go
			audioQueue.add(new String[]{"init",audioFilePath,playbackPosition});
			outputLogDisplay.rewriteField(logText);
		}
	}
	
	private void performNew() {
		audioQueue.add(new String[]{"init",""});
		outputLogDisplay.rewriteField(new ArrayList<String>());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
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
	}
}
