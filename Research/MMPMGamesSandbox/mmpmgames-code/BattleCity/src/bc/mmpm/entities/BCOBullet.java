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
 *               Santi Ontanon                                     *
 *******************************************************************/


package bc.mmpm.entities;

import gatech.mmpm.Action;


import java.util.List;

/**
 * Class that represents a particular entity type
 * of the game. It contains machine generate code.
 * Go to gatech.mmpm.Entity for more information.
 */
public class BCOBullet extends gatech.mmpm.PhysicalEntity {

	public BCOBullet(String entityID, String owner) {
	
		super(entityID, owner);
		_width = 16;
		_length = 16;
		
	} // Constructor

	//---------------------------------------------------------------

	public BCOBullet(BCOBullet rhs) {

		super(rhs);
		_direction = rhs._direction;
		_tank = rhs._tank;

	} // Copy constructor 
	
	//---------------------------------------------------------------

	public Object clone() {

		BCOBullet e = new BCOBullet(this);
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
	//        direction feature 
	//- - - - - - - - - - - - - - - - - - - - - - - - 

	public int getDirection() {

		return _direction;

	} // getDirection

	public void setDirection(int rhs) {

		_direction = rhs;

	} // setDirection

	public void setDirection(String rhs) {

		_direction = Integer.parseInt(rhs);

	} // setDirection(String)

	//- - - - - - - - - - - - - - - - - - - - - - - -
	//        tank feature 
	//- - - - - - - - - - - - - - - - - - - - - - - - 

	public String getTank() {

		return _tank;

	} // getTank

	public void setTank(String rhs) {

		_tank = rhs;

	} // setTank


	//---------------------------------------------------------------
	//                 Generic getter and setter
	//---------------------------------------------------------------

	public Object featureValue(String feature) {

		if (feature.compareTo("direction") == 0)
			return getDirection();
		else 
		if (feature.compareTo("tank") == 0)
			return getTank();
		else 
			return super.featureValue(feature);
	
	} // featureValue

	//---------------------------------------------------------------

	public void setFeatureValue(String feature, String value) {

		if (feature.compareTo("direction") == 0)
			setDirection(value);
		else 
		if (feature.compareTo("tank") == 0)
			setTank(value);
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
	
	protected int _direction;

	protected String _tank;



	static java.util.List<String> _listOfFeatures;

	static java.util.List<Action> _listOfActions;

	//---------------------------------------------------------------
	//                       Static initializers
	//---------------------------------------------------------------

	static {

		// Add features to _listOfFeatures.
		_listOfFeatures = new java.util.LinkedList<String>(gatech.mmpm.PhysicalEntity.staticListOfFeatures());
		_listOfFeatures.add("direction");
		_listOfFeatures.add("tank");

		// Add valid actions to _listOfActions.
		_listOfActions = new java.util.LinkedList<Action>(gatech.mmpm.PhysicalEntity.staticListOfActions());

	} // static initializer

} // class BCOBullet

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
 *               Santi Onta��n                                     *
 *******************************************************************/

