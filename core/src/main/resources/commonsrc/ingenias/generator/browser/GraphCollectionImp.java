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

import java.util.Vector;

import ingenias.editor.IDEState;
import ingenias.editor.ModelJGraph;
import ingenias.editor.TypedVector;
import ingenias.editor.entities.Entity;
import ingenias.exception.InvalidColection;
import ingenias.exception.NullEntity;

import org.jgraph.JGraph;

public class GraphCollectionImp implements GraphCollection {

  TypedVector tv=null;
  ModelJGraph g;
  IDEState ids;

  public GraphCollectionImp(TypedVector tv, ModelJGraph g, IDEState ids) {
    this.tv=tv;
    this.g=g;
    this.ids=ids;
  }

  public int size(){
    return tv.size();
  }

  public GraphEntity getElementAt(int k)  throws NullEntity{
  
		return new GraphEntityImp((Entity)tv.elementAt(k),g,ids);
	
  }

public TypedVector getValue() {
	// TODO Auto-generated method stub
	return this.tv;
}

@Override
public void addElementAt(int k, GraphEntity ge) {
	this.tv.add(k,ge.getEntity());
	
}

@Override
public void addElementAt( GraphEntity ge) {
	this.tv.add(ge.getEntity());
}


public void removeElementAt( int k) {
	this.tv.remove(k);
}

public void removeElement( GraphEntity ge) {
	this.tv.remove(ge);
}
  
@Override
public boolean contains( GraphEntity ge) {
	return tv.contains(ge);
	
} 


}