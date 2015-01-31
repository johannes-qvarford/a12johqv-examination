package bc;

import gatech.mmpm.ConfigurationException;
import gatech.mmpm.GameState;
import gatech.mmpm.learningengine.IMEExecutor;
import gatech.mmpm.learningengine.MEExecutorFactory;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
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

import bc.ai.AI;
import bc.ai.AIDefense;
import bc.ai.AIFollower;
import bc.ai.AIMeek;
import bc.ai.AIRandom;
import bc.ai.AI_D2;
import bc.helpers.Auxiliar;
import bc.helpers.KeyInputHandler;
import bc.helpers.VirtualController;
import bc.objects.BCOPlayerTank;
import bc.objects.BCPhysicalEntity;

public class BattleCityApp extends Canvas {

    private static final long serialVersionUID = 1L;
    private BufferStrategy strategy;
    private boolean gameRunning = true;
    public static boolean s_save_trace = true;
    public static final int SCREEN_X = 640;
    public static final int SCREEN_Y = 480;
    public static final int REDRAWING_PERIOD = 20;
    public static final int m_trace_interval = 50;
    public static final int MAX_FRAME_SKIP = 10;
    public static final int STATE_INIT = 0;
    public static final int STATE_GAME = 1;
    public static final int STATE_QUITTING = 2;
    public static final int STATE_ERROR = 3;
    public static List<String> s_error_messages = new LinkedList<String>();
    int m_state = STATE_INIT, m_previous_state = STATE_INIT;
    int m_state_cycle = 0;
    JFrame container = null;
    KeyInputHandler m_keyboardState = new KeyInputHandler();
    Font m_font32, m_font16;
    // Game info:
    BattleCity m_game = null;
    String m_mapName = null;
    List<PlayerInput> m_pi_l = new LinkedList<PlayerInput>();
    List<VirtualController> m_still_not_ready = new LinkedList<VirtualController>();
    List<VirtualController> m_vc_l = new LinkedList<VirtualController>();
    List<AI> m_ai_l = new LinkedList<AI>();
    gatech.mmpm.IDomain idomain = null;
    gatech.mmpm.tracer.ITracer tracer = null;
    String _userName;

    public void setUserName(String userName) {
        _userName = userName;
    }

