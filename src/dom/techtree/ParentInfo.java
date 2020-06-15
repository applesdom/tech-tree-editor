package dom.techtree;

public class ParentInfo {
	public static final int NONE = 0, TOP = 1, RIGHT = 2, BOTTOM = 3, LEFT = 4;
	
	public String id;
	public int lineTo, lineFrom;
	
	public ParentInfo() {}
	
	public ParentInfo(String id, int lineTo, int lineFrom) {
		this.id = id;
		this.lineTo = lineTo;
		this.lineFrom = lineFrom;
	}
}
