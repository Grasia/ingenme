
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
import ingenias.editor.CommonMenuEntriesActionFactory;
import ingenias.editor.cell.NAryEdge;
import ingenias.editor.editiondialog.MyJLabel;
import ingenias.editor.entities.Entity;
import ingenias.editor.entities.RoleEntity;
import ingenias.editor.widget.GraphicsUtils;
import ingenias.exception.NotInitialised;
import ingenias.exception.NullEntity;
import ingenias.generator.browser.BrowserImp;
import ingenias.generator.browser.Graph;
import ingenias.generator.browser.GraphEntity;
import ingenias.generator.browser.GraphRelationship;
import ingenias.generator.browser.GraphRelationshipImp;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.Map;
import java.util.Hashtable;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.event.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.event.UndoableEditEvent;
import javax.swing.tree.TreePath;

import org.jgraph.JGraph;
import org.jgraph.graph.*;
import org.jgraph.event.*;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.jgraph.JGraph;
import org.jgraph.graph.*;
import org.jgraph.event.*;
import org.jgraph.plaf.basic.*;

//MarqueeHandler that Connects Vertices and Displays PopupMenus


public class MarqueeHandler extends BasicMarqueeHandler  implements java.io.Serializable {


	/*  public Preferences getPref() {
		return pref;
	}*/


	// Holds the Start and the Current Point
	/**
	 *  Description of the Field
	 */
	protected Point start, current;

	// Holds the First and the Current Port
	/**
	 *  Description of the Field
	 */
	protected PortView port, firstPort;

	private ModelJGraph graph;

	private Vector<AbstractAction> additionalActions=new Vector<AbstractAction>(); 
	private CommonMenuEntriesActionFactory af;
	private DiagramMenuEntriesActionsFactory daf;

	private GUIResources resources;

	private IDEState ids;

	public MarqueeHandler(ModelJGraph graph, 
			GUIResources resources, 
			IDEState ids, DiagramMenuEntriesActionsFactory daf){
		this.graph=graph;
		this.af=new CommonMenuEntriesActionFactory(resources,ids);
		this.daf=daf;
		this.resources=resources;
		this.ids=ids;
	}
	
	public MarqueeHandler(ModelJGraph graph,			
			IDEState ids, DiagramMenuEntriesActionsFactory daf){
		this.graph=graph;	
		this.daf=daf;
		this.af=new CommonMenuEntriesActionFactory(null,ids);
		this.ids=ids;
		
	}

	public void addContextualMenuAction(AbstractAction action){
		additionalActions.add(action);
	}


	protected Point convert(java.awt.geom.Point2D p2d){
		Point p=new Point((int)p2d.getX(),(int)p2d.getY());
		return p;
	}


	public static DefaultGraphCell getOtherExtremeFromAryEdge(GraphModel m,DefaultEdge de){
		DefaultPort sourcePort = (DefaultPort) ( (Edge) de).getSource();
		Object source = m.getParent(sourcePort);
		Port targetPort = (Port) ( (Edge) de).getTarget();
		Object target = m.getParent(targetPort);

		if (!(source instanceof NAryEdge)) {
			return (DefaultGraphCell) source;
		}
		if (!(target instanceof NAryEdge)) {
			return (DefaultGraphCell) target;
		}
		return null;

	}
	
	public static ingenias.editor.cell.NAryEdge getNAryEdge(GraphModel m,DefaultEdge de){
		DefaultPort sourcePort = (DefaultPort) ( (Edge) de).getSource();
		Object source = m.getParent(sourcePort);
		Port targetPort = (Port) ( (Edge) de).getTarget();
		Object target = m.getParent(targetPort);

		if (source instanceof NAryEdge) {
			return (NAryEdge) source;
		}
		if (target instanceof NAryEdge) {
			return (NAryEdge) target;
		}
		return null;

	}

