/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.util.planvisualizer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import d2.plans.ActionPlan;
import d2.plans.DummyState;
import d2.plans.FailureTransition;
import d2.plans.GoalPlan;
import d2.plans.Plan;
import d2.plans.PreTransition;
import d2.plans.State;
import d2.plans.SuccessTransition;
import d2.plans.Transition;
import java.util.LinkedList;
import java.util.List;

class Location {

    final int state_radius = 25;
    final int transition_width = 50;
    final int transition_height = 10;
    final int action_width = 50;
    final int action_height = 50;
    final int goal_width = 50;
    final int goal_height = 50;
    public int m_x, m_y;
    public Object m_element;
    public PlanVisualizer m_pv;

    public Location(int x, int y, Object element, PlanVisualizer pv) {
        m_x = x;
        m_y = y;
        m_element = element;
        m_pv = pv;
    }

    public boolean screen_point_inside(int x, int y) {
        return point_inside(m_pv.fromScreenX(x), m_pv.fromScreenY(y));
    }

    public boolean point_inside(double x, double y) {
        if (m_element instanceof State) {
            double sqd = (m_x - x) * (m_x - x) + (m_y - y) * (m_y - y);
            if (sqd < state_radius * state_radius) {
                return true;
            }
        }
        if (m_element instanceof Transition) {
            if (x > m_x - (transition_width / 2)
                && x < m_x + (transition_width / 2)
                && y > m_y - (transition_height / 2)
                && y < m_y + (transition_height / 2)) {
                return true;
            }
        }
        if (m_element instanceof ActionPlan) {
            if (x > m_x - (action_width / 2)
                && x < m_x + (action_width / 2)
                && y > m_y - (goal_height / 2)
                && y < m_y + (goal_height / 2)) {
                return true;
            }
        }
        if (m_element instanceof GoalPlan) {
            if (x > m_x - (goal_width / 2)
                && x < m_x + (goal_width / 2)
                && y > m_y - (goal_height / 2)
                && y < m_y + (goal_height / 2)) {
                return true;
            }
        }
        if (m_element instanceof PlanFrame) {
            PlanFrame pf = (PlanFrame) m_element;
            if (x > m_x - (pf.m_width / 2)
                && x < m_x + (pf.m_width / 2)
                && y > m_y - (pf.m_height / 2)
                && y < m_y + (pf.m_height / 2)) {
                return true;
            }
        }
        return false;
    }

