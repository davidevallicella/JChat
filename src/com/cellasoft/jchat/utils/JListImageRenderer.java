package com.cellasoft.jchat.utils;

import java.awt.Color;
import java.awt.Component;
import java.util.Hashtable;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

public class JListImageRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = -243688659059092021L;
	private Hashtable iconTable = new Hashtable();

    @Override
    public Component getListCellRendererComponent(JList list,
                                                Object value,
                                                int index,
                                                boolean isSelected,
                                                boolean hasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(list,
                                                                    value,
                                                                    index,
                                                                    isSelected,
                                                                    hasFocus);
        if (value instanceof JListClientItem) {
            JListClientItem jLI = (JListClientItem) value;
            ImageIcon icon = (ImageIcon) iconTable.get(value);
            if (icon == null) {
                icon = new ImageIcon(jLI.getImageFile());
                iconTable.put(value, icon);
            }
            label.setIcon(icon);
            setToolTipText(jLI.getComment());
            if (index % 2 == 0) {
                setBackground(new Color(233, 239, 248));
            } else {
                setBackground(Color.white);
            }
        } else {
            // Clear old icon; needed in 1st release of JDK 1.2
            label.setIcon(null);
        }
        return (label);
    }
}
