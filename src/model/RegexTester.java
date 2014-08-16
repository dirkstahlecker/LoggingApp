package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RegexTester {
	
	public static void main(String[] args) throws IOException {
		//String regex = "\\.*?file:/\\S+?\\.\\S+?     \n\\d+\n(\\d\\s+:\\s*\\d:\\s*\\.+\n?)*";
		String filepath = "/users/dirk/desktop/test.txt";
		
		String[] regex = {"\\s*file:/\\S+?\\.\\S+?", "\\d+", "\\d+\\s*:\\s*\\d+\\s*:\\s*.*"};
		//TODO: make second regex match possible floats, rather than just ints
		
		FileReader fileReader = new FileReader(filepath);
		BufferedReader reader = new BufferedReader(fileReader);
		
		String line = "";
		int count = 0;
		boolean matches = true;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			String r = "";
			if (count == 0)
				r = regex[0];
			else if (count == 1)
				r = regex[1];
			else
				r = regex[2];
			
			if (!line.matches(r)) {
				System.out.println("\""+line+"\""+" Does not match");
				matches = false;
				break;
			}
			count++;
		}
		System.out.println("Matches? " + matches);
		reader.close();
	}
}
