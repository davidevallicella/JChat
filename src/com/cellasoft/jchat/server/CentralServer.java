package com.cellasoft.jchat.server;

import java.rmi.MarshalledObject;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationID;
import java.rmi.activation.UnknownObjectException;
import java.rmi.server.Unreferenced;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.cellasoft.jchat.exceptions.ClientBannedException;
import com.cellasoft.jchat.exceptions.ClientConnectException;
import com.cellasoft.jchat.exceptions.ClientKickException;
import com.cellasoft.jchat.socket.RMISSLClientSocketFactory;
import com.cellasoft.jchat.socket.RMISSLServerSocketFactory;
import com.cellasoft.jchat.utils.Message;
import com.cellasoft.jchat.utils.MyHashtable;
import com.cellasoft.jchat.utils.Utils;

/**
 * Server centrale attivabile che implementa {@link ServerInterface} e {@link Unreferenced}.
 * Il compito di questa classe è quella di gestire i messaggi che vengono spediti nella chat pubblica
 * e di fornire le referenze dei mobile server per le chat private.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave;
 *
 * @version 1.0
 */
public class CentralServer extends Activatable implements ServerInterface, Unreferenced {

    private static final long serialVersionUID = -4885180016006885657L;
    private static final String NO_SUCH_OBJECT_EXCEPTION = "Central Server [unreferenced]: Il server non è stato esportato correttamente.";
    private static final String REMOTE_EXCEPTION = "Central Server [unreferenced]: Errore remoto.";
    private static final String UN_KNOWN_OBJECT_EXCEPTION = "Central Server [unreferenced]: Il server è già inattivo.";
    private static final String ACTIVATION_EXCEPTION = "Central Server [unreferenced]: Il gruppo di attivazione non è attivo.";
    
    private static int port = 5432;
    private MyHashtable userList;
    private Vector<String> bannedList;
    private Lock addLock;
    private Lock sendLock;
    private ActiveReferences ping;
    private boolean isActive = false;

    /**
     * Crea una nuova istanza del server centrale e lo mette in ascolto sulla porta 5432.
     * Questo costruttore è chiamato solo dal sistema di attivazione RMI, in quanto
     * questo è un server attivabile.
     * 
     * @param id Identificativo del gruppo di attivazione
     * @param obj Oggetto Marshallizzato che contiene eventuali parametri di inizializzazione del server
     * @throws RemoteException
     * @throws Exception
     */
    public CentralServer(ActivationID id, MarshalledObject objMO) throws RemoteException, Exception {
        super(id, port, new RMISSLClientSocketFactory(), new RMISSLServerSocketFactory());
        initResource();
        isActive = true;
    }

    /**
     * Metodo con il quale i client si connettono alla chat.
     *
     * @param user Mobile server dell'utente che si connette in chat.
     * @throws RemoteException eccezione remota
     * @throws ClientBannedException eccezione sollevata se il client che vuole
     * connettersi alla chat &egrave bannato
     * @throws ClientConnectException eccezione sollevata se l'utente &egrave 
     * gi&agrave connesso in chat
     */
    @Override
    public synchronized void connect(MobileServer user) throws RemoteException, 
                                                        ClientBannedException, 
                                                        ClientConnectException {
        if (!isActive) {
            throw new ClientConnectException("Il server di chat non è ancora attivo"
                    + "..attendere ancora qualche secondo e riprovare.");
        }
        try {
            
            boolean acquired = addLock.tryLock(5, TimeUnit.SECONDS);

            if (acquired) {

                String nick = user.getUsername();
                if (bannedList.contains(nick)) {
                    throw new ClientBannedException("L'utente " + nick + " è stato bannato!");
                }
                if (userList.containsKey(nick)) {
                    throw new ClientConnectException("L'utente " + nick + " è già connesso!");
                } else {
                    userList.put(nick, user);
                    broadcastMessage(new Message("Connesso.",
                                                 nick,
                                                 Utils.getDate(),
                                                 Message.CONNECT_MESSAGE));
                }
                if (!ping.isAlive()) {
                    ping.start();
                }
            }
        } catch (InterruptedException ex) {
            System.out.println("Problema durante l'acquisizione delle risorse");
        } finally {
            addLock.unlock();
        }
    }

    /**
     * Metodo con il quale i client si disconnettono alla chat.
     * 
     * @param user Mobile server dell'utente che si connette in chat.
     * @throws RemoteException eccezione remota
     */
    @Override
    public synchronized void disconnect(MobileServer user) throws RemoteException {
        String nick = userList.getKey(user);
        if (nick == null) {
            return;
        }
        if (userList.containsKey(nick)) {
            userList.remove(nick);
            broadcastMessage(new Message("Disconnesso.", 
                                         nick, 
                                         Utils.getDate(),
                                         Message.DISCONNECT_MESSAGE));
            
        }
    }

