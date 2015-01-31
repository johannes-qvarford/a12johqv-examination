/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.util.planvisualizer;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class PVMouseDragger implements MouseMotionListener,MouseListener {
		PlanVisualizer m_v;
		int last_x=-1,last_y=-1;
		int dragged_offset_x = 0, dragged_offset_y = 0;
		Location m_dragging = null;

		PVMouseDragger(PlanVisualizer v) {
			m_v = v;			
		}
		
		Location findElement(int x,int y,PlanFrame frame) {
			if (frame==null) {
				for(Location element:m_v.m_elements.values()) {
					if (element.point_inside(x,y)) {
						if (element.m_element instanceof PlanFrame) {
							Location e = findElement(x,y,(PlanFrame)(element.m_element));
							if (e!=null) return e;
						} // if 
						return element;
					}
				}
			} else {
				for(Location element:frame.m_elements.values()) {
					if (element.point_inside(x,y)) {
						if (element.m_element instanceof PlanFrame) {
							Location e = findElement(x,y,(PlanFrame)(element.m_element));
							if (e!=null) return e;
						} // if 
						return element;
					}
				}				
			}
			return null;
		}

		public void mouseDragged(MouseEvent e) {

//			System.out.println(last_x + "," + last_y + " -> " + e.getX() + "," + e.getY());

			if (last_x==-1) {	
				m_dragging = findElement((int)m_v.fromScreenX(e.getX()),(int)m_v.fromScreenY(e.getY()),null);
				if (m_dragging!=null) {
					dragged_offset_x = (int) (m_dragging.m_x - m_v.fromScreenX2(e.getX()));
					dragged_offset_y = (int) (m_dragging.m_y - m_v.fromScreenY2(e.getY()));											
				} else {
					m_dragging = null;
					dragged_offset_x = (int) (m_v.m_vp_x + m_v.fromScreenX2(e.getX()));
					dragged_offset_y = (int) (m_v.m_vp_y + m_v.fromScreenY2(e.getY()));					
				}
			} else {
				if (m_dragging!=null) {
					// Dragging an element:
					m_dragging.m_x = (int) (m_v.fromScreenX2(e.getX()) + dragged_offset_x);
					m_dragging.m_y = (int) (m_v.fromScreenY2(e.getY()) + dragged_offset_y);
					if (m_dragging.m_element instanceof PlanFrame) {
						PlanFrame pf = (PlanFrame)m_dragging.m_element;
						pf.move(m_dragging.m_x - pf.m_x, m_dragging.m_y - pf.m_y);
					}
				} else {
					// Dragging the view:
					m_v.m_vp_x = (int) ((-m_v.fromScreenX2(e.getX())) + dragged_offset_x);
					m_v.m_vp_y = (int) ((-m_v.fromScreenY2(e.getY())) + dragged_offset_y);
				}
			}

			last_x = e.getX();
			last_y = e.getY();
			m_v.m_mouse_x = m_v.fromScreenX(e.getX());
			m_v.m_mouse_y = m_v.fromScreenY(e.getY());
		}

		public void mouseMoved(MouseEvent e) {
			m_v.m_mouse_x = m_v.fromScreenX(e.getX());
			m_v.m_mouse_y = m_v.fromScreenY(e.getY());
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
			last_x = -1;
			last_y = -1;
			m_dragging = null;
		}

	}