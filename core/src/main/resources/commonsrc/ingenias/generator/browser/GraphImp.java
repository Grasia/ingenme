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

import ingenias.editor.Editor;
import ingenias.editor.IDEState;
import ingenias.editor.Model;
import ingenias.editor.ModelJGraph;
import ingenias.editor.entities.Entity;
import ingenias.exception.InvalidGraph;
import ingenias.exception.NullEntity;

import java.util.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jgraph.JGraph;
import org.jgraph.graph.BasicMarqueeHandler;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ContainerListener;

class GraphImp
extends AttributedElementImp
implements Graph {

	private ingenias.editor.ModelJGraph mjg;
	private IDEState ids;

	GraphImp(ingenias.editor.ModelJGraph mjg, IDEState ids) {
		super(mjg.getProperties(), mjg,ids);
		this.mjg = mjg;
		this.ids=ids;
	}


	public String getID() {
		return mjg.getID();
	}


	public String getName() {
		return mjg.getID();
	}
	
	 public Vector<GraphEntity> findEntity(String sourceTaskID) {
		 GraphEntity[] entities;
		 Vector<GraphEntity> occurrences=new Vector<GraphEntity>();
		try {
			entities = getEntitiesWithDuplicates();
			 GraphEntity found=null;
			 for (GraphEntity ge:entities){
				 if (ge.getID().equalsIgnoreCase(sourceTaskID)){
					 occurrences.add(ge);
				 }					
			 }
			 return occurrences;
		} catch (NullEntity e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
		return occurrences;
		
	 }

	public String getType() {
		String name = mjg.getClass().getName();
		int endName = name.lastIndexOf("Model");
		int startName = name.lastIndexOf(".") + 1;
		name = name.substring(startName, endName);
		return name;
	}

	public String[] getPath() {
		try {
			return ids.gm.getModelPath(this.
					getName());
		}
		catch (ingenias.exception.NotFound nf) {
			nf.printStackTrace();
		}
		return null;

	}

	public GraphRelationship[] getRelationships() {
		Object[] roots= mjg.getRoots();
		java.util.Vector v = new java.util.Vector();

		boolean found = false;
		int k = 0;
		org.jgraph.graph.DefaultGraphCell dgc = null;
		while (k <  roots.length) {
			Object o = roots[k];

			if (org.jgraph.graph.DefaultGraphCell.class.isAssignableFrom(o.getClass())) {
				dgc = (org.jgraph.graph.DefaultGraphCell) o;
				if (ingenias.editor.entities.NAryEdgeEntity.class.isAssignableFrom(dgc.
						getUserObject().getClass())) {
					ingenias.editor.entities.NAryEdgeEntity ne =
							(ingenias.editor.entities.NAryEdgeEntity) dgc.getUserObject();

					v.add(new GraphRelationshipImp(ne, mjg,ids));
				}

			}
			k++;
		}

		GraphRelationship[] result = new GraphRelationship[v.size()];
		for (k = 0; k < result.length; k++) {
			result[k] = (GraphRelationship) v.elementAt(k);
		}

		return result;

	}


	public GraphEntity[] getEntitiesWithDuplicates()  throws NullEntity{
		Object[] roots= mjg.getRoots();
		java.util.Vector v = new java.util.Vector();

		boolean found = false;
		int k = 0;
		org.jgraph.graph.DefaultGraphCell dgc = null;
		while (k <  roots.length) {
			Object o = roots[k];
			if (o instanceof org.jgraph.graph.DefaultGraphCell) {
				dgc = (org.jgraph.graph.DefaultGraphCell) o;
				if (! (dgc.getUserObject()instanceof ingenias.editor.entities.
						NAryEdgeEntity) &&
						! (dgc.getUserObject()instanceof ingenias.editor.entities.
								RoleEntity)) {
					ingenias.editor.entities.Entity ne =
							(ingenias.editor.entities.Entity) dgc.getUserObject();
					GraphEntity ge=null;

					ge = new GraphEntityImp(ne, dgc,mjg,ids);


					v.add(ge);

				}

			}
			k++;
		}

		GraphEntity[] result = new GraphEntity[v.size()];
		Iterator it = v.iterator();
		k = 0;
		while (it.hasNext()) {
			result[k] = (GraphEntity) it.next();
			k++;
		}
		//	    System.err.println("terminado con" +result.length);
		return result;  
	}


	public GraphEntity[] getEntities() throws NullEntity {
		Object[] roots= mjg.getRoots();
		java.util.Vector v = new java.util.Vector();

		boolean found = false;
		int k = 0;
		org.jgraph.graph.DefaultGraphCell dgc = null;
		while (k <  roots.length) {
			Object o = roots[k];
			if (o instanceof org.jgraph.graph.DefaultGraphCell) {
				dgc = (org.jgraph.graph.DefaultGraphCell) o;
				if (dgc.getUserObject() !=null &&
						! (dgc.getUserObject() instanceof ingenias.editor.entities.
								NAryEdgeEntity) &&
								! (dgc.getUserObject()instanceof ingenias.editor.entities.
										RoleEntity)) {
					ingenias.editor.entities.Entity ne =
							(ingenias.editor.entities.Entity) dgc.getUserObject();
					GraphEntity ge=null;

					ge = new GraphEntityImp(ne, mjg,ids);

					if (!v.contains(ge)) {
						v.add(ge);
					}
				}

			}
			k++;
		}

		GraphEntity[] result = new GraphEntity[v.size()];
		Iterator it = v.iterator();
		k = 0;
		while (it.hasNext()) {
			result[k] = (GraphEntity) it.next();
			k++;
		}
		//    System.err.println("terminado con" +result.length);
		return result;
	}

	private void createSubFolders(File f) {
		if (!f.exists()) {
			createSubFolders(new File(f.getParent()));
			try {
				f.createNewFile();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}

		}

	}

	public void generateImage(String filename) {
		final File target = new File(filename);
		new File(target.getParent()).mkdirs();		
		//final JPanel temp=new JPanel(new BorderLayout());
		//final JGraph njg=this.mjg.cloneJGraph(ids); // the cloned jgraph works bad 

		//temp.add(mjg,BorderLayout.CENTER);
		mjg.setSelectionCells(new Object[0]);
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				@Override
				public void run() {
					mjg.getListenerContainer().setEnabled(true);					
					mjg.getListenerContainer().graphChanged(null); // to refresh the container layout
					mjg.getListenerContainer().setToFrontVisibleChildren();				
				}

			});
			
			SwingUtilities.invokeAndWait(new Runnable(){
				@Override
				public void run() {

					//((ModelJGraph)mjg).createListeners();
					//((ModelJGraph)mjg).enableAllListeners();
					//((ModelJGraph)mjg).getListenerContainer().setEnabled(true);	
					JPanel temp=new JPanel(new BorderLayout());
					Container parent = mjg.getParent();
					if (parent!=null)
						parent.remove(mjg);
					temp.add(mjg);	
					temp.invalidate();
					temp.repaint();					
					//((ModelJGraph)mjg).getListenerContainer().refreshContainer();
					//((ModelJGraph)mjg).getListenerContainer().refreshContainer();
					//((ModelJGraph)mjg).getListenerContainer().setToFrontVisibleChildren();
					ingenias.editor.export.Diagram2SVG.diagram2SVG(temp, target,"png");
					temp.remove(mjg);
					if (parent!=null){
						parent.add(mjg);
						parent.repaint();
					}
				}

			});

		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

	public ingenias.editor.ModelJGraph getGraph() {
		return this.mjg;
	}


	@Override
	public void remove(GraphRelationship rel) {
	 this.mjg.getModel().remove(new Object[]{((GraphRelationshipImp)rel).getDGC()});		
	}


	@Override
	public void remove(GraphEntity ent) {
		 this.mjg.getModel().remove(new Object[]{((GraphEntityImp)ent).getDgc()});		
	}

}
