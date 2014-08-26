package menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;

import model.PopupDialog;

public class PlaybackRateActionListener implements ActionListener {
/*
	private final JFrame frame;
	private final BlockingQueue<String[]> queue;
	private final String messageText;
	private final String titleText;
	
	public PlaybackRateActionListener(JFrame frame, BlockingQueue<String[]> queue, String messageText, String titleText) {
		this.frame = frame;
		this.queue = queue;
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
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (messageText == null) {
			queue.add(new String[]{"playback rate"});
		}
		else {
			String s = PopupDialog.showInputBox(frame, messageText, titleText);
			queue.add(new String[]{"playback rate",s});
		}
	}*/
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
}
