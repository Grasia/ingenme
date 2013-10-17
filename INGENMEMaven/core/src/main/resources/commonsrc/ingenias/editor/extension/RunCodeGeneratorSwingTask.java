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

import ingenias.editor.GUIResources;
import ingenias.editor.IDEState;
import ingenias.editor.IDEUpdater;
import ingenias.editor.Log;
import ingenias.editor.ProgressListener;
import ingenias.editor.actions.HistoryManager;
import ingenias.editor.actions.LoadFileAction;

import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;


public class RunCodeGeneratorSwingTask extends SwingWorker<Void, Void> implements ProgressListener{
	
	
	private IDEState ids;
	private GUIResources resources;
	private IDEState nids;
	private BasicCodeGenerator bcg;
	private int hclogs;
	private int hcout;
	public RunCodeGeneratorSwingTask(final BasicCodeGenerator bcg, IDEState ids, final GUIResources resources){				
		this.ids=ids;
		this.resources=resources;
		this.bcg=bcg;
		final SwingWorker sw=this;
		this.addPropertyChangeListener(new PropertyChangeListener(){
			/**
			 * Invoked when task's progress property changes.
			 */
			public void propertyChange(PropertyChangeEvent evt) {
				if (!sw.isDone()) {					
					int progress = sw.getProgress();
					resources.getProgressBar().setValue(progress);
					resources.getProgressBar().setString("Running "+bcg.getName()+" ..."+((progress))+"%");					
				} 
			}
		});
		this.resources.getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		resources.addProgressListener(this);
		resources.getProgressBar().setVisible(true);
		resources.getProgressBar().invalidate();
		resources.getProgressBar().validate();
		resources.getProgressBar().repaint();	
		resources.getProgressBar().setString("Running "+bcg.getName());
		resources.getProgressBar().setStringPainted(true);
		resources.getMainFrame().setEnabled(false);		
		
	}
	/*
	 * Main task. Executed in background thread.
	 */
	@Override
	public Void doInBackground() {
		 hclogs = resources.getLogs().getText().hashCode();
		 hcout = resources.getModuleOutput().getText().hashCode();
		bcg.setProperties(ids.prop);
		try {
			bcg.setProgressListener(this);
		bcg.run();		
	
	}
	catch (Throwable ex) {
		ex.printStackTrace();		
		Log.getInstance().logERROR(ex);
		StackTraceElement[] ste = ex.getStackTrace();
		for (int k = 0; k < ste.length; k++) {
			Log.getInstance().logERROR(ste[k].toString());
		}
	//	AudioPlayer.play("watershot.wav"); // An error sound
		
	}
		return null;
	}

	/*
	 * Executed in event dispatching thread
	 */
	@Override
	public void done() {		
		if (resources.getModuleOutput().getText().hashCode() != hcout) {
			resources.getMessagespane().setSelectedIndex(1);
		}

		if (resources.getLogs().getText().hashCode() != hclogs) {
			resources.getMessagespane().setSelectedIndex(0);
		}
		resources.getProgressBar().setVisible(false);
		resources.getProgressBar().invalidate();
		resources.getProgressBar().validate();
		resources.getProgressBar().repaint();
		resources.removeProgressListener(this);
		resources.getProgressBar().setValue(resources.getProgressBar().getMaximum());		
		this.resources.getMainFrame().setCursor(null);
		resources.getMainFrame().setEnabled(true);

		//setCursor(null); //turn off the wait cursor
	}
	public void setCurrentProgress(int progress) {
		setProgress(progress);

	}
}


