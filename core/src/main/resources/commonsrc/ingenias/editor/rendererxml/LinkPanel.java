
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
package ingenias.editor.rendererxml;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;

import javax.swing.border.*;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class LinkPanel extends JPanel {

	public LinkPanel() {
		//    DashedBorder db=new DashedBorder(Color.black);
		//    this.setBorder(db);
		this.setLayout(new FlowLayout(FlowLayout.CENTER,0,2));
		addLinkEventTrigger();
	}

	public LinkPanel(LayoutManager p0, boolean p1) {
		super(p0, p1);
		addLinkEventTrigger();
	}

	public LinkPanel(LayoutManager p0) {
		super(p0);
		addLinkEventTrigger();
	}

	public LinkPanel(boolean p0) {
		super(p0);    
		addLinkEventTrigger();
	}

	private void addLinkEventTrigger() {
		final Component source=this;
		this.addMouseListener(new MouseListener(){
			Cursor oldCursor=Cursor.getDefaultCursor();

			public void mouseDragged(MouseEvent e) {

			}


			@Override
			public void mouseClicked(MouseEvent e) {
				// goes upward in the hierarchy to deal with this event
				Container parent = getParent();
				while (parent!=null && ! (parent instanceof HyperlinkListener)){
					parent=getParent();
				}
				final Container fparent=parent;
				if (fparent!=null && fparent instanceof HyperlinkListener){
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							((HyperlinkListener)fparent).hyperlinkUpdate(new HyperlinkEvent(source, null, url));		
						}
					});
				}
			}


			@Override
			public void mousePressed(MouseEvent e) {

			}


			@Override
			public void mouseReleased(MouseEvent e) {

			}


			@Override
			public void mouseEntered(MouseEvent e) {
				oldCursor=getCursor();
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));						
			}


			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(oldCursor);			
			}
		});
	}



	private URL url=null;
	public void setLink(URL url){
		this.url=url;
	}

	public void getLink(URL url){
		this.url=url;
	}


	public void paint(Graphics g){
		super.paint(g);       
		Dimension size=this.getSize();
		Color color = g.getColor();
		//    g.setPaintMode();
		g.setColor(Color.blue);
		g.drawLine(0,size.height,size.width,size.height);
		g.setColor(color);
	}

	public static void main(String[] args) {
		LinkPanel dashedBorderPanel1 = new LinkPanel();
	}
}
