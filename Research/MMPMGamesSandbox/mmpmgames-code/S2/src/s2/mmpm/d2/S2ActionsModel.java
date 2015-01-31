package s2.mmpm.d2;

import gatech.mmpm.Action;
import gatech.mmpm.GameState;
import gatech.mmpm.Trace;
import gatech.mmpm.util.XMLWriter;

import java.util.List;

import org.jdom.Element;

import d2.worldmodel.ActionsModel;


public class S2ActionsModel extends ActionsModel {

	// Variables for the simulation:
	private int m_current_simulation_cycle = 0;
	private int nextEntityID = 0;

	public S2ActionsModel() {
		super();
	}

	public void learn(Trace traces) {

	}

	public String toString() {
		String out = "S2ActionsModel.";
		return out;
	}

	public void savetoXML(XMLWriter w) {
		w.tagWithAttributes("actions-model", "class='s2.d2.S2ActionsModel'");
		w.tag("/actions-model");
	}

	public void loadfromXML(Element e) {
	}

	public GameState simulate(GameState working_gs, List<Action> newActions, int cycles) {

		return working_gs;
	}

}
