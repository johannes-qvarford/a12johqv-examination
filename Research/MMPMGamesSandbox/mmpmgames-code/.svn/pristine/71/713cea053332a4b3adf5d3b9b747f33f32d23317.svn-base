package bc.objects;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public abstract class BCEntity 
{
	static int next_id = 0;

	public String entityID ="";
	//public boolean active;
	public String owner;
	
	public BCEntity(String iEntityID, String iOwner)
	{
		entityID = iEntityID;
		owner = iOwner;
	}
	
	public BCEntity()
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

	public BCEntity( BCEntity incoming)
	{
		this.entityID = incoming.entityID;
		this.owner = incoming.owner;
	}
	
	public abstract Object clone();
		
	public boolean greater(BCEntity incoming)
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

	public boolean lesser(BCEntity incoming)
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

	public boolean equals(BCEntity incoming)
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
	
	public boolean equivalents(BCEntity e) {
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
	
	public String toXMLString() {
		String out = "<entity id=\"" + entityID + "\">\n" + 
					 "  <type>" + getClass().getSimpleName() + "</type>\n";				
		for(String f:listOfFeatures()) 
			if (featureValue(f)!=null) {
				// only save it if the value is different than the default value:
				try {
					BCEntity e = this.getClass().newInstance();
					if (!featureValue(f).equals(e.featureValue(f))) {
						out += "  <" + f + ">" + featureValue(f) + "</" + f + ">\n";
					} // if
				} catch(Exception e) {
					out += "  <" + f + ">" + featureValue(f) + "</" + f + ">\n";
				}
					
			}
			
		return out + "</entity>\n";
	}	

}
