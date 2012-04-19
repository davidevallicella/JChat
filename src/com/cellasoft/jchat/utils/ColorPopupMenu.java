package com.cellasoft.jchat.utils;

import javax.swing.JPopupMenu;

/**
 * 
 * @author Davide
 */
public class ColorPopupMenu extends JPopupMenu {

	private static final long serialVersionUID = 5239557727948811168L;
	private ColorPanelChooser colorPanel;

	public ColorPopupMenu(ChatGUIInterface owner, MenuToggleButton b, boolean modal) {
		super();
		colorPanel = new ColorPanelChooser(owner, this, b);
		add(colorPanel);
	}
}
