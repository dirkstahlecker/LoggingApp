package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class OutputLogDisplay implements Runnable {

	private final BlockingQueue<String> outputQueue;
	private final JTextField commentField;
	private final JTextPane logOutputField;
	private final AtomicInteger time;
	private int count;
	private List<String> lines; //internal structure to hold just the comments, in order
	
	public OutputLogDisplay(BlockingQueue<String> outputQueue, JTextField commentField, JTextPane outputLog, AtomicInteger time) {
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
	
	/**
	 * Add text to the output log
	 */
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

	/**
	 * Clear and replace the output text
	 * @param lineToRemove
	 */
	private void writeArrayToField(int lineToRemove) {
		logOutputField.setText(""); //clear field
		int count = 1;
		for (String line : this.lines) {
			//logOutputField.append(makeCol(String.valueOf(count)) + line);
			
			
			StyledDocument doc = logOutputField.getStyledDocument();

			//  Define a keyword attribute
			SimpleAttributeSet keyWord = new SimpleAttributeSet();
			StyleConstants.setForeground(keyWord, Color.RED);
			StyleConstants.setBackground(keyWord, Color.YELLOW);
			StyleConstants.setBold(keyWord, true);

			//  Add some text
			try
			{
			    doc.insertString(doc.getLength(), makeCol(String.valueOf(count)) + line, keyWord );
			}
			catch(Exception e) { 
				System.out.println(e); 
			}
			
			
			
			count++;
		}
	}
	
	/**
	 * Writes an entire list of strings to the output
	 * Mainly used when loading
	 * @param newLines list of lines to add
	 */
	public void rewriteField(List<String> newLines) {
		if (newLines.size() == 0) {
			logOutputField.setText("");
		}
		this.lines = new ArrayList<String>();
		for (String line : newLines) {
			String out = "";
			out += time.get();
			out += ": ";
			out += line;
			out += '\n';

			lines.add(out);
			writeArrayToField(-1);
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
