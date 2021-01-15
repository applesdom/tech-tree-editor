package dom.techtree;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
	
	// Returns an icon associated with the given internal name or file path, or null if none found
	public static Image get(String name) {
		if(imgMap.isEmpty()) {
			load();
		}
		
		if(aliasMap.isEmpty()) {
			loadAliasMap();
		}
		
		String alias = aliasMap.get(name);
		if(alias == null) {
			alias = name;
		}
		
		Image img = imgMap.get(alias);
		if(img == null ) {
			File file = new File(Persistent.gameDataDirectory, alias + ".png");
			try {
				return ImageIO.read(file);
			} catch(Exception e) {
				System.out.printf("IconManager: Couldn\'t find icon: %s\n", alias + ".png");
				return null;
			}
		} else {
			return img;
		}
	}
	
	public static int load() {
		if(Persistent.gameDataDirectory == null) {
			return -1;
		}
		
		File dir = new File(Persistent.gameDataDirectory, "Squad/PartList/SimpleIcons");
		if(!dir.exists() || !dir.isDirectory()) {
			return -1;
		}
		
		imgMap.clear();
		
		int count = 0;
		for(File file : dir.listFiles()) {
			if(!file.getName().endsWith(".png")) {
				continue;
			}
			String relative = Persistent.gameDataDirectory.toPath().relativize(file.toPath()).toString();
			String trimmed = relative.substring(0, relative.length() - 4);
			try {
				imgMap.put(trimmed, ImageIO.read(file));
				count ++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
	
	private static void loadAliasMap() {
		BufferedReader br = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream(STOCK_ICON_MAPPING_FILE_NAME)));
		try {
			String line;
			while((line = br.readLine()) != null) {
				String[] lineSplit = line.split(",");
				aliasMap.put(lineSplit[0], lineSplit[1]);
			}
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
