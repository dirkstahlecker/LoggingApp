package menu;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class PerformSave implements ActionListener {
	
	private final BlockingQueue<String[]> performSaveQueue;
	private final JTextArea log;
	private final JFrame frame;
	
	public PerformSave(JFrame frame, BlockingQueue<String[]> performSaveQueue, JTextArea log) {
		this.log = log;
		this.performSaveQueue = performSaveQueue;
		this.frame = frame;
	}
	
	/**
	 * Save all relevant information to a text file
	 */
	public void save() {
		String out = "";
		//store stuff in a text file
		/*
		 * audio file path\n
		 * playback position\n
		 * full log text
		 */
		String audioPath = "";
		int position = 0;
		String[] message = null;
		
		message = performSaveQueue.poll();
		if (message != null) {
			audioPath = message[0];
			position = (int)Double.parseDouble(message[1]);
		}
		else {
			//TODO: raise error
			System.err.println("Message empty");
		}

		out += audioPath + '\n';
		out += String.valueOf(position) + '\n';
		
		out += log.getText();
		System.out.println("opening save dialog");
		System.setProperty("apple.awt.fileDialogForDirectories", "true");
		FileDialog fileDialog = new FileDialog(frame, "Choose a folder to save to", FileDialog.SAVE);
		fileDialog.setFile("*.txt");
		fileDialog.setVisible(true);

		if (fileDialog.getFile() != null) { //user clicked okay and not cancel 
			String outputFilePath = fileDialog.getDirectory() + fileDialog.getFile();
			File file = new File(outputFilePath);

			PrintWriter fileWriter;
			try {
				fileWriter = new PrintWriter(file,"UTF-8");
				fileWriter.write(out);
				fileWriter.close();
				JOptionPane.showMessageDialog(frame, "File created");
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				JOptionPane.showMessageDialog(frame, "Error creating file","Error",JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("In save action listener");
		save();
	}

}
