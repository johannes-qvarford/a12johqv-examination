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
import towers.mmpm.actions.Build;


import java.util.List;

/**
 * Class that represents a particular entity type
 * of the game. It contains machine generate code.
 * Go to gatech.mmpm.Entity for more information.
 */
public class TPlayer extends gatech.mmpm.Entity {

	public TPlayer(String entityID, String owner) {
	
		super(entityID, owner);
		
	} // Constructor

	//---------------------------------------------------------------

	public TPlayer(TPlayer rhs) {

		super(rhs);
		_color = rhs._color;
		_gold = rhs._gold;

	} // Copy constructor 
	
	//---------------------------------------------------------------

	public Object clone() {

		TPlayer e = new TPlayer(this);
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

		return true;

	} // isActive

	//---------------------------------------------------------------
	//                       Getter & setter
	//---------------------------------------------------------------


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
	//        gold feature 
	//- - - - - - - - - - - - - - - - - - - - - - - - 

	public int getGold() {

		return _gold;

	} // getGold

	public void setGold(int rhs) {

		_gold = rhs;

	} // setGold

	public void setGold(String rhs) {

		_gold = Integer.parseInt(rhs);

	} // setGold(String)


	//---------------------------------------------------------------
	//                 Generic getter and setter
	//---------------------------------------------------------------

	public Object featureValue(String feature) {

		if (feature.compareTo("color") == 0)
			return getColor();
		else 
		if (feature.compareTo("gold") == 0)
			return getGold();
		else 
			return super.featureValue(feature);
	
	} // featureValue

	//---------------------------------------------------------------

	public void setFeatureValue(String feature, String value) {

		if (feature.compareTo("color") == 0)
			setColor(value);
		else 
		if (feature.compareTo("gold") == 0)
			setGold(value);
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
	
	protected String _color;

	protected int _gold;



	static java.util.List<String> _listOfFeatures;

	static java.util.List<Action> _listOfActions;

	//---------------------------------------------------------------
	//                       Static initializers
	//---------------------------------------------------------------

	static {

		// Add features to _listOfFeatures.
		_listOfFeatures = new java.util.LinkedList<String>(gatech.mmpm.Entity.staticListOfFeatures());
		_listOfFeatures.add("color");
		_listOfFeatures.add("gold");

		// Add valid actions to _listOfActions.
		_listOfActions = new java.util.LinkedList<Action>(gatech.mmpm.Entity.staticListOfActions());
		Action a;
		a = new Build(null, null);
		a.setParameterValue("type", "towers.mmpm.entities.TTower");
		_listOfActions.add(a);

		a = new Build(null, null);
		a.setParameterValue("type", "towers.mmpm.entities.TWall");
		_listOfActions.add(a);

		a = new Build(null, null);
		a.setParameterValue("type", "towers.mmpm.entities.TUpgradeGold");
		_listOfActions.add(a);

		a = new Build(null, null);
		a.setParameterValue("type", "towers.mmpm.entities.TUpgradeUnits");
		_listOfActions.add(a);


	} // static initializer

} // class TPlayer

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