	// Find a Cell at point and Return its first Port as a PortView
	/**
	 *  Gets the targetPortAt attribute of the MarqueeHandler object
	 *
	 *@param  point  Description of Parameter
	 *@return        The targetPortAt value
	 */
	protected PortView getTargetPortAt(Point point) {
		// Find Cell at point (No scaling needed here)
		Object cell = getGraph().getFirstCellForLocation(point.x, point.y);
		// Loop Children to find PortView
		for (int i = 0; i < getGraph().getModel().getChildCount(cell); i++) {
			// Get Child from Model
			Object tmp = getGraph().getModel().getChild(cell, i);
			// Get View for Child using the Graph's View as a Cell Mapper
			tmp = getGraph().getGraphLayoutCache().getMapping(tmp, false);
			// If Child View is a Port View and not equal to First Port
			if (tmp instanceof PortView && tmp != firstPort) {
				// Return as PortView
				return (PortView) tmp;
			}
		}
		// No Port View found
		return getSourcePortAt(point);
	}


	// Use Xor-Mode on getGraph()ics to Paint Connector
	/**
	 *  Description of the Method
	 *
	 *@param  fg  Description of Parameter
	 *@param  bg  Description of Parameter
	 *@param  g   Description of Parameter
	 */
	protected void paintConnector(Color fg, Color bg, Graphics g) {
		// Set Foreground
		g.setColor(fg);
		// Set Xor-Mode Color
		//
		// Highlight the Current Port
		paintPort(getGraph().getGraphics());
		// If Valid First Port, Start and Current Point
		if (firstPort != null && start != null && current != null) {
			// Then Draw A Line From Start to Current Point
			g.setXORMode(bg);
			g.drawLine(start.x, start.y, current.x, current.y);
			g.setPaintMode();
		}
	}


	// Use the Preview Flag to Draw a Highlighted Port
	/**
	 *  Description of the Method
	 *
	 *@param  g  Description of Parameter
	 */
	protected void paintPort(Graphics g) {
		// If Current Port is Valid
		if (port != null) {
			// If Not Floating Port...
			boolean o = (GraphConstants.getOffset(port.getAttributes()) != null);
			// ...Then use Parent's Bounds
			Rectangle r = (o) ? port.getBounds().getBounds() : port.getParentView().getBounds().getBounds();
			// Scale from Model to Screen
			r = getGraph().toScreen(new Rectangle(r)).getBounds();
			// Add Space For the Highlight Border
			r.setBounds(r.x - 3, r.y - 3, r.width + 6, r.height + 6);
			// Paint Port in Preview (=Highlight) Mode
			//getGraph().getUI().paintCell(g, port, r, true);
		}
	}

	protected ModelJGraph getGraph(){
		return graph;
	}

	// Override to Gain Control (for PopupMenu and ConnectMode)
	/**
	 *  Gets the forceMarqueeEvent attribute of the MarqueeHandler object
	 *
	 *@param  e  Description of Parameter
	 *@return    The forceMarqueeEvent value
	 */
	public boolean isForceMarqueeEvent(MouseEvent e) {
		// If Right Mouse Button we want to Display the PopupMenu
		if (SwingUtilities.isRightMouseButton(e)) {
			// Return Immediately
			return true;
		}
		// Find and Remember Port
		port = getSourcePortAt(e.getPoint());
		// If Port Found and in ConnectMode (=Ports Visible)
		if (port != null && getGraph().isPortsVisible()) {
			return true;
		}
		// Else Call Superclass
		return super.isForceMarqueeEvent(e);
	}


	/**
	 *  Gets the sourcePortAt attribute of the MarqueeHandler object
	 *
	 *@param  point  Description of Parameter
	 *@return        The sourcePortAt value
	 */
	public PortView getSourcePortAt(Point point) {
		// Scale from Screen to Model
		Point tmp = convert(getGraph().fromScreen(new Point(point)));
		// Find a Port View in Model Coordinates and Remember
		return getGraph().getPortViewAt(tmp.x, tmp.y);
	}


