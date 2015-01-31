package bc;

import java.util.LinkedList;
import java.util.List;

import bc.PlayerInput;
import gatech.mmpm.learningengine.MEExecutorFactory;
import gatech.mmpm.tracer.ITracer;
import jargs.gnu.CmdLineParser;

/**
 * Main class of the "BattleCityJ" application. It just contains the main method
 * that launch all the application.
 *
 * @author Marco Antonio G�mez Mart�n and David Llanso edited by: Santi Onta��n
 */
public class Main {

    public static ITracer configureTracer(String appArg) {
        try {
            return gatech.mmpm.tracer.TracerFactory.BuildTracer(appArg);
        } catch (gatech.mmpm.ConfigurationException ex) {
            System.err.println("Error creating the tracer: ");
            System.err.println(ex.getMessage());
            System.err.println("Traces will not be generated");
            ex.printStackTrace();
            return null;
        }
    }

    public static void printUsage() {
        System.out.println("BattleCityJ: play BattleCity according to the config file.");
        System.out.println();
        System.out.println("Usage: BattleCityJ -m map -i interval [-t method] [-u user] [-p playerType|idname|AIType|ME]...");
        System.out.println();
        System.out.println("\t-m|--map: map file rute name.");
        System.out.println("\t-i|--interval: int, interval trace.");
        System.out.println(gatech.mmpm.tracer.TracerFactory.getUserFriendlyHelp());
        System.out.println("\t-u|--user: player name who generates the trace.");
        System.out.println("\t-p|--player: playerType|idname|up|down|left|right|fire. If playerType = 0.");
        System.out.println("\t             playerType|idname|AIType|ME. If playerType = 1.");
        System.out.println("\t             Note: | is a separator of the player fields.");
        System.out.println("\t             Where playerType: an int: ");
        System.out.println("\t		             INPUT_NONE = -1");
        System.out.println("\t		             INPUT_MOUSE = 0");
        System.out.println("\t		             INPUT_AI = 1");
        System.out.println("\t		             INPUT_EXTERNAL = 2");
        System.out.println("\t             Where idname: player name");
        System.out.println("\t             Where up|down|left|right|fire: Key numbers");
        System.out.println("\t             Where AIType (in case playerType = 1): bc.ai.AIRandom|ME");
        System.out.println("\t             Where ME (in case AIType = ME):");
        System.out.println(MEExecutorFactory.getUserFriendlyHelp());
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  BattleCityJ -t file:trace.xml config.xml -p 0|Peter|ai.AIRandom -m ./map1.xml -i 50");
        System.out.println("     Launches BattleCityJ and save the traces to trace.xml");
        System.out.println();
        System.out.println("  BattleCityJ -t file config.xml [...]");
        System.out.println("     Launches BattleCityJ and save the trace to a file whose name");
        System.out.println("     is chosen based on the current time.");
        System.out.println();
        System.out.println("  BattleCityJ -t remote:BCportal.com:8888 config.xml [...]");
        System.out.println("     Launches BattleCityJ and send the trace to the server BCportal.com");
    }

    public static void main(String args[]) {
        internalMain(args, -1);
    }

    public static String internalMain(String args[], int maxCycles) 
    {
        BattleCityApp app = setupApp(args);
        try {
            String retval = app.gameLoop(maxCycles);
            app.clear();
            return retval;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }        

    public static BattleCityApp setupApp(String args[]) {
        CmdLineParser parser = new CmdLineParser();

        CmdLineParser.Option traceOpt = parser.addStringOption('t', "tracer");
        CmdLineParser.Option userOpt = parser.addStringOption('u', "user");
        CmdLineParser.Option helpOpt = parser.addBooleanOption('h', "help");

        CmdLineParser.Option playerOpt = parser.addStringOption('p', "player");
        CmdLineParser.Option mapOpt = parser.addStringOption('m', "map");
        CmdLineParser.Option intervalOpt = parser.addIntegerOption('i', "interval");

        String saveTraceOpt = null;
        String userName = null;
        String map = null;
        int traceInterval = -1;
        List<PlayerInput> players = new LinkedList<PlayerInput>();
        ITracer tracerUsed = null;

        try {
            parser.parse(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            printUsage();
            System.exit(1);
        }

        saveTraceOpt = (String) parser.getOptionValue(traceOpt);
        userName = (String) parser.getOptionValue(userOpt);
        map = (String) parser.getOptionValue(mapOpt);
        traceInterval = (Integer) parser.getOptionValue(intervalOpt);


        boolean help = (Boolean) parser.getOptionValue(helpOpt, (Boolean) false);
        if (help) {
            printUsage();
            System.exit(0);
        }

        // Analyze properties parameters and create the Properties
        // object.
        java.util.Vector<?> propStrings = parser.getOptionValues(playerOpt);
        for (Object prop : propStrings) {
            String current = (String) prop;
            // We must split the string using the '|' as separator.
            String[] splitStr = current.split("\\|");
            if ((splitStr.length < 2) || (splitStr.length > 7)) {
                printUsage();
                System.out.println("A player specification must be something like -p int|idname|AItype|ME");
                return null;
            }
            PlayerInput pi = new PlayerInput();
            pi.m_inputType = new Integer(splitStr[0]);
            if ((pi.m_inputType < 0) || (pi.m_inputType > 2)) {
                System.out.println("A Type of a player specification must be an integer:");
                System.out.println("		INPUT_NONE = -1");
                System.out.println("		INPUT_MOUSE = 0");
                System.out.println("		INPUT_AI = 1");
                System.out.println("		INPUT_EXTERNAL = 2");
                return null;
            }
            pi.m_playerID = splitStr[1];
            pi.m_playerName = splitStr[1];
            if ((pi.m_inputType == PlayerInput.INPUT_KEYBOARD) && (splitStr.length == 7)) {
                pi.m_keyboardCfg[PlayerInput.DIRECTION_UP] = Integer.parseInt(splitStr[2]);
                pi.m_keyboardCfg[PlayerInput.DIRECTION_DOWN] = Integer.parseInt(splitStr[3]);
                pi.m_keyboardCfg[PlayerInput.DIRECTION_LEFT] = Integer.parseInt(splitStr[4]);
                pi.m_keyboardCfg[PlayerInput.DIRECTION_RIGHT] = Integer.parseInt(splitStr[5]);
                pi.m_keyboardCfg[PlayerInput.FIRE] = Integer.parseInt(splitStr[6]);
            } else if ((pi.m_inputType == PlayerInput.INPUT_AI) && (splitStr.length > 2)) {
                pi.AIType = splitStr[2];
                if (splitStr.length > 3) {
                    pi.ME = splitStr[3];
                }
            } else {
                printUsage();
                return null;
            }
            players.add(pi);
        } // for

        if (saveTraceOpt != null) {
            tracerUsed = configureTracer(saveTraceOpt);
        }

        String[] remainingOpts = parser.getRemainingArgs();

        if (remainingOpts.length > 0) {
            printUsage();
            System.exit(1);
        }

        // At this point, the parameters have been validated. Start!
        try {
            BattleCityApp app = new BattleCityApp(map, traceInterval, players, tracerUsed);

            if (userName != null) {
                app.setUserName(userName);
            }
            return app;        
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
