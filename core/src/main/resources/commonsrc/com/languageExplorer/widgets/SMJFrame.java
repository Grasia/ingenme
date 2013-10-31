/*
 * ===========================================================================
 * Copyright 2004 by Volker H. Simonis. All rights reserved.
 * ===========================================================================
 */
package com.languageExplorer.widgets;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.JLayeredPane;

public class SMJFrame extends JFrame {

  protected JRootPane createRootPane() {

    return new JRootPane() {

        protected ScrollableBar scrollableBar;

        /* This method is deprecated however it is used from within JFrame
         * to set a menu, so we have to override it!
         */
        public void setMenuBar(JMenuBar menu){
          setJMenuBar(menu);
        }

        public void setJMenuBar(JMenuBar menu) {
          if(menuBar != null && menuBar.getParent() == scrollableBar)
            layeredPane.remove(scrollableBar);
          menuBar = menu;
                
          if(menuBar != null) {
            scrollableBar = new ScrollableBar(menu);
            layeredPane.add(scrollableBar, JLayeredPane.FRAME_CONTENT_LAYER);
          }
        }


        protected LayoutManager createRootLayout() {

          return new RootLayout() {
              
              public Dimension preferredLayoutSize(Container parent) {
                Dimension rd, mbd;
                Insets i = getInsets();
        
                if(contentPane != null) {
                  rd = contentPane.getPreferredSize();
                } else {
                  rd = parent.getSize();
                }
                if(scrollableBar != null && scrollableBar.isVisible()) {
                  mbd = scrollableBar.getPreferredSize();
                } else {
                  mbd = new Dimension(0, 0);
                }
                return new 
                  Dimension(Math.max(rd.width, mbd.width) + i.left + i.right, 
                            rd.height + mbd.height + i.top + i.bottom);
              }

              public Dimension minimumLayoutSize(Container parent) {
                Dimension rd, mbd;
                Insets i = getInsets();
                if(contentPane != null) {
                  rd = contentPane.getMinimumSize();
                } else {
                  rd = parent.getSize();
                }
                if(scrollableBar != null && scrollableBar.isVisible()) {
                  mbd = scrollableBar.getMinimumSize();
                } else {
                  mbd = new Dimension(0, 0);
                }
                return new 
                  Dimension(Math.max(rd.width, mbd.width) + i.left + i.right, 
                            rd.height + mbd.height + i.top + i.bottom);
              }

              public Dimension maximumLayoutSize(Container target) {
                Dimension rd, mbd;
                Insets i = getInsets();
                if(scrollableBar != null && scrollableBar.isVisible()) {
                  mbd = scrollableBar.getMaximumSize();
                } else {
                  mbd = new Dimension(0, 0);
                }
                if(contentPane != null) {
                  rd = contentPane.getMaximumSize();
                } else {
                  // This is silly, but should stop an overflow error
                  rd = new 
                    Dimension(Integer.MAX_VALUE, 
                              Integer.MAX_VALUE - i.top - i.bottom - mbd.height - 1);
                }
                return new 
                  Dimension(Math.min(rd.width, mbd.width) + i.left + i.right,
                            rd.height + mbd.height + i.top + i.bottom);
              }
        
              public void layoutContainer(Container parent) {
                Rectangle b = parent.getBounds();
                Insets i = getInsets();
                int contentY = 0;
                int w = b.width - i.right - i.left;
                int h = b.height - i.top - i.bottom;
        
                if(layeredPane != null) {
                  layeredPane.setBounds(i.left, i.top, w, h);
                }
                if(glassPane != null) {
                  glassPane.setBounds(i.left, i.top, w, h);
                }
                // Note: This is laying out the children in the layeredPane,
                // technically, these are not our children.
                if(scrollableBar != null && scrollableBar.isVisible()) {
                  Dimension mbd = scrollableBar.getPreferredSize();
                  scrollableBar.setBounds(0, 0, w, mbd.height);
                  contentY += mbd.height;
                }
                if(contentPane != null) {
                  contentPane.setBounds(0, contentY, w, h - contentY);
                }
              }


            };
        } 
      };
  }
}
