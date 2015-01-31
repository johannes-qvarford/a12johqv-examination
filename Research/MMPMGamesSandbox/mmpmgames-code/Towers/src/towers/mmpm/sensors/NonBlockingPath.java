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
package towers.mmpm.sensors;

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
public class NonBlockingPath extends Sensor {

    public NonBlockingPath() {
    } // Constructor

    //---------------------------------------------------------------
    public NonBlockingPath(NonBlockingPath rhs) {

        super(rhs);

    } // Copy constructor

    //---------------------------------------------------------------
    public Object clone() {

        NonBlockingPath e = new NonBlockingPath(this);
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

        return ActionParameterType.BOOLEAN;

    } // getType

    //---------------------------------------------------------------
    public Object evaluate(int cycle, GameState gs, String player, Context parameters) {

        if (parameters == null) {
            return 1.0f;
        }
        boolean blocked[];
        gatech.mmpm.TwoDMap map = (gatech.mmpm.TwoDMap) gs.getMap();
        int sx = map.getSizeInDimension(0);
        int sy = map.getSizeInDimension(1);
        int cellCoor[] = map.toCellCoords(getCoorParam(parameters, "coor"));
        int cx = cellCoor[0];
        int cy = cellCoor[1];
        towers.mmpm.entities.TBase own_base = null, opponent_base = null;

        blocked = new boolean[sx * sy];
        for (int i = 0; i < sx * sy; i++) {
            blocked[i] = false;
        }

        for (gatech.mmpm.Entity e : gs.getAllEntities()) {
            if (e instanceof gatech.mmpm.PhysicalEntity
                && !(e instanceof towers.mmpm.entities.TUnit)) {
                gatech.mmpm.PhysicalEntity pe = (gatech.mmpm.PhysicalEntity) e;

                if (e instanceof towers.mmpm.entities.TBase) {
                    // We only need one of the opponent bases, no matter which one:
                    if (e.getowner().equals(player)) {
                        own_base = (towers.mmpm.entities.TBase) e;
                    } else {
                        opponent_base = (towers.mmpm.entities.TBase) e;
                    }
                }

                cellCoor = map.toCellCoords(pe);
                blocked[cellCoor[0] + cellCoor[1] * sx] = true;
            }
        }

        //		System.out.println("Checking:" +  cx + "," + cy);

        // First test if it is a blocking candidate by checking the surroundings of the coordinate:
        // (this is just a filter, it's not necessary, but makes things faster)
        // This cryptic array contains simply a set of patterns.
        // If a coordinate does NOT satisfy any of the patterns, it is not necessary that we perform the full path finding
        // algorithm, since it is safe to assume that it won't block any path
        boolean patterns[][] = {
            {true, false, true,
                false, false, false,
                false, false, false},
            {false, false, true,
                false, false, false,
                false, false, true},
            {false, false, false,
                false, false, false,
                true, false, true},
            {true, false, false,
                false, false, false,
                true, false, false},
            {false, true, false,
                false, false, false,
                false, true, false},
            {false, false, false,
                true, false, true,
                false, false, false},
            {true, false, false,
                false, false, false,
                false, false, true},
            {false, false, true,
                false, false, false,
                true, false, false},
            {false, true, false,
                false, false, false,
                true, false, false},
            {false, true, false,
                false, false, false,
                false, false, true},
            {true, false, false,
                false, false, true,
                false, false, false},
            {false, false, false,
                false, false, true,
                true, false, false},
            {true, false, false,
                false, false, false,
                false, true, false},
            {false, false, true,
                false, false, false,
                false, true, false},
            {false, false, true,
                true, false, false,
                false, false, false},
            {false, false, false,
                true, false, false,
                false, false, true},};

        for (int i = 0; i < patterns.length; i++) {
            if (patterns[i][0] && !(cx - 1 < 0 || cy - 1 < 0 || cx - 1 >= sx || cy - 1 >= sy || blocked[cx - 1 + (cy - 1) * sx])) {
                continue;
            }
            if (patterns[i][1] && !(cx < 0 || cy - 1 < 0 || cx >= sx || cy - 1 >= sy || blocked[cx + (cy - 1) * sx])) {
                continue;
            }
            if (patterns[i][2] && !(cx + 1 < 0 || cy - 1 < 0 || cx + 1 >= sx || cy - 1 >= sy || blocked[cx + 1 + (cy - 1) * sx])) {
                continue;
            }
            if (patterns[i][3] && !(cx - 1 < 0 || cy < 0 || cx - 1 >= sx || cy >= sy || blocked[cx - 1 + cy * sx])) {
                continue;
            }
            if (patterns[i][4] && !(cx < 0 || cy < 0 || cx >= sx || cy >= sy || blocked[cx + cy * sx])) {
                continue;
            }
            if (patterns[i][5] && !(cx + 1 < 0 || cy < 0 || cx + 1 >= sx || cy >= sy || blocked[cx + 1 + cy * sx])) {
                continue;
            }
            if (patterns[i][6] && !(cx - 1 < 0 || cy + 1 < 0 || cx - 1 >= sx || cy + 1 >= sy || blocked[cx - 1 + (cy + 1) * sx])) {
                continue;
            }
            if (patterns[i][7] && !(cx < 0 || cy + 1 < 0 || cx >= sx || cy + 1 >= sy || blocked[cx + (cy + 1) * sx])) {
                continue;
            }
            if (patterns[i][8] && !(cx + 1 < 0 || cy + 1 < 0 || cx + 1 >= sx || cy + 1 >= sy || blocked[cx + 1 + (cy + 1) * sx])) {
                continue;
            }
            return 1.0f;
        }
        //				System.out.println("Position " + x + "," + y + " satisfies pattern " + i + " -> candidate blocker!");
        {
            // block the coordinate and see if there is a path to any of the opponents (we only need to see if there is
            // a path to one of them, since form any of them there has to be a path to the rest (since all of them are
            // satisfying this restriction):
            blocked[cx + cy * sx] = true;
            int node = (int) (own_base.getx() / 16 + (own_base.gety() / 16) * sx);
            java.util.List<Integer> open = new java.util.LinkedList<Integer>();
            java.util.HashSet<Integer> visited = new java.util.HashSet<Integer>();
            open.add(node);
            visited.add(node);
            while (!open.isEmpty()) {
                node = open.remove(0);
                int x = node % sx;
                int y = node / sx;

                if (!(x - 1 < 0 || y < 0 || x - 1 >= sx || y >= sy || blocked[x - 1 + y * sx])
                    && !visited.contains(node - 1)) {
                    open.add(node - 1);
                    visited.add(node - 1);
                }
                if (!(x + 1 < 0 || y < 0 || x + 1 >= sx || y >= sy || blocked[x + 1 + y * sx])
                    && !visited.contains(node + 1)) {
                    open.add(node - 1);
                    visited.add(node - 1);
                }
                if (!(x < 0 || y - 1 < 0 || x >= sx || y - 1 >= sy || blocked[x + (y - 1) * sx])
                    && !visited.contains(node - sx)) {
                    open.add(node - sx);
                    visited.add(node - sx);
                }
                if (!(x < 0 || y + 1 < 0 || x >= sx || y + 1 >= sy || blocked[x + (y + 1) * sx])
                    && !visited.contains(node + sx)) {
                    open.add(node + sx);
                    visited.add(node + sx);
                }
            }
            node = (int) (opponent_base.getx() / 16 + (opponent_base.gety() / 16) * sx);

            if (visited.contains(node)) {
                return 1.0f;
            }

            return 0.0f;
        }

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
    public java.util.List<Pair<String, ActionParameterType>> getNeededParameters() {

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
    public static java.util.List<Pair<String, ActionParameterType>> getStaticNeededParameters() {

        return _listOfNeededParameters;

    } // getStaticNeededParameters
    //---------------------------------------------------------------
    //                       Static fields
    //---------------------------------------------------------------
    static java.util.List<Pair<String, ActionParameterType>> _listOfNeededParameters;

    //---------------------------------------------------------------
    //                       Static initializers
    //---------------------------------------------------------------
    static {

        // Add parameters to _listOfNeededParameters.
        _listOfNeededParameters = new java.util.LinkedList<Pair<String, ActionParameterType>>(gatech.mmpm.sensor.Sensor.getStaticNeededParameters());
        _listOfNeededParameters.add(new Pair<String, ActionParameterType>("coor", ActionParameterType.COORDINATE));


    } // static initializer
} // class NonBlockingPath
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

