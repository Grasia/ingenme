
/** 
 * Copyright (C) 2010  Jorge J. Gomez-Sanz sobre código original de Rubén Fuentes
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


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.jgraph.graph.*;

import ingenias.editor.entities.*;



// N-ary relationship.

abstract public class NAryEdge extends DefaultGraphCell  implements java.io.Serializable {

  // Arities in relationships with another classes.
  // Key = role name, value = atrributes hashtable.
  // In attributes hashtable
  // Key = {min, max} (String), value = (Integer)
  // Key = classes (String), value = Set of class name  implements java.io.Serializable (String)
  private Hashtable roleData = new Hashtable();

  // Model in which exists this n-edge.
  //private transient GraphModel model;



  public NAryEdge(Entity userObject) {
    super(userObject);
    // Model in which exists this n-edge.
    //this.model = model; 
  }


  // It returns the Port in this n-edge corresponding to a role.
  public Port getPort(String roleName) {

    // Iterate over all NAryEdge Children.
    for (int i = 0; i < this.getChildCount(); i++) {
      // Fetch the Child of Vertex at Index i
      Object child = this.getChildAt(i);
      // Check if Child is a Port
      if (child instanceof DefaultPort &&
        ( (String) ( (DefaultPort) child ).getUserObject() ).equals(roleName) )
        // Return the Child as a Port
        return (Port) child;
    }
    // No Ports Found
    return null;
  }



  // It returns Object Ports corresponding to a role.
  public DefaultPort[] getPorts(String roleName) {

    ArrayList ports = new ArrayList();

    // Iterate over edges connected with this n-edge role port.
    Iterator it = this.getPort(roleName).edges();
    while (it.hasNext()) {
      org.jgraph.graph.Edge edge = (org.jgraph.graph.Edge) it.next();
      ports.add( edge.getTarget() );
    }

    DefaultPort[] result = new DefaultPort[ports.size()];
    for (int i = 0; i < ports.size(); i++)
      result[i] = (DefaultPort) ports.get(i);

    return result;
  }
	// It returns all the Objects connected with this relationship.
	public GraphCell[] getObjects() {
		// Binary edges in this relationship.
		Object[] edgesAsObjects = this.getRepresentation();
		// Obtain objects connected with the edges.
		GraphCell[] nAryEdgeObjects = new GraphCell[edgesAsObjects.length];
		// Edge source is always the NAryEdge and the target is an object.
		for (int i = 0; i < edgesAsObjects.length; i++) {
			nAryEdgeObjects[i] = (GraphCell)((DefaultPort) ((org.jgraph.graph.Edge) edgesAsObjects[i]).getTarget()).getParent();
		}

		return nAryEdgeObjects;
	}



  // It returns Objects playing roleName in the relationship.
  public GraphCell[] getObjects(String roleName) {
    // Ports related with this roleName.
    DefaultPort[] rolePorts = this.getPorts(roleName);
    // Container for GraphCells.
    GraphCell[] roleObjects = new GraphCell[rolePorts.length];
    // Obtain objects.
    for (int i = 0; i < rolePorts.length; i++)
      roleObjects[i] = (GraphCell) rolePorts[i].getParent();

    return roleObjects;
  }



  // It returns this NAryEdge with all objects that constitute its representation.
  // Returns binary edges related with this NAryEdge.
  public DefaultEdge[] getRepresentation() {
    Vector<DefaultEdge> elements = new Vector<DefaultEdge>();
    Iterator itRoles = ( (Set) this.getRoles() ).iterator();
    while (itRoles.hasNext()) {
      String roleName = (String) itRoles.next();
      Iterator itEdges = this.getPort(roleName).edges();
      while (itEdges.hasNext()) {
    	  DefaultEdge edge = (DefaultEdge) itEdges.next();
        elements.add(edge);
      }
    }

    
    // Convert elements to Object[]
    DefaultEdge[] result = new DefaultEdge[elements.size()];
    for (int i = 0; i < elements.size(); i++)
      result[i] = elements.get(i);
    return result;
  }



  // The connection is acceptable if there is a role assignation to selected classes
  // considering, if they exists, classes already included in the relationship.
  static  boolean acceptConnection(GraphModel model, GraphCell[] selected){return false;};


  // Connection logic.
  // The deletion is acceptable if the resulting relationship is valid.
  // selected are the nodes to removed.
  abstract public boolean acceptRemove(GraphCell[] selected) ;



  // Remove the edge itself from selected.
  public GraphCell[] prepareSelected(GraphCell[] selected) {
    boolean iAm = false;
    for (int i = 0; i < selected.length; i++)
       if (this.equals(selected[i]))
         iAm = true;

    if (iAm) {
      GraphCell[] newSelected = new GraphCell[selected.length - 1];
      int j = 0;
      for (int i = 0; i < selected.length; i++)
        if (!this.equals(selected[i])) {
          newSelected[j] = selected[i];
          j++;
        }
      return newSelected;
    } else
      return selected;

  }



  // The result is a List of List of String v where where v[i] is the role assigned to
  // selectedNodes[i].
  // allSolutions indicate if the methos has to find the first solution or all.
  abstract public List assignRoles(GraphCell[] selectedNodes, boolean allSolutions) ;


  // Returns a DefaultEdge[] related with this n-edge.
  // In selected there can be 0 or 1 n-edge. If there is one is the object itself.
  abstract public DefaultEdge[] connectionsEdges(GraphCell[] selected, String[] roles)throws ingenias.exception.WrongParameters ;


//ConnectionSet cs = nEdge.connections((java.util.List) assignations.get(0), auxiliaryEdges,
//                       getPorts( graph.getSelectionCells() ) );
  // Returns a ConnectionSet for the edge given source and target ports.
  // edges is generated with connectionsEdges(sources.length + target.length)
  // Always in edges, first sources and then targets.
  // roles has roles for objects.
  // It occurs that roles[i] is the role for the object with objects[i] port
  // and edges[i] is the binary edge which connects the n-edge with objects[i].
  public ConnectionSet connections(String[] roles, DefaultEdge[] edges, Port[] objects) {
    // Connections that will be inserted into the Model
    ConnectionSet cs = new ConnectionSet();
    // Create connections between objects and this n-edge.
    for (int i = 0; i < objects.length; i++) {
      cs.connect(edges[i], this.getPort(roles[i]), objects[i]);
    }

    return cs;
  }



  public String toString() {
    return this.getUserObject().toString();////////// No habr�a que hacer un (Entity) ???
  }



  // Add a new role allowed with this relationship.
  public void addRole(String roleName) {
    if (roleData.get(roleName) == null) {
      Hashtable properties = new Hashtable();
      properties.put("classes", (Set) new HashSet());
      roleData.put(roleName, properties);
    }
  }


  // Add a new class allowed in a role with this relationship. implements java.io.Serializable
  public void addClass(String roleName, String className) {
    Hashtable properties = (Hashtable) roleData.get(roleName);

    // If exists the role.
    if (properties != null) {
      Set classes = (Set) properties.get("classes");
      classes.add(className);
      properties.put("classes", classes);
      roleData.put(roleName, properties);
    }
  }



  // Set arity of this relationship with a role.
  public void setArity(String roleName, boolean minimun, int arity) {
    Hashtable properties = (Hashtable) roleData.get(roleName);

    // If exists the role.
    if (properties != null) {
      // Minimun arity is updated.
      if (minimun)
        properties.put("min", new Integer(arity));
      // Maximun arity is updated.
      else
        properties.put("max", new Integer(arity));
      roleData.put(roleName, properties);
    }
  }



  // Get roles related with this relationship.
  public Set<String> getRoles() {
    return roleData.keySet();
  }



  // Get roles related with this relationship.
  public List getOrderedRoles() {
    Vector roles = new Vector(this.getRoles().size());
    Iterator rolesIt = this.getRoles().iterator();
    for (int i = 0; i < this.getRoles().size(); i++)
      roles.add(i, (String) rolesIt.next());
    return ((List) roles);
  }



  // Get classes related with this relationship.
  public Set getClasses() {
    HashSet result = new HashSet();

    // Iterator over roles.
//////////    Iterator it = NAryEdge.getRoles().iterator();
    Iterator it = getRoles().iterator();
    while (it.hasNext()) {
//////////      Set classes = NAryEdge.getClasses( (String) it.next() );
      Set classes = getClasses( (String) it.next() );
      Iterator propertiesIt = classes.iterator();
      while (propertiesIt.hasNext())
        result.add( (String) propertiesIt.next() );
    }

    return ( (Set) result );
  }



  // Get classes (Set of Strings) related with this relationship and playing the role.
  public Set getClasses(String roleName) {
    Hashtable properties = (Hashtable) roleData.get(roleName);

    if (properties != null)
      return ( (Set) properties.get("classes") );
    else
      return null;
  }



  // Get arity of this relationship with a class.
  public Integer getArity(String roleName, boolean minimun) {
    Hashtable properties = (Hashtable) roleData.get(roleName);
    // Minimun arity is obtained.
    if (properties != null)
      if (minimun)
        return ( (Integer) properties.get("min") );
      else
        return ( (Integer) properties.get("max") );
    // Maximun arity is obtained.
    else
      return null;
  }



  // Get current arity of this relationship (an object) with a role.
  public Integer getCurrentArity(String roleName) {
    if (this.getPort(roleName) != null) {
      int num = 0;
      Iterator it = ( (Port) this.getPort(roleName) ).edges();
      while (it.hasNext()) {
        num++;
        it.next();
      }
      return new Integer(num);
    } else
      return null;
  }



  // Get current arities of this relationship for every role.
  // Pairs (role, current arity which is an Integer)
  public Map getCurrentArities() {
    Hashtable arities = new Hashtable();
    Iterator it = this.getRoles().iterator();

    while (it.hasNext()) {
      String role = (String) it.next();
      arities.put(role, this.getCurrentArity(role));
    }
    return ( (Map) arities );
  }


  /**
 * Get the relationships in which this cell participates.
 * @return
 */
