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

public class Role {

  private String id;
  private String inherits;
  private Vector validPlayers = new Vector();
  Hashtable properties = new Hashtable();
  Vector preferredOrder = new Vector();
String toString="";

  public Role(Node n) {
    NamedNodeMap nnm;
    nnm = n.getAttributes();
    id = nnm.getNamedItem("id").getNodeValue();
    if (nnm.getNamedItem("inherits") != null) {
      inherits = nnm.getNamedItem("inherits").getNodeValue();
    }
    if (nnm.getNamedItem("toString")!=null)
			toString = nnm.getNamedItem("toString").getNodeValue();

    NodeList entities = n.getChildNodes();
    for (int j = 0; j < entities.getLength(); j++) {
      Node entity = entities.item(j);
      if (entity.getNodeName().equalsIgnoreCase("validplayers")) {
        NodeList players = entity.getChildNodes();
        for (int k = 0; k < players.getLength(); k++) {
          Node player = players.item(k);
          if (player != null && player.getNodeName().equalsIgnoreCase("player")) {

            nnm = player.getAttributes();
            String id = nnm.getNamedItem("id").getNodeValue();
            System.err.println("adding " + id);
            this.validPlayers.add(id);
          }
        }
      }

      if (entity.getNodeName().equalsIgnoreCase("properties")) {
        NodeList properties = entity.getChildNodes();
        for (int m = 0; m < properties.getLength(); m++) {
          Node property = properties.item(m);

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

    }
  }

  public Vector getPreferredOrder(){
    return this.preferredOrder;
  }


  public String getId() {
    return id;
  }

  public String getInherits() {
    return inherits;
  }

  public Vector getPlayers() {
    return this.validPlayers;
  }

  public Hashtable getProperties() {
    return properties;
  }

	public String getToString(){
		return toString;
	}
}
