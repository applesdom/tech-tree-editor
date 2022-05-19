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

public class TechTreePanel extends JPanel {
	// Constant values to control the look and feel of this component
	private static final double MIN_SCALE = 0.25,
								MAX_SCALE = 4.0,
								SCALE_RATE = 1.0905,
								GRID_SIZE = 40.0,
								NODE_SIZE = 64.0,
								CONNECTION_HANDLE_SIZE = 8.0,
								FRINGE_SIZE = 6.0;
	private static final Color BACKGROUND_COLOR = new Color(32, 16, 48),
							   GRID_MINOR_COLOR = new Color(48, 64, 32),
							   GRID_MAJOR_COLOR = new Color(64, 64, 64),
							   SELECTED_NODE_COLOR = new Color(128, 192, 72),
							   CONNECTION_HANDLE_COLOR = new Color(32, 128, 240),
							   CONNECTION_COLOR = new Color(192, 192, 192),
							   TEXT_COLOR = new Color(255, 255, 255);
	private static final float GRID_MINOR_STROKE_SIZE = 1.0f,
							   GRID_MAJOR_STROKE_SIZE = 3.0f,
							   SELECTED_NODE_STROKE_SIZE = 3.0f,
							   CONNECTION_STROKE_SIZE = 1.0f;
	
	// Values to store the position and zoom factor of the window
	// In window space (local): x values increase to the right, y values increase to the bottom, origin is at top left of window
	// In tech tree space (global): x values increase to the right, y values increase to the top, top left of window is located at viewPos
	private Point.Double viewPos = new Point.Double(-1560.0, 1120.0);
	private double scale = 1.0;
	
	private TechTree tree;
	private Node selectedNode = null, hoverNode= null;
	private int selectedSide = Parent.NONE;
	
	private Point mousePos = null;
	
