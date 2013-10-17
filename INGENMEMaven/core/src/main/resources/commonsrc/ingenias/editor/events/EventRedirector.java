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
package ingenias.editor.events;

import ingenias.editor.ButtonToolBar;
import ingenias.editor.Editor;
import ingenias.editor.ModelJGraph;
import ingenias.editor.cell.NAryEdge;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphModel;

/**
 *  Description of the Class
 *
 *@author     developer
 *@created    29 de septiembre de 2002
 */
public class EventRedirector
extends AbstractAction
implements java.io.Serializable {

	/**
	 * 
	 */
	protected final Editor editor;
	/**
	 *  Description of the Field
	 */
	protected Action action;

	protected ModelJGraph model;

	public ModelJGraph getModel() {
		return model;
	}

	// Construct the "Wrapper" Action
	/**
	 *  Constructor for the EventRedirector object
	 *
	 *@param  a  Description of Parameter
	 * @param editor TODO
	 * @param imageIcon 
	 */
	public EventRedirector(Editor editor, Action a, ImageIcon imageIcon) {
		super("", imageIcon);		
		this.editor = editor;
		this.action = a;
	}

	public void updateAction(Action a, ModelJGraph graph) {
		this.action=a;
		model=graph;
	}

	public Action getAction() {
		return action;
	}

	private static DefaultGraphCell[] getEdgeExtremes(DefaultEdge edge, GraphModel m) {
		DefaultEdge de =  edge;
		DefaultGraphCell target = (DefaultGraphCell) ( (DefaultPort) m.getTarget(de)).
		getParent();
		DefaultGraphCell source = (DefaultGraphCell) ( (DefaultPort) m.getSource(de)).
		getParent();

		return new DefaultGraphCell[]{target,source};
	}

	// Redirect the Actionevent. It extends the selection so that whenever a relationship is selected (part or whole), the whole relationships and extremes are selected as well

	public void actionPerformed(ActionEvent e) {
		if (editor.getGraph() != null) {
			// Unselects relationships and edges
			Object[] objs = this.editor.getGraph().getSelectionCells();
			Vector<Object> nobjs=new Vector<Object>();
			for (Object obj:objs){
				Vector<DefaultGraphCell> children = this.editor.getGraph().getListenerContainer().getChildren(obj);
				nobjs.addAll(children);
				nobjs.add(obj);
			}
			objs=nobjs.toArray();
			this.editor.getGraph().setSelectionCells(objs);
			expandSelectionToRelationshipsAndEdges(objs, this.editor.getGraph());

			// }
			e = new ActionEvent(this.editor.getGraph(), e.getID(),
					e.getActionCommand(), e.getModifiers());
			action.actionPerformed(e);
		}

	}

	public static void expandSelectionToRelationshipsAndEdgesExcludingOtherExtremes(Object[] objs, JGraph graph) {

		int oldSelectionCount=0;
		do {
			oldSelectionCount=graph.getSelectionCells().length;
			HashSet<Object> childrenInContainer=new HashSet<Object>();
			for (Object ccell:objs){
				if (ccell instanceof DefaultGraphCell)
					childrenInContainer.addAll(
							((ModelJGraph)graph).getListenerContainer().getRecursivelyChildren(
									(DefaultGraphCell) ccell));
				childrenInContainer.add(ccell);
			}
			objs=childrenInContainer.toArray();
			graph.addSelectionCells(objs);

			for (int k = 0; k < objs.length; k++) {
				if ((objs[k] instanceof DefaultGraphCell) 
						&& !(objs[k] instanceof NAryEdge)
						&& !(objs[k] instanceof DefaultEdge)) {
					if (((DefaultGraphCell)objs[k]).getChildCount()>0){
						Iterator edges = graph.getModel().edges(((DefaultGraphCell)objs[k]).getChildAt(0));
						// verify that both sources and target are included
						while (edges.hasNext()){
							graph.addSelectionCells(new Object[]{edges.next()});
						}
					}
				} 
			}

			objs=graph.getSelectionCells();
			for (int k = 0; k < objs.length; k++) {
				if (objs[k] instanceof DefaultEdge) {
					// verify that both sources and target are included
					DefaultGraphCell[] cells=getEdgeExtremes((DefaultEdge)objs[k],graph.getModel());
					for (DefaultGraphCell cell:cells){
						if (cell instanceof NAryEdge)
							graph.addSelectionCells(new Object[]{cell});
					}

				} 

			}

			objs =graph.getSelectionCells();
			for (int k = 0; k < objs.length; k++) {
				if (objs[k] instanceof NAryEdge) {
					NAryEdge edges=(NAryEdge)objs[k];
					DefaultEdge[] des=edges.getRepresentation();
					graph.addSelectionCells(des);
				}
			}
		} while (graph.getSelectionCells().length!=oldSelectionCount);

	}


	public static void expandSelectionToRelationshipsAndEdges(Object[] objs, JGraph graph) {
		for (int k = 0; k < objs.length; k++) {
			if (objs[k] instanceof DefaultEdge) {
				// verify that both sources and target are included
				DefaultGraphCell[] cells=getEdgeExtremes((DefaultEdge)objs[k],graph.getModel());
				graph.addSelectionCells(cells);			
			}
		}
		objs =graph.getSelectionCells();
		for (int k = 0; k < objs.length; k++) {
			if (objs[k] instanceof NAryEdge) {
				NAryEdge edges=(NAryEdge)objs[k];
				DefaultEdge[] des=edges.getRepresentation();
				graph.addSelectionCells(des);
				boolean wrong=false;
				for (int j=0;j<des.length;j++){
					if ((findElementInArray(des[j],objs)==null)){
						wrong=true;
						System.err.println("Not found edge in "+edges);
					} else {
						DefaultGraphCell[] cells=getEdgeExtremes(des[j],graph.getModel());
						graph.addSelectionCells(cells);

					}
				}				
			}

		}
		objs = graph.getSelectionCells();

		for (int k = 0; k < objs.length; k++) {
			if (objs[k] instanceof DefaultEdge) {
				// verify that both sources and target are included
				DefaultGraphCell[] cells=getEdgeExtremes((DefaultEdge)objs[k],graph.getModel());
				graph.addSelectionCells(cells);			
			}
		}
	}

	private static Object findElementInArray(DefaultGraphCell cell, Object[] objs) {
		for (int k=0;k<objs.length;k++){
			if (objs[k]==cell)
				return cell;
		}
		return null;
	}
}