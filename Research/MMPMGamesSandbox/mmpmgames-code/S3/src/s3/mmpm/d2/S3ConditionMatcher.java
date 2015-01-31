/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package s3.mmpm.d2;

import d2.plans.ActionPlan;
import d2.plans.GoalPlan;
import d2.plans.Plan;
import d2.worldmodel.ConditionMatcher;
import gatech.mmpm.Action;
import gatech.mmpm.Context;
import gatech.mmpm.Entity;
import gatech.mmpm.GameState;
import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.builtin.EntityTypeExists;
import gatech.mmpm.sensor.composite.Invocation;
import java.util.LinkedList;
import java.util.List;
import s3.mmpm.actions.Attack;
import s3.mmpm.actions.Build;
import s3.mmpm.actions.ResourceLocation;
import s3.mmpm.actions.Train;
import s3.mmpm.entities.*;
import s3.mmpm.goals.*;
import s3.mmpm.sensors.EntitiesNeededToBuildType;
import s3.mmpm.sensors.GoldCondition;
import s3.mmpm.sensors.WoodCondition;

/**
 *
 * @author santi
 */
public class S3ConditionMatcher extends ConditionMatcher {
    ConditionMatcher base = new ConditionMatcher();

    public double match(Context postContext, Sensor postCondition, int cycle1, GameState gs1, String player1,
                        Context preContext, Sensor preCondition, int cycle2, GameState gs2, String player2) {
        if (base.match(postContext, postCondition, cycle1, gs1, player1,
                       preContext, preCondition, cycle2, gs2, player2)>=1.0) return 1.0;

        // Specific S3 rules:
        // ...

        return 0.0;
    }


