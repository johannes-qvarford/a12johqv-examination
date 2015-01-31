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


package towers.mmpm.actions;

import gatech.mmpm.Context;
import gatech.mmpm.ActionParameter;
import gatech.mmpm.ActionParameterType;
import gatech.mmpm.sensor.Sensor;
import towers.mmpm.sensors.BuildableCoordinates;
import gatech.mmpm.sensor.composite.RelationalCondition;
import towers.mmpm.sensors.NonBlockingPath;
import towers.mmpm.sensors.BuildingCost;
import gatech.mmpm.sensor.composite.ArithmeticSensor;
import gatech.mmpm.sensor.composite.AndCondition;
import gatech.mmpm.sensor.composite.Invocation;
import gatech.mmpm.util.Pair;
import gatech.mmpm.sensor.composite.GetContextValue;
import gatech.mmpm.sensor.builtin.EntityTypeAt;
import towers.mmpm.sensors.Gold;
import gatech.mmpm.sensor.constant.ConstantInteger;
import gatech.mmpm.sensor.builtin.Timer;


import java.util.List;

/**
 * Class that represents a particular action type
 * of the game. It contains machine generate code.
 * Go to gatech.mmpm.Action for more information.
 */
public class Build extends gatech.mmpm.Action {

	/**
	 * Constructor
	 * 
	 * @param entityID Entity identifier which receives
	 * the action.
	 * @param playerID Player identifier that makes
	 * the action.
	 */
	public Build(String entityID, String playerID) {
	
		super(entityID, playerID);

	} // Constructor

	//---------------------------------------------------------------

	public Build(Build rhs) {

		super(rhs);
		_type = rhs._type;
		_coor = rhs._coor;
		_lifeTime = rhs._lifeTime;

	} // Copy constructor 
	
	//---------------------------------------------------------------

	public Object clone() {

		Build e = new Build(this);
		return e;

	} // clone

	//---------------------------------------------------------------
	
	/**
	 * Returns a list with the names and types of the
	 * parameters that this action type has.
	 * 
	 * @return List of the action parameters.
	 * 
	 * @note This method must be overwritten in each
	 * subclass.
	 */
	public List<ActionParameter> listOfParameters() {
	
		return _listOfParameters;

	} // listOfParameters

	//---------------------------------------------------------------
	//                       Getter & setter
	//---------------------------------------------------------------


	//- - - - - - - - - - - - - - - - - - - - - - - -
	//        type parameter
	//- - - - - - - - - - - - - - - - - - - - - - - - 

	public Class<? extends gatech.mmpm.Entity> getType() {

		return _type;

	} // getType

	public void setType(Class<? extends gatech.mmpm.Entity> rhs) {

		_type = rhs;

	} // setType

	/**
	 * Returns the value of the type
	 * parameter as a String.
	 *
	 * @return type as String
	 */
	public String getStringType() {

		if(_type == null)
			return null;
		return ActionParameterType.ENTITY_TYPE.toString(_type);

	} // getType

	public void setType(String rhs) {

		_type = (Class<? extends gatech.mmpm.Entity>) 
		          ActionParameterType.ENTITY_TYPE.fromString(rhs);

	} // setType(String)

	//- - - - - - - - - - - - - - - - - - - - - - - -
	//        coor parameter
	//- - - - - - - - - - - - - - - - - - - - - - - - 

	public float[] getCoor() {

		return _coor;

	} // getCoor

	public void setCoor(float[] rhs) {

		_coor = rhs;

	} // setCoor

	/**
	 * Returns the value of the coor
	 * parameter as a String.
	 *
	 * @return coor as String
	 */
	public String getStringCoor() {

		if(_coor == null)
			return null;
		return ActionParameterType.COORDINATE.toString(_coor);

	} // getCoor

	public void setCoor(String rhs) {

		_coor = (float[]) 
		          ActionParameterType.COORDINATE.fromString(rhs);

	} // setCoor(String)

