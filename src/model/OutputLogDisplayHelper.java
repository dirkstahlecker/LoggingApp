package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JTextField;
import javax.swing.JTextPane;

import view.OutputArrayElement;

/**
 * Used to hold methods that need to be called with arguments, outside of the queue.
 * Inherits from OutputLogDisplayRunnable to have access to fields
 * @author Dirk
 *
 */
public class OutputLogDisplayHelper{
	
	private JTextPane logOutputField;

	public OutputLogDisplayHelper(JTextPane logOutputField) {
		this.logOutputField = logOutputField;
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
	

}
