/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.core;

import d2.execution.adaptation.NothingPlanAdaptation;
import d2.execution.adaptation.PlanAdaptation;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import d2.execution.planbase.PlanBase;
import d2.execution.planexecution.PlanExecution;
import d2.execution.planexecution.RealTimePlanExecution;
import d2.execution.planner.Planner;
import d2.execution.planner.SimpleExpandPlanner;
import d2.plans.GoalPlan;
import d2.plans.Plan;
import d2.worldmodel.WorldModel;

import gatech.mmpm.*;
import gatech.mmpm.util.XMLWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;

public class D2 extends gatech.mmpm.learningengine.AbstractMEExecutor {

    public static int DEBUG = 0;
    boolean gameStarted = false;
    boolean gameEnded = false;

    ////
    // Empty Constructor, used by the D2METrainer
    ////
    public D2() {
    }

    ////
    // Constructor, in order to create AIs directly from a planBase
    ////
    public D2(String planBaseFileName, Planner planner, PlanExecution pe, PlanAdaptation pa, PlanAdaptation ppa, WorldModel wm) throws Exception {
        m_wm = wm;

        try {
            InputStream inFile = null;
            inFile = new FileInputStream(planBaseFileName);
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(inFile);
            Element root = document.getRootElement();
            m_pb = PlanBase.loadFromXML(root, d2.core.Config.getDomainName(), this);
            inFile.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        m_originalDomain = d2.core.Config.getDomainName();
        pe.setPlanAdaptation(pa);
        pe.setParameterAdaptation(ppa);
        m_planner = planner;
        m_planner.setPlanExecution(pe);
        m_planner.setPlanAdaptation(pa);

        m_pb.setD2(this);
        m_planner.setD2(this);
    }

    ////
    // Constructor, in order to create AIs directly
    ////
    public D2(PlanBase pb, Planner planner, PlanExecution pe, PlanAdaptation pa, PlanAdaptation ppa, WorldModel wm) throws Exception {
        m_wm = wm;
        m_pb = pb;
        m_originalDomain = d2.core.Config.getDomainName();
        pe.setPlanAdaptation(pa);
        pe.setParameterAdaptation(ppa);
        m_planner = planner;
        m_planner.setPlanExecution(pe);
        m_planner.setPlanAdaptation(pa);

        m_pb.setD2(this);
        m_planner.setD2(this);
    }

    ////
    // Save/Load to XML methods
    ////	
    public void saveToXML(XMLWriter w) throws IOException {
        w.tag("BasicAI");
        w.tag("originalDomain", m_originalDomain);
        m_pb.saveToXML(w);
        m_planner.saveToXML(w);
        m_wm.savetoXML(w);
        w.tag("/BasicAI");
        w.flush();
    }

    public static D2 loadFromXML(Element xml) {
        D2 ret = new D2();

        ret.m_wm = WorldModel.loadfromXML(xml.getChild("world-model"));
        ret.m_originalDomain = xml.getChildText("originalDomain");
        ret.m_pb = PlanBase.loadFromXML(xml.getChild("PlanBase"), d2.core.Config.getDomainName(), ret);
        ret.m_planner = Planner.loadFromXML(xml.getChild("Planner"), ret, "");

        return ret;
    }

    ////
    // Serialization methods
    ////
    public void saveToDisk(java.io.PrintStream o) throws java.io.IOException {

        java.io.ObjectOutputStream writer;
        writer = new java.io.ObjectOutputStream(o);
        writer.writeObject(m_originalDomain);
        writer.writeObject(m_pb);

        System.err.println("Warning: Method BasicAI.saveToDisk() relies on java.io.Serializable...");
    }

    public static D2 loadFromDisk(java.io.InputStream r) throws gatech.mmpm.ConfigurationException {

        String domain;
        d2.execution.planbase.PlanBase planBase;

        try {
            java.io.ObjectInputStream in;
            in = new java.io.ObjectInputStream(r);
            domain = (String) in.readObject();
            planBase = (d2.execution.planbase.PlanBase) in.readObject();
        } catch (Exception ex) {
            gatech.mmpm.ConfigurationException thrown;
            thrown = new gatech.mmpm.ConfigurationException("Impossible to read BasicAI from file.\n");
            thrown.initCause(ex);
            throw thrown;
        }

        D2 ret = new D2();
        ret.m_pb = planBase;
        ret.m_originalDomain = domain;

        System.err.println("Warning: Method BasicAI.loadFromDisk() relies on java.io.Serializable...");
        return ret;
    }

    ////
    // IMEExecutor methods
    ////
    /**
     * Method that loads a ME to be executed during the game.
     *
     * @param me ME to be loaded.
     * @param idomain Domain of the game to play.
     * @throws ConfigurationException In case of error.
     */
    public boolean loadME(java.io.InputStream me, gatech.mmpm.IDomain idomain)
            throws ConfigurationException {

        SAXBuilder builder = new SAXBuilder();
        Document doc;

        try {
            doc = builder.build(me);
            Element xml = doc.getRootElement();

            d2.core.Config.setDomain(idomain);
            m_wm = WorldModel.loadfromXML(xml.getChild("world-model"));
            m_originalDomain = xml.getChildText("originalDomain");
            m_pb = PlanBase.loadFromXML(xml.getChild("PlanBase"), d2.core.Config.getDomainName(), this);
            m_planner = Planner.loadFromXML(xml.getChild("Planner"), this, "");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new gatech.mmpm.ConfigurationException("Impossible to load the ME.\n");
        }

        /*
         * try { saveToXML(new XMLWriter(new FileWriter("MEs/test.xml"))); }
         * catch (IOException e) { e.printStackTrace(); }
         */
        return true;
    }

    public void gameStart(String playerName) {

        if (!m_originalDomain.equals(d2.core.Config.getDomainName())) // The domain has changed!
        {
            System.err.println("D2 was configured for a different domain!");
            return;
        }

        d2.util.Log.print("D2 - IA for " + playerName + " starts.");
        
        m_pb.analyzeFeatures();

        m_playerName = playerName;
        m_plan = new GoalPlan(d2.core.Config.getWinGoal());

        if (m_planner!=null) {
            m_planner.setPlayer(m_playerName);
            m_planner.addPlan(m_plan);
        }

        gameStarted = true;
        t = new Trace();
    }

    public void gameEnd() {
        d2.util.Log.print("D2 - IA for " + m_playerName + " ends.\n");

        gameEnded = true;
    }

    public void getActions(int cycle, GameState gs, List<Action> actions) {
        if (!gameStarted) System.err.println("D2: getActions method called without having first called \"gameStart\"!!!");
        if (gameEnded) {
            System.err.println("D2: getActions method called after having called \"gameEnd\"!!!");
            return;
        }


        long t0 = System.currentTimeMillis();
        m_planner.plan(cycle, gs, m_playerName);
        long t1 = System.currentTimeMillis();
        m_planner.execute(actions, cycle, gs, m_playerName);
        long t2 = System.currentTimeMillis();
        if (DEBUG >= 1) {
            System.out.println("AI times: " + (t1 - t0) + " for planning, " + (t2 - t1) + " for executing.");
        }

        for (Action a : actions) {
            System.out.println(cycle + " - D2 wants to execute " + a);
        }
        
        // D2 keeps an internal trace of all the actions it executes (this is used by some modules like Temporal BackTracking retrieval):
        if (!actions.isEmpty()) {
            Entry te = new Entry(cycle, gs);
            for(Action a: actions) te.addAction(a);
            t.addEntry(te);
        }
    }

    ////
    // Methods and attributes used by the learn process to set up the object.
    ////
    public void setWorldModel(WorldModel wm) {
        m_wm = wm;
    }

    public void setPlanBase(d2.execution.planbase.PlanBase pb) {
        m_pb = pb;
        m_pb.setD2(this);
    }

    public void setOriginalDomainName(String name) {
        m_originalDomain = name;
    }

    public void setPlanner(Planner m_p) {
        m_planner = m_p;
        m_planner.setD2(this);
    }

    public WorldModel getWorldModel() {
        return m_wm;
    }

    public PlanBase getPlanBase() {
        return m_pb;
    }

    public Planner getPlanner() {
        return m_planner;
    }
    
    public Trace getTrace() {
        return t;
    }
    
    
    WorldModel m_wm = null;
    PlanBase m_pb;
    /**
     * Name of the domain where we learnt
     */
    String m_originalDomain = "";
    Planner m_planner = null;
    ////
    // Attributes used while playing
    ////
    Plan m_plan = null;
    String m_playerName = "";
    Trace t = null;
}
