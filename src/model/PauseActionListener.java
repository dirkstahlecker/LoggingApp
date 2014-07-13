package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;

import javax.swing.JButton;

/**
 * invoked when the game is paused or resumed
 *
 * utilizes a boolean to determines if it's paused, therefore allowing
 * this class to perform both pause and unpause actions
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
