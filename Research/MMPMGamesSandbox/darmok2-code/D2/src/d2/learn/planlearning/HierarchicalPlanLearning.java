/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.learn.planlearning;

import d2.core.D2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import d2.execution.planbase.PlanBase;
import d2.execution.planbase.PlanBaseEntry;
import d2.execution.planbase.PlayerGameState;
import d2.plans.ActionPlan;
import d2.plans.DummyState;
import d2.plans.GoalPlan;
import d2.plans.PetriNetPlan;
import d2.plans.Plan;
import gatech.mmpm.util.Pair;
import d2.util.plancreator.PetriNetHelper;
import d2.worldmodel.WorldModel;
import gatech.mmpm.Action;
import gatech.mmpm.Entity;
import gatech.mmpm.Entry;
import gatech.mmpm.GameState;
import gatech.mmpm.IDomain;
import gatech.mmpm.Trace;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.SensorLibrary;
import gatech.mmpm.sensor.constant.True;
import java.util.HashSet;

public class HierarchicalPlanLearning extends PlanLearning {

    public static int DEBUG = 0;
    static boolean showUnits = false;
    Pair<Sensor, List<List<Pair<PlayerGameState, Plan>>>>[] _goalActionList;

    public HierarchicalPlanLearning(IDomain domain) {
        super(domain);
        if (DEBUG>=1) System.out.println("Goal Count :" + SensorLibrary.getGoalCount());

        _goalActionList = (new Pair[SensorLibrary.getGoalCount()]);
        for (int goalIndex = 0; goalIndex < SensorLibrary.getGoalCount(); ++goalIndex) {
            _goalActionList[goalIndex] = new Pair<Sensor, List<List<Pair<PlayerGameState, Plan>>>>(
                    SensorLibrary.getGoal(goalIndex),
                    new ArrayList<List<Pair<PlayerGameState, Plan>>>());
        }

    }

    public void printActionList() {
        for (int i = 0; i < _goalActionList.length; i++) {
            for (List<Pair<PlayerGameState, Plan>> actionList1 : _goalActionList[i]._b) {
                System.out.print(_goalActionList[i]._a + " (" + actionList1.size() + ") -> ");
                for (Pair<PlayerGameState, Plan> gs_action : actionList1) {
                    Plan action = gs_action._b;
                    System.out.print(action);

                    // Print the existing units in the game:
                    if (showUnits) {
                        System.out.print("[ ");
                        for (Entity e : gs_action._a.gs.getAllEntities()) {
                            System.out.print(e.getentityID() + " ");
                        }
                        System.out.print("]");
                    }

                    System.out.print("\n");
                }
                System.out.println("\n");

            }
        }
    }

    public List<PlanBaseEntry> learnFromTrace(Trace t, String playerID, WorldModel wm) throws Exception {

        if (DEBUG>=1) System.out.println("HierarchicalPlanLearning.learnFromTrace: " + t.getEntries().size() + " entries, learning from player " + playerID);
//		System.out.println("First state:");
//		System.out.println(t.getEntries().get(0).toString());
//		System.out.println("Last state:");
//		System.out.println(t.getEntries().get(t.getEntries().size()-1).toString());

        t.cleanUpAbortedActions();

        createActionLists(t,playerID);

        if (DEBUG>=2) {
            System.out.println("Before subgoal substitution:");
            printActionList();
        }

        findSubstitutions(playerID);

        if (DEBUG>=2) {
            System.out.println("After subgoal substitution:");
           printActionList();
        }
        return createIndividualPetriNets(playerID);
    }

