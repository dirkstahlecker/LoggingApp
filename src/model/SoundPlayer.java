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
public class SoundPlayer implements Runnable, LineListener {

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
			
			long frames = audioStream.getFrameLength();
			length = (frames+0.0) / format.getFrameRate();
			
			audioClip = (Clip) AudioSystem.getLine(info);
			audioClip.addLineListener(this);
			audioClip.open(audioStream);
			
			timeStamp.setText("     0:00 / " + (double)Math.round(length * 1000) / 1000);
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
					
					if (isPaused) { //initial start
						if (Constants.debug) System.out.println("startTime: "+startTime+" currentTime: "+getTime()+" playedLength: "+playedLength);
						startTime = getTime() - playedLength;
					}
					else { //resume
						startTime = getTime();
					}
					isPaused = false;
					
					break;
				case "pause":
					if (Constants.debug) System.out.println("SoundPlayer: pause");
					audioClip.stop();
					isPaused = true;
					playedLength = getTime() - startTime; //elapsed time since starting to play
					if (Constants.debug) System.out.println("playedLength: " + playedLength);
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
		currentTime = getTime() - startTime;
		time.set((int)Math.round(currentTime));
		
		if (currentTime <= length) {
			timeStamp.setText("     " + (double)Math.round(currentTime * 1000) / 1000 + " / " + (double)Math.round(length * 1000) / 1000);
		}
	}
	
	private double getTime() {
		return System.nanoTime() / 10e8;
	}

	/**
	 * Listens to the START and STOP events of the audio line.
	 * Does nothing, because playback is controlled via the queue
	 */
	@Override
	public void update(LineEvent event) {
		LineEvent.Type type = event.getType();

		if (type == LineEvent.Type.START) {
			//System.out.println("Playback started.");

		} else if (type == LineEvent.Type.STOP) {
			//System.out.println("Playback completed.");
		}

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
