
/** 
 * Copyright (C) 2010  Jorge J. Gomez-Sanz sobre código original de Rubén Fuentes
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
import java.io.OutputStreamWriter;
import java.io.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;
import java.util.Map;
import java.util.Hashtable;
import java.util.ArrayList;
import javax.xml.parsers.*;
import org.jgraph.JGraph;
import org.jgraph.graph.*;
import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import ingenias.editor.entities.*;
import ingenias.exception.*;
import ingenias.editor.cell.*;
import ingenias.editor.*;



public abstract class RelationshipSaveAbs {
  public RelationshipSaveAbs() {
  }



  public  void saveRelationships(RelationshipManager rm, GraphManager gm,
                                       OutputStreamWriter fos) throws IOException {
    Enumeration enumeration = rm.getRelationships(gm);
    fos.write("<relationships>\n");
    OutputStreamWriter tempOutput = null;
    ObjectSave objsave=new ObjectSave();
    while (enumeration.hasMoreElements()) {
    	java.io.ByteArrayOutputStream byteStore=new java.io.ByteArrayOutputStream();
      tempOutput = new OutputStreamWriter(byteStore);
      ingenias.editor.entities.NAryEdgeEntity en = (NAryEdgeEntity) enumeration.
          nextElement();
      try {
        tempOutput.write( "<relationship id=\"" + en.getId() + 
        		"\" type=\"" +
        		en.getClass().getName() + "\">\n");
        objsave.saveObject(en, tempOutput);
        String[] idEnt = en.getIds();

        for (int k = 0; k < idEnt.length; k++) {
          RoleEntity re = en.getRoleEntity(idEnt[k]);
          if (re != null) {
            tempOutput.write( ("<role idEntity=\"" + 
            		ingenias.generator.util.Conversor.replaceInvalidChar(en.getEntity(idEnt[k]).getId()) + "\" class=\"" +
                               ingenias.generator.util.Conversor.replaceInvalidChar(en.getClass(idEnt[k])) + "\" roleName=\"" +
                               en.getRole(idEnt[k]) + "\" type=\"" +
                               re.getClass().getName() + "\" dgcid=\""+idEnt[k]+"\">\n"));
            try {
              saveRole(re, tempOutput);
            }
            catch (Exception e2) {
              e2.printStackTrace();

            }

            tempOutput.write("</role>");
          } else {
            System.err.println("WARNING!!!!!!!!!!!!! role for "+idEnt[k]+" is missing in "+en.getId());
            Log.getInstance().log("WARNING!!!!!!!!!!!!! role for "+idEnt[k]+" is missing in "+en.getId());
          }
        }
        tempOutput.write( ("</relationship>\n"));
        tempOutput.flush();
        fos.write(byteStore.toString());
     

      }
      catch (Exception e1) {
        e1.printStackTrace();
      }

    }
    fos.write("</relationships>\n");
  }

  abstract protected void saveRole(ingenias.editor.entities.RoleEntity en,
                               OutputStreamWriter tempOutput) throws
      IOException ;
  

 

}