	// Display PopupMenu or Remember Start Location and First Port
	/**
	 *  Description of the Method
	 *
	 *@param  e  Description of Parameter
	 */
	public void mousePressed(final MouseEvent e) {

		// If Right Mouse Button
		if (SwingUtilities.isRightMouseButton(e)) {
			// Scale From Screen to Model
			Point loc = convert(getGraph().fromScreen(e.getPoint()));
			// Find Cell in Model Coordinates
			Object cell = getGraph().getFirstCellForLocation(loc.x, loc.y);

			JPopupMenu menu=new JPopupMenu();	
			
			menu.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

			if (cell instanceof DefaultEdge){
				menu.add("Relationship");
				menu.addSeparator();
				NAryEdge nary=this.getNAryEdge((Model)(getGraph().getModel()),(DefaultEdge)cell);
				createRelationshipMenu(menu, nary);	
			} 			
			else
				if (cell instanceof NAryEdge){
					menu.add("Relationship");
					menu.addSeparator();
					NAryEdge nary=(NAryEdge)cell;
					createRelationshipMenu(menu, nary);	
				} 

				else {
					if (cell instanceof DefaultGraphCell){
						// Create PopupMenu for the Cell
						menu.add("Entity");
						menu.addSeparator();
						addActionsToPopupMenu(menu,af.createCellActions((DefaultGraphCell)cell, getGraph()));				
						menu.addSeparator();
						menu.add(createMenu("Views",daf.createChangeViewActions((DefaultGraphCell)cell, getGraph())));
						final ingenias.editor.entities.Entity ent=((ingenias.editor.entities.Entity)((DefaultGraphCell)cell).getUserObject());
						Vector<AbstractAction> actions = af.createEntityActions(ent);
						for (AbstractAction action:actions){
							menu.add(action);
						}                        
						menu.add(createMenu("Refinement",af.createCellRefinementActions(ent)));
					} else {
						if (getGraph().getSelectionCells()!=null && getGraph().getSelectionCells().length>1){
							GraphCell[] gc=new GraphCell[getGraph().getSelectionCells().length];
							System.arraycopy(getGraph().getSelectionCells(),0,gc,0,gc.length);
							addActionsToPopupMenu(menu,af.createDiagramIndependentActions(e.getPoint(),
									gc, getGraph()));
						}
						addActionsToPopupMenu(menu,af.createDiagramOperations(getGraph()));
						menu.addSeparator();
						Vector<AbstractAction> insertActions = daf.createDiagramSpecificInsertActions(e.getPoint(),graph);
						Collections.sort(insertActions, new Comparator<AbstractAction>(){

							@Override
							public int compare(AbstractAction o1,
									AbstractAction o2) {
								return o1.getValue(AbstractAction.NAME).toString().compareTo(o2.getValue(AbstractAction.NAME).toString());
							}
							
						});
						
						addActionsToPopupMenuInsideComponent(menu,insertActions);	
					}
				}


			
			// Display PopupMenu
			menu.show(getGraph(), e.getX(), e.getY());

			// Else if in ConnectMode and Remembered Port is Valid
		}
		else if (port != null && !e.isConsumed() && getGraph().isPortsVisible()) {
			// Remember Start Location
			start = convert(getGraph().toScreen(port.getLocation(null)));
			// Remember First Port
			firstPort = port;
			// Consume Event
			e.consume();

		}
		else {
			// Call Superclass

		}
		super.mousePressed(e);		
	}

