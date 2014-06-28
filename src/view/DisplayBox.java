package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * Used to display the past logs
 * 
 * @author Dirk
 *
 */
public class DisplayBox extends JPanel {
	private static final long serialVersionUID = 1L; //required
	
	public DisplayBox() {
		
	}
	
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        //fillWindow(g2);
        paintText(g2);

    }
    
    private void fillWindow(Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.fillRect(0,  0,  getWidth(), getHeight());
    }
    
    private void paintText(Graphics2D g) {
    	g.setColor(Color.BLACK);
    	g.drawString("Testing", 100, 100);
    }

}
