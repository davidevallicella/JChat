
package com.cellasoft.jchat.socket;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Questa classe fornisce una implementazione di RMIClientSocketFactory per
 * poter creare delle connessioni tramite il protocollo SSL lato client.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave
 *
 * @version 1.0
 */
public class RMISSLClientSocketFactory
        implements RMIClientSocketFactory, Serializable {

	private static final long serialVersionUID = -3957367308084038144L;

	/**
     * Crea effettivamente la Socket per poter collegarsi al server
     *
     * @param host l'indirizzo dell'host a cui connettersi.
     * @param port la porta dell'host a cui connettersi.
     * @return la Socket creata
     * @throws IOException se si verifica qualche problema durante la creazione della Socket.
     */
    public Socket createSocket(String host, int port) throws IOException {
        SSLSocketFactory factory =
                (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
        return socket;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }
}
