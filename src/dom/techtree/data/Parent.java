package dom.techtree.data;

public class Parent {
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
