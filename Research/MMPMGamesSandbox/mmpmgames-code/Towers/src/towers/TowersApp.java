package towers;


import gatech.mmpm.ActionParameterType;
import gatech.mmpm.GameState;
import gatech.mmpm.learningengine.IMEExecutor;
import gatech.mmpm.learningengine.MEExecutorFactory;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


import towers.ai.AI;
import towers.helpers.Auxiliar;
import towers.helpers.MouseHandler;
import towers.objects.Entity;
import towers.objects.PhysicalEntity;
import towers.objects.TBase;
import towers.objects.TPlayer;


public class TowersApp extends Canvas {
	private static final long serialVersionUID = 1L;
	
	private BufferStrategy strategy;
	private boolean gameRunning = true;

	public static final int SCREEN_X = 640;
	public static final int SCREEN_Y = 480; 
	public static final int REDRAWING_PERIOD = 20;
	public static final int m_trace_interval = 100;
	public static final int MAX_FRAME_SKIP = 10;

	public static final int STATE_INIT = 0;
	public static final int STATE_GAME = 1;
	public static final int STATE_QUITTING = 2;

	int m_state = STATE_INIT, m_previous_state = STATE_INIT;
	int m_state_cycle = 0;

	Font m_font32,m_font16;

	JFrame container = null;
	private MouseHandler m_mouse_handler;
	int m_button_selected = 0;

	String _userName = null;
	
	// Game info:
	Towers m_game = null;
	String m_mapName = null;
	List<PlayerInput> m_pi_l = new LinkedList<PlayerInput>();
	List<PlayerInput> m_still_not_ready = new LinkedList<PlayerInput>();
	List<towers.ai.AI> m_ai_l = new LinkedList<towers.ai.AI>();

	// External input queues:
	List<Action> m_mouseActionQueue = new LinkedList<Action>();
	boolean m_synchronized = false;
	int m_synchronized_remaining_cycles = 0;

	int m_map_center_x = 320,m_map_center_y = 280;
	float m_zoom = 1.0f;

	gatech.mmpm.IDomain idomain = null;	
	gatech.mmpm.tracer.ITracer tracer = null;
	
	public void setUserName(String userName) {
		_userName = userName;
	}
	
	public void gameLoop(int maxCycles) throws ClassNotFoundException, IOException {
		long time = System.currentTimeMillis();
		long actTime;

		boolean need_to_redraw = true;

		if (tracer != null) {
			try {
				configureDomain();
				tracer.beginTrace();
				java.util.Properties prop = new java.util.Properties();
				prop.setProperty("domain", idomain.getName());
				prop.setProperty("map", m_mapName);
				{
					String players_string = "";
					for(PlayerInput p:m_pi_l) {
						players_string+="<player>" + p.m_playerID + "</player>";
					}
					prop.setProperty("players", players_string);				
				}
				if (_userName != null)
					prop.setProperty("user", _userName);
				tracer.putMetadata(prop);
			} catch (gatech.mmpm.ConfigurationException ex) {
				System.err.println("Exception configuring D2.");
				System.err.println("Traces will not be generated.");
				tracer = null;
			}
		}
		
		while (gameRunning) {
			actTime = System.currentTimeMillis();
			if (actTime - time >= REDRAWING_PERIOD) {
				int max_frame_step = MAX_FRAME_SKIP;
				do {
					time += REDRAWING_PERIOD;
					if ((actTime - time) > 10*REDRAWING_PERIOD)
						time = actTime;
					if (!cycle(maxCycles)) gameRunning = false;
					need_to_redraw = true;

					actTime = System.currentTimeMillis();
					max_frame_step--;
				} while (actTime - time >= REDRAWING_PERIOD && max_frame_step > 0);
			} /* if */

			/* Redraw */
			if (need_to_redraw) {
				need_to_redraw = false;
				draw();				
			} /* if */			

			try { Thread.sleep(1); } catch (Exception e) {}
		}
		
		if (tracer != null) {
			tracer.endTrace(idomain, getWinner());
			if (!tracer.success()) {
				System.err.println("There were some errors saving thre trace:");
				System.err.println(tracer.getErrorMessage());
			}
		}
		
        container.getContentPane().remove(this);
		container.dispose();

        for(AI ai:m_ai_l) {
            ai.gameEnd();
        }		

	}


	public TowersApp(String map, int traceInterval, List<PlayerInput> players, 
					 gatech.mmpm.tracer.ITracer tracer, boolean sync)
					 throws Exception {
		
		this.tracer = tracer;
		
		container = new JFrame("Towers");

		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(SCREEN_X,SCREEN_Y));
		panel.setLayout(null);

