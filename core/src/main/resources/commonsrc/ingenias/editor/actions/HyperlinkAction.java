
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
import ingenias.editor.entities.Entity;
import ingenias.exception.NotInitialised;
import ingenias.generator.browser.Browser;
import ingenias.generator.browser.BrowserImp;
import ingenias.generator.browser.Graph;

import java.awt.Component;
import java.net.URL;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.tree.TreePath;

import org.jgraph.graph.DefaultGraphCell;

public class HyperlinkAction implements HyperlinkListener{
	private String lastScrolledEntity="";
	private int lastScrolledIndex=0;

	private IDEState ids;
	private GUIResources resources;
	protected Vector<TreePath> foundpaths=new Vector<TreePath>();

	protected int lastFoundIndex=0;
	
	private Browser browser;

	protected String lastSearch="";
	
	public HyperlinkAction(IDEState ids, GUIResources resources){
		this.ids=ids;
		this.resources=resources;
		browser=new BrowserImp(ids);
	}
	
	public void hyperlinkUpdate(HyperlinkEvent e) {
		
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				JEditorPane pane = (JEditorPane) e.getSource();
				URL url=e.getURL();
				if (url!=null){
					if (url.getHost().equals("app")){
						String completePath=url.getPath().substring(1);								
						String diagramPath=null;
						String entityPath=null;
						if (completePath.indexOf("/")>-1){
							diagramPath=completePath.substring(0,completePath.indexOf("/"));
							entityPath=completePath.substring(completePath.indexOf("/")+1,completePath.length());
						} else 
							diagramPath=completePath;																							

						Graph g=null;
						BrowserImp bimp=new BrowserImp(ids);
						bimp.getGraph(diagramPath);
						g = bimp.getGraph(diagramPath);						
						ids.editor.changeGraph(g.getGraph(),ids);
						//updateButtonBars();

						if (entityPath!=null){
							DefaultGraphCell dgc=null;
							g.getGraph().clearSelection();
							Vector<DefaultGraphCell> dgcs=new Vector<DefaultGraphCell>(); 
							for (int j=0;j<g.getGraph().getModel().getRootCount();j++){
								dgc=(DefaultGraphCell)g.getGraph().getModel().getRootAt(j);
								if (dgc.getUserObject() instanceof Entity){
									Entity ent=(Entity)dgc.getUserObject();
									if (ent.getId().equals(entityPath)){
										g.getGraph().addSelectionCell(dgc);	
										dgcs.add(dgc);
									}
								};
							}
							if (dgc!=null)

								if (lastScrolledEntity.equals(entityPath) && lastScrolledIndex<dgcs.size()){
									g.getGraph().scrollCellToVisible(dgcs.elementAt(lastScrolledIndex));
									lastScrolledIndex=(lastScrolledIndex+1)%dgcs.size();
								} else {
									lastScrolledEntity=entityPath;
									lastScrolledIndex=0;

									g.getGraph().scrollCellToVisible(dgcs.elementAt(lastScrolledIndex));
								}
						}
					} else
						if (url.getHost().equals("ent")){
							String entity=url.getFile().substring(1);

							Vector userobject = ids.om.findUserObject(entity);
							if (userobject.size()==0){
								userobject=new Vector();
								Graph[] graphs=browser.getGraphs();
								for (int k=0;k<graphs.length;k++){

									for (int j=0;j<graphs[k].getGraph().getModel().getRootCount();j++){
										DefaultGraphCell dgc=(DefaultGraphCell)graphs[k].getGraph().getModel().getRootAt(j);
										if (dgc.getUserObject() instanceof Entity){
											Entity ent=(Entity)dgc.getUserObject();
											if (ent.getId().equals(entity)){
												userobject.add(ent);
												graphs[k].getGraph().setSelectionCell(dgc);
												graphs[k].getGraph().scrollCellToVisible(dgc);
											}
										};
									}
								}
							} else {  			

								locateAndScrollToObject(entity);

							}
						}
				}

			}	
		
		
	}
	
	private void locateAndScrollToObject(String id) {
		if (id.equals(lastSearch) && lastFoundIndex <foundpaths.size() ){
			TreePath tp=(TreePath)this.foundpaths.elementAt(lastFoundIndex);
			ids.om.arbolObjetos.expandPath(tp);	  
			ids.om.arbolObjetos.scrollPathToVisible(tp);
			ids.om.arbolObjetos.setSelectionPath(tp);
			lastFoundIndex++;
		} else{
			foundpaths=this.ids.om.findUserObjectPathRegexp(id+".*");
			if (foundpaths.size()>0){
				lastFoundIndex=0;
				lastSearch=id;
				TreePath tp=(TreePath)this.foundpaths.elementAt(lastFoundIndex);
				ids.om.arbolObjetos.expandPath(tp);	  
				ids.om.arbolObjetos.scrollPathToVisible(tp);
				ids.om.arbolObjetos.setSelectionPath(tp);
				lastFoundIndex++;

			}

		}
	}
}
