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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.jgraph.graph.*;

import ingenias.editor.IDEState;
import ingenias.editor.ModelJGraph;
import ingenias.editor.TypedVector;
import ingenias.editor.entities.*;
import ingenias.exception.*;
import ingenias.exception.NotFound;

public class GraphEntityImp extends AttributedElementImp implements GraphEntity{

	private IDEState ids;
	private Browser browser;

	public org.jgraph.graph.DefaultGraphCell getDgc() {
		return dgc;
	}

	public void setDgc(org.jgraph.graph.DefaultGraphCell dgc) {
		this.dgc = dgc;
	}

	private ingenias.editor.entities.Entity ent;
	private ModelJGraph graph;
	private org.jgraph.graph.DefaultGraphCell dgc;

	GraphEntityImp(ingenias.editor.entities.Entity ent,  org.jgraph.graph.DefaultGraphCell dgc,
			ModelJGraph graph, IDEState ids) throws NullEntity{
		super(ent,graph,ids);
		if (ent==null) throw new ingenias.exception.NullEntity();
		this.ent=ent;
		this.graph=graph;
		this.dgc=dgc;
		this.ids=ids;
		if (graph==null){
			throw new RuntimeException("Graph is null in entity "+ent+ " when creating a GraphEntityImp");
		}
		if (ids==null)
			throw new RuntimeException("The ids parameter cannot be null");
		browser=new BrowserImp(ids);
	}

	GraphEntityImp(ingenias.editor.entities.Entity ent,
			ModelJGraph graph, IDEState ids) throws NullEntity{
		super(ent,graph,ids);
		if (ent==null) throw new ingenias.exception.NullEntity();
		this.ent=ent;
		this.graph=graph;
		dgc=this.getCell(graph);
		this.ids=ids;
		if (graph==null){
			throw new RuntimeException("Graph is null in entity "+ent+ " when creating a GraphEntityImp");
		}
		if (ids==null)
			throw new RuntimeException("The ids parameter cannot be null");
		this.browser=new BrowserImp(ids);
	}


	private Vector getCells(org.jgraph.JGraph graph){		
		List roots=new Vector(((DefaultGraphModel)graph.getModel()).getRoots());
		Vector v=new Vector();

		boolean found=false;
		int k=0;
		Vector dgcs=new Vector();
		org.jgraph.graph.DefaultGraphCell dgc=null;

		while (k<roots.size()){
			Object o=roots.get(k);
			if (o instanceof org.jgraph.graph.DefaultGraphCell){
				dgc=(org.jgraph.graph.DefaultGraphCell)o;
				if (dgc.getUserObject()!=null)
					found=((ingenias.editor.entities.Entity)dgc.getUserObject()).getId().equals(ent.getId());
				if (found)
					dgcs.add(dgc);
			}
			k++;
		}

		return dgcs;
	}


	private DefaultGraphCell getCell(org.jgraph.JGraph graph){
		List roots=new Vector(((DefaultGraphModel)graph.getModel()).getRoots());
		Vector v=new Vector();

		boolean found=false;
		int k=0;
		Vector dgcs=getCells(graph);
		org.jgraph.graph.DefaultGraphCell dgc=null;
		while (k<roots.size() &&!found){
			Object o=roots.get(k);
			if (o instanceof org.jgraph.graph.DefaultGraphCell){
				dgc=(org.jgraph.graph.DefaultGraphCell)o;
				if (dgc.getUserObject()!=null)
				found=((ingenias.editor.entities.Entity)dgc.getUserObject()).getId().equals(ent.getId());
			}
			k++;
		}

		return dgc;
	}


	private DefaultGraphCell getExtreme(org.jgraph.graph.Edge edge){
		if (!(((DefaultGraphCell)((DefaultPort)edge.getTarget()).getParent()).getUserObject()
				instanceof ingenias.editor.entities.NAryEdgeEntity))
			return (DefaultGraphCell)((DefaultPort)edge.getSource()).getParent();
		else
			return (DefaultGraphCell)((DefaultPort)edge.getTarget()).getParent();
	}




	private HashSet<GraphRelationship> getRelationshipsFromAGraph(ingenias.editor.ModelJGraph graph){
		HashSet<GraphRelationship> v=new HashSet<GraphRelationship>();
		Enumeration dgcs=this.getCells(graph).elements();
		while (dgcs.hasMoreElements()){

			DefaultGraphCell dgc=(DefaultGraphCell)dgcs.nextElement();

			if (dgc!=null && dgc.getChildren()!=null){
				Iterator ports=dgc.getChildren().iterator();
				while (ports.hasNext()){
					Object port=ports.next();
					Iterator it=graph.getModel().edges(port);

					while (it.hasNext()){
						org.jgraph.graph.Edge current=
							(org.jgraph.graph.Edge)it.next();
						DefaultGraphCell extr=this.getExtreme(current);
						ingenias.editor.entities.NAryEdgeEntity nary=
							(ingenias.editor.entities.NAryEdgeEntity)extr.getUserObject();
						v.add(new GraphRelationshipImp(nary,graph,ids));
					}


				}
			}
		}
		return v;


	}

