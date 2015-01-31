package s2.entities;

import java.awt.Color;

public class PlayerColorRelationship {
	static Color[] colors = {new Color(255,10,10,100), 
		new Color(255,10,255,75)};
	static int playerCount = 0;
	
	public static void reset() {
		playerCount = 0;
	}

}
