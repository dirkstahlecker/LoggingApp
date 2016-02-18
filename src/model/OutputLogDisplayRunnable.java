package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;

import audio.SoundPlayerFX;

import java.awt.Rectangle;

import menu.PerformFileAction;
import view.OutputArrayElement;

public class OutputLogDisplayRunnable implements Runnable {

	private final BlockingQueue<String> outputQueue;
	private final JTextField commentField;
	protected final JTextPane logOutputField;
	protected final JScrollPane scrollPane;
	protected final AtomicInteger time;
	private int count;
	protected List<OutputArrayElement> lines; //internal structure to hold just the comments, in order
	private boolean currentlyHighlighted;
	public OutputLogDisplayHelper outputHelper;
	
	public OutputLogDisplayRunnable(BlockingQueue<String> outputQueue, JTextField commentField, JTextPane outputLog, JScrollPane scrollPane,
			AtomicInteger time) {
		this.outputQueue = outputQueue;
		this.commentField = commentField;
		this.logOutputField = outputLog;
		this.scrollPane = scrollPane;
		this.time = time;
		this.count = 1;
		this.lines = new ArrayList<OutputArrayElement>();
		this.currentlyHighlighted = false;
		
		this.outputHelper = new OutputLogDisplayHelper(this.logOutputField);
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
				case "new":
					rewriteField(new ArrayList<String>());
					break;
				}
			}
		}	
	}
	
	protected synchronized void clear() {
		//show confirmation and make sure user clicks yes
		if (FileDialogClass.showConfirmation("Are you sure you want to clear the log? (There is no undo)", "Are you sure?")) {
			logOutputField.setText("");
			lines.removeAll(lines);
		}
	}
	
	/**
	 * Add text to the output log
	 */
	protected synchronized void enterText() {
		String comment = commentField.getText();
		/*
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
		*/
			
			String out = "";
			out += " <a href=\"http://" + time.get() + "\">" + SoundPlayerFX.convertTime(time.get()) + "</a>";
			out += Constants.SEPARATOR;
			out += comment;
			out += '\n';

			if (this.currentlyHighlighted)
				lines.add(new OutputArrayElement(out, true, Constants.YELLOW));
			else
				lines.add(new OutputArrayElement(out));
			count++;
			writeArrayToField(this.currentlyHighlighted);
			//appendToField(out, null, new Color(255,255,0));
		//}
		commentField.setText("");
	}

	/**
	 * Writes an entire list of strings to the output
	 * Mainly used when loading
	 * @param newLines list of lines to add
	 */
	private synchronized void rewriteField(List<String> newLines) {
		if (newLines.size() == 0) {
			logOutputField.setText("");
		}
		this.lines = new ArrayList<OutputArrayElement>();
		for (String line : newLines) {
			String out = "";
			out += time.get();
			out += Constants.SEPARATOR;
			out += line;
			out += '\n';

			lines.add(new OutputArrayElement(out));
			writeArrayToField();
		}
	}
	
	/**
	 * Clear and replace the output text
	 * @param lineToRemove
	 */
	protected synchronized void writeArrayToField(Color textColor, Color background) {
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
		
		//scroll to bottom
		
		JViewport viewPort = scrollPane.getViewport();
		/*
		Rectangle r = new Rectangle(logOutputField.getWidth(), logOutputField.getHeight(), logOutputField.getWidth(), logOutputField.getHeight());
		System.out.println(logOutputField.getHeight() + " " + logOutputField.getWidth());
        scrollPane.scrollRectToVisible(r);
		*/

		
		JScrollBar vertical = scrollPane.getVerticalScrollBar();
		vertical.setValue( vertical.getMaximum() );
	}
	
	//Overload for default text color and background
	protected synchronized void writeArrayToField() {
		writeArrayToField(null, null);
	}
	
	protected synchronized void writeArrayToField(boolean highlight) {
		if (highlight) {
			writeArrayToField(null, Constants.YELLOW); //default yellow
		}
		else {
			writeArrayToField();
		}
	}

	/**
	 * Creates equally spaced columns
	 * @param inp string to format
	 * @return string with the proper number of spaces appended
	 */
	protected synchronized String makeCol(String inp) {
		int maxLen = 4;
		int diff = 0;
		String out = inp;
		if (inp.length() != maxLen) 
			diff = maxLen - inp.length();
		else 
			diff = 0;
		for (int i = 0; i < diff; i++) 
			out += "&nbsp;";
		return out + Constants.SEPARATOR;
	}
}
