/*
    Copyright (C) 2002 Jorge Gomez Sanz

    This file is part of INGENIAS IDE, a support tool for the INGENIAS
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
    along with INGENIAS IDE; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

*/

package ingenias.editor;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.*;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ingenias.editor.cell.NAryEdge;
import ingenias.editor.cell.RenderComponentManager;
import ingenias.editor.entities.Entity;
import ingenias.editor.entities.NAryEdgeEntity;
import ingenias.editor.entities.RoleEntity;
import ingenias.exception.InvalidEntity;
import ingenias.exception.WrongParameters;

import org.jgraph.graph.CellView;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.Port;

public class RelationshipManager implements java.io.Serializable {



  private static Vector loadedRelationships=new Vector();
//  private static Hashtable extremes=new Hashtable();

 public RelationshipManager(){
 }

/* public static RelationshipManager createIndependentCopy(){
  RelationshiObjectManager om=new ObjectManager(gm,root,arbolObjetos);
  return om;
 }

 public static void updateCopy(ObjectManager copyom){
  om=copyom;
 }*/


  public static void addRelationship(Entity nedge){
   if (!loadedRelationships.contains(nedge))
    loadedRelationships.add(nedge);
  }

/*  public static void putDGC(String dgcid, DefaultGraphCell dgc){
    extremes.put(dgcid,dgc);
  }

  public DefaultGraphCell getDGC(String dgcid){
    return (DefaultGraphCell)extremes.get(dgcid);
  }*/

  public static void removeRelationship(Entity nedge){
   loadedRelationships.remove(nedge);
  }

  public static Entity getRelationship(String nedge){
   Enumeration enumeration=loadedRelationships.elements();
    while (enumeration.hasMoreElements()){
      Entity ent=(Entity)enumeration.nextElement();
      if (ent.getId().equalsIgnoreCase(nedge))
       return ent;
    }
    return null;
  }

  

  public static Vector<NAryEdgeEntity> getRelationshipsVector(GraphManager gm){
 	    Enumeration graphs=gm.getUOModels().elements();
 	    Vector result=new Vector();
 	    while (graphs.hasMoreElements()){
 	      ModelJGraph jg=(ModelJGraph) graphs.nextElement();
 	      for (int k=0;k<jg.getModel().getRootCount();k++){
 	        Object cand=((DefaultGraphCell)jg.getModel().getRootAt(k)).getUserObject();
 	        if (cand!=null && ingenias.editor.entities.NAryEdgeEntity.class.isAssignableFrom(cand.getClass())){
 	          result.add(cand);
 	      }
 	      }

 	    }
 	    return result;
  }



  public static Enumeration getRelationships(GraphManager gm){
    Enumeration graphs=gm.getUOModels().elements();
    Vector result=new Vector();
    while (graphs.hasMoreElements()){
      ModelJGraph jg=(ModelJGraph) graphs.nextElement();
      for (int k=0;k<jg.getModel().getRootCount();k++){
        Object cand=((DefaultGraphCell)jg.getModel().getRootAt(k)).getUserObject();
        if (cand!=null && ingenias.editor.entities.NAryEdgeEntity.class.isAssignableFrom(cand.getClass())){
          result.add(cand);
      }
      }

    }
    return result.elements();
  }
  
