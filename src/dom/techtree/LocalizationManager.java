package dom.techtree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LocalizationManager {
	private static Map<String, String> translationMap = new HashMap<String, String>();
	
	public static String translate(String key) {
		return translationMap.get(key);
	}
	
	public static boolean hasTranslation(String key) {
		return translationMap.containsKey(key);
	}
	
	public static void readLocalizationFile(File file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			String line;
			while((line = br.readLine()) != null) {
				line = line.strip();
				int splitIndex = line.indexOf(" = ");
				if(splitIndex > 0) {
					translationMap.put(line.substring(0, splitIndex), line.substring(splitIndex + 3));
				} else {
					// Ignore malformed lines
				}
				
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