    public void draw(Graphics g) {

        g.setFont(g.getFont().deriveFont((float) (10.0f * m_pv.m_vp_viewed_zoom)));

        if (m_element instanceof State) {
            g.setColor(Color.lightGray);
            if (m_element instanceof DummyState) {
                g.setColor(Color.white);
            }
            g.fillArc(m_pv.toScreenX(m_x - state_radius),
                m_pv.toScreenY(m_y - state_radius),
                m_pv.toScreen(state_radius * 2),
                m_pv.toScreen(state_radius * 2), 0, 360);
            g.setColor(Color.black);
            g.drawArc(m_pv.toScreenX(m_x - state_radius),
                m_pv.toScreenY(m_y - state_radius),
                m_pv.toScreen(state_radius * 2),
                m_pv.toScreen(state_radius * 2), 0, 360);


            String name = ((State) m_element).getElementID();
            Plan p = ((State) m_element).getPlan();
            if (p != null) {
                if (p instanceof ActionPlan) {
//                    name = ((ActionPlan) p).getAction().getClass().getSimpleName();
                    name = ((ActionPlan) p).getAction().toSimpleString();

                } else if (p instanceof GoalPlan) {
                    name = ((GoalPlan) p).getGoal().getClass().getSimpleName();
                }
            }

            Rectangle2D b = g.getFontMetrics().getStringBounds(name, g);
            g.drawString(name,
                         (int) (m_pv.toScreenX(m_x) - b.getWidth() / 2),
                         (int) (m_pv.toScreenY(m_y - 10) + b.getHeight() / 2));


            // Draw tokens:
            {
                int tokens = ((State) (m_element)).getCurrentNumberOfTokens();
                int n_rows = 0;
                int n_tokens_per_row = 0;
                int n_tokens_last_row = 0;
                int tmp;

                n_tokens_per_row = (int) Math.sqrt(tokens);
                if (n_tokens_per_row > 0) {
                    n_rows = tokens / n_tokens_per_row;
                    n_tokens_last_row = tokens - n_tokens_per_row * (n_rows - 1);

                    for (int i = 0; i < n_rows; i++) {
                        double y = m_y + 10 + (i - ((double) n_rows - 1) / 2.0) * 8;
                        tmp = (i == n_rows - 1 ? n_tokens_last_row : n_tokens_per_row);
                        for (int j = 0; j < tmp; j++) {
                            double x = m_x + (j - ((double) tmp - 1) / 2.0) * 8;
                            g.fillArc(m_pv.toScreenX(x - 3),
                                m_pv.toScreenY(y - 3),
                                m_pv.toScreen(6),
                                m_pv.toScreen(6), 0, 360);
                        }
                    }
                }
            }
        }
        if (m_element instanceof Transition) {
            g.setColor(Color.black);
            if (m_element instanceof PreTransition) {
                g.setColor(Color.yellow);
            }
            if (m_element instanceof SuccessTransition) {
                g.setColor(Color.green);
            }
            if (m_element instanceof FailureTransition) {
                g.setColor(Color.red);
            }
            g.fillRect(m_pv.toScreenX(m_x - (transition_width / 2)), m_pv.toScreenY(m_y - (transition_height / 2)),
                m_pv.toScreen(transition_width), m_pv.toScreen(transition_height));
            g.setColor(Color.black);
            g.drawRect(m_pv.toScreenX(m_x - (transition_width / 2)), m_pv.toScreenY(m_y - (transition_height / 2)),
                m_pv.toScreen(transition_width), m_pv.toScreen(transition_height));

            Rectangle2D b = g.getFontMetrics().getStringBounds(((Transition) m_element).getElementID(), g);
            g.drawString(((Transition) m_element).getElementID(), m_pv.toScreenX(m_x + transition_width * 0.6), (int) (m_pv.toScreenY(m_y) + b.getHeight() / 2));
        }
        if (m_element instanceof ActionPlan) {
            ActionPlan e = (ActionPlan) m_element;
            g.setColor(Color.white);
            g.fillRect(m_pv.toScreenX(m_x - (action_width / 2)), m_pv.toScreenY(m_y - (action_height / 2)),
                m_pv.toScreen(action_width), m_pv.toScreen(action_height));
            g.setColor(Color.black);
            g.drawRect(m_pv.toScreenX(m_x - (action_width / 2)), m_pv.toScreenY(m_y - (action_height / 2)),
                m_pv.toScreen(action_width), m_pv.toScreen(action_height));
            Rectangle2D b = g.getFontMetrics().getStringBounds(e.getAction().getClass().getSimpleName(), g);
            g.drawString(e.getAction().getClass().getSimpleName(), (int) (m_pv.toScreenX(m_x) - b.getWidth() / 2), (int) (m_pv.toScreenY(m_y) + b.getHeight() / 2));
        }
        if (m_element instanceof PlanFrame) {
            PlanFrame e = (PlanFrame) m_element;
            g.setColor(Color.decode("0xdddddd"));
            g.fillRect(m_pv.toScreenX(m_x - (e.m_width / 2)), m_pv.toScreenY(m_y - (e.m_height / 2)),
                m_pv.toScreen(e.m_width), m_pv.toScreen(e.m_height));
            g.setColor(Color.black);
            g.drawRect(m_pv.toScreenX(m_x - (e.m_width / 2)), m_pv.toScreenY(m_y - (e.m_height / 2)),
                m_pv.toScreen(e.m_width), m_pv.toScreen(e.m_height));

            if (e.m_links != null) {
                for (LocationLink p : e.m_links) {
                    Location e1 = m_pv.m_all_elements.get(p.m_a);
                    Location e2 = m_pv.m_all_elements.get(p.m_b);
                    if (e1 != null && e2 != null) {
                        if (p instanceof WeakLocationLink) {
                            e1.drawWeakLink(e2, g);
                        } else {
                            e1.drawLink(e2, g);
                        } // if
                    }
                }
            }
            {
                List<Location> ll = new LinkedList<Location>();
                ll.addAll(e.m_elements.values());
                for (Location l : ll) {
                    l.draw(g);
                }
            }
        }
        if (m_element instanceof GoalPlan) {
            GoalPlan e = (GoalPlan) m_element;
            g.setColor(Color.decode("0xbbbbff"));
            g.fillRect(m_pv.toScreenX(m_x - (action_width / 2)), m_pv.toScreenY(m_y - (action_height / 2)),
                m_pv.toScreen(action_width), m_pv.toScreen(action_height));
            g.setColor(Color.black);
            g.drawRect(m_pv.toScreenX(m_x - (action_width / 2)), m_pv.toScreenY(m_y - (action_height / 2)),
                m_pv.toScreen(action_width), m_pv.toScreen(action_height));
            if (e.getGoal() == null) {
                Rectangle2D b = g.getFontMetrics().getStringBounds("Goal = null", g);
                g.drawString("Goal = null", (int) (m_pv.toScreenX(m_x) - b.getWidth() / 2), (int) (m_pv.toScreenY(m_y) + b.getHeight() / 2));
            } else {
                Rectangle2D b = g.getFontMetrics().getStringBounds(e.getGoal().getClass().getSimpleName(), g);
                g.drawString(e.getGoal().getClass().getSimpleName(), (int) (m_pv.toScreenX(m_x) - b.getWidth() / 2), (int) (m_pv.toScreenY(m_y) + b.getHeight() / 2));
            }
        }

    }

