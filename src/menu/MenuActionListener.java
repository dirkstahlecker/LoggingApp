package menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;

import model.LoggingException;
import model.PopupDialog;

public class MenuActionListener implements ActionListener {
	
	private final JFrame frame;
	private final BlockingQueue<String[]> queue;
	private final String actionType;
	private final String messageText;
	private final String titleText;
	
	public MenuActionListener(String actionType, BlockingQueue<String[]> queue) {
		this.frame = null;
		this.queue = queue;
		this.actionType = actionType;
		this.messageText = null;
		this.titleText = null;
	}
	/*
	public MenuActionListener(JFrame frame, String actionType, BlockingQueue<String[]> queue, String messageText, String titleText) {
		this.frame = frame;
		this.queue = queue;
		this.actionType = actionType;
		if (messageText != null && titleText != null) {
			this.messageText = messageText;
			this.titleText = titleText;
		}
		else {
			this.messageText = "";
			this.titleText = "";
			//throw new LoggingException("Invalid input to MenuActionListener (null values)");
			//TODO: do something here
		}
	}*/
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (messageText == null) {
			queue.add(new String[]{actionType});
		}
		else {
			String s = PopupDialog.showInputBox(frame, messageText, titleText);
			queue.add(new String[]{actionType,s});
		}
	}

}
