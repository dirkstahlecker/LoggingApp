package menu;

import java.io.File;
import java.util.concurrent.BlockingQueue;

import javafx.util.Duration;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import model.Constants;
import model.Constants.FileAction;
import model.FileDialogClass;
import model.Globals;
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
					JOptionPane.showMessageDialog(frame,"Version " + Constants.VERSION + "\n\n"
							+ "Created by Dirk Stahlecker\nstahdirk@mit.edu\nCopyright 2015","About",JOptionPane.PLAIN_MESSAGE);
					break;
				case "view help":
					JOptionPane.showMessageDialog(frame,"This app helps radio logging by automatically timestamping comments and offering other features.\n"
							+ "Type in the comment box, then click \"Enter Text\" or press enter to submit text to the log\n"
							+ "Clicking on a timestamp in the log will jump to that point in the audio file.\n"
							+ "Change the playback rate of the audio in the \"Options\" menu.\n"
							+ "Click \"Open Audio\" to import an audio file\n"
							+ "Clicking \"Fast Forward\" or \"Rewind\" moves the audio file forward or backward an amount set by the gain.\n"
							+ "You can change the gain in its respective menu.\n"
							+ "Saving a project allows you to open it again for editing.\n"
							+ "Exporting a project saves the log to a file format of your choosing.\n"
							+ "Clicking \"Highlight\" will highlights whatever text you enter. There is no way to highlight after entering.\n"
							+ "\nReminder: There is no autosave feature. Save frequently, because if the app crashes it can't recover your log.\n"
							+ "Java 8 is required for the proper function of this application."
							,"Help",JOptionPane.PLAIN_MESSAGE);
					break;
				case "known bugs":
					JOptionPane.showMessageDialog(frame,"Known Bugs:\n"
							+ "After saving, you cannot open a file without restarting the app.\n"
							+ "Length of audio file occasionally shows up as \"NaN\" for some unknown reason. No known fix.\n"
							+ "Occasionally, changing the rewind and fastforward gains doesn't work. No known fix.\n"
							+ "Occasional crashes. Restart the app and it should be fine.\n"
							+ "Many other bugs likely exist. Feel free to email stahdirk@mit.edu if you experience problems.",
							"About",JOptionPane.PLAIN_MESSAGE);
					break;
				case "playback rate":
					String rateStr = PopupDialog.showInputBox(frame,"Playback rate is currently "
							+Constants.playbackRate+"\nEnter new playback rate between 0 and 8:","Playback Rate");
					audioQueue.add(new String[]{"rate","",rateStr});
					break;
				case "rewind gain":
					String rewindStr = PopupDialog.showInputBox(frame, "Rewind gain is currently "+Constants.rewindGain.toSeconds()
							+" second\nEnter new rewind gain (in seconds)", "Rewind Gain");
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
					Globals.log("changing rewind gain to " + rewindTime);
					Constants.rewindGain = new Duration(rewindTime); //TODO: error logging and handling
					break;
				case "fastforward gain":
					String ffStr = PopupDialog.showInputBox(frame, "Fast Forward gain is currently "+Constants.fastforwardGain.toSeconds()
							+" seconds\nEnter new fast forward gain (in seconds)","Fast Forward Gain");
					if (ffStr == null || ffStr.equals("") || ffStr.equals(" ")) {
						break;
					}
					double ffTime;
					try {
						ffTime = Double.parseDouble(ffStr);
					}
					catch (NumberFormatException e) {
						//TODO: something here
						Globals.log("Problem parsing double in rewind gain", true);
						break;
					}
					ffTime *= 1000; //convert to milliseconds
					Globals.log("changing fastforward gain to " + ffTime);
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
				case "debug":
					Constants.DEBUG = !Constants.DEBUG;
					break;
				}
			}
		}
	}
}
