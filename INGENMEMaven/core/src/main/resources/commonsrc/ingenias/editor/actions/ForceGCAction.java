
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

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import ingenias.editor.GUIResources;
import ingenias.editor.IDEState;

public class ForceGCAction {
	private IDEState ids;
	private GUIResources resources;
	
	public ForceGCAction(IDEState ids, GUIResources resources){
		this.ids=ids;
		this.resources=resources;
	}
	
	public void forcegc_actionPerformed(ActionEvent e) {
		long before = Runtime.getRuntime().freeMemory();
		System.gc();
		JOptionPane.showMessageDialog(resources.getMainFrame(),
				"Free memory before:" + before +
				" and now:" +
				Runtime.getRuntime().freeMemory(),
				"Free memory",
				JOptionPane.INFORMATION_MESSAGE);

	}
}