    /**
     * Metodo utilizzato per spedire messaggi in broadcast nella chat pubblica.
     * 
     * @param user Mobile server dell'utente che invia il messaggio
     * @param msg messaggio da spedire
     * @throws RemoteException eccezione remota
     */
    @Override
    public void broadcastMessage(Message msg) throws RemoteException {
        try {

            boolean acquired = sendLock.tryLock(5, TimeUnit.SECONDS);

            if (acquired) {
                
                String sender = msg.getSender();
                for (Iterator<MobileServer> it = userList.values().iterator(); it.hasNext();) {
                    MobileServer receiver = it.next();
                    if (!sender.equals(receiver.getUsername())) {
                        new SendMessageThread(receiver, new Message(msg.getMessage(), 
	                                                            sender, 
	                                            	  	    Utils.getDate(), 
	                                            		    msg.getType())).start();
                    }
                }


            }
        } catch (InterruptedException ex) {
            System.out.println("Problema durante l'acquisizione delle risorse");
        } finally {
            sendLock.unlock();
        }
    }

    /**
     * Metodo che ritorna la lista degli utenti connessi in chat.
     * 
     * @return una LinkedList contente le stringhe degli utenti connessi in chat
     * @throws RemoteException eccezione remota
     */
    @Override
    public synchronized LinkedList getClientsList() throws RemoteException {
        return new LinkedList(userList.keySet());
    }

    /**
     * Metodo chiamato dall'amministratore per espellere un utente dalla chat.
     *
     * @param nick Nome dell'utente da kickare
     * @throws RemoteException eccezione remota
     * @throws ClientKickException eccezione sollevata se l'utente non esiste o
     * non &egrave connesso
     */
    @Override
    public synchronized void kickClient(String nick) throws RemoteException, 
                                                        ClientKickException {
        if (userList.containsKey(nick)) {
            MobileServer user = userList.get(nick);
            disconnect(user);
            new SendMessageThread(user,	new Message("Sei stato disconnesso!",
                                        	    toString(),
                                        	    Utils.getDate(),
                                		    Message.KICK_MESSAGE)).start();
        } else {
            throw new ClientKickException(nick + " non esiste o non è attualmente connesso.");
        }
    }

    /**
     * Metodo chiamato dall'amministratore per bannare un utente dalla chat.
     *
     * @param nick Nome dell'utente da bannare
     * @throws RemoteException eccezione remota
     * @throws ClientKickException eccezione sollevata se l'utente non esiste o
     * non &egrave connesso
     */
    @Override
    public synchronized void banClient(String nick) throws RemoteException,
                                                    ClientKickException {
        kickClient(nick);
        bannedList.add(nick);
    }

    
    /**
     * Metodo chiamato dal sistema di attivazione RMI quando non esitono pi&ugrave 
     * referenze attive al server.
     * Viene eseguita la sua de-esportazione dalla porta su cui &egrave in ascolto.
     *
     */
    @Override
    public void unreferenced() {
        try {
            System.out.println("Central Server: non ci sono piu client remoti, disattivo il server attivabile.");

            if (unexportObject(this, true)) {
                isActive = !inactive(getID());
                System.out.println("Il Server Centrale è attivo? " + isActive);
            }
           
            if(!isActive)
                System.out.println("############### Server Centrale Inattivo ###############\n");
        } catch (NoSuchObjectException ex) {
            System.out.println(NO_SUCH_OBJECT_EXCEPTION);
        } catch (RemoteException ex) {
            System.out.println(REMOTE_EXCEPTION);
        } catch (UnknownObjectException ex) {
            System.out.println(UN_KNOWN_OBJECT_EXCEPTION);
        } catch (ActivationException ex) {
            System.out.println(ACTIVATION_EXCEPTION);
        } finally {
       	    System.gc();
        }
    }

    private void initResource() {
        userList = new MyHashtable();
        bannedList = new Vector<String>();
        addLock = new ReentrantLock(true);
        sendLock = new ReentrantLock(true);
        ping = new ActiveReferences();
    }

    /**
     * Metodo chiamato da un client che vuole avviare una chat privata con un altro
     * client.
     *
     * @param builder Mobile server del client che chiede la chat privata
     * @param nick username dell'utente con cui si vuole chattare privatamente
     * @return Una stringa di conferma della richiesta inoltrata al client o un 
     * messaggio errore nel caso in cui il client non esista o non sia attualmente connesso
     * @throws RemoteException eccezione remota
     */
    public synchronized String privateChat(final MobileServer builder, String nick) throws RemoteException {
        if (userList.containsKey(nick)) {
            final MobileServer user = userList.get(nick);
            Thread confirm = new Thread() {

                @Override
                public void run() {
                    try {
                        if (user.acceptPrivateChat(builder)) {
                            builder.sendPrivateChatRef(user);
                        } else {
                            builder.sendPrivateChatRef(null);
                        }
                    } catch (RemoteException ex) {
                        System.out.println("Problema a contattare l'utente!:\n" + ex.getMessage());
                    }
                }
            };
            confirm.start();
            return "Richiesta inoltrata.\n";
        }
        return "Utente non connesso o inesistente!\n";
    }

    class ActiveReferences extends Thread {

        @Override
        public void run() {
            while (true) {
                if (userList.getCountRef() == 0) {
                    unreferenced();
                    stop();
                } else {
                    try {
                        sleep(2000);
                        for (Enumeration<MobileServer> enumRef = userList.elements(); enumRef.hasMoreElements();) {
                            MobileServer user = enumRef.nextElement();
                            try {
                                user.isConnect();
                            } catch (Exception ex) {
                                disconnect(user);
                            }
                        }
                    } catch (RemoteException ex) {
                        System.out.println("Non riesco ad espellere utente");
                    } catch (InterruptedException ex) {
                        System.out.println("Errore in ActiveReferences thread");
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Server";
    }
}
