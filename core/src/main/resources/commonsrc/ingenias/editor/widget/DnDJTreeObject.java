/*
    This code has been extracted and modified from an original
    work from Rob Kenworthy and an example from Sheetal Gupta
    (http://java.sun.com/docs/books/tutorial/dnd/sheetal.html)

 */

package ingenias.editor.widget;

import ingenias.editor.entities.Entity;
import ingenias.editor.events.DndObjectTreeTransferable;
import ingenias.generator.browser.Browser;
import ingenias.generator.browser.BrowserImp;
import ingenias.generator.browser.GraphEntity;
import ingenias.generator.browser.GraphEntityImp;

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
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.GraphTransferable;
import org.jgraph.graph.ParentMap;
import org.jgraph.plaf.basic.BasicGraphUI;

import javax.swing.JViewport;

import java.awt.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.datatransfer.*;

import javax.swing.*;

import java.util.*;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.jgraph.JGraph;

public class DnDJTreeObject extends JTree implements java.io.Serializable,
DragSourceListener,DragGestureListener{

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
	private Browser browser;

	public DnDJTreeObject(JScrollPane jsp,TreeNode tn) {
		super(tn);
		this.setAutoscrolls(true);
		this.container=jsp;
		final JTree jt=this;
		this.root=tn;
		dragSource=new DragSource();
		dragSource.createDefaultDragGestureRecognizer(this,
				DnDConstants.ACTION_COPY,this);
		this.setEditable(true); 

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

	public DnDJTreeObject() {
		super(new DefaultMutableTreeNode("Project"));
	
		dragSource=new DragSource();
		dragSource.createDefaultDragGestureRecognizer(this,
				DnDConstants.ACTION_COPY,this);
	}
	public DnDJTreeObject(DefaultMutableTreeNode tn) {
		super(tn);
		dragSource=new DragSource();
		dragSource.createDefaultDragGestureRecognizer(this,
				DnDConstants.ACTION_COPY,this);
	}

	/**
	 * is invoked when you are dragging over the DropSite
	 *
	 */

	public void dragEnter (DropTargetDragEvent event) {

		// debug messages for diagnostics

		event.acceptDrag (DnDConstants.ACTION_COPY);
	}

	/**
	 * is invoked when you are exit the DropSite without dropping
	 *
	 */

	public void dragExit (DropTargetEvent event) {


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
				Entity ent=(Entity) selected.getUserObject();		
				
				DndObjectTreeTransferable nodeTrans=new DndObjectTreeTransferable(selected);
								
				this.nodeInTransfer=selected;
				// as the name suggests, starts the dragging
				event.getDragSource().startDrag (event, DragSource.DefaultMoveDrop, nodeTrans, this);
			} else {

			}
		}
	}

	/**
	 * this message goes to DragSourceListener, informing it that the dragging
	 * has ended
	 *
	 */

	public void dragDropEnd (DragSourceDropEvent event) {	}

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

		System.err.println("terminado2");
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


	public void setBrowser(Browser browser) {
		this.browser=browser;		
	}


	@Override
	public void dropActionChanged(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub
		
	}

	

}





