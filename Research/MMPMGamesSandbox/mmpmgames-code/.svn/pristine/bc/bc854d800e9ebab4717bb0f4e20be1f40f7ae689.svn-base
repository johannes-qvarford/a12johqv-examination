/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Santi Ontanon
****************************************************************************/
package s2.entities.map;

import gatech.mmpm.Entity;

public class WOTree extends WOMapEntity {


	public WOTree()
	{
		this.spriteName = "graphics/tree.png";
	}
	public WOTree( WOTree incoming )
	{
		super(incoming);
		this.spriteName = "graphics/tree.png";
	}
	public Object clone() {
		WOTree e = new WOTree(this);
		return e;
	}


	public static boolean isActive() 
	{
		return false;
	}
	
	public Entity toD2Entity() {
		s2.mmpm.entities.WOTree ret;
		ret = new s2.mmpm.entities.WOTree(""+entityID, owner);
		ret.setx(x);
		ret.sety(y);
		return ret;
	}

}