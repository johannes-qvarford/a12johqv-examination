/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.worldmodel.knnoponentmodel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jdom.Element;

import d2.execution.planbase.GameStateFeatures;
import d2.execution.planbase.GameStateSimilarity;
import d2.execution.planbase.PlanBase;
import d2.worldmodel.OpponentsModel;

import gatech.mmpm.Action;
import gatech.mmpm.Entry;
import gatech.mmpm.GameState;
import gatech.mmpm.Trace;
import gatech.mmpm.util.XMLWriter;

public class KNNOponentModel extends OpponentsModel{

	public static final int DEBUG = 0;
	Random rng = new Random();
	
	HashMap<String,List<KNNOMExample> > m_examples = new HashMap<String,List<KNNOMExample> >();
	
	public KNNOponentModel() {
		super();
	}

	public void learn(Trace t) {
		for(Entry e:t.getEntries()) {
			HashMap<String,List<Action> > playerActions = new HashMap<String,List<Action> >();
			
			for(String p:e.getGameState().getAllPlayers()) {
				playerActions.put(p, new LinkedList<Action>());
			}
			for(Action a:e.getActions()) {
				playerActions.get(a.getPlayerID()).add(a);
			}
			
			for(String p:e.getGameState().getAllPlayers()) {
				KNNOMExample example = new KNNOMExample(new GameStateFeatures(e.getTimeStamp(),e.getGameState(),p),playerActions.get(p));
				
				if (m_examples.get(p)==null) {
					List<KNNOMExample> l = new LinkedList<KNNOMExample>();
					l.add(example);
					m_examples.put(p,l);						
				} else {
					m_examples.get(p).add(example);
				}
				
				if (DEBUG>=2) System.out.println("KNNOponentModel: Learnt new example for " + p + ": " + example);
			}
		}
		
		if (DEBUG>=1) {
			System.out.println("KNNOponentModel finished learning.");
			for(String player:m_examples.keySet()) {
				System.out.println("Learned " + m_examples.get(player).size() + " examples for player " + player + ".");
			}
		}
	}

	public List<Action> predictActions(int cycle, GameState gs, Set<String> players) {
		List<Action> predictedActions = new LinkedList<Action>();
		
		for(String player:players) {
			GameStateFeatures gsf = new GameStateFeatures(cycle, gs,player);
			List<KNNOMExample> examples = m_examples.get(player);
			
//			System.out.println("\npredictActions: for player " + player + " -------------------------------");

			if (examples!=null) {
				List<KNNOMExample> best = new LinkedList<KNNOMExample>();
				double best_similarity = 0, similarity;
				
				for(KNNOMExample e:examples) {
					similarity = GameStateSimilarity.gameStateSimilarity(e.m_features, gsf, null);
					
//					System.out.println("Similarity: " + similarity + " (best: " + best + ")");
					
					if (best.isEmpty()) {
						best.add(e);
						best_similarity = similarity;
					} else if (similarity>best_similarity) {
						best.clear();
						best.add(e);
						best_similarity = similarity;
					} else if (similarity==best_similarity) {
						best.add(e);
						best_similarity = similarity;						
					}
				}
				 if (!best.isEmpty()) {
					 KNNOMExample e = best.get(rng.nextInt(best.size()));
					 predictedActions.addAll(e.m_actions);
				 }
			}
		}
		return predictedActions;
	}
	
	public void savetoXML(XMLWriter w) {
		w.tagWithAttributes("opponent-model","class='" + this.getClass().getName() + "'");
		for(String e:m_examples.keySet()) {
			w.tagWithAttributes("opponent","name='" + e + "'");
			for(KNNOMExample ex:m_examples.get(e)) {
				ex.savetoXML(w);				
			}
			w.tag("/opponent");		
		}
		w.tag("/opponent-model");		
	}
	
//	public void savetoXML(XMLWriter w) {
//		saveDifferenceToXML(w);
//	}
	
	public void saveDifferenceToXML(XMLWriter w) {
		w.tagWithAttributes("opponent-model","class='" + this.getClass().getName() + "'");
		for(String e:m_examples.keySet()) {
			w.tagWithAttributes("opponent","name='" + e + "'");
			KNNOMExample prev_example = null;
			for(KNNOMExample ex:m_examples.get(e)) {
				if (prev_example == null)
					ex.savetoXML(w);
				else
					ex.saveDifferenceToXML(w, prev_example);
				prev_example = ex;
			}
			w.tag("/opponent");		
		}
		w.tag("/opponent-model");		
	}


	public void loadfromXML(Element e)
	{
		for(Object o:e.getChildren("opponent")) {
			Element opponent = (Element)o;
			
			String opp_name = opponent.getAttributeValue("name");
			List<KNNOMExample> examples;
			
			examples = m_examples.get(opp_name);
			if (examples == null) {
				examples = new LinkedList<KNNOMExample>(); 
				m_examples.put(opp_name,examples);
			}
			
			for(Object ex_o:opponent.getChildren("KNNOMExample")) {
				KNNOMExample ex = KNNOMExample.loadfromXML((Element)ex_o);
				examples.add(ex);
			}
			
			System.out.println("KNNOponentModel: " + examples.size() + " examples for opponent " + opp_name);
		}
	}

	public void loadDifferencefromXML(Element e) {
		for(Object o:e.getChildren("opponent")) {
			Element opponent = (Element)o;
			
			String opp_name = opponent.getAttributeValue("name");
			List<KNNOMExample> examples;
			
			examples = m_examples.get(opp_name);
			if (examples == null) {
				examples = new LinkedList<KNNOMExample>(); 
				m_examples.put(opp_name,examples);
			}
			
			KNNOMExample prev_ex = null;
			for(Object ex_o:opponent.getChildren("KNNOMExample")) {
				KNNOMExample ex = null;
				if (prev_ex == null) {
					ex = KNNOMExample.loadfromXML((Element)ex_o);
					examples.add(ex);
				} else {
					ex = KNNOMExample.loadDifferenceFromXML((Element)ex_o,prev_ex);
					examples.add(ex);
				}
				prev_ex = ex;
			}
			
			System.out.println("KNNOponentModel: " + examples.size() + " examples for opponent " + opp_name);
		}
	}

}
