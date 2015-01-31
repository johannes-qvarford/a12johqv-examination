/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.execution.planbase;

import gatech.mmpm.*;
import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.SensorLibrary;
import gatech.mmpm.util.Pair;
import gatech.mmpm.util.XMLWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Element;

public class GameStateFeatures {

    GameState m_gs;
    HashMap<String, Double> m_features;
    Set<String> m_featureNames;	// This is just a cache, it should be equivalent to m_features.getKeySet();

    private GameStateFeatures() {
        m_gs = null;
        m_features = new HashMap<String, Double>();
        m_featureNames = new HashSet<String>();
    }

    public GameStateFeatures(int cycle, GameState gs, String player) {
        m_gs = gs;
        m_features = new HashMap<String, Double>();
        m_featureNames = new HashSet<String>();
        String featureName;
        Double featureValue;
        
        IDomain d = d2.core.Config.getDomain();
        for(Class c:d.getEntities()) {
            m_features.put("totalCount_" + c.getSimpleName(),0.0);
            m_featureNames.add("totalCount_" + c.getSimpleName());
            m_features.put("player_Count_" + c.getSimpleName(),0.0);
            m_featureNames.add("player_Count_" + c.getSimpleName());
        }        

        // histogram of entities in the map:
        {
            Map m = m_gs.getMap();
            int size = m.size();           
            for (int i = 0; i < size; i++) {
                PhysicalEntity e = m.get(i);

                if (e != null) {
                    String cn = e.getClass().getSimpleName();
                    featureName = "totalCount_" + cn;
                    featureValue = m_features.get(featureName);
                    featureValue++;
                    m_features.put(featureName, featureValue);
                }
            }
        }

        // histogram of entities in the game state:
        {
            for (Entity e : gs.getAllEntities()) {
                if (e != null) {
                    String cn = e.getClass().getSimpleName();
                    featureName = "totalCount_" + cn;
                    featureValue = m_features.get(featureName);
                    featureValue++;
                    m_features.put(featureName, featureValue);
                }
            }
        }

        // Number of units per player:
        {
            for (String pn : gs.getAllPlayers()) {
                List<Entity> l = gs.getEntityByOwner(pn);
                int n = l.size();
                if (pn.equals(player)) {
                    for (Entity e : gs.getAllEntities()) {
                        if (e != null) {
                            String cn = e.getClass().getSimpleName();
                            featureName = "player_Count_" + cn;
                            featureValue = m_features.get(featureName);
                            featureValue++;
                            m_features.put(featureName, featureValue);
                        }
                    }                       
                    featureName = "totalPlayerEntities";
                    m_features.put(featureName, (double) n);
                    m_featureNames.add(featureName);
                }
            }
        }

        // Number of units total in map and game state
        {
            m_features.put("totalEntities", (double) gs.getAllEntities().size());
            m_featureNames.add("totalEntities");

            {
                int n = 0;
                Map m = m_gs.getMap();
                int size = m.size();
                for (int i = 0; i < size; i++) {
                    if (m.get(i) != null) {
                        n++;
                    }
                }
                m_features.put("totalEntitiesMap", (double) n);
                m_featureNames.add("totalEntitiesMap");
            }
        }
        // Compute features based on sensors:
        List<Sensor> sensorsByType = SensorLibrary.getSensorsByType(ActionParameterType.BOOLEAN,
                ActionParameterType.FLOAT,
                ActionParameterType.INTEGER);
        {
            for (Sensor s : sensorsByType) {
                /*
                System.out.print(s.getClass().getSimpleName() + "[ ");
                for(Pair<String,ActionParameterType> p:s.getNeededParameters()) {
                    System.out.print(p._a + " ");
                }
                System.out.println(" ]");
                */
                if (s.getNeededParameters().size() == 0) {
                    featureName = "sensor_" + s.getClass().getSimpleName();
                    Object o = s.evaluate(cycle, m_gs, player);
                    if (o == null) {
                        System.err.println("Sensor " + s.getClass().getSimpleName() + " evaluated to null for player " + player);
                        System.err.println(m_gs);
                    } else {
                        featureValue = ((Number) o).doubleValue();
                        m_features.put(featureName, featureValue);
                        m_featureNames.add(featureName);
                    }
                }
            }
        }

    }

