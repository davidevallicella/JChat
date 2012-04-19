
package com.cellasoft.jchat.server;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cellasoft.jchat.utils.Message;

/**
 * Classe utilizzata dal server centrale per spedire i messaggi ai client.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave
 *
 * @version 1.0
 */
public class SendMessageThread extends Thread {

    MobileServer user;
    Message msg;

    /**
     * Costruisce una nuova thread per spedire il messaggio.
     *
     * @param user Mobile server del'utente a cui si desidera mandare il messaggio
     * @param msg messaggio da spedire
     */
    public SendMessageThread(MobileServer user, Message msg) {
        this.user = user;
        this.msg = msg;
    }

    /**
     * chiama il metodo sendMessage dell'utente.
     * 
     */
    @Override
    public void run() {
        try {
            user.sendMessage(msg);
            stop();
        } catch (RemoteException ex) {
            Logger.getLogger(SendMessageThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
