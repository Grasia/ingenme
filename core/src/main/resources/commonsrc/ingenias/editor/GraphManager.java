
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


package ingenias.editor;
import java.util.*;

import org.jgraph.JGraph;
import org.jgraph.graph.*;

import javax.swing.tree.*;
import javax.swing.JTree;

import ingenias.editor.entities.NAryEdgeEntity;
import ingenias.editor.widget.DnDJTree;
import ingenias.exception.NotInitialised;
import ingenias.generator.browser.BrowserImp;

public class GraphManager implements java.io.Serializable {
	public javax.swing.tree.DefaultMutableTreeNode root=null;
	public DnDJTree arbolProyecto=null;

	//	Hashtable models=new Hashtable();
	JGraph current;
	public Vector<TreePath> toExpad=new Vector<TreePath>();
	private int idCounter;




	public GraphManager(javax.swing.tree.DefaultMutableTreeNode root, DnDJTree arbolProyecto) {
		this.root=root;
		this.arbolProyecto=arbolProyecto;
	}

	public void addModel(Object[]path, String nombre, JGraph model1){

		DefaultMutableTreeNode dmn=this.getPath(path);
		if (dmn!=null){

			DefaultMutableTreeNode nn=new DefaultMutableTreeNode(model1);
			dmn.insert(nn,dmn.getChildCount());
			nn.setParent(dmn);
			//			this.models.put(pathToString(path)+","+nombre,model1);
			this.reload();

		}
	}

	public void addPackage(Object[]path, String nombre){
		DefaultMutableTreeNode dmn=this.getPath(path);


		boolean found=false;
		if (dmn!=null && dmn.getChildCount()!=0){
			DefaultMutableTreeNode node=(DefaultMutableTreeNode)dmn.getFirstChild();
			while (node!=null && !found){
				//System.err.println("mirando "+node.getUserObject().toString());
				if (node.getUserObject().toString().equalsIgnoreCase(nombre)){
					/*System.err.print("nodo "+nombre+" encontrado en ");
					for (int k=0;k<path.length;k++){
						System.err.print("/"+path[k]);
					}
					System.err.println("/"+node.getUserObject().toString());*/

					found=true;
				}
				node=node.getNextSibling();
			}
		}

		if (!found && dmn!=null){
			DefaultMutableTreeNode nn=new DefaultMutableTreeNode(nombre);
			dmn.insert(nn,dmn.getChildCount());
			nn.setParent(dmn);
			this.reload();

		} else {		
			//new Exception().printStackTrace();
			//System.err.println("Package already exists "+found+" "+dmn);
		}
	}

	private String pathToString(Object[] path){
		String cam="";
		for (int k=0;k<path.length;k++){
			cam=cam+","+path[k];
		}
		return cam;
	}

	public void removeModel(String[]path){

	}

	public void removePackage(Object[]path){
		DefaultMutableTreeNode dmn=this.getPath(path);
		for (int k=0;k<path.length;k++){
			System.err.print(path[k].toString()+",");
		}
		if (dmn.getParent()!=null){
			dmn.removeFromParent();
			this.reload();
		} else
			System.err.println("null package");
	}

	public DefaultMutableTreeNode getPath(Object[] path){
		if (path.length==1)
			return root;
		return getPath(root,path,1);
	}




	private DefaultMutableTreeNode getPath(DefaultMutableTreeNode root,Object[] path, int index){
		boolean found=false;
		DefaultMutableTreeNode dm=null;
		if (index < path.length){
			Enumeration enumeration=root.children();
			while (enumeration.hasMoreElements() && !found){
				dm=(DefaultMutableTreeNode)enumeration.nextElement();
				if ((dm.getUserObject() instanceof String) &&
						dm.getUserObject().toString().equalsIgnoreCase(path[index].toString())){
					found=true;
				} else 
					if ((dm.getUserObject() instanceof ModelJGraph) &&
							((ModelJGraph)dm.getUserObject()).getName().equalsIgnoreCase(path[index].toString())){
						found=true;
					}

			}
			if (found && index<path.length-1)
				return getPath(dm,path,index+1);
			else
				if (found && index==path.length-1)
					return dm;
				else return null;
		} else return null;
	}

