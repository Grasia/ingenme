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
package ingenias.editor.extension;

import ingenias.editor.AudioPlayer;
import ingenias.editor.GUIResources;
import ingenias.editor.IDEState;
import ingenias.editor.IDEUpdater;
import ingenias.editor.Log;
import ingenias.editor.actions.HistoryManager;
import ingenias.editor.utils.DialogWindows;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JWindow;

public class ManageExtensions {

	private IDEState ids;
	private GUIResources resources;
	private IDEUpdater updater;

	public ManageExtensions(IDEState ids, GUIResources resources, IDEUpdater updater){
		this.ids=ids;
		this.resources=resources;
		this.updater=updater;
	}

	public void addToolEntry(ingenias.editor.extension.BasicTool bt) {		
		Log.getInstance().logSYS("Added new module with name \"" + bt.getName() +
		"\"");
		
		((BasicToolImp)bt).setIds(ids);// enables access to the editor
		((BasicToolImp)bt).setResources(resources);// enables access to the editor
		((BasicToolImp)bt).setIdeUpdater(updater);// enables access to the editor
		
		JMenuItem nentry = new JMenuItem(bt.getName());
		nentry.setToolTipText(bt.getDescription());
		JMenuItem version = new JMenuItem("version");
		resources.getTools().add(version);
		resources.getTools().add(nentry);
		HistoryManager.updateProperties(bt.getProperties(),ids);
		final BasicTool bt1 = bt;
		final Frame jf = resources.getMainFrame();
		nentry.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				new RunToolSwingTask(bt1,ids,resources).execute();
				System.gc();
			}
		});
		version.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				JOptionPane.showMessageDialog(jf, bt1.getVersion(),"Version",JOptionPane.INFORMATION_MESSAGE);
			}
		});

	}
	public void removeEntry(ingenias.editor.extension.BasicTool bt) {
		int k = 0;
		boolean found = false;
		JMenu entry = null;
		while (!found && k < resources.getCodeGenerator().getItemCount()) {
			entry = (JMenu) resources.getCodeGenerator().getItem(k);
			found = entry.getText().equals(bt.getName());
			if (!found) {
				k = k + 1;
			}
		}
		;
		if (found) {
			resources.getCodeGenerator().remove(k);
		}
		found = false;
		JMenuItem tentry = null;
		k = 0;
		while (!found && k < resources.getTools().getItemCount()) {
			tentry = (JMenuItem) resources.getTools().getItem(k);
			found = tentry.getText().equals(bt.getName());
			if (!found) {
				k = k + 1;
			}
		}
		;
		if (found) {
			resources.getTools().remove(k);
		}
	}

	public void addCGEntry(ingenias.editor.extension.BasicCodeGenerator bcg) {
		Log.getInstance().logSYS("Added new module with name \"" + bcg.getName() +
		"\"");
		JMenu nentry = new JMenu(bcg.getName());
		JMenuItem generate = new JMenuItem("generate");
		JMenuItem version = new JMenuItem("version");
		nentry.add(version);
		nentry.add(generate);
		
		nentry.setToolTipText(bcg.getDescription());
		resources.getCodeGenerator().add(nentry);
		
		((BasicToolImp)bcg).setIds(ids); // enables access to the editor
		((BasicToolImp)bcg).setResources(resources);// enables access to the editor
		((BasicToolImp)bcg).setIdeUpdater(updater);// enables access to the editor

		HistoryManager.updateProperties(bcg.getProperties(),ids);
		final BasicCodeGenerator bcg1 = bcg;
		final Frame jf = resources.getMainFrame();

		generate.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				new RunCodeGeneratorSwingTask(bcg1,ids,resources).execute();
				System.gc();
			}
		});
		
		version.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				JOptionPane.showMessageDialog(jf, bcg1.getVersion(),"Version",JOptionPane.INFORMATION_MESSAGE);
			}
		});


	}

	public void stopManager() {
		// TODO Auto-generated method stub

	}
}
