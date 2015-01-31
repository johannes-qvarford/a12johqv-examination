/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Santi Ontanon
 ****************************************************************************/
package s2.entities;

import gatech.mmpm.Entity;

import java.awt.Color;


public class WPlayer extends S2Entity {

	private int gold;

	private int wood;
	
	private int inputType;
	
	private Color playerColor;

	public int getInputType() {
		return inputType;
	}

	public void setInputType(int inputType) {
		this.inputType = inputType;
	}

	public WPlayer() {
		//set the playerColor
		playerColor = PlayerColorRelationship.colors[PlayerColorRelationship.playerCount++];
	}

	public WPlayer(WPlayer incoming) {
		super(incoming);
		this.gold = incoming.gold;
		this.wood = incoming.wood;
		this.playerColor = incoming.playerColor;
	}

	public Object clone() {
		WPlayer e = new WPlayer(this);
		return e;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getWood() {
		return wood;
	}

	public void setWood(int wood) {
		this.wood = wood;
	}
	
	public boolean canAfford(Class<? extends WUnit> type) {
		
		return false;
	}

	public Entity toD2Entity() {
		s2.mmpm.entities.WPlayer ret;
		
		ret = new s2.mmpm.entities.WPlayer(""+entityID, owner);
		ret.setGold(gold);
		ret.setWood(wood);
		return ret;
	}

	public Color getColor() {
		return playerColor;
	}

}