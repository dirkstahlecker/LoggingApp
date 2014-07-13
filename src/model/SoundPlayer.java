package model;

import java.applet.AudioClip;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JLabel;

/**
 * Controls the playback of the audio. Must run in a separate
 * thread, and continually listens to a queue for instructions.
 * @author Dirk
 *
 */
public class SoundPlayer implements Runnable {

	private Clip audioClip;
	private BlockingQueue<String[]> audioQueue;
	private double length;
	private double currentTime;
	private final JLabel timeStamp;
	private FloatControl volume;
	private final JLabel currentAudioSource;
	private Boolean isPaused;
	private AtomicInteger time; //set current time here so other threads can read it
	
	public SoundPlayer(BlockingQueue<String[]> audioQueue, JLabel timeStamp, JLabel currentAudioSource, AtomicInteger time) {
		
		this.audioQueue = audioQueue;
		this.timeStamp = timeStamp;
		this.time = time;
		this.currentAudioSource = currentAudioSource;
		
	}
	
	/**
	 * Configure the sound
	 * Can be called again to change the file
	 * @param audioFilePath the path to the sound file to play
	 */
	public void setupAudio(String audioFilePath) {
		System.out.println("Audio source: " + audioFilePath);
		AudioInputStream audioStream;
		File audioFile = new File(audioFilePath);
		try {
			audioStream = AudioSystem.getAudioInputStream(audioFile);

			AudioFormat format = audioStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);

			audioClip = (Clip) AudioSystem.getLine(info);
			audioClip.open(audioStream);
			
			length = audioClip.getMicrosecondLength();
			length = length / 1000000;
			length = (double)Math.round(length * 1000.0) / 1000.0;
			outputTime();
			isPaused = true;
			
			currentAudioSource.setText("File: " + audioFilePath);
			volume = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
			
			System.out.println("Player configured");
			
		} catch (LineUnavailableException e) { //Do nothing on catch - should alert the user
			audioClip = null;
			currentAudioSource.setText("Audio file not found");
		} catch (IOException e) {
			audioClip = null;
			currentAudioSource.setText("Audio file not found");
		} catch (UnsupportedAudioFileException e1) {
			audioClip = null;
			currentAudioSource.setText("Audio file not found");
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
					setupAudio(message[1]);
					System.out.println("SoundPlayer: init");
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//then listen to all message
		while (true) {
			message = audioQueue.poll();
			long index;
			int timeeGain = 250000; //quarter second
			float volumeGain = 2;
			
			if (message != null) {
				switch (message[0]) {
				case "init":
					if (audioClip != null) {
						audioClip.stop();
					}
					setupAudio(message[1]);
					if (Constants.debug) System.out.println("SoundPlayer: init");
					break;
				case "playpause":
					if (isPaused && audioClip != null) {
						audioClip.start();
						if (Constants.debug) System.out.println("SoundPlayer: play");
						isPaused = false;
					}
					else if (audioClip != null) {
						audioClip.stop();
						isPaused = true;
					}
					break;
				case "rewind":
					index = audioClip.getMicrosecondPosition();
					index -= timeeGain;
					if (index < 0) index = 0;
					audioClip.setMicrosecondPosition(index);
					System.out.println("rewinding");
					outputTime();
					break;
				case "fastforward":
					index = audioClip.getMicrosecondPosition();
					index += timeeGain;
					if (index > length) index = (long)length;
					audioClip.setMicrosecondPosition(index);
					System.out.println("fast forwarding");
					outputTime();
					break;
				case "volume":
					if (message[1].equals("up")) {
						float newVolume = volume.getValue() + volumeGain;
						if (newVolume > volume.getMaximum()) 
							newVolume = volume.getMaximum();
						volume.setValue(newVolume);
					}
					else {
						float newVolume = volume.getValue() - volumeGain;
						if (newVolume < volume.getMinimum()) 
							newVolume = volume.getMinimum();
						volume.setValue(newVolume);
					}
					break;
				}
			}
			
			
			else { //no new message
				if (isPaused != null && !isPaused) { //checking against null necessary to not throw exception
					outputTime();
				}
			}
		}
	}
	
	private void outputTime() {
		currentTime = audioClip.getMicrosecondPosition();
		currentTime /= 1000000;
		currentTime = (double)Math.round(currentTime * 1000.0) / 1000.0;
		timeStamp.setText("     " + currentTime + "/" + length);
	}

	/**
	 * For local testing only - ultimately will be deleted
	 * @param args
	 */
	public static void main(String[] args) {
		String audioFilePath = "/Users/Dirk/Desktop/Takeoff1.wav";
		File audioFile = new File(audioFilePath);
		AudioTesting player = new AudioTesting();
		player.setupAudio(audioFile);
		player.play();

	}

}
