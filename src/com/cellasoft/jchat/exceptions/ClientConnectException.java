
package com.cellasoft.jchat.exceptions;

/**
 * Questa eccezione viene sollevata quando il client tenta di connettersi 
 * ma &egrave gi&agrave connesso.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave;
 *
 * @version 1.0
 */
public class ClientConnectException extends Exception {

	private static final long serialVersionUID = -3711451578091440067L;
	private String msg;

    /**
     *
     * @param msg Messaggio che verr&agrave spedito al client che solleva l'eccezione
     */
    public ClientConnectException(String msg) {
        this.msg = msg;
    }

    /**
     *
     * @return Messaggio di errore
     */
    @Override
    public String getMessage() {
        return msg;
    }
}
