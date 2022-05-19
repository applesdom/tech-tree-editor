package dom.techtree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LocalizationManager {
	public static Map<String, String> dictionary = new HashMap<String, String>();
	
	public static String translate(String key) {
		if(dictionary.isEmpty()) {
			load();
		}
		
		return dictionary.get(key);
	}
	
	public static boolean hasTranslation(String key) {
		if(dictionary.isEmpty()) {
			load();
		}
		
		return dictionary.containsKey(key);
	}
	
	public static int load() {
		if(Persistent.gameDataDirectory == null) {
			return -1;
		}
		
		File file = new File(Persistent.gameDataDirectory, "Squad/Localization/dictionary.cfg");
		if(!file.exists()) {
			return -1;
		}
		
		dictionary.clear();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			int count = 0;
			String line;
			while((line = br.readLine()) != null) {
				line = line.strip();
				int splitIndex = line.indexOf(" = ");
				if(splitIndex > 0) {
					dictionary.put(line.substring(0, splitIndex), line.substring(splitIndex + 3));
					count ++;
				} else {
					// Ignore malformed lines
				}
			}
			br.close();
			return count;
		} catch(IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
}
