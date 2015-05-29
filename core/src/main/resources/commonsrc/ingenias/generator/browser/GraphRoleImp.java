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
import ingenias.exception.NullEntity;

public class GraphRoleImp extends AttributedElementImp implements GraphRole {

 private RoleEntity re;

 private Entity player;
 private ModelJGraph graph;
 private IDEState ids;

 public GraphRoleImp(RoleEntity re,Entity player, ModelJGraph graph, IDEState ids){
   super(re,graph,ids);
   this.re=re;
   this.player=player;
   this.graph=graph;
   this.ids=ids;
   if (ids==null)
		throw new RuntimeException("The ids parameter cannot be null");
 }
 
 public String getID() {
		return this.re.getId();
	}

  public GraphEntity getPlayer() throws NullEntity{
    return new GraphEntityImp(player,graph,ids);
  }


  public String getName(){
    return re.getType().substring(0,re.getType().length()-4);
  }
  
  public void setPlayer(Entity e){
   this.player=e;
  }

  public boolean equals(Object obj){
    if (obj instanceof GraphRoleImp){
      return  getName().equals(((GraphRoleImp)obj).getName()) && 
    		  ((GraphRoleImp)obj).player.equals(player);
    } else
     return super.equals(obj);
  }

@Override
public int hashCode() {	
	return getName().hashCode();
}

public String toString(){ 
	return getName()+":"+re.toString();
}

  
  

}