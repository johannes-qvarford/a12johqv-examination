package s2.base;

import gatech.mmpm.Action;
import gatech.mmpm.ConfigurationException;
import gatech.mmpm.learningengine.IMEExecutor;
import gatech.mmpm.learningengine.MEExecutorFactory;
import gatech.mmpm.tracer.ITracer;
import jargs.gnu.CmdLineParser;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import s2.actionControllers.ActionController;
import s2.actionControllers.AttackActionController;
import s2.actionControllers.HarvestActionController;
import s2.actionControllers.MoveActionController;
import s2.actionControllers.RepairActionController;
import s2.base.ai.AI;
import s2.base.ai.AIEmpty;
import s2.base.ai.AI_D2;
import s2.base.ai.RushAI;
import s2.entities.S2Entity;
import s2.entities.WPlayer;
import s2.entities.WUnit;
import s2.entities.buildings.WBuilding;
import s2.entities.buildings.WGoldMine;
import s2.entities.map.WOMapEntity;
import s2.entities.map.WOTree;
import s2.entities.troops.WPeasant;
import s2.entities.troops.WTroop;
import s2.game.HUD;
import s2.game.S2;
import s2.helpers.MouseHandler;

public class S2App extends Canvas {
	private static final boolean TIME_DEBUG = false;

	private static final String PLAY = "Click the mouse to continue...";

	private static final String COMMENTS = "For any comment/question, contact: jai@cc.gatech.edu";

	private static final String TECH = "Georgia Tech";

	private static final String LAB = "Cognitive Computing Lab";

	private static final String AUTHORS = "Jai Rad, Kane Bonnette, Santi Ontanon Villar (2008)";

	private static final String TITLE = "Multi-Player S2";

	/** size of a cell side */
	public static int CELL_SIZE = 32;

	/** Leeway for mouse click */
	public static int MOUSE_CLICK_LEEWAY = 5;

	private static final long serialVersionUID = 1L;

	private BufferStrategy strategy;

	private boolean gameRunning = true;

	public boolean waitingForKeyPress = false;

	public static final int SCREEN_X = 800;

	public static final int SCREEN_Y = 600;

	public static final int REDRAWING_PERIOD = 5;
//	public static final int REDRAWING_PERIOD = 2;

	public static final int MAX_FRAMESKIP = 10;

	public static final int m_trace_interval = 500;

	public static final int STATE_INIT = 0;

	public static final int STATE_GAME = 1;

	public static final int STATE_QUITTING = 2;

	public static final int STATE_QUIT = 3;

	private int m_state = STATE_INIT;

	private int m_state_cycle = 0;

	private List<PlayerInput> m_pi_l = new LinkedList<PlayerInput>();

	private Font m_font32, m_font16;

	String _userName = null;

	private S2 m_game;

	private String mapName;

	// Add the mouse handler
	private MouseHandler m_mouse_handler;
	
	JFrame container = null;

	protected ActionController selectedAction = null;

	protected Set<WUnit> selectedEntities = null;

	protected HUD theHUD;

	List<AI> m_ai_l = new LinkedList<AI>();

	gatech.mmpm.IDomain idomain = null;

	gatech.mmpm.tracer.ITracer tracer = null;

	HashMap<ActionController, Action> m_actionMaps = new HashMap<ActionController, Action>();

