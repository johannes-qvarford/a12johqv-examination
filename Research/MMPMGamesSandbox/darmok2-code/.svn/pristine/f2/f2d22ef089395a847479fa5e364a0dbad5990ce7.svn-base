/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.worldmodel.traceanalysis;

import gatech.mmpm.Action;

import java.util.LinkedList;
import java.util.List;


public class DifferenceTraceEntry {
	int m_timeStamp = 0;
	List<Action> m_actions = null;
	List<Difference> m_differences = null;	
	
	public DifferenceTraceEntry(int timeStamp) {
		m_timeStamp = timeStamp;
		m_actions = new LinkedList<Action>();
		m_differences = new LinkedList<Difference>();
	}
	
	public int getTimeStamp() {
		return m_timeStamp;
	}
	
	public List<Action> getActions() {
		return m_actions;
	}
	
	public List<Difference> getDifferences() {
		return m_differences;
	}
	
	public String toString() {
		String out = "t " + m_timeStamp + "\n";
		for(Action a:m_actions) {
			out += "  a: " + a.toString() + "\n";
		}
		for(Difference d:m_differences) {
			out += "  d: " + d.toString() + "\n";			
		}
		return out;
	}
}
