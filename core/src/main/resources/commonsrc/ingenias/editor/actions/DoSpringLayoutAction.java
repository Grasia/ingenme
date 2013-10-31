
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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;

public class DoSpringLayoutAction {
	private IDEState ids;
	private GUIResources resources;
	
	public DoSpringLayoutAction(IDEState ids, GUIResources resources){
		this.ids=ids;
		this.resources=resources;
	}
	
	public void springLayout(){
		GraphModel gm=this.ids.editor.getGraph().getModel();
		Hashtable vectors=new Hashtable();


		for (int k=0;k<gm.getRootCount();k++){
			DefaultGraphCell dgc = (DefaultGraphCell) gm.getRootAt(k);
			Object userobject=dgc.getUserObject();
			CellView currentview=this.ids.editor.getGraph().getGraphLayoutCache().getMapping(dgc,false);
			Map attributes=dgc.getAttributes();
			Rectangle2D loc=GraphConstants.getBounds(attributes);
			double dirx=0;
			double diry=0;
			if (loc!=null){
				loc=(Rectangle2D)loc.clone();
				loc.setRect(loc.getX(),loc.getY(),loc.getWidth()+10,loc.getHeight()+10);
				Vector alreadyconsidered=new Vector();

				for (int i=0;i<gm.getRootCount();i++){
					DefaultGraphCell otherdgc = (DefaultGraphCell) gm.getRootAt(i);
					if (otherdgc!=dgc){
						Map otheratts=otherdgc.getAttributes();
						Rectangle2D otherloc=GraphConstants.getBounds(otheratts);
						if (otherloc!=null){
							otherloc=(Rectangle2D)otherloc.clone();
							otherloc.setRect(otherloc.getX(),otherloc.getY(),
									otherloc.getWidth()+10,otherloc.getHeight()+10);

							if (otherloc.intersects(loc) ){
								dirx=dirx+(otherloc.getCenterX()-loc.getCenterX());
								diry=diry+(otherloc.getCenterY()-loc.getCenterY());
							}

							for (int j=0;j<otherdgc.getChildCount();j++){
								if (gm.isPort(otherdgc.getChildAt(j))){
									Iterator edges = gm.edges(otherdgc.getChildAt(j));
									while (edges.hasNext()){
										DefaultEdge de=(DefaultEdge)edges.next();
										if (!alreadyconsidered.contains(de)){							  
											Map atts=de.getAttributes();

											DefaultGraphCell source=(DefaultGraphCell)((DefaultPort)de.getSource()).getParent();
											DefaultGraphCell target=(DefaultGraphCell)((DefaultPort)de.getTarget()).getParent();

											if (source!=dgc && target!=dgc){

												Map sourceat=source.getAttributes();							   
												Map targetat=target.getAttributes();
												Rectangle2D locporto = GraphConstants.getBounds(sourceat);
												Rectangle2D locportt=GraphConstants.getBounds(targetat);
												if (locporto!=null && locportt!=null &&
														loc.intersectsLine(
																locporto.getCenterX(),
																locporto.getCenterY(),
																locportt.getCenterX(),
																locportt.getCenterY())){
													double midx=(locportt.getCenterX()+locporto.getCenterX())/2;
													double midy=(locportt.getCenterY()+locporto.getCenterY())/2;

													dirx=dirx+(midx-loc.getCenterX());
													diry=diry+(midy-loc.getCenterY());
												}
											}
											alreadyconsidered.add(de);
										}

									}
								}
							}
						}
					}
				} 		   
			}

			vectors.put(dgc,new Point((int)dirx,(int)diry));
		}
		Enumeration enumeration=vectors.keys();
		Hashtable newCoords=new Hashtable();
		while (enumeration.hasMoreElements()){
			DefaultGraphCell dgc=(DefaultGraphCell)  enumeration.nextElement();
			Point p=(Point)vectors.get(dgc);
			double module=Math.sqrt(p.x*p.x+p.y*p.y);
			if (module!=0){
				Map m=dgc.getAttributes();
				Rectangle2D loc=GraphConstants.getBounds(m);
				if (loc!=null){
					double x=loc.getX()-(p.x/module)*5;
					double y=loc.getY()-(p.y/module)*5;
					double w=loc.getWidth();
					double h=loc.getHeight();
					if (x<0) x=1;
					if (y<0) y=1;
					Rectangle nrect=new Rectangle((int)x,(int)y,(int)w,(int)h);
					GraphConstants.setBounds(m,nrect);

					newCoords.put(dgc,m);
				}
			}
		}	  
		this.ids.editor.getGraph().getModel().edit(newCoords,null,null,null);  
	}

}
