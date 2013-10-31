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


public class FunctionGenerator {

	/**
	 *  Default constructor.
	 */
	public FunctionGenerator() {

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
	public void parse(String source) throws IOException, SAXException {
		DOMParser parser = new DOMParser();
		//  Parse the Document
		//  and traverse the DOM

		//	parser.setIncludeIgnorableWhitespace(false);
		InputSource sr = new InputSource(source);

		parser.parse(sr);

		// Obtain the document.
		Document doc = parser.getDocument();

		Vector qr = new Vector();

		NodeList nl = doc.getElementsByTagName("program");

		for (int k = 0; k < nl.getLength(); k++) {
			Node n = nl.item(k);

			this.analiseNode(n,0);
		}


	}


        public void analiseNode(Node n, int indent){
         if (n.getNodeName().equalsIgnoreCase("repeat")){
            NamedNodeMap nnm;
            nnm = n.getAttributes();
            String id = nnm.getNamedItem("id").getNodeValue();
            for (int k=0;k<indent;k++) System.out.print("  ");
            System.out.println("repeat "+id.toUpperCase());
            NodeList vars=n.getChildNodes();
            for (int k=0;k<vars.getLength();k++){
             Node current=vars.item(k);
             this.analiseNode(current, indent+1);
            }
         } else

         if (n.getNodeName().equalsIgnoreCase("v")){
          for (int k=0;k<indent;k++) System.out.print("  ");
          String text=n.getChildNodes().item(0).getNodeValue();
          System.out.println("v:"+text);
         } else {
            NodeList vars=n.getChildNodes();
            for (int k=0;k<vars.getLength();k++){
             Node current=vars.item(k);
             this.analiseNode(current, indent);
            }
         }


        }


 public static void main(String args[]) throws Exception{
  FunctionGenerator fg=new FunctionGenerator();
  fg.parse("d:/inged/templates/template.xml");
 }



}

