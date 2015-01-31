/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.learn.melearning;

import d2.core.D2;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Properties;

import d2.execution.adaptation.ActionAdderPlanAdaptation;
import d2.execution.adaptation.DependencyGraphPlanAdaptation;
import d2.execution.adaptation.NothingPlanAdaptation;
import d2.execution.adaptation.PlanAdaptation;
import d2.execution.adaptation.parameters.ParameterAdaptation;
import d2.execution.adaptation.parameters.SystematicParameterAdaptation;
import d2.execution.planbase.PlanBase;
import d2.execution.planbase.SimilarityRetrieval;
import d2.execution.planexecution.PlanExecution;
import d2.execution.planexecution.RealTimePlanExecution;
import d2.execution.planexecution.TurnBasedPlanExecution;
import d2.execution.planner.MinMaxPlanner;
import d2.execution.planner.NothingPlanner;
import d2.execution.planner.Planner;
import d2.execution.planner.SimpleExpandPlanner;
import d2.execution.planner.SingleSimulatorBasedExpanderPlanner;
import d2.learn.planlearning.HierarchicalPlanLearning;
import d2.learn.planlearning.MonolithicPlanLearning;
import d2.learn.planlearning.PlanLearning;
import d2.plans.GoalPlan;
import d2.plans.Plan;
import d2.util.Log;
import d2.util.statevisualizer.TraceVisualizer;
import d2.worldmodel.WorldModel;


import gatech.mmpm.ConfigurationException;
import gatech.mmpm.IDomain;
import gatech.mmpm.Trace;
import gatech.mmpm.learningengine.AbstractMEExecutor;
import gatech.mmpm.learningengine.IMETrainer;
import gatech.mmpm.util.XMLWriter;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * D2 implementation of the IMETrainer interface to create MEs using game
 * traces.
 *
 * @see IMEExecutor
 *
 * @author Pedro Pablo Gomez-Martin and David Llanso @date August, 2009
 */
public class D2METrainer implements IMETrainer {

    /**
     * Main function of the class. This method creates a ME from the provided
     * traces according with the domain and configuration.
     *
     * @param traces Traces to be used in the ME generation.
     *
     * @param playerNames Each player name is linked in order with each trace.
     * Each one specifies the player, of the trace, used to learn from his
     * actions.
     *
     * @param me Output stream with the new ME. Make Me Play Me will consider
     * the ME as a black box. The ME format must be synchronized with the format
     * expected in the IMEExecutor interface for the same learning engine.
     *
     * @param domain Java class that contains the information of the game
     * domain.
     *
     * @param config Extra configuration parameters. There are extracted from
     * the command line arguments. Make Me Play Me don't process them, they are
     * specific from the concrete learner engine.
     *
     * @return True if the ME was generated, and false in other case. Any
     * partial information sent to the output stream will be discarded.
     */
    public AbstractMEExecutor train(List<Trace> traces,
            List<String> playerNames,
            IDomain domain,
            Properties config,
            OutputStream serializedMEStream)
            throws ConfigurationException, IOException, Exception {

        d2.core.Config.setDomain(domain);
        d2.core.Config.setProperties(config);

        config(config);

        D2 me = new D2();
        me.setOriginalDomainName(domain.getName());
        me.setWorldModel(m_wm);
        me.setPlanBase(m_pb);
        me.setPlanner(m_p);

        Log.println("D2 - Learning Started...");
        for (int count = 0; count < traces.size(); count++) {
            String playerName = playerNames.get(count);
            Trace trace = traces.get(count);

//            new TraceVisualizer(trace, 800,600);

            if (trace != null) {
                trace.setTraceID(count);
                m_pb.addPlans(m_pl.learnFromTrace(trace, playerName, m_wm));
            } else {
                Exception e = new Exception("Failed to load a trace");
                throw e;
            }
        }

        //This is for train the World Model, but nowadays there is not
        //a way to do it from the general specification of MMPM
        //ret.m_wm = d2.core.Config.getDomain().getWorldModel();
        //ret.m_wm.learnFromTraces(traceFiles);

        // Sent back the generated ME.
        if (serializedMEStream!=null) {
            Writer output = new OutputStreamWriter(serializedMEStream);
            me.saveToXML(new XMLWriter(output));
            output.close();
        }
        Log.println("D2 - Learning Done.");

        return me;
    }

    /**
     * Set the configuration needed by D2 in order to create MEs.
     *
     * @param config Different configuration parameters.
     * @throws gatech.mmpm.ConfigurationException
     */
    public void config(Properties config)
            throws gatech.mmpm.ConfigurationException {
        setWorldModel(config);
        m_pb = getPlanRetrieval(config.getProperty("plan_retrieval"));
        setPlanLearning(config.getProperty("plan_learning"));
        PlanAdaptation ppa = getParameterAdapter(config.getProperty("parameter_adaptation"));
        PlanAdaptation pa = getPlanAdapter(config.getProperty("plan_adaptation"));
        PlanExecution pe = getPlanExecution(config.getProperty("plan_execution"), ppa, pa, m_pb);
        setPlanner(config.getProperty("planner"), pe, pa, m_wm);
    }

    /**
     * Generates the world model.
     *
     * @param name Name of the model. When null or empty string, it sets the
     * default world model
     * @throws D2ConfigurationException If the model name is not a valid one.
     */
    public void setWorldModel(Properties config) throws ConfigurationException {

        m_wm = new WorldModel();
        m_wm.config(config);
    }

