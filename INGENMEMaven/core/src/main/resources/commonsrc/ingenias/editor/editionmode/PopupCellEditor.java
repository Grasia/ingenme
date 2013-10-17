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

package ingenias.editor.editionmode;

import ingenias.editor.GraphManager;
import ingenias.editor.ObjectManager;
import ingenias.editor.cell.NAryEdge;
import ingenias.editor.cell.RenderComponentManager;
import ingenias.editor.editiondialog.GeneralEditionPanel;
import ingenias.editor.entities.Entity;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.CellEditorListener;

import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.GraphConstants;
import org.jgraph.plaf.basic.BasicGraphUI;

public class PopupCellEditor  extends BasicGraphUI implements java.io.Serializable {

	protected CellEditorListener cellEditorListener;

	protected JDialog editDialog = null;
	private ObjectManager om=null;
	private GraphManager gm=null;
	private Frame owner=null;


	
	
	public PopupCellEditor(ObjectManager om,GraphManager gm, Frame owner){
		this.om=om;
		this.gm=gm;
		this.owner=owner;
	}

	protected void completeEditing(boolean messageStop,
			boolean messageCancel,
			boolean messageGraph) {
		if (stopEditingInCompleteEditing && editingComponent != null &&
				editDialog != null) {
			Component oldComponent = editingComponent;
			Object oldCell = editingCell;
			GraphCellEditor oldEditor = cellEditor;
			Object newValue = oldEditor.getCellEditorValue();
			Rectangle editingBounds = graph.getCellBounds(editingCell).getBounds();
			boolean requestFocus = (graph != null &&
					(graph.hasFocus() || editingComponent.hasFocus()));
			editingCell = null;
			editingComponent = null;
			if (messageStop) {
				oldEditor.stopCellEditing();
			}
			else if (messageCancel) {
				oldEditor.cancelCellEditing();
			}
			editDialog.dispose();
			if (requestFocus) {
				graph.requestFocus();
			}
			if (messageGraph) {
				Map map =new Hashtable();
				GraphConstants.setValue(map, newValue);
				Map insert = new Hashtable();
				insert.put(oldCell, map);
				graphModel.edit(insert,null,  null, null);
			}
			updateSize();
			// Remove Editor Listener
			if (oldEditor != null && cellEditorListener != null) {
				oldEditor.removeCellEditorListener(cellEditorListener);
			}
			cellEditor = null;
			editDialog = null;
		}
	}
	protected boolean startEditing(final Object cell, MouseEvent event) {

		completeEditing();
	
		if (graph.isCellEditable(cell) && editDialog == null) {      	
			CellView tmp = graphLayoutCache.getMapping(cell, false);
			cellEditor = tmp.getEditor();
			editingComponent = cellEditor.getGraphCellEditorComponent(graph, cell,
					graph.isCellSelected(cell));
			if (cellEditor.isCellEditable(event)) {
				new Thread(){
					public void run(){
						JScrollPane jsp=new JScrollPane();

						JPanel south=new JPanel();
						JButton accept=new JButton("Accept");					
						JButton cancel=new JButton("Cancel");
						south.add(accept);
						south.add(cancel);
						JPanel main=new JPanel();
						main.setLayout(new BorderLayout());
						main.add(south, BorderLayout.SOUTH);
						final Entity ent=(Entity) ((DefaultGraphCell)cell).getUserObject();
						final GeneralEditionPanel gep=new GeneralEditionPanel(null, owner, om,gm, ent);
						jsp.getViewport().add(gep,null);
						main.add(jsp, BorderLayout.CENTER);
						createIndependentEditDialog(cell, accept, cancel, main,
								ent, gep);
			
					}
				}.start();

				return true;

			}
			else {
				editingComponent = null;
			}
		}
		return false;
	}

	private void createIndependentEditDialog(Object cell, JButton accept,
			JButton cancel, JPanel main, final Entity ent,
			final GeneralEditionPanel gep) {
		editingCell = cell;
		Dimension editorSize = editingComponent.getPreferredSize();
		final JDialog editDialog=new JDialog(owner,true);
		editDialog.getContentPane().setLayout(new BorderLayout());
		editDialog.getContentPane().add(main,BorderLayout.CENTER);						
		editDialog.pack();						
		String oldid=ent.getId();
		editDialog.setLocation(ingenias.editor.widget.GraphicsUtils.getCenter(owner,editDialog.getSize()));
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (gep.isModified()){
					int result = JOptionPane.showConfirmDialog(owner,
							"You will loose current changes. Do you want to continue (y/n)?",
							"Warning", JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);
					if (result == JOptionPane.OK_OPTION){
						gep.undo();		
						editDialog.setVisible(false);
					}
				} else {								
					editDialog.setVisible(false);
				}
				graph.repaint();
			};
		});
		accept.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				editDialog.setVisible(false);
				gep.confirmActions();
				graph.repaint();
				graph.firePropertyChange(org.jgraph.JGraph.GRAPH_MODEL_PROPERTY,0,0);
			};
		});


		editDialog.setVisible(true);
		if (cell instanceof NAryEdge){
			RenderComponentManager.setRelationshipView(ent.getPrefs(graph.getModel().getAttributes(cell)).getView(),ent,(DefaultGraphCell)cell,graph);			
		}
		/*Vector result=getOM().findUserObject(ent.getId());
		if (result.size()>1 ){							
			JOptionPane.showMessageDialog(null,"There is already another entity with name "+ent.getId()+". I will restore old id","ERROR",JOptionPane.ERROR_MESSAGE);
			ent.setId(oldid);
		}*/
		om.reload();
	}

}

