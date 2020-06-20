package dom.techtree;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Node {
	// A unique identifier, used for part reqs and parent connections
	public String id;
	
	// The in-game title for this node
	public String title;
	
	// The in-game description for this node
	public String description;
	
	// The science cost to purchase this node (not sure if double or int)
	public double cost;
	
	// Whether this node will be displayed if it has no assigned parts
	public boolean hideEmpty;
	
	// Unknown what this is used for
	public String nodeName;
	
	// Whether this node requires all parents purchased to unlock, or just any
	public boolean anyToUnlock;
	
	// Name of the image to be displayed for this node, either an in-game icon or external filepath
	public String icon;
	
	// Location of this node in the tech tree (positive x is right, positive y is up)
	public Point.Double pos;
	
	// Z-index of this node, used when nodes overlap (maybe?)
	public double zPos;
	
	// Multiplier for node size (at 1.0, size is 64x64)
	public double scale;
	
	// List of parent connections
	public final List<ParentInfo> parentList;
	
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
		parentList = new ArrayList<ParentInfo>();
	}
}
