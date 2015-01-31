/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.worldmodel;

import gatech.mmpm.Action;
import gatech.mmpm.GameState;
import gatech.mmpm.Trace;
import gatech.mmpm.util.XMLWriter;

import java.util.List;

import org.jdom.Element;


public class EmptyActionsModel extends ActionsModel {

	public EmptyActionsModel() {
		super();
	}

	public void learn(Trace trace) {
	}
	public void savetoXML(XMLWriter w)
	{
	}
	public void loadfromXML(Element e)
	{
		
	}
	public GameState simulate(GameState gs,List<Action> newActions, int cycles) {
		return (GameState)gs.clone();
	}

}
