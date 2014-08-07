package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JTextArea;
import javax.swing.JTextField;

public class OutputLogDisplay implements Runnable {

	private final BlockingQueue<String> outputQueue;
	private final JTextField commentField;
	private final JTextArea logOutputField;
	private final AtomicInteger time;
	private int count;
	private List<String> lines; //internal structure to hold just the comments, in order

	public OutputLogDisplay(BlockingQueue<String> outputQueue, JTextField commentField, JTextArea outputLog, AtomicInteger time) {
		this.outputQueue = outputQueue;
		this.commentField = commentField;
		this.logOutputField = outputLog;
		this.time = time;
		this.count = 1;
		this.lines = new ArrayList<String>();
	}

	@Override
	public void run() {
		System.out.println("Running outputLogDisplay");
		while (true) {
			String message = outputQueue.poll();
			if (message != null) {
				if (Constants.debug) System.out.println("message: " + message);
				switch(message) {
				case "enter":
					enterText();
					break;
				case "clear":
					clear();
					break;
				}
			}
		}	
	}
	
	public void clear() {
		logOutputField.setText("");
	}

	public void enterText() {
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

	/**
	 * Creates equally spaced columns
	 * @param inp string to format
	 * @return string with the proper number of spaces appended
	 */
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
