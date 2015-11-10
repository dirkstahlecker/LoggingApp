package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Listens to the comment box, waiting for user to enter a comment.
 * Updates the display area with the timestamp and the comment
 * @author Dirk
 *
 */
public class AddToOutputQueue implements ActionListener {

	private String message;
	private BlockingQueue<String> outputQueue;
	
	public AddToOutputQueue(BlockingQueue<String> outputQueue, String message) {
		this.outputQueue = outputQueue;
		this.message = message;
	}
	
	@Override
	public synchronized void actionPerformed(ActionEvent e) {
		outputQueue.add(message);
	}
}