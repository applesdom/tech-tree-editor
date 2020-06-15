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
	public static final String STOCK_ICON_MAPPING_FILE_NAME = "/stock_icon_mapping.csv",
							   STOCK_ICON_DIR_NAME = "/stock_icon";
	public static final Image BLANK_NODE = new ImageIcon(IconManager.class.getResource("/node.png")).getImage(),
							  BLANK_SELECTED_NODE = new ImageIcon(IconManager.class.getResource("/node_selected.png")).getImage(),
							  LOGO = new ImageIcon(IconManager.class.getResource("/logo.png")).getImage();
	
	private static Map<String, Image> imgMap = new HashMap<String, Image>();
	
	// Returns an icon associated with the given internal name (or file path)
	public static Image get(String name) {
		if(imgMap.isEmpty()) {
			readImages();
		}
		
		Image image;
		if((image = imgMap.get(name)) != null) {
			return image;
		}
		
		File file = new File("/" + name + ".png");
		try {
			return ImageIO.read(file);
		} catch(FileNotFoundException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			System.out.println(file.getPath());
			//e.printStackTrace();
		}
		
		return null;
	}
	
	// Populates the imgMap, runs once the first time an image is requested
	// The internal names of stock icons do not exactly match the file names, so
	// a hand-made .csv is read to provide the mapping.
	private static void readImages() {
		// Load stock icon name mappings from predefined .csv file
		BufferedReader br = new BufferedReader(new InputStreamReader(
				Main.class.getResourceAsStream(STOCK_ICON_MAPPING_FILE_NAME)));
		String line;
		try {
			// For each line in the .csv, add an entry to the imgMap
			while((line = br.readLine()) != null) {
				String[] lineSplit = line.split(",");
				// 1st string is internal name of icon, 2nd string is file name
				URL url = Main.class.getResource(STOCK_ICON_DIR_NAME + "/" + lineSplit[1] + ".png");
				if(url != null) {
					imgMap.put(lineSplit[0], new ImageIcon(url).getImage());
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