		setBounds(0,0,SCREEN_X,SCREEN_Y);
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

		requestFocus();

		createBufferStrategy(2);
		strategy = getBufferStrategy();

		m_font32 = new Font("Arial", Font.PLAIN, 32);
		m_font16 = new Font("Arial", Font.PLAIN, 16);

		m_synchronized = sync;

		{

			m_mapName = map;
			m_pi_l = players;
			for(PlayerInput pi:players) {

				if (pi.m_inputType == PlayerInput.INPUT_AI) {
					if (pi.AIType.equals("ai-random")) {
						towers.ai.AIRandom ai = new towers.ai.AIRandom(pi.m_playerID);
						m_ai_l.add(ai);
					}
	
					else if (pi.AIType.equals("ME")) {
						if ( !pi.ME.isEmpty() ) {
							m_ai_l.add(createD2IA(pi));
						}
						else 
							throw new Exception("The ME to load must be specified.");
					}
					else
						throw new Exception(pi.m_playerID + " AIType must be \"ai-random\" or \"ME\".");
				}

			}

			// Create the Game
			{
				Document doc2;
				SAXBuilder builder = new SAXBuilder();
				try {
					// When Towers is a JAR
					InputStream f1 = this.getClass().getResourceAsStream("/" + m_mapName);
					doc2 = builder.build(f1);
				} catch (Exception e) {
					// When it's not:
					doc2 = builder.build(m_mapName);
				}
				Element map_xml = doc2.getRootElement();

				if (map_xml!=null) {
					m_game = new Towers(map_xml);
				} else {
					throw new Exception("TowersApp: Cannot open map file " + m_mapName);
				} // if 
			}
		}	

