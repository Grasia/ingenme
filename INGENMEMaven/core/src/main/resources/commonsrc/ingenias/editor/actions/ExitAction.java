
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
import ingenias.editor.persistence.PersistenceManager;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

public class ExitAction {
	private IDEState ids;
	private GUIResources resources;

	public ExitAction(IDEState ids, GUIResources resources){
		this.ids=ids;
		this.resources=resources;
	}

	public void exit_actionPerformed() {
		int result = JOptionPane.OK_OPTION;
		if (ids.isChanged()) {
			result = JOptionPane.showConfirmDialog(resources.getMainFrame(),
					"If you exit, you will loose all changes. Are you sure?",
					"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		}

		long ctime = System.currentTimeMillis();
		//StatsManager.saveSession(ctime,initTime);

		if (result == JOptionPane.OK_OPTION) {
			new PersistenceManager().savePreferences(ids);
			resources.getMainFrame().setVisible(false);
			System.exit(0);
			
		}
	}
}
