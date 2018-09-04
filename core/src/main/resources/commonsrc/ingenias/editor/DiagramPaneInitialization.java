
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

import ingenias.editor.actions.HistoryManager;
import ingenias.editor.actions.SwitchViewsAction;
import ingenias.editor.editionmode.EmbeddedCellEditor;
import ingenias.editor.editionmode.PopupCellEditor;
import ingenias.editor.entities.Entity;
import ingenias.editor.filters.DiagramFilter;
import ingenias.editor.filters.FilterManager;
import ingenias.editor.persistence.PersistenceManager;
import ingenias.editor.widget.DnDJTree;
import ingenias.exception.CannotLoad;
import ingenias.exception.UnknowFormat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.Vector;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.graph.DefaultGraphCell;

import com.languageExplorer.widgets.ScrollableBar;


public class DiagramPaneInitialization {
	private IDEState ids;
	private GUIResources resources;


	public DiagramPaneInitialization(IDEState ids, GUIResources resources){
		this.ids=ids;
		this.resources=resources;
	}

	/*public void initialiseResourcesFromInitialIDEState(){
		DnDJTree arbolProyectos=resources.getArbolProyectos();
		JTree arbolObjetos=resources.getArbolObjetos();
		DefaultMutableTreeNode rootProyectos=(DefaultMutableTreeNode) arbolProyectos.getModel().getRoot();
		DefaultMutableTreeNode rootObjetos=(DefaultMutableTreeNode) arbolObjetos.getModel().getRoot();
		this.replaceTree(rootObjetos,
				(DefaultMutableTreeNode) ids.om.arbolObjetos.getModel().getRoot());

		this.replaceTree(rootProyectos,
				(DefaultMutableTreeNode) resources.getArbolProyectos().getModel().getRoot());
		ids.setChanged(false);
		resources.setUnChanged();
	}*/

