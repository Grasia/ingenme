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
import java.awt.geom.Rectangle2D;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Hashtable;
import java.util.Set;

import org.jgraph.graph.*;
import org.jgraph.*;
import org.jgraph.event.*;
import ingenias.editor.ObservableModel;
import java.util.*;

/**
 *
 * Avoids that an entity gets outta of the screen.
 * It does so by setting X to 1 when X<0 and Y to 1 when Y<0
 *
 */
public class CenterRelationships
implements org.jgraph.event.GraphModelListener {


	private boolean enabled = true;
	String workingObject = null;
	JGraph graph;

	public CenterRelationships(JGraph graph) {
		this.graph = graph;
	}

	public void graphChanged(org.jgraph.event.GraphModelEvent gme) {
	/*	if (enabled && this.workingObject == null) {
			this.workingObject = "hola";
			this.updateEdges( (ingenias.editor.Model) gme.getSource());
			this.workingObject = null;
		}*/
	}

/*	public Point getCenter(Vector points) {
		Iterator it = points.iterator();
		int x = 0;
		int y = 0;
		while (it.hasNext()) {
			Point point = (Point) it.next();
			x = x + point.x;
			y = y + point.y;
		}
		return new Point( (int) (x / points.size()), (int) (y / points.size()));
	}*/
	
	public static DefaultGraphCell getCellDefaultGraphCellFromDefaultEdge(DefaultEdge edge, ingenias.editor.Model m) {
		DefaultEdge de =  edge;
		DefaultGraphCell target = (DefaultGraphCell) ( (DefaultPort) m.getTarget(de)).
		getParent();
		DefaultGraphCell source = (DefaultGraphCell) ( (DefaultPort) m.getSource(de)).
		getParent();
		DefaultGraphCell defaultGraphCellExtreme = null;
		if (NAryEdge.class.isAssignableFrom(target.getClass())) {
			defaultGraphCellExtreme =  source;
		}
		if (NAryEdge.class.isAssignableFrom(source.getClass())) {
			defaultGraphCellExtreme =  target;
		}
		return defaultGraphCellExtreme;
	}

	public static NAryEdge getCellNAryEdgeFromDefaultEdge(DefaultEdge edge, ingenias.editor.Model m) {
		DefaultEdge de =  edge;
		DefaultGraphCell target = (DefaultGraphCell) ( (DefaultPort) m.getTarget(de)).
		getParent();
		DefaultGraphCell source = (DefaultGraphCell) ( (DefaultPort) m.getSource(de)).
		getParent();
		NAryEdge nary = null;
		if (NAryEdge.class.isAssignableFrom(target.getClass())) {
			nary = (NAryEdge) target;
		}
		if (NAryEdge.class.isAssignableFrom(source.getClass())) {
			nary = (NAryEdge) source;
		}
		
		return nary;
	}

	/*private void updateEdges(ingenias.editor.Model model) {
		Hashtable<Object,Map> changes = new Hashtable<Object,Map>();
		for (int k = 0; k < model.getRootCount(); k++) {
			if (ingenias.editor.cell.NAryEdge.class.isAssignableFrom(
					model.getRootAt(k).getClass())) {

				DefaultGraphCell dgc = (DefaultGraphCell) model.getRootAt(k);

				Iterator it = model.getEdges(model, new Object[] {dgc}).iterator();

				while (it.hasNext()) {
					DefaultEdge de = (DefaultEdge) it.next();
					DefaultGraphCell nary = dgc;
					Iterator nedges = model.getEdges(model, new Object[] {dgc}).iterator();
					Vector points = new Vector();
					while (nedges.hasNext()) {
						DefaultEdge edgeline = (DefaultEdge) nedges.next();
						DefaultGraphCell extreme = this.getCellExtreme(edgeline, model);
						Rectangle rect = GraphConstants.getBounds(extreme.getAttributes()).
						getBounds().getBounds();
						points.add(new Point( (int) rect.getCenterX(),
								(int) rect.getCenterY()));

					}
					Map edgem = nary.getAttributes();
					Point p = this.getCenter(points);
					Rectangle boundsEdge = GraphConstants.getBounds(edgem).getBounds();
					Rectangle oldvalue=new Rectangle(boundsEdge);
					p.x = p.x - boundsEdge.width / 2;
					p.y = p.y - boundsEdge.height / 2;
					if (!p.equals(boundsEdge.getLocation())){
						boundsEdge.setLocation(p);
						GraphConstants.setBounds(edgem, boundsEdge);						
					}
					changes.put(nary, edgem);
				}
			}			
		}

		System.err.println("reviewin1g edges......................");
		Set<Object> naries = changes.keySet();
		for (Object nary:naries){
			System.err.println("reviewing "+nary);
			Rectangle2D rect1= GraphConstants.getBounds(changes.get(nary));
			HashSet<Object> nset = new HashSet<Object>();
			nset.addAll(naries);
			nset.remove(nary);
			for (Object othernary:nset){
				Rectangle2D rect2=GraphConstants.getBounds(changes.get(othernary));
				if (rect1.intersects(rect2)){
					double cxr1 = rect1.getCenterX();
					double cyr1=rect1.getCenterY();
					double cxr2=rect2.getCenterX();
					double cyr2=rect2.getCenterY();						
					double dx=cxr1-cxr2;
					double dy=cyr1-cyr2;
					double module=Math.sqrt(dx*dx+dy*dy);
					dx=dx/module;
					dy=dy/module;
					double distance=10;
					boolean additionaloverlapping=true;
					HashSet<Object> overlappingcheckset = new HashSet<Object>();
					overlappingcheckset.addAll(nset);
					overlappingcheckset.remove(othernary);
					Rectangle rectn1=null;
					do{
						rectn1=new Rectangle((int)(cxr1-distance*module),(int)(cyr1-distance*module),(int)rect1.getWidth(),(int)rect1.getHeight());
						distance=distance+5;
					} while (checkOverlapping(rectn1,overlappingcheckset,changes));
					Rectangle rectn2=null;
					distance=10;
					do{
						rectn2=new Rectangle((int)(cxr2+distance*module),(int)(cyr2+distance*module),(int)rect2.getWidth(),(int)rect2.getHeight());
						distance=distance+5;
					} while (checkOverlapping(rectn2,overlappingcheckset,changes));
					GraphConstants.setBounds(changes.get(othernary),rectn2);
					GraphConstants.setBounds(changes.get(nary),rectn1);			
				}
			}
		}

		if (changes.size()>0)
			model.edit(changes, null, null, null);

	}

	private boolean checkOverlapping(Rectangle rect1,
			HashSet<Object> overlappingcheckset,
			Hashtable<Object,Map> changes) {
		Iterator<Object> it = overlappingcheckset.iterator();
		Rectangle2D rect2=null;
		do {
			rect2=GraphConstants.getBounds(changes.get(it.next()));						
		} while (it.hasNext() && rect1.intersects(rect2));
		return rect2!=null && rect2.intersects(rect1);		
	}*/

}
