
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

package ingenias.editor.persistence;

import java.lang.reflect.*;
import javax.swing.tree.*;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.InputSource;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

import javax.xml.parsers.*;
import org.jgraph.JGraph;
import org.jgraph.graph.*;
import org.w3c.dom.*;
import ingenias.editor.entities.*;
import ingenias.exception.*;
import ingenias.editor.cell.*;
import ingenias.editor.*;


/**
 *  Description of the Class
 *
 *@author     developer
 *@created    9 de agosto de 2003
 */
public class GraphSave {
	/**
	 *  Constructor for the GraphSave object
	 */
	public GraphSave() { }


	/**
	 *  Saves a diagram description. The diagram description contains
         *  information about physical position of different entities in
         *  a diagram. There is no reference to the links among entities here.
         *  This information appear previously in the "relationships" section
	 *
	 *@param  model                    Diagram to save
	 *@param  modelPath                Allocation of this diagram in the project tree
	 *@param  fos                      Stream to write the XML description to
	 *@exception  java.io.IOException  Description of Exception
	 */
	public static void saveModel(ModelJGraph model, TreeNode[] modelPath, OutputStreamWriter fos) throws java.io.IOException {

		//String mid = ingenias.editor.entities.Entity.encodeutf8Text(model.getID());
		String mid = ingenias.editor.entities.Entity.encodeutf8Text(model.getID());
		ObjectSave objsave=new ObjectSave();
		fos.write((" <model id=\"" + mid + "\" type=\"" +
				model.getClass().getName() + "\">\n"));
		objsave.saveObject(model.getProperties(), fos);
		fos.write("  <path>\n");
		for (int k = 0; k < modelPath.length - 1; k++) {
			String packageName = ((DefaultMutableTreeNode) modelPath[k]).
					getUserObject().toString();
			packageName = ingenias.editor.entities.Entity.encodeutf8Text(
					packageName);
			fos.write(("    <package id=\"" + packageName + "\"/>\n"));
		}
		fos.write("   </path>\n");

		diagramToXML(model, fos);
		fos.write(" </model>\n");

	}



	/**
	 *  The main program for the GraphSave class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args) {
		GraphSave graphSave1 = new GraphSave();
	}


	/**
	 *  Gets the vertex attribute of the GraphSave class
	 *
	 *@param  graph   Description of Parameter
	 *@param  object  Description of Parameter
	 *@return         The vertex value
	 */
	private static boolean isVertex(JGraph graph, Object object) {
		if (!(object instanceof org.jgraph.graph.Port) && !(object instanceof org.jgraph.graph.Edge)) {
			return !(isGroup(graph, object));
		}
		return false;
	}


	/**
	 *  Gets the group attribute of the GraphSave class
	 *
	 *@param  graph  Description of Parameter
	 *@param  cell   Description of Parameter
	 *@return        The group value
	 */
	private static boolean isGroup(JGraph graph, Object cell) {
		// Map the Cell to its View
		CellView view = graph.getGraphLayoutCache().getMapping(cell, false);
		if (view != null) {
			return !view.isLeaf();
		}
		return false;
	}


	/**
	 *  Gets the extreme attribute of the GraphSave class
	 *
	 *@param  edge  Description of Parameter
	 *@param  dgc   Description of Parameter
	 *@return       The extreme value
	 */
	private static DefaultGraphCell getExtreme(org.jgraph.graph.Edge edge, Object dgc) {		
		if (((DefaultPort) edge.getTarget()).getParent() == dgc) {
			return (DefaultGraphCell) ((DefaultPort) edge.getSource()).getParent();
		} else {
			return (DefaultGraphCell) ((DefaultPort) edge.getTarget()).getParent();
		}
	}



	/**
	 *  Create a GXL-representation for the specified cells.
	 *
	 *@param  graph   Diagram to translate to XML
	 *@param  fos  Output stream to write to
	 */

