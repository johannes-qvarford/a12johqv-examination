package towers.objects;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import towers.Action;
import towers.TMap;
import towers.Towers;


public abstract class Entity 
{
	public static int next_ID = 10000;
	
	public String entityID ="";
	public String owner;
	protected static HashMap<String,List<Action>> executableActions = new HashMap<String,List<Action>>();
	
	public Entity(String iEntityID, String iOwner)
	{
		entityID = iEntityID;
		owner = iOwner;
	}
	
	public Entity()
	{
	}
	
	public String getowner()
	{
		return owner;
	}

	public void setowner(String iOwner)
	{
		owner = iOwner;
	}

	public String getentityID()
	{
		return entityID;
	}
	
	public void setentityID(String iEntityID)
	{
		entityID = iEntityID;
	}

	public Entity( Entity incoming)
	{
		this.entityID = incoming.entityID;
		this.owner = incoming.owner;
	}
	
	public abstract Object clone();
		
	/*
	public boolean greater(Entity incoming)
	{
		boolean returnFlag = true;
		//System.out.println(this.getClass());
		//System.out.println(incoming.getClass());
		try
		{
		if ( this.getClass().equals(incoming.getClass()) )
			{
				//System.out.println("Classes are equal: Can Compare");
				Field[] fx = this.getClass().getFields();
				
				for ( Field a : fx)
				{
					String fieldType = a.getType().toString(); 
					//System.out.println(" ---->" + fieldType);
					
					if ( fieldType.equals("class java.lang.String") )
						//do nothing u MORON!!!
						continue;
					
					if ( fieldType.equals("boolean") )
						//U tell me how to make true > false, and I'l give u the Nobel prize for PEACE!
						continue;
					
					if ( fieldType.equals("char") )
					{
						if ( a.getChar(this) <= a.getChar(incoming))
							returnFlag = returnFlag && false;
						continue;
					}
					
					//System.out.println("Is " + a.getDouble(this) + " > " + a.getDouble(incoming));
					
					if ( a.getDouble(this) <= a.getDouble(incoming) )
					{
						//System.out.println("lesser than satisfied...");
						returnFlag = returnFlag && false;
						//still wanna run this thru, as after one touch here, you have to break out
						//as it makes no sense in continuing on
						//break;
					}
					
					
				}
				
					
			}
		
		return returnFlag;
		}
		catch ( Exception e )
		{
			System.out.println("Now you're screwed! " + e);
		}
		return true;
	}

	public boolean lesser(Entity incoming)
	{
		boolean returnFlag = true;
		//System.out.println(this.getClass());
		//System.out.println(incoming.getClass());
		try
		{
		if ( this.getClass().equals(incoming.getClass()) )
			{
				//System.out.println("Classes are equal: Can Compare");
				Field[] fx = this.getClass().getFields();
				
				for ( Field a : fx)
				{
					String fieldType = a.getType().toString(); 
					//System.out.println(" ---->" + fieldType);
					
					if ( fieldType.equals("class java.lang.String") )
						//do nothing u MORON!!!
						continue;
					
					if ( fieldType.equals("boolean") )
						//U tell me how to make true > false, and I'l give u the Nobel prize for PEACE!
						continue;
					
					if ( fieldType.equals("char") )
					{
						if ( a.getChar(this) >= a.getChar(incoming))
							returnFlag = returnFlag && false;
						continue;
					}
					
					//System.out.println("Is " + a.getDouble(this) + " > " + a.getDouble(incoming));
					
					if ( a.getDouble(this) >= a.getDouble(incoming) )
					{
						//System.out.println("lesser than satisfied...");
						returnFlag = returnFlag && false;
						//still wanna run this thru, as after one touch here, you have to break out
						//as it makes no sense in continuing on
						//break;
					}
					
					
				}
				
					
			}
		
		return returnFlag;
		}
		catch ( Exception e )
		{
			System.out.println("Now you're screwed! " + e);
		}
		return true;
	}

	public boolean equals(Entity incoming)
	{
		boolean returnFlag = true;
		//System.out.println(this.getClass());
		//System.out.println(incoming.getClass());
		try
		{
		if ( this.getClass().equals(incoming.getClass()) )
			{
				//System.out.println("Classes are equal: Can Compare");
				Field[] fx = this.getClass().getFields();
				
				for ( Field a : fx)
				{
					String fieldType = a.getType().toString(); 
					//System.out.println(" ---->" + fieldType);
					
					if ( fieldType.equals("class java.lang.String") )
					{
						//do nothing u MORON!!! <--- I Take that back! I'm doing Something now!
						//System.out.println("Comparing strings...");
						if ( a.get(this).equals(a.get(incoming)))
							;
						else
							returnFlag = returnFlag && false;
						
						continue;
					}
					
					if ( fieldType.equals("boolean") )
						//U tell me how to make true > false, and I'l give u the Nobel prize for PEACE!
						continue;
					
					if ( fieldType.equals("char") )
					{
						if ( a.getChar(this) != a.getChar(incoming))
							returnFlag = returnFlag && false;
						continue;
					}
					
					//System.out.println("Is " + a.getDouble(this) + " > " + a.getDouble(incoming));
					
					if ( a.getDouble(this) != a.getDouble(incoming) )
					{
						//System.out.println("lesser than satisfied...");
						returnFlag = returnFlag && false;
						//still wanna run this thru, as after one touch here, you have to break out
						//as it makes no sense in continuing on
						//break;
					}
					
					
				}
				
					
			}
		
		return returnFlag;
		}
		catch ( Exception e )
		{
			System.out.println("Now you're screwed! " + e);
		}
		return true;
	}
	*/
	
	static private HashMap<String,List<String>> m_listOfFeaturesHash = new HashMap<String,List<String>>();
	
