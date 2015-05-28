package ingenias.ingened.test;

import ingenias.codeproc.HTMLDocumentGenerator;

import org.testng.annotations.Test;

public class GraphEntityGenerationTest {
@Test
	public void htmlDocGeneration() throws Exception{
		HTMLDocumentGenerator.main(
				new String[]{"../nodereled/src/main/resources/metamodel/metamodelINGENED.xml", 
						"../nodereled"});
	}
	
}
