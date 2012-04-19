
package com.cellasoft.jchat.exceptions;

/**
 * Questa eccezione viene sollevata quando il client tenta di connettersi 
 * ma che &egrave stato bannato precedentemente.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave;
 *
 * @version 1.0
 */
public class ClientBannedException extends Exception {

	private static final long serialVersionUID = 3579610779534382151L;
	private String msg;

    /**
     *
     * @param msg Messaggio che verr&agrave spedito al client che solleva l'eccezione
     */
    public ClientBannedException(String msg) {
        super();
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
