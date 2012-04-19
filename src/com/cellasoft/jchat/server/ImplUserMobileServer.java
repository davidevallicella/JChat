/*
 * ImplUserMobileServer.java
 *
 * Created on 20-lug-2011
 */
package com.cellasoft.jchat.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import com.cellasoft.jchat.exceptions.ClientBannedException;
import com.cellasoft.jchat.exceptions.ClientConnectException;
import com.cellasoft.jchat.utils.Account;
import com.cellasoft.jchat.utils.ChatArea;
import com.cellasoft.jchat.utils.ChatGUIInterface;
import com.cellasoft.jchat.utils.Message;

/**
 * Implementazione del mobile server dell'utente generico che estende {@link UnicastRemoteObject} 
 * e implementa l'interfaccia remota {@link MobileServer}.
 * Fornisce tutte le operazioni che un client pu&ograve svolgere in chat.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave
 *
 * @version 1.0
 */
public class ImplUserMobileServer extends UnicastRemoteObject implements MobileServer {

	private static final long serialVersionUID = 1512655519448520406L;
	private UserProxyInterface server_stub;
    private Account account;
    private ChatGUIInterface chatGUI;
    private Hashtable<String, MobileServer> privateUsersList = new Hashtable<String, MobileServer>();
    private boolean isConnect;

    /**
     * Crea una nuova istanza di ImplUserMobileServer e poich&egrave estente
     * {@link UnicastRemoteObject} viene esportato su una porta stabilita 
     * dal sistema RMI.
     *
     * @param ref referenza remota del server centrale
     * @param account utente
     * @throws RemoteException
     */
    public ImplUserMobileServer(Remote ref, Account account) throws RemoteException {
        if (ref instanceof UserProxyInterface) {
            this.server_stub = (UserProxyInterface) ref;
            this.account = account;
        }
    }

    /**
     * Imposta l'interfaccia grafica di chat.
     *
     * @param chatGUI interfaccia grafica di chat
     */
    public void setGUI(ChatGUIInterface chatGUI) {
        this.chatGUI = chatGUI;
    }

    /**
     * Metodo invocato dal server centrale nel caso di chat pubblica o da un mobile server
     * nel caso in cui si sta chattando privatamente.
     * Qui si ricevono anche eventuali messaggi di disconnessione o espulsione.
     *
     * @param msg Messaggio ricevuto
     * @throws RemoteException eccezione remota
     */
    @Override
    public void sendMessage(Message msg) throws RemoteException {
        String sender = msg.getSender();
        ChatArea area = null;
        switch (msg.getType()) {
            case Message.PRIVATE_MASSAGE:
                area = chatGUI.getPrivateArea(sender);
                if (area == null) {
                    break;
                }
                area.append(msg);
                break;
            case Message.PUBLIC_MASSAGE:
                area = chatGUI.getPublicArea();
                area.append(msg);
                break;
            case Message.CONNECT_MESSAGE:
                chatGUI.updateList(sender,true);
                area = chatGUI.getPublicArea();
                area.append(msg);
                break;
            case Message.DISCONNECT_MESSAGE:
                if (privateUsersList.containsKey(sender)) {
                    removePrivateChat(sender);
                    chatGUI.removePrivateArea(sender);
                }
                chatGUI.updateList(sender,false);
                area = chatGUI.getPublicArea();
                area.append(msg);
                break;
            case Message.KICK_MESSAGE:
                reset();
                JOptionPane.showMessageDialog(chatGUI.getFrame(), msg.getMessage(), "Kick message", JOptionPane.INFORMATION_MESSAGE);
                chatGUI.close();
                break;
        }
        if (area != null) {
            area.setCaretPosition(area.getText().length());
        }
    }

    /**
     * Ritorna l'username dell'account.
     *
     * @return Il nome utente con i quali si &egrave loggati in chat
     * @throws RemoteException
     */
    @Override
    public String getUsername() throws RemoteException {
        return account.getUsername();
    }

    /**
     * Metodo con il quale i mobile server inviano messaggi nella chat pubblica.
     *
     * @param msg Messaggio da inviare
     * @throws RemoteException
     */
    public void broadcastMessage(Message msg) throws RemoteException {
        if (server_stub != null) {
            server_stub.broadcastMessage(msg);
        }
    }

