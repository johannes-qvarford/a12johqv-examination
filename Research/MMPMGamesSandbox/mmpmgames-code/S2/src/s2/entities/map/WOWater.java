/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Santi Ontanon
****************************************************************************/
package s2.entities.map;

import gatech.mmpm.Entity;

public class WOWater extends WOMapEntity {


	public WOWater()
	{
		this.spriteName = "graphics/water.png";
	}
	public WOWater( WOWater incoming )
	{
		super(incoming);
		this.spriteName = "graphics/water.png";
	}
	public Object clone() {
		WOWater e = new WOWater(this);
		return e;
	}


	public static boolean isActive() 
	{
		return false;
	}
	
	public Entity toD2Entity() {
		s2.mmpm.entities.WOWater ret;
		ret = new s2.mmpm.entities.WOWater(""+entityID, owner);
		ret.setx(x);
		ret.sety(y);
		return ret;
	}

}