	public void createEditorAndRelatedComponents(final IDEState ids){
		ids.gm.getArbolProyecto().getModel().addTreeModelListener(new TreeModelListener(){
			public void treeNodesChanged(TreeModelEvent arg0) {
				ids.otherChange();

			}
			public void treeNodesInserted(TreeModelEvent arg0) {
				ids.otherChange();				
			}
			public void treeNodesRemoved(TreeModelEvent arg0) {
				ids.otherChange();				
			}
			public void treeStructureChanged(TreeModelEvent arg0) {
				ids.otherChange();				
			}			
		});
		ids.gm.getArbolProyecto().addTreeExpansionListener(new TreeExpansionListener(){

			public void treeCollapsed(TreeExpansionEvent arg0) {
				ids.otherChange();						
			}

			public void treeExpanded(TreeExpansionEvent arg0) {
				ids.otherChange();						
			}

		});

		resources.setCommonButtons(new ButtonToolBar(ids.editor,ids.gm,ids.om));
		//resources.getPprin().removeAll();
		resources.getButtonModelPanel().removeAll();
		resources.getPprin().add(ids.editor, java.awt.BorderLayout.CENTER);		
		//ids.editor = new Editor(ids.om,ids.gm, ids.prefs);		
		resources.getPprin().add(resources.getCommonButtons(), java.awt.BorderLayout.NORTH);
		ActionListener copyAction=new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				resources.getCommonButtons().copy.actionPerformed(e);
			}
		};
		ActionListener pasteAction=new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				resources.getCommonButtons().paste.actionPerformed(e);
			}
		};
		ActionListener removeAction=new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				resources.getCommonButtons().remove.actionPerformed(e);
			}
		};

		initialiseEditor(ids.editor, ids);
	}

	private void locateAndScrollToObject(String id) {	
		Vector foundpaths = this.ids.om.findUserObjectPathRegexp(id);
		if (foundpaths.size()>0){											
			TreePath tp=(TreePath)foundpaths.elementAt(0);
			ids.om.arbolObjetos.expandPath(tp);	  
			ids.om.arbolObjetos.scrollPathToVisible(tp);
			ids.om.arbolObjetos.setSelectionPath(tp);				
		}
	}



	private void initialiseEditor(final Editor editor, final IDEState ids){
		editor.setEnabled(false);

		
		addBasicListenersToEditor(editor, ids,resources);

		editor.addGraphModelListener(new GraphModelListener(){

			public void graphChanged(GraphModelEvent e) {
				ids.setChanged(true);
				resources.setChanged();
				Object[] insertedObjects=e.getChange().getInserted();
				if (insertedObjects!=null){
					for (int k=0;k<insertedObjects.length;k++){
						Object currentObject = insertedObjects[k];
						if (currentObject instanceof DefaultGraphCell){
							if (((DefaultGraphCell)currentObject).getUserObject() instanceof ingenias.editor.entities.Entity){
								ingenias.editor.entities.Entity entity=(Entity) ((DefaultGraphCell)currentObject).getUserObject();
								locateAndScrollToObject(entity.getId());
							}
						}
					}
				}
			}
		});





	}

	private void addBasicListenersToEditor(final Editor editor,
			final IDEState ids, final GUIResources resources) {
		
		
		editor.addTabSelectorChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				Runnable run=new Runnable(){
					public void run(){
						ModelJGraph graph = ids.editor.getGraph();
						
						if (graph!=null){
							
							
							editor.changeGraph(graph,ids);				
							resources.getCommonButtons().updateActions(graph);
							updateButtonBars();		
							
						}

					}
				};
			//	new Thread(run).start();
			SwingUtilities.invokeLater(run);

			}			
		});


		resources.getCommonButtons().getJc().setAction(new AbstractAction("") {
			private boolean enabled = true;
			public void actionPerformed(ActionEvent e) {
				if (resources.getCommonButtons().getJc().getSelectedIndex() == 0) {					
					ids.prefs.setRelationshiplayout(Preferences.RelationshipLayout.AUTOMATIC_STRAIGHT);
					ids.editor.enableAutomaticLayout();
				} else
					if (resources.getCommonButtons().getJc().getSelectedIndex() == 1) {
						ids.prefs.setRelationshiplayout(Preferences.RelationshipLayout.AUTOMATIC_RADIAL);
						ids.editor.enableAutomaticLayout();
					}
				else {
					ids.prefs.setRelationshiplayout(Preferences.RelationshipLayout.MANUAL);
					ids.editor.disableAutomaticLayout();
				}
			}
		});
	}

	


	/*public void initialiseInternalIDEStateFromNewIDEState(
			Properties oldprops, IDEState nids, IDEUpdater updater)
	throws UnknowFormat, CannotLoad {

		DnDJTree arbolProyectos=resources.getArbolProyectos();
		JTree arbolObjetos=resources.getArbolObjetos();
		DefaultMutableTreeNode rootProyectos=(DefaultMutableTreeNode) arbolProyectos.getModel().getRoot();
		DefaultMutableTreeNode rootObjetos=(DefaultMutableTreeNode) arbolObjetos.getModel().getRoot();


		// If no exception is thrown, then the load process was succesful.
		// So current data can be replaced with the new one

		ids.editor = nids.editor;
		resources.getButtonModelPanel().removeAll();      
		resources.getPprin().add(ids.editor, BorderLayout.CENTER);

		this.replaceTree(rootObjetos,
				(DefaultMutableTreeNode) nids.om.arbolObjetos.getModel().getRoot());
		this.replaceTree(rootProyectos,
				(DefaultMutableTreeNode) nids.gm.arbolProyecto.getModel().getRoot());

		nids.gm.arbolProyecto =resources.getArbolProyectos();
		nids.gm.root = rootProyectos;
		nids.om.arbolObjetos = resources.getArbolObjetos();
		nids.om.setRoot(rootObjetos);

		( (DefaultTreeModel) resources.getArbolProyectos().getModel()).reload();
		( (DefaultTreeModel) resources.getArbolObjetos().getModel()).reload();


		for (Object key:nids.prop.keySet()){
			ids.prop.put(key,nids.prop.get(key));							
		}

		Log.getInstance().logSYS("Project loaded successfully");
		resources.getMainFrame().validate();


		ids.setChanged(false);
		resources.setUnChanged();

		// Stores last file name path so that next save is performed in the same
		// folder

		HistoryManager.updateProperties(oldprops, nids);



		for (TreePath tp:ids.gm.toExpad){
			Vector<Object> npath=new Vector<Object>(); 
			for (Object path:tp.getPath()){
				npath.add(path);
			}
			npath.remove(0);
			npath.insertElementAt(rootProyectos, 0);
			arbolProyectos.expandPath(new TreePath(npath.toArray()));				
		}

		restorePreferences(updater);
		createEditorAndRelatedComponents(ids);		
		if (ids.editor.getGraph()!=null){
			ids.editor.changeGraph((ModelJGraph)ids.editor.getGraph());
			updateButtonBars();
		}

	}*/

	public void updateButtonBars(){
		Runnable refreshButtons=new Runnable(){
			public void run(){		
				
				resources.getButtonModelPanel().removeAll();
				if (ids.editor.getGraph()!=null){
					FilteredJToolBar ftb=(FilteredJToolBar)ids.editor.creaPaleta();		
					if (ids.getDiagramFilter()!=null)
					 ftb.applyFilter(ids.getDiagramFilter());
					ScrollableBar modelToolBar = new ScrollableBar(ftb, ScrollableBar.VERTICAL);					
					//ids.editor.getUpperSidePanel().add(resources.getCommonButtons());
					resources.getButtonModelPanel().add(modelToolBar, BorderLayout.CENTER);
					modelToolBar.invalidate();
					modelToolBar.validate();
					modelToolBar.repaint();

				}

				
				/*resources.getCommonButtons().invalidate();
				resources.getMainFrame().validate();
				resources.getMainFrame().invalidate();
				resources.getMainFrame().validate();
				resources.getMainFrame().repaint();				
				resources.getCommonButtons().repaint();*/		
			}
		};

		SwingUtilities.invokeLater(refreshButtons);
	}

	public void ChangeCurrentDiagram(ModelJGraph m) {
		//ids.gm.setCurrent(m);
		ids.editor.changeGraph(m,ids);
		//updateButtonBars();
		ids.editor.validate();
		ids.editor.repaint();		
	}

	


}
