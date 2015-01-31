/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Santi Ontanon
 ****************************************************************************/
package s2.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import s2.actionControllers.ActionController;
import s2.game.S2;


public abstract class WUnit extends S2PhysicalEntity implements Comparable<WUnit>{
	public static final int ACTION_NULL = 0;
	public static final int ACTION_MOVE = 1;
	public static final int ACTION_ATTACK = 2;
	public static final int ACTION_HARVEST = 3;
	public static final int ACTION_STAND_GROUND = 5;
	public static final int ACTION_REPAIR = 4;
	public static final int ACTION_TRAIN = 6;
	public static final int ACTION_BUILD = 7;

	protected int range;
	protected int attack;

	protected int max_hitpoints;
	protected int current_hitpoints;
	protected List<Integer> status;
	protected ActionController lastAction = null;
	protected int cost_gold;
	protected int cost_wood;

	protected int target_x = -1;
	protected int target_y = -1;

	protected int cycle_created = 0;
	protected int cycle_last_attacked = -1;
	protected WUnit creator = null;

	public List <String> actionList = new ArrayList<String>();

	public List<String> getActionList() {
		return actionList;
	}
	public WUnit()
	{
		status = new ArrayList <Integer>();
	}
	public WUnit( WUnit incoming )
	{
		super(incoming);
		this.max_hitpoints = incoming.max_hitpoints;
		this.current_hitpoints = incoming.current_hitpoints;
		this.status = incoming.status;
		this.cost_gold = incoming.cost_gold;
		this.cost_wood = incoming.cost_wood;
	}

	public static boolean isActive() 
	{
		return true;
	}



	public int getMax_hitpoints() {
		return max_hitpoints;
	}
	public void setMax_hitpoints(int max_hitpoints) {
		this.max_hitpoints = max_hitpoints;
	}
	public int getCurrent_hitpoints() {
		return current_hitpoints;
	}
	public void setCurrent_hitpoints(int current_hitpoints) {
		if (this.current_hitpoints > current_hitpoints)
			hit_timer =10;
		this.current_hitpoints = current_hitpoints;
	}
	public List<Integer> getStatus() {
		return status;
	}
	public void setStatus(List<Integer> status) {
		this.status = status;
	}
	public int getCost_gold() {
		return cost_gold;
	}
	public void setCost_gold(int cost_gold) {
		this.cost_gold = cost_gold;
	}
	public int getCost_wood() {
		return cost_wood;
	}
	public void setCost_wood(int cost_wood) {
		this.cost_wood = cost_wood;
	}
	/**
	 * gets the current player from the game state.
	 * 
	 * @param game
	 *            the current game.
	 * @return the player that owns this unit.
	 */
	protected WPlayer getPlayer(S2 game) {
		WPlayer player = null;
		for (S2Entity entity : game.getPlayers()) {
			if (entity instanceof WPlayer) {
				if (((WPlayer) entity).owner.equals(owner)) {
					player = (WPlayer) entity;
					break;
				}
			}
		}
		return player;
	}

	public void draw(Graphics2D g, int x_offset, int y_offset) {
		super.draw(g, x_offset, y_offset);
		//check to see if the status has ATTACK or not
		if (target_x >=0 && target_y >=0) {
			g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND, 1f, new float[] { 5f }, 1.0f));

			g.setColor(Color.RED);
			g.drawLine((x+getWidth()/2) * CELL_SIZE - x_offset, 
					(y+getLength()/2)* CELL_SIZE - y_offset, 
					target_x* CELL_SIZE - x_offset, 
					target_y* CELL_SIZE - y_offset);
			g.setStroke(new BasicStroke());
		}
		//draw COLOR
//		this.getOwner()
	}
	
	public int compareTo(WUnit u) {
		return entityID - u.entityID;
	}


	public void setCreator(WUnit c) {
		creator = c;
	}

	public void setCreatedCycle(int cycle) {
		cycle_created = cycle;
	}

	public void setLastAttackCycle(int cycle) {
		cycle_last_attacked = cycle;
	}

	public void setColor(Color incomingColor) {
		playerColor = incomingColor;
	}
}