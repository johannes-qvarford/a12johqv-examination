/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package d2.util.planvisualizer;

import d2.plans.ActionPlan;
import d2.plans.GoalPlan;
import d2.plans.PetriNetElement;
import d2.plans.PetriNetPlan;
import d2.plans.Plan;
import d2.plans.PlanState;
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

/**
 *
 * @author santi
 */
public class SimplifiedPlanVisualizer extends PlanVisualizer {

    void createPlanElements(Plan plan, PlanFrame frame) {
        int x, y;

        if (plan == null) {
            return;
        }

        if (plan instanceof PetriNetPlan) {
            PlanFrame f = new PlanFrame();
            if (frame == null) {
                x = rand.nextInt(getWidth() / 2) + getWidth() / 4;
                y = rand.nextInt(getHeight() / 2) + getHeight() / 4;
                Location l = new Location(x, y, f, this);
                m_elements.put(plan, l);
                m_all_elements.put(plan, l);
//                System.out.println(x + " " + y + " " + f + " " + this);
            } else {
                x = (rand.nextInt(frame.m_width / 2) + frame.m_x + (frame.m_width / 4));
                y = (rand.nextInt(frame.m_height / 2) + frame.m_y + (frame.m_height / 4));
                Location l = new Location(x, y, f, this);
                frame.m_elements.put(plan, l);
                m_all_elements.put(plan, l);
//                System.out.println(x + " " + y + " " + f + " " + this);
            }

            PetriNetPlan pn = (PetriNetPlan) plan;
            // Create all the nodes, transitions, and links (with initial random coordinates):
            for (PetriNetElement e : pn.getPetriNetElements()) {

                if (e instanceof PlanState) {
//                    if (recurse > 0) {
//                        if (m_all_elements.get(e.getPlan()) == null) {
//                            createPlanElements(e.getPlan(), frame, recurse - 1);
//                        }
//                        f.m_links.add(new WeakLocationLink(e, e.getPlan()));
//                    }
                    if (frame == null) {
                        x = rand.nextInt(getWidth() / 2) + getWidth() / 4;
                        y = rand.nextInt(getHeight() / 2) + getHeight() / 4;
                        Location l = new Location(x, y, e, this);
                        f.m_elements.put(e, l);
                        m_all_elements.put(e, l);
//                        System.out.println(x + " " + y + " " + e + " " + this);
                    } else {
                        x = (rand.nextInt(frame.m_width / 2) + frame.m_x + (frame.m_width / 4));
                        y = (rand.nextInt(frame.m_height / 2) + frame.m_y + (frame.m_height / 4));
                        Location l = new Location(x, y, e, this);
                        f.m_elements.put(e, l);
                        m_all_elements.put(e, l);
//                        System.out.println(x + " " + y + " " + e + " " + this);
                    }
                }
            }


            // create all the links:
            for(Object o:f.m_elements.keySet()) {
                for(Object o2:f.m_elements.keySet()) {
                    if (o!=o2) {
                        PlanState ps1 = (PlanState)o;
                        PlanState ps2 = (PlanState)o2;

                        if (pn.planDirectlyPrecedes(ps1, ps2)) {
    						f.m_links.add(new LocationLink(ps1,ps2));
                        }
                    }
                }
            }

        }
        if (plan instanceof ActionPlan) {
            if (frame == null) {
                x = rand.nextInt(getWidth() / 2) + getWidth() / 4;
                y = rand.nextInt(getHeight() / 2) + getHeight() / 4;
                Location l = new Location(x, y, plan, this);
                m_elements.put(plan, l);
                m_all_elements.put(plan, l);
//                System.out.println(x + " " + y + " " + plan + " " + this);
            } else {
                x = rand.nextInt(frame.m_width / 2) + frame.m_x + (frame.m_width / 4);
                y = rand.nextInt(frame.m_height / 2) + frame.m_y + (frame.m_height / 4);
                Location l = new Location(x, y, plan, this);
                frame.m_elements.put(plan, l);
                m_all_elements.put(plan, l);
                //System.out.println(x + " " + y + " " + plan + " " + this);
            }
        }
        if (plan instanceof GoalPlan) {
            GoalPlan gp = (GoalPlan) plan;
            if (frame == null) {
                x = rand.nextInt(getWidth() / 2) + getWidth() / 4;
                y = rand.nextInt(getHeight() / 2) + getHeight() / 4;
                Location l = new Location(x, y, plan, this);
                m_elements.put(plan, l);
                m_all_elements.put(plan, l);
//                System.out.println(x + " " + y + " " + plan + " " + this);

                if (gp.getExpandedGoalPlan() != null) {
//                    if (recurse > 0) {
//                        createPlanElements(gp.getExpandedGoalPlan(), frame, recurse - 1);
//                        m_links.add(new WeakLocationLink(plan, gp.getExpandedGoalPlan()));
//                    }
                }
            } else {
                x = rand.nextInt(frame.m_width / 2) + frame.m_x + (frame.m_width / 4);
                y = rand.nextInt(frame.m_height / 2) + frame.m_y + (frame.m_height / 4);
                Location l = new Location(x, y, plan, this);
                frame.m_elements.put(plan, l);
                m_all_elements.put(plan, l);
//                System.out.println(x + " " + y + " " + plan + " " + this);

                if (gp.getExpandedGoalPlan() != null) {
//                    if (recurse > 0) {
//                        createPlanElements(gp.getExpandedGoalPlan(), frame, recurse - 1);
//                        frame.m_links.add(new WeakLocationLink(plan, gp.getExpandedGoalPlan()));
//                    }
                }
            }
        }
    }