    public Double getFeature(String featureName) {

        Double v = m_features.get(featureName);

        if (v != null) {
            return v;
        }
        return 0.0;
    }

    public Set<String> getFeatureNames() {
        return m_featureNames;
    }

    public String toXMLString() {
        String s = "<GameStateFeatures>\n";
        for (String fn : m_featureNames) {
            s += "  <" + fn + ">" + getFeature(fn) + "</" + fn + ">\n";
        }
        s += "</gameStateFeatures>\n";
        return s;
    }

    public void writeToXML(XMLWriter w) {
        w.tag("GameStateFeatures");
        w.tag("features");
        for (String fn : m_featureNames) {
            w.tag(fn, getFeature(fn));
        }
        w.tag("/features");
        m_gs.writeToXML(w);
        w.tag("/GameStateFeatures");
    }

    public void writeDifferenceToXML(XMLWriter w, GameStateFeatures prev_gsf) {
        w.tag("GameStateFeatures");
        w.tag("features");
        for (String fn : m_featureNames) {
            if (prev_gsf.getFeature(fn) != (getFeature(fn))) {
                w.tag(fn, getFeature(fn));
            }
        }
        w.tag("/features");

//		w.tag("gamestate");
//		m_gs.getMap().writeDifferenceToXML(w,prev_gsf.getM_gs().getMap());
//		for (domain.Entity e : m_gs.getAllEntities())
//			e.writeDifferenceToXML(w,prev_gsf.getM_gs().getEntity(e.getentityID()));
//		w.tag("/gamestate");

        //m_gs.writeToXML(w);
        w.tag("/GameStateFeatures");
    }

    public void writeToXMLOnlyFeatures(XMLWriter w) {
        w.tag("GameStateFeatures");
        w.tag("features");
        for (String fn : m_featureNames) {
            w.tag(fn, getFeature(fn));
        }
        w.tag("/features");
        w.tag("/GameStateFeatures");
    }

    public String toString() {
        String s = "";
        for (String fn : m_featureNames) {
            s += fn + ": " + getFeature(fn) + " ";
        }
        return s;
    }

    public static GameStateFeatures loadFromXML(Element e) {
        GameStateFeatures ret = new GameStateFeatures();
        Element features_e = e.getChild("features");

        for (Object o : features_e.getChildren()) {
            Element fe = (Element) o;
            ret.m_featureNames.add(fe.getName());
            ret.m_features.put(fe.getName(), Double.parseDouble(fe.getValue()));
        }

        return ret;
    }

    public GameState getM_gs() {
        return m_gs;
    }

    public static GameStateFeatures loadDifferenceFromXML(Element e, GameStateFeatures prev_features) {
        GameStateFeatures ret = new GameStateFeatures();

        ret.m_features = new HashMap<String, Double>();

        for (String feature : prev_features.getM_features().keySet()) {
            if (!ret.m_featureNames.contains(feature)) {
                ret.m_featureNames.add(feature);
            }
            ret.m_features.put(feature, prev_features.getM_features().get(feature));
        }

        Element features_e = e.getChild("features");

        for (Object o : features_e.getChildren()) {
            Element fe = (Element) o;
            if (!ret.m_featureNames.contains(fe.getName())) {
                ret.m_featureNames.add(fe.getName());
            }
            ret.m_features.put(fe.getName(), Double.parseDouble(e.getValue()));
        }
//		Iterator ii = ret.m_features.values().iterator();
//		while(ii.hasNext()) {
//			System.out.print(ii.next() + " && ");
//		}
//		System.out.println();

        return ret;
    }

    public HashMap<String, Double> getM_features() {
        return m_features;
    }

    public void setM_features(HashMap<String, Double> m_features) {
        this.m_features = m_features;
    }
}
