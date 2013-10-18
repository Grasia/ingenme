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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.w3c.dom.*;
import org.apache.xerces.parsers.*;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import org.w3c.dom.*;
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
import ingenias.idegen.*;

public class ObjectsGenerator {
	private Hashtable<String,ingenias.idegen.Object> objects = new Hashtable<String,ingenias.idegen.Object>();
	private Hashtable metamodels = new Hashtable();
	private Hashtable roles = new Hashtable();
	private Hashtable relationships = new Hashtable();

	/**
	 *  Default constructor.
	 */
	public ObjectsGenerator() {


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
	public String parse(String source, String directory) throws IOException, SAXException,
	Exception {
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
		/*NodeList nlobjects = doc.getElementsByTagName("object");
    this.extractObjects(nlobjects);*/
		nl = nl.item(0).getChildNodes();
		// Read symbols
		for (int k = 0; k < nl.getLength(); k++) {
			Node n = nl.item(k);

			if (n.getNodeName().equalsIgnoreCase("object")) {
				ingenias.idegen.Object object = new ingenias.idegen.Object(n);
				this.objects.put(object.getId(), object);
			}	

			/*if (n.getNodeName().equalsIgnoreCase("objects")) {
        this.extractObjects(n.getChildNodes());
      }*/

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
		String result = "<sequences>";
		Enumeration enumeration = objects.elements();
		while (enumeration.hasMoreElements()) {
			ingenias.idegen.Object object = (ingenias.idegen.Object) enumeration.
					nextElement();
			result = result + this.generateObjectsCode(object);
		}

		enumeration = relationships.elements();
		while (enumeration.hasMoreElements()) {
			Relationship relationship = (Relationship) enumeration.nextElement();
			result = result + this.generateRelationshipCode(relationship);
		}

		enumeration = metamodels.elements();
		while (enumeration.hasMoreElements()) {
			Metamodel metamodel = (Metamodel) enumeration.nextElement();
			result = result + this.generateMetamodelCode(metamodel);
		}
		result=result+"<v id=\"jadeproject\">"+directory+"</v>";
		
		result = result + "</sequences>";
		return result;
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

	private String generateRelationshipCode(ingenias.idegen.Relationship
			relationship) throws Exception {

		String result = "";
		String resultRoles = "";

		result = "<repeat id=\"relationshipedges\">\n";
		result = result + " <v id=\"relationship\">" + relationship.getId() +"</v>\n";

		Enumeration enumeration = relationship.getPreferredOrder().elements();
		while (enumeration.hasMoreElements()) {
			result = result + "<repeat id=\"preferredorder\">";
			result = result + "<v id=\"order\">" +
					enumeration.nextElement().toString() + "</v>\n";
			result = result + "</repeat>";
		}

		Graphics graphics = relationship.getGraphics();
		if (relationship.getGraphics() != null) {
			result = result + "<repeat id=\"graphobject\">";
			result = result + " <v id=\"relationship\">" + relationship.getId() +
					"</v>\n";
			result = result + " <v id=\"normal_icon\">" +
					relationship.getGraphics().getBigIcon() + "</v>\n";
			result = result + " <v id=\"small_icon\">" +
					relationship.getGraphics().getSmallIcon() + "</v>\n";
			enumeration = relationship.getGraphics().getProperties().keys();

			/*			Hashtable properties = relationship.getProperties();
			enumeration = properties.elements();

			while (enumeration.hasMoreElements()) {
				ingenias.idegen.Property property = (ingenias.idegen.Property) enumeration.
				nextElement();
				System.err.println("rel code for "+relationship.getId()+" generating "+property.getId()+"\n\n"+result);
				String relAttrCode=generateCodeAttributes(property);
				result = result + relAttrCode;
				System.err.println("rel code for "+relationship.getId()+"\n\n"+result);
			}*/

			while (enumeration.hasMoreElements()) {
				String pname = enumeration.nextElement().toString();
				java.awt.Point value = (java.awt.Point) relationship.getGraphics().
						getProperties().get(pname);
				result = result + " <repeat id=\"propertieslayout\">\n";
				result = result + "  <v id=\"prop\">" +
						pname.substring(0, 1).toUpperCase() +
						pname.substring(1, pname.length()) + "</v>\n";
				result = result + "  <v id=\"xp\">" + value.x + "</v>\n";
				result = result + "  <v id=\"yp\">" + value.y + "</v>\n";
				result = result + "  </repeat>\n";
			}
			/*      result = result + "  <v id=\"xi\">" +
                relationship.getGraphics().getImage().x +
                "</v>";
            result = result + "  <v id=\"yi\">" +
                relationship.getGraphics().getImage().y +
                "</v>";*/
			Hashtable views = relationship.getGraphics().getContent();
			enumeration = views.keys();
			while (enumeration.hasMoreElements()) {
				String viewid = enumeration.nextElement().toString();
				result = result + "<repeat id=\"views\">";
				result = result + "  <v id=\"content\">" +
						views.get(viewid).toString() +
						"</v>";
				result = result + "  <v id=\"viewid\">" +
						viewid +
						"</v>";

				result = result + "</repeat>\n";
			}

			enumeration = relationship.getProperties().keys();
			while (enumeration.hasMoreElements()) {
				String pname = enumeration.nextElement().toString();
				Property value = (Property) relationship.getProperties().get(pname);
				result = result + " <repeat id=\"graphobjectatts\">\n";
				pname = value.getId().substring(0, 1).toUpperCase() +
						value.getId().substring(1, value.getId().length());
				result = result + "  <v id=\"fieldid\">" + pname + "</v>\n";
				result = result + "  </repeat>\n";
			}

			Hashtable properties = relationship.getProperties();
			enumeration = properties.elements();

			while (enumeration.hasMoreElements()) {
				ingenias.idegen.Property property = (ingenias.idegen.Property) enumeration.
						nextElement();
				String relAttrCode=generateCodeAttributes(property);
				result = result + relAttrCode;
			}
			result = result + "</repeat>\n";

		}

		Hashtable properties = relationship.getProperties();
		enumeration = properties.elements();

		while (enumeration.hasMoreElements()) {
			ingenias.idegen.Property property = (ingenias.idegen.Property) enumeration.
					nextElement();
			String relAttrCode=generateCodeAttributes(property);
			result = result + relAttrCode;
		}

		/*    Object[] spair=(Object[])(relationship.getSource().firstElement());
        String source=spair[0].toString();
        String smincard=spair[1].toString();
        String smaxcard=spair[2].toString();
        result=result+"   <v id=\"role\">"+source+"</v>\n";
        result=result+"   <v id=\"smincard\">"+smincard+"</v>\n";
        result=result+"   <v id=\"smaxcard\">"+smaxcard+"</v>\n";
        Role rs=(Role)this.roles.get(source);
        Enumeration enumeration=rs.getPlayers().elements();
        while (enumeration.hasMoreElements()){
          String player=enumeration.nextElement().toString();
          result=result+"  <repeat id=\"sourceplayer\">\n";
          result=result+"   <v id=\"player\">"+player+"</v>\n";
          result=result+"  </repeat>";
        }
        result=result+"  </repeat>";*/

		Enumeration tenumeration = relationship.getRoles().elements();
		while (tenumeration.hasMoreElements()) {

			Vector rpair = (Vector) tenumeration.nextElement();
			String target = rpair.elementAt(0).toString();
			String tmincard = rpair.elementAt(1).toString();
			String tmaxcard = rpair.elementAt(2).toString();

			resultRoles = resultRoles + "  <repeat id=\"roles\">\n";

			resultRoles = resultRoles + "  <v id=\"role\">" + target + "</v>\n";			
			try {
				if (this.roles.get(target)==null){
					System.err.println("Could not find a role named "+target);
					throw new Exception("Could not find a role named "+target);
				} else {
					Enumeration enumeration2 = ( (Role)this.roles.get(target)).getPreferredOrder().
							elements();
					while (enumeration2.hasMoreElements()) {
						resultRoles = resultRoles + "<repeat id=\"preferredorder\">";
						resultRoles = resultRoles + "<v id=\"order\">" +
								enumeration2.nextElement().toString() + "</v>\n";
						resultRoles = resultRoles + "</repeat>";
					}
				}
			}
			catch (Exception t) {
				System.err.println("error processing " + target);
				throw t;
			}

			Hashtable properties1 = ( (Role)this.roles.get(target)).getProperties();
			Enumeration enumeration1 = properties1.elements();

			while (enumeration1.hasMoreElements()) {
				ingenias.idegen.Property property = (ingenias.idegen.Property)
						enumeration1.nextElement();
				resultRoles = resultRoles + this.generateCodeAttributes(property);
			}

			resultRoles = resultRoles + "</repeat>";

			result = result + "  <repeat id=\"relationshiproles\">\n";

			result = result + "  <v id=\"role\">" + target + "</v>\n";
			result = result + "   <v id=\"mincard\">" + tmincard + "</v>\n";
			result = result + "   <v id=\"maxcard\">" + tmaxcard + "</v>\n";

			Role rt = (Role)this.roles.get(target);
			if (rt == null) {
				System.err.println("error obteniendo " + target);
			}
			enumeration = rt.getPlayers().elements();
			while (enumeration.hasMoreElements()) {
				String player = enumeration.nextElement().toString();
				result = result + "  <repeat id=\"roleplayer\">\n";
				result = result + "   <v id=\"player\">" + player + "</v>\n";
				result = result + "  </repeat>";
			}
			result = result + " </repeat>";
		}

		result = result + "</repeat>\n" + resultRoles + "\n";

		return result;
	}

	private String generateObjectsCode(ingenias.idegen.Object object) {
		String result = "";
		result = "<repeat id=\"objects\">";
		result = result + " <v id=\"object\">" + object.getId() + "</v>\n";
		System.err.println(object.getId());
		result = result + " <v id=\"keyfield\">" + this.capitalize(object.getKey()) +
				"</v>\n";
		if (!object.getToString().equals(""))
			result = result + " <v id=\"toString\">" + object.getToString() +"</v>\n";
		else 
			result = result + " <v id=\"toString\">get" + this.capitalize(object.getKey()) +"()</v>\n";
		result = result + " <v id=\"desc\">" +
				ingenias.generator.util.Conversor.
				replace(object.getDescription(), "\n", "&lt;br&gt;") +
				"</v>\n";
		result = result + " <v id=\"rec\">" +
				ingenias.generator.util.Conversor.
				replace(object.getRecomm(), "\n", "&lt;br&gt;") +
				"</v>\n";

		Enumeration enumeration = object.getPreferredOrder().elements();
		while (enumeration.hasMoreElements()) {
			result = result + "<repeat id=\"preferredorder\">";
			result = result + "<v id=\"order\">" +
					enumeration.nextElement().toString() + "</v>\n";
			result = result + "</repeat>";
		}

		if (object.getInherits() == null ||
				object.getInherits().trim().equalsIgnoreCase("")) {
			result = result + " <v id=\"parent\">Entity</v>\n";
		}
		else {
			result = result + " <v id=\"parent\">" + object.getInherits() + "</v>\n";

		}
		Hashtable properties = object.getProperties();
		enumeration = properties.elements();

		while (enumeration.hasMoreElements()) {
			ingenias.idegen.Property property = (ingenias.idegen.Property) enumeration.
					nextElement();
			result = result + generateCodeAttributes(property);
		}

		result = result + "</repeat>\n";

		if (object.getGraphics() != null) {
			result = result + "<repeat id=\"graphobject\">";

			properties = object.getProperties();
			enumeration = properties.elements();
			while (enumeration.hasMoreElements()) {
				ingenias.idegen.Property property =
						(ingenias.idegen.Property) enumeration.   nextElement();
				result = result + generateCodeAttributes(property);
			}

			result = result + " <v id=\"object\">" + object.getId() + "</v>\n";
			result = result + " <v id=\"normal_icon\">" +
					object.getGraphics().getBigIcon() + "</v>\n";
			result = result + " <v id=\"small_icon\">" +
					object.getGraphics().getSmallIcon() + "</v>\n";
			enumeration = object.getGraphics().getProperties().keys();

			while (enumeration.hasMoreElements()) {
				String pname = enumeration.nextElement().toString();
				java.awt.Point value = (java.awt.Point) object.getGraphics().
						getProperties().get(pname);
				result = result + " <repeat id=\"propertieslayout\">\n";
				result = result + "  <v id=\"prop\">" +
						pname.substring(0, 1).toUpperCase() +
						pname.substring(1, pname.length()) + "</v>\n";
				result = result + "  <v id=\"xp\">" + value.x + "</v>\n";
				result = result + "  <v id=\"yp\">" + value.y + "</v>\n";
				result = result + "  </repeat>\n";
				System.err.println("looking "+object.getId()+" "+pname);
				Property gproperty=this.findProperty(object,pname);
				if (gproperty!=null)
					result = result + generateCodeAttributes(gproperty);


			}
			/*      result = result + "  <v id=\"xi\">" + object.getGraphics().getImage().x +
                "</v>";
       result = result + "  <v id=\"yi\">" + object.getGraphics().getImage().y +
                "</v>";*/
			Hashtable views = object.getGraphics().getContent();
			enumeration = views.keys();
			while (enumeration.hasMoreElements()) {
				String viewid = enumeration.nextElement().toString();
				result = result + "<repeat id=\"views\">";
				result = result + "  <v id=\"content\">" +
						views.get(viewid).toString() +
						"</v>";
				result = result + "  <v id=\"viewid\">" +
						viewid +
						"</v>";

				result = result + "</repeat>\n";
			}

			enumeration = object.getProperties().keys();
			while (enumeration.hasMoreElements()) {
				String pname = enumeration.nextElement().toString();
				Property value = (Property) object.getProperties().get(pname);
				result = result + " <repeat id=\"graphobjectatts\">\n";
				pname = value.getId().substring(0, 1).toUpperCase() +
						value.getId().substring(1, value.getId().length());
				result = result + "  <v id=\"fieldid\">" + pname + "</v>\n";
				result = result + "  </repeat>\n";


			}

			if (object.getInstanciable()) {
				result = result + " <repeat id=\"insertentities\">\n";
				result = result + "  <v id=\"object\">" + object.getId() + "</v>\n";
				result = result + " </repeat>\n";
			}

			result = result + "</repeat>\n";

		}

		result = result + "<repeat id=\"objecthierarchynodes\">\n";
		result = result + " <v id=\"object\">" + object.getId() + "</v>\n";
		result = result + "</repeat>\n";

		if (object.getInstanciable()) {
			result = result + "<repeat id=\"createhierarchyobject\">\n";
			result = result + " <v id=\"object\">" + object.getId() + "</v>\n";
			result = result + "</repeat>\n";
		}

		if (object.getInherits() == null || object.getInherits().equals("")) {
			result = result + "<repeat id=\"objecttophierarchynodes\">\n";
			result = result + " <v id=\"object\">" + object.getId() + "</v>\n";
			result = result + "</repeat>\n";
		}

		if (object.getInherits() != null && !object.getInherits().equals("")) {
			result = result + "<repeat id=\"objectmiddlehierarchynodes\">\n";
			result = result + " <v id=\"object\">" + object.getId() + "</v>\n";
			result = result + " <v id=\"parent\">" + object.getInherits() + "</v>\n";
			result = result + "</repeat>\n";
		}

		return result;
	}

	private String generateSingleType(Property property) {
		String result = "";
		String pname = property.getId();
		String pid = pname.substring(0, 1).toUpperCase() +
				pname.substring(1, pname.length());

		if (property.getIsmetamodelinstance() || property.getIsmetaclassinstance()) {
			result = result + "  <repeat id=\"entityatts\">\n";
			result = result + "    <v id=\"name\">" + pid + "</v>\n";
			result = result + "  </repeat>\n";
		}

		result = result + " <repeat id=\"attributes\">\n";
		result = result + "  <v id=\"name\">" + pid + "</v>\n";
		if (property.getIsmetamodelinstance()) {
			result = result + "  <v id=\"type\">ingenias.editor.entities." +
					property.getType() + "ModelEntity</v>\n";
		}
		else {
			if (property.getIsmetaclassinstance()) {
				result = result + "  <v id=\"type\">ingenias.editor.entities." +
						property.getType() + "</v>\n";
			}
			else {
				result = result + "  <v id=\"type\">" + property.getType() + "</v>\n";
			}
		}
		result = result + " </repeat>\n";

		result = result + " <repeat id=\"methods\">\n";
		if (property.getIsmetamodelinstance()) {
			result = result + "  <v id=\"type\">" + property.getType() +
					"ModelEntity</v>\n";
		}
		else
			if (property.getIsmetaclassinstance()) {
				result = result + "  <v id=\"type\">ingenias.editor.entities." +
						property.getType() + "</v>\n";
			}
			else {
				result = result + "  <v id=\"type\">" + property.getType() + "</v>\n";
			}
		result = result + "  <v id=\"name\">" + pid + "</v>\n";
		result = result + " </repeat>\n";

		if (!property.getIsmetaclassinstance() && !property.getIsmetamodelinstance() && !property.getIscollection()) {
			System.err.println("Es simple........"+property.getId());
			result = result + " <repeat id=\"simpleattributes\">\n";
			result=result+"  <v id=\"type\">"+property.getType()+"</v>\n";
			result = result + "  <v id=\"name\">" + pid + "</v>\n";
			if (property.getPreferredwidget() == null) {
				result = result +
						"  <v id=\"widget\">ingenias.editor.widget.CustomJTextField</v>\n";
			}
			else {
				result = result + "  <v id=\"widget\">" + property.getPreferredwidget() +
						"</v>\n";
			}
			Enumeration enumeration2 = property.getDefaultValues().elements();
			while (enumeration2.hasMoreElements()) {
				String value = enumeration2.nextElement().toString();
				result = result + " <repeat id=\"defaultvalues\">\n";
				result = result + "  <v id=\"value\">" + value + "</v>\n";
				result = result + " </repeat>\n";
			}

			result = result + " </repeat>\n";
		}

		return result;
	}

	private String generateCollectionType(Property property) {
		String result = "";
		String pname = property.getId();
		String pid = pname.substring(0, 1).toUpperCase() +
				pname.substring(1, pname.length());

		if (property.getIsmetamodelinstance() || property.getIsmetaclassinstance()) {
			result = result + "  <repeat id=\"collectionentityatts\">";
			result = result + "    <v id=\"name\">" + pid + "</v>\n";
			result = result + "  </repeat>\n";
		}

		result = result + " <repeat id=\"collectionattributes\">\n";
		result = result + "  <v id=\"name\">" + pid + "</v>\n";
		if (property.getIsmetamodelinstance()) {
			result = result + "  <v id=\"type\">ingenias.editor.entities." +
					property.getType() + "ModelEntity</v>\n";
		}
		else {
			if (property.getIsmetaclassinstance()) {
				result = result + "  <v id=\"type\">ingenias.editor.entities." +
						property.getType() + "</v>\n";
			}
			else {
				result = result + "  <v id=\"type\">" + property.getType() + "</v>\n";
			}
		}
		result = result + " </repeat>\n";

		result = result + " <repeat id=\"collectionmethods\">\n";
		if (property.getIsmetamodelinstance()) {
			result = result + "  <v id=\"type\">" + property.getType() +
					"ModelEntity</v>\n";
		}
		else
			if (property.getIsmetaclassinstance()) {
				result = result + "  <v id=\"type\">ingenias.editor.entities." +
						property.getType() + "</v>\n";
			}
			else {
				result = result + "  <v id=\"type\">" + property.getType() + "</v>\n";
			}
		result = result + "  <v id=\"name\">" + pid + "</v>\n";
		result = result + " </repeat>\n";

		if (!property.getIsmetaclassinstance() && !property.getIsmetamodelinstance() && !property.getIscollection()) {
			System.err.println("Ejecución de simple1");
			System.err.println("Ejecución de simple");
			System.err.println("Ejecución de simple");
			System.err.println("Ejecución de simple");
			System.err.println("Ejecución de simple");
			System.err.println("Ejecución de simple");
			System.err.println("Ejecución de simple");
			result = result + " <repeat id=\"simpleattributes\">\n";
			//      result=result+"  <v id=\"type\">"+property.getType()+"</v>\n";
			result = result + "  <v id=\"name\">" + pid + "</v>\n";
			result = result + " </repeat>\n";
		}

		return result;
	}

	private String generateCodeAttributes(Property property) {
		if (property.getIscollection()) {
			return this.generateCollectionType(property);
		}
		else {
			return this.generateSingleType(property);
		}

	}

	private Property findProperty(ingenias.idegen.Object obj,String pname){
		if (obj.getProperties().containsKey(pname))
			return (Property)obj.getProperties().get(pname);
		else {
			if (obj.getInherits()!=null){
				System.err.println("now "+obj.getInherits()+" "+this.objects.get(obj.getInherits())+" "+pname);
				return findProperty((ingenias.idegen.Object)this.objects.get(obj.getInherits()),pname);
			} else
				return null;
		}
	}

	private Vector<ingenias.idegen.Object> findInstantiableGraphicalInheritors(ingenias.idegen.Object obj){
		Vector<ingenias.idegen.Object> result=new Vector<ingenias.idegen.Object>();
		for (ingenias.idegen.Object currentObject:this.objects.values()){
			if (currentObject.getInherits()!=null && currentObject.getInherits().equalsIgnoreCase(obj.getId())){
				if (currentObject.getInstanciable() && currentObject.getGraphics()!=null){
					result.add(currentObject);
				}
				result.addAll(findInstantiableGraphicalInheritors(currentObject));
			}
		}
		return result;
	}



	private String generateMetamodelCode(Metamodel metamodel) throws Exception {
		String result = "";
		try {

			result = "<repeat id=\"meta-model-creation\">\n";
			result = result + "<v id=\"modelid\">" + metamodel.getId() + "</v>\n";
			result = result + "<v id=\"modelicon\">" + metamodel.getIcon() + "</v>\n";
			result = result + "</repeat>";
			result = result + "<repeat id=\"meta-models\">\n";
			result = result + "<v id=\"modelicon\">" + metamodel.getIcon() + "</v>\n";
			result = result + "<v id=\"code\">" + metamodel.getCode() + "</v>\n";

			Hashtable properties = metamodel.getProperties();

			Enumeration enumeration = metamodel.getPreferredOrder().elements();
			while (enumeration.hasMoreElements()) {
				result = result + "<repeat id=\"preferredorder\">";
				result = result + "<v id=\"order\">" +
						enumeration.nextElement().toString() + "</v>";
				result = result + "</repeat>\n";
			}

			enumeration = properties.elements();

			while (enumeration.hasMoreElements()) {
				ingenias.idegen.Property property = (ingenias.idegen.Property) enumeration.
						nextElement();
				result = result + generateCodeAttributes(property);
			}
			enumeration = metamodel.getRelationships().elements();
			while (enumeration.hasMoreElements()) {
				String relat = enumeration.nextElement().toString();
				result = result + "<repeat id=\"createedgeview\">\n";
				result = result + "<v id=\"relationship\">" + relat + "</v>";
				ingenias.idegen.Relationship relationshipobject = (ingenias.idegen.Relationship)this.
						relationships.get(relat);
				Hashtable views = relationshipobject.getGraphics().getContent();
				Enumeration enumeration_1 = views.keys();
				while (enumeration_1.hasMoreElements()) {
					String viewid = enumeration_1.nextElement().toString();
					result = result + "<repeat id=\"views\">";
					result = result + "  <v id=\"content\">" + views.get(viewid).toString() +    "</v>";
					result = result + "  <v id=\"viewid\">" +viewid +"</v>";
					result = result + "</repeat>\n";
				}
				result = result + " </repeat>\n";

				result = result + "<repeat id=\"relationshipsallowed\">\n";
				result = result + "<v id=\"relationship\">" + relat + "</v>";
				result = result + "</repeat>";
				result = result + "<repeat id=\"relationshipsinstance\">\n";
				result = result + "<v id=\"relationship\">" + relat + "</v>";
				result = result + "</repeat>";
			}
			result = result + "<v id=\"modelid\">" + metamodel.getId() + "</v>\n";
			Enumeration objects = metamodel.getObjects().elements();
			HashSet<String> additionalInvisibleTypes=new HashSet<String> ();
			HashSet<String> visibleTypes=new HashSet<String> ();
			while (objects.hasMoreElements()) {
				String objid = objects.nextElement().toString();

				ingenias.idegen.Object object = (ingenias.idegen.Object)this.
						objects.get(objid);
				if (object==null)
					System.err.println(objid+" not found when studying metamodel "+metamodel.getId());
				result = result + " <repeat id=\"vertexviewcreation\">\n";
				try {
					result = result + "  <v id=\"object\">" + object.getId() + "</v>\n";
				}
				catch (Exception ex) {
					System.err.println("error reading " + objid);
					throw ex;
				}

				Hashtable views = object.getGraphics().getContent();
				Enumeration enumeration1 = views.keys();
				while (enumeration1.hasMoreElements()) {
					String viewid = enumeration1.nextElement().toString();
					result = result + "<repeat id=\"views\">";
					result = result + "  <v id=\"content\">" +
							views.get(viewid).toString() +
							"</v>";
					result = result + "  <v id=\"viewid\">" +
							viewid +
							"</v>";
					result = result + "</repeat>\n";
				}
				result = result + " </repeat>\n";

				result = result + " <repeat id=\"agentbuttons\">\n";
				result = result + "  <v id=\"ismain\">true</v>\n";
				result = result + "  <v id=\"object\">" + object.getId() + "</v>\n";
				result = result + "  <v id=\"small_icon\">" +
						object.getGraphics().getSmallIcon() + "</v>\n";
				result = result + " </repeat>\n";

				visibleTypes.add(object.getId());
				Hashtable<String,ingenias.idegen.Property> props = object.getProperties();
				for (ingenias.idegen.Property prop:props.values()){
					if (prop.ismetaclassinstance){
						Object currentType = this.objects.get(prop.getType());
						if (currentType.getInstanciable() && currentType.getGraphics()!=null){
							additionalInvisibleTypes.add(currentType.getId());
						}
						Vector<Object> othercandidates = findInstantiableGraphicalInheritors(currentType);
						for (Object currentObject:othercandidates){
							additionalInvisibleTypes.add(currentObject.getId());
						}
					}
				}								
			}

			// To have some elements admitted by the diagram but that cannot be created through buttons
			// or menu items
			additionalInvisibleTypes.removeAll(visibleTypes);
			for (String additional:additionalInvisibleTypes){
				ingenias.idegen.Object object = (ingenias.idegen.Object)this.
						objects.get(additional);			
				if (object.getInstanciable() && object.getGraphics()!=null){
					if (object==null)
						System.err.println(additional+" not found when studying metamodel "+metamodel.getId());
					result = result + " <repeat id=\"vertexviewcreation\">\n";
					try {
						result = result + "  <v id=\"object\">" + object.getId() + "</v>\n";
					}
					catch (Exception ex) {
						System.err.println("error reading " + additional);
						throw ex;
					}
					if (object.getGraphics()==null)
						System.err.println(object.getId()+" sin descripción....");
					Hashtable views = object.getGraphics().getContent();
					Enumeration enumeration1 = views.keys();
					while (enumeration1.hasMoreElements()) {
						String viewid = enumeration1.nextElement().toString();
						result = result + "<repeat id=\"views\">";
						result = result + "  <v id=\"content\">" +
								views.get(viewid).toString() +
								"</v>";
						result = result + "  <v id=\"viewid\">" +
								viewid +
								"</v>";
						result = result + "</repeat>\n";
					}
					result = result + " </repeat>\n";
					result = result + " <repeat id=\"agentbuttons\">\n";
					result = result + "  <v id=\"ismain\">false</v>\n";
					result = result + "  <v id=\"object\">" + additional + "</v>\n";
					result = result + "  <v id=\"small_icon\"></v>\n";
					result = result + " </repeat>\n";
				}
			}

			result = result + "</repeat>";
		}
		catch (RuntimeException ex) {
			ex.printStackTrace();
			System.err.println(result);
			throw ex;
		}
		return result;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  nl  Description of Parameter
	 *@return     Description of the Returned Value
	 */
	private String extractRelationships(NodeList nl) {
		return "";
	}

	/**
	 *  Description of the Method
	 *
	 *@param  nl  Description of Parameter
	 *@return     Description of the Returned Value
	 */
	private String extractMetamodels(NodeList nl) {
		return "";
	}

	public static void main(String args[]) throws Exception {
		ingenias.editor.Log.initInstance(new PrintWriter(System.out),
				new PrintWriter(System.err));
		if (args.length==0){
			System.err.println("Call this program with the following arguments");
			System.err.println("java ingenias.idegen.ObjectsGenerator path_to_a_metamodelfile [path_to_a_template_file]");
			// instead of doing a System.exit, a runtimeException is created
			// this permits to return an error code. System.exit simply aborts the
			// running JVM and disturbs the original execution
			throw new RuntimeException(new Exception("Wrong invocation")); 
		}
		boolean alreadyAccepted=false;
		char acceptance=' ';
		File acceptanceFile=new File (""+System.getProperty("user.home")+"/.ingened.properties");
		if (System.getProperty("user.home")!=null){
			alreadyAccepted=acceptanceFile.exists();
		}
		String header="\n\n\nINGENME Copyright (C) 2010  Jorge J. Gomez-Sanz\n"+
				"This file is part of the INGENME tool. INGENME is an open source meta-editor\n"+
				"which produces customized editors for user-defined modeling languages\n"+
				"This program comes with ABSOLUTELY NO WARRANTY. This is free software, and you \n" +
				"are welcome to use it according to the following terms :\n\n";
		String toBeShown="";
		if (!alreadyAccepted){



			toBeShown=toBeShown+header;

			StringBuffer licenseContent=new StringBuffer();
			System.err.println(getResourceList("/licenses"));
			InputStream fis=getResourceList("/licenses").get("licenses/LICENSE_EN.txt");
			int read=1;
			while (read!=-1){
				read=fis.read();
				if (read!=-1)
					licenseContent.append((char)read);
			}
			fis.close();
			toBeShown=toBeShown+("\n"+licenseContent);
			/*	String[] lines = toBeShown.split("\n");
			int k=0;
			while (k<lines.length){
				for (int j=k;j<Math.min(k+40,lines.length);j++){
					System.out.println(lines[j]);
				}
				k=Math.min(k+40,lines.length);
				System.out.println("--------------- Press enter to read more --------------");
				read=System.in.read();
			}


			BufferedReader reader= new BufferedReader(new InputStreamReader(System.in) );
			System.out.println("\nPlease, type y or n and then press enter:");
			String linea= reader.readLine();
			while(linea==null || 
					(linea!=null && !linea.toLowerCase().equals("n") && !linea.toLowerCase().equals("y"))) {
				System.out.println("\nPlease, type y or n and then press enter:");
				linea= reader.readLine();
			}
			if (linea.toLowerCase().equals("n"))
				acceptance='N';

			if (linea.toLowerCase().equals("y"))
				acceptance='Y';*/

			JTextPane textArea=new JTextPane();			
			textArea.setText(toBeShown);
			JPanel content=new JPanel();
			content.setLayout(new BorderLayout());
			JScrollPane jsp=new JScrollPane(textArea);
			jsp.setPreferredSize(new Dimension(600,200));
			content.add(jsp, BorderLayout.CENTER);
			content.add(new JLabel("Do you accept this license?"), BorderLayout.SOUTH);		

			int result = JOptionPane.showConfirmDialog(
					null, content, "License acceptance",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (result==JOptionPane.YES_OPTION){
				acceptance='Y';
				acceptanceFile.createNewFile();
			}

		}
		if (acceptance=='Y' || alreadyAccepted){

			System.out.println(header);

			acceptanceFile.createNewFile();
			String metamodelfile=args[0];
			if (!new File(metamodelfile).exists()){
				System.err.println("The metamodel file "+args[0]+" does not exist");
				// instead of doing a System.exit, a runtimeException is created
				// this permits to return an error code. System.exit simply aborts the
				// running JVM and disturbs the original execution
				throw new RuntimeException(new Exception("The metamodel file "+args[0]+" does not exist")); 
			}

			ObjectsGenerator og = new ObjectsGenerator();
			String result = og.parse(metamodelfile,args[1]);
			File tfile = File.createTempFile("objgen", "xml");
			tfile.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(tfile);
			fos.write(result.getBytes());
			fos.close();
			if (args.length >2) {
				String file = args[2];
				if (!new File(file).exists()){
					System.err.println("The template file "+args[1]+" does not exist");
					// instead of doing a System.exit, a runtimeException is created
					// this permits to return an error code. System.exit simply aborts the
					// running JVM and disturbs the original execution
					throw new RuntimeException(new Exception("The template file "+args[1]+" does not exist")); 
				}
				System.err.println(file);
					
				Codegen.applyArroba(result, new FileInputStream(file));
			}
			else {

				String packageName="/templates";
				Hashtable<String, InputStream> inputStreamInFolder = getResourceList(packageName);
				for(String filename:inputStreamInFolder.keySet()){
					if (filename.toLowerCase().endsWith(".xml")){
						System.err.println(filename);
						Codegen.applyArroba(result, inputStreamInFolder.get(filename));
					}
				};
			}

		} else {
			for (Frame f:Frame.getFrames()){ 
				f.dispose();
			}
			// instead of doing a System.exit, a runtimeException is created
			// this permits to return an error code. System.exit simply aborts the
			// running JVM and disturbs the original execution
			throw new RuntimeException(new Exception("Wrong parameters")); 
		}
	}

	
	// based on code from Axel in https://forums.oracle.com/thread/2086378
	public static Hashtable<String,InputStream> getResourceList(String resourceFolder) throws IOException {	
		URL url = ObjectsGenerator.class.getResource(resourceFolder);
		URLConnection uconn = url.openConnection();    	
		Hashtable<String,InputStream> inputStreams=new Hashtable<String,InputStream>();

		if (uconn.getClass().getName().endsWith("FileURLConnection")) {
			File f = new File(url.getFile());
			String[] dir = f.list();
			if (dir != null) {
				for (int i = 0 ; i < dir.length; i++) {
					inputStreams.put(dir[i], uconn.getInputStream());
				}
			}

		} else if (uconn.getClass().getName().endsWith("JarURLConnection")) {
			JarFile jf = ((JarURLConnection)uconn).getJarFile();
			for (Enumeration e = jf.entries() ; e.hasMoreElements() ;) {
				JarEntry je = (JarEntry)e.nextElement();			
				if (je.getName().startsWith(resourceFolder.substring(1,resourceFolder.length()))) { 
					// it is assumed the first character in the resourceFolder variable is a slash "/"			
					inputStreams.put(je.getName(), jf.getInputStream(je));
				}
			}

		}
		return inputStreams;

	}


	// Modified version of 
	// http://stackoverflow.com/questions/6022672/why-does-inputstreamreader-throw-a-npe-when-reading-from-a-jar
	public static List<String> getResourcesInFolder(JarInputStream jarFile, String packageName) throws IOException {
		packageName = packageName.replace(".", "/");
		List<String> classes = new ArrayList<String>();
		try {
			for (JarEntry jarEntry; (jarEntry = jarFile.getNextJarEntry()) != null;) {
				if ((jarEntry.getName().startsWith(packageName)) ) {
					classes.add(jarEntry.getName().replace("/", "."));
				}
			}
		} finally {
			jarFile.close();
		}

		return classes;
	}

	private String capitalize(String pname) {
		return pname.substring(0, 1).toUpperCase() +
				pname.substring(1, pname.length());
	}

}
