
/** 
 * Copyright (C) 2010  Jorge J. Gomez-Sanz, Ruben Fuentes
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


import ingenias.editor.editiondialog.GeneralEditionPanel;
import ingenias.editor.entities.Entity;
import ingenias.editor.entities.ModelDataEntity;
import ingenias.editor.events.ChangeEntityLocation;
import ingenias.editor.events.ChangeNARYEdgeLocation;
import ingenias.editor.events.CustomTransferHandler;
import ingenias.editor.events.ListenerContainer;
import ingenias.editor.events.ObjectTreeFlavor;
import ingenias.editor.rendererxml.CollectionPanel;
import ingenias.exception.InvalidEntity;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.TooManyListenersException;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.CellMapper;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.Port;
import org.jgraph.graph.VertexView;
import org.jgraph.plaf.basic.BasicGraphUI;


public abstract class ModelJGraph extends JGraph implements Cloneable,HyperlinkListener{
	protected Action undo, redo, remove, group, ungroup, tofront, toback,
	cut, copy, paste;
	protected FilteredJToolBar toolbar;
	protected String name;

	private ListenerContainer lc=null;


	protected ModelDataEntity mde=null;

	private ObjectManager om;
	private ChangeNARYEdgeLocation naryEdgeLocation;
	private ChangeEntityLocation changeEntityLocation;
	private static boolean enabledAllListeners=true;

	public static void disableAllListeners(){
		enabledAllListeners=false;
	}
		

	public static void enableAllListeners(){
		enabledAllListeners=true;
	}

	static{ 
		// To enable SVG. These definitions are needed to let the UIManager
		// know for this look and feel which ComponentUI are needed
		UIManager.getDefaults().put(new org.swixml.XVBox().getUIClassID(),"org.swixml.XVBox");
		UIManager.getDefaults().put(new JGraph().getUIClassID(),"ingenias.editor.ModelJGraph");
	}

	public ListenerContainer getListenerContainer(){
		return lc;
	}


	public ModelJGraph(ModelDataEntity mde,String nombre,Model m,BasicMarqueeHandler mh, ObjectManager om){
		super(m,mh);
		this.setGraphLayoutCache(new GraphLayoutCache(m,getGraphLayoutCache().getFactory(),true));
		this.mde=mde;
		this.name=nombre;
		this.setId(name);
		this.setAntiAliased(true);
		this.om=om;
		if (om==null)
			throw new RuntimeException("IDEState is null  in Model JGraph");
		creaToolBar();
		this.setBackground(Color.white);
		this.setTransferHandler(new CustomTransferHandler(om,this));
		this.setDropEnabled(true);
		// this.setTransferHandler(new RelationshipEntityTransferHandler());

		lc=new ListenerContainer(m,this.getGraphLayoutCache(),this);
		naryEdgeLocation = new ChangeNARYEdgeLocation(this);
		changeEntityLocation= new ChangeEntityLocation(this);
		setGridSize(1.0);

		

		if (enabledAllListeners){

			createListeners();
		}
		final ModelJGraph self=this;
		this.setDropEnabled(true);
		DropTarget dt1=new DropTarget(this,new DropTargetListener(){

				@Override
				public void dragEnter(DropTargetDragEvent dtde) {
					// Determine if can actual process the contents comming in.
			        // You could try and inspect the transferable as well, but 
			        // There is an issue on the MacOS under some circumstances
			        // where it does not actually bundle the data until you accept the
			        // drop.
			        if (dtde.isDataFlavorSupported(ObjectTreeFlavor.SINGLETON)) {
			        	dtde.acceptDrag(DnDConstants.ACTION_COPY);

			        } else {

			            dtde.rejectDrag();

			        }
					
				}

				@Override
				public void dragOver(DropTargetDragEvent dtde) {}

				@Override
				public void dropActionChanged(DropTargetDragEvent dtde) {}

				@Override
				public void dragExit(DropTargetEvent dte) {}

				@Override
				public void drop(DropTargetDropEvent dtde) {
					 boolean success = false;
				        // Basically, we want to unwrap the present...
				        if (dtde.isDataFlavorSupported(ObjectTreeFlavor.SINGLETON)) {

				            Transferable transferable = dtde.getTransferable();
				            try {

				                Object data = transferable.getTransferData(ObjectTreeFlavor.SINGLETON);
				                if (data instanceof DefaultMutableTreeNode) {

				                	DefaultMutableTreeNode node = (DefaultMutableTreeNode) data;

				                    DropTargetContext dtc = dtde.getDropTargetContext();
				                    
				                    Entity ent=(Entity) node.getUserObject();
				                    
				                    Point point=null;
				                    point=dtde.getLocation();
									DefaultGraphCell insertedDGC = insertDuplicated(point, ent);
				                    
				                    if (insertedDGC!=null){				                    
				                    
				                        success = true;
				                        dtde.acceptDrop(DnDConstants.ACTION_COPY);

				                    } else {

				                        success = false;
				                        dtde.rejectDrop();

				                    }

				                } else {

				                    success = false;
				                    dtde.rejectDrop();

				                }

				            } catch (Exception exp) {

				                success = false;
				                dtde.rejectDrop();
				                exp.printStackTrace();

				            }

				        } else {

				            success = false;
				            dtde.rejectDrop();

				        }

				        dtde.dropComplete(success);
					
				}
				
			});
		

	}
	
	
	private HyperlinkListener hll=null;
	
	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (hll!=null)
		 hll.hyperlinkUpdate(e);		
	}


	public void setHyperLinkListener(HyperlinkListener hll) {
	 this.hll=hll;		
	}


	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}


	public void createListeners() {		
		// add listeners in the inverse order. Event listeners are notified in reverse order 
		addGraphSelectionListener(new GraphSelectionListener() {

			@Override
			public void valueChanged(GraphSelectionEvent arg0) {

				invalidate();
				repaint();
				refresh();

			}});
		this.getModel().addGraphModelListener(naryEdgeLocation);
		this.getModel().addGraphModelListener(changeEntityLocation);
		this.getModel().addGraphModelListener(lc);
		this.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				if (arg0.getKeyChar()=='H' ||arg0.getKeyChar()=='h'){
					BasicGraphUI bui = ((BasicGraphUI)getUI());
					for (Object selcell:getSelectionCells()){
						if (selcell instanceof DefaultGraphCell)
							if (bui.isHiglighted((DefaultGraphCell) selcell))
								bui.unhiglight((DefaultGraphCell) selcell);
							else
								bui.higlight((DefaultGraphCell) selcell);	
					}

				}

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	public void disableChangeEntityLocation(){
		if (enabledAllListeners)
			this.getModel().removeGraphModelListener(changeEntityLocation);
	}

	public void disableNARYEdgeLocation(){
		if (enabledAllListeners)
			this.getModel().removeGraphModelListener(naryEdgeLocation);
	}

	public void enableChangeEntityLocation(){
		if (enabledAllListeners){
			this.getModel().removeGraphModelListener(lc);
			this.getModel().addGraphModelListener(changeEntityLocation);
			this.getModel().addGraphModelListener(lc); // listenercontainer must be always the last event listener
		}
	}

	public void enableNARYEdgeLocation(){
		if (enabledAllListeners){
			this.getModel().removeGraphModelListener(lc);
			this.getModel().addGraphModelListener(naryEdgeLocation);
			this.getModel().addGraphModelListener(lc); // listenercontainer must be always the last event listener
		}
	}

	public Model getMJGraph(){
		return (Model) this.getModel();
	}

	public abstract JGraph cloneJGraph(IDEState ids);


	public static ComponentUI createUI(JComponent jc){
		return new BasicGraphUI();
	}

	protected abstract void creaToolBar();

	public JToolBar getPaleta(){ return toolbar;}

	//	abstract public DefaultEdge getInstanciaRelacion(String relacion);

	public abstract DefaultGraphCell getInstanciaNRelacion(String relacion, GraphCell[] selected);

	public abstract DefaultGraphCell createCell(String entity) throws InvalidEntity;
	public abstract Dimension getDefaultSize(Entity entity) throws InvalidEntity;


	public Object[] getRelacionesPosibles(Port source, Port target) {
		// The general getRelacionesPosibles method only admits a GraphCell[] parameter.
		GraphCell sourceGraphCell = (GraphCell) this.getModel().getParent(source);
		GraphCell targetGraphCell = (GraphCell) this.getModel().getParent(target);
		// The general getRelacionesPosibles method is invoked.
		return this.getPossibleRelationships(new GraphCell[]{sourceGraphCell, targetGraphCell});
	}

	abstract public Object[] getPossibleRelationships(GraphCell[] selected);

	abstract public DefaultGraphCell insert(Point point, String entity) throws InvalidEntity;

	abstract public DefaultGraphCell insertDuplicated(Point point, ingenias.editor.entities.Entity entity);

	abstract public Vector<String> getAllowedEntities();
	abstract public Vector<String> getAllowedRelationships();


	public String getID(){
		return this.mde.getId();
	}

	public void setId(String id){
		this.mde.setId(id);
	}

	public ModelDataEntity getProperties(){
		return mde;
	}
	protected Point convert(java.awt.geom.Point2D p2d){
		Point p=new Point((int)p2d.getX(),(int)p2d.getY());
		return p;
	}


	public VertexView createExternalVertexView(Object v, CellMapper cm){
		return null;
	}

	public static java.awt.Point findEmptyPlacePoint(Dimension dim, org.jgraph.JGraph model){
		int j=0;
		int y=30;
		boolean occupied=true;
		while (occupied){
			Rectangle2D clip=new Rectangle(new Point(j,y), dim);
			occupied=model.getGraphLayoutCache().getRoots(clip)!=null  && model.getGraphLayoutCache().getRoots(clip).length!=0;

			/*FirstCellForLocation(j, y)!=null ||
			model.getFirstCellForLocation(j+20, y)!=null ||
			model.getFirstCellForLocation(j, y+20)!=null ||
			model.getFirstCellForLocation(j+20, y+20)!=null;*/
			if (occupied){
				j=(int) (j+dim.getWidth());
			}
			if (j>(model.getVisibleRect().x+model.getVisibleRect().width)){
				j=model.getVisibleRect().x;
				y=(int) (y+dim.getHeight());
			}

		}
		return new  java.awt.Point(j,y);

	}

	public static int findEmptyPlace(org.jgraph.JGraph model){
		int j=0;
		int y=30;
		boolean occupied=true;
		while (occupied){
			occupied=model.getFirstCellForLocation(j, y)!=null ||
					model.getFirstCellForLocation(j+20, y)!=null ||
					model.getFirstCellForLocation(j, y+20)!=null ||
					model.getFirstCellForLocation(j+20, y+20)!=null;
			if (occupied){
				j=j+10;
			}
			if (j>(model.getVisibleRect().x+model.getVisibleRect().width)){
				j=model.getVisibleRect().x;
				y=y+20;
			}

		}
		return j;

	}

	protected ObjectManager getOM() {
		return om;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static boolean getEnabledAllListeners() {
		
		return enabledAllListeners;
	}


	public void paint(Graphics g){
		FieldPositionHelper.clear();
		super.paint(g);		
	}

}
