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

import java.awt.Point;
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

public class Graphics {
  String smallIcon;
  String bigIcon;
  Point image;
  Hashtable properties = new Hashtable(); ;
  Hashtable content = new Hashtable();

  public Graphics(Node n) {
    NodeList nl = n.getChildNodes();
    for (int k = 0; k < nl.getLength(); k++) {
      Node current = nl.item(k);
      if (current.getNodeName().equalsIgnoreCase("small-icon")) {
        smallIcon = current.getChildNodes().item(0).getNodeValue();
      }
      if (current.getNodeName().equalsIgnoreCase("normal-icon")) {
        bigIcon = current.getChildNodes().item(0).getNodeValue();
      }

      if (current.getNodeName().equalsIgnoreCase("layout")) {
        NodeList nlcurrent = current.getChildNodes();
        for (int j = 0; j < nl.getLength(); j++) {
          Node cc = nlcurrent.item(j);
          if (cc != null) {
            if (cc.getNodeName().equalsIgnoreCase("image")) {
              NamedNodeMap nnm;
              nnm = cc.getAttributes();
            }

            if (cc.getNodeName().equalsIgnoreCase("properties")) {
              NodeList properties = cc.getChildNodes();
              for (int l = 0; l < properties.getLength(); l++) {
                Node property = properties.item(l);
                if (property.getNodeName().equalsIgnoreCase("property")) {
                  NamedNodeMap nnm;
                  nnm = property.getAttributes();
                  if (nnm != null) {
                    int x =0;
//                       Integer.parseInt(nnm.getNamedItem("x").getNodeValue());
                    int y =0;
//                        Integer.parseInt(nnm.getNamedItem("y").getNodeValue());
                    String id = nnm.getNamedItem("id").getNodeValue();
                    this.properties.put(id, new Point(x, y));
                  }
                }
              }
            }
            if (cc.getNodeName().equalsIgnoreCase("views")) {
              NodeList properties = cc.getChildNodes();
              for (int l = 0; l < properties.getLength(); l++) {
                Node property = properties.item(l);
                if (property.getNodeName().equalsIgnoreCase("content")) {
                  String contStr = property.getChildNodes().item(0).getNodeValue();
                  String id=property.getAttributes().getNamedItem("id").getNodeValue();
                  content.put(id,contStr);
                }
              }

            }

          }

        }
      }

    }
  }

  public Hashtable getContent(){
    return content;
  }

  public Hashtable getProperties() {
    return this.properties;
  }

  public String getSmallIcon() {
    return smallIcon;
  }

  public String getBigIcon() {
    return bigIcon;
  }

  public Point getImage() {
    return image;
  }

  public Point getLabel(String id) {
    return (Point) properties.get(id);
  }

}
