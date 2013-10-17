/** 
 * Copyright (C) 2010  Jorge J. Gomez-Sanz 
 * 
 * This file is part of the INGENME tool. INGENME is an open source meta-editor
 * which produces customized editors for user-defined modeling languages
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3 of the License
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 **/

package ingenias.generator.browser;

import ingenias.editor.GUIResources;
import ingenias.editor.IDEState;
import ingenias.editor.ModelJGraph;
import ingenias.editor.entities.Entity;

import java.util.*;
import java.io.*;
import ingenias.exception.*;

/**
 *  Implements a Singleton pattern to gain global access to diagrams contained
 *  in a file
 *
 *@author     Jorge J. Gomez-Sanz
 *@created    30 November 2003
 */
public class BrowserImp implements Browser {

	private static Browser browser = null;
	private ingenias.editor.IDEState ids;
	File currentProject=null;

	/**
	 *  Constructor for the BrowserImp object
	 */
	/*private BrowserImp() {
		ids=ingenias.editor.IDEAbs.ide.ids;

	}*/


	public BrowserImp(IDEState ids) {
		this.ids=ids;
		if (ids==null)
			throw new RuntimeException("The ids parameter cannot be null");
		currentProject=ids.getCurrentFile();
	}


	/**
	 *  Constructor for the BrowserImp object
	 *
	 *@param  file           Description of Parameter
	 *@exception  Exception  Description of Exception
	 */
	private BrowserImp(String file) throws ingenias.exception.UnknowFormat, ingenias.exception.DamagedFormat, ingenias.exception.CannotLoad{
		// This is a method to be run in headless mode

		ingenias.editor.persistence.PersistenceManager p = new ingenias.editor.persistence.PersistenceManager();
		ids=IDEState.emptyIDEState();
		try {
			p.loadWithoutListeners(file, new GUIResources(),new Properties(),ids);
		} catch (Throwable t){
			t.printStackTrace();
		}

		this.currentProject=new File(file);
	}



	/**
	 *  Obtains all existing graphs
	 *
	 *@return    The graphs value
	 */
	public Graph[] getGraphs() {
		ingenias.editor.GraphManager gm = ids.gm;
		Vector models = gm.getUOModels();
		Graph[] gs = new Graph[models.size()];
		Iterator it = models.iterator();
		int k = 0;
		while (it.hasNext()) {
			ingenias.editor.ModelJGraph model = (ingenias.editor.ModelJGraph) it.next();
			gs[k] = new GraphImp(model,ids);
			k++;
		}

		return gs;
	}


