package s2.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;

import s2.actionControllers.ActionController;
import s2.entities.PlayerColorRelationship;
import s2.entities.S2Entity;
import s2.entities.S2PhysicalEntity;
import s2.entities.WPlayer;
import s2.entities.WUnit;
import s2.entities.buildings.WGoldMine;
import s2.entities.map.WOGrass;
import s2.entities.map.WOMapEntity;
import s2.helpers.Pair;

public class S2 {
	/** the current map. */
	private S2Map m_map;

	/** the entities that are not map parts or metadata. Usually units. */
	private List<WUnit> units;

	/** units to add at the end of a cycle. */
	private List<WUnit> newUnits;

	/** entities that are not displayed, like the player. */
	private List<WPlayer> players;

	/** the offset of the view pane. */
	private int x_offset = 0, y_offset = 0;

	/** what game cycle is currently being executed. */
	private int m_cycle;

	/** the next entityID to be assigned. */
	private int nextID = 0;

	/** warning message for the user */
	private String message = "";

	/**
	 * Default constructor.
	 * 
	 * @param game_doc
	 *            game definition.
	 * @throws Exception
	 */
	public S2(Document game_doc) throws Exception {
		units = new LinkedList<WUnit>();
		newUnits = new LinkedList<WUnit>();
		players = new LinkedList<WPlayer>();
		Element root = game_doc.getRootElement();
		// parse XML to create S2 Map object
		List<Element> entities = root.getChildren();
		
		PlayerColorRelationship.reset();
		
		for (Element entity : entities) {
			String entity_type = entity.getChild("type").getValue();
			// MAP
			if (entity_type.equals("map")) {
				m_map = new S2Map(entity);
			} else {
				// NON-MAP NON-PHYSICAL ENTITY
				if (entity_type.equals("WPlayer")) {

					Class<? extends S2Entity> entityClass = (Class<? extends S2Entity>) Class
							.forName("s2.entities." + entity_type);
					WPlayer gsEntity = (WPlayer) entityClass.newInstance();

					setFeaturesForEntity(entity, entityClass, gsEntity);

					setEntityID(entity, entityClass, gsEntity);
					players.add(gsEntity);
				} else {
					units.add(getEntityFromElement(entity));
				}
			}
		} // end for

		m_cycle = 0;
	}

	private WUnit getEntityFromElement(Element entity) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
		String entityName = entity.getChild("type").getValue();

		Class<? extends WUnit> entityClass;

		if (entityName.equals("WFarm")) {
			entityClass = (Class<? extends WUnit>) Class.forName("s2.entities.buildings."
					+ entityName);
		} else if (entityName.equals("WFortress")) {
			entityClass = (Class<? extends WUnit>) Class.forName("s2.entities.buildings."
					+ entityName);
		} else if (entityName.equals("WGoldMine")) {
			entityClass = (Class<? extends WUnit>) Class.forName("s2.entities.buildings."
					+ entityName);
		} else if (entityName.equals("WHumanbarracks")) {
			entityClass = (Class<? extends WUnit>) Class.forName("s2.entities.buildings."
					+ entityName);
		} else if (entityName.equals("WHumanblacksmith")) {
			entityClass = (Class<? extends WUnit>) Class.forName("s2.entities.buildings."
					+ entityName);
		} else if (entityName.equals("WLumberMill")) {
			entityClass = (Class<? extends WUnit>) Class.forName("s2.entities.buildings."
					+ entityName);
		} else if (entityName.equals("WTownhall")) {
			entityClass = (Class<? extends WUnit>) Class.forName("s2.entities.buildings."
					+ entityName);
		} else if (entityName.equals("WTower")) {
			entityClass = (Class<? extends WUnit>) Class.forName("s2.entities.buildings."
					+ entityName);
		} else if (entityName.equals("WArcher")) {
			entityClass = (Class<? extends WUnit>) Class
					.forName("s2.entities.troops." + entityName);
		} else if (entityName.equals("WCatapult")) {
			entityClass = (Class<? extends WUnit>) Class
					.forName("s2.entities.troops." + entityName);
		} else if (entityName.equals("WFootman")) {
			entityClass = (Class<? extends WUnit>) Class
					.forName("s2.entities.troops." + entityName);
		} else if (entityName.equals("WKnight")) {
			entityClass = (Class<? extends WUnit>) Class
					.forName("s2.entities.troops." + entityName);
		} else if (entityName.equals("WPeasant")) {
			entityClass = (Class<? extends WUnit>) Class
					.forName("s2.entities.troops." + entityName);
		} else {
			throw new ClassNotFoundException(entityName);
		}