    public void updatePositionsTransitionTypes(PlanFrame frame) {
        // Just try to make things that are linked close by, and nodes that are together separate:
        double desired_distance_min = 80;
        double desired_distance = 100;
        double desired_distance_max = 200;
        double toofar_distance = 500;	// if an object is further than this to the closest object, it will be pulled together
        double weight_repulsion = 0.5f;
        double weight_adyacency = 5.0f;
        double weight_toofar = 0.005f;
        double weight_hierarchy = 1.0f;

        double autostretch_amount = 1000;
        double autostretch_weight = 100.0f;

        double distance, dx, dy;
        double offset_x = 0, offset_y = 0;
        boolean desired_offset = false;
        double desired_offset_x = 0, desired_offset_y = 0;

        HashMap<Object, Location> elements = null;
        List<LocationLink> links = null;

        if (frame == null) {
            elements = m_elements;
            links = m_links;
        } else {
            elements = frame.m_elements;
            links = frame.m_links;
        }

        if (updateP()) {
            HashMap<Location, List<Push>> pushes = new HashMap<Location, List<Push>>();

            for (Location l1 : elements.values()) {
                pushes.put(l1, new LinkedList<Push>());
            }

            for (Location l1 : elements.values()) {
                for (Location l2 : elements.values()) {
                    if (l1 != l2) {
                        offset_x = 0;
                        offset_y = 0;
//						distance = l1.distance(l2);
                        distance = Math.sqrt((l1.m_x - l2.m_x) * (l1.m_x - l2.m_x) + (l1.m_y - l2.m_y) * (l1.m_y - l2.m_y));

                        if (distance < desired_distance_min) {
                            // Push them appart:

                            if (distance == 0) {
                                if (l1.m_x == l2.m_x
                                        && l1.m_y == l2.m_y) {

                                    pushes.get(l2).add(new Push("Repulsion(d==0, identical)", rand.nextInt(11) - 5, rand.nextInt(11) - 5, 1.0f));
                                } else {
                                    dx = (l2.m_x - l1.m_x);
                                    dy = (l2.m_y - l1.m_y);
                                    distance = Math.sqrt(dx * dx + dy * dy);
                                    dx /= distance;
                                    dy /= distance;

                                    offset_x = (desired_distance_min) * dx / 2;
                                    offset_y = (desired_distance_min) * dy / 2;

                                    //								offset_x = 5*dx/2;
                                    //								offset_y = 5*dy/2;

                                    offset_x += rand.nextDouble() * m_randomize;
                                    offset_y += rand.nextDouble() * m_randomize;

                                    pushes.get(l2).add(new Push("Repulsion(d==0)", offset_x, offset_y, weight_repulsion));
                                    pushes.get(l1).add(new Push("Repulsion(d==0)", -offset_x, -offset_y, weight_repulsion));
                                }
                            } else {
                                dx = (l2.m_x - l1.m_x);
                                dy = (l2.m_y - l1.m_y);
                                distance = Math.sqrt(dx * dx + dy * dy);
                                dx /= distance;
                                dy /= distance;

                                offset_x = (desired_distance_min - distance) * dx / 2;
                                offset_y = (desired_distance_min - distance) * dy / 2;
                                //							offset_x = 5*dx/2;
                                //							offset_y = 5*dy/2;

                                offset_x += rand.nextDouble() * m_randomize;
                                offset_y += rand.nextDouble() * m_randomize;

                                pushes.get(l2).add(new Push("Repulsion", offset_x, offset_y, weight_repulsion));
                                pushes.get(l1).add(new Push("Repulsion", -offset_x, -offset_y, weight_repulsion));
                            }
                        }
                    }
                }
            }

            for (LocationLink l : links) {
                Location l2 = m_all_elements.get(l.m_a);
                Location l1 = m_all_elements.get(l.m_b);
                if (l1 == null) {
                    System.err.println("No element for " + l.m_a);
                }
                if (l2 == null) {
                    System.err.println("No element for " + l.m_b);
                }
                
                offset_x = 0;
                offset_y = 0;
//						distance = l1.distance(l2);
                distance = Math.sqrt((l1.m_x - l2.m_x) * (l1.m_x - l2.m_x) + (l1.m_y - l2.m_y) * (l1.m_y - l2.m_y));

                if (distance > desired_distance_max) {
                    // Pull them together:
                    dx = (l2.m_x - l1.m_x);
                    dy = (l2.m_y - l1.m_y);
                    distance = Math.sqrt(dx * dx + dy * dy);
                    dx /= distance;
                    dy /= distance;

                    offset_x = 0.5*(desired_distance_max - distance) * dx / 2;
                    offset_y = 0.5*(desired_distance_max - distance) * dy / 2;

                    offset_x += rand.nextDouble() * m_randomize;
                    offset_y += rand.nextDouble() * m_randomize;

                    pushes.get(l2).add(new Push("Attraction", offset_x, offset_y, weight_repulsion));
                    pushes.get(l1).add(new Push("Attraction", -offset_x, -offset_y, weight_repulsion));
                }
            }

            // too far objects:
            {
                double min_distance = 0, d;
                Location closest = null;
                for (Location l1 : elements.values()) {
                    closest = null;
                    for (Location l2 : elements.values()) {
                        if (l1 != l2) {
                            d = l1.distance(l2);
                            if (closest == null || d < min_distance) {
                                min_distance = d;
                                closest = l2;
                            }
                            if (min_distance < toofar_distance) {
                                break;
                            }
                        }
                    }

                    if (closest != null && min_distance > toofar_distance) {
                        // pull objects close together:
                        dx = (closest.m_x - l1.m_x);
                        dy = (closest.m_y - l1.m_y);
                        dx /= min_distance;
                        dy /= min_distance;

                        offset_x = (toofar_distance - min_distance) * dx / 2;
                        offset_y = (toofar_distance - min_distance) * dy / 2;

                        offset_x += rand.nextDouble() * m_randomize;
                        offset_y += rand.nextDouble() * m_randomize;

                        pushes.get(closest).add(new Push("Too Far (closest)", offset_x, offset_y, weight_toofar));
                        pushes.get(l1).add(new Push("Too Far", -offset_x, -offset_y, weight_toofar));
                    }
                }
            }

            // Hierarchically do for all the frames
            for (Location l : elements.values()) {
                if (l.m_element instanceof PlanFrame) {
                    updatePositionsTransitionTypes((PlanFrame) (l.m_element));
                    offset_x = ((PlanFrame) (l.m_element)).m_x - l.m_x;
                    offset_y = ((PlanFrame) (l.m_element)).m_y - l.m_y;

                    pushes.get(l).add(new Push("Hierarchy", offset_x, offset_y, weight_hierarchy));

                }
            }

            // Process all the Pushes:
//			System.out.println(" ---------------------------- ");
            {
                Push largest = null;

                for (Location l : pushes.keySet()) {
                    double xinc = 0;
                    double yinc = 0;
                    double weight = 0;

//					System.out.println("E:");
                    for (Push p : pushes.get(l)) {
                        xinc += p.x * p.strength;
                        yinc += p.y * p.strength;
                        weight += p.strength;

//						System.out.println(p.ID + " -> " + xinc + "," + yinc + " * " + p.strength);

                        if (largest == null || (p.x + p.y) > (largest.x + largest.y)) {
                            largest = p;
                        }
                    }

                    if (m_autostretch && l.m_element == m_head) {
                        yinc -= autostretch_amount * autostretch_weight;
                        weight += autostretch_weight;
                    }

                    if (weight > 0) {
                        xinc /= weight;
                        yinc /= weight;

                        l.m_x += xinc;
                        l.m_y += yinc;
                    }
                }

//				if (largest!=null) System.out.println(largest.ID + " -> " + largest.x + "," + largest.y + " * " + largest.strength);
            }
        }

        // Recenter all the elements in the screen and process zoom:
        {
            boolean first = true;
            int min_x = 0, max_x = 0;
            int min_y = 0, max_y = 0;
            double center_x, center_y;

            for (Location l : elements.values()) {
                Rectangle r = l.boundingBox();
                //			System.out.println(l.m_element.getClass().getSimpleName() + " -> " + r);
                if (first) {
                    min_x = r.x;
                    min_y = r.y;
                    max_x = r.x + r.width;
                    max_y = r.y + r.height;
                    first = false;
                } else {
                    if (r.x < min_x) {
                        min_x = r.x;
                    }
                    if (r.x + r.width > max_x) {
                        max_x = r.x + r.width;
                    }
                    if (r.y < min_y) {
                        min_y = r.y;
                    }
                    if (r.y + r.height > max_y) {
                        max_y = r.y + r.height;
                    }
                }
            }

            //		System.out.println("(" + min_x + "," + min_y + ") -> (" + max_x + "," + max_y + ")");


            min_x -= 50;
            max_x += 50;
            min_y -= 50;
            max_y += 50;
            center_x = (min_x + max_x) / 2;
            center_y = (min_y + max_y) / 2;

            // upate frame size and position:
            if (frame != null) {
                int old_x = frame.m_x, old_y = frame.m_y;
                frame.m_x = (int) center_x;
                frame.m_y = (int) center_y;
                frame.move(old_x - frame.m_x, old_y - frame.m_y);
                frame.m_width = max_x - min_x;
                frame.m_height = max_y - min_y;
            } else {
                for (Location l : elements.values()) {
                    l.m_x -= (center_x - (getWidth() / 2));
                    l.m_y -= (center_y - (67 + ((getHeight() - 67) / 2)));
                    if (l.m_element instanceof PlanFrame) {
                        ((PlanFrame) l.m_element).move((int) -(center_x - (getWidth() / 2)),
                                (int) -(center_y - (67 + ((getHeight() - 67) / 2))));
                    }
                }

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

                m_randomize *= 0.975f;
            }
        }

    }

    
    public SimplifiedPlanVisualizer(String windowName,Plan plan, int a_width, int a_height, Object head) {
        super(windowName);
        m_plan = plan;
        m_head = head;
        m_all_elements = new HashMap<Object, Location>();
        m_elements = new HashMap<Object, Location>();
        m_links = new LinkedList<LocationLink>();
        rand = new Random();

        m_randomize = 5.0f;

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
            JButton button4 = new JButton("Pause/Unpause");
            button4.addActionListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            m_update = (m_update ? false : true);
                        }
                    });
            JButton button5 = new JButton("Auto Stretch on/off");
            button5.addActionListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            m_autostretch = (m_autostretch ? false : true);
