/*
    This code has been extracted and modified from an original
    work from Rob Kenworthy and an example from Sheetal Gupta
    (http://java.sun.com/docs/books/tutorial/dnd/sheetal.html)

 */

package ingenias.editor.widget;

import javax.swing.JTree;
import java.awt.dnd.*;
import java.awt.Point;
import java.awt.datatransfer.*;
import java.awt.Cursor;
import java.io.*;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;

import java.awt.Component;
import org.jgraph.JGraph;
import javax.swing.JViewport;
import java.awt.*;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import java.util.*;


public class DnDJTree extends JTree implements java.io.Serializable,
DropTargetListener,DragSourceListener,DragGestureListener{

	public JScrollPane getContainer() {
		return container;
	}


	public void setContainer(JScrollPane container) {
		this.container = container;
	}

	DropTarget dropTarget=null;
	DragSource dragSource=null;
	JScrollPane container=null;
	Vector expansionPaths=new Vector();

	boolean dragOn=false;

	DefaultMutableTreeNode nodeInTransfer=null;
	TreeNode root=null;

	public DnDJTree(JScrollPane jsp,TreeNode tn) {
		super(tn);
		this.setAutoscrolls(true);
		this.container=jsp;
		final JTree jt=this;
		this.root=tn;
		dropTarget=new DropTarget(this,this);
		dragSource=new DragSource();
		dragSource.createDefaultDragGestureRecognizer(this,
				DnDConstants.ACTION_MOVE,this);
		this.setEditable(true); 


		this.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			public void keyReleased(KeyEvent e) {

			}

			// Code added to make nodes go up and down among siblings
			public void keyTyped(final KeyEvent e) {
				if (e.getKeyChar()=='q' && jt.getSelectionPath()!=null){
					DefaultMutableTreeNode node =
							(DefaultMutableTreeNode)jt.getSelectionPath().getLastPathComponent();
					if (node.getParent()!=null){
						int index=node.getParent().getIndex(node);
						if (index>0){
							DefaultMutableTreeNode parent=(DefaultMutableTreeNode)node.getParent();
							node.removeFromParent();
							parent.insert(node, index-1);
							node.setParent(parent);
							storeTreeExpansionPaths();
							((DefaultTreeModel)getModel()).reload();
							validate();
							restoreTreeExpansionPath();
							jt.addSelectionPath(new TreePath(node.getPath()));
						}
					}
				}
				if (e.getKeyChar()=='z' && jt.getSelectionPath()!=null){
					DefaultMutableTreeNode node =
							(DefaultMutableTreeNode)jt.getSelectionPath().getLastPathComponent();
					if (node.getParent()!=null){
						int index=node.getParent().getIndex(node);
						if (index<node.getParent().getChildCount()-1){

							DefaultMutableTreeNode parent=(DefaultMutableTreeNode)node.getParent();
							node.removeFromParent();							
							parent.insert(node, index+1);
							node.setParent(parent);
							storeTreeExpansionPaths();
							((DefaultTreeModel)getModel()).reload();
							validate();
							restoreTreeExpansionPath();
							jt.addSelectionPath(new TreePath(node.getPath()));
						}
					}
				}

			};
		});


	}

	public TreeNode getRoot(){
		return root;
	}

	public void storeTreeExpansionPaths(){
		TreePath[] tp=this.getPathBetweenRows(0,this.getRowCount()-1);
		expansionPaths=new Vector();
		for (int k=0;k<tp.length;k++){
			if (this.isExpanded(tp[k]))
				expansionPaths.add(tp[k]);
		}

	}

	public void restoreTreeExpansionPath(){
		Enumeration enumeration=this.expansionPaths.elements();
		while (enumeration.hasMoreElements()){
			TreePath tp1=(TreePath)enumeration.nextElement();
			this.expandPath(tp1);
		}
		this.expansionPaths=new Vector();

	}

	public Insets getAutoscrollInsets(){
		Rectangle r=this.getVisibleRect();
		Dimension s=this.getSize();

		return new Insets(r.y+15,r.x+15,s.height-r.y-r.height+15,s.width-r.x-r.width+15);
	}

	public DnDJTree() {
		super(new DefaultMutableTreeNode("Project"));
		dropTarget=new DropTarget(this,this);
		dragSource=new DragSource();
		dragSource.createDefaultDragGestureRecognizer(this,
				DnDConstants.ACTION_MOVE,this);
	}

	/**
	 * is invoked when you are dragging over the DropSite
	 *
	 */

	public void dragEnter (DropTargetDragEvent event) {

		// debug messages for diagnostics

		event.acceptDrag (DnDConstants.ACTION_MOVE);
	}

	/**
	 * is invoked when you are exit the DropSite without dropping
	 *
	 */

	public void dragExit (DropTargetEvent event) {


	}


	/**
	 * a drop has occurred
	 *
	 */


	public void drop (final DropTargetDropEvent event) {
	/*	this.getModel().addTreeModelListener(
				new TreeModelListener() {
					public void treeNodesChanged(TreeModelEvent evt) {
						System.out.println("Tree Nodes Changed Event");
						Object[] children = evt.getChildren();
						int[] childIndices = evt.getChildIndices();
						for (int i = 0; i < children.length; i++) {
							System.out.println("Index " + childIndices[i] +
									", changed value: " + children[0]);
						}
					}
					public void treeStructureChanged(TreeModelEvent evt) {
						System.out.println("Tree Structure Changed Event");
					}
					public void treeNodesInserted(TreeModelEvent evt) {
						System.out.println("Tree Nodes Inserted Event");
					} 
					public void treeNodesRemoved(TreeModelEvent evt) {
						System.out.println("Tree Nodes Removed Event");
					}
				}); */
		try {
			Transferable transferable = event.getTransferable();
			DefaultMutableTreeNode target=findNode(event.getLocation());
			DefaultTreeModel dtm=(DefaultTreeModel) this.getModel();

			if (target!=null){// There is a drop target 
				// we accept only Strings
				if (transferable.isDataFlavorSupported (DataFlavor.stringFlavor) &&
						target!=null &&
						!JGraph.class.isAssignableFrom(target.getUserObject().getClass()) &&
						!nodeInTransfer.isNodeDescendant(target)){

					event.acceptDrop(DnDConstants.ACTION_MOVE);
					String s = (String)transferable.getTransferData ( DataFlavor.stringFlavor);
					//           addElement( s );
					dtm.removeNodeFromParent(nodeInTransfer);
					dtm.insertNodeInto(nodeInTransfer, (MutableTreeNode) target, 0);
					//nodeInTransfer.removeFromParent();
					//target.add( nodeInTransfer);
					//nodeInTransfer.setParent(target);
					storeTreeExpansionPaths();
					((DefaultTreeModel)getModel()).reload();
					validate();
					restoreTreeExpansionPath();
					event.getDropTargetContext().dropComplete(true);	
					dtm.nodeStructureChanged(target);
				}
				else{ 
					//Change the order

					if (target.getParent()!=null){
						event.acceptDrop(DnDConstants.ACTION_MOVE);
						int index=target.getParent().getIndex(target);
						event.getDropTargetContext().dropComplete(true);
						if (index!=-1){
							dtm.removeNodeFromParent(nodeInTransfer);
							//nodeInTransfer.removeFromParent();
							if (target!=null && nodeInTransfer!=null &&  target.getParent()!=null && dtm!=null){
								dtm.insertNodeInto(nodeInTransfer, (MutableTreeNode) target.getParent(), index);
								//((DefaultMutableTreeNode)target.getParent()).insert( nodeInTransfer, index);							
								//nodeInTransfer.setParent((DefaultMutableTreeNode)(target.getParent()));
								storeTreeExpansionPaths();
								((DefaultTreeModel)getModel()).reload();
								validate();
								restoreTreeExpansionPath();
								event.getDropTargetContext().dropComplete(true);
								dtm.nodeStructureChanged(target);
							}
						}
					} else
						event.rejectDrop();

				}
			}
			//			this.setCursor(null);
		}
		catch (IOException exception) {
			exception.printStackTrace();

			event.rejectDrop();
		}
		catch (UnsupportedFlavorException ufException ) {
			ufException.printStackTrace();

			event.rejectDrop();
		}


	}

	/**
	 * is invoked if the use modifies the current drop gesture
	 *
	 */


	public void dropActionChanged ( DropTargetDragEvent event ) {
	}

	/**
	 * a drag gesture has been initiated
	 *
	 */

	public void dragGestureRecognized( DragGestureEvent event) {
		TreePath tp=this.getSelectionPath();
		if (tp!=null){
			DefaultMutableTreeNode selected =(DefaultMutableTreeNode) tp.getLastPathComponent();
			if ( selected != null ){
				StringSelection text = new StringSelection( "");
				this.nodeInTransfer=selected;
				// as the name suggests, starts the dragging
				dragSource.startDrag (event, DragSource.DefaultMoveDrop, text, this);
			} else {

			}
		}
	}

	/**
	 * this message goes to DragSourceListener, informing it that the dragging
	 * has ended
	 *
	 */

	public void dragDropEnd (DragSourceDropEvent event) {
		if ( event.getDropSuccess()){

		}
	}

	/**
	 * this message goes to DragSourceListener, informing it that the dragging
	 * has entered the DropSite
	 *
	 */

	public void dragEnter (DragSourceDragEvent event) {

	}

	/**
	 * this message goes to DragSourceListener, informing it that the dragging
	 * has exited the DropSite
	 *
	 */

	public void dragExit (DragSourceEvent event) {


	}

	/**
	 * this message goes to DragSourceListener, informing it that the dragging is currently
	 * ocurring over the DropSite
	 *
	 */

	public void dragOver (DragSourceDragEvent event) {
	}

	private DefaultMutableTreeNode findNode(Point p){
		TreePath tp=this.getPathForLocation(p.x,p.y);
		if (tp!=null){

			DefaultMutableTreeNode dmtn=(DefaultMutableTreeNode)tp.getLastPathComponent();
			return dmtn;
		} else
			return null;
	}

	private void moveMouseWithDrag(Point p){

		Rectangle visibleHeight=container.getViewport().getViewRect();

		DefaultMutableTreeNode dmtn=this.findNode(p);

		if (visibleHeight.height-p.y<40){
			container.validate();

			container.getVerticalScrollBar().setValue(container.getVerticalScrollBar().getValue()+10);
		}
		if ((p.y-visibleHeight.y<40) && (container.getVerticalScrollBar().getValue()>10)){
			container.validate();

			container.getVerticalScrollBar().setValue(container.getVerticalScrollBar().getValue()-10);
		}




		// this.getLocation()

		if (dmtn!=null){


			this.setSelectionPath(new TreePath(dmtn.getPath()));

		} else {
		}
	}

	public void dragOver (DropTargetDragEvent event) {
		this.moveMouseWithDrag(event.getLocation());


	}

	/**
	 * is invoked when the user changes the dropAction
	 *
	 */

	public void dropActionChanged ( DragSourceDragEvent event) {

	}


}





