package dom.techtree;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class IconManager {
	public static final Image BLANK_NODE = new ImageIcon(IconManager.class.getResource("/node.png")).getImage(),
							  BLANK_SELECTED_NODE = new ImageIcon(IconManager.class.getResource("/node_selected.png")).getImage(),
							  LOGO = new ImageIcon(IconManager.class.getResource("/logo.png")).getImage();
	public static final String STOCK_ICON_MAPPING_FILE_NAME = "/stock_icon_mapping.csv";
	
	private static Map<String, Image> imgMap = new HashMap<String, Image>();
	private static Map<String, String> aliasMap = new HashMap<String, String>();
	
	// Returns an icon associated with the given internal name (or file path)
	public static Image get(String name) {
		if(aliasMap.isEmpty()) {
			readAliases();
		}
		
		String alias = aliasMap.get(name);
		if(alias == null) {
			alias = name;
		}
		
		Image image;
		if((image = imgMap.get(alias)) != null) {
			return image;
		}
		
		File file = new File("/" + name + ".png");
		try {
			return ImageIO.read(file);
		} catch(FileNotFoundException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Couldn\'t find image: " + name);
			//e.printStackTrace();
		}
		
		return null;
	}

	private static void readAliases() {
		// Load stock icon name mappings from predefined .csv file
		BufferedReader br = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream(STOCK_ICON_MAPPING_FILE_NAME)));
		String line;
		try {
			// For each line in the .csv, add an entry to the imgMap
			while((line = br.readLine()) != null) {
				String[] lineSplit = line.split(",");
				// 1st string is internal name of icon, 2nd string is file name
				aliasMap.put(lineSplit[0], lineSplit[1]);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int readIconDirectory(File dir) {
		if(!dir.exists() || !dir.isDirectory()) {
			return -1;
		}
		int count = 0;
		for(File file : dir.listFiles()) {
			if(!file.getName().endsWith(".png")) {
				continue;
			}
			try {
				String trimmedName = file.getName().substring(0, file.getName().length() - 4);
				imgMap.put(trimmedName, ImageIO.read(file));
				count ++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
	
	public static void clear() {
		imgMap.clear();
	}
}
