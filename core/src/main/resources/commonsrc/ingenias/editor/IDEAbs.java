


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

import ingenias.editor.actions.CaptureAction;
import ingenias.editor.actions.CopyImageToClipboardAction;
import ingenias.editor.actions.CopyObjectToClipboardAction;
import ingenias.editor.actions.DoSpringLayoutAction;
import ingenias.editor.actions.ExitAction;
import ingenias.editor.actions.ForceGCAction;
import ingenias.editor.actions.HistoryManager;
import ingenias.editor.actions.HyperlinkAction;
import ingenias.editor.actions.ImportFileAction;
import ingenias.editor.actions.LoadFileAction;
import ingenias.editor.actions.LoadFileSwingTask;
import ingenias.editor.actions.NewProjectAction;
import ingenias.editor.actions.OpenManualAction;
import ingenias.editor.actions.PasteObjectFromClipboardAction;
import ingenias.editor.actions.ResizeAllDiagramsAction;
import ingenias.editor.actions.ResizeCurrentDiagramAction;
import ingenias.editor.actions.SaveAction;
import ingenias.editor.actions.SearchAction;
import ingenias.editor.actions.ShowPropertiesWindowAction;
import ingenias.editor.actions.SwitchViewsAction;
import ingenias.editor.actions.UndoRedoAction;
import ingenias.editor.extension.ManageExtensions;
import ingenias.editor.extension.ModuleLoader;
import ingenias.editor.extension.UpdateToolsAndCG;
import ingenias.editor.filters.DiagramFilter;
import ingenias.editor.filters.FilterManager;
import ingenias.editor.persistence.AutomaticBackupAction;
import ingenias.editor.persistence.PersistenceManager;
import ingenias.editor.utils.DialogWindows;
import ingenias.exception.CannotLoad;
import ingenias.exception.UnknowFormat;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jgraph.JGraph;

/**
 * @opt inferrel
 * @opt collpackages java.util.*
 * @opt inferdep
 * @opt hide java.*
 * @view
 * @opt inferrel
 * @opt inferdep
 * @opt useimports
 * @hidden
 */
class UMLOptions {}


/**
 * @opt shape class
 * @opt all
 * @note The main frame of the editor is an instance of IDEAbs.
 * It bases on the definition of an IDEState instance. Hence, it
 * can be resconstructed from any new instance.
 **/

