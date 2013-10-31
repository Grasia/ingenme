package ingenias.editor.rendererxml;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.*;

import javax.swing.border.*;
import ingenias.editor.FontConfiguration;

public class HTMLLabel extends JLabel {

	public HTMLLabel() {
		//    DashedBorder db=new DashedBorder(Color.black);
		//    this.setBorder(db);
		this.setFont(FontConfiguration.getConfiguration().getStandardFont());
	}

	public HTMLLabel(String text) {
		//    DashedBorder db=new DashedBorder(Color.black);
		//    this.setBorder(db);
		this.setFont(FontConfiguration.getConfiguration().getStandardFont());
		setText(text);
	}

	public void setText(String t){
			if (t.length()==0)
				super.setText("");
			else
				super.setText(t); //HTML labels consume too much cpu
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
		jf.getContentPane().add(new HTMLLabel("hola"));
		jf.pack();
		jf.setVisible(true);
	}



}
