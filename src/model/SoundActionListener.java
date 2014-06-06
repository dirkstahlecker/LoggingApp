package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

public class SoundActionListener implements ActionListener {
	
	private final SoundPlayer player;
	private final JTextField audioSource;
	
	public SoundActionListener(SoundPlayer player, JTextField audioSource) {
		this.player = player;
		this.audioSource = audioSource;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String path = audioSource.getText();
		player.setSource(path);
		audioSource.setText("");
		
		player.play();
	}

}