	public ModelJGraph getModel(Object[]path){
		DefaultMutableTreeNode dmtn=(DefaultMutableTreeNode)path[path.length-1];
		if (!((dmtn).getUserObject() instanceof ModelJGraph))
			return null;
		else
			return (ModelJGraph)(dmtn).getUserObject();
		/*   String key=this.pathToString(path);
		 if (models.containsKey(key))
		 return (JGraph)this.models.get(key);
		 else
		 return null;*/
	}

	public void setCurrent(JGraph jg){
		this.current=jg;

	}

	public JGraph getCurrent(){
		return current;
	}

	public void reload(){
		Enumeration expanded=arbolProyecto.getExpandedDescendants(new TreePath(root.getPath()));
		((DefaultTreeModel)arbolProyecto.getModel()).reload();
		while (expanded!=null && expanded.hasMoreElements()){
			TreePath tp=(TreePath)expanded.nextElement();

			arbolProyecto.expandPath(tp);
		}

	}



	public Vector<DefaultGraphCell> getCell(JGraph model, Object o){
		Object[] roots=model.getRoots();
		Vector<DefaultGraphCell> results=new Vector<DefaultGraphCell>();
		for (int k=0;k<roots.length;k++){
			if (((DefaultGraphCell)roots[k]).getUserObject() == o)
				results.add((DefaultGraphCell)roots[k]);
		}
		return results;
	}

	private void removeConnectedEdges(JGraph jg, DefaultGraphCell dgc){

		Vector removableEdges=new Vector();
		for (int k=0;k<jg.getModel().getRootCount();k++){
			Object o=jg.getModel().getRootAt(k);
			if (o instanceof DefaultEdge){
				DefaultEdge de=(DefaultEdge)o;
				DefaultPort sourcePort =(DefaultPort)de.getSource();
				DefaultPort targetPort =(DefaultPort)de.getTarget();
				GraphCell sourceGraphCell=null;

				GraphCell targetGraphCell = null;

				sourceGraphCell = (GraphCell)sourcePort.getParent();
				targetGraphCell = (GraphCell)targetPort.getParent();

				ingenias.editor.cell.NAryEdge ne=null;
				GraphCell object=null;
				if (sourceGraphCell!=null){
					if (ingenias.editor.cell.NAryEdge.class.isAssignableFrom(sourceGraphCell.getClass())){
						ne = (ingenias.editor.cell.NAryEdge)sourceGraphCell;
					} else
						object= (GraphCell) sourceGraphCell;
				}

				if (targetGraphCell!=null){
					if (ingenias.editor.cell.NAryEdge.class.isAssignableFrom(targetGraphCell.getClass())){
						ne = (ingenias.editor.cell.NAryEdge)targetGraphCell;
					} else
						object = (GraphCell) targetGraphCell;
				}

				if (object == dgc){
					GraphCell gc1[]={de};
					GraphCell gc2[]={ne};
					GraphCell gc3[]={object};
					if (ne !=null && ne.acceptRemove(gc1))
						jg.getGraphLayoutCache().remove(gc1,true, true);
					else
						jg.getGraphLayoutCache().remove(gc2,true,true);
				}

			}

		}

	}

	public void removeEntityFromAllGraphs(Object ent){
		Vector v=this.getUOModels();
		Enumeration enumeration=v.elements();
		while (enumeration.hasMoreElements()){
			JGraph jg=(JGraph)enumeration.nextElement();

			Vector<DefaultGraphCell> dgcs=this.getCell(jg,ent);

			if (!dgcs.isEmpty()){
				Object[] cells=dgcs.toArray();

				//cells = ButtonToolBar.this.editor.graph.getDescendants(cells);

				jg.getGraphLayoutCache().remove(cells,true,true);
				jg.getModel().remove(cells);

				//removeConnectedEdges(jg,dgc);
				//dgc.removeAllChildren();
				//cells=jg.getDescendants(cells);
				//jg.getGraphLayoutCache().remove(cells,true,true);

			}
		}
	}

