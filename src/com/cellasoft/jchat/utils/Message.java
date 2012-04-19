package com.cellasoft.jchat.utils;

import java.io.Serializable;

/**
 * Classe utilizzata usata per gestire i messaggi di chat.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave
 *
 * @version 1.0
 */
public class Message implements Serializable {

	private static final long serialVersionUID = -8166558262603841809L;
	/**
     *
     * Variabile per identificare il messaggio pubblico
     */
    public static final int PUBLIC_MASSAGE = 0;
    /**
     *
     * Variabile per identificare il messaggio privato
     */
    public static final int PRIVATE_MASSAGE = 1;
    /**
     *
     * Variabile per identificare il messaggio di disconnessione
     */
    public static final int DISCONNECT_MESSAGE = 2;
    /**
     *
     * Variabile per identificare il messaggio di connessione
     */
    public static final int CONNECT_MESSAGE = 3;
    /**
     *
     * Variabile per identificare il messaggio di espulsione
     */
    public static final int KICK_MESSAGE = 4;
    /**
     *
     * Variabile per identificare il messaggio di errore
     */
    public static final int WARNING_MESSAGE = 5;
    
    private String sender;
    private String msg;
    private String time;
    private int type;

    /**
     * Crea un nuovo oggetto di tipo Message.
     *
     * @param msg messaggio
     * @param sender mittente
     * @param time ora
     * @param type tipo di messaggio
     */
    public Message(String msg, String sender, String time, int type) {
        this.msg = msg;
        this.type = type;
        this.sender = sender;
        this.time = time;
    }

    /**
     *
     * @see Message#Message(java.lang.String, java.lang.String, java.lang.String, int)
     */
    public Message(String msg, String sender, int type) {
        this(msg, sender, "", type);
    }

    /**
     * Ritorna il messaggio.
     *
     * @return messaggio
     */
    public String getMessage() {
        return msg;
    }

    /**
     * Ritorna il mittente.
     *
     * @return mittente
     */
    public String getSender() {
        return sender;
    }

    /**
     * Ritorna il tipo di messaggio.
     *
     * @return tipo del messaggio
     */
    public int getType() {
        return type;
    }

    /**
     * Ritorna l'ora del messaggio.
     *
     * @return ora
     */
    public String getTime() {
        return time;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    
}
