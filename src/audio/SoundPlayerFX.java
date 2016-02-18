package audio;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import view.SetupUtils;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.util.Duration;
import model.Constants;
import model.Globals;
import model.PopupDialog;

/**
 * Controls the playback of the audio. Must run in a separate
 * thread, and continually listens to a queue for instructions.
 * This is the JavaFX version
 * @author Dirk
 *
 */
public class SoundPlayerFX implements Runnable {

	private BlockingQueue<String[]> audioQueue;
	private double length;
	private double currentTime;
	private final JLabel timeStamp;
	private double volume;
	private final JLabel currentAudioSource;
	private Boolean isPaused;
	private AtomicInteger time; //set current time here so other threads can read it
	private MediaPlayer audioPlayer;
	private final JButton playpause;
	private final BlockingQueue<String[]> performSaveQueue;
	private String audioFilePath;
	private final PopupDialog popupDialog;
	private boolean initialSetup = false; //false if audio isn't set up, to prevent errors
	private JProgressBar audioProgressBar;
	//private final JSlider slider;

	public SoundPlayerFX(BlockingQueue<String[]> audioQueue, JLabel timeStamp, JLabel currentAudioSource, 
			AtomicInteger time, JButton playpause, BlockingQueue<String[]> performSaveQueue, JFrame frame, 
			JProgressBar audioProgressBar) {

		this.audioQueue = audioQueue;
		this.timeStamp = timeStamp;
		this.time = time;
		this.currentAudioSource = currentAudioSource;
		this.audioPlayer = null;
		this.playpause = playpause;
		this.performSaveQueue = performSaveQueue;
		this.popupDialog = new PopupDialog(frame);
		this.audioProgressBar = audioProgressBar;
		//this.slider = slider;
	}

	/**
	 * Configure the sound
	 * Can be called again to change the file
	 * @param inputPath path to the audio file to play. If empty, initialize no file (but don't throw exception)
	 * @param startTime time in seconds to start playing the audio. If empty, starts at beginning
	 */
	public synchronized void setupAudio(String inputPath, long startTime) {
		initialSetup = false;
		if (audioPlayer != null) {
			audioPlayer.stop();
		}
		audioFilePath = inputPath;
		if (audioFilePath == "" || audioFilePath == null) { //don't do anything if empty or null - there's no file to load
			currentAudioSource.setText("File: none");
			Globals.log("No audio file to set up");
			
			//used to just return here - now trying to do everything else that doesn't throw an error
			//////////////////////////////////////////

			audioPlayer = null;
			
			outputTime();
			isPaused = true;
			
			currentAudioSource.setText("No audio file loaded");
			volume = 0;
			
			Duration rawDuration = new Duration(0);
			double rawLength = rawDuration.toSeconds();
			//length = convertTime(rawLength);
			length = rawLength;
			
			SetupUtils.setTimeStampText(timeStamp, startTime * 1000, length);
			
			//set the initial configuration of the progress bar
			try {
				audioProgressBar.setModel(new DefaultBoundedRangeModel(0,1,0,(int)length));
			}
			catch (IllegalArgumentException iae) {
				Globals.log("Illegal argument in progress bar", true);
				Globals.log("length: " + length, true);
				Globals.log("rawLength: " + rawLength, true);

				//this exception is triggered only sometimes - maybe based on specific threading calls
				//TODO: this must run in a non-deterministic order - exception is thrown sometimes, not others
			}
			
			initialSetup = true;

			
			//////////////////////////////////////////
			
			
			return; //TODO: this is returning early I think - something needs to run after this to not freeze? 
		}
		
		Globals.log("Audio source: " + audioFilePath); 
		audioPlayer = null;
		try {
			audioPlayer = new MediaPlayer(new Media(audioFilePath));
			audioPlayer.play(); //check if it works
			audioPlayer.stop();
			//TODO: slow to throw exception when illegal
			Globals.log("audioPlayer: " + audioPlayer.toString());
			
			outputTime();
			isPaused = true;
			
			currentAudioSource.setText("File: " + audioFilePath);
			volume = audioPlayer.getVolume();
			//audioPlayer.setStartTime(new Duration(startTime * 1000)); //startTime is in seconds, so convert to milliseconds TODO: reenable
			
			changeRate(Constants.playbackRate,false);
			
			Globals.log("Player configured");
			addInfoToQueue();
			
			audioPlayer.play();
			Duration rawDuration = audioPlayer.getTotalDuration();
			double rawLength = rawDuration.toSeconds();
			//length = convertTime(rawLength);
			length = rawLength;
			audioPlayer.stop();
			
			seek(startTime * 10000);
			
			SetupUtils.setTimeStampText(timeStamp, startTime * 1000, length);
			
			int count = 0;
			while (audioPlayer.getStatus() != MediaPlayer.Status.READY) {
				count++;
				if (count > 100000) { //naive safeguard against infinite looping
					break;
				}
				continue;
			}
			
			//set the initial configuration of the progress bar
			try {
				audioProgressBar.setModel(new DefaultBoundedRangeModel(0,1,0,(int)length));
			}
			catch (IllegalArgumentException iae) {
				Globals.log("Illegal argument in progress bar", true);
				Globals.log("length: " + length, true);
				Globals.log("rawLength: " + rawLength, true);

				//this exception is triggered only sometimes - maybe based on specific threading calls
				//TODO: this must run in a non-deterministic order - exception is thrown sometimes, not others
			}
			
			outputTime();
			initialSetup = true;
		}
		catch (MediaException me) {
			popupDialog.showError("Invalid file", "Error");
			Globals.log("Invalid file", true);
		}
		catch (IllegalArgumentException iae) {
			popupDialog.showError("Invalid file", "Error");
			Globals.log("IllegalArgumentException",true);
		}
		catch (UnsupportedOperationException uoe) {
			popupDialog.showError("Invalid file", "Error");
			Globals.log("UnsupportedOperationException",true);
		}
		catch (NullPointerException npe) {
			popupDialog.showError("Invalid file", "Error");
			Globals.log("NullPointerException",true);
		}
	}

