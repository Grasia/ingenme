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
package ingenias.editor.filters;

import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

public class DiagramFilter {
	
	public Hashtable<String, Vector<String>> getCurrentAllowedRelationships() {
		return currentAllowedRelationships;
	}

	public void setCurrentAllowedRelationships(
			Hashtable<String, Vector<String>> currentAllowedRelationships) {
		this.currentAllowedRelationships = currentAllowedRelationships;
	}

	private Hashtable<String,Vector<String>> currentAllowedEntities=new Hashtable<String,Vector<String>>();
	private Hashtable<String,Vector<String>> currentAllowedRelationships=new Hashtable<String,Vector<String>>();
	private String name;
	
	public DiagramFilter(){
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Hashtable<String, Vector<String>> getCurrentAllowedEntities() {
		return currentAllowedEntities;
	}

	public void setCurrentAllowedEntities(
			Hashtable<String, Vector<String>> currentAllowedEntities) {
		this.currentAllowedEntities = currentAllowedEntities;
	}

	
	
	public boolean isValidEntity(String diagramType, String entityType){
		if (this.currentAllowedEntities.containsKey(diagramType)){
			return this.currentAllowedEntities.get(diagramType).contains(entityType);
		}
		return false;
	}	
	public boolean isValidRelationship(String diagramType, String relationshipType){
		if (this.currentAllowedRelationships.containsKey(diagramType)){
			return this.currentAllowedRelationships.get(diagramType).contains(relationshipType);
		}
		return false;
	}	
	
	public boolean isValidDiagram(String diagramType){
		return (this.currentAllowedRelationships.containsKey(diagramType) || this.currentAllowedEntities.containsKey(diagramType));
	}	
	
	public String toString(){
		Set<String> keys = currentAllowedEntities.keySet();
		StringBuffer filter=new StringBuffer();
		filter.append("filter "+name+" ------>");
		for (String key:keys){
			filter.append("diagram "+key+":");
			for (String entity:currentAllowedEntities.get(key)){
				filter.append(entity+",");
			}
			filter.append("\n");
		}
		return filter.toString();
	}
	
	public boolean equals(Object object){
		if (object instanceof DiagramFilter){
			DiagramFilter target=(DiagramFilter)object;
			return target.name.equals(name) &&
			currentAllowedEntities.equals(target.getCurrentAllowedEntities()) &&
			currentAllowedRelationships.equals(target.getCurrentAllowedRelationships());
		}
		return super.equals(object);
	}

}