		WUnit gsEntity = entityClass.newInstance();

		// gsEntity.setColor()

		setFeaturesForEntity(entity, entityClass, gsEntity);

		setEntityID(entity, entityClass, gsEntity);

		for (WPlayer p : players) {
			if (p.getOwner().equals(gsEntity.getOwner())) {
				gsEntity.setColor(p.getColor());
				break;
			}
		}

		return gsEntity;
	}

	private void setEntityID(Element entity, Class<? extends S2Entity> entityClass,
			S2Entity gsEntity) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		// Get the entityID and set it
		Method setEntityID = entityClass.getMethod("setEntityID", int.class);
		int id = Integer.parseInt(entity.getAttributeValue("id"));
		setEntityID.invoke(gsEntity, id);
		// keep track of highest unused entityID
		if (id >= nextID) {
			nextID = id++;
		}
	}

	private void setFeaturesForEntity(Element entity, Class<? extends S2Entity> entityClass,
			S2Entity gsEntity) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		List<Element> features = entity.getChildren();

		for (Element feature : features) {
			if (!feature.getName().equals("type")) {
				String featureName = feature.getName();
				featureName = featureName.substring(0, 1).toUpperCase() + featureName.substring(1);
				Method setter;
				try {
					setter = entityClass.getMethod("set" + featureName, String.class);
					setter.invoke(gsEntity, feature.getValue());
				} catch (NoSuchMethodException e) {
					setter = entityClass.getMethod("set" + featureName, int.class);
					setter.invoke(gsEntity, Integer.parseInt(feature.getValue()));
				}
			}
		}
	}

	public boolean cycle(List<ActionController> failedActions) {
		// get the actions and do the game!
		m_cycle++;

		if (m_map == null) {
			return false;
		}
		m_map.cycle(this);

		List<S2PhysicalEntity> toRemove = new LinkedList<S2PhysicalEntity>();
		{
			List<WUnit> l = new LinkedList<WUnit>(); // This variable is just
														// to avoid a concurrent
														// modification
			l.addAll(units);
			for (WUnit unit : l) {
				unit.cycle(m_cycle, this, failedActions);
				if (unit.getCurrent_hitpoints() <= 0) {
					toRemove.add(unit);
				}
			}

		}
		units.removeAll(toRemove);

		units.addAll(newUnits);
		newUnits.removeAll(newUnits);

		String owner = null;
		for (WUnit unit : units) {
			if (unit instanceof WGoldMine) {
				continue;
			}

			if (null == owner) {
				owner = unit.getOwner();
				continue;
			}

			if (!owner.equals(unit.getOwner())) {
				return true;
			}
		}

		return false;
	}

	public void draw(Graphics2D g, Set<WUnit> selectedEntities) {
		// draw the map
		m_map.draw(g);
		// draw the entities
		for (WUnit e : units) {
			if (null == e) {
				continue;
			}
			e.draw(g, x_offset, y_offset);
			// mark the Entity selected
			if (selectedEntities.size() > 0) {
				if (selectedEntities.contains(e)) {
					g.setColor(Color.GREEN);
					g.drawRect((e.getX() * S2PhysicalEntity.CELL_SIZE - x_offset), e
							.getY()
							* S2PhysicalEntity.CELL_SIZE - y_offset, e.getWidth()
							* S2PhysicalEntity.CELL_SIZE, e.getLength()
							* S2PhysicalEntity.CELL_SIZE);
				}
			}
		}
	}

	public int get_x_offset() {
		return x_offset;
	}

	public int get_y_offset() {
		return y_offset;
	}

	public void set_x_offset(int i_x_offset) {

		x_offset += i_x_offset;
		if (x_offset < 0)
			x_offset = 0;
		if (x_offset > (m_map.layers[0].map_width * S2PhysicalEntity.CELL_SIZE) - 800)
			x_offset = (m_map.layers[0].map_width * S2PhysicalEntity.CELL_SIZE) - 800;
		m_map.set_x_offset(x_offset);
	}

	public void set_y_offset(int i_y_offset) {
		y_offset += i_y_offset;
		if (y_offset < 0)
			y_offset = 0;
		if (y_offset > (m_map.layers[0].map_height * S2PhysicalEntity.CELL_SIZE) - 525) // Fix
			// for HUD
			y_offset = (m_map.layers[0].map_height * S2PhysicalEntity.CELL_SIZE) - 525;
		m_map.set_y_offset(y_offset);
	}

	public WUnit entityAt(int map_x, int map_y) {
		// get the unit at the location
		for (WUnit e : units) {
			if (e.isEntityAt(map_x, map_y)) {
				return e;
			}
		}
		for (WUnit e : newUnits) {
			if (e.isEntityAt(map_x, map_y)) {
				return e;
			}
		}
		return null;
	}

	public WOMapEntity mapEntityAt(int map_x, int map_y) {
		// get the map tile for that location.
		if (map_x >= 0 && map_y >= 0 && map_x < m_map.layers[1].map.length
				&& map_y < m_map.layers[1].map[map_x].length)
			return m_map.layers[1].map[map_x][map_y];

		return null;
	}

	/**
	 * Method returns the nearest map entity of the particular type from the
	 * given x and y co-ordinates
	 * 
	 * @param x
	 *            current x co-ordinate
	 * @param y
	 *            current y co-ordinate
	 * @param mapEntityType
	 *            type of the map entity
	 * @param home
	 *            find the wood closest to this entity
	 * @return the mapEntity if found, or null
	 */

	public S2PhysicalEntity locateNearestMapEntity(int x, int y,
			Class<? extends WOMapEntity> mapEntityType, S2PhysicalEntity home) {
		return m_map.layers[1].nearestMapEntity(x, y, mapEntityType, home);
	}

	/**
	 * removes a mapEntity at the given x,y coordinates.
	 * 
	 * @param x
	 * @param y
	 */
	public void clearMapEntity(int x, int y) {
		// System.out.println("Clearing --> (" + x + "," + y + ")");
		WOGrass clearedZone = new WOGrass();
		clearedZone.setX(x);
		clearedZone.setY(y);
		m_map.layers[1].map[x][y] = clearedZone;
	}

	/**
	 * Returns the first instance of the given unit for the given player.
	 * 
	 * @param player
	 * @param type
	 * @param game
	 * @return
	 */
	public WUnit getUnitType(WPlayer player, Class<? extends WUnit> unitType) {
		for (WUnit unit : units) {
			if (unit.getClass().equals(unitType)) {
				if (null == player && null == unit.owner) {
					return unit;
				}
				if (unit.owner.equals(player.owner)) {
					return unit;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the all instances of the given unit for the given player.
	 * 
	 * @param player
	 * @param type
	 * @param game
	 * @return
	 */
	public List<WUnit> getUnitTypes(WPlayer player, Class<? extends WUnit> unitType) {
		List<WUnit> list = new LinkedList<WUnit>();
		for (WUnit unit : units) {
			if (unit.getClass().equals(unitType)) {
				if (null == player && null == unit.owner) {
					list.add(unit);
				} else if (null == player) {
					continue;
				} else if (unit.owner.equals(player.owner)) {
					list.add(unit);
				}
			}
		}
		return list;
	}

	/**
	 * retrieves the unit with the given ID.
	 * 
	 * @param id
	 *            the entityID.
	 * @return the corresponding unit.
	 */
	public WUnit getUnit(int id) {
		for (WUnit entity : units) {
			if (entity.entityID == id) {
				return entity;
			}
		}
		return null;
	}

	/**
	 * lets S2 know to add the given unit at the end of the cycle.
	 * 
	 * @param unit
	 */
	public void addUnit(WUnit unit) {
		newUnits.add(unit);
	}

	/**
	 * returns the next unused entityID and increments the ID
	 * 
	 * @return
	 */
	public int nextID() {
		nextID++;
		return nextID;
	}

	public List<WPlayer> getPlayers() {
		return players;
	}

	/**
	 * checks if the unit collides with another unit
	 * 
	 * @param i_pe
	 * @return true if a collision occurs
	 */
	public boolean anyLevelCollision(S2PhysicalEntity i_pe) {
		for (WUnit e : units) {
			if (i_pe.collision(e))
				if (i_pe.entityID != e.entityID) {
					// System.out.println("COLLISION with " + e);
					return true;
				}
		}
		// return false;
		return m_map.anyLevelCollision(i_pe);
	}

	public S2Map getMap() {
		return m_map;
	}

	public List<WUnit> getUnits() {
		return units;
	}

	public List<S2Entity> getAllUnits() {
		List<S2Entity> ret = new LinkedList<S2Entity>();

		ret.addAll(units);
		ret.addAll(players);
		return ret;
	}

	public int getCycle() {
		return m_cycle;
	}

	public S2PhysicalEntity getEntity(int x, int y) {
		if (x < 0 || y < 0 || x >= m_map.getWidth() || y >= m_map.getHeight())
			return null;

		for (WUnit u : units) {
			if (u.getX() <= x && u.getX() + u.getWidth() > x && u.getY() <= y
					&& u.getY() + u.getLength() > y) {
				return u;
			}
		}

		return m_map.getEntity(x, y);
	}

	public WPlayer getPlayer(String m_playerid) {
		for (S2Entity e : players) {
			if (e instanceof WPlayer && e.getOwner().equals(m_playerid))
				return (WPlayer) e;
		}
		return null;
	}

	/**
	 * finds the square of free space that is size x size large that is closest
	 * to startx and starty.
	 * 
	 * @param startx
	 * @param starty
	 * @param size
	 * @return the pair representing the top left location of the free square.
	 *         null if one doesn't exist.
	 */
	public Pair<Integer, Integer> findFreeSpace(int startx, int starty, int size) {
		int x = startx;
		int y = starty;

		for (int i = 0; i < getMap().width / 2 && i < getMap().height / 2; i++) {
			for (int j = -1; j <= i; j++) {
				if (isSpaceFree(size, x + j, y + i)) {
					return new Pair<Integer, Integer>(x + j, y + i);
				}
				if (isSpaceFree(size, x + i, y + j)) {
					return new Pair<Integer, Integer>(x + i, y + j);
				}
				if (isSpaceFree(size, x + j, y - i)) {
					return new Pair<Integer, Integer>(x + j, y - i);
				}
				if (isSpaceFree(size, x - i, y + j)) {
					return new Pair<Integer, Integer>(x - i, y + j);
				}
			}
		}

		return null;
	}

	/**
	 * @param size
	 * @param x
	 * @param y
	 */
	private boolean isSpaceFree(int size, int x, int y) {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int xLoc = x + i;
				int yLoc = y + j;
				if (xLoc <= 0 || yLoc <= 0 || xLoc >= getMap().width || yLoc >= getMap().height) {
					return false;
				}
				if (!(mapEntityAt(xLoc, yLoc) instanceof WOGrass) || null != entityAt(xLoc, yLoc)) {
					return false;
				}
			}
		}
		return true;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void removeUnit(WUnit u) {
		units.remove(u);
	}
}
