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
		// Consume lines until "TechTree" token found
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = "";
		while(!line.trim().startsWith("TechTree") && !line.trim().startsWith("﻿TechTree")) {
			line = br.readLine();
			if(line == null) {
				return tree;
			}
		}
		
		// Consume opening parenthesis
		br.readLine();
		
		// Parse each node definition
		while(br.readLine().trim().startsWith("RDNode")) {
			// Consume opening parenthesis
			br.readLine();
			
			// Create a new node and set attributes
			Node node = new Node();
			node.id = parseFieldValuePair(br.readLine())[1];
			node.title = parseFieldValuePair(br.readLine())[1];
			node.description = parseFieldValuePair(br.readLine())[1];
			node.cost = Integer.parseInt(parseFieldValuePair(br.readLine())[1]);
			node.hideEmpty = Boolean.parseBoolean(parseFieldValuePair(br.readLine())[1]);
			node.nodeName = parseFieldValuePair(br.readLine())[1];
			node.anyToUnlock = Boolean.parseBoolean(parseFieldValuePair(br.readLine())[1]);
			node.icon = parseFieldValuePair(br.readLine())[1];
			String[] posSplit = parseFieldValuePair(br.readLine())[1].split(",");
			node.pos.x = Double.parseDouble(posSplit[0]);
			node.pos.y = Double.parseDouble(posSplit[1]);
			node.zPos = Double.parseDouble(posSplit[2]);
			node.scale = Double.parseDouble(parseFieldValuePair(br.readLine())[1]);
			
			// Read parent data for this node
			while(br.readLine().trim().startsWith("Parent")) {
				// Consume opening parenthesis
				br.readLine();
				
				// Read parent attributes
				ParentInfo parent = new ParentInfo();
				parent.id = parseFieldValuePair(br.readLine())[1];
				parent.lineFrom = parseSide(parseFieldValuePair(br.readLine())[1]);
				parent.lineTo = parseSide(parseFieldValuePair(br.readLine())[1]);
				
				node.parentList.add(parent);
				
				// Consume closing parenthesis
				br.readLine();
			}
			
			tree.addNode(node);
			
			// Closing parenthesis already consumed by parent loop
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
	
	// Takes a string with format "  field = value //Comment" and converts it to [field, value]
	// Returns null if failed
	private static String[] parseFieldValuePair(String line) {
		try {
			String noComment = line.split("//")[0].trim();
			String[] fieldValue = noComment.split("=");
			return new String[] {fieldValue[0].trim(), fieldValue[1].trim()};
		} catch(ArrayIndexOutOfBoundsException e) {
			//e.printStackTrace();
			return null;
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
