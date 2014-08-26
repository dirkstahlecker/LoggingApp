package menu;

import java.util.concurrent.BlockingQueue;

import javafx.util.Duration;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import model.Constants;
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
					JOptionPane.showMessageDialog(frame, "Created by Dirk Stahlecker\nCopyright 2014","About",JOptionPane.PLAIN_MESSAGE);
					break;
				case "view help":
					
					break;
				case "playback rate":
					String s = PopupDialog.showInputBox(frame,"Playback rate is currently "+Constants.playbackRate+"\nEnter new playback rate between 0 and 8:","Playback Rate");
					audioQueue.add(new String[]{"rate","",s});
					break;
				case "rewind gain":
					double rewindTime = Double.parseDouble(message[1]);
					rewindTime *= 1000; //convert to milliseconds
					Constants.rewindGain = new Duration(rewindTime); //TODO: error logging and handling
					break;
				case "fastforward gain":
					double fastforwardTime = Double.parseDouble(message[1]);
					fastforwardTime *= 1000;
					Constants.fastforwardGain = new Duration(fastforwardTime);
					break;
				}
			}
		}
	}
	

}
