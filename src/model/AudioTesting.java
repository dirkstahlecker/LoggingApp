package model;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This is an example program that demonstrates how to play back an audio file
 * using the Clip in Java Sound API.
 * @author www.codejava.net
 *
 */
public class AudioTesting implements LineListener {

	boolean playCompleted;
	String pause = "";

	/**
	 * Play a given audio file.
	 * @param audioFilePath Path of the audio file.
	 */
	void play(String audioFilePath) {
		File audioFile = new File(audioFilePath);
		Timer timer = new Timer();
		timer.schedule(new AudioPause(), 2000);
		timer.schedule(new AudioResume(), 4000);

		try {
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
			AudioFormat format = audioStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);

			Clip audioClip = (Clip) AudioSystem.getLine(info);
			audioClip.addLineListener(this);
			audioClip.open(audioStream);
			audioClip.start();

			while (!playCompleted) {
				if (pause.equals("pause")) {
					audioClip.stop();
				}
				else if (pause.equals("resume")) {
					audioClip.start();
				}
				else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}

			audioClip.close();

		} catch (UnsupportedAudioFileException ex) {
			System.out.println("The specified audio file is not supported.");
			ex.printStackTrace();
		} catch (LineUnavailableException ex) {
			System.out.println("Audio line for playing back is unavailable.");
			ex.printStackTrace();
		} catch (IOException ex) {
			System.out.println("Error playing the audio file.");
			ex.printStackTrace();
		}

	}

	/**
	 * Listens to the START and STOP events of the audio line.
	 */
	@Override
	public void update(LineEvent event) {
		LineEvent.Type type = event.getType();

		if (type == LineEvent.Type.START) {
			System.out.println("Playback started.");

		} else if (type == LineEvent.Type.STOP) {
			//playCompleted = true;
			System.out.println("Playback completed.");
		}

	}
	
	private class AudioPause extends TimerTask{
		@Override
		public void run() {
			pause = "pause";
		}
	}
	
	private class AudioResume extends TimerTask{
		@Override
		public void run() {
			pause = "resume";
		}
	}

	public static void main(String[] args) {
		String audioFilePath = "/Users/Dirk/Desktop/Takeoff1.wav";
		AudioTesting player = new AudioTesting();
		player.play(audioFilePath);
	}
}
