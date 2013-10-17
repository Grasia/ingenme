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

import ingenias.editor.cell.*;
import ingenias.editor.entities.*;

import java.awt.*;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Hashtable;
import org.jgraph.graph.*;
import org.jgraph.*;
import org.jgraph.event.*;

import ingenias.editor.Model;
import ingenias.editor.ModelJGraph;
import ingenias.editor.ObservableModel;
import java.util.*;

/**
 *
 * Avoids that an entity gets outta of the screen.
 * It does so by setting X to 1 when X<0 and Y to 1 when Y<0
 *
 */
public class ChangeEntityLocation
implements org.jgraph.event.GraphModelListener {
	private Object workingObject = null;
	private boolean enabled = true;
	private JGraph mjg;

	public ChangeEntityLocation(JGraph graph) {
		this.mjg=graph;
	}

	public void graphChanged(org.jgraph.event.GraphModelEvent gme) {
		
		if (( gme.getChange().getInserted() == null
				|| gme.getChange().getInserted().length == 0)) {
			Map old = gme.getChange().getPreviousAttributes();
			Map newAt = gme.getChange().getAttributes();
			if (old != null) {
				Hashtable<DefaultGraphCell, Map> changes=new Hashtable<DefaultGraphCell, Map>();
				Iterator keys = old.keySet().iterator();
				while (keys.hasNext()) {
					Object current = keys.next();
					if (current != null && ! 
							(NAryEdge.class.isAssignableFrom(current.getClass()))) {
						Map oluomap = (Map) old.get(current);
						Map newuomap = (Map) newAt.get(current);
						// Naryedges can be moved without invoking centering
					/*	if (newuomap != null && GraphConstants.getBounds(newuomap) != null &&
								oluomap!=null &&	GraphConstants.getBounds(oluomap)!=null) {
							Rectangle rect1 = GraphConstants.getBounds(oluomap).getBounds();
							Rectangle rect2 = GraphConstants.getBounds(newuomap).getBounds();							
							if (rect1 == null || rect2 == null || !rect1.equals(rect2)) {		
								System.err.println("new "+rect2);
								this.processChange(current,mjg,changes);
							}
						}*/
					}
					if (current != null &&
							! (NAryEdge.class.isAssignableFrom(current.getClass()))) {
						Map oluomap = (Map) old.get(current);
						Map newuomap = (Map) newAt.get(current);

						if (newuomap != null && GraphConstants.getBounds(newuomap) != null &&
								oluomap!=null &&	GraphConstants.getBounds(oluomap)!=null) {
							Rectangle rect1 = GraphConstants.getBounds(oluomap).getBounds();
							Rectangle rect2 = GraphConstants.getBounds(newuomap).getBounds();

							if (rect1 == null || rect2 == null || !rect1.equals(rect2)) {							
								this.processChange(current,mjg,changes);
							}
						}
					}

				}
				if (changes.size()>0){
			//		ChangeNARYEdgeLocation.solveOverlappings((ModelJGraph) mjg, changes);
					mjg.getModel().edit(changes, null, null, null);
				}
				
			}
		}
	}

	

	

	

	public void processChange(Object cell, JGraph mjg, Hashtable changes) {

		DefaultGraphCell dgc = (DefaultGraphCell) cell;
		ingenias.editor.Model m=(Model) mjg.getModel();

		Iterator it = m.getEdges(m, new Object[] {cell}).iterator();

		
		while (it.hasNext()) {
			DefaultEdge de = (DefaultEdge) it.next();
			NAryEdge nary = LocationChange.getNAryEdgeExtreme(de, m);
			if (nary!=null &&(
					enabled || 
					((Entity)(nary.getUserObject())).getPrefs(mjg.getModel().getAttributes(cell)).getView().equals(ViewPreferences.ViewType.NOICON))){
				LocationChange.centerNAryEdge(mjg, m, changes, nary);
			}
		}

	}

	

	

	public void disableAutomaticAllocation() {
		enabled = false;
	}

	public void enableAutomaticAllocation() {
		enabled = true;
	}
}