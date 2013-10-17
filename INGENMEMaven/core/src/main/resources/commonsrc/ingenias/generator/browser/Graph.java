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

import ingenias.exception.NullEntity;

import java.util.*;

// This is the interface for an INGENIAS diagram
public interface Graph extends AttributedElement {

 // Obtains the id of the graph
 public String getName();
 // Obtains the type of the graph
 public String getType();

 // Obtains all existing relationships in a diagram
 public GraphRelationship[] getRelationships();
 // Obtains all existing entities in a diagram
 public GraphEntity[] getEntities()  throws NullEntity;
 
//Obtains all existing entities in a diagram without eliminating duplicates
 public GraphEntity[] getEntitiesWithDuplicates()  throws NullEntity;
 
 // Generates a PNG image of a diagram
 public void generateImage(String filename);

 // It returns an array with the path of packages above this diagram
 public String[] getPath();

 // It returns a JPanel which can be used in a GUI to show what
 // this diagram contains
 public ingenias.editor.ModelJGraph getGraph();
 
 public Vector<GraphEntity> findEntity(String sourceTaskID);
 
 public void remove(GraphRelationship rel);
 public void remove(GraphEntity ent);

}