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
import java.util.Map;
import java.util.Hashtable;
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
public class DiagramChangesManager
    implements org.jgraph.event.GraphModelListener {

  public final int COL_SEPARATOR = 5;
  public static boolean enabled = true;
  JGraph graph;
  private static Hashtable userObjects = new Hashtable();
  private Object workingObject = null;
  private boolean duringChange = false;
  private int cont=0;
  private int cont1=0;
  Vector eventprocessors = new Vector();

  private static void updateTable(GraphModelEvent gme) {
    if (gme.getChange().getInserted() != null) {
      for (int k = 0; k < gme.getChange().getInserted().length; k++) {
        userObjects.put( ( (DefaultGraphCell) gme.getChange().getInserted()[k]).
                        getUserObject(), gme.getChange().getInserted()[k]);
      }
    }
    if (gme.getChange().getRemoved() != null) {
      for (int k = 0; k < gme.getChange().getRemoved().length; k++) {
        userObjects.remove( ( (DefaultGraphCell) gme.getChange().getRemoved()[
                             k]).getUserObject());
      }
    }
  }


  public static Enumeration getUserObjects() {
    return userObjects.keys();
  }

  public static DefaultGraphCell getCellFromUserObject(Object obj) {
    return (DefaultGraphCell) userObjects.get(obj);
  }

  public DiagramChangesManager(JGraph graph) {
    this.graph = graph;
    //this.addEventProcessor(new ResizeRelationships(graph));
    //this.addEventProcessor(new ChangeEntityLocation(null));
    this.addEventProcessor(new CenterRelationships(graph));

  }

  public void enableAutomaticLayout(){
    this.enabled=true;
  }

  public void disableAutomaticLayout(){
    this.enabled=false;
  }


  public void addEventProcessor(org.jgraph.event.GraphModelListener gme) {
    this.eventprocessors.add(gme);
  }

  public void graphChanged(org.jgraph.event.GraphModelEvent gme) {
    this.updateTable(gme);

     if (enabled &&
        this.workingObject == null) {

      try {
        workingObject = "hola";

        for (int k = 0; k < eventprocessors.size(); k++) {

          org.jgraph.event.GraphModelListener gml =
              (org.jgraph.event.GraphModelListener) eventprocessors.elementAt(
              k);
          gml.graphChanged(gme);
        }
        workingObject = null;

      }
      catch (Throwable th) {
        th.printStackTrace();
      }
    }
  }

}