
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

package ingenias.editor.actions;

import ingenias.editor.GUIResources;
import ingenias.editor.IDEState;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jgraph.JGraph;

public class CaptureAction {
	private IDEState ids;
	private GUIResources resources;
	
	public CaptureAction(IDEState ids, GUIResources resources){
		this.ids=ids;
		this.resources=resources;
	}
	
	public void capture_action() {
		try {
			JGraph graph = this.ids.editor.getGraph();
			if (graph  == null) {
				JOptionPane.showMessageDialog(resources.getMainFrame(), "Please, open a diagram first");
			}
			else {
				JFileChooser jfc = null;
				if (ids.getCurrentImageFolder() == null && ids.getCurrentFileFolder() == null) {
					jfc = new JFileChooser();
				}
				else {
					if (ids.getCurrentImageFolder() != null) {
						jfc = new JFileChooser(ids.getCurrentImageFolder().getPath());
					}
					else {
						jfc = new JFileChooser(ids.getCurrentFileFolder().getPath());
					}
				}

				//String[] validformats = javax.imageio.ImageIO.getWriterFormatNames();
				final HashSet hs = new HashSet();
				/*for (int k = 0; k < validformats.length; k++) {
					hs.add(validformats[k].toLowerCase());
				}*/
				hs.add("png");
				hs.add("svg");
				hs.add("eps");

				jfc.setAcceptAllFileFilterUsed(false);
				Iterator it = hs.iterator();
				while (it.hasNext()) {
					final String nextFormat = it.next().toString();
					jfc.addChoosableFileFilter(
							new javax.swing.filechooser.FileFilter() {
								public boolean accept(File f) {
									boolean acceptedFormat = f.getName().toLowerCase().endsWith(
											nextFormat);
									return acceptedFormat || f.isDirectory();
								}

								public String getDescription() {
									return nextFormat;
								}
							});
				}
				jfc.setLocation(getCenter(resources.getMainFrame().getSize()));
				jfc.showDialog(resources.getMainFrame(), "Save");
				File sel = jfc.getSelectedFile();
				it = hs.iterator();
				String selectedFormat = jfc.getFileFilter().getDescription();

				if (sel != null && !sel.isDirectory()) {
					if (sel != null &&
							! (sel.getName().toLowerCase().endsWith(selectedFormat))) {
						sel = new File(sel.getPath() + "." + selectedFormat);
					}					

					JPanel temp=new JPanel(new BorderLayout());
					Container parent = graph.getParent();					
					parent.remove(graph);
					temp.add(graph);							
					ingenias.editor.export.Diagram2SVG.diagram2SVG(temp,
							sel, selectedFormat);
				
					parent.add(graph);
					parent.repaint();
					ids.setCurrentImageFolder(sel);
				}
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	Point getCenter(Dimension size){
		Dimension d = resources.getMainFrame().getSize();
		Point result=new Point(
				(d.width / 2 - size.width / 2)+resources.getMainFrame().getLocation().x,
				(d.height / 2 - size.height / 2)+resources.getMainFrame().getLocation().y);
		return result;
	}
}
