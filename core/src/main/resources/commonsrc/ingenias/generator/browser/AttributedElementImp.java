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
import ingenias.editor.TypedVector;
import ingenias.exception.InvalidAttribute;
import ingenias.exception.InvalidEntity;
import ingenias.exception.NotFound;

import java.lang.reflect.*;
import java.util.*;

abstract class AttributedElementImp implements AttributedElement {

	private Object element;
	private ModelJGraph graph;
	private IDEState ids;

	AttributedElementImp(Object ent,ModelJGraph graph, IDEState ids ){
		this.element=ent;
		this.graph=graph;
		this.ids=ids;
	}

	public GraphAttribute[] getAllAttrs(){
		Field[] fields=element.getClass().getFields();
		GraphAttribute[] result=new GraphAttribute[fields.length];
		Vector v=new Vector();
		int k=0;
		boolean found=false;
		while (k<fields.length){
			try {
				result[k]=new GraphAttributeImp(fields[k].getName(),fields[k].get(this.element),graph,ids);
			} catch (IllegalAccessException iae){
				iae.printStackTrace();
			}

			k=k+1;
		}

		return result;

	}

		public static String  capitaliseFirstLetter(String word){
			return word.substring(0, 1).toUpperCase() + word.substring(1);
		}
	
	public void setAttribute(GraphAttribute ga) throws InvalidAttribute{
		try {
			GraphAttribute oldga=this.getAttributeByName(ga.getName());
			((GraphAttributeImp)oldga).setValue(((GraphAttributeImp)ga).getValue());
			Object nvalue=((GraphAttributeImp)ga).getValue();
			if (nvalue instanceof TypedVector){
	//				nvalue=((GraphCollectionImp)nvalue).getValue();
				Class nvalueclass=((TypedVector)nvalue).getType();
				Class entclass=this.element.getClass();
				Method m=
org.apache.commons.beanutils.MethodUtils.getMatchingAccessibleMethod(entclass, "add"+capitaliseFirstLetter(ga.getName()),
						new Class[]{nvalueclass});
				TypedVector tv=(TypedVector)nvalue;
				for (int k=0;k<tv.size();k++){
					m.invoke(element,new Object[]{tv.elementAt(k)});
				}
			} else {
				if (nvalue instanceof GraphEntity){
					nvalue=((GraphEntityImp)nvalue).getEntity();	
				}
				Class entclass=this.element.getClass();
				Method m=org.apache.commons.beanutils.MethodUtils.getMatchingAccessibleMethod(entclass,"set"+capitaliseFirstLetter(ga.getName()),
						new Class[]{nvalue.getClass()});

				m.invoke(element,new Object[]{nvalue});
			}
		} catch (NotFound e) {				
			throw new InvalidAttribute(e);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidAttribute(e);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidAttribute(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidAttribute(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InvalidAttribute(e);
		}	
	}


	 
	 public void setAttributeValue(String name,Object value) throws NotFound, InvalidEntity{

			Field[] fields=element.getClass().getFields();
			GraphAttribute result=null;
			int k=0;
			boolean found=false;
			String availableFields="";
			while (k<fields.length && !found){
				try {
					availableFields=availableFields+fields[k].getName()+"="+fields[k].get(element)+",";
				} catch (IllegalArgumentException e) {

					e.printStackTrace();
				} catch (IllegalAccessException e) {

					e.printStackTrace();
				}
				found=fields[k].getName().equalsIgnoreCase(name);
				if (!found) k++;
			}

			if (!found)
				throw new NotFound("Field "+name+" not found in entity "+ element+":"+element.getClass().getName()+".Available fields in "+element.getClass()+" are "+availableFields);
			if (fields[k].getType().isAssignableFrom(value.getClass())){
				try {
					fields[k].set(element, value);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new InvalidEntity("Could not assign value to field "+name,e);
				}
			} else 
				throw new InvalidEntity("Supplied value is not compatible with field "+name);
	 }

	public GraphAttribute getAttributeByName(String name) throws NotFound{

		Field[] fields=element.getClass().getFields();
		GraphAttribute result=null;
		int k=0;
		boolean found=false;
		String availableFields="";
		while (k<fields.length && !found){
			try {
				availableFields=availableFields+fields[k].getName()+"="+fields[k].get(element)+",";
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			}
			found=fields[k].getName().equalsIgnoreCase(name);
			if (!found) k++;
		}

		if (!found)
			throw new NotFound("Field "+name+" not found in entity "+ element+":"+element.getClass().getName()+".Available fields in "+element.getClass()+" are "+availableFields);
		try {
			result=new GraphAttributeImp(name,fields[k].get(this.element),graph,ids);
		} catch (IllegalAccessException iae){
			iae.printStackTrace();
		}
		return result;
	}

}
