/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Santi Ontanon
****************************************************************************/
package s2.entities.map;

import gatech.mmpm.Entity;

public class WOGrass extends WOMapEntity {


	public WOGrass()
	{
		this.spriteName = "graphics/grass.png";
	}
	public WOGrass( WOGrass incoming )
	{
		super(incoming);
		this.spriteName = "graphics/grass.png";
	}
	public Object clone() {
		WOGrass e = new WOGrass(this);
		return e;
	}


	public static boolean isActive() 
	{
		return false;
	}
	
	public Entity toD2Entity() {
		s2.mmpm.entities.WOGrass ret;
		ret = new s2.mmpm.entities.WOGrass(""+entityID, owner);
		ret.setx(x);
		ret.sety(y);
		return ret;
	}

}