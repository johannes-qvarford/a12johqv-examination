/*******************************************************************
 *                       MACHINE GENERATED CODE                    *
 *                            DO NOT EDIT                          *
 *        FIX THE SOURCE XML INSTEAD, AND REGENERATE IT AGAIN      *
 *                                                                 *
 *                                                                 *
 * Tool: gatech.mmpm.tools.DomainGenerator                         *
 *                                                                 *
 * Organization: Georgia Institute of Technology                   *
 *               Cognitive Computing Lab (CCL)                     *
 * Authors:      Pedro Pablo Gomez Martin                          *
 *               Marco Antonio Gomez Martin                        *
 * Based on previous work of:                                      *
 *               Jai Rad                                           *
 *               Prafulla Mahindrakar                              *
 *               Santi Ontañón                                     *
 *******************************************************************/


package towers.mmpm.entities;

import gatech.mmpm.Action;


import java.util.List;

/**
 * Class that represents a particular entity type
 * of the game. It contains machine generate code.
 * Go to gatech.mmpm.Entity for more information.
 */
public class TBase extends gatech.mmpm.PhysicalEntity {

	public TBase(String entityID, String owner) {
	
		super(entityID, owner);
		_width = 16;
		_length = 16;
		
	} // Constructor

	//---------------------------------------------------------------

	public TBase(TBase rhs) {

		super(rhs);
		_hitpoints = rhs._hitpoints;
		_nextunit = rhs._nextunit;

	} // Copy constructor 
	
	//---------------------------------------------------------------

	public Object clone() {

		TBase e = new TBase(this);
		return e;

	} // clone

	//---------------------------------------------------------------

	public char instanceShortName() {
	
		return '\0';
	
	} // instanceShortName

	//---------------------------------------------------------------
	
	public List<String> listOfFeatures() {
	
		// Overwritten in each entity class to return the
		// class static attribute. 
		return _listOfFeatures;

	} // listOfFeatures
	
	//---------------------------------------------------------------
	
	public List<gatech.mmpm.Action> listOfActions() {
	
		// Overwritten in each entity class to return the
		// class static attribute. 
		return _listOfActions;

	} // listOfActions

	//---------------------------------------------------------------

	public boolean isActive() {

		return false;

	} // isActive

	//---------------------------------------------------------------
	//                       Getter & setter
	//---------------------------------------------------------------


	//- - - - - - - - - - - - - - - - - - - - - - - -
	//        hitpoints feature 
	//- - - - - - - - - - - - - - - - - - - - - - - - 

	public int getHitpoints() {

		return _hitpoints;

	} // getHitpoints

	public void setHitpoints(int rhs) {

		_hitpoints = rhs;

	} // setHitpoints

	public void setHitpoints(String rhs) {

		_hitpoints = Integer.parseInt(rhs);

	} // setHitpoints(String)

	//- - - - - - - - - - - - - - - - - - - - - - - -
	//        nextunit feature 
	//- - - - - - - - - - - - - - - - - - - - - - - - 

	public int getNextunit() {

		return _nextunit;

	} // getNextunit

	public void setNextunit(int rhs) {

		_nextunit = rhs;

	} // setNextunit

	public void setNextunit(String rhs) {

		_nextunit = Integer.parseInt(rhs);

	} // setNextunit(String)


	//---------------------------------------------------------------
	//                 Generic getter and setter
	//---------------------------------------------------------------

	public Object featureValue(String feature) {

		if (feature.compareTo("hitpoints") == 0)
			return getHitpoints();
		else 
		if (feature.compareTo("nextunit") == 0)
			return getNextunit();
		else 
			return super.featureValue(feature);
	
	} // featureValue

	//---------------------------------------------------------------

	public void setFeatureValue(String feature, String value) {

		if (feature.compareTo("hitpoints") == 0)
			setHitpoints(value);
		else 
		if (feature.compareTo("nextunit") == 0)
			setNextunit(value);
		else 
			super.setFeatureValue(feature, value);
	
	} // setFeatureValue

	//---------------------------------------------------------------
	//                       Static methods
	//---------------------------------------------------------------
	
	public static char shortName() {

		return '\0';

	} // shortName

	//---------------------------------------------------------------

	public static List<String> staticListOfFeatures() {

		return _listOfFeatures;

	}

	//---------------------------------------------------------------

	public static List<gatech.mmpm.Action> staticListOfActions() {

		return _listOfActions;

	}

	//---------------------------------------------------------------
	//                       Protected fields
	//---------------------------------------------------------------
	
	protected int _hitpoints;

	protected int _nextunit;



	static java.util.List<String> _listOfFeatures;

	static java.util.List<Action> _listOfActions;

	//---------------------------------------------------------------
	//                       Static initializers
	//---------------------------------------------------------------

	static {

		// Add features to _listOfFeatures.
		_listOfFeatures = new java.util.LinkedList<String>(gatech.mmpm.PhysicalEntity.staticListOfFeatures());
		_listOfFeatures.add("hitpoints");
		_listOfFeatures.add("nextunit");

		// Add valid actions to _listOfActions.
		_listOfActions = new java.util.LinkedList<Action>(gatech.mmpm.PhysicalEntity.staticListOfActions());

	} // static initializer

} // class TBase

/*******************************************************************
 *                       MACHINE GENERATED CODE                    *
 *                            DO NOT EDIT                          *
 *        FIX THE SOURCE XML INSTEAD, AND REGENERATE IT AGAIN      *
 *                                                                 *
 *                                                                 *
 * Tool: gatech.mmpm.tools.DomainGenerator                         *
 *                                                                 *
 * Organization: Georgia Institute of Technology                   *
 *               Cognitive Computing Lab (CCL)                     *
 * Authors:      Pedro Pablo Gomez Martin                          *
 *               Marco Antonio Gomez Martin                        *
 * Based on previous work of:                                      *
 *               Jai Rad                                           *
 *               Prafulla Mahindrakar                              *
 *               Santi Ontañón                                     *
 *******************************************************************/

