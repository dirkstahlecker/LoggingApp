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
import javax.swing.text.html.HTMLDocument;

import menu.PerformFileAction;
import view.OutputArrayElement;

public class OutputLogDisplay implements Runnable {

	private final BlockingQueue<String> outputQueue;
	private final JTextField commentField;
	private final JTextPane logOutputField;
	private final AtomicInteger time;
	private int count;
	private List<OutputArrayElement> lines; //internal structure to hold just the comments, in order
	private boolean currentlyHighlighted;
	
	public OutputLogDisplay(BlockingQueue<String> outputQueue, JTextField commentField, JTextPane outputLog, AtomicInteger time) {
		this.outputQueue = outputQueue;
		this.commentField = commentField;
		this.logOutputField = outputLog;
		this.time = time;
		this.count = 1;
		this.lines = new ArrayList<OutputArrayElement>();
		this.currentlyHighlighted = false;
	}
	
	@Override
	public synchronized void run() {
		Globals.log("Running outputLogDisplay");
		while (true) {
			String message = outputQueue.poll();
			if (message != null) {
				Globals.log("message: " + message);
				switch(message) {
				case "toggle highlight":
					this.currentlyHighlighted = !this.currentlyHighlighted;
					Globals.log("highlighting is now " + (this.currentlyHighlighted ? "on" : "off"));
					break;
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
	
	public synchronized void clear() {
		//show confirmation and make sure user clicks yes
		if (FileDialogClass.showConfirmation("Are you sure you want to clear the log? (There is no undo)", "Are you sure?")) {
			logOutputField.setText("");
			lines.removeAll(lines);
		}
	}
	
	/**
	 * Add text to the output log
	 */
	public synchronized void enterText() {
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
					Globals.log("Arguments must be integers", true);
				}
				break;
			case 3:
				try {
					lineNum = Integer.parseInt(parts[1]) - 1;
					lineNum2 = Integer.parseInt(parts[2]) - 1;
				}
				catch (NumberFormatException nfe) {
					Globals.log("Arguments must be integers", true);
				}
				break;
			default:
				Globals.log("Incorrect number of arguments", true);
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
				Globals.log("Removal index out of bounds");
			}
			writeArrayToField();
		}
		else { //comment, add like normal
			String out = "";
			out += " <a href=\"http://" + time.get() + "\">" + time.get() + "</a>";
			out += ": ";
			out += comment;
			out += '\n';

			if (this.currentlyHighlighted)
				lines.add(new OutputArrayElement(out, true, Constants.YELLOW));
			else
				lines.add(new OutputArrayElement(out));
			count++;
			writeArrayToField(this.currentlyHighlighted);
			//appendToField(out, null, new Color(255,255,0));
		}
		commentField.setText("");
	}
	
	public synchronized String getTestString() {
		return "Test String from OutputLogDisplay";
	}
	
	/**
	 * Used to enter a string directly into the pane
	 * Does not additional markup on it; enters exactly as passed
	 * @param textIn String to enter
	 */
	public synchronized void enterTextNoAdditionalMarkup(String textIn) {
		logOutputField.setText(textIn);
	}
	public synchronized void enterTextNoAdditionalMarkup(List<String> textIn) {
		Globals.log("In enterTextNoAdditionalMarkup");
		String text = "";
		for (String s : textIn) {
			text += s;
			text += '\n';
		}
		Globals.log("got here");
		logOutputField.setText(text);
	}
	
	//Overload for default text color and background
	private synchronized void writeArrayToField() {
		writeArrayToField(null, null);
	}
	
	private synchronized void writeArrayToField(boolean highlight) {
		if (highlight) {
			writeArrayToField(null, Constants.YELLOW); //default yellow
		}
		else {
			writeArrayToField();
		}
	}
	
	/**
	 * Clear and replace the output text
	 * @param lineToRemove
	 */
	private synchronized void writeArrayToField(Color textColor, Color background) {
		logOutputField.setText(""); //clear field
		int count = 1;
		String newText = "";
		for (OutputArrayElement lineObj : this.lines) {
			String line = lineObj.getLine();

			//StyledDocument doc = logOutputField.getStyledDocument();
			//HTMLDocument doc = (HTMLDocument) logOutputField.getStyledDocument();

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
			    //doc.insertString(doc.getLength(), makeCol(String.valueOf(count)) + line + " <a href='#'>Click here</a>", keyWord);
				newText += makeCol(String.valueOf(count)) + line + "<br />";
			}
			catch(Exception e) { 
				Globals.log(e.toString(), true);
			}
			
			count++;
		}
		Globals.log("newText: " + newText);
		logOutputField.setText(newText);
	}
	
	/**
	 * Writes an entire list of strings to the output
	 * Mainly used when loading
	 * @param newLines list of lines to add
	 */
	public synchronized void rewriteField(List<String> newLines) {
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
	private synchronized String makeCol(String inp) {
		int maxLen = 4;
		int diff = 0;
		String out = inp;
		if (inp.length() != maxLen) 
			diff = maxLen - inp.length();
		else 
			diff = 0;
		for (int i = 0; i < diff; i++) 
			out += "&nbsp;";
		return out + ": ";
	}
}
