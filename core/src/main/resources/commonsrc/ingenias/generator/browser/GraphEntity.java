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

import ingenias.editor.entities.Entity;
import ingenias.exception.InvalidAttribute;

import java.util.*;

//It represents an entity in a diagram. It can contain attributes.
public interface GraphEntity  extends AttributedElement{
	
	// Obtains all relationships of an entity in the diagram from which
	// this entity was extracted
	public GraphRelationship[] getRelationships();
	public GraphRelationship[] getRelationships(String type);
	
	// Obtains all relationships in which this entity participates
	// no matter what is the diagram
	public Vector<GraphRelationship> getAllRelationships();
	public Vector<GraphRelationship> getAllRelationships(String relType);
	
	// Obtains the id of the entity
	public String getID();
	
	// Obtains the type of the entity
	public String getType();
	
	public Entity getEntity();
	
	
	/**
	 * sets the value of an attribute to "ga"
	 * @param ga Attribute to be updated
	 * @throws InvalidAttribute the attribute pased as
	 *  value was not an original attribute of this entity
	 *  
	 */
	public void setAttribute(GraphAttribute ga) throws InvalidAttribute;
	
}