	//- - - - - - - - - - - - - - - - - - - - - - - -
	//        lifeTime parameter
	//- - - - - - - - - - - - - - - - - - - - - - - - 

	public Integer getLifeTime() {

		return _lifeTime;

	} // getLifeTime

	public void setLifeTime(Integer rhs) {

		_lifeTime = rhs;

	} // setLifeTime

	/**
	 * Returns the value of the lifeTime
	 * parameter as a String.
	 *
	 * @return lifeTime as String
	 */
	public String getStringLifeTime() {

		if(_lifeTime == null)
			return null;
		return ActionParameterType.INTEGER.toString(_lifeTime);

	} // getLifeTime

	public void setLifeTime(String rhs) {

		_lifeTime = (Integer) 
		          ActionParameterType.INTEGER.fromString(rhs);

	} // setLifeTime(String)


	//---------------------------------------------------------------
	//                 Generic getter and setter
	//---------------------------------------------------------------

	/**
	 * Returns a parameter value by its name.
	 * 
	 * @param parameter Parameter name which value want to be recovered.
	 * 
	 * @return Parameter value, or null if it do not exist.
	 * 
	 * @note This method MUST BE overwritten in subclasses if more
	 * parameters are added.
	 */
	public Object parameterValue(String parameter) {

		if (parameter.compareTo("type") == 0)
			return getType();
		else 
		if (parameter.compareTo("coor") == 0)
			return getCoor();
		else 
		if (parameter.compareTo("lifeTime") == 0)
			return getLifeTime();
		else 
			return super.parameterValue(parameter);
	
	} // parameterValue


	//---------------------------------------------------------------

	/**
	 * Returns a String with the value of a parameter by its name.
	 * 
	 * @param parameter Parameter name which value want to be recovered.
	 * 
	 * @return Parameter value (as string), or null if it do not exist.
	 * 
	 * @note This method MUST BE overwritten in subclasses if more
	 * parameters are added.
	 */
	public String parameterStringValue(String parameter) {

		if (parameter.compareTo("type") == 0)
			return getStringType();
		else 
		if (parameter.compareTo("coor") == 0)
			return getStringCoor();
		else 
		if (parameter.compareTo("lifeTime") == 0)
			return getStringLifeTime();
		else 
			return super.parameterStringValue(parameter);
	
	} // parameterStringValue

	//---------------------------------------------------------------

	/**
	 * Set a parameter value by its name.
	 * 
	 * @param parameter Parameter name which value want to be set.
	 * 
	 * @param value New value. It will be converted to the real
	 * data type.
	 * 
	 * @note This method MUST BE overwritten in subclasses if more
	 * parameters are added.
	 * 
	 * @note Although <tt>actionID</tt> is shown as an
	 * action parameter, it cannot be changed with this
	 * method (is a read-only parameter automatically
	 * established in the constructor). 
	 */
	public void setParameterValue(String parameter, String value) {

		if (parameter.compareTo("type") == 0)
			setType(value);
		else 
		if (parameter.compareTo("coor") == 0)
			setCoor(value);
		else 
		if (parameter.compareTo("lifeTime") == 0)
			setLifeTime(value);
		else 
			super.setParameterValue(parameter, value);
	
	} // setParameterValue

	//---------------------------------------------------------------

	/**
	 * Returns the action context, in other words, a context
	 * with pairs with the action parameter names and their
	 * values.
	 * 
	 * @return The action context.
	 */
	public Context getContext() {

		Context result;
		result = new Context(super.getContext());
		result.put("type", _type);
		result.put("coor", _coor);
		result.put("lifeTime", _lifeTime);

		return result;

	} // getContext

	//---------------------------------------------------------------
	//                    getXXXCondition()
	//---------------------------------------------------------------

	/**
	 * Returns the static precondition of the Action.
	 * Note that every instance of this class will return
	 * the same precondition. 
	 * Subclasses must "overwrite" this method.
	 * 
	 * @return The static precondition.
	 */
	public Sensor getPreCondition() 
	{
		return _preCondition;
	}

