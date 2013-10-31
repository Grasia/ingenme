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

import java.util.*;
import java.io.*;

/**
 *  Defines an interface for traversing diagrams generated with INGENIAS IDE.
 *
 *@author     Jorge J. Gomez-Sanz
 *@created    30 November 2003
 */

public interface Browser {

	/**
	 *  Obtains all existing diagrams in the specification.
	 *
	 *@return    An array of graphs
	 */

	public Graph[] getGraphs();


	/**
	 *  Obtains all the entities defined in all diagrams. This array of entities
	 *  show no duplicates
	 *
	 *@return    an array of entities
	 */
	public GraphEntity[] getAllEntities();


	/**
	 *  Obtains a graph with a concrete id. If there is no any, it returns null
	 *
	 *@param  id  The id of the diagram. It must be the same as the one returned by
	 *      the "getID" method of a Graph instance
	 *@return     The diagram whose identificator is the same as id
	 */
	public Graph getGraph(String id);
	
	/**
	 * It locates the entity whose id is "id" in the whole specification. If no 
	 * entity is found, it returns a null value
	 * @param id The id of the entity to look for
	 * @return The entity or null
	 */
	public GraphEntity findEntity(String id);
	
	
	/**
	 * It locates the first graph where an entity with the "id" exists. If no 
	 * entity is found, it returns a null value
	 * @param id The id of the entity to look for
	 * @return The first graph found where this entity exists or none
	 */
	public Graph findFirstEntityOccurrence(String id);

	/**
	 * It obtains the internal descriptors associated with the editor
	 * @return
	 */
	public ingenias.editor.IDEState getState();
        
        /**
         * It returns the currently loaded project file. It returns null if none is available
         **/
        public File getProjectFilePath();
	
	
}
