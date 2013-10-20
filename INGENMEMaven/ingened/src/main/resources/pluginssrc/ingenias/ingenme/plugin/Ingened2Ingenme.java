
/** 
 * Copyright (C) 2010  Jorge J. Gomez-Sanz
 * 
 * This file is part of the INGENME tool. INGENME is an open source meta-editor
 * which produces customized editors for user-defined modeling languages
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 **/

package ingenias.ingenme.plugin;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ingenias.editor.Log;
import java.awt.Frame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;


import ingenias.editor.ProjectProperty;
import ingenias.editor.export.Diagram2SVG;
import ingenias.exception.NullEntity;
import ingenias.generator.browser.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import ingenias.exception.InvalidEntity;
import ingenias.exception.InvalidGraph;
import ingenias.exception.NotFound;
import ingenias.exception.NotInitialised;
import ingenias.exception.NullEntity;
import ingenias.generator.browser.AttributedElement;
import ingenias.generator.browser.Browser;
import ingenias.generator.browser.BrowserImp;
import ingenias.generator.browser.Graph;
import ingenias.generator.browser.GraphAttribute;
import ingenias.generator.browser.GraphAttributeFactory;
import ingenias.generator.browser.GraphCollection;
import ingenias.generator.browser.GraphEntity;
import ingenias.generator.browser.GraphEntityFactory;
import ingenias.generator.browser.GraphFactory;
import ingenias.generator.browser.GraphRelationship;
import ingenias.generator.browser.GraphRole;
import ingenias.generator.datatemplate.Sequences;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;



class FileCopy {
	public static void main(String[] args) {
		try {
			copy("fromFile.txt", "toFile.txt");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public static File copy(InputStream from, String toFileName)
			throws IOException {

		File toFile = new File(toFileName);

		System.out.println("AbsolutePath toFile: "+ toFile.getAbsolutePath());


		if (toFile.isDirectory())
			throw new IOException("Target file "+toFileName+" is a directory when a file was expected");

		if (toFile.exists()) {
			if (!toFile.canWrite())
				throw new IOException("FileCopy: "
						+ "destination file is unwriteable: " + toFileName);
		} else {
			String parent = toFile.getParent();
			if (parent == null)
				parent = System.getProperty("user.dir");
			File dir = new File(parent);
			if (!dir.exists())
				throw new IOException("FileCopy: "
						+ "destination directory doesn't exist: " + parent);
			if (dir.isFile())
				throw new IOException("FileCopy: "
						+ "destination is not a directory: " + parent);
			if (!dir.canWrite())
				throw new IOException("FileCopy: "
						+ "destination directory is unwriteable: " + parent);
		}


		FileOutputStream to = null;
		try {  
			to = new FileOutputStream(toFile);
			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1)
				to.write(buffer, 0, bytesRead); // write
		} finally {
			if (from != null)
				try {
					from.close();
				} catch (IOException e) {
					;
				}
			if (to != null)
				try {
					to.close();
				} catch (IOException e) {
					;
				}
		}
		return toFile;
	}

	public static File copy(String fromFileName, String toFileName)
			throws IOException {
		File fromFile = new File(fromFileName);
		File toFile = new File(toFileName);

		System.out.println("AbsolutePath fromFile: "+ fromFile.getAbsolutePath());
		System.out.println("AbsolutePath toFile: "+ toFile.getAbsolutePath());


		if (!fromFile.exists())
			throw new IOException("FileCopy: " + "no such source file: "
					+ fromFileName);
		if (!fromFile.isFile())
			throw new IOException("FileCopy: " + "can't copy directory: "
					+ fromFileName);
		if (!fromFile.canRead())
			throw new IOException("FileCopy: " + "source file is unreadable: "
					+ fromFileName);

		if (toFile.isDirectory())
			toFile = new File(toFile, fromFile.getName());

		if (toFile.exists()) {
			if (!toFile.canWrite())
				throw new IOException("FileCopy: "
						+ "destination file is unwriteable: " + toFileName);

		} else {
			String parent = toFile.getParent();
			if (parent == null)
				parent = System.getProperty("user.dir");
			File dir = new File(parent);
			if (!dir.exists())
				throw new IOException("FileCopy: "
						+ "destination directory doesn't exist: " + parent);
			if (dir.isFile())
				throw new IOException("FileCopy: "
						+ "destination is not a directory: " + parent);
			if (!dir.canWrite())
				throw new IOException("FileCopy: "
						+ "destination directory is unwriteable: " + parent);
		}

		FileInputStream from = null;
		FileOutputStream to = null;
		try {
			from = new FileInputStream(fromFile);
			to = new FileOutputStream(toFile);
			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1)
				to.write(buffer, 0, bytesRead); // write
		} finally {
			if (from != null)
				try {
					from.close();
				} catch (IOException e) {
					;
				}
			if (to != null)
				try {
					to.close();
				} catch (IOException e) {
					;
				}
		}
		return toFile;
	}
}

public class Ingened2Ingenme extends ingenias.editor.extension.BasicToolImp {

	private String lastValue="";
	private Vector<String> iconsToMove=new Vector<String>();
	private int k;
	private GraphEntityFactory gef;
	private GraphFactory gfact;
	private Graph tmpdiagram;
	private GraphAttributeFactory atfact;
	private String folder="";


	/**
	 *  Initialises the class with a file containing a INGENIAS specification
	 *
	 *@param  file           Path to file containing INGENIAS specification
	 *@exception  Exception  Error accessing any file or malformed XML exception
	 */

	public Ingened2Ingenme(String file, String folder) throws Exception {
		super(file);
		this.folder=folder;
	}

	/**
	 *  Initialises the class giving access to diagrams in run-time
	 **/

	public Ingened2Ingenme(Browser browser) throws Exception {
		super(browser);
	}

	@Override
	public String getVersion() {
		return "@modingened2ingenme.ver@";
	}


	/**
	 *  Gets the description of this module
	 *
	 *@return    The description
	 */
	public String getDescription() {
		return "This translates the modeling language specification into the GOPRR-like xml format required by INGENME";
	}


	/**
	 *  Gets the name of this module
	 *
	 *@return    The name
	 */
	public String getName() {
		return "INGENME translator";
	}

