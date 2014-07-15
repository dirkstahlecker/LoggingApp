package model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Testing {

	public static void main(String[] args) {
		String logText = "0: hello\n0: testing\n0: one";
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher matcher = pattern.matcher(logText);
		// Check all occurrences
		int lastNum = -1;
		String outStr = "";
		while (matcher.find()) {
			int curNum = Integer.parseInt(matcher.group());
			if (curNum == lastNum) { //replace number with a space
				outStr = logText.substring(0, matcher.start());
			}
			else {
				lastNum = curNum;
			}
		}
	}
}
