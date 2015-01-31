package bc.helpers;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class SpriteManager {
	static HashMap<String,Image> m_sprites = new HashMap<String,Image>();
	static SpriteManager m_sm = new SpriteManager();
	
	public static Image get(String id) throws IOException {
//		String path = "/" + id;
		if (m_sprites.get(id) != null) {
			return m_sprites.get(id);
		}
		
//		System.out.println("Image not in hash, loading...:" + id);
		
		BufferedImage sourceImage = null;
//		URL url = SpriteManager.class.getClassLoader().getResource(path);			
//		if (url == null) System.err.println("Can't find ref: " + path);
//		sourceImage = ImageIO.read(url);
		
//		File f = new File("graphics/" + id + ".png");
		try {
			// When BattleCity is a JAR
			InputStream f = m_sm.getClass().getResourceAsStream("/graphics/" + id + ".png");
			sourceImage = ImageIO.read(f);
		} catch (Exception e) {
			// When it's not:
			File f = new File("graphics/" + id + ".png");
			sourceImage = ImageIO.read(f);
		}


		
		// create an accelerated image of the right size to store our sprite in
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Image image = gc.createCompatibleImage(sourceImage.getWidth(),sourceImage.getHeight(),Transparency.BITMASK);
		
		image.getGraphics().drawImage(sourceImage,0,0,null);
		m_sprites.put(id,image);
		
		return image;
	}
	
}
