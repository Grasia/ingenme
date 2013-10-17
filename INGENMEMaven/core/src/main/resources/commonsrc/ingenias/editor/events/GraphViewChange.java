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
import java.util.*;
import java.io.*;
import ingenias.editor.*;

import org.jgraph.event.GraphLayoutCacheEvent;
import org.jgraph.graph.*;



public class GraphViewChange implements   org.jgraph.event.GraphLayoutCacheListener {
  private GraphModel m;
  private Object working=null;

  public GraphViewChange(GraphModel m) {
    this.m=m;
  }

  public void update(Observable obs,Object arg1){

  }

public void graphLayoutCacheChanged(GraphLayoutCacheEvent e) {
	// TODO Auto-generated method stub
	
}

/*  private synchronized void notifyChanges(Object arg){
/*    working=arg;
    Object[] os=((GraphView.GraphViewEdit)arg).getChanged();
    boolean found=false;
    int k=0;
    while (k<os.length){
      found=os[k] instanceof VertexView;
      if (found){
       m.fireChange1(os[k]);
      }
      k++;
    }
    working=null;
  }

  public boolean equals(Object obj){
    return obj.getClass().equals(this.getClass());
  }*/

}