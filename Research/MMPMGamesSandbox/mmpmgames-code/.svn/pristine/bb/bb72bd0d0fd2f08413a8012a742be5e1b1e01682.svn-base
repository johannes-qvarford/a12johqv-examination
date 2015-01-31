/**
 * *******************************************************************************
 * Organization : Georgia Institute of Technology Cognitive Computing Lab (CCL)
 * Authors	: Jai Rad Prafulla Mahindrakar Santi Ontanon
 ***************************************************************************
 */
package bc.objects;

import java.awt.Graphics2D;
import java.io.IOException;
import java.util.List;

import bc.Action;
import bc.BCMap;
import bc.BattleCity;
import bc.PlayerInput;
import bc.helpers.SpriteManager;
import bc.helpers.VirtualController;

public class BCOBullet extends BCPhysicalEntity {

    public int direction;

    public BCOBullet(int ax, int ay, int adirection) {
        entityID = "e" + (next_id++);
        width = 16;
        length = 16;
        x = ax;
        y = ay;
        direction = adirection;
    }

    public BCOBullet(String id, int ax, int ay, int adirection) {
        entityID = id;
        width = 16;
        length = 16;
        x = ax;
        y = ay;
        direction = adirection;
    }

    public BCOBullet() {
        width = 16;
        length = 16;
    }

    public BCOBullet(BCOBullet incoming) {
        super(incoming);
        this.direction = incoming.direction;
        width = 16;
        length = 16;
    }

    public Object clone() {
        BCOBullet e = new BCOBullet(this);
        return e;
    }

    public static boolean isActive() {
        return false;
    }

    public int getdirection() {
        return direction;
    }
/*
    public String gettank() {
        return tank;
    }
*/
    public void setdirection(int incoming) {
        this.direction = incoming;
    }

    public void setdirection(String incoming) {
        this.direction = Integer.parseInt(incoming);
    }
/*
    public void settank(String incoming) {
        this.tank = incoming;
    }
*/
    public void draw(Graphics2D g) throws IOException {
        switch (direction) {
            case PlayerInput.DIRECTION_UP:
                m_lastTileUsed = SpriteManager.get("bullet-up");
                break;
            case PlayerInput.DIRECTION_DOWN:
                m_lastTileUsed = SpriteManager.get("bullet-down");
                break;
            case PlayerInput.DIRECTION_LEFT:
                m_lastTileUsed = SpriteManager.get("bullet-left");
                break;
            case PlayerInput.DIRECTION_RIGHT:
                m_lastTileUsed = SpriteManager.get("bullet-right");
                break;
        } // switch
        if (m_lastTileUsed != null) {
            g.drawImage(m_lastTileUsed, x, y, null);
        }
    }

    public boolean cycle(List<VirtualController> l_vc, BCMap map, BattleCity game, List<Action> actions) throws IOException, ClassNotFoundException {
        if (!super.cycle(l_vc, map, game, actions)) {
            return false;
        }

        switch (direction) {
            case PlayerInput.DIRECTION_UP:
                y -= 4;
                break;
            case PlayerInput.DIRECTION_DOWN:
                y += 4;
                break;
            case PlayerInput.DIRECTION_LEFT:
                x -= 4;
                break;
            case PlayerInput.DIRECTION_RIGHT:
                x += 4;
                break;
        } // switch

        if (map.collisionExcludingTile(this, BCMap.TILE_WATER)) {
            BCPhysicalEntity col = map.collisionWithObject(this);

            if (col != null) {
                col.bulletHit();
            } else {
                // Destroy walls:
                map.removeBricks(x, y, 16, 16);
            } // if 
            return false;
        } // if 

        return true;
    }
}