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

	//private List<String> log = new ArrayList<String>();
	private final JTextField commentField;
	private final JTextArea logOutputField;
	private final AtomicInteger time;
	private int count;
	
	public EnterMessageActionListener(JTextField commentField, JTextArea outputLog, AtomicInteger time) {
		this.commentField = commentField;
		this.logOutputField = outputLog;
		this.time = time;
		this.count = 0;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String out = "";
		out += time.get();
		out += ": ";
		out += commentField.getText();
		out += '\n';
		
		//logOutputField.append(count + out);
		logOutputField.append(makeCol(String.valueOf(count)) + out);
		commentField.setText("");
		count++;
	}
	
	private String makeCol(String inp) {
		int maxLen = 4;
		int diff = 0;
		String out = inp;
		if (inp.length() != maxLen) 
			diff = maxLen - inp.length();
		else 
			 diff = 0;
		for (int i = 0; i < diff; i++) 
			out += ' ';
		return out + ": ";
	}
}
