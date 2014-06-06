package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import model.SoundPlayer;

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
	private final SoundPlayer player;

	/**
	 * initializes fields
	 * @param playpause the play/pause button
	 */
	public PauseActionListener(SoundPlayer player, JButton playpause) {
		this.playpause = playpause;
		this.player = player;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (player.getState().equals("play")) { //need to play
			player.play();
		}
		else if (player.getState().equals("pause")) { //need to pause
			player.pause();
		}
		else if (player.getState().equals("init")) { //need to initialize with textfield value

		}

		if (isPaused) {
			playpause.setText("Pause");
		}
		else {
			playpause.setText("Play");
		}
	}
}
