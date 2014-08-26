package model;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class PopupDialog {
	
	private final JFrame frame;
	
	public PopupDialog(JFrame frame) {
		this.frame = frame;
	}
	
	/**
	 * Display a generic message box
	 * @param messageText message to display
	 */
	public void showMessage(String messageText) {
		JOptionPane.showMessageDialog(this.frame,messageText);
	}
	static public void showMessage(JFrame frame,String messageText) {
		JOptionPane.showMessageDialog(frame,messageText);
	}
	
	/**
	 * Display an error dialog
	 * @param messageText text to display
	 * @param titleText title of the dialog box
	 */
	public void showError(String messageText, String titleText) {
		JOptionPane.showMessageDialog(this.frame,messageText,titleText,JOptionPane.ERROR_MESSAGE);
	}
	static public void showError(JFrame frame,String messageText, String titleText) {
		JOptionPane.showMessageDialog(frame,messageText,titleText,JOptionPane.ERROR_MESSAGE);
	}
	public void showError(String messageText) {
		JOptionPane.showMessageDialog(this.frame,messageText,"Error",JOptionPane.ERROR_MESSAGE);
	}
	static public void showError(JFrame frame,String messageText) {
		JOptionPane.showMessageDialog(frame,messageText,"Error",JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Prompts the user for input into a single text field
	 * @param messageText text to display
	 * @param titleText title of the dialog box
	 * @return String representing text input to the box
	 */
	public String showInputBox(String messageText,String titleText) {
		String s = JOptionPane.showInputDialog(frame,messageText,titleText,JOptionPane.PLAIN_MESSAGE);
		return s;
	}
	public static String showInputBox(JFrame frame,String messageText,String titleText) {
		String s = JOptionPane.showInputDialog(frame,messageText,titleText,JOptionPane.PLAIN_MESSAGE);
		return s;
	}
}