public DefaultGraphCell[] getRelationships() {
  // Temporal container.
  Vector relationContainer = new Vector();
  // Search for relationships connected with cell ports.
  DefaultPort port = (DefaultPort)this.getChildAt(0);
  int k=0;
  while (k<this.getChildCount() && port != null) {
    if (port.edges().hasNext()) {
      DefaultEdge edge = (DefaultEdge) port.edges().next();
      NAryEdge relation = (NAryEdge) ((DefaultPort) edge.getSource()).getParent();
      relationContainer.add(relation);
    }
    port = (DefaultPort) this.getChildAt(k);
    k=k+1;
  }

  // Convert the Vector to DefaultGraphCell[].
  DefaultGraphCell[] result = new DefaultGraphCell[relationContainer.size()];
  for (int i = 0; i < relationContainer.size(); i++)
    result[i] = (NAryEdge) relationContainer.get(i);

  return result;
  }



public DefaultEdge[] getRoleEdges(String roleName) {
	Vector<DefaultEdge> elements = new Vector<DefaultEdge>();
	
	Iterator itEdges = this.getPort(roleName).edges();
	while (itEdges.hasNext()) {
		DefaultEdge edge = (DefaultEdge) itEdges.next();
		elements.add(edge);
	}
	
	
	// Convert elements to Object[]
	DefaultEdge[] result = new DefaultEdge[elements.size()];
	for (int i = 0; i < elements.size(); i++)
		result[i] = elements.get(i);
	return result;
}

  public Object clone() {
	  NAryEdge c = (NAryEdge) super.clone();
	/*  NAryEdgeEntity nae=(NAryEdgeEntity) c.getUserObject();
	  java.io.ByteArrayOutputStream bos=new  java.io.ByteArrayOutputStream();
	  java.io.ObjectOutputStream oos=new java.io.ObjectOutputStream(bos);
	  oos.writeObject(nae);
	  oos.close();
	  bos.close();
	  java.io.ObjectInputStream ois= new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(bos.toByteArray()));
	  NAryEdgeEntity nnae=ois.readObject();
	  ois.close();	  
	  c.setUserObject(ois);*/
	  c.roleData=(Hashtable) this.roleData.clone();
	return c;
}
  

  // Check if a role can be assigned to a node with data of class nodeDataClass according
  // to currentSolution.
  protected boolean checkAssignation(List currentSolution, String roleName, Class nodeDataClass) {
    HashSet classes = new HashSet( this.getClasses(roleName) );
    Integer maxAllowedTimes = this.getArity(roleName, false);
    Integer minAllowedTimes = this.getArity(roleName, true);
    Integer currentTimes = this.getCurrentArity(roleName);////////// Deber?hacer referencia al objeto.
    // currentTimes is updated with the times the role is already used in the solution.
    int currentUse = 0;
    // Last element is not assigned in currentSolution when checked.
    for (int i = 0; i < (currentSolution.size() - 1); i++) ///////// currentSolution.size() en vez de nodesIndex
      if ( ( (String) currentSolution.get(i) ).equals(roleName) )
        currentUse++;
    // Check the role for the class.
    if ( (containsInstance(classes.iterator(), nodeDataClass) &&
      (currentTimes.intValue() + currentUse) < maxAllowedTimes.intValue()) )
      return true;
    return false;
  }



 


  protected boolean containsInstance(java.util.Iterator enumeration, Class c){
    boolean result=false;
    while (enumeration.hasNext()){
      String cname=(String)enumeration.next();
      try {
      Class current=Class.forName(cname);
      if (current.isAssignableFrom(c))
         return true;
      } catch (Exception nnf){
        nnf.printStackTrace();
      }
    }
    return false;

  }
  
