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


package bc.mmpm.actions;

import gatech.mmpm.Context;
import gatech.mmpm.ActionParameter;
import gatech.mmpm.ActionParameterType;
import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.sensor.composite.Invocation;
import gatech.mmpm.util.Pair;
import gatech.mmpm.sensor.composite.GetContextValue;
import bc.mmpm.sensors.NextMoveDelay;
import gatech.mmpm.sensor.composite.ArithmeticSensor;
import gatech.mmpm.sensor.constant.ConstantInteger;
import gatech.mmpm.sensor.composite.EqualitySensor;
import gatech.mmpm.sensor.builtin.Timer;


import java.util.List;

/**
 * Class that represents a particular action type
 * of the game. It contains machine generate code.
 * Go to gatech.mmpm.Action for more information.
 */
public class Move extends gatech.mmpm.Action {

	/**
	 * Constructor
	 * 
	 * @param entityID Entity identifier which receives
	 * the action.
	 * @param playerID Player identifier that makes
	 * the action.
	 */
	public Move(String entityID, String playerID) {
	
		super(entityID, playerID);

	} // Constructor

	//---------------------------------------------------------------

	public Move(Move rhs) {

		super(rhs);
		_failureTime = rhs._failureTime;
		_successTime = rhs._successTime;
		_direction = rhs._direction;

	} // Copy constructor 
	
	//---------------------------------------------------------------

	public Object clone() {

		Move e = new Move(this);
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
	//        failureTime parameter
	//- - - - - - - - - - - - - - - - - - - - - - - - 

	public Integer getFailureTime() {

		return _failureTime;

	} // getFailureTime

	public void setFailureTime(Integer rhs) {

		_failureTime = rhs;

	} // setFailureTime

	/**
	 * Returns the value of the failureTime
	 * parameter as a String.
	 *
	 * @return failureTime as String
	 */
	public String getStringFailureTime() {

		if(_failureTime == null)
			return null;
		return ActionParameterType.INTEGER.toString(_failureTime);

	} // getFailureTime

	public void setFailureTime(String rhs) {

		_failureTime = (Integer) 
		          ActionParameterType.INTEGER.fromString(rhs);

	} // setFailureTime(String)

	//- - - - - - - - - - - - - - - - - - - - - - - -
	//        successTime parameter
	//- - - - - - - - - - - - - - - - - - - - - - - - 

	public Integer getSuccessTime() {

		return _successTime;

	} // getSuccessTime

	public void setSuccessTime(Integer rhs) {

		_successTime = rhs;

	} // setSuccessTime

	/**
	 * Returns the value of the successTime
	 * parameter as a String.
	 *
	 * @return successTime as String
	 */
	public String getStringSuccessTime() {

		if(_successTime == null)
			return null;
		return ActionParameterType.INTEGER.toString(_successTime);

	} // getSuccessTime

	public void setSuccessTime(String rhs) {

		_successTime = (Integer) 
		          ActionParameterType.INTEGER.fromString(rhs);

	} // setSuccessTime(String)

	//- - - - - - - - - - - - - - - - - - - - - - - -
	//        direction parameter
	//- - - - - - - - - - - - - - - - - - - - - - - - 

	public Integer getDirection() {

		return _direction;

	} // getDirection

	public void setDirection(Integer rhs) {

		_direction = rhs;

	} // setDirection

	/**
	 * Returns the value of the direction
	 * parameter as a String.
	 *
	 * @return direction as String
	 */
	public String getStringDirection() {

		if(_direction == null)
			return null;
		return ActionParameterType.DIRECTION.toString(_direction);

	} // getDirection

	public void setDirection(String rhs) {

		_direction = (Integer) 
		          ActionParameterType.DIRECTION.fromString(rhs);

	} // setDirection(String)


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

		if (parameter.compareTo("failureTime") == 0)
			return getFailureTime();
		else 
		if (parameter.compareTo("successTime") == 0)
			return getSuccessTime();
		else 
		if (parameter.compareTo("direction") == 0)
			return getDirection();
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

		if (parameter.compareTo("failureTime") == 0)
			return getStringFailureTime();
		else 
		if (parameter.compareTo("successTime") == 0)
			return getStringSuccessTime();
		else 
		if (parameter.compareTo("direction") == 0)
			return getStringDirection();
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

		if (parameter.compareTo("failureTime") == 0)
			setFailureTime(value);
		else 
		if (parameter.compareTo("successTime") == 0)
			setSuccessTime(value);
		else 
		if (parameter.compareTo("direction") == 0)
			setDirection(value);
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
		result.put("failureTime", _failureTime);
		result.put("successTime", _successTime);
		result.put("direction", _direction);

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

	protected Integer _failureTime;

	protected Integer _successTime;

	protected Integer _direction;


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
	static protected Sensor _preCondition = new EqualitySensor(new Invocation(new NextMoveDelay()), EqualitySensor.Operator.EQUAL_THAN, new ConstantInteger(0));

	/**
	 * Action success condition.
	 * 
	 * The attribute is <em>static</em>, so in order to
	 * be evaluated, a <em>context</em> (provided by the
	 * specific <tt>Action</tt> objects) is needed, in 
	 * a similar way to that seen in the flyweight
	 * pattern.
	 */
	static protected Sensor _successCondition = new Invocation(new Timer(), new Pair<String, Sensor>("waitTime", new GetContextValue("successTime", ActionParameterType.INTEGER)));

	/**
	 * Action failure condition.
	 * 
	 * The attribute is <em>static</em>, so in order to
	 * be evaluated, a <em>context</em> (provided by the
	 * specific <tt>Action</tt> objects) is needed, in 
	 * a similar way to that seen in the flyweight
	 * pattern.
	 */
	static protected Sensor _failureCondition = new Invocation(new Timer(), new Pair<String, Sensor>("waitTime", new GetContextValue("failureTime", ActionParameterType.INTEGER)));

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
	static protected Sensor _validCondition = gatech.mmpm.Action._validCondition;

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

		_failureTime = (Integer) _onFailureCondition.evaluate(cycle, gameState, player, parameters);

	} // onFailureCondition