    public void drawLink(Location e2, Graphics g) {

        // Draw the arrow tips:
        {
            double vx = m_x - e2.m_x;
            double vy = m_y - e2.m_y;
            double n = Math.sqrt(vx * vx + vy * vy);
            if (n != 0) {
                vx /= n;
                vy /= n;
                double cvx = -vy;
                double cvy = vx;
                double start_x = e2.m_x;
                double start_y = e2.m_y;
                double f = 0.0;
                boolean found = false;
                while (!found && f < n) {
                    f += 1.0;
                    if (!e2.point_inside((int) (start_x + f * vx), (int) (start_y + f * vy))) {
                        found = true;
                    }
                }

                g.setColor(Color.black);
                if (found) {/* Copyright 2010 Santiago Ontanon and Ashwin Ram */


                    start_x += f * vx;
                    start_y += f * vy;
                    int px[] = {m_pv.toScreenX(start_x), m_pv.toScreenX(start_x + vx * 10 + cvx * 5), m_pv.toScreenX(start_x + vx * 10 - cvx * 5)};
                    int py[] = {m_pv.toScreenY(start_y), m_pv.toScreenY(start_y + vy * 10 + cvy * 5), m_pv.toScreenY(start_y + vy * 10 - cvy * 5)};

                    g.fillPolygon(px, py, 3);
                    g.drawLine(m_pv.toScreenX(m_x), m_pv.toScreenY(m_y),
                        m_pv.toScreenX(start_x), m_pv.toScreenY(start_y));
                } else {
                    g.drawLine(m_pv.toScreenX(m_x), m_pv.toScreenY(m_y),
                        m_pv.toScreenX(e2.m_x), m_pv.toScreenY(e2.m_y));
                } // if (found)
            }
        }
    }

