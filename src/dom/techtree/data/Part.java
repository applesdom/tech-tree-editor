package dom.techtree.data;

import java.io.Serializable;

public class Part implements Serializable {
	private static final long serialVersionUID = 4488593681342942995L;
	
	public String  name;         // Unique identifier
	public String  title;        // In-game title
	public String  techRequired; // ID of containing tech node
	public double  entryCost;    // Funds cost to purchase
	public boolean techHidden;   // If true, part will NOT appear in R&D or editor
}
