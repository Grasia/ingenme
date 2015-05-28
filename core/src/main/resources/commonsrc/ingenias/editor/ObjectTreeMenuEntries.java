
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

import ingenias.editor.entities.Entity;
import ingenias.editor.utils.DialogWindows;
import ingenias.exception.NotInitialised;
import ingenias.exception.NullEntity;
import ingenias.generator.browser.AttributedElement;
import ingenias.generator.browser.Browser;
import ingenias.generator.browser.BrowserImp;
import ingenias.generator.browser.Graph;
import ingenias.generator.browser.GraphAttribute;
import ingenias.generator.browser.GraphEntity;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jgraph.JGraph;

public class ObjectTreeMenuEntries {

	private IDEState ids;
	private GUIResources resources;
	private Browser browser;

	public ObjectTreeMenuEntries(IDEState ids, GUIResources resources){
		this.ids=ids;
		this.resources=resources;
		browser=new BrowserImp(ids);
	}

	public JPopupMenu menuObjectTree(MouseEvent me1) {
		JPopupMenu menu = new JPopupMenu();
		final MouseEvent me = me1;
		final TreePath tp = ids.om.arbolObjetos.getSelectionPath();
		final TreePath[] tps = ids.om.arbolObjetos.getSelectionPaths();

		if (tp != null && ids.gm.getModel(tp.getPath()) == null) {
			JGraph jg = ids.gm.getCurrent();
			final javax.swing.tree.DefaultMutableTreeNode dmtn =
					(javax.swing.tree.DefaultMutableTreeNode) tp.getLastPathComponent();

			if (dmtn != null && dmtn.getUserObject()instanceof Entity) {
				Entity entity=(Entity) dmtn.getUserObject();


				// Edit
				menu.add(
						new AbstractAction("Add to current graph") {

							public void actionPerformed(ActionEvent e) {


								Entity sel = (Entity) dmtn.getUserObject();
								ids.editor.insertDuplicated(new Point(0, 0), sel);
								ids.otherChange();
							}
						});

				menu.add(
						new AbstractAction("Edit") {

							public void actionPerformed(ActionEvent e) {

								Entity sel = (Entity) dmtn.getUserObject();
								ingenias.editor.editiondialog.GeneralEditionFrame jf = new ingenias.editor.editiondialog.GeneralEditionFrame(ids.editor, 
										ids.om,ids.gm, resources.getMainFrame(),
										"Edit " + sel.getId(), sel);

								jf.setLocation(DialogWindows.getCenter(jf.getSize(),resources.getMainFrame()));
								jf.pack();
								jf.setVisible(true);
								if (jf.getStatus()==jf.ACCEPTED){
									ids.om.reload();								
									resources.getArbolObjetos().invalidate();								
									resources.getMainFrame().repaint();
									ids.setChanged(true);
									resources.setUnChanged();
								}
							}
						});

				menu.add(
						new AbstractAction("Remove") {

							public void actionPerformed(ActionEvent e) {
								for (int k=0;k<tps.length;k++){
									javax.swing.tree.DefaultMutableTreeNode dmtn =
											(javax.swing.tree.DefaultMutableTreeNode) tps[k].getLastPathComponent();
									if (dmtn != null && dmtn.getUserObject()instanceof Entity) {
										int result = JOptionPane.showConfirmDialog(resources.getMainFrame(),
												"This will remove permanently " + tps[k].getLastPathComponent() +
												". Are you sure?",
												"removing object", JOptionPane.YES_NO_OPTION);
										if (result == JOptionPane.OK_OPTION) {											
											Entity sel = (Entity) dmtn.getUserObject();
											ids.om.removeEntity(sel);
											ids.gm.removeEntityFromAllGraphs(sel);											
											resources.getMainFrame().repaint();
											ids.otherChange(); 
										}}
								}
							}
						});

				menu.add(
						new AbstractAction("Search occurrences") {				
							public void actionPerformed(ActionEvent e) {

								if (dmtn.getUserObject()instanceof Entity){
									Entity ent=(Entity)dmtn.getUserObject();

									StringBuffer result=new StringBuffer();
									result.append("Diagrams found:<ul><li>Explicit occurrences:<ul>");
									Graph[] graphs=browser.getGraphs();
									for (int k=0;k<graphs.length;k++){
										GraphEntity[] ges;
										try {
											ges = graphs[k].getEntities();
											boolean found=false;
											for (int j=0;j<ges.length &&!found;j++){
												found=ges[j].getID().equals(ent.getId());
											}
											if (found){

												result.append("<li><a href=\"http://app/"+graphs[k].getName()+"/"+ent.getId()+"\">"+graphs[k].getName()+"</a>");
											}
										} catch (NullEntity e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
									}
									result.append("</ul></li><li>Occurrences inside other entities:<ul>");
									graphs=browser.getGraphs();
									for (int k=0;k<graphs.length;k++){
										GraphEntity[] ges;
										try {
											ges = graphs[k].getEntities();
											boolean found=false;
											int j=0;
											for (j=0;j<ges.length &&!found;j++){
												found=!ges[j].getID().equals(ent.getId()) && 
														isUsedInAtts(
																browser.findEntity(ent.getId()), 
														browser.findEntity(ges[j].getID()), 
														browser, 
														new Hashtable<String,AttributedElement>());
											}
											if (found){
												result.append("<li><a href=\"http://app/"+graphs[k].getName()+"/"+ges[j].getID()+"\">"+graphs[k].getName()+" inside entity "+ges[j].getID()+" </a>");
											}
										} catch (NullEntity e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} catch (IllegalAccessException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
									}

									
									result.append("</ul></li></ul>");
									resources.getSearchDiagramPanel().setText(result.toString());
									resources.focusSearchPane();
								}

							}
						});
			} else {
				menu.add(
						new AbstractAction("Expand") {

							public void actionPerformed(ActionEvent e){
								Enumeration<DefaultMutableTreeNode> enumNodes=dmtn.depthFirstEnumeration();
								while (enumNodes.hasMoreElements()){									
									DefaultMutableTreeNode next=enumNodes.nextElement();
									resources.getArbolObjetos().expandPath(new TreePath(next.getPath()));
								}

							}

						});
			}
		}

		return menu;
	}
	
	public static boolean isUsedInAtts(GraphEntity original, AttributedElement tested, Browser browser, Hashtable<String, AttributedElement> checked) throws IllegalAccessException, NullEntity{
		boolean used=false;
		GraphAttribute [] fs=tested.getAllAttrs();
		for (int j=0;j<fs.length && !used;j++){
			GraphAttribute att=fs[j];
			if (att.isEntityValue() && att.getEntityValue()!=null && att.getEntityValue().equals(original)){
				used=true;
			} else
				if (att.isEntityValue() && att.getEntityValue()!=null && !checked.contains(att.getEntityValue().getID())){
					checked.put(att.getEntityValue().getID(), att.getEntityValue());
					used=isUsedInAtts(original, att.getEntityValue(), browser,checked);
				} else
					if (att.isCollectionValue() && att.getCollectionValue()!=null){
						if (att.getCollectionValue().contains(original)){
							used =true;
						}	else {
							for (int k=0;k<att.getCollectionValue().size() &&!used;k++){
								if (!checked.contains(att.getCollectionValue().getElementAt(k).getID())){
									checked.put(att.getCollectionValue().getElementAt(k).getID(), att.getCollectionValue().getElementAt(k));
									used=isUsedInAtts(original, att.getCollectionValue().getElementAt(k), browser, checked);
								}
							}
						}
					}
		}

		return used;
	}

}
