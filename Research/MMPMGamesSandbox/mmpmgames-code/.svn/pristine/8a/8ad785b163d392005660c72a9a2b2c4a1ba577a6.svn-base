package bc;

import gatech.mmpm.TwoDMap;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import bc.helpers.SpriteManager;
import bc.helpers.VirtualController;
import bc.objects.BCOBullet;
import bc.objects.BCOPlayerTank;
import bc.objects.BCEntity;
import bc.objects.BCOBase;
import bc.objects.BCPhysicalEntity;
import bc.objects.EntityCreator;

public class BCMap {

    public static final String tileNames[] = {"brick", "metal", "water"};
    public static final int TILE_BRICK = 0;
    public static final int TILE_METAL = 1;
    public static final int TILE_WATER = 2;
    
    public static final int TILE_WIDTH = 16;
    public static final int TILE_HEIGHT = 16;
    int m_dx, m_dy;
    int m_tile_dx, m_tile_dy;
    BCMapTilePlace []m_tiles = null;
    List<BCObjectPlace> m_objects = new LinkedList<BCObjectPlace>();
    List<BCPhysicalEntity> m_running_objects = new LinkedList<BCPhysicalEntity>();
    List<BCPhysicalEntity> m_newly_added_running_objects = new LinkedList<BCPhysicalEntity>();
    List<BCPhysicalEntity> m_auxiliar_objects = new LinkedList<BCPhysicalEntity>();

