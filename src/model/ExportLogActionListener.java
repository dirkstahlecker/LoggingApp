package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;


public class ExportLogActionListener implements ActionListener {
	
	private final JTextArea log;
	private final File file;
	private final JFrame frame;
	
	public ExportLogActionListener(JFrame frame, JTextArea log) {
		this.log = log;
		this.file = new File("/users/dirk/desktop/log.xls"); //TODO: get this from somewhere
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String logText = log.getText();
		String path = file.getPath();
		if (path.matches(".+?\\.txt$")) {
			
		}
		else if (path.endsWith(".xls") || path.endsWith(".xlsx")) {
			logText = logText.replaceAll(":", "\t"); //tabs create new column in excel
		}
		/*
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher matcher = pattern.matcher(logText);
		// Check all occurrences
		int lastNum = -1;
		String outStr = "";
		while (matcher.find()) {
			int curNum = Integer.parseInt(matcher.group());
			if (curNum == lastNum) { //replace number with a space
				outStr = logText.substring(0, matcher.start());
				outStr += String.valueOf(lastNum);
			}
			else {
				lastNum = curNum;
			}
		}
		*/

		try {
			PrintWriter fileWriter = new PrintWriter(file,"UTF-8");

			fileWriter.write(logText);
			fileWriter.close();
			
			JOptionPane.showMessageDialog(frame, "File created");
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(frame, "Error creating file: File not found");
		} catch (UnsupportedEncodingException e1) {
			JOptionPane.showMessageDialog(frame, "Error creating file: Unsupported encoding");
		}
		
	}

}
