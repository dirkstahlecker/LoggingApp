package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class AudioControlActionListener implements ActionListener {
	
	private final BlockingQueue<String[]> audioQueue;
	private final String message;
	private final String message2;
	
	public AudioControlActionListener(BlockingQueue<String[]> audioQueue, String message) {
		this.audioQueue = audioQueue;
		this.message = message;
		this.message2 = "";
	}
	public AudioControlActionListener(BlockingQueue<String[]> audioQueue, String message, String message2) {
		this.audioQueue = audioQueue;
		this.message = message;
		this.message2 = message2;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		audioQueue.add(new String[]{message,message2});
	}
}