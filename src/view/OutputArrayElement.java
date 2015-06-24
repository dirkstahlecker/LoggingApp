package view;

import java.awt.Color;

import model.Constants;

/**
 * Object to hold line elements
 * Create one per line and hold them in an array
 * Invariant: is highlighted is true, highlightColor is not null
 * @author Dirk
 *
 */
public class OutputArrayElement {

	private String line;
	private boolean highlighted;
	private Color highlightColor;
	
	public OutputArrayElement(String line, boolean highlighted, Color color) {
		this.line = line;
		this.highlighted = highlighted;
		this.highlightColor = color;
		checkRep();
	}
	public OutputArrayElement(String line) {
		this.line = line;
		this.highlighted = false;
		this.highlightColor = Constants.WHITE;
		checkRep();
	}
	
	private void checkRep() {
		if (highlighted)
			assert highlightColor != null;
	}
	
	//Getters
	public String getLine() {
		return new String(line);
	}
	public boolean isHighlighted() {
		return highlighted;
	}
	public Color getHighhlightColor() {
		return highlightColor;
	}
}
