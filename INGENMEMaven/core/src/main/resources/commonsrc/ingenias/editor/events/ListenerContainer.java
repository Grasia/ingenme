package ingenias.editor.events;

import ingenias.editor.Model;
import ingenias.editor.ModelJGraph;
import ingenias.editor.cell.CompositeRenderer;
import ingenias.editor.cell.NAryEdge;
import ingenias.editor.entities.Entity;
import ingenias.editor.rendererxml.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.CellRendererPane;
import javax.swing.SwingUtilities;

import org.jgraph.JGraph;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.graph.CellView;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphContext;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.VertexView;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The listener assumes only one movement is necessary each time to layout the
 * diagram as desired. It will not work well when more than one element is
 * moved.
 * 
 * @author jj
 * 
 */
public class ListenerContainer implements GraphModelListener {

	private DefaultGraphModel dgm;
	private GraphLayoutCache glc;
	private ModelJGraph jg;
	private boolean enabled = true;
	// To store the parent-children relationship between containers and
	// contained elements
	Hashtable<DefaultGraphCell, DefaultGraphCell> parentship = new Hashtable<DefaultGraphCell, DefaultGraphCell>();
	// this data serves to avoid overlapping of leafs
	private Hashtable<DefaultGraphCell, Rectangle2D> occupiedpositions = new Hashtable<DefaultGraphCell, Rectangle2D>();

	// to store the constraints among changes of representations
	private Hashtable<String, Hashtable<DefaultGraphCell, Dimension>> entityConstraints = new Hashtable<String, Hashtable<DefaultGraphCell, Dimension>>();
	// auxiliary structure to remember which internal node of a container was
	// linking with
	// what external (visible) node. The elements are
	// Key<invisibleelement,defaultedge connecting
	// the visible parent with the other external visible extreme of a
	// relationship>
	private Vector<Key> substitutedNode = new Vector<Key>();
	// auxiliary structure to remember that the parent of an invisible element
	// is already
	// connected to the element the invisible element was connected to. The
	// connection is made
	// through a dashed defaultedge. The key is
	// Key<parentcell,visibleconnectedelement> and the
	// value a defaultedge
	private Hashtable<Key, DefaultGraphCell> substituteEdge = new Hashtable<Key, DefaultGraphCell>();

	public ListenerContainer(DefaultGraphModel dgm, GraphLayoutCache glc,
			ModelJGraph jg) {
		this.dgm = dgm;
		this.glc = glc;
		this.jg = jg;
	}


	@Override
	public synchronized void graphChanged(final GraphModelEvent arg0) {
		if (arg0!=null && arg0.getChange().getRemoved() != null
				&& arg0.getChange().getRemoved().length > 0) {
			for (Object removed : arg0.getChange().getRemoved()) {
				parentship.remove(removed);
			}
		}
		if (enabled) {
			enabled = false; // this serves to ignore the notification when this
			// listener performs changes in the model
			// it listens again after suggested changes are applied
			jg.disableChangeEntityLocation();
			jg.disableNARYEdgeLocation();
			initializeOccupiedPositions();

			if (arg0!=null){
				final Object[] changedcollection = arg0.getChange().getChanged();
				final Hashtable<DefaultGraphCell, Map> map = new Hashtable<DefaultGraphCell, Map>();

				// first, move all children to where parents are
				Hashtable<Object, Object> hasAsParent = new Hashtable<Object, Object>();
				for (Object changed : changedcollection) {
					for (Object candidate : changedcollection) {
						if (isAncestor(candidate, changed)) {
							hasAsParent.put(changed, candidate);
						}
					}
				}
				Vector<Object> globalRoots = new Vector<Object>();
				for (Object changed : changedcollection) {
					if (!hasAsParent.containsKey(changed)) {
						globalRoots.add(changed);
					}
				}

				for (Object changed : changedcollection)
					if ((parentship.containsValue(changed) || parentship
							.containsKey(changed))
							&& (Map) arg0.getChange().getAttributes() != null) { // no
						// other
						// changes
						// have
						// been
						// applied){
						// move all children when parent moves
						Vector<DefaultGraphCell> children = getChildren(changed);
						insertNAryEdges(children);
						// System.err.println("moving "+children);
						Map attsparent = (Map) (dgm.getAttributes(changed));
						Map newAtts = (Map) arg0.getChange().getAttributes()
								.get(changed);
						if (newAtts!=null && GraphConstants.getBounds(newAtts) != null)
							moveChildrenToParent(map, children, changed,
									attsparent, newAtts);
					}
				applyChanges(map); // applies these translations				
			}
			//	if (ModelJGraph.getEnabledAllListeners())
			refreshContainer();

		}

	}


