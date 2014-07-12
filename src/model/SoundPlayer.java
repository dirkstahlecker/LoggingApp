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
	private double startTime;
	private double playedLength;
	private final JLabel timeStamp;
	private Boolean isPaused;
	private AtomicInteger time; //set current time here so other threads can read it
	
	public SoundPlayer(BlockingQueue<String[]> audioQueue, JLabel timeStamp, AtomicInteger time) {
		this.audioQueue = audioQueue;
		this.timeStamp = timeStamp;
		this.time = time;
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
			
			//FloatControl volume = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
			//volume.setValue(-20);
			
			System.out.println("Player configured");
			
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
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
			if (message != null) {
				switch (message[0]) {
				case "init":
					setupAudio(message[1]);
					if (Constants.debug) System.out.println("SoundPlayer: init");
					break;
				case "play":
					audioClip.start();
					if (Constants.debug) System.out.println("SoundPlayer: play");
					isPaused = false;
					
					break;
				case "pause":
					if (Constants.debug) System.out.println("SoundPlayer: pause");
					audioClip.stop();
					isPaused = true;
					if (Constants.debug) System.out.println("playedLength: " + playedLength);
					break;
				case "rewind":
					long index = audioClip.getMicrosecondPosition();
					System.out.println("odl index: " + index);
					index -= 500;
					System.out.println("new index:" + index);
					if (index < 0) index = 0;
					audioClip.setMicrosecondPosition(index);
					System.out.println("rewinding");
					outputTime();
					break;
				case "fastfoward":
					System.out.println("fastforwarding");
					break;
				}
			}
			
			
			else { //no new message
				if (isPaused != null && !isPaused) { //checking against null necessary to not throw exception
					//this is necessary since we don't want to display anything from here when the playback hasn't started
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