    public void drawWeakLink(Location e2, Graphics g) {

        // Draw the arrow tips:
        {
            double vx = m_x - e2.m_x;
            double vy = m_y - e2.m_y;
            double n = Math.sqrt(vx * vx + vy * vy);
            if (n != 0) {
                vx /= n;
                vy /= n;
                double cvx = -vy;
                double cvy = vx;
                double start_x = e2.m_x;
                double start_y = e2.m_y;
                double f = 0.0;
                boolean found = false;
                while (!found && f < n) {
                    f += 1.0;
                    if (!e2.point_inside((int) (start_x + f * vx), (int) (start_y + f * vy))) {
                        found = true;
                    }
                }

                g.setColor(Color.gray);
                if (found) {
                    start_x += f * vx;
                    start_y += f * vy;
                    int px[] = {m_pv.toScreenX(start_x), m_pv.toScreenX(start_x + vx * 10 + cvx * 5), m_pv.toScreenX(start_x + vx * 10 - cvx * 5)};
                    int py[] = {m_pv.toScreenY(start_y), m_pv.toScreenY(start_y + vy * 10 + cvy * 5), m_pv.toScreenY(start_y + vy * 10 - cvy * 5)};

                    g.fillPolygon(px, py, 3);
                    g.drawLine(m_pv.toScreenX(m_x), m_pv.toScreenY(m_y),
                        m_pv.toScreenX(start_x), m_pv.toScreenY(start_y));
                } else {
                    g.drawLine(m_pv.toScreenX(m_x), m_pv.toScreenY(m_y),
                        m_pv.toScreenX(e2.m_x), m_pv.toScreenY(e2.m_y));
                } // if (found)
            }
        }
    }

