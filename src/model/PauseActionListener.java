package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;

import javax.swing.JButton;

/**
 * Invoked when the audio is paused or resumed
 * @author Dirk
 *
 */
public class PauseActionListener implements ActionListener {

	private final BlockingQueue<String[]> audioQueue;

	/**
	 * initializes fields
	 * @param playpause the play/pause button
	 */
	public PauseActionListener(BlockingQueue<String[]> audioQueue) {
		this.audioQueue = audioQueue;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		audioQueue.add(new String[]{"playpause",""});
	}
}
