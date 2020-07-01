package dom.techtree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LocalizationManager {
	public static final String LOCALIZATION_FILE_NAME = "/dictionary.cfg";
	
	private static Map<String, String> translationMap = new HashMap<String, String>();
	
	public static String translate(String key) {
		if(translationMap.isEmpty()) {
			readLocalizationFile();
		}
		return translationMap.get(key);
	}
	
	public static boolean hasTranslation(String key) {
		if(translationMap.isEmpty()) {
			readLocalizationFile();
		}
		return translationMap.containsKey(key);
	}
	
	private static void readLocalizationFile() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					LocalizationManager.class.getResourceAsStream(LOCALIZATION_FILE_NAME)));
			
			String line;
			while((line = br.readLine()) != null) {
				String[] lineSplit = line.split(" = ");
				translationMap.put(lineSplit[0], lineSplit[1]);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