	/**
	 * Sensor to be evaluated in the onFailureCondition() method.
	 */
	static protected Sensor _onFailureCondition = new ArithmeticSensor(new ConstantInteger(32), ArithmeticSensor.Operator.ADD, new GetContextValue("cycle", ActionParameterType.INTEGER));
	/**
	 * Does some assignments needed before calling the first time to
	 * checkSuccessCondition() method. This method should be overwritten in
	 * some child action classes.
	 * 
	 * @param cycle The cycle.
	 * @param gameState The game state.
	 * @param player Player who checks the condition. 
	 * @param parameters The context to evaluate the condition. 
	 */
	protected void onSuccessCondition(int cycle, gatech.mmpm.GameState gameState, String player, Context parameters) {

		_successTime = (Integer) _onSuccessCondition.evaluate(cycle, gameState, player, parameters);

	} // onSuccessCondition

	/**
	 * Sensor to be evaluated in the onSuccessCondition() method.
	 */
	static protected Sensor _onSuccessCondition = new ArithmeticSensor(new ConstantInteger(8), ArithmeticSensor.Operator.ADD, new GetContextValue("cycle", ActionParameterType.INTEGER));

	
	//---------------------------------------------------------------
	//                       Static initializers
	//---------------------------------------------------------------

	static {

		// Add parameters to _listOfParameters.
		_listOfParameters = new java.util.LinkedList<ActionParameter>(gatech.mmpm.Action.staticListOfParameters());
		_listOfParameters.add(new ActionParameter("failureTime", ActionParameterType.INTEGER));
		_listOfParameters.add(new ActionParameter("successTime", ActionParameterType.INTEGER));
		_listOfParameters.add(new ActionParameter("direction", ActionParameterType.DIRECTION));

	} // static initializer

} // class Move

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