	public Vector<TreeNode[]> getModels(javax.swing.tree.DefaultMutableTreeNode root){
		Vector<TreeNode[]> result=new Vector<TreeNode[]>();
		if (root.getChildCount()>0){
			javax.swing.tree.DefaultMutableTreeNode dfn=(DefaultMutableTreeNode) root.getFirstChild();
			while (dfn!=null){
				TreeNode[] path=dfn.getPath();
				Object uo=((DefaultMutableTreeNode)(path[path.length-1])).getUserObject();
				if (uo instanceof ModelJGraph)
					result.add(path);
				result.addAll(getModels(dfn));
				dfn=dfn.getNextSibling();			
			}		
		}
		return result;
	}

	public Vector<TreeNode[]> getModels(){
		return getModels(this.root);
	}

	public Vector<ModelJGraph> getUOModels(javax.swing.tree.DefaultMutableTreeNode root){
		Vector result=new Vector();
		int k=0;
		while ( k<root.getChildCount()){
			TreeNode dfn = root.getChildAt(k);
			if (
					((DefaultMutableTreeNode)dfn).getUserObject() instanceof ModelJGraph){
				result.add(((DefaultMutableTreeNode)dfn).getUserObject());
			};
			result.addAll(getUOModels((DefaultMutableTreeNode) dfn));

			dfn=root.getChildAt(k);
			k=k+1;
		}
		return result;
	}

	public Vector<ModelJGraph> getUOModels(){
		Vector result=new Vector();
		javax.swing.tree.DefaultMutableTreeNode dfn=this.root;
		return getUOModels(this.root);
	}

	public boolean isDuplicated(String id){
		Enumeration enumeration=this.getUOModels().elements();
		int found=0;

		while (enumeration.hasMoreElements() && found<2){
			ModelJGraph mjg=(ModelJGraph)enumeration.nextElement();
			if (mjg.getID().equalsIgnoreCase(id))
				found++;
		}
		return found>=2;
	}

	public Vector<TreeNode[]> getLeafPackages(){
		Vector result=new Vector();
		javax.swing.tree.DefaultMutableTreeNode dfn=this.root.getFirstLeaf();
		while (dfn!=null){
			TreeNode[] path=dfn.getPath();
			Object uo=((DefaultMutableTreeNode)(path[path.length-1])).getUserObject();
			if (!(uo instanceof ModelJGraph))
				result.add(path);
			dfn=dfn.getNextLeaf();
		}
		return result;
	}

	public boolean existsModel(String id){
		Vector models=this.getUOModels();
		Enumeration enumeration=models.elements();
		boolean found=false;
		while (enumeration.hasMoreElements() && !found){
			ModelJGraph mjg=(ModelJGraph)enumeration.nextElement();
			//System.err.println(mjg.getID());
			found=mjg.getID()!=null && mjg.getID().equalsIgnoreCase(id);
		}
		return found;
	}

	public void createPath(Object[] path){


	}

	/*public static GraphManager createIndependentCopy(javax.swing.tree.DefaultMutableTreeNode root, DnDJTree arbolProyecto){
		GraphManager instance=new GraphManager(root,arbolProyecto);
		return instance;
	}*/

	/*public static void updateCopy(GraphManager copygm){
		instance=copygm;
	}*/



	/*public static GraphManager getInstance(){
		if (instance==null)
			throw new RuntimeException("There is no graph manager instance initialized");
		return instance;
	}*/



	public static GraphManager initInstance(javax.swing.tree.DefaultMutableTreeNode root, DnDJTree arbolProyecto){

		GraphManager instance=new GraphManager(root,arbolProyecto);
		return instance;
	}

