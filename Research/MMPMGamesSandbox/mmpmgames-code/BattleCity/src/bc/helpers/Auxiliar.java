package bc.helpers;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class Auxiliar {
	public static void drawCentered(String text,int x,int y,Graphics2D g) {
		FontMetrics metrics = g.getFontMetrics();
		int strWidth = metrics.stringWidth(text);
		g.drawString(text, x-strWidth/2, y);
	}
}
