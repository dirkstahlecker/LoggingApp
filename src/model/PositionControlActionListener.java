package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class PositionControlActionListener implements ActionListener {
	
	private final BlockingQueue<String[]> audioQueue;
	private final String message;
	
	public PositionControlActionListener(BlockingQueue<String[]> audioQueue, String message) {
		this.audioQueue = audioQueue;
		this.message = message;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		audioQueue.add(new String[]{message,""});
	}
}