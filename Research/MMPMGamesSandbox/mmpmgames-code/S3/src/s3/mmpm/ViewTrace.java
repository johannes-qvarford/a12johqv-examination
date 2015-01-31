package s3.mmpm;

import d2.execution.adaptation.ExpandConditionMatcher;
import d2.plans.PlanDependencyGraph;
import d2.execution.planbase.PlanBaseEntry;
import d2.execution.planbase.PlayerGameState;
import d2.plans.PetriNetPlan;
import d2.util.plancreator.PetriNetHelper;
import d2.util.planvisualizer.PlanVisualizer;
import d2.util.planvisualizer.SimplifiedPlanVisualizer;
import java.util.LinkedList;
import java.util.List;

import gatech.mmpm.Entry;
import gatech.mmpm.GameState;
import gatech.mmpm.IDomain;
import gatech.mmpm.Trace;
import gatech.mmpm.tools.Trainer;
import d2.util.statevisualizer.PotentialFieldsVisualizer;
import d2.util.statevisualizer.StateSequenceVisualizer;
import d2.util.statevisualizer.TraceVisualizer;
import gatech.mmpm.Action;
import gatech.mmpm.Entity;
import gatech.mmpm.util.Pair;
import java.util.HashMap;
import s3.mmpm.d2.S3ConditionMatcher;
import s3.mmpm.goals.WinGoal;

public class ViewTrace {
	
	public static void main(String args[]) throws Exception
	{
		IDomain domain = new S3Domain();
		
		long ta = System.currentTimeMillis();
		Trace t = Trainer.parseTraceFromFile("catapult-vs-knights-islands2.xml", domain);
		System.out.println("Time taken to load trace: " + (System.currentTimeMillis()-ta));
        long max_actions = 25;

        // View trace:
//		new TraceVisualizer(t,800,600);
//		new PotentialFieldsVisualizer(gsL.get(2),"P3",800,600);

        /*
        // Print all the actions in the trace:
        System.out.println("\n\nFull trace:");
        for(Entry entry:t.getEntries()) {
            for(Action a:entry.getActions()) {
                System.out.println(entry.getTimeStamp() + " - " + a);
            }
        }
         */

        // Generate a petrinet plan with the actions of each player:
        HashMap<String,List<Pair<PlayerGameState,Action>>> actionLists = new HashMap<String,List<Pair<PlayerGameState,Action>>>();
        for(Entry entry:t.getEntries()) {
            for(Action a:entry.getActions()) {
                List<Pair<PlayerGameState,Action>> l = actionLists.get(a.getPlayerID());
                if (l==null) {
                    l = new LinkedList<Pair<PlayerGameState,Action>>();
                    actionLists.put(a.getPlayerID(), l);
                }
                l.add(new Pair<PlayerGameState,Action>(new PlayerGameState(entry.getGameState(), entry.getTimeStamp(), a.getPlayerID()),a));
            }
        }

        ExpandConditionMatcher ecm = new ExpandConditionMatcher(new S3ConditionMatcher());
//        for(String player:actionLists.keySet())
        {
            String player = "player1";

            List<Pair<PlayerGameState,Action>> l = actionLists.get(player);

            while(l.size()>max_actions) l.remove(l.size()-1);

            PlanBaseEntry p = PetriNetHelper.createPlanBaseEntryFromActionSequence(l, new WinGoal(), player);
            PlanDependencyGraph pdg = new PlanDependencyGraph(p.m_plan, ecm, t.getEntries().get(0).getTimeStamp(), t.getEntries().get(0).getGameState(), player);
            System.out.println("Dependency graph for player " + player);
            pdg.print();

            PlanBaseEntry pbe = pdg.generateFlexiblePlan(l.get(0)._a, new s3.mmpm.goals.WinGoal());
            PetriNetPlan pnp = pbe.m_plan;

            new SimplifiedPlanVisualizer("Plan Visualizer", pnp, 800,600, pnp.getPetriNetElement(pbe.m_entryPoint.keySet().iterator().next()));
        }


	}


}
