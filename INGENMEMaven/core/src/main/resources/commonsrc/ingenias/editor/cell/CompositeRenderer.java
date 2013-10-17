package ingenias.editor.cell;

import java.util.Map;

import javax.swing.JComponent;

import org.jgraph.graph.VertexRenderer;

import ingenias.editor.entities.Entity;

public abstract class CompositeRenderer extends VertexRenderer{
	
	  public abstract JComponent getConcreteSubComponent(String fieldname, Entity ent,Map cellAttributes); 
}
