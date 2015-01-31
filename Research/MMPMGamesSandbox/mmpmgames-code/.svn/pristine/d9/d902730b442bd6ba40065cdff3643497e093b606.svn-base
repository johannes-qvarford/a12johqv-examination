package bc.objects;


import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import bc.Action;
import bc.BCMap;
import bc.BattleCity;
import bc.helpers.VirtualController;

public class BCPhysicalEntity extends BCEntity {
	public static final String coordNames[] = {"x","y","z"};
	
	protected int x = 0, y = 0, z = 0;
	protected int width = 1,length = 1,height = 1;
	
	Image m_lastTileUsed = null;
	
	boolean m_destroyed = false;
	
	List<BCPhysicalEntity> m_excludedForCollition = new LinkedList<BCPhysicalEntity>();
	
	public BCPhysicalEntity()
	{
	}
	
	public BCPhysicalEntity( BCPhysicalEntity incoming )
	{
		super(incoming);
		this.x = incoming.x;
		this.y = incoming.y;
		this.z = incoming.z;
		this.width = incoming.width;
		this.length = incoming.length;
		this.height = incoming.height;
                this.m_destroyed = incoming.m_destroyed;
                this.m_excludedForCollition.addAll(incoming.m_excludedForCollition);
	}
	
	public int getx()
	{
		return x;
	}

	public void setx(int a_x)
	{
		x = a_x;
	}

	public void setx(String a_x)
	{
		x = Integer.parseInt(a_x);
	}
	

	public int gety()
	{
		return y;
	}

	public void sety(int a_y)
	{
		y = a_y;
	}

	public void sety(String a_y)
	{
		y = Integer.parseInt(a_y);
	}
	
	public int getz()
	{
		return z;
	}

	public void setz(int a_z)
	{
		z = a_z;
	}

	public void setz(String a_z)
	{
		z = Integer.parseInt(a_z);
	}
	
	
	public int getwidth()
	{
		return width;
	}

	public void sezwidth(int a_width)
	{
		width = a_width;
	}

	public void setwidth(String a_width)
	{
		width = Integer.parseInt(a_width);
	}
	
	
	public int getlength()
	{
		return length;
	}

	public void setlength(int a_length)
	{
		length = a_length;
	}

	public void setlength(String a_length)
	{
		length = Integer.parseInt(a_length);
	}
	
	
	public int getheight()
	{
		return height;
	}

	public void setheight(int a_height)
	{
		height = a_height;
	}

	public void setheight(String a_height)
	{
		height = Integer.parseInt(a_height);
	}

	public Object clone() {
		BCPhysicalEntity e = new BCPhysicalEntity(this);
		return e;
	}
	
	public boolean collision(BCPhysicalEntity e) {
		if (e.m_excludedForCollition.contains(this) || m_excludedForCollition.contains(e)) return false;
		if (getx() >= e.getx()+e.getwidth() ||
			getx()+getwidth() <= e.getx() ||
			gety() >= e.gety()+e.getlength() ||
			gety()+getlength() <= e.gety() ||
			getz() >= e.getz()+e.getheight() ||
			getz()+getheight() <= e.getz()) {
			return false;
		}
		return true;
	}
	
	/*
	 *  This method is like the previous one, but assumes that "coords" are the coordinaes at which
	 *  the entity "e" is located right now.
	 */
	public boolean collision(BCPhysicalEntity e,int coords[]) {
		if (e.m_excludedForCollition.contains(this) || m_excludedForCollition.contains(e)) return false;
		if (getx() >= coords[0]+e.getwidth() ||
			getx()+getwidth() <= coords[0] ||
			gety() >= coords[1]+e.getlength() ||
			gety()+getlength() <= coords[1] ||
			getz() >= coords[2]+e.getheight() ||
			getz()+getheight() <= coords[2]) {
			return false;
		}
		return true;
	}
	
	public double collisionSoft(BCPhysicalEntity e) {
		if (e.m_excludedForCollition.contains(this) || m_excludedForCollition.contains(e)) return 0;
		if (getx() >= e.getx()+e.getwidth() ||
			getx()+getwidth() <= e.getx() ||
			gety() >= e.gety()+e.getlength() ||
			gety()+getlength() <= e.gety() ||
			getz() >= e.getz()+e.getheight() ||
			getz()+getheight() <= e.getz()) {
			return 0;
		}
		return 1;
	}

	public double collisionSoft(BCPhysicalEntity e,int coords[]) {
		if (e.m_excludedForCollition.contains(this) || m_excludedForCollition.contains(e)) return 0;
		if (getx() >= coords[0]+e.getwidth() ||
			getx()+getwidth() <= coords[0] ||
			gety() >= coords[1]+e.getlength() ||
			gety()+getlength() <= coords[1] ||
			getz() >= coords[2]+e.getheight() ||
			getz()+getheight() <= coords[2]) {
			return 0;
		}
		return 1;
	}

	public boolean cycle(List<VirtualController> l_vc, BCMap map, BattleCity game, List<Action> actions) throws IOException, ClassNotFoundException {
		if (m_destroyed) return false;
		return true;
	}

	public void draw(Graphics2D g) throws IOException {		
	}
	
	public Image get_LastTileUsed() {
		return m_lastTileUsed;
	}

	public boolean collision(Image im, int ax, int ay) {
		if (getx() >= ax+im.getWidth(null) ||
			getx()+getwidth() <= ax ||
			gety() >= ay+im.getHeight(null) ||
			gety()+getlength() <= ay) return false;
		return true;
	}


	public void excludeForCollision(BCPhysicalEntity o) {
		m_excludedForCollition.add(o);
	}
        
        public List<BCPhysicalEntity> getExcludedForCollision() {
            return m_excludedForCollition;
        }
        
        public void clearExcludedForCollision() {
            m_excludedForCollition.clear();
        }
	
	public void bulletHit() {
		m_destroyed = true;
	}


}
