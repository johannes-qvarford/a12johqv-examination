package s2.mmpm;

import gatech.mmpm.Action;
import gatech.mmpm.ActionParameterType;
import gatech.mmpm.ParseLmxTrace;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import s2.actionControllers.ActionController;
import s2.actionControllers.AttackActionController;
import s2.actionControllers.BuildActionController;
import s2.actionControllers.HarvestActionController;
import s2.actionControllers.MoveActionController;
import s2.actionControllers.RepairActionController;
import s2.actionControllers.StandGroundActionController;
import s2.actionControllers.TrainActionController;
import s2.mmpm.actions.Attack;
import s2.mmpm.actions.AttackLocation;
import s2.mmpm.actions.Build;
import s2.mmpm.actions.Move;
import s2.mmpm.actions.Repair;
import s2.mmpm.actions.ResourceLocation;
import s2.mmpm.actions.Stop;
import s2.mmpm.actions.Train;
import s2.entities.S2PhysicalEntity;
import s2.entities.WUnit;
import s2.entities.buildings.WBuilding;
import s2.entities.buildings.WGoldMine;
import s2.entities.map.WOTree;
import s2.game.S2;

/**
 * Class used to convert elements between both domains: game and D2.
 * 
 * @author Santi Ontanon
 */

public class Game2D2Converter {

	public static ActionController toGameAction(gatech.mmpm.Action d2Action, S2 game) {

		WUnit unit = game.getUnit(Integer.parseInt(d2Action.getEntityID()));
		Set<WUnit> units = new TreeSet<WUnit>();
		units.add(unit);
		if (d2Action instanceof ResourceLocation) {
			S2PhysicalEntity pe = game.getEntity((int)((ResourceLocation) d2Action).getCoor()[0],
												 (int)((ResourceLocation) d2Action).getCoor()[1]);
			if (pe instanceof WGoldMine) {
				return new HarvestActionController(units, pe, game);
			} else if (pe instanceof WOTree) {
				return new HarvestActionController(units, pe, game);
			} else {
				System.err
						.println("Game2D2Converter.toGameAction: D2 is trying to harvest an entity that is not a goldmine nor a tree!");
			}
		} else if (d2Action instanceof Train) {
			return new TrainActionController(units, TrainActionController.ALL_UNIT_MAPPINGS.indexOf(((Train) d2Action).getType().getName().replaceFirst("s2.mmpm.entities","s2.entities.troops")),true);
		} else if (d2Action instanceof Build) {
			return new BuildActionController(units, (int)((Build) d2Action).getCoor()[0],
												    (int)((Build) d2Action).getCoor()[1],
												    BuildActionController.ALL_BUILDING_MAPPINGS.indexOf(((Build) d2Action).getType().getName().replaceFirst("s2.mmpm.entities","s2.entities.buildings")));
		} else if (d2Action instanceof Attack) {
			return new AttackActionController(units, game.getUnit(Integer.parseInt(((Attack) d2Action).getTarget().getentityID())), game);
		} else if (d2Action instanceof AttackLocation) {
			WUnit pe = (WUnit) game.getEntity((int)((Build) d2Action).getCoor()[0],
											  (int)((Build) d2Action).getCoor()[1]);
			return new AttackActionController(units, pe, game);
		} else if (d2Action instanceof Move) {
			return new MoveActionController(units, (int)((Build) d2Action).getCoor()[0],
												   (int)((Build) d2Action).getCoor()[1]);
		} else if (d2Action instanceof Repair) {
			return new RepairActionController(units, (WBuilding) game.getUnit(Integer.parseInt(((Repair) d2Action).getTarget().getentityID())), game);
		} else if (d2Action instanceof Stop) {
			return new StandGroundActionController(units);
		}

		return null;
	}

