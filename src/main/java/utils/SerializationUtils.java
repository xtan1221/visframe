package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;

public class SerializationUtils {
	
	public static void serializeToFile(Path outputFilePath, Serializable target) {
		try {
			FileOutputStream fileOut =
			new FileOutputStream(outputFilePath.toString());
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(target);
			out.close();
			fileOut.close();
			
		} catch (IOException i) {
			i.printStackTrace();
		}
	}
	
	public static Object deserializeFromFile(Path serializedFilePath) {
		Object object = null;
		try {
			FileInputStream fileIn = new FileInputStream(serializedFilePath.toString());
			ObjectInputStream in = new ObjectInputStream(fileIn);
			object = in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException c) {
			c.printStackTrace();
		}
		return object;
	}
}