	//---------------------------------------------------------------

	/**
	 * Returns the static success condition of the Action.
	 * Note that every instance of this class will return
	 * the same success condition. 
	 * Subclasses must "overwrite" this method.
	 * 
	 * @return The static success condition.
	 */
	public Sensor getSuccessCondition() 
	{
		return _successCondition;
	}

	//---------------------------------------------------------------

	/**
	 * Returns the static failure condition of the Action.
	 * Note that every instance of this class will return
	 * the same failure condition. 
	 * Subclasses must "overwrite" this method.
	 * 
	 * @return The static failure condition.
	 */
	public Sensor getFailureCondition() 
	{
		return _failureCondition;
	}

	//---------------------------------------------------------------

	/**
	 * Returns the static valid condition of the Action.
	 * Note that every instance of this class will return
	 * the same valid condition. 
	 * Subclasses must "overwrite" this method.
	 * 
	 * @return The static valid condition.
	 */
	public Sensor getValidCondition() 
	{
		return _validCondition;
	}

	//---------------------------------------------------------------

	/**
	 * Returns the static postcondition of the Action.
	 * Note that every instance of this class will return
	 * the same postcondition. 
	 * Subclasses must "overwrite" this method.
	 * 
	 * @return The static postcondition.
	 */
	public Sensor getPostCondition() 
	{
		return _postCondition;
	}

	//---------------------------------------------------------------

	/**
	 * Returns the static pre-failure condition of the Action.
	 * Note that every instance of this class will return
	 * the same pre-failure condition. 
	 * Subclasses must "overwrite" this method.
	 * 
	 * @return The static pre-failure condition.
	 */
	public Sensor getPreFailureCondition() 
	{
		return _preFailureCondition;
	}

	//---------------------------------------------------------------
	//                       Static methods
	//---------------------------------------------------------------

	public static List<ActionParameter> staticListOfParameters() {

		return _listOfParameters;

	}

	//---------------------------------------------------------------
	//                       Protected fields
	//---------------------------------------------------------------

	protected Class<? extends gatech.mmpm.Entity> _type;

	protected float[] _coor;

	protected Integer _lifeTime;


	/**
	 * List of action parameter. All subclasses have their own
	 * _listOfParameter static field, that is initialized in a
	 * static initializer using the parent list and the new ones
	 * for that action.
	 */
	static java.util.List<ActionParameter> _listOfParameters;

	/**
	 * Action precondition.
	 * 
	 * The attribute is <em>static</em>, so in order to
	 * be evaluated, a <em>context</em> (provided by the
	 * specific <tt>Action</tt> objects) is needed, in 
	 * a similar way to that seen in the flyweight
	 * pattern.
	 */
	static protected Sensor _preCondition = new AndCondition(new AndCondition(new Invocation(new BuildableCoordinates(), new Pair<String, Sensor>("coor", new GetContextValue("coor", ActionParameterType.COORDINATE))), new Invocation(new NonBlockingPath(), new Pair<String, Sensor>("coor", new GetContextValue("coor", ActionParameterType.COORDINATE)))), new RelationalCondition(new Invocation(new Gold()), RelationalCondition.Operator.GREATER_EQUAL_THAN, new Invocation(new BuildingCost(), new Pair<String, Sensor>("type", new GetContextValue("type", ActionParameterType.ENTITY_TYPE)))));

	/**
	 * Action success condition.
	 * 
	 * The attribute is <em>static</em>, so in order to
	 * be evaluated, a <em>context</em> (provided by the
	 * specific <tt>Action</tt> objects) is needed, in 
	 * a similar way to that seen in the flyweight
	 * pattern.
	 */
	static protected Sensor _successCondition = new Invocation(new EntityTypeAt(), new Pair<String, Sensor>("type", new GetContextValue("type", ActionParameterType.ENTITY_TYPE)), new Pair<String, Sensor>("coor", new GetContextValue("coor", ActionParameterType.COORDINATE)), new Pair<String, Sensor>("owner", null));

