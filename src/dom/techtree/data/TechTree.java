package dom.techtree.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TechTree implements Serializable {
	private static final long serialVersionUID = 3688113512358774621L;
	
	private final Map<String, Node> nodeMap = new HashMap<String, Node>();
	private final Map<String, Part> partMap = new HashMap<String, Part>();
	
	public void addNode(String id) {
		nodeMap.put(id, new Node());
	}
	
	public void addNode(Node node) {
		nodeMap.put(node.id, node);
	}
	
	public void removeNode(String id) {
		nodeMap.remove(id);
	}
	
	public void removeNode(Node node) {
		nodeMap.remove(node.id);
	}
	
	public Node getNodeByID(String id) {
		Node node = nodeMap.get(id);
		if(node == null || node.id.equals(id)) {
			return node;
		} else {
			removeNode(node);
			addNode(node);
			return null;
		}
	}
	
	public int getNodeCount() {
		return nodeMap.size();
	}
	
	public boolean hasNode(String id) {
		return nodeMap.containsKey(id);
	}
	
	public List<Node> getNodeList() {
		return new ArrayList<Node>(nodeMap.values());
	}
	
	public void addPart(String name) {
		partMap.put(name, new Part());
	}
	
	public void addPart(Part part) {
		partMap.put(part.name, part);
	}
	
	public void removePart(String name) {
		partMap.remove(name);
	}
	
	public void removePart(Part part) {
		partMap.remove(part.name);
	}
	
	public Part getPart(String name) {
		Part part = partMap.get(name);
		if(part == null) {
			return null;
		} else if(part.name.equals(name)) {
			return part;
		} else {
			removePart(part);
			addPart(part);
			return null;
		}
	}
	
	public int getPartCount() {
		return partMap.size();
	}
	
	public Collection<Part> getPartList() {
		return partMap.values();
	}
	
	public ArrayList<Part> getPartList(Node node) {
		ArrayList<Part> partList = new ArrayList<Part>();
		for (Part part : getPartList()) {
			if (part.techRequired.equals(node.id)) {
				partList.add(part);
			}
		}
		return partList;
	}
	
	public ArrayList<Part> getPartList(String nodeID) {
		ArrayList<Part> partList = new ArrayList<Part>();
		for (Part part : getPartList()) {
			if (part.techRequired.equals(nodeID)) {
				partList.add(part);
			}
		}
		return partList;
	}
	
	public void clear() {
		nodeMap.clear();
		partMap.clear();
	}
}
