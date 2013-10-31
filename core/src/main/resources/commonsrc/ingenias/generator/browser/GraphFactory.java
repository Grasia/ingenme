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

import ingenias.editor.DiagramMenuEntriesActionsFactory;
import ingenias.editor.Editor;
import ingenias.editor.GUIResources;
import ingenias.editor.IDE;
import ingenias.editor.IDEState;
import ingenias.editor.Log;
import ingenias.editor.MarqueeHandler;
import ingenias.editor.Model;
import ingenias.editor.ModelJGraph;
import ingenias.editor.ObjectManager;
import ingenias.editor.Preferences;
import ingenias.editor.TypedVector;
import ingenias.editor.cell.NAryEdge;
import ingenias.editor.cell.RenderComponentManager;
import ingenias.editor.entities.Entity;
import ingenias.editor.entities.NAryEdgeEntity;
import ingenias.editor.entities.RoleEntity;
import ingenias.exception.InvalidAttribute;
import ingenias.exception.InvalidColection;
import ingenias.exception.InvalidEntity;
import ingenias.exception.InvalidGraph;
import ingenias.exception.InvalidPath;
import ingenias.exception.NotInitialised;
import ingenias.exception.NullEntity;
import ingenias.exception.WrongParameters;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import org.jgraph.JGraph;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.Port;

public class GraphFactory {
	private IDEState ids;


	public static GraphFactory createDefaultEmptyGraphFactory(){
		return new GraphFactory(IDEState.emptyIDEState());
	}

	public static GraphFactory createDefaultGraphFactory(Browser browser) throws NotInitialised{
		return new GraphFactory(browser.getState());
	}

	public GraphFactory(IDEState ids){
		this.ids=ids;	
	}