    public void createActionLists(Trace t, String playerID)
    {
        List<Entry> entries = t.getEntries();
        HashMap<Action,Plan> actionPlanMap = new HashMap<Action,Plan>();

        List<Pair<PlayerGameState, Plan>> temporaryActionList[] = new LinkedList[SensorLibrary.getGoalCount()];
        boolean completedg[][] = new boolean[SensorLibrary.getGoalCount()][entries.size()];
        for (int i = 0; i < completedg.length; i++) {
            temporaryActionList[i] = new LinkedList<Pair<PlayerGameState, Plan>>();
            for (int j = 0; j < completedg[i].length; j++) {
                completedg[i][j] = true;
            }
        }

        int j = 0;
        for (Entry entry : entries) {

            if (DEBUG>=2) System.out.println("Entry: ----- (" + entry.getActions().size() + " actions)");
            GameState gs = entry.getGameState();
            ArrayList<Action> actions = entry.getActions();
            List<Action> playerActions = new LinkedList<Action>();
            // only add actions that are for the current player.
            for (Action action : actions) {
                if (DEBUG>=2) System.out.println("Action :" + action);
                if (action.getPlayerID().equals(playerID)) {
                    playerActions.add(action);
                }
            }

            int i = 0;
            for (Pair<Sensor, List<List<Pair<PlayerGameState, Plan>>>> i_goalActionList : _goalActionList) {
                Float value = (Float) i_goalActionList._a.evaluate(entry.getTimeStamp(), gs, playerID);
                // Force the end of the game to be a win for the player who's closest to winning:
                if (i_goalActionList._a.getClass().getSimpleName().equals("WinGoal")
                        && j == entries.size() - 1) {
                    if (value >= 0.5) {
                        value = 1.0f;
                    }
                }
                if (DEBUG>=2) System.out.println("Condition: " + i_goalActionList._a.getClass().getName() + " : " + value);
                if (value >= Sensor.BOOLEAN_TRUE_THRESHOLD) {
                    // goal is satisfied:
                    completedg[i][j] = true;
                    if (temporaryActionList[i].size() > 0) {
                        i_goalActionList._b.add(temporaryActionList[i]);
                        temporaryActionList[i] = new LinkedList<Pair<PlayerGameState, Plan>>();
                    }
                } else {
                    // we found a new gamestate where this goal is not satisfied:
                    completedg[i][j] = false;

                    for (Action a : playerActions) {
                        Plan p = actionPlanMap.get(a);
                        if (p==null) {
                            p = new ActionPlan(a);
                            p.setOriginalGameState(entry.getGameState());
                            actionPlanMap.put(a,p);
                        }
                        temporaryActionList[i].add(new Pair<PlayerGameState, Plan>(new PlayerGameState(entry.getGameState(), entry.getTimeStamp(), playerID), p));
                    }
                }
                // Add to end of the list
                i++;
            }
            j++;
        }
    }

    void findSubstitutions(String playerName) {
        List<Object> substitutions = new LinkedList<Object>();

        for (int g1 = 0; g1 < _goalActionList.length; g1++) {
            List<List<Pair<PlayerGameState, Plan>>> g1_actionLists = _goalActionList[g1]._b;
            for (List<Pair<PlayerGameState, Plan>> g1_actionList : g1_actionLists) {
                List<Pair<PlayerGameState, Plan>> candidate = null;
                int candidateG = -1;
                if (DEBUG>=1) System.out.println("Processing: " + g1_actionList.size() + " - " + _goalActionList[g1]._a);

                for (int g2 = 0; g2 < _goalActionList.length; g2++) {
                    List<List<Pair<PlayerGameState, Plan>>> g2_actionLists = _goalActionList[g2]._b;
                    if (g1 == g2) {
                        continue;
                    }
                    for (List<Pair<PlayerGameState, Plan>> g2_actionList : g2_actionLists) {
                        if (isSubGoal(g1_actionList, g2_actionList)) {
                            if (DEBUG>=1) System.out.println("Candidate: " + g2_actionList.size() + " - " + _goalActionList[g2]._a);
                            if (candidate==null || g2_actionList.size()>candidate.size()) {
                                candidate = g2_actionList;
                                candidateG = g2;
                            }
                        }
                    }
                }

                if (candidate!=null) {
                    List<Pair<PlayerGameState, Plan>> tmp = new LinkedList<Pair<PlayerGameState, Plan>>();
                    tmp.addAll(candidate);
                    substitutions.add(new Pair<Object,Object>(g1_actionList,new Pair<Object,Integer>(tmp,candidateG)));
                }
            }
        }

        for(Object tmp:substitutions) {
            List<Pair<PlayerGameState, Plan>> g1_actionList = (List<Pair<PlayerGameState, Plan>>)((Pair<Object,Object>)tmp)._a;
            Pair<List<Pair<PlayerGameState, Plan>>,Integer> tmp2 = (Pair<List<Pair<PlayerGameState, Plan>>,Integer>)((Pair<Object,Object>)tmp)._b;
            List<Pair<PlayerGameState, Plan>> candidate = tmp2._a;
            int candidateG = tmp2._b;
            int firstIndex = -1;
            GameState firstGS = null;
            int firstCycle = 0;

//			System.out.println("A sequence of size " + g2_actionList.m_b.size() + " is a subsequence of another one of " + g1_actionList.m_b.size());

            Pair<PlayerGameState, Plan> a = candidate.get(0);
            int i = 0;
            for (Pair<PlayerGameState, Plan> b : g1_actionList) {
                if (a._b instanceof ActionPlan &&
                    b._b instanceof ActionPlan) {
                    if (((ActionPlan) a._b).getAction().equals(((ActionPlan) b._b).getAction())) {
                        firstIndex = i;
                        firstGS = a._a.gs;
                        firstCycle = a._a.cycle;
                        break;
                    }
                }
                i++;
            }

            if (DEBUG>=1) System.out.println("Before substitution: plan size: " + g1_actionList.size() + " , candidate size: " + candidate.size());
            for (Pair<PlayerGameState, Plan> b : candidate) {
                Pair<PlayerGameState, Plan> found = null;
                for(Pair<PlayerGameState, Plan> a2:g1_actionList) {
                    if (a2._b instanceof ActionPlan &&
                        b._b instanceof ActionPlan) {
                        if (((ActionPlan) a2._b).getAction().equals(((ActionPlan) b._b).getAction())) {
                            found = a2;
                            break;
                        }
                    }
                }
                if (found!=null) {
                    g1_actionList.remove(found);
                } else {
                    System.err.println("Action not found!!!");
                }
            }
            if (DEBUG>=1) System.out.println("After substitution: plan size: " + g1_actionList.size());

//			System.out.println("index: " + index + " (firstIndex: " + firstIndex + ")");
            g1_actionList.add(firstIndex, new Pair<PlayerGameState, Plan>(new PlayerGameState(firstGS, firstCycle, playerName), new GoalPlan(_goalActionList[candidateG]._a)));
        }


    }

