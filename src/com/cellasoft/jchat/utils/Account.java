
package com.cellasoft.jchat.utils;

import java.io.Serializable;

/**
 * Classe per gestire le informazioni degli utenti che si connettono in chat.
 *
 * @author Davide Vallicella
 * @author Nicola Aldà
 *
 * @version 1.0
 */
public class Account implements Serializable {
    
	private static final long serialVersionUID = 2412534856287019733L;
	private String username;
    private String password;


    /**
     * Costruisce un nuovo account.
     *
     * @param username 
     * @param password 
     */
    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }
    

    /**
     * Imposta nuovo username
     *
     * @param username Nuovo username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Imposta nuova password
     *
     * @param password Nuova password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Ritorna la password di questo account.
     *
     * @return Password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Ritorna l'username di questo account.
     *
     * @return Username
     */
    public String getUsername() {
        return username;
    }
    
    
}
