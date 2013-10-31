
/** 
 * Copyright (C) 2010  Jorge J. Gomez-Sanz sobre código original de Rubén Fuentes
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

package ingenias.editor.persistence;

import java.lang.reflect.*;
import javax.swing.tree.*;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.InputSource;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;
import javax.xml.parsers.*;
import org.jgraph.JGraph;
import org.jgraph.graph.*;
import org.w3c.dom.*;
import ingenias.editor.entities.*;
import ingenias.exception.*;
import ingenias.editor.cell.*;
import ingenias.editor.*;
import ingenias.generator.browser.Browser;
import ingenias.generator.browser.BrowserImp;

public class ObjectLoadImp1
implements ObjectLoad {
	private Browser browser;

	public ObjectLoadImp1(Browser browser){
		this.browser=browser;
	}
	
	public ingenias.editor.entities.Entity restoreObject(ObjectManager om,
			GraphManager gm, org.w3c.dom.Node n) throws
			ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			InstantiationException, InvocationTargetException {
		ingenias.editor.entities.Entity en = null;
		String type = n.getAttributes().getNamedItem("type").getNodeValue();
		String id = n.getAttributes().getNamedItem("id").getNodeValue();
		// To restore objects from existing ones

		if (id.equals("140")){
			int m=1;
		}
		// if it is a relationship and it has already being retrieved
		if (RelationshipManager.getRelationship(id) != null) {
			return RelationshipManager.getRelationship(id);
		}

		if (om.getEntity(id, type) != null) {
			en = om.getEntity(id, type);
		} else {
			Class encClass = Class.forName(type);
			if (!ingenias.editor.entities.ModelEntity.class.isAssignableFrom(encClass)
					&& om.getValidEntitiesClasses().contains(encClass)) {
				if (en == null) {

					int index = type.lastIndexOf(".");
					String className = type.substring(index + 1, type.length());
//					String id=n.getAttributes().getNamedItem("id").getNodeValue();
					Class objectManager = om.getClass();
					Object[] params = {
							id};
					Class[] paramtype = {
							"".getClass()};
					java.lang.reflect.Method m = objectManager.getDeclaredMethod("create" +
							className, paramtype);
					en = (ingenias.editor.entities.Entity) m.invoke(om, params);
				}
				NodeList children = n.getChildNodes();
				for (int k = 0; k < children.getLength(); k++) {
					org.w3c.dom.Node current = children.item(k);
					PersistenceManager.getPL().restoreProperty(om, gm, current, en);
				}
			}
			else {
				if (ingenias.editor.entities.ModelEntity.class.isAssignableFrom(encClass)) {
//					String id=n.getAttributes().getNamedItem("id").getNodeValue();
					NodeList children = n.getChildNodes();
					org.w3c.dom.Node current = null;
					boolean found = false;
					for (int k = 0; k < children.getLength() && !found; k++) {
						current = children.item(k);
						if (current.getNodeName().equalsIgnoreCase("mapproperties")) {
							found = true;
						}
					}
					if (found) {
						Class[] types = {
								String.class};
						Object[] params = {
								 Editor.getNewId(browser)};
						en = (ModelEntity) encClass.getConstructor(types).newInstance(params);
						PersistenceManager.getPL().restoreProperty(om, gm, current, en);
					}
				}
				else {
					int index = type.lastIndexOf(".");
					String className = type.substring(index + 1, type.length());
//					String id=n.getAttributes().getNamedItem("id").getNodeValue();
					Class objectManager = om.getClass();
					Object[] params = {
							id};
					Class[] paramtype = {
							"".getClass()};
					Constructor cons = Class.forName(type).getConstructor(paramtype);
					en = (ingenias.editor.entities.Entity) cons.newInstance(params);
					NodeList children = n.getChildNodes();
					for (int k = 0; k < children.getLength(); k++) {
						org.w3c.dom.Node current = children.item(k);
						PersistenceManager.getPL().restoreProperty(om, gm, current, en);
					}


				}
			}
		}
		return en;
	}

	public static void main(String[] args) {
		ObjectLoad objectLoad1 = new ObjectLoadImp1(new BrowserImp(IDEState.emptyIDEState()));
	}

}