    int hex_value(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return 10 + c - 'a';
        }
        if (c >= 'A' && c <= 'F') {
            return 10 + c - 'A';
        }
        return 0;
    }
    
    
    public int getDx() {
        return m_dx; 
    }
    
    public int getDy() {
        return m_dy; 
    }

    public BCMap(BCMap map) {
        m_dx = map.m_dx;
        m_dy = map.m_dy;
        m_tile_dx = map.m_tile_dx;
        m_tile_dy = map.m_tile_dy;
        m_tiles = new BCMapTilePlace[map.m_tiles.length];
        for(int i = 0;i<m_tiles.length;i++) m_tiles[i] = map.m_tiles[i];
        m_objects = map.m_objects;
        
        m_running_objects = new LinkedList<BCPhysicalEntity>();
        m_newly_added_running_objects = new LinkedList<BCPhysicalEntity>();
        m_auxiliar_objects = new LinkedList<BCPhysicalEntity>();
        for(BCPhysicalEntity tmp:map.m_running_objects) m_running_objects.add((BCPhysicalEntity)tmp.clone());
        for(BCPhysicalEntity tmp:map.m_newly_added_running_objects) m_newly_added_running_objects.add((BCPhysicalEntity)tmp.clone());
        for(BCPhysicalEntity tmp:map.m_auxiliar_objects) m_auxiliar_objects.add((BCPhysicalEntity)tmp.clone());
        
        // replace the excluded_for_collision objects:
        for(BCPhysicalEntity tmp:m_running_objects) {
            if (!tmp.getExcludedForCollision().isEmpty()) {
                List<BCPhysicalEntity> tmpl = new LinkedList<BCPhysicalEntity>();
                for(BCPhysicalEntity tmp2:tmp.getExcludedForCollision()) {
                    int idx = map.m_running_objects.indexOf(tmp2);
                    if (idx!=-1) tmpl.add(m_running_objects.get(idx));
                }
                tmp.clearExcludedForCollision();
                for(BCPhysicalEntity tmp2:tmpl) tmp.excludeForCollision(tmp2);
            }
        }
    }
        
    public BCMap(Element map) {

        for (Object o : map.getChildren("entity")) {
            Element entity = (Element) o;

            String type = entity.getChildText("type");
            if (type.equals("map")) {
                Element background = entity.getChild("background");

                m_tile_dx = Integer.parseInt(entity.getChildText("width"));
                m_tile_dy = Integer.parseInt(entity.getChildText("height"));
                m_dx = m_tile_dx * TILE_WIDTH;
                m_dy = m_tile_dy * TILE_HEIGHT;
                m_tiles = new BCMapTilePlace[m_tile_dx*m_tile_dy];

                String row;
                int y = 0;
                int x;

                for (Object o2 : background.getChildren()) {
                    Element row_xml = (Element) o2;
                    row = row_xml.getValue();
                    for (x = 0; x < row.length(); x++) {
                        BCMapTilePlace t = null;

                        if (row.charAt(x) == 'b') {
                            t = new BCMapTilePlace(TILE_BRICK);
                        } // if 
                        if (row.charAt(x) == 'm') {
                            t = new BCMapTilePlace(TILE_METAL);
                        } // if 
                        if (row.charAt(x) == 'w') {
                            t = new BCMapTilePlace(TILE_WATER);
                        } // if 

                        if (t != null) {
                            t.m_x = x * TILE_WIDTH;
                            t.m_y = y * TILE_WIDTH;
                            m_tiles[x+y*m_tile_dx] = t;
                        } // if 
                    } // for
                    y++;
                } // while 
            } // if 


            if (type.equals("BCOBase")) {
                BCObjectPlace op = new BCObjectPlace(entity.getAttributeValue("id"));
                op.m_object_name = "BCOBase";
                op.m_player_id = entity.getChildText("owner");
                op.m_x = Integer.parseInt(entity.getChildText("x")) * TILE_WIDTH;
                op.m_y = Integer.parseInt(entity.getChildText("y")) * TILE_WIDTH;
                m_objects.add(op);
            } // if

            if (type.equals("BCOPlayerTank")) {
                BCObjectPlace op = new BCObjectPlace(entity.getAttributeValue("id"));
                op.m_object_name = "BCOPlayerTank";
                op.m_player_id = entity.getChildText("owner");
                op.m_x = Integer.parseInt(entity.getChildText("x")) * TILE_WIDTH;
                op.m_y = Integer.parseInt(entity.getChildText("y")) * TILE_WIDTH;
                {
                    String color = entity.getChildText("color");
                    op.m_parameters.add(hex_value(color.charAt(0)) * 16 + hex_value(color.charAt(1)));
                    op.m_parameters.add(hex_value(color.charAt(2)) * 16 + hex_value(color.charAt(3)));
                    op.m_parameters.add(hex_value(color.charAt(4)) * 16 + hex_value(color.charAt(5)));
                }
                {
                    int dir = PlayerInput.DIRECTION_UP;
                    String d = entity.getChildText("direction");
                    if (d.equals("0")) {
                        dir = PlayerInput.DIRECTION_UP;
                    }
                    if (d.equals("2")) {
                        dir = PlayerInput.DIRECTION_DOWN;
                    }
                    if (d.equals("3")) {
                        dir = PlayerInput.DIRECTION_LEFT;
                    }
                    if (d.equals("1")) {
                        dir = PlayerInput.DIRECTION_RIGHT;
                    }
                    op.m_parameters.add(dir);
                }
                m_objects.add(op);
            } // if 


            if (type.equals("BCOBullet")) {
                BCObjectPlace op = new BCObjectPlace(entity.getAttributeValue("id"));
                op.m_object_name = "BCOBullet";
                op.m_player_id = entity.getChildText("owner");
                op.m_x = Integer.parseInt(entity.getChildText("x")) * TILE_WIDTH;
                op.m_y = Integer.parseInt(entity.getChildText("y")) * TILE_WIDTH;
                {
                    int dir = PlayerInput.DIRECTION_UP;
                    String d = entity.getChildText("direction");
                    if (d.equals("0")) {
                        dir = PlayerInput.DIRECTION_UP;
                    }
                    if (d.equals("2")) {
                        dir = PlayerInput.DIRECTION_DOWN;
                    }
                    if (d.equals("3")) {
                        dir = PlayerInput.DIRECTION_LEFT;
                    }
                    if (d.equals("1")) {
                        dir = PlayerInput.DIRECTION_RIGHT;
                    }
                    op.m_parameters.add(dir);
                }
                m_objects.add(op);
            } // if 

            if (type.equals("BCOEnemyTank")) {
                BCObjectPlace op = new BCObjectPlace(entity.getAttributeValue("id"));
                op.m_object_name = "BCOEnemyTank";
                op.m_player_id = entity.getChildText("owner");
                op.m_x = Integer.parseInt(entity.getChildText("x")) * TILE_WIDTH;
                op.m_y = Integer.parseInt(entity.getChildText("y")) * TILE_WIDTH;
                {
                    int dir = PlayerInput.DIRECTION_UP;
                    String d = entity.getChildText("direction");
                    if (d.equals("0")) {
                        dir = PlayerInput.DIRECTION_UP;
                    }
                    if (d.equals("2")) {
                        dir = PlayerInput.DIRECTION_DOWN;
                    }
                    if (d.equals("3")) {
                        dir = PlayerInput.DIRECTION_LEFT;
                    }
                    if (d.equals("1")) {
                        dir = PlayerInput.DIRECTION_RIGHT;
                    }
                    op.m_parameters.add(dir);
                }
                op.m_parameters.add(Integer.parseInt(entity.getChildText("type")));
                m_objects.add(op);
            } // if 

            if (type.equals("BCOTankGenerator")) {
                // <tank-generator x="50" y="2" time-for-next="200" remaining-tanks="4" interval="400" tank-type="0"/>
                BCObjectPlace op = new BCObjectPlace(entity.getAttributeValue("id"));
                op.m_object_name = "BCOTankGenerator";
                op.m_player_id = null;
                op.m_x = Integer.parseInt(entity.getChildText("x")) * TILE_WIDTH;
                op.m_y = Integer.parseInt(entity.getChildText("y")) * TILE_WIDTH;
                op.m_parameters.add(Integer.parseInt(entity.getChildText("time-for-next")));
                op.m_parameters.add(Integer.parseInt(entity.getChildText("interval")));
                op.m_parameters.add(Integer.parseInt(entity.getChildText("remaining-tanks")));
                op.m_parameters.add(Integer.parseInt(entity.getChildText("tank-type")));
                m_objects.add(op);
            } // if 

        } // while 

    }

    public void reset() throws IOException {
        BCPhysicalEntity o;
        m_running_objects.clear();

        for (BCObjectPlace po : m_objects) {
            o = EntityCreator.create(po.m_object_name, po.m_id, po.m_x, po.m_y, po.m_player_id, po.m_parameters);
            m_running_objects.add(o);
        }
    }

    public void cycle(List<VirtualController> l_vc, BattleCity game, List<Action> actions) throws IOException, ClassNotFoundException {
        List<BCPhysicalEntity> to_delete = new LinkedList<BCPhysicalEntity>();

        for (BCPhysicalEntity o : m_running_objects) {
            if (!o.cycle(l_vc, this, game, actions)) {
                to_delete.add(o);
            }
        }

        while (!to_delete.isEmpty()) {
            BCPhysicalEntity o = to_delete.remove(0);
            m_running_objects.remove(o);
        }

        for (BCPhysicalEntity o : m_auxiliar_objects) {
            if (!o.cycle(l_vc, this, game, actions)) {
                to_delete.add(o);
            }
        }

        while (!to_delete.isEmpty()) {
            BCPhysicalEntity o = to_delete.remove(0);
            m_auxiliar_objects.remove(o);
        }

        while (!m_newly_added_running_objects.isEmpty()) {
            m_running_objects.add(m_newly_added_running_objects.remove(0));
        }
    }

    public void draw(Graphics2D g) throws IOException {
        for(int i = 0;i<m_tiles.length;i++) {
            BCMapTilePlace tp = m_tiles[i];
            if (tp!=null && tp.m_tile_cache != null) g.drawImage(tp.m_tile_cache, tp.m_x, tp.m_y, null);
        }

        for (BCPhysicalEntity o : m_running_objects) {
            o.draw(g);
        }
        for (BCPhysicalEntity o : m_auxiliar_objects) {
            o.draw(g);
        }
    }
    
    
    public List<BCPhysicalEntity> getObjects(String type) throws ClassNotFoundException {
        List<BCPhysicalEntity> res = new LinkedList<BCPhysicalEntity>();

        for (BCPhysicalEntity o : m_running_objects) {
            if (Class.forName("bc.objects." + type).isInstance(o)) {
                res.add(o);
            }
        }

        return res;
    }
    
    
    public List<BCPhysicalEntity> getObjects(Class type) {
        List<BCPhysicalEntity> res = new LinkedList<BCPhysicalEntity>();

        for (BCPhysicalEntity o : m_running_objects) {
            if (type.isInstance(o)) {
                res.add(o);
            }
        }

        return res;
    }


    public void removeObject(BCPhysicalEntity o) {
        m_running_objects.remove(o);
    }

    public synchronized String saveToXML(int spaces) {
        String tmp = "";	// something big enough :)
        int i;
        int dx = m_dx / 16, dy = m_dy / 16;

        for (i = 0; i < spaces; i++) {
            tmp += " ";
        }
        tmp += "<entity id=\"0\">\n";

        for (i = 0; i < spaces + 2; i++) {
            tmp += " ";
        }
        tmp += "<type>map</type>\n";

        for (i = 0; i < spaces + 2; i++) {
            tmp += " ";
        }
        tmp += "<width>" + dx + "</width>\n";

        for (i = 0; i < spaces + 2; i++) {
            tmp += " ";
        }
        tmp += "<height>" + dy + "</height>\n";

        for (i = 0; i < spaces + 2; i++) {
            tmp += " ";
        }
        tmp += "<cell-width>16</cell-width>\n";

        for (i = 0; i < spaces + 2; i++) {
            tmp += " ";
        }
        tmp += "<cell-height>16</cell-height>\n";

        for (i = 0; i < spaces + 2; i++) {
            tmp += " ";
        }
        tmp += "<background>\n";
        {
            char bg[] = new char[dx * dy];
            char bgtmp[] = new char[dx];

            for (i = 0; i < dx * dy; i++) {
                bg[i] = '.';
            }

            for(int j = 0;j<m_tiles.length;j++) {
                BCMapTilePlace t = m_tiles[j];
                if (t!=null) {
                    if (t.m_tile_type == TILE_BRICK) {
                        bg[j] = 'b';
                    }
                    if (t.m_tile_type == TILE_METAL) {
                        bg[j] = 'm';
                    }
                    if (t.m_tile_type == TILE_WATER) {
                        bg[j] = 'w';
                    }
                }
            } // while 

            for (int j = 0; j < dy; j++) {
                for (i = 0; i < spaces + 4; i++) {
                    tmp += " ";
                }
                tmp += "<row>";
                System.arraycopy(bg, j * dx, bgtmp, 0, dx);
//				bgtmp[dx]=0;
                tmp += new String(bgtmp);
                tmp += "</row>\n";
            } // for
        }
        for (i = 0; i < spaces + 2; i++) {
            tmp += " ";
        }
        tmp += "</background>\n";

        for (i = 0; i < spaces; i++) {
            tmp += " ";
        }
        tmp += "</entity>\n";

        for (BCPhysicalEntity o : m_running_objects) {
            tmp += o.toXMLString();
        }

        return tmp;
    }

    class BCMapTilePlace {

        public int m_tile_type;
        Image m_tile_cache = null;
        public int m_x, m_y;
        
        public BCMapTilePlace(int type) {
            m_tile_type = type;
            try {
                m_tile_cache = SpriteManager.get(tileNames[type]);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    };

    class BCObjectPlace {

        public BCObjectPlace(String id) {
            m_id = id;
        }
        public String m_id;
        public String m_object_name;
        public String m_player_id;
        public int m_x, m_y;
        public List<Integer> m_parameters = new LinkedList<Integer>();
    }

    public boolean collision(BCPhysicalEntity o2, int xoffs, int yoffs) {
        o2.setx(o2.getx() + xoffs);
        o2.sety(o2.gety() + yoffs);
        boolean ret = collision(o2);
        o2.setx(o2.getx() - xoffs);
        o2.sety(o2.gety() - yoffs);
        return ret;
    }

    public boolean collision(BCPhysicalEntity o2) {

        int x1 = o2.getx();
        int y1 = o2.gety();
        int x2 = x1 + o2.getwidth() - 1;
        int y2 = y1 + o2.getlength() - 1;
        x1/=TILE_WIDTH;
        y1/=TILE_HEIGHT;
        x2/=TILE_WIDTH;
        y2/=TILE_HEIGHT;
        if (x1<0) x1 = 0;
        if (y1<0) y1 = 0;
        if (x2>m_tile_dx-1) x2 = m_tile_dx-1;
        if (y2>m_tile_dy-1) y2 = m_tile_dy-1;
        for(int i = x1;i<=x2;i++) {
            for(int j = y1;j<=y2;j++) {
                if (m_tiles[i+j*m_tile_dx]!=null) return true;
            }
        }

        for (BCPhysicalEntity o : m_running_objects) {
            if (o != o2 && o2.collision(o)) {
                return true;
            }
        }

        return false;
    }

    public boolean collisionExcludingTile(BCPhysicalEntity o2, int tileType) throws IOException {

        int x1 = o2.getx();
        int y1 = o2.gety();
        int x2 = x1 + o2.getwidth() - 1;
        int y2 = y1 + o2.getlength() - 1;
        x1/=TILE_WIDTH;
        y1/=TILE_HEIGHT;
        x2/=TILE_WIDTH;
        y2/=TILE_HEIGHT;
        if (x1<0) x1 = 0;
        if (y1<0) y1 = 0;
        if (x2>m_tile_dx-1) x2 = m_tile_dx-1;
        if (y2>m_tile_dy-1) y2 = m_tile_dy-1;
        for(int i = x1;i<=x2;i++) {
            for(int j = y1;j<=y2;j++) {
                BCMapTilePlace tp = m_tiles[i+j*m_tile_dx];
                if (tp!=null && tp.m_tile_type != tileType) {
//                    if (o2.collision(tp.m_tile_cache, tp.m_x, tp.m_y)) {
                        return true;
//                    }
                }
            }
        }

        for (BCPhysicalEntity o : m_running_objects) {
            if (o != o2 && o2.collision(o)) {
                return true;
            }
        }

        return false;
    }

    public void addObject(BCPhysicalEntity o) {
        m_newly_added_running_objects.add(o);
    }

    public BCPhysicalEntity collisionWithObject(BCPhysicalEntity o2) {
        for (BCPhysicalEntity o : m_running_objects) {
            if (o != o2 && o2.collision(o)) {
                return o;
            }
        }

        return null;
    }

    public void removeBricks(int x1, int y1, int dx, int dy) {
        int x2 = x1 + dx - 1;
        int y2 = y1 + dy - 1;
        x1/=TILE_WIDTH;
        y1/=TILE_HEIGHT;
        x2/=TILE_WIDTH;
        y2/=TILE_HEIGHT;
        if (x1<0) x1 = 0;
        if (y1<0) y1 = 0;
        if (x2>m_tile_dx-1) x2 = m_tile_dx-1;
        if (y2>m_tile_dy-1) y2 = m_tile_dy-1;
        for(int i = x1;i<=x2;i++) {
            for(int j = y1;j<=y2;j++) {
                BCMapTilePlace tp = m_tiles[i+j*m_tile_dx];
                if (tp != null && tp.m_tile_type == TILE_BRICK) {
                    m_tiles[i+j*m_tile_dx] = null;
                }
            }
        }        
    }

    public boolean collisionExcludingObject(BCPhysicalEntity o2, int offsx, int offsy, Class type) throws IOException, ClassNotFoundException {
        int x1 = o2.getx() + offsx;
        int y1 = o2.gety() + offsy;
        int x2 = x1 + (o2.getwidth()-1);
        int y2 = y1 + (o2.getlength()-1);
        x1/=TILE_WIDTH;
        y1/=TILE_HEIGHT;
        x2/=TILE_WIDTH;
        y2/=TILE_HEIGHT;
        if (x1<0) x1 = 0;
        if (y1<0) y1 = 0;
        if (x2>m_tile_dx-1) x2 = m_tile_dx-1;
        if (y2>m_tile_dy-1) y2 = m_tile_dy-1;
        for(int i = x1;i<=x2;i++) {
            for(int j = y1;j<=y2;j++) {
                BCMapTilePlace tp = m_tiles[i+j*m_tile_dx];
                if (tp!=null) return true;
            }
        }
         
        for (BCPhysicalEntity o : m_running_objects) {
            o2.setx(o2.getx() + offsx);
            o2.sety(o2.gety() + offsy);

            if (type.isInstance(o) && o != o2 && o2.collision(o)) {
                o2.setx(o2.getx() - offsx);
                o2.sety(o2.gety() - offsy);
                return true;
            }
            o2.setx(o2.getx() - offsx);
            o2.sety(o2.gety() - offsy);
        }

        return false;
    }
    
    
    public boolean collisionOnlyMap(BCPhysicalEntity o2, int offsx, int offsy) throws IOException, ClassNotFoundException {
        int x1 = o2.getx() + offsx;
        int y1 = o2.gety() + offsy;
        int x2 = x1 + (o2.getwidth()-1);
        int y2 = y1 + (o2.getlength()-1);
        x1/=TILE_WIDTH;
        y1/=TILE_HEIGHT;
        x2/=TILE_WIDTH;
        y2/=TILE_HEIGHT;
        if (x1<0) x1 = 0;
        if (y1<0) y1 = 0;
        if (x2>m_tile_dx-1) x2 = m_tile_dx-1;
        if (y2>m_tile_dy-1) y2 = m_tile_dy-1;
        for(int i = x1;i<=x2;i++) {
            for(int j = y1;j<=y2;j++) {
                BCMapTilePlace tp = m_tiles[i+j*m_tile_dx];
                if (tp!=null) return true;
            }
        }
         
        return false;
    }    
    
    
    public void dumpToScreen() {
        StringBuffer rows[] = new StringBuffer[m_tile_dy];
        
        for(int y = 0;y<m_tile_dy;y++) {
            rows[y] = new StringBuffer("");
            for(int x = 0;x<m_tile_dx;x++) {
                if (m_tiles[x+y*m_tile_dx]==null) {
                    rows[y].append(" ");
                } else {
                    switch(m_tiles[x+y*m_tile_dx].m_tile_type) {
                        case TILE_BRICK: rows[y].append("b");
                                         break;
                        case TILE_METAL: rows[y].append("m");
                                         break;
                        case TILE_WATER: rows[y].append("w");
                                         break;
                    }
                }
            }            
        }
        
        for(BCPhysicalEntity pe:m_running_objects) {
            char s = '.';
            if (pe instanceof BCOPlayerTank) s = 'T';
            if (pe instanceof BCOBase) s = 'B';
            if (pe instanceof BCOBullet) s = '*';
            int x1 = pe.getx();
            int y1 = pe.gety();
            int x2 = x1 + (pe.getwidth());
            int y2 = y1 + (pe.getlength());
            x1/=TILE_WIDTH;
            y1/=TILE_HEIGHT;
            x2/=TILE_WIDTH;
            y2/=TILE_HEIGHT;
            if (x1<0) x1 = 0;
            if (y1<0) y1 = 0;
            if (x2>m_tile_dx-1) x2 = m_tile_dx-1;
            if (y2>m_tile_dy-1) y2 = m_tile_dy-1;
            for(int y = y1;y<y2;y++) {
                for(int x = x1;x<x2;x++) {
                    rows[y].setCharAt(x, s);
                }
            }
        }
        
        for(StringBuffer r:rows) System.out.println(r);
    }

    /**
     * @param game Current game
     * @return GameState in
     */
    public gatech.mmpm.GameState toGameState(gatech.mmpm.IDomain idomain) {
        gatech.mmpm.GameState ret = new gatech.mmpm.GameState();
        int dx = m_dx / 16, dy = m_dy / 16;
        int coords[] = {0, 0, 0};
        TwoDMap map = new TwoDMap(dx, dy, 16, 16);

        for(int i = 0;i<m_tiles.length;i++) {
            BCMapTilePlace t = m_tiles[i];
            if (t!=null) {
                coords[0] = t.m_x / 16;
                coords[1] = t.m_y / 16;
                if (t.m_tile_type == TILE_BRICK) {
                    map.setCellLocation('b', coords, idomain);
                }
                if (t.m_tile_type == TILE_METAL) {
                    map.setCellLocation('m', coords, idomain);
                }
                if (t.m_tile_type == TILE_WATER) {
                    map.setCellLocation('w', coords, idomain);
                }
            }
        } // while 

        ret.addMap(map);

        for (BCEntity e : m_running_objects) {
            gatech.mmpm.Entity d2Entity = null;
            if (e instanceof bc.objects.BCOBase) {
                d2Entity = new bc.mmpm.entities.BCOBase(e.getentityID(), e.getowner());
                ((bc.mmpm.entities.BCOBase) d2Entity).setx(((bc.objects.BCOBase) e).getx());
                ((bc.mmpm.entities.BCOBase) d2Entity).sety(((bc.objects.BCOBase) e).gety());
            } else if (e instanceof bc.objects.BCOBullet) {
                d2Entity = new bc.mmpm.entities.BCOBullet(e.getentityID(), e.getowner());
                ((bc.mmpm.entities.BCOBullet) d2Entity).setx(((bc.objects.BCOBullet) e).getx());
                ((bc.mmpm.entities.BCOBullet) d2Entity).sety(((bc.objects.BCOBullet) e).gety());
            } else if (e instanceof bc.objects.BCOEnemyTank) {
                d2Entity = new bc.mmpm.entities.BCOEnemyTank(e.getentityID(), e.getowner());
                ((bc.mmpm.entities.BCOEnemyTank) d2Entity).setx(((bc.objects.BCOEnemyTank) e).getx());
                ((bc.mmpm.entities.BCOEnemyTank) d2Entity).sety(((bc.objects.BCOEnemyTank) e).gety());
                ((bc.mmpm.entities.BCOEnemyTank) d2Entity).setDirection(((bc.objects.BCOEnemyTank) e).getdirection());
                ((bc.mmpm.entities.BCOEnemyTank) d2Entity).setNext_shot_delay(((bc.objects.BCOEnemyTank) e).getnext_shot_delay());
                ((bc.mmpm.entities.BCOEnemyTank) d2Entity).setNext_move_delay(
                        Math.max(((bc.objects.BCOEnemyTank) e).getnext_shot_delay(),
                        ((bc.objects.BCOEnemyTank) e).getnext_shot_delay()));
            } else if (e instanceof bc.objects.BCOPlayerTank) {
                d2Entity = new bc.mmpm.entities.BCOPlayerTank(e.getentityID(), e.getowner());
                ((bc.mmpm.entities.BCOPlayerTank) d2Entity).setx(((bc.objects.BCOPlayerTank) e).getx());
                ((bc.mmpm.entities.BCOPlayerTank) d2Entity).sety(((bc.objects.BCOPlayerTank) e).gety());
                ((bc.mmpm.entities.BCOPlayerTank) d2Entity).setDirection(((bc.objects.BCOPlayerTank) e).getdirection());
                ((bc.mmpm.entities.BCOPlayerTank) d2Entity).setNext_shot_delay(((bc.objects.BCOPlayerTank) e).getnext_shot_delay());
                ((bc.mmpm.entities.BCOPlayerTank) d2Entity).setNext_move_delay(
                        Math.max(((bc.objects.BCOPlayerTank) e).getnext_shot_delay(),
                        ((bc.objects.BCOPlayerTank) e).getnext_shot_delay()));
                ((bc.mmpm.entities.BCOPlayerTank) d2Entity).setColor(((bc.objects.BCOPlayerTank) e).rgbStringColor());

            } else if (e instanceof bc.objects.BCOTankGenerator) {
                d2Entity = new bc.mmpm.entities.BCOTankGenerator(e.getentityID(), e.getowner());
                ((bc.mmpm.entities.BCOTankGenerator) d2Entity).setx(((bc.objects.BCOTankGenerator) e).getx());
                ((bc.mmpm.entities.BCOTankGenerator) d2Entity).sety(((bc.objects.BCOTankGenerator) e).gety());
                ((bc.mmpm.entities.BCOTankGenerator) d2Entity).setTime_for_next(((bc.objects.BCOTankGenerator) e).gettime_for_next());
                ((bc.mmpm.entities.BCOTankGenerator) d2Entity).setRemaining_tanks(((bc.objects.BCOTankGenerator) e).getremaining_tanks());
                ((bc.mmpm.entities.BCOTankGenerator) d2Entity).setInterval(((bc.objects.BCOTankGenerator) e).getinterval());
                ((bc.mmpm.entities.BCOTankGenerator) d2Entity).setTank_type("" + ((bc.objects.BCOTankGenerator) e).gettank_type());
            } else {
                System.err.println("Entity of class " + e.getClass().getName() + " not supported in BCMap.toGameState!");
            }
            if ((d2Entity != null)) {
                ret.addEntity(d2Entity);
            }
        }

//		System.out.println(ret.toString());		
        return ret;
    }
}
