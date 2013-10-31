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

import ingenias.generator.interpreter.*;
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
import ingenias.generator.datatemplate.*;

public class HelpGenerator {
  private Hashtable objects = new Hashtable();
  private Hashtable metamodels = new Hashtable();
  private Hashtable roles = new Hashtable();
  private Hashtable relationships = new Hashtable();
  /**
   *  Default constructor.
   */
  public HelpGenerator() {

  }

  // <init>()

  /**
   *  Description of the Method
   *
   *@param  source            Description of Parameter
   *@return                   Description of the Returned Value
   *@exception  IOException   Description of Exception
   *@exception  SAXException  Description of Exception
   */
  public Sequences parse(String source) throws IOException, SAXException {
    DOMParser parser = new DOMParser();
    //  Parse the Document
    //  and traverse the DOM

    //	parser.setIncludeIgnorableWhitespace(false);
    InputSource sr = new InputSource(source);

    parser.parse(sr);

    // Obtain the document.
    Document doc = parser.getDocument();

    Vector qr = new Vector();

    NodeList nl = doc.getElementsByTagName("meta-models");
    nl = nl.item(0).getChildNodes();
    // Read symbols
    for (int k = 0; k < nl.getLength(); k++) {
      Node n = nl.item(k);

      if (n.getNodeName().equalsIgnoreCase("objects")) {
        this.extractObjects(n.getChildNodes());
      }

      if (n.getNodeName().equalsIgnoreCase("metamodel")) {
        Metamodel mm = new Metamodel(n);
        this.metamodels.put(mm.getId(), mm);
      }

      if (n.getNodeName().equalsIgnoreCase("relationship")) {
        Relationship mm = new Relationship(n);
        this.relationships.put(mm.getId(), mm);
      }

      if (n.getNodeName().equalsIgnoreCase("role")) {
        Role mm = new Role(n);
        this.roles.put(mm.getId(), mm);
      }

    }

    // Generate symbols
    Sequences seq=new Sequences();
    Enumeration enumeration = objects.elements();
    while (enumeration.hasMoreElements()) {
      ingenias.idegen.Object object = (ingenias.idegen.Object) enumeration.
          nextElement();
      this.generateObjectsCode(object,seq);
    }

/*    enumeration = relationships.elements();
    while (enumeration.hasMoreElements()) {
      Relationship relationship = (Relationship) enumeration.nextElement();
      result = result + this.generateRelationshipCode(relationship);
    }

    enumeration = metamodels.elements();
    while (enumeration.hasMoreElements()) {
      Metamodel metamodel = (Metamodel) enumeration.nextElement();
      result = result + this.generateMetamodelCode(metamodel);
    }

    result = result + "</sequences>";*/
    return seq;
  }

  /**
   *  Description of the Method
   *
   *@param  nl  Description of Parameter
   *@return     Description of the Returned Value
   */
  private void extractObjects(NodeList nl) throws SAXException {

    for (int k = 0; k < nl.getLength(); k++) {
      Node n = nl.item(k);
      if (n.getNodeName().equalsIgnoreCase("object")) {
        ingenias.idegen.Object object = new ingenias.idegen.Object(n);
        this.objects.put(object.getId(), object);
      }
    }

  }


  private void generateObjectsCode(ingenias.idegen.Object object,
ingenias.generator.datatemplate.Sequences seq) {
    if (object.getGraphics()!=null){
    Repeat rep=new Repeat("objects");
    seq.addRepeat(rep);
    System.err.println("--------OBJECT:"+object);
    rep.add(new Var("object",object.getId()));
    rep.add(new Var("description",object.getDescription()));
    rep.add(new Var("recomm",object.getRecomm()));
    rep.add(new Var("image",object.getGraphics().getBigIcon()));

    }
  }



  public static void main(String args[]) throws Exception {
  HelpGenerator og = new HelpGenerator();
    Sequences result = og.parse("meta-modelo.xml");

//    System.out.println(result);
    if (args.length != 0) {
      String file = args[0];
      Codegen.applyArroba(result.toString(), new FileInputStream(file));
    }

  }

  private String capitalize(String pname) {
    return pname.substring(0, 1).toUpperCase() +
        pname.substring(1, pname.length());
  }

}