    public double distance(Location e2) {
        if (m_element instanceof State) {
            if (e2.m_element instanceof State) {
                return circleToCircleDistance(m_x, m_y, state_radius, e2.m_x, e2.m_y, state_radius);
            } else if (e2.m_element instanceof Transition) {
                return circleToSquareDistance(m_x, m_y, state_radius,
                    e2.m_x - transition_width / 2, e2.m_y - transition_height / 2, e2.m_x + transition_width / 2, e2.m_y + transition_height / 2);
            } else if (e2.m_element instanceof PlanFrame) {
                return circleToSquareDistance(m_x, m_y, state_radius,
                    e2.m_x - ((PlanFrame) e2.m_element).m_width / 2, e2.m_y - ((PlanFrame) e2.m_element).m_height / 2, e2.m_x + ((PlanFrame) e2.m_element).m_width / 2, e2.m_y + ((PlanFrame) e2.m_element).m_height / 2);
            } else if (e2.m_element instanceof ActionPlan) {
                return circleToSquareDistance(m_x, m_y, state_radius,
                    e2.m_x - action_width / 2, e2.m_y - action_height / 2, e2.m_x + action_width / 2, e2.m_y + action_height / 2);
            } else if (e2.m_element instanceof GoalPlan) {
                return circleToSquareDistance(m_x, m_y, state_radius,
                    e2.m_x - goal_width / 2, e2.m_y - goal_height / 2, e2.m_x + goal_width / 2, e2.m_y + goal_height / 2);
            }
        } else if (m_element instanceof Transition) {
            if (e2.m_element instanceof State) {
                return circleToSquareDistance(e2.m_x, e2.m_y, state_radius,
                    m_x - transition_width / 2, m_y - transition_height / 2, m_x + transition_width / 2, m_y + transition_height / 2);
            } else if (e2.m_element instanceof Transition) {
                return squareToSquareDistance(m_x - transition_width / 2, m_y - transition_height / 2, m_x + transition_width / 2, m_y + transition_height / 2,
                    e2.m_x - transition_width / 2, e2.m_y - transition_height / 2, e2.m_x + transition_width / 2, e2.m_y + transition_height / 2);
            } else if (e2.m_element instanceof PlanFrame) {
                return squareToSquareDistance(m_x - transition_width / 2, m_y - transition_height / 2, m_x + transition_width / 2, m_y + transition_height / 2,
                    e2.m_x - ((PlanFrame) e2.m_element).m_width / 2, e2.m_y - ((PlanFrame) e2.m_element).m_height / 2, e2.m_x + ((PlanFrame) e2.m_element).m_width / 2, e2.m_y + ((PlanFrame) e2.m_element).m_height / 2);
            } else if (e2.m_element instanceof ActionPlan) {
                return squareToSquareDistance(m_x - transition_width / 2, m_y - transition_height / 2, m_x + transition_width / 2, m_y + transition_height / 2,
                    e2.m_x - action_width / 2, e2.m_y - action_height / 2, e2.m_x + action_width / 2, e2.m_y + action_height / 2);
            } else if (e2.m_element instanceof GoalPlan) {
                return squareToSquareDistance(m_x - transition_width / 2, m_y - transition_height / 2, m_x + transition_width / 2, m_y + transition_height / 2,
                    e2.m_x - goal_width / 2, e2.m_y - goal_height / 2, e2.m_x + goal_width / 2, e2.m_y + goal_height / 2);
            }
        } else if (m_element instanceof PlanFrame) {
            if (e2.m_element instanceof State) {
                return circleToSquareDistance(e2.m_x, e2.m_y, state_radius,
                    m_x - ((PlanFrame) m_element).m_width / 2, m_y - ((PlanFrame) m_element).m_height / 2, m_x + ((PlanFrame) m_element).m_width / 2, m_y + ((PlanFrame) m_element).m_height / 2);
            } else if (e2.m_element instanceof Transition) {
                return squareToSquareDistance(m_x - ((PlanFrame) m_element).m_width / 2, m_y - ((PlanFrame) m_element).m_height / 2, m_x + ((PlanFrame) m_element).m_width / 2, m_y + ((PlanFrame) m_element).m_height / 2,
                    e2.m_x - transition_width / 2, e2.m_y - transition_height / 2, e2.m_x + transition_width / 2, e2.m_y + transition_height / 2);
            } else if (e2.m_element instanceof PlanFrame) {
                return squareToSquareDistance(m_x - ((PlanFrame) m_element).m_width / 2, m_y - ((PlanFrame) m_element).m_height / 2, m_x + ((PlanFrame) m_element).m_width / 2, m_y + ((PlanFrame) m_element).m_height / 2,
                    e2.m_x - ((PlanFrame) e2.m_element).m_width / 2, e2.m_y - ((PlanFrame) e2.m_element).m_height / 2, e2.m_x + ((PlanFrame) e2.m_element).m_width / 2, e2.m_y + ((PlanFrame) e2.m_element).m_height / 2);
            } else if (e2.m_element instanceof ActionPlan) {
                return squareToSquareDistance(m_x - ((PlanFrame) m_element).m_width / 2, m_y - ((PlanFrame) m_element).m_height / 2, m_x + ((PlanFrame) m_element).m_width / 2, m_y + ((PlanFrame) m_element).m_height / 2,
                    e2.m_x - action_width / 2, e2.m_y - action_height / 2, e2.m_x + action_width / 2, e2.m_y + action_height / 2);
            } else if (e2.m_element instanceof GoalPlan) {
                return squareToSquareDistance(m_x - ((PlanFrame) m_element).m_width / 2, m_y - ((PlanFrame) m_element).m_height / 2, m_x + ((PlanFrame) m_element).m_width / 2, m_y + ((PlanFrame) m_element).m_height / 2,
                    e2.m_x - goal_width / 2, e2.m_y - goal_height / 2, e2.m_x + goal_width / 2, e2.m_y + goal_height / 2);
            }
        } else if (m_element instanceof ActionPlan) {
            if (e2.m_element instanceof State) {
                return circleToSquareDistance(e2.m_x, e2.m_y, state_radius,
                    m_x - action_width / 2, m_y - action_height / 2, m_x + action_width / 2, m_y + action_height / 2);
            } else if (e2.m_element instanceof Transition) {
                return squareToSquareDistance(m_x - action_width / 2, m_y - action_height / 2, m_x + action_width / 2, m_y + action_height / 2,
                    e2.m_x - transition_width / 2, e2.m_y - transition_height / 2, e2.m_x + transition_width / 2, e2.m_y + transition_height / 2);
            } else if (e2.m_element instanceof PlanFrame) {
                return squareToSquareDistance(m_x - action_width / 2, m_y - action_height / 2, m_x + action_width / 2, m_y + action_height / 2,
                    e2.m_x - ((PlanFrame) e2.m_element).m_width / 2, e2.m_y - ((PlanFrame) e2.m_element).m_height / 2, e2.m_x + ((PlanFrame) e2.m_element).m_width / 2, e2.m_y + ((PlanFrame) e2.m_element).m_height / 2);
            } else if (e2.m_element instanceof ActionPlan) {
                return squareToSquareDistance(m_x - action_width / 2, m_y - action_height / 2, m_x + action_width / 2, m_y + action_height / 2,
                    e2.m_x - action_width / 2, e2.m_y - action_height / 2, e2.m_x + action_width / 2, e2.m_y + action_height / 2);
            } else if (e2.m_element instanceof GoalPlan) {
                return squareToSquareDistance(m_x - action_width / 2, m_y - action_height / 2, m_x + action_width / 2, m_y + action_height / 2,
                    e2.m_x - goal_width / 2, e2.m_y - goal_height / 2, e2.m_x + goal_width / 2, e2.m_y + goal_height / 2);
            }
        } else if (m_element instanceof GoalPlan) {
            if (e2.m_element instanceof State) {
                return circleToSquareDistance(e2.m_x, e2.m_y, state_radius,
                    m_x - goal_width / 2, m_y - goal_height / 2, m_x + goal_width / 2, m_y + goal_height / 2);
            } else if (e2.m_element instanceof Transition) {
                return squareToSquareDistance(m_x - goal_width / 2, m_y - goal_height / 2, m_x + goal_width / 2, m_y + goal_height / 2,
                    e2.m_x - transition_width / 2, e2.m_y - transition_height / 2, e2.m_x + transition_width / 2, e2.m_y + transition_height / 2);
            } else if (e2.m_element instanceof PlanFrame) {
                return squareToSquareDistance(m_x - goal_width / 2, m_y - goal_height / 2, m_x + goal_width / 2, m_y + goal_height / 2,
                    e2.m_x - ((PlanFrame) e2.m_element).m_width / 2, e2.m_y - ((PlanFrame) e2.m_element).m_height / 2, e2.m_x + ((PlanFrame) e2.m_element).m_width / 2, e2.m_y + ((PlanFrame) e2.m_element).m_height / 2);
            } else if (e2.m_element instanceof ActionPlan) {
                return squareToSquareDistance(m_x - goal_width / 2, m_y - goal_height / 2, m_x + goal_width / 2, m_y + goal_height / 2,
                    e2.m_x - action_width / 2, e2.m_y - action_height / 2, e2.m_x + action_width / 2, e2.m_y + action_height / 2);
            } else if (e2.m_element instanceof GoalPlan) {
                return squareToSquareDistance(m_x - goal_width / 2, m_y - goal_height / 2, m_x + goal_width / 2, m_y + goal_height / 2,
                    e2.m_x - goal_width / 2, e2.m_y - goal_height / 2, e2.m_x + goal_width / 2, e2.m_y + goal_height / 2);
            }
        }

        return 0.0;
    }

