
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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import ingenias.editor.DiagramPaneInitialization;
import ingenias.editor.GUIResources;
import ingenias.editor.IDEState;
import ingenias.editor.IDEUpdater;
import ingenias.editor.Log;
import ingenias.editor.VisitedFileMenuItem;
import ingenias.editor.persistence.PersistenceManager;
import ingenias.editor.utils.DialogWindows;

public class SaveAction {
	private IDEState ids;
	private GUIResources resources;
	private DiagramPaneInitialization em;
	
	public SaveAction(IDEState ids, GUIResources resources, DiagramPaneInitialization em){
		this.ids=ids;
		this.resources=resources;
		this.em=em;
	}
	
	public void save_actionPerformed(IDEUpdater updater) {
		if (ids.getCurrentFile() != null) {
			// int result = JOptionPane.showConfirmDialog(resources.getMainFrame(),
			// 		"Are you sure you want to overwrite (y/n)?",
			// 		"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			// if (result == JOptionPane.OK_OPTION) {
				try {
					System.err.println(ids.getCurrentFile().getName());
					ingenias.editor.persistence.PersistenceManager pm = new ingenias.
					editor.persistence.PersistenceManager();
					pm.save(ids.getCurrentFile(), ids);
					HistoryManager.updateHistory(ids.getCurrentFile(), resources,ids, updater);

					Log.getInstance().log("Project saved successfully!!");
					ids.setChanged(false);
					resources.setUnChanged();
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
			//}
		}
		else {
			this.saveas_action(updater);
			HistoryManager.updateHistory(ids.getCurrentFile(), resources, ids,updater); 
		}

	}
	
	
	public void saveas_action(IDEUpdater updater) {
		try {
			JFileChooser jfc = null;
			if (ids.getCurrentFileFolder() == null) {
				jfc = new JFileChooser();
			}
			else {

				jfc = new JFileChooser(ids.getCurrentFileFolder());
				jfc.addChoosableFileFilter(
						new javax.swing.filechooser.FileFilter() {
							public boolean accept(File f) {
								boolean acceptedFormat = f.getName().toLowerCase().endsWith(
								".xml");
								return acceptedFormat || f.isDirectory();
							}

							public String getDescription() {
								return "xml";
							}
						});

			}
			boolean invalidFolder = true;
			File sel = null;
			while (invalidFolder) {
				jfc.setLocation(DialogWindows.getCenter(resources.getMainFrame().getSize(),resources.getMainFrame()));
				jfc.showSaveDialog(resources.getMainFrame());
				sel = jfc.getSelectedFile();

				invalidFolder = sel != null && !sel.getParentFile().exists();
				if (invalidFolder) {
					JOptionPane.showMessageDialog(resources.getMainFrame(),
							"You cannot save your file to " +
							sel.getParentFile().getPath() +
							". That folder does not exist. Please, try again", "Error",
							JOptionPane.WARNING_MESSAGE);
				}
			}
			if (sel != null && !sel.isDirectory()) {
				if (sel.exists()) {
					int result = JOptionPane.showConfirmDialog(resources.getMainFrame(),
							"The file already exists. Do you want to overwrite (y/n)?",
							"Warning", JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);
					if (result == JOptionPane.OK_OPTION) {
						PersistenceManager p = new PersistenceManager();
						p.save(sel, ids);						
						ids.setCurrentFile(sel);
						ids.setCurrentFileFolder( sel.getParentFile());
						HistoryManager.updateHistory(ids.getCurrentFile(),resources,ids,updater);
						resources.getMainFrame().setTitle("Project:" + sel.getAbsolutePath());
						ids.setChanged(false);
					}
				}
				else {
					PersistenceManager p = new PersistenceManager();
					if (!sel.getPath().toLowerCase().endsWith(".xml")) {
						sel = new File(sel.getPath() + ".xml");
					}
					p.save(sel, ids);
					ids.setCurrentFile( sel);
					ids.setCurrentFileFolder(sel.getParentFile());
					resources.getMainFrame().setTitle("Project:" + sel.getAbsolutePath());
					HistoryManager.updateHistory(ids.getCurrentFile(),resources,ids,updater);
					ids.setChanged(false);

				}

			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	
	
}
