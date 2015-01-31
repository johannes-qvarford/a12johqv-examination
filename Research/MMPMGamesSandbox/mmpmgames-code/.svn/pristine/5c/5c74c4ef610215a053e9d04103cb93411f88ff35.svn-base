package s2.actionControllers;

import java.awt.Graphics2D;
import java.util.Set;

import s2.entities.Sprite;
import s2.entities.SpriteStore;
import s2.entities.WUnit;
import s2.game.HUD;


public abstract class ActionController {
	public static final String coordNames[] = { "x", "y", "z" };

	protected int x = 0, y = 0, z = 0;

	// DEFAULT VALUES . THEY WOULD NEVER CHANGE!
	protected final int width = 4, length = 4, height = 1;

	protected String spriteName = "";

	// CELL_WIDTH
	public final static int CELL_WIDTH = 8;

	// CELL_HEIGHT
	public final static int CELL_HEIGHT = 8;

	protected int hudBoundary_y;

	protected String actionName = null;

	private Sprite sprite = null;
	//Leeway for mouse click
	public static int MOUSE_CLICK_LEEWAY = 5;
	
	protected Set<WUnit> units = null;

	public ActionController() {
	}

	public ActionController(Set<WUnit> i_unit, String i_spriteName, int i_hudBoundary_y, String i_actionName) {
		units = i_unit;
		spriteName = i_spriteName;
		hudBoundary_y = i_hudBoundary_y;
		actionName = i_actionName;
		sprite = SpriteStore.get().getSprite(spriteName);
	}
	
	public void setUnits(Set<WUnit> a_unit) {
		System.out.println("resetting");
		units = a_unit;
	}
	
	public Set<WUnit> getUnits() {
		return units;
	}

	public int getx() {
		return x;
	}

	public void setx(int a_x) {
		x = a_x;
	}

	public int gety() {
		return y;
	}

	public void sety(int a_y) {
		y = a_y;
	}

	/**
	 * Method determines if the Action has been clicked or not. If it's clicked,
	 * it also displays the action params for the same on the HUD
	 * 
	 * @param mouse_x
	 *            X coordinate of mouse click
	 * @param mouse_y
	 *            Y coordinate of mouse click
	 * @return true if mouse clicked, else false
	 */
	public boolean isEntityAt(float mouse_x, float mouse_y, HUD theHUD) {
		//System.out.print("(" + x + "," + y + ") ");
		if ( x*CELL_WIDTH-MOUSE_CLICK_LEEWAY <= mouse_x && 
				((x+width)*CELL_WIDTH+MOUSE_CLICK_LEEWAY) >= mouse_x &&
				y*CELL_HEIGHT-MOUSE_CLICK_LEEWAY <= mouse_y &&
				((y+length)*CELL_HEIGHT+MOUSE_CLICK_LEEWAY) >= mouse_y) {
			enableActionParameters(theHUD); 
			return true;
		}
		return false;
	}

	public void draw(Graphics2D g, int x_offset, int y_offset) {
		if (!spriteName.equals(""))
			sprite.draw(g, x * CELL_WIDTH - x_offset, y * CELL_HEIGHT - y_offset, 32, 32);
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public abstract boolean paramsSatisfied();

	public abstract void performAction();

	public abstract boolean clickForParams(int map_x, int map_y, int current_screen_x,
			int current_screen_y, HUD theHUD);

	protected abstract void enableActionParameters(HUD theHUD);

	public abstract void reset();

	public abstract boolean scrollLock();

	public abstract Object clone();
}
