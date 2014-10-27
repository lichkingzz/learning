package poiconvert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		Convertor convertor = Convertor.createByConfigFile(new File("test.config"));
		System.out.println(convertor.convert());
	}
}