	public void refreshContainer() {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				/*
				 * try { Thread.currentThread().sleep(1000); } catch
				 * (InterruptedException e) { // TODO Auto-generated catch
				 * block e.printStackTrace(); }
				 */
				// now apply the constraints of the different panels
				try {
					if (!enabled){
						hideOrShowChildrenAndDrawExtraEdges();
						// toFront contains the leaves. Recursively invoking toback will
						// cause a reorder
						// of nodes so that the deepes are situated the first
						setToFrontVisibleChildren();
						processInternalConstraints();
						jg.enableChangeEntityLocation();
						jg.enableNARYEdgeLocation();

					}

				} catch (Throwable t) {
					t.printStackTrace();
				}

				// applyChanges(nmap); // applies changes to satisfy
				// constraints
				enabled = true;
			}
		});
	}

	private void insertNAryEdges(Vector<DefaultGraphCell> children) {

		HashSet<NAryEdge> newNAryEdges = new HashSet<NAryEdge>();
		for (DefaultGraphCell dgc : children) {

			if (dgc.getChildCount()>0){
				Iterator it = jg.getModel().edges(dgc.getFirstChild()); // takes the
				// port of
				// dgc

				while (it.hasNext()) {

					DefaultEdge edge = (DefaultEdge) it.next();
					DefaultGraphCell  otherExtreme1 =CenterRelationships.getCellDefaultGraphCellFromDefaultEdge(edge,(Model) jg.getModel());
					NAryEdge naryEdge=CenterRelationships.getCellNAryEdgeFromDefaultEdge(edge,(Model) jg.getModel());
					if(naryEdge!=null){ // This implies the defaultedge connects two cells. It is the artifical edge
						// representing hidden relationships within containers
						GraphCell[] otherExtremes = naryEdge.getObjects();
						boolean allIn=true;
						for (GraphCell gc:otherExtremes){
							allIn=allIn && children.contains(gc);
						}
						if (allIn)
							newNAryEdges.add(naryEdge);
					}
					/*if (otherExtreme1.equals(dgc)) {
					GraphCell[] otherExtremes = naryEdge.getObjects();
					boolean allIn=true;
					for (GraphCell gc:otherExtremes){
						allIn=allIn && children.contains(gc);
					}
					if (allIn)
						newNAryEdges.add(naryEdge);
				} else {
					GraphCell[] otherExtremes = naryEdge.getObjects();
					boolean allIn=true;
					for (GraphCell gc:otherExtremes){
						allIn=allIn && children.contains(gc);
					}
					if (allIn)
						newNAryEdges.add(naryEdge);
				}*/

				}
			}

		}
		children.addAll(newNAryEdges);
	}

	private boolean isAncestor(Object candidate, Object changed) {
		if (parentship.containsKey(changed)
				&& !parentship.get(changed).equals(candidate)) {
			return isAncestor(candidate, parentship.get(changed));
		} else if (parentship.containsKey(changed)
				&& parentship.get(changed).equals(candidate))
			return true;
		else
			return false;

	}

	private boolean constrained(Object changed) {
		boolean constrained = false;

		if (changed instanceof DefaultGraphCell) {
			Hashtable<String, CollectionPanel> containers = parentHasVisibleContainers(parentship
					.get(changed));
			if (!containers.isEmpty()) {
				if (parentship.containsKey(changed)) {
					for (String fieldname : containers.keySet()) {

						Enumeration containedelementsInContainer = obtainEnumerationOfCollectionField(
								changed, fieldname);
						if (containedelementsInContainer != null) {
							boolean found = false;
							while (containedelementsInContainer
									.hasMoreElements() && !found) {
								found = containedelementsInContainer
										.nextElement().equals(
												((DefaultGraphCell) changed)
												.getUserObject());
							}
							if (found) {
								constrained = true;
							}
						}
					}
				}
			}
		}
		return constrained;

	}

	private void processInternalConstraints() {
		// now assume all translations have been made properly and take bound
		// values from the new map instead
		// of the actual one
		Hashtable<DefaultGraphCell, Map> map = new Hashtable<DefaultGraphCell, Map>();
		for (int k = 0; k < 5; k++) {
			for (Object changed : getRoots(k)) {
				// System.err.println("Processing " + changed);
				if (changed instanceof DefaultGraphCell) {
					Hashtable<String, CollectionPanel> containers = parentHasVisibleContainers(parentship
							.get(changed));
					if (!containers.isEmpty()) {
						if (parentship.containsKey(changed)) {
							// it has a father. Move the children without going
							// outside the father
							Map attschildren = (Map) (dgm
									.getAttributes(changed));
							Map attsparent = (Map) (dgm
									.getAttributes(parentship.get(changed)));

							Rectangle2D coordChildren = GraphConstants
									.getBounds(attschildren);
							Rectangle2D coordParent = GraphConstants
									.getBounds(attsparent);
							if (coordChildren != null && coordParent != null) {
								for (String fieldname : containers.keySet()) {
									coordParent = GraphConstants
											.getBounds(attsparent);

									Enumeration containedelementsInContainer = obtainEnumerationOfCollectionField(
											changed, fieldname);
									if (containedelementsInContainer != null) {
										boolean found = false;
										while (containedelementsInContainer
												.hasMoreElements() && !found) {
											found = containedelementsInContainer
													.nextElement()
													.equals(((DefaultGraphCell) changed)
															.getUserObject());
										}
										if (found && jg
												.getModel()
												.getAttributes(
														parentship
														.get(changed))!=null
														&& ((Map) jg
																.getModel()
																.getAttributes(
																		parentship
																		.get(changed))
																		.get("subcomponentbounds"))!=null) {
											Rectangle fieldLoc = (Rectangle) ((Map) jg
													.getModel()
													.getAttributes(
															parentship
															.get(changed))
															.get("subcomponentbounds"))
															.get(fieldname);
											if (fieldLoc != null) {
												if (containers
														.get(fieldname)
														.getClass()
														.equals(ContainerPanel.class)) {

													// System.err.println("Moving children "+parentship.get(changed)+"-"+coordParent+":"+fieldLoc+":"+changed+"-"+coordChildren);
													coordParent = new Rectangle(
															(int) (coordParent
																	.getX() + fieldLoc
																	.getX()),
																	(int) (coordParent
																			.getY() + fieldLoc
																			.getY()),
																			(int) (fieldLoc
																					.getWidth()),
																					(int) (fieldLoc
																							.getHeight()));
													if (!coordParent
															.contains(coordChildren)) {
														// System.err.println("Moving children "+parentship.get(changed)+"-"+coordParent+":"+changed+"-"+coordChildren);
														Rectangle nbound = moveChildWithinParent(
																map,
																(DefaultGraphCell) changed,
																coordChildren,
																coordParent,
																fieldname);
														/*jg.getGraphics()
																.setColor(
																		Color.blue);
														jg.getGraphics()
																.drawRect(
																		(int) coordParent
																				.getX(),
																		(int) coordParent
																				.getY(),
																		(int) coordParent
																				.getWidth(),
																		(int) coordParent
																				.getHeight());*/

													}

												}
												if (containers
														.get(fieldname)
														.getClass()
														.equals(ExternalContainerPanel.class)) {

													if (isNotExternal(
															coordChildren,
															coordParent)) {

														moveChildExternalParent(
																map,
																(DefaultGraphCell) changed,
																coordChildren,
																coordParent,
																fieldname);
													}
												}
												if (containers
														.get(fieldname)
														.getClass()
														.equals(VerticalContainerPanel.class)) {

													coordParent = new Rectangle(
															(int) (coordParent
																	.getX() + fieldLoc
																	.getX()),
																	(int) (coordParent
																			.getY() + fieldLoc
																			.getY()),
																			(int) (fieldLoc
																					.getWidth()),
																					(int) (fieldLoc
																							.getHeight()));
													processVerticalPanel(map,
															changed,
															coordChildren,
															coordParent,
															fieldname);
												}
												if (containers
														.get(fieldname)
														.getClass()
														.equals(HorizontalContainerPanel.class)) {

													coordParent = new Rectangle(
															(int) (coordParent
																	.getX() + fieldLoc
																	.getX()),
																	(int) (coordParent
																			.getY() + fieldLoc
																			.getY()),
																			(int) (fieldLoc
																					.getWidth()),
																					(int) (fieldLoc
																							.getHeight()));
													processHorizontalPanel(map,
															changed,
															coordChildren,
															coordParent,
															fieldname);
												}
											}
										}
									}
								}
							}
						}

					}

				}
			}
			moveChildrenToNewLocation(map);
			//ChangeNARYEdgeLocation.solveOverlappings(jg, map);
			applyChanges(map);
			map = new Hashtable<DefaultGraphCell, Map>();

		}
	}

	private void moveChildrenToNewLocation(Hashtable<DefaultGraphCell, Map> map) {
		Vector<DefaultGraphCell> collectionkey = new Vector<DefaultGraphCell>(
				map.keySet());
		for (Object key : collectionkey) {
			Vector<DefaultGraphCell> children = getChildren(key);

			// System.err.println("moving "+children);
			Map attsparent = (Map) (dgm.getAttributes(key));
			Map newAtts = (Map) map.get(key);
			if (GraphConstants.getBounds(newAtts) != null)
				moveChildrenToParent(map, children, key, attsparent, newAtts);
		}

	}

	private Vector<Object> getRoots(int k) {
		// TODO Auto-generated method stub
		Vector<Object> result = new Vector<Object>();
		Object[] roots = dgm.getAll(dgm);
		for (Object root :roots) {
			if (getDepth(root) == k)
				result.add(root);
		}
		return result;

	}

	private int getDepth(Object root) {
		if (parentship.containsKey(root))
			return 1 + getDepth(parentship.get(root));
		else
			return 0;
	}

	private Rectangle2D processHorizontalPanel(
			final Hashtable<DefaultGraphCell, Map> map, Object changed,
			Rectangle2D coordChildren, Rectangle2D coordParent, String fieldname) {

		if (!isHorizontal(coordChildren, coordParent)
				|| overlap(coordChildren.getBounds(),
						(DefaultGraphCell) changed)) {

			Rectangle nbound = moveChildWithinHorizontalParent(map,
					(DefaultGraphCell) changed, coordChildren, coordParent,
					fieldname);
			Rectangle lastbound = nbound; // to remember the optimal location
			Rectangle repeatedbound = null; // to remember last location
			Rectangle2D initloc = coordChildren;
			// first we move left the location of the cell until a suitable
			// place is found
			while (overlap(nbound, (DefaultGraphCell) changed)
					&& (coordChildren.getX() > coordParent.getX())) {
				repeatedbound = nbound;
				coordChildren = new Rectangle((int) coordChildren.getX() - 10,
						(int) coordChildren.getY(),
						(int) coordChildren.getWidth(),
						(int) coordChildren.getHeight());
				nbound = moveChildWithinHorizontalParent(map,
						(DefaultGraphCell) changed, coordChildren, coordParent,
						fieldname);
			}
			// if that did not work, the cell moves right
			if (overlap(nbound, (DefaultGraphCell) changed)) {
				repeatedbound = null;
				coordChildren = initloc;
				while (overlap(nbound, (DefaultGraphCell) changed)
						&& (coordChildren.getX() + coordChildren.getWidth() < coordParent
								.getX() + coordParent.getWidth())) {
					repeatedbound = nbound;
					coordChildren = new Rectangle(
							(int) coordChildren.getX() + 10,
							(int) coordChildren.getY(),
							(int) coordChildren.getWidth(),
							(int) coordChildren.getHeight());
					nbound = moveChildWithinHorizontalParent(map,
							(DefaultGraphCell) changed, coordChildren,
							coordParent, fieldname);
				}
			}
			if (overlap(nbound, (DefaultGraphCell) changed)) {
				nbound = moveChildWithinHorizontalParent(map,
						(DefaultGraphCell) changed, initloc, coordParent,
						fieldname);
			}
			occupiedpositions.put((DefaultGraphCell) changed, nbound);
			return nbound;
		}

		return coordChildren;
	}


	private void applyChanges(final Hashtable<DefaultGraphCell, Map> map) {
		final Set<DefaultGraphCell> allchildren = parentship.keySet();

		if (!map.isEmpty()) {

			glc.edit(map, null, null, null);

			for (Object key : map.keySet()) {
				if (GraphConstants.getBounds(map.get(key)) != null) {
					occupiedpositions.put((DefaultGraphCell) key,
							GraphConstants.getBounds(map.get(key)).getBounds());
				}
			}




		}

	}

	public void hideOrShowChildrenAndDrawExtraEdges() {
		Vector<Runnable> graphicmodificationactions = new Vector<Runnable>();
		for (Object root : DefaultGraphModel.getRoots(dgm)) {
			Object nedge;
			final DefaultGraphCell vertex = (DefaultGraphCell) root;
			if (parentHasVisibleContainers(vertex).isEmpty()) {
				// hide children
				hideAllChildren(graphicmodificationactions, vertex);
			} else {
				showAllChildren(vertex);
			}
		}
		for (Runnable run : graphicmodificationactions) {
			run.run(); // insert pending edges, if there is any
		}
	}

	public void setToFrontVisibleChildren() {
		Vector<Object> toFront = new Vector<Object>();
		for (Object root : DefaultGraphModel.getRootsAsCollection(dgm)) {
			final DefaultGraphCell vertex = (DefaultGraphCell) root;
			//if (!parentship.values().contains(vertex)) { // bring all
			// children to front
			toFront.add(vertex);
			//	}
		}
		setToFront(toFront.toArray(), parentship);
	}

	private void showAllChildren(final DefaultGraphCell vertex) {
		Vector<Object> toShow = new Vector<Object>();
		for (DefaultGraphCell o : getChildren(vertex)) {
			glc.setVisible(o, true);
			// make incoming and outgoing relationships visible as
			// well
			toShow.addAll(EventRedirectorForGraphCopy
					.getRelationshipsAndEdgesExcludingOtherExtremes(
							new Object[] { o }, jg));
			Vector<Key> processed = new Vector<Key>();
			for (Key k : substitutedNode) {
				if (k.getA().equals(o)) {
					DefaultEdge de = (DefaultEdge) k.getB();
					Object otherextreme = ((DefaultPort) de.getTarget())
							.getParent();
					if (otherextreme.equals(vertex))
						otherextreme = ((DefaultPort) de.getSource())
						.getParent();

					/*	substituteEdge.remove(new Key(vertex, otherextreme));

					if (parentship.get(otherextreme)!=null){
						Key altKey=new Key(vertex,parentship.get(otherextreme));
						if (substitutedNode.contains(altKey)){
							substitutedNode.remove(altKey);
							substituteEdge.remove(new Key(vertex, parentship.get(otherextreme)));
						}


						glc.remove(new Object[] { k.getB() });
					}*/
					substituteEdge.remove(new Key(vertex, otherextreme));
					glc.remove(new Object[] { k.getB() });
					processed.add(k);
				}
			}
			substitutedNode.removeAll(processed);
		}

		for (Object o : toShow) {
			if ((o instanceof DefaultEdge)||(o instanceof NAryEdge) ){
				Vector<DefaultGraphCell> extremes=null;
				if (o instanceof DefaultEdge) {
					extremes = getExtremes((DefaultEdge) o);
				}			
				if (o instanceof NAryEdge) {
					extremes = getExtremes((NAryEdge) o);
				}
				if (extremes!=null){
					boolean allVisible=true;
					for (DefaultGraphCell cell:extremes){
						allVisible=allVisible&& glc.isVisible(cell);
					}
					if (allVisible)
						glc.setVisible(o, true);
				} 
			} else
				if (o instanceof DefaultGraphCell)
					glc.setVisible(o, true);



		}
	}

	private void hideAllChildren(Vector<Runnable> graphicmodificationactions,
			final DefaultGraphCell vertex) {
		Vector<Object> toHide = new Vector<Object>();
		Hashtable<Object, Vector<Object>> connectedelements = new Hashtable<Object, Vector<Object>>();
		for (DefaultGraphCell o : getChildren(vertex)) {
			glc.setVisible(o, false);
			// make incoming and outgoing relationships invisible as well
			Vector<Object> nelementstohide = EventRedirectorForGraphCopy
					.getRelationshipsAndEdgesExcludingOtherExtremes(
							new Object[] { o }, jg);
			connectedelements.put(o, nelementstohide);
		}

		for (Object invisibleextreme : connectedelements.keySet()) {
			Vector<Object> nelementstohide = connectedelements
					.get(invisibleextreme);
			for (Object nelement : nelementstohide) {
				if (nelement instanceof NAryEdge) {

					// it is a relationships and a dashed line has
					// to be built
					Vector<DefaultGraphCell> extremes = getExtreme(
							(NAryEdge) nelement,
							(DefaultGraphCell) invisibleextreme);
					glc.setVisible(nelement, false);
					for (DefaultGraphCell otherextreme1 : extremes) {
						if (glc.isVisible(otherextreme1) || 
								(parentship.get(otherextreme1)!=null && 
								glc.isVisible(parentship.get(otherextreme1)) &&
								parentship.get(otherextreme1).equals(parentship.get(vertex)))) {


							if (!glc.isVisible(otherextreme1))
								otherextreme1=parentship.get(otherextreme1);

							final DefaultGraphCell otherextreme=otherextreme1;

							if (!substituteEdge.containsKey(new Key(vertex,
									otherextreme))) { 
								// to prevent creating more than one default
								// edge if several children with relationships to
								// external visible elements exist
								final DefaultEdge newEdge = new DefaultEdge();//vertex.toString()+"."+otherextreme.toString());
								Runnable run = new Runnable() {
									public void run() {
										ConnectionSet cs = new ConnectionSet();
										cs.connect(newEdge,
												otherextreme.getFirstChild(),
												vertex.getFirstChild());
										Hashtable edgeprops = new Hashtable();
										Hashtable elements = new Hashtable();
										GraphConstants.setDashPattern(edgeprops,
												new float[] { 3, 3 });
										elements.put(newEdge, edgeprops);
										glc.insert(new Object[] { newEdge },
												elements, cs, null); 
										// to conenct port with port

									}
								};
								graphicmodificationactions.add(run); 								
								// new edges have to be inserted when
								// the iteration concludes
								substituteEdge.put(
										new Key(vertex, otherextreme), newEdge);

								substitutedNode.add(new Key(invisibleextreme,
										newEdge)); 
								// to remember the external node
							}
						}
					}
				}
			}

		}
	}

	private Vector<DefaultGraphCell> getExtreme(NAryEdge o,
			DefaultGraphCell otherextreme) {
		Iterator edges = ingenias.editor.Model.getEdges(jg.getModel(),
				new Object[] { o }).iterator();
		Vector<DefaultGraphCell> dgcs = new Vector<DefaultGraphCell>();
		while (edges.hasNext()) {

			Object edge = edges.next();
			DefaultGraphCell extreme = CenterRelationships.getCellDefaultGraphCellFromDefaultEdge((DefaultEdge) edge,
					(Model) jg.getModel());
			if (!extreme.equals(otherextreme))
				dgcs.add(extreme);
		}
		return dgcs;
	}
	private Vector<DefaultGraphCell> getExtremes(NAryEdge o) {
		Iterator edges = ingenias.editor.Model.getEdges(jg.getModel(),
				new Object[] { o }).iterator();
		Vector<DefaultGraphCell> dgcs = new Vector<DefaultGraphCell>();
		while (edges.hasNext()) {

			Object edge = edges.next();
			DefaultGraphCell extreme = CenterRelationships.getCellDefaultGraphCellFromDefaultEdge((DefaultEdge) edge,
					(Model) jg.getModel());			
			dgcs.add(extreme);
		}
		return dgcs;
	}

	private Vector<DefaultGraphCell> getExtremes(DefaultEdge o) {
		NAryEdge nary=CenterRelationships.getCellNAryEdgeFromDefaultEdge(o,
				(Model) jg.getModel());		
		return getExtremes(nary);
	}


	private void setToFront(Object[] toFront,
			Hashtable<DefaultGraphCell, DefaultGraphCell> parentship) {

		glc.toBack(toFront);
		for (Object node : toFront) {
			if (parentship.containsKey(node))
				setToFront(new Object[] { parentship.get(node) }, parentship);
		}
	}

	private void initializeOccupiedPositions() {
		occupiedpositions.clear();
		for (Object obj : dgm.getRoots()) {
			if (obj instanceof DefaultGraphCell) {
				// if (getChildren(obj).isEmpty()){
				if (dgm.getAttributes(obj) != null
						&& GraphConstants.getBounds(dgm.getAttributes(obj)) != null)
					occupiedpositions.put((DefaultGraphCell) obj,
							GraphConstants.getBounds(dgm.getAttributes(obj)));
				// }
			}
		}

	}

	private Rectangle2D processVerticalPanel(
			final Hashtable<DefaultGraphCell, Map> map, Object changed,
			Rectangle2D coordChildren, Rectangle2D coordParent, String fieldname) {

		if (!isVertical(coordChildren, coordParent)
				|| overlap(coordChildren.getBounds(),
						(DefaultGraphCell) changed)) {

			Rectangle nbound = moveChildWithinVerticalParent(map,
					(DefaultGraphCell) changed, coordChildren, coordParent,
					fieldname);
			Rectangle lastbound = nbound; // to remember the optimal location
			Rectangle repeatedbound = null; // to remember last location
			Rectangle2D initloc = coordChildren;
			// first we move down the location of the cell until a suitable
			// place is found
			while (overlap(nbound, (DefaultGraphCell) changed)
					&& coordChildren.getY() + coordChildren.getHeight() <= coordParent
					.getY() + coordParent.getHeight()) {
				repeatedbound = nbound;
				coordChildren = new Rectangle((int) coordChildren.getX(),
						(int) coordChildren.getY() + 10,
						(int) coordChildren.getWidth(),
						(int) coordChildren.getHeight());
				nbound = moveChildWithinVerticalParent(map,
						(DefaultGraphCell) changed, coordChildren, coordParent,
						fieldname);				
			}
			// if that did not work, the cell moves up
			if (overlap(nbound, (DefaultGraphCell) changed)) {
				repeatedbound = null;
				coordChildren = initloc;
				while (overlap(nbound, (DefaultGraphCell) changed)
						&& (coordChildren.getY() >= coordParent.getY())) {
					repeatedbound = nbound;
					coordChildren = new Rectangle((int) coordChildren.getX(),
							(int) coordChildren.getY() - 10,
							(int) coordChildren.getWidth(),
							(int) coordChildren.getHeight());
					nbound = moveChildWithinVerticalParent(map,
							(DefaultGraphCell) changed, coordChildren,
							coordParent, fieldname);

				}
			}
			if (overlap(nbound, (DefaultGraphCell) changed)) {

				nbound = moveChildWithinVerticalParent(map,
						(DefaultGraphCell) changed, initloc, coordParent,
						fieldname);

			}
			occupiedpositions.put((DefaultGraphCell) changed, nbound);
			return nbound;
		}
		return coordChildren;
	}

	private boolean overlap(Rectangle nbound, DefaultGraphCell candidate) {
		Iterator<DefaultGraphCell> positions = occupiedpositions.keySet()
				.iterator();
		boolean found = false;
		while (positions.hasNext() && !found) {
			DefaultGraphCell cbound = positions.next();
			if (!cbound.equals(candidate)
					&& cbound.getClass().equals(candidate.getClass())) {
				// System.err.println("overlap between "+cbound+"("+occupiedpositions.get(cbound)+") and "+candidate+"("+nbound+")");
				found = occupiedpositions.get(cbound).intersects(nbound);
			}
		}
		return found;
	}

	private boolean isVertical(Rectangle2D coordChildren,
			Rectangle2D coordParent) {
		int xpar = (int) coordParent.getX();
		int ypar = (int) coordParent.getY();
		int widthpar = (int) coordParent.getWidth();
		int heightpar = (int) coordParent.getHeight();

		int xchild = (int) coordChildren.getX();
		int ychild = (int) coordChildren.getY();
		int widthchild = (int) coordChildren.getWidth();
		int heightchild = (int) coordChildren.getHeight();
		return xchild == xpar + widthpar / 2 - widthchild / 2 && ychild >= ypar
				&& ychild + heightchild <= ypar + heightpar;
	}

	private boolean isHorizontal(Rectangle2D coordChildren,
			Rectangle2D coordParent) {
		int xpar = (int) coordParent.getX();
		int ypar = (int) coordParent.getY();
		int widthpar = (int) coordParent.getWidth();
		int heightpar = (int) coordParent.getHeight();

		int xchild = (int) coordChildren.getX();
		int ychild = (int) coordChildren.getY();
		int widthchild = (int) coordChildren.getWidth();
		int heightchild = (int) coordChildren.getHeight();

		return ychild == ypar + heightpar / 2 - heightchild / 2
				&& xchild >= xpar && xchild + widthchild <= xpar + widthpar;
	}

	private Enumeration obtainEnumerationOfCollectionField(Object changed,
			String fieldname) {
		DefaultGraphCell parentCell = parentship.get(changed);
		ingenias.editor.entities.Entity parentEntity = (ingenias.editor.entities.Entity) parentCell
				.getUserObject();
		Enumeration containedelementsInContainer = null;
		try {
			Method enumerationObtention = parentEntity.getClass().getMethod(
					"get" + fieldname + "Elements");
			containedelementsInContainer = (Enumeration) enumerationObtention
					.invoke(parentEntity, new Object[] {});
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
		return containedelementsInContainer;
	}

	public static Hashtable<String, CollectionPanel> evaluate(
			CompositeRenderer jc, ingenias.editor.entities.Entity ent, Map cellAttributes) {

		Field[] fields = ent.getClass().getFields();

		Hashtable<String, CollectionPanel> containersFields = new Hashtable<String, CollectionPanel>();
		Method m;
		try {
			m = jc.getClass().getMethod("setEntity",
					new Class[] { ent.getClass(), Map.class });
			m.invoke(jc, ent,cellAttributes); // to associate the entity to the panel
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

		for (Field field : fields) {
			if (jc.getConcreteSubComponent(field.getName(), ent, cellAttributes) instanceof ContainerPanel) {
				containersFields.put(
						field.getName(),
						(CollectionPanel) jc.getConcreteSubComponent(
								field.getName(), ent,cellAttributes));
			}
			;
		}
		return containersFields;
	}
	/**
	 * This method invokes computations within the event dispatch thread. So be careful if you call
	 * it while processing some event
	 * @param object
	 * @param jg
	 * @return
	 */
	public static boolean isContainer(final DefaultGraphCell object, final JGraph jg){
		boolean found=false;		
		final Hashtable<String, CollectionPanel> vc = new Hashtable<String, CollectionPanel>();


		// this verification needs to be done within the event dispatch thread.
		// otherwise, the cell update and refresh will not work properly. Sometimes, it may
		// give false positives.When doing this within  the event dispatch thread, it should 
		// be fine
		CellView[] views = jg.getGraphLayoutCache().getAllViews();
		for (final CellView cv : views) {
			if (cv.getCell() == object) {
				if (cv instanceof VertexView) {
					if (((VertexView) cv).getRenderer() instanceof CompositeRenderer) {

						cv.refresh(jg.getGraphLayoutCache(), new GraphContext(jg,
								new Object[] { ((VertexView) cv).getCell() }),
								false);	
					}


					Component element = ((VertexView) cv)
							.getRendererComponent(jg, false, false, false); // to
							// set
					// the
					// entity

					CompositeRenderer jc = (CompositeRenderer) ((VertexView) cv)
							.getRenderer();

					vc.putAll(evaluate(jc,
							(ingenias.editor.entities.Entity) object
							.getUserObject(),jg.getModel().getAttributes(object)));

				}
			}
		}


		return vc.size()>0;
	}
	public boolean isContainer(
			DefaultGraphCell object) {
		return isContainer(object,jg);
	}

	public Hashtable<String, CollectionPanel> parentHasVisibleContainers(
			DefaultGraphCell object) {
		CellView[] views = glc.getAllViews();
		Hashtable<String, CollectionPanel> vc = new Hashtable<String, CollectionPanel>();

		for (CellView cv : views) {
			if (cv.getCell() == object) {
				if (cv instanceof VertexView) {
					if (((VertexView) cv).getRenderer() instanceof CompositeRenderer) {
						cv.refresh(glc, new GraphContext(jg,
								new Object[] { ((VertexView) cv).getCell() }),
								false);
						Component element = ((VertexView) cv)
								.getRendererComponent(jg, false, false, false); // to
						// set
						// the
						// entity

						CompositeRenderer jc = (CompositeRenderer) ((VertexView) cv)
								.getRenderer();

						vc = evaluate(jc,
								(ingenias.editor.entities.Entity) object
								.getUserObject(),jg.getModel().getAttributes(object));

					}
				}
			}
		}
		return vc;
	}

	private void moveChildrenToParent(Hashtable map,
			Vector<DefaultGraphCell> children, Object changed, Map attsparent,
			Map newatts) {
		Rectangle2D coordParent = GraphConstants.getBounds(attsparent);
		Rectangle2D newCoordParent = GraphConstants.getBounds(newatts);
		Rectangle2D coordAtts = GraphConstants.getBounds(newatts);

		for (Object child : children) {
			Map attsChildren = dgm.getAttributes(child);
			// System.err.println("moving "+child);
			Rectangle2D coordChildren = GraphConstants.getBounds(attsChildren);
			int xchild = (int) coordChildren.getX();
			int ychild = (int) coordChildren.getY();
			int widthchild = (int) coordChildren.getWidth();
			int heightchild = (int) coordChildren.getHeight();
			xchild = (xchild + ((int) coordParent.getX() - (int) newCoordParent
					.getX()));
			ychild = ychild
					+ ((int) coordParent.getY() - (int) newCoordParent.getY());
			Hashtable newMap = new Hashtable();
			GraphConstants.setBounds(newMap, new Rectangle(xchild, ychild,
					widthchild, heightchild));
			map.put(child, newMap);
		}
	}

	public static Vector<DefaultGraphCell> getChildren(Object changed, Hashtable<DefaultGraphCell, DefaultGraphCell> parentship) {
		Vector<DefaultGraphCell> children = new Vector<DefaultGraphCell>();
		for (DefaultGraphCell possibleChild : parentship.keySet()) {
			if (parentship.get(possibleChild) == changed) {
				children.add(possibleChild);
				children.addAll(getChildren(possibleChild,parentship));// to obtain
				// recursively all
				// children
			}
		}
		return children;
	}


	public Vector<DefaultGraphCell> getChildren(Object changed) {
		return getChildren(changed,parentship);
	}

	private Rectangle moveChildWithinParent(final Hashtable map,
			DefaultGraphCell changed, Rectangle2D coordChildren,
			Rectangle2D coordParent, String fieldname) {
		DefaultGraphCell parentCell = parentship.get(changed);
		ingenias.editor.entities.Entity parentEntity = (ingenias.editor.entities.Entity) parentCell
				.getUserObject();
		double xpar = coordParent.getX();
		double ypar = coordParent.getY();
		double widthpar = coordParent.getWidth();
		double heightpar = coordParent.getHeight();

		double xchild = coordChildren.getX();
		double ychild = coordChildren.getY();
		double widthchild = coordChildren.getWidth();
		double heightchild = coordChildren.getHeight();

		coordChildren = new Rectangle(0, 0, 0, 0);
		if (xpar > xchild) {
			xchild = xpar + 10;
		}
		if (widthpar < widthchild) {
			widthchild = widthpar;
		}

		if (xpar + widthpar <= xchild + widthchild) {
			xchild = xpar + widthpar - widthchild;
		}

		if (ypar > ychild) {
			ychild = ypar + 10;
		}
		if (heightpar < heightchild) {
			heightchild = heightpar;
		}

		if (ypar + heightpar <= ychild + heightchild) {
			ychild = ypar + heightpar - heightchild;
		}

		Hashtable mapnest1 = new Hashtable();
		map.put(changed, mapnest1);
		Rectangle newBound = new Rectangle((int) xchild, (int) ychild,
				(int) widthchild, (int) heightchild);
		GraphConstants.setBounds(mapnest1, newBound);
		return newBound;
	}

	private boolean isNotExternal(Rectangle2D coordChildren,
			Rectangle2D coordParent) {
		int xpar = (int) coordParent.getX();
		int ypar = (int) coordParent.getY();
		int widthpar = (int) coordParent.getWidth();
		int heightpar = (int) coordParent.getHeight();
		int xchild = (int) coordChildren.getX();
		int ychild = (int) coordChildren.getY();
		int widthchild = (int) coordChildren.getWidth();
		int heightchild = (int) coordChildren.getHeight();

		boolean alongXaxis = ((xchild + widthchild / 2) == xpar || (xchild + widthchild / 2) == xpar
				+ widthpar)
				&& (ychild + heightchild / 2 >= ypar && ychild + heightchild
				/ 2 <= ypar + heightpar);
		boolean alongYAxis = ((ychild + heightchild / 2) == ypar || (ychild + heightchild / 2) == ypar
				+ heightpar)
				&& (xchild + widthchild / 2 >= xpar && xchild + widthchild / 2 <= xpar
				+ widthpar);

		return !alongXaxis && !alongYAxis;
	}

	private void moveChildExternalParent(final Hashtable map,
			DefaultGraphCell changed, Rectangle2D coordChildren,
			Rectangle2D coordParent, String fieldname) {
		DefaultGraphCell parentCell = parentship.get(changed);
		ingenias.editor.entities.Entity parentEntity = (ingenias.editor.entities.Entity) parentCell
				.getUserObject();
		int xpar = (int) coordParent.getX();
		int ypar = (int) coordParent.getY();
		int widthpar = (int) coordParent.getWidth();
		int heightpar = (int) coordParent.getHeight();

		int xchild = (int) coordChildren.getX();
		int ychild = (int) coordChildren.getY();
		int widthchild = (int) coordChildren.getWidth();
		int heightchild = (int) coordChildren.getHeight();

		if (widthchild >= widthpar / 2)
			widthchild = widthpar / 2 - 1;
		if (heightchild >= heightpar / 2)
			heightchild = heightpar / 2 - 1;

		Line2D diag1 = new Line2D.Double(xpar, ypar, xpar + widthpar, ypar
				+ heightpar);
		Line2D diag2 = new Line2D.Double(xpar + widthpar, ypar, xpar, ypar
				+ heightpar);

		switch (diag1.relativeCCW(xchild + widthchild / 2, ychild + heightchild
				/ 2)) {
				case -1:
					if (diag2.relativeCCW(xchild + widthchild / 2, ychild + heightchild
							/ 2) == -1) {
						xchild = xpar - widthchild / 2;
						if (ychild + heightchild / 2 >= ypar + heightpar)
							ychild = ypar + heightpar - heightchild / 2;
						else if (ychild + heightchild / 2 <= ypar)
							ychild = ypar - heightchild / 2;
					} else {
						ychild = ypar + heightpar - heightchild / 2;
						if (xchild + widthchild / 2 >= xpar + widthpar) {
							xchild = xpar + widthpar - widthchild / 2;
						} else if (xchild + widthchild / 2 <= xpar) {
							xchild = xpar - widthchild / 2;
						}
					}
					break;
				case 0:
					xchild = xpar - widthchild / 2;
					ychild = ypar - heightchild / 2;
					break;
				case 1:
					if (diag2.relativeCCW(xchild + widthchild / 2, ychild + heightchild
							/ 2) == 1) {
						xchild = xpar + widthpar - widthchild / 2;
						if (ychild + heightchild / 2 >= ypar + heightpar)
							ychild = ypar + heightpar - heightchild / 2;
						else if (ychild + heightchild / 2 <= ypar)
							ychild = ypar - heightchild / 2;
					} else {
						ychild = ypar - heightchild / 2;
						if (xchild + widthchild / 2 >= xpar + widthpar) {
							xchild = xpar + widthpar - widthchild / 2;
						} else if (xchild + widthchild / 2 < xpar) {
							xchild = xpar - widthchild / 2;
						}
					}
					break;

		}

		coordChildren = new Rectangle((int) xchild, (int) ychild,
				(int) widthchild, (int) heightchild);

		if (isNotExternal(coordChildren, coordParent)){
			new Exception("External layout failed").printStackTrace();
		}else {

			Hashtable mapnest1 = new Hashtable();
			map.put(changed, mapnest1);
			GraphConstants.setBounds(mapnest1, coordChildren);
		}

	}

	private Rectangle moveChildWithinHorizontalParent(final Hashtable map,
			DefaultGraphCell changed, Rectangle2D coordChildren,
			Rectangle2D coordParent, String fieldname) {
		DefaultGraphCell parentCell = parentship.get(changed);
		ingenias.editor.entities.Entity parentEntity = (ingenias.editor.entities.Entity) parentCell
				.getUserObject();
		int xpar = (int) coordParent.getX();
		int ypar = (int) coordParent.getY();
		int widthpar = (int) coordParent.getWidth();
		int heightpar = (int) coordParent.getHeight();

		int xchild = (int) coordChildren.getX();
		int ychild = (int) coordChildren.getY();
		int widthchild = (int) coordChildren.getWidth();
		int heightchild = (int) coordChildren.getHeight();

		coordChildren = new Rectangle(0, 0, 0, 0);

		if (xpar + widthpar <= xchild + widthchild) {
			xchild = xpar + widthpar - widthchild;
		}

		if (xpar >= xchild) {
			xchild = xpar;
		}

		ychild = ypar + heightpar / 2 - heightchild / 2;

		Rectangle newBound = new Rectangle((int) xchild, (int) ychild,
				(int) widthchild, (int) heightchild);
		if (!isHorizontal(newBound, coordParent))
			new Exception("Horizontal layout failed").printStackTrace();
		else {

			Hashtable mapnest1 = new Hashtable();
			map.put(changed, mapnest1);

			GraphConstants.setBounds(mapnest1, newBound);
		}
		return newBound;
	}

	private Rectangle moveChildWithinVerticalParent(final Hashtable map,
			DefaultGraphCell changed, Rectangle2D coordChildren,
			Rectangle2D coordParent, String fieldname) {
		DefaultGraphCell parentCell = parentship.get(changed);
		ingenias.editor.entities.Entity parentEntity = (ingenias.editor.entities.Entity) parentCell
				.getUserObject();
		int xpar = (int) coordParent.getX();
		int ypar = (int) coordParent.getY();
		int widthpar = (int) coordParent.getWidth();
		int heightpar = (int) coordParent.getHeight();

		int xchild = (int) coordChildren.getX();
		int ychild = (int) coordChildren.getY();
		int widthchild = (int) coordChildren.getWidth();
		int heightchild = (int) coordChildren.getHeight();

		coordChildren = new Rectangle(0, 0, 0, 0);

		xchild = xpar + widthpar / 2 - widthchild / 2;

		if (ypar + heightpar <= ychild + heightchild) {
			ychild = ypar + heightpar - heightchild;
		}
		if (ypar > ychild) {
			ychild = ypar;
		}

		Rectangle newBound = new Rectangle((int) xchild, (int) ychild,
				(int) widthchild, (int) heightchild);
		if (!isVertical(newBound, coordParent))
			new Exception("Vertical layout failed").printStackTrace();
		else {
			Hashtable mapnest1 = new Hashtable();
			map.put(changed, mapnest1);
			GraphConstants.setBounds(mapnest1, newBound);
		}
		return newBound;
	}

	public void setParent(DefaultGraphCell principal, DefaultGraphCell nested1) throws WrongParent {
		if (nested1!=null && isContainer(nested1))
			parentship.put(principal, nested1);
		else 
			throw new WrongParent("Tried to use as a container entity the cell "+
					nested1+" when it is not a container. "+
					"Error triggered while "+principal+" was set as the child of "+nested1);
		// principal.setParent(nested1); // The JGRaph parentship relationship
		// cannot be used. Only the root in a parental relationship is visible
		// everytime. Children are not.

	}

	public String parentshipToXML(Hashtable<DefaultGraphCell, Integer> nodeids) {
		String result = "";
		for (DefaultGraphCell child : parentship.keySet()) {
			if (nodeids.get(child) == null
					|| nodeids.get(parentship.get(child)) == null)
				new Exception().printStackTrace();
			result = result + "<child id=\"node" + nodeids.get(child)
					+ "\" parent=\"node" + nodeids.get(parentship.get(child))
					+ "\"/>\n";
		}
		return result;
	}

	public String constraintsToXML(Hashtable<DefaultGraphCell, Integer> nodeids) {
		String result = "";
		for (DefaultGraphCell cell:nodeids.keySet()){
			Entity ent=(Entity) cell.getUserObject();
			for (String key:entityConstraints.keySet()){
				String code=this.hashCode()+":"+cell.hashCode() + ":";
				if (key.startsWith(code)){					
					Hashtable<DefaultGraphCell, Dimension> currentEntityConstraints = entityConstraints.get(key);
					String view=key.substring(code.length(),key.length());
					if (currentEntityConstraints!=null){
						result=result+"<cellconstraint id=\"node"+nodeids.get(cell)+":" + view+"\">\n";
						for (DefaultGraphCell currentCell:currentEntityConstraints.keySet()){
							result=result+"<currentcell id=\"node"+nodeids.get(currentCell)+"\" height=\""+
									currentEntityConstraints.get(currentCell).height+"\" width=\""+currentEntityConstraints.get(currentCell).width+"\"/>\n";
						}
						result=result+"</cellconstraint>\n";
					}
				}
			}
		}
		return result;
	}

	public void constraintsFromXML(Hashtable<String, DefaultGraphCell> nodeids,
			Node constraintsnode) {
		NodeList children = constraintsnode.getChildNodes();
		for (int k = 0; k < children.getLength(); k++) {
			String childtype = (String) children.item(k).getNodeName();
			if (childtype.equals("cellconstraint")) {
				String cellindexid = children.item(k).getAttributes()
						.getNamedItem("id").getNodeValue();
				String defaultView=cellindexid.substring(cellindexid.indexOf(":")+1,cellindexid.length());
				String defaultCellID=cellindexid.substring(0,cellindexid.indexOf(":"));
				DefaultGraphCell cell=nodeids.get(defaultCellID);
				NodeList cellcontrstaintschildren = children.item(k).getChildNodes();
				if (cellcontrstaintschildren!=null){
					String entityconstraintskey=this.hashCode()+":"+cell.hashCode() + ":" + defaultView;
					Hashtable<DefaultGraphCell, Dimension> cellDimension = entityConstraints
							.get(entityconstraintskey);
					if (cellDimension==null){
						cellDimension= new Hashtable<DefaultGraphCell, Dimension>();
						entityConstraints.put(entityconstraintskey, cellDimension);					 
					}
					for (int j=0;j<cellcontrstaintschildren.getLength();j++){
						if (cellcontrstaintschildren.item(j).getNodeName()!=null && 
								cellcontrstaintschildren.item(j).getNodeName().equalsIgnoreCase("currentcell")){
							String currentcellid = cellcontrstaintschildren.item(j).getAttributes()
									.getNamedItem("id").getNodeValue();
							DefaultGraphCell currentcell=nodeids.get(currentcellid);

							String height = cellcontrstaintschildren.item(j).getAttributes()
									.getNamedItem("height").getNodeValue();
							String width = cellcontrstaintschildren.item(j).getAttributes()
									.getNamedItem("width").getNodeValue();

							Entity ent=(Entity) ((DefaultGraphCell)nodeids.get(currentcellid)).getUserObject();										
							cellDimension.put(currentcell, new Dimension(Integer.parseInt(width),Integer.parseInt(height)));
						}

					}
				}

			}
		}
	}

	public void fromXML(final Hashtable<String, DefaultGraphCell> nodeids,
			Node parentshipnode) {
		NodeList children = parentshipnode.getChildNodes();
		for (int k = 0; k < children.getLength(); k++) {
			String childtype = (String) children.item(k).getNodeName();
			if (childtype.equals("child")) {
				final String childid = children.item(k).getAttributes()
						.getNamedItem("id").getNodeValue();
				final String parentid = children.item(k).getAttributes()
						.getNamedItem("parent").getNodeValue();
				try {
					SwingUtilities.invokeAndWait(new Runnable(){
						public void run (){
							try {
								// setparent implies certain checks that affect the 
								// event dispatch thread. It works better when this method
								// is called within the EDT.
								setParent(nodeids.get(childid), nodeids.get(parentid));
							} catch (WrongParent e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}		
						}
					});
				} catch (InvocationTargetException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Hashtable<DefaultGraphCell, DefaultGraphCell> getParentRelationships() {
		// TODO Auto-generated method stub
		return new Hashtable<DefaultGraphCell, DefaultGraphCell>(parentship);
	}

	public void storeContraints(DefaultGraphCell cell) {
		Hashtable<DefaultGraphCell, Dimension> cellDimension = new Hashtable<DefaultGraphCell, Dimension>();
		Entity ent=(Entity)cell.getUserObject();
		String constraintKey = this.hashCode()+":"+cell.hashCode() + ":" + ent.getPrefs(jg.getModel().getAttributes(cell)).getView();
		entityConstraints.put(
				constraintKey,
				cellDimension);
		for (int k = 0; k < jg.getModel().getRootCount(); k++) {
			if (jg.getModel().getRootAt(k) instanceof DefaultGraphCell
					&& !(jg.getModel().getRootAt(k) instanceof DefaultEdge)) {
				DefaultGraphCell currentDGCRoot = (DefaultGraphCell) jg
						.getModel().getRootAt(k);
				//if (currentDGCRoot.getUserObject().equals(ent)
				if (currentDGCRoot.equals(cell)
						&& currentDGCRoot.getAttributes() != null
						&& GraphConstants.getBounds(currentDGCRoot
								.getAttributes()) != null) {

					cellDimension.put(
							currentDGCRoot,
							new Dimension((int) GraphConstants.getBounds(
									currentDGCRoot.getAttributes()).getWidth(),
									(int) GraphConstants.getBounds(
											currentDGCRoot.getAttributes())
											.getHeight()));
				}
			}
		}
	}

	public void restoreContraints(DefaultGraphCell cell) {
		Entity ent=(Entity)cell.getUserObject();
		Hashtable<DefaultGraphCell, Dimension> cellDimension = entityConstraints
				.get(this.hashCode()+":"+cell.hashCode() + ":" + ent.getPrefs(jg.getModel().getAttributes(cell)).getView());

		Hashtable<Object, Map> changes = new Hashtable<Object, Map>();
		if (cellDimension != null) {
			for (DefaultGraphCell dgc : cellDimension.keySet()) {
				if (dgc.getAttributes() != null) { // still exists					
					Map oldmap = dgc.getAttributes();
					Rectangle oldbounds = GraphConstants.getBounds(oldmap)
							.getBounds();
					oldbounds.setSize(cellDimension.get(dgc));
					GraphConstants.setBounds(oldmap, oldbounds);
					changes.put(dgc, oldmap);
				}
			}

			jg.getModel().edit(changes, null, null, null);
			jg.invalidate();
			jg.repaint();
		}
	}




	public void copyConstraints(DefaultGraphCell copied, DefaultGraphCell original,
			Vector<DefaultGraphCell> childrenOriginal, Map clonesOfOriginal, Hashtable<String, 
			Hashtable<DefaultGraphCell, Dimension>> externalConstraints) {
		if (original.getUserObject() instanceof Entity){
			Entity ent=(Entity)original.getUserObject();

			Vector<String> involvedKeys=new Vector<String>();
			for (String key:externalConstraints.keySet()){
				String originalConstraintKey = 
						":"+original.hashCode() + ":" ;
				if (key.indexOf(originalConstraintKey)>=0)
					involvedKeys.add(key);
			}
			for (String key:involvedKeys){
				Hashtable<DefaultGraphCell, Dimension> cellDimension = externalConstraints
						.get(key);
				if (cellDimension!=null){
					Hashtable<DefaultGraphCell, Dimension> newcellDimension=new Hashtable<DefaultGraphCell, Dimension>();
					for (DefaultGraphCell originalCell:cellDimension.keySet()){
						newcellDimension.put((DefaultGraphCell) clonesOfOriginal.get(originalCell),cellDimension.get(originalCell));
					}
					String implicitView=key.substring(key.lastIndexOf(":")+1,key.length());
					String copiedConstraintKey = 
							this.hashCode()+":"+copied.hashCode() + ":" + implicitView;
					entityConstraints.put(copiedConstraintKey,newcellDimension);
				}
			}
		}

	}


	public void removeCellFromParentShip(DefaultGraphCell dgc) {
		if (parentship.containsKey(dgc))
			parentship.remove(dgc);
		Vector<DefaultGraphCell> toRemove=new Vector<DefaultGraphCell>();

		for (DefaultGraphCell key:parentship.keySet()){
			if (parentship.get(key)==dgc)
				toRemove.add(key);
		}
		for (DefaultGraphCell key:toRemove){
			parentship.remove(key);
		}
	}

	public static HashSet<DefaultGraphCell> getRecursivelyChildren(DefaultGraphCell ccell, Hashtable<DefaultGraphCell, DefaultGraphCell> parentship) {
		HashSet<DefaultGraphCell> result=new HashSet<DefaultGraphCell> ();		

		result.add(ccell);
		for (DefaultGraphCell current:getChildren(ccell,parentship)){
			result.addAll(getRecursivelyChildren(current,parentship));
		}

		return result;
	}


	public HashSet<DefaultGraphCell> getRecursivelyChildren(DefaultGraphCell ccell) {
		return getRecursivelyChildren(ccell,parentship);
	}


	public  Hashtable<String, Hashtable<DefaultGraphCell, Dimension>> getEntityConstraints() {

		return entityConstraints;
	}






}
