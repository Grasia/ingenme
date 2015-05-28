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

 import ingenias.editor.TypedVector;
 import ingenias.editor.entities.Entity;
import ingenias.exception.NullEntity;

import org.jgraph.JGraph;

 // It represents a collection of GraphEntities
 public interface GraphCollection {

   // It tells how many elements are there in the collection
   public int size();

   // It tells what is the k-th element
   public GraphEntity getElementAt(int k)  throws NullEntity;

}