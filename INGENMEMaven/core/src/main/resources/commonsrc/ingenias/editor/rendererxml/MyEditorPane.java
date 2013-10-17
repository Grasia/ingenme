package ingenias.editor.rendererxml;

import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;

import java.awt.Dimension; 
import javax.swing.*; 
import javax.swing.text.Element; 
import javax.swing.text.View; 
import javax.swing.text.ViewFactory; 
import javax.swing.text.html.HTMLEditorKit; 
import javax.swing.text.html.InlineView; 
import javax.swing.text.html.ParagraphView; 

public class MyEditorPane extends JTextPane {

	public MyEditorPane() {
		super();
		// taken from Stanislav Lapitsky. http://java-sl.com/tip_html_letter_wrap.html
		setEditorKit(new HTMLEditorKit(){ 
	           @Override 
	           public ViewFactory getViewFactory(){ 
	 
	               return new HTMLFactory(){ 
	                   public View create(Element e){ 
	                      View v = super.create(e); 
	                      if(v instanceof InlineView){ 
	                          return new InlineView(e){ 
	                              public int getBreakWeight(int axis, float pos, float len) { 
	                                  return GoodBreakWeight; 
	                              } 
	                              public View breakView(int axis, int p0, float pos, float len) { 
	                                  if(axis == View.X_AXIS) { 
	                                      checkPainter(); 
	                                      int p1 = getGlyphPainter().getBoundedPosition(this, p0, pos, len); 
	                                      if(p0 == getStartOffset() && p1 == getEndOffset()) { 
	                                          return this; 
	                                      } 
	                                      return createFragment(p0, p1); 
	                                  } 
	                                  return this; 
	                                } 
	                            }; 
	                      } 
	                      else if (v instanceof ParagraphView) { 
	                          return new ParagraphView(e) { 
	                              protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) { 
	                                  if (r == null) { 
	                                        r = new SizeRequirements(); 
	                                  } 
	                                  float pref = layoutPool.getPreferredSpan(axis); 
	                                  float min = layoutPool.getMinimumSpan(axis); 
	                                  // Don't include insets, Box.getXXXSpan will include them. 
	                                    r.minimum = (int)min; 
	                                    r.preferred = Math.max(r.minimum, (int) pref); 
	                                    r.maximum = Integer.MAX_VALUE; 
	                                    r.alignment = 0.5f; 
	                                  return r; 
	                                } 
	 
	                            }; 
	                        } 
	                      return v; 
	                    } 
	                }; 
	            } 
	        }); 
		//StyledEditorKit editor = new StyledEditorKit();	
		setContentType("text/html");
		//setBorder(BorderFactory.createEtchedBorder());		
	}

	@Override
	public void setText(String t) {
		// TODO Auto-generated method stub
		super.setText(t);
		StyledDocument doc = this.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
	}
	
	public Dimension getPreferredSize(){
		Dimension current=super.getPreferredSize();
		if (current.height<10) current.height=20;
		if (current.width<50) current.width=50;
		return current;
	}
	
	

	/*public MyEditorPane(String type, String text) {
		super(type, "<html><font size=\"24\">"+text+"</font></html>");
		setEditorKit(new HTMLEditorKit());
		setBorder(BorderFactory.createEtchedBorder());
	}

	public MyEditorPane(String url) throws IOException {
		super(url);
		setEditorKit(new StyledEditorKit());
		setBorder(BorderFactory.createEtchedBorder());
	}

	public MyEditorPane(URL url) throws IOException {
		super(url);
		setEditorKit(new StyledEditorKit());
		setBorder(BorderFactory.createEtchedBorder());
	}*/

}
