package view;

import javax.swing.JSlider;

public class SetupUtils {

	public static void setupSlider(JSlider slider, int initialTime, int finalTime, int startTime, int majorSpacing, int minorSpacing) {
        int tickSpacing = (finalTime - initialTime) / majorSpacing;
        slider.setMajorTickSpacing(tickSpacing);
        slider.setMinorTickSpacing(tickSpacing / minorSpacing);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
	}
}