	/**
	 * Action failure condition.
	 * 
	 * The attribute is <em>static</em>, so in order to
	 * be evaluated, a <em>context</em> (provided by the
	 * specific <tt>Action</tt> objects) is needed, in 
	 * a similar way to that seen in the flyweight
	 * pattern.
	 */
	static protected Sensor _failureCondition = new Invocation(new Timer(), new Pair<String, Sensor>("waitTime", new GetContextValue("lifeTime", ActionParameterType.INTEGER)));

	/**
	 * Action pre-failure condition.
	 * 
	 * The attribute is <em>static</em>, so in order to
	 * be evaluated, a <em>context</em> (provided by the
	 * specific <tt>Action</tt> objects) is needed, in 
	 * a similar way to that seen in the flyweight
	 * pattern.
	 */
	static protected Sensor _preFailureCondition = gatech.mmpm.Action._preFailureCondition;
	
	/**
	 * Action valid condition.
	 * It specifies whether a particular
	 * combination of parameters is valid or not.
	 * This can be used by the learning engine adaptation
	 * components to ensure that the actions being
	 * issued are valid.
	 * 
	 * The attribute is <em>static</em>, so in order to
	 * be evaluated, a <em>context</em> (provided by the
	 * specific <tt>Action</tt> objects) is needed, in 
	 * a similar way to that seen in the flyweight
	 * pattern.
	 */
	static protected Sensor _validCondition = new AndCondition(new Invocation(new BuildableCoordinates(), new Pair<String, Sensor>("coor", new GetContextValue("coor", ActionParameterType.COORDINATE))), new Invocation(new NonBlockingPath(), new Pair<String, Sensor>("coor", new GetContextValue("coor", ActionParameterType.COORDINATE))));

	/**
	 * Action postcondition.
	 * It will hold as a side effect of the action success,
	 * thus, it is a super-set of the successCondition
	 * 
	 * The attribute is <em>static</em>, so in order to
	 * be evaluated, a <em>context</em> (provided by the
	 * specific <tt>Action</tt> objects) is needed, in 
	 * a similar way to that seen in the flyweight
	 * pattern.
	 */
	static protected Sensor _postCondition = gatech.mmpm.Action._postCondition;

	//---------------------------------------------------------------
	//                     onXXXCondition (if any)
	//---------------------------------------------------------------

	/**
	 * Does some assignments needed before calling the first time to
	 * checkFailureCondition() method. This method should be overwritten in
	 * some child action classes.
	 * 
	 * @param cycle The cycle.
	 * @param gameState The game state.
	 * @param player Player who checks the condition. 
	 * @param parameters The context to evaluate the condition. 
	 */
	protected void onFailureCondition(int cycle, gatech.mmpm.GameState gameState, String player, Context parameters) {

		_lifeTime = (Integer) _onFailureCondition.evaluate(cycle, gameState, player, parameters);

	} // onFailureCondition

	/**
	 * Sensor to be evaluated in the onFailureCondition() method.
	 */
	static protected Sensor _onFailureCondition = new ArithmeticSensor(new ConstantInteger(32), ArithmeticSensor.Operator.ADD, new GetContextValue("cycle", ActionParameterType.INTEGER));

	
	//---------------------------------------------------------------
	//                       Static initializers
	//---------------------------------------------------------------

	static {

		// Add parameters to _listOfParameters.
		_listOfParameters = new java.util.LinkedList<ActionParameter>(gatech.mmpm.Action.staticListOfParameters());
		_listOfParameters.add(new ActionParameter("type", ActionParameterType.ENTITY_TYPE));
		_listOfParameters.add(new ActionParameter("coor", ActionParameterType.COORDINATE));
		_listOfParameters.add(new ActionParameter("lifeTime", ActionParameterType.INTEGER));

	} // static initializer

} // class Build

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

