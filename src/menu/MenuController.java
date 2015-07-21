package menu;

import java.io.File;
import java.util.concurrent.BlockingQueue;

import javafx.util.Duration;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import model.Constants;
import model.Constants.FileAction;
import model.FileDialogClass;
import model.LoggingException;
import model.PopupDialog;

/**
 * Used as a hub for handling menu options that aren't handled explicitly
 * by an action listener. 
 * @author Dirk
 *
 */
public class MenuController implements Runnable {
	
	private final BlockingQueue<String[]> queue;
	private final BlockingQueue<String[]> audioQueue;
	private final JFrame frame;
	
	public MenuController(BlockingQueue<String[]> queue, JFrame frame, BlockingQueue<String[]> audioQueue) {
		this.queue = queue;
		this.frame = frame;
		this.audioQueue = audioQueue;
	}

	@Override
	public void run() {
		String[] message = null;
		
		while(true) {
			message = queue.poll();
			if (message != null) {

				switch(message[0]) {
				case "about":
					JOptionPane.showMessageDialog(frame,"Created by Dirk Stahlecker\nstahdirk@mit.edu\nCopyright 2015","About",JOptionPane.PLAIN_MESSAGE);
					break;
				case "view help":
					JOptionPane.showMessageDialog(frame,"Click \"Open Audio\" to import an audio file\n"
							+ "Clicking \"Fast Forward\" or \"Rewind\" moves the audio file forward or backward an amount set by the gain.\n"
							+ "You can change the gain in its respective menu.\n"
							+ "Saving a project allows you to open it again for editing.\n"
							+ "Exporting a project saves the log to a file format of your choosing.\n"
							,"Help",JOptionPane.PLAIN_MESSAGE);
					break;
				case "playback rate":
					String rateStr = PopupDialog.showInputBox(frame,"Playback rate is currently "+Constants.playbackRate+"\nEnter new playback rate between 0 and 8:","Playback Rate");
					audioQueue.add(new String[]{"rate","",rateStr});
					break;
				case "rewind gain":
					String rewindStr = PopupDialog.showInputBox(frame, "Rewind gain is currently "+Constants.rewindGain+"\nEnter new rewind gain (in seconds)", "Rewind Gain");
					if (rewindStr == null || rewindStr.equals("") || rewindStr.equals(" ")) {
						break;
					}
					double rewindTime;
					try {
						rewindTime = Double.parseDouble(rewindStr);
					}
					catch (NumberFormatException e) {
						//TODO: something here
						System.err.println("Problem parsing double in rewind gain");
						break;
					}
					rewindTime *= 1000; //convert to milliseconds
					if (Constants.DEBUG) System.out.println("changing rewind gain to " + rewindTime);
					Constants.rewindGain = new Duration(rewindTime); //TODO: error logging and handling
					System.out.println(Constants.rewindGain);
					break;
				case "fastforward gain":
					String ffStr = PopupDialog.showInputBox(frame, "Fast Forward gain is currently "+Constants.fastforwardGain+"\nEnter new fast forward gain (in seconds)","Fast Forward Gain");
					if (ffStr == null || ffStr.equals("") || ffStr.equals(" ")) {
						break;
					}
					double ffTime;
					try {
						ffTime = Double.parseDouble(ffStr);
					}
					catch (NumberFormatException e) {
						//TODO: something here
						System.err.println("Problem parsing double in rewind gain");
						break;
					}
					ffTime *= 1000; //convert to milliseconds
					if (Constants.DEBUG) System.out.println("changing fastforward gain to " + ffTime);
					Constants.fastforwardGain = new Duration(ffTime); //TODO: error logging and handling
					break;
				case "open audio":
					try {
						File audioPathFile = FileDialogClass.showDialog(frame, FileAction.OPEN);
						String audioPath = audioPathFile.toURI().toString();
						if (!(audioPath == null || audioPath.trim().equals(""))) {
							audioQueue.add(new String[]{"init",audioPath});	
						}
					} catch (LoggingException e) {
						PopupDialog.showError(frame, "Error opening file");
					}
					break;
				}
			}
		}
	}
}