	//just a handler - all specific implementations are abstracted into separate functions
	@Override
	public synchronized void run() {
		String[] message = null;
		
		//listen to all message
		while (true) {
			message = audioQueue.poll();
			
			if (message != null) {
				Globals.log("message: " + message[0]);
				
				switch (message[0]) {
				case "init":
					long startTime;
					try {
						startTime = Long.parseLong(message[2]);
					}
					catch (NumberFormatException | NullPointerException | ArrayIndexOutOfBoundsException e) {
						startTime = 0;
					}
					setupAudio(message[1],startTime);
					Globals.log("Finished with setupAudio");
					break;
				case "playpause":
					if (!initialSetup) break;
					playpause();
					break;
				case "rate":
					if (message[2] == null || message[2].equals("") || message[2].equals(" ")) {
						break;
					}
					
					try {
						double newRate = Double.parseDouble(message[2]);
						changeRate(newRate,true);
					}
					catch (NumberFormatException e) {
						//PopupDialog.showError(frame, "Rate must be an integer", "Error"); //need frame here
						//TODO: throw error here?
						Globals.log("Error converting rate to double",true);
					}
					break;
				case "rewind":
					rewind();
					break;
				case "fastforward":
					fastforward();
					break;
				case "volume":
					if (!initialSetup) break;
					volume(message[1]);
					break;
				case "seek":
					seek(message[1]);
					break;
				}
			}
			
			if (isPaused != null && !isPaused) { //checking against null necessary to not throw exception
				outputTime();
				addInfoToQueue();
			}
		}
	}
	
	/**
	 * Plays or pauses the audio, depending on its current state
	 */
	private synchronized void playpause() {
		if (isPaused && audioPlayer != null) {
			Globals.log("SoundPlayer: play");
			audioPlayer.play();
			isPaused = false;
			playpause.setText("Pause");
		}
		else if (audioPlayer != null) {
			Globals.log("SoundPlayer: pause"); 
			audioPlayer.pause();
			isPaused = true;
			playpause.setText("Play");
		}
	}
	
