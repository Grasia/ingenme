
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

package ingenias.editor;

import javax.swing.tree.DefaultTreeModel;

import ingenias.editor.events.DiagramChangeHandler;

public class IDEChangesHandler implements DiagramChangeHandler{
	
	private IDEState ids;
	private GUIResources resources;
	
	public IDEChangesHandler(IDEState ids, GUIResources resources){
		this.ids=ids;
		this.resources=resources;
	}



	public void addNewDiagram(ModelJGraph mjg) {
		ids.setChanged(true);
		resources.setChanged();
		ids.gm.reload();		
		ids.editor.reloadDiagrams();
		resources.getArbolProyectos().repaint();		
	}

	public void addNewPackage(Object[] path, String nombre) {
		ids.setChanged(true);
		resources.setChanged();
		ids.gm.reload();		
		ids.editor.reloadDiagrams();
		resources.getArbolProyectos().repaint();		
		
	}

	public void diagramDeleted(ModelJGraph mj) {
		ids.setChanged(true);
		resources.setChanged();
		ids.gm.reload();		
		ids.editor.reloadDiagrams();
		resources.getArbolProyectos().repaint();				
	}

	public void diagramPropertiesChanged(ModelJGraph mjg) {
		ids.setChanged(true);
		resources.setChanged();
		ids.gm.reload();		
		ids.editor.reloadDiagrams();
		resources.getArbolProyectos().repaint();		
	}

	public void diagramRenamed(ModelJGraph mjg) {
		ids.setChanged(true);
		resources.setChanged();
		ids.gm.reload();		
		ids.editor.reloadDiagrams();
		resources.getArbolProyectos().repaint();		
	}

	public void packageRenamed(String result) {
		ids.setChanged(true);
		resources.setChanged();
		ids.gm.reload();			
		resources.getArbolObjetos().repaint();
	}



	public void otherChange() {
		ids.setChanged(true);		
		resources.setChanged();		
	}

}
