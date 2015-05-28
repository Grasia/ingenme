
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


package ingenias.editor;

import javax.swing.*;
import javax.swing.tree.*;

import org.apache.xerces.parsers.DOMParser;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import ingenias.editor.events.DiagramChangeHandler;
import ingenias.editor.filters.DiagramFilter;
import ingenias.editor.filters.FilterManager;
import ingenias.editor.widget.DnDJTree;
import ingenias.editor.widget.DnDJTreeObject;
import ingenias.generator.browser.BrowserImp;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.*;


public class IDEState  implements java.io.Serializable, DiagramChangeHandler {

	

	public Editor editor;
	
	public ObjectManager om;
	
	public GraphManager gm;
	
	public Properties prop=null;
	
	public Preferences prefs=new Preferences();	
	
	private File currentFile;	

	public File currentImageFolder;
	// stores last folder associated with images

	private File currentFileFolder;
	// stores last folder associated with images

	private  boolean changed = false;
	// true if some change was performed

	private boolean busy=false;
	
	private Vector<File> lastFiles = new Vector<File>();
	
	private Vector<DiagramChangeHandler> stateChangeListeners=new Vector<DiagramChangeHandler>();
	
	
	private Hashtable<String,Vector<String>> currentAllowedEntities=new Hashtable<String,Vector<String>>();
	
	private String currentFilter="";

	private DiagramFilter diagramFilter;
	
	private IDEState(final Editor editor,DefaultMutableTreeNode rootObjetos,
			DnDJTreeObject arbolObjectos,
			DefaultMutableTreeNode rootProyectos,
			DnDJTree arbolProyectos, 
			Properties oldProperties){
		this.editor=editor;
		this.gm=GraphManager.initInstance(rootProyectos,arbolProyectos);
		this.om= ObjectManager.initialise(rootObjetos,arbolObjectos);
		this.prop=new Properties();
		prop.putAll(oldProperties);
		arbolObjectos.setBrowser(new BrowserImp(this));
		//ingenias.editor.persistence.PersistenceManager.defaultProperties(prop);
	}
	
	public DiagramFilter getDiagramFilter() {
		return diagramFilter;
	}


	public void setDiagramFilter(DiagramFilter diagramFilter) {
		this.diagramFilter = diagramFilter;
	}


	public Hashtable<String, Vector<String>> getCurrentAllowedEntities() {
		return currentAllowedEntities;
	}


	public void setCurrentAllowedEntities(
			Hashtable<String, Vector<String>> currentAllowedEntities) {
		this.currentAllowedEntities = currentAllowedEntities;
	}

	
	public File getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(File currentFile) {
		this.currentFile = currentFile;
	}

	public File getCurrentImageFolder() {
		return currentImageFolder;
	}

	public void setCurrentImageFolder(File currentImageFolder) {
		this.currentImageFolder = currentImageFolder;
	}

	public File getCurrentFileFolder() {
		return currentFileFolder;
	}

	public void setCurrentFileFolder(File currentFileFolder) {
		this.currentFileFolder = currentFileFolder;
	}

	public  boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	

	public Vector<File> getLastFiles(){
		return lastFiles;
	}

	public void putProperty(ProjectProperty pp){
		this.prop.put(pp.module+":"+pp.key, pp);	  
	}

	public ProjectProperty getProperty(String module, String key){
		return (ProjectProperty) this.prop.get(module+":"+key);	  
	}

	private IDEState(Editor editor,GraphManager gm,ObjectManager om,  Properties oldProperties){
		this.om=om;
		this.gm=gm;

		om.arbolObjetos.setBrowser(new BrowserImp(this));
		this.editor=editor;
		this.prop=new Properties();
		prop.putAll(oldProperties);
		currentFile=null;
		try {
		 Vector<DiagramFilter> confs = FilterManager.listAvailableConfigurations();
		 for (DiagramFilter df:confs){				
				if (df.getName().equalsIgnoreCase("Full INGENIAS")){
					this.setDiagramFilter(df);			
				}
		 }
		//this.setDiagramFilter(FilterManager.getINGENIASConfiguration(this.getClass().getClassLoader()));
		} catch (Throwable t){
			System.err.println("Could not load the default filter from classpath");
			System.err.println(t.getMessage());
		}
		//  ingenias.editor.persistence.PersistenceManager.defaultProperties(prop);
	}

	public static IDEState emptyIDEState(){
		DefaultMutableTreeNode rootObjects = new DefaultMutableTreeNode("Objects");
		DnDJTreeObject treeObjects = new DnDJTreeObject(rootObjects);	
		treeObjects.setExpandsSelectedPaths(true);
		treeObjects.setName("ObjectsTree");
		DnDJTree treeProjects = new DnDJTree();
		treeProjects.setExpandsSelectedPaths(true);
		treeProjects.setName("ProjectsTree");
		DefaultMutableTreeNode rootProjects =(DefaultMutableTreeNode)treeProjects.getModel().getRoot();
		GraphManager gm = GraphManager.initInstance(rootProjects, treeProjects);
		ObjectManager om = ObjectManager.initialise(rootObjects, treeObjects);
		Properties prop=new Properties();
		prop.put("IDK:extfolder",
				new ProjectProperty("IDK","extfolder", "Extension Module Folder",
						"ext",
				"Folder where the IDE will find its new modules"));
		Preferences pref=new Preferences();
		Editor ed=new Editor(om,gm,  pref);
		
		return new IDEState(ed, gm, om, prop);
	}

	public IDEState createEmpty(){

		return new IDEState(this.editor,new DefaultMutableTreeNode(),new DnDJTreeObject(),
				new DefaultMutableTreeNode(),new DnDJTree(), new Properties());

	}

	public synchronized void setBusy() {
		while (busy)
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		busy=true;
		
	}
	public synchronized void setNotBusy() {
		busy=false;
		notifyAll();
	}

	public boolean isBusy(){
		return busy;
	}


	public void addNewDiagram(ModelJGraph mjg) {
		for (DiagramChangeHandler listener:this.stateChangeListeners)
			listener.addNewDiagram(mjg);
		
	}


	public void addNewPackage(Object[] path, String nombre) {
		for (DiagramChangeHandler listener:this.stateChangeListeners)
			listener.addNewPackage(path,nombre);
		
	}


	public void diagramDeleted(ModelJGraph mj) {
		for (DiagramChangeHandler listener:this.stateChangeListeners)
			listener.diagramDeleted(mj);
		
	}


	public void diagramPropertiesChanged(ModelJGraph mjg) {
		for (DiagramChangeHandler listener:this.stateChangeListeners)
			listener.diagramPropertiesChanged(mjg);		
	}


	public void diagramRenamed(ModelJGraph mjg) {
		for (DiagramChangeHandler listener:this.stateChangeListeners)
			listener.diagramRenamed(mjg);				
	}


	public void packageRenamed(String result) {
		for (DiagramChangeHandler listener:this.stateChangeListeners)
			listener.packageRenamed(result);	
	}

	public void addStateChangelistener(DiagramChangeHandler changesHandler) {
		stateChangeListeners.add(changesHandler);
	}
	
	public  Vector<DiagramChangeHandler> getStateChangelistener() {
		return stateChangeListeners;
	}

	public void otherChange() {
		for (DiagramChangeHandler listener:this.stateChangeListeners)
			listener.otherChange();	
		
	}
	

	
}