	public Vector<GraphRelationship> getAllRelationships(){
		HashSet<GraphRelationship> result=new HashSet<GraphRelationship>();
		Graph[] g=null;

		g = browser.getGraphs();

		for (int k=0;k<g.length;k++){
			HashSet rel=this.getRelationshipsFromAGraph(((GraphImp)g[k]).getGraph());
			result.addAll(rel);
		}
		return new Vector<GraphRelationship>(result);
	}

	public Vector getAllRelationships(String relType){
		HashSet<GraphRelationship> result=new HashSet<GraphRelationship>();
		Graph[] g=null;

		g = browser.getGraphs();

		for (int k=0;k<g.length;k++){
			HashSet<GraphRelationship> rel=this.getRelationshipsFromAGraph(((GraphImp)g[k]).getGraph());
			for (GraphRelationship gr:rel){
				if (gr.getType().equalsIgnoreCase(relType))
					result.add(gr);
			}
		}
		return new Vector<GraphRelationship>(result);
	}



	public GraphRelationship[] getRelationships(){	

		Vector v=new Vector();

		Iterator ports=dgc.getChildren().iterator();
		while (ports.hasNext()){
			Object port=ports.next();
			Iterator it=graph.getModel().edges(port);

			while (it.hasNext()){

				org.jgraph.graph.Edge current=
					(org.jgraph.graph.Edge)it.next();
				DefaultGraphCell extr=this.getExtreme(current);

				ingenias.editor.entities.NAryEdgeEntity nary=
					(ingenias.editor.entities.NAryEdgeEntity)extr.getUserObject();
				v.add(nary);
			}

		}

		GraphRelationship[] result=new GraphRelationship[v.size()];
		for (int k=0;k<result.length;k++){
			result[k]=new GraphRelationshipImp((NAryEdgeEntity)v.elementAt(k),graph,ids);
		}

		return result;
	}


	public String getType(){
		String name=ent.getClass().getName();
		int endName=name.length();//lastIndexOf("Entity");
		int startName=name.lastIndexOf(".")+1;
		name=name.substring(startName,endName);
		return name;

	}

	public boolean equals(Object o){
		if (o instanceof GraphEntityImp){
			return ((GraphEntityImp)o).ent.getId().equalsIgnoreCase(ent.getId());
		}
		else return super.equals(o);
	}

	public String toString(){
		return ent.getType()+":"+ent.getId();
	}

	public int hashCode(){
		return this.ent.hashCode();
	}

	public void setAttribute(GraphAttribute ga) throws InvalidAttribute{
		try {
			GraphAttribute oldga=this.getAttributeByName(ga.getName());
			((GraphAttributeImp)oldga).setValue(((GraphAttributeImp)ga).getValue());
			Object nvalue=((GraphAttributeImp)ga).getValue();
			if (nvalue instanceof GraphCollection){
				nvalue=((GraphCollectionImp)nvalue).getValue();
				Class nvalueclass=((TypedVector)nvalue).getType();
				Class entclass=this.ent.getClass();
				Method m=entclass.getMethod("add"+ga.getName(),
						new Class[]{nvalueclass});
				TypedVector tv=(TypedVector)nvalue;
				for (int k=0;k<tv.size();k++){
					m.invoke(ent,new Object[]{tv.elementAt(k)});
				}
			} else {
				if (nvalue instanceof GraphEntity){
					nvalue=((GraphEntityImp)nvalue).getEntity();	
				}
				Class entclass=this.ent.getClass();
				Method m=entclass.getMethod("set"+ga.getName(),
						new Class[]{nvalue.getClass()});

				m.invoke(ent,new Object[]{nvalue});
			}
		} catch (NotFound e) {				
			throw new InvalidAttribute(e);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidAttribute(e);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidAttribute(e);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidAttribute(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidAttribute(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidAttribute(e);
		}	
	}

	public Entity getEntity(){
		return this.ent;
	}

	@Override
	public String getID() {
		
		return ent.getId();
	}

	@Override
	public GraphRelationship[] getRelationships(String type) {
		Vector v=new Vector();

		Iterator ports=dgc.getChildren().iterator();
		while (ports.hasNext()){
			Object port=ports.next();
			Iterator it=graph.getModel().edges(port);

			while (it.hasNext()){

				org.jgraph.graph.Edge current=
					(org.jgraph.graph.Edge)it.next();
				DefaultGraphCell extr=this.getExtreme(current);

				ingenias.editor.entities.NAryEdgeEntity nary=
					(ingenias.editor.entities.NAryEdgeEntity)extr.getUserObject();
				if (nary.getType().equalsIgnoreCase(type))
				 v.add(nary);
			}

		}

		GraphRelationship[] result=new GraphRelationship[v.size()];
		for (int k=0;k<result.length;k++){
			result[k]=new GraphRelationshipImp((NAryEdgeEntity)v.elementAt(k),graph,ids);
		}

		return result;
	}



}