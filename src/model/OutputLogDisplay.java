package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import view.OutputArrayElement;

public class OutputLogDisplay implements Runnable {

	private final BlockingQueue<String> outputQueue;
	private final JTextField commentField;
	private final JTextPane logOutputField;
	private final AtomicInteger time;
	private int count;
	private List<OutputArrayElement> lines; //internal structure to hold just the comments, in order
	
	public OutputLogDisplay(BlockingQueue<String> outputQueue, JTextField commentField, JTextPane outputLog, AtomicInteger time) {
		this.outputQueue = outputQueue;
		this.commentField = commentField;
		this.logOutputField = outputLog;
		this.time = time;
		this.count = 1;
		this.lines = new ArrayList<OutputArrayElement>();
	}
	
	@Override
	public void run() {
		System.out.println("Running outputLogDisplay");
		while (true) {
			String message = outputQueue.poll();
			if (message != null) {
				if (Constants.DEBUG) System.out.println("message: " + message);
				switch(message) {
				case "enter highlight":
					enterText(true);
					break;
				case "enter":
					enterText(false);
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
	public void enterText(boolean highlight) {
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
			writeArrayToField();
		}
		else { //comment, add like normal
			String out = "";
			out += time.get();
			out += ": ";
			out += comment;
			out += '\n';

			lines.add(new OutputArrayElement(out));
			count++;
			writeArrayToField(highlight);
			//appendToField(out, null, new Color(255,255,0));
		}
		commentField.setText("");
	}
	
	/**
	 * Instead of clearing and rewriting, this just appends to the end
	 * @param textColor
	 * @param background
	 */
	private void appendToField(String line, Color textColor, Color background) {
		StyledDocument doc = logOutputField.getStyledDocument();
		SimpleAttributeSet keyWord = new SimpleAttributeSet();
		if (textColor != null) {
			StyleConstants.setForeground(keyWord, textColor);
		}
		if (background != null) {
			StyleConstants.setBackground(keyWord, background);
		}
		
		try {
		    doc.insertString(doc.getLength(), makeCol(String.valueOf(count)) + line, keyWord );
		}
		catch(Exception e) { 
			System.out.println(e); 
		}
	}
	
	//Overload for default text color and background
	private void writeArrayToField() {
		writeArrayToField(null, null);
	}
	
	private void writeArrayToField(boolean highlight) {
		if (highlight) {
			writeArrayToField(null, new Color(255,255,0)); //default yellow
		}
		else {
			writeArrayToField();
		}
	}
	
	/**
	 * Clear and replace the output text
	 * @param lineToRemove
	 */
	private void writeArrayToField(Color textColor, Color background) {
		logOutputField.setText(""); //clear field
		int count = 1;
		for (OutputArrayElement lineObj : this.lines) {
			String line = lineObj.getLine();

			StyledDocument doc = logOutputField.getStyledDocument();

			SimpleAttributeSet keyWord = new SimpleAttributeSet();
			if (textColor != null) {
				StyleConstants.setForeground(keyWord, textColor); //TODO: do something with this
			}
			if (lineObj.isHighlighted()) {
				StyleConstants.setBackground(keyWord, lineObj.getHighhlightColor());
			}
			//StyleConstants.setBold(keyWord, true);

			//Add text
			try {
			    doc.insertString(doc.getLength(), makeCol(String.valueOf(count)) + line, keyWord);
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
		this.lines = new ArrayList<OutputArrayElement>();
		for (String line : newLines) {
			String out = "";
			out += time.get();
			out += ": ";
			out += line;
			out += '\n';

			lines.add(new OutputArrayElement(out));
			writeArrayToField();
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
