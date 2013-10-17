
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
package ingenias.editor.rendererxml;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.border.*;
import java.util.*;
import java.beans.*;
import java.io.*;
import ingenias.editor.*;
import javax.print.attribute.HashAttributeSet;
import javax.swing.*;

public class ExternalContainerPanel extends ContainerPanel {
	public ExternalContainerPanel() {
		super();
		this.removeAll();
	
	}

	public ExternalContainerPanel(LayoutManager p0, boolean p1) {
		super(p0, p1);
	}

	public ExternalContainerPanel(LayoutManager p0) {
		super(p0);
	}

	public ExternalContainerPanel(boolean p0) {
		super(p0);

	}
}
