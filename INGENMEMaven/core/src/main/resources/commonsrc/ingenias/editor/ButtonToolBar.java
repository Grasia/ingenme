
/** 
 * Copyright (C) 2010  Jorge J. Gomez-Sanz
 * 
 * This file is part of the INGENME tool. INGENME is an open source meta-editor
 * which produces customized editors for user-defined modeling languages
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 **/

package ingenias.editor;

import ingenias.editor.entities.Entity;
import ingenias.editor.events.*;
import ingenias.generator.browser.Browser;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;

public class ButtonToolBar extends JToolBar {
	/**
	 * 
	 */
	private final Editor editor;
	JComboBox jc = new JComboBox(new Object[] {"automatic straight", "automatic radial", "manual"});
	// Actions which Change State
	/**
	 *  Description of the Field
	 */
	protected Action undo, redo, remove, group, ungroup, tofront, toback,
	cut;
	protected EventRedirector copy;
	protected EventRedirectorPaste paste;
	private GraphManager gm;
	protected ObjectManager om;

	public ButtonToolBar(Editor editor, final GraphManager gm, final ObjectManager om){
		this.editor = editor;
		this.setFloatable(false);
		this.gm=gm;
		this.om=om;
		JButton jb = null;

		// Automatic layout


		JPanel jp = new JPanel();
		jp.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		jp.add(new JLabel("Relationship Layout"));
		jp.add(jc);

		this.add(jp);
		
		this.addSeparator();
		Image img_resize = ImageLoader.getImage("images/arrow_inout.png");
		ImageIcon resizeIcon = new ImageIcon(img_resize);
		JButton resize=new JButton(resizeIcon);
		resize.setToolTipText("Resize");
		
		resize.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				if (ButtonToolBar.this.editor.getGraph() != null) {
					new ingenias.editor.actions.ResizeCurrentDiagramAction(ButtonToolBar.this.editor).resizeCurrentDiagram(arg0);					
					
				}
				
			}
			
		});
		this.add(resize);
		

		// Undo
		this.addSeparator();
		Image img_undo = ImageLoader.getImage("images/undo.gif");
		ImageIcon undoIcon = new ImageIcon(img_undo);
		this.undo =
			new AbstractAction("", undoIcon) {

			public void actionPerformed(ActionEvent e) {
				ButtonToolBar.this.editor.undo();
			}
		};
		this.undo.setEnabled(false);
		jb = new JButton(this.undo);
		jb.setToolTipText("Undo an action");
		this.add(jb);
		// Redo
		Image img_redo = ImageLoader.getImage("images/redo.gif");
		ImageIcon redoIcon = new ImageIcon(img_redo);
		redo =
			new AbstractAction("", redoIcon) {

			public void actionPerformed(ActionEvent e) {
				if (ButtonToolBar.this.editor.getGraph() != null) {
					ButtonToolBar.this.editor.redo();
				}
			}
		};
		redo.setEnabled(false);
		jb = new JButton(redo);
		jb.setToolTipText("Redo last action");
		this.add(jb);

		//
		// Edit Block
		//
		this.addSeparator();
		Action action;
		Image img;


		JGraph jg=new JGraph();// fake graph to extract initial action handlers
		// Copy
		action = jg.getTransferHandler().getCopyAction();
		img = ImageLoader.getImage("images/page_copy.png");
		action.putValue(Action.SMALL_ICON, new ImageIcon(img));
		copy = new EventRedirector(this.editor, action,new ImageIcon(img));		
		this.add(copy);						

		// Paste
		action = jg.getTransferHandler().getPasteAction();
		img = ImageLoader.getImage("images/page_paste.png");		

		action.putValue(Action.SMALL_ICON, new ImageIcon(img));
		paste = new EventRedirectorPaste(this.editor, action,new ImageIcon(img));
		this.add(paste);


		// Cut
		action = jg.getTransferHandler().getCopyAction(); // cut is simulated
		// with a copy and a delete when paste was performed.
		// it was required this solution to prevent deleting from
		// the ingenias model elements appearing only once.
		/* img = ImageLoader.getImage("images/cut.gif");
			 action.putValue(Action.SMALL_ICON, new ImageIcon(img));
			 cut = new EventRedirectorCut(Editor.this,action);
			 this.add(cut);*/


		// Remove
		Image img_delete =
			ImageLoader.getImage("images/bin.png");
		ImageIcon removeIcon = new ImageIcon(img_delete);
		remove =
			new AbstractAction("", removeIcon) {

			public void actionPerformed(ActionEvent e) {
				MarqueeHandler.removeAction(ButtonToolBar.this.editor.getGraph(),gm, om);
			}

			
		};

		this.add(remove);

		// Zoom Std
		this.addSeparator();
		Image img_zoom = ImageLoader.getImage("images/zoom.png");
		ImageIcon zoomIcon = new ImageIcon(img_zoom);
		this.add(
				new AbstractAction("", zoomIcon) {
					/**
					 *  Description of the Method
					 *
					 *@param  e  Description of Parameter
					 */
					public void actionPerformed(ActionEvent e) {
						if (ButtonToolBar.this.editor.getGraph() != null) {
							ButtonToolBar.this.editor.getGraph().setScale(1.0);
						}
					}
				});
		// Zoom In
		Image img_zoomin =
			ImageLoader.getImage("images/zoom_in.png");
		ImageIcon zoomInIcon = new ImageIcon(img_zoomin);
		this.add(
				new AbstractAction("", zoomInIcon) {

					public void actionPerformed(ActionEvent e) {
						if (ButtonToolBar.this.editor.getGraph() != null) {
							ButtonToolBar.this.editor.getGraph().setScale(2 * ButtonToolBar.this.editor.getGraph().getScale());
						}
					}
				});
		// Zoom Out
		Image img_zoomout =
			ImageLoader.getImage("images/zoom_out.png");
		ImageIcon zoomOutIcon = new ImageIcon(img_zoomout);
		this.add(
				new AbstractAction("", zoomOutIcon) {

					public void actionPerformed(ActionEvent e) {
						if (ButtonToolBar.this.editor.getGraph() != null) {
							ButtonToolBar.this.editor.getGraph().setScale(ButtonToolBar.this.editor.getGraph().getScale() / 2);
						}
					}
				});

		
	}

	public void updateActions(ModelJGraph graph){
		copy.updateAction(graph.getTransferHandler().getCopyAction(),graph);
		paste.updateAction(graph.getTransferHandler().getPasteAction(),graph);
	}

	public Action getUndo() {
		return undo;
	}

	protected void setUndo(Action undo) {
		this.undo = undo;
	}

	protected Action getCopy() {
		return copy;
	}

	protected Action getCut() {
		return cut;
	}

	protected Action getGroup() {
		return group;
	}

	public JComboBox getJc() {
		return jc;
	}

	protected Action getPaste() {
		return paste;
	}

	public Action getRedo() {
		return redo;
	}

	protected Action getRemove() {
		return remove;
	}

	protected Action getToback() {
		return toback;
	}

	protected Action getTofront() {
		return tofront;
	}

	protected Action getUngroup() {
		return ungroup;
	}




}