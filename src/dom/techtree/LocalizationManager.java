package dom.techtree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LocalizationManager {
	public static String translate(String key) {
		return Persistent.locTranslationMap.get(key);
	}
	
	public static boolean hasTranslation(String key) {
		return Persistent.locTranslationMap.containsKey(key);
	}
	
	public static int readLocalizationFile(File file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			int count = 0;
			String line;
			while((line = br.readLine()) != null) {
				line = line.strip();
				int splitIndex = line.indexOf(" = ");
				if(splitIndex > 0) {
					Persistent.locTranslationMap.put(line.substring(0, splitIndex), line.substring(splitIndex + 3));
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
	
	public static void clear() {
		Persistent.locTranslationMap.clear();
	}
}
