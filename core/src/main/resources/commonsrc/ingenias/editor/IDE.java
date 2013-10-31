

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

import ingenias.editor.actions.LoadFileSwingTask;
import ingenias.editor.persistence.TextAreaOutputStream;
import ingenias.exception.CannotLoad;
import ingenias.exception.DamagedFormat;
import ingenias.exception.UnknowFormat;
import ingenias.generator.browser.Browser;
import ingenias.generator.browser.BrowserImp;

import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

class CheckChangesInFile extends Thread implements  WindowListener {
	boolean stop=false;
	File watchedFile=null;
	private long watchedFileLastModified;
	IDE ide;
	private boolean closing=false;

	CheckChangesInFile(IDE ide){		
		super("Spec File Monitoring");
		this.ide=ide;
	}

	public static int showConfirmDialog(Component parentComponent,
			Object message, String title,
			int optionType)
	{
		JOptionPane pane = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE, optionType);
		JDialog dialog = pane.createDialog(parentComponent, title);
		dialog.setLocationByPlatform(true);
		dialog.setVisible(true);
		if (pane.getValue() instanceof Integer)
			return ((Integer) pane.getValue()).intValue();
		return -1;
	}

	public void run(){
		boolean isCurrentFileContentDifferentFromIDEContent = true;
		while (!stop && !closing){

			try {
				Thread.currentThread().sleep(500);
				if (watchedFile!=null &&
						ide.getIds().getCurrentFile()!=null 
						&& ide.getIds().getCurrentFile().getCanonicalPath().equals(watchedFile.getCanonicalPath())){
					long currentFileLastModified = new File(watchedFile.getCanonicalPath()).lastModified();
					if (currentFileLastModified>watchedFileLastModified){
						isCurrentFileContentDifferentFromIDEContent=!getDifferences().isEmpty();
						watchedFileLastModified=currentFileLastModified;												
						if (isCurrentFileContentDifferentFromIDEContent && !closing){
							if (!ide.getIds().isChanged()){										
								int result = showConfirmDialog(ide, 
										"The specification has changed. \n Press OK to " +
												"load the new one from disk. " +
												" If you cancel, no further action will be taken.","Specification file changed in disk",
												JOptionPane.OK_CANCEL_OPTION);
								if (result==JOptionPane.OK_OPTION){
									watchedFileLastModified=currentFileLastModified;
									new LoadFileSwingTask(watchedFile,ide,ide.getIds(),ide.getResources()).execute();
								} else {
									// update data to match the new file
									watchedFile=ide.getIds().getCurrentFile();
									watchedFileLastModified=this.watchedFile.lastModified();
								}
							} else {
								int result = showConfirmDialog(ide, "The specification has changed in disk." +
										" Opened one is modified and changes can be lost. Do you want continue?",
										"Specification file changed in disk", 
										JOptionPane.OK_CANCEL_OPTION);

								watchedFileLastModified=currentFileLastModified;
								if (result==JOptionPane.OK_OPTION)
									new LoadFileSwingTask(watchedFile,ide,ide.getIds(),ide.getResources()).execute();
								else {
									// update data to match the new file
									watchedFile=ide.getIds().getCurrentFile();
									watchedFileLastModified=this.watchedFile.lastModified();
								}
							}
						} else {
							watchedFileLastModified=this.watchedFile.lastModified();
						}
					} 
				} else {
					if (watchedFile==null && ide.getIds().getCurrentFile()!=null ){
						// nothing is being watched, but a file is opened
						watchedFile=ide.getIds().getCurrentFile();
						watchedFileLastModified=this.watchedFile.lastModified();
					} else {
						if (watchedFile!=null &&
								ide.getIds().getCurrentFile()!=null 
								&& !ide.getIds().getCurrentFile().getCanonicalPath().equals(watchedFile.getCanonicalPath())){
							// files have changed and it is required to watch a new one
							watchedFile=ide.getIds().getCurrentFile();
							watchedFileLastModified=this.watchedFile.lastModified();
						}
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
	}
	
	private Vector<String> getDifferences() {		
		try {
			Browser bimp=BrowserImp.initialise(watchedFile.getCanonicalPath());
			ModelJGraph.enableAllListeners(); // a browser initialised through the initialise method is assumed to be
			//linked to no gui. Therefore, the persistence does load the spec without any listeners
			// and this prevents some runtime exception due to the event managers acting over elements in the specification
			// which were not layered out. This causes the listeners to be disabled by default, which is handled by
			// a static var. So, loading a new spec in headless mode causes a gui to ignore events.
			//return !BrowserImp.compare(bimp, new BrowserImp(ide.getIds()));
			Vector<String> diffs2 = BrowserImp.findAllDifferences(bimp, new BrowserImp(ide.getIds()));
			Vector<String> diffs1=BrowserImp.findAllDifferences(new BrowserImp(ide.getIds()),bimp);
			diffs1.addAll(diffs2);
			if (diffs1.size()>0)
				System.out.println("aqui");
			return diffs1;
		} catch (UnknowFormat e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DamagedFormat e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotLoad e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Vector<String>();


	}

	private boolean differentContent() {		
		try {
			Browser bimp=BrowserImp.initialise(watchedFile.getCanonicalPath());
			ModelJGraph.enableAllListeners(); // a browser initialised through the initialise method is assumed to be
			//linked to no gui. Therefore, the persistence does load the spec without any listeners
			// and this prevents some runtime exception due to the event managers acting over elements in the specification
			// which were not layered out. This causes the listeners to be disabled by default, which is handled by
			// a static var. So, loading a new spec in headless mode causes a gui to ignore events.
			//return !BrowserImp.compare(bimp, new BrowserImp(ide.getIds()));
			Vector<String> diffs2 = BrowserImp.findAllDifferences(bimp, new BrowserImp(ide.getIds()));
			Vector<String> diffs1=BrowserImp.findAllDifferences(new BrowserImp(ide.getIds()),bimp);
			return !diffs2.isEmpty() || !diffs1.isEmpty();
		} catch (UnknowFormat e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DamagedFormat e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotLoad e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;


	}

	public void stopUpdate(){
		stop=true;
	}


	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		closing=true;
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

}

public class IDE extends ingenias.editor.IDEAbs {


	/**
	 *  Constructor for the IDE object
	 */
	public IDE() {
		super();
		try {
			if (getClass().getResourceAsStream("/editor.properties")!=null)
				System.getProperties().load(getClass().getResourceAsStream("/editor.properties"));
		} catch (IOException e) {			

		}

	}
	public static void setUIFont (javax.swing.plaf.FontUIResource f){
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get (key);
			if (value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put (key, f);
		}
	}    

	public IDE launchIDE(String args[]){
		IDEState ids=IDEState.emptyIDEState();
		try {
			ingenias.generator.browser.BrowserImp.initialise(ids);
		}	
		catch (Exception e) {
			e.printStackTrace();
		}	;	
		GUIResources resources=null;
		IDE ide=this;
		resources=ide.getResources();
		Log.initInstance(new PrintWriter(new TextAreaOutputStream(resources.getModuleOutput())),
				new PrintWriter(new TextAreaOutputStream(resources.getLogs())));
		ide.updateIDEState(ids);


		//ide.initialiseActionHandlers();
		ide.validate();
		ide.setLocationByPlatform(true);
		ide.pack();
		ide.setVisible(true);
		ide.setTitle("IDK-INGENME");

		CheckChangesInFile ccif=new CheckChangesInFile(ide);
		this.addWindowListener(ccif);
		ccif.start();

		if (args.length != 0 && !args[0].equalsIgnoreCase("testing")) {		
			new LoadFileSwingTask(new File(args[0]),ide,ide.getIds(),resources).execute();	

		}

		return ide;
	}





	public static void main(String args[]) throws Exception {
		System.out.println("INGENIAS Development Kit (C) 2012 Jorge Gomez");
		System.out.println("This program comes with ABSOLUTELY NO WARRANTY; for details check www.gnu.org/copyleft/gpl.html.");
		System.out.println("This is free software, and you are welcome to redistribute it under certain conditions;; for details check www.gnu.org/copyleft/gpl.html.");
		setUIFont(new javax.swing.plaf.FontUIResource(FontConfiguration.getConfiguration().getGUIFont()));   
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		}catch(Exception ex){
		}

		if (args.length != 0) {			
			if (args[0].equalsIgnoreCase("testing")){
				StaticPreferences.setTesting(true);
			}
		}

		new IDE().launchIDE(args);

	}

	public void removeExitAction() {
		WindowListener[] wls = this.getWindowListeners();
		for (WindowListener wl:wls){
			if (!(wl instanceof CheckChangesInFile))
				this.removeWindowListener(wl);
		}
		this.exit.removeActionListener(this.exit.getActionListeners()[0]);
		
	}
}



