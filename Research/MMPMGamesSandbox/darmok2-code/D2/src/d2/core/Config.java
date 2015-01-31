/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.core;

import gatech.mmpm.ConfigurationException;
import gatech.mmpm.sensor.Sensor;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import d2.util.Log;

/**
 * Class used to configure the general behaviour of D2 library. 
 * 
 * This class should be used *before* any attempt to learn from
 * traces or load a D2 AI from file.
 * 
 * Examples of use: learning process (d2.tools.Learner could be
 * used instead).
 *    
 * <code>
 * try {
 *    d2.core.Config.setLogFileName("log/D2.log");
 *    d2.core.Config.setDomain(new towersreloaded.d2.TowersDomain());
 * } catch (d2.core.ConfigurationException ex) {
 *	  System.err.println("Error while configuring D2: \n" +
 *						 ex.getMessage());
 *	  ex.printStackTrace();
 *	  return;
 * };
 * IMEExecutor ai;
 * Learn l = new Learn();
 * learn.config(....);
 * ai = l.learn(traceFiles, player);
 * </code>
 * 
 * @author Marco Antonio Gomez Martin and David Llanso
 * 
 * Modifications:
 * Santi Ontanon: 
 * - removed the world model from here, since that assumed that there is a 
 *   single WorldModel for every single instance of D2 running in the JVM. Now
 *   the WorldModel is stored in each one of the AIs.
 * 
 */
public class Config {

	protected static gatech.mmpm.IDomain m_domain = null;

//	protected static worldmodel.WorldModel m_worldModel = null;

	protected static Sensor m_winGoal = null;

	protected static java.util.Properties m_configuration = null;

	////
	// Methods used to configure D2
	////

	/**
	 * Set the domain used by D2. This method should be called
	 * only once at the beggining of the execution; otherwise
	 * one exception will be thrown.
	 * @param domain Domain used by D2.
	 * @throws ConfigurationException when the domain is not
	 * correct or the user try to change the domain, once one was set.
	 */
	public static void setDomain(gatech.mmpm.IDomain domain) 
	throws ConfigurationException {

		if (domain == null)
			throw new ConfigurationException("Not 'null' domain allowed in D2!");

		if (domain == m_domain)
			// We have done that before
			return;

		if ((m_domain != null) && (m_domain != domain)) {
			System.err.println("Domain changes are not allowed.");
			return;
		}

		// Parameters are correct, we can do our job...
		m_domain = domain;

		// Initialize the domain entities
		initDomainEntities(domain);

		// Initialize the domain sensors
		initDomainSensors(domain);

		// Initialize the domain goals
		initDomainGoals(domain);

		// Initialize the actions
		initDomainActions(domain);

		// Init win goal
		m_winGoal = domain.getWinGoal();

		// Init world model
//		m_worldModel = domain.getWorldModel();

//		if (m_configuration != null)
//			m_worldModel.config(m_configuration);
	}


	public static void setDomain(String domainClass) 
	throws ConfigurationException {
		// Try to load the class
		Class<?> c;
		try {
			c = Class.forName(domainClass);
		} catch (ClassNotFoundException ex) {
			ConfigurationException e;
			e = new ConfigurationException("Class with the domain not found. Ensure you have added it to the CLASSPATH.");
			e.initCause(ex);
			throw e;
		}

		// Ensure this is a IDomain class...
		Class []interfaces = c.getInterfaces();

		boolean iDomainFound = false;
		for (Class i : interfaces) {
			if (i.equals(gatech.mmpm.IDomain.class))
				iDomainFound = true;
		}

		if (!iDomainFound)
			throw new ConfigurationException("Domain class is not a valid " + gatech.mmpm.IDomain.class.getName() + "class.");

		// Create an instance of the domain
		gatech.mmpm.IDomain domain;
		try {
			domain = (gatech.mmpm.IDomain)c.newInstance();
		} catch (Exception ex) {
			ConfigurationException e;
			e = new ConfigurationException("Impossible to create an instance of the domain. Has the class a default constructor?");
			e.initCause(ex);
			throw e;
		}

		// Finally... es can configure D2!
		d2.core.Config.setDomain(domain);
	}


