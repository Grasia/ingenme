package ingenias.generator.browser;
/*
 Copyright (C) 2005 Jorge Gomez Sanz

 This file is part of INGENIAS Development Kit (IDK), a support tool for the INGENIAS
 methodology, availabe at http://grasia.fdi.ucm.es/ingenias or
 http://ingenias.sourceforge.net

 INGENIAS IDE is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 INGENIAS IDE is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with IDK; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 */
import ingenias.editor.Editor;
import ingenias.editor.IDE;
import ingenias.editor.IDEState;
import ingenias.editor.Log;
import ingenias.editor.Model;
import ingenias.editor.ModelJGraph;
import ingenias.editor.ObjectManager;
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
import ingenias.exception.NotFound;
import ingenias.exception.NotInitialised;
import ingenias.exception.NullEntity;
import ingenias.exception.WrongParameters;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.jgraph.JGraph;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.Port;

public class GraphRelationshipFactory {
	private IDEState ids;
	private Browser browser;

	public static GraphRelationshipFactory createDefaultEmptyGraphFactory(){
		return new GraphRelationshipFactory(IDEState.emptyIDEState());
	}

	public static GraphRelationshipFactory createDefaultGraphFactory(Browser browser) throws NotInitialised{
		return new GraphRelationshipFactory(browser.getState());
	}



	public GraphRelationshipFactory(IDEState ids){
		this.ids=ids;
		browser=new BrowserImp(ids);
	}

	/**
	 * The method converts assignations to an array of String.
	 * assignations is a Vector of Vectors of Strings where Strings represents roles.
	 * In a solution assignations.get(j) the role assigned to selected[i] is
	 *((Vector) assignations.get(j)).get(i).
	 * @param selected
	 * @param assignations
	 * @return
	 */
	private String[] assignationsToStringArray(GraphCell[] selected,
			Vector assignations) {
		// The user selects one possible assignation.
		String[] displayedAssignations = new String[assignations.size()];
		for (int i = 0; i < assignations.size(); i++) {
			displayedAssignations[i] = "";
			for (int j = 0; j < selected.length; j++) {
				if (selected[j] instanceof DefaultGraphCell) {
					displayedAssignations[i] +=
						( (String) ( (Vector) assignations.get(i)).get(j)) +
						" = " +
						( (DefaultGraphCell) selected[j]).getUserObject().toString();
				}
				else {
					displayedAssignations[i] +=
						( (String) ( (Vector) assignations.get(i)).get(j)) +
						" = " + selected[j].getClass().getName();
				}
				if (j < (selected.length - 1)) {
					displayedAssignations[i] += ", ";
				}
			}
		}
		return displayedAssignations;
	}

	private static DefaultGraphCell findEntity(String id, Graph graph){
		DefaultGraphCell result=null;		
		for (int k=0;k<graph.getGraph().getModel().getRootCount() && result==null;k++){
			if (graph.getGraph().getModel().getRootAt(k) instanceof DefaultGraphCell){
				DefaultGraphCell root=(DefaultGraphCell)graph.getGraph().getModel().getRootAt(k);
				if (root.getUserObject()!=null && ((Entity)root.getUserObject()).getId().equals(id)){
					result=root;
				}
			}
		}
		return result;
	}

	private DefaultGraphCell findEntity(String id){
		Vector<ModelJGraph> graphs = this.ids.gm.getUOModels();
		int k=0;
		boolean found=false;
		DefaultGraphCell result=null;
		while (result==null && k<graphs.size()){
			result=findEntity(id,new GraphImp(graphs.elementAt(k), ids));
			k++;
		}


		return result;
	}

