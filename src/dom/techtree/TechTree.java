package dom.techtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TechTree {
	private final Map<String, Node> nodeMap = new HashMap<String, Node>();
	private final Map<String, PartInfo> partMap = new HashMap<String, PartInfo>();
	
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
	
	public boolean hasNode(String id) {
		return nodeMap.containsKey(id);
	}
	
	public List<Node> getNodeList() {
		return new ArrayList<Node>(nodeMap.values());
	}
	
	public void addPart(String name) {
		partMap.put(name, new PartInfo());
	}
	
	public void addPart(PartInfo part) {
		partMap.put(part.name, part);
	}
	
	public void removePart(String name) {
		partMap.remove(name);
	}
	
	public void removePart(PartInfo part) {
		partMap.remove(part.name);
	}
	
	public PartInfo getPart(String name) {
		PartInfo part = partMap.get(name);
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
	
	public Collection<PartInfo> getPartList() {
		return partMap.values();
	}
	
	public ArrayList<PartInfo> getPartList(Node node) {
		ArrayList<PartInfo> partList = new ArrayList<PartInfo>();
		for (PartInfo part : getPartList()) {
			if (part.techRequired == node.id) {
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
