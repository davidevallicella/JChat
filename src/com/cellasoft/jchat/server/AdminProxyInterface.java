
package com.cellasoft.jchat.server;


import java.rmi.RemoteException;

import com.cellasoft.jchat.exceptions.ClientKickException;

/**
 * Interfaccia remota che estende {@link UserProxyInterface}, viene implementata dal
 * server centrale e fornisce all'amministratore tutti i metodi dell'utente 
 * semplice pi&ugrave i metodi riservati all'amministratore.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave;
 *
 * @version 1.0
 */
public interface AdminProxyInterface extends UserProxyInterface {

    /**
     * @see CentralServer#kickClient(java.lang.String)
     */
    public void kickClient(String nick) throws RemoteException, ClientKickException;

    /**
     * @see CentralServer#banClient(java.lang.String) 
     */
    public void banClient(String nick) throws RemoteException, ClientKickException;
}
