package com.cellasoft.jchat.utils;

import java.rmi.RemoteException;

import javax.swing.JFrame;

import com.cellasoft.jchat.admin.AdminGUI;
import com.cellasoft.jchat.server.MobileServer;
import com.cellasoft.jchat.user.UserGUI;

/**
 * Interfaccia locale implementata dalle classi {@link AdminGUI} e {@link UserGUI}
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave;
 *
 * @version 1.0
 */
public interface ChatGUIInterface {

    /**
     * @see AdminGUI#getPrivateArea(java.lang.String) 
     * @see UserGUI#getPrivateArea(java.lang.String) 
     */
    public ChatArea getPrivateArea(String user) throws RemoteException;

    /**
     * @see AdminGUI#getPublicArea() 
     * @see UserGUI#getPublicArea() 
     */
    public ChatArea getPublicArea();

    /**
     * @see AdminGUI#addPrivateArea(chat.MobileServer) 
     * @see UserGUI#addPrivateArea(chat.MobileServer) 
     */
    public ChatArea addPrivateArea(MobileServer builder) throws RemoteException;

    /**
     * @see AdminGUI#removePrivateArea(java.lang.String) 
     * @see UserGUI#removePrivateArea(java.lang.String) 
     */
    public void removePrivateArea(String user);

    /**
     * @see AdminGUI#updateList(java.lang.String, boolean) 
     * @see UserGUI#updateList(java.lang.String, boolean)      
     */
    public void updateList(String name, boolean connect) throws RemoteException;

    /**
     * @see AdminGUI#close() 
     * @see UserGUI#close() 
     */
    public void close();

    /**
     * @see AdminGUI#getFrame() 
     * @see UserGUI#getFrame() 
     */
    public JFrame getFrame();
}
