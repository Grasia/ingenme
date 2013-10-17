
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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.tree.DefaultTreeModel;

import ingenias.editor.DiagramPaneInitialization;
import ingenias.editor.GUIResources;
import ingenias.editor.IDEAbs;
import ingenias.editor.IDEState;
import ingenias.editor.IDEUpdater;
import ingenias.editor.Log;
import ingenias.editor.persistence.PersistenceManager;
import ingenias.editor.utils.DialogWindows;

public class ImportFileAction {
	private IDEState ids;
	private GUIResources resources;
	private DiagramPaneInitialization em;
	
	public ImportFileAction(IDEState ids, GUIResources resources, DiagramPaneInitialization em){
		this.ids=ids;
		this.resources=resources;
		this.em=em;
	}
	public void importFileActionPerformed(ActionEvent evt, final IDEUpdater updater) {
		int result = JOptionPane.OK_OPTION;
		if (ids.getCurrentFile() != null && ids.isChanged()) {
			result = JOptionPane.showConfirmDialog(resources.getMainFrame(),
					"You will merge the imported file with current one. Do you want to continue (y/n)?",
					"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		}

		if (result == JOptionPane.OK_OPTION) {
			try {
				JFileChooser jfc = null;
				if (ids.getCurrentFileFolder() == null) {
					jfc = new JFileChooser();
				}
				else {
					jfc = new JFileChooser(ids.getCurrentFileFolder().getPath());
				}
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
				jfc.showOpenDialog(resources.getMainFrame());
				final File input = jfc.getSelectedFile();
				if (input != null && !input.isDirectory()) {
					/*new Thread() {
						public void run() {*/
							importFile(input,updater);
							ids.otherChange(); 
						/*}
					}

					.start();*/
				}
			}
			catch (Exception e1) {
				e1.printStackTrace();

			}
			resources.getMainFrame().setEnabled(true);
		}

	}

	public void importFile(File file, final IDEUpdater updater) {
		final File input = file;
		final JWindow jw = DialogWindows.showMessageWindow("IMPORTING...",ids,resources);
		final Frame self=resources.getMainFrame();
		new Thread(){
			public void run(){
				while (!jw.isVisible()){
					Thread.currentThread().yield();
				}
				//				ingenias.editor.events.AUMLDiagramChangesManager.enabled = false;
				try {

					resources.getMainFrame().setEnabled(false);

					Properties oldprops = (Properties) ids.prop;

					PersistenceManager pm = new PersistenceManager();
					// Merges data from the file	      
					pm.mergeFile(input.getAbsolutePath(),ids,resources);	      

					( (DefaultTreeModel) resources.getArbolProyectos().getModel()).reload();
					( (DefaultTreeModel) resources.getArbolObjetos().getModel()).reload();

					Log.getInstance().logSYS("Project imported successfully");
					resources.getMainFrame().validate();

					ids.setChanged(false);
					resources.setUnChanged();

					// Stores last file name path so that next save is performed in the same
					// folder

					ids.setCurrentFile(input);
					ids.setCurrentFileFolder(ids.getCurrentFile().getParentFile());
					resources.getMainFrame().setTitle("Project:" + input.getAbsolutePath());
					HistoryManager.updateProperties(oldprops,ids);
					HistoryManager.updateHistory(ids.getCurrentFile(), resources,ids, updater);
					jw.setVisible(false);

				}
				catch (ingenias.exception.UnknowFormat e1) {
					Log.getInstance().logSYS(e1.getMessage());
					JOptionPane.showMessageDialog(self,
					"Failure loading: format unknown. See MESSAGES pane");
					jw.hide();

				}
				catch (ingenias.exception.DamagedFormat df) {
					Log.getInstance().logSYS(df.getMessage());
					JOptionPane.showMessageDialog(self,
					"Failure loading: some diagrams could not be loaded. See MESSAGES pane");
					jw.hide();

				}
				catch (ingenias.exception.CannotLoad cl) {
					Log.getInstance().logSYS(cl.getMessage());
					JOptionPane.showMessageDialog(self,
					"Failure loading: could not load anything. See MESSAGES pane");
					jw.hide();

				}
				//				ingenias.editor.events.AUMLDiagramChangesManager.enabled = true;

				ids.setChanged(true);
				resources.getMainFrame().setEnabled(true);
				resources.setChanged();
			}}.start();

	}
}