	private static void diagramToXML(ModelJGraph graph, OutputStreamWriter fos) {

		// cells contains all elements in graph.
		Object[] cells = new Object[graph.getModel().getRootCount()];
		for (int k=0;k<cells.length;k++)
			cells[k]=graph.getModel().getRootAt(k);

		Hashtable<DefaultGraphCell,Integer> hash= new Hashtable<DefaultGraphCell,Integer>();
		String gxl = "\n\t<graph>";
		
		
		
		
		String gxlView = "\n\t<layout>";

		// Create external keys for nodes
		for (int i = 0; i < cells.length; i++) {
			if (isVertex(graph, cells[i])) {
				hash.put((DefaultGraphCell) cells[i], new Integer(hash.size()));

				// Convert Nodes
			}
		}
		String hierarchyorder="<parentship>\n";
		hierarchyorder=hierarchyorder+graph.getListenerContainer().parentshipToXML(hash);
		hierarchyorder=hierarchyorder+"</parentship>\n";
		hierarchyorder=hierarchyorder+"<entityconstraints>\n";
		hierarchyorder=hierarchyorder+graph.getListenerContainer().constraintsToXML(hash);
		hierarchyorder=hierarchyorder+"</entityconstraints>\n";
	
		
		Iterator it = hash.keySet().iterator();
		while (it.hasNext()) {
			Object node = it.next();
			if (node instanceof DefaultGraphCell) {
				gxl += "\n\t\t\t" + vertexGXL(graph, "node" + hash.get(node), node,hash);
				gxlView += "\n\t\t\t" +
						cellViewGXL(graph, "node" + hash.get(node), node);
			}
		}
		gxl=gxl+hierarchyorder+"\n"; // to ensure parentship is process
		// Close main tags
		gxl += "\n\t</graph>";
		gxlView += "\n\t</layout>";
		gxl = "<gxl>\n" + gxl + "\n\n" + gxlView + "\n</gxl>";

		try {
                      // Create data output stream

			// Write the GXL string to file.
			fos.write(gxl);
			// Close the data output stream
			fos.flush();
		}
		catch (IOException e) {
			// Display error message on stderr
			System.err.println(e.toString());
		}
	}



	/**
	 *  Returns the XML representation of the CellView related to node.
	 *
	 *@param  graph   Diagram that contains an entity
	 *@param  id      ID of the entity that is contained in the cell
	 *@param  vertex  Cell allocated in the diagram that contains the previous entity
	 *@return         Description of the Returned Value
	 */
	private static String cellViewGXL(JGraph graph, Object id, Object vertex) {
		Map attributes = null;
		String gxl = "";
		// Some cells, though removed, remain in the model. With this "if", they are omitted
	//	if (graph.getGraphLayoutCache().getMapping((DefaultGraphCell) vertex, false) != null) {
			attributes =graph.getModel().getAttributes( vertex);
				//graph.getGraphLayoutCache().getMapping((DefaultGraphCell) vertex, false).
				//	getAllAttributes();
			gxl = "\n\t<node id=\"" + id.toString() + "\">\n";
			if (vertex instanceof NAryEdge){
				NAryEdge edge=(NAryEdge)vertex;
				DefaultEdge[] edges=edge.getRepresentation();
				AttributeMap am=edges[0].getAttributes();
				Object[] labels = GraphConstants.getExtraLabels(am);

				//attributes.put("label",)
			}
			if (attributes != null) {
				gxl = gxl + Map2GXLattr(attributes);
			}
			gxl = gxl + "\n\t</node>";

	
		return gxl;
	}


