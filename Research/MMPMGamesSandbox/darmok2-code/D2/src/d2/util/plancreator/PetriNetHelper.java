/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.util.plancreator;

import d2.plans.PlanDependencyGraph;
import d2.execution.planbase.PlanBaseEntry;
import d2.execution.planbase.PlayerGameState;
import d2.plans.ActionPlan;
import d2.plans.DummyState;
import d2.plans.DummyTransition;
import d2.plans.FailureTransition;
import d2.plans.PetriNetPlan;
import d2.plans.Plan;
import d2.plans.PlanPreconditionFailedState;
import d2.plans.PlanState;
import d2.plans.PreFailureTransition;
import d2.plans.PreTransition;
import d2.plans.State;
import d2.plans.SuccessTransition;
import d2.plans.Transition;
import d2.util.planvisualizer.PlanVisualizer;
import gatech.mmpm.Action;
import gatech.mmpm.GameState;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.composite.AndCondition;
import gatech.mmpm.sensor.constant.False;
import gatech.mmpm.sensor.constant.True;
import gatech.mmpm.util.Pair;
import java.util.HashMap;
import java.util.List;

public class PetriNetHelper {

    public static PetriNetPlan createPetriNetPlanForAction(Action action,
        Sensor pre_condition, DummyState head_ds, DummyState next_ds,
        GameState gs, String player) {
        PetriNetPlan pnp = new PetriNetPlan();
        ActionPlan ap = new ActionPlan();
        ap.setOriginalGameState(gs);
        ap.setOriginalPlayer(player);
        // Set the action in the action plan
        ap.setAction(action);
        // System.out.println("Added action " + act + " to plan.");
        // hav to and the pre_condition with the action's pre_condition
        AndCondition act_pre_condition = new AndCondition();
        act_pre_condition.addChild(pre_condition);
        act_pre_condition.addChild(action.getPreCondition());
        // now, create the plan state for that action plan
        PlanState ps = new PlanState();
        ps.setPlan(ap);
        // now the transitions
        // first we create the pre transition from the token dummy state
        // (token_ds)
        PreTransition from_token_to_plan_state = new PreTransition(ap);
        from_token_to_plan_state.addNextState(1, ps);
        head_ds.addNextTransition(1, from_token_to_plan_state);
        // next we create the success transition from plan state to the next
        // dummy state (next_ds)
        SuccessTransition from_plan_state_to_next_dummy = new SuccessTransition(
            ap);
        from_plan_state_to_next_dummy.addNextState(1, next_ds);
        ps.addNextTransition(1, from_plan_state_to_next_dummy);
        // finally we deal with the failure transition, which requires that
        // we create a new "failure" dummy state
        DummyState fs = new DummyState();
        FailureTransition from_plan_state_to_failure_state = new FailureTransition(
            ap);
        from_plan_state_to_failure_state.addNextState(1, fs);
        ps.addNextTransition(1, from_plan_state_to_failure_state);
        // add all the action specific elements to pnp
        pnp.addPetriNetElement(from_token_to_plan_state); // pre_transition
        pnp.addPetriNetElement(ps); // plan state
        pnp.addPetriNetElement(from_plan_state_to_failure_state); // failure_transition
        pnp.addPetriNetElement(fs); // failure state
        pnp.addPetriNetElement(from_plan_state_to_next_dummy);
        return null;
    }

    public static PetriNetPlan createEmptyPetriNetPlan(GameState gs, String player) {
        PetriNetPlan pnp = new PetriNetPlan();
        pnp.setPreCondition(new True());
        pnp.setFailureCondition(new False()); // can't fail, for now
        pnp.setSuccessCondition(new True());
        pnp.setOriginalGameState(gs);
        pnp.setOriginalPlayer(player);

        return pnp;
    }

