package dom.techtree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class TechTreeIO {
	public static TechTree readAll(File file) throws IOException {
		return readAll(new TechTree(), file);
	}
	
	public static TechTree readAll(TechTree tree, File file) throws IOException {
		if(file.isDirectory()) {
			for(File subfolder : file.listFiles()) {
				tree = readAll(tree, subfolder);
			}
			return tree;
		} else if(file.getName().endsWith(".cfg")) {
			return read(tree, file);
		} else {
			return tree;
		}
	}
	
	public static TechTree read(File in) throws IOException {
		return read(new FileInputStream(in));
	}
	
	public static TechTree read(InputStream in) throws IOException {
		return read(new TechTree(), in);
	}
	
	public static TechTree read(TechTree tree, File in) throws IOException {
		return read(tree, new FileInputStream(in));
	}
	public static TechTree read(TechTree tree, InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		while(true) {
			// Read a line and extract a token (ignore comment)
			String line = br.readLine();
			if(line == null) break;
			if(line.equals("//")) continue;
			String token = line.split("//")[0].trim();
			
			// Remove leading '﻿' from token if present (does not affect parsing)
			if(token.startsWith("﻿") && token.length() > 3) {
				token = token.substring(3);
			}
			
			if(token.equals("TechTree") || token.equals("@TechTree")) {
				br.readLine();	// Consume opening bracket
				
				// Check each line for a RDNode definition
				while(true) {
					// Parse each RDNode definition
					line = br.readLine();
					if(line == null) break;
					if(line.equals("//")) continue;
					token = line.split("//")[0].trim();
					
					// Determine whether this token creates a new Node or modifies an existing or ends the tree
					Node node;
					if(token.startsWith("RDNode")) {
						node = new Node();
					} else if(token.startsWith("@RDNode:HAS[#id:[")) {
						// Parse id from token
						int endIndex = token.indexOf(']');
						String id;
						if(endIndex < 0) {
							id = token.substring(17);
						} else {
							id = token.substring(17, endIndex);
						}
						node = tree.getNodeByID(id);
						if(node == null) {
							continue;
						} else {
							// Remove the node temporarily, in case id is changed
							// It will be added back with updated name later
							tree.removeNode(node);
						}
					} else if(token.equals("}")) {
						// End of node definition
						break;
					} else {
						// Unrecognized token
						continue;
					}
					
					parseNode(node, br);
					
					tree.addNode(node);
				}
			} else if(token.equals("PART") || token.startsWith("@PART[")) {
				// Determine whether this definition creates a new Part or modifies an existing
				PartInfo part;
				if(token.equals("PART")) {
					part = new PartInfo();
				} else {
					// Parse name from token
					String name;
					int endIndex = token.indexOf(']');
					if(endIndex < 0) {
						name = token.substring(6);
					} else {
						name = token.substring(6, endIndex);
					}
					part = tree.getPart(name);
					if(part == null) {
						// New part definition
						part = new PartInfo();
						part.name = name;
					}
				}
				
				parsePart(part, br);
				
				// Exclude parts with no in-game title (eg kerbalEVA)
				if(part.title != null) {
					tree.addPart(part);
				}
			}
		}
		return tree;
	}
	
	// Helper methods for reading the tech tree
	private static Node parseNode(Node node, BufferedReader br) throws IOException {
		br.readLine();	// Consume opening bracket
		
		while(true) {
			// Read a line and extract a token (ignore comment)
			String line = br.readLine();
			if(line == null) break;
			if(line.equals("//")) continue;
			String token = line.split("//")[0].trim();
			
			// Read a field/value pair OR parse a parent
			String field, value;
			if(token.contains("=")) {
				String[] split = token.split("=");
				if(split.length < 2) {
					continue;
				} else {
					field = split[0].trim();
					value = split[1].trim();
				}
			} else if(token.equals("-Parent,* {}")) {
				node.parentList.clear();
				continue;
			} else if(token.equals("Parent")) {
				ParentInfo parent = parseParent(new ParentInfo(), br);
				node.parentList.add(parent);
				continue;
			} else if(token.equals("}")) {
				// End of node has been reached
				break;
			} else {
				// Unrecognized token
				continue;
			}
			
			// Remove leading '@' from field if present (does not affect parsing)
			if(field.charAt(0) == '@' && field.length() > 1) {
				field = field.substring(1);
			}
			
			try {
				switch(field) {
				case "id":
					node.id = value;
					break;
				case "title":
					node.title = value;
					break;
				case "description":
					node.description = value;
					break;
				case "cost":
					node.cost = Double.parseDouble(value);
					break;
				case "hideEmpty":
					node.hideEmpty = Boolean.parseBoolean(value);
					break;
				case "nodeName":
					node.nodeName = value;
					break;
				case "anyToUnlock":
					node.anyToUnlock = Boolean.parseBoolean(value);
					break;
				case "icon":
					node.icon = value;
					break;
				case "pos":
					String[] split = value.split(",");
					node.pos.x = Double.parseDouble(split[0].trim());
					node.pos.y = Double.parseDouble(split[1].trim());
					node.zPos = Double.parseDouble(split[2].trim());
					break;
				case "scale":
					node.scale = Double.parseDouble(value);
					break;
				default:
					// Unrecognized field/value pair, do nothing
				}
			} catch(Exception e) {
				// Bad number or boolean format, do nothing
			}
		}
		
		return node;
	}
	private static ParentInfo parseParent(ParentInfo parent, BufferedReader br) throws IOException {
		br.readLine();	// Consume opening bracket
		
		// Read parent fields
		parent.id = br.readLine().split("//")[0].trim().substring(11);
		parent.lineFrom = parseSide(br.readLine().split("//")[0].trim().substring(11));
		parent.lineTo = parseSide(br.readLine().split("//")[0].trim().substring(9));
		
		br.readLine();	// Consume closing bracket
		return parent;
	}
	private static PartInfo parsePart(PartInfo part, BufferedReader br) throws IOException {
		br.readLine();	// Consume opening bracket
		
		while(true) {
			// Read a line and extract a token (ignore comment)
			String line = br.readLine();
			if(line == null) break;
			if(line.equals("//")) continue;
			String token = line.split("//")[0].trim();
			
			// Read a field/value pair
			String field, value;
			if(token.contains("=")) {
				String[] split = token.split("=");
				if(split.length < 2) {
					continue;
				} else {
					field = split[0].trim();
					value = split[1].trim();
				}
			} else if(token.equals("}")) {
				break;		// Conclude this Part definition
			} else if(token.endsWith("{")) {
				// Sub-definition found, skip forward until back to PART def
				int layer = 1;
				while(layer > 0) {
					line = br.readLine();
					if(line == null) break;
					if(line.equals("//")) continue;
					token = line.split("//")[0].trim();
					if(token.equals("}")) {
						layer --;
					} else if(token.equals("{")) {
						layer ++;
					}
				}
				continue;
			} else {
				continue;	// Unrecognized token, skip to next line
			}
			
			// Remove leading '@' from field if present (does not affect parsing)
			if(field.charAt(0) == '@' && field.length() > 1) {
				field = field.substring(1);
			}
			
			try {
				switch(field) {
				case "name":
					part.name = value;
					break;
				case "title":
					part.title = value;
					break;
				case "TechRequired":
					part.techRequired = value;
					break;
				case "entryCost":
					part.entryCost = Double.parseDouble(value);
					break;
				case "TechHidden":
					part.techHidden = Boolean.parseBoolean(value);
					break;
				default:
					// Unrecognized field/value pair, do nothing
				}
			} catch(Exception e) {
				// Bad number or boolean format, do nothing
			}
		}
		
		return part;
	}
	private static int parseSide(String side) {
		switch(side) {
		case "TOP":
			return ParentInfo.TOP;
		case "RIGHT":
			return ParentInfo.RIGHT;
		case "BOTTOM":
			return ParentInfo.BOTTOM;
		case "LEFT":
			return ParentInfo.LEFT;
		case "NONE":
		default:
			return ParentInfo.NONE;
		}
	}
	
	// Converts side enums into strings
	private static String sideToString(int side) {
		switch(side) {
		case ParentInfo.TOP:
			return "TOP";
		case ParentInfo.RIGHT:
			return "RIGHT";
		case ParentInfo.BOTTOM:
			return "BOTTOM";
		case ParentInfo.LEFT:
			return "LEFT";
		default:
			return "NONE";
		}
	}
	
	public static void write(TechTree tree, TechTree base, File file) throws FileNotFoundException {
		write(tree, base, new FileOutputStream(file));
	}
	
	// Writes a ModuleManager definition that can be loaded by KSP to produce the given tech tree
	public static void write(TechTree tree, TechTree base, OutputStream out) {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
		pw.println("TechTree");
		pw.println("{");
		
		// Write all removed nodes
		for(Node baseNode : base.getNodeList()) {
			if(!tree.hasNode(baseNode.id)) {
				pw.printf("\t-RDNode:HAS[#id[%s]]\n");
			}
		}
		
		for(Node node : tree.getNodeList()) {
			Node baseNode = base.getNodeByID(node.id);
			
			if(baseNode == null) {
				pw.println("\tRDNode");
				pw.println("\t{");
				pw.printf("\t\tid = %s\n", node.id);
				pw.printf("\t\ttitle = %s\n", node.title);
				pw.printf("\t\tdescription = %s\n", node.description);
				pw.printf("\t\tcost = %f\n", node.cost);
				pw.printf("\t\thideEmpty = %s\n", node.hideEmpty ? "True" : "False");
				pw.printf("\t\tnodeName = %s\n", node.nodeName);
				pw.printf("\t\tanyToUnlock = %s\n", node.anyToUnlock ? "True" : "False");
				pw.printf("\t\ticon = %s\n", node.icon);
				pw.printf("\t\tpos = %f,%f,%f\n", node.pos.x, node.pos.y, node.zPos);
				pw.printf("\t\tscale = %f\n", node.scale);
				for(ParentInfo parent : node.parentList) {
					pw.println("\t\tParent");
					pw.println("\t\t{");
					pw.printf("\t\t\tparentID = %s\n", parent.id);
					pw.printf("\t\t\tlineFrom = %s\n", sideToString(parent.lineFrom));
					pw.printf("\t\t\tlineTo = %s\n", sideToString(parent.lineTo));
					pw.println("\t\t}");
				}
				pw.println("\t}");
			} else {
				// Check for any difference in fields
				boolean fieldDifference = !node.title.equals(baseNode.title)
						|| !node.description.equals(baseNode.description)
						|| node.cost != baseNode.cost
						|| node.hideEmpty != baseNode.hideEmpty
						|| !node.nodeName.equals(baseNode.nodeName)
						|| node.anyToUnlock != baseNode.anyToUnlock
						|| !node.icon.equals(baseNode.icon)
						|| node.pos.x != baseNode.pos.x
						|| node.pos.y != baseNode.pos.y
						|| node.zPos != baseNode.zPos
						|| node.scale != baseNode.scale;
				
				// Check for any difference in parent lists
				boolean parentDifference = false;
				if(node.parentList.size() == baseNode.parentList.size()) {
					for(ParentInfo parent : node.parentList) {
						boolean match = false;
						for(ParentInfo baseParent : baseNode.parentList) {
							if(parent.id.equals(baseParent.id)) {
								match = true;
								if(parent.lineTo != baseParent.lineTo || parent.lineFrom != baseParent.lineFrom) {
									parentDifference = true;
								}
								break;
							}
						}
						if(!match) {
							parentDifference = true;
							break;
						}
					}
				} else {
					parentDifference = true;
				}
				
				if(fieldDifference || parentDifference) {
					pw.printf("\t@RDNode:HAS[#id[%s]]\n", node.id);
					pw.println("\t{");
					if(!node.title.equals(baseNode.title)) {
						pw.printf("\t\t@title = %s\n", node.title);
					}
					if(!node.description.equals(baseNode.description)) {
						pw.printf("\t\t@description = %s\n", node.description);
					}
					if(node.cost != baseNode.cost) {
						pw.printf("\t\t@cost = %f\n", node.cost);
					}
					if(node.hideEmpty != baseNode.hideEmpty) {
						pw.printf("\t\t@hideEmpty = %s\n", node.hideEmpty ? "True" : "False");
					}
					if(!node.nodeName.equals(baseNode.nodeName)) {
						pw.printf("\t\t@nodeName = %s\n", node.nodeName);
					}
					if(node.anyToUnlock != baseNode.anyToUnlock) {
						pw.printf("\t\t@anyToUnlock = %s\n", node.anyToUnlock ? "True" : "False");
					}
					if(!node.icon.equals(baseNode.icon)) {
						pw.printf("\t\t@icon = %s\n", node.icon);
					}
					if(node.pos.x != baseNode.pos.x || node.pos.y != baseNode.pos.y || node.zPos != baseNode.zPos) {
						pw.printf("\t\t@pos = %f,%f,%f\n", node.pos.x, node.pos.y, node.zPos);
					}
					if(node.scale != baseNode.scale) {
						pw.printf("\t\t@scale = %f\n", node.scale);
					}
					
					if(parentDifference) {
						pw.println("\t\t-Parent,* {}");
						for(ParentInfo parent : node.parentList) {
							pw.println("\t\tParent");
							pw.println("\t\t{");
							pw.printf("\t\t\tparentfID = %s\n", parent.id);
							pw.printf("\t\t\tlineFrom = %s\n", sideToString(parent.lineFrom));
							pw.printf("\t\t\tlineTo = %s\n", sideToString(parent.lineTo));
							pw.println("\t\t}");
						}
					}
					pw.println("\t}");
				}
			}
		}
		
		pw.println("}");
		
		for(PartInfo part : tree.getPartList()) {
			PartInfo basePart = base.getPart(part.name);
			
			boolean fieldDifference = basePart == null
					|| !part.techRequired.equals(basePart.techRequired)
					|| part.entryCost != basePart.entryCost
					|| part.techHidden != basePart.techHidden;
			
			if(fieldDifference) {
				pw.printf("@PART[#id[%s]]\n");
				pw.println("{");
				if(!part.techRequired.equals(basePart.techRequired)) {
					pw.printf("\tTechRequired = %s\n", part.techRequired);
				}
				if(part.entryCost != basePart.entryCost) {
					pw.printf("\tentryCost = %f\n", part.entryCost);
				}
				if(part.techHidden != basePart.techHidden) {
					pw.printf("\tTechHidden = %s\n", part.techHidden ? "True" : "False");
				}
				pw.println("}");
			}
		}
		
		pw.close();
	}
}