	private void findInstancesInTree(DefaultMutableTreeNode dtn,String type, Vector result){
		Enumeration enumeration=this.getUOModels().elements();
		while (enumeration.hasMoreElements()){
			ModelJGraph model=(ModelJGraph)enumeration.nextElement();
			int index=model.getClass().getName().lastIndexOf(".");
			String className=model.getClass().getName().substring(index+1,model.getClass().getName().length());
			if (className.equalsIgnoreCase(type+"modeljgraph"))
				result.add(model.getID());
		}
	}

	public Vector getInstances(String type){
		int index=type.lastIndexOf(".");
		String className=type.substring(index+1,type.length());
		Vector result=new Vector();
		this.findInstancesInTree(root,className,result);
		return result;
	}

	public String[] getModelPath(String id) throws ingenias.exception.NotFound{
		String[] result=null;
		boolean found=false;
		javax.swing.tree.DefaultMutableTreeNode dfn=this.root.getFirstLeaf();
		Object uo=null;
		TreeNode[] path=null;
		while (dfn!=null && !found){
			path=dfn.getPath();
			uo=((DefaultMutableTreeNode)(path[path.length-1])).getUserObject();
			found= (uo instanceof ModelJGraph) &&
			(((ModelJGraph)uo).getID().equals(id));

			dfn=dfn.getNextLeaf();
		}
		if (found){
			result=new String[path.length];
			for (int k=0;k<result.length;k++){
				result[k]=((DefaultMutableTreeNode)path[k]).getUserObject().toString();
			}
		}
		return result;
	}


	public ModelJGraph getModel(String id){
		Enumeration enumeration=this.getUOModels().elements();
		while (enumeration.hasMoreElements()){
			ModelJGraph mjg=(ModelJGraph)enumeration.nextElement();
			if (mjg.getID().equalsIgnoreCase(id))
				return mjg;
		}
		return null;
	}

	public int repeatedInstanceInModels(String id){
		int repeated=0;
		Vector v=this.getUOModels();
		Enumeration enumeration=v.elements();
		while (enumeration.hasMoreElements()){
			ModelJGraph next=(ModelJGraph)enumeration.nextElement();
			for (int k=0;k<next.getModel().getRootCount();k++){
				Object root=next.getModel().getRootAt(k);
				if (root instanceof DefaultGraphCell && ((DefaultGraphCell) root).getUserObject() instanceof ingenias.editor.entities.Entity){
					ingenias.editor.entities.Entity ent=(ingenias.editor.entities.Entity)((DefaultGraphCell)root).getUserObject();
					if (ent.getId().equalsIgnoreCase(id)){
						repeated++;

					}
				}
			}
		}
		return repeated;
	}

	public TreePath findModelTreePath(String nameregexp){
		TreePath found=null;
		Enumeration<DefaultMutableTreeNode> postOrderEnumeration= root.postorderEnumeration();
		while (found==null && postOrderEnumeration.hasMoreElements()){
			DefaultMutableTreeNode current=postOrderEnumeration.nextElement();
			if (ModelJGraph.class.isAssignableFrom(current.getUserObject().getClass())){
				ModelJGraph uo=(ModelJGraph)current.getUserObject();
				if (nameregexp.toLowerCase().equals(uo.getName().toLowerCase()))              	
					found=new TreePath(current.getPath());        

				
			}
			if (String.class.isAssignableFrom(current.getUserObject().getClass())){				
				if (nameregexp.toLowerCase().equals(current.getUserObject().toString().toLowerCase()))              	
					found=new TreePath(current.getPath());        				
			}
		}
		return found;
	}

	public DnDJTree getArbolProyecto() {
		return arbolProyecto;
	}

	public void setArbolProyecto(DnDJTree arbolProyecto) {
		this.arbolProyecto = arbolProyecto;
	}





}