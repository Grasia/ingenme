
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

import ingenias.editor.GraphManager;
import ingenias.editor.IDEState;
import ingenias.editor.Log;
import ingenias.editor.ModelJGraph;
import ingenias.editor.entities.Entity;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jgraph.graph.DefaultGraphCell;

public class ConvertUtils {
	
	 public static Entity convert(IDEState ids, String objectid,String objecttype,Class newClassType)
     throws java.lang.NoSuchMethodException,
      java.lang.reflect.InvocationTargetException,
      java.lang.IllegalAccessException,
      java.lang.InstantiationException
 {

  DefaultMutableTreeNode oldnode=ids.om.findNodeInTree(ids.om.getRoot(),objectid,objecttype);
  Log.getInstance().log("Finding "+objectid+":"+objecttype +oldnode);
  Entity oldent=(Entity)oldnode.getUserObject();

  java.lang.reflect.Constructor cons=newClassType.getConstructor(new Class[] {String.class});
  Entity newent=(Entity)cons.newInstance(new Object[]{oldent.getId()});
  Log.getInstance().log("Replacing with "+objectid+":"+newent.getType());
  ids.om.transferFields(oldent,newent);
  ids.om.removeEntity(oldent);
  try {
    DefaultMutableTreeNode entnode = (DefaultMutableTreeNode)(ids.om.getClass().getField(newent.getType() + "Node").get(ids.om));
    DefaultMutableTreeNode nn = new DefaultMutableTreeNode(newent);
    entnode.insert(nn, entnode.getChildCount());
    nn.setParent(entnode);
  }catch (Exception e){
    e.printStackTrace();
    Log.getInstance().log(e.getMessage());
  }

//  oldnode.setUserObject(newent);
  Vector v=ids.om.getAllObjects();
  ids.om.replaceReferencesOM(v,oldent,newent);
  replaceCellObjects(ids,oldent,newent);

  //this.updateCellViews();
  ids.om.reload();
  Log.getInstance().log("Finished");
  return newent;
 };

 private static void replaceCellObjects(IDEState ids,Entity oldent, Entity newent){
  Vector graphs=ids.gm.getUOModels();
  Enumeration enumeration=graphs.elements();
  while (enumeration.hasMoreElements()){
   ModelJGraph mjg=(ModelJGraph)enumeration.nextElement();
   for (int k=0;k<mjg.getModel().getRootCount();k++){
     if (mjg.getModel().getRootAt(k) instanceof DefaultGraphCell){
       DefaultGraphCell dgc=(DefaultGraphCell)mjg.getModel().getRootAt(k);
       if (dgc.getUserObject().equals(oldent)){
         dgc.setUserObject(newent);
       } else {
/*          if (dgc.getUserObject() instanceof ingenias.editor.entities.NAryEdgeEntity){
           NAryEdgeEntity ne=(NAryEdgeEntity)dgc.getUserObject();
           ne.setObject(oldent,newent); // changes the object iif it exists
         }*/
       }
     } else {
      // System.err.println(mjg.getModel().getRootAt(k).getClass());
     }

   }
   mjg.repaint();
   mjg.revalidate();

  /* for (int k=0;k<mjg.getGraphLayoutCache().getRoots().length;k++){
     System.err.println(mjg.getGraphLayoutCache().getRoots()[k].getClass());
   }*/

  }
 }
}
