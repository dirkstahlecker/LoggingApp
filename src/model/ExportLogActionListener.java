package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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
		String outStr = log.getText();
		String path = file.getPath();
		if (path.matches(".+?\\.txt$")) {
			
		}
		else if (path.matches(".+?\\.xls$") || path.matches(".+?\\.xlsx$")) {
			outStr = outStr.replaceAll(":", "\t"); //tabs create new column in excel
		}
		
		try {
			PrintWriter fileWriter = new PrintWriter(file,"UTF-8");

			fileWriter.write(outStr);
			fileWriter.close();
			
			JOptionPane.showMessageDialog(frame, "File created");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
	}
}
