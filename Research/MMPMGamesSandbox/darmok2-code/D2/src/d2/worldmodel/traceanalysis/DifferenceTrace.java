/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.worldmodel.traceanalysis;

import gatech.mmpm.Entity;
import gatech.mmpm.Entry;
import gatech.mmpm.GameState;
import gatech.mmpm.Trace;

import java.util.LinkedList;
import java.util.List;


public class DifferenceTrace {
	int m_id;

	GameState m_initialState = null;

	List<DifferenceTraceEntry> m_entries = new LinkedList<DifferenceTraceEntry>();

	/**
	 * Finds all the differences between game states in a trace and sets them to the m_entries.
	 * @param t
	 */
	public DifferenceTrace(Trace t) {
		GameState previous_gs = null;

		m_id = t.getID();

		for (Entry e : t.getEntries()) {
			if (m_initialState == null) {
				m_initialState = (GameState) e.getGameState().clone();
			}

			if (previous_gs != null || e.getActions() != null) {
				DifferenceTraceEntry de = (previous_gs != null ? findDifferences(previous_gs, e
						.getGameState(), e.getTimeStamp()) : null);

				if (e.getActions() != null) {
					if (de == null)
						de = new DifferenceTraceEntry(e.getTimeStamp());
					de.m_actions.addAll(e.getActions());
				}

				if (de != null)
					m_entries.add(de);
			}

			previous_gs = e.getGameState();
		}

	}

	/**
	 * Finds the differences between s1 and s2.
	 * 
	 * @param s1
	 * @param s2
	 * @param timeStamp
	 *            game cycle the GameStates exist at?
	 * @return the differences, or null if there are no differences.
	 */
	public static DifferenceTraceEntry findDifferences(GameState s1, GameState s2, int timeStamp) {
		DifferenceTraceEntry de = new DifferenceTraceEntry(timeStamp);
		List<Difference> l = findDifferences(s1, s2);
		de.m_differences.addAll(l);
		if (de.m_differences.size() == 0)
			de = null;
		return de;
	}

	/**
	 * finds all differences between s1 and s2.
	 * 
	 * @param s1
	 *            the first game state.
	 * @param s2
	 *            the second game state.
	 * @return the different entities and map entities.
	 */
	public static List<Difference> findDifferences(GameState s1, GameState s2) {
		List<Difference> l = new LinkedList<Difference>();

		// Find differences in the entities:
		for (Entity e1 : s1.getAllEntities()) {
			Entity e2 = s2.getEntity(e1.getentityID());
//TODO check type
			if (e2 == null) {
				// Entity disappeared:
				l.add(new DisappearedEntityDifference((Entity) e1.clone(), false));
			} else {
				// Check for feature changes:
				for (String f : e2.listOfFeatures()) {
					Object v1, v2;

					v1 = e1.featureValue(f);
					v2 = e2.featureValue(f);

					if ((v1 == null && v2 != null) || (v1 != null && !v1.equals(v2))) {
						l.add(new ValueChangeDifference((Entity) e1.clone(), f, v2));
					}
				}
			}
		}
		for (Entity e2 : s2.getAllEntities()) {
			Entity e1 = s1.getEntity(e2.getentityID());

			if (e1 == null) {
				// New entity created:
				l.add(new NewEntityDifference((Entity) e2.clone(), false));
			}
		}

		// Find differences in the map:
		for (int i = 0; i < s1.getMap().size(); i++) {
			Entity e1 = s1.getMap().get(i);
			Entity e2 = s2.getMap().get(i);

			if (e1 != null && e2 == null) {
				// Entity disappeared:
				l.add(new DisappearedEntityDifference((Entity) e1.clone(), true));
			}

			if (e1 == null && e2 != null) {
				l.add(new NewEntityDifference((Entity) e2.clone(), true));
			}

			if (e1 != null && e2 != null) {

				if (e1.getClass().getSimpleName().equals(e2.getClass().getSimpleName())) {
					// Check for feature changes:
					for (String f : e2.listOfFeatures()) {
						Object v1, v2;

						v1 = e1.featureValue(f);
						v2 = e2.featureValue(f);

						if ((v1 == null && v2 != null) || (v1 != null && !v1.equals(v2))) {
							l.add(new ValueChangeDifference((Entity) e1.clone(), f, v2));
						}
					}
				} else {
					// One entity disappeared and another one substitutes it:
					l.add(new DisappearedEntityDifference((Entity) e1.clone(), true));
					l.add(new NewEntityDifference((Entity) e2.clone(), true));
				}
			}
		}

		return l;
	}

	public GameState getInitialGameState() {
		return m_initialState;
	}

	public List<DifferenceTraceEntry> getEntries() {
		return m_entries;
	}

	public String toString() {
		String out = "Differences Trace " + m_id + "\n";

		out += "Initial State:\n" + m_initialState.toString();

		for (DifferenceTraceEntry e : m_entries) {
			out += e.toString() + "\n";
		}

		return out;
	}
}
