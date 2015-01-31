/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.execution.adaptation;

import d2.worldmodel.ConditionMatcher;
import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.composite.AndCondition;

import java.util.LinkedList;
import java.util.List;

import d2.plans.Plan;
import gatech.mmpm.GameState;
import gatech.mmpm.sensor.composite.Invocation;

/**
 * @author rushabh kane
 *
 * This class is designed to take some given Plans, and determine if any or all
 * of the postconditions of one plan satisfy any or all of the preconditions of
 * the other.
 */
public class ExpandConditionMatcher {

    /**
     * @author rushabh kane
     *
     * Wrapper class for holding pre- and post- conditions and if they match.
     */
    public static class Match {

        public Sensor postCondition;
        public Sensor preCondition;
        public double match;

        public Match(Sensor postC, Sensor preC, double m) {
            postCondition = postC;
            preCondition = preC;
            match = m;
        }
    }
    /**
     * 0 for no debug statement, more than that for some.
     */
    public static int DEBUG = 0;
    /**
     * used for matching one Sensor with another.
     */
    private ConditionMatcher m_atomicMatcher = null;

    /**
     * Default Constructor.
     *
     * @param rulesFile the rules for the conditions.
     */
    public ExpandConditionMatcher(ConditionMatcher cm) {
        m_atomicMatcher = cm;
    }

    /**
     * Expands a Sensor in conjunctive normal form (i.e., many conditions
     * logically 'and'ed together) into a list of the component conditions.
     *
     * NOTE: could be changed to have a return value of the list, instead of
     * passing a list in. However, I have not had time to review the code and
     * see where else this is used, and there is a performance benefit to doing
     * it this way, so while it is more difficult to understand, I am leaving it
     * this way for now. - KDB
     *
     *
     * @param condition
     * @param list
     */
    public static void expandCondition(Sensor condition, List<Sensor> list) {
        if (condition instanceof AndCondition) {
            for (Sensor c : ((AndCondition) condition).getChildren()) {
                expandCondition(c, list);
            }
        } else {
            list.add(condition);
        }
    }

    /**
     * Tests if the success conditions of the first plan satisfy at least one of
     * the preconditions of the second plan.
     *
     * @param first the first plan (earlier).
     * @param second the second plan (later).
     * @return true if the success conditions satisfy the preconditions.
     */
    public boolean matchSuccessWithPreconditions(Plan first, int cycle1, GameState gs1, String player1,
            Plan second, int cycle2, GameState gs2, String player2) {
        return matchUnexpanded(first, first.getSuccessCondition(), cycle1, gs1, player1, second, cycle2, gs2, player2);
    }

    /**
     * Tests if the failure conditions of the first plan satisfy at least one of
     * the preconditions of the second plan.
     *
     * @param first the first plan (earlier).
     * @param second the second plan (later).
     * @return true if the failure conditions satisfy the preconditions.
     */
    public boolean matchFailureWithPreconditions(Plan first, int cycle1, GameState gs1, String player1,
            Plan second, int cycle2, GameState gs2, String player2) {
        return matchUnexpanded(first, first.getFailureCondition(), cycle1, gs1, player1, second, cycle2, gs2, player2);
    }

    /**
     * Expands the given post Sensor (which comes from the first plan - may be
     * either success or failure conditions) with the preconditions from the
     * second plan. There one needs to be one postcondition/precondition match
     * to return true.
     *
     * @param first the first plan.
     * @param second the second plan.
     * @param post the failure/success Sensor from the first plan.
     * @return true if at least one postcondition matches at least one
     * precondition.
     */
    private boolean matchUnexpanded(Plan first, Sensor post, int cycle1, GameState gs1, String player1,
            Plan second, int cycle2, GameState gs2, String player2) {
        List<Sensor> preConditions = new LinkedList<Sensor>();
        List<Sensor> postConditions = new LinkedList<Sensor>();
        boolean ret;

        if (DEBUG >= 1) {
            System.out.println("matchUnexpanded: start");
            if (DEBUG>=2) {
                System.out.println("postConditions: " + post);
                System.out.println("preConditions: " + second.getPreCondition());
            }
        }

        expandCondition(post, postConditions);
        expandCondition(second.getPreCondition(), preConditions);

        ret = matchPostWithPreConditions(first, postConditions, cycle1, gs1, player1,
                second, preConditions, cycle2, gs2, player2);

        if (!ret) {
            double match = m_atomicMatcher.match(first, null, cycle1, gs1, player1,
                    second, null, cycle2, gs2, player2);
            if (match > 0.1) {
                ret = true;
            }
        }

        if (DEBUG >= 1) {
            System.out.println("matchUnexpanded: end");
        }

        return ret;
    }

