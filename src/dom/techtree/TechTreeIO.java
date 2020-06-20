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
		String line, token;
		
		// Parse each TechTree or part definition
		while(true) {
			line = br.readLine();
			if(line == null) {
				break;
			}
			token = line.split("//")[0].trim();
			
			if(token.startsWith("TechTree") ||
			   token.startsWith("﻿TechTree") ||
			   token.startsWith("@Techtree")) {
				// Consume opening bracket
				br.readLine();
				
				// Parse each RDNode definition
				while(true) {
					line = br.readLine();
					if(line == null) {
						break;
					}
					token = line.split("//")[0].trim();
					
					// Determine whether this definition creates a new Node or modifies an existing
					Node node;
					if(token.startsWith("RDNode")) {
						node = new Node();
					} else if(token.startsWith("@RDNode:HAS[#id:[")) {
						// Parse name from token
						int endIndex = token.indexOf(']');
						String id;
						if(endIndex < 0) {
							id = token.substring(17);
						} else {
							id = token.substring(17, endIndex);
						}
						node = tree.getNodeByID(id);
						if(node == null) {
							System.out.printf("Invalid node id: %s\n", id);
							continue;
						}
						tree.removeNode(node);
					} else if(token.equals("}")) {
						break;
					} else {
						System.out.printf("Unrecognized token: %s\n", token);
						continue;
					}
					
					System.out.println("Parsing node: " + node.id);
					
					// Consume opening bracket
					br.readLine();
					
					// Parse each field for this Node
					while(true) {
						line = br.readLine();
						if(line == null) {
							break;
						}
						token = line.split("//")[0].trim();
						
						// Read a field/value pair for the node
						String field, value;
						if(token.contains("=")) {
							field = token.split("=")[0].trim();
							value = token.split("=")[1].trim();
						} else if(token.equals("}")) {
							break;
						} else {
							field = "";
							value = token.split("=")[0].trim();
						}
						
						// Set the field of the node accordionly
						try {
							switch(field) {
							case "id":
							case "@id":
								node.id = value;
								break;
							case "title":
							case "@title":
								node.title = value;
								break;
							case "description":
							case "@description":
								node.description = value;
								break;
							case "cost":
							case "@cost":
								node.cost = Double.parseDouble(value);
								break;
							case "hideEmpty":
							case "@hideEmpty":
								node.hideEmpty = Boolean.parseBoolean(value);
								break;
							case "nodeName":
							case "@nodeName":
								node.nodeName = value;
								break;
							case "anyToUnlock":
							case "@anyToUnlock":
								node.anyToUnlock = Boolean.parseBoolean(value);
								break;
							case "icon":
							case "@icon":
								node.icon = value;
								break;
							case "pos":
							case "@pos":
								String[] split = value.split(",");
								node.pos.x = Double.parseDouble(split[0]);
								node.pos.y = Double.parseDouble(split[1]);
								node.zPos = Double.parseDouble(split[2]);
								break;
							case "scale":
							case "@scale":
								node.scale = Double.parseDouble(value);
								break;
							case "":
								switch(value) {
								case "-Parent,* {}":
									node.parentList.clear();
									break;
								case "Parent":
									// Consume opening bracket
									br.readLine();
									
									// Read parent fields
									ParentInfo parent = new ParentInfo();
									parent.id = br.readLine().split("//")[0].trim().substring(11);
									parent.lineFrom = parseSide(br.readLine().split("//")[0].trim().substring(11));
									parent.lineTo = parseSide(br.readLine().split("//")[0].trim().substring(9));
									
									node.parentList.add(parent);
									
									// Consume closing bracket
									br.readLine();
									break;
								default:
									System.out.printf("Unrecognized value pair: %s\n", value);
								}
								break;
							default:
								System.out.printf("Unrecognized field/value pair: %s/%s\n", field, value);
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
					
					tree.addNode(node);
				}
			} else if(token.startsWith("Part") ||
					  token.startsWith("@Part")) {
				// Determine whether this definition creates a new Part or modifies an existing
				PartInfo part;
				if(token.startsWith("Part")) {
					part = new PartInfo();
				} else if(token.startsWith("@Part[")) {
					// Parse name from token
					int endIndex = token.indexOf(']');
					String name;
					if(endIndex < 0) {
						name = token.substring(6);
					} else {
						name = token.substring(6, endIndex);
					}
					part = tree.getPart(name);
					if(part == null) {
						System.out.printf("Invalid part name: %s\n", name);
						continue;
					}
				} else if(token.equals("}")) {
					break;
				} else {
					System.out.printf("Unrecognized token: %s\n", token);
					continue;
				}
				
				// Consume opening bracket
				br.readLine();
				
				// Parse each field for this Part
				while(true) {
					line = br.readLine();
					if(line == null) {
						break;
					}
					token = line.split("//")[0].trim();
					
					// Read a field/value pair
					String field, value;
					if(token.contains("=")) {
						field = token.split("=")[0].trim();
						value = token.split("=")[1].trim();
					} else if(token.equals("}")) {
						break;
					} else {
						field = "";
						value = token.split("=")[0].trim();
					}
					
					// Set the field of the node accordionly
					try {
						switch(field) {
						case "name":
						case "@name":
							part.name = value;
							break;
						case "techRequired":
						case "@techRequired":
							part.techRequired = value;
							break;
						case "entryCost":
						case "@entryCost":
							part.entryCost = Double.parseDouble(value);
							break;
						case "techHidden":
						case "@techHidden":
							part.techHidden = Boolean.parseBoolean(value);
							break;
						default:
							System.out.printf("Unrecognized field/value pair: %s/%s\n", field, value);
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				
				tree.addPart(part);
			}
		}
		return tree;
	}
	
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
			System.out.println(file.getAbsolutePath());
			return read(tree, file);
		} else {
			return tree;
		}
	}
	
	// Converts the strings "TOP", "RIGHT", "BOTTOM", and "LEFT" into corresponding enums
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
	
	public static void write(TechTree tree, File file) throws FileNotFoundException {
		write(tree, new FileOutputStream(file));
	}
	
	// Writes a ModuleManager definition that can be loaded by KSP to produce the given tech tree
	// TODO Only write the necessary nodes to create the tree
	public static void write(TechTree tree, OutputStream out) {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
		pw.println("-TechTree{}");
		pw.println("TechTree");
		pw.println("{");
		
		for(Node node : tree.getNodeList()) {
			pw.println("\tRDNode");
			pw.println("\t{");
			pw.printf("\t\tid = %s\n", node.id);
			pw.printf("\t\ttitle = %s\n", node.title);
			pw.printf("\t\tdescription = %s\n", node.description);
			pw.printf("\t\tcost = %d\n", node.cost);
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
		}
		
		pw.println("}");
	}
}
