package dom.techtree.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import dom.techtree.IconManager;
import dom.techtree.LocalizationManager;
import dom.techtree.data.Node;
import dom.techtree.data.Parent;
import dom.techtree.data.TechTree;

@SuppressWarnings("serial")
public class TechTreePanel extends JPanel {
	private static final int GRID_SIZE = 40;
	private static final double NODE_SIZE = 64.0, SMALL_NODE_SIZE = 8.0;
	private static final float SELECTION_HALO_WIDTH = 3.0f;
	
	private Point.Double viewPos = new Point.Double(-1560.0, -1120.0);
	private double scale = 1.0;
	private TechTree tree;
	private Node selectedNode = null, hoverNode= null;
	private int selectedSide = Parent.NONE;
	
	private Point mousePos = null;
	
	public TechTreePanel() {
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					if(e.getClickCount() == 1) {
						mousePos = e.getPoint();
						Point.Double localPos = new Point.Double(viewPos.x + e.getX()/scale, viewPos.y + e.getY()/scale);
						if(selectedNode != null) {
							onDeselect(selectedNode);
							selectedNode = null;
						}
						Node node = getNodeAtPoint(localPos);
						if(node != null) {
							selectedNode = node;
							onSelect(node);
							int side = getSideAtPoint(node, localPos);
							if(side != Parent.NONE) {
								selectedSide = side;
							}
						}
						repaint();
					} else if(e.getClickCount() == 2) {
						Point.Double localPos = new Point.Double(viewPos.x + e.getX()/scale, viewPos.y + e.getY()/scale);
						for(Node node : tree.getNodeList()) {
							for(Parent parentInfo : node.parentList) {
								Node parent = tree.getNodeByID(parentInfo.id);
								Point.Double p1 = getSmallNodePos(parent, parentInfo.lineFrom);
								Point.Double p2 = getSmallNodePos(node, parentInfo.lineTo);
								Point.Double max = new Point.Double(Math.max(p1.x, p2.x), Math.max(p1.y, p2.y));
								Point.Double min = new Point.Double(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y));
								if(localPos.x < max.x + 4 && localPos.x > min.x - 4 && localPos.y < max.y + 4&& localPos.y > min.y - 4) {
									double dis = Math.abs((p2.x - p1.x) * (p1.y - localPos.y) - (p1.x - localPos.x) * (p2.y - p1.y)) / Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2)); 
									if(dis < 8) {
										node.parentList.remove(parentInfo);
										repaint();
										return;
									}
								}
							}
						}
					}
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					Point.Double localPos = new Point.Double(viewPos.x + e.getX()/scale, viewPos.y + e.getY()/scale);
					if(selectedSide != Parent.NONE) {
						Node node = getNodeAtPoint(localPos);
						if(node != null) {
							int side = getSideAtPoint(node, localPos);
							if(side == Parent.NONE) {
								double relX = node.pos.x - selectedNode.pos.x,
									   relY = node.pos.y - selectedNode.pos.y;
								if(Math.abs(relX) > Math.abs(relY)) {
									if(relX < 0) {
										side = Parent.RIGHT;
									} else {
										side = Parent.LEFT;
									}
								} else {
									if(relY < 0) {
										side = Parent.TOP;
									} else {
										side = Parent.BOTTOM;
									}
								}
							}
							node.parentList.add(new Parent(selectedNode.id, side, selectedSide));
							repaint();
						}
					}
					selectedSide = Parent.NONE;
				}
			}	
			
			@Override 
			public void mouseMoved(MouseEvent e) {
				if(tree != null) {
					Point.Double localPos = new Point.Double(viewPos.x + e.getX()/scale, viewPos.y + e.getY()/scale);
					Node newHoverNode = getNodeAtPoint(localPos);
					if(newHoverNode != hoverNode) {
						hoverNode = newHoverNode;
						repaint();
					}
				}
				mousePos = e.getPoint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					int deltaX = e.getX() - mousePos.x;
					int deltaY = e.getY() - mousePos.y;
					if(selectedNode == null) {
						viewPos.x -= deltaX / scale;
						viewPos.y -= deltaY / scale;
					} else if(selectedSide == Parent.NONE){
						selectedNode.pos.x += deltaX / scale;
						selectedNode.pos.y -= deltaY / scale;
					}
					repaint();
				}
				mousePos = e.getPoint();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				hoverNode = null;
				repaint();
			}
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				double prevScale = scale;
				scale *= Math.pow(1.0905, -e.getWheelRotation());
				if(scale > 2) { scale = 2; }
				if(scale < 0.5) { scale = 0.5; }
				viewPos.x -= 0.5*(1/scale - 1/prevScale)*getWidth();
				viewPos.y -= 0.5*(1/scale - 1/prevScale)*getHeight();
				repaint();
			}
		};
		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);
		this.addMouseWheelListener(mouseAdapter);
	}
	
	public void setTechTree(TechTree tree) {
		this.tree = tree;
		selectedNode = null;
		hoverNode = null;
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(
	             RenderingHints.KEY_TEXT_ANTIALIASING,
	             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHints(rh);
		g2d.setColor(new Color(32, 16, 48));
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		g2d.setColor(new Color(48, 64, 32));
		g2d.setStroke(new BasicStroke(1.0f));
		for(double x = (int) (-viewPos.x % GRID_SIZE*scale); x < getWidth(); x += GRID_SIZE*scale) {
			g2d.drawLine((int) x, 0, (int) x, getHeight());
		}
		for(double y = (int) (-viewPos.y % GRID_SIZE*scale); y < getHeight(); y += GRID_SIZE*scale) {
			g2d.drawLine(0, (int) y, getWidth(), (int) y);
		}
		g2d.setColor(new Color(64, 64, 64));
		g2d.setStroke(new BasicStroke(3.0f));
		g2d.drawLine((int) (-viewPos.x*scale), 0, (int) (-viewPos.x*scale), getHeight());
		g2d.drawLine(0, (int) (-viewPos.y*scale), getWidth(), (int) (-viewPos.y*scale));
		
		g2d.scale(scale, scale);
		g2d.translate(-viewPos.x, -viewPos.y);
		
		if(tree != null) {
			g2d.setColor(new Color(192, 192, 192));
			g2d.setStroke(new BasicStroke(1.0f));
			for(Node node : tree.getNodeList()) {
				// Draw the icon of this node
				g2d.drawImage(IconManager.BLANK_NODE, (int) node.pos.x, (int) -node.pos.y,
						(int) (NODE_SIZE*node.scale), (int) (NODE_SIZE*node.scale), null);
				Image icon = IconManager.get(node.icon);
				if(icon != null) {
					g2d.drawImage(icon, (int) node.pos.x, (int) -node.pos.y,
							(int) (NODE_SIZE*node.scale), (int) (NODE_SIZE*node.scale), null);
				}
				
				// Draw an arrow from this node to each of its parents
				for(Parent parentInfo : node.parentList) {
					Node parent = tree.getNodeByID(parentInfo.id);
					if(parent != null) {
						Point.Double p1 = getSmallNodePos(parent, parentInfo.lineFrom);
						Point.Double p2 = getSmallNodePos(node, parentInfo.lineTo);
						drawArrow(p1, p2, g2d);
					}
				}
			}
			
			// Draw arrow-in-progress
			if(selectedNode != null && selectedSide > 0 && mousePos != null) {
				Point.Double p1 = getSmallNodePos(selectedNode, selectedSide);
				Point.Double localMousePos = new Point.Double(viewPos.x + mousePos.getX()/scale, viewPos.y + mousePos.getY()/scale);
				drawArrow(p1, localMousePos, g2d);
			}
			
			// Draw green border around selected node
			if(selectedNode != null) {
				g2d.setColor(new Color(128, 192, 72));
				g2d.setStroke(new BasicStroke(SELECTION_HALO_WIDTH));
				g2d.drawRect((int) selectedNode.pos.x, (int) -selectedNode.pos.y,
						(int) (NODE_SIZE*selectedNode.scale), (int) (NODE_SIZE*selectedNode.scale));
			}
			
			// Draw four blue nodules around hovered node
			if(hoverNode != null) {
				g2d.setColor(new Color(32, 128, 240));
				g2d.fillOval((int) (hoverNode.pos.x + 0.5*NODE_SIZE*hoverNode.scale - 0.5*SMALL_NODE_SIZE),
						(int) (-hoverNode.pos.y - 0.5*SMALL_NODE_SIZE),
						(int) SMALL_NODE_SIZE, (int) SMALL_NODE_SIZE);
				g2d.fillOval((int) (hoverNode.pos.x - 0.5*SMALL_NODE_SIZE),
						(int) (-hoverNode.pos.y + 0.5*NODE_SIZE*hoverNode.scale - 0.5*SMALL_NODE_SIZE),
						(int) SMALL_NODE_SIZE, (int) SMALL_NODE_SIZE);
				g2d.fillOval((int) (hoverNode.pos.x + 0.5*NODE_SIZE*hoverNode.scale - 0.5*SMALL_NODE_SIZE),
						(int) (-hoverNode.pos.y + NODE_SIZE*hoverNode.scale - 0.5*SMALL_NODE_SIZE),
						(int) SMALL_NODE_SIZE, (int) SMALL_NODE_SIZE);
				g2d.fillOval((int) (hoverNode.pos.x + NODE_SIZE*hoverNode.scale - 0.5*SMALL_NODE_SIZE),
						(int) (-hoverNode.pos.y + 0.5*NODE_SIZE*hoverNode.scale - 0.5*SMALL_NODE_SIZE),
						(int) SMALL_NODE_SIZE, (int) SMALL_NODE_SIZE);
			}
			
			// Draw part count in upper left corner of each node
			for(Node node : tree.getNodeList()) {
				g2d.setColor(new Color(255, 255, 255));
				g2d.drawString(String.format("%s (%d)", LocalizationManager.translate(node.title), tree.getPartList(node).size()), (int) node.pos.x, (int) -node.pos.y - 2);
			}
		}
	}
	
	// Returns the node at a given point, or null
	private Node getNodeAtPoint(Point.Double p) {
		for(Node node : tree.getNodeList()) {
			if(p.x > node.pos.x - 4 && p.x < node.pos.x + NODE_SIZE*node.scale + 4 &&
			   p.y > -node.pos.y - 4 && p.y < -node.pos.y + NODE_SIZE*node.scale + 4) {
				return node;
			}
		}
		return null;
	}
	
	// Returns which side of the given node is located at the given point, or NONE if none are close
	private int getSideAtPoint(Node node, Point.Double p) {
		for(int i = 1; i <= 4; i ++) {
			Point.Double smallNodePos = getSmallNodePos(node, i);
			if(p.x > smallNodePos.x - 2 && p.x < smallNodePos.x + SMALL_NODE_SIZE*node.scale + 2 &&
			   p.y > smallNodePos.y - 2 && p.y < smallNodePos.y + SMALL_NODE_SIZE*node.scale + 2) {
				return i;
			}
		}
		return Parent.NONE;
	}
	
	
	// Returns the location of the small node on the given side of a large node
	private Point.Double getSmallNodePos(Node node, int side) {
		Point.Double p = new Point.Double(node.pos.x, -node.pos.y);
		switch(side) {
		case Parent.TOP:
			p.x += 0.5*NODE_SIZE*node.scale;
			break;
		case Parent.RIGHT:
			p.x += NODE_SIZE*node.scale;
			p.y += 0.5*NODE_SIZE*node.scale;
			break;
		case Parent.BOTTOM:
			p.x += 0.5*NODE_SIZE*node.scale;
			p.y += NODE_SIZE*node.scale;
			break;
		case Parent.LEFT:
			p.y += 0.5*NODE_SIZE*node.scale;
			break;
		}
		return p;
	}
	
	private void drawArrow(Point.Double p1, Point.Double p2, Graphics g) {
		double d = 14, h = 6;
		double dx = p2.x - p1.x, dy = p2.y - p1.y;
	    double D = Math.sqrt(dx*dx + dy*dy);
	    double xm = D - d, xn = xm, ym = h, yn = -h, x;
	    double sin = dy / D, cos = dx / D;

	    x = xm*cos - ym*sin + p1.x;
	    ym = xm*sin + ym*cos + p1.y;
	    xm = x;

	    x = xn*cos - yn*sin + p1.x;
	    yn = xn*sin + yn*cos + p1.y;
	    xn = x;

	    int[] xpoints = {(int) p2.x, (int) xm, (int) xn};
	    int[] ypoints = {(int) p2.y, (int) ym, (int) yn};

	    g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
	    g.fillPolygon(xpoints, ypoints, 3);
	}
	
	// Empty functions for overriding
	public void onSelect(Node node) {}
	public void onDeselect(Node node) {}
	public void onMove(Node node) {}
}