    // seta is the big one, and setb is the small one
    public boolean isSubGoal(List<Pair<PlayerGameState, Plan>> seta, List<Pair<PlayerGameState, Plan>> setb) {
        HashSet<Action> actionhash = new HashSet<Action>();
        if (seta.size() <= setb.size()) {   // we don't consider two plans with the same action to be subgoal of each other
            return false;
        }

        // Set B can be subset of set A
        for (Pair<PlayerGameState, Plan> p : seta) {
            if (p._b instanceof ActionPlan) {
                Action action = ((ActionPlan) p._b).getAction();
                actionhash.add(action);
            }
        }
        for (Pair<PlayerGameState, Plan> p : setb) {
            if (p._b instanceof ActionPlan) {
                Action action = ((ActionPlan) p._b).getAction();
                if (!actionhash.contains(action)) return false;
            } else {
                return false;
            }
        }
        return true;
    }

    List<PlanBaseEntry> createIndividualPetriNets(String playerName) throws Exception {
        List<PlanBaseEntry> learntPlans = new LinkedList<PlanBaseEntry>();
        for (int i = 0; i < _goalActionList.length; i++) {
            // This is the SubGoal
            Sensor m_goal = _goalActionList[i]._a;
            for (List<Pair<PlayerGameState, Plan>> actionList1 : _goalActionList[i]._b) {
                // actionList1.m_a: This is the GameState
                // actionList1.m_b: List of actions
                // For each different GameState, create a new PetriNetPlan for
                // that subgoal
                GameState gamestate_head = actionList1.get(0)._a.gs;
                int cycle_head = actionList1.get(0)._a.cycle;
                PetriNetPlan pnp = PetriNetHelper.createEmptyPetriNetPlan(gamestate_head, playerName);
                DummyState petrinet_head = null;
                DummyState head_ds = new DummyState(); // each "block" has a
                // head dummy
                petrinet_head = head_ds;

                for (Pair<PlayerGameState, Plan> gs_action : actionList1) {
                    Sensor pre_condition = new True();
                    DummyState next_ds = new DummyState();
                    pnp.addPetriNetElement(head_ds); // we're done with head_ds
                    pnp.addPetriNetElement(next_ds); // we're done with head_ds
                    PetriNetHelper.handlePlanBlock(pnp, head_ds, gs_action._b, next_ds,
                            pre_condition, gs_action._a.gs, playerName);
                    head_ds = next_ds;
                }

                PlanBaseEntry pbe = new PlanBaseEntry(pnp, m_goal, gamestate_head, cycle_head, playerName);
                HashMap<String, Integer> tokensInStates = new HashMap<String, Integer>();

                // Set only one entry point, which is the beginning of the petri
                // net:
                tokensInStates.put(petrinet_head.getElementID(), 1);
                pbe.setEntryPoint(tokensInStates);
                if (pnp.getPetriNetElements().size() != 0) {
                    learntPlans.add(pbe);
                }

            }

            // Remove the action lists, so that we don't create behaviors twice when more than one trace is used
            _goalActionList[i]._b.clear();
        }

        return learntPlans;
    }
}
