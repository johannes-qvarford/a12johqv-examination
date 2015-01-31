package towers.objects;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;

public abstract class PhysicalEntity extends Entity {	
	public static final String coordNames[] = {"x","y","z"};
	
	protected int coords[] = {0,0,0};
	protected int width = 1,length = 1,height = 1;
	
	Image m_lastTileUsed = null;
		
	public PhysicalEntity(String id,String owner)
	{
		super(id,owner);
	}
	
	public PhysicalEntity( PhysicalEntity incoming )
	{
		super(incoming);
		this.coords[0] = incoming.coords[0];
		this.coords[1] = incoming.coords[1];
		this.coords[2] = incoming.coords[2];
		this.width = incoming.width;
		this.length = incoming.length;
		this.height = incoming.height;
	}
		
	public int getx()
	{
		return coords[0];
	}

	public void setx(int a_x)
	{
		coords[0] = a_x;
	}

	public void setx(String a_x)
	{
		coords[0] = Integer.parseInt(a_x);
	}
	

	public int gety()
	{
		return coords[1];
	}

	public void sety(int a_y)
	{
		coords[1] = a_y;
	}

	public void sety(String a_y)
	{
		coords[1] = Integer.parseInt(a_y);
	}
	
	public int getz()
	{
		return coords[2];
	}

	public void setz(int a_z)
	{
		coords[2] = a_z;
	}

	public void setz(String a_z)
	{
		coords[2] = Integer.parseInt(a_z);
	}
	
	public int []get_Coords() {		// Added the "_" in between get and Coords, so that the method "listOfFeatures" does not get this a =s a feature
		return coords;
	}
	
	public void set_Coords(int []c) {
		coords = c;
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

	public abstract Object clone();
	
	public boolean collision(PhysicalEntity e) {
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
	public boolean collision(PhysicalEntity e,int coords[]) {
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
	
	public double collisionSoft(PhysicalEntity e) {
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

	public double collisionSoft(PhysicalEntity e,int coords[]) {
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

	public void draw(Graphics2D g) throws IOException {
		// TODO Auto-generated method stub
		
	}	

}
