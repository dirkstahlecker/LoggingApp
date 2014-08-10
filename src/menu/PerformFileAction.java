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
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import sun.misc.IOUtils;
import model.Constants.FileAction;
import model.OutputLogDisplay;

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
	
	public PerformFileAction(JFrame frame, BlockingQueue<String[]> performSaveQueue, JTextArea log, FileAction action, 
			BlockingQueue<String[]> audioQueue, OutputLogDisplay outputLogDisplay) {
		this.log = log;
		this.performSaveQueue = performSaveQueue;
		this.audioQueue = audioQueue;
		this.frame = frame;
		this.action = action;
		this.outputLogDisplay = outputLogDisplay;
	}
	
	/**
	 * Save all relevant information to a text file
	 */
	public void save() {
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
		System.setProperty("apple.awt.fileDialogForDirectories", "true");
		FileDialog fileDialog = new FileDialog(frame, "Choose a folder to save to", FileDialog.SAVE);
		fileDialog.setFile("*.txt");
		fileDialog.setVisible(true);

		if (fileDialog.getFile() != null) { //user clicked okay and not cancel 
			String outputFilePath = fileDialog.getDirectory() + fileDialog.getFile();
			File file = new File(outputFilePath);

			PrintWriter fileWriter;
			try {
				fileWriter = new PrintWriter(file,"UTF-8");
				fileWriter.write(out);
				fileWriter.close();
				JOptionPane.showMessageDialog(frame, "File created");
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				JOptionPane.showMessageDialog(frame, "Error creating file","Error",JOptionPane.ERROR_MESSAGE);
			}
		}

	}
	
	/**
	 * Open a previously saved file
	 */
	private void open() {
		//System.setProperty("apple.awt.fileDialogForDirectories", "true");
		FileDialog fileDialog = new FileDialog(frame, "Choose a file to open", FileDialog.LOAD);
		fileDialog.setFile("*.txt");
		fileDialog.setVisible(true);

		if (fileDialog.getFile() != null) { //user clicked okay and not cancel 
			String filePath = fileDialog.getDirectory() + fileDialog.getFile();
			String audioFilePath = null;
			String playbackPosition = "";
			List<String> logText = new ArrayList<String>();
			
			FileReader fileReader;
			try {
				fileReader = new FileReader(filePath);
				BufferedReader reader = new BufferedReader(fileReader);
				String line;
				int count = 0;
				while ((line = reader.readLine()) != null) {
					switch (count) {
					case 0:
						if (line.matches("file:///[^\\s]+?\\.[0-9a-zA-Z]+")) {
							audioFilePath = line;
						}
						break;
					case 1:
						try {
							playbackPosition = line;
						}
						catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(frame, "Error loading file","Error",JOptionPane.ERROR_MESSAGE);
						}
						break;
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
			
			//give this info to wherever it needs to go
			audioQueue.add(new String[]{"init",audioFilePath,playbackPosition});
			outputLogDisplay.rewriteField(logText);
			
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (action == FileAction.SAVE) 
			save();
		else if (action == FileAction.OPEN)
			open();
	}
}