    static public double squareToSquareDistance(double s1x1, double s1y1, double s1x2, double s1y2,
        double s2x1, double s2y1, double s2x2, double s2y2) {
        if (s2x1 > s1x2) {
            if (s2y1 > s1y2) {
                return Math.sqrt((s1x2 - s2x1) * (s1x2 - s2x1) + (s1y2 - s2y1) * (s1y2 - s2y1));
            } else if (s2y2 < s1y1) {
                return Math.sqrt((s1x2 - s2x1) * (s1x2 - s2x1) + (s1y1 - s2y2) * (s1y1 - s2y2));
            } else {
                return s2x1 - s1x2;
            }
        } else if (s2x2 < s1x1) {
            if (s2y1 > s1y2) {
                return Math.sqrt((s1x1 - s2x2) * (s1x1 - s2x2) + (s1y2 - s2y1) * (s1y2 - s2y1));
            } else if (s2y2 < s1y1) {
                return Math.sqrt((s1x1 - s2x2) * (s1x1 - s2x2) + (s1y1 - s2y2) * (s1y1 - s2y2));
            } else {
                return s1x1 - s2x2;
            }
        } else {
            if (s2y1 > s1y2) {
                return s2y1 - s1y2;
            } else if (s2y2 < s1y1) {
                return s1y1 - s2y2;
            } else {
                return 0.0;
            }
        }
    }

