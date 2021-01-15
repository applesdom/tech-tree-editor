package dom.techtree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dom.techtree.data.TechTree;

public class Persistent {
	private static final File PERSISTENT_FILE = new File(System.getProperty("user.home") + "/.techtreeeditor/persistent.dat");
	
	// All fields below MUST be serializable. They will automagically be saved via reflection.
	public static TechTree currentTree;
	public static File gameDataDirectory;
	
	public static void save() {
		File file = PERSISTENT_FILE;

		try {
			if(!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			List<Field> fieldList = getFieldList();
			out.writeInt(fieldList.size());
			for(Field field : getFieldList()) {
				Serializable sObject = (Serializable) field.get(null);
				out.writeObject(field.getName());
				out.writeObject(sObject);
			}
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void load() {
		File file = PERSISTENT_FILE;
		if(!file.exists()) {
			return;
		}
		
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			int objectCount = in.readInt();
			for(int i = 0; i < objectCount; i ++) {
				String fieldName = (String) in.readObject();
				Object value = in.readObject();
				
				try {
					Field field = Persistent.class.getField(fieldName);
					field.set(null, value);
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}
			in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static List<Field> getFieldList() {
		List<Field> fieldList = new ArrayList<Field>();
		for(Field field : Persistent.class.getDeclaredFields()) {
			if(field.getName().equals("PERSISTENT_FILE")) {
				continue;
			}
			fieldList.add(field);
		}
		fieldList.sort(new Comparator<Field>() {
			@Override
			public int compare(Field field1, Field field2) {
				return field1.getName().compareTo(field2.getName());
			}
		});
		return fieldList;
	}
}
