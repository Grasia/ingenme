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
import ingenias.exception.InvalidAttribute;
import ingenias.exception.InvalidEntity;
import ingenias.exception.NotFound;

import java.util.Iterator;

// This is the interface for an element that can have attributes.
public interface AttributedElement {

	// Obtains the id of the entity
	public String getID();
	
 // Obtains all associated attributes
 public GraphAttribute[]  getAllAttrs();

 // Obtains an attribute just by its name
 public GraphAttribute getAttributeByName(String name) throws NotFound;
 
 public void setAttributeValue(String name,Object value) throws NotFound, InvalidEntity;
 /**
	 * sets the value of an attribute to "ga"
	 * @param ga Attribute to be updated
	 * @throws InvalidAttribute the attribute pased as
	 *  value was not an original attribute of this entity
	 *  
	 */
 public void setAttribute(GraphAttribute ga) throws InvalidAttribute;
}