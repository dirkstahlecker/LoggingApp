package model;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyPressedListener implements KeyListener {

	public KeyPressedListener() {
		System.out.println("in key listener constructor");
	}
	
	@Override
	public synchronized void keyTyped(KeyEvent e) {
	}

	@Override
	public synchronized void keyPressed(KeyEvent e) {
		System.out.print(e);
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			System.out.println("RIGHT");
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			System.out.println("LEFT");
		}
	}

	@Override
	public synchronized void keyReleased(KeyEvent e) {
	}

}
