/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cellasoft.jchat.utils;

import javax.swing.JPopupMenu;
import javax.swing.JTextField;

/**
 *
 * @author davide
 */
public class EmoticonPopupMenu extends JPopupMenu{
  
	private static final long serialVersionUID = 272885324576692635L;
	private EmoticonPanelChooser emoticonChooser;

    public EmoticonPopupMenu(JTextField inputTF) {
        super();
        emoticonChooser = new EmoticonPanelChooser(inputTF, this);
        add(emoticonChooser);
    }
    
}