//							System.out.println("autostretch: " + m_autostretch + " head it " + m_head);
                        }
                    });
            JButton button6 = new JButton("Random Push");
            button6.addActionListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            m_randomize = 100.0f;
                        }
                    });
            JButton button7 = new JButton("Cancel Randomness");
            button7.addActionListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            m_randomize = 0.0f;
                        }
                    });
            panel.add(button1);
            panel.add(button2);
            panel.add(button3);
            panel.add(button4);
            panel.add(button5);
            panel.add(button6);
            panel.add(button7);
        }

        setSize(a_width, a_height);
        {
			PVMouseDragger md = new PVMouseDragger(this);
			addMouseMotionListener(md);
			addMouseListener(md);
        }

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        createBufferStrategy(2);
        m_bufferStrategy = getBufferStrategy();
        m_graph2D = (Graphics2D) m_bufferStrategy.getDrawGraphics();

        m_vp_x = (getWidth() / 2);
        m_vp_y = (getHeight() / 2);

        class PlanVisualizerUpdater extends Thread {

            SimplifiedPlanVisualizer v;

            public PlanVisualizerUpdater(SimplifiedPlanVisualizer a_v) {
                v = a_v;
            }

            public void run() {
                do {
                    try {
                        v.updatePositionsTransitionTypes(null);
                        v.updatePositionsTransitionTypes(null);
                        v.updatePositionsTransitionTypes(null);
                        v.updatePositionsTransitionTypes(null);
                        v.update(m_graph2D);
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (v.isShowing());

            }
        }

        createPlanElements(m_plan, null);

        PlanVisualizerUpdater updater = new PlanVisualizerUpdater(this);
        new Thread(updater).start();



    }
}
