package menu;

import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MenuController implements Runnable {
	
	private final BlockingQueue<String> queue;
	private final JFrame frame;
	
	public MenuController(BlockingQueue<String> queue, JFrame frame) {
		this.queue = queue;
		this.frame = frame;
	}

	@Override
	public void run() {
		String message = null;
		
		while(true) {
			message = queue.poll();
			if (message != null) {

				switch(message) {
				case "new":
					
					break;
				case "open":
					
					break;
				case "save":
					
					break;
				case "save as":
					
					break;
				case "export txt":
					
					break;
				case "export pdf":
					
					break;
				case "export excel":
					
					break;
				case "about":
					JOptionPane.showMessageDialog(frame, "Created by Dirk Stahlecker\nCopyright 2014","About",JOptionPane.PLAIN_MESSAGE);
					break;
				case "view help":
					
					break;
				}
			}
		}
		
	}
}