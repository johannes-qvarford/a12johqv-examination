package s2.game;

import gatech.mmpm.Map;
import gatech.mmpm.TwoDMap;

import java.awt.Graphics2D;

import org.jdom.Element;

import s2.entities.S2PhysicalEntity;
import s2.entities.map.WOGrass;
import s2.entities.map.WOTree;
import s2.entities.map.WOWater;

public class S2Map {
	protected S2MapLayer layers[];

	protected static final int NO_OF_LAYERS = 2;

	protected int x_offset = 0, y_offset = 0;

	protected int width, height;

	public S2Map(Element mapEntity) {
		int x = Integer.parseInt(mapEntity.getChild("width").getValue());
		int y = Integer.parseInt(mapEntity.getChild("height").getValue());
		layers = new S2MapLayer[NO_OF_LAYERS];
		// grass layer
		layers[0] = new S2MapLayer(x, y);
		layers[0].fillGrass();
		// entities on grass layer
		layers[1] = new S2MapLayer(x, y);
		layers[1].parse(mapEntity.getChild("background"));

		width = x;
		height = y;
	}

	public void set_x_offset(int i_x_offset) {
		x_offset = i_x_offset;
		for (S2MapLayer s : layers) {
			s.set_x_offset(x_offset);
		}
	}

	public void set_y_offset(int i_y_offset) {
		y_offset = i_y_offset;
		for (S2MapLayer s : layers) {
			s.set_y_offset(y_offset);
		}
	}

	public void draw(Graphics2D g) {
		for (int i = 0; i < NO_OF_LAYERS; i++) {
			layers[i].draw(g);
		}
	}

	public void cycle(S2 s2) {
		// iterate thru the layers applying the actions...
		// Ignore layer 0
		layers[1].cycle(s2);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean anyLevelCollision(S2PhysicalEntity i_pe) {
		// Only check for level 1 as level 0 is grass
		return layers[1].collidesWith(i_pe);
	}

	public Map toD2Map(gatech.mmpm.IDomain idomain) {
		int coords[] = new int[2];

		TwoDMap ret = new TwoDMap(width, height,
				1,
				1);

		for (S2MapLayer l : layers) {
			for (int y = 0; y < l.map_height; y++) {
				for (int x = 0; x < l.map_width; x++) {
					coords[0] = x;
					coords[1] = y;
					if (l.map[x][y] != null) {
						if (l.map[x][y] instanceof WOGrass)
							ret.setCellLocation('.', coords, idomain);
						if (l.map[x][y] instanceof WOTree)
							ret.setCellLocation('t', coords, idomain);
						if (l.map[x][y] instanceof WOWater)
							ret.setCellLocation('w', coords, idomain);
					}
				} // for
			} // for
		}

		return ret;
	}

	public S2PhysicalEntity getEntity(int x, int y) {
		for (int l = layers.length - 1; l >= 0; l--) {
			if (layers[l].map[x][y] != null)
				return layers[l].map[x][y];
		}
		return null;
	}


}