	public static void setLogFileName(String fileName) throws ConfigurationException {
		try {
			d2.util.Log.setLogFileName(fileName);
		} catch (java.io.IOException ex) {
			ConfigurationException raised;
			raised =new ConfigurationException("Error opening the file.\n" + ex.getMessage());
			raised.initCause(ex);

			throw raised; 
		}
	}

	/**
	 * I don't like it, but it is faster (dirty, but faster).
	 * @param prop Properties from the D2 configuration file... ARGH
	 * @note This should be removed. The learn process receives the
	 * Properties, but we still do not have the concept of "Play"
	 * phase.
	 */
	public static void setProperties(java.util.Properties prop) {
		m_configuration = (java.util.Properties)prop.clone();
//		if (m_worldModel != null)
//			m_worldModel.config(m_configuration);
	}

	////
	// Get methods
	////

	/**
	 * Get the properties.
	 * @return Properties loaded from the config file.
	 */
	public static java.util.Properties getProperties() {
		return m_configuration;
	}

	/**
	 * Get the domain name.
	 * @return Domain name, or null if it has not been set.
	 */
	public static String getDomainName() {
		return (m_domain == null) ? null : m_domain.getName();
	}
	
	/**
	 * Get the domain.
	 * @return Domain, or null if it has not been set.
	 */
	public static gatech.mmpm.IDomain getDomain() {
		return m_domain;
	}

	/**
	 * Get the win goal.
	 * @return Win goal, or null if the domain has not been set.
	 */
	public static gatech.mmpm.sensor.Sensor getWinGoal() {
		return m_winGoal;
	}

	/**
	 * Get the world model. this method retrieves the corresponding
	 * WorldModel class from the name specified in the IDomain.
	 * Notice that every WorldModel has to be now in the
	 * d2.core.worldmodel packet and its name must be:
	 * 		gamename+"WorldModel.java"
	 * where gamename is the name specified in the IDomain.
	 * @return The generated WorldModel. null if there were any 
	 * error.
	 */
	/*
	public static d2.worldmodel.WorldModel getWorldModel() 
	{
		String className = "d2.worldmodel.domainspecific."+getDomainName()+"WorldModel";
		try 
		{
			Class<? extends d2.worldmodel.WorldModel> wmClass = null;
			Class<?> askedClass;
			askedClass = Class.forName(className);
	
			//Ensure that base class inherits from WorldModel.
			wmClass = askedClass.asSubclass(d2.worldmodel.WorldModel.class);
			
			return wmClass.newInstance();
		} 
		catch (java.lang.ClassCastException e) 
		{
			System.out.println(className + 
					" does not extend WorldModel class.");
			e.printStackTrace();
			return null;
		}
		catch (ClassNotFoundException e) 
		{
			System.out.println(className + 
					" class does not exist.");
			e.printStackTrace();
			return null;
		} 
		catch (Exception e) 
		{
			System.out.println(className + 
			" cannot be instantiated.");
			e.printStackTrace();
			return null;
		} 
	}
	*/

	////
	// NOT PUBLIC METHODS
	////