    /**
     * Metodo che serve per connettersi alla chat.
     *
     * @throws RemoteException eccezione remota
     * @throws ClientBannedException Eccezione sollevata nel caso in cui un client bannato tenta di connettersi
     * @throws ClientConnectException Eccezione sollevata quando il client &egrave gi&agrave connesso alla chat
     */
    public void connect() throws RemoteException, ClientBannedException, ClientConnectException {
        if (server_stub != null) {
            server_stub.connect(this);
            isConnect = true;
        }
    }

    /**
     * Metodo che serve per disconnettersi dalla chat.
     *
     * @throws RemoteException eccezione remota
     */
    public void disconnect() throws RemoteException {
        if (server_stub != null) {
            server_stub.disconnect(this);
            reset();
        }
    }

    /**
     * Ritorna la lista degli utenti connessi in chat.
     *
     * @return Una LinkedList contenente le stringhe degli utenti connessi
     * @throws RemoteException eccezione remota
     */
    public LinkedList getClientsList() throws RemoteException {
        if (server_stub != null) {
            return server_stub.getClientsList();
        } else {
            return null;
        }
    }

    /**
     *
     * @return True se il {@link MobileServer} &egrave connesso al server di chat
     * False altrimenti.
     * 
     * @throws RemoteException eccezione remota
     */
    public boolean isConnect() throws RemoteException {
        return isConnect;
    }

    /**
     * Metodo con il quale un client pu&ograve richidere una chat privata.
     *
     * @param nick Username del client con il quale stabilire una chat privata
     * @throws RemoteException eccezione remota
     */
    public void privateChat(String nick) throws RemoteException {
        if (server_stub != null) {
            if (nick.equals(account.getUsername())) {
                JOptionPane.showMessageDialog(chatGUI.getFrame(), "Non puoi chattare privatamente con te stesso " + nick, "Private chat", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (privateUsersList.containsKey(nick)) {
                chatGUI.getPrivateArea(nick);
                return;
            }
            if (!getClientsList().contains(nick)) {
                JOptionPane.showMessageDialog(chatGUI.getFrame(), "L'utente " + nick + " non è online o non esiste!", "Private chat", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String info = server_stub.privateChat(this, nick);
            JOptionPane.showMessageDialog(chatGUI.getFrame(), info, "Private chat", JOptionPane.INFORMATION_MESSAGE);

        }
    }

    /**
     * Questo metodo viene invocato dal server di chat qualora un client 
     * richieda di chattare privatamente con noi.
     *
     * @param builder {@link MobileServer} del client che vuole chattare privatamente
     * @return True se accetiamo la chat privata False altrimenti
     * @throws RemoteException eccezione remota
     */
    public boolean acceptPrivateChat(MobileServer builder) throws RemoteException {
        String nick = builder.getUsername();
        int value = JOptionPane.showConfirmDialog(chatGUI.getFrame(), nick + " vuole chattare privatamente.\nAccettare richiesta?", nick, JOptionPane.YES_NO_OPTION);

        if (value == JOptionPane.YES_OPTION) {
            sendPrivateChatRef(builder);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Questo metodo serve per rimuovere client disconnessi dalla lista degli 
     * utenti con i quali si ha una chat privata.     *  
     *
     * @param user Username dell'utente da eliminare
     */
    public void removePrivateChat(String user) {
        if (privateUsersList.containsKey(user)) {
            privateUsersList.remove(user);
        }
    }

    /**
     *
     * @return Hashtable<code> username, {@link MobileServer}</code> degli 
     * utenti con i quali si ha una chat privata attiva
     */
    public Hashtable getPrivateList() {
        return privateUsersList;
    }

    private void reset() {
        server_stub = null;
        isConnect = false;
        privateUsersList.clear();
    }

    /**
     * Questo metodo viene invocato dal server di chat qualora un client 
     * accetta la richiesta di chat privata.
     *
     * @param builder {@link MobileServer} del client che vuole chattare privatamente
     * @throws RemoteException eccezione remota
     */
    public void sendPrivateChatRef(MobileServer user) throws RemoteException {
        if (user == null) {
            JOptionPane.showMessageDialog(chatGUI.getFrame(), "Richiesta non accettata.", "Private chat", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String nick = user.getUsername();
        privateUsersList.put(nick, user);
        chatGUI.addPrivateArea(user);
    }
}
