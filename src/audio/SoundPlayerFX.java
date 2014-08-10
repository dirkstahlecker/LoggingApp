package audio;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JLabel;

import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.util.Duration;
import model.Constants;

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

	public SoundPlayerFX(BlockingQueue<String[]> audioQueue, JLabel timeStamp, JLabel currentAudioSource, 
			AtomicInteger time, JButton playpause, BlockingQueue<String[]> performSaveQueue) {

		this.audioQueue = audioQueue;
		this.timeStamp = timeStamp;
		this.time = time;
		this.currentAudioSource = currentAudioSource;
		this.audioPlayer = null;
		this.playpause = playpause;
		this.performSaveQueue = performSaveQueue;
	}

	/**
	 * Configure the sound
	 * Can be called again to change the file
	 * @param audioFilePath the path to the sound file to play
	 */
	public void setupAudio(String inputPath, long startTime) {
		if (audioPlayer != null) {
			audioPlayer.stop();
		}
		audioFilePath = inputPath;
		
		//TODO: error handling
		if (!audioFilePath.startsWith("file://")) {
			audioFilePath = "file://" + audioFilePath;
		}
		
		System.out.println("Audio source: " + audioFilePath); 
		audioPlayer = null;
		try {
			audioPlayer = new MediaPlayer(new Media(audioFilePath));
			
			length = convertTime(audioPlayer.getTotalDuration().toSeconds());
			outputTime();
			isPaused = true;
			
			currentAudioSource.setText("File: " + audioFilePath);
			volume = audioPlayer.getVolume();
			audioPlayer.setStartTime(new Duration(startTime)); //startTime in milliseconds
			
			System.out.println("audioPlayer: " + audioPlayer.toString());
			System.out.println("Player configured");
			addInfoToQueue();
		}
		catch (MediaException me) {
			System.err.println("MediaException");
		}
		catch (IllegalArgumentException iae) {
			System.err.println("IllegalArgumentException");
		}
		catch (UnsupportedOperationException uoe) {
			System.err.println("UnsupportedOperationException");
		}
		catch (NullPointerException npe) {
			System.err.println("NullPointerException");
		}
	}

	@Override
	public void run() {
		String[] message = null;

		//block until setup message initially seen
		while (true) {
			try {
				message = audioQueue.take();
				if (message[0].equals("init")) {
					long startTime;
					try {
						startTime = Long.parseLong(message[2]);
					}
					catch (NumberFormatException | NullPointerException e) {
						startTime = 0;
					}
					setupAudio(message[1],startTime);
					System.out.println("SoundPlayer: init");
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Listening for messages");
		//then listen to all message
		while (true) {
			message = audioQueue.poll();
			long index;
			int timeGain = 250000; //quarter second
			

			if (message != null) {
				if (Constants.debug) System.out.println("message: " + message[0]);
				
				switch (message[0]) {
				case "init":
					setupAudio(message[1],Long.parseLong(message[2]));
					break;
				case "playpause":
					playpause();
					break;
				/*
				case "rewind":
					index = audioClip.getMicrosecondPosition();
					index -= timeGain;
					if (index < 0) index = 0;
					audioClip.setMicrosecondPosition(index);
					System.out.println("rewinding");
					outputTime();
					break;
				case "fastforward":
					index = audioClip.getMicrosecondPosition();
					index += timeGain;
					if (index > length) index = (long)length;
					audioClip.setMicrosecondPosition(index);
					System.out.println("fast forwarding");
					outputTime();
					break;
				*/
				case "volume":
					volume(message[1]);
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
	private void playpause() {
		if (isPaused && audioPlayer != null) {
			if (Constants.debug) System.out.println("SoundPlayer: play");
			System.out.println("in play case");
			audioPlayer.play();
			isPaused = false;
			playpause.setText("Pause");
		}
		else if (audioPlayer != null) {
			if (Constants.debug) System.out.println("SoundPlayer: pause"); 
			audioPlayer.pause();
			isPaused = true;
			playpause.setText("Play");
		}
	}
	
	/**
	 * Adjusts the volume by a constant amount, Constants.volumeGain
	 * @param m string "up" or "down"
	 */
	private void volume(String m) {
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
	 * displays the current playback location to the gui
	 */
	private void outputTime() {
		if (audioPlayer != null) {
			currentTime = audioPlayer.getCurrentTime().toSeconds();
		}
		else {
			currentTime = 0;
		}
		currentTime = convertTime(currentTime);
		timeStamp.setText("     " + currentTime + "/" + length);
		time.set((int) currentTime);
	}
	
	private double currentTime() {
		return audioPlayer.getCurrentTime().toSeconds();
	}
	
	private double convertTime(double time) {
		return time;
	}
	
	private void addInfoToQueue() {
		performSaveQueue.poll();//don't care about old value, so remove it
		performSaveQueue.add(new String[]{audioFilePath,String.valueOf(currentTime())});
	}

}
