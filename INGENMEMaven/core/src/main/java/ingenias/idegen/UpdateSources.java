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

import java.io.File; 
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class UpdateSources {
   private static byte[] buffer;
   static{ 
	   buffer=new byte[1000000];
   }
	public UpdateSources() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public static File findSame(File file,File folder){
		File[] fs=folder.listFiles();
		int k=0;
		for (k=0;k<fs.length && !fs[k].getName().equals(file.getName());k++);
		if (k<fs.length)
			return fs[k];
		else 
			return null;
	}
	
	
	public static void update(File orig,File target) throws IOException{
		File[] filesorig=orig.listFiles();
		File[] filestarget=target.listFiles();
		System.err.println("analizing "+orig+" vs "+target);
	    for (int k=0;k<filesorig.length;k++){
	    	System.err.println("studing "+filesorig[k].getName());
	    	File f=findSame(filesorig[k],target);
	    
	    	if (!filesorig[k].isDirectory()){
	    	 if (f!=null){
	    		 System.err.println("found "+filesorig[k]);
	    		 File nfile=new File(target,filesorig[k].getName());
	    		 FileInputStream fis=new FileInputStream(filesorig[k]);
	    		 int read=fis.read(buffer);
	    		 if (read==buffer.length){
	    			 System.err.println("File too long "+filesorig[k]);	    			 
	    		 }
	    		 System.err.println("writing "+buffer);
	    		 FileOutputStream fos=new FileOutputStream(nfile);
	    		 fos.write(buffer,0,read);
	    		 fos.close();
	    	 }	    		
	    } else
	    	if (f!=null)
	    	update(filesorig[k],f);
	    }
	}
	

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	public static void main(String[] args) throws IOException {
		File sourcestarget=new File(args[1]);
		File sourcesorig=new File(args[0]);
		update(sourcesorig,sourcestarget);

	}

}
