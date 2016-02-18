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
import javax.swing.JTextPane;

public class ExportLogActionListener implements ActionListener {

	private final JTextPane log;
	private File file;
	private final JFrame frame;
	private final String extension;

	public ExportLogActionListener(JFrame frame, JTextPane log, String format) {
		this.log = log;
		this.file = null;
		this.frame = frame;
		this.extension = format;
	}

	@Override
	public synchronized void actionPerformed(ActionEvent e) {
		Globals.log("Exporting file with extension " + extension);
		String logText = log.getText();
		//remove html and tabs, replace with single space after colon
		//logText = logText.replaceAll("\\n|\\s","");
		logText = logText.replaceAll("\\n","");
		logText = logText.replaceAll("\\s\\s+","");
		logText = logText.replaceAll(":",": ");
		logText = logText.replaceAll("<br>","\n");
		logText = logText.replaceAll("\\<[^>]*>","");
		logText = logText.replaceAll("&#160;","");
		logText = logText.replaceAll("&gt;",">");
		logText = logText.trim();
		Globals.log("File contents:\n");
		Globals.log(logText);

		System.setProperty("apple.awt.fileDialogForDirectories", "true");
		FileDialog fileDialog = new FileDialog(frame, "Choose an output file", FileDialog.SAVE);
		fileDialog.setFile('*' + extension);
		
		if (file != null) {
			//fileDialog.setFile(this.file.getPath());
		} //TODO: something here?
		fileDialog.setVisible(true);
		
		if (fileDialog.getFile() != null) { //user clicked okay and not cancel
			String path = fileDialog.getDirectory() + fileDialog.getFile();
			if (!path.endsWith(extension)) {
				if (path.matches("\\..+$")) {
					JOptionPane.showMessageDialog(frame, "Error creating file: Incorrect file extension","Error",JOptionPane.ERROR_MESSAGE);
					actionPerformed(null); //recurse to start over
				}
				path += extension;
			}
			file = new File(path);
			Globals.log("path: " + path);
			
			//Do format-specific formatting
			if (extension.equals(".xls") || extension.equals(".xlsx")) {
				logText = logText.replaceAll(":", "\t"); //tabs create new column in excel
			}
			
			try {
				PrintWriter fileWriter = new PrintWriter(file,"UTF-8");

				fileWriter.write(logText);
				fileWriter.close();

				JOptionPane.showMessageDialog(frame, "File successfully exported");
				Globals.log("File successfully exported");
			} 
			catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(frame, "Error creating file: File not found","Error",JOptionPane.ERROR_MESSAGE);
				Globals.log("Export failed with file not found error");
			} 
			catch (UnsupportedEncodingException e1) {
				JOptionPane.showMessageDialog(frame, "Error creating file: Unsupported encoding","Error",JOptionPane.ERROR_MESSAGE);
				Globals.log("Export failed with unsupported encoding error");
			}
		}
	}
}
