package audio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;

import javax.swing.JLabel;
import javax.swing.JTextField;

import model.Globals;

public class AudioControlActionListener implements ActionListener {
	
	private final BlockingQueue<String[]> audioQueue;
	private final String message;
	private final String message2;
	private final JTextField rateField;
	
	public AudioControlActionListener(BlockingQueue<String[]> audioQueue, String message) {
		this.audioQueue = audioQueue;
		this.message = message;
		this.message2 = "";
		this.rateField = new JTextField();
	}
	public AudioControlActionListener(BlockingQueue<String[]> audioQueue, String message, String message2) {
		this.audioQueue = audioQueue;
		this.message = message;
		this.message2 = message2;
		this.rateField = new JTextField();
	}
	public AudioControlActionListener(BlockingQueue<String[]> audioQueue, String message, JTextField rateField) {
		this.audioQueue = audioQueue;
		this.message = message;
		this.message2 = "";
		this.rateField = rateField;
		
		Globals.log("instantiated action listener for rate");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		audioQueue.add(new String[]{message,message2,rateField.getText()});
		rateField.setText("");
	}
}