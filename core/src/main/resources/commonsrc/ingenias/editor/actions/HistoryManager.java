
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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;


import ingenias.editor.IDEAbs;
import ingenias.editor.DiagramPaneInitialization;
import ingenias.editor.GUIResources;
import ingenias.editor.IDEState;
import ingenias.editor.IDEUpdater;
import ingenias.editor.ProgressListener;
import ingenias.editor.VisitedFileMenuItem;

public class HistoryManager {
	private IDEState ids;
	private GUIResources resources;

	

	public HistoryManager(IDEState ids, GUIResources resources){
		this.ids=ids;
		this.resources=resources;
	}

	public static void updateProperties(Properties extprop, IDEState ids) {
		Enumeration enumeration = extprop.keys();
		while (enumeration.hasMoreElements()) {
			Object key = enumeration.nextElement();
			if (!ids.prop.containsKey(key)) {
				ids.prop.put(key, extprop.get(key));
			}
		}
		//System.err.println (ids.prop);
	}

	protected void updateHistoryButtons() {
		// The View Argument Defines the Context
		//	ids.btb.getUndo().setEnabled(undoManager.canUndo(getGraph().getGraphLayoutCache()));
		//	ids.btb.getRedo().setEnabled(undoManager.canRedo(getGraph().getGraphLayoutCache()));
	}



	public static void updateHistory(File newFile, final GUIResources resources, final IDEState ids, final IDEUpdater updater){
	
		if (!ids.getLastFiles().contains(newFile)) {
			if (ids.getLastFiles().size() > 5) {
				ids.getLastFiles().remove(0);
			}
		}
		else {
			ids.getLastFiles().remove(newFile);
		}

		ids.getLastFiles().add(newFile);
		//resources.getFile().removeAll();
		Component[] me = resources.getFile().getMenuComponents();
		for (int k = 0; k < me.length; k++) {
			if (me[k] instanceof VisitedFileMenuItem) {
				resources.getFile().remove(me[k]);
			}
		}		

		for (int k = 0; k < ids.getLastFiles().size(); k++) {
			final File current = (File)ids.getLastFiles().elementAt(k);
			if (current!=null){
				VisitedFileMenuItem vfmi = new VisitedFileMenuItem(current.getPath()
						, current);
				vfmi.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) {	
						

						new LoadFileSwingTask(current,updater,ids,resources).execute();
					}
				});

				resources.getFile().add(vfmi);
			}
		}
		
	}
}
