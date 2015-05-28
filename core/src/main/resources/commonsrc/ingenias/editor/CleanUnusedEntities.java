package ingenias.editor;

import ingenias.editor.entities.Entity;
import ingenias.editor.persistence.PersistenceManager;
import ingenias.exception.NullEntity;
import ingenias.generator.browser.AttributedElement;
import ingenias.generator.browser.Browser;
import ingenias.generator.browser.BrowserImp;
import ingenias.generator.browser.Graph;
import ingenias.generator.browser.GraphAttribute;
import ingenias.generator.browser.GraphEntity;
import ingenias.generator.browser.GraphEntityImp;
import ingenias.generator.browser.GraphRelationship;
import ingenias.generator.browser.GraphRole;
import ingenias.generator.interpreter.SplitHandler;

import java.awt.Frame;
import java.io.File;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;


public class CleanUnusedEntities {


	private static boolean isUsedInSomeDiagram(GraphEntity original,Browser browser) throws IllegalAccessException, NullEntity{
		Graph[] v=browser.getGraphs();
		Hashtable<String, AttributedElement> checked=new  Hashtable<String, AttributedElement>();
		boolean used=false;
		for (int l=0;l<v.length && !used;l++){
			Graph tested=v[l];		
			used=isUsedInAtts(original, tested, browser, checked);				
			if (!used){
				GraphRelationship[] rels=tested.getRelationships();
				for (int k=0;k<rels.length && !used;k++){
					used=isUsedInAtts(original, rels[k], browser, checked);
					if (!used){
						GraphRole[] roles=rels[k].getRoles();
						for (int m=0;m<roles.length && !used;m++){														
								used=isUsedInAtts(original, roles[m], browser, checked);
								// no need to check GraphRole players since they have to appear in some diagram
																
						}
					}
				}
			}
			GraphEntity[] entities=tested.getEntities();
			for (int n=0;n<entities.length && !used;n++){
				if (original.equals(entities[n]))
					used=true;
				else
					if (!checked.contains(entities[n].getID())){
						checked.put(entities[n].getID(), entities[n]);
						used=isUsedInAtts(original, entities[n],browser,checked);
					}
			}
		}
		return used;
	}

	private static boolean isUsed(GraphEntity original,Browser browser) throws IllegalAccessException, NullEntity{
		return isUsedInSomeDiagram(original,browser) ;
	}



	private static boolean isUsedInAtts(GraphEntity original, AttributedElement tested, Browser browser, Hashtable<String, AttributedElement> checked) throws IllegalAccessException, NullEntity{
		boolean used=false;
		GraphAttribute [] fs=tested.getAllAttrs();
		for (int j=0;j<fs.length && !used;j++){
			GraphAttribute att=fs[j];
			if (att.isEntityValue() && att.getEntityValue()!=null && att.getEntityValue().equals(original)){
				used=true;
			} else
				if (att.isEntityValue() && att.getEntityValue()!=null && !checked.contains(att.getEntityValue().getID())){
					checked.put(att.getEntityValue().getID(), att.getEntityValue());
					used=isUsedInAtts(original, att.getEntityValue(), browser,checked);
				} else
					if (att.isCollectionValue() && att.getCollectionValue()!=null){
						if (att.getCollectionValue().contains(original)){
							used =true;
						}	else {
							for (int k=0;k<att.getCollectionValue().size() &&!used;k++){
								if (!checked.contains(att.getCollectionValue().getElementAt(k).getID())){
									checked.put(att.getCollectionValue().getElementAt(k).getID(), att.getCollectionValue().getElementAt(k));
									used=isUsedInAtts(original, att.getCollectionValue().getElementAt(k), browser, checked);
								}
							}
						}
					}
		}

		return used;
	}

	public static void main(String[] args) throws Exception {
		System.out
		.println("Non-used entities cleaner by Jorge Gomez");
		System.out
		.println("This program comes with ABSOLUTELY NO WARRANTY; for details check www.gnu.org/copyleft/gpl.html.");
		System.out
		.println("This is free software, and you are welcome to redistribute it under certain conditions;; for details check www.gnu.org/copyleft/gpl.html.");

		ingenias.editor.Log.initInstance(new PrintWriter(System.out), new PrintWriter(System.err));
		if (args.length != 2) {
			System.err
			.println("The first argument (mandatory) has to be the specification file and the second (mandatory) a target file");
		} else {
			BrowserImp browser=(BrowserImp) BrowserImp.initialise(args[0]);
			boolean thereAreUnusedEntities=false;
			while (!thereAreUnusedEntities){
				Vector ge=browser.getState().om.getAllObjects();
				for (Object nentity:ge){					
					GraphEntity entity=new GraphEntityImp((Entity)nentity,browser.getGraphs()[0].getGraph(),browser.getState());
					if (!isUsed(entity, browser)){
						browser.getState().om.removeEntity(entity.getEntity());	
						thereAreUnusedEntities=true;
					}
				}
				PersistenceManager pm=new PersistenceManager();
				pm.save(new File(args[1]), browser.getState());


			}
			for (Frame f : Frame.getFrames()) {
				f.dispose();
			}

		}
	}

}
