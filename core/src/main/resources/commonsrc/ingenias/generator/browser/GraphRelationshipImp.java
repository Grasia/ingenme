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

package ingenias.generator.browser;
import java.util.*;
import org.jgraph.graph.*;

import ingenias.editor.IDEState;
import ingenias.editor.ModelJGraph;
import ingenias.editor.entities.*;


public class GraphRelationshipImp extends AttributedElementImp implements GraphRelationship{
	private ingenias.editor.entities.NAryEdgeEntity rel;
	private ModelJGraph graph;
	private org.jgraph.graph.DefaultGraphCell dgc;
	IDEState ids;

	GraphRelationshipImp(ingenias.editor.entities.NAryEdgeEntity rel,ModelJGraph graph, IDEState ids){
		super(rel,graph,ids);
		this.rel=rel;
		this.graph=graph;
		dgc=this.getCell();
		this.ids=ids;
		if (ids==null)
			throw new RuntimeException("The ids parameter cannot be null");
	}
	

	
	public DefaultGraphCell getDGC(){
		return dgc;
	}

	public ingenias.editor.entities.NAryEdgeEntity getNAryEdge(){
		return rel;
	}

	private DefaultGraphCell getCell(){
		int max=graph.getModel().getRootCount();
		Vector v=new Vector();

		boolean found=false;
		int k=0;
		org.jgraph.graph.DefaultGraphCell dgc=null;
		while (k<max &&!found){
			Object o=graph.getModel().getRootAt(k);
			if (o instanceof org.jgraph.graph.DefaultGraphCell){
				dgc=(org.jgraph.graph.DefaultGraphCell)o;
				found=dgc.getUserObject()==rel;
			}
			k++;
		}
		return dgc;
	}

	public String getType(){
		String name=rel.getClass().getName();
		//		int endName=name.lastIndexOf("Enity");
		int startName=name.lastIndexOf(".")+1;
		name=name.substring(startName,name.length());
		return name;

	}

	public Graph getGraph(){
		return new GraphImp(this.graph, ids);
	}

	public GraphRole[] getRoles(){
		String ids[]=rel.getIds();
		Vector result=new Vector();
		for (int k=0;k<ids.length;k++){
			try {
				RoleEntity re=rel.getRoleEntity(ids[k]);
				//				System.err.println("agregado "+ent);
				GraphRoleImp gri = new GraphRoleImp(re, rel.getEntity(ids[k]), graph,this.ids);
				//				if (!.contains(gri))
				result.add(gri);
			} catch (ingenias.exception.NotFound nf){
				nf.printStackTrace();
			}

		}
		GraphRole[] groles=new GraphRole[result.size()];
		result.toArray(groles);
		return groles;

		/*  int max=graph.getModel().getRootCount();

		 Vector v=new Vector();

		 if (dgc.getChildren()==null)
		 return new GraphRole[0];

		 Iterator ports=dgc.getChildren().iterator();
		 while (ports.hasNext()){
		 Object port=ports.next();
		 Iterator it=graph.getModel().edges(port);
		 while (it.hasNext()){
		 org.jgraph.graph.DefaultEdge current=
		 (org.jgraph.graph.DefaultEdge)it.next();
		 RoleEntity re=(RoleEntity)current.getUserObject();
		 String roleName=re.getClass().getName();
		 int index=roleName.lastIndexOf(".");
		 roleName=roleName.substring(index+1,roleName.length()-4);
		 //    System.err.println("role name" +roleName);
		 */
		/*      for (int j=0;j<this.rel.getIds().length;j++){
		 //        System.err.println(this.rel.getIds()[j]);
		  }*/
		/*     try {
		 Entity ent = this.rel.getPlayer(roleName);
		 //      System.err.println("agregado "+ent);
		  GraphRoleImp gri = new GraphRoleImp(re, ent, graph);
		  if (!v.contains(gri))
		  v.add(gri);
		  } catch (ingenias.exception.NotFound nf){
		  nf.printStackTrace();
		  }
		  }
		  }

		  GraphRole[] result=new GraphRole[v.size()];
		  for (int k=0;k<result.length;k++){
		  result[k]=(GraphRole)v.elementAt(k);
		  }
		  return result;*/
	}

	public int hashCode(){
		return rel.getId().hashCode();
	}

	public boolean equals(Object o){

		if (o instanceof GraphRelationshipImp)

			return ((GraphRelationshipImp)o).rel.getId().equals(this.rel.getId());
		return false;

	}

	public String toString(){
		return this.rel.getType();
	}

	public String getID() {
		return this.rel.getId();
	}

	@Override
	public GraphRole[] getRoles(String roleName) {
		GraphRole[] roles = getRoles();
		int k=0;
		Vector<GraphRole> rolesFound=new Vector<GraphRole>(); 
		for (GraphRole role:roles){ 
			if (role.getName().equalsIgnoreCase(roleName)){
				rolesFound.add(role);
			}
		}
		return rolesFound.toArray(new GraphRole[rolesFound.size()]);
	}

}