	private boolean error=false;
	/**
	 *  It opens the different files generated under the ingenias/jade/components folder looking
	 *  for specific tags. These tags mark the beginning and the end of the modification
	 */
	public void run() {
		try {
			gef=new GraphEntityFactory(browser.getState()); 
			gfact=new GraphFactory(browser.getState());
			atfact=new GraphAttributeFactory(browser.getState());


			iconsToMove.clear();
			final StringBuffer output=new StringBuffer();
			Vector<String> errors=new Vector<String>(); 
			Vector<GraphRelationship> binaryRelationships = processEdges(output, errors);
			processEntities(output, errors,binaryRelationships);


			if (errors.isEmpty()) {
				String saveLocation=saveMetamodel(output);
				String projectHome = new File(saveLocation).getParentFile().getParent();
				for (String icon:iconsToMove){
					File iconFile = new File(icon);
					try {
						FileCopy.copy(icon, projectHome+"/images/"+iconFile.getName());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else
			{
				/*	javax.swing.JOptionPane.showMessageDialog(getResources().getMainFrame(), "There are errors in the metamodel. Check the editor panels","Error",javax.swing.JOptionPane.ERROR_MESSAGE);*/
				ingenias.editor.Log.getInstance().logERROR("There were errors processing the metamodel");					
				int k=0;
				for (String errorEntry:errors){
					ingenias.editor.Log.getInstance().logERROR(k+":"+errorEntry);						
					k++;
				}
				error=true;
			}
		}
		catch (NotFound ex) {
			ex.printStackTrace();
		}
		catch (NullEntity ex) {
			ex.printStackTrace();
		}

	}

	private Vector<GraphRelationship> processEdges(StringBuffer output, Vector<String> errors) throws NotFound, NullEntity {
		Vector<GraphRelationship> relationships=new Vector<GraphRelationship>();
		Vector<GraphRelationship> binaryRelationships=new Vector<GraphRelationship>();

		Graph[] graphs=getBrowser().getGraphs();

		for (Graph g:graphs){
			GraphRelationship[] grels=g.getRelationships();
			for (GraphRelationship grel:grels){
				relationships.add(grel);
			}
		};

		for (GraphRelationship grel:relationships){
			if (grel.getType().equalsIgnoreCase("BinaryRel")){
				// build fake metarel
				binaryRelationships.add(grel);
				String relName=grel.getID();
				String rolSourceName=relName+"source";
				String rolTargetName=relName+"target";
				output.append("<relationship id=\""+relName+"\" >\n");					
				appendProperties(errors,output,grel,grel.getID());
				appendVisualRepresentationRelationship(errors,output,grel);
				output.append("<roles>\n");
				boolean oneSource=false;
				boolean oneTarget=false;
				Vector<GraphRole> sources=new Vector<GraphRole>();
				Vector<GraphRole> targets=new Vector<GraphRole>();
				Hashtable<GraphRole,String> mincardsource=new Hashtable<GraphRole,String>();
				Hashtable<GraphRole,String> mincardtarget=new Hashtable<GraphRole,String>();
				Hashtable<GraphRole,String> maxcardsource=new Hashtable<GraphRole,String>();
				Hashtable<GraphRole,String> maxcardtarget=new Hashtable<GraphRole,String>();

				output.append("<role id=\""+rolSourceName+"\" type=\"source\"" +
						" mincard=\"1\" " +
						"maxcard=\"1\"/>\n");
				output.append("<role id=\""+rolTargetName+"\" type=\"target\"" +
						" mincard=\"1\" " +
						"maxcard=\"1\"/>\n");

				output.append("</roles>\n");
				output.append("</relationship>\n");

				// Build fake roles
				GraphRole[] groles=grel.getRoles();
				String playerSource="";
				String playerTarget="";

				for (GraphRole grole:groles){
					if (grole.getName().equalsIgnoreCase("BinaryRelsource")){
						try {
							playerSource=grole.getPlayer().getID();
						} catch (NullEntity e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (grole.getName().equalsIgnoreCase("BinaryReltarget")){
						try {
							playerTarget=grole.getPlayer().getID();
						} catch (NullEntity e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				output.append("<role id=\""+rolSourceName+"\" >\n");
				output.append("<properties/>\n");					
				output.append("<graphics><small-icon/><normal-icon/></graphics>\n");
				// empty properties
				output.append("<validplayers>\n");
				output.append("<player id=\""+playerSource+"\"/>\n");					
				output.append("</validplayers>\n");
				output.append("</role>\n");

				output.append("<role id=\""+rolTargetName+"\" >\n");
				output.append("<properties/>\n");					
				output.append("<graphics><small-icon/><normal-icon/></graphics>\n");
				// empty properties
				output.append("<validplayers>\n");
				output.append("<player id=\""+playerTarget+"\"/>\n");
				output.append("</validplayers>\n");
				output.append("</role>\n");


			}
		}	
		return binaryRelationships;
	}

	private void processEntities(final StringBuffer output,
			Vector<String> errors, Vector<GraphRelationship> binaryRelationships) throws NotFound, NullEntity {
		GraphEntity[] entities = browser.getAllEntities();
		for (GraphEntity ge:entities){
			if (ge.getType().equalsIgnoreCase("MetaDiagram")){
				// generate diagram entry
				GraphEntity basicrepre=obtainBasicRepresentation(errors, ge);
				String smalliconPath=null;

				if (basicrepre==null){
					smalliconPath="images/m"+ge.getID().replace(' ', '_').replace(':', '_')+".png";
					new File("target/images").mkdirs();
					File smallicon=new File("target/"+smalliconPath);
					inventIcon(ge, smallicon);
					iconsToMove.add(smallicon.getAbsolutePath());
				} else {
					if (basicrepre.getAttributeByName("SmallIcon")==null){
						errors.add("smallicon field of entity "+basicrepre.getID()+" is empty");
					}else {
						smalliconPath=basicrepre.getAttributeByName("SmallIcon").getSimpleValue();						
					}
				}
				output.append("<metamodel id=\""+ge.getID()+"\" name=\"\" icon=\""+smalliconPath+"\">\n");
				appendProperties(errors,output,ge);					
				output.append("<code id=\"constructor\">\n");
				output.append("<![CDATA[");
				output.append("ToolTipManager.sharedInstance().registerComponent(this);\n");
				output.append("this.getModel().addGraphModelListener(new ChangeNARYEdgeLocation(this));\n");
				output.append("this.getModel().addGraphModelListener(new ChangeEntityLocation(this));\n");
				output.append("]]>\n");
				output.append("</code>\n");
				GraphEntity[] connectedElements = getRelatedElements(ge, "Contains", "Containstarget");
				if (connectedElements==null || connectedElements.length==0)
					errors.add("metadiagram "+ge.getID()+" does not have objects or relationships associated");
				else  {
					output.append("<objects>");
					for (int k=0;k<connectedElements.length;k++){
						if (connectedElements[k].getType().equalsIgnoreCase("MetaObject")){
							output.append("<object id=\""+connectedElements[k].getID()+"\"/>");
						}
					}
					output.append("</objects>");
					output.append("<relationships>");
					for (int k=0;k<connectedElements.length;k++){
						if (connectedElements[k].getType().equalsIgnoreCase("MetaRelationship")){
							output.append("<relationship id=\""+connectedElements[k].getID()+"\"/>");
						}
					}
					for (GraphRelationship binaryRel:binaryRelationships){
						output.append("<relationship id=\""+binaryRel.getID()+"\"/>");
					}
					output.append("</relationships>");
				}
				output.append("</metamodel>\n");

			}
			if (ge.getType().equalsIgnoreCase("MetaObject")){
				if (ge.getAttributeByName("Instantiable")==null||ge.getAttributeByName("Instantiable").getSimpleValue().equalsIgnoreCase(""))
					errors.add("Instantiable field in entity "+ge.getID()+" is empty"); 
				else
					if (ge.getAttributeByName("Package")==null)
						errors.add("Package field in entity "+ge.getID()+" is empty");
					else{
						String keyfield="id";
						if (ge.getAttributeByName("Keyfield")!=null)
							try {

								keyfield=ge.getAttributeByName("Keyfield").getEntityValue().getID();
							} catch (NullEntity ne){
								keyfield="id";
							}
						Hashtable<GraphEntity, GraphRole> inheriting = getRelatedElementsHashtable(ge,"InheritsO", "InheritsOtarget");
						Set<GraphEntity> inheritingSet = inheriting.keySet();
						inheritingSet.remove(ge);

						if (inheritingSet.size()!=0){
							if (inheritingSet.size()>1){
								errors.add("Entity "+ge.getID()+" inherits from more than one entity. Only simple inheritance is permitted.");
							} else 
								output.append("<object id=\""+ge.getID()+"\" instanciable=\""+
										ge.getAttributeByName("Instantiable").getSimpleValue()+"\" package=\""+
										ge.getAttributeByName("Package").getSimpleValue()+"\"  keyfield=\""+keyfield+"\" " +
										"inherits=\"" +inheritingSet.iterator().next().getID()+"\" "+
										">\n");
						} else

							output.append("<object id=\""+ge.getID()+"\" instanciable=\""+
									ge.getAttributeByName("Instantiable").getSimpleValue()+"\" package=\""+
									ge.getAttributeByName("Package").getSimpleValue()+"\"  keyfield=\""+keyfield+"\" " +				
									">\n");


						output.append("<description>\n");
						if (ge.getAttributeByName("Description")==null)
							errors.add("The description field of "+ge.getID()+" is empty");
						else
							output.append(ge.getAttributeByName("Description").getSimpleValue());
						output.append("</description>\n");
						output.append("<recommendation>\n");
						if (ge.getAttributeByName("Recommendation")==null)
							errors.add("The recommendation field of "+ge.getID()+" is empty");
						else
							output.append(ge.getAttributeByName("Recommendation").getSimpleValue());
						output.append("</recommendation>\n");

						appendProperties(errors,output,ge);

						appendVisualRepresentationObject(errors,output,ge);
						output.append("</object>\n");
					}
			}
			if (ge.getType().equalsIgnoreCase("MetaRole")){
				output.append("<role id=\""+ge.getID()+"target\" >\n");
				output.append("<graphics><small-icon/><normal-icon/></graphics>\n");
				appendProperties(errors,output,ge);
				output.append("<validplayers>\n");
				GraphCollection localValidPlayers = ge.getAttributeByName("ValidPlayers").getCollectionValue();
				GraphEntity[] playedElements = getRelatedElements(ge, "PlayedBy", "PlayedBytarget");
				if ((localValidPlayers==null||localValidPlayers.size()==0) &&
						(playedElements==null|| playedElements.length==0)){
					errors.add("Metarole "+ge.getID()+" is not played by anybody. Please, connect metaroles" +
							" to metaobjects with playedby relationships or incorporating new entries in the " +
							"playedby field of the metarole");
				} else {
					for (int k=0;k<localValidPlayers.size();k++){
						output.append("<player id=\""+localValidPlayers.getElementAt(k).getID()+"\"/>\n");
					}
					for (int k=0;k<playedElements.length;k++){
						output.append("<player id=\""+playedElements[k].getID()+"\"/>\n");
					}
				}
				output.append("</validplayers>\n");
				output.append("</role>\n");
				output.append("<role id=\""+ge.getID()+"source\" >\n");
				output.append("<graphics><small-icon/><normal-icon/></graphics>\n");

				appendProperties(errors,output,ge);

				output.append("<validplayers>\n");
				localValidPlayers = ge.getAttributeByName("ValidPlayers").getCollectionValue();
				playedElements = getRelatedElements(ge, "PlayedBy", "PlayedBytarget");
				if ((localValidPlayers==null||localValidPlayers.size()==0) &&
						(playedElements==null|| playedElements.length==0)){
					errors.add("Metarole "+ge.getID()+" is not played by anybody. Please, connect metaroles" +
							" to metaobjects with playedby relationships or incorporating new entries in the " +
							"playedby field of the metarole");
				} else {
					for (int k=0;k<localValidPlayers.size();k++){
						output.append("<player id=\""+localValidPlayers.getElementAt(k).getID()+"\"/>\n");
					}
					for (int k=0;k<playedElements.length;k++){
						output.append("<player id=\""+playedElements[k].getID()+"\"/>\n");
					}
				}
				output.append("</validplayers>\n");
				output.append("</role>\n");
			}
			if (ge.getType().equalsIgnoreCase("MetaRelationship")){
				output.append("<relationship id=\""+ge.getID()+"\" >\n");					

				appendProperties(errors,output,ge);

				appendVisualRepresentationRelationship(errors,output,ge);
				output.append("<roles>\n");
				GraphRelationship[] connectedRoles = getRelatedElementsRels(ge, "AssociationEnd", "AssociationEndtarget");
				if (connectedRoles==null || connectedRoles.length==0)
					errors.add("Metarelationship "+ge.getID()+" does not have any associationend relationship connecting to a metrole");
				else {
					if ( connectedRoles.length!=2)
						errors.add("Metarelationship "+ge.getID()+" must have two associationend relationships connecting to a metrole");
					else {
						boolean oneSource=false;
						boolean oneTarget=false;
						Vector<GraphRole> sources=new Vector<GraphRole>();
						Vector<GraphRole> targets=new Vector<GraphRole>();
						Hashtable<GraphRole,String> mincardsource=new Hashtable<GraphRole,String>();
						Hashtable<GraphRole,String> mincardtarget=new Hashtable<GraphRole,String>();
						Hashtable<GraphRole,String> maxcardsource=new Hashtable<GraphRole,String>();
						Hashtable<GraphRole,String> maxcardtarget=new Hashtable<GraphRole,String>();

						for (int k=0;k<connectedRoles.length;k++){
							GraphRole[] roles = connectedRoles[k].getRoles();
							GraphRole role=null;
							if (roles[0].getName().equalsIgnoreCase("AssociationEndtarget"))
								role=roles[0];
							if (roles[1].getName().equalsIgnoreCase("AssociationEndtarget"))
								role=roles[1];
							if (connectedRoles[k].getAttributeByName("SourceOrTarget")==null)
								errors.add("AssociationEnd "+connectedRoles[k].getID()+ " conneted to metarelationship"+ ge.getID()+" does not define" +
										"the sourceOrTarget field");
							else
								if (connectedRoles[k].getAttributeByName("MinCardinality")==null)
									errors.add("AssociationEnd "+connectedRoles[k].getID()+ " conneted to metarelationship"+ ge.getID()+" does not define" +
											"the MinCardinality field");
								else 
									if (connectedRoles[k].getAttributeByName("MaxCardinality")==null)
										errors.add("AssociationEnd "+connectedRoles[k].getID()+ " conneted to metarelationship"+ ge.getID()+" does not define" +
												"the MaxCardinality field");
									else {
										oneSource=connectedRoles[k].getAttributeByName("SourceOrTarget").getSimpleValue().equalsIgnoreCase("source");
										oneTarget=connectedRoles[k].getAttributeByName("SourceOrTarget").getSimpleValue().equalsIgnoreCase("target");
										if (oneSource){
											sources.add(role);
											mincardsource.put(role, connectedRoles[k].getAttributeByName("MinCardinality").getSimpleValue());
											maxcardsource.put(role, connectedRoles[k].getAttributeByName("MaxCardinality").getSimpleValue());
										} else
											if (oneTarget){
												targets.add(role);
												mincardtarget.put(role, connectedRoles[k].getAttributeByName("MinCardinality").getSimpleValue());
												maxcardtarget.put(role, connectedRoles[k].getAttributeByName("MaxCardinality").getSimpleValue());
											} else
												errors.add("AssociationEnd "+connectedRoles[k].getID()+ " conneted to metarelationship"+ ge.getID()+" must be marked up either as source or target in the sourcetarget field");
									}
						}

						for (GraphRole role:sources){
							output.append("<role id=\""+role.getPlayer().getID()+"source\" type=\"source\"" +
									" mincard=\""+mincardsource.get(role)+"\" " +
									"maxcard=\""+maxcardsource.get(role)+"\"/>\n");
						}
						for (GraphRole role:targets){
							output.append("<role id=\""+role.getPlayer().getID()+"target\" type=\"target\"" +
									" mincard=\""+mincardtarget.get(role)+"\" " +
									"maxcard=\""+maxcardtarget.get(role)+"\"/>\n");
						}
					}
				}
				output.append("</roles>\n");
				output.append("</relationship>\n");
			}

			/**/

		}
	}

	public String format(String unformattedXml) {
		try {
			final Document document = parseXmlFile(unformattedXml);

			OutputFormat format = new OutputFormat(document);
			format.setLineWidth(65);
			format.setIndenting(true);
			format.setIndent(2);
			Writer out = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(document);

			return out.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Document parseXmlFile(String in) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(in));
			return db.parse(is);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	private String saveMetamodel(final StringBuffer output) {
		if (super.getResources()!=null) // command line execution
		{
			final javax.swing.JDialog saveSpecField=new javax.swing.JDialog(super.getResources().getMainFrame(),"Save metamodel");

			saveSpecField.getContentPane().setLayout(new BorderLayout());
			saveSpecField.getContentPane().add(new JLabel("Generation sucessful. Select a name and location for the generated file"),BorderLayout.NORTH);

			JPanel locationPanel=new JPanel();
			if (lastValue==null || lastValue.isEmpty()){
				lastValue=this.getProperty("defaultOutput").value;
			}
			final JTextField saveLocation=new JTextField(lastValue,30);
			JButton chooseLocation=new JButton("Browse files");
			locationPanel.add(new JLabel("Save location:"));
			locationPanel.add(saveLocation);
			locationPanel.add(chooseLocation);
			chooseLocation.addActionListener(new java.awt.event.ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					String cpath="";
					if (lastValue!=null && new File(lastValue).exists() && new File(lastValue).getParent()!=null){
						cpath=new File(lastValue).getParent();
					} else {
						cpath=System.getProperty("user.dir");
					}

					javax.swing.JFileChooser chooser=new javax.swing.JFileChooser(new File(cpath).getParentFile());
					chooser.setDialogTitle("Choose a file to write to:");
					chooser.setFileFilter(new FileFilter(){
						@Override
						public boolean accept(File arg0) {
							return  arg0.getName().toLowerCase().endsWith(".xml") || arg0.isDirectory();
						}

						@Override
						public String getDescription() {
							return "XML files";
						}});

					chooser.showOpenDialog(saveSpecField);
					if (chooser.getSelectedFile()!=null){					

						if (!chooser.getSelectedFile().getName().toLowerCase().endsWith(".xml"))
							saveLocation.setText(chooser.getSelectedFile().getAbsolutePath()+".xml");
						else
							saveLocation.setText(chooser.getSelectedFile().getAbsolutePath());
					}
				}

			});

			saveSpecField.getContentPane().add(locationPanel,BorderLayout.CENTER);

			JPanel savePanel=new JPanel();
			JButton accept=new JButton("Save");
			JButton cancel=new JButton("Cancel");
			cancel.addActionListener(new java.awt.event.ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					saveSpecField.setVisible(false);						
				}
			});
			accept.addActionListener(new java.awt.event.ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					File location=new File(saveLocation.getText());
					if (location.getParentFile().getName().equals("metamodel")) // the specfile must be stored in a metamodel folder
					{
						if (!(new File(location.getParentFile().getParent()+"/images").exists())){
							JOptionPane.showMessageDialog(null,"The folder structure is not valid. There should be a folder named images at the same level as the metamodel folder", "error",JOptionPane.ERROR_MESSAGE);
						} else
							if (location.exists()){
								int result = javax.swing.JOptionPane.showConfirmDialog(saveSpecField, "The file already exists. Do you want to overwrite?","Warning",javax.swing.JOptionPane.OK_CANCEL_OPTION);
								if (result==javax.swing.JOptionPane.OK_OPTION){
									writeFileContent(output, saveLocation, location);
									saveSpecField.setVisible(false);
								}							
							} else {
								writeFileContent(output, saveLocation, location);
								saveSpecField.setVisible(false);
							}
					} else
						JOptionPane.showMessageDialog(null,"The folder structure is not valid. Metamodel file should be stored in a folder named metamodel and current one is "+location.getParent(), "error",JOptionPane.ERROR_MESSAGE);

				}


			});
			savePanel.add(accept);
			savePanel.add(cancel);

			saveSpecField.getContentPane().add(savePanel,BorderLayout.SOUTH);



			saveSpecField.getContentPane().add(savePanel,BorderLayout.SOUTH);
			saveSpecField.setModal(true);
			saveSpecField.pack();
			saveSpecField.setVisible(true);
			return saveLocation.getText();
		}
		else{
			String saveLocation=		folder+"/src/main/resources/metamodel/metamodel.xml";
			File location=new File(saveLocation);
			writeFileContent(output, null, location);
			return folder+"/src/main/resources/metamodel/metamodel.xml";
		}

	}
	private void writeFileContent(final StringBuffer output,
			final JTextField saveLocation, File location) {
		java.io.FileOutputStream fos;
		try {
			fos = new FileOutputStream(location);
			output.insert(0, "<meta-models examplefile=\"example/demo.xml\">");
			output.append( "</meta-models>");			
			fos.write(format(output.toString()).getBytes());

			fos.close();
			if (saveLocation!=null)
				javax.swing.JOptionPane.showMessageDialog(saveLocation, "File saved successfully","Finished",javax.swing.JOptionPane.INFORMATION_MESSAGE);
			ingenias.editor.Log.getInstance().logSYS("Metamodel saved in location "+location);
		} catch (FileNotFoundException e1) {
			ingenias.editor.Log.getInstance().logERROR(e1.getMessage());
			javax.swing.JOptionPane.showMessageDialog(saveLocation,"Failure on writing the file. More details in the editor panel and console output","Error",javax.swing.JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		} catch (IOException e2) {
			ingenias.editor.Log.getInstance().logERROR(e2.getMessage());
			javax.swing.JOptionPane.showMessageDialog(saveLocation,"Failure on writing the file. More details in the editor panel and console output","Error",javax.swing.JOptionPane.ERROR_MESSAGE);
			e2.printStackTrace();
		}
		if (saveLocation!=null)
			lastValue=saveLocation.getText();
	}

	private void appendVisualRepresentationRelationship(Vector<String> errors, StringBuffer output, AttributedElement metarelationship) throws NotFound, NullEntity {
		GraphEntity[] visualRepr=getConnectedVisualRepr(metarelationship);
		output.append("<graphics>\n");
		GraphEntity basicRepresentation=createBasicGraphicRepresentation(errors, output, metarelationship);
		output.append("<layout>\n");

		createPropertiesSection(output, metarelationship);

		output.append("<views>\n");
		if (basicRepresentation==null){
			output.append("<content id=\"INGENIAS\">\n");
			output.append("##panel layout=\"BorderLayout(0,0)\"  Background=\"white\"#\n");
			output.append("##vbox Background=\"white\" constraints=\"BorderLayout.CENTER\"#\n");
			output.append("##stereotype text=\""+metarelationship.getID()+"\" HorizontalAlignment=\"CENTER\"/#\n");
			output.append("##/vbox#\n");
			output.append("##/panel#\n");
			output.append("</content>\n");
		} else {
			output.append("<content id=\"INGENIAS\">\n");
			output.append("##panel layout=\"BorderLayout(0,0)\"  Background=\"white\"#\n");
			output.append("##panel constraints=\"BorderLayout.NORTH\" Background=\"white\" layout=\"GridBagLayout\"#\n");
			output.append("##htmllabel id=\"Id\"#\n");
			output.append("##gridbagconstraints id=\"gbc_1\" insets=\"0,0,0,0\" gridx=\"0\" gridy=\"0\" fill=\"GridBagLayout.BOTH\" anchor=\"GridBagLayout.CENTER\"/#\n");
			output.append("##/htmllabel#\n");
			output.append("##/panel#\n");
			output.append("##label Foreground=\"blue\" icon=\""+basicRepresentation.getAttributeByName("NormalIcon").getSimpleValue()+"\" constraints=\"BorderLayout.CENTER\" HorizontalAlignment=\"CENTER\" /#\n");
			output.append("##/panel#\n");
			output.append("</content>\n");
		}
		output.append("<content id=\"LABEL\">\n");
		output.append("##panel layout=\"BorderLayout(0,0)\"  Background=\"white\"#\n");
		output.append("##htmllabel id=\"Id\"/#\n");
		output.append("##/panel#\n");

		output.append("</content>\n");
		output.append("<content id=\"NOICON\">\n");
		output.append("##panel layout=\"BorderLayout(0,0)\"  Background=\"white\"#\n");
		output.append("##/panel#\n");
		output.append("</content>\n");

		for (GraphEntity repr:visualRepr){
			if (repr.getAttributeByName("GenericId")==null)
				errors.add("GenericId field of entity "+metarelationship.getID()+" is empty");
			else
				output.append("<content id=\""+repr.getAttributeByName("GenericId").getSimpleValue()+"\">\n");
			if (repr.getAttributeByName("Content")==null)
				errors.add("Content field of entity "+metarelationship.getID()+" is empty");
			else
				output.append(repr.getAttributeByName("Content").getSimpleValue().replaceAll("<","##").replaceAll(">","#")+"\n");
			output.append("</content>\n");
		}

		output.append("</views>\n");
		output.append("</layout>\n");
		output.append("</graphics>\n");
	}

	private void appendVisualRepresentationObject(Vector<String> errors, StringBuffer output, GraphEntity ge) throws NotFound, NullEntity {
		GraphEntity[] visualRepr=getConnectedVisualRepr(ge);
		output.append("<graphics>\n");
		GraphEntity basicRepresentation=createBasicGraphicRepresentation(errors, output, ge);
		output.append("<layout>\n");

		createPropertiesSection(output, ge);

		output.append("<views>\n");
		if (basicRepresentation==null){
			output.append("<content id=\"INGENIAS\">\n");

			output.append("##panel layout=\"BorderLayout(0,0)\"  Background=\"white\" border=\"LineBorder(Color.black,1)\"#\n");
			output.append("##vbox Background=\"white\" constraints=\"BorderLayout.CENTER\"#\n");
			output.append("##panel Background=\"white\"  HorizontalAlignment=\"CENTER\" layout=\"FlowLayout(FlowLayout.CENTER,0,0)\"#\n");
			output.append("##stereotype text=\""+ge.getID()+"\" HorizontalAlignment=\"CENTER\"/#\n");
			output.append("##/panel#\n");
			output.append("##panel Background=\"white\"  HorizontalAlignment=\"CENTER\"  layout=\"FlowLayout(FlowLayout.CENTER,0,0)\"#\n");
			output.append("##label id=\"Id\"/#\n");
			output.append("##/panel#\n");
			output.append("##linepanel Background=\"white\"/#\n");
			output.append("##/vbox#\n");
			output.append("##/panel#\n");
			output.append("</content>\n");
		} else {
			output.append("<content id=\"INGENIAS\">\n");

			output.append("##panel layout=\"BorderLayout(0,0)\"  Background=\"white\"#\n");
			output.append("##panel constraints=\"BorderLayout.NORTH\" Background=\"white\" layout=\"GridBagLayout\"#\n");
			output.append("##htmllabel id=\"Id\"#\n");
			output.append("##gridbagconstraints id=\"gbc_1\" insets=\"0,0,0,0\" gridx=\"0\" gridy=\"0\" fill=\"GridBagLayout.BOTH\" anchor=\"GridBagLayout.CENTER\"/#\n");
			output.append("##/htmllabel#\n");
			output.append("##/panel#\n");
			output.append("##label Foreground=\"blue\" icon=\""+basicRepresentation.getAttributeByName("NormalIcon").getSimpleValue()+"\" constraints=\"BorderLayout.CENTER\" HorizontalAlignment=\"CENTER\" /#\n");
			output.append("##/panel#\n");

			output.append("</content>\n");
		}

		for (GraphEntity repr:visualRepr){
			if (repr.getAttributeByName("GenericId")==null)
				errors.add("GenericId field of entity "+ge.getID()+" is empty");
			else
				output.append("<content id=\""+repr.getAttributeByName("GenericId").getSimpleValue()+"\">\n");
			if (repr.getAttributeByName("Content")==null)
				errors.add("Content field of entity "+ge.getID()+" is empty");
			else
				output.append(repr.getAttributeByName("Content").getSimpleValue().replaceAll("<","##").replaceAll(">","#")+"\n");
			output.append("</content>\n");
		}

		output.append("</views>\n");
		output.append("</layout>\n");
		output.append("</graphics>\n");
	}

	private void createPropertiesSection(StringBuffer output, AttributedElement ge)
			throws NotFound, NullEntity {
		GraphAttribute localPropertiesAttribute = ge.getAttributeByName("Properties");
		GraphCollection propertiesCollection = localPropertiesAttribute.getCollectionValue();
		output.append("<properties>\n");
		output.append("<property id=\"id\"/>\n");
		for (int k=0;k<propertiesCollection.size();k++){
			output.append("<property id=\""+propertiesCollection.getElementAt(k).getID()+"\"/>");
		}
		output.append("</properties>\n");
	}

	private GraphEntity createBasicGraphicRepresentation(Vector<String> errors,
			StringBuffer output, AttributedElement metamodelEntity) throws NotFound {
		GraphEntity basicRepresentation=null;
		basicRepresentation = obtainBasicRepresentation(errors,	metamodelEntity);
		if (errors.isEmpty()){
			if (basicRepresentation!=null){
				if (basicRepresentation.getAttributeByName("SmallIcon")==null){
					errors.add("smallicon field of entity "+basicRepresentation.getID()+" is empty");
				}else 
					output.append("<small-icon>"+basicRepresentation.getAttributeByName("SmallIcon").getSimpleValue()+"</small-icon>\n");
				if (basicRepresentation.getAttributeByName("NormalIcon")==null){
					errors.add("NormalIcon field of entity "+basicRepresentation.getID()+" is empty");

				}else 
					output.append("<normal-icon>"+basicRepresentation.getAttributeByName("NormalIcon").getSimpleValue()+"</normal-icon>\n");				
			} else {
				File smallicon=new File(folder+"/target/m"+metamodelEntity.getID().replace(' ', '_').replace(':', '_')+".png");
				this.iconsToMove.add(smallicon.getAbsolutePath());
				inventIcon(metamodelEntity, smallicon);

				File bigicon=new File(folder+"/target/"+metamodelEntity.getID().replace(' ', '_').replace(':', '_')+".png");
				this.iconsToMove.add(bigicon.getAbsolutePath());
				inventIcon(metamodelEntity, bigicon);

				output.append("<small-icon>images/"+smallicon.getName()+"</small-icon>\n");
				output.append("<normal-icon>images/"+bigicon.getName()+"</normal-icon>\n");
			}
		}
		return basicRepresentation;
	}

	private void inventIcon(AttributedElement basicRepresentation, File smallicon) {
		System.err.println("trying ...");

		JPanel tempImage=new JPanel();
		System.err.println("saving to  "+smallicon.getAbsolutePath());
		tempImage.add(new JLabel(basicRepresentation.getID()));								
		try {							
			tempImage.doLayout();
			Diagram2SVG.createPNG(tempImage,smallicon); // to save icons to the images folder in the project resources path
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private GraphEntity obtainBasicRepresentation(Vector<String> errors,
			AttributedElement metamodelEntity)
					throws NotFound {
		if (metamodelEntity instanceof GraphEntity){
			return obtainBasicRepresentation(errors, (GraphEntity)metamodelEntity);
		} else {
			GraphEntity basicRepresentation=null;


			if (errors.isEmpty() && metamodelEntity.getAttributeByName("BasicRepresentations")!=null &&
					basicRepresentation==null){
				try {
					basicRepresentation=metamodelEntity.getAttributeByName("BasicRepresentations").getEntityValue();
				} catch (NullEntity e) {
					// No basic representation available
				}
			}
			return basicRepresentation;
		}
	}



	private GraphEntity obtainBasicRepresentation(Vector<String> errors,
			GraphEntity metamodelEntity)
					throws NotFound {
		GraphEntity basicRepresentation=null;
		Vector<GraphRole> basicRepresentationRoles = getRelatedElementsRolesVector(metamodelEntity, "VisualizedAs", "VisualizedAstarget");
		Vector<GraphRole> toRemove=new 	Vector<GraphRole> ();
		for (GraphRole gr:basicRepresentationRoles) {
			try {
				if (!gr.getPlayer().getType().equalsIgnoreCase("BasicRepresentation"))
					toRemove.add(gr);
			} catch (NullEntity e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		basicRepresentationRoles.removeAll(toRemove);
		if (basicRepresentationRoles.size()>1)
			errors.add("Entity "+metamodelEntity.getID()+" has more than one basic representation");
		else 
			if (basicRepresentationRoles.size()!=0){
				try {
					basicRepresentation=basicRepresentationRoles.elementAt(0).getPlayer();
				} catch (NullEntity e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
		try {
			if (metamodelEntity.getAttributeByName("BasicRepresentations")!=null &&
					basicRepresentation!=null &&
					metamodelEntity.getAttributeByName("BasicRepresentations").getEntityValue().equals(basicRepresentation)){
				errors.add("Entity "+metamodelEntity.getID()+" has more than one basic representation, one associated by means of VisualizedAs relationship and another associated as attribute ");
			}
		} catch (NullEntity e) {

		}
		if (errors.isEmpty() && metamodelEntity.getAttributeByName("BasicRepresentations")!=null &&
				basicRepresentation==null){
			try {
				basicRepresentation=metamodelEntity.getAttributeByName("BasicRepresentations").getEntityValue();
			} catch (NullEntity e) {
				// No basic representation available
			}
		}
		return basicRepresentation;
	}

	private GraphEntity[] getConnectedVisualRepr(GraphEntity ge) throws NullEntity, NotFound {
		GraphCollection localVisualRepresentations = ge.getAttributeByName("VisualRepresentations").getCollectionValue();
		GraphEntity[] relatedVRepr = getRelatedElements(ge, "VisualizedAs", "VisualizedAstarget");
		Vector<GraphEntity> ges=new Vector<GraphEntity>();
		for (int k=0;k<localVisualRepresentations.size();k++) {

			ges.add(localVisualRepresentations.getElementAt(k));
		}
		for (GraphEntity vrepr:relatedVRepr){
			if (vrepr.getType().equalsIgnoreCase("VisualRepresentation"))
				ges.add(vrepr);	
		}
		return ges.toArray(new GraphEntity[ges.size()]);
	}

	private GraphEntity[] getConnectedVisualRepr(AttributedElement ge) throws NullEntity, NotFound {
		GraphCollection localVisualRepresentations = ge.getAttributeByName("VisualRepresentations").getCollectionValue();
		Vector<GraphEntity> ges=new Vector<GraphEntity>();
		for (int k=0;k<localVisualRepresentations.size();k++) {

			ges.add(localVisualRepresentations.getElementAt(k));
		}
		return ges.toArray(new GraphEntity[ges.size()]);
	}



	private void appendProperties(Vector<String> errors,StringBuffer output, AttributedElement ge, String gid) throws NullEntity, NotFound {
		HashSet<GraphEntity> propertiesToConsider=new HashSet<GraphEntity>();		
		getInternalProperties(ge, propertiesToConsider);
		HashSet<GraphRelationship> aggregationToConsider=new HashSet<GraphRelationship>();
		// this method is for edges, so no aggregations are needed to be considered
		generateProperties(errors, output, ge, gid, propertiesToConsider,aggregationToConsider);

	}

	public HashSet<GraphEntity> getAllAncestors(GraphEntity ge){
		HashSet<GraphEntity> result=new HashSet<GraphEntity>();
		try {
			getAllAncestorsAux(ge,result);
		} catch (NullEntity e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public void getAllAncestorsAux(GraphEntity ge, HashSet<GraphEntity> result) throws NullEntity{
		Hashtable<GraphEntity, GraphRole> inheriting = getRelatedElementsHashtable(ge,"InheritsO", "InheritsOtarget");
		Set<GraphEntity> inheritingKeySet = inheriting.keySet();
		for (GraphEntity parent:inheritingKeySet){
			if (!result.contains(parent)){
				result.add(parent);
				getAllAncestorsAux(parent, result);
			}
		}
	}

	private void appendProperties(Vector<String> errors,StringBuffer output, GraphEntity ge) throws NullEntity, NotFound {
		HashSet<GraphEntity> propertiesToConsider=new HashSet<GraphEntity>();


		HashSet<GraphEntity> ancestors = getAllAncestors(ge);
		producePropertiesForEntity(errors, output, ge,
				propertiesToConsider, ge);
		for (GraphEntity ancestor:ancestors){
			producePropertiesForEntity(errors, output, ge,
					propertiesToConsider, ancestor);

		}

	}

	private void producePropertiesForEntity(Vector<String> errors,
			StringBuffer output, GraphEntity ge,
			HashSet<GraphEntity> propertiesToConsider, GraphEntity ancestor)
					throws NullEntity, NotFound {
		HashSet<GraphRelationship> aggregationToConsider=new HashSet<GraphRelationship>();
		GraphRelationship[] rels = getRelatedElementsRels(ancestor,"HasMO", "HasMOtarget");		
		for (GraphRelationship gr:rels){
			if (gr.getRoles("Hassource")[0].getPlayer().getID().equalsIgnoreCase(ancestor.getID()))
				aggregationToConsider.add(gr);
		}
		getExternalProperties(ancestor, propertiesToConsider);
		getInternalProperties(ancestor, propertiesToConsider);
		generateProperties(errors, output, ancestor,ge.getID(), propertiesToConsider,aggregationToConsider);
	}

	private void generateProperties(Vector<String> errors, StringBuffer output,
			AttributedElement ge, String gid, HashSet<GraphEntity> propertiesToConsider, 
			HashSet<GraphRelationship> aggregationToConsider)
					throws NotFound, NullEntity {
		Vector<String> artificialpropertiespreferredorder=new Vector<String>();
		output.append("<properties>\n");
		for (GraphEntity property:propertiesToConsider){			
			createProperty(errors, output, ge,gid, property);
		}
		for (GraphRelationship aggregation:aggregationToConsider){

			if (aggregation.getRoles("HasMOtarget")[0].getPlayer().getType().equalsIgnoreCase("MetaDiagram")){
				try{
					GraphEntity metadiagramEntity=aggregation.getRoles("HasMOtarget")[0].getPlayer();
					String isCollection=aggregation.getRoles("HasMOtarget")[0].getAttributeByName("Iscollection").getSimpleValue();
					output.append("<property id=\""+aggregation.getRoles("HasMOtarget")[0].getAttributeByName("Role").getSimpleValue()+"\" ");
					output.append(" type=\""+metadiagramEntity.getID()+"\"");
					output.append(" iscollection=\""+isCollection+"\"");
					output.append(" ismetamodelinstance=\"yes\" ");
					output.append(">\n");
					artificialpropertiespreferredorder.add(aggregation.getRoles("HasMOtarget")[0].getAttributeByName("Role").getSimpleValue());
				}catch (NullEntity ne){
					ne.printStackTrace();
				}
			}
			if (aggregation.getRoles("HasMOtarget")[0].getPlayer().getType().equalsIgnoreCase("MetaObject")){
				try{
					GraphEntity metadiagramEntity=aggregation.getRoles("HasMOtarget")[0].getPlayer();
					String isCollection=aggregation.getRoles("HasMOtarget")[0].getAttributeByName("Iscollection").getSimpleValue();
					output.append("<property id=\""+aggregation.getRoles("HasMOtarget")[0].getAttributeByName("Role").getSimpleValue()+"\" ");
					output.append(" type=\""+metadiagramEntity.getID()+"\"");
					output.append(" iscollection=\""+isCollection+"\"");
					output.append(" ismetaclassinstance=\"yes\" ");
					output.append("/>\n");
					artificialpropertiespreferredorder.add(aggregation.getRoles("HasMOtarget")[0].getAttributeByName("Role").getSimpleValue());
				}catch (NullEntity ne){
					ne.printStackTrace();
				}
			}
		}

		GraphCollection prefordercollection = ge.getAttributeByName("PreferredOrder").getCollectionValue();
		Hashtable<Integer,GraphEntity> table=new Hashtable<Integer,GraphEntity>();

		if (prefordercollection.size()>0){
			for (int k=0;k<prefordercollection.size();k++){
				GraphEntity porder=prefordercollection.getElementAt(k);
				try {
					if (porder.getAttributeByName("Property")==null)
						errors.add("Property field in entity "+gid+" is empty");
					else  {
						GraphEntity property=porder.getAttributeByName("Property").getEntityValue();
						if (porder.getAttributeByName("Order")==null || porder.getAttributeByName("Order").equals("")){
							errors.add("Preferred order field in entity "+gid+" is empty ");
						}
						try {
							String order=porder.getAttributeByName("Order").getSimpleValue();
							table.put(new Integer(order),property);
						} catch (NumberFormatException nfe){
							errors.add("Preferred order field in entity "+gid+" has a non integer value. Current value is "+porder.getAttributeByName("Order").getSimpleValue());
						}
					}
				} catch (NullEntity ne) {
					errors.add("Property field in entity "+gid+" is empty");
				}
			}
		} else {
			int order=0;
			for (GraphEntity property:propertiesToConsider){			
				table.put(new Integer(order),property);
				order=order+1;
			}
		}

		output.append("<preferredorder>\n");
		output.append("<order>id</order>\n");
		Vector<Integer> keys = new Vector<Integer>(table.keySet());
		java.util.Collections.sort(keys);
		for (Integer integer:keys) {
			output.append("<order>"+table.get(integer).getID()+"</order>\n");
		}
		for (String pid:artificialpropertiespreferredorder) {
			output.append("<order>"+pid.substring(0,1).toUpperCase()+pid.substring(1,pid.length())+"</order>\n");
		}
		output.append("</preferredorder>\n");
		output.append("</properties>\n");
	}

	private void getInternalProperties(AttributedElement ge,
			HashSet<GraphEntity> propertiesToConsider) throws NotFound,
			NullEntity {
		GraphAttribute localPropertiesAttribute = ge.getAttributeByName("Properties");
		GraphCollection propertiesCollection = localPropertiesAttribute.getCollectionValue();
		for (int k=0;k<propertiesCollection.size();k++){
			GraphEntity propertyFieldElement = propertiesCollection.getElementAt(k);
			propertiesToConsider.add(propertyFieldElement);

		}
	}

	private void getExternalProperties(GraphEntity ge,
			HashSet<GraphEntity> propertiesToConsider) throws NullEntity {
		GraphEntity[] externalProperties = getRelatedElements(ge, "Has", "Hastarget");
		for (GraphEntity property:externalProperties){
			propertiesToConsider.add(property);
		}

		//	GraphRelationship[] hasMOProperties = getRelatedElementsRels(ge, "HasMO", "HasMOtarget");


		/*	for (GraphRelationship property:hasMOProperties){
			GraphEntity prop;
			try {
				prop = gef.createEntity("PropertyField", "_pf00000"+k, tmpdiagram);
				GraphEntity motw = gef.createEntity("MetaObjectTypeWrapper", "_pf00000"+k, tmpdiagram);
				Object mot = property.getRoles("HasMOtarget")
				Iscollection
				prop.setAttribute(atfact.createAttribute("WrappedType", value, tmpdiagram));
			} catch (InvalidEntity e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			propertiesToConsider.add(prop);
			k=k+1;
		}


		 */

	}

	private void createProperty(Vector<String> errors, StringBuffer output,
			AttributedElement ge, String gid,GraphEntity propertyFieldElement) throws NotFound,
			NullEntity {
		output.append("<property id=\""+propertyFieldElement.getID()+"\" ");			
		String prefwidget = propertyFieldElement.getAttributeByName("Preferredwidget").getSimpleValue();
		output.append(" preferredwidget=\""+prefwidget+"\" ");
		if (propertyFieldElement.getAttributeByName("WrappedType")!=null) {
			GraphEntity wrappedTypeEntity=null;

			try {
				wrappedTypeEntity=propertyFieldElement.getAttributeByName("WrappedType").getEntityValue();
			}catch (NullEntity ne){
				errors.add("WrappedType field  in field "+propertyFieldElement.getID()+" of entity "+gid +" does not contain a reference to an entity");
			}
			if (wrappedTypeEntity!=null){
				if (wrappedTypeEntity.getType().equalsIgnoreCase("MetaDiagramTypeWrapper")){
					try{
						GraphEntity metadiagramEntity=wrappedTypeEntity.getAttributeByName("MetaDiagramType").getEntityValue();
						String isCollection=wrappedTypeEntity.getAttributeByName("Iscollection").getSimpleValue();
						output.append(" iscollection=\""+isCollection+"\"");
						output.append(" ismetamodelinstance=\"yes\" ");
						output.append(">\n");
					}catch (NullEntity ne){
						errors.add(" MetaDiagramType field in wrappedtype attribute in field "+propertyFieldElement.getID()+" of entity "+gid +" does not contain a reference to an entity");
					}
				}	
				if (wrappedTypeEntity.getType().equalsIgnoreCase("MetaObjectTypeWrapper")){
					try{
						GraphEntity metaObjectEntity=wrappedTypeEntity.getAttributeByName("MetaObjectType").getEntityValue();
						String isCollection=wrappedTypeEntity.getAttributeByName("Iscollection").getSimpleValue();
						output.append(" iscollection=\""+isCollection+"\"");
						output.append(" type=\""+metaObjectEntity.getID()+"\"");

						output.append(" ismetaclassinstance=\"yes\" ");
						output.append(">\n");
					}catch (NullEntity ne){
						errors.add("MetaObjectType field in wrappedtype attruibute in field "+propertyFieldElement.getID()+" of entity "+gid +" does not contain a reference to an entity");
					}
				}
				if (wrappedTypeEntity.getType().equalsIgnoreCase("ExternalTypeWrapper")){
					String externalType=wrappedTypeEntity.getAttributeByName("ExternalType").getSimpleValue();
					output.append(" type=\""+externalType+"\" ");

					output.append(">\n");
					GraphCollection defaultValuesCollection = wrappedTypeEntity.getAttributeByName("DefaultValues").getCollectionValue();
					output.append("<defaultvalues>");
					for (int j=0;j<defaultValuesCollection.size();j++){
						output.append("<value>");
						String value=defaultValuesCollection.getElementAt(j).getAttributeByName("DefaultValue").getSimpleValue();
						output.append(value);
						output.append("<value>\n");
					}
					output.append("</defaultvalues>");
				}
			}
		} else  
		{
			errors.add("WrappedEntity field in entity "+gid+" is empty");
		}
		output.append("<label lang=\"en\">Description</label>");	
		output.append("</property>");
	}

	private String getContent(File task) {

		FileInputStream fis;
		try {
			fis = new FileInputStream(task);
			int read=0;
			byte[] bytesRead=new byte[100];
			StringBuffer sb=new StringBuffer(); 
			while (read>-1){
				try {
					read=fis.read(bytesRead);
					for (int k=0;k<read;k++){
						sb.append((char)bytesRead[k]);
					}
				} catch (IOException e) {

					e.printStackTrace();
				}

			}
			return sb.toString();
		} catch (FileNotFoundException e1) {			
			e1.printStackTrace();
		}

		return "";
	}

	/**
	 *  This module defines no properties
	 *
	 *@return    Empty properties
	 */
	public Vector<ProjectProperty> defaultProperties() {
		Vector<ProjectProperty> result=new Vector<ProjectProperty>();
		result.add(new ingenias.editor.ProjectProperty(this.getName(), "defaultOutput", "Default file where results should be dumped",
				"", ""));
		return result;
	}


	/**
	 *  Generates an stats report from a INGENIAS specification file (1st param)
	 *
	 *@param  args           Arguments typed in the command line. Only first one is attended
	 *@exception  Exception  Sth went wrong
	 */
	public static void main(String args[]) throws Exception {
		ingenias.editor.Log.initInstance(new java.io.PrintWriter(System.err));
		Ingened2Ingenme ingened=new Ingened2Ingenme(args[0], args[1]);
		ingened.run();
		// Prints the result
		if (ingenias.editor.Log.getInstance().areThereErrors() || ingened.error){
			for (Frame f:Frame.getFrames()){
				f.dispose();

			}
			throw new RuntimeException("There are the following code generation errors: "+Log.getInstance().getErrors());		
		}
		for (Frame f:Frame.getFrames()){ 
			f.dispose();
		}			
	}



	/**
	 * It replaces incorrect chars that may cause conflicts in the final instances
	 * @param string The string being converted, like white spaces
	 * @return A string without improper characters
	 */
	public static String replaceBadChars(String string){
		return string.replace(' ','_').replace(',','_').replace('.','_').replace('-', '_');
	}


	/**
	 * It obtains the elements in the specification linked with "element" that have an association of type
	 * "relationshipname" and they occupy the extreme labelled with the same string as "role"
	 * 
	 * @param element The element to be studied
	 * @param relationshipname The name of the relationship which will be studied
	 * @param role The name of the extreme of the relationship that has to be studied
	 * @return a list of elements placed in the association extreme
	 * @throws NullEntity
	 */
	public static GraphEntity[] getRelatedElements(GraphEntity element,
			String relationshipname, String role) throws NullEntity {
		Vector rels = element.getAllRelationships();
		Enumeration enumeration = rels.elements();
		Vector related = new Vector();
		while (enumeration.hasMoreElements()) {
			GraphRelationship gr = (GraphRelationship) enumeration.nextElement();
			if (gr.getType().toLowerCase().equals(relationshipname.toLowerCase())) {
				GraphRole[] roles = gr.getRoles();
				for (int k = 0; k < roles.length; k++) {
					//System.err.println(roles[k].getName());
					if (roles[k].getName().toLowerCase().equals(role.toLowerCase())) {
						//System.err.println("added"+roles[k].getName());
						related.add(roles[k].getPlayer());
					}
				}
			}
		}
		return toGEArray(new HashSet(related).toArray());
	}

	/**
	 * Same as getRelatedElementsAux but returning the result as vectors
	 * 
	 * @param element The element to be studied
	 * @param relationshipname The name of the relationship which will be studied
	 * @param role The name of the extreme of the relationship that has to be studied
	 * @return a list of elements placed in the association extreme
	 * @throws NullEntity
	 */
	public static  Vector getRelatedElementsVectorAux(GraphEntity element,
			String relationshipname,
			String role) throws NullEntity {
		Vector rels = element.getAllRelationships();
		Enumeration enumeration = rels.elements();
		Vector related = new Vector();
		while (enumeration.hasMoreElements()) {
			GraphRelationship gr = (GraphRelationship) enumeration.nextElement();
			if (gr.getType().toLowerCase().equals(relationshipname.toLowerCase())) {
				GraphRole[] roles = gr.getRoles();
				for (int k = 0; k < roles.length; k++) {
					if (roles[k].getName().toLowerCase().equals(role.toLowerCase()) &&
							! (roles[k].getPlayer().equals(element))) {
						related.add(roles[k].getPlayer());
					}
				}
			}
		}
		return new Vector(new HashSet(related));
	}

	/**
	 * Same as getRelatedElements but returning the result as a vector
	 * 
	 * @param element The element to be studied
	 * @param relationshipname The name of the relationship which will be studied
	 * @param role The name of the extreme of the relationship that has to be studied
	 * @return a list of elements placed in the association extreme
	 */
	public static Vector<GraphEntity> getRelatedElementsVector(GraphEntity agent,
			String relationshipname, String role) throws NullEntity {
		Vector rels = agent.getAllRelationships();
		Enumeration enumeration = rels.elements();
		Vector related = new Vector();
		while (enumeration.hasMoreElements()) {
			GraphRelationship gr = (GraphRelationship) enumeration.nextElement();
			if (gr.getType().toLowerCase().equals(relationshipname.toLowerCase())) {
				GraphRole[] roles = gr.getRoles();
				for (int k = 0; k < roles.length; k++) {
					if (roles[k].getName().toLowerCase().equals(role.toLowerCase())) {
						related.add(roles[k].getPlayer());
					}
				}
			}
		}
		return new Vector(new HashSet(related));
	}

	/**
	 * It obtains all elements related with "element" with "relationshipname" and occupying the extreme "role.
	 * These elements are then allocated in a hashtable using the obtained entity as key and the relationship
	 * where this entity appears as value 
	 * 
	 * @param element The element to be studied
	 * @param relationshipname The name of the relationship which will be studied
	 * @param role The name of the extreme of the relationship that has to be studied
	 * @return a hashtable
	 * @throws NullEntity
	 */
	public static  Hashtable<GraphEntity,GraphRole> getRelatedElementsHashtable(GraphEntity element,
			String relationshipname, String role) throws NullEntity {
		Vector rels = element.getAllRelationships();
		Enumeration enumeration = rels.elements();
		Hashtable related = new Hashtable();
		while (enumeration.hasMoreElements()) {
			GraphRelationship gr = (GraphRelationship) enumeration.nextElement();
			if (gr.getType().toLowerCase().equals(relationshipname.toLowerCase())) {
				GraphRole[] roles = gr.getRoles();
				for (int k = 0; k < roles.length; k++) {
					if (roles[k].getName().toLowerCase().equals(role.toLowerCase())) {
						related.put(roles[k].getPlayer(),gr);
					}
				}
			}
		}
		return related;
	}

	/**
	 * It obtains all elements related with "element" with "relationshipname" and occupying the extreme "role.
	 * Also, the association where these elements appear must be allocated in the package whose pathname
	 * matches the "pathname" parameter
	 *  
	 * @param pathname Part of a path name. It will force located relationships to belong to concrete sets of diagrams
	 * allocated in concrete packages
	 * @param element The element to be studied
	 * @param relationshipname The name of the relationship which will be studied
	 * @param role The name of the extreme of the relationship that has to be studied
	 * @return A list of entities.
	 * @throws NullEntity
	 */
	public static  Vector getRelatedElementsVector(String pathname,GraphEntity element,
			String relationshipname, String role) throws NullEntity {
		Vector rels = element.getAllRelationships();
		Enumeration enumeration = rels.elements();
		Vector related = new Vector();
		while (enumeration.hasMoreElements()) {
			GraphRelationship gr = (GraphRelationship) enumeration.nextElement();
			String[] path=gr.getGraph().getPath();
			boolean found=false;
			for (int k=0;k<path.length && !found;k++){
				found=path[k].toLowerCase().indexOf(pathname)>=0;
			}
			if (found && gr.getType().toLowerCase().equals(relationshipname.toLowerCase())) {
				GraphRole[] roles = gr.getRoles();
				for (int k = 0; k < roles.length; k++) {
					if (roles[k].getName().toLowerCase().equals(role.toLowerCase())) {
						related.add(roles[k].getPlayer());
					}
				}
			}
		}
		return new Vector(new HashSet(related));
	}

	/**
	 * It casts an array of objets to an array of GraphEntity
	 *  
	 * @param o the array of objects
	 * @return
	 */
	public static GraphEntity[] toGEArray(Object[] o) {
		GraphEntity[] result = new GraphEntity[o.length];
		System.arraycopy(o, 0, result, 0, o.length);
		return result;
	}

	/**
	 * It casts an array of objets to an array of GraphRelationship
	 *  
	 * @param o the array of objects
	 * @return
	 */
	public static GraphRelationship[] toGRArray(Object[] o) {
		GraphRelationship[] result = new GraphRelationship[o.length];
		System.arraycopy(o, 0, result, 0, o.length);
		return result;
	}

	/**
	 * It casts an array of objets to an array of GraphRole
	 *  
	 * @param o the array of objects
	 * @return
	 */
	public static GraphRole[] toGRoArray(Object[] o) {
		GraphRole[] result = new GraphRole[o.length];
		System.arraycopy(o, 0, result, 0, o.length);
		return result;
	}

	/**
	 * It obtains all entities in the specification whose type represented as string
	 * is the same as the string passed as parameter
	 * 
	 * @param type The type the application is looking for
	 * @return
	 * @throws NotInitialised 
	 */
	public static  GraphEntity[] generateEntitiesOfType(String type, Browser browser) throws NotInitialised {
		Graph[] gs = browser.getGraphs();
		Sequences p = new Sequences();
		GraphEntity[] ges = browser.getAllEntities();
		HashSet actors = new HashSet();
		for (int k = 0; k < ges.length; k++) {
			if (ges[k].getType().equals(type)) {
				actors.add(ges[k]);
			}
		}
		return toGEArray(actors.toArray());
	}

	/**
	 * It obtains the extremes of the association of type 	"relationshipname", where one
	 * of their roles is "role", and originated in the "element"
	 * 
	 * @param element The element to be studied
	 * @param relationshipname The name of the relationship which will be studied
	 * @param role The name of the extreme of the relationship that has to be studied
	 * @return An array of roles
	 */
	public static  GraphRole[] getRelatedElementsRoles(GraphEntity element,
			String relationshipname,
			String role) {
		Vector rels = element.getAllRelationships();
		Enumeration enumeration = rels.elements();
		Vector related = new Vector();
		while (enumeration.hasMoreElements()) {
			GraphRelationship gr = (GraphRelationship) enumeration.nextElement();
			if (gr.getType().toLowerCase().equals(relationshipname.toLowerCase())) {
				GraphRole[] roles = gr.getRoles();
				for (int k = 0; k < roles.length; k++) {
					if (roles[k].getName().toLowerCase().equals(role.toLowerCase())) {
						related.add(roles[k]);
					}
				}
			}
		}
		return toGRoArray(related.toArray());
	}


	/**
	 * It obtains the extremes of the association of type 	"relationshipname", where one
	 * of their roles is "role", and originated in the "element"
	 * 
	 * @param element The element to be studied
	 * @param relationshipname The name of the relationship which will be studied
	 * @param role The name of the extreme of the relationship that has to be studied
	 * @return A vector of roles
	 */
	public static  Vector<GraphRole> getRelatedElementsRolesVector(GraphEntity element,
			String relationshipname,
			String role) {
		Vector rels = element.getAllRelationships();
		Enumeration enumeration = rels.elements();
		Vector<GraphRole> related = new Vector<GraphRole>();
		while (enumeration.hasMoreElements()) {
			GraphRelationship gr = (GraphRelationship) enumeration.nextElement();
			if (gr.getType().toLowerCase().equals(relationshipname.toLowerCase())) {
				GraphRole[] roles = gr.getRoles();
				for (int k = 0; k < roles.length; k++) {
					if (roles[k].getName().toLowerCase().equals(role.toLowerCase())) {
						related.add(roles[k]);
					}
				}
			}
		}
		return related;
	}

	/**
	 * It returns an array of the relationships whose name is "relationshipname" and 
	 * that are linked to "element" and there is an element occupiying the extreme
	 * labelled with "role"
	 * 
	 * @param element The element to be studied
	 * @param relationshipname The name of the relationship which will be studied
	 * @param role The name of the extreme of the relationship that has to be studied
	 * @return an array of relationships
	 */
	public static GraphRelationship[] getRelatedElementsRels(GraphEntity element,
			String relationshipname, String role) {
		Vector rels = element.getAllRelationships();
		Enumeration enumeration = rels.elements();
		Vector related = new Vector();
		while (enumeration.hasMoreElements()) {
			GraphRelationship gr = (GraphRelationship) enumeration.nextElement();
			if (gr.getType().toLowerCase().equals(relationshipname.toLowerCase())) {
				GraphRole[] roles = gr.getRoles();
				for (int k = 0; k < roles.length; k++) {
					if (roles[k].getName().toLowerCase().equals(role.toLowerCase())) {
						related.add(gr);
					}
				}
			}
		}
		return toGRArray(related.toArray());
	}

	/**
	 * It obtains the entities in the graph "g" whose type is the same as "typeName".
	 * 
	 * @param g The graph considered
	 * @param typeName The type being searched
	 * @return The list of entities
	 * @throws NullEntity
	 */
	public static Vector getEntities(Graph g, String typeName) throws NullEntity {
		GraphEntity[] ge = g.getEntities();
		Vector result = new Vector();
		for (int k = 0; k < ge.length; k++) {
			if (ge[k].getType().equals(typeName)) {
				result.add(ge[k]);
			}
		}
		return result;
	}

}



