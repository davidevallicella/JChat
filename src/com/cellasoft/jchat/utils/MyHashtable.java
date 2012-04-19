
package com.cellasoft.jchat.utils;

import java.util.Hashtable;

import com.cellasoft.jchat.server.MobileServer;

/**
 * Classe che estende Hashtable per gestire gli utenti in chat.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave
 *
 * @version 1.0
 */
public class MyHashtable extends Hashtable<String, MobileServer> {

	private static final long serialVersionUID = 4873872032071901726L;
	private Hashtable<MobileServer, String> hash = new Hashtable<MobileServer, String>();
    private int count = 0;

    /**
     *
     * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public synchronized MobileServer put(String key, MobileServer value) {
        hash.put(value, key);
        count += 1;
        return super.put(key, value);
    }

    /**
     *
     * @see java.util.Hashtable#remove(java.lang.Object, java.lang.Object)
     */
    @Override
    @SuppressWarnings("element-type-mismatch")
    public synchronized MobileServer remove(Object key) {
        hash.remove(this.get(key));
        count -= 1;
        return super.remove(key);
    }

    /**
     * Ritorna l'username associato al mobile server.
     *
     * @param user Mobile server
     * @return l'username
     */
    public String getKey(MobileServer user) {
        return hash.get(user);
    }

    /**
     *
     * @return il numero di utenti in chat
     */
    public int getCountRef() {
        return count;
    }
}
