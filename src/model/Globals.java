package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

public class Globals {
	
	private static String filename = "/log.txt";
	
	/**
	 * Log a message to both the console (if debug is enabled) and to the log file
	 * @param msg message to log
	 * @param err log to System.err if true, System.out if false
	 */
	public synchronized static void log(String msg, boolean err) {
		if (err) {
			System.err.println(msg);
		}
		else if (Constants.DEBUG) {
			System.out.println(msg);
		}
		
		String currentDir;
		File directory = new File ("");
		try {
		    currentDir = directory.getAbsolutePath();
		}catch(Exception e) {
		    System.err.println("Can't get current directory");
		    return;
		}
		/*
		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(currentDir + filename), "utf-8"));
		    writer.write(msg + '\n');
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
		 */
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(currentDir + filename, true)))) {
		    if (err) {
		    	out.println("~~~" + msg); //something to indicate an error ocurred 
		    }
		    else {
		    	out.println(msg);
		    }
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
	}
	
	/**
	 * Overload to default err to false
	 * @param msg message to print
	 */
	public synchronized static void log(String msg) {
		log(msg, false);
	}
	
	public synchronized static void logConstants() {
		log(Constants.DEBUG ? "Debug: true" : "Debug: false");
		//log(Constants.rewindGain)
		//TODO: finish this
	}
	
	/**
	 * Return the text to show on the help menu
	 */
	public synchronized static String getHelpText() {
		return "This app helps radio logging by automatically timestamping comments and offering other features.\n"
				+ "Type in the comment box, then click \"Enter Text\" or press enter to submit text to the log\n"
				+ "Clicking on a timestamp in the log will jump to that point in the audio file.\n"
				+ "Change the playback rate of the audio in the \"Options\" menu.\n"
				+ "Click \"Open Audio\" to import an audio file\n"
				+ "Clicking \"Fast Forward\" or \"Rewind\" moves the audio file forward or backward an amount set by the gain.\n"
				+ "You can change the gain in its respective menu.\n"
				+ "Saving a project allows you to open it again for editing.\n"
				+ "Exporting a project saves the log to a file format of your choosing.\n"
				//+ "Clicking \"Highlight\" will highlights whatever text you enter. There is no way to highlight after entering.\n"
				+ "\nReminder: There is no autosave feature. Save frequently, because if the app crashes it can't recover your log.\n"
				+ "Java 7 or higher is required for the proper function of this application.";
	}
	
	/**
	 * Return the text to show on the known bugs menu
	 */
	public synchronized static String getKnownBugsText() {
		return "Known Bugs:\n"
				+ "After saving, you cannot open a file without restarting the app.\n"
				+ "Length of audio file occasionally shows up as \"NaN\" for some unknown reason. No known fix.\n"
				+ "Occasionally, changing the rewind and fastforward gains doesn't work. No known fix.\n"
				+ "Occasional crashes. Restart the app and it should be fine.\n"
				+ "Many other bugs likely exist. Feel free to email stahdirk@mit.edu if you experience problems.";
	}
}
