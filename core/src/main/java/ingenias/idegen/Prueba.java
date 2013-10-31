package ingenias.idegen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipInputStream;

public class Prueba {

	public static void main(String args[]) throws IOException{
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new ZipInputStream(ObjectsGenerator.class.getResourceAsStream("/templates"))));
		String fileName;
		while((fileName = br.readLine()) != null){ 
			System.err.println(fileName);
		}
	}
}
