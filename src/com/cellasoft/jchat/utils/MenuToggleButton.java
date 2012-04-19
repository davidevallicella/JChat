/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cellasoft.jchat.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 *
 * @author Davide
 */
class MenuArrowIcon implements Icon {

    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.BLACK);
        g2.translate(x, y);
        g2.drawLine(2, 3, 6, 3);
        g2.drawLine(3, 4, 5, 4);
        g2.drawLine(4, 5, 4, 5);
        g2.translate(-x, -y);
    }

    public int getIconWidth() {
        return 9;
    }

    public int getIconHeight() {
        return 9;
    }
}

class ColorIcon implements Icon {

    Color color;

    public ColorIcon(Color c) {
        color = c;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(color);
        g2.translate(x, y);
        g2.drawLine(0, 2, 14, 2);
        g2.drawLine(0, 3, 14, 3);
        g2.drawLine(0, 4, 14, 4);
        g2.drawLine(0, 5, 14, 5);
        g2.drawLine(0, 6, 14, 6);
        g2.translate(-x, -y);
    }

    public int getIconWidth() {
        return 14;
    }

    public int getIconHeight() {
        return 6;
    }
}

public class MenuToggleButton extends JToggleButton {

	private static final long serialVersionUID = 3670426512083199063L;
	private static final Icon i = new MenuArrowIcon();
    private Icon c = new ColorIcon(Color.black);
    private boolean typeFore;
    private Color color = Color.BLACK;

    public MenuToggleButton() {
        this("", null, false);
    }

    public MenuToggleButton(boolean foreground) {
        this("", null, foreground);
    }

    public MenuToggleButton(Icon icon, boolean foreground) {
        this("", icon, foreground);
    }

    public MenuToggleButton(String text) {
        this(text, null, false);
    }

    public MenuToggleButton(String text, Icon icon, boolean foreground) {
        super();
        typeFore = foreground;
        Action a = new AbstractAction(text) {

            public void actionPerformed(ActionEvent ae) {
                MenuToggleButton b = (MenuToggleButton) ae.getSource();
                if (pop != null) {
                    pop.show(b, 0, b.getHeight());
                }
            }
        };
        a.putValue(Action.SMALL_ICON, icon);
        setAction(a);
        setFocusable(false);
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4 + i.getIconWidth()));
    }
    protected JPopupMenu pop;

    public void setPopupMenu(final JPopupMenu pop) {
        this.pop = pop;
        pop.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuCanceled(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                setSelected(false);
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension dim = getSize();
        Insets ins = getInsets();
        int x = dim.width - ins.right;
        int y = ins.top + (dim.height - ins.top - ins.bottom - i.getIconHeight()) / 2;
        i.paintIcon(this, g, x, y);
        if (typeFore) {
            x = 4;
            y = 13;
            c.paintIcon(this, g, x, y);
        }
    }

    public void setIcon(Color color) {
        this.color = color;
        c = new ColorIcon(color);
        repaint();
    }
    
    public Color getColor(){
        return color;
    }
}
