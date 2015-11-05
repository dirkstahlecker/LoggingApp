package view;

import javax.swing.JLabel;
import javax.swing.JSlider;

import model.Constants;

public class SetupUtils {

	public static void setupSlider(JSlider slider, int initialTime, int finalTime, int startTime, int majorSpacing, int minorSpacing) {
        int tickSpacing = (finalTime - initialTime) / majorSpacing;
        slider.setMajorTickSpacing(tickSpacing);
        slider.setMinorTickSpacing(tickSpacing / minorSpacing);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
	}
	
	/**
	 * Truncates time to the correct length to display on the gui
	 * Adds spaces to the front if it's shorter
	 * @param timeIn double to truncate
	 * @return string of the correct length
	 */
	private synchronized static String truncateTime(double timeIn) {
		String outTime = "";
		String time = String.valueOf(timeIn);
		if (time.length() < Constants.displayTimeDigits) {
			int zerosToAdd = Constants.displayTimeDigits - time.length();
			for (int i = 0; i < zerosToAdd; i++) {
				time = ' ' + time;
			}
		}
		for (int i = 0; i < Constants.displayTimeDigits; i++) {
			outTime += time.charAt(i);
		}
		return outTime;
	}

	public synchronized static void setTimeStampText(JLabel timeStamp, double curNum, double totalNum) {
		timeStamp.setText("     " + truncateTime(curNum) + "/" + truncateTime(totalNum));
	}
}
