/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package d2.util.statevisualizer;

import gatech.mmpm.Action;
import gatech.mmpm.ActionParameter;
import gatech.mmpm.ActionParameterType;
import gatech.mmpm.Entity;
import gatech.mmpm.Entry;
import gatech.mmpm.GameState;
import gatech.mmpm.PhysicalEntity;
import gatech.mmpm.Trace;
import gatech.mmpm.TwoDMap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TraceVisualizer extends JFrame {

    protected Trace m_trace = null;
    protected int m_currentState = 0;
    protected Random rand = null;
    protected Graphics2D m_graph2D = null;
    protected BufferStrategy m_bufferStrategy = null;
    // viewport control:
    protected double m_center_x = 0, m_center_y = 0;
    protected double m_max_zoom = 64.0, m_min_zoom = 0.015625;
    protected boolean m_vp_autozoom = true;
    protected double m_vp_viewed_zoom = 1.0;	// This is the zoom for drawing ( it converges slowly to "m_vp_zoom" for smooth zoom effects.
    protected double m_vp_zoom = 1.0;	// This is the goal zoom
    protected HashMap<Class<?>, Color> m_ClassColors = new HashMap<Class<?>, Color>();

    public int toScreenX(double x) {
        return (int) ((((x - m_center_x)) * m_vp_viewed_zoom) + (getWidth()) / 2);
    }

    public int toScreenY(double y) {
        return (int) ((((y - m_center_y)) * m_vp_viewed_zoom) + (getHeight() / 2));
    }

    public int toScreen(double v) {
        return (int) (v * m_vp_viewed_zoom);
    }

    public double fromScreenX(double x) {
        return (int) (((x - (getWidth() / 2)) / m_vp_viewed_zoom) + (getWidth() / 2));
    }

    public double fromScreenY(double y) {
        return (int) (((y - (getHeight() / 2)) / m_vp_viewed_zoom) + (getHeight() / 2));
    }

    public double fromScreen(double v) {
        return (int) (v / m_vp_viewed_zoom);
    }

    
    public TraceVisualizer(Trace t, int a_width, int a_height) {
        super("D2 Game Trace Visualization");
        m_trace = t;
        m_currentState = 0;
        rand = new Random();

        {
            JPanel panel = new JPanel();
            getContentPane().add(panel);
            JButton button1 = new JButton("Zoom in");
            button1.addActionListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            if (m_vp_zoom < m_max_zoom) {
                                m_vp_zoom *= 1.25;
                            }
                            m_vp_autozoom = false;
                        }
                    });
            JButton button2 = new JButton("Zoom out");
            button2.addActionListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            if (m_vp_zoom > m_min_zoom) {
                                m_vp_zoom /= 1.25;
                            }
                            m_vp_autozoom = false;
                        }
                    });
            JButton button3 = new JButton("Auto zoom");
            button3.addActionListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            m_vp_autozoom = true;
                        }
                    });
            JButton button4 = new JButton("-1");
            button4.addActionListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            if (m_currentState > 0) {
                                m_currentState--;
                            }
                        }
                    });
            JButton button5 = new JButton("+1");
            button5.addActionListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            if (m_currentState < m_trace.getEntries().size() - 1) {
                                m_currentState++;
                            }
                        }
                    });
            JButton button6 = new JButton("+100");
            button6.addActionListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            for(int i = 0;i<100;i++) {
                                if (m_currentState < m_trace.getEntries().size() - 1) {
                                    m_currentState++;
                                }
                            }
                        }
                    });
            panel.add(button1);
            panel.add(button2);
            panel.add(button3);
            panel.add(button4);
            panel.add(button5);
            panel.add(button6);
        }

        setSize(a_width, a_height);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        createBufferStrategy(2);
        m_bufferStrategy = getBufferStrategy();
        m_graph2D = (Graphics2D) m_bufferStrategy.getDrawGraphics();

        // Populate some Class Colors:
        {
            // BattleCity:
//			m_ClassColors.put(BCOWall.class, Color.black);
//			m_ClassColors.put(BCOWater.class, Color.blue);
//			m_ClassColors.put(BCOBlock.class, Color.red);
            // Wargus
//			m_ClassColors.put(WOWall.class, Color.white);
//			m_ClassColors.put(WOWater.class, Color.blue);
//			m_ClassColors.put(WOGrass.class, new Color(128,255,128));
//			m_ClassColors.put(WOTree.class, new Color(0,128,0));
//			m_ClassColors.put(WOCoast.class, Color.yellow);
        }

        class StateVisualizerUpdater extends Thread {

            TraceVisualizer v;

            public StateVisualizerUpdater(TraceVisualizer a_v) {
                v = a_v;
            }

            public void run() {
                Entry entry = null;
                GameState state = null;
                do {
                    try {
                        {
                            float min_x = 0, max_x = 0;
                            float min_y = 0, max_y = 0;

                            min_x = 0;
                            min_y = 0;
                            max_x = 1;
                            max_y = 1;

                            entry = m_trace.getEntries().get(m_currentState);
                            state = entry.getGameState();

                            if (state.getMap() != null
                                    && state.getMap() instanceof TwoDMap) {
                                TwoDMap m = (TwoDMap) state.getMap();
                                max_x = m.getSizeInDimension(0) * m.getCellSizeInDimension(0);
                                max_y = m.getSizeInDimension(1) * m.getCellSizeInDimension(1);
                            }

                            float border = (max_x - min_x) * 0.05f;
                            min_x -= border;
                            max_x += border;
                            min_y -= border;
                            max_y += border;
                            m_center_x = (min_x + max_x) / 2;
                            m_center_y = (min_y + max_y) / 2;

                            if (m_vp_autozoom && max_x - min_x != 0 && max_y - min_y != 0) {
                                double required_zoom_x = ((double) getWidth()) / (max_x - min_x);
                                double required_zoom_y = ((double) getHeight() - 67) / (max_y - min_y);
                                double required_zoom = Math.min(required_zoom_x, required_zoom_y);
                                m_vp_zoom = required_zoom;
                            }

                            if (m_vp_zoom > m_max_zoom) {
                                m_vp_zoom = m_max_zoom;
                            }
                            if (m_vp_zoom < m_min_zoom) {
                                m_vp_zoom = m_min_zoom;
                            }
                            if (m_vp_viewed_zoom != m_vp_zoom) {
                                m_vp_viewed_zoom = (3 * m_vp_viewed_zoom + m_vp_zoom) / 4;
                            }
                        }
                        v.update(m_graph2D);
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (v.isShowing());

            }
        }

        StateVisualizerUpdater updater = new StateVisualizerUpdater(this);
        new Thread(updater).start();
    }

    protected Color getEntityColor(PhysicalEntity pe) {
        Class<?> cl = pe.getClass();
        Color co = m_ClassColors.get(cl);

        if (co == null) {
            co = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
            m_ClassColors.put(cl, co);
        }

        return co;
    }

    public void paint(Graphics g) {
        Entry entry = null;
        GameState state = null;

        try {
            super.paint(g);

            g.setClip(0, 67, getWidth(), getHeight() - 67);

            entry = m_trace.getEntries().get(m_currentState);
            state = entry.getGameState();

            g.drawString("[" + entry.getTimeStamp() + "] " + (m_currentState + 1) + "/" + m_trace.getEntries().size(), 10, getHeight() - 16);

            // Draw the map:
            if (state.getMap() instanceof TwoDMap) {
                TwoDMap m = (TwoDMap) state.getMap();
                for (int y = 0; y < m.getSizeInDimension(1); y++) {
                    for (int x = 0; x < m.getSizeInDimension(0); x++) {
                        Entity e = m.getCellLocation(new int[]{x, y});
                        if (e != null && e instanceof PhysicalEntity) {
                            PhysicalEntity pe = (PhysicalEntity) e;
                            g.setColor(getEntityColor(pe));
                            int ex = toScreenX(pe.getx());
                            int ey = toScreenY(pe.gety());
                            g.fillRect(ex, ey, toScreen(pe.getwidth())+1, toScreen(pe.getlength())+1);
                        }
                    }
                }
            }

            // Draw the other entities:
            for (Entity e : state.getAllEntities()) {
                if (e != null && e instanceof PhysicalEntity) {
                    PhysicalEntity pe = (PhysicalEntity) e;
                    g.setColor(getEntityColor(pe));
                    int ex = toScreenX(pe.getx());
                    int ey = toScreenY(pe.gety());
                    g.fillRect(ex, ey, toScreen(pe.getwidth())+1, toScreen(pe.getlength())+1);
                    g.setColor(Color.black);
                    g.drawString(pe.getentityID(), ex, ey);
                }
            }

            // Draw the actions:
            g.setColor(Color.black);
            int y = getHeight()-16;
            for(Action a:entry.getActions()) {
                g.drawString("[" + state.getEntity(a.getEntityID()).getClass().getSimpleName() + "] " + a.toString(), 100, y);
                y-=16;
                // if actions have a position draw a line from unit to position:
                PhysicalEntity e = (PhysicalEntity)state.getEntity(a.getEntityID());
                for(ActionParameter ap:a.listOfParameters()) {
                    if (ap.getType()==ActionParameterType.COORDINATE) {
                        float coord[] = (float[])a.parameterValue(ap.m_name);
                        g.drawLine(toScreenX(e.getx()), toScreenY(e.gety()), toScreenX(coord[0]), toScreenY(coord[1]));
                    }
                }
                // circle the entities related to the action:
                {
                    float width = e.getwidth();
                    float length = e.getlength();
                    g.drawArc(toScreenX(e.getx()-1), toScreenY(e.gety()-1), toScreen(width+2), toScreen(length+2), 0, 360);                
                }
                for(ActionParameter ap:a.listOfParameters()) {
                    if (ap.getType() == ActionParameterType.ENTITY_ID) {
                        Object v = a.parameterValue(ap.m_name);
                        if (v instanceof PhysicalEntity) {
                            PhysicalEntity pe2 = (PhysicalEntity)v;
                            float width = pe2.getwidth();
                            float length = pe2.getlength();
                            g.drawArc(toScreenX(pe2.getx()-1), toScreenY(pe2.gety()-1), toScreen(width+2), toScreen(length+2), 0, 360);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        if (m_graph2D != null) {
            m_graph2D.dispose();
        }
        if (m_bufferStrategy != null) {
            try {
                m_bufferStrategy.show();
                m_graph2D = (Graphics2D) m_bufferStrategy.getDrawGraphics();
            } catch (Exception e) {
                // Ignore, this happens when you resize the window...
            }
        }
    }
}
