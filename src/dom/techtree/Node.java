package dom.techtree;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Node {
	public String id, title, description, nodeName, icon;
	public int cost;
	public boolean hideEmpty, anyToUnlock;
	public Point.Double pos;
	public double scale, zPos;
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
