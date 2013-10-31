
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

import ingenias.editor.persistence.PersistenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;


public class StatsManager {

	
	public static void saveSessionREG(long ctime, long initTime, Vector<String> questions) {
		try {
			FileOutputStream fos=new FileOutputStream("logs/sessionDataREG"+ctime+".xml");			
			for (String question:questions){				
				fos.write((""+ctime+","+(ctime-initTime)+","+question+"\n").getBytes());
			}
			fos.close();
	//		new PersistenceManager().save(new File("logs/sessionSpecREG"+ctime+".xml"), IDE.ide.ids);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveSession(long ctime, long initTime) {
		try {
			FileOutputStream fos=new FileOutputStream("logs/sessionData"+ctime+".xml");
			fos.write((""+ctime+","+(ctime-initTime)).getBytes());
			fos.close();
		//	new PersistenceManager().save(new File("logs/sessionSpec"+ctime+".xml"), IDE.ide.ids);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

}