	public Vector<Hashtable<String,String>> getPossibleRoleAssignment(String relationshipType, String[] connectedEntities) throws NotFound{
		Vector<GraphCell> entities=new Vector<GraphCell>();

		for (String entityID:connectedEntities) {
			DefaultGraphCell entity = findEntity(entityID);
			if (entity==null){
				// the entity may have not been created yet as graphical object
				// The object manager may know about it.
				if (this.ids.om.findUserObject(entityID).size()>0)
					entity=new DefaultGraphCell(this.ids.om.findUserObject(entityID).firstElement());
				else
					throw new ingenias.exception.NotFound("Entity "+entityID+" that you tried to connect did not exist in any graph ");
			}
			//throw new ingenias.exception.NotFound("Entity "+entityID+" that you tried to connect did not exist in any graph ");
			entities.add(entity);	
		}

		GraphCell[] selected=new GraphCell[entities.size()];
		selected=entities.toArray(selected);
		NAryEdge nEdge=(NAryEdge) RelationshipFactory.getNRelationshipInstance(relationshipType, selected,browser);
		Vector<Hashtable<String, String>> result = convertToVectorHashtable(connectedEntities, selected, nEdge);
		return result;
	}


	public static Vector<Hashtable<String,String>> getPossibleRoleAssignment(String relationshipType, String[] connectedEntities, Graph graph, Browser browser) throws NotFound{
		Vector<GraphCell> entities=new Vector<GraphCell>();
		for (String entityID:connectedEntities) {
			DefaultGraphCell entity = findEntity(entityID,graph);
			if (entity==null)
				throw new ingenias.exception.NotFound("Entity "+entityID+" that you tried to connect did not exist in the graph "+graph.getName());
			entities.add(entity);	
		}

		GraphCell[] selected=new GraphCell[entities.size()];
		selected=entities.toArray(selected);
		NAryEdge nEdge=(NAryEdge) RelationshipFactory.getNRelationshipInstance(relationshipType, selected,browser);
		Vector<Hashtable<String, String>> result = convertToVectorHashtable(connectedEntities, selected, nEdge);
		return result;
	}

	private static Vector<Hashtable<String, String>> convertToVectorHashtable(String[] connectedEntities, GraphCell[] selected, NAryEdge nEdge) {
		Vector<List<String>> assignationsList = new Vector<List<String>>(nEdge.assignRoles(selected, true));
		Vector<Hashtable<String,String>> result=new Vector<Hashtable<String,String>>();
		for (List<String> assignation:assignationsList){
			Hashtable<String,String> assignmentTable=new Hashtable<String,String>();
			for (int k=0;k<connectedEntities.length;k++){
				assignmentTable.put(assignation.get(k), connectedEntities[k]);
			}
			result.add(assignmentTable);
		}
		return result;
	}

	public static  Vector< String> getPossibleRelationships( List<String> connectedEntities, Graph graph) throws NotFound{

		return getPossibleRelationships(connectedEntities.toArray(new String[connectedEntities.size()]),graph);

	}

	public static  Vector< String> getPossibleRelationships( String[] connectedEntities, Graph graph) throws NotFound{
		Vector<GraphCell> entities=new Vector<GraphCell>();
		for (String entityID:connectedEntities) {
			DefaultGraphCell entity = findEntity(entityID,graph);
			if (entity==null)
				throw new ingenias.exception.NotFound("Entity "+entityID+" that you tried to connect did not exist in the graph "+graph.getName());
			entities.add(findEntity(entityID,graph));	
		}

		GraphCell[] selected=new GraphCell[entities.size()];
		selected=entities.toArray(selected);
		Object[] relObjArray = graph.getGraph().getPossibleRelationships(selected);
		Vector<String> result = new Vector<String>();
		for (Object rel:relObjArray){
			result.add(rel.toString());
		}		
		return result;

	}


	public static  Vector<Hashtable<String, String>> getPossibleRoleAssignment(String relationshipType, List<String> connectedEntities, Graph graph, Browser browser) throws NotFound{

		return getPossibleRoleAssignment(relationshipType,connectedEntities.toArray(new String[connectedEntities.size()]),graph,browser);
	}


