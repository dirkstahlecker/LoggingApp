package audio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;

import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Called when text is entered in the audioSource textfield.
 * Always updates the source of the audio, and restarts it.
 * @author Dirk
 *
 */
public class SoundActionListener implements ActionListener {
	
	private final JTextField audioSource;
	private final BlockingQueue<String[]> audioQueue;
	
	public SoundActionListener(JTextField audioSource, BlockingQueue<String[]> audioQueue) {
		this.audioSource = audioSource;
		this.audioQueue = audioQueue;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String path = audioSource.getText();
		audioQueue.add(new String[]{"init",path});
		audioSource.setText("");
	}
}
