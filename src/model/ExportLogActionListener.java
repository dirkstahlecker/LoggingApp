package model;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class ExportLogActionListener implements ActionListener {

	private final JTextArea log;
	private File file;
	private final JFrame frame;
	private final String extension;

	public ExportLogActionListener(JFrame frame, JTextArea log, String format) {
		this.log = log;
		this.file = null;
		this.frame = frame;
		this.extension = format;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String logText = log.getText();

		System.setProperty("apple.awt.fileDialogForDirectories", "true");
		FileDialog fileDialog = new FileDialog(frame, "Choose an output file", FileDialog.SAVE);
		fileDialog.setFile('*' + extension);
		
		if (file != null) {
			//fileDialog.setFile(this.file.getPath());
		}
		fileDialog.setVisible(true);
		
		if (fileDialog.getFile() != null) { //user clicked okay and not cancel
			String path = fileDialog.getDirectory() + fileDialog.getFile();
			if (!path.endsWith(extension)) { //TODO: this seems useless, or at least not called ever
				if (path.matches("\\..+$")) {
					JOptionPane.showMessageDialog(frame, "Error creating file: Incorrect file extension","Error",JOptionPane.ERROR_MESSAGE);
					actionPerformed(null); //recurse to start over
				}
				path += extension;
			}
			file = new File(path);
			System.out.println("path: " + path);
			
			//Do format-specific formatting
			if (extension.equals(".xls") || extension.equals(".xlsx")) {
				logText = logText.replaceAll(":", "\t"); //tabs create new column in excel
			}
			
			try {
				PrintWriter fileWriter = new PrintWriter(file,"UTF-8");

				fileWriter.write(logText);
				fileWriter.close();

				JOptionPane.showMessageDialog(frame, "File created");
			} 
			catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(frame, "Error creating file: File not found","Error",JOptionPane.ERROR_MESSAGE);
			} 
			catch (UnsupportedEncodingException e1) {
				JOptionPane.showMessageDialog(frame, "Error creating file: Unsupported encoding","Error",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