	private synchronized void rewind() {
		if (!initialSetup) {
			return;
		}
		Globals.log("rewinding");
		Duration index = audioPlayer.getCurrentTime();
		index = index.subtract(Constants.rewindGain);
		audioPlayer.seek(index);
		outputTime();
	}
	
	private synchronized void fastforward() {
		if (!initialSetup) {
			return;
		}
		Globals.log("fastforwarding");
		Duration index = audioPlayer.getCurrentTime();
		index = index.add(Constants.fastforwardGain);
		audioPlayer.seek(index);
		outputTime();
	}
	
	/**
	 * Moves the audio to a particular point
	 * Used to jump to positions when clicking a link
	 * @param pos
	 */
	private synchronized void seek(String pos) {
		//TODO: error handling
		seek(Long.parseLong(pos));
	}
	private synchronized void seek(long index) {
		if (audioPlayer != null) {
			audioPlayer.seek(new Duration(index * 1000));
			outputTime();
		}
		else {
			Globals.log("audioPlayer is null; can't seek");
		}
	}
	
	/**
	 * Adjusts the volume by a constant amount, Constants.volumeGain
	 * @param m string "up" or "down"
	 */
	private synchronized void volume(String m) {
		if (m.equals("up")) {
			volume = audioPlayer.getVolume() + Constants.volumeGain;
		}
		else {
			volume = audioPlayer.getVolume() - Constants.volumeGain;
		}
		if (volume > 1) {
			volume = 1;
		}
		else if (volume < 0) {
			volume = 0;
		}
		audioPlayer.setVolume(volume);
	}
	
	/**
	 * Change the rate of playback
	 * @param rate new playback speed (0.0 to 8.0)
	 */
	private synchronized void changeRate(double rate, boolean showMessage) {
		if (rate < 0.0) {
			rate = 0.0;
		}
		else if (rate > 8.0) {
			rate = 8.0;
		}
		System.out.println("setting rate to: " + rate);
		
		if (showMessage) {
			popupDialog.showMessage("Playback rate set to " + rate);
		}
		Constants.playbackRate = rate;
		
		if (audioPlayer != null) {
			audioPlayer.setRate(rate);
		}
	}
	
	/**
	 * Displays the current playback location to the gui
	 * Also update the progress bar
	 */
	private synchronized void outputTime() {
		if (audioPlayer != null) {
			currentTime = audioPlayer.getCurrentTime().toSeconds();
		}
		else {
			currentTime = 0;
		}
		String timeStr = convertTime(currentTime);
		
		timeStamp.setText("     " + timeStr + "/" + length);
		time.set((int) currentTime);
		try {
			audioProgressBar.setValue((int)currentTime);
		}
		catch (IllegalArgumentException iae) {
			Globals.log("Illegal Argument Exception for progress bar",true);
			Globals.log("currentTime: " + currentTime,true);
		}
		/*
		if (currentTime >= length) { //at the end of the file
			//TODO: should this happen here? should this be more elegant?
			playpause.setText("Play");
		}
		*/
	}
	
	private synchronized double currentTime() {
		return audioPlayer.getCurrentTime().toSeconds();
	}
	
	/**
	 * Convert time from seconds to minutes:seconds
	 * @param time time in seconds
	 * @return time in minutes:seconds
	 */
	private synchronized String convertTime(double time) {
		int minutes;
		int seconds;
		minutes = (int)time / 60;
		seconds = (int)time % 60;
		
		return minutes + ":" + seconds;
	}
	
	private synchronized void addInfoToQueue() {
		performSaveQueue.poll();//don't care about old value, so remove it
		performSaveQueue.add(new String[]{audioFilePath,String.valueOf(currentTime())});
	}
	
	//TODO: add listener to when the file ends, and change the playpause text to "play"

}
