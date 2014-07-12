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

	private final JButton playpause;
	private boolean isPaused;
	private final BlockingQueue<String[]> audioQueue;

	/**
	 * initializes fields
	 * @param playpause the play/pause button
	 */
	public PauseActionListener(JButton playpause, BlockingQueue<String[]> audioQueue) {
		this.playpause = playpause;
		this.audioQueue = audioQueue;
		isPaused = false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		audioQueue.add(new String[]{"playpause",""});
		/*
		if (player.getState().equals("play")) { //need to play
			player.play();
			isPaused = false;
		}
		else if (player.getState().equals("pause")) { //need to pause
			player.pause();
			isPaused = true;
		}
		else if (player.getState().equals("init")) { //need to initialize with textfield value
			player.play();
			isPaused = false;
		}

		
		if (playpause.getText().equalsIgnoreCase("Play")) {
			//System.out.println("sending play message");
			audioQueue.add(new String[]{"play",""});
			playpause.setText("Pause");
		}
		else {
			//System.out.println("sending pause message");
			audioQueue.add(new String[]{"pause",""});
			playpause.setText("Play");
		}
		*/
	}
}
