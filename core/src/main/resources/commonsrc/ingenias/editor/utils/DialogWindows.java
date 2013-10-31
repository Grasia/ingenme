
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
package ingenias.editor.utils;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;

import ingenias.editor.BusyMessageWindow;
import ingenias.editor.GUIResources;
import ingenias.editor.IDEState;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

public class DialogWindows {
	
	public static JWindow showMessageWindow(String mess,IDEState ids, final GUIResources resources) {
		final JWindow jw = new BusyMessageWindow(ids,resources.getMainFrame());

		final String message = mess;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JLabel jl = new JLabel(message);
				jl.setFont(new java.awt.Font("Dialog", 1, 36));
				jw.getContentPane().add(jl);				
				jw.pack();
				jw.setLocation(getCenter(jw.getSize(),resources.getMainFrame()));
				jw.setVisible(true);
			}
		});
		return jw;
	}
	
	public static Point getCenter(Dimension size, Frame frame){
		Dimension d = frame.getSize();
		Point result=new Point(
				(d.width / 2 - size.width / 2)+frame.getLocation().x,
				(d.height / 2 - size.height / 2)+frame.getLocation().y);
		return result;
	}
	
	/*public static File getHomeDir() {
		JFileChooser jfc = new JFileChooser();
		File homedir = jfc.getCurrentDirectory();
		new File(homedir.getPath() + "/.idk").mkdir();
		
	}*/

}
