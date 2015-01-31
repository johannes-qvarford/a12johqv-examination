/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.util.planvisualizer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import d2.plans.ActionPlan;
import d2.plans.FailureTransition;
import d2.plans.GoalPlan;
import d2.plans.PetriNetElement;
import d2.plans.PetriNetPlan;
import d2.plans.Plan;
import d2.plans.PlanState;
import d2.plans.PreTransition;
import d2.plans.State;
import d2.plans.SuccessTransition;
import d2.plans.Transition;
import gatech.mmpm.util.Pair;

/*
 * TO-DO:
 *  ok - add basic action plans to visualization:
 *  	ok - white boxes with the action name
 * 	ok - draw plans with which states and transitions are associated:
 *  ok - compute proper and generic distance among elements
 *  ok - add subgoal plans to visualization 
 * 	ok - hierarchy:
 * 		ok - when you visualize a plan, it's inside a box that represents the plan: PlanFrame
 * 		ok - define the PlanFrame class
 * 		ok - add the frame to the m_elements list, the individual plan elements go into the frame
 * 		ok - apply positioning rules just among objects in the same level:
 * 		ok - generalize rule: no elements in the same position for every shape
 * 		ok - new rule: bring things that are outside of the screen inside
 *  ok - resize window
 * 	OK - draw intermitent lines from the states/transitions to the plans associated with them
 *  - make all the effects to add simultaneously
 * 	- expand / retract parts of the plan: expand / retract button in the PlanFrames
 *  - heuristic initial positions
 */
public class PlanVisualizer extends JFrame {

    protected boolean m_update = true;
    protected boolean m_autostretch = false;
    protected static final long serialVersionUID = 4182862635710825999L;

    class Link {

        public Object m_a, m_b;
    }

    class WeakLink extends Link {
    }
    Object m_head = null;
    Plan m_plan = null;
    HashMap<Object, Location> m_all_elements = null;
    HashMap<Object, Location> m_elements = null;
    List<LocationLink> m_links = null;
    Random rand = null;
    Graphics2D m_graph2D = null;
    BufferStrategy m_bufferStrategy = null;
    double m_randomize = 0.0f;
    // viewport control:
    double m_max_zoom = 2.0, m_min_zoom = 0.015625;
    boolean m_vp_autozoom = true;
    double m_vp_viewed_zoom = 1.0;	// This is the zoom for drawing ( it converges slowly to "m_vp_zoom" for smooth zoom effects.
    double m_vp_zoom = 1.0;	// This is the goal zoom
    double m_vp_x = (getWidth() / 2);
    double m_vp_y = (getHeight() / 2);
    double m_mouse_x = 0, m_mouse_y = 0;

    public int toScreenX(double x) {
        return (int) (((x - m_vp_x) * m_vp_viewed_zoom) + (getWidth() / 2));
    }

    public int toScreenY(double y) {
        return (int) (((y - m_vp_y) * m_vp_viewed_zoom) + (getHeight() / 2));
    }

    public int toScreen(double v) {
        return (int) (v * m_vp_viewed_zoom);
    }

    public double fromScreenX(double x) {
        return (int) (((x - (getWidth() / 2)) / m_vp_viewed_zoom) + m_vp_x);
    }

    public double fromScreenY(double y) {
        return (int) (((y - (getHeight() / 2)) / m_vp_viewed_zoom) + m_vp_y);
    }

    public double fromScreen(double v) {
        return (int) (v / m_vp_viewed_zoom);
    }

    public int toScreenX2(double x) {
        return (int) (((x) * m_vp_viewed_zoom) + (getWidth() / 2));
    }

    public int toScreenY2(double y) {
        return (int) (((y) * m_vp_viewed_zoom) + (getHeight() / 2));
    }

    public double fromScreenX2(double x) {
        return (int) (((x - ((getWidth() / 2))) / m_vp_viewed_zoom));
    }

    public double fromScreenY2(double y) {
        return (int) (((y - ((getHeight() / 2))) / m_vp_viewed_zoom));
    }

    boolean updateP() {
        return m_update;
    }

    public void paint(Graphics g) {
        super.paint(g);

        g.setClip(0, 67, getWidth(), getHeight() - 67);
//		m_graph2D.drawString("Center:" + m_vp_x + "," + m_vp_y, 10, 100);
//		m_graph2D.drawString("Zoom:" + m_vp_zoom, 10, 120);

        if (m_links != null) {
            for (LocationLink p : m_links) {
                Location e1 = m_all_elements.get(p.m_a);
                Location e2 = m_all_elements.get(p.m_b);
                if (p instanceof WeakLocationLink) {
                    e1.drawWeakLink(e2, g);
                } else {
                    e1.drawLink(e2, g);
                } // if

            }
        }

        if (m_elements != null) {
            List<Location> ll = new LinkedList<Location>();
            ll.addAll(m_elements.values());
            for (Location l : ll) {
                l.draw(g);
            }
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

    public PlanVisualizer(String name) {
        super(name);
    }
}
