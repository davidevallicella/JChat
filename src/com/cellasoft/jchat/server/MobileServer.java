
package com.cellasoft.jchat.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.cellasoft.jchat.utils.Message;

/**
 * Interfaccia remota per un generico utente che viene implementata dal 
 * mobile server di un generico utente.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave
 *
 * @version 1.0
 */
public interface MobileServer extends Remote {

    /**
     *
     * @see ImplUserMobileServer#sendMessage(chat.Message) 
     */
    public void sendMessage(Message msg) throws RemoteException;
    
    /**
     *
     * @see ImplUserMobileServer#isConnect()
     */
    public boolean isConnect() throws RemoteException;

    /**
     *
     * @see ImplUserMobileServer#getUsername()
     */
    public String getUsername() throws RemoteException;

    /**
     *
     * @see ImplUserMobileServer#sendPrivateChatRef(chat.MobileServer) 
     */    
    public void sendPrivateChatRef(MobileServer user) throws RemoteException;

    /**
     *
     * @see ImplUserMobileServer#acceptPrivateChat(chat.MobileServer) 
     */
    public boolean acceptPrivateChat(MobileServer builder) throws RemoteException;
}
