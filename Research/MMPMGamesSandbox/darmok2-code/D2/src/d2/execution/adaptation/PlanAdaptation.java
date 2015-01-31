/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.execution.adaptation;

import d2.core.D2;
import d2.core.D2Module;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jdom.Element;

import d2.execution.planbase.PlanBase;
import d2.plans.Plan;

import gatech.mmpm.GameState;

import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.util.XMLWriter;
import java.util.List;

public abstract class PlanAdaptation extends D2Module {

        protected D2 m_d2 = null;
	
	public abstract Plan adapt(Plan original_plan,GameState original_gs, String original_player, Sensor original_goal,
							   int current_cycle, GameState current_gs, String current_player, Sensor current_goal,
                               List<String> usedIDs);
	
	public abstract void adaptToAllow(Plan original_plan, GameState original_gs, String original_player,
                                      Plan planToAllow, int current_cycle, GameState current_gs, String current_player,
                                      PlanBase pb, PlanAdaptation parameterAdapter,List<String> usedIDs);
	
	public PlanAdaptation()
	{
	}
	
        public void setD2(D2 d2) {
            m_d2 = d2;
        }
	
	public abstract void saveToXML(XMLWriter w);

	public static PlanAdaptation loadFromXML(Element xml) {
		Class<?> c;
		try {
			
			c = Class.forName(xml.getChildText("type"));
			Method m = c.getMethod("loadFromXMLInternal", new Class[]{Element.class});
			return (PlanAdaptation) m.invoke(c, xml);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
                        System.err.println("Error loading module '" + xml.getChildText("type") + "'");
			e.printStackTrace();
		}
		return null;		
	}

	public static PlanAdaptation loadFromXMLInternal(Element xml) {
		return null;
	}
	
}
