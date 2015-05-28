
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

package ingenias.editor.actions;

import ingenias.editor.GUIResources;
import ingenias.editor.IDEState;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.tree.TreePath;

public class SearchAction {
	private IDEState ids;
	private GUIResources resources;
	
	protected Vector<TreePath> foundpaths=new Vector<TreePath>();

	protected int lastFoundIndex=0;

	protected String lastSearch="";
	
	public SearchAction(IDEState ids, GUIResources resources){
		this.ids=ids;
		this.resources=resources;
	}
	
	public void searchActionPerformed(ActionEvent evt) {
		String id=resources.getSearchField().getText();
		locateAndScrollToObject(id);

	}

	private void locateAndScrollToObject(String id) {
		if (id.equals(lastSearch) && lastFoundIndex <foundpaths.size() ){
			TreePath tp=(TreePath)this.foundpaths.elementAt(lastFoundIndex);
			ids.om.arbolObjetos.expandPath(tp);	  
			ids.om.arbolObjetos.scrollPathToVisible(tp);
			ids.om.arbolObjetos.setSelectionPath(tp);
			lastFoundIndex++;
		} else{
			foundpaths=this.ids.om.findUserObjectPathRegexp(".*"+id+".*");
			if (foundpaths.size()>0){
				lastFoundIndex=0;
				lastSearch=id;
				TreePath tp=(TreePath)this.foundpaths.elementAt(lastFoundIndex);
				ids.om.arbolObjetos.expandPath(tp);	  
				ids.om.arbolObjetos.scrollPathToVisible(tp);
				ids.om.arbolObjetos.setSelectionPath(tp);
				lastFoundIndex++;
			}
		}
	}
}
