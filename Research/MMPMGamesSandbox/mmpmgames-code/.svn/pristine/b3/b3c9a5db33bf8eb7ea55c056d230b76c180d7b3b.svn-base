package bc.helpers;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInputHandler extends KeyAdapter {
	public boolean m_keyboardStatus[];

	public KeyInputHandler() {
		int i;
		m_keyboardStatus = new boolean[KeyEvent.KEY_LAST];
		
		for(i=0;i<KeyEvent.KEY_LAST;i++) m_keyboardStatus[i]=false;
	}
	
	
	/**
	 * Notification from AWT that a key has been pressed. Note that
	 * a key being pressed is equal to being pushed down but *NOT*
	 * released. Thats where keyTyped() comes in.
	 *
	 * @param e The details of the key that was pressed 
	 */
	public void keyPressed(KeyEvent e) {
		m_keyboardStatus[e.getKeyCode()]=true;		
	} 

	/**
	 * Notification from AWT that a key has been released.
	 *
	 * @param e The details of the key that was released 
	 */
	public void keyReleased(KeyEvent e) {
		m_keyboardStatus[e.getKeyCode()]=false;
	}

	/**
	 * Notification from AWT that a key has been typed. Note that
	 * typing a key means to both press and then release it.
	 *
	 * @param e The details of the key that was typed. 
	 */
	public void keyTyped(KeyEvent e) {
		// ...
	}
}