  public static Enumeration getRelationshipsCells(GraphManager gm){
	    Enumeration graphs=gm.getUOModels().elements();
	    Vector result=new Vector();
	    while (graphs.hasMoreElements()){
	      ModelJGraph jg=(ModelJGraph) graphs.nextElement();
	      for (int k=0;k<jg.getModel().getRootCount();k++){
	        Object cand=((DefaultGraphCell)jg.getModel().getRootAt(k));
	        if (cand!=null && ingenias.editor.cell.NAryEdge.class.isAssignableFrom(cand.getClass())){
	          result.add(cand);
	      }
	      }

	    }
	    return result.elements();
	  }
  public static Enumeration getRelationshipsModels(GraphManager gm){
	    Enumeration graphs=gm.getUOModels().elements();
	    Vector result=new Vector();
	    while (graphs.hasMoreElements()){
	      ModelJGraph jg=(ModelJGraph) graphs.nextElement();
	      for (int k=0;k<jg.getModel().getRootCount();k++){
	        Object cand=((DefaultGraphCell)jg.getModel().getRootAt(k));
	        if (cand!=null && ingenias.editor.cell.NAryEdge.class.isAssignableFrom(cand.getClass())){
	          result.add(jg);
	      }
	      }

	    }
	    return result.elements();
	  }
  
  
	// This method is called through the pop-up menu.
	// The elements to connect have to be selected.
	/*public static void connect(Point pt, ModelJGraph graph) {
		// The general connect method only admits a GraphCell[] parameter.
		GraphCell[] selected = new GraphCell[graph.getSelectionCells().length];
		int x = 0;
		int y = 0;
		for (int i = 0; i < graph.getSelectionCells().length; i++) {
			selected[i] = (GraphCell)graph.getSelectionCells()[i];
			//x=x+
		}
		// General connect method.
		connect(pt, selected,graph);

	}*/
	

	private static NAryEdge findRelationshipInArray(Object[] ops) {
		// TODO Auto-generated method stub
		for (int k=0;k<ops.length;k++){
			if (ops[k] instanceof NAryEdge)
				return (NAryEdge)ops[k];		
		}
		return null;
	}