	private void insertRelationshipInManager(NAryEdge nEdge, DefaultEdge[] edges,
			GraphCell[] selected,
			java.util.List currentAssignation) {
		// The NAryEdgeEntity of the relationship is built.
		NAryEdgeEntity nae = (NAryEdgeEntity) nEdge.getUserObject();
		for (int i = 0; i < currentAssignation.size(); i++) {
			if (! ( ( (DefaultGraphCell) selected[i]).getUserObject()instanceof
					NAryEdgeEntity)) {
				nae.addObject(selected[i].hashCode() + "",
						( (Entity) ( (DefaultGraphCell) selected[i]).
								getUserObject()),
								( (RoleEntity) edges[i].getUserObject()),
								(String) currentAssignation.get(i),
								( ( (DefaultGraphCell) selected[i]).getUserObject().
										getClass().getName()));
			}
		}
		// Insert the Edge in the relationship manager.
		// this.rm.addRelationship((Entity) nEdge.getUserObject());
	}

	private Port[] getPorts(Object[] vertexList, ModelJGraph graph) {

		// Ports of argument vertexs.
		Port[] ports = new Port[vertexList.length];
		// Obtain the model.
		GraphModel model = graph.getModel();

		// Iterate over all Objects.
		for (int i = 0; i < vertexList.length; i++) {
			Port objectPort = null;
			// Iterate over all Children
			for (int j = 0; j < model.getChildCount(vertexList[i]); j++) {
				// Fetch the Child of Vertex at Index i
				Object child = model.getChild(vertexList[i], j);
				// Check if Child is a Port
				if (child instanceof Port) {

					// Return the Child as a Port
					objectPort = (Port) child;
				}
			}
			ports[i] = objectPort;
		}

		return ports;
	}

	private Point calculateCenter(GraphCell[] selected){
		int x=0;
		int y=0;
		for (int k=0;k<selected.length;k++){
			GraphCell current=selected[k];
			Rectangle rect=GraphConstants.getBounds(current.getAttributes()).getBounds();
			x=x+rect.x+rect.width/2;
			y=y+rect.
			y+rect.height/2;
		}
		return new Point((int)(x/selected.length),(int)(y/selected.length));
	}

//	Atributes for the binary edges of this NAryEdge according to targets and sources.
	private Hashtable edgesAttributes(DefaultEdge[] edges,
			String[] roleAssignation) {
		Hashtable edgesAttributes = new Hashtable();
		for (int i = 0; i < roleAssignation.length; i++) {
			// Create a Map that holds the attributes for the edge

			RoleEntity re = (RoleEntity) edges[i].getUserObject();
			Map attr = re.getAttributes();

			// Source
			/*if (selectedAssignation[i].indexOf("source") >= 0)
			 // Add a Line Begin Attribute
			  GraphConstants.setLineBegin(attr, GraphConstants.SIMPLE);*/
			// Target
			if (roleAssignation[i].toUpperCase().indexOf("TARGET") >= 0 ||
					roleAssignation[i].endsWith("T")) {

				// Add a Line End Attribute
				GraphConstants.setLineEnd(attr, GraphConstants.ARROW_SIMPLE);

				// Associate the Edge with its Attributes
			}
			GraphConstants.setDisconnectable(attr,false);
                        GraphConstants.setLineWidth(attr, 1);
                        GraphConstants.setEndSize(attr, 7);
			GraphConstants.setBendable(attr,false);
			edgesAttributes.put(edges[i], attr);
		}
		return edgesAttributes;
	}

