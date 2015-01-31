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
public class BCOTank extends gatech.mmpm.PhysicalEntity {

	public BCOTank(String entityID, String owner) {
	
		super(entityID, owner);
		_width = 32;
		_length = 32;
		
	} // Constructor

	//---------------------------------------------------------------

	public BCOTank(BCOTank rhs) {

		super(rhs);
		_direction = rhs._direction;
		_color = rhs._color;
		_next_shot_delay = rhs._next_shot_delay;
		_next_move_delay = rhs._next_move_delay;
		_ai_type = rhs._ai_type;

	} // Copy constructor 
	
	//---------------------------------------------------------------

	public Object clone() {

		BCOTank e = new BCOTank(this);
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
	//        color feature 
	//- - - - - - - - - - - - - - - - - - - - - - - - 

	public String getColor() {

		return _color;

	} // getColor

	public void setColor(String rhs) {

		_color = rhs;

	} // setColor

	//- - - - - - - - - - - - - - - - - - - - - - - -
	//        next_shot_delay feature 
	//- - - - - - - - - - - - - - - - - - - - - - - - 

	public int getNext_shot_delay() {

		return _next_shot_delay;

	} // getNext_shot_delay

	public void setNext_shot_delay(int rhs) {

		_next_shot_delay = rhs;

	} // setNext_shot_delay

	public void setNext_shot_delay(String rhs) {

		_next_shot_delay = Integer.parseInt(rhs);

	} // setNext_shot_delay(String)

	//- - - - - - - - - - - - - - - - - - - - - - - -
	//        next_move_delay feature 
	//- - - - - - - - - - - - - - - - - - - - - - - - 

	public int getNext_move_delay() {

		return _next_move_delay;

	} // getNext_move_delay

	public void setNext_move_delay(int rhs) {

		_next_move_delay = rhs;

	} // setNext_move_delay

	public void setNext_move_delay(String rhs) {

		_next_move_delay = Integer.parseInt(rhs);

	} // setNext_move_delay(String)

	//- - - - - - - - - - - - - - - - - - - - - - - -
	//        ai_type feature 
	//- - - - - - - - - - - - - - - - - - - - - - - - 

	public String getAi_type() {

		return _ai_type;

	} // getAi_type

	public void setAi_type(String rhs) {

		_ai_type = rhs;

	} // setAi_type


	//---------------------------------------------------------------
	//                 Generic getter and setter
	//---------------------------------------------------------------

	public Object featureValue(String feature) {

		if (feature.compareTo("direction") == 0)
			return getDirection();
		else 
		if (feature.compareTo("color") == 0)
			return getColor();
		else 
		if (feature.compareTo("next_shot_delay") == 0)
			return getNext_shot_delay();
		else 
		if (feature.compareTo("next_move_delay") == 0)
			return getNext_move_delay();
		else 
		if (feature.compareTo("ai_type") == 0)
			return getAi_type();
		else 
			return super.featureValue(feature);
	
	} // featureValue

	//---------------------------------------------------------------

	public void setFeatureValue(String feature, String value) {

		if (feature.compareTo("direction") == 0)
			setDirection(value);
		else 
		if (feature.compareTo("color") == 0)
			setColor(value);
		else 
		if (feature.compareTo("next_shot_delay") == 0)
			setNext_shot_delay(value);
		else 
		if (feature.compareTo("next_move_delay") == 0)
			setNext_move_delay(value);
		else 
		if (feature.compareTo("ai_type") == 0)
			setAi_type(value);
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

	protected String _color;

	protected int _next_shot_delay;

	protected int _next_move_delay;

	protected String _ai_type;



	static java.util.List<String> _listOfFeatures;

	static java.util.List<Action> _listOfActions;

	//---------------------------------------------------------------
	//                       Static initializers
	//---------------------------------------------------------------

	static {

		// Add features to _listOfFeatures.
		_listOfFeatures = new java.util.LinkedList<String>(gatech.mmpm.PhysicalEntity.staticListOfFeatures());
		_listOfFeatures.add("direction");
		_listOfFeatures.add("color");
		_listOfFeatures.add("next_shot_delay");
		_listOfFeatures.add("next_move_delay");
		_listOfFeatures.add("ai_type");

		// Add valid actions to _listOfActions.
		_listOfActions = new java.util.LinkedList<Action>(gatech.mmpm.PhysicalEntity.staticListOfActions());

	} // static initializer

} // class BCOTank

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

