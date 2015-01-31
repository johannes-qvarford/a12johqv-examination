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


package bc.mmpm.sensors;

import gatech.mmpm.Context;
import gatech.mmpm.GameState;
import gatech.mmpm.sensor.Sensor;
import gatech.mmpm.ActionParameterType;
import gatech.mmpm.util.Pair;




/**
 * Class that represents a particular sensor type
 * of the game. It contains machine generate code.
 * Go to gatech.mmpm.Sensor for more information.
 */
public class ClosestEntityInLine extends Sensor {

	public ClosestEntityInLine() {
	} // Constructor

	//---------------------------------------------------------------

	public ClosestEntityInLine(ClosestEntityInLine rhs) {

		super(rhs);

	} // Copy constructor 
	
	//---------------------------------------------------------------

	public Object clone() {

		ClosestEntityInLine e = new ClosestEntityInLine(this);
		return e;

	} // clone

	//---------------------------------------------------------------

	/**
	 * Return the type of the sensor.
	 * 
	 * Keep in mind that this is <em>not</em> the real Java type,
	 * but the MMPM type. See the 
	 * gatech.mmpm.ActionParameterType.getJavaType() method
	 * for more information.
	 * 
	 * @return Type of the sensor. 
	 */
	public ActionParameterType getType() {
	
		return ActionParameterType.ENTITY_ID;

	} // getType

	//---------------------------------------------------------------
	
	public Object evaluate(int cycle, GameState gs, String player, Context parameters) {

		gatech.mmpm.TwoDMap grid = (gatech.mmpm.TwoDMap) gs.getMap();
        bc.mmpm.entities.BCOPlayerTank entityfromGameState = null;

        java.util.List<gatech.mmpm.Entity> entities = gs.getEntityByType(bc.mmpm.entities.BCOPlayerTank.class);

		  for (gatech.mmpm.Entity entity : entities)
			  if (entity.getowner()!=null && 
                  entity.getowner().equals(player))
				  entityfromGameState = (bc.mmpm.entities.BCOPlayerTank)entity;	

		  if(entityfromGameState!= null)
		  {
			  int cellCoor[] = grid.toCellCoords(entityfromGameState);
			  int x = cellCoor[0];
			  int y = cellCoor[1];
			  int direction = entityfromGameState.getDirection();
			  float coor[] = new float[3];
			  int incx = 0, incy = 0;
			  if (direction == 0) incy = -1;
			  if (direction == 1) incx = 1;
			  if (direction == 2) incy = 1;
			  if (direction == 3) incx = -1;

			  x += incx;
			  y += incy;
			  while(x>0 && x<grid.getSizeInDimension(0) &&
					  y>0 && y<grid.getSizeInDimension(1)) {

				  cellCoor[0] = x;
				  cellCoor[1] = y;
				  cellCoor[2] = 0;
				  gatech.mmpm.PhysicalEntity mapEntity = grid.getCellLocation(cellCoor);
				  if(mapEntity instanceof bc.mmpm.entities.BCOWall) 
					  return mapEntity;

				  coor = grid.toCoords(cellCoor);
				  for(gatech.mmpm.Entity e:entities) {					
					  if (e!=entityfromGameState && ((gatech.mmpm.PhysicalEntity)e).collision(entityfromGameState,coor)) {
						  return entityfromGameState;
					  }
				  }

				  x+=incx;
				  y+=incy;
			  } // while

				  return null;
		  }
		  return null;
	
	} // evaluate
	
	//---------------------------------------------------------------
	
	/**
	 * Protected method called from equivalents to compare
	 * two sensors. Subclasses must override this method to
	 * decide if a sensor of the current class is equivalent
	 * to the current sensor.
	 * 
	 * @param s Sensor to compare the current one with.
	 * It will always be an instance of the current class.
	 * @return True if both sensors are equivalents.
	 * 
	 * @note This method should never be externally called.
	 * Use equivalents() instead.
	 */
	protected boolean internalEquivalents(Sensor s) {

		// Auto-generated sensors of the same class are
		// always equivalent in between.
		return true;

	} // internalEquivalents

	//---------------------------------------------------------------
	
	/**
	 * Public method that provides the parameters that 
	 * this sensor uses to be evaluated. This method provides
	 * all the parameters that can be used in the evaluation, 
	 * nevertheless some sensor can be evaluated with only 
	 * some of them.
	 * 
	 * @return The list of needed parameters this sensor needs
	 * to be evaluated.
	 */
	public java.util.List<Pair<String,ActionParameterType>> getNeededParameters() {

		return _listOfNeededParameters;
	
	} // getNeededParameters

	//---------------------------------------------------------------
	
	/**
	 * Public static method that provides the parameters that 
	 * this sensor uses to be evaluated. This method provides
	 * all the parameters that can be used in the evaluation, 
	 * nevertheless some sensor can be evaluated with only 
	 * some of them.
	 * 
	 * @return The list of needed parameters this sensor needs
	 * to be evaluated.
	 */
	public static java.util.List<Pair<String,ActionParameterType>> getStaticNeededParameters() {

		return _listOfNeededParameters;
	
	} // getStaticNeededParameters
	
	//---------------------------------------------------------------
	//                       Static fields
	//---------------------------------------------------------------
	
	static java.util.List<Pair<String,ActionParameterType>> _listOfNeededParameters;
	
	
	
	//---------------------------------------------------------------
	//                       Static initializers
	//---------------------------------------------------------------

	static {

		// Add parameters to _listOfNeededParameters.
		_listOfNeededParameters = new java.util.LinkedList<Pair<String,ActionParameterType>>(gatech.mmpm.sensor.Sensor.getStaticNeededParameters());
		

	} // static initializer

} // class ClosestEntityInLine

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

