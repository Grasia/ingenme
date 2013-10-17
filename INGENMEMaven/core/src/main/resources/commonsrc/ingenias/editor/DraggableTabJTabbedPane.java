/*
    Copyright (C) 2012 Jorge Gomez Sanz

    This file is part of INGENIAS Agent Framework, an agent infrastructure linked
    to the INGENIAS Development Kit, and availabe at http://grasia.fdi.ucm.es/ingenias or
    http://ingenias.sourceforge.net. 

    INGENIAS Agent Framework is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    INGENIAS Agent Framework is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with INGENIAS Agent Framework; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

*/

package ingenias.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

// Code partially reused from http://stackoverflow.com/questions/60269/how-to-implement-draggable-tab-using-java-swing
// drop on new window code is original

public class DraggableTabJTabbedPane extends JTabbedPane {
	protected boolean dragging=false;
	protected int draggedTabIndex=0;
	protected Image tabImage;
	protected Point currentMouseLocation;
	
	public DraggableTabJTabbedPane() {
		addMouseMotionListener(new MouseMotionAdapter(){

			@Override
			public void mouseDragged(MouseEvent e) {
				if(!dragging) {
					// Gets the tab index based on the mouse position
					int tabNumber = DraggableTabJTabbedPane.this.getUI().tabForCoordinate(DraggableTabJTabbedPane.this, e.getX(), e.getY());

					if(tabNumber >= 0) {
						draggedTabIndex = tabNumber;
						Rectangle bounds = DraggableTabJTabbedPane.this.getUI().getTabBounds(DraggableTabJTabbedPane.this, tabNumber);

						// Paint the tabbed pane to a buffer
						Image totalImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
						Graphics totalGraphics = totalImage.getGraphics();
						totalGraphics.setClip(bounds);
						// Don't be double buffered when painting to a static image.
						DraggableTabJTabbedPane.this.setDoubleBuffered(false);
						DraggableTabJTabbedPane.this.paintComponents(totalGraphics);

						// Paint just the dragged tab to the buffer
						tabImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
						Graphics graphics = tabImage.getGraphics();
						graphics.drawImage(totalImage, 0, 0, 
								bounds.width, bounds.height, bounds.x, bounds.y, 
								bounds.x + bounds.width, bounds.y+bounds.height, DraggableTabJTabbedPane.this);

						dragging = true;
						repaint();
					}
				} else {
					currentMouseLocation = e.getPoint();

					// Need to repaint
					repaint();
				}

			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

		DraggableTabJTabbedPane.this.addMouseListener(new MouseAdapter() {			

			@Override
			public void mouseReleased(MouseEvent e) {
				if(dragging) {
					int tabNumber = DraggableTabJTabbedPane.this.getUI().tabForCoordinate(DraggableTabJTabbedPane.this, e.getX(), 10);
					final Component comp = DraggableTabJTabbedPane.this.getComponentAt(draggedTabIndex);
					final String title = DraggableTabJTabbedPane.this.getTitleAt(draggedTabIndex);
					DraggableTabJTabbedPane.this.removeTabAt(draggedTabIndex);
					if(tabNumber >= 0) {		            
						DraggableTabJTabbedPane.this.insertTab(title, null, comp, null, tabNumber);
					} else {
						// create a tab in a separated window
						final JFrame jf=new JFrame(title);
						jf.getContentPane().setLayout(new BorderLayout());  
						jf.getContentPane().add(comp,BorderLayout.CENTER);
						jf.addWindowListener(new WindowAdapter() {
							public void windowClosing(WindowEvent evt) {
								jf.getContentPane().remove(comp);
								DraggableTabJTabbedPane.this.insertTab(title, null, comp, null, draggedTabIndex);
							}
						});
						jf.pack();
						jf.setVisible(true);
					}
				}

				dragging = false;				       
				tabImage = null;
			}

		});
	}



	
	public void paint(Graphics g) {
		super.paint(g);
		// Are we dragging?
		System.err.println(""+dragging + currentMouseLocation + tabImage );
		if(dragging && currentMouseLocation != null && tabImage != null) {
			// Draw the dragged tab
			System.err.println("redrawing");
			g.drawImage(tabImage, currentMouseLocation.x, currentMouseLocation.y, this);
		}
	}
}
