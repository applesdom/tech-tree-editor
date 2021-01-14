package dom.techtree.data;

import java.io.Serializable;

public class Part implements Serializable {
	private static final long serialVersionUID = 4488593681342942995L;
	
	public String name, title, techRequired;
	public double entryCost;
	public boolean techHidden;
}
