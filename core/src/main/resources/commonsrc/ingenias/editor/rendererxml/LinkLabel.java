package ingenias.editor.rendererxml;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.net.URL;
import java.util.Map;

import javax.swing.border.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import ingenias.editor.FieldPositionHelper;
import ingenias.editor.FontConfiguration;
import ingenias.editor.ModelJGraph;

public class LinkLabel extends JLabel {

	public LinkLabel() {
		//    DashedBorder db=new DashedBorder(Color.black);
		//    this.setBorder(db);
		
		this.setFont(FontConfiguration.getConfiguration().getStandardFont());
		Font font = this.getFont();
		Map attributes = font.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		this.setFont(font.deriveFont(attributes));
		this.setForeground(Color.blue);
		
	}

	public LinkLabel(String text) {
		//    DashedBorder db=new DashedBorder(Color.black);
		//    this.setBorder(db);
		
		this.setFont(FontConfiguration.getConfiguration().getStandardFont());		
		Font font = this.getFont();
		Map attributes = font.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);		
		this.setFont(font.deriveFont(attributes));
		this.setForeground(Color.blue);
		setText(text);		
	}
	

	



	private URL url=null;
	public void setLink(URL url){
		this.url=url;
	}

	public URL getLink(){
		return this.url;
	}

	public void setText(String t){
			if (t.length()==0)
				super.setText("");
			else
				super.setText(t); //HTML labels consume too much cpu
			this.setFont(FontConfiguration.getConfiguration().getStandardFont());		
			Font font = this.getFont();
			Map attributes = font.getAttributes();
			attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);		
			this.setFont(font.deriveFont(attributes));
			this.setForeground(Color.blue);
	}


	@Override
	public Dimension getPreferredSize() {
		if (getText().length()==0 && getIcon()==null)
			return new Dimension(0,0);
		else
			return super.getPreferredSize();
	}



	@Override
	public Dimension getMaximumSize() {
		if (getText().length()==0 && getIcon()==null)
			return new Dimension(0,0);
		else
			return super.getMaximumSize();
	}
	


	@Override
	public Dimension getMinimumSize() {
		if (getText().length()==0 && getIcon()==null)
			return new Dimension(0,0);
		else
			return super.getMinimumSize();
	}

	public static void main(String args[]){

		JFrame jf=new JFrame();
		jf.getContentPane().add(new LinkLabel("hola"));
		jf.pack();
		jf.setVisible(true);
	}


	public void paint(Graphics g){
		super.paint(g);   
		this.enableEvents(MouseEvent.MOUSE_CLICKED|MouseEvent.MOUSE_ENTERED|MouseEvent.MOUSE_EXITED);
		this.setEnabled(true);
		Dimension size=this.getSize();
		/*Color color = g.getColor();
		g.setPaintMode();
		g.setColor(Color.blue);		
		g.drawRect(0,0,size.width-1,size.height-1);
		g.setColor(color);*/
		 Container parent = this;
		 Rectangle nbound=this.getBounds();
		while  (parent.getParent()!=null && !(parent.getParent() instanceof ModelJGraph)){
			parent=parent.getParent();
			nbound.setLocation((int)(nbound.getX()+parent.getBounds().getX()),(int)(nbound.getY()+parent.getBounds().getY()));
		}
		if (parent!=null && this.getLink()!=null){			
		 FieldPositionHelper.put(this.getLink().toString(), "", nbound);

		}
		
	
	}


}
