
/** 
 * Copyright (C) 2010  Jorge J. Gomez-Sanz, Ruben Fuentes
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


package ingenias.editor;

import org.jgraph.graph.*;
import javax.swing.event.*;
import org.jgraph.event.*;
import ingenias.editor.events.*;

public class ObservableModel extends DefaultGraphModel  implements java.io.Serializable {

  public ObservableModel() {
  }
  public void addUndoableEditListener(UndoableEditListener parm1) {
    /**@todo Implement this org.jgraph.graph.GraphModel abstract method*/
    super.addUndoableEditListener(parm1);
  }
  public void removeUndoableEditListener(UndoableEditListener parm1) {
    /**@todo Implement this org.jgraph.graph.GraphModel abstract method*/
    super.removeUndoableEditListener(parm1);
  }


}