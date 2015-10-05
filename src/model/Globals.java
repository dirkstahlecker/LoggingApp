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
	public static void log(String msg, boolean err) {
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
	public static void log(String msg) {
		log(msg, false);
	}
	
	public static void logConstants() {
		log(Constants.DEBUG ? "Debug: true" : "Debug: false");
		//log(Constants.rewindGain)
		//TODO: finish this
	}
}