	/**
	 *  Gets the allEntities attribute of the BrowserImp object
	 *
	 *@return    The allEntities value
	 */
	public GraphEntity[] getAllEntities() {
		
		Graph[] gs = this.getGraphs();
		HashSet<GraphEntity> entities = new HashSet<GraphEntity>();
		for (int k = 0; k < gs.length; k++) {
			Graph current = gs[k];
			GraphEntity[] ges=null;
			try {
				ges = current.getEntities();
			} catch (NullEntity e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (int i = 0; i < ges.length; i++) {
				entities.add(ges[i]);
			}
		}
		
		GraphEntity[] temp = entities.toArray(new GraphEntity[entities.size()]);
		/*Object[] temp = ids.om.getAllObjects().toArray();
		Graph[] gs=this.getGraphs();
		Vector<GraphEntity> entities=new Vector<GraphEntity>(); 		
		for (Object ent:temp){
			if ( Entity.class.isAssignableFrom(ent.getClass()))
				try {
					entities.add(new GraphEntityImp((Entity)ent,null,(ModelJGraph) gs[0].getGraph(), getState()));
				} catch (NullEntity e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		GraphEntity[] result = new GraphEntity[entities.size()];*/
		//System.arraycopy(entities.toArray(), 0, temp, 0, temp.length);
		return temp;
	}


	// Obtains a graph with a concrete id. If there is no any, it returns null
	/**
	 *  Gets the graph attribute of the BrowserImp object
	 *
	 *@param  id  Description of Parameter
	 *@return     The graph value
	 */
	public Graph getGraph(String id) {
		ingenias.editor.GraphManager gm = ids.gm;
		ingenias.editor.ModelJGraph mg = (ingenias.editor.ModelJGraph) gm.getModel(id);
		if (mg != null) {
			return new GraphImp(mg, ids);
		} else {
			return null;
		}
	}


	/**
	 *  Obtains an instance
	 *
	 *@return    The instance value
	 */
	/*public static Browser getInstance()  throws ingenias.exception.NotInitialised{
		if (browser == null) {
			throw new ingenias.exception.NotInitialised("Browser not initialised. You must call \"initialise\" first");
		} else {
			return browser;
		}
	}*/


	/**
	 *  Initialises the diagram browser with a INGENIAS specification file
	 *
	 *@param  file           Description of Parameter
	 *@exception  Exception  Description of Exception
	 */
	public static Browser initialise(String file) throws
	ingenias.exception.UnknowFormat,
	ingenias.exception.DamagedFormat,
	ingenias.exception.CannotLoad {

		//browser = new BrowserImp(IDEState.emptyIDEState());

		browser = new BrowserImp(file);
		return browser;

	}


	/**
	 *  Performs an empty initialisation. Only works when there is an already existing
	 *  instance of GraphManager
	 *
	 *@exception  Exception  Description of Exception
	 */
	/*public static void initialise() {
		ingenias.editor.GraphManager.getInstance();
		// To check if it is initialised
		browser = new BrowserImp();

	}*/


	/**
	 *  Performs an empty initialisation. Only works when there is an already existing
	 *  instance of GraphManager
	 *
	 *@exception  Exception  Description of Exception
	 */
	public static void initialise(IDEState ids) {

		// To check if it is initialised
		browser = new BrowserImp(ids);

	}

	public GraphEntity findEntity(String id){
		GraphEntity[] ents=this.getAllEntities();
		boolean found=false;
		int k=0;
		for (k=0;k<ents.length &&!found;k++){
			found=ents[k].getID().equals(id);
		}
		if (found)
			return ents[k-1];
		else 
			return null;
	}

	public ingenias.editor.IDEState getState(){
		return this.ids;
	}

	public Graph findFirstEntityOccurrence(String id){
		Graph[] gs=this.getGraphs();
		boolean found=false;
		Graph result=null;
		for (int k=0;k<gs.length && result==null;k++){
			GraphEntity[] entities;
			try {
				entities = gs[k].getEntities();
				for (int j=0;j<entities.length && result==null;j++){
					if (entities[j].getID().equalsIgnoreCase(id)){
						result=gs[k];
					}
				}
			} catch (NullEntity e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;

	}

	public File getProjectFilePath(){
		return this.currentProject;
	}



	/**
	 *  Description of the Method
	 *
	 *@param  args           Description of Parameter
	 *@exception  Exception  Description of Exception
	 */
	public static void main(String args[]) throws Exception {
		String file = args[0];
		Browser b = new BrowserImp(file);
		Graph[] gs = b.getGraphs();
		for (int k = 0; k < gs.length; k++) {
			System.err.println(gs[k].getType() + ":" + gs[k].getName());
		}
	}

	public static boolean containedInto(Browser bimp1, Browser bimp2){
		GraphEntity[] entities = bimp1.getAllEntities();
		boolean allIncluded=true;
		int k=0;
		while (allIncluded && k<entities.length){
			GraphEntity ent1 = entities[k];
			GraphEntity ent2 = bimp2.findEntity(ent1.getID());
			allIncluded=allIncluded && ent2!=null;			
			allIncluded = allIncluded &&  containedIntoAttributes(allIncluded, ent1, ent2);			
			Vector<GraphRelationship> relationships1 = ent1.getAllRelationships();
			Vector<GraphRelationship> relationships2 = ent2.getAllRelationships();
			int j=0;
			allIncluded=allIncluded && relationships1.size()==relationships2.size();
			while (allIncluded && j<relationships1.size()){
				GraphRelationship gr1=relationships1.elementAt(j);
				boolean found=false;
				for (int l=0;l<relationships2.size() && !found;l++){
					GraphRelationship gr2=relationships2.elementAt(l);
					found=found || (gr2.getID().equals(gr1.getID()) && 
							containedIntoAttributes(allIncluded, gr1, gr2));
				}
				allIncluded=allIncluded && found;
				j++;	
			}			
			k++;
		}		
		return allIncluded;		
	}


	private static boolean containedIntoAttributes(boolean allIncluded,
			AttributedElement ent1, AttributedElement ent2) {
		GraphAttribute[] attributes1 = ent1.getAllAttrs();
		GraphAttribute[] attributes2 = ent2.getAllAttrs();
		int j=0;
		allIncluded=allIncluded && attributes1.length==attributes2.length;
		while (allIncluded && j<attributes1.length){
			boolean found=false;
			GraphAttribute attr1=attributes2[j];
			for (int l=0;l<attributes2.length && !found;l++){
				GraphAttribute attr2=attributes2[l];												
				found=found || 
						attr1.getName().equals(attr2.getName());
				if (found){
					found = found || (attr1.getSimpleValue()!=null && 
							attr2.getSimpleValue()!=null && 
							attr1.getSimpleValue().equals(attr2.getSimpleValue()));
					if (!found){									
						try {
							attr1.getEntityValue();
							try {
								attr2.getEntityValue();
								found = found || 
										attr1.getEntityValue().getID().equals(attr2.getEntityValue().getID());
							} catch (NullEntity e1) {
								// Not OK.											
							}
						} catch (NullEntity e) {
							try {
								attr2.getEntityValue();
								// Not ok.
							} catch (NullEntity e1) {
								// It's ok
								found = true;
							}
						}

					}								
				}
			}
			allIncluded=allIncluded && found;
			j++;
		}
		return allIncluded;
	}

	public static Vector<String> findAllDifferences(Browser bimp1, Browser bimp2){
		GraphEntity[] entities = bimp1.getAllEntities();
		boolean allIncluded=true;
		Vector<String> differences=new Vector<String>();
		int k=0;
		while (k<entities.length){
			GraphEntity ent1 = entities[k];
			GraphEntity ent2 = bimp2.findEntity(ent1.getID());
			allIncluded=allIncluded && ent2!=null;
			if (ent2==null){
				differences.add("entity "+ent1.getID()+":"+ent1.getType()+" does not exist");
			} else {
				differences.addAll(checkEntity(ent1, ent2, new Vector()));


			}
			k++;
		}		
		return differences;		
	}

	private static Vector<String> checkEntity(GraphEntity ent1,
			GraphEntity ent2, Vector<GraphEntity> alreadyVerified) {
		boolean allIncluded=true;
		Vector<String> differences=new Vector<String>();
		if (!ent1.getClass().equals(ent2.getClass())){
			differences.add("entity "+ent1.getID()+":"+ent1.getType()+" does not exist but there is one with id "+ent1.getID()+":"+ent2.getType());
		} else {
			if (!alreadyVerified.contains(ent1)){
				alreadyVerified.add(ent1);
				Vector<String> fieldDifferences=checkFields(ent1,ent2,alreadyVerified);
				differences.addAll(fieldDifferences);
				if (!fieldDifferences.isEmpty()){
					Vector<GraphRelationship> relationships1 = ent1.getAllRelationships();
					Vector<GraphRelationship> relationships2 = ent2.getAllRelationships();
					int j=0;
					allIncluded=allIncluded && relationships1.size()==relationships2.size();
					while (j<relationships1.size()){
						GraphRelationship gr1=relationships1.elementAt(j);
						boolean found=false;
						for (int l=0;l<relationships2.size() && !found;l++){
							GraphRelationship gr2=relationships2.elementAt(l);
							found=found || gr2.getID().equals(gr1.getID());
						}
						if (!found){
							differences.add("relationship "+gr1.getID()+":"+gr1.getType()+" does not exist");
						}
						j++;	
					}
				}
			}
		}
		return differences;
	}

	private static Vector<String> checkFields(GraphEntity ent1,
			GraphEntity ent2, Vector alreadyVerified) {
		GraphAttribute[] attsEnt1 = ent1.getAllAttrs();
		GraphAttribute[] attsEnt2 = ent2.getAllAttrs();
		Vector<String> differences=new Vector<String>();

		for (int k=0;k<attsEnt1.length; k++){
			boolean found=false;
			GraphAttribute gaE1=attsEnt1[k];
			if (!gaE1.getName().equalsIgnoreCase("prefs")){
				for (int j=0;j<attsEnt2.length && !found;j++) {
					GraphAttribute gaE2=attsEnt2[j];

					if (gaE1.getName().equals(gaE2.getName())){
						if (gaE1.isSimpleValue() && gaE2.isSimpleValue()){
							evaluateSimpleValueField(ent1, differences, gaE1, gaE2);
						} else

							if (gaE1.isCollectionValue() && gaE2.isCollectionValue()){
								evaluateCollectionValueField(ent1, alreadyVerified,
										differences, found, gaE1, gaE2);
							} else {
								if (gaE1.isEntityValue() && gaE2.isEntityValue())
								evaluateEntityValueField(ent1, alreadyVerified,
										differences, gaE1, gaE2);
								else {
									differences.add("entity "+ent1.getID()+":"+ent1.getType()+" has an attribute named "+gaE1.getName()+" with different type in the second spec");
								}
							} 
					}
				}
			}
		}
		return differences;
	}


	private static void evaluateEntityValueField(GraphEntity ent1,
			Vector alreadyVerified, Vector<String> differences,
			GraphAttribute gaE1, GraphAttribute gaE2) {
		boolean found;
		if (gaE1.isEntityValue() && gaE2.isEntityValue()){
			// different types of fields. It sho
			GraphEntity entValue1=null;
			GraphEntity entValue2=null;
			try {
				entValue1=gaE1.getEntityValue();
			} catch (NullEntity ne){};
			try {
				entValue2=gaE2.getEntityValue();
			} catch (NullEntity ne){};
			if (entValue1==null && entValue2==null)
				found=true;
			else
				if (entValue1!=null && entValue2==null){
					differences.add("entity " +ent1.getID()+":"+ent1.getType()+" has not the same values for attribute "+
							gaE1.getName()+":"+entValue1+" instead the second spec has null");
				} else
					if (entValue1==null && entValue2!=null){
						differences.add("entity " +ent1.getID()+":"+ent1.getType()+" has not the same values for attribute "+
								gaE1.getName()+":null instead the second spec has \""+entValue2+"\"");
					} else
						if (entValue1!=null && entValue2!=null){
							Vector<String> cdifferences = checkEntity(entValue1,entValue2,alreadyVerified);
							differences.addAll(cdifferences);
							found=cdifferences.isEmpty();
						} 
		}
	}


	private static void evaluateCollectionValueField(GraphEntity ent1,
			Vector alreadyVerified, Vector<String> differences, boolean found,
			GraphAttribute gaE1, GraphAttribute gaE2)  {
		GraphCollection colValue1=null;
		GraphCollection colValue2=null;		
		try {
			colValue1=gaE1.getCollectionValue();
		} catch (ingenias.exception.NullEntity ne){};
		try {
			colValue2=gaE2.getCollectionValue();
		} catch (ingenias.exception.NullEntity ne){};
		if (colValue1==null && colValue2==null){
			found=true;
		} else
			if (colValue1==null && colValue2!=null){
				differences.add("entity " +ent1.getID()+":"+ent1.getType()+" has a null value  for attribute "+
						gaE1.getName()+" instead the second spec has "+colValue2);
			} else 
				if (colValue1!=null && colValue2==null){
					differences.add("entity " +ent1.getID()+":"+ent1.getType()+" has not the same values for attribute "+
							gaE1.getName()+":"+colValue1+" instead the second spec has null");
				} else
					if (colValue1!=null && colValue2!=null &&
					colValue1.size()==colValue2.size()){
						Vector<String> cdifferences = new Vector<String>();
						for (int l=0;l<colValue1.size();l++){
							try {
								cdifferences.addAll(checkEntity(colValue1.getElementAt(l), 
										colValue2.getElementAt(l), 
										alreadyVerified));
							} catch (NullEntity e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						differences.addAll(cdifferences);									
						found=found || cdifferences.isEmpty();
					} else {
						differences.add("entity " +ent1.getID()+":"+ent1.getType()+" has not the same number of values for attribute "+
								gaE1.getName()+" with "+colValue1.size()+" instead the second spec has "+colValue2.size()+" elements");
					}
	}


	private static void evaluateSimpleValueField(GraphEntity ent1,
			Vector<String> differences, GraphAttribute gaE1, GraphAttribute gaE2) {
		boolean found;
		String simpleValue1=null;
		String simpleValue2=null;
		simpleValue1=gaE1.getSimpleValue();
		simpleValue2=gaE2.getSimpleValue();
		if (simpleValue1==null && simpleValue1==null){		
			found=true;
		} else
			if (simpleValue1==null && simpleValue2!=null){
				differences.add("entity " +ent1.getID()+":"+ent1.getType()+" has a null value  for attribute "+
						gaE1.getName()+" instead the second spec has "+simpleValue2);
			} else 
				if (simpleValue1!=null && simpleValue2==null){
					differences.add("entity " +ent1.getID()+":"+ent1.getType()+" has not the same values for attribute "+
							gaE1.getName()+":"+simpleValue1+" instead the second spec has null");
				} else
					if (simpleValue1!=null && simpleValue2!=null && !simpleValue1.equals(simpleValue2)){
						differences.add("entity " +ent1.getID()+":"+ent1.getType()+" has a value \""+simpleValue1+"\"  for attribute "+
								gaE1.getName()+" instead the second spec has \""+simpleValue2+"\"");
					}
	}


	public static boolean compare(Browser bimp1, Browser bimp2) {
		return findAllDifferences(bimp1, bimp2).isEmpty() && findAllDifferences(bimp2, bimp1).isEmpty();
	}

}
