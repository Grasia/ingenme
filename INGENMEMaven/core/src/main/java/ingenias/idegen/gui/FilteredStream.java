package ingenias.idegen.gui;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

//Code taken from http://www.java2s.com/Code/Java/File-Input-Output/CaptureSystemoutintoaJFrame.htm
public class FilteredStream extends FilterOutputStream {
	JTextArea jta;
    public FilteredStream(OutputStream aStream,JTextArea jta) {
      super(aStream);
      this.jta=jta;
    }

    public void write(byte b[]) throws IOException {
      final String aString = new String(b);
      SwingUtilities.invokeLater(new Runnable(){

		@Override
		public void run() {
			jta.append(aString);
		}
    	  
      });
      
    }

    public void write(byte b[], int off, int len) throws IOException {
      final String aString = new String(b, off, len);
      SwingUtilities.invokeLater(new Runnable(){

  		@Override
  		public void run() {
  			jta.append(aString);
  		}
      	  
        });
    }
  };
