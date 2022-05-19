package dom.techtree.data;

import java.io.Serializable;

public class Parent implements Serializable {
	private static final long serialVersionUID = -1815542288964964747L;

	public enum Side {TOP, RIGHT, BOTTOM, LEFT};
	
	public String id;       // ID of parent tech node
	public Side   lineTo;   // Which side to connect to on parent
	public Side   lineFrom; // Which side to connect to on child

	public Parent() {}
	
	public Parent(String id, Side lineTo, Side lineFrom) {
		this.id = id;
		this.lineTo = lineTo;
		this.lineFrom = lineFrom;
	}
}
