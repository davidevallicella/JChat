package com.cellasoft.jchat.exceptions;

/**
 * Questa eccezione viene sollevata quando si cerca di espellere un client non 
 * connesso o inesistente dalla chat.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave;
 *
 * @version 1.0
 */
public class ClientKickException extends Exception {

	private static final long serialVersionUID = -1391573229380990723L;
	private String msg;

    /**
     *
     * @param msg Messaggio che verr&agrave spedito al client che solleva l'eccezione
     */
    public ClientKickException(String msg) {
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