	public List<String> listOfFeatures() {
		Class<?> c = getClass();
		String c_name = c.getSimpleName();
		List<String> features;
		
		features = m_listOfFeaturesHash.get(c_name);
		
		if (features==null) {
			features = new LinkedList<String>();
			do {
				for(Method m:c.getDeclaredMethods()) {
					if (m.getName().startsWith("get") &&
						!m.getName().startsWith("get_")) {
						features.add(m.getName().substring(3));
					}
				}
				c = c.getSuperclass();
			}while(c!=null && !c.getSimpleName().equals("Object"));
			m_listOfFeaturesHash.put(c_name, features);			
		}		
		return features;		
	}
	
	public Object featureValue(String feature) {
		
		if (feature.equals("type")) return getClass().getSimpleName();
		if (feature.equals("id")) return entityID;
		
		Method m;
		try {
			m = getClass().getMethod("get"+feature, (Class[])null);
			if (m!=null) return m.invoke(this, (Object[])null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	public void setfeatureValue(String feature,String value) {
		Method m;
		try {
			m = getClass().getMethod("set"+feature, String.class);
			m.invoke(this, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean equivalents(Entity e) {
		if (!getClass().equals(e.getClass())) return false;
		for(String f:listOfFeatures()) {
			
			// We require them to be the same in all features except the ID:
			if (!f.equals("entityID")) {
				Object v = featureValue(f);
				if (v==null) {
					if (e.featureValue(f)!=null) return false;
				} else {
					if (!v.equals(e.featureValue(f))) return false;
				}
			}
		}
		return true;
	}
	
	public String toString() {
		String out = "Entity(" + entityID +"): " + getClass().getSimpleName() + " [ ";				
		for(String f:listOfFeatures()) 
			out += "(" + f + " = " + featureValue(f) + ") ";
			
		return out + "]";
	}

	
	public abstract String toXMLString();
	
	public abstract gatech.mmpm.Entity toD2Entity();
	

	/*
	public String toXMLString() {
		String out = "<entity id=\"" + entityID + "\">\n" + 
					 "  <type>" + getClass().getSimpleName() + "</type>\n";				
		for(String f:listOfFeatures()) 
			if (featureValue(f)!=null) {
				// only save it if the value is different than the default value:
				try {
					Entity e = this.getClass().newInstance();
					if (!featureValue(f).equals(e.featureValue(f))) {
						out += "  <" + f + ">" + featureValue(f) + "</" + f + ">\n";
					} // if
				} catch(Exception e) {
					out += "  <" + f + ">" + featureValue(f) + "</" + f + ">\n";
				}
					
			}
			
		return out + "</entity>";
	}	
	*/
	
	protected void addExecutableAction(Action action) {
		String cn = this.getClass().getName();
		List<Action> la = executableActions.get(cn);
		
		if (la==null) {
			la = new LinkedList<Action>();
			executableActions.put(cn,la);
		}
		
		la.add(action);
		
	}
	

	
	public static double entityClassSimilarity(Entity e1,Entity e2) {
		// The similarity computation is:
		// a: distance from root (Entity) to antiunification
		// b: distance from antiunificaiton to c1
		// c: distance from antiunification to c2
		// the similarity is: sym = a*2/(a*2+b+c)
		
		double a = 0, b = 0, c = 0;
		Class<?> antiunification = null,c1,c2;
		
		c1 = e1.getClass();
		c2 = e2.getClass();
		
		antiunification = c1;
		
//		System.out.println("entityClassSimilarity, c1: " + c1.getName());
//		System.out.println("entityClassSimilarity, c2: " + c2.getName());
		
//		System.out.println("entityClassSimilarity, au: " + antiunification.getName());
		while (!antiunification.isInstance(e2)) {
			antiunification = antiunification.getSuperclass();
//			System.out.println("entityClassSimilarity, au: " + antiunification.getName());
			b++;
		}
		while(c2!=antiunification) {
			c2 = c2.getSuperclass();
			c++;
		}
		
		while(antiunification!=Entity.class) {
			antiunification = antiunification.getSuperclass();
			a++;
		}
		
//		System.out.println("entityClassSimilarity, c1: " + c1.getName()+ " c2: " + c2.getName() + ", a: " + a + " b: " + b + " c: " + c + " similarity: " + ((2*a)/(2*a + b + c)));

		return (2*a)/(2*a + b + c);
	}
	
	
	public static double entitySimilarity(Entity e1,Entity e2) {
		double baseSimilarity = entityClassSimilarity(e1, e2);
		List<String> fl1 = e1.listOfFeatures();
		List<String> fl2 = e2.listOfFeatures();
		double featureSimilarity = 0;
		int nCommonFeatures = 0;
		
		for(String f:fl1) {
			if (fl2.contains(f)) {
				Object fv1 = e1.featureValue(f);
				Object fv2 = e2.featureValue(f);
				
//				System.out.println("entitySimilarity: f: " + f + " fv1: " + fv1 + " fv2: " + fv2);
				
				if (fv1==null || fv2==null) {
					// we don't know, so we don't count this feature
				} else {
					if (fv1.equals(fv2)) featureSimilarity++;
					nCommonFeatures++;
				}
			}
		}
		
		if (nCommonFeatures ==0) {
			featureSimilarity = 1; 
		} else {
			featureSimilarity = featureSimilarity/nCommonFeatures;
		}

//		System.out.println("entitySimilarity: " + baseSimilarity*featureSimilarity);
		
		return baseSimilarity*featureSimilarity;
	}

	public boolean cycle(TMap map, Towers game, List<Action> actions) throws IOException, ClassNotFoundException {
		return true;
	}
	
}
