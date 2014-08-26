package menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;

public class MenuInputActionListener implements ActionListener {
	
	private final BlockingQueue<String[]> queue;
	private final String actionType;
	
	public MenuInputActionListener(String actionType, BlockingQueue<String[]> queue) {
		this.queue = queue;
		this.actionType = actionType;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		queue.add(new String[]{actionType});
	}

}
