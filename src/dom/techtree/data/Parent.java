package dom.techtree.data;

import java.io.Serializable;

public class Parent implements Serializable {
	private static final long serialVersionUID = -1815542288964964747L;

	public static final int NONE = 0, TOP = 1, RIGHT = 2, BOTTOM = 3, LEFT = 4;
	
	public String id;       // ID of parent tech node
	public int    lineTo;   // Which side to connect to on parent
	public int    lineFrom; // Which side to connect to on child
	
	public Parent() {}
	
	public Parent(String id, int lineTo, int lineFrom) {
		this.id = id;
		this.lineTo = lineTo;
		this.lineFrom = lineFrom;
	}
}
