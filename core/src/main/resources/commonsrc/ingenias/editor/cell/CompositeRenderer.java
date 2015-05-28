package ingenias.editor.cell;

import java.awt.Component;
import java.util.Map;

import javax.swing.JComponent;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.VertexRenderer;

import ingenias.editor.entities.Entity;
import ingenias.editor.entities.ViewPreferences.ViewType;

public abstract class CompositeRenderer extends VertexRenderer{
	
	  public abstract JComponent getConcreteSubComponent(String fieldname, Entity ent,Map cellAttributes); 
	  public abstract Component getRendererComponent(JGraph graph,
				CellView view, boolean sel,
				boolean focus, boolean preview, ViewType vt);
	  
}