	public static List<Action> toD2Action(ActionController action) {
		List<Action> list = new LinkedList<Action>();
		if (action instanceof AttackActionController) {
			for (WUnit u : action.getUnits()) {
				if (u==null) {
					System.err.println("Null unit in Attack action produced by D2, ignoring!");
				} else {
					Attack ret = new Attack("" + u.getEntityID(), u.getOwner());
					if (((AttackActionController) action).getTarget()!=null) {
						ret.setTarget(((AttackActionController) action).getTarget().toD2Entity());
						list.add(ret);
					} else {
						System.err.println("Null target in Attack action produced by D2, ignoring!");
					}
				}
			}
		} else if (action instanceof BuildActionController) {
			for (WUnit u : action.getUnits()) {
				if (u==null) {
					System.err.println("Null unit in Build action produced by D2, ignoring!");
				} else {
					Build ret = new Build("" + u.getEntityID(), u.getOwner());
					String type = BuildActionController.ALL_BUILDING_MAPPINGS.get(((BuildActionController) action).getType()).replaceFirst("s2.entities.buildings", "s2.mmpm.entities");
					if (type==null) {
						System.err.println("error translating entity: " + ((BuildActionController) action).getType());
					} else {
						if ((Class<? extends gatech.mmpm.Entity>) 
							ActionParameterType.ENTITY_TYPE.fromString(type)==null) {
							System.err.println("error 2 translating entity: " + ((BuildActionController) action).getType());						
						}
					}
					ret.setType(type);
					ret.setCoor(new float[]{((BuildActionController) action).getBuildX(),
										    ((BuildActionController) action).getBuildY(),0});
					list.add(ret);
				}
			}
		} else if (action instanceof MoveActionController) {
			for (WUnit u : action.getUnits()) {
				if (u==null) {
					System.err.println("Null unit in Move action produced by D2, ignoring!");
				} else {
					Move ret = new Move("" + u.getEntityID(), u.getOwner());
					ret.setCoor(new float[]{((MoveActionController) action).getMoveX(),
											((MoveActionController) action).getMoveY(),0});
					list.add(ret);
				}
			}
		} else if (action instanceof RepairActionController) {
			for (WUnit u : action.getUnits()) {
				if (u==null) {
					System.err.println("Null unit in Repair action produced by D2, ignoring!");
				} else {
					Repair ret = new Repair("" + u.getEntityID(), u.getOwner());
					ret.setTarget(((RepairActionController) action).getTarget().toD2Entity());
					list.add(ret);
				}
			}
		} else if (action instanceof HarvestActionController) {
			for (WUnit u : action.getUnits()) {
				if (u==null) {
					System.err.println("Null unit in Harvest action produced by D2, ignoring!");
				} else {
					ResourceLocation ret = new ResourceLocation("" + u.getEntityID(), u.getOwner());
					ret.setCoor(new float[]{((HarvestActionController) action).getTarget().getX(),
											((HarvestActionController) action).getTarget().getY(),0});
					list.add(ret);
				}
			}
		} else if (action instanceof StandGroundActionController) {
			for (WUnit u : action.getUnits()) {
				if (u==null) {
					System.err.println("Null unit in Stop action produced by D2, ignoring!");
				} else {
					Stop ret = new Stop("" + u.getEntityID(), u.getOwner());
					list.add(ret);
				}
			}
		} else if (action instanceof TrainActionController) {
			for (WUnit u : action.getUnits()) {
				if (u==null) {
					System.err.println("Null unit in Train action produced by D2, ignoring!");
				} else {
					Train ret = new Train("" + u.getEntityID(), u.getOwner());
					ret.setType(TrainActionController.ALL_UNIT_MAPPINGS.get(((TrainActionController) action).getType()).replaceFirst("s2.entities.troops", "s2.mmpm.entities"));
					list.add(ret);
				}
			}
		}
		return list;
	}

	/**
	 * @param game
	 *            Current game
	 * @return GameState in
	 */
	public static gatech.mmpm.GameState toGameState(s2.game.S2 game, gatech.mmpm.IDomain idomain) {
		gatech.mmpm.GameState ret = new gatech.mmpm.GameState();

		ret.addMap(game.getMap().toD2Map(idomain));

		// Add the entities stored in our TMap
		List<s2.entities.S2Entity> entities;

		entities = game.getAllUnits();
		for (s2.entities.S2Entity e : entities) {
			gatech.mmpm.Entity d2Entity = e.toD2Entity();
			if ((d2Entity != null))
				ret.addEntity(d2Entity);
		}

		// System.out.println(ret.toString());

		return ret;
	}

	static ParseLmxTrace parser = new ParseLmxTrace(S2Domain.getDomainName());
}
