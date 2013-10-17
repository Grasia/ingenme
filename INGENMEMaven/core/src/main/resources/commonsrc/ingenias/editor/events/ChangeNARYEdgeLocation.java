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

import ingenias.editor.Model;
import ingenias.editor.ModelJGraph;
import ingenias.editor.cell.*;
import ingenias.editor.entities.Entity;
import ingenias.editor.entities.ViewPreferences;

import java.awt.*;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

import org.jgraph.graph.*;
import org.jgraph.*;
import org.jgraph.event.*;

/**
 * This class reallocates a n-ary edge so the central point of the
 * set of ports that it inter-connects. The position is calculated
 * by adding the (x,y) of each port and dividing by the number of
 * ports
 */
public class ChangeNARYEdgeLocation
implements org.jgraph.event.GraphModelListener {
	private Object workingObject = null;
	private boolean alreadyExecuting = true;
	private int counter = 0;
	JGraph graph = null;
	private boolean enabled=true;

	public ChangeNARYEdgeLocation(JGraph graph) {
		this.graph = graph;
	}

	public void graphChanged(org.jgraph.event.GraphModelEvent gme) {

		if (enabled && alreadyExecuting && (gme.getChange().getInserted()==null
				||gme.getChange().getInserted().length==0 )) {
			alreadyExecuting=false;
			Hashtable<DefaultGraphCell, Map> changes = new Hashtable<DefaultGraphCell,Map>();
			Map old = gme.getChange().getPreviousAttributes();
			Map newAt = gme.getChange().getAttributes();
			if (old != null) {
				// No autocenter. Just watch that no overlapping occurs.
				Iterator keys = old.keySet().iterator();

				while (keys.hasNext()) {					
					Object current = keys.next();

					Map oluomap = (Map) old.get(current);
					Map newuomap = (Map) newAt.get(current);
					if ((GraphConstants.getBounds(oluomap)==null 
							||GraphConstants.getBounds(newuomap)==null) ){

						if (current instanceof NAryEdge)  {
							this.processChange(current,changes);				
						}
					} else {
						Rectangle rect1 = GraphConstants.getBounds(oluomap).getBounds();
						Rectangle rect2 = GraphConstants.getBounds(newuomap).getBounds();
						if (((rect1 == null || rect2 == null || !rect1.equals(rect2))) &&
								current instanceof NAryEdge) {
							this.processChange( current,changes);						
						}
					}

				}


				solveOverlappings((ModelJGraph) graph, changes);

				if (changes.size()>0){
					graph.getModel().edit(changes, null, null, null);					
				}
			}
			alreadyExecuting=true;
		}

	}

	public static void solveOverlappings(ModelJGraph graph,
			Hashtable<DefaultGraphCell, Map> map) {		
				Hashtable<Object,Map> naryedgeatts=new  Hashtable<Object,Map>();
			for (int k=0;k<graph.getModel().getRootCount();k++){
				if (graph.getModel().getRootAt(k) instanceof NAryEdge){
					/*if (changes.get(graph.getModel().getRootAt(k))!=null)
					naryedgeatts.put(graph.getModel().getRootAt(k),
							changes.get(graph.getModel().getRootAt(k)));
				else*/
					naryedgeatts.put(graph.getModel().getRootAt(k),
							graph.getModel().getAttributes(graph.getModel().getRootAt(k)));
				}
			}



			Set<DefaultGraphCell> naries = map.keySet();
			for (Object currentnary:naries){
				if (currentnary instanceof NAryEdge){
					Rectangle2D rect1= GraphConstants.getBounds(map.get(currentnary));
					detectingOverlappingsAndRadialLayout(map, naryedgeatts,
							currentnary, rect1,graph);

				}
			}
	
	}



	private static void detectingOverlappingsAndRadialLayout(
			Hashtable<DefaultGraphCell, Map> map,
			Hashtable<Object, Map> naryedgeatts, Object currentnary,
			Rectangle2D rect1, ModelJGraph graph) {
		HashSet<Object> nset = new HashSet<Object>();
		nset.addAll(naryedgeatts.keySet());
		nset.remove(currentnary);


		double cxr1 = rect1.getX();
		double cyr1=  rect1.getY();
		Rectangle rectn1=new Rectangle((int)(cxr1),(int)(cyr1),(int)rect1.getWidth(),(int)rect1.getHeight());

		double distance = 10;
		double angle = 0;

		while (checkOverlapping(rectn1,(NAryEdge) currentnary,nset,naryedgeatts,graph)){		
			double nx=Math.max(rect1.getX()+distance*Math.cos(angle),0);
			double ny=Math.max(rect1.getY()+distance*Math.sin(angle),0);				 
			rectn1=new Rectangle((int)(nx),(int)(ny),(int)rect1.getWidth(),(int)rect1.getHeight());
			angle = angle + 2;
			if (angle >=360){
				distance = distance + 15;
				angle = 0;
			};
		};

		GraphConstants.setBounds(map.get(currentnary),rectn1);		
		rect1=rectn1;

	}

	private static void detectingOverlappingsAndSpringLayout(
			Hashtable<DefaultGraphCell, Map> map,
			Hashtable<Object, Map> naryedgeatts, Object currentnary,
			Rectangle2D rect1, ModelJGraph graph) {
		HashSet<Object> nset = new HashSet<Object>();
		nset.addAll(naryedgeatts.keySet());
		nset.remove(currentnary);
		for (Object othernary:naryedgeatts.keySet()){
			if (othernary!=currentnary){
				Rectangle2D rect2=GraphConstants.getBounds(naryedgeatts.get(othernary));
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
					double distance=3;
					boolean additionaloverlapping=true;
					HashSet<Object> overlappingcheckset = new HashSet<Object>();
					overlappingcheckset.addAll(nset);
					Rectangle rectn1=null;
					do{
						rectn1=new Rectangle((int)(rect1.getX()-distance*dx),(int)(rect1.getY()-distance*dy),(int)rect1.getWidth(),(int)rect1.getHeight());
						distance=distance+5;
						//	System.err.println("increasing distance "+distance+ " checking over "+overlappingcheckset.size());
					} while (checkOverlapping(rectn1,(NAryEdge) currentnary,overlappingcheckset,naryedgeatts,graph));			
					GraphConstants.setBounds(map.get(currentnary),rectn1);			
					rect1=rectn1;
				}
			}
		}
	}

	private static boolean checkOverlapping(Rectangle rect1,NAryEdge currentNary,
			HashSet<Object> overlappingcheckset,
			Hashtable<Object,Map> changes, ModelJGraph graph) {
		Iterator<Object> it = overlappingcheckset.iterator();
		Rectangle2D rect2=null;
		boolean intersectsline=evaluateCurrentNAryEdge(rect1, graph,
				false, currentNary);
		if (it.hasNext() && !intersectsline){

			do {
				NAryEdge current=(NAryEdge) it.next();
				Map next = changes.get(current);				
				if (next!=null){
					rect2=GraphConstants.getBounds(next);
					intersectsline=intersectsline|| evaluateCurrentNAryEdge(rect1, graph, intersectsline, current);			
				}				
			} while (it.hasNext() && (rect2==null || !rect1.intersects(rect2)) && !intersectsline);
		}
		// this slows down things a lot
		for (int k=0;k<graph.getModel().getRootCount() && !intersectsline;k++){
			if (!(graph.getModel().getRootAt(k) instanceof NAryEdge) &&
					graph.getModel().getRootAt(k) instanceof DefaultGraphCell &&
					!graph.getListenerContainer().isContainer((DefaultGraphCell) graph.getModel().getRootAt(k)) &&
					graph.getListenerContainer().parentHasVisibleContainers(
							(DefaultGraphCell)graph.getModel().getRootAt(k)).isEmpty()){
				AttributeMap atts = graph.getModel().getAttributes(graph.getModel().getRootAt(k));
				if (atts!=null&& GraphConstants.getBounds(atts)!=null)
					intersectsline=intersectsline||rect1.intersects(
							GraphConstants.getBounds(atts).getBounds());
			}
		}
		return intersectsline || (rect2!=null && rect2.intersects(rect1));		
	}

	private static boolean evaluateCurrentNAryEdge(Rectangle rect1,
			ModelJGraph graph, boolean intersectsline, NAryEdge currentNary) {

		for (int k=0;k<graph.getModel().getChildCount(currentNary) && !intersectsline;k++){
			Object port = graph.getModel().getChild(currentNary,k);							
			intersectsline = intersectsline||detectEdgeIntersections(rect1, graph,
					intersectsline, port);
		}	
		return intersectsline;
	}

	private static boolean detectEdgeIntersections(Rectangle rect1,
			JGraph graph, boolean intersectsline, Object port) {
		Iterator edges = graph.getModel().edges(port);
		while (edges.hasNext()){
			Object edge=edges.next();
			Object targetport = graph.getModel().getTarget(edge);
			Object sourceport = graph.getModel().getSource(edge);
			AttributeMap attargetport = graph.getModel().getAttributes(graph.getModel().getParent(targetport));
			AttributeMap atsourceport = graph.getModel().getAttributes(graph.getModel().getParent(sourceport));
			Rectangle b1 = GraphConstants.getBounds(attargetport).getBounds();
			Rectangle b2 = GraphConstants.getBounds(atsourceport).getBounds();
			Float line = new Line2D.Float(new Point((int)b1.getCenterX(),(int)b1.getCenterY()),new Point((int)b2.getCenterX(),(int)b2.getCenterY()));					    
			intersectsline=intersectsline||line.intersects(rect1);
			if (!(graph.getModel().getParent(targetport) instanceof NAryEdge) && 
					!ListenerContainer.isContainer((DefaultGraphCell) graph.getModel().getParent(targetport), 
							graph)){
				intersectsline=intersectsline || b1.intersects(rect1);
				//		System.err.println("overlapping1 with "+targetport+","+graph.getModel().getParent(targetport)+" "+rect1+":"+b1);
			} else
				if (!(graph.getModel().getParent(sourceport) instanceof NAryEdge) && 
						!ListenerContainer.isContainer((DefaultGraphCell) graph.getModel().getParent(sourceport), 
								graph)){
					intersectsline=intersectsline || b2.intersects(rect1);
					//	System.err.println("overlapping2 with "+sourceport+","+graph.getModel().getParent(sourceport)+" "+rect1+":"+b2);
				}
		}
		return intersectsline;
	}

	public void processChange(Object naryedge,Hashtable changes) {
		NAryEdge nary = (NAryEdge)naryedge;		
		LocationChange.centerNAryEdge(graph, (Model)graph.getModel(), changes, nary);

	}



	public void disableAutomaticAllocation() {
		alreadyExecuting = false;
		enabled=false;
	}

	public void enableAutomaticAllocation() {
		alreadyExecuting = true;
		enabled=true;
	}

}
