
package com.cellasoft.jchat.server;

import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.security.auth.login.FailedLoginException;

import com.cellasoft.jchat.utils.Account;

/**
 * Interfaccia remota che verr&agrave implementata dal server di autenticazione
 * per fornire ai client la possibilità di loggarsi in chat.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave
 *
 * @version 1.0
 */
public interface LoginProxyInterface extends Remote {

    /**
     *
     * @see AuthenticationServer#login(chat.Account) 
     */
    public MarshalledObject<?> login(Account account) throws RemoteException, FailedLoginException, IOException;
}