//Check if the solution is valid for selectedNodes.
  protected boolean checkSolution(GraphCell[] selectedNodes, List solution) {
    boolean ok = true;
    int selectedNodesMatched=0;
    if (solution.size() == selectedNodes.length) {
      List roles = this.getOrderedRoles();
      for (int i = 0; i < roles.size() && ok; i++) {
        String roleName = (String) roles.get(i);
        HashSet classes = new HashSet( this.getClasses(roleName) );
        Integer minAllowedTimes = this.getArity(roleName, true);
        Integer maxAllowedTimes = this.getArity(roleName, false);
        Integer currentTimes = this.getCurrentArity(roleName);////////// Deber?hacer referencia al objeto.
        // currentTimes is updated with the times the role is already used in the solution.
        int currentUse = 0;
        // Assignation to classes is  correct for this role.
        boolean classesAssignation = true;
        for (int j = 0; j < selectedNodes.length; j++){
        	
          if ( ( (String) solution.get(j) ).equals(roleName) ) {
            currentUse++;
            Class nodeDataClass=( (DefaultGraphCell) selectedNodes[j] ).getUserObject().getClass();
            if (!(containsInstance(classes.iterator(), nodeDataClass)))
              classesAssignation = false;
                        	
          }
        }
        // Check the role for the class.
        if ( !classesAssignation ||
          ( (currentTimes.intValue() + currentUse) > maxAllowedTimes.intValue()) || 
          ( (currentTimes.intValue() + currentUse) < minAllowedTimes.intValue()) )
          ok = false;
        else
        	if (classesAssignation)
        	selectedNodesMatched++;
      }
    } else
      ok = false;
    if (selectedNodes.length==1 && selectedNodesMatched==1)
    	ok=true;// modified to allow paste of edges that connect a single element to the naryedge
    
    return ok;
  }


}
