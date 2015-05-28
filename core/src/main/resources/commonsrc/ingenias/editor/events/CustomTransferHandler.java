/** 
 * Copyright (C) 2010  Jorge J. Gomez-Sanz
 * 
 * This file is part of the INGENME tool. INGENME is an open source meta-editor
 * which produces customized editors for user-defined modeling languages
 * 
 * This files is a modified version of JGraphs's TransferHandler
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
import java.awt.Rectangle;
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
import javax.swing.TransferHandler.TransferSupport;

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
		boolean result=false;
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
						Rectangle visibleRect = this.originaljgraph.getVisibleRect();
						Point leftMost=new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
						final Hashtable<DefaultGraphCell,AttributeMap> atts=new Hashtable<DefaultGraphCell,AttributeMap>(); 
						for (Object original:cells){
							if (original instanceof DefaultGraphCell &&
									!(original instanceof NAryEdge) && 
									!(original instanceof DefaultEdge) &&
									!(original instanceof DefaultPort)){
								DefaultGraphCell dgc=(DefaultGraphCell)original;
								Rectangle2D bounds = GraphConstants.getBounds(dgc.getAttributes());
								if (bounds!=null) {
									if (bounds.getX()<leftMost.x)
										leftMost.x=(int)bounds.getX();
									if (bounds.getY()<leftMost.y)
										leftMost.y=(int)bounds.getY();
								}					
							}						
						}

						int dx=visibleRect.x-leftMost.x+20;
						int dy=visibleRect.y-leftMost.y+20;

						if (leftMost.x>visibleRect.x && leftMost.x<visibleRect.x+visibleRect.width)
							dx=0;

						if (leftMost.y>visibleRect.y && leftMost.y<visibleRect.y+visibleRect.height)
							dy=0;					

				
						result= superimportData(comp, t,dx,dy);
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
					}
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

	// NOTE: 1. We abuse return value to signal removal to the sender.
	// 2. We always clone cells when transferred between two models
	// This is because they contain parts of the model's data.
	// 3. Transfer is passed to importDataImpl for unsupported
	// dataflavors (becaue method may return false, see 1.)
	public boolean superimportData(JComponent comp, Transferable t, double tdx, double tdy) {
		try {
			if (comp instanceof JGraph) {
				JGraph graph = (JGraph) comp;
				GraphModel model = graph.getModel();
				GraphLayoutCache cache = graph.getGraphLayoutCache();
				if (t.isDataFlavorSupported(GraphTransferable.dataFlavor)
						&& graph.isEnabled()) {
					// May be null
					Point p = graph.getUI().getInsertionLocation();

					// Get Local Machine Flavor
					Object obj = t
							.getTransferData(GraphTransferable.dataFlavor);
					GraphTransferable gt = (GraphTransferable) obj;

					// Get Transferred Cells
					Object[] cells = gt.getCells();

					// Check if all cells are in the model
					boolean allInModel = true;
					for (int i = 0; i < cells.length && allInModel; i++)
						allInModel = allInModel && model.contains(cells[i]);

					// Count repetitive inserts
					if (in == cells)
						inCount++;
					else
						inCount = (allInModel) ? 1 : 0;
					in = cells;

					// Delegate to handle
					if (p != null && in == out
							&& graph.getUI().getHandle() != null) {
						int mod = (graph.getUI().getDropAction() == TransferHandler.COPY) ? InputEvent.CTRL_MASK
								: 0;
						graph.getUI().getHandle().mouseReleased(
								new MouseEvent(comp, 0, 0, mod, p.x, p.y, 1,
										false));
						return false;
					}

					// Get more Transfer Data
					Rectangle2D bounds = gt.getBounds();
					Map nested = gt.getAttributeMap();
					ConnectionSet cs = gt.getConnectionSet();
					ParentMap pm = gt.getParentMap();

					// Move across models or via clipboard always clones
					if (!allInModel
							|| p == null
							|| alwaysReceiveAsCopyAction
							|| graph.getUI().getDropAction() == TransferHandler.COPY) {

						// Translate cells
						double dx = 0, dy = 0;

						// Cloned via Drag and Drop
						if (nested != null) {
							if (p != null && bounds != null) {
								Point2D insert = graph.fromScreen(graph
										.snap((Point2D) p.clone()));
								dx = insert.getX() - bounds.getX();
								dy = insert.getY() - bounds.getY();

								// Cloned via Clipboard
							} else {
								Point2D insertPoint = getInsertionOffset(graph,
										inCount, bounds);
								if (insertPoint != null) {
									dx = insertPoint.getX();
									dy = insertPoint.getY();
								}
							}
						}

						handleExternalDrop(graph, cells, nested, cs, pm, tdx
								+dx, tdy+dy);

						// Signal sender to remove only if moved between
						// different models
						return (graph.getUI().getDropAction() == TransferHandler.MOVE && !allInModel);
					}

					// We are dealing with a move across multiple views
					// of the same model
					else {

						// Moved via Drag and Drop
						if (p != null) {
							// Scale insertion location
							Point2D insert = graph.fromScreen(graph
									.snap(new Point(p)));

							// Compute translation vector and translate all
							// attribute maps.
							if (bounds != null && nested != null) {
								double dx = insert.getX() - bounds.getX();
								double dy = insert.getY() - bounds.getY();
								AttributeMap.translate(nested.values(), dx, dy);
							} else if (bounds == null) {

								// Prevents overwriting view-local
								// attributes
								// for known cells. Note: This is because
								// if bounds is null, the caller wants
								// to signal that the bounds were
								// not available, which is typically the
								// case if no graph layout cache
								// is at hand. To avoid overriding the
								// local attributes such as the bounds
								// with the default bounds from the model,
								// we remove all attributes that travel
								// along with the transferable. (Since
								// all cells are already in the model
								// no information is lost by doing this.)
								double gs2 = 2 * graph.getGridSize();
								nested = new Hashtable();
								Map emptyMap = new Hashtable();
								for (int i = 0; i < cells.length; i++) {

									// This also gives us the chance to
									// provide useful default location and
									// resize if there are no useful bounds
									// that travel along with the cells.
									if (!model.isEdge(cells[i])
											&& !model.isPort(cells[i])) {

										// Check if there are useful bounds
										// defined in the model, otherwise
										// resize,
										// because the view does not yet exist.
										Rectangle2D tmp = graph
												.getCellBounds(cells[i]);
										if (tmp == null)
											tmp = GraphConstants
											.getBounds(model
													.getAttributes(cells[i]));

										// Clone the rectangle to force a
										// repaint
										if (tmp != null)
											tmp = (Rectangle2D) tmp.clone();

										Hashtable attrs = new Hashtable();
										Object parent = model
												.getParent(cells[i]);
										if (tmp == null) {
											tmp = new Rectangle2D.Double(p
													.getX(), p.getY(), gs2 / 2,
													gs2);
											GraphConstants.setResize(attrs,
													true);

											// Shift
											p.setLocation(p.getX() + gs2, p
													.getY()
													+ gs2);
											graph.snap(p);
											// If parent processed then childs
											// are already located
										} else if (parent == null
												|| !nested
												.keySet()
												.contains(
														model
														.getParent(cells[i]))) {
											CellView view = graph
													.getGraphLayoutCache()
													.getMapping(cells[i], false);
											if (view != null && !view.isLeaf()) {
												double dx = p.getX()
														- tmp.getX();
												double dy = p.getY()
														- tmp.getY();
												GraphLayoutCache
												.translateViews(
														new CellView[] { view },
														dx, dy);
											} else {
												tmp.setFrame(p.getX(),
														p.getY(), tmp
														.getWidth(),
														tmp.getHeight());
											}

											// Shift
											p.setLocation(p.getX() + gs2, p
													.getY()
													+ gs2);
											graph.snap(p);
										}
										GraphConstants.setBounds(attrs, tmp);
										nested.put(cells[i], attrs);
									} else {
										nested.put(cells[i], emptyMap);
									}
								}
							}

							// Edit cells (and make visible)
							cache.edit(nested, null, null, null);
						}

						// Select topmost cells in group-structure
						graph.setSelectionCells(DefaultGraphModel
								.getTopmostCells(model, cells));

						// Don't remove at sender
						return false;
					}
				} else
					return importDataImpl(comp, t);
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
