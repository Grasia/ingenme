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

package ingenias.idegen;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.w3c.dom.*;
import org.apache.xerces.parsers.*;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.apache.xerces.dom.*;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.InputSource;

import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.ParserAdapter;
import org.xml.sax.helpers.ParserFactory;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.apache.xerces.parsers.SAXParser;

public class Metamodel {
  Vector<String> objects = new Vector<String>();
  Vector relationships = new Vector();
  Vector preferredOrder=new Vector();
  Hashtable<String,Boolean> isMain=new  Hashtable<String,Boolean>();
  String id;
  String icon="";
  Hashtable properties = new Hashtable();
  String code="";

  public Metamodel(Node n) {
    NamedNodeMap nnm;
    nnm = n.getAttributes();
    id = nnm.getNamedItem("id").getNodeValue();
    if (nnm.getNamedItem("icon")!=null)
     icon = nnm.getNamedItem("icon").getNodeValue();
    NodeList nl = n.getChildNodes();

    for (int k = 0; k < nl.getLength(); k++) {
      Node current = nl.item(k);
      if (current.getNodeName().equalsIgnoreCase("objects")) {
        NodeList objects = current.getChildNodes();
        for (int j = 0; j < objects.getLength(); j++) {
          Node object = objects.item(j);
          if (object.getNodeName().equalsIgnoreCase("object")) {
            nnm = object.getAttributes();
            String id = nnm.getNamedItem("id").getNodeValue();
            if (nnm.getNamedItem("ismain")!=null)
             isMain.put(id, nnm.getNamedItem("ismain").getNodeValue().equalsIgnoreCase("true"));
            else 
            	isMain.put(id, true);
            this.objects.add(id);
          }
        }
      }

	if (current.getNodeName().equalsIgnoreCase("code")) {
	
	NodeList texts = current.getChildNodes();
        for (int j = 0; j < texts.getLength(); j++) {
          Node text = texts.item(j);
	 this.code=this.code+text.getNodeValue();
	 }
	
	}

      if (current.getNodeName().equalsIgnoreCase("properties")) {
        NodeList properties = current.getChildNodes();
        for (int j = 0; j < properties.getLength(); j++) {
          Node property = properties.item(j);

          if (property.getNodeName().equalsIgnoreCase("property")) {
            ingenias.idegen.Property p = new ingenias.idegen.Property(
                property);
            this.properties.put(p.getId(), p);
          }
          if (property.getNodeName().equalsIgnoreCase("preferredorder")) {
            NodeList preferred = property.getChildNodes();
            for (int l = 0; l < preferred.getLength(); l++) {
              Node currentp = preferred.item(l);
              if (currentp.getNodeName().equalsIgnoreCase("order")) {
                this.preferredOrder.add(currentp.getFirstChild().getNodeValue());
              }
            }
          }

        }
      }

      if (current.getNodeName().equalsIgnoreCase("relationships")) {
        NodeList relationships = current.getChildNodes();
        for (int j = 0; j < relationships.getLength(); j++) {
          Node relationship = relationships.item(j);
          if (relationship.getNodeName().equalsIgnoreCase("relationship")) {
            nnm = relationship.getAttributes();
            String id = nnm.getNamedItem("id").getNodeValue();
            //System.err.println("------------- Adding " + id);
            this.relationships.add(id);
          }
        }

      }
    }
  }

  public Vector getPreferredOrder(){
    return this.preferredOrder;
  }

   public String getCode(){
	return code;
   }


  public Vector getObjects() {
    return objects;
  }

  public Vector getRelationships() {
    return relationships;
  }

  public String getId() {
    return id;
  }

  public String getIcon(){
    return icon;
  }
  
  public boolean isMain(String objid){
	  return !isMain.containsKey(objid) || isMain.get(objid); 
  }

  public Hashtable getProperties() {
    return properties;
  }
}
