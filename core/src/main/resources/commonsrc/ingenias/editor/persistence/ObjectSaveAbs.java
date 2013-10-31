
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

package ingenias.editor.persistence;
import java.lang.reflect.*;
import javax.swing.tree.*;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.InputSource;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
//import java.awt.image.*;
import java.io.*;
//import java.awt.*;
//import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
//import javax.swing.event.*;
//import javax.swing.*;
//import java.awt.event.*;
//import java.awt.Rectangle;
//import java.awt.Color;
//import java.net.URL;
import java.util.*;
//import javax.swing.*;
//import javax.swing.event.UndoableEditEvent;
import javax.xml.parsers.*;

import org.jgraph.JGraph;
import org.jgraph.graph.*;
//import org.jgraph.event.*;
//import java.util.Vector;
//import org.jgraph.event.*;
//import org.jgraph.plaf.basic.*;
import org.w3c.dom.*;

import ingenias.editor.entities.*;
import ingenias.exception.*;
import ingenias.editor.cell.*;
import ingenias.editor.*;

abstract public class ObjectSaveAbs {

  public abstract void saveObject(ingenias.editor.entities.Entity en, OutputStreamWriter fos) throws
      IOException;
  

  public static void saveMap(Hashtable ht, OutputStreamWriter fos) throws IOException {
    fos.write("<mapproperties>\n");
    Enumeration keys = ht.keys();
    while (keys.hasMoreElements()) {
      String key = keys.nextElement().toString();

       fos.write( ("<key id=\"" + key + "\">" + 
    		   ingenias.generator.util.Conversor.replaceInvalidChar(ht.get(key).toString()) +
                 "</key>\n"));

    }
    fos.write("</mapproperties>\n");
  }


  public static void saveObjects(ObjectManager om, GraphManager gm, OutputStreamWriter fos) throws
      IOException {
	  fos.write("<objects>\n");

    Vector v = om.getAllObjects();
    Enumeration enumeration = v.elements();
    ObjectSave objsave=new ObjectSave();
    while (enumeration.hasMoreElements()) {
      ingenias.editor.entities.Entity en = (ingenias.editor.entities.Entity)
          enumeration.nextElement();
      try {
    	  objsave.saveObject(en, fos);
      }
      catch (Exception e) {
        e.printStackTrace();
      }

    }
    fos.write("</objects>\n");
  }


  public static void main(String[] args) {
    ObjectSave objectSave1 = new ObjectSave();
  }

}
