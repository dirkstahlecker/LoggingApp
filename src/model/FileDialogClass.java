package model;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class FileDialogClass implements ActionListener {

	private final JFrame frame;

	public FileDialogClass(JFrame frame) {
		this.frame = frame;
	}

	private void open() {
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
				//fileWriter.write(out);
				fileWriter.close();
				JOptionPane.showMessageDialog(frame, "File created");
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				JOptionPane.showMessageDialog(frame, "Error creating file","Error",JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		open();
	}
}
