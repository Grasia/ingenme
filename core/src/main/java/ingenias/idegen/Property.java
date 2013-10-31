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

public class Property {
 String id;
 private String type;
 String preferredwidget;
 boolean ismetaclassinstance;
 boolean ismetamodelinstance;
 boolean iscollection;
 Vector defaultValues=new Vector();

  public Property(Node n) {

   NamedNodeMap nnm;
   nnm = n.getAttributes();
   id = nnm.getNamedItem("id").getNodeValue();
   type=nnm.getNamedItem("type").getNodeValue();
   ismetaclassinstance=nnm.getNamedItem("ismetaclassinstance")!=null &&
nnm.getNamedItem("ismetaclassinstance").getNodeValue().equalsIgnoreCase("yes");
   ismetamodelinstance=nnm.getNamedItem("ismetamodelinstance")!=null &&
nnm.getNamedItem("ismetamodelinstance").getNodeValue().equalsIgnoreCase("yes");
   iscollection=nnm.getNamedItem("iscollection")!=null &&
nnm.getNamedItem("iscollection").getNodeValue().equalsIgnoreCase("yes");
   if (nnm.getNamedItem("preferredwidget")!=null)
    preferredwidget=nnm.getNamedItem("preferredwidget").getNodeValue();
   defaultValues=parseDefaultValues(n);
  }

  private Vector parseDefaultValues(Node n){
    NodeList nl=n.getChildNodes();
    Vector result=new Vector();
    for (int k=0;k<nl.getLength();k++){
      Node defaultValues=nl.item(k);
      if ((defaultValues!=null) &&
defaultValues.getNodeName().equalsIgnoreCase("defaultvalues")){
       NodeList nl1=defaultValues.getChildNodes();
       for (int j=0;j<nl1.getLength();j++){
         Node current=nl1.item(j);
         if (current!=null && current.getNodeName().equalsIgnoreCase("value")){
           result.add(current.getChildNodes().item(0).getNodeValue());
         }
       }

      }
    }
    return result;
  }

  public String getId(){
   return id;
  }

 public String getType(){
  return type;
 }

 public boolean getIsmetaclassinstance(){
  return this.ismetaclassinstance;
 }

 public boolean getIsmetamodelinstance(){
 return this.ismetamodelinstance;
 }

 public boolean getIscollection(){
 return this.iscollection;
 }

 public String getPreferredwidget(){
 return this.preferredwidget;
 }

 public Vector getDefaultValues(){
   return this.defaultValues;
 }
}