    /**
     * Generates the plan execution strategy.
     *
     * @param name Name of the strategy. When null or empty string, it does not
     * set any plan execution.
     * @throws D2ConfigurationException If the strategy name is not a valid one.
     */
    public PlanExecution getPlanExecution(String name, PlanAdaptation parama, PlanAdaptation plana,
            PlanBase pb) throws ConfigurationException {

        if ((name == null) || (name.length() == 0)) {
            return null;
        }

        if (name.equals("real-time")) {
            return new RealTimePlanExecution("", parama, plana, null);
        } else if (name.equals("turn-based")) {
            return new TurnBasedPlanExecution("", parama, plana, null);
        } else {
            throw new ConfigurationException("'" + name
                    + "' is not a valid plan retrieval strategy.");
        }
    }

    /**
     * Generates the parameter adaptation strategy.
     *
     * @param name Name of the strategy. When null or empty string, it does not
     * set any parameter adaptation.
     * @throws D2ConfigurationException If the strategy name is not a valid one.
     */
    public PlanAdaptation getParameterAdapter(String name) throws ConfigurationException {

        if ((name == null) || (name.length() == 0)) {
            return null;
        }
        
        // parse name:
        StringTokenizer st = new StringTokenizer(name,"(,)");
        String className = st.nextToken();
        List<Integer> parameters = new LinkedList<Integer>();
        while(st.hasMoreTokens()) parameters.add(Integer.parseInt(st.nextToken()));
        
        try {
            Class c = Class.forName(className);
            for(Constructor cons:c.getConstructors()) {
                if (cons.getParameterTypes().length == parameters.size()) {
                    if (parameters.size()==0) {
                        return (PlanAdaptation) cons.newInstance();            
                    } else if (parameters.size()==1) {
                        return (PlanAdaptation) cons.newInstance(parameters.get(0));
                    } else if (parameters.size()==2) {
                        return (PlanAdaptation) cons.newInstance(parameters.get(0), parameters.get(1));
                    }
                }
            }
            throw new ConfigurationException("'" + name + "' is not a valid parameter adaptation strategy.");            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConfigurationException("'" + name + "' is not a valid parameter adaptation strategy.");
        }        
    }

    /**
     * Generates the plan adaptation strategy.
     *
     * @param name Name of the strategy. When null or empty string, it does not
     * set any parameter adaptation.
     * @throws D2ConfigurationException If the strategy name is not a valid one.
     */
    public PlanAdaptation getPlanAdapter(String name) throws ConfigurationException {

        if ((name == null) || (name.length() == 0)) {
            return new NothingPlanAdaptation();
        }

        try {
            Class c = Class.forName(name);
            Constructor cons = c.getConstructor();
            return (PlanAdaptation) cons.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConfigurationException("'" + name + "' is not a valid plan adaptation strategy.");
        }
    }

    /**
     * Gets the plan retrieval strategy.
     *
     * @param name Name of the strategy. When null or empty string, it does not
     * set any plan retrieval.
     * @throws D2ConfigurationException If the strategy name is not a valid one.
     */
    public PlanBase getPlanRetrieval(String name) throws ConfigurationException {

        if ((name == null) || (name.length() == 0)) {
            return null;
        }
        
        try {
            Class c = Class.forName(name);
            Constructor cons = c.getConstructor(D2.class);
            return  (PlanBase) cons.newInstance((D2)null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConfigurationException("'" + name + "' is not a valid plan retrieval strategy.");
        }
    }

    /**
     * Sets the plan learning strategy.
     *
     * @param name Name of the strategy.
     * @throws D2ConfigurationException If the strategy is not valid.
     */
    public void setPlanLearning(String name) throws ConfigurationException {

        if (name == null) {
            throw new ConfigurationException("Plan learning strategy not set (name was null)");
        }

        // parse name:
        StringTokenizer st = new StringTokenizer(name,"(,)");
        String className = st.nextToken();
        List<Double> parameters = new LinkedList<Double>();
        while(st.hasMoreTokens()) parameters.add(Double.parseDouble(st.nextToken()));
        
        try {
            Class c = Class.forName(className);
            for(Constructor cons:c.getConstructors()) {
                if (cons.getParameterTypes().length == parameters.size()+1) {
                    if (parameters.size()==0) {
                        m_pl = (PlanLearning) cons.newInstance(d2.core.Config.getDomain());
                        return;
                    } else if (parameters.size()==1) {
                        m_pl = (PlanLearning) cons.newInstance(d2.core.Config.getDomain(),parameters.get(0));
                        return;
                    } else if (parameters.size()==2) {
                        m_pl = (PlanLearning) cons.newInstance(d2.core.Config.getDomain(),parameters.get(0),parameters.get(1));
                        return;
                    }
                }
            }
            throw new ConfigurationException("'" + name + "' is not a valid plan learning strategy.");            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConfigurationException("'" + name + "' is not a valid plan learning strategy.");
        }                
    }

    /**
     * Sets the plan learning strategy.
     *
     * @param name Name of the strategy.
     * @param parameterAdapter Parameter adapter to be used by the planner.
     * @throws D2ConfigurationException If the strategy is not valid.
     */
    public void setPlanner(String name, PlanExecution pe, PlanAdaptation pa, WorldModel wm)
            throws ConfigurationException {

        if (name == null) {
            throw new ConfigurationException("Planner not set (name was null)");
        }

        try {
            Class c = Class.forName(name);
            Constructor cons = c.getConstructor(Plan.class, D2.class, String.class, PlanExecution.class, PlanAdaptation.class);
            m_p = (Planner) cons.newInstance(new GoalPlan(d2.core.Config.getWinGoal()), null, "", pe, pa);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConfigurationException("'" + name + "' is not a valid planning strategy.");
        }
    }
    private Planner m_p = null;
    private PlanBase m_pb = null;
    private PlanLearning m_pl = null;
    private WorldModel m_wm = null;
}
