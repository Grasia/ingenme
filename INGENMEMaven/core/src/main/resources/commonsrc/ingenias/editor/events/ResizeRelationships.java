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
import java.lang.reflect.InvocationTargetException;
import javax.swing.event.UndoableEditEvent;

/**
 *
 * Avoids that an entity gets outta of the screen.
 * It does so by setting X to 1 when X<0 and Y to 1 when Y<0
 *
 */
public class ResizeRelationships
    implements org.jgraph.event.GraphModelListener,
    javax.swing.event.UndoableEditListener {
  private Object workingObject = null;
  private boolean enabled = true;
  Hashtable classes = new Hashtable();
  Hashtable methods = new Hashtable();
  JGraph graph;

  public ResizeRelationships(JGraph graph) {
    this.graph = graph;
  }

  private Dimension getDimension(NAryEdge ne) throws ClassNotFoundException,
      SecurityException, NoSuchMethodException, InvocationTargetException,
      IllegalArgumentException, IllegalAccessException {
    Dimension result = null;

    String cname = ne.getClass().getName();

    cname = cname.substring(0, cname.length() - 4) + "Renderer";

    java.lang.reflect.Method m;
    java.lang.Class c;
    if (!this.classes.containsKey(cname)) {
      c = Class.forName(cname);
      this.classes.put(cname, c);
    }
    else {
      c = (Class)this.classes.get(cname);

    }
    if (!this.methods.containsKey(cname)) {
      m = Class.forName(cname).getMethod("setEntity",
                                         new Class[] {
                                         ne.getUserObject().getClass()}
                                         );
      this.methods.put(cname, m);
    }
    else {
      m = (java.lang.reflect.Method)this.methods.get(cname);

    }

    m.invoke(
        c,
        new Object[] {
        ne.getUserObject()});
    result = (Dimension) c.getMethod("getSize",
                                     new Class[] {}).invoke(c,
        new Object[] {});

    return result;
  }

  public void graphChanged(org.jgraph.event.GraphModelEvent gme) {

    if (enabled && this.workingObject == null) {
      workingObject = "hello";
      boolean foundEdge = false;
      ingenias.editor.cell.NAryEdge asel = null;
      ingenias.editor.Model model = (ingenias.editor.Model) graph.getModel();
      Hashtable changes = new Hashtable();
      int changesc = 0;
      for (int k = 0; k < model.getRootCount(); k++) {
        foundEdge = NAryEdge.class.isAssignableFrom(
            model.getRootAt(k).getClass());
        if (foundEdge) {

          asel = (NAryEdge) model.getRootAt(k);
          Map m = asel.getAttributes();
          Rectangle bounds = GraphConstants.getBounds(m).getBounds();
          try {
            if (bounds == null) {
              bounds = new Rectangle(0, 0, 0, 0);
            }
            if (!bounds.getSize().equals(this.getDimension(asel))) {
              bounds.setSize(this.getDimension(asel));
              GraphConstants.setBounds(m, bounds);
              changes.put(asel, m);
              changesc++;
            }
          }
          catch (IllegalAccessException ex) {
            ex.printStackTrace();
          }
          catch (IllegalArgumentException ex) {
            ex.printStackTrace();
          }
          catch (InvocationTargetException ex) {
            ex.printStackTrace();
          }
          catch (NoSuchMethodException ex) {
            ex.printStackTrace();
          }
          catch (SecurityException ex) {
            ex.printStackTrace();
          }
          catch (ClassNotFoundException ex) {
            ex.printStackTrace();
          }

        }
      }
      if (changesc > 0) {
        model.edit(changes, null, null, null);
        model.toFront(changes.keySet().toArray());
      }
      workingObject = null;
    }
  }

  public void undoableEditHappened(UndoableEditEvent undoableEditEvent) {
    // this.graphChanged(null);
  }
}
