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
	public synchronized void run() {
		String[] message = null;
		
		while(true) {
			message = queue.poll();
			if (message != null) {

				switch(message[0]) {
				case "about":
					JOptionPane.showMessageDialog(frame,"Version " + Constants.VERSION + "\n\n"
							+ "Created by Dirk Stahlecker\nstahdirk@mit.edu\nCopyright 2015\n\n"
							+ "This software comes AS IS with ABSOLUTELY NO WARRANTY\n"
							+ "Use at your own risk.","About",JOptionPane.PLAIN_MESSAGE);
					break;
				case "view help":
					JOptionPane.showMessageDialog(frame,Globals.getHelpText(),"Help",JOptionPane.PLAIN_MESSAGE);
					break;
				case "known bugs":
					JOptionPane.showMessageDialog(frame,Globals.getKnownBugsText(),"Known Bugs",JOptionPane.PLAIN_MESSAGE);
					break;
				case "playback rate":
					String rateStr = PopupDialog.showInputBox(frame,"Playback rate is currently "
							+Constants.playbackRate+"\nEnter new playback rate between 0 and 8 (decimals allowed):","Playback Rate");
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