	public static void removeAction(
			final ModelJGraph graph,
			final GraphManager gm,
			final ObjectManager om) {
		if (graph != null) {
			if (!graph.isSelectionEmpty()) {
				Object[] cells =
					graph.getSelectionCells();
				
				for (Object cell:cells){
					if (cell instanceof DefaultEdge){
						DefaultEdge de=(DefaultEdge)cell;
							if (!(((DefaultPort)de.getSource()).getParent() instanceof NAryEdge) &&
									!(((DefaultPort)de.getTarget()).getParent() instanceof NAryEdge))
								graph.removeSelectionCell(cell);
					}
				}
				
				cells=graph.getSelectionCells();
				HashSet<Object> childrenInContainer=new HashSet<Object>();
				for (Object ccell:cells){
					if (ccell instanceof DefaultGraphCell)
						childrenInContainer.addAll(
								graph.getListenerContainer().getRecursivelyChildren(
										(DefaultGraphCell) ccell));
					childrenInContainer.add(ccell);
				}
				cells=childrenInContainer.toArray();
				Vector<Object> cellsToRemove=new Vector<Object>();
				for (Object cell:cells){
					if (cell instanceof DefaultGraphCell && ((DefaultGraphCell)cell).getUserObject() instanceof Entity){
						Entity ent=(Entity)((DefaultGraphCell)cell).getUserObject();								
						int rep=gm.repeatedInstanceInModels(ent.getId());
						if (rep==1){
							int res = JOptionPane.showConfirmDialog(graph,
									"Element " + ent.getId() +
									" of type " + ent.getType() + " is no longer used in other diagrams." +
									" Do you want to remove it from the objects database (y/n)?",
									"Remove?",
									JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
							if (res == JOptionPane.OK_OPTION) {										
								om.removeEntity(ent);
								om.reload();									} 
							cellsToRemove.add(cell);

						} else {
							cellsToRemove.add(cell);
						}
					} else {
						cellsToRemove.add(cell);
					}


				}
				//cells = ButtonToolBar.this.editor.graph.getDescendants(cells);
				graph.getGraphLayoutCache().remove(cellsToRemove.toArray(),true,true);
			}
		}
	}


	private void createRelationshipMenu(JPopupMenu menu, NAryEdge nary) {
		String typeOfRelationship=((ingenias.editor.entities.NAryEdgeEntity)nary.getUserObject()).getType();
		Vector<String> roles=new Vector<String>(nary.getRoles());
		addActionsToPopupMenu(menu,af.createCellActions(nary, getGraph()));		
		menu.addSeparator();
		menu.add(createMenu("views",daf.createChangeViewActions(nary, graph)));

		for (int k=0;k<roles.size();k++){
			DefaultEdge[] edgesPerRole=nary.getRoleEdges(roles.elementAt(k));					
			if (edgesPerRole.length>1){
				for (int j=0;j<edgesPerRole.length;j++){
					final RoleEntity re=(RoleEntity)(edgesPerRole[j].getUserObject());
					Vector<AbstractAction> edgeMenuActions = af.createEdgeActions(
							nary.getRoleEdges(roles.elementAt(k))[j], getGraph());		
					DefaultGraphCell dgc=getOtherExtremeFromAryEdge(graph.getModel(), edgesPerRole[j]);
					
					menu.add(createMenu("role:"+roles.elementAt(k)+":"+
							((Entity)dgc.getUserObject()).getId(),edgeMenuActions));
				}
			} else {
				if (edgesPerRole.length==1){
					DefaultGraphCell dgc=getOtherExtremeFromAryEdge(graph.getModel(), nary.getRoleEdges(roles.elementAt(k))[0]);
					
					Vector<AbstractAction> edgeMenuActions = af.createEdgeActions(nary.getRoleEdges(roles.elementAt(k))[0], getGraph());
					menu.add(createMenu("role:"+roles.elementAt(k)+":"+
							((Entity)dgc.getUserObject()).getId(),edgeMenuActions));
				} 
			}


		}
	}


	private JMenu createMenu(String name, Vector<AbstractAction> actions) {
		Iterator<AbstractAction> it=actions.iterator();
		JMenu menu=new JMenu(name);
		while (it.hasNext()){
			menu.add(it.next());
		}
		// TODO Auto-generated method stub
		return menu;
	}
	
	private void addActionsToPopupMenuInsideComponent(final JPopupMenu menu, 
			Vector<AbstractAction> actions) {

		int columns=4;
		int rows=(actions.size()/columns)+1;;
		
		if (actions.size() % columns ==0)
			rows=actions.size()/columns;
		
		
		JPanel jp=new JPanel(new GridLayout(rows, columns,5,5));
			
		
		jp.setBackground(menu.getBackground());
		JScrollPane jsp=new JScrollPane(jp);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		Iterator<AbstractAction> it=actions.iterator();
		while (it.hasNext()){
			final AbstractAction aa=it.next();
			final JLabel jl=new MyJLabel(aa.getValue(AbstractAction.NAME).toString());
			jl.setName(aa.getValue(AbstractAction.NAME).toString());
			if (aa.getValue("tooltip")!=null && 
					!aa.getValue("tooltip").equals("")){
				String text=aa.getValue("tooltip").toString();
				/*if (text.length()>80){
					String ntext="";
					for (int k=0;k<=text.length()/80;k++){
						ntext=ntext+text.substring(k*80,
								Math.min(text.length(), (k+1)*80))+"\n";
					}
					text=ntext;
				}
				text="<html>"+text
						.replaceAll("\n",
								"<br/>")+
								"</html>";*/
				jl.setToolTipText(text);
				// trick from http://tech.chitgoks.com/2010/05/31/disable-tooltip-delay-in-java-swing/
				jl.addMouseListener(new MouseAdapter() {
				    final int defaultDismissTimeout = ToolTipManager.sharedInstance().getDismissDelay();
				    final int dismissDelayMinutes = (int) TimeUnit.MINUTES.toMillis(10); // 10 minutes
				    @Override
				    public void mouseEntered(MouseEvent me) {
				        ToolTipManager.sharedInstance().setDismissDelay(dismissDelayMinutes);
				    }
				 
				    @Override
				    public void mouseExited(MouseEvent me) {
				        ToolTipManager.sharedInstance().setDismissDelay(defaultDismissTimeout);
				    }
				});
			}
			jl.addMouseListener(new MouseAdapter() {
				Color lastColor=null;
				@Override
				public void mouseEntered(MouseEvent e) {
					jl.setOpaque(true);
					lastColor=jl.getBackground();
					jl.setBackground(Color.lightGray);
				}
				
				public void mouseClicked(MouseEvent e) {
					aa.actionPerformed(new ActionEvent(e.getSource(),
							ActionEvent.ACTION_PERFORMED,""));
					menu.setVisible(false);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					jl.setOpaque(false);
					jl.setBackground(lastColor);
					jl.invalidate();
					jl.repaint();
				}
			});
			jp.add(jl);
		}
		menu.add(jsp);
	}


	private void addActionsToPopupMenu(JPopupMenu menu, Vector<AbstractAction> actions) {

		Iterator<AbstractAction> it=actions.iterator();		
		
		while (it.hasNext()){
			AbstractAction aa=it.next();
			
			menu.add(aa);
		}
	}





	// Find Port under Mouse and Repaint Connector
	/**
	 *  Description of the Method
	 *
	 *@param  e  Description of Parameter
	 */
	public void mouseDragged(MouseEvent e) {
		// If remembered Start Point is Valid
		if (start != null && !e.isConsumed()) {
			// Fetch getGraph()ics from Graph
			Graphics g = getGraph().getGraphics();
			// Xor-Paint the old Connector (Hide old Connector)
			paintConnector(Color.black, getGraph().getBackground(), g);
			// Reset Remembered Port
			port = getTargetPortAt(e.getPoint());
			// If Port was found then Point to Port Location
			if (port != null) {
				current = convert(getGraph().toScreen(port.getLocation(null)));
			}
			// Else If no Port was found then Point to Mouse Location
			else {
				current = convert(getGraph().snap(e.getPoint()));

			}
			// Xor-Paint the new Connector
			paintConnector(getGraph().getBackground(), Color.black, g);
			// Consume Event
			e.consume();
		}
		// Call Superclass
		super.mouseDragged(e);
	}


	// Connect the First Port and the Current Port in the Graph or Repaint
	/**
	 *  Description of the Method
	 *
	 *@param  e  Description of Parameter
	 */
	public void mouseReleased(MouseEvent e) {
		// If Valid Event, Current and First Port
		if (e != null && !e.isConsumed() && port != null && firstPort != null &&
				firstPort != port) {
			final PortView firstPortBackup=firstPort;
			final PortView portBackup=port;
			Runnable connectThread=new Runnable(){
				public void run(){
					String className=graph.getClass().getName();
					String diagramType=className.substring(className.lastIndexOf(".")+1, className.indexOf("ModelJGraph"));
					// Then Establish Connection
					RelationshipManager.connect((Port) firstPortBackup.getCell(), 
							(Port) portBackup.getCell(),
							graph, ids.getDiagramFilter().getCurrentAllowedRelationships().get(diagramType));					
				}
			};
			SwingUtilities.invokeLater(connectThread);
			// Consume Event
			e.consume();
			// Else Repaint the Graph
		}

		// Reset Global Vars
		firstPort = port = null;
		start = current = null;
		// Call Superclass
		super.mouseReleased(e);
		if (getGraph()!=null){

			getGraph().invalidate();
			getGraph().revalidate();
			getGraph().repaint();
		}
	}


	// Show Special Cursor if Over Port
	/**
	 *  Description of the Method
	 *
	 *@param  e  Description of Parameter
	 */
	public void mouseMoved(MouseEvent e) {		
		// Check Mode and Find Port
		if (e != null && getGraph()!=null && getSourcePortAt(e.getPoint()) != null &&
				!e.isConsumed() && getGraph().isPortsVisible()) {
			// Set Cusor on Graph (Automatically Reset)
			getGraph().setCursor(new Cursor(Cursor.HAND_CURSOR));
			// Consume Event
			e.consume();
		}
		// Call Superclass
		super.mouseReleased(e);
	}






}
