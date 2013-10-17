
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

import java.awt.BorderLayout;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import ingenias.editor.GUIResources;
import ingenias.editor.GraphManager;
import ingenias.editor.IDEState;
import ingenias.editor.IDEUpdater;
import ingenias.editor.ObjectManager;
import ingenias.editor.widget.DnDJTree;

public class NewProjectAction {
	private IDEState ids;
	private GUIResources resources;

	public NewProjectAction(IDEState ids, GUIResources resources){
		this.ids=ids;
		this.resources=resources;
	}
	
	public void newProject_actionPerformed(IDEUpdater updater) {

		DnDJTree arbolProyectos=resources.getArbolProyectos();
		JTree arbolObjetos=resources.getArbolObjetos();
		DefaultMutableTreeNode rootProyectos=(DefaultMutableTreeNode) arbolProyectos.getModel().getRoot();
		DefaultMutableTreeNode rootObjetos=(DefaultMutableTreeNode) arbolObjetos.getModel().getRoot();
		
		int result = JOptionPane.OK_OPTION;
		if (ids.isChanged()) {
			result = JOptionPane.showConfirmDialog(resources.getMainFrame(),
					"If you create a new project, you will loose all changes. Are you sure?",
					"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		}
		if (result == JOptionPane.OK_OPTION) {
			//resources.get.buttonModelPanel.removeAll();
			IDEState nids = IDEState.emptyIDEState();
			
			updater.updateIDEState(nids);

			// If no exception is thrown, then the load process was succesful.
			// So current data can be replaced with the new one
			/*resources.getPprin().removeAll();
			ids.editor = nids.editor;
			resources.getPprin().add(ids.editor, BorderLayout.CENTER);

			this.replaceTree(rootObjetos,
					(DefaultMutableTreeNode) nids.om.arbolObjetos.getModel().
					getRoot());
			this.replaceTree(rootProyectos,
					(DefaultMutableTreeNode) nids.gm.arbolProyecto.getModel().
					getRoot());
			nids.gm.arbolProyecto = (DnDJTree) arbolProyectos;
			nids.gm.root = rootProyectos;
			nids.om.arbolObjetos = arbolObjetos;
			nids.om.setRoot((DefaultMutableTreeNode) resources.getArbolObjetos().getModel().getRoot());

			( (DefaultTreeModel) arbolProyectos.getModel()).reload();
			( (DefaultTreeModel) arbolObjetos.getModel()).reload();

			
			this.ids.gm = nids.gm;
			
			this.ids.om = nids.om;

			this.ids.prop = nids.prop;
			resources.getMainFrame().validate();

			// Stores last file name path so that next save is performed in the same
			// folder

			resources.getMainFrame().setTitle("Empty project");
			ids.setChanged(false);
			resources.setUnChanged();
			ids.setCurrentFile(null);*/      
		}

	}
	
	private void replaceTree(DefaultMutableTreeNode replaced,
			DefaultMutableTreeNode replacee) {

		replaced.removeAllChildren();
		while (replacee.getChildCount() > 0) {
			DefaultMutableTreeNode first = (DefaultMutableTreeNode) replacee.
			getChildAt(0);
			replacee.remove(first);
			first.removeFromParent();
			replaced.add(first);
			first.setParent(replaced);
		}

	}
}
