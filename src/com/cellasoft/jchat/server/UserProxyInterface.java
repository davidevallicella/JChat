
package com.cellasoft.jchat.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

import com.cellasoft.jchat.exceptions.ClientBannedException;
import com.cellasoft.jchat.exceptions.ClientConnectException;
import com.cellasoft.jchat.utils.Message;

/**
 * Interfaccia remota per un generico utente che viene implementata dal server centrale.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave
 *
 * @version 1.0
 */
public interface UserProxyInterface extends Remote {

    /**
     *
     * @see CentralServer#connect(chat.MobileServer)
     */
    public void connect(MobileServer user) throws RemoteException, ClientBannedException, ClientConnectException;

    /**
     * 
     * @see CentralServer#disconnect(chat.MobileServer)
     */
    public void disconnect(MobileServer user) throws RemoteException;

    /**
     * 
     * @see CentralServer#broadcastMessage(chat.MobileServer, chat.Message)
     */
    public void broadcastMessage(Message msg) throws RemoteException;

    /**
     *
     * @see CentralServer#getClientsList()
     */
    public LinkedList getClientsList() throws RemoteException;

    /**
     *
     * @see CentralServer#privateChat(chat.MobileServer, java.lang.String) 
     */
    public String privateChat(MobileServer builder, String nick) throws RemoteException;
}
