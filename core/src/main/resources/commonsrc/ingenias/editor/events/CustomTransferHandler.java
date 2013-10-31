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
package ingenias.editor.events;

import ingenias.editor.ModelJGraph;
import ingenias.editor.ObjectManager;
import ingenias.editor.cell.NAryEdge;
import ingenias.editor.entities.Entity;
import ingenias.editor.entities.RoleEntity;
import ingenias.editor.filters.DiagramFilter;
import ingenias.editor.filters.FilterManager;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.GraphTransferHandler;
import org.jgraph.graph.GraphTransferable;
import org.jgraph.graph.ParentMap;

public class CustomTransferHandler extends GraphTransferHandler{

	private static DiagramFilter defaultFilter;

	static {
		try {
			if (FilterManager.getDefaultFilterFromClassLoader(FilterManager.class.getClassLoader())!=null)
				defaultFilter=
				FilterManager.obtainDiagramFilter(
						FilterManager.getDefaultFilterFromClassLoader(FilterManager.class.getClassLoader()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private ObjectManager om;
	private JComponent source;
	private Map lastclones;
	private ModelJGraph originaljgraph;

	public CustomTransferHandler(ObjectManager om, ModelJGraph original){
		this.om=om;
		this.originaljgraph=original;
	}

	/**
	 * This method is modified to store a reference to the original model where the
	 * parent relationship is known
	 */
	@Override

	protected Transferable createTransferable(JComponent c) {
		if (c instanceof ModelJGraph) {
			JGraph graph = (JGraph) c;
			if (!graph.isSelectionEmpty()) {
				originaljgraph=(ModelJGraph) graph;
				return createTransferable(graph, graph.getSelectionCells());
			}
		}
		return null;
	}


	/**
	 * This method makes sure, before pasting is done, that pasted entities types
	 * are going to be inserted and that they do exist in the target ingenias
	 * data model
	 * 	  
	 */
	public boolean importData(JComponent comp, Transferable t) {
		try {
			Hashtable<DefaultGraphCell,DefaultGraphCell> transferredparentship=null;
			Hashtable<String, Hashtable<DefaultGraphCell, Dimension>> entityConstraints=null;
			if (comp instanceof JGraph) {
				JGraph graph = (JGraph) comp;
				GraphModel model = graph.getModel();
				GraphLayoutCache cache = graph.getGraphLayoutCache();
				ModelJGraph mjgraph=(ModelJGraph)graph;

				if (t.isDataFlavorSupported(GraphTransferable.dataFlavor)
						&& graph.isEnabled()) {
					// May be null
					Point p = graph.getUI().getInsertionLocation();

					// Get Local Machine Flavor
					Object obj = t
							.getTransferData(GraphTransferable.dataFlavor);
					GraphTransferable gt = (GraphTransferable) obj;
					transferredparentship=
							(Hashtable<DefaultGraphCell, DefaultGraphCell>) 
							gt.getAttributeMap().get("parentshiprelationships");
					entityConstraints=
							(Hashtable<String, Hashtable<DefaultGraphCell, Dimension>>) 
							gt.getAttributeMap().get("entityconstraints");
					// Get Transferred Cells
					final Object[] cells = gt.getCells();



					// Check if all cells are in the model
					boolean allInModel = true;
					String diagramType=mjgraph.getClass().getSimpleName();
					diagramType=diagramType.substring(0,diagramType.lastIndexOf("Model"));
					for (int i = 0; i < cells.length && allInModel; i++){
						if (cells[i] instanceof DefaultGraphCell &&
								!(cells[i] instanceof NAryEdge) && 
								!(cells[i] instanceof DefaultPort) &&
								!(cells[i] instanceof DefaultEdge)){
							
							ingenias.editor.entities.Entity ent=
									(Entity) ((DefaultGraphCell)cells[i]).getUserObject();
							allInModel=allInModel && 
									defaultFilter.isValidEntity(diagramType, ent.getType());
						} else 
							if (cells[i] instanceof NAryEdge){
								ingenias.editor.entities.Entity ent=
										(Entity) ((DefaultGraphCell)cells[i]).getUserObject();

								allInModel=allInModel && 
										defaultFilter.isValidRelationship(diagramType, ent.getType());
							}						
					}
					if (!allInModel)
						return false;
					else {
						for ( int i = 0; i < cells.length && allInModel; i++){

							final Object currentCell=cells[i];
							Runnable createNewNonExistingEntities=new Runnable(){
								public void run(){
									if (currentCell instanceof DefaultGraphCell &&
											!(currentCell instanceof NAryEdge) && 
											!(currentCell instanceof DefaultEdge) &&
											!(currentCell instanceof DefaultPort)){
										ingenias.editor.entities.Entity ent=
												(Entity) ((DefaultGraphCell)currentCell).getUserObject();
										if ( 
												om.findUserObject(ent.getId()).isEmpty())
											try {
												om.insert(ent);
											} catch (SecurityException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (IllegalArgumentException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (NoSuchFieldException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (NoSuchMethodException e) {
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

								}
							};
							SwingUtilities.invokeLater(createNewNonExistingEntities);
							// now, all parent relationship is created 

						}	
					}
				}
				boolean result= super.importData(comp, t);
				if (entityConstraints!=null){
					for (Object original:lastclones.keySet()){
						// now, parent relationship is rebuid
						if (original instanceof DefaultGraphCell) {
							((ModelJGraph)graph).getListenerContainer().copyConstraints(
									(DefaultGraphCell)(lastclones.get(original)),
									(DefaultGraphCell)(original),
									getChildren(original, transferredparentship),
									lastclones,entityConstraints);
							for (DefaultGraphCell originalchild:getChildren(original, transferredparentship)){
								((ModelJGraph)graph).getListenerContainer().setParent((DefaultGraphCell)(lastclones.get(originalchild)),
										(DefaultGraphCell)(lastclones.get(original)) );

							}

						}
					}
					graph.setSelectionCells(lastclones.values().toArray());
				}
				return result;
			}

		} catch (Exception exception) {
			// System.err.println("Cannot import: " +
			// exception.getMessage());
			exception.printStackTrace();
		}
		return false;

	}

	public Vector<DefaultGraphCell> getChildren(Object changed,Hashtable<DefaultGraphCell,DefaultGraphCell> parentship) {
		Vector<DefaultGraphCell> children=new Vector<DefaultGraphCell>();
		for (DefaultGraphCell possibleChild:parentship.keySet()){
			if (parentship.get(possibleChild)==changed){
				children.add(possibleChild);
			}
		}
		return children;
	}

	/**
	 * This method overrides the original one because it is necessary to access
	 * to the map of cloned cells, and then, rebuild the parent relationship
	 * when pasting cells. The matching between cloned vs original will be stored
	 * in the field lastclones.
	 */

	@Override
	protected void handleExternalDrop(JGraph graph, Object[] cells, Map nested,
			ConnectionSet cs, ParentMap pm, double dx, double dy) {
		// Removes all connections for which the port is neither
		// passed in the parent map nor already in the model.
		Iterator it = cs.connections();
		while (it.hasNext()) {
			ConnectionSet.Connection conn = (ConnectionSet.Connection) it
					.next();
			if (!pm.getChangedNodes().contains(conn.getPort())
					&& !graph.getModel().contains(conn.getPort())) {
				it.remove();
			}
		}
		Vector<Object> dashedEdgesToRemove=new Vector<Object>();
		for (Object cell:cells){
			if (cell instanceof DefaultEdge){
				DefaultEdge de=(DefaultEdge)cell;
				if (!(((DefaultPort)de.getSource()).getParent() instanceof NAryEdge) &&
						!(((DefaultPort)de.getTarget()).getParent() instanceof NAryEdge))
					dashedEdgesToRemove.add(de);
			}
		}

		HashSet<Object> finalCells=new HashSet<Object>();
		ListenerContainer lc = ((ModelJGraph)graph).getListenerContainer();

		Hashtable<DefaultGraphCell, DefaultGraphCell> transferredparentship = (Hashtable<DefaultGraphCell, DefaultGraphCell>) nested.get("parentshiprelationships");
		for (Object ccell:cells){
			// Replicating all child-parent structure of each copied element. 
			// JGraph handles one parent-child and ingenme the parent-child managed by listenercontainer
			HashSet<DefaultGraphCell> containerChildren = lc.getRecursivelyChildren((DefaultGraphCell) ccell,transferredparentship);			
			for (DefaultGraphCell containerChild:containerChildren){
				if (graph.getModel().getChildCount(containerChild)>0){
					for (int k=0;k<graph.getModel().getChildCount(containerChild);k++){
						finalCells.add(graph.getModel().getChild(containerChild, k));
					}
				}
			}
			finalCells.addAll(containerChildren);
			finalCells.add(ccell);
			//}
		}
		finalCells.removeAll(dashedEdgesToRemove);
		cells=finalCells.toArray();
		lastclones = graph.cloneCells(cells);
		graph.getGraphLayoutCache().insertClones(cells, lastclones, nested, cs, pm,
				dx, dy);
	}

	protected GraphTransferable create(JGraph graph, Object[] cells,
			Map viewAttributes, Rectangle2D bounds, ConnectionSet cs,
			ParentMap pm) {
		GraphTransferable gt;
		viewAttributes.put("parentshiprelationships", ((ModelJGraph)graph).getListenerContainer().getParentRelationships());
		viewAttributes.put("entityconstraints", ((ModelJGraph)graph).getListenerContainer().getEntityConstraints());
		return new GraphTransferable(cells, viewAttributes, bounds, cs, pm);
	}




}