    /**
     * checks if at least one of the postconditions from the first plan match at
     * least one of the preconditions from the second plan
     *
     * @param first the first plan.
     * @param second the second plan.
     * @param postConditions the post conditions (success/failure) of the first
     * plan.
     * @param preConditions the preconditions of the first plan.
     * @return true if there is at least one match.
     */
    private boolean matchPostWithPreConditions(
            Plan first, List<Sensor> postConditions, int cycle1, GameState gs1, String player1,
            Plan second, List<Sensor> preConditions, int cycle2, GameState gs2, String player2) {
        if (DEBUG >= 2) {
            System.out.println("\nMatching: " + first + " with " + second);
            System.out.println("postConditions: " + postConditions);
            System.out.println("preConditions: " + preConditions);
        }

        for (int i = 0; i < preConditions.size(); i++) {
            for (int j = 0; j < postConditions.size(); j++) {
                // only with one Sensor match we already consider the
                // dependency to exist!
                if (m_atomicMatcher.match(first, postConditions.get(j), cycle1, gs1, player1,
                        second, preConditions.get(i), cycle2, gs2, player2) > 0.0) {
                    
                    if (DEBUG>=1) {
                        System.out.println("matchPostWithPreConditions: match between:");
                        System.out.println("postcondition: " + postConditions.get(j));
                        System.out.println("precondition: " + preConditions.get(i));
                    }
                    
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Gets the list of preconditions and success conditions matched by the two
     * plans.
     *
     * @param first the first plan.
     * @param second the second plan.
     * @return the list of matched conditions.
     */
    public List<Match> matchSuccessWithPreconditionsDetailed(Plan first, int cycle1, GameState gs1, String player1,
            Plan second, int cycle2, GameState gs2, String player2) {
        return matchUnexpandedDetailed(first, cycle1, gs1, player1, second, first.getSuccessCondition(), cycle2, gs2, player2);
    }

    /**
     * Gets the list of preconditions and failure conditions matched by the two
     * plans.
     *
     * @param first the first plan.
     * @param second the second plan.
     * @return the list of matched conditions.
     */
    public List<Match> matchFailureWithPreconditionsDetailed(Plan first, int cycle1, GameState gs1, String player1,
            Plan second, int cycle2, GameState gs2, String player2) {
        return matchUnexpandedDetailed(first, cycle1, gs1, player1,
                second, first.getFailureCondition(), cycle2, gs2, player2);
    }

    /**
     * Gets the list of preconditions and postconditions matched by the two
     * plans.
     *
     * @param first the first plan.
     * @param second the second plan.
     * @param post the failure/success Sensor from the first plan.
     * @return the list of matched conditions.
     */
    private List<Match> matchUnexpandedDetailed(Plan first, int cycle1, GameState gs1, String player1,
            Plan second, Sensor post, int cycle2, GameState gs2, String player2) {
        List<Sensor> preConditions = new LinkedList<Sensor>();
        List<Sensor> postConditions = new LinkedList<Sensor>();
        List<Match> ret;

        if (DEBUG >= 1) {
            System.out.println("matchUnexpandedDetailed: start");
            System.out.println("postConditions: " + post);
            System.out.println("preConditions: " + second.getPreCondition());
        }

        expandCondition(post, postConditions);
        expandCondition(second.getPreCondition(), preConditions);

        ret = matchPostWithPreConditionsDetailed(first, postConditions, cycle1, gs1, player1,
                second, preConditions, cycle2, gs2, player2);

        if (ret.isEmpty()) {
            double match = m_atomicMatcher.match(first, null, cycle1, gs1, player1,
                    second, null, cycle2, gs2, player2);
            if (match > 0.0) {
                ret.add(new Match(null, null, match));
            }
        }

        if (DEBUG >= 1) {
            System.out.println("matchUnexpandedDetailed: end");
        }

        return ret;
    }

    /**
     * Collects the list of matching post and pre conditions.
     *
     * @param first
     * @param second
     * @param postConditions
     * @param preConditions
     * @return
     */
    private List<Match> matchPostWithPreConditionsDetailed(
            Plan first, List<Sensor> postConditions, int cycle1, GameState gs1, String player1,
            Plan second, List<Sensor> preConditions, int cycle2, GameState gs2, String player2) {
        List<Match> ret = new LinkedList<Match>();
        if (DEBUG >= 1) {
            System.out.println("\nMatching: " + first + " with " + second);
            System.out.println("postConditions: " + postConditions);
            System.out.println("preConditions: " + preConditions);
        }

        for (int i = 0; i < preConditions.size(); i++) {
            for (int j = 0; j < postConditions.size(); j++) {
                // only with one Sensor match we already consider the
                // dependency to exist!
                double match = m_atomicMatcher.match(first, postConditions.get(j), cycle1, gs1, player1,
                        second, preConditions.get(i), cycle2, gs2, player2);
                if (match > 0.0) {
                    ret.add(new Match(postConditions.get(j), preConditions.get(i), match));
                }
            }
        }

        return ret;
    }
}
