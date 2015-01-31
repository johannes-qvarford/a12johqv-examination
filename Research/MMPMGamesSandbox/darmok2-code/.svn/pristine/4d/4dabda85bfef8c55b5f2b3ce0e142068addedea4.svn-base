/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.plans;

import gatech.mmpm.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Class that stores all the classes that implements actions
 * in the current domain.
 * <p>
 * All the data is static.
 * 
 * @author Marco Antonio Gomez Martin
 */
public class ActionLibrary {
	
	static List<Class<? extends Action>> m_actionClassList = new LinkedList<Class<? extends Action>>();

	/**
	 * Add a new actions class.
	 * @param actionClass Class that represents the action. Should
	 * inherit from plans.Action and have a constructor that
	 * receives two strings.
	 * @throws ActionLibraryException when the class is not a valid
	 * Action.
	 */
	public static void addAction(Class<? extends Action> actionClass) throws ActionLibraryException {
		
		// Check whether the actionClass is a plans.Action class.
		// Pedro: I'm not sure if this is needed now that generics are
		// used in the prototype.
		Class superclass = actionClass.getSuperclass();
		while (superclass != null) {
			if (superclass == gatech.mmpm.Action.class)
				// c is valid
				break;
			superclass = superclass.getSuperclass();
		}
		
		if (superclass == null)
			// We reached Object class without finding D2 base entity
			throw new ActionLibrary.ActionLibraryException(
					"Class not valid. " + 
					actionClass.getName() + " is not a valid D2 action.");
		
		// Check if it has a valid constructor
		try {
			actionClass.getConstructor(String.class, String.class);
		} catch (java.lang.NoSuchMethodException ex) {
			throw new ActionLibraryException(
					"Action class not valid. It has no public constructor(String, String).");
		}
		
		// Register
		m_actionClassList.add(actionClass);
	}
	
	/**
	 * Creates an action given its name. The method needs the name
	 * of the action, and the values of the two arguments that
	 * will be passed to the Action constructor.
	 * @param name Name of the action, that should match with the name
	 * of the class that implemented it (whithout the packages).
	 * @param entityId First parameter of the constructor of the Action.
	 * @param actor Second parameter.
	 * @return The object created. The condition
	 * <code>returnObject.getClass().getSimpleName() == name</code>
	 * should be true.
	 * @throws ActionLibraryException if the action cannot be created,
	 * usually because of no class with this name has been registered
	 * in the library.
	 */
	public static gatech.mmpm.Action createAction(
								String name,
								String entityId,
								String actor) throws ActionLibraryException {
		
		for (Class<? extends Action> c : m_actionClassList) {
			if (c.getSimpleName().equals(name)) {
				// Look for the constructor
				try {
					java.lang.reflect.Constructor<? extends Action> ctor;
					ctor = c.getConstructor(String.class, String.class);
					Object ret = ctor.newInstance(new Object[]{entityId, actor});
					return (Action)ret;
				} catch (Exception ex) {
					// We should never have this exception,
					// because we checked the existence of the
					// constructor in addAction... unless the
					// constructor itself raises it.
					throw new ActionLibraryException(
							"Error creating the action '" + name + "'. "+
							"Have you registered it in the ActionLibrary?\n"+
							ex.getMessage());
				}
			}
		}
		
		throw new ActionLibraryException(
				"Error creating the action '" + name + "'. " +
				"Action not found. " +
				"Have you registered it in the ActionLibrary?\n");
	}
	
	/**
	 * Exception thrown when some error happens using the
	 * ActionLibrary class.
	 *  
	 * @author Marco Antonio Gomez Martin
	 */
	public static class ActionLibraryException extends Exception {
		
		public ActionLibraryException(String msg) {
			super(msg);
		}
		
		static final long serialVersionUID = 0x3494814;
	}
}
