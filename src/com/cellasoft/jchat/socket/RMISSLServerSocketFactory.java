
package com.cellasoft.jchat.socket;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

/**
 * Questa classe fornisce una implementazione di RMIServerSocketFactory per
 * poter creare delle connessioni tramite il protocollo SSL lato server.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave
 *
 * @version 1.0
 */
public class RMISSLServerSocketFactory implements RMIServerSocketFactory {

    private SSLServerSocketFactory ssf;

    /**
     * Apre il keystore usando una password nota per poter prendere il certificato
     * e poter così creare una connessione SSL.
     * 
     * @throws Exception se si verifica qualche problema durante la creazione della
     * factory di connessione.
     */
    public RMISSLServerSocketFactory() throws Exception {
        char[] passphrase = "123456".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");

        String path = System.getProperty("user.dir");
        ks.load(new FileInputStream(path + "/javarmi/server/keystore"), passphrase);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, passphrase);

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(kmf.getKeyManagers(), null, null);

        ssf = ctx.getServerSocketFactory();
    }

    /**
     * Crea effettivamente la ServerSocket che si mette in ascolto di messaggi
     * da parte dei client.
     * 
     * @param port la porta a cui bindarsi per ricevere i messaggi.
     * @return la ServerSocket creata
     * @throws IOException se si verifica qualche problema durante la creazione della ServerSocket.
     */
    public ServerSocket createServerSocket(int port) throws IOException {
        return ssf.createServerSocket(port);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Ritorna true se l'oggetto obj &egrave uguale alla classe.
     *
     * @param obj
     * @return boolean
     */
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
