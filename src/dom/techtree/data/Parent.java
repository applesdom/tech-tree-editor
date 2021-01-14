package dom.techtree.data;

import java.io.Serializable;

public class Parent implements Serializable {
	private static final long serialVersionUID = -1815542288964964747L;

	public static final int NONE = 0, TOP = 1, RIGHT = 2, BOTTOM = 3, LEFT = 4;
	
	public String id;
	public int lineTo, lineFrom;
	
	public Parent() {}
	
	public Parent(String id, int lineTo, int lineFrom) {
		this.id = id;
		this.lineTo = lineTo;
		this.lineFrom = lineFrom;
	}
}