		m_mouse_handler = new MouseHandler(this);
		addMouseListener(m_mouse_handler);
	}


	boolean cycle(int maxCycles) throws ClassNotFoundException, IOException {
		int old_state = m_state;

		switch (m_state) {
		case STATE_INIT:
			m_state = init_cycle();
			break;
		case STATE_GAME:
			if (maxCycles!=-1 && m_game.getCycle()>maxCycles) {
				m_state = STATE_QUITTING;
			} else {
				m_state = game_cycle();
			}
			break;
		case STATE_QUITTING:
			m_state = quitting_cycle();
			break;
		default:
			return false;
		}

		if (old_state == m_state) {
			m_state_cycle++;
		} else {
			m_state_cycle = 0;
		}

		m_previous_state = old_state;

		return true;
	}

	boolean draw() throws ClassNotFoundException, IOException {

		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

		g.setColor(Color.black);
		g.fillRect(0,0,SCREEN_X,SCREEN_Y);	

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


	PlayerInput get_input(String player)
	{
		for(PlayerInput pi:m_pi_l) {
			if (pi.m_playerID.equals(player)) return pi;
		} // while 

		return null;
	}


	AI get_AI(String player)
	{
		for(towers.ai.AI ai:m_ai_l) {
			if (ai.getPlayerId().equals(player)) return ai;
		} // while 

		return null;
	}


	public void externalAdvanceCycles(int cycles) {
		m_synchronized_remaining_cycles+=cycles;
	}

	List<Action> update_input() throws ClassNotFoundException, IOException
	{
		List<Action> newActions = new LinkedList<Action>();

		for(PlayerInput pi:m_pi_l) {

			switch(pi.m_inputType) {
			case PlayerInput.INPUT_MOUSE:
			{
				List<Action> todelete = new LinkedList<Action>();

				for(Action a:m_mouseActionQueue) {
					if (a.m_actor.equals(pi.m_playerID)) {
						todelete.add(a);
					}
				}
				for(Action a:todelete) {
					m_mouseActionQueue.remove(a);
					newActions.add(a);
				}
			}
			break;
			case PlayerInput.INPUT_AI:		
			{
				AI ai = get_AI(pi.m_playerID);
				if (ai!=null) ai.cycle(m_state,m_game,newActions);
			}
			break;
			} // switch:
		} // while 

		return newActions;
	}  




	int init_cycle() throws ClassNotFoundException, IOException {
		List<Action> l = update_input();
		List<PlayerInput> ready = new LinkedList<PlayerInput>();

		if (m_state_cycle==0) {
			m_still_not_ready.addAll(m_pi_l);
		} // if 

		for(Action a:l) {
			for(PlayerInput p:m_still_not_ready) {
				if (p.m_playerID.equals(a.m_actor)) {
					ready.add(p);
				}
			}
		}

		for(PlayerInput pi:ready) m_still_not_ready.remove(pi);

		if (m_still_not_ready.isEmpty()) {
			// Game start!
			for (towers.ai.AI ai : this.m_ai_l)
				ai.gameStarts();
			return STATE_GAME;
		}
		return STATE_INIT;
	}


	void init_draw(Graphics2D g) throws ClassNotFoundException {	
		g.setColor(Color.WHITE);		
		g.setFont(m_font32);
		Auxiliar.drawCentered("Multi-Player Towers", 320, 150,g);
		g.setFont(m_font16);
		Auxiliar.drawCentered("Santi Onta��n Villar (2008)", 320, 190,g);
		Auxiliar.drawCentered("Cognitive Computing Lab (CCL)", 320, 210,g);
		Auxiliar.drawCentered("Georgia Tech", 320, 230,g);
		Auxiliar.drawCentered("For any comment/question, contact: santi@cc.gatech.edu", 320, 270,g);

		// Status:
		{
			List<Entity> l = null;
			Entity o2;
			float x = 8.0f;

			if (m_game!=null && m_game.getMap()!=null) {
				l = m_game.getObjects("TPlayer");
			} // if

			for(PlayerInput pi:m_pi_l) {
				o2 = null;
				if (l!=null) {
					for(Entity o:l) {
						if (((TPlayer)o).getowner().equals(pi.m_playerID)) o2=o;
					} // while 
				} // if 

				if (o2!=null) {
					g.setFont(m_font16);
					g.setColor(new Color(1,1.0f,1.0f));
					g.drawString(pi.m_playerName, x,24);

					if (m_still_not_ready.contains(pi)) {
						g.setColor(new Color(1,0.5f,0.5f));
						g.drawString("- Press Mouse -",x,68);
					} else {
						g.setColor(new Color(0.5f,1,0.5f));
						g.drawString("Ready",x,68);
					} // if

				} else {
					g.setFont(m_font16);
					g.drawString(pi.m_playerName, x,24);
					m_still_not_ready.remove(pi);
					g.drawString("Dead", x,68);
				} // if 

				x+=160;
			} // while 
		}
	}


	int game_cycle() throws ClassNotFoundException, IOException {
		List<Action> actions = null;
		boolean advance_cycle = false;

		if (!m_synchronized || m_synchronized_remaining_cycles>0) advance_cycle = true;
		if (advance_cycle && m_game!=null) {

			if (m_synchronized_remaining_cycles>0) m_synchronized_remaining_cycles--;

			actions = update_input();

			// Make a copy of the state before actions are executed, just in case we have to save it in the trace:
			int cycle = m_game.getCycle();
			GameState gs = towers.mmpm.Game2D2Converter.toGameState(m_game);

			if (!m_game.cycle(actions)) {
				// Save the last state:
				if (tracer != null) {
					tracer.beginGameCycle(m_game.getCycle());
					tracer.putGameState(towers.mmpm.Game2D2Converter.toGameState(m_game));
					tracer.endGameCycle();					
				}
				return STATE_QUITTING;
			}

			// Save trace entry:
			if (tracer != null) {
				if (m_game!=null && (cycle%m_trace_interval)==0 || !actions.isEmpty()) {
					tracer.beginGameCycle(cycle);
					tracer.putGameState(gs);
					for(Action a:actions)
						tracer.putAction(towers.mmpm.Game2D2Converter.toD2Action(a));
					tracer.endGameCycle();
				}				
			}			
		} // if 

		if (m_game==null) return STATE_QUITTING;

		return STATE_GAME;
	}


	void game_draw(Graphics2D g) throws ClassNotFoundException, IOException {
		if (m_game==null) return;

		AffineTransform at = g.getTransform();
		if (m_game.getMap()!=null) {
			float dx = (m_game.getMap().m_dx);
			float dy = (m_game.getMap().m_dy);

			if (dx>640.0f) {
				float z = 640.0f/dx;
				if (m_zoom>z) m_zoom = z;
			} // if 
			if (dy>480.0f) {
				float z = 400.0f/dy;
				if (m_zoom>z) m_zoom = z;
			} // if 

			// center:
			dx*=m_zoom;
			dy*=m_zoom;
			g.translate(m_map_center_x-(dx/2.0f),m_map_center_y-(dy/2.0f));
			g.scale(m_zoom,m_zoom);
		} // if
		m_game.draw(g);
		g.setTransform(at);
		
		String playerID = "";
		{
			for(PlayerInput pi:m_pi_l) if (pi.m_inputType==PlayerInput.INPUT_MOUSE) playerID = pi.m_playerID;
		}
		
		// Draw buttons:
		g.setColor(Color.DARK_GRAY);
		g.fillRect(4, 80, 80, 32);
		g.setColor(Color.WHITE);
		Auxiliar.drawCentered("TOWER: " + m_game.cost("towers.mmpm.entities.TTower", playerID), 44, 100, g);

		g.setColor(Color.DARK_GRAY);
		g.fillRect(4, 120, 80, 32);
		g.setColor(Color.WHITE);
		Auxiliar.drawCentered("WALL: " + m_game.cost("towers.mmpm.entities.TWall", playerID), 44, 140, g);

		g.setColor(Color.DARK_GRAY);
		g.fillRect(4, 160, 80, 32);
		g.setColor(Color.WHITE);
		Auxiliar.drawCentered("GOLD: " + m_game.cost("towers.mmpm.entities.TUpgradeGold", playerID), 44, 180, g);

		g.setColor(Color.DARK_GRAY);
		g.fillRect(4, 200, 80, 32);
		g.setColor(Color.WHITE);
		Auxiliar.drawCentered("UNIT: " + m_game.cost("towers.mmpm.entities.TUpgradeUnits", playerID), 44, 220, g);
		
		g.setColor(Color.GREEN);
		g.drawRect(2, 78+m_button_selected*40, 82, 34);

		// Status:
		{
			List<Entity> l = null;
			List<PhysicalEntity> l2 = null;
			Entity o2 = null;
			PhysicalEntity base = null;
			int x = 8;

			if (m_game!=null && m_game.getMap()!=null) {
				l = m_game.getObjects("TPlayer");
				l2 = m_game.getMap().getObjects(TBase.class);
			} // if

			for(PlayerInput pi:m_pi_l) {				
				g.setFont(m_font16);

				o2 = null;
				base = null;
				if (l!=null) {
					for(Entity o:l) {
						if (((TPlayer)o).getowner().equals(pi.m_playerID)) o2=o;
					} // while 
				} // if
				if (l2!=null) {
					for(PhysicalEntity o:l2) {
						if (((TBase)o).getowner().equals(pi.m_playerID)) base=o;
					} // while 
				} // if

				if (o2!=null && base!=null) {
					TPlayer player = (TPlayer)o2;
					g.setColor(new Color(player.get_r(),player.get_g(),player.get_b()));
					g.drawString(pi.m_playerName,x,24);
					g.drawString("Gold: " + player.getGold(),x,48);
					g.drawString("HP: " + ((TBase)base).getHP(),x,72);
				} else {
					g.setColor(Color.GRAY);
					g.drawString(pi.m_playerName,x,24);
					g.drawString("Dead",x,48);
				} // if 

				x+=160;
			} // while 

			g.setColor(Color.WHITE);
			g.drawString("" + m_game.getCycle(),1,460);
		}

	}


	int quitting_cycle() throws ClassNotFoundException, IOException {
		if (m_state_cycle==0) {
			m_still_not_ready.addAll(m_pi_l);
		} // if 

		List<Action> l = update_input();
		List<PlayerInput> ready = new LinkedList<PlayerInput>();

		for(Action a:l) {
			for(PlayerInput p:m_still_not_ready) {
				if (p.m_playerID.equals(a.m_actor)) {
					ready.add(p);
				}
			}
		}

		for(PlayerInput pi:ready) m_still_not_ready.remove(pi);

		if (m_still_not_ready.isEmpty()) {
			gameRunning = false;
		}
		return STATE_QUITTING;
	}


	void quitting_draw(Graphics2D g) throws ClassNotFoundException
	{		
		g.setColor(Color.WHITE);		
		g.setFont(m_font32);

		Auxiliar.drawCentered("Game Over",320,150,g);

		// Result:
		{
			List<PhysicalEntity> l = null;

			if (m_game!=null && m_game.getMap()!=null) {
				l = m_game.getMap().getObjects(TBase.class);
			} // if 

			if (l==null || l.size()==0) {
				Auxiliar.drawCentered("Draw!",320,200,g);
			} else if (l.size()==1) {
				PlayerInput pi = get_input(((TBase)(l.get(0))).getowner());
				Auxiliar.drawCentered("Winner is " + pi.m_playerName + "!",320,200,g);						
			} else {
				Auxiliar.drawCentered("Incomplete game...",320,200,g);
			} // if 
		}

		// Status:
		{
			float x = 8.0f;

			for(PlayerInput pi:m_pi_l) {
				g.setFont(m_font16);
				g.drawString(pi.m_playerID,x,24);

				if (pi==null) {
					g.drawString("No input!",x,48);
				} else {
					switch(pi.m_inputType) {
					case PlayerInput.INPUT_MOUSE: g.drawString("Mouse",x,48);
					break;
					case PlayerInput.INPUT_AI: g.drawString("AI",x,48);
					break;
					case PlayerInput.INPUT_EXTERNAL: g.drawString("D2",x,48);
					break;
					} // switch

				} // if 


				if (m_still_not_ready.contains(pi)) {
					g.setColor(new Color(1,0.5f,0.5f));
					g.drawString("- Press Mouse -",x,68);
				} else {
					g.setColor(new Color(0.5f,1,0.5f));
					g.drawString("Ready",x,68);
				} // if

				x+=160;
			} // while 
		}
	}

	public Towers getGame() {
		return m_game;
	}

	public void mouseClick(int x, int y) {
		for(PlayerInput pi:m_pi_l) {
			if (pi.m_inputType==PlayerInput.INPUT_MOUSE) {
				switch(m_state) {
				case STATE_INIT:
				case STATE_QUITTING:
					m_mouseActionQueue.add(new Action("Accept",pi.m_playerID,""));
					break;
				case STATE_GAME:
					if (x>=4 && x<84) {
						if (y>80 && y<112) m_button_selected = 0;
						if (y>120 && y<152) m_button_selected = 1;
						if (y>160 && y<192) m_button_selected = 2;
						if (y>200 && y<232) m_button_selected = 3;
					} else {
						if (x>m_map_center_x-(m_game.getMap().m_dx/2)*m_zoom &&
							x<m_map_center_x+(m_game.getMap().m_dx/2)*m_zoom &&
							y>m_map_center_y-(m_game.getMap().m_dy/2)*m_zoom &&
							y<m_map_center_y+(m_game.getMap().m_dy/2)*m_zoom) {
							int map_x = (int) ((x-(m_map_center_x-((m_game.getMap().m_dx/2)*m_zoom) ) )/m_zoom);
							int map_y = (int) ((y-(m_map_center_y-((m_game.getMap().m_dy/2)*m_zoom) ) )/m_zoom);
							int cell_x = map_x / TMap.TILE_WIDTH;
							int cell_y = map_y / TMap.TILE_HEIGHT;
							
							PhysicalEntity pe = m_game.getMap().getObjectAt(cell_x,cell_y);
							
							if (pe==null) {
								double d = m_game.getMap().getDistancetoClosestTowerOfPlayer(cell_x,cell_y,pi.m_playerID);
								
								if (d<=Towers.TOWER_RANGE) {
									String buildingName[]={"towers.mmpm.entities.TTower",
														   "towers.mmpm.entities.TWall",
														   "towers.mmpm.entities.TUpgradeGold",
														   "towers.mmpm.entities.TUpgradeUnits"};
									Action a = new Action("Build",pi.m_playerID,pi.m_playerID);
									a.addParameter("type", buildingName[m_button_selected]);
									float coor[] = new float[]{cell_x*TMap.TILE_WIDTH,
															   cell_y*TMap.TILE_HEIGHT,
															   0};
									a.addParameter("coor", ActionParameterType.COORDINATE.toString(coor));
									m_mouseActionQueue.add(a);
								} else {
									System.out.println("Too far: " + d);
								}								
							} else {
								System.out.println("Cell occupied!");
							}
						}
					}
					break;
				}
			}
		}
	}
	
	////
	// D2 related method and attributes
	
	/// It could throw d2.core.ConfigurationException and java.io.IOException.
	private towers.ai.AI createD2IA(PlayerInput pi) throws Exception {

		// Make sure we have Domain created... 
		configureDomain();
		
		IMEExecutor meExec = MEExecutorFactory.BuildMEExecutor( 
					pi.ME, idomain
					//"d2.learn.BasicAI@@@file@@@./AIs/mmpmME.xml@@@gatech.mmpm.learningengine.ThreadedMEExecutor"
					);

		
		// Create Towers AI
		towers.ai.AI_D2 ai = new towers.ai.AI_D2(meExec,pi.m_playerID);
		
		return ai;
	}
	
	private void configureDomain() throws gatech.mmpm.ConfigurationException {
		if (idomain == null) {
			idomain = new towers.mmpm.TowersDomain();
		}
	}
	
	public String getWinner() throws ClassNotFoundException {
		if (m_state==STATE_QUITTING) {	
			List<PhysicalEntity> l = null;
	
			if (m_game!=null && m_game.getMap()!=null) {
				l = m_game.getMap().getObjects(TBase.class);
			} // if 
	
			if (l==null || l.size()==0) {
				return null;
			} else if (l.size()==1) {
				PlayerInput pi = get_input(((TBase)(l.get(0))).getowner());
				return pi.m_playerName;
			} else {
				return null;
			} // if 
		}
		
		return null;
	}
}