	public static Vector<String> getSupportedRelationships(String diagramType)  throws InvalidGraph, NotInitialised{
		try {
			Class diagram = Class.forName("ingenias.editor."+diagramType+"ModelJGraph");
			Method m=diagram.getMethod("getAllowedRelationships", new Class[0]);
			return (Vector<String> ) m.invoke(diagram, new Object[0]);
		} catch (ClassNotFoundException e) {
			throw new InvalidGraph("Graph class ingenias.editor."+diagramType+"ModelJGraph does not exist in the classpath");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Graph class ingenias.editor."+diagramType+"ModelJGraph is not accesible for security reasons");
		} catch (NoSuchMethodException e) {
			// There are diagrams that do not have this method. Those diagrams
			// are not generated automatically

			/*e.printStackTrace();
			throw new InvalidGraph("Graph class ingenias.editor."+diagramType+"ModelJGraph has not that method");*/
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Graph class ingenias.editor."+diagramType+"ModelJGraph has not that method");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Graph class ingenias.editor."+diagramType+"ModelJGraph has not that method");
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Graph class ingenias.editor."+diagramType+"ModelJGraph has not that method");
		}
		return new Vector();


	}

	public static Vector<String> getSupportedEntities(String diagramType)  throws InvalidGraph, NotInitialised{
		try {
			Class diagram = Class.forName("ingenias.editor."+diagramType+"ModelJGraph");
			Method m=diagram.getMethod("getAllowedEntities", new Class[0]);
			return (Vector<String> ) m.invoke(diagram, new Object[0]);
		} catch (ClassNotFoundException e) {
			throw new InvalidGraph("Graph class ingenias.editor."+diagramType+"ModelJGraph does not exist in the classpath");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Graph class ingenias.editor."+diagramType+"ModelJGraph is not accesible for security reasons");
		} catch (NoSuchMethodException e) {			
			/*e.printStackTrace();
			throw new InvalidGraph("Graph class ingenias.editor."+diagramType+"ModelJGraph has not that method");*/
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Graph class ingenias.editor."+diagramType+"ModelJGraph has not that method");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Graph class ingenias.editor."+diagramType+"ModelJGraph has not that method");
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Graph class ingenias.editor."+diagramType+"ModelJGraph has not that method");
		}
		return new Vector();

	}

	public static Vector<String> getDiagramTypes() {
		Vector<String> result=new Vector<String>();
		File sources=new File("src/ingenias/editor");
		String[] knownFiles = sources.list();
		for (int k=0;k<knownFiles.length;k++){
			if (knownFiles[k].endsWith("ModelJGraph.java")){
				result.add(knownFiles[k].substring(0,knownFiles[k].length()-"ModelJGraph.java".length()));
			}
		}
		return result;
	}




	public Graph createSimpleGraph(String type,String name) throws InvalidGraph, NotInitialised{

		try { 
			Class modelData = Class.forName("ingenias.editor.entities."+type+"DataEntity");
			Constructor constructorModelData=modelData.getConstructor(new Class[]{String.class});
			Object mde = constructorModelData.newInstance(new Object[]{name});

			Constructor constructorGraph = Class.forName("ingenias.editor."+type+"ModelJGraph").getConstructor(new Class[]{	  
					modelData,Editor.class,String.class,ObjectManager.class,
					Model.class,BasicMarqueeHandler.class});

			ModelJGraph ndiagram=(ModelJGraph) constructorGraph.newInstance(new Object[]{
					mde,ids.editor,name,ids.om,
					new Model(ids),new BasicMarqueeHandler()});

			ids.gm.addModel(new Object[]{"Project"}, name, ndiagram);	
			GraphImp gimp=new GraphImp(ndiagram,ids);

			return gimp;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Either "+type+" name is unknown or "+type+"DataEntity is unknown");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Security exceptionon creating either "+type+"  or "+type+"DataEntity");
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("No such methods on creating either "+type+"  or "+type+"DataEntity");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Illegal argument when creating either "+type+"  or "+type+"DataEntity");
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Error instantiating either "+type+"  or "+type+"DataEntity");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Wrong constructor permissions when creating either "+type+"  or "+type+"DataEntity");

		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Error invoking either "+type+"  or "+type+"DataEntity");
		}


	}

	public void createPackage(String[] path, String packageName) throws InvalidPath{
		String[] npath=null;
		String[] completePath=new String[path.length+2];
		System.arraycopy(path, 0, completePath,1, path.length);
		completePath[path.length+1]=packageName;
		completePath[0]="Project";
		if (path.length==0 || !path[0].equals("Project")){
			npath=new String[path.length+1];
			npath[0]="Project";
			System.arraycopy(path, 0, npath,1, path.length);
		} else
			npath=path;
		
		if (packageName != null && npath!=null 
				&& !ids.gm.existsModel(packageName) 
				&& ids.gm.getPath(completePath)==null
				&& ids.gm.getPath(npath)!=null) {
			
			ids.gm.addPackage(npath,packageName);
			ids.gm.arbolProyecto.repaint();	            
		} else {
			if (packageName == null)
				throw new InvalidPath("The package name cannot be null");
			if (path==null)
				throw new InvalidPath("The path to the new package cannot be null");
			if (ids.gm.existsModel(packageName))
				throw new InvalidPath("There is already a model with the name "+packageName);
			if (ids.gm.getPath(npath)==null)
				throw new InvalidPath("The path "+convert(npath)+" is not valid because one or more entries do not exist in the tree");
			if (ids.gm.getPath(completePath)!=null)
				throw new InvalidPath("The path "+convert(completePath)+" is not valid because it already exists");
			
		}
	}
	
	private String convert(Object[] object){
		String res="";
		for (Object obj:object){
			res=res+obj.toString();
		}
		return res;
	}
	
	public Graph createCompleteGraph(String[] path, String type,String name) throws InvalidGraph, NotInitialised{

		try { 
			if (ids.gm.getModel(name)==null){
				
			}
			Class modelData = Class.forName("ingenias.editor.entities."+type+"DataEntity");
			Constructor constructorModelDataEntity=modelData.getConstructor(new Class[]{String.class});
			Object mde = constructorModelDataEntity.newInstance(new Object[]{name});
			Constructor constructorGraph = Class.forName("ingenias.editor.models."+type+"ModelJGraph").
				getConstructor(new Class[]{	  
					modelData,String.class,
					ObjectManager.class,
					Model.class,
					BasicMarqueeHandler.class,
					Preferences.class});
			Model m=new Model(ids);
			ModelJGraph ndiagram=(ModelJGraph) constructorGraph.newInstance(new Object[]{
					mde,
					name,
					ids.om,					
					m,
					new BasicMarqueeHandler(),
					ids.prefs});
			
			Constructor constructorMarquee = Class.forName("ingenias.editor.MarqueeHandler").getConstructor(
					new Class[]{	  
					ModelJGraph.class,
					GUIResources.class, 
					IDEState.class, 
					DiagramMenuEntriesActionsFactory.class});
			
			MarqueeHandler marquee = (MarqueeHandler) constructorMarquee.newInstance(new Object[]{
					ndiagram,
					null, 
					ids, 
					null});

			ndiagram.setMarqueeHandler(marquee);
			
			String[] npath=null;
			if (path.length==0 || !path[0].equals("Project")){
				npath=new String[path.length+1];
				npath[0]="Project";
				System.arraycopy(path, 0, npath,1, path.length);
			} else
				npath=path;
			ids.gm.addModel(npath, name, ndiagram);	
			GraphImp gimp=new GraphImp(ndiagram,ids);

			return gimp;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Either "+type+" name is unknown or "+type+"DataEntity is unknown");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Security exceptionon creating either "+type+"  or "+type+"DataEntity");
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("No such methods on creating either "+type+"  or "+type+"DataEntity");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Illegal argument when creating either "+type+"  or "+type+"DataEntity");
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Error instantiating either "+type+"  or "+type+"DataEntity");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Wrong constructor permissions when creating either "+type+"  or "+type+"DataEntity");

		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidGraph("Error invoking either "+type+"  or "+type+"DataEntity");
		}


	}

	public Graph createCompleteGraph(String type,String name) throws InvalidGraph, NotInitialised{
		return createCompleteGraph(new String[]{"Project"}, type,name);	
	}






}
