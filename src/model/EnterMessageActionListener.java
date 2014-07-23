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

	private final JTextField commentField;
	private final JTextArea logOutputField;
	private final AtomicInteger time;
	private int count;
	private List<String> lines; //internal structure to hold just the comments, in order
	
	public EnterMessageActionListener(JTextField commentField, JTextArea outputLog, AtomicInteger time) {
		this.commentField = commentField;
		this.logOutputField = outputLog;
		this.time = time;
		this.count = 1;
		this.lines = new ArrayList<String>();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String comment = commentField.getText();
		if (comment.matches("(\\s*rm)|(\\s*rm\\s+.*)")) { //remove command, handle and don't add comment
			comment = comment.trim();
			String[] parts = comment.split("\\s+");
			
			int lineNum = -1;
			int lineNum2 = -1;
			switch(parts.length) {
			case 1:
				lineNum = lines.size()-1;
				break;
			case 2:
				try {
					lineNum = Integer.parseInt(parts[1]) - 1;
				}
				catch (NumberFormatException nfe) {
					System.err.println("Arguments must be integers");
				}
				break;
			case 3:
				try {
					lineNum = Integer.parseInt(parts[1]) - 1;
					lineNum2 = Integer.parseInt(parts[2]) - 1;
				}
				catch (NumberFormatException nfe) {
					System.err.println("Arguments must be integers");
				}
				break;
			default:
				System.err.println("Incorrect number of arguments");
				break;
			}
			//TODO: error catching
			try {
				if (lineNum2 != -1) {
					count = lineNum;
					while (count <= lineNum2) {
						lines.remove(lineNum);
						count++;
					}
				}
				else 
					lines.remove(lineNum);
			}
			catch (IndexOutOfBoundsException obe) {
				//TODO: alert user of invalid command?
				System.err.println("Removal index out of bound");
			}

			writeArrayToField(lineNum);
		}
		else { //comment, add like normal
			String out = "";
			out += time.get();
			out += ": ";
			out += comment;
			out += '\n';

			lines.add(out);
			count++;
			writeArrayToField(-1);
		}
		commentField.setText("");
	}
	
	private void writeArrayToField(int lineToRemove) {
		logOutputField.setText(""); //clear field
		int count = 1;
		for (String line : this.lines) {
			logOutputField.append(makeCol(String.valueOf(count)) + line);
			count++;
		}
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
