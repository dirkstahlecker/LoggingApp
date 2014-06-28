package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Listens to the comment box, waiting for user to enter a comment.
 * Updates the display area with the timestamp and the comment
 * @author Dirk
 *
 */
public class EnterMessageActionListener implements ActionListener {
	
	/*
	 * Held as a list because it will keep the different entries
	 * separate from one another. Possibly convert this to a dictionary
	 * or something later, with timestamp as key for ease of access.
	 */
	//private List<String> log = new ArrayList<String>();
	private final JTextField commentField;
	private final JTextArea logOutputField;
	private final AtomicInteger time;
	
	public EnterMessageActionListener(JTextField commentField, JTextArea outputLog, AtomicInteger time) {
		this.commentField = commentField;
		this.logOutputField = outputLog;
		this.time = time;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String out = "";
		out += time.get();
		out += ": ";
		out += commentField.getText();
		out += '\n';
		logOutputField.append(out);
		commentField.setText("");
	}
}
