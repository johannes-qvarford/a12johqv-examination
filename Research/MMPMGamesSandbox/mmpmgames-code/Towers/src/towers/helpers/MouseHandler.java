package towers.helpers;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import towers.TowersApp;

public class MouseHandler implements MouseListener, MouseMotionListener {

	protected TowersApp l_game;
	
	
	public MouseHandler(TowersApp m_game) {
		l_game = m_game;
	}

	public void mouseClicked(MouseEvent e) {
		// Do nothing
	}

	public void mouseEntered(MouseEvent e) {
		//Do nothing
	}

	public void mouseExited(MouseEvent e) {
		//Do nothing
	}

	public void mousePressed(MouseEvent e) {
		l_game.mouseClick(e.getX(),e.getY());
	}

	public void mouseReleased(MouseEvent e) {
		// do nothing
	}

	public void mouseDragged(MouseEvent e) {
		// do nothing
	}

	public void mouseMoved(MouseEvent e) {
		// do nothing
	}


}
