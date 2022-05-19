package dom.techtree.data;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
	private static final long serialVersionUID = -744513963369870594L;

	public String       id;	         // Unique identifier, for part reqs and parent connections
	public String       title;       // In-game title
	public String       description; // In-game description
	public double       cost;        // Science cost to research (TODO: Find out if double or int)
	public boolean      hideEmpty;   // If true, node will NOT appear in-game if no parts are assigned
	public String       nodeName;    // Unknown usage
	public boolean      anyToUnlock; // If true, only one parent must be researched to unlock this node
	public String       icon;        // Image to display, refer to stock icon mappings for aliases
	public Point.Double pos;         // Position, +x is right, +y is up, center is near (-2000, 1200)
	public double       zPos;        // Z component of position (TODO: Find out if overlapping is supported)
	public double       scale;       // Size multiplier, stock tree defaults to 0.6

	public final List<Parent> parentList; // List of parent connections
	
	public Node() {
		id= "newNode";
		title = "New Node";
		description = "Description goes here...";
		cost = 1;
		hideEmpty = false;
		nodeName = "node0_new";
		anyToUnlock = false;
		icon = "RDicon_start";
		pos = new Point.Double(0, 0);
		zPos = 0;
		scale = 0.6;
		parentList = new ArrayList<Parent>();
	}
}