	// coordinates for the selection drag box
	private int startDragX = -1, startDragY = -1, currDragX = -1, currDragY = -1;

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
		System.out.println("S2: play S2 according to the config file.");
		System.out.println();
		System.out.println("Usage: S2 -m map -i interval [-t method] [-u user] [-p playerType|idname|AIType|ME]...");
		System.out.println();
		System.out.println("\t-m|--map: map file rute name.");
		System.out.println("\t-i|--interval: int, interval trace.");
		System.out.println(gatech.mmpm.tracer.TracerFactory.getUserFriendlyHelp());
		System.out.println("\t-u|--user: player name who generates the trace.");
		System.out.println("\t-p|--player: playerType|idname|AIType|ME. Note: | is a separator of player fields.");
		System.out.println("\t             Where playerType: an int: ");
		System.out.println("\t		             INPUT_NONE = -1");
		System.out.println("\t		             INPUT_MOUSE = 0");
		System.out.println("\t		             INPUT_AI = 1");
		System.out.println("\t		             INPUT_EXTERNAL = 2");
		System.out.println("\t             Where idname: player name");
		System.out.println("\t             Where AIType (in case playerType = 1): ai-random|ME");
		System.out.println("\t             Where ME (in case AIType = ME):");
		System.out.println(MEExecutorFactory.getUserFriendlyHelp());
		System.out.println();
		System.out.println("Examples:");
		System.out.println("  S2 -t file:trace.xml config.xml -p 0|Peter|ia-random -m ./map1.xml -i 50");
		System.out.println("     Launches S2 and save the traces to trace.xml");
		System.out.println();
		System.out.println("  S2 -t file config.xml [...]");
		System.out.println("     Launches S2 and save the trace to a file whose name");
		System.out.println("     is chosen based on the current time.");
		System.out.println();
		System.out.println("  S2 -t remote:S2portal.com:8888 config.xml [...]");
		System.out.println("     Launches S2 and send the trace to the server S2portal.com");
	}

	public static void main(String args[]) {
		internalMain(args, -1);
	}

	public static String internalMain(String args[], int maxCycles) {

		CmdLineParser parser = new CmdLineParser();

		CmdLineParser.Option traceOpt = parser.addStringOption('t', "trace");
		CmdLineParser.Option userOpt = parser.addStringOption('u', "user");
		CmdLineParser.Option helpOpt = parser.addBooleanOption('h', "help");
		
		CmdLineParser.Option playerOpt = parser.addStringOption('p', "player");
		CmdLineParser.Option mapOpt = parser.addStringOption('m', "map");
		CmdLineParser.Option intervalOpt = parser.addIntegerOption('i', "interval");
		
        try {
        	parser.parse(args);
        } catch (CmdLineParser.OptionException e) {
        	System.err.println(e.getMessage());
        	printUsage();
        	System.exit(1);
        }

        boolean help = (Boolean)parser.getOptionValue(helpOpt, false);
        if (help) {
        	printUsage();
        	System.exit(0);
        }
        
        String saveTraceOpt;
        String userName = null;
        String map = null;
        int traceInterval = -1;
        List<PlayerInput> players = new LinkedList<PlayerInput>();
        ITracer tracerUsed = null;

        saveTraceOpt = (String)parser.getOptionValue(traceOpt);
        userName = (String)parser.getOptionValue(userOpt);
        map = (String)parser.getOptionValue(mapOpt);
        traceInterval = (Integer)parser.getOptionValue(intervalOpt);

		// Analyze properties parameters and create the Properties
		// object.
		java.util.Vector<?> propStrings = parser.getOptionValues(playerOpt);
		for (Object prop:propStrings) {
			String current = (String) prop;
			// We must split the string using the '|' as separator.
			String[] splitStr = current.split("\\|");
			if ( (splitStr.length < 2) || (splitStr.length > 4) ) {
				printUsage();
				System.out.println("A player specification must be something like -p int|id|name|AItype|ME");
				return null;
			}
			PlayerInput pi = new PlayerInput();
			pi.m_inputType = new Integer(splitStr[0]);
			if ( (pi.m_inputType < 0) || (pi.m_inputType > 2) ) {
				System.out.println("A Type of a player specification must be an integer:");
				System.out.println("		INPUT_NONE = -1");
				System.out.println("		INPUT_MOUSE = 0");
				System.out.println("		INPUT_AI = 1");
				return null;
			}
			pi.m_playerID = splitStr[1];
			pi.m_playerName = splitStr[1];
			if(splitStr.length > 2){
				pi.AIType = splitStr[2];
				if(splitStr.length > 3)
					pi.ME = splitStr[3];
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
			S2App app = new S2App(map, traceInterval, players, tracerUsed);
			if (userName != null)
				app.setUserName(userName);
			app.gameLoop(maxCycles);
			String retval = app.getWinner();
			return retval;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	private void gameLoop(int maxCycles) {
		long time = System.currentTimeMillis();
		long actTime;

		boolean need_to_redraw = true;

		if (tracer != null) {
			tracer.beginTrace();
			java.util.Properties prop = new java.util.Properties();
			prop.setProperty("domain", idomain.getName());
			prop.setProperty("map", mapName);
			{
				String players_string = "";
				for (PlayerInput p : m_pi_l) {
					players_string += "<player>" + p.m_playerID + "</player>";
				}
				prop.setProperty("players", players_string);
			}
			if (_userName != null)
				prop.setProperty("user", _userName);
			tracer.putMetadata(prop);
		}

		while (gameRunning) {
			actTime = System.currentTimeMillis();
			if (actTime - time >= REDRAWING_PERIOD) {
				int max_frame_step = MAX_FRAMESKIP;
				do {
					time += REDRAWING_PERIOD;
					if (!cycle(maxCycles)) {
						gameRunning = false;
					}
					need_to_redraw = true;
					actTime = System.currentTimeMillis();
					max_frame_step--;
				} while (actTime - time >= REDRAWING_PERIOD && max_frame_step > 0);
				if ((actTime - time) > MAX_FRAMESKIP * REDRAWING_PERIOD) {
					time = actTime;
				}
			} /* if */

			/* Redraw */
			if (need_to_redraw) {
				need_to_redraw = false;

				draw();
			} /* if */

			try {
				Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (maxCycles>=0 && m_game.getCycle()>maxCycles) gameRunning = false;
		}

		if (tracer != null) {
			tracer.endTrace(idomain, getWinner());
			if (!tracer.success()) {
				System.err.println("There were some errors saving the trace:");
				System.err.println(tracer.getErrorMessage());
			}
		}
		
        container.getContentPane().remove(this);
		container.dispose();

        for(AI ai:m_ai_l) {
            ai.gameEnd();
        }		
	}

	public S2App(String map, int traceInterval, List<PlayerInput> players, 
			 gatech.mmpm.tracer.ITracer a_tracer) throws Exception {

		idomain = new s2.mmpm.S2Domain();
		tracer = a_tracer;

		try {
			container = new JFrame("S2");
		} catch (HeadlessException e) {
			System.err.println("Error opening window");
			System.exit(1);
		}
		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(SCREEN_X, SCREEN_Y));
		panel.setLayout(null);

		setBounds(0, 0, SCREEN_X, SCREEN_Y);
		panel.add(this);

		setIgnoreRepaint(true);

		container.pack();
		container.setResizable(false);
		container.setVisible(true);

		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// Open configuration
		getConfigInfo(map, traceInterval, players, tracer);

		requestFocus();
		createBufferStrategy(2);
		strategy = getBufferStrategy();

		m_font32 = new Font("Arial", Font.PLAIN, 32);
		m_font16 = new Font("Arial", Font.PLAIN, 16);

		selectedEntities = new TreeSet<WUnit>();
	}

	// Reads the config file and loads the game info : mouse, AI, map etc
	private void getConfigInfo(String map, int traceInterval, List<PlayerInput> players, 
			 gatech.mmpm.tracer.ITracer a_tracer) throws Exception, IOException {

		tracer = a_tracer;
		mapName = map;
		// create the Game object if the file exists
		Document game_doc = null;
		SAXBuilder builder = new SAXBuilder();
		try {
			// When BattleCity is a JAR
			InputStream f = this.getClass().getResourceAsStream("/" + mapName);
			game_doc = builder.build(f);
		} catch (Exception e) {
			// When it's not:
			game_doc = builder.build(mapName);
		}

		if (game_doc != null) {
			m_game = new S2(game_doc);
			// Create the HUD
			theHUD = new HUD(m_game);
		} else {
			System.err.println("No MAP file found!");
		}
		
		m_pi_l = players;
		for(PlayerInput pi:players) {

			if (pi.m_inputType == PlayerInput.INPUT_AI) {
				AI ai;

				if (pi.AIType.equals("ai-empty")) {
					// Create the AI objects here
					ai = new AIEmpty(pi.m_playerID);
					ai.gameStarts();
					pi.m_inputType = PlayerInput.INPUT_AI;
				}

				else if (pi.AIType.equals("ai-rush")) {
					// Create the AI objects here
					ai = new RushAI(pi.m_playerID);
					ai.gameStarts();
				}

				else if (pi.AIType.equals("ME")) {
					if ( !pi.ME.isEmpty() ) {
						ai = createD2IA(pi);
						ai.gameStarts();
					}
					else 
						throw new Exception("The ME to load must be specified.");
				}
				else
					throw new Exception(pi.m_playerID + " AIType must be \"ai-empty\", \"ai-rush\" or \"ME\".");

				m_ai_l.add(ai);
			}

		}
		m_mouse_handler = new MouseHandler(SCREEN_X, SCREEN_Y, this);
		addMouseListener(m_mouse_handler);
		addMouseMotionListener(m_mouse_handler);

	}

	private boolean cycle(int maxCycles) {
		int old_state = m_state;

		switch (m_state) {
		case STATE_INIT:
			m_state = init_cycle();
			break;
		case STATE_GAME:
			m_state = game_cycle();
			break;
		case STATE_QUITTING:
			m_state = quitting_cycle(maxCycles);
			break;
		default:
			return false;
		}

		if (old_state == m_state) {
			m_state_cycle++;
		} else {
			m_state_cycle = 0;
		}

		return true;
	}

	private boolean draw() {
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, SCREEN_X, SCREEN_Y);

		switch (m_state) {
		case STATE_INIT:
			init_draw(g);
			break;
		case STATE_GAME:
			game_draw(g);
			break;
		case STATE_QUITTING:
			quitting_draw(g);
			break;
		}
		g.dispose();
		strategy.show();

		return true;
	}

	private int init_cycle() {

		// Only wait for mouse click if there is one human in the game:
		{
			boolean isThereAnyHuman = false;
			for (PlayerInput pi : m_pi_l)
				if (pi.m_inputType == PlayerInput.INPUT_MOUSE)
					isThereAnyHuman = true;
			if (!isThereAnyHuman)
				return STATE_GAME;
		}

		return STATE_INIT;
	}

	private void init_draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setFont(m_font32);
		g.drawString(TITLE, (getWidth() - g.getFontMetrics().stringWidth(TITLE)) / 2, 235);
		g.setFont(m_font16);
		g.drawString(AUTHORS, (getWidth() - g.getFontMetrics().stringWidth(AUTHORS)) / 2, 295);
		g.drawString(LAB, (getWidth() - g.getFontMetrics().stringWidth(LAB)) / 2, 315);
		g.drawString(TECH, (getWidth() - g.getFontMetrics().stringWidth(TECH)) / 2, 335);
		g.drawString(COMMENTS, (getWidth() - g.getFontMetrics().stringWidth(COMMENTS)) / 2, 355);
		g.drawString(PLAY, (getWidth() - g.getFontMetrics().stringWidth(PLAY)) / 2, 395);
	}

	private int game_cycle() {
		List<ActionController> failedActions = new LinkedList<ActionController>();
		List<ActionController> actions; // = new LinkedList<Action>();
		int cycle = m_game.getCycle();
		boolean traceEntryStarted = false;
		long start_time = System.currentTimeMillis();
		long t1, t2, t3, t4;

		// create list up (synchronized) and listener appends actions
		actions = update_input();

		t1 = System.currentTimeMillis();

		if (!m_game.cycle(failedActions)) {
			// Save the last state:
			if (tracer != null) {
				tracer.beginGameCycle(m_game.getCycle());
				tracer.putGameState(s2.mmpm.Game2D2Converter.toGameState(m_game,idomain));
				tracer.endGameCycle();
			}
			return STATE_QUITTING;
		}

		t2 = System.currentTimeMillis();

		// Save trace entry:
		if (tracer != null) {
			if (m_game != null && (cycle % m_trace_interval) == 0 || !actions.isEmpty()) {
				traceEntryStarted = true;
				tracer.beginGameCycle(cycle);
				tracer.putGameState(s2.mmpm.Game2D2Converter.toGameState(m_game,idomain));
				for (ActionController a : actions) {
					List<Action> d2a = s2.mmpm.Game2D2Converter.toD2Action(a);
					for (Action act : d2a) {
						m_actionMaps.put(a, act);
						tracer.putAction(act);
					}
				}
			}
		}

		t3 = System.currentTimeMillis();

		// perform the action
		performActions(actions);

		t4 = System.currentTimeMillis();

		for (ActionController a : failedActions) {

			if (tracer != null) {
				if (!traceEntryStarted) {
					traceEntryStarted = true;
					tracer.beginGameCycle(cycle);
					tracer.putGameState(s2.mmpm.Game2D2Converter.toGameState(m_game,idomain));
				}
				tracer.putAbortedAction(m_actionMaps.get(a));
			}
		}

		if (traceEntryStarted)
			tracer.endGameCycle();

		if (TIME_DEBUG)
			System.out.println("Time: " + start_time + ": input " + (t1 - start_time) + " - cycle "
					+ (t2 - t1) + " - trace " + (t3 - t2) + " - actions " + (t4 - t3));

		// scroll();

		return STATE_GAME;
	}

	private void game_draw(Graphics2D g) {
		m_game.draw(g, selectedEntities);
		// draw the HUD
		theHUD.draw(g, selectedEntities, selectedAction, m_pi_l);

		drawDragBox(g);

	}

	/**
	 * draws the box for drag and select
	 * 
	 * @param g
	 */
	private void drawDragBox(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.drawLine(startDragX, startDragY, startDragX, currDragY);
		g.drawLine(startDragX, startDragY, currDragX, startDragY);
		g.drawLine(startDragX, currDragY, currDragX, currDragY);
		g.drawLine(currDragX, startDragY, currDragX, currDragY);
	}

	private int quitting_cycle(int maxCycles) {

		if (maxCycles>=0) return STATE_QUIT;

		return STATE_QUITTING;
	}

	private void quitting_draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setFont(m_font32);
		String over = "GAME OVER";
		g.drawString(over, (getWidth() - g.getFontMetrics().stringWidth(over)) / 2, 235);

	}

	private List<ActionController> update_input() {
		List<ActionController> actions = new LinkedList<ActionController>();
		// VirtualController selected_vc;

		for (PlayerInput pi : m_pi_l) {
			switch (pi.m_inputType) {
			case PlayerInput.INPUT_MOUSE:
				if (selectedAction != null && selectedAction.paramsSatisfied()) {
					actions.add((ActionController) selectedAction.clone());
					selectedAction.reset();
					selectedAction = null;
					theHUD.resetActionParams();
					m_mouse_handler.enableScrolling();
				}

				break;
			case PlayerInput.INPUT_AI: {
				// Find the AI:
				AI selected_AI = null;
				for (AI ai : m_ai_l) {
					if (ai.getPlayerId().equals(pi.m_playerID))
						selected_AI = ai;
				}

				if (selected_AI != null) {
					try {
						selected_AI.game_cycle(m_game, m_game.getPlayer(pi.m_playerID), actions);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
				break;
			}
		} // while

		return actions;
	}

	public S2 getGame() {
		return m_game;
	}

	/**
	 * handles the start of a drag action
	 * 
	 * @param current_screen_x
	 * @param current_screen_y
	 */
	public void mousePress(int current_screen_x, int current_screen_y) {
		if (m_state == STATE_GAME) {
			startDragX = current_screen_x;
			startDragY = current_screen_y;
			currDragX = current_screen_x;
			currDragY = current_screen_y;
		}
	}

	/**
	 * handles dragging actions
	 * 
	 * @param current_screen_x
	 * @param current_screen_y
	 */
	public void mouseDrag(int current_screen_x, int current_screen_y) {
		if (m_state == STATE_GAME) {
			currDragX = current_screen_x;
			currDragY = current_screen_y;
		}
	}

	/**
	 * handles end of dragging actions, and selecting all units in the given
	 * area. If multiple units are selected, only select the player's units. If
	 * multiple units are still selected, only select troops.
	 * 
	 * @param current_screen_x
	 * @param current_screen_y
	 */
	public void mouseRelease(int current_screen_x, int current_screen_y) {
		if (m_state == STATE_GAME) {
			if (current_screen_y > HUD.HUD_Y_LOC || selectedAction != null) {
				startDragX = -1;
				startDragY = -1;
				currDragX = -1;
				currDragY = -1;
				return;
			}

			// convert to map coords
			int map_x_start = startDragX + m_game.get_x_offset();
			int map_y_start = startDragY + m_game.get_y_offset();
			int map_x_end = current_screen_x + m_game.get_x_offset();
			int map_y_end = current_screen_y + m_game.get_y_offset();
			map_x_start = map_x_start / CELL_SIZE;
			map_y_start = map_y_start / CELL_SIZE;
			map_x_end = map_x_end / CELL_SIZE;
			map_y_end = map_y_end / CELL_SIZE;

			selectedEntities.clear();

			// get selected units
			for (int i = map_x_start < map_x_end ? map_x_start : map_x_end; i < (map_x_start > map_x_end ? map_x_start
					: map_x_end) + 1; i++) {
				for (int j = map_y_start < map_y_end ? map_y_start : map_y_end; j < (map_y_start > map_y_end ? map_y_start
						: map_y_end) + 1; j++) {
					WUnit unit = m_game.entityAt(i, j);
					if (null != unit) {
						selectedEntities.add(unit);
					}
				}
			}

			// clean up drag constants
			startDragX = -1;
			startDragY = -1;
			currDragX = -1;
			currDragY = -1;

			// just use this one
			if (selectedEntities.size() <= 1) {
				return;
			}

			List<WUnit> toRemove = new LinkedList<WUnit>();
			String player = null;
			// get player
			for (WPlayer p : m_game.getPlayers()) {
				if (p.getInputType() == PlayerInput.INPUT_MOUSE) {
					player = p.owner;
					break;
				}
			}
			// get enemy units
			for (WUnit u : selectedEntities) {
				if (u.owner == null || !u.owner.equals(player)) {
					toRemove.add(u);
				}
			}

			if (toRemove.size() == selectedEntities.size()) {
				// whoops, leave one;
				WUnit toKeep = selectedEntities.iterator().next();
				selectedEntities.clear();
				selectedEntities.add(toKeep);
				return;
			} else {
				selectedEntities.removeAll(toRemove);
			}

			// just use this one
			if (selectedEntities.size() == 1) {
				return;
			}

			toRemove.clear();

			// remove Buildings
			for (WUnit u : selectedEntities) {
				if (u instanceof WBuilding) {
					toRemove.add(u);
				}
			}

			if (toRemove.size() == selectedEntities.size()) {
				// select the buildings
				return;
			} else {
				selectedEntities.removeAll(toRemove);
			}

		}
	}

	/**
	 * handle mouse click events.
	 * 
	 * @param current_screen_x
	 * @param current_screen_y
	 */
	public void mouseClick(int current_screen_x, int current_screen_y) {
		int map_x = current_screen_x + m_game.get_x_offset();
		int map_y = current_screen_y + m_game.get_y_offset();
		map_x = map_x / CELL_SIZE;
		map_y = map_y / CELL_SIZE;

		if (m_state == STATE_INIT) {
			boolean isThereAnyHuman = false;
			for (PlayerInput pi : m_pi_l)
				if (pi.m_inputType == PlayerInput.INPUT_MOUSE)
					isThereAnyHuman = true;
			if (isThereAnyHuman)
				m_state = STATE_GAME;
			return;
		}

		if (m_state == STATE_GAME) {
			gameMouseClick(current_screen_x, current_screen_y, map_x, map_y);
		}

		if (m_state == STATE_QUITTING) {
			gameRunning = false;
		}

	}

	private void gameMouseClick(int current_screen_x, int current_screen_y, int map_x, int map_y) {
		System.out.println(selectedEntities.size() + " " + selectedAction);
		if (selectedEntities.size() == 0) {
			WUnit unit = m_game.entityAt(map_x, map_y);
			if (null != unit) {
				selectedEntities.add(unit);
			}
			if (selectedEntities.size() > 0) {
				// Remove action params (if any)
				theHUD.resetActionParams();
				snapToCenter(current_screen_x, current_screen_y);
			}
			return;
		}

		System.out.println("not getting a new unit");

		if (selectedAction == null) {
			selectedAction = theHUD.hudClick(current_screen_x, current_screen_y);
			if (selectedAction == null) {
				WUnit newSelectedEntity = m_game.entityAt(map_x, map_y);
				WUnit unit = selectedEntities.iterator().next();
				String owner = selectedEntities.iterator().next().owner;
				if (newSelectedEntity != null) {
					if ((owner != null && owner.equals(newSelectedEntity.getOwner()))
							|| (owner == null && newSelectedEntity.getOwner() == null)) {
						if (unit instanceof WPeasant
								&& newSelectedEntity instanceof WBuilding
								&& newSelectedEntity.getCurrent_hitpoints() < newSelectedEntity
										.getMax_hitpoints()) {
							// repair the building
							selectedAction = new RepairActionController(selectedEntities,
									(WBuilding) newSelectedEntity, m_game);
						} else {
							// switch the entities
							selectedEntities.clear();
							selectedEntities.add(newSelectedEntity);
							theHUD.resetActionParams();
							snapToCenter(current_screen_x, current_screen_y);
						}
					} else {
						if (unit instanceof WPeasant && newSelectedEntity instanceof WGoldMine) {
							selectedAction = new HarvestActionController(selectedEntities,
									newSelectedEntity, m_game);
						} else if (unit instanceof WTroop) {
							selectedAction = new AttackActionController(selectedEntities,
									newSelectedEntity, m_game);
						} else {
							selectedEntities.clear();
							selectedEntities.add(newSelectedEntity);
							theHUD.resetActionParams();
							snapToCenter(current_screen_x, current_screen_y);
						}

					}
				} else {
					WOMapEntity mapEntity = m_game.mapEntityAt(map_x, map_y);
					if (unit instanceof WPeasant && mapEntity instanceof WOTree) {
						selectedAction = new HarvestActionController(selectedEntities,
								mapEntity, m_game);
						// ((WPeasant) selectedEntity).harvest(map_x, map_y);
					} else if (unit instanceof WTroop) {
						selectedAction = new MoveActionController(selectedEntities, map_x, map_y);
						// ((WTroop) selectedEntity).move(map_x, map_y);
					}
				}
				m_mouse_handler.enableScrolling();
				return;
			} else {
				selectedAction.setUnits(selectedEntities);
			}
			return;
		}

		System.out.println("not getting a new action");

		if (selectedAction != null && selectedEntities.size() > 0) {
			System.out.println("checking params for " + selectedAction);
			if (!selectedAction.clickForParams(map_x, map_y, current_screen_x, current_screen_y,
					theHUD)) {
				System.out.println("params returned false, resetting");
				selectedAction = null;
			} else {
				System.out.println("params returned true");
				// fixScrolling();
			}
		}
	}

	public void snapToCenter(int current_screen_x, int current_screen_y) {
		m_game.set_x_offset(current_screen_x - 800 / 2);
		m_game.set_y_offset(current_screen_y - 525 / 2); // 525 is the fix
		// for HUD considering 1/8th of screen for HUD
	}

	private void performActions(List<ActionController> actions) {
		for (ActionController action : actions)
			action.performAction();
	}

	// / It could throw d2.core.ConfigurationException and java.io.IOException.
	private AI createD2IA(PlayerInput pi) throws Exception {

		// Should we configure D2 before loading IA?
		if (idomain == null) {
			idomain = new s2.mmpm.S2Domain();
		}
		IMEExecutor meExec = MEExecutorFactory.BuildMEExecutor(pi.ME,idomain);
	
		
		// Create S2 AI
		AI_D2 ai = new AI_D2(meExec,pi.m_playerID,idomain);
		
		return ai;
	}

	public String getWinner() {
		String owner = null;
		for (S2Entity e : m_game.getUnits()) {
			if (null == owner) {
				owner = e.getOwner();
			} else {
				if (!owner.equals(e.getOwner())) {
					// we have units from more than one player
					System.out.println("Two Players remaining: '" + owner + "' and '" + e.getOwner() + "'");
					return null;
				}
			}

		}
		
		// this is the only player with units; they must have won:
		for (PlayerInput p : m_pi_l) {
			if (p.m_playerID.equals(owner)) return p.m_playerName;
		}

		return owner;
	}

}
