package bc.objects;

import java.io.IOException;
import java.util.List;

public class EntityCreator {

	public static BCPhysicalEntity create(String name, String id, int x, int y, String playerid, List<Integer> parameters) throws IOException {
	    
	    if (name.equals("BCOBase"))
	        return new BCOBase(id,x,y,playerid);
	    if (name.equals("BCOPlayerTank"))
	        return new BCOPlayerTank(id,x, y, playerid, parameters.get(0), parameters.get(1), parameters.get(2), parameters.get(3));
	    if (name.equals("BCOBullet"))
	        return new BCOBullet(id,x, y, parameters.get(0));
	    if (name.equals("BCOEnemyTank"))
	        return new BCOEnemyTank(id,x, y, parameters.get(0), parameters.get(1));
	    if (name.equals("BCOTankGenerator"))
	        return new BCOTankGenerator(id,x, y, parameters.get(0), parameters.get(1), parameters.get(2), parameters.get(3));
		return null;
	}

}