    public String gameLoop(int maxCycles) throws ClassNotFoundException, IOException {
        long time = System.currentTimeMillis();
        long actTime;

        boolean need_to_redraw = true;

        if (tracer != null) {
            tracer.beginTrace();
            java.util.Properties prop = new java.util.Properties();
            prop.setProperty("domain", idomain.getName());
            prop.setProperty("map", m_mapName);
            if (_userName != null) {
                prop.setProperty("user", _userName);
            }
            tracer.putMetadata(prop);
        }

        while (gameRunning) {
            actTime = System.currentTimeMillis();
            if (actTime - time >= REDRAWING_PERIOD) {
                int max_frame_step = MAX_FRAME_SKIP;
                do {
                    time += REDRAWING_PERIOD;
                    if ((actTime - time) > 10 * REDRAWING_PERIOD) {
                        time = actTime;
                    }
                    if (!cycle(maxCycles)) {
                        gameRunning = false;
                    }
                    need_to_redraw = true;

                    actTime = System.currentTimeMillis();
                    max_frame_step--;
                } while (actTime - time >= REDRAWING_PERIOD && max_frame_step > 0);
            } /*
             * if
             */

            /*
             * Redraw
             */
            if (need_to_redraw) {
                need_to_redraw = false;

                draw();
            } /*
             * if
             */

            try {
                Thread.sleep(1);
            } catch (Exception e) {
            }
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

        for (AI ai : m_ai_l) {
            ai.gameEnd();
        }
        return getWinner();
    }

    public BattleCityApp(String map, int traceInterval, List<PlayerInput> players, gatech.mmpm.tracer.ITracer a_tracer) throws Exception {

        idomain = new bc.mmpm.BCDomain();

        tracer = a_tracer;

        container = new JFrame("Battle City");

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
                gameRunning = false;
            }
        });

        addKeyListener(m_keyboardState);
        requestFocus();

        createBufferStrategy(2);
        strategy = getBufferStrategy();

        m_font32 = new Font("Arial", Font.PLAIN, 32);
        m_font16 = new Font("Arial", Font.PLAIN, 16);

        // Load the configuration file:
        try {

            m_mapName = map;
            m_pi_l = players;
            for (PlayerInput pi : players) {

                if (pi.m_inputType == PlayerInput.INPUT_AI) {
                    AI ai;
                    if (pi.AIType.equals("ME")) {
                        if (!pi.ME.isEmpty()) {
                            ai = createD2IA(pi);
                        } else {
                            throw new gatech.mmpm.ConfigurationException("The ME to load must be specified.");
                        }
                    } else {
                        Class c = Class.forName(pi.AIType);
                        if (c!=null) {
                            ai = (AI) c.getConstructor(String.class).newInstance(pi.m_playerID);
                        } else {
                            throw new gatech.mmpm.ConfigurationException(pi.m_playerID + " AIType must be \"ME\" or the name of a class.");                            
                        }
                    }

                    m_ai_l.add(ai);
                }

                VirtualController vc = new VirtualController();
                vc.m_id = pi.m_playerID;
                vc.m_name = pi.m_playerName;
                vc.reset();
                m_vc_l.add(vc);

            }
            /*
             * for(Object o:root.getChild("players").getChildren()) { Element e
             * = (Element)o; PlayerInput pi = new PlayerInput(); pi.m_playerID =
             * e.getAttributeValue("id"); pi.m_playerName =
             * e.getAttributeValue("name"); if (pi.m_playerName==null)
             * pi.m_playerName = pi.m_playerID; pi.m_inputType =
             * PlayerInput.INPUT_NONE;
             *
             * if (e.getAttributeValue("type").equals("keyboard")) {
             * pi.m_inputType = PlayerInput.INPUT_KEYBOARD;
             *
             * pi.m_keyboardCfg[DIRECTION_UP] =
             * Integer.parseInt(e.getChild("keys").getAttributeValue("up"));
             * pi.m_keyboardCfg[DIRECTION_DOWN] =
             * Integer.parseInt(e.getChild("keys").getAttributeValue("down"));
             * pi.m_keyboardCfg[DIRECTION_LEFT] =
             * Integer.parseInt(e.getChild("keys").getAttributeValue("left"));
             * pi.m_keyboardCfg[DIRECTION_RIGHT] =
             * Integer.parseInt(e.getChild("keys").getAttributeValue("right"));
             * pi.m_keyboardCfg[4] =
             * Integer.parseInt(e.getChild("keys").getAttributeValue("fire")); }
             *
             * if (e.getAttributeValue("type").equals("ai-random")) { AI ai =
             * new AIRandom(pi.m_playerID); m_ai_l.add(ai);
             *
             * pi.m_inputType = PlayerInput.INPUT_AI; }
             *
             * if (e.getAttributeValue("type").equals("ai-random-meek")) { AI ai
             * = new AIMeek(pi.m_playerID); m_ai_l.add(ai);
             *
             * pi.m_inputType = PlayerInput.INPUT_AI; }
             *
             * if (e.getAttributeValue("type").equals("ai-defense")) { AI ai =
             * new AIDefense(pi.m_playerID); m_ai_l.add(ai);
             *
             * pi.m_inputType = PlayerInput.INPUT_AI; }
             *
             * if (e.getAttributeValue("type").equals("ai-follower")) { AIRandom
             * ai = new AIFollower(pi.m_playerID); m_ai_l.add(ai);
             *
             * pi.m_inputType = PlayerInput.INPUT_AI; }
             *
             * if (e.getAttributeValue("type").equals("d2")) { pi.m_inputType =
             * PlayerInput.INPUT_AI; m_ai_l.add(createD2IA(e)); }
             *
             * VirtualController vc = new VirtualController(); vc.m_id =
             * e.getAttributeValue("id"); vc.m_name =
             * e.getAttributeValue("name"); if (vc.m_name == null) vc.m_name =
             * vc.m_id; vc.reset(); m_vc_l.add(vc);
			}
             */

            // Create the Game
            {
                Document doc2;
                SAXBuilder builder = new SAXBuilder();
                try {
                    // When BattleCity is a JAR
                    InputStream f1 = this.getClass().getResourceAsStream("/" + m_mapName);
                    doc2 = builder.build(f1);
                } catch (Exception e) {
                    // When it's not:
                    doc2 = builder.build(m_mapName);
                }
                Element map_xml = doc2.getRootElement();

                if (map_xml != null) {
                    m_game = new BattleCity(map_xml);
                } else {
                    throw new Exception("BattleCityApp: Cannot open map file " + m_mapName);
                } // if 
            }
        } catch (Exception e) {
            e.printStackTrace();
            s_error_messages.add(e.getLocalizedMessage());
            s_error_messages.add("Error Loading the configuration file!");
            m_state = STATE_ERROR;
        }
    }

    boolean cycle(int maxCycles) throws ClassNotFoundException, IOException {
        int old_state = m_state;

        switch (m_state) {
            case STATE_INIT:
                m_state = init_cycle();
                break;
            case STATE_GAME:
                if (maxCycles > 0 && m_game.m_cycle >= maxCycles) {
                    m_state = STATE_QUITTING;
                } else {
                    m_state = game_cycle();
                }
                break;
            case STATE_QUITTING:
                m_state = quitting_cycle();
                break;
            case STATE_ERROR:
                m_state = error_cycle();
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
            case STATE_ERROR:
                error_draw(g);
                break;
        }

        g.dispose();
        strategy.show();

        return true;
    }

    PlayerInput get_input(String player) {
        for (PlayerInput pi : m_pi_l) {
            if (pi.m_playerID.equals(player)) {
                return pi;
            }
        } // while 

        return null;
    }

    AI get_AI(String player) {
        for (AI ai : m_ai_l) {
            if (ai.getPlayerId().equals(player)) {
                return ai;
            }
        } // while 

        return null;
    }

    void update_input() throws ClassNotFoundException, IOException {
        VirtualController selected_vc;

        for (VirtualController vc : m_vc_l) {
            vc.cycle();
        }

        for (PlayerInput pi : m_pi_l) {
            selected_vc = null;

            for (VirtualController vc : m_vc_l) {
                if (vc.m_id.equals(pi.m_playerID)) {
                    selected_vc = vc;
                }
            }
            switch (pi.m_inputType) {
                case PlayerInput.INPUT_KEYBOARD:
                    if (selected_vc != null) {
                        selected_vc.m_joystick[PlayerInput.DIRECTION_UP] = (m_keyboardState.m_keyboardStatus[pi.m_keyboardCfg[PlayerInput.DIRECTION_UP]] ? true : false);
                        selected_vc.m_joystick[PlayerInput.DIRECTION_RIGHT] = (m_keyboardState.m_keyboardStatus[pi.m_keyboardCfg[PlayerInput.DIRECTION_RIGHT]] ? true : false);
                        selected_vc.m_joystick[PlayerInput.DIRECTION_DOWN] = (m_keyboardState.m_keyboardStatus[pi.m_keyboardCfg[PlayerInput.DIRECTION_DOWN]] ? true : false);
                        selected_vc.m_joystick[PlayerInput.DIRECTION_LEFT] = (m_keyboardState.m_keyboardStatus[pi.m_keyboardCfg[PlayerInput.DIRECTION_LEFT]] ? true : false);
                        selected_vc.m_button[0] = (m_keyboardState.m_keyboardStatus[pi.m_keyboardCfg[PlayerInput.FIRE]] ? true : false);
                    } // if 
                    break;
                case PlayerInput.INPUT_AI: {
                    AI ai = get_AI(pi.m_playerID);
                    if (ai != null) {
                        ai.cycle(m_state, m_game, selected_vc);
                    }
                }
                break;
            } // switch:
        } // while 

    } /*
     * BattleCityApp::update_input
     */


    int error_cycle() throws ClassNotFoundException, IOException {
        return STATE_ERROR;
    }

    void error_draw(Graphics2D g) throws ClassNotFoundException {
        int y;
        g.setColor(Color.WHITE);
        g.setFont(m_font32);
        Auxiliar.drawCentered("ERROR!", 320, 150, g);
        g.setFont(m_font16);
        y = 190;
        for (String s : s_error_messages) {
            Auxiliar.drawCentered(s, 320, y, g);
            y += 24;
        }
    }

    int init_cycle() throws ClassNotFoundException, IOException {
        if (m_state_cycle == 0) {
            for (VirtualController vc : m_vc_l) {
                m_still_not_ready.add(vc);
            }
        } // if 

        update_input();

        for (VirtualController vc : m_vc_l) {
            if (vc.m_button[0]) {
                m_still_not_ready.remove(vc);
            }
        } // while

        if (m_still_not_ready.isEmpty()) {
            // Game start!
            for (bc.ai.AI ai : this.m_ai_l) {
                ai.gameStarts();
            }
            return STATE_GAME;
        }

        return STATE_INIT;
    }

    void init_draw(Graphics2D g) throws ClassNotFoundException {
        g.setColor(Color.WHITE);
        g.setFont(m_font32);
        Auxiliar.drawCentered("Multi-Player Battle City", 320, 150, g);
        g.setFont(m_font16);
        Auxiliar.drawCentered("Santi Onta��n Villar (2008)", 320, 190, g);
        Auxiliar.drawCentered("Cognitive Computing Lab (CCL)", 320, 210, g);
        Auxiliar.drawCentered("Georgia Tech", 320, 230, g);
        Auxiliar.drawCentered("For any comment/question, contact: santi@cc.gatech.edu", 320, 270, g);

        // Status:
        {
            List<BCPhysicalEntity> l = null;
            BCPhysicalEntity o2;
            float x = 8.0f;

            if (m_game != null && m_game.getMap() != null) {
                l = m_game.getMap().getObjects("BCOPlayerTank");
            } // if

            for (VirtualController vc : m_vc_l) {
                o2 = null;
                if (l != null) {
                    for (BCPhysicalEntity o : l) {
                        if (((BCOPlayerTank) o).getowner().equals(vc.m_id)) {
                            o2 = o;
                        }
                    } // while 
                } // if 

                if (o2 != null) {

                    BCOPlayerTank tank = (BCOPlayerTank) o2;
                    PlayerInput pi = get_input(vc.m_id);

                    g.setFont(m_font16);
                    g.setColor(new Color(tank.getr(), tank.getg(), tank.getb()));
                    g.drawString(vc.m_name, x, 24);

                    if (pi == null) {
                        g.drawString("No input!", x, 48);
                    } else {
                        switch (pi.m_inputType) {
                            case PlayerInput.INPUT_KEYBOARD:
                                g.drawString("Keyboard", x, 48);
                                break;
                            case PlayerInput.INPUT_AI:
                                g.drawString("AI", x, 48);
                                break;
                        } // switch

                    } // if 


                    if (m_still_not_ready.contains(vc)) {
                        g.setColor(new Color(1, 0.5f, 0.5f));
                        g.drawString("- Press Fire -", x, 68);
                    } else {
                        g.setColor(new Color(0.5f, 1, 0.5f));
                        g.drawString("Ready", x, 68);
                    } // if

                } else {
                    g.setFont(m_font16);
                    g.drawString(vc.m_name, x, 24);
                    m_still_not_ready.remove(vc);
                    g.drawString("Dead", x, 68);
                } // if 

                x += 160;
            } // while 
        }
    }

    int game_cycle() throws ClassNotFoundException, IOException {
        List<Action> actions = new LinkedList<Action>();
        boolean advance_cycle = false;

        advance_cycle = true;
        if (advance_cycle && m_game != null) {

            update_input();

            int cycle = m_game.getCycle();
            GameState gs = bc.mmpm.Game2D2Converter.toGameState(m_game, idomain);

            if (!m_game.cycle(m_vc_l, actions)) {
                // Save the last state:
                if (tracer != null) {
                    tracer.beginGameCycle(m_game.getCycle());
                    tracer.putGameState(bc.mmpm.Game2D2Converter.toGameState(m_game, idomain));
                    tracer.endGameCycle();
                }
                return STATE_QUITTING;
            }

            // Save trace entry:
            if (tracer != null) {
                if (m_game != null && (cycle % m_trace_interval) == 0 || !actions.isEmpty()) {
                    tracer.beginGameCycle(cycle);
                    tracer.putGameState(gs);
                    for (Action a : actions) {
                        tracer.putAction(bc.mmpm.Game2D2Converter.toD2Action(a));
                    }
                    tracer.endGameCycle();
                }
            }
        } // if 

        if (m_game == null) {
            return STATE_QUITTING;
        }

        return STATE_GAME;
    }

    void game_draw(Graphics2D g) throws ClassNotFoundException, IOException {
        if (m_game == null) {
            return;
        }

        AffineTransform at = g.getTransform();
        if (m_game.getMap() != null) {
            float dx = (m_game.getMap().m_dx);
            float dy = (m_game.getMap().m_dy);

            float zoom = 1.0f;

            if (dx > 640.0f) {
                float z = 640.0f / dx;
                if (zoom > z) {
                    zoom = z;
                }
            } // if 
            if (dy > 480.0f) {
                float z = 400.0f / dy;
                if (zoom > z) {
                    zoom = z;
                }
            } // if 

            // center:
            dx *= zoom;
            dy *= zoom;
            g.translate(320 - (dx / 2.0f), 280 - (dy / 2.0f));
            g.scale(zoom, zoom);
        } // if
        m_game.draw(g);
        g.setTransform(at);

        // Status:
        {
            List<BCPhysicalEntity> l = null;
            BCPhysicalEntity o2;
            int x = 8;

            if (m_game != null && m_game.getMap() != null) {
                l = m_game.getMap().getObjects("BCOPlayerTank");
            } // if

            for (PlayerInput pi : m_pi_l) {
                g.setFont(m_font16);
                g.setColor(Color.WHITE);
                g.drawString(pi.m_playerName, x, 24);

                o2 = null;
                if (l != null) {
                    for (BCPhysicalEntity o : l) {
                        if (((BCOPlayerTank) o).getowner().equals(pi.m_playerID)) {
                            o2 = o;
                        }
                    } // while 
                } // if

                if (o2 != null) {
                    BCOPlayerTank tank = (BCOPlayerTank) o2;
                    Image i = tank.get_LastTileUsed();
                    if (i != null) {
                        if (i != null) {
                            g.drawImage(i, x, 32, null);
                        }
                    }
                } else {
                    g.drawString("Dead", x, 48);
                } // if 

                x += 160;
            } // while 

            g.setColor(Color.WHITE);
            g.drawString("" + m_game.getCycle(), 1, 460);
        }

    }

    int quitting_cycle() throws ClassNotFoundException, IOException {
        if (m_state_cycle == 0) {
            m_still_not_ready.addAll(m_vc_l);
        } // if 

        update_input();

        for (VirtualController vc : m_vc_l) {
            if (vc.m_button[0]) {
                m_still_not_ready.remove(vc);
            }
        } // while

        if (m_still_not_ready.isEmpty()) {
            gameRunning = false;
        }
        return STATE_QUITTING;
    }

    void quitting_draw(Graphics2D g) throws ClassNotFoundException {
        g.setColor(Color.WHITE);
        g.setFont(m_font32);

        Auxiliar.drawCentered("Game Over", 320, 150, g);

        // Result:
        {
            List<BCPhysicalEntity> l = null;

            if (m_game != null && m_game.getMap() != null) {
                l = m_game.getMap().getObjects("BCOPlayerTank");
            } // if 

            if (l == null || l.size() == 0) {
                Auxiliar.drawCentered("Draw!", 320, 200, g);
            } else if (l.size() == 1) {
                for (VirtualController vc : m_vc_l) {
                    PlayerInput pi = get_input(vc.m_id);
                    if (((BCOPlayerTank) (l.get(0))).getowner().equals(pi.m_playerID)) {
                        Auxiliar.drawCentered("Winner is " + pi.m_playerName + "!", 320, 200, g);
                    }
                }

            } else {
                Auxiliar.drawCentered("Incomplete game...", 320, 200, g);
            } // if 
        }

        // Status:
        {
            float x = 8.0f;

            for (VirtualController vc : m_vc_l) {
                PlayerInput pi = get_input(vc.m_id);

                g.setFont(m_font16);
                g.drawString(vc.m_name, x, 24);

                if (pi == null) {
                    g.drawString("No input!", x, 48);
                } else {
                    switch (pi.m_inputType) {
                        case PlayerInput.INPUT_KEYBOARD:
                            g.drawString("Keyboard", x, 48);
                            break;
                        case PlayerInput.INPUT_AI:
                            g.drawString("AI", x, 48);
                            break;
                    } // switch

                } // if 


                if (m_still_not_ready.contains(vc)) {
                    g.setColor(new Color(1, 0.5f, 0.5f));
                    g.drawString("- Press Fire -", x, 68);
                } else {
                    g.setColor(new Color(0.5f, 1, 0.5f));
                    g.drawString("Ready", x, 68);
                } // if

                x += 160;
            } // while 
        }
    }

    public BattleCity getGame() {
        return m_game;
    }

    /// It could throw d2.core.ConfigurationException and java.io.IOException.
    private AI createD2IA(PlayerInput pi) throws Exception {

        // Should we configure D2 before loading IA?
        if (idomain == null) {
            idomain = new bc.mmpm.BCDomain();
        }

        IMEExecutor meExec = MEExecutorFactory.BuildMEExecutor(
                pi.ME, idomain //"d2.learn.BasicAI@@@file@@@./AIs/mmpmME.xml@@@gatech.mmpm.learningengine.ThreadedMEExecutor"
                );


        // Create Towers AI
        AI_D2 ai = new AI_D2(meExec, pi.m_playerID, idomain);

        return ai;
    }

    public String getWinner() throws ClassNotFoundException {
        if (m_state == STATE_QUITTING) {
            List<BCPhysicalEntity> l = null;

            if (m_game != null && m_game.getMap() != null) {
                l = m_game.getMap().getObjects("BCOPlayerTank");
            } // if 

            if (l == null || l.size() == 0) {
                return null;
            } else if (l.size() == 1) {
                for (VirtualController vc : m_vc_l) {
                    PlayerInput pi = get_input(vc.m_id);
                    if (((BCOPlayerTank) (l.get(0))).getowner().equals(pi.m_playerID)) {
                        return pi.m_playerName;
                    }
                }
            } else {
                return null;
            } // if 

        }

        return null;
    }

    public void clear() {
        strategy = null;
        s_error_messages = null;
        container = null;
        m_font32 = null;
        m_font16 = null;
        m_game = null;
        m_mapName = null;
        m_pi_l = null;
        m_still_not_ready = null;
        m_vc_l = null;
        m_ai_l = null;
        idomain = null;
        tracer = null;
        _userName = null;
        this.setVisible(false);
        this.setEnabled(false);
    }
}