	public  GraphRelationship createRelationship(String relType, 
			Graph diagram,
			Hashtable<String,String> assignment) throws InvalidEntity{

		ModelJGraph graph = diagram.getGraph();
		Collection<String> cells = assignment.values();
		Vector<DefaultGraphCell> selectedV=new Vector<DefaultGraphCell>();
		for (String cellid:cells){
			selectedV.add(this.findEntity(cellid, diagram));
		}
		DefaultGraphCell[] selected=selectedV.toArray(new DefaultGraphCell[selectedV.size()]);

		NAryEdge nEdge=(NAryEdge) graph.getInstanciaNRelacion(relType, selected);

		if (nEdge != null) {// make sure the relationship type is correct
			// All role assignations to classes are obtained.
			GraphCell[] newSelected = nEdge.prepareSelected(selected);
			// The user selects a role assignation (List of Strings).
			java.util.Vector<String> currentAssignation = new Vector<String>(assignment.keySet());

			// Connections that will be inserted into the Model.
			String[] selectedAssignation = new String[currentAssignation.size()];
			for (int i = 0; i < currentAssignation.size(); i++) {
				selectedAssignation[i] = (String) currentAssignation.get(i);
			}
			try {
				// Auxiliary edges that will be inserted in the Model.
				DefaultEdge[] auxiliaryEdges = nEdge.connectionsEdges(newSelected,
						selectedAssignation);
				ConnectionSet cs = nEdge.connections(selectedAssignation,
						auxiliaryEdges,
						getPorts(newSelected, diagram.getGraph()));
				// Create a Map that holds the attributes for the NAryEdge Vertex.
				// Associate the NAryEdge Vertex with its Attributes.
				//             Hashtable attributes = nEdgeAttributes(nEdge, pt);
				// Atributes for the binary edges of this NAryEdge according to
				// targets and sources.
				Hashtable attributes = new Hashtable();
				Hashtable edgesAttributes = this.edgesAttributes(auxiliaryEdges,
						selectedAssignation);

//				if (existAlreadyRelationship==null){
				// A new relationship. Otherwise, the relationship did exist before
				Map m=new Hashtable();
				Point centerP=calculateCenter(selected);
				Rectangle2D edgeb=GraphConstants.getBounds(nEdge.getAttributes());
				GraphConstants.setBounds(m,new Rectangle(centerP,new Dimension(0,0)));
				// Associate the Vertex with its Attributes
				attributes.put(nEdge, m);
				// Insert the Edge and its Attributes. The order matters.
				graph.getModel().insert(new Object[] {nEdge},attributes
						, null, null, null);
				Class.forName("ingenias.editor.cell."+relType+"Renderer"); // to force the static initialization
				GraphConstants.setBounds(m,new Rectangle(centerP,
						RenderComponentManager.getSize(
								((Entity)nEdge.getUserObject()).getType(),
								((Entity)nEdge.getUserObject()).getPrefs(graph.getModel().getAttributes(nEdge)).getView())));
				graph.getModel().edit(attributes,null,null,null);

				// New relationship is inserted also in the relationship manager.
				this.insertRelationshipInManager(nEdge, auxiliaryEdges,
						newSelected,
						currentAssignation);



				graph.getModel().insert( (Object[]) auxiliaryEdges,edgesAttributes, cs, null
						,null);
				Hashtable changes=new Hashtable();
				ingenias.editor.events.LocationChange.centerNAryEdge(graph, 
						(Model) graph.getModel(), changes, nEdge);
				if (changes.size()>0)
					graph.getModel().edit(changes, null, null, null);


			}
			catch (WrongParameters wp) {
				Log.getInstance().logSYS(
						"WARNING: internal error on connecting elements. " +
				" Cannot produce edges for this connection");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Entity entNEdge=(Entity)nEdge.getUserObject();
			RenderComponentManager.setRelationshipView(entNEdge.getPrefs(graph.getModel().getAttributes(nEdge)).getView(),entNEdge,nEdge,graph);



		} else {
			throw new InvalidEntity("The relationship type "
					+relType+" does not exist");
		}

		return new GraphRelationshipImp((NAryEdgeEntity) nEdge.getUserObject(),graph,ids); 
	}




}
