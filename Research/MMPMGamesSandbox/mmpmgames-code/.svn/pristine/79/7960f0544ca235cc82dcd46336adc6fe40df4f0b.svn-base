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
public class TUnit extends gatech.mmpm.PhysicalEntity {

	public TUnit(String entityID, String owner) {
	
		super(entityID, owner);
		_width = 16;
		_length = 16;
		
	} // Constructor

	//---------------------------------------------------------------

	public TUnit(TUnit rhs) {

		super(rhs);
		_hitpoints = rhs._hitpoints;
		_target = rhs._target;

	} // Copy constructor 
	
	//---------------------------------------------------------------

	public Object clone() {

		TUnit e = new TUnit(this);
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
	//        target feature 
	//- - - - - - - - - - - - - - - - - - - - - - - - 

	public String getTarget() {

		return _target;

	} // getTarget

	public void setTarget(String rhs) {

		_target = rhs;

	} // setTarget


	//---------------------------------------------------------------
	//                 Generic getter and setter
	//---------------------------------------------------------------

	public Object featureValue(String feature) {

		if (feature.compareTo("hitpoints") == 0)
			return getHitpoints();
		else 
		if (feature.compareTo("target") == 0)
			return getTarget();
		else 
			return super.featureValue(feature);
	
	} // featureValue

	//---------------------------------------------------------------

	public void setFeatureValue(String feature, String value) {

		if (feature.compareTo("hitpoints") == 0)
			setHitpoints(value);
		else 
		if (feature.compareTo("target") == 0)
			setTarget(value);
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

	protected String _target;



	static java.util.List<String> _listOfFeatures;

	static java.util.List<Action> _listOfActions;

	//---------------------------------------------------------------
	//                       Static initializers
	//---------------------------------------------------------------

	static {

		// Add features to _listOfFeatures.
		_listOfFeatures = new java.util.LinkedList<String>(gatech.mmpm.PhysicalEntity.staticListOfFeatures());
		_listOfFeatures.add("hitpoints");
		_listOfFeatures.add("target");

		// Add valid actions to _listOfActions.
		_listOfActions = new java.util.LinkedList<Action>(gatech.mmpm.PhysicalEntity.staticListOfActions());

	} // static initializer

} // class TUnit

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

