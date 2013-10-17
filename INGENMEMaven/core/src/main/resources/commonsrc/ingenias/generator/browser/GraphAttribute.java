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

import ingenias.exception.*;

// This is the interface for an attribute belonging to an
// class implementing the AttributedElement interface
public interface GraphAttribute {

	/* static final int _GRAPH_ENTITY=0;
   static final int _STRING=1;*/
	// Obtains the value of this attribute considering it as an string
	public String getSimpleValue();

	// Obtains the value of this attribute considering it as a GraphEntity
	// If the attribute is set to null, a NullEntity exception will be thrown
	public GraphEntity getEntityValue() throws NullEntity;

	// If this type is a collection, it obtains all associated instances
	// If the attribute is set to null, a NullEntity exception will be thrown
	public GraphCollection getCollectionValue() throws NullEntity;

	// Name of the attribute
	public String getName();

	// Type of the attribute
	public String getType();

	public boolean isCollectionValue();

	public boolean isEntityValue();

	public boolean isSimpleValue();

}