    /**
     * Creates all the states and transitions needed to have an action in a petrinet:
     *
     * @param pnp
     * @param head_ds
     * @param actions
     * @param next_ds
     * @param pre_condition
     */
    public static void handlePlanBlock(PetriNetPlan pnp, State head_ds,
        Plan p, State next_ds, Sensor pre_condition,
        GameState gs, String player) {

        if (p == null) {
            System.err.println("handlePlanBlock: null p!!!");
        }

        // now handle the actions block
        // first, create the action plan
        p.setOriginalGameState(gs);
        p.setOriginalPlayer(player);

        // System.out.println("Added action " + act + " to plan.");

        // havta and the pre_condition with the action's pre_condition
        Sensor act_pre_condition = null;
        if (pre_condition!=null) {
            act_pre_condition = new AndCondition();
            ((AndCondition)act_pre_condition).addChild(pre_condition);
            ((AndCondition)act_pre_condition).addChild(p.getPreCondition());
        } else {
            act_pre_condition = p.getPreCondition();
        }

        // now, create the plan state for that action plan
        PlanState ps = new PlanState();
        ps.setPlan(p);

        // now the transitions

        // first we create the pre transition from the token dummy state
        // (token_ds)
        PreTransition from_token_to_plan_state = new PreTransition(p);
        from_token_to_plan_state.addNextState(1, ps);
        head_ds.addNextTransition(1, from_token_to_plan_state);

        // next we create the success transition from plan state to the next
        // dummy state (next_ds)
        SuccessTransition from_plan_state_to_next_dummy = new SuccessTransition(p);
        from_plan_state_to_next_dummy.addNextState(1, next_ds);
        ps.addNextTransition(1, from_plan_state_to_next_dummy);

        // finally we deal with the failure transition, which requires that
        // we create a new "failure" dummy state
        // But we only do it if the action has a non-empty Failure Condition
        if (!(p.getFailureCondition() instanceof False)) {
            DummyState fs = new DummyState("FAILURESTATE");
            FailureTransition from_plan_state_to_failure_state = new FailureTransition(p);
            from_plan_state_to_failure_state.addNextState(1, fs);
            ps.addNextTransition(1, from_plan_state_to_failure_state);
            pnp.addPetriNetElement(from_plan_state_to_failure_state); // failure_transition
            pnp.addPetriNetElement(fs); // failure state
        }

        // If there is a preFailureCondition, add it:
        if (p instanceof ActionPlan
            && !(((ActionPlan) p).getPreFailureCondition() instanceof False)) {
            PlanPreconditionFailedState fs = new PlanPreconditionFailedState(p);
            Transition from_dummy_state_to_failure_state = new PreFailureTransition(p);
            from_dummy_state_to_failure_state.addNextState(1, fs);
            head_ds.addNextTransition(1, from_dummy_state_to_failure_state);
            pnp.addPetriNetElement(from_dummy_state_to_failure_state); // failure_transition
            pnp.addPetriNetElement(fs); // failure state

//			System.out.println("Creating preFailureCondition!!!!!!!!!!!!!!!!!!!!!! for " + p);
        }

        // add all the action specific elements to pnp
        pnp.addPetriNetElement(from_token_to_plan_state); // pre_transition
        pnp.addPetriNetElement(ps); // plan state
        pnp.addPetriNetElement(from_plan_state_to_next_dummy); // success_transition
    }

    public static PlanBaseEntry createPlanBaseEntryFromActionSequence(List<Pair<PlayerGameState, Action>> l, Sensor goal, String player) {
        PetriNetPlan pnp = PetriNetHelper.createEmptyPetriNetPlan(l.get(0)._a.gs, player);
        DummyState petrinet_head = null;
        DummyState head_ds = new DummyState(); // each "block" has a
        petrinet_head = head_ds;

        for (Pair<PlayerGameState, Action> gs_action : l) {
            Sensor pre_condition = new True();
            DummyState next_ds = new DummyState();
            pnp.addPetriNetElement(head_ds); // we're done with head_ds
            pnp.addPetriNetElement(next_ds); // we're done with head_ds
            PetriNetHelper.handlePlanBlock(pnp, head_ds, new ActionPlan(gs_action._b), next_ds,
                pre_condition, gs_action._a.gs, player);
            head_ds = next_ds;
        }

        PlanBaseEntry pbe = new PlanBaseEntry(pnp, goal, l.get(0)._a.gs, l.get(0)._a.cycle, player);
        HashMap<String, Integer> tokensInStates = new HashMap<String, Integer>();

        // Set only one entry point, which is the beginning of the petri net:
        tokensInStates.put(petrinet_head.getElementID(), 1);
        pbe.setEntryPoint(tokensInStates);

        return pbe;
    }

    public static PlanBaseEntry createPlanBaseEntryFromPlanSequence(List<Pair<PlayerGameState, Plan>> l, Sensor goal, String player) {
        PetriNetPlan pnp = PetriNetHelper.createEmptyPetriNetPlan(l.get(0)._a.gs, player);
        DummyState petrinet_head = null;
        DummyState head_ds = new DummyState(); // each "block" has a
        petrinet_head = head_ds;

        for (Pair<PlayerGameState, Plan> gs_plan : l) {
            Sensor pre_condition = new True();
            DummyState next_ds = new DummyState();
            pnp.addPetriNetElement(head_ds); // we're done with head_ds
            pnp.addPetriNetElement(next_ds); // we're done with head_ds
            PetriNetHelper.handlePlanBlock(pnp, head_ds, gs_plan._b, next_ds,
                pre_condition, gs_plan._a.gs, player);
            head_ds = next_ds;
        }

        PlanBaseEntry pbe = new PlanBaseEntry(pnp, goal, l.get(0)._a.gs, l.get(0)._a.cycle, player);
        HashMap<String, Integer> tokensInStates = new HashMap<String, Integer>();

        // Set only one entry point, which is the beginning of the petri net:
        tokensInStates.put(petrinet_head.getElementID(), 1);
        pbe.setEntryPoint(tokensInStates);

        return pbe;
    }

