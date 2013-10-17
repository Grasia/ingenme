
/** 
 * Copyright (C) 2010  Jorge J. Gomez-Sanz, ruben Fuentes
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


package ingenias.editor;

import ingenias.editor.cell.NAryEdge;
import ingenias.editor.entities.NAryEdgeEntity;
import ingenias.editor.events.AnyChangeListener;
import ingenias.editor.events.EventRedirector;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.Edge;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.Port;

//
// Custom Model
//

// A Custom Model that does not allow Self-References
public class Model
extends DefaultGraphModel
implements java.io.Serializable {

	// It is used to revet disconnections.
	private Port previousPort = null;
	private IDEState ids=null;
	private boolean messagesOn=true;
	private boolean hardChange;
	private int idCounter;

	public Model(IDEState ids) {
		this.ids=ids;

	}



	// Override Superclass Method implements java.io.Serializable
	public boolean acceptsSource(Object edge, Object port) {
		//System.out.println("acceptsSource");//////////
		// Source only Valid if not Equal Target
		// return (((Edge) edge).getTarget() != port);
		if ( ( (Edge) edge).getTarget() == port) {
			return false;
		}
		else {
			return true;
		}

	}

	public String getNewId() {
		idCounter=0;

		Vector<NAryEdgeEntity> rels;
		rels = RelationshipManager.getRelationshipsVector(ids.gm);
		HashSet<String> trels=new HashSet<String> ();
		for (NAryEdgeEntity nedge:rels){
			trels.add(nedge.getId());						
		}


		while (trels.contains(""+idCounter) || 
				ids.om.findUserObject(""+idCounter).size()>0 ||
				ids.gm.getModel(""+idCounter)!=null){
			idCounter++;
		}

		return ""+idCounter;
	}

	public String getNewId(String fromID) {
		idCounter=0;

		Vector<NAryEdgeEntity> rels = RelationshipManager.getRelationshipsVector(ids.gm);
		Hashtable<String,NAryEdgeEntity> trels=new Hashtable<String,NAryEdgeEntity>();
		for (NAryEdgeEntity nedge:rels){
			if (nedge.getId().equals(fromID+idCounter))
				idCounter++;
		}

		while (ids.om.findUserObject(fromID+idCounter).size()>0){
			idCounter++;
		}

		while (ids.gm.getModel(fromID+idCounter)!=null){
			idCounter++;
		}

		return fromID+idCounter;
	}


	// Override Superclass Method implements java.io.Serializable
	public boolean acceptsTarget(Object edge, Object port) {
		//System.out.println("acceptsTarget");//////////
		// Target only Valid if not Equal Source
		//return (((Edge) edge).getSource() != port);
		if ( ( (Edge) edge).getSource() == port) {
			return false;
		}
		else {
			return true;
		}

	}

	protected void connect(Object edge, Object port, boolean isSource,
			boolean insert) {

		// connect is called 4 times when connecting an edge: 2 to disconnect the edge from
		// the previous source and target and 2 more to connect it to the current
		// source and target.
		// remove = disconnect.
		// port = Port connected or disconnected from the edge.
		if (!insert) {
			// If the action is a disconnection from a relationship previousPort
			// takes note of the original port.
			if (this.getParent( ( (Edge) edge).getSource())instanceof NAryEdge) {
				previousPort = (Port) ( (Edge) edge).getTarget();
			}
			super.connect(edge, port, isSource, insert);
		}
		else {
			// Check if an edge moving is correct according to destination.
			boolean applicable = true;
			/*if (this.getParent( ( (Edge) edge).getSource())instanceof NAryEdge) {
        NAryEdge nEdge = (NAryEdge)this.getParent( ( (Edge) edge).getSource());
        GraphCell[] selectedNodes = new GraphCell[] {
            (GraphCell)this.getParent(port)};
        //(GraphCell)((DefaultPort)port).getParent()};
        applicable = (nEdge.assignRoles(selectedNodes, false).size() > 0);
//////////  acceptConnection(GraphModel model, GraphCell[] selected) {
//////////undoManager.undo(graph.getGraphLayoutCache())
        // An edge has always a source and a target.
        if (!applicable)
        System.err.println("cannot find a proper role for "+edge+" "+port+ " "+selectedNodes.length);
      }
      else if (port == null) {
        applicable = false;
        System.err.println("port is null when connecting "+edge+" "+port);
      }*/



			// If the movement is correct connect.
			if (applicable) {
				if ( (isSource && this.acceptsSource(edge, port)) ||
						(!isSource && this.acceptsTarget(edge, port))) {
					super.connect(edge, port, isSource, insert);
				}
				// If the movement is incorrect and the previous port is known,
				// reconnect with that previous port.
			}
			else if (previousPort != null) {
				super.connect(edge, previousPort, false, false);
				previousPort = null;
			}

		}
	}

	public void defaultRemove(Object[] roots) {
		super.remove(roots);
	}

	private void removeIfObjectAppearsOnlyOnce(Object[] objectsToRemove) {
		Vector reviewed = new Vector();
		for (int i = 0; i < objectsToRemove.length; i++) {
			if (objectsToRemove[i] instanceof DefaultGraphCell &&
					( (DefaultGraphCell) objectsToRemove[i]).getUserObject()instanceof
					ingenias.editor.entities.Entity &&
					! ( ( (DefaultGraphCell) objectsToRemove[i]).getUserObject()
							instanceof ingenias.editor.entities.NAryEdgeEntity)) {
				ingenias.editor.entities.Entity ent =
					(ingenias.editor.entities.Entity) ( (DefaultGraphCell)
							objectsToRemove[i]).
							getUserObject();
				if (!reviewed.contains(ent)) {
					reviewed.add(ent);

					ids.om.removeEntity(ent);

				}
			}
		}
	}

	private Vector getElementsToRemove(Object[] roots) {
		// Prepare elements to be removed.
		// Expand NAryEdges with related binary edges.
		Vector elementsToRemove = new Vector();
		for (int i = 0; i < roots.length; i++) {
			// If a n-edge is selected, related binary relationships has to be removed too.
			if (roots[i] instanceof NAryEdge) {
				NAryEdge nEdge = (NAryEdge) roots[i];

				//          this.rm.removeRelationship( (ingenias.editor.entities.Entity) nEdge.getUserObject() );
				Object[] objects = nEdge.getRepresentation();
				for (int j = 0; j < objects.length; j++) {
					elementsToRemove.add(objects[j]);
					// If it is a Vertex, binary edges connected with it has to be deleted.
				}
			}
			else if ( (roots[i] instanceof DefaultGraphCell) &&
					! (roots[i] instanceof DefaultEdge) &&
					! (roots[i] instanceof DefaultPort)) {
				Iterator itEdges = DefaultGraphModel.getEdges(this,
						new Object[] {roots[i]}).iterator();
				while (itEdges.hasNext()) {
					DefaultEdge de = (DefaultEdge) itEdges.next();
					elementsToRemove.add(de);
				}
				//ObjectManager.getInstance().removeEntity((Entity)((DefaultGraphCell)roots[i]).getUserObject());
			}
			else if (roots[i] instanceof DefaultEdge) {
				DefaultEdge de = (DefaultEdge) roots[i];
				NAryEdgeEntity naryedge = null;
				DefaultGraphCell naryedgetarget = null;
				DefaultGraphCell source = (DefaultGraphCell) ( (DefaultPort) de.
						getSource()).getParent();
				DefaultGraphCell target = (DefaultGraphCell) ( (DefaultPort) de.
						getTarget()).getParent();
				if (source.getUserObject()instanceof ingenias.editor.entities.
						NAryEdgeEntity) {
					naryedge = (ingenias.editor.entities.NAryEdgeEntity) source.
					getUserObject();
					naryedgetarget = target;
				}
				else 
					if (target.getUserObject()instanceof ingenias.editor.entities.
							NAryEdgeEntity){
						naryedge = (NAryEdgeEntity) target.getUserObject();
						naryedgetarget = source;
					} 
				if (naryedge!=null){
					naryedge.removeObject(""+naryedgetarget.hashCode());
					String[] strs = naryedge.getIds();
					String ids = "";
					for (int k = 0; k < strs.length; k++) {
						ids = ids + strs[k];
					}
					Log.getInstance().logSYS(ids);
				}
			}
		}
		return elementsToRemove;

	}

	public void remove(Object[] roots) {
		// All operations are done with GraphCell[].



		GraphCell[] objectsToRemove = new GraphCell[roots.length];
		for (int i = 0; i < roots.length; i++) {
			objectsToRemove[i] = (GraphCell) roots[i];
		} 

		if ( !this.checkRelatedNAryEdges(objectsToRemove)) {

			// remove all associated relationships first
			// if an edge or a nary entity was selected, this will complete the selection
			EventRedirector.expandSelectionToRelationshipsAndEdgesExcludingOtherExtremes(roots, ids.editor.getGraph());


			Object[] complete=ids.editor.getGraph().getSelectionCells();
			Vector<GraphCell> onlyrelationships=new Vector<GraphCell>();
			Vector<GraphCell> others=new Vector<GraphCell>();
			for (int k=0;k<complete.length;k++){
				if (complete[k] instanceof NAryEdge)
					onlyrelationships.add((GraphCell)complete[k]);
				else 
					if (!(complete[k] instanceof DefaultEdge))
						others.add((GraphCell)complete[k]);
			}

			removeSelectedRootsWithoutQuestions(onlyrelationships.toArray(new GraphCell[onlyrelationships.size()]));
			removeSelectedRootsWithoutQuestions(others.toArray(new GraphCell[others.size()]));

		} else
		{

			// Update internal representation
			removeSelectedRootsWithoutQuestions(objectsToRemove);
		}

	}



	private void removeSelectedRootsWithoutQuestions(GraphCell[] objectsToRemove) {
		//this.removeIfObjectAppearsOnlyOnce(objectsToRemove);

		Vector elementsToRemove = this.getElementsToRemove(objectsToRemove);

		// Convert elementsToRemove vector in an Object[].
		Object[] expandedRoots = new Object[objectsToRemove.length + elementsToRemove.size()];
		int i = 0;
		int j = 0;
		while (i < elementsToRemove.size()) {
			if (! (elementsToRemove.get(i)instanceof DefaultPort)) {
				expandedRoots[j] = elementsToRemove.get(i);
				j++;
			}
			i++;
		}

		i = 0;

		while (i < objectsToRemove.length) {
			if (! (objectsToRemove[i] instanceof DefaultPort)) {
				expandedRoots[j] = objectsToRemove[i];
				j++;
			}
			i++;
		}

		Object objs[] = new Object[j];
		for (i = 0; i < j; i++) {
			objs[i] = expandedRoots[i];
		}

		super.remove(objs);
		super.remove(objs); // To avoid a jgraph bug

		Vector isolatedPorts = new Vector();
		for (i = 0; i < this.getRootCount(); i++) {
			if (this.getRootAt(i)instanceof DefaultPort) {
				isolatedPorts.add(this.getRootAt(i));
			}

		}
		super.remove(isolatedPorts.toArray());
		super.remove(isolatedPorts.toArray()); // To avoid a jgraph bug

		for (i = 0; i < this.getRootCount(); i++) {
			if (this.getRootAt(i)instanceof DefaultPort) {
				isolatedPorts.add(this.getRootAt(i));
			}

		}
	}

	// Get NAryEdges connected with this object.
	public  NAryEdge[] getConnectedNAryEdges(GraphCell object) {
		Vector nEdges = new Vector();
		// If they are Vertex, they have children, that is, ports.
		// Iterate over all nodeObject children looking for ports.
		for (int i = 0; i < getChildCount(object); i++) {
			// Fetch the Child of Vertex at Index i
			Object child = this.getChild(object, i);
			// Check if Child is a Port
			if (child instanceof DefaultPort) {
				Iterator itEdges = this.edges(child);
				while (itEdges.hasNext()) {
					Edge edge = (Edge) itEdges.next();
					Port sourcePort = (Port) edge.getSource();
					Port targetPort = (Port) edge.getTarget();
					Object otherSide = null;
					if (sourcePort.equals(child)) {
						otherSide = this.getParent(targetPort);
					}
					else {
						otherSide = this.getParent(sourcePort);
					}
					if (otherSide instanceof NAryEdge) {
						nEdges.add( (NAryEdge) otherSide);
					}
				}
			}
		}

		// If they are DefaultEdge could be connected with NAryEdge through its ports.
		if (object instanceof DefaultEdge) {
			Port sourcePort = (Port) ( (Edge) object).getSource();
			Object source = this.getParent(sourcePort);
			Port targetPort = (Port) ( (Edge) object).getTarget();
			Object target = this.getParent(targetPort);

			if (source instanceof NAryEdge) {
				nEdges.add( (NAryEdge) source);
			}
			if (target instanceof NAryEdge) {
				nEdges.add( (NAryEdge) target);
			}
		}

		// Put results in an array.
		NAryEdge[] result = new NAryEdge[nEdges.size()];
		for (int i = 0; i < nEdges.size(); i++) {
			result[i] = (NAryEdge) nEdges.get(i);
		}
		return result;
	}

	// Check if the deletion of objectsToRemove lets related NAryEdges ok.
	// GraphCell can be Vertex (DefaultGraphCel), BinaryEdges (DefaultEdge) or
	// Ports (DefaultPort).
	private boolean checkRelatedNAryEdges(GraphCell[] objectsToRemove) {
		boolean nEdgesOK = true;
		for (int i = 0; i < objectsToRemove.length; i++) {
			NAryEdge[] connectedNEdges = this.getConnectedNAryEdges( (GraphCell)
					objectsToRemove[i]);
			for (int j = 0; j < connectedNEdges.length; j++) {
				nEdgesOK = nEdgesOK && connectedNEdges[j].acceptRemove(objectsToRemove);
			}
		}

		return nEdgesOK;
	}

	public void fireChange() {
		super.edit(new Hashtable(),null,null, null);
		/*    super.fireGraphChanged(this,new DefaultGraphModel.GraphModelEdit(new Object[]{},
        new Object[]{},new Hashtable(),new ConnectionSet(),null));*/
	}

	public void fireChange1(Object source) {
		//    System.err.println("disparando cambio");
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		//System.err.println("listener "+listeners.length);
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = 0; i < listeners.length; i = i + 1) {
			//System.err.println(listeners[i].getClass().getName());
			if (AnyChangeListener.class.isAssignableFrom(listeners[i].getClass())) {
				// Lazily create the event:
				//        ( (AnyChangeListener) listeners[i]).processChange(source);

			}

		}

	}

	/**
	 * Create a GraphCell[] with objects in Object[].
	 * @param objects
	 * @return
	 */
	private GraphCell[] toGraphCellArray(Object[] objects) {
		GraphCell[] graphCellArray = new GraphCell[objects.length];
		for (int i = 0; i < objects.length; i++) {
			graphCellArray[i] = (GraphCell) objects[i];

		}
		return graphCellArray;
	}

	public void setAskMessages(boolean messagesOn){
		this.messagesOn=messagesOn;
	}

	public boolean getAskMessages(){
		return this.messagesOn;
	}

} // End of Editor.MyMode
