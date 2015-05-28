/*
 Copyright (C) 2014 Jorge Gomez Sanz (initial versions with Rafael Martínez)
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
import java.awt.Frame;
import java.awt.geom.Rectangle2D;
import java.io.*;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;

import org.pegdown.PegDownProcessor;


/**
 *  This class generates HTML documentation from a INGENIAS specification
 *
 *@author     Jorge Gomez
 *@created    29 de marzo de 2003
 */
public class HTMLDocumentGenerator
extends ingenias.editor.extension.BasicCodeGeneratorImp {
	
	private PegDownProcessor markdown=new PegDownProcessor();

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
		this.addTemplate("pluginssrc/templates/package-index-xdoc.xml");
		this.addTemplate("pluginssrc/templates/index.xml");
		this.addTemplate("pluginssrc/templates/index-xdoc.xml");
		this.addTemplate("pluginssrc/templates/diagram-xdoc.xml");
		this.addTemplate("pluginssrc/templates/diagram.xml");
		this.addTemplate("pluginssrc/templates/site.xml");
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
		this.addTemplate("pluginssrc/templates/index.xml");
		this.addTemplate("pluginssrc/templates/index-xdoc.xml");
		this.addTemplate("pluginssrc/templates/diagram.xml");
		this.addTemplate("pluginssrc/templates/diagram-xdoc.xml");
		this.addTemplate("pluginssrc/templates/site.xml");
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

		/*result.add(
				new ingenias.editor.ProjectProperty(this.getName(),"htmldoc", "HTML document folder",
						"html",
						"The document folder that will contain HTML version of this specification"));*/
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
			this.generateSiteIndexForMaven(seq);
			this.generatePages(seq);
			copyResourceFromTo("images/logograsia.jpg", ( (ProjectProperty)this.getProperty("htmldoc:output")).
					value+"/logograsia.jpg");
			copyResourceFromTo("images/htmldocpackage.png", ( (ProjectProperty)this.getProperty("htmldoc:output")).
					value+"/resources/images/htmldocpackage.png");
		}
		catch (Throwable ex) {
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
				( (ProjectProperty)this.getProperty("htmldoc:output")).
				value));

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
					String[] packagePath=Arrays.copyOfRange(path, 0,j);
					generateIndexPerPackage(p, packagePath);
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
			g.generateImage( ( (ProjectProperty)this.getProperty("htmldoc:output")).
					value + "/images/" + toSafeName(g.getName()) + ".png");
			if (!new File(((ProjectProperty)this.getProperty("htmldoc:output")).value + "/resources/images/").exists())
				new File(((ProjectProperty)this.getProperty("htmldoc:output")).value + "/resources/images/").mkdirs();
			g.generateImage( ( (ProjectProperty)this.getProperty("htmldoc:output")).
					value + "/resources/images/" + toSafeName(g.getName()) + ".png");
			rp1.add(rp2);
		}

	}
	
	private void generateIndexPerPackage(Sequences p, String [] packagePath) throws Exception {

		Graph[] gs = browser.getGraphs();

		p.addVar(new Var("output",
				( (ProjectProperty)this.getProperty("htmldoc:output")).
				value));

		Hashtable pathtable = new Hashtable();

		Hashtable paths = new Hashtable();
		Hashtable depth = new Hashtable();
		Repeat main = new Repeat("packageindexrepeat");
		
		main.add(new Var("pname",toString(packagePath)));	
		p.addRepeat(main);
		
		Vector alreadyShown=new Vector();
		float increment=25f/gs.length;		
		for (int k = 0; k < gs.length; k++) {
			Repeat rp1=null;
			this.setProgress((int) (k*increment));
			String[] path=gs[k].getPath();
			if (isWithinPath(path, packagePath)){
			boolean already=true;
			while (already){
				rp1 = new Repeat("paquete");
				main.add(rp1);
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
			g.generateImage( ( (ProjectProperty)this.getProperty("htmldoc:output")).
					value + "/images/" + toSafeName(g.getName()) + ".png");
			if (!new File(((ProjectProperty)this.getProperty("htmldoc:output")).value + "/resources/images/").exists())
				new File(((ProjectProperty)this.getProperty("htmldoc:output")).value + "/resources/images/").mkdirs();
			g.generateImage( ( (ProjectProperty)this.getProperty("htmldoc:output")).
					value + "/resources/images/" + toSafeName(g.getName()) + ".png");
			rp1.add(rp2);
			}
		}

	}
	
	private String toString(String[] packagePath) {
	 String result="package";
	 for (String path:packagePath){
		 result=result+"-"+toSafeName(path);
	 }
		return result;
	}

	private void generateSiteIndexForMaven(Sequences p) throws Exception {

		Graph[] gs = browser.getGraphs();

		p.addVar(new Var("output",
				( (ProjectProperty)this.getProperty("htmldoc:output")).
				value));

		Hashtable pathtable = new Hashtable();

		Hashtable paths = new Hashtable();
		Hashtable depth = new Hashtable();

		StringBuffer itemMenu=new StringBuffer();
		
		Vector alreadyShown=new Vector();		
		Repeat rp1 = new Repeat("siteindexrepeat");
		p.addRepeat(rp1);		
		String[] lastPath=new String[0];
		int nuevosNiveles=0;
		int diferringPosition=0;
		for (int k = 0; k < gs.length; k++) {			
			String[] path=gs[k].getPath();
			diferringPosition=lastPath.length;
			if (!Arrays.equals(lastPath, path)){				
				for (int j=0;j<lastPath.length && j<path.length && diferringPosition==lastPath.length;j++){					
					if (lastPath[j]!=null && !lastPath[j].equals(path[j])){						
						diferringPosition=j;
					}
						
				}
				for (int j=diferringPosition+1;j<lastPath.length;j++)
					itemMenu.append("</item>");
			}
			boolean already=true;
			
			while (already){				
				int j=0;
				String pathToBeCompared="";
				for (j=0;j<path.length-1 && already;j++){
					pathToBeCompared=pathToBeCompared+"+*"+path[j];
					if (!alreadyShown.contains(pathToBeCompared)){
						already=false;
						alreadyShown.add(pathToBeCompared);
					}
				}
				if (!already){
					already=true;
					
					String[] packagePath=Arrays.copyOfRange(path, 0,j);
					
					itemMenu.append("<item name=\""+path[j-1]+"\" href=\""+toString(packagePath)+"-index-diag.html\" img=\"images/htmldocpackage.png\">");
					nuevosNiveles++;
				} else {
					already=false;
				}
			}
			Graph g = gs[k];
			itemMenu.append("<item name=\""+ingenias.generator.util.Conversor.replaceInvalidCharsForID(g.getName())
					+"\" href=\""+this.toSafeName(g.getName())+".html\"/>");
			lastPath=path;
		}
		for (int j=1;j<lastPath.length;j++)
			itemMenu.append("</item>");
		rp1.add(new Var("siteindex",itemMenu.toString()));
		

	}
	
	


	private boolean isWithinPath(String[] path, String[] packagePath) {
		boolean result=true;
		int k=0;
		while (result && k<packagePath.length && k<path.length){
			result=result && packagePath[k].equals(path[k]);
			k++;			
		}
		return result && k>=packagePath.length;
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
					( (ProjectProperty)this.getProperty("htmldoc:output")).
					value));
			r.add(new Var("name", gs[k].getName()));
			r.add(new Var("fname", this.toSafeName(gs[k].getName())));
			r.add(new Var("image", toSafeName(gs[k].getName()) + ".png"));
			r.add(new Var("tipo", gs[k].getType()));

			try {
				
				r.add(new Var("description",
						markDownIfNotHTML(gs[k].getAttributeByName("Description").getSimpleValue())));				
				
			}
			catch (NotFound nf) {
				nf.printStackTrace();
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
							markDownIfNotHTML(ge.getAttributeByName("Description").getSimpleValue())));				

				}
				catch (NotFound nf) {
					// nf.printStackTrace();
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

	private String markDownIfNotHTML(String simpleValue) {		
		if (!simpleValue.contains("/>"))
			return markdown.markdownToHtml(simpleValue);
		else 
			return simpleValue;
		
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
		System.out.println("INGENIAS HTML Document Generator  (C) 2012 Jorge Gomez");
		System.out.println("This program comes with ABSOLUTELY NO WARRANTY; for details check www.gnu.org/copyleft/gpl.html.");
		System.out.println("This is free software, and you are welcome to redistribute it under certain conditions;; for details check www.gnu.org/copyleft/gpl.html.");

		if (args.length==0){
			System.err.println("The first argument (mandatory) has to be the specification file and the second " +
					"the outputfolder folder");
		} else {

			if (args.length>=2){ 
				ingenias.editor.Log.initInstance(new PrintWriter(System.out));
				ModelJGraph.disableAllListeners(); // this disable layout listeners that slow down code generation
				// it is a bug of the platform which will be addressed in the future

				ingenias.editor.Log.initInstance(new PrintWriter(System.out));
				HTMLDocumentGenerator html = new HTMLDocumentGenerator(args[0]);
				Properties props = html.getBrowser().getState().prop;
				new File(args[1]+"/target/dochtml").mkdirs();
				html.putProperty(new ProjectProperty("htmldoc","htmldoc:output","output",args[1]+"/target/dochtml","htmldoc"));  
				
				html.run();
				if (ingenias.editor.Log.getInstance().areThereErrors() ){
					for (Frame f:Frame.getFrames()){
						f.dispose();

					}
					throw new RuntimeException("There are the following code generation errors: "+Log.getInstance().getErrors());		
				}
			} else {
				System.err.println("The first argument (mandatory) has to be the specification file and the second  " +
						"the outputfolder");
			}

		}
		for (Frame f:Frame.getFrames()){ 
			f.dispose();
		}	
	}


}