	public TechTreePanel() {
		// Set up this component's own mouse listener
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					if(e.getClickCount() == 1) {
						// Deselect the current node
						if(selectedNode != null) {
							onDeselect(selectedNode);
							selectedNode = null;
						}
						
						// Check if a new node was selected
						Point.Double globalPos = convertToGlobalSpace(e.getPoint());
						Node node = getNodeAtPoint(globalPos);
						if(node != null) {
							selectedNode = node;
							onSelect(node);
							
							// Check if a connection handle was clicked
							selectedSide = getSideAtPoint(node, globalPos);
						}
					} else if(e.getClickCount() == 2) {
						// Check if a node connection was double-clicked, and delete it if so
						Point.Double globalPos = convertToGlobalSpace(e.getPoint());
						nodeLoop:
						for(Node node : tree.getNodeList()) {
							for(Parent parentInfo : node.parentList) {
								Node parent = tree.getNodeByID(parentInfo.id);
								Point.Double p1 = getConnectionHandlePos(parent, parentInfo.lineFrom);
								Point.Double p2 = getConnectionHandlePos(node, parentInfo.lineTo);
								Point.Double max = new Point.Double(Math.max(p1.x, p2.x), Math.max(p1.y, p2.y));
								Point.Double min = new Point.Double(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y));
								
								// Use box approximation first
								if(globalPos.x < max.x + FRINGE_SIZE && globalPos.x > min.x - FRINGE_SIZE && globalPos.y < max.y + FRINGE_SIZE && globalPos.y > min.y - FRINGE_SIZE) {
									// Then use highly advanced line-distance formula
									double dis = Math.abs((p2.x - p1.x) * (p1.y - globalPos.y) - (p1.x - globalPos.x) * (p2.y - p1.y)) /
											Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2)); 
									if(dis < FRINGE_SIZE) {
										node.parentList.remove(parentInfo);
										repaint();
										break nodeLoop;
									}
								}
							}
						}
					}
				}
				mousePos = e.getPoint();
				repaint();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					// Check if a new node connection is being formed
					if(selectedSide != Parent.NONE) {
						// Check if there is a valid terminal node to connect to
						Point.Double globalPos = convertToGlobalSpace(e.getPoint());
						Node node = getNodeAtPoint(globalPos);
						if(node != null && node != selectedNode) {
							// Determine which side of the terminal node to connect to
							int side = getSideAtPoint(node, globalPos);
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
										side = Parent.BOTTOM;
									} else {
										side = Parent.TOP;
									}
								}
							}
							
							// Check if there is already a connection between these two nodes
							for(int i = 0; i < node.parentList.size(); i ++) {
								if(node.parentList.get(i).id.equals(selectedNode.id)) {
									node.parentList.remove(i);
									i --;
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
				// Check which node is currently being hovered over
				Point.Double globalPos = convertToGlobalSpace(e.getPoint());
				Node newHoverNode = getNodeAtPoint(globalPos);
				if(newHoverNode != hoverNode) {
					hoverNode = newHoverNode;
					repaint();
				}
				mousePos = e.getPoint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					// Check how far the mouse was dragged
					int deltaX = e.getX() - mousePos.x;
					int deltaY = e.getY() - mousePos.y;
					
					// Check whether to drag the screen OR the selected node
					if(selectedNode == null) {
						viewPos.x -= deltaX / scale;
						viewPos.y += deltaY / scale;
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
				// Disable hover effect
				if(hoverNode != null) {
					hoverNode = null;
					repaint();
				}
			}
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				// Calculate how much to adjust zoom
				double prevScale = scale;
				scale *= Math.pow(SCALE_RATE, -e.getWheelRotation());
				if(scale > MAX_SCALE) { scale = MAX_SCALE; }
				if(scale < MIN_SCALE) { scale = MIN_SCALE; }

				// Zoom to center
				//viewPos.x -= 0.5*(1/scale - 1/prevScale)*getWidth();
				//viewPos.y += 0.5*(1/scale - 1/prevScale)*getHeight();

				// Zoom to mouse cursor
				viewPos.x -= (1/scale - 1/prevScale)*e.getX();
				viewPos.y += (1/scale - 1/prevScale)*e.getY();

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
		
		// Draw solid background color
		g2d.setColor(BACKGROUND_COLOR);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		// Draw small grid lines at regular intervals
		g2d.setColor(GRID_MINOR_COLOR);
		g2d.setStroke(new BasicStroke(GRID_MINOR_STROKE_SIZE));
		for(double x = (int) (-viewPos.x % GRID_SIZE*scale); x < getWidth(); x += GRID_SIZE*scale) {
			g2d.drawLine((int) x, 0, (int) x, getHeight());
		}
		for(double y = (int) (viewPos.y % GRID_SIZE*scale); y < getHeight(); y += GRID_SIZE*scale) {
			g2d.drawLine(0, (int) y, getWidth(), (int) y);
		}
		
		// Draw large grid lines at x=0 and y=0
		g2d.setColor(GRID_MAJOR_COLOR);
		g2d.setStroke(new BasicStroke(GRID_MAJOR_STROKE_SIZE));
		g2d.drawLine((int) (-viewPos.x*scale), 0, (int) (-viewPos.x*scale), getHeight());
		g2d.drawLine(0, (int) (viewPos.y*scale), getWidth(), (int) (viewPos.y*scale));
		
		// Apply coordinate transform (NOTE: although the global space is flipped vertically compared to the local
		// space, a negative y scale CANNOT be used because this will invert text and icons. Therefore, all y values
		// used below must be inverted.)
		g2d.scale(scale, scale);
		g2d.translate(-viewPos.x, viewPos.y);
		
		// Draw each node and connection
		if(tree != null) {
			for(Node node : tree.getNodeList()) {
				// Draw the icon of this node
				g2d.drawImage(IconManager.BLANK_NODE, (int) node.pos.x, (int) -node.pos.y, (int) (NODE_SIZE*node.scale), (int) (NODE_SIZE*node.scale), null);
				Image icon = IconManager.get(node.icon);
				if(icon != null) {
					g2d.drawImage(icon, (int) node.pos.x, (int) -node.pos.y, (int) (NODE_SIZE*node.scale), (int) (NODE_SIZE*node.scale), null);
				}
				
				// Draw an arrow from this node to each of its parents
				g2d.setColor(CONNECTION_COLOR);
				g2d.setStroke(new BasicStroke(CONNECTION_STROKE_SIZE));
				for(Parent parentInfo : node.parentList) {
					Node parent = tree.getNodeByID(parentInfo.id);
					if(parent != null) {
						Point.Double p1 = getConnectionHandlePos(parent, parentInfo.lineFrom);
						p1.y *= -1;
						Point.Double p2 = getConnectionHandlePos(node, parentInfo.lineTo);
						p2.y *= -1;
						drawArrow(p1, p2, g2d);
					}
				}
				
				// Draw the name of this node above the upper left corner
				g2d.setColor(TEXT_COLOR);
				g2d.drawString(String.format("%s (%d)", LocalizationManager.translate(node.title), tree.getPartList(node).size()), (int) node.pos.x, (int) -node.pos.y - 2);
			}
		}
			
		// Draw arrow for connection in progress
		if(selectedNode != null && selectedSide != Parent.NONE && mousePos != null) {
			Point.Double p1 = getConnectionHandlePos(selectedNode, selectedSide);
			p1.y *= -1;
			Point.Double globalMousePos = convertToGlobalSpace(mousePos);
			globalMousePos.y *= -1;
			drawArrow(p1, globalMousePos, g2d);
		}
		
		// Draw green border around selected node
		if(selectedNode != null) {
			g2d.setColor(SELECTED_NODE_COLOR);
			g2d.setStroke(new BasicStroke(SELECTED_NODE_STROKE_SIZE));
			g2d.drawRect((int) selectedNode.pos.x, (int) -selectedNode.pos.y,
					(int) (NODE_SIZE*selectedNode.scale), (int) (NODE_SIZE*selectedNode.scale));
		}
		
		// Draw four connection handles around hovered node
		if(hoverNode != null) {
			g2d.setColor(CONNECTION_HANDLE_COLOR);
			for(int i = 1; i <= 4; i ++) {
				Point.Double handlePos = getConnectionHandlePos(hoverNode, i);
				g2d.fillOval((int) (handlePos.x - 0.5*CONNECTION_HANDLE_SIZE),
						(int) (-handlePos.y - 0.5*CONNECTION_HANDLE_SIZE),
						(int) CONNECTION_HANDLE_SIZE, (int) CONNECTION_HANDLE_SIZE);
			}
		}
	}
	
	// Returns the global space (tech tree) point corresponding to the given local point (screen space)
	private Point.Double convertToGlobalSpace(Point p) {
		return new Point.Double(viewPos.x + (p.getX() / scale), viewPos.y - (p.getY() / scale));
	}
	
	// Returns the node at a given point, or null
	private Node getNodeAtPoint(Point.Double p) {
		if (tree != null) {
			for(Node node : tree.getNodeList()) {
				if(p.x > node.pos.x - FRINGE_SIZE && p.x < node.pos.x + NODE_SIZE*node.scale + FRINGE_SIZE &&
				   p.y < node.pos.y + FRINGE_SIZE && p.y > node.pos.y - NODE_SIZE*node.scale - FRINGE_SIZE) {
					return node;
				}
			}
		}
		return null;
	}
	
	// Returns which side of the given node is located at the given point, or NONE if none are close
	private int getSideAtPoint(Node node, Point.Double p) {
		for(int i = 1; i <= 4; i ++) {
			Point.Double smallNodePos = getConnectionHandlePos(node, i);
			if(p.x > smallNodePos.x - FRINGE_SIZE && p.x < smallNodePos.x + CONNECTION_HANDLE_SIZE*node.scale + FRINGE_SIZE &&
			   p.y > smallNodePos.y - FRINGE_SIZE && p.y < smallNodePos.y + CONNECTION_HANDLE_SIZE*node.scale + FRINGE_SIZE) {
				return i;
			}
		}
		return Parent.NONE;
	}
	
	
	// Returns the location of the connection handle on the given side of a node
	private Point.Double getConnectionHandlePos(Node node, int side) {
		Point.Double p = new Point.Double(node.pos.x, node.pos.y);
		switch(side) {
		case Parent.TOP:
			p.x += 0.5*NODE_SIZE*node.scale;
			break;
		case Parent.RIGHT:
			p.x += NODE_SIZE*node.scale;
			p.y -= 0.5*NODE_SIZE*node.scale;
			break;
		case Parent.BOTTOM:
			p.x += 0.5*NODE_SIZE*node.scale;
			p.y -= NODE_SIZE*node.scale;
			break;
		case Parent.LEFT:
			p.y -= 0.5*NODE_SIZE*node.scale;
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