    static public double circleToSquareDistance(double cx, double cy, double cr,
        double sx1, double sy1, double sx2, double sy2) {
        if (cx < sx1) {
            if (cy < sy1) {
                double d = Math.sqrt((cx - sx1) * (cx - sx1) + (cy - sy1) * (cy - sy1));
                d -= cr;
                if (d < 0) {
                    d = 0;
                }
                return d;
            } else if (cy > sy2) {
                double d = Math.sqrt((cx - sx1) * (cx - sx1) + (cy - sy2) * (cy - sy2));
                d -= cr;
                if (d < 0) {
                    d = 0;
                }
                return d;
            } else {
                double d = (sx1 - cx) - cr;
                if (d < 0) {
                    d = 0;
                }
                return d;
            }
        } else if (cx > sx2) {
            if (cy < sy1) {
                double d = Math.sqrt((cx - sx2) * (cx - sx2) + (cy - sy1) * (cy - sy1));
                d -= cr;
                if (d < 0) {
                    d = 0;
                }
                return d;
            } else if (cy > sy2) {
                double d = Math.sqrt((cx - sx2) * (cx - sx2) + (cy - sy2) * (cy - sy2));
                d -= cr;
                if (d < 0) {
                    d = 0;
                }
                return d;
            } else {
                double d = (cx - sx2) - cr;
                if (d < 0) {
                    d = 0;
                }
                return d;
            }
        } else {
            if (cy < sy1) {
                double d = (sy1 - cy) - cr;
                if (d < 0) {
                    d = 0;
                }
                return d;
            } else if (cy > sy2) {
                double d = (cy - sy2) - cr;
                if (d < 0) {
                    d = 0;
                }
                return d;
            } else {
                return 0.0;
            }
        }
    }

    static public double circleToCircleDistance(double c1x, double c1y, double c1r,
        double c2x, double c2y, double c2r) {
        double d = Math.sqrt((c1x - c2x) * (c1x - c2x) + (c1y - c2y) * (c1y - c2y));
        d -= c1r + c2r;
        if (d < 0) {
            d = 0;
        }
        return d;
    }

    public Rectangle boundingBox() {
        Rectangle r = new Rectangle(m_x, m_y, 0, 0);
        if (m_element instanceof State) {
            r.setFrame(m_x - state_radius, m_y - state_radius, state_radius * 2, state_radius * 2);
        } else if (m_element instanceof Transition) {
            r.setFrame(m_x - transition_width / 2, m_y - transition_height / 2, transition_width, transition_height);
        } else if (m_element instanceof PlanFrame) {
            r.setFrame(m_x - ((PlanFrame) m_element).m_width / 2, m_y - ((PlanFrame) m_element).m_height / 2, ((PlanFrame) m_element).m_width, ((PlanFrame) m_element).m_height);
        } else if (m_element instanceof ActionPlan) {
            r.setFrame(m_x - action_width / 2, m_y - action_height / 2, action_width, action_height);
        } else if (m_element instanceof GoalPlan) {
            r.setFrame(m_x - goal_width / 2, m_y - goal_height / 2, goal_width, goal_height);
        }
        return r;
    }
}
