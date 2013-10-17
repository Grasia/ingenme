/*
 Copyright (C) 2002 Rafael Martinez, Jorge Gomez Sanz
 This file is part of INGENIAS IDE, a support tool for the INGENIAS
 methodology, availabe at http://grasia.fdi.ucm.es/ingenias or
 http://ingenias.sourceforge.net
 INGENIAS IDE is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.
 INGENIAS IDE is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with INGENIAS IDE; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ingenias.codeproc;

import ingenias.generator.browser.*;
import ingenias.generator.datatemplate.*;
import ingenias.generator.interpreter.Codegen;
import ingenias.editor.*;
import ingenias.editor.entities.Entity;
import ingenias.exception.NotFound;
import ingenias.exception.NullEntity;

import java.util.*;
import java.awt.geom.Rectangle2D;
import java.io.*;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;

/**
 *  This class generates HTML documentation from a INGENIAS specification
 *
 *@author     Jorge Gomez
 *@created    29 de marzo de 2003
 */
public class HTMLDocumentGenerator
extends ingenias.editor.extension.BasicCodeGeneratorImp {
	
	/**
	 *  Initialises HTML generation from a file containing INGENIAS specification
	 *  and files containing templates to fullfill
	 *
	 *@param  file             Path to file containing INGENIAS specification
	 *@param  diagramTemplate  Description of Parameter
	 *@param  indexTemplate    Description of Parameter
	 *@param  output           Output path for the specification
	 *@exception  Exception    Error accessing any file or malformed XML exception
	 */
	
	public HTMLDocumentGenerator(String file) throws Exception {
		super(file);
		this.addTemplate("templates/index.xml");
		this.addTemplate("templates/diagram.xml");
	}
	
	/**
	 *  Initialises HTML generation from an existing browser
	 *  and files containing templates to fullfill.
	 *
	 *@param  diagramTemplate  Description of Parameter
	 *@param  indexTemplate    Description of Parameter
	 *@exception  Exception    Description of Exception
	 */
	public HTMLDocumentGenerator(Browser browser) throws Exception {
		super(browser);
		this.addTemplate("templates/index.xml");
		this.addTemplate("templates/diagram.xml");
	}
	
	@Override
	public String getVersion() {
		return "@modhtmldoc.ver@";
	}
	
	public boolean verify() {
		return true;
	}
	
	public Vector<ProjectProperty> defaultProperties() {
		Vector<ProjectProperty> result=new Vector<ProjectProperty>();
		Properties p = new Properties();
		
		result.add(
				new ingenias.editor.ProjectProperty(this.getName(),"htmldoc", "HTML document folder",
						"html",
				"The document folder that will contain HTML version of this specification"));
		return result;
	}
	
	public String getName() {
		return "HTML Document generator";
	}
	
	public String getDescription() {
		return "It generates HTML documentation of your diagrams";
	}
	
	/**
	 *  Generates HTML code
	 *
	 *@exception  Exception  XML exception
	 */
	public Sequences generate() {
		Sequences seq = new Sequences();
		try {
			this.generateIndex(seq);
			this.generatePages(seq);
			copyResourceFromTo("logograsia.jpg", ( (ProjectProperty)this.getProperty("htmldoc")).
				value+"/logograsia.jpg");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return seq;
	}
	
	/**
	 *  Generates an index page for the documentation
	 *
	 *@exception  Exception  Description of Exception
	 */
	
	private String[] toArray(String path) {
		StringTokenizer st = new StringTokenizer(path);
		int tokens = st.countTokens();
		String[] result = new String[tokens];
		for (int k = 0; k < tokens; k++) {
			result[k] = st.nextToken();
		}
		return result;
	}
	
	private String toSafeName(String name){
		name=name.replace('/','_');
		name=name.replace('\\','_');
		name=name.replace(' ','_');
		return name;
		
	}
	
	private void generateIndex(Sequences p) throws Exception {
		
		Graph[] gs = browser.getGraphs();
	
		p.addVar(new Var("output",
				( (ProjectProperty)this.getProperty("htmldoc")).
				value));
		System.err.println("output:"+( (ProjectProperty)this.getProperty("htmldoc")).
				value);
		Hashtable pathtable = new Hashtable();
		
		Hashtable paths = new Hashtable();
		Hashtable depth = new Hashtable();
	
		
		Vector alreadyShown=new Vector();
		float increment=25f/gs.length;		
		for (int k = 0; k < gs.length; k++) {
			Repeat rp1=null;
			this.setProgress((int) (k*increment));
			String[] path=gs[k].getPath();
			boolean already=true;
			while (already){
				rp1 = new Repeat("paquete");
				p.addRepeat(rp1);
				int j=0;
				for (j=0;j<path.length-1 && already;j++){
					Repeat level=new Repeat("level");    	             
					rp1.add(level);         
					if (!alreadyShown.contains(path[j])){
						already=false;
						alreadyShown.add(path[j]);
					}
				}
				if (!already){
					Repeat onlypackage=new Repeat("onlypackage");
					rp1.add(onlypackage);
					onlypackage.add(new Var("name",path[j-1]));
					already=true;
				} else 
					already=false;
				
			}
			
			
			Repeat rp2;
			Graph g = gs[k];
			rp2 = new Repeat("graph");
			rp2.add(new Var("name", ingenias.generator.util.Conversor.replaceInvalidCharsForID(g.getName())));
			rp2.add(new Var("fname", this.toSafeName(g.getName())));
			rp2.add(new Var("tipo", g.getType()));
			rp2.add(new Var("image", toSafeName(g.getName()) + ".png"));
			g.generateImage( ( (ProjectProperty)this.getProperty("htmldoc")).
					value + "/" + toSafeName(g.getName()) + ".png");
			rp1.add(rp2);
			
		
			
		}
		
	}
	
	/**
	 *  Generates HTML pages to show diagrams.
	 *
	 *@exception  Exception  XML exception
	 */
	private void generatePages(Sequences p) throws Exception {
		Graph[] gs = browser.getGraphs();
		float increment=25f/gs.length;		
		for (int k = 0; k < gs.length; k++) {
			Graph g = gs[k];
			Repeat r = new Repeat("graph1");
			this.setProgress(25+(int) (k*increment));
			
									
			
			
			p.addRepeat(r);
			r.add(new Var("output",
					( (ProjectProperty)this.getProperty("htmldoc")).
					value));
			r.add(new Var("name", gs[k].getName()));
			r.add(new Var("fname", this.toSafeName(gs[k].getName())));
			r.add(new Var("image", toSafeName(gs[k].getName()) + ".png"));
			r.add(new Var("tipo", gs[k].getType()));
			
			try {
				System.err.println("adding"+gs[k].getAttributeByName("Description").getSimpleValue());
				r.add(new Var("description",
						gs[k].getAttributeByName("Description").getSimpleValue()));
			}
			catch (NotFound nf) {
				nf.printStackTrace();
				r.add(new Var("description",
						"No description available."));
			}
			
			GraphEntity[] gesWithDups = g.getEntitiesWithDuplicates();
			for (int j = 0; j < gesWithDups.length; j++) {
				Repeat ens = new Repeat("mapentities");
				GraphEntity ge = gesWithDups[j];				
				GraphEntityImp gei = (ingenias.generator.browser.GraphEntityImp)ge;
				DefaultGraphCell dgc1=gei.getDgc();
				AttributeMap attributes = dgc1.getAttributes();
				Rectangle2D bounds = org.jgraph.graph.GraphConstants.getBounds(attributes);
				
				ens.add(new Var("coordrect",""+(int)bounds.getMinX()+","+(int)bounds.getMinY()+","+(int)bounds.getMaxX()+","+(int)bounds.getMaxY()));
				ens.add(new Var("link",""+ge.getID()));
				r.add(ens);
			}
			
			GraphEntity[] ges = g.getEntities();			
			
			for (int j = 0; j < ges.length; j++) {
				Repeat ens = new Repeat("entities");
				GraphEntity ge = ges[j];
				
				GraphEntityImp gei = (ingenias.generator.browser.GraphEntityImp)ge;
				DefaultGraphCell dgc1=gei.getDgc();
				AttributeMap attributes = dgc1.getAttributes();
				Rectangle2D bounds = org.jgraph.graph.GraphConstants.getBounds(attributes);
				
				
				ens.add(new Var("coordrect",""+(int)bounds.getMinX()+","+(int)bounds.getMinY()+","+(int)bounds.getMaxX()+","+(int)bounds.getMaxY()));
				ens.add(new Var("link",""+ge.getID()));
				
				ens.add(new Var("name", ge.getID()));
				ens.add(new Var("tipo", ge.getType()));
				try {
					ens.add(new Var("description",
							ge.getAttributeByName("Description").getSimpleValue()));
					
				}
				catch (NotFound nf) {
					ens.add(new Var("description",
				"No description available."));
					nf.printStackTrace();
				}
				
				
				
				StringBuffer result=new StringBuffer();
				
				
				for (int m=0;m<gs.length;m++){
					GraphEntity[] dges;
					try {
						dges = gs[m].getEntities();
						boolean found=false;
						Vector mentioned=new Vector();
						for (int l=0;l<dges.length;l++){
							found=dges[l].getID().equals(ge.getID());
							if (found && !mentioned.contains(gs[m].getName())){
								Repeat diagram=new Repeat("relateddiagrams");								
								diagram.add(new Var("diagramname",toSafeName(gs[m].getName())));
								diagram.add(new Var("diagramtype",gs[m].getType()));
								ens.add(diagram);
								mentioned.add(gs[k].getName());
							}
						}
						
					} catch (NullEntity e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				
				GraphRelationship[] gr = ge.getRelationships();
				for (int i = 0; i < gr.length; i++) {
					Repeat rels = new Repeat("relationship");
					
					Repeat meRel = new Repeat("mapentry");
					rels.add(meRel);				
					meRel.add(new Var("coordrect","0,0,115,33"));
					meRel.add(new Var("link","#link"));
					
					rels.add(new Var("name", gr[i].getType()));
					GraphRole[] groles = gr[i].getRoles();
					for (int h = 0; h < groles.length; h++) {
						Repeat rroles = new Repeat("roles");
						rroles.add(new Var("name", groles[h].getName()));
						rroles.add(new Var("player", groles[h].getPlayer().getID()));
						rels.add(rroles);
					}
					ens.add(rels);
				}
				r.add(ens);
			}
		}
	}
	
	private File copyResourceFromTo(
			String from, String to)
	throws FileNotFoundException, IOException {
		// the resource files are packaged in the same jar as this class.
		// hence, it is not recommendable to move this method to another
		// class to facilitate reuse unless a class loader is passed
		// as parameter
		InputStream streamToModiaf =this.getClass().getClassLoader().getResourceAsStream(from);
		if (streamToModiaf==null)
			throw new FileNotFoundException(from+" resource not found");
		File destination = new File(to);
		if (destination.isDirectory() && !destination.exists())
			destination.mkdirs();
		FileOutputStream target=new FileOutputStream(destination);
		byte[] bytes=new byte[8000];
		int read=0;
		do {
			read=streamToModiaf.read(bytes);
			if (read>0){
				target.write(bytes,0,read);
			}
		} while (read!=-1);
		target.close();
		streamToModiaf.close();
		return destination;
	}
	
	
	/**
	 *  Generates HTMLdoc from a INGENIAS specification file (1st param), a diagram
	 *  template (2nd param), and an indexTemplate (3rd param)
	 *
	 *@param  args           Description of Parameter
	 *@exception  Exception  Description of Exception
	 */
	public static void main(String args[]) throws Exception {
		ingenias.editor.Log.initInstance(new PrintWriter(System.out));
		HTMLDocumentGenerator html = new HTMLDocumentGenerator(args[0]);
		html.run();
		System.exit(0);
	}
	
}
