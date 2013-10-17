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
package ingenias.editor.filters;


import ingenias.editor.cell.RenderComponentManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.swing.JCheckBoxMenuItem;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



public class FilterManager {

	public static DiagramFilter getINGENIASConfiguration(ClassLoader cl){

		DiagramFilter defaultFilter=null;
		try {
			if (FilterManager.getDefaultFilterFromClassLoader(FilterManager.class.getClassLoader())!=null)
			defaultFilter = obtainDiagramFilter(getDefaultFilterFromClassLoader(cl));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return defaultFilter;
	}

	public static InputStream getDefaultFilterFromClassLoader(ClassLoader cl) throws java.io.FileNotFoundException {
		InputStream  defaultFilter=null;
		// Load the directory as a resource
		URL dir_url = FilterManager.class.getResource("/configs");
		// Turn the resource into a File object		
		if (dir_url!=null){
		File configs;
		try {
			try {
				configs = new File(dir_url.toURI()); // this method works well when the config is in a separated file
				// List the directory
				if (!configs.exists())
					throw new RuntimeException("configs folder does not exist. Please, create one with that name");
				File[] containedConfigs=configs.listFiles();
				for (File config:containedConfigs){			
					if (config.isFile() && config.getName().equalsIgnoreCase("default.xml") ){				 
						defaultFilter=new FileInputStream(config.getCanonicalPath());				
					}

				}
			} catch (IllegalArgumentException iae){
				// this happens when the exception "URI is not hierarchical" is raised
				// it means the configs are inside a jar file and are not accesible as a file themselves

				// code from http://stackoverflow.com/questions/1429172/how-do-i-list-the-files-inside-a-jar-file
				CodeSource src = FilterManager.class.getProtectionDomain().getCodeSource();
				if (src != null) {
					URL jar = src.getLocation();
					ZipInputStream zip;
					ZipFile zf=null;
					ZipEntry nextEntry=null;
					try {
						zf=new ZipFile(new File(jar.toURI()));
						zip = new ZipInputStream(jar.openStream());
						nextEntry=zip.getNextEntry();
						while (nextEntry!=null && defaultFilter==null){
							String entryName = nextEntry.getName();
							if( entryName.startsWith("configs") && !nextEntry.isDirectory() &&
									(entryName.endsWith(".xml")|| entryName.endsWith(".XML"))){
								defaultFilter=zf.getInputStream(nextEntry);		
							}
							nextEntry=zip.getNextEntry();
						} 
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("configs folder could not be accessed. Please, create one with that name");
		}}
		return defaultFilter;
	/*	String filePath="/configs/default.xml";

		InputStream filterIS=null;
		if (cl instanceof java.net.URLClassLoader) {					
			java.net.URL baseURL = ((java.net.URLClassLoader)cl).getResource(filePath);
			if (baseURL == null) {
				try {
					System.err.println("Loading.....");					
					filterIS=new URL(filePath).openStream();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new java.io.FileNotFoundException(filePath +
					" was not found in the classpath or current jar");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new java.io.FileNotFoundException(filePath +
					" was not found in the classpath or current jar");
				}

			}
			else {
				try {
					filterIS=baseURL.openStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					filterIS=new FileInputStream(filePath);					
				}

			}
			//     System.err.println(baseURL);
		}
		else {
			ClassLoader loader = cl;
			try {
				System.err.println("Loading..... from "+cl.getClass().getName());
				//new URL(filePath).openStream().close();
				if (loader.getResource(filePath)==null)
					throw new IOException ();
				return loader.getResourceAsStream(filePath);				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new java.io.FileNotFoundException(filePath +
				" was not found in the classpath or current jar");
			}
			//throw new java.io.FileNotFoundException(filePath +
			//    " was not found in the classpath or current jar");
		}
		return filterIS;*/
	}


	
	public static Vector<DiagramFilter> listAvailableConfigurations(){
		Vector<DiagramFilter>  list=new  Vector<DiagramFilter> ();
		// Load the directory as a resource
		URL dir_url = FilterManager.class.getResource("/configs");
		// Turn the resource into a File object		
		if (dir_url!=null){
		File configs;
		try {
			try {
				configs = new File(dir_url.toURI()); // this method works well when the config is in a separated file
				// List the directory
				if (!configs.exists())
					throw new RuntimeException("configs folder does not exist. Please, create one with that name");
				File[] containedConfigs=configs.listFiles();
				for (File config:containedConfigs){			
					if (config.isFile() && (config.getName().endsWith(".xml") || config.getName().endsWith(".XML"))){				 
						list.add(obtainDiagramFilter(config.getAbsolutePath()));				
					}

				}
			} catch (IllegalArgumentException iae){
				// this happens when the exception "URI is not hierarchical" is raised
				// it means the configs are inside a jar file and are not accesible as a file themselves

				// code from http://stackoverflow.com/questions/1429172/how-do-i-list-the-files-inside-a-jar-file
				CodeSource src = FilterManager.class.getProtectionDomain().getCodeSource();
				if (src != null) {
					URL jar = src.getLocation();
					ZipInputStream zip;
					ZipFile zf=null;
					ZipEntry nextEntry=null;
					try {
						zf=new ZipFile(new File(jar.toURI()));
						zip = new ZipInputStream(jar.openStream());
						nextEntry=zip.getNextEntry();
						while (nextEntry!=null){
							String entryName = nextEntry.getName();
							if( entryName.startsWith("configs") && !nextEntry.isDirectory() &&
									(entryName.endsWith(".xml")|| entryName.endsWith(".XML"))){
								list.add(obtainDiagramFilter(zf.getInputStream(nextEntry)));		
							}
							nextEntry=zip.getNextEntry();
						} 
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("configs folder could not be accessed. Please, create one with that name");
		}}
		return list;
	}

	public static ingenias.editor.filters.DiagramFilter obtainDiagramFilter(InputStream xmlConfiguration){
		DiagramFilter df=new DiagramFilter();
		Hashtable<String,Vector<String>> tempAllowedEntities=new Hashtable<String,Vector<String>>();
		Hashtable<String,Vector<String>> tempAllowedRelationships=new Hashtable<String,Vector<String>>();
		try {
			org.apache.xerces.parsers.DOMParser parser = new org.apache.xerces.parsers.DOMParser();		
			parser.parse(new InputSource(xmlConfiguration));
			Document doc = parser.getDocument();
			NodeList nl = doc.getElementsByTagName("name");
			String filterName=nl.item(0).getChildNodes().item(0).getNodeValue();
			df.setName(filterName);
			df.setCurrentAllowedEntities(tempAllowedEntities);
			df.setCurrentAllowedRelationships(tempAllowedRelationships);

			nl = doc.getElementsByTagName("diagram");
			for (int k=0;k<nl.getLength();k++){
				Node node=nl.item(k);
				String diagramName=node.getAttributes().getNamedItem("name").getNodeValue();
				NodeList entityNodes=node.getChildNodes();
				Vector<String> entities=new Vector<String>();
				Vector<String> relationships=new Vector<String>();
				for (int indexEnt=0;indexEnt<entityNodes.getLength();indexEnt++){
					if (entityNodes.item(indexEnt).getNodeName().equalsIgnoreCase("entity")){
						Node entity=entityNodes.item(indexEnt);
						entities.add(entity.getChildNodes().item(0).getNodeValue());		
					}
					if (entityNodes.item(indexEnt).getNodeName().equalsIgnoreCase("relationship")){
						Node relationship=entityNodes.item(indexEnt);
						relationships.add(relationship.getChildNodes().item(0).getNodeValue());		
					}
				}
				tempAllowedEntities.put(diagramName, entities);
				tempAllowedRelationships.put(diagramName,relationships);
			}			

			return df;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static ingenias.editor.filters.DiagramFilter obtainDiagramFilter(String xmlConfiguration){		
		try {
			return obtainDiagramFilter(new FileInputStream(xmlConfiguration));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public static void main(String args[]){
		System.err.println(listAvailableConfigurations());
	}


}
