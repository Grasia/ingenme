
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
package ingenias.editor.persistence;

import ingenias.editor.Editor;
import ingenias.editor.GUIResources;
import ingenias.editor.GraphManager;
import ingenias.editor.IDEState;
import ingenias.editor.IDEUpdater;
import ingenias.editor.ModelJGraph;
import ingenias.editor.ObjectManager;
import ingenias.editor.Preferences;
import ingenias.editor.ProjectProperty;
import ingenias.editor.RelationshipManager;
import ingenias.editor.actions.HistoryManager;
import ingenias.editor.editionmode.EmbeddedAndPopupCellEditor;
import ingenias.editor.entities.NAryEdgeEntity;
import ingenias.editor.widget.DnDJTree;
import ingenias.exception.CannotLoad;
import ingenias.exception.InvalidProjectProperty;
import ingenias.exception.UnknowFormat;
import ingenias.exception.VersionNotFound;
import ingenias.generator.browser.BrowserImp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Description of the Class
 * 
 * @author developer
 * @created 7 de agosto de 2003
 */
public class PersistenceManager {

	private static GraphLoad gl;
	private static ObjectLoad ol;
	private static RelationshipLoad rl;
	private static PropertyLoad pl;


	/**
	 * Constructor for the PersistenceManager object
	 */
	public PersistenceManager() {

	}



	/**
	 * Description of the Method
	 * 
	 * @param abs
	 *            Description of the Parameter
	 * @exception UnknowFormat
	 *                Description of the Exception
	 * @exception CannotLoad
	 *                Description of the Exception
	 */
	public void restorePreferences(IDEState ids, GUIResources resources, IDEUpdater updater) throws UnknowFormat,
	CannotLoad {
		JFileChooser jfc = new JFileChooser();
		File homedir = jfc.getCurrentDirectory();
		String filename = homedir.getPath() + "/.idk/idkproperties.xml";
		if (new File(filename).exists()) {
			try {

				DOMParser parser = new DOMParser();

				// Parse the Document
				// and traverse the DOM
				parser.parse("file:" + filename);
				Document doc = parser.getDocument();
				NodeList nl = doc.getElementsByTagName("preferences");
				Preferences prefs = Preferences.fromXML(nl.item(0));
				ids.prefs = prefs;
				ids.getLastFiles().clear();
				nl = nl.item(0).getChildNodes();
				for (int k = 0; k < nl.getLength(); k++) {
					Node n = nl.item(k);
					if (n.getNodeName().equals("lastfile")) {
						if (n.getChildNodes().getLength() > 0) {
							Node file = n.getChildNodes().item(0);
							if (file.getNodeType() == Node.TEXT_NODE) {
								String path = file.getNodeValue();
								HistoryManager.updateHistory(new File(path), resources, ids,updater);						

							} 

						}
					}
					if (n.getNodeName().equals("lastimage")) {
						if (n.getChildNodes().getLength() > 0) {
							Node file = n.getChildNodes().item(0);
							if (file.getNodeType() == Node.TEXT_NODE) {
								String path = file.getNodeValue();
								HistoryManager.updateHistory(new File(path), resources, ids,updater);		
							}
						}
					}

				}
				// deleteBadRelationships(gm);
			}
			/*
			 * catch (java.io.FileNotFoundException fnf) { throw new
			 * CannotLoad("File " + filename + " not found"); }
			 */catch (java.io.IOException ioe) {

				 throw new CannotLoad("File " + filename
						 + " could not be loaded. " + ioe.getMessage());
			 } catch (org.xml.sax.SAXException se) {
				 se.printStackTrace();
				 throw new UnknowFormat("File " + filename
						 + " is not valid xml." + se.getMessage());
			 }
		}

	}

	/**
	 * Description of the Method
	 * 
	 * @param ids
	 *            Description of the Parameter
	 */
	public void savePreferences(IDEState ids) {
		try {			
			File homedir =  new File(System.getProperty("user.home"));
			String filename = homedir.getPath() + "/.idk/idkproperties.xml";
			if (!new File(homedir.getPath() + "/.idk").exists() ){
				new File(homedir.getPath() + "/.idk").mkdir();
			}
			java.io.FileOutputStream fos = new java.io.FileOutputStream(
					filename);

			fos
			.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<preferences>\n"
					.getBytes());
			Vector v = ids.getLastFiles();
			for (int k = 0; k < v.size(); k++) {
				File current = (File) v.elementAt(k);
				if (current!=null)
					fos.write(("<lastfile>" + ingenias.generator.util.Conversor.replaceInvalidChar(current.getPath()) + "</lastfile>\n").getBytes());
			}
			if (ids.getCurrentImageFolder() != null) {
				fos.write(("<lastimage>"
						+ ingenias.generator.util.Conversor
						.replaceInvalidChar(ids.getCurrentImageFolder()
								.getPath()) + "</lastimage>\n")
								.getBytes());
			}
			fos.write(ids.prefs.toXML().getBytes());

			fos.write("</preferences>".getBytes());

			fos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	/**
	 * Description of the Method
	 * 
	 * @param input
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 * @exception ingenias.exception.UnknowFormat
	 *                Description of Exception
	 * @exception ingenias.exception.DamagedFormat
	 *                Description of Exception
	 * @exception CannotLoad
	 *                Description of Exception
	 */
	public IDEState mergeFile(String input, IDEState ids,GUIResources resources)
			throws ingenias.exception.UnknowFormat,
			ingenias.exception.DamagedFormat, CannotLoad {

		try {

			DOMParser parser = new DOMParser();
			// Parse the Document
			// and traverse the DOM
			parser.parse(new InputSource(new FileInputStream(input)));

			Document doc = parser.getDocument();
			NodeList reltags = doc.getElementsByTagName("relationship");
			NodeList objects = doc.getElementsByTagName("object");
			NodeList nodesGLX = doc.getElementsByTagName("node");
			NodeList models = doc.getElementsByTagName("model");

			Vector<NAryEdgeEntity> rels = RelationshipManager
					.getRelationshipsVector(ids.gm);
			Hashtable<String, NAryEdgeEntity> trels = new Hashtable<String, NAryEdgeEntity>();
			for (NAryEdgeEntity nedge : rels) {
				trels.put(nedge.getId(), nedge);
			}
			for (int k = 0; k < reltags.getLength(); k++) {
				String id = reltags.item(k).getAttributes().getNamedItem("id")
						.getNodeValue();
				String nid = id;
				while (trels.containsKey(nid)
						|| ids.om.findUserObject(nid).size() > 0) {
					nid = nid + "_";
				}
				if (!id.equals(nid)) {

					reltags.item(k).getAttributes().getNamedItem("id")
					.setNodeValue(nid);
					renameNodes(nodesGLX, id, nid);
					renameNodes(objects, id, nid);
				}
			}
			for (int k = 0; k < models.getLength(); k++) {
				String id = models.item(k).getAttributes().getNamedItem("id")
						.getNodeValue();
				String nid = id;
				while (ids.gm.getModel(nid) != null) {
					nid = nid + "_";
				}
				if (!id.equals(nid)) {

					models.item(k).getAttributes().getNamedItem("id")
					.setNodeValue(nid);
					renameNodes(models, id, nid);
					renameNodes(objects, id, nid);
				}
			}

			String version = "1.0";
			try {
				version = this.getVersion(doc);
			} catch (VersionNotFound vnf) {
				// vnf.printStackTrace();
			}

			this.setVersion(version,ids);
			// ol.restoreObject(ids.om, ids.gm, doc);
			restoreObjects(ids.om, ids.gm, doc);
			rl.restoreRelationships(ids.om, ids.gm, doc);
			try {
				gl.restoreModels(ids, resources,doc);
			} catch (ingenias.exception.CannotLoadDiagram cld) {
				throw new ingenias.exception.DamagedFormat(cld.getMessage());
			}

			Vector<NAryEdgeEntity> rels1 = RelationshipManager
					.getRelationshipsVector(ids.gm);
			HashSet<String> trels1 = new HashSet<String>();
			for (NAryEdgeEntity nedge : rels) {
				trels1.add(nedge.getId());
			}
			int n = 1;
			// deleteBadRelationships(gm);

		} catch (java.io.FileNotFoundException fnf) {
			throw new CannotLoad("File " + input + " not found");
		} catch (java.io.IOException ioe) {
			throw new CannotLoad("File " + input + " could not be loaded. "
					+ ioe.getMessage());
		} catch (org.xml.sax.SAXException se) {
			throw new UnknowFormat("File " + input + " is not valid xml."
					+ se.getMessage());
		} catch (UnknownVersion uv) {
			throw new ingenias.exception.CannotLoad(
					"File "
							+ input
							+ " version is not recognised. Try downloading a new version of the IDE in http://ingenias.sourceforge.net");
		} catch (ClassNotFoundException cnf) {
			cnf.printStackTrace();

		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		} catch (InstantiationException ie) {
			ie.printStackTrace();
		} catch (InvocationTargetException ite) {
			ite.printStackTrace();
		}

		return ids;
	}

	private void renameNodes(NodeList nodesGLX, String id, String nid) {
		for (int k = 0; k < nodesGLX.getLength(); k++) {
			if (nodesGLX.item(k).getAttributes() != null
					&& nodesGLX.item(k).getAttributes().getNamedItem("id") != null) {
				String atid = nodesGLX.item(k).getAttributes().getNamedItem(
						"id").getNodeValue();
				if (atid.equals(id)) {
					nodesGLX.item(k).getAttributes().getNamedItem("id")
					.setNodeValue(nid);
				}
			}

		}

	}

	/**
	 * Description of the Method
	 * 
	 * @param input
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 * @exception ingenias.exception.UnknowFormat
	 *                Description of Exception
	 * @exception ingenias.exception.DamagedFormat
	 *                Description of Exception
	 * @exception CannotLoad
	 *                Description of Exception
	 */

	public synchronized void loadWithoutListeners(String input,GUIResources resources, Properties oldProperties, IDEState ids)
			throws ingenias.exception.UnknowFormat,
			ingenias.exception.DamagedFormat, CannotLoad {

		FileChannel channel=null;
		FileLock lock=null;
		File lockFile=new File(System.getProperty("user.home")+"/.idk/lock");
		if (!lockFile.exists())
			try {
				lockFile.createNewFile();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		try {
			channel = new RandomAccessFile(lockFile, "rw").getChannel();
			// Use the file channel to create a lock on the file.
			// This method blocks until it can retrieve the lock.
			// Being the load method synchronized, it ensures in-jvm consistency. The lock ensures out-jvm consistency
			int timeout=0;
			while (timeout<10000 && lock==null){
				try {
					lock = channel.lock();
				} catch (OverlappingFileLockException ofl){
					try {
						Thread.currentThread().sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					timeout=timeout+500;
				}
			}
			if (timeout>=10000)
				throw new CannotLoad("Could not acquire a lock on file "+input);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			RelationshipManager.clearRelationships();

			try {

				DOMParser parser = new DOMParser();
				// Parse the Document
				// and traverse the DOM
				InputSource is = new InputSource(new FileInputStream(input));
				is.setEncoding("UTF-8");
				parser.parse(is);

				Document doc = parser.getDocument();

				String version = "1.0";
				try {
					version = this.getVersion(doc);
				} catch (VersionNotFound vnf) {
					// vnf.printStackTrace();
				}

				this.setVersion(version,ids);

				ids.prop.putAll(oldProperties);			
				// ol.restoreObject(ids.om, ids.gm, doc);
				restoreObjects(ids.om, ids.gm, doc);
				rl.restoreRelationships(ids.om, ids.gm, doc);

				try {

					gl.restoreModels(ids, resources,doc);
					Vector<ModelJGraph> mjg = ids.gm.getUOModels();
					for (int k = 0; k < mjg.size(); k++) {
						mjg.elementAt(k).setSelectionCells(new Object[0]);
						mjg.elementAt(k).setUI(new EmbeddedAndPopupCellEditor(ids,resources));
					}
				} catch (ingenias.exception.CannotLoadDiagram cld) {
					throw new ingenias.exception.DamagedFormat(cld.getMessage());
				}

				NodeList leafpackages = doc.getElementsByTagName("leafpackages");
				if (leafpackages != null && leafpackages.getLength() > 0) {
					NodeList paths = leafpackages.item(0).getChildNodes();
					ids.gm.toExpad = new Vector<TreePath>();
					for (int k = 0; k < paths.getLength(); k++) {
						if (paths.item(k).getNodeName().equalsIgnoreCase("path")) {
							NodeList pathToAdd = paths.item(k).getChildNodes();
							Vector<String> pathlist = new Vector<String>();
							for (int j = 0; j < pathToAdd.getLength(); j++) {
								if (pathToAdd.item(j).getNodeName()
										.equalsIgnoreCase("package")) {
									String pname = pathToAdd.item(j)
											.getAttributes().getNamedItem("id")
											.getNodeValue();								
									ids.gm.addPackage(pathlist.toArray(), pname);								
									pathlist.add(pname);

								}
							}
							TreePath tp = getPath(pathlist, ids.gm.arbolProyecto).getParentPath();
							ids.gm.toExpad.add(tp);
							ids.gm.getArbolProyecto().expandPath(tp);	
						}
					}

					for (TreePath tp:ids.gm.toExpad){
						Vector<Object> npath=new Vector<Object>(); 
						if (tp!=null){
							for (Object path:tp.getPath()){
								npath.add(path);
							}
							npath.remove(0);
							npath.insertElementAt(ids.gm.getArbolProyecto().getModel().getRoot(), 0);					
							ids.gm.getArbolProyecto().expandPath(new TreePath(npath.toArray()));	
						}
					}
					resources.setCurrentProgress(90);
				}
				//	ids.gm.arbolProyecto.validate();
				this.restoreProjectProperties(doc, ids); // It has to be done at
				// this moment to open
				// the different diagram
				// tabs
				// deleteBadRelationships(gm);

			} catch (java.io.FileNotFoundException fnf) {
				throw new CannotLoad("File " + input + " not found");
			} catch (java.io.IOException ioe) {
				throw new CannotLoad("File " + input + " could not be loaded. "
						+ ioe.getMessage());
			} catch (org.xml.sax.SAXException se) {
				throw new UnknowFormat("File " + input + " is not valid xml."
						+ se.getMessage());
			} catch (UnknownVersion uv) {
				throw new ingenias.exception.CannotLoad(
						"File "
								+ input
								+ " version is not recognised. Try downloading a new version of the IDE in http://ingenias.sourceforge.net");
			} catch (ClassNotFoundException cnf) {
				cnf.printStackTrace();

			} catch (NoSuchMethodException nsme) {
				nsme.printStackTrace();
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();
			} catch (InstantiationException ie) {
				ie.printStackTrace();
			} catch (InvocationTargetException ite) {
				ite.printStackTrace();
			}


		} catch (ingenias.exception.UnknowFormat uf){
			try {
				ids.editor.setEnabled(true);
				lock.release();
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			throw uf;
		} catch (ingenias.exception.DamagedFormat df){
			try {
				ids.editor.setEnabled(true);
				lock.release();
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			throw df;
		} catch (CannotLoad cl){
			try {
				ids.editor.setEnabled(true);
				lock.release();
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			throw cl;
		}
		try {
			lock.release();
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	/**
	 * Description of the Method
	 * 
	 * @param input
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 * @exception ingenias.exception.UnknowFormat
	 *                Description of Exception
	 * @exception ingenias.exception.DamagedFormat
	 *                Description of Exception
	 * @exception CannotLoad
	 *                Description of Exception
	 */

	public void load(String input,GUIResources resources, Properties oldProperties, IDEState ids)
			throws ingenias.exception.UnknowFormat,
			ingenias.exception.DamagedFormat, CannotLoad {

		ModelJGraph.disableAllListeners();
		loadWithoutListeners(input,resources,oldProperties,ids);
		ModelJGraph.enableAllListeners();
		for (ModelJGraph mjg:ids.gm.getUOModels()){
			mjg.createListeners();
			mjg.getListenerContainer().setEnabled(true);
			mjg.getListenerContainer().setToFrontVisibleChildren();
			mjg.getListenerContainer().graphChanged(null); // to refresh the container layout			
		}
		ids.editor.setEnabled(true);



	}




	private TreePath getPath(Vector<String> pathlist, DnDJTree arbolProyecto) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) arbolProyecto
				.getModel().getRoot();
		pathlist.remove(0);
		Vector<TreeNode> tn = new Vector<TreeNode>();
		tn.add(node);
		for (String name : pathlist) {
			boolean found = false;
			for (int k = 0; k < node.getChildCount() && !found; k++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
						.getChildAt(k);
				found = child.getUserObject().toString().equalsIgnoreCase(name);
				if (found) {
					node = child;
					tn.add(node);
				}
			}
		}
		return new TreePath(tn.toArray());
	}

	/**
	 * Description of the Method
	 * 
	 * @param output
	 *            Description of the Parameter
	 * @param ids
	 *            Description of the Parameter
	 * @exception java.io.IOException
	 *                Description of Exception
	 */
	public void save(File output, IDEState ids) throws java.io.IOException {
		this.saveAllModels(ids, new RelationshipManager(), output);
	}

	/**
	 * Sets the version attribute of the PersistenceManager object
	 * 
	 * @param ver
	 *            The new version value
	 * @exception UnknownVersion
	 *                Description of Exception
	 */
	private void setVersion(String ver, IDEState ids) throws UnknownVersion {
		if (ver.equals("1.0")) {
			gl = new GraphLoadImp1();
			rl = new RelationshipLoadImp1();
			ol = new ObjectLoadImp1(new BrowserImp(ids));
			pl = new PropertyLoadImp1();
		} else {
			if (ver.equals("1.1") ||ver.equals("1.2")) {
				gl = new GraphLoadImp2();
				rl = new RelationshipLoadImp2();
				ol = new ObjectLoadImp1(new BrowserImp(ids));
				pl = new PropertyLoadImp1();
			} else {
				gl = new GraphLoadImp1();
				rl = new RelationshipLoadImp1();
				ol = new ObjectLoadImp1(new BrowserImp(ids));
				pl = new PropertyLoadImp1();
			} 
		}

		// throw new UnknownVersion();
	}

	/**
	 * Gets the version attribute of the PersistenceManager object
	 * 
	 * @param doc
	 *            Description of Parameter
	 * @return The version value
	 * @exception ingenias.exception.VersionNotFound
	 *                Description of Exception
	 */
	private String getVersion(org.w3c.dom.Document doc)
			throws ingenias.exception.VersionNotFound {
		try {
			String version = "";
			NodeList nl = doc.getElementsByTagName("project");
			version = nl.item(0).getAttributes().getNamedItem("version")
					.getNodeValue();
			return version;
		} catch (Exception e) {
			throw new VersionNotFound(e.getMessage());
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param doc
	 *            Description of Parameter
	 * @param ids
	 *            Description of Parameter
	 */
	private void restoreProjectProperties(Document doc, IDEState ids) {
		NodeList nl = doc.getElementsByTagName("project");
		for (int k = 0; k < nl.getLength(); k++) {
			Node current = nl.item(k);
			if (current.getNodeName().equalsIgnoreCase("project")) {
				String cid = current.getAttributes().getNamedItem("cid")
						.getNodeValue();
				Editor.idCounter = Integer.parseInt(cid);
				NodeList nll = current.getChildNodes();
				for (int l = 0; l < nll.getLength(); l++) {
					current = nll.item(l);
					if (current.getNodeName().equalsIgnoreCase(
							"projectproperties")) {
						NodeList nl1 = current.getChildNodes();
						for (int j = 0; j < nl1.getLength(); j++) {
							try {
								if (nl1.item(j).getNodeName().equalsIgnoreCase(
										"projectproperty")) {
									ProjectProperty pp = ProjectProperty
											.fromXML(nl1.item(j));
									ids.prop.put(pp.module + ":" + pp.key, pp);
								}
							} catch (InvalidProjectProperty ipp) {
								// ipp.printStackTrace();
							}
							if (nl1.item(j).getNodeName().equals(
									"openeddiagram")) {
								if (nl1.item(j).getChildNodes().getLength() > 0) {
									Node diagram = nl1.item(j).getChildNodes()
											.item(0);
									if (diagram.getNodeType() == Node.TEXT_NODE) {
										String diagramT = diagram
												.getNodeValue();
										if (ids.gm.getModel(diagramT) != null){
											ids.editor.changeGraph(ids.gm
													.getModel(diagramT));

										}
									}
								}
							}
						}
					}
				}
			}
		}

	}

	/**
	 * Description of the Method
	 * 
	 * @param om
	 *            Description of Parameter
	 * @param gm
	 *            Description of Parameter
	 * @param doc
	 *            Description of Parameter
	 * @exception ClassNotFoundException
	 *                Description of Exception
	 * @exception NoSuchMethodException
	 *                Description of Exception
	 * @exception IllegalAccessException
	 *                Description of Exception
	 * @exception InstantiationException
	 *                Description of Exception
	 * @exception InvocationTargetException
	 *                Description of Exception
	 */
	private void restoreObjects(ObjectManager om, GraphManager gm, Document doc)
			throws ClassNotFoundException, NoSuchMethodException,
			IllegalAccessException, InstantiationException,
			InvocationTargetException {
		NodeList nl = doc.getElementsByTagName("objects");
		NodeList objects = nl.item(0).getChildNodes();
		for (int k = 0; k < objects.getLength(); k++) {
			Node n = objects.item(k);
			if (n.getNodeName().equalsIgnoreCase("object")) {
				ol.restoreObject(om, gm, n);
			}
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param prop
	 *            Description of Parameter
	 * @param fos
	 *            Description of Parameter
	 * @exception java.io.IOException
	 *                Description of Exception
	 */
	private void saveProjectProperties(IDEState ids, OutputStreamWriter fos)
			throws java.io.IOException {
		fos.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<project cid=\""
				+ Editor.idCounter + "\" version=\"1.2\">\n");
		Enumeration ppenumeration = ids.prop.elements();
		fos.write(("<projectproperties>\n"));
		while (ppenumeration.hasMoreElements()) {
			ProjectProperty pp = (ProjectProperty) ppenumeration.nextElement();
			fos.write(pp.toXML());
		}
		Vector<String> diagrams = ids.editor.getOpenedDiagrams();

		for (String diagram : diagrams) {
			fos.write(" <openeddiagram>"
					+ ingenias.generator.util.Conversor
					.replaceInvalidChar(diagram) + "</openeddiagram>");
		}

		fos.write(("</projectproperties>\n"));

	}

	/**
	 * Description of the Method
	 * 
	 * @param prop
	 *            Description of Parameter
	 * @param leafpackages
	 *            Description of Parameter
	 * @param fos
	 *            Description of Parameter
	 * @exception java.io.IOException
	 *                Description of Exception
	 */
	private void saveProjectTree(IDEState ids, Vector leafpackages,
			OutputStreamWriter fos) throws java.io.IOException {

		TreePath parent = new TreePath(((DefaultMutableTreeNode)ids.gm.arbolProyecto.getModel().getRoot()).getPath());
		Enumeration<TreePath> leaf = ids.gm.arbolProyecto
				.getExpandedDescendants(parent);
		Vector<TreePath> expandedpaths=new Vector<TreePath>();
		while (leaf!=null && leaf.hasMoreElements()){
			expandedpaths.add(leaf.nextElement());
		}

		DefaultMutableTreeNode currentLeaf=((DefaultMutableTreeNode)ids.gm.arbolProyecto.getModel().getRoot()).getFirstLeaf();

		Enumeration enumerationPack = leafpackages.elements();		

		this.saveProjectProperties(ids, fos);

		fos.write("<leafpackages>\n");
		while (currentLeaf != null) {
			if (currentLeaf.getUserObject()!=null){
				TreePath currentLeafPath = new TreePath(currentLeaf.getPath());
				if (expandedpaths.contains(currentLeafPath)){
					fos.write("   <path expanded=\"true\">\n");
				} else
					fos.write("   <path>\n");
				Object[] packPath = currentLeafPath.getPath();
				for (int k = 0; k < packPath.length; k++) {
					String packageName = packPath[k].toString();
					fos.write(("    <package id=\"" + packageName + "\"/>\n"));
				}
				fos.write("   </path>\n");				
			}
			currentLeaf=currentLeaf.getNextLeaf();
		}		
		fos.write("</leafpackages>\n");


	}

	/**
	 * Description of the Method
	 * 
	 * @param prop
	 *            Description of Parameter
	 * @param om
	 *            Description of Parameter
	 * @param rm
	 *            Description of Parameter
	 * @param gm
	 *            Description of Parameter
	 * @param output
	 *            Description of Parameter
	 * @exception IOException
	 *                Description of Exception
	 * @exception FileNotFoundException
	 *                Description of Exception
	 */
	private synchronized void saveAllModels(IDEState ids, RelationshipManager rm, File output)
			throws IOException, FileNotFoundException {
		FileChannel channel=null;
		FileLock lock=null;
		File lockFile=new File(System.getProperty("user.home")+"/.idk/lock");
		if (!lockFile.exists())
			try {
				lockFile.createNewFile();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		try {



			channel = new RandomAccessFile(lockFile, "rw").getChannel();
			// Use the file channel to create a lock on the file.
			// This method blocks until it can retrieve the lock.
			// Being the load method synchronized, it ensures in-jvm consistency. The lock ensures out-jvm consistency
			int timeout=0;
			while (timeout<10000 && lock==null){
				try {
					lock = channel.lock();
				} catch (OverlappingFileLockException ofl){
					try {
						Thread.currentThread().sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					timeout=timeout+500;
				}
			}
			if (timeout>=10000)
				throw new IOException("Could not acquire a lock on file "+output.getCanonicalPath());


			RelationshipSave rs = new RelationshipSave();
			ObjectSave objsave = new ObjectSave();
			Vector<TreeNode[]> models = ids.gm.getModels();
			// Vector objects=om.getObjects();
			Enumeration enumeration = models.elements();
			output.getParentFile().mkdirs(); // to create the path if necessary
			FileOutputStream os = new FileOutputStream(output);
			OutputStreamWriter fos = new OutputStreamWriter(os, "UTF-8");

			this.saveProjectTree(ids, new Vector(), fos);

			objsave.saveObjects(ids.om, ids.gm, fos);

			rs.saveRelationships(rm, ids.gm, fos);

			fos.write("<models> \n");
			while (enumeration.hasMoreElements()) {
				TreeNode[] modelPath = (TreeNode[]) enumeration.nextElement();
				ModelJGraph model = (ModelJGraph) ((DefaultMutableTreeNode) modelPath[modelPath.length - 1])
						.getUserObject();

				GraphSave.saveModel(model, modelPath, fos);

			}

			fos.write("</models>\n");
			fos.write("</project>\n");
			fos.close();
		} catch (IOException th) {
			th.printStackTrace();
			try {
				lock.release();
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			throw th;

		} 
		try {
			lock.release();
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Gets the gL attribute of the PersistenceManager class
	 * 
	 * @return The gL value
	 */
	public static GraphLoad getGL() {
		return gl;
	}

	/**
	 * Gets the oL attribute of the PersistenceManager class
	 * 
	 * @return The oL value
	 */
	public static ObjectLoad getOL() {
		return ol;
	}

	/**
	 * Gets the rL attribute of the PersistenceManager class
	 * 
	 * @return The rL value
	 */
	public static RelationshipLoad getRL() {
		return rl;
	}

	/**
	 * Gets the pL attribute of the PersistenceManager class
	 * 
	 * @return The pL value
	 */
	public static PropertyLoad getPL() {
		return pl;
	}






}
