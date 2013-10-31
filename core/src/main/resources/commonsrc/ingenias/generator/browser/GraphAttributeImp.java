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

import ingenias.editor.IDEState;
import ingenias.editor.ModelJGraph;
import ingenias.exception.*;

public class GraphAttributeImp
implements GraphAttribute {

	private Object attribute;
	private String name;
	private ModelJGraph graph;
	private IDEState ids;

	public GraphAttributeImp(String name, Object attribute,
			ModelJGraph graph, IDEState ids) {
		this.name = name;
		this.attribute = attribute;
		this.graph = graph;
		this.ids=ids;
	}

	public String getSimpleValue() {
		if (attribute == null) {
			return "";
		}
		else {
			return attribute.toString();
		}
	}

	public GraphEntity getEntityValue() throws NullEntity {
		if (attribute == null) {
			throw new NullEntity();
		}
		else {
			return new GraphEntityImp( (ingenias.editor.entities.Entity) attribute,
					graph,ids);
		}
	}

	public GraphCollection getCollectionValue() throws NullEntity {
		return new GraphCollectionImp( (ingenias.editor.TypedVector) attribute,
				graph,ids);
	}

	public boolean isCollectionValue(){
		return attribute!=null && ingenias.editor.TypedVector.class.isAssignableFrom(attribute.getClass());
	}

	public boolean isEntityValue(){
		return attribute!=null && ingenias.editor.entities.Entity.class.isAssignableFrom(attribute.getClass());
	}

	public boolean isSimpleValue(){
		return !isCollectionValue() && !isEntityValue();
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return attribute.getClass().getName();
	}


	protected Object getValue(){
		return this.attribute;
	}

	protected void setValue(Object value){
		this.attribute=value;
	}




}