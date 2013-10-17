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

package ingenias.editor.cell;

import ingenias.editor.Model;
import ingenias.editor.entities.Entity;
import ingenias.editor.entities.ViewPreferences;
import ingenias.editor.entities.ViewPreferences.ViewType;
import ingenias.exception.ParseException;
import ingenias.exception.WrongConversion;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.swixml.SwingEngine;
/*
 * It manages the renderers and the association of id-component they have. It stores this information using
 * as index the class of the registerer and the kind of view to be used
 */




public class RenderComponentManager {



	private static Hashtable<RenderIndex,JPanel> renderer = new Hashtable<RenderIndex,JPanel>();
	private static Hashtable<RenderIndex,Hashtable> components = new Hashtable<RenderIndex,Hashtable>();
	
	private static Hashtable<Entity,String> hashcodeEntities=new Hashtable();
	private static Hashtable<AbstractMap.SimpleEntry<String,String>,Dimension> initialDimension=
			new Hashtable<AbstractMap.SimpleEntry<String,String>,Dimension>();
	private static Hashtable<AbstractMap.SimpleEntry<Entity,String>,Dimension> registeredDimension=
			new Hashtable<AbstractMap.SimpleEntry<Entity,String>,Dimension>();
	
	
	private static JWindow helperFrame=new JWindow();
	static {
		helperFrame.setVisible(false);
	}


	public static void register(String classname, ingenias.editor.entities.ViewPreferences.ViewType kind, JPanel component,Hashtable ids){
		synchronized (renderer){
			//System.err.println("REgistered ("+classname+","+kind+")"+ ids);
			renderer.put(new RenderIndex(classname,kind),component);
			components.put(new RenderIndex(classname,kind),ids);
		}
	}

	public static JPanel retrievePanel(String classname, ingenias.editor.entities.ViewPreferences.ViewType kind){
		synchronized (renderer){
			//System.err.println("Retrieving "+classname+ " "+ kind);
			return renderer.get(new RenderIndex(classname,kind));
		}
	}


