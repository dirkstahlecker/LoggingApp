package model;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
//import java.util.Timer;
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
public class SoundPlayer implements LineListener {

	private boolean playCompleted = false;
	private String state = "init";
	private File audioFile;
	private Clip audioClip;

	public void setSource(String filePath) {
		audioFile = new File(filePath);
		System.out.println(filePath);

		try {
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
			AudioFormat format = audioStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);

			audioClip = (Clip) AudioSystem.getLine(info);
			audioClip.addLineListener(this);

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

	public void play() {
		if (!playCompleted) {
			if (state.equals("pause") || state.equals("init")) {
				System.out.println("Playing");
				audioClip.start();
				System.out.println("after playing");
			}
		}
	}

	public void pause() {
		audioClip.stop();
	}

	/**
	 * Play a given audio file.
	 * @param audioFilePath Path of the audio file.
	 */
	/*public void play() {

		while (!playCompleted) {
			if (state.equals("pause")) {
				audioClip.stop();
			}
			else if (state.equals("resume") || state.equals("init")) {
				System.out.println("Playing");
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
	}*/

	public String getState() {
		return new String(this.state);
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
			if (state.equals("play")) {
				//if state is play, we didn't call to pause
				//therefore, the file has played through
				playCompleted = true;
			}

			System.out.println("Playback completed.");
		}

	}

	private class AudioPause extends TimerTask{
		@Override
		public void run() {
			state = "pause";
		}
	}

	private class AudioResume extends TimerTask{
		@Override
		public void run() {
			state = "resume";
		}
	}

	public static void main(String[] args) {
		String audioFilePath = "/Users/Dirk/Desktop/Takeoff1.wav";
		SoundPlayer player = new SoundPlayer();
		player.setSource(audioFilePath);
		player.play();
	}
}
