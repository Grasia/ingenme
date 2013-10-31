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

public class Relationship {
	/*  private Vector source=new Vector();
    private Vector target=new Vector();*/
	private Vector roles = new Vector();

	private String id;
	private String inherits;
	Hashtable properties = new Hashtable();
	private ingenias.idegen.Graphics graphics;
	Vector preferredOrder=new Vector();

	public Relationship(Node n) {
		NamedNodeMap nnm;
		nnm = n.getAttributes();
		id = nnm.getNamedItem("id").getNodeValue();
		if (nnm.getNamedItem("inherits") != null) {
			inherits = nnm.getNamedItem("inherits").getNodeValue();
		}
		NodeList entities = n.getChildNodes();
		for (int j = 0; j < entities.getLength(); j++) {
			Node entity = entities.item(j);
			if (entity.getNodeName().equalsIgnoreCase("properties")) {
				NodeList properties = entity.getChildNodes();
				for (int l = 0; l < properties.getLength(); l++) {
					Node property = properties.item(l);
					if (property.getNodeName().equalsIgnoreCase("property")) {
						ingenias.idegen.Property p = new ingenias.idegen.Property(
								property);
						this.properties.put(p.getId(), p);
					}
					if (property.getNodeName().equalsIgnoreCase("preferredorder")) {
						NodeList preferred = property.getChildNodes();
						for (int m = 0; m < preferred.getLength(); m++) {
							Node currentp = preferred.item(m);
							if (currentp.getNodeName().equalsIgnoreCase("order")) {
								this.preferredOrder.add(currentp.getFirstChild().getNodeValue());
							}
						}
					}

				}

			}

			if (entity.getNodeName().equalsIgnoreCase("graphics")){
				graphics=new ingenias.idegen.Graphics(entity);
			}

			if (entity.getNodeName().equalsIgnoreCase("roles")) {
				NodeList roleList = entity.getChildNodes();
				for (int k = 0; k < roleList.getLength(); k++) {
					Node role = roleList.item(k);
					if (role.getNodeName().equalsIgnoreCase("role")) {
						nnm = role.getAttributes();
						String id = nnm.getNamedItem("id").getNodeValue();
						String type = nnm.getNamedItem("type").getNodeValue();
						String mincard = nnm.getNamedItem("mincard").getNodeValue();
						String maxcard = nnm.getNamedItem("maxcard").getNodeValue();
						Vector spair = new Vector();
						spair.add(id);
						if (maxcard.equalsIgnoreCase("n")) {
							maxcard = "" + Integer.MAX_VALUE;
						}
						spair.add(mincard);
						spair.add(maxcard);
						roles.add(spair);
						/*             if (type.equalsIgnoreCase("source"))
                          source.add(spair);
                         else
                          target.add(spair);*/
					}
				}
			}
		}

	}

	public Vector getPreferredOrder(){
		return this.preferredOrder;
	}

	public Vector getRoles() {
		return roles;
	}

	/*public Vector getTarget(){
   return target;
     }*/

	public String getId() {
		return id;
	}

	public String getInherits() {
		return inherits;
	}

	public Hashtable getProperties() {
		return properties;
	}

	public Graphics getGraphics() {
		return graphics;
	}
}