	/**
	 *  Convert a vertex to a string representing it in GXL.
	 *
	 *@param  graph   Description of Parameter
	 *@param  id      Description of Parameter
	 *@param  vertex  Description of Parameter
	 * @param hash 
	 *@return         Description of the Returned Value
	 */
	private static String vertexGXL(JGraph graph, Object id, Object vertex, Hashtable<DefaultGraphCell,Integer> ids) {

		String gxl = "";
		// Some cells, though removed, remain in the model. With this "if", they are omitted
		//if (graph.getGraphLayoutCache().getMapping((DefaultGraphCell) vertex, true) != null) {
			// DefaultGraphCell implements MutableTreeNode.
			// The UserObject in our JGraph is instance of Entity.
			// The UserObject data are expressed in GXL.
			if (vertex instanceof DefaultMutableTreeNode &&
					((DefaultMutableTreeNode) vertex).getUserObject() != null) {

				Object userObject = ((DefaultMutableTreeNode) vertex).getUserObject();

				if (userObject instanceof ingenias.editor.entities.Entity) {

					// Attributes
					ingenias.editor.entities.Entity en = (ingenias.editor.entities.Entity)
							userObject;
					String _enid = en.encodeutf8Text(en.getId());
					
					// id and type are attributes in GXL, not children.
					gxl = "\n\t<node id=\"" + _enid + "\" nid=\"" + id+// vertex.hashCode() +
							"\" type=\"" + en.getType() + "\">\n";
					if (graph.getModel().getAttributes(vertex)!=null)
					gxl=gxl+Map2GXLattr(graph.getModel().getAttributes(vertex));
					if (userObject instanceof ingenias.editor.entities.NAryEdgeEntity) {
						DefaultGraphCell dgc = (DefaultGraphCell) vertex;
						Iterator ports = dgc.getChildren().iterator();
						while (ports.hasNext()) {
							Object port = ports.next();
							Iterator it = graph.getModel().edges(port);
							while (it.hasNext()) {
								org.jgraph.graph.Edge current =
										(org.jgraph.graph.Edge) it.next();
								DefaultGraphCell extr = getExtreme(current, dgc);
								gxl = gxl + "\n\t<connected id=\"node" + ids.get(extr) + "\">";
								if (current.getAttributes()!=null){
									
								 gxl=gxl+Map2GXLattr(current.getAttributes());
								}
								gxl = gxl + "\n\t</connected>";

							}
						}
						
					}
					// The other attributes.
					gxl += "\n\t</node>";

				} else {
					gxl = userObject.toString();

				}
			} else if (vertex != null) {
				gxl = vertex.toString();
			}
		//}
		return gxl;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  object  Description of Parameter
	 *@param  key     Description of Parameter
	 *@return         Description of the Returned Value
	 */
	private static String object2GXL(Object object, String key) {
		String gxl = "";
		
		if (object instanceof java.awt.geom.Point2D) {
			gxl = point2GXL((java.awt.geom.Point2D) object, key);
		} else if (object instanceof java.awt.geom.Rectangle2D) {
			gxl = rectangle2GXL((java.awt.geom.Rectangle2D) object, key);
		} else if (object instanceof List) {
			gxl = list2GXL((List) object, key);
		} else if (object instanceof String) {
			gxl = string2GXL((String) object, key);
		} 
		
		else if (object instanceof Object[]) {
			gxl = array2GXL((Object[]) object, key);
			/*
			 *  else
			 *  gxl = "<attr name=\"" + key + "\">" +
			 *  "\n\t\t\t<string>" + object.toString() + "</string>" +
			 *  "</attr>";
			 */
		} else {			
			// if the entity is not recognised, nothing is done with it
			//throw new RuntimeException("Could not convert xml to object with "+object.toString());
			
		}
		return gxl;
	}


	/**
	 *  Object conversions to GXL Strings.
	 *
	 *@param  point  Description of Parameter
	 *@param  key    Description of Parameter
	 *@return        Description of the Returned Value
	 */

	private static String point2GXL(Point2D point, String key) {
		String gxl = "\t\t<attr name=\"" + key + "\">" +
				"\n\t\t\t<point " +
				"x=\"" + (new Double(point.getX())).doubleValue() + "\" " +
				"y=\"" + (new Double(point.getY())).doubleValue() + "\"" +
				">" +
				"</point>\n" +
				"\t\t</attr>\n";
		return gxl;
	}
	
	private static String string2GXL(String value, String key) {
		String gxl = "\t\t<attr name=\"" + key + "\">" +
				"\n\t\t\t<string>" +
				ingenias.generator.util.Conversor.replaceInvalidChar(value)+
				"</string>\n" +
				"\t\t</attr>\n";
		return gxl;
	}
	
	


	/**
	 *  Description of the Method
	 *
	 *@param  rectangle  Description of Parameter
	 *@param  key        Description of Parameter
	 *@return            Description of the Returned Value
	 */
	private static String rectangle2GXL(Rectangle2D rectangle, String key) {
		String gxl = "\t\t<attr name=\"" + key + "\">" +
				"\n\t\t\t<rectangle " +
				"x=\"" + (new Double(rectangle.getX())).intValue() + "\" " +
				"y=\"" + (new Double(rectangle.getY())).intValue() + "\" " +
				"width=\"" + (new Double(rectangle.getWidth())).intValue() + "\" " +
				"height=\"" + (new Double(rectangle.getHeight())).intValue() + "\"" +
				">" +
				"</rectangle>" +
				"\t\t</attr>";
		return gxl;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  arrayObject  Description of Parameter
	 *@param  key          Description of Parameter
	 *@return              Description of the Returned Value
	 */
	private static String array2GXL(Object[] arrayObject, String key) {
		String gxl = "<attr name=\"" + key + "\">" +
				"\n\t\t\t<array>\n";
		for (int i = 0; i < arrayObject.length; i++) {
			gxl += object2GXL(arrayObject[i], key + new Integer(i));
		}
		gxl += "\n</array>" +
				"\n</attr>";
		return gxl;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  list  Description of Parameter
	 *@param  key   Description of Parameter
	 *@return       Description of the Returned Value
	 */
	private static String list2GXL(List list, String key) {
		String gxl = "\t\t<attr name=\"" + key + "\">" +
				"\n\t\t\t<list>\n";
		for (int i = 0; i < list.size(); i++) {
			gxl += object2GXL(list.get(i), key + new Integer(i));
		}
		gxl += "\n</list>" +
				"\n\t\t</attr>";
		return gxl;
	}



	/**
	 *  Convert a Map to a sequence of GXL attributes as text.
	 *
	 *@param  attributes  Description of Parameter
	 *@return             Description of the Returned Value
	 */
	private static String Map2GXLattr(Map attributes) {
		
		Iterator it = attributes.keySet().iterator();
		String gxl = "";

		while (it.hasNext()) {
			String key = it.next().toString();
			Object value = attributes.get(key);
			if (value != null) {
				gxl += object2GXL(value, key) + "\n";
			}
		}

		return gxl;
	}

}