    public static PlanBaseEntry createPlanBaseEntryFromParallelPlans(List<Pair<PlayerGameState, Plan>> l, Sensor goal, String player) {
        PetriNetPlan pnp = PetriNetHelper.createEmptyPetriNetPlan(l.get(0)._a.gs, player);
        DummyState head_ds = new DummyState();
        DummyTransition head_ts = new DummyTransition();
        DummyState terminal_ds = new DummyState();
        pnp.addPetriNetElement(head_ds);
        pnp.addPetriNetElement(head_ts);
        pnp.addPetriNetElement(terminal_ds);
        head_ds.addNextTransition(1, head_ts);

        for (Pair<PlayerGameState, Plan> gs_plan : l) {
            gs_plan._b.setOriginalGameState(gs_plan._a.gs);
            gs_plan._b.setOriginalPlayer(player);
            PlanState pState = new PlanState();
            pnp.addPetriNetElement(pState);
            pState.setPlan(gs_plan._b);
            DummyState failState = new DummyState();
            pnp.addPetriNetElement(failState);
            PreTransition preTrans = new PreTransition(gs_plan._b);
            pnp.addPetriNetElement(preTrans);
            FailureTransition failTrans = new FailureTransition(gs_plan._b);
            pnp.addPetriNetElement(failTrans);
            SuccessTransition succTrans = new SuccessTransition(gs_plan._b);
            pnp.addPetriNetElement(succTrans);

            DummyState line_head_ds = new DummyState();
            pnp.addPetriNetElement(line_head_ds);
            head_ts.addNextState(1, line_head_ds);

            line_head_ds.addNextTransition(1, preTrans);
            preTrans.addNextState(1, pState);

            pState.addNextTransition(1, failTrans);
            failTrans.addNextState(1, failState);
            pState.addNextTransition(1, succTrans);
            succTrans.addNextState(1, terminal_ds);
        }

        PlanBaseEntry pbe = new PlanBaseEntry(pnp, goal, l.get(0)._a.gs, l.get(0)._a.cycle, player);
        HashMap<String, Integer> tokensInStates = new HashMap<String, Integer>();

        // Set only one entry point, which is the beginning of the petri net:
        tokensInStates.put(head_ds.getElementID(), 1);
        pbe.setEntryPoint(tokensInStates);

//                new PlanVisualizer(pnp, 800,600, 1, head_ds);

        return pbe;
    }


    public static PlanBaseEntry createPlanBaseEntryFromDependencyGraph(PlanDependencyGraph g, Sensor goal, PlayerGameState pgs) throws Exception {
        PetriNetPlan pnp = PetriNetHelper.createEmptyPetriNetPlan(pgs.gs, pgs.player);

        g.checkIntegrity();

        // create a dummy starting point:
        DummyState head_ds = new DummyState();
        pnp.addPetriNetElement(head_ds);
        DummyTransition head_t = new DummyTransition();
        pnp.addPetriNetElement(head_t);
        head_ds.addNextTransition(1, head_t);
        HashMap<Plan,Pair<DummyState,SuccessTransition>> planTable = new HashMap<Plan,Pair<DummyState,SuccessTransition>>();

        // create one block for each action:
        for(Plan p:g.m_subPlans) {
            PlanState pState = new PlanState();
            pnp.addPetriNetElement(pState);
            pState.setPlan(p);
            DummyState failState = new DummyState();
            pnp.addPetriNetElement(failState);
            PlanPreconditionFailedState preFailState = new PlanPreconditionFailedState(p);
            pnp.addPetriNetElement(preFailState);
            PreTransition preTrans = new PreTransition(p);
            pnp.addPetriNetElement(preTrans);
            PreFailureTransition preFTrans = new PreFailureTransition(p);
            pnp.addPetriNetElement(preFTrans);
            FailureTransition failTrans = new FailureTransition(p);
            pnp.addPetriNetElement(failTrans);
            SuccessTransition succTrans = new SuccessTransition(p);
            pnp.addPetriNetElement(succTrans);

            DummyState action_head_ds = new DummyState();
            pnp.addPetriNetElement(action_head_ds);

            action_head_ds.addNextTransition(1, preTrans);
            preTrans.addNextState(1, pState);
            action_head_ds.addNextTransition(1, preFTrans);
            preFTrans.addNextState(1, preFailState);

            planTable.put(p,new Pair<DummyState,SuccessTransition>(action_head_ds,succTrans));

            pState.addNextTransition(1, failTrans);
            failTrans.addNextState(1, failState);
            pState.addNextTransition(1, succTrans);
        }

        // Create all the dependencies:
        for(Plan p:g.m_subPlans) {
            List<PlanDependencyGraph.Dependency> l = g.dependencies(p);
            if (l.isEmpty()) {
                head_t.addNextState(1, planTable.get(p)._a);
            } else {
                for(PlanDependencyGraph.Dependency d:l) {
                    planTable.get(d.m_previousPlan)._b.addNextState(1, planTable.get(p)._a);
                }
            }
        }

        PlanBaseEntry pbe = new PlanBaseEntry(pnp, goal, pgs.gs, pgs.cycle, pgs.player);
        HashMap<String, Integer> tokensInStates = new HashMap<String, Integer>();

        // Set only one entry point, which is the beginning of the petri net:
        tokensInStates.put(head_ds.getElementID(), 1);
        pbe.setEntryPoint(tokensInStates);

        return pbe;
    }
}