	// Returns the index of the relationship selected by the user between possible ones.
	// possible relationships contains is an array of String's with the name of relationships.
	private static Integer getSelectedRelationship(Object[] possibleRelationships, ModelJGraph graph) {
		int index = -1;
		if (possibleRelationships.length > 1) {

			JComboBox pops = new JComboBox(possibleRelationships);
			JPanel temp = new JPanel();
			temp.add(new JLabel("Select one of the following relationships"));
			temp.add(pops);
			int result = JOptionPane.showConfirmDialog(graph, temp,
					"Valid Relationships",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			int sel = pops.getSelectedIndex();
			if (sel >= 0 && result == JOptionPane.OK_OPTION) {
				index = sel;
			}
		}  else {
			index=0;
		}
		return index;
	}

	// Connect the selected GraphCells with a relationship.
	// A relationship can be binary (DefaultEdge) or n-ary (NAryEdge).
	// pt where user asks for connection and ports if user selected them.
	// The requested action is slightly different depending on selected items.
	// According to the number of Edges in selected, the action can be:
	// 0 => Propose a relationship between selected and connect them with
	//      a new relationship.
	// 1 and it is NAryEdge => Connect the remaining GraphCells with that NAryEdge.
	// other cases => Error unable to connect.

	/**
	 *  Gets the port attribute of the Editor object
	 *
	 *@param  vertexNode  Description of Parameter
	 *@return             The port value
	 */
	public static Port getPort(Object vertexNode, ModelJGraph graph) {
		GraphModel model = graph.getModel();

		// Iterate over all Children
		for (int i = 0; i < model.getChildCount(vertexNode); i++) {
			// Fetch the Child of Vertex at Index i
			Object child = model.getChild(vertexNode, i);
			// Check if Child is a Port
			if (child instanceof Port) {
				// Return the Child as a Port
				return (Port) child;
			}
		}

		// No Ports Found
		return null;
	}
	
	// Gives the ports in the model related with GraphCells in vertexList.
	public static Port[] getPorts(Object[] vertexList, ModelJGraph graph) {

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
	
	// Atributes for the binary edges of this NAryEdge according to targets and sources.
	private static Hashtable edgesAttributes(DefaultEdge[] edges,
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

	


	private static Point calculateCenter(GraphCell[] selected){
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
  
	// Gives the ports in the model related with GraphCells in vertexList.
	// If the GraphCell is in portsLists, that port has priority.

	// Connect the selected GraphCells with a relationship.
	// A relationship can be binary (DefaultEdge) or n-ary (NAryEdge).
	// pt where user asks for connection and ports if user selected them.
	// The requested action is slightly different depending on selected items.
	// According to the number of Edges in selected, the action can be:
	// 0 => Propose a relationship between selected and connect them with
	//      a new relationship.
	// 1 and it is NAryEdge => Connect the remaining GraphCells with that NAryEdge.
	// other cases => Error unable to connect.
	public static NAryEdge connect(Point pt, GraphCell[] selected, ModelJGraph graph, Vector<String> allowedRelationships) {
		// Possible edges.
		Object[] nops = graph.getPossibleRelationships(selected);
		Vector<Object> possibleRels=new Vector<Object>();
		for (Object obj:nops){
			if (allowedRelationships.contains(obj))
				possibleRels.add(obj);
		}
		Object[] ops=possibleRels.toArray();
		
		NAryEdge nEdge = null;

		if (ops.length > 0) {
			// sel can be:
			// >= 0   If the user selects a relationship and accepts it.
			// < 0    In other case.
			NAryEdge existAlreadyRelationship=findRelationshipInArray(selected);
			if (existAlreadyRelationship!=null){
				nEdge = existAlreadyRelationship;
				//System.err.println("Relacion previa");
			} else {
				int sel = getSelectedRelationship(ops, graph).intValue();
				if (sel >= 0) {
					// N-ary relationship.
					nEdge = (NAryEdge) graph.getInstanciaNRelacion(ops[sel].toString(), selected);
				}
			}

			if (nEdge != null) {
				// All role assignations to classes are obtained.
				GraphCell[] newSelected = nEdge.prepareSelected(selected);
				// The user selects a role assignation (List of Strings).
				java.util.List currentAssignation = selectAssignation(newSelected,
						nEdge,graph);
				if (currentAssignation != null) {
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
								getPorts(newSelected, graph));
						// Create a Map that holds the attributes for the NAryEdge Vertex.
						// Associate the NAryEdge Vertex with its Attributes.
						//             Hashtable attributes = nEdgeAttributes(nEdge, pt);
						// Atributes for the binary edges of this NAryEdge according to
						// targets and sources.
						Hashtable attributes = new Hashtable();
						Hashtable edgesAttributes = edgesAttributes(auxiliaryEdges,
								selectedAssignation);

						if (existAlreadyRelationship==null){
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
							graph.getGraphLayoutCache().setVisible(nEdge, true);
							try {
								GraphConstants.setBounds(m,new Rectangle(centerP,
										graph.getDefaultSize(
												((Entity)nEdge.getUserObject()))));
							} catch (InvalidEntity e) {
								e.printStackTrace();
							}
							/*System.err.println("nuevo tamaño:"+RenderComponentManager.getSize(
							 ((Entity)nEdge.getUserObject()).getType(),
							 ((Entity)nEdge.getUserObject()).getPrefs().getView()));*/
							graph.getModel().edit(attributes,null,null,null);

							// New relationship is inserted also in the relationship manager.
							insertRelationshipInManager(nEdge, auxiliaryEdges,
									newSelected,
									currentAssignation);
						} else {
							graph.getGraphLayoutCache().setVisible(nEdge, true);
							insertRelationshipInManager(nEdge, auxiliaryEdges,
									newSelected,
									currentAssignation);
						}


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
					}
					Entity entNEdge=(Entity)nEdge.getUserObject();
					
					RenderComponentManager.setRelationshipView(entNEdge.getPrefs(null).getView(),entNEdge,nEdge,graph);

				}
				else {
					JOptionPane.showMessageDialog(graph, "Assignation not allowed",
							"Warning",
							JOptionPane.WARNING_MESSAGE);
				}
			}


		}
		else 
		{
			JOptionPane.showMessageDialog(graph, "Relationship not allowed", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}
		return nEdge;
	}
	
	// Insert a relationship in the relationship manager.
	private static void insertRelationshipInManager(NAryEdge nEdge, DefaultEdge[] edges,
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
	

	// The method converts assignations to an array of String.
	// assignations is a Vector of Vectors of Strings where Strings represents roles.
	// In a solution assignations.get(j) the role assigned to selected[i] is
	// ((Vector) assignations.get(j)).get(i).
	private static String[] assignationsToStringArray(GraphCell[] selected,
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
	
	// Selects a roles assignation for the given NAryEdge and selected cells to connect.
	// The assignation is represented as a List of Strings.
	private static java.util.List selectAssignation(GraphCell[] selected, NAryEdge nEdge, ModelJGraph graph) {
		// assignations is a Vector of Vectors of Strings where Strings represents roles.
		Vector assignations = new Vector(nEdge.assignRoles(selected, true));
		// The user selects one possible assignation.
		String[] displayedAssignations = assignationsToStringArray(selected,
				assignations);
		if (displayedAssignations.length>1){
		// Ask the user to select a role assignation.
		String selectedOption = (String) JOptionPane.showInputDialog(graph,
				"Select one of the following assignations", "Valid Assignations",
				JOptionPane.OK_CANCEL_OPTION, null,
				displayedAssignations, displayedAssignations[0]);
		// Obtain the index of the selected option.
		int assignSelection = -1;
		for (int i = 0; i < displayedAssignations.length; i++) {
			if (displayedAssignations[i].equals(selectedOption)) {
				assignSelection = i;
				// Roles assignation.
			}
		}
		if (assignSelection >= 0) {
			return ( (java.util.List) assignations.get(assignSelection));
		}
		else {
			return null;
		}
		} else {
			return ( (java.util.List) assignations.get(0));
		}
	}
  
	// This method is called to connect two ports directly, that is,
	// not through the pop-up menu.
	// Both source and target are not null.
  public static NAryEdge connect(Port source, Port target, ModelJGraph graph, Vector<String> allowedRelationships) {
		;
		// The general connect method only admits a GraphCell[] parameter.
		GraphCell sourceGraphCell = (GraphCell)graph.getModel().getParent(
				source);
		GraphCell targetGraphCell = (GraphCell)graph.getModel().getParent(
				target);
		// Obtain the wiews to get the middle point.
		CellView sourceView = graph.getGraphLayoutCache().getMapping(sourceGraphCell, false);
		CellView targetGraphLayoutCache = graph.getGraphLayoutCache().getMapping(targetGraphCell, false);
		//System.err.println(sourceView);
		//System.err.println(targetGraphLayoutCache);
		// Middle point between source and target.
		int x = (new Double( (GraphConstants.getBounds(sourceView.getAllAttributes()
		).getX() +
		GraphConstants.getBounds(targetGraphLayoutCache.getAllAttributes()).
		getX()) /
		2)).intValue();
		int y = (new Double( (GraphConstants.getBounds(sourceView.getAllAttributes()
		).getY() +
		GraphConstants.getBounds(targetGraphLayoutCache.getAllAttributes()).
		getY()) /
		2)).intValue();
		// General connect method.

		Map selectedPorts = new Hashtable();
		selectedPorts.put(sourceGraphCell, (DefaultPort) source);
		selectedPorts.put(targetGraphCell, (DefaultPort) target);
		/*    if (this.graph instanceof SequenceModelModelJGraph) {
		 return connectSequence(new Point(x, y), new GraphCell[] {
		 sourceGraphCell, targetGraphCell
		 }   , selectedPorts);  }
		 else {*/
		return connect(new Point(x, y), new GraphCell[] {
			sourceGraphCell, targetGraphCell
		},graph, allowedRelationships);
//		}

	}

  public static Entity getLocalRelationships(String id,GraphManager gm){
  Entity result=null;
  Enumeration enumeration=getRelationships(gm);
  while (enumeration.hasMoreElements() && result==null){
    Entity current=(Entity)enumeration.nextElement();
    if (current.getId().equalsIgnoreCase(id))
       result=current;
  }
  return result;
  }

  public static void clearRelationships(){
   loadedRelationships.removeAllElements();
  }

}