	public static Hashtable retrieveIDs(String classname, ingenias.editor.entities.ViewPreferences.ViewType kind){
		synchronized (renderer){
			//System.err.println("retrieved ("+classname+","+kind+")" +" with "+components.get(new RenderIndex(classname,kind)));
			return components.get(new RenderIndex(classname,kind));
		}
	}
	public static JPanel loadRenderFile(String classname, ingenias.editor.entities.ViewPreferences.ViewType kind, String file) throws IOException, ParseException {
		synchronized (renderer){
			InputStream fis = null;
			StringBuffer sb = null;
			int read = 0;
			String result = "";
			SwingEngine se = null;
			se = SWIRenderer.getAnotherSWIEngine();

			se.cleanup();


			fis =	RenderComponentManager.class.getResource(file).openStream();

			sb = new StringBuffer();
			read = 0;
			while (read != -1) {
				read = fis.read();
				if (read != -1) {
					sb.append( (char) read);
				}
			}
			;
			fis.close();



			result = sb.toString(); 
			result = result.replaceAll("##", "<");
			result = result.replaceAll("#", ">");


			JPanel panel;
			try {

				// For assigning each component a unique identifier. It can be the field "name" in the descriptor
				// or the "id" field. In future releases, only "name" field will be valid 

				panel = new DefaultPanel((JPanel)se.render(new java.io.StringReader(result)));
				Iterator compIt = se.getAllComponentItertor();
				Map ids=(Map) se.getIdMap();
				Set<String> compKeys = ids.keySet();

				Hashtable compNames=new Hashtable();
				while (compIt.hasNext()){
					Component comp=(Component) compIt.next();
					if (comp.getName()!=null){					
						compNames.put(comp.getName(), comp);
					} else {
						for (String id:compKeys){
							if (ids.get(id)==comp){
								comp.setName(id);
								compNames.put(id, comp);
							}
						}
					}

				}
				RenderComponentManager.register(classname,kind,
						panel,
						compNames);
				return panel;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			throw new ingenias.exception.ParseException("Review XML compliance in file "+file+" with content "+result);
		}
	}
	
	

	public static Dimension getSize(Entity entity,
			String classname, ingenias.editor.entities.ViewPreferences.ViewType kind){

		if (!SwingUtilities.isEventDispatchThread()){
			throw new RuntimeException("Method getSize was called from outside the event dispatch thread");
		}
		if (!SwingUtilities.isEventDispatchThread()){
			throw new RuntimeException("Method getSize was called from outside the event dispatch thread");
		}
		synchronized (renderer){
			try {
				helperFrame.getContentPane().removeAll();
				// Determine the renderer
				Class renderer;

				renderer = Class.forName("ingenias.editor.cell."+classname+"Renderer");
				Class entityClass=Class.forName("ingenias.editor.entities."+classname);

				// Tell the specific renderer to focus on the entity
				Method method=renderer.getMethod("setEntity", new Class[]{entityClass,Map.class});
				Map m=new Hashtable();
				m.put("view", kind.toString());
				method.invoke(renderer, new Object[]{entity,m});
				// Obtains the panel to be drawn. The size of elements has been set for current entity
				JPanel entPanel=(JPanel)retrievePanel(classname,kind);
				//entPanel.getClass().getMethod(", parameterTypes)
				helperFrame.getContentPane().add((JPanel)retrievePanel(classname,kind));
				helperFrame.pack();
				helperFrame.pack();
				return helperFrame.getSize();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new Dimension(0,0);

	}

	public static Dimension getSize(String classname, ingenias.editor.entities.ViewPreferences.ViewType kind){
		synchronized (renderer) {
			helperFrame.getContentPane().removeAll();
			if (retrievePanel(classname,kind)==null){
				throw new RuntimeException("Error: there is no renderer defined for \""+classname+"\" for the notation "+kind+". Existing renderers are :"+renderer.keySet());
			}
			helperFrame.getContentPane().add((JPanel)retrievePanel(classname,kind));
			helperFrame.pack();
		}

		return helperFrame.getSize();
	}

	public static void setRelationshipView(ViewPreferences.ViewType viewtype, 
			Entity ent, 
			DefaultGraphCell cell, JGraph graph){
		synchronized (renderer){
			if (viewtype==ViewPreferences.ViewType.LABEL){
				graph.getModel().getAttributes(cell).put("view", "label");
				//ent.getPrefs(null).setView(ViewPreferences.ViewType.LABEL);
				//System.err.println("Acting on "+ent.getType()+" "+ent.getPrefs().getView());
				NAryEdge naryedge=(NAryEdge)cell;
				DefaultEdge[] edge=naryedge.getRepresentation();
				Hashtable nmap=new Hashtable();

				// Clean previous labels
				for (int k=0;k<edge.length;k++){
					AttributeMap am=edge[k].getAttributes();
					GraphConstants.setExtraLabels(am,new Object[]{""});
					nmap.put(edge[k],am);				
				}

				AttributeMap am=edge[0].getAttributes();
				GraphConstants.setLabelAlongEdge(am,true);
				if (((ingenias.editor.entities.NAryEdgeEntity)ent).getLabel()!=null)
					GraphConstants.setExtraLabels(am,new Object[]{((ingenias.editor.entities.NAryEdgeEntity)ent).getLabel()});
				else
					GraphConstants.setExtraLabels(am,new Object[]{""});
				if (GraphConstants.getExtraLabelPositions(am)==null)
					GraphConstants.setExtraLabelPositions(am,new Point[]{new Point(0, 20)});
				Hashtable naryEdgeAtts=naryedge.getAttributes();

				Rectangle2D loc=GraphConstants.getBounds(naryEdgeAtts);			  
				loc.setRect(loc.getX(),loc.getY(),0,0);
				GraphConstants.setBounds(naryEdgeAtts,loc);			  		

				nmap.put(edge[0],am);
				nmap.put(naryedge,naryEdgeAtts);
				ingenias.editor.events.LocationChange.centerNAryEdge(graph, 
						(Model) graph.getModel(), nmap, naryedge);
				graph.getModel().edit(nmap,null,null,null);
			}
			if (viewtype==ViewPreferences.ViewType.NOICON){
				ent.getPrefs(null).setView(ViewPreferences.ViewType.NOICON);
				//System.err.println("Acting on "+ent.getType()+" "+ent.getPrefs().getView());
				NAryEdge naryedge=(NAryEdge)cell;
				DefaultEdge[] edge=naryedge.getRepresentation();
				AttributeMap am=edge[0].getAttributes();
				GraphConstants.setLabelAlongEdge(am,true);
				GraphConstants.setExtraLabels(am,new Object[]{});
				Hashtable naryEdgeAtts=naryedge.getAttributes();
				Rectangle2D loc=GraphConstants.getBounds(naryEdgeAtts);			  
				loc.setRect(loc.getX(),loc.getY(),0,0);
				GraphConstants.setBounds(naryEdgeAtts,loc);			  
				Hashtable nmap=new Hashtable();
				nmap.put(edge[0],am);
				nmap.put(naryedge,naryEdgeAtts);
				ingenias.editor.events.LocationChange.centerNAryEdge(graph, 
						(Model) graph.getModel(), nmap, naryedge);
				graph.getModel().edit(nmap,null,null,null);
			}
			if (viewtype==ViewPreferences.ViewType.INGENIAS){
				graph.getModel().getAttributes(cell).put("view", "ingenias");
				//ent.getPrefs().setView(ViewPreferences.ViewType.INGENIAS);
				NAryEdge naryedge=(NAryEdge)cell;
				Hashtable am=naryedge.getAttributes();

				Rectangle2D loc=GraphConstants.getBounds(am);
				Dimension nsize;
				try {
					nsize = RenderComponentManager.getSize(
							ent.getType(),
							ViewType.fromString("ingenias"));

					loc.setRect(loc.getX(),loc.getY(),nsize.width,nsize.height);

					GraphConstants.setBounds(am,loc);			  
					DefaultEdge[] edge=naryedge.getRepresentation();
					Hashtable edgesAtts=edge[0].getAttributes();
					GraphConstants.setExtraLabels(edgesAtts,new Object[]{});
					Hashtable nmap=new Hashtable();
					nmap.put(naryedge,am);
					nmap.put(edge[0],edgesAtts);

					ingenias.editor.events.LocationChange.centerNAryEdge(graph, 
							(Model) graph.getModel(), nmap, naryedge);


					graph.getModel().edit(nmap,null,null,null);
				} catch (WrongConversion e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//ingenias.editor.cell.UMLAssociationRenderer.setCurrent(ViewPreferences.ViewType.LABEL);

			}
		}



	}
}

class RenderIndex {
	String classname;
	ingenias.editor.entities.ViewPreferences.ViewType kind;
	public RenderIndex(String classname, ingenias.editor.entities.ViewPreferences.ViewType kind){
		this.classname=classname;
		this.kind=kind;
		//System.err.println("created "+classname+kind.name() + this.hashCode());
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RenderIndex){
			RenderIndex index=(RenderIndex)obj;
			return index.classname.equals(classname) && index.kind.equals(kind);
		}
		return super.equals(obj);
	}
	@Override
	public int hashCode() {

		return (classname+kind.name()).hashCode();
	}

	public String toString(){
		return classname+kind;
	}
}
