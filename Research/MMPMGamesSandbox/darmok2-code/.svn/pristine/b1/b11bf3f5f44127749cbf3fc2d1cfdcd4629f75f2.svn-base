/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.execution.adaptation.parameters;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import gatech.mmpm.Entity;
import gatech.mmpm.util.Pair;

public class EntityWeights {
	static class ClassEntry {
		Class<? extends gatech.mmpm.Entity> m_class = null;
		java.lang.reflect.Constructor<?> m_constructor = null;
		String m_name = null;
		ClassEntry m_super = null;
		HashMap<String,ClassEntry> m_children = new HashMap<String,ClassEntry>();
		double m_weight = 0.0;
		char m_shortName = 0;
	}

	static HashMap<String,ClassEntry> s_entries = new HashMap<String,ClassEntry>();
	static HashMap<Character,ClassEntry> s_entriesShortNames = new HashMap<Character,ClassEntry>();
	
	// The "plain" name of the class. Ex. Vector (instead of java.util.Vector).
	static HashMap<String,Class<? extends gatech.mmpm.Entity> > s_entriesSimpleName = new HashMap<String, Class<? extends gatech.mmpm.Entity> >();
	
	// Returns true if the class had not been added before.
	// The class should inherit from domain.Entity and should have
	// one constructor with two String arguments, but this
	// is not checked by this method.
	public static boolean addClass(Class<? extends gatech.mmpm.Entity> c,char shortName) {
		ClassEntry oldEntry = s_entries.get(c.getName());
		
		if (oldEntry == null) {
			ClassEntry ce = addClassInternal(c);
			if (shortName!=0) {
				ce.m_shortName = shortName;
				s_entriesShortNames.put(shortName,ce);
			}
			s_entriesSimpleName.put(c.getSimpleName(), c);
			return true;
		} else {
			if (s_entriesShortNames.get(c.getName()) != null)
				return false;
			else {
				if (shortName != 0)
					s_entriesShortNames.put(shortName, oldEntry);
				s_entriesSimpleName.put(c.getSimpleName(), c);
				return true;
			}
		}
	}

        public static List<String> getClasses() {
            List<String> l = new LinkedList<String>();
            for(String c:s_entries.keySet()) l.add(c);
            return l;
        }
	
	static ClassEntry addClassInternal(Class<? extends gatech.mmpm.Entity> c) {
		ClassEntry e = s_entries.get(c.getName());
		
		if (e==null) {
			e = new ClassEntry();
			e.m_class = c;
			try {
				e.m_constructor = c.getConstructor(String.class, String.class);
			} catch (Exception exc) {
				e.m_constructor = null;
			}
			e.m_name = c.getName();
			s_entries.put(c.getName(), e);
			
			if (c==Entity.class) {
				e.m_super = null;
			} else {
				e.m_super = addClassInternal((Class<? extends gatech.mmpm.Entity>)c.getSuperclass());
			}
			
			if (e.m_super!=null) e.m_super.m_children.put(c.getName(),e);			
		}
		
		return e;
	}
	

	/**
	 * Adds a fake entry for something that is not a class (for instance for the edges of the map)
	 */ 
	public static boolean addFakeClass(String name,double weight) {
		ClassEntry oldEntry = s_entries.get(name);
		
		if (oldEntry == null) {
			addFakeClassInternal(name,weight);
			return true;
		}
		return false;
	}

	
	static ClassEntry addFakeClassInternal(String name,double weight) {
		ClassEntry e = s_entries.get(name);
		
		if (e==null) {
			e = new ClassEntry();
			e.m_class = null;
			e.m_constructor = null;
			e.m_name = name;
			s_entries.put(name, e);	
			e.m_super = null;
			e.m_weight = weight;
		}
		
		return e;
	}

	
	public static void computeWeights(Class<? extends gatech.mmpm.Entity> c,double weight) {
		ClassEntry root = s_entries.get(c.getName());
		
		computeWeightsInternal(root,weight);		
	}
	
	
	public static void printWeights(Class<? extends gatech.mmpm.Entity> c) {
		ClassEntry root = s_entries.get(c.getName());
		List<Pair<ClassEntry,Integer>> l = new LinkedList<Pair<ClassEntry,Integer>>();
		l.add(new Pair<ClassEntry,Integer>(root,0));
		System.out.println("Resulting Class wrights:");
		while(l.size()>0) {
			Pair<ClassEntry,Integer> e = l.remove(0);
			for(int i=0;i<e.getSecond();i++) System.out.print("  ");
			System.out.println(e.getFirst().m_name + ": " + e.getFirst().m_weight);
			for(ClassEntry e2:e.getFirst().m_children.values()) {
				l.add(0,new Pair<ClassEntry,Integer>(e2,e.getSecond()+1));
			}
		}		
	}
	
	
	static void computeWeightsInternal(ClassEntry root,double w) {
		Set<String> sub;
		if (root==null) return;

		sub = root.m_children.keySet();

		{
			double f = 1.0 / (sub.size() + 1);
			root.m_weight = f*w;
			for(String cn:sub) {
				computeWeightsInternal(root.m_children.get(cn),(1.0 / (sub.size() + 1))*w);
			}			
		}
/*		
		if (sub.size()==1) {
			double f = 0.5;
			root.m_weight = f*w;
			for(String cn:sub) {
				computeWeightsInternal(root.m_children.get(cn),(1-f)*w);
			}
		} else if (sub.size()>1) {
			double f = 0.33;
			root.m_weight = f*w;
			for(String cn:sub) {
				computeWeightsInternal(root.m_children.get(cn),((1-f)*w)/sub.size());
			}
		} else {
			root.m_weight = w;
		}
*/
	}
	
	
	// Note: this class assumes that the class is present, it's up to the code that uses this class to ensure that.
	public static double getWeight(Class<? extends gatech.mmpm.Entity> c) {
		return s_entries.get(c.getName()).m_weight;
	}
		
	public static double getWeight(String c) {
		return s_entries.get(c).m_weight;
	}
}