	/**
	 * Get the list of the domain entities and register them
	 * into the system.
	 * 
	 * @param d Domain used.
	 */
	private static void initDomainEntities(gatech.mmpm.IDomain d) throws ConfigurationException {

		Log.println("D2 - Initializing domain entites.");

		Class<? extends gatech.mmpm.Entity>[] classes = d.getEntities();

		if (classes == null) {
			Log.println("D2 - Domain sensors initialized (no entities found!).");
			return;
		}

		// Check if every entity is a subclass of D2 base entity
		// and whether it has a constructor with two String args.
		for (Class<? extends gatech.mmpm.Entity> c : classes) {
			Class<?> superclass = c.getSuperclass();
			while (superclass != null) {
				if (superclass == gatech.mmpm.Entity.class)
					// c is valid
					break;
				superclass = superclass.getSuperclass();
			}

			if (superclass == null)
				// We reached Object class without finding D2 base entity
				throw new ConfigurationException(
						"Domain entities not valid. " + 
						c.getName() + " is not a valid D2 entity.");

			// Check if it has a valid constructor
			try {
				c.getConstructor(String.class, String.class);
			} catch (java.lang.NoSuchMethodException ex) {
				throw new ConfigurationException(
				"Action class not valid. It has no public constructor(String, String).");
			}
		}

		// Register the classes and its actions
		for (Class<? extends gatech.mmpm.Entity> c : classes) {
			// Get the short name using public static char shortName()
			// method
			char shortName;

			// Register
			try {
				Method getShortName = c.getMethod("shortName");
				if (!Modifier.isStatic(getShortName.getModifiers()) ||
						(getShortName.getReturnType() != char.class))
					throw new ConfigurationException("Unexpected error. Has you refactored the domain.Entity.shortName Method?");

				Object ret = getShortName.invoke(null);
				shortName = (Character)ret; 
			} catch (Exception ex1) {
				throw new ConfigurationException("Unexpected error. Has you refactored the domain.Entity.shortName Method?");
			}

			//			System.out.println("Registering class " + c.getName() + " with short name " + shortName);
			d2.execution.adaptation.parameters.EntityWeights.addClass(c, shortName);

			// Entity.registerActions()
		/*	try {
				Method registerActions = c.getMethod("registerActions");
				if (!Modifier.isStatic(registerActions.getModifiers()) ||
						(registerActions.getReturnType() != void.class))
					throw new ConfigurationException("Unexpected error. Have you refactored the domain.Entity.registerActions Method?");

				registerActions.invoke(null);
			} catch (Exception ex1) {
				ex1.printStackTrace();
				throw new ConfigurationException("Unexpected error. Have you refactored the domain.Entity.registerActions Method? (of " + c.getName() + ")");
			}*/
		}


		d2.execution.adaptation.parameters.EntityWeights.computeWeights(gatech.mmpm.PhysicalEntity.class, 1.0);
		Log.println("D2 - Domain entites initialized.");
	}

	/**
	 * Get the list of game's sensors, and register them into
	 * the system.
	 * 
	 * @param d Domain used.
	 */
	private static void initDomainSensors(gatech.mmpm.IDomain d) {

		Log.println("D2 - Initializing domain sensors.");

		gatech.mmpm.sensor.Sensor[] sensorsList = d.getSensors();

		if (sensorsList == null) {
			Log.println("D2 - Domain sensors initialized (no sensors found).");
			return;
		}

		for (gatech.mmpm.sensor.Sensor s : sensorsList) 
			gatech.mmpm.sensor.SensorLibrary.registerSensor(s);

		Log.println("D2 - Domain sensors initialized.");
	}

	/**
	 * Get the list of game's goals, and register them into
	 * the system.
	 * 
	 * @param d Domain used.
	 */
	private static void initDomainGoals(gatech.mmpm.IDomain d) {

		Log.println("D2 - Initializing domain goals.");

		gatech.mmpm.sensor.Sensor[] goalsList = d.getGoals();

		if (goalsList == null) {
			Log.println("D2 - Domain sensors initialized (no sensors found).");
			return;
		}
		
		for (gatech.mmpm.sensor.Sensor g : goalsList)
			gatech.mmpm.sensor.SensorLibrary.registerGoal(g);

		Log.println("D2 - Domain goals initialized.");
	}



	/**
	 * Get the list of game's actions, and register them into
	 * the system.
	 * 
	 * @param d Domain used.
	 */
	private static void initDomainActions(gatech.mmpm.IDomain d) throws ConfigurationException {

		Log.println("D2 - Initializing domain actions.");

		Class[] actionClassList = d.getActions();

		if (actionClassList == null) {
			Log.println("D2 - Domain actions initialized (no actions found).");
			return;
		}

		for (Class actionClass : actionClassList)
			try {
				d2.plans.ActionLibrary.addAction(actionClass);			
			} catch (d2.plans.ActionLibrary.ActionLibraryException ex) {
				throw new ConfigurationException("Domain action not valid: " + actionClass.getName() + ":\n" + ex.getMessage());
			}

			Log.println("D2 - Domain actions initialized.");
	}	

}
