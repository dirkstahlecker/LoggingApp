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

}
