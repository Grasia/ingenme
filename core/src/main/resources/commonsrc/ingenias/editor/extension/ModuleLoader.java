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

package ingenias.editor.extension;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.JarURLConnection;
import java.nio.channels.FileChannel;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.util.jar.Attributes;
import java.util.*;
import java.io.*;
import java.util.zip.*;

/**
 *
 *
 *@author     developer
 *@created    30 November 2003
 */
public class ModuleLoader
extends URLClassLoader {
	private URL url;
	private Vector reviewedFiles = new Vector();
	private Hashtable<String,Long> modificationTime=new Hashtable<String,Long>();
	private boolean alreadyWarned=false;

	/**
	 *  Creates a new JarClassLoader for the specified url.
	 *
	 *@param  url  the url of the jar file
	 */
	public ModuleLoader(URL url) {
		super(new URL[] {url});
		this.url = url;
	}

	/**
	 *  Loads tools and code generators which are newer than existing ones
	 *
	 *@exception  IOException  The jas file could not be loaded
	 */
	public void getToolsAndCG(Set tools, Set cg) throws Exception {

		URL u = new URL("jar", "", url + "!/");
		JarURLConnection uc = (JarURLConnection) u.openConnection();
		Enumeration enumeration = uc.getJarFile().entries();
		boolean newclass = false;
		while (enumeration.hasMoreElements()) {
			newclass = true;
			ZipEntry ze = (ZipEntry) enumeration.nextElement();
			Class c = null;
			String cname = "";
			try {
				if (ze.getName().indexOf(".class") > -1) {
					cname = ze.getName().substring(0, ze.getName().length() - 6).
					replace('/', '.');
					c = this.loadClass(cname);
					if (ingenias.editor.extension.BasicTool.class.isAssignableFrom(c) &&
							!ingenias.editor.extension.BasicCodeGenerator.class.
							isAssignableFrom(c)) {
						tools.add(c);
					}
					else
						if (ingenias.editor.extension.BasicTool.class.isAssignableFrom(c) &&
								ingenias.editor.extension.BasicCodeGenerator.class.
								isAssignableFrom(c)) {
							cg.add(c);
						}
				}
			}
			catch (ClassNotFoundException cnf) {
				cnf.printStackTrace();
			}
		}
	}

	public static void copyFile(File sourceFile, File destFile) throws IOException {
		if(!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		}
		finally {
			if(source != null) {
				source.close();
			}
			if(destination != null) {
				destination.close();
			}
		}
	}

	public void updateStatus(String folder,Set currenttools, Set currentcg) {
		try {
			File f = new File(folder);
			if (f.isDirectory()) {
				File[] fs = f.listFiles();
				for (int k = 0; k < fs.length; k++) {
					if (fs[k].getName().toLowerCase().endsWith(".jar") &&
							(!reviewedFiles.contains(fs[k].getName())|| 
									modificationTime.get(fs[k].getName()).longValue()!=fs[k].lastModified())) {
						File nname = File.createTempFile("modulecopy", "");
						nname.deleteOnExit(); // To remove the file on JVM exit
						copyFile(fs[k],nname);						

						reviewedFiles.add(fs[k].getName());
						modificationTime.put(fs[k].getName(),fs[k].lastModified()); // registers modification time

						ModuleLoader ml = new ModuleLoader(new URL("file:" +  nname.getAbsolutePath()));
						ml.getToolsAndCG(currenttools, currentcg);					
					}
				}
			}
			else {
				if (!alreadyWarned)
				 ingenias.editor.Log.getInstance().logWARNING(
					"\""+folder+"\" has to be a folder. If it does not exist, please create it. If you use modules, these have to be put inside a folder called ext. Otherwise, you will not be able to use any embedded module");
				alreadyWarned=true;
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void cleanExtensionFolder() {
		try {
			File f = new File("ext");
			if (f.isDirectory()) {
				File[] fs = f.listFiles();
				Hashtable set = new Hashtable();
				for (int k = 0; k < fs.length; k++) {
					String rname = fs[k].getName();
					rname = rname.replace('_', ' ').trim();
					Vector row = null;
					if (set.containsKey(rname)) {
						row = (Vector) set.get(rname);
					}
					else {
						row = new Vector();
						set.put(rname, row);
					}
					row.add(fs[k]);
				}
				Enumeration keys = set.keys();
				while (keys.hasMoreElements()) {
					String name = keys.nextElement().toString();
					Vector row = (Vector) set.get(name);
					File selected = null;
					int countedscores = 0;
					for (int k = 0; k < row.size(); k++) {
						File current = (File) row.elementAt(k);
						int index = current.getName().lastIndexOf("_");
						if (index > countedscores) {
							selected = current;
							countedscores = index;
						}
					}
					if (selected != null) {
						for (int k = 0; k < row.size(); k++) {
							File current = (File) row.elementAt(k);
							if (!current.equals(selected)) {
								current.delete();
							}
						}
						selected.renameTo(new File("ext/" + name));
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 *  Description of the Method
	 *
	 *@param  args           Description of Parameter
	 *@exception  Exception  Description of Exception
	 */
	public static void main(String args[]) throws Exception {

	}

}
