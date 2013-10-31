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

package ingenias.editor.cell;


import org.swixml.SwingEngine;

public class SWIRenderer {
  private static SwingEngine swiengine = new SwingEngine(null);
  static {
	  
      swiengine.getTaglib().registerTag("scalablelabel".toLowerCase(),
              ingenias.editor.rendererxml.ScalableJLabel.class);
        swiengine.getTaglib().registerTag("stereotype".toLowerCase(),
                                      ingenias.editor.rendererxml.JLabelStereotype.class);
    swiengine.getTaglib().registerTag("htmllabel".toLowerCase(),
                                      ingenias.editor.rendererxml.JLabelHTML.class);
    swiengine.getTaglib().registerTag("iconlabel".toLowerCase(),
                                      ingenias.editor.rendererxml.JLabelIcon.class);
    swiengine.getTaglib().registerTag("collectionpanel",
                                      ingenias.editor.rendererxml.CollectionPanel.class);
    swiengine.getTaglib().registerTag("withoutportspanel",
            ingenias.editor.rendererxml.WithoutPortsPanel.class);
    swiengine.getTaglib().registerTag("container",
            ingenias.editor.rendererxml.ContainerPanel.class);
    swiengine.getTaglib().registerTag("vcontainer",
            ingenias.editor.rendererxml.VerticalContainerPanel.class);
    swiengine.getTaglib().registerTag("hcontainer",
            ingenias.editor.rendererxml.HorizontalContainerPanel.class);
    swiengine.getTaglib().registerTag("econtainer",
            ingenias.editor.rendererxml.ExternalContainerPanel.class);
    swiengine.getTaglib().registerTag("label",
    		 ingenias.editor.rendererxml.HTMLLabel.class);
    swiengine.getTaglib().registerTag("linepanel",
                                      ingenias.editor.rendererxml.LinePanel.class);
    swiengine.getTaglib().registerTag("htmllabel",
                                      ingenias.editor.rendererxml.HTMLLabel.class);
    swiengine.getTaglib().registerTag("wraplabel",
                                      ingenias.editor.rendererxml.JMultilineLabel.class);
    swiengine.getTaglib().registerTag("dashedpanel",
                                      ingenias.editor.rendererxml.
                                      DashedBorderPanel.class);
    swiengine.getTaglib().registerTag("doubleborderpanel",
            ingenias.editor.rendererxml.
            DoubleBorderPanel.class);

    swiengine.getTaglib().registerTag("myeditor",
            ingenias.editor.rendererxml.
            MyEditorPane.class);
    
    swiengine.getTaglib().registerTag("dashedverticallinepanel",
                                      ingenias.editor.rendererxml.DashedVerticalLinePanel.class);
    swiengine.getTaglib().registerTag("attributepanel",
            ingenias.editor.rendererxml.AttributesPanel.class);

    /*    swiengine.getTaglib().registerTag("paintpanel",
         ingenias.editor.rendererxml.PaintModePanel.class);*/

  }

  public static SwingEngine getSWIEngine() {
    return swiengine;
  }

  public static SwingEngine getAnotherSWIEngine() {
    SwingEngine swiengine = new SwingEngine(null); 
       swiengine.getTaglib().registerTag("stereotype".toLowerCase(),
                                      ingenias.editor.rendererxml.JLabelStereotype.class);
    swiengine.getTaglib().registerTag("htmllabel".toLowerCase(),
                                      ingenias.editor.rendererxml.JLabelHTML.class);
    swiengine.getTaglib().registerTag("iconlabel".toLowerCase(),
                                      ingenias.editor.rendererxml.JLabelIcon.class);
    swiengine.getTaglib().registerTag("collectionpanel",
                                      ingenias.editor.rendererxml.CollectionPanel.class);
    swiengine.getTaglib().registerTag("linepanel",
                                      ingenias.editor.rendererxml.LinePanel.class);
   /* swiengine.getTaglib().registerTag("htmllabel",
                                      ingenias.editor.rendererxml.HTMLLabel.class);*/
    swiengine.getTaglib().registerTag("wraplabel",
                                      ingenias.editor.rendererxml.JMultilineLabel.class);
    swiengine.getTaglib().registerTag("dashedpanel",
                                      ingenias.editor.rendererxml.
                                      DashedBorderPanel.class);
    swiengine.getTaglib().registerTag("dashedverticallinepanel",
                                      ingenias.editor.rendererxml.DashedVerticalLinePanel.class);

    return swiengine;
  }

}