public class IDEAbs
extends ingenias.editor.IDEGUI
implements java.io.Serializable,IDEUpdater
{
	private IDEState ids;


	DiagramPaneInitialization em=null;

	/**
	 *  Description of the Field
	 */

	private GUIResources resources=new GUIResources();

	private AutomaticBackupAction abackup;

	private SearchAction sa;

	private UpdateToolsAndCG update;

	private HyperlinkAction diagramLocator=null;

	public IDEState getIds() {
		return ids;
	}



	public GUIResources getResources() {
		return resources;
	}


	/**
	 *  Constructor for the IDE object
	 */
	public IDEAbs() {
		super();	
		ids=IDEState.emptyIDEState();
		initialiseGUIResources();
		setLocationByPlatform(true);
	}

	public void updateIDEState(IDEState nids){

		if (update!=null&&abackup!=null){
			update.stopUpdate();
			abackup.stop();
		}
		if (diagramLocator!=null){
			searchDiagramPanel.removeHyperlinkListener(diagramLocator);
			logs.removeHyperlinkListener(diagramLocator);
		}

		resources.getTools().removeAll();
		resources.getCodeGenerator().removeAll();
		resources.getPprin().remove(ids.editor);
		if (resources.getCommonButtons()!=null)
			resources.getPprin().remove(resources.getCommonButtons());
		if (resources.getButtonModelPanel()!=null)
			resources.getPprin().remove(resources.getButtonModelPanel());

		resources.setProgressBar(pbar);
		DiagramFilter oldDiagramFilter=ids.getDiagramFilter();
		this.ids=nids;		
		replaceCurrentTrees(ids.gm.arbolProyecto, ids.om.arbolObjetos);		

		em=new DiagramPaneInitialization(ids,resources);

		initialiseGUIResources();
		ids.setChanged(false);
		resources.setUnChanged();		
		//this.setExtendedState(JFrame.MAXIMIZED_BOTH);	
		em.createEditorAndRelatedComponents(ids);		
		sa= new SearchAction(ids, resources);

		try {
			restorePreferences();
		} catch (UnknowFormat e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotLoad e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initialiseActionHandlers();

		Vector<DiagramFilter> profileConfigurations = FilterManager.listAvailableConfigurations();
		this.profiles.removeAll();		
		DiagramFilter defaultFilter=null;		
		JCheckBoxMenuItem defaultMenuEntry=null;		
		ButtonGroup profileSelector=new ButtonGroup();
		for (DiagramFilter df:profileConfigurations){
			final JCheckBoxMenuItem menuEntry=new JCheckBoxMenuItem();
			profileSelector.add(menuEntry);
			menuEntry.setText(df.getName());
			if (oldDiagramFilter!=null && df.equals(oldDiagramFilter)){
				defaultMenuEntry=menuEntry;
				defaultFilter=oldDiagramFilter;
			}
			if (df.getName().equalsIgnoreCase("Full INGENIAS") && defaultMenuEntry==null){
				defaultFilter=df;
				defaultMenuEntry=menuEntry;			
			}
			final DiagramFilter cdf=df;			
			menuEntry.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					ids.setDiagramFilter(cdf);		
					em.updateButtonBars();							  
					menuEntry.setSelected(true);				  
				}
			});
			this.profiles.add(menuEntry);							
		}		
		ids.setDiagramFilter(defaultFilter);
		
		defaultMenuEntry.setSelected(true);

		resources.getPprin().invalidate();
		resources.getPprin().repaint();
		this.getContentPane().invalidate();
		this.getContentPane().repaint();
		resources.getPprin().validate();
		this.validate();

	}

	/**
	 * @throws UnknowFormat
	 * @throws CannotLoad
	 */
	public void restorePreferences() throws UnknowFormat, CannotLoad {
		new PersistenceManager().restorePreferences(ids,resources, this);
		if (ids.getCurrentFile() != null) {
			ids.setCurrentFileFolder(ids.getCurrentFile().getParentFile());
		}
		if (ids.prefs.getEditPropertiesMode().equals(Preferences.EditPropertiesMode.PANEL)){
			resources.getEditOnMessages().setSelected(true);
		}
		if (ids.prefs.getEditPropertiesMode().equals(Preferences.EditPropertiesMode.POPUP)){
			resources.getEditPopUpProperties().setSelected(true);
		}
		if (resources.getCommonButtons().getJc()!=null){
			if (ids.prefs.getRelationshiplayout().equals(Preferences.RelationshipLayout.MANUAL)){
				resources.getCommonButtons().getJc().setSelectedIndex(1);

			}
			if (ids.prefs.getRelationshiplayout().equals(Preferences.RelationshipLayout.AUTOMATIC_STRAIGHT)){
				resources.getCommonButtons().getJc().setSelectedIndex(0);
			}
		}

		if (ids.prefs.getModelingLanguage().equals(Preferences.ModelingLanguage.INGENIAS)){
			resources.getEnableINGENIASView().setSelected(true);
			new SwitchViewsAction(ids,resources).enableINGENIASView_actionPerformed(null);			

		}

		if (ids.prefs.getModelingLanguage().equals(Preferences.ModelingLanguage.UML)){			
			new SwitchViewsAction(ids,resources).enableUMLView_actionPerformed(null);
			resources.getEnableUMLView().setSelected(true);
		}


		if (ids.prefs.getRelationshipsLookAndFeel().equals(Preferences.RelationshipsLookAndFeel.FULL)){


		}
	}



	public void initialiseActionHandlers() {
		try {

			ids.addStateChangelistener(new IDEChangesHandler(ids,resources));
			ManageExtensions me=new ManageExtensions(ids,resources,this);

			ModuleLoader.cleanExtensionFolder();
			ModuleLoader ml=null;
			try {
				ml = new ModuleLoader(new URL("file:ext/"));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			diagramLocator = new HyperlinkAction(ids,resources);		
			searchDiagramPanel.addHyperlinkListener(diagramLocator);    
			logs.addHyperlinkListener(diagramLocator);

			this.abackup=new AutomaticBackupAction(ids,resources,5);


			me=new ManageExtensions(ids,resources,this);
			update = new UpdateToolsAndCG(me,ml,ids);
			update.readLibs("ext");
			update.start();
		}catch (Throwable t){
			t.printStackTrace();
		}

	}

	public void stop(){
		if (update!=null)
			update.stopUpdate();
	}


	protected void finalize() throws Throwable{
		if (update!=null)
			update.stopUpdate();
		super.finalize();

	}



	public void initialiseGUIResources() {		
		resources.setMessagespane(this.messagespane);
		resources.setSearchDiagramPanel(this.searchDiagramPanel);
		resources.setFile(file);
		resources.setMainFrame(this);
		resources.setSave(save);
		resources.setSaveAs(saveas);
		resources.setTools(menuTools);
		resources.setSearchField(searchField);
		resources.setLogs(logs);
		resources.setModuleOutput(moduleOutput);
		resources.setCodeGenerator(menuCodeGenerator);
		resources.setArbolProyectos(arbolProyectos);
		resources.getArbolProyectos().setContainer(scrollPaneForProyectView);
		resources.setArbolObjetos(arbolObjetos);
		resources.getArbolObjetos().setContainer(scrollPaneForEntitiesView);
		resources.setEditPopUpProperties(editPopUpProperties);
		resources.setButtonModelPanel(buttonModelPanel);
		resources.setPprin(pprin);
		resources.setEnableUMLView(enableUMLView);
		resources.setEnableINGENIASView(enableINGENIASView);
		resources.setEditOnMessages(editOnMessages);		
	}



	//protected void updateHistoryButtons() {
	// The View Argument Defines the Context
	//	ids.btb.getUndo().setEnabled(undoManager.canUndo(getGraph().getGraphLayoutCache()));
	//	ids.btb.getRedo().setEnabled(undoManager.canRedo(getGraph().getGraphLayoutCache()));
	//	}



	/*	private void updateProjectsMenu(JMenu project) {
		// TODO Auto-generated method stub

	}*/	

	protected void addToolEntry(ingenias.editor.extension.BasicTool bt) {
		new ManageExtensions(ids,resources,this).addToolEntry(bt);		
	}
	protected void removeEntry(ingenias.editor.extension.BasicTool bt) {
		new ManageExtensions(ids,resources,this).removeEntry(bt);

	}

	protected void addCGEntry(ingenias.editor.extension.BasicCodeGenerator bcg) {
		new ManageExtensions(ids,resources,this).addCGEntry(bcg);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  e  Description of Parameter
	 */
	void arbolProyectos_mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			JPopupMenu jp = this.menuProjectTree(e);
			TreePath tp = resources.getArbolProyectos().getSelectionPath();
			if (tp != null) {
				e.translatePoint(0,0);
				jp.show(resources.getArbolProyectos(), e.getPoint().x, e.getPoint().y);
				this.repaint();
			}
		}
		else
			if (e.getClickCount() > 1) {
				TreePath tp =resources.getArbolProyectos().getSelectionPath();
				if (tp!=null){
					DefaultMutableTreeNode
					dmtn = (DefaultMutableTreeNode) tp.getLastPathComponent();
					Object uo = dmtn.getUserObject();

					if (tp != null && tp.getPathCount() > 1 && uo instanceof ModelJGraph) {

						ModelJGraph m = (ModelJGraph) uo;
						if (m != null) {
							em.ChangeCurrentDiagram(m);

						}
					}
				}
			}
	}

	protected JPopupMenu menuProjectTree(MouseEvent e){
		return new ProjectMenuCreator(ids,resources.getMainFrame(),resources).menuProjectTree(e);
	}

	void arbolObjetos_mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			JPopupMenu jp = new ObjectTreeMenuEntries(ids,resources).menuObjectTree(e);
			TreePath tp = ids.om.arbolObjetos.getSelectionPath();
			if (tp != null) {
				jp.show(arbolObjetos, e.getPoint().x, e.getPoint().y);
				this.repaint();
			}
		}
		else
			if (e.getClickCount() > 1) {
				TreePath tp = ids.om.arbolObjetos.getSelectionPath();
				if (tp != null && tp.getPathCount() > 1) {
					JGraph jg = ids.gm.getCurrent();
					javax.swing.tree.DefaultMutableTreeNode dmtn =

							(javax.swing.tree.DefaultMutableTreeNode) tp.getLastPathComponent();
					// jg.setSelectionCell(dmtnetUserObject());
					this.getContentPane().validate();

				}
			}
	}


	void properties_actionPerformed(ActionEvent e) {
		new ShowPropertiesWindowAction(ids,resources).properties_actionPerformed(e);

	}

	void save_actionPerformed(ActionEvent e) {
		new SaveAction(ids,resources,em).save_actionPerformed(this);

	}


	void load_actionPerformed(ActionEvent e) {

		new LoadFileAction(ids,resources).loadNewFile(this);
		//updateIDEState(nids);
		//em.updateButtonBars();
	}



	void capture_actionPerformed(ActionEvent e) {
		new CaptureAction(ids,resources).capture_action();
	}

	void saveas_actionPerformed(ActionEvent e) {
		new SaveAction(ids,resources,em).saveas_action(this);

	}

	void this_windowClosed(WindowEvent e) {
		this.exit_actionPerformed(null);
	}

	void this_windowClosing(WindowEvent e) {
		this.exit_actionPerformed(null);
	}

	void exit_actionPerformed(ActionEvent e) {
		new ExitAction(ids,resources).exit_actionPerformed();
	}




	void undo_actionPerformed(ActionEvent e) {
		new  UndoRedoAction(ids,resources). undo_actionPerformed(e);
	}

	void redo_actionPerformed(ActionEvent e) {
		new  UndoRedoAction(ids,resources). redo_actionPerformed(e);
	}

	void delete_actionPerformed(ActionEvent e) {
		ActionEvent e1 = new ActionEvent(this.ids.editor.getGraph(), e.getID(),
				e.getActionCommand(), e.getModifiers());
		if (ids.editor.getGraph()!=null){
			resources.getCommonButtons().getRemove().actionPerformed(e1);
			ids.otherChange();
		}
	}

	void selectall_actionPerformed(ActionEvent e) {
		if (ids.editor.getGraph()!=null)
			ids.editor.getGraph().setSelectionCells( ids.editor.getGraph().getRoots());

	}

	void copy_actionPerformed(ActionEvent e) {
		new CopyObjectToClipboardAction(ids,resources).copy_actionPerformed(e);
	}

	void paste_actionPerformed(ActionEvent e) {
		new PasteObjectFromClipboardAction(ids,resources).paste_actionPerformed(e);
	}

	void about_actionPerformed(ActionEvent e) {
		About a = new About();
		a.pack();    
		a.setLocation(DialogWindows.getCenter(a.getSize(),resources.getMainFrame()));
		a.show();
	}


	void manual_actionPerformed(ActionEvent e) {
		new OpenManualAction(ids,resources).manual_actionPerformed(e);
	}

	void clearMessages_actionPerformed(ActionEvent e, JTextPane pane) {
		pane.setText("");
	}


	void logs_mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 || e.getButton() == e.BUTTON3) {
			this.messagesMenu.show(this.logs, e.getX(), e.getY());
		}
	}

	void moduleOutput_mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 || e.getButton() == e.BUTTON3) {
			this.messagesMenu.show(this.moduleOutput, e.getX(), e.getY());
		}

	}

	void newProject_actionPerformed(ActionEvent e) {
		new NewProjectAction(ids,resources).newProject_actionPerformed(this);

	}


	/**
	 *  Description of the Method
	 *
	 * <param  e  Description of the Parameter
	 */
	void cpClipboard_actionPerformed(ActionEvent e) {
		new CopyImageToClipboardAction(ids,resources).cpClipboard_actionPerformed();

	}

	/**
	 *  Description of the Method
	 *
	 * >param  e  Description of the Parameter
	 */
	void forcegc_actionPerformed(ActionEvent e) {
		new ForceGCAction(ids,resources).forcegc_actionPerformed(e);

	}


	void enableUMLView_actionPerformed(ActionEvent e) {
		new SwitchViewsAction(ids, resources).enableUMLView_actionPerformed(e);
	}

	void enableINGENIASView_actionPerformed(ActionEvent e) {
		new SwitchViewsAction(ids, resources).enableINGENIASView_actionPerformed(e);

	}

	public void switchUMLView_actionPerformed(ActionEvent e) {
		new SwitchViewsAction(ids, resources).switchUMLView_actionPerformed(e);

	}
	public void switchINGENIASView_actionPerformed(ActionEvent e) {
		new SwitchViewsAction(ids, resources).switchINGENIASView_actionPerformed(e);

	}


	void resizeAllDiagrams_actionPerformed(ActionEvent e) {
		new ResizeAllDiagramsAction(ids,resources).resizeAllDiagrams_actionPerformed(e);
	}



	void resizeAll_actionPerformed(ActionEvent e) {
		new ResizeCurrentDiagramAction(ids.editor).resizeCurrentDiagram(e);

	}

	void importFileActionPerformed(ActionEvent evt) {
		new ImportFileAction(ids,resources,em).importFileActionPerformed(evt, this);
	}


	private void springLayout(){
		new DoSpringLayoutAction(ids,resources).springLayout();
	}

	void elimOverlapActionPerformed(ActionEvent evt) {
		new DoSpringLayoutAction(ids,resources).springLayout();
	}

	public void SearchActionPerformed(ActionEvent evt) {
		sa.searchActionPerformed(evt);
	}

	public void searchFieldKeyTyped(KeyEvent evt) {
		if (evt.getKeyChar()==evt.VK_ENTER){
			this.SearchActionPerformed(null);
		}
	}

	public void editPopUpProperties_selected() {
		ids.prefs.setEditPropertiesMode(Preferences.EditPropertiesMode.POPUP);
	};

	public void editOnMessages_selected() {
		ids.prefs.setEditPropertiesMode(Preferences.EditPropertiesMode.PANEL);
	};

	void changeWorkspace(ActionEvent e) {
		final JDialog workspaceChange=new JDialog(this,true);
		final JFileChooser chooser=new JFileChooser();
		if (!this.getIds().prefs.getWorkspacePath().equals("")){
			chooser.setCurrentDirectory(new File(ids.prefs.getWorkspacePath()));	
		}

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // To choose only directories

		final JTextField directory=new JTextField(30);
		directory.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode()==KeyEvent.VK_ENTER){
					performWorkspaceSwitch(directory,workspaceChange);

				}

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

		});
		directory.setText(ids.prefs.getWorkspacePath());
		JButton browse=new JButton("Browse");
		browse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				chooser.setDialogType(chooser.OPEN_DIALOG);
				chooser.showOpenDialog(null);
				if (chooser.getSelectedFile()==null)
					JOptionPane.showMessageDialog(workspaceChange, 
							"A directory has to be chosen",
							"No directory selected",JOptionPane.ERROR_MESSAGE);
				else
					directory.setText(chooser.getSelectedFile().toString());
			}

		});
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				workspaceChange.setVisible(false);	
			}
		});
		JButton accept=new JButton("Accept");
		accept.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				performWorkspaceSwitch(directory,workspaceChange);
			}
		});

		JPanel buttons=new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttons.add(accept);
		buttons.add(cancel);
		JPanel input=new JPanel(new FlowLayout(FlowLayout.LEFT));
		input.add(new JLabel("Current workspace:"));
		input.add(directory);
		input.add(browse);
		Box vbox=Box.createVerticalBox();
		vbox.add(input);
		vbox.add(buttons);
		workspaceChange.getContentPane().add(vbox);
		workspaceChange.pack();
		workspaceChange.setLocation(ingenias.editor.utils.DialogWindows.getCenter(workspaceChange.getSize(), resources.getMainFrame()));
		workspaceChange.setVisible(true);
	}



	protected void performWorkspaceSwitch(JTextField directory,
			JDialog workspaceChange) {
		File newWorkspace=new File(directory.getText());
		if (newWorkspace.exists() && newWorkspace.isDirectory()){
			ids.prefs.setWorkspacePath(directory.getText());	
			workspaceChange.setVisible(false);
		} else {
			JOptionPane.showMessageDialog(resources.getMainFrame(),
					"That folder does not exist. The workspace must be an existing folder",
					"Folder does not exist",JOptionPane.ERROR_MESSAGE);
		}

	}

}



