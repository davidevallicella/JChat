
package com.cellasoft.jchat.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Classe che permette di chiudere i tab delle chat private.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave;
 *
 * @version 1.0
 */
public class CloseTabButton extends JPanel implements ActionListener {

	private static final long serialVersionUID = 393689841101436978L;
	private JTabbedPane pane;
    private JLabel l;

    public CloseTabButton(JTabbedPane pane, int index) {
        this.pane = pane;
        setOpaque(false);
        l = new JLabel(
                pane.getTitleAt(index),
                pane.getIconAt(index),
                JLabel.LEFT);

        add(l);
        ImageIcon closeIcon = new ImageIcon("chat/resources/close.gif");
        ImageIcon closeIcon2 = new ImageIcon("chat/resources/close2.gif");
        JButton btClose = new JButton(closeIcon);
        btClose.setOpaque(false);
        btClose.setRolloverIcon(closeIcon2);
        btClose.setPressedIcon(closeIcon);
        btClose.setBorderPainted(false);
        btClose.setContentAreaFilled(false);
        btClose.setPreferredSize(new Dimension(10, 10));
        btClose.addActionListener(this);
        add(btClose);
        pane.setTabComponentAt(index, this);
    }

    public void colorTitle(Color color) {
        l.setForeground(color);
    }

    public void actionPerformed(ActionEvent e) {
        int i = pane.indexOfTabComponent(this);
        if (i != -1) {
            pane.remove(i);
        }

    }
}