    public double match(Plan first, Sensor postCondition, int cycle1, GameState gs1, String player1,
                        Plan second, Sensor preCondition, int cycle2, GameState gs2, String player2) {

        Context preConditionParameters = null;
        Context postConditionParameters = null;

        if (preCondition instanceof Invocation) {
            Invocation inv = (Invocation)preCondition;
            preCondition = inv.getSensor();
            preConditionParameters = inv.evaluateParameters(cycle2, gs2, player2, second.getContext(cycle2, gs2, player2));
        }
        if (postCondition instanceof Invocation) {
            Invocation inv = (Invocation)postCondition;
            postCondition = inv.getSensor();
            postConditionParameters = inv.evaluateParameters(cycle2, gs2, player2, first.getContext(cycle2, gs2, player2));
        }
        
        
        if (base.match(first, postCondition, cycle1, gs1, player1,
                       second, preCondition, cycle2, gs2, player2)>=1.0) return 1.0;

        if (preCondition!=null) {
            if (preCondition instanceof WoodCondition) {
                Integer tmp = (preConditionParameters==null ? null:(Integer)preConditionParameters.get("minimum"));
                if (tmp==null || tmp>0) {
                    if (first instanceof ActionPlan &&
                        ((ActionPlan)first).getAction() instanceof ResourceLocation) {
                        ResourceLocation a = (ResourceLocation)(((ActionPlan)first).getAction());
                        Entity e = gs1.getEntityAt(a.getCoor());
                        if (e!=null && e instanceof WOTree) return 1.0;
                    }
                }
            } else if (preCondition instanceof GoldCondition) {
                Integer tmp = (preConditionParameters==null ? null:(Integer)preConditionParameters.get("minimum"));
                if (tmp==null || tmp>0) {
                    if (first instanceof ActionPlan &&
                        ((ActionPlan)first).getAction() instanceof ResourceLocation) {
                        ResourceLocation a = (ResourceLocation)(((ActionPlan)first).getAction());
                        Entity e = gs1.getEntityAt(a.getCoor());
                        if (e!=null && e instanceof WGoldMine) return 1.0;
                    }
                }
            } else if (preCondition instanceof EntitiesNeededToBuildType) {
                List<Class> needed = new LinkedList<Class>();
                if (first instanceof ActionPlan &&
                    ((ActionPlan)first).getAction() instanceof Build &&
                    second instanceof ActionPlan &&
                    ((ActionPlan)second).getAction() instanceof Build) {
                    Build a1 = (Build)(((ActionPlan)first).getAction());
                    Build a2 = (Build)(((ActionPlan)second).getAction());
                    if (a2.getType() == WBarracks.class) {
                        needed.add(WTownhall.class);
                    }
                    if (a2.getType() == WBlacksmith.class) {
                        needed.add(WTownhall.class);
                    }
                    if (a2.getType() == WLumberMill.class) {
                        needed.add(WTownhall.class);
                    }
                    if (a2.getType() == WFortress.class) {
                        needed.add(WBarracks.class);
                        needed.add(WLumberMill.class);
                        needed.add(WBlacksmith.class);
                    }
                    if (a2.getType() == WStable.class) {
                        needed.add(WFortress.class);
                    }
                    if (a2.getType() == WTower.class) {
                        needed.add(WLumberMill.class);
                    }
                    for(Class c:needed) {
                        if (a1.getType()==c) {
                            List<Entity> l = gs2.getEntityByTypeAndOwner(c, a2.getPlayerID());
                            if (l==null || l.isEmpty()) return 1.0;
                        }
                    }
                }
            } else if (preCondition instanceof EntityTypeExists) {
                Class type = (preConditionParameters==null ? null:(Class)preConditionParameters.get("type"));
                if (first instanceof ActionPlan &&
                    ((ActionPlan)first).getAction() instanceof Train) {
                    Train a1 = (Train)((ActionPlan)first).getAction();
                    if (type==null || a1.getType() == type) return 1.0;
                }
                if (first instanceof ActionPlan &&
                    ((ActionPlan)first).getAction() instanceof Build) {
                    Build a1 = (Build)((ActionPlan)first).getAction();
                    if (type==null || a1.getType() == type) return 1.0;
                }
            }
        } else {
            // precondition == null
            // Here encode ONLY those conditions that are not encoded as pre/postcondition matching:
            if (second instanceof ActionPlan &&
                first instanceof ActionPlan) {
                Action a1 = ((ActionPlan)first).getAction();
                Action a2 = ((ActionPlan)second).getAction();
                
                if (a1.getEntityID().equals(a2.getEntityID())) return 1.0;
            }
            if (second instanceof ActionPlan &&
                       ((ActionPlan)second).getAction() instanceof Build) {
                if (first instanceof ActionPlan &&
                    ((ActionPlan)first).getAction() instanceof Train &&
                    ((Train)(((ActionPlan)first).getAction())).getType() == WPeasant.class) {
                    return 1.0;
                }
                if (first instanceof ActionPlan &&
                    ((ActionPlan)first).getAction() instanceof Build) {
                    Build a1 = (Build)((ActionPlan)first).getAction();
                    Build a2 = (Build)((ActionPlan)second).getAction();
                    if (a2.getType() == WBarracks.class &&
                        a1.getType() == WTownhall.class) return 1.0;
                    if (a2.getType() == WLumberMill.class &&
                        a1.getType() == WTownhall.class) return 1.0;
                    if (a2.getType() == WBlacksmith.class &&
                        a1.getType() == WTownhall.class) return 1.0;
                    if (a2.getType() == WTower.class &&
                        a1.getType() == WLumberMill.class) return 1.0;
                    if (a2.getType() == WFortress.class &&
                        (a1.getType() == WLumberMill.class ||
                         a1.getType() == WBlacksmith.class ||
                         a1.getType() == WBarracks.class)) return 1.0;
                    if (a2.getType() == WStable.class &&
                        a1.getType() == WFortress.class) return 1.0;
                }
            } else if (second instanceof ActionPlan &&
                       ((ActionPlan)second).getAction() instanceof Train) {
                Train a2 = (Train)((ActionPlan)second).getAction();
                if (first instanceof ActionPlan &&
                    ((ActionPlan)first).getAction() instanceof Build) {
                    Build a1 = (Build)((ActionPlan)first).getAction();
                    if (a2.getType() == WPeasant.class &&
                        a1.getType() == WTownhall.class) return 1.0;
                    if (a2.getType() != WPeasant.class &&
                        a1.getType() == WBarracks.class) return 1.0;
                    if (a2.getType() == WArcher.class &&
                        a1.getType() == WLumberMill.class) return 1.0;
                    if (a2.getType() == WCatapult.class &&
                        (a1.getType() == WLumberMill.class ||
                         a1.getType() == WBlacksmith.class)) return 1.0;
                    if (a2.getType() == WKnight.class &&
                        (a1.getType() == WFortress.class ||
                         a1.getType() == WStable.class)) return 1.0;
                }
            } else if (second instanceof ActionPlan &&
                       ((ActionPlan)second).getAction() instanceof ResourceLocation) {
                if (first instanceof ActionPlan &&
                    ((ActionPlan)first).getAction() instanceof Train &&
                    ((Train)(((ActionPlan)first).getAction())).getType() == WPeasant.class) {
                    return 1.0;
                }
            } else if (second instanceof ActionPlan &&
                       ((ActionPlan)second).getAction() instanceof Attack) {
                Attack a2 = (Attack)((ActionPlan)second).getAction();
                Entity e2 = gs2.getEntity(a2.getEntityID());
                if (e2!=null &&
                    first instanceof ActionPlan &&
                    ((ActionPlan)first).getAction() instanceof Train &&
                    ((Train)(((ActionPlan)first).getAction())).getType() == e2.getClass()) {
                    return 1.0;
                }
            } else if (second instanceof GoalPlan) {
                Sensor goal2 = ((GoalPlan)second).getGoal();
                if (goal2 instanceof WinGoal) return 1.0;    // everything matches with win goal
                if (first instanceof ActionPlan &&
                    ((ActionPlan)first).getAction() instanceof Build) {
                    Build a1 = (Build)((ActionPlan)first).getAction();

                    if (a1.getType() == WBarracks.class && goal2 instanceof HaveWBarracks) return 1.0;
                    if (a1.getType() == WBlacksmith.class && goal2 instanceof HaveWBlacksmith) return 1.0;
                    if (a1.getType() == WLumberMill.class && goal2 instanceof HaveWLumberMill) return 1.0;
                    if (a1.getType() == WStable.class && goal2 instanceof HaveWStable) return 1.0;
                    if (a1.getType() == WTower.class && goal2 instanceof HaveWTower) return 1.0;
                    if (a1.getType() == WTownhall.class && goal2 instanceof HaveWTownhall) return 1.0;
                    if (a1.getType() == WFortress.class && goal2 instanceof HaveWFortress) return 1.0;
                }
                if (first instanceof ActionPlan &&
                    ((ActionPlan)first).getAction() instanceof Train) {
                    Train a1 = (Train)((ActionPlan)first).getAction();

                    if (a1.getType() == WPeasant.class && goal2 instanceof HaveWPeasant) return 1.0;
                    if (a1.getType() == WFootman.class && goal2 instanceof HaveWFootman) return 1.0;
                    if (a1.getType() == WArcher.class && goal2 instanceof HaveWArcher) return 1.0;
                    if (a1.getType() == WCatapult.class && goal2 instanceof HaveWCatapult) return 1.0;
                    if (a1.getType() == WKnight.class && goal2 instanceof HaveWKnight) return 1.0;
                }
            }
        }

        return 0.0;
	}

}
