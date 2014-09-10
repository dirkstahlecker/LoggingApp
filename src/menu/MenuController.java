package menu;

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
					JOptionPane.showMessageDialog(frame, "Created by Dirk Stahlecker\nstahdirk@mit.edu\nCopyright 2014","About",JOptionPane.PLAIN_MESSAGE);
					break;
				case "view help":
					
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
					System.out.println("changing rewind gain to " + rewindTime);
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
					Constants.rewindGain = new Duration(ffTime); //TODO: error logging and handling
					break;
				case "open audio":
					try {
						String audioPath = FileDialogClass.showDialog(frame, FileAction.OPEN);
						audioQueue.add(new String[]{"init",audioPath});
					} catch (LoggingException e) {
						PopupDialog.showError(frame, "Error opening file");
					}
					break;
				}
			}
		}
	}
}
