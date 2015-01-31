package s2.helpers;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import s2.base.S2App;

public class MouseHandler implements MouseListener, MouseMotionListener {

	protected S2App l_game;

	protected boolean scrollFlag = true;

	public MouseHandler(int full_screen_x, int full_screen_y, S2App m_game) {
		l_game = m_game;
	}

	public void mouseClicked(MouseEvent e) {
		int current_screen_x = e.getX();
		int current_screen_y = e.getY();
		// //Check for right click
		if (e.getButton() == MouseEvent.BUTTON3) {
			// System.out.println("Right");
			l_game.snapToCenter(current_screen_x, current_screen_y);
		} else {
			// System.out.println("Left");
			l_game.mouseClick(current_screen_x, current_screen_y);
		}
	}

	public void mouseEntered(MouseEvent e) {
		// Do nothing?
	}

	public void mouseExited(MouseEvent e) {
		// Do nothing?
	}

	public void mousePressed(MouseEvent e) {
		int current_screen_x = e.getX();
		int current_screen_y = e.getY();
		// Check for right click
		if (e.getButton() != MouseEvent.BUTTON3) {
			// System.out.println("Left");
			l_game.mousePress(current_screen_x, current_screen_y);
		}

	}

	public void mouseReleased(MouseEvent e) {
		int current_screen_x = e.getX();
		int current_screen_y = e.getY();
		// Check for right click
		if (e.getButton() != MouseEvent.BUTTON3) {
			// System.out.println("Left");
			l_game.mouseRelease(current_screen_x, current_screen_y);
		}
	}

	public void mouseDragged(MouseEvent e) {
		int current_screen_x = e.getX();
		int current_screen_y = e.getY();
		// Check for right click
		if (e.getButton() != MouseEvent.BUTTON3) {
			// System.out.println("Left");
			l_game.mouseDrag(current_screen_x, current_screen_y);
		}
	}

	public void mouseMoved(MouseEvent e) {
		// Do nothing?
	}

	public void disableScrolling() {
		scrollFlag = false;
	}

	public void enableScrolling() {
		scrollFlag = true;
	}

}
