package com.cellasoft.jchat.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.MarshalledObject;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationID;
import java.rmi.activation.UnknownObjectException;
import java.rmi.server.Unreferenced;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.security.auth.login.FailedLoginException;

import com.cellasoft.jchat.utils.Account;
import com.cellasoft.jchat.utils.Utils;

/**
 * Server di autenticazione attivabile con i quali i client ottengono, in caso di successo,
 * il rispettivo mobile server;
 * estende Activatable e implementa l'interfaccia remota {@link LoginProxyInterface} e Unreferenced
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave;
 *
 * @version 1.0
 * 
 */
public class AuthenticationServer extends Activatable implements
        LoginProxyInterface, Unreferenced {
  
	private static final long serialVersionUID = 4446289673764727221L;
	private static final String FAILED_LOGIN_EXCEPTION_NOT_READY = 
            "Il server di autenticazione non è ancora attivo.."
            + "attendere ancora qualche secondo e riprovare.";
    private static final String FAILED_LOGIN_EXCEPTION =
            "Nome utente e/o password errati!!!";
    private static final String NULL_POINTER_EXCEPTION =
            "Referenza al server centrale nulla!!!";
    private static final String NO_SUCH_OBJECT_EXCEPTION =
            "Authentication Server [unreferenced]:"
            + " Il server non è stato esportato correttamente.";
    private static final String REMOTE_EXCEPTION =
            "Authentication Server [unreferenced]: Errore remoto.";
    private static final String NOT_BOUND_EXCEPTION = 
            "Authentication Server [unreferenced]: "
            + "La referenza corrispondente a AuthServer non è presente nel registro rmi.";
    private static final String MALFORMED_URL_EXCEPTION = 
            "Authentication Server [unreferenced]:"
            + " L'URL specificato nella unbind non è corretto.";
    private static final String UN_KNOWN_OBJECT_EXCEPTION = 
            "Authentication Server [unreferenced]: Il server è già inattivo.";
    private static final String ACTIVATION_EXCEPTION = 
            "Authentication Server [unreferenced]:"
            + " Il gruppo di attivazione non è attivo.";

    private static final int ADMIN_USER = 0;
    private static final int NORMAL_USER = 1;
    private static final int LOGIN_FAILED = -1;
    
    private static int port = 2345;
    private ServerInterface server_stub;    
    private boolean isActive = false;
    private Lock lock = new ReentrantLock(true); // con true gestisco l'ordine delle code
    private Remote ms;

    /**
     * Instanzia un nuovo AuthenticationServer e lo mette in ascolto sulla porta
     * 2345.
     * Questo costruttore è chiamato solo dal sistema di attivazione RMI, in quanto
     * questo è un server attivabile.
     * 
     * @param id Identificativo del gruppo di attivazione
     * @param obj Oggetto Marshallizzato che contiene lo stub del server centrale
     * @throws RemoteException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public AuthenticationServer(ActivationID id, MarshalledObject obj) throws 
                                                         RemoteException, 
                                                         IOException,
                                                         ClassNotFoundException {
        super(id, port);
        Object objRef = obj.get();
        if (objRef instanceof ServerInterface) {
            server_stub = (ServerInterface) objRef;
            isActive = true;
        }
    }

    /**
     *
     * Effettua il login di un client tramite un'autenticazione con un 
     * oggetto {@link Account}.
     * Se username e password contenuti nell'account sono corretti viene 
     * restituito il {@link MobileServer} corrispondente.
     * Se la login non va a buon fine viene sollevato un'eccezione del tipo 
     * {@link FailedLoginException}
     * 
     * @param account Contiene username e password
     * @return Un MarshalledObject che contiene un {@link MobileServer}
     * @throws RemoteException
     * @throws FailedLoginException
     * @throws IOException
     */
    @Override
    public MarshalledObject login(Account account) throws RemoteException, 
                                                    IOException, 
                                                    FailedLoginException {
        try {

            boolean acquired = lock.tryLock(5, TimeUnit.SECONDS);

            if (acquired) {

                if (!isActive) {
                    throw new FailedLoginException(FAILED_LOGIN_EXCEPTION_NOT_READY);
                }

                if (server_stub == null) {
                    throw new NullPointerException(NULL_POINTER_EXCEPTION);
                }

                int type = ceckAccount(account);

                switch (type) {
                    case ADMIN_USER:
                        ms = new ImplAdminMobileServer((AdminProxyInterface) server_stub, account);
                        unexportObject(ms, true);
                        break;
                    case NORMAL_USER:
                        ms = new ImplUserMobileServer((UserProxyInterface) server_stub, account);
                        unexportObject(ms, true);
                        break;
                    case LOGIN_FAILED:
                        throw new FailedLoginException(FAILED_LOGIN_EXCEPTION);
                }
            }
        } catch (InterruptedException ex) {
            System.out.println("Problema durante l'acquisizione delle risorse");
        } finally {
            lock.unlock();
        }
        return new MarshalledObject(ms);
    }

    /**
     * Questo metodo viene chiamato dal sistema di attivazione RMI quando non esitono pi&ugrave 
     * referenze attive al server di autenticazione.
     * Viene eseguita la <code>unbind</code> dal registro rmi del server di autenticazione
     * e la sua de-esportazione dalla porta su cui &egrave in ascolto.
     */
    @Override
    public void unreferenced() {

        System.out.println("Authentication Server: start unreferenced.");
        try {
            Naming.unbind("//" + Utils.getIP() + ":1098/AuthServer");

            if (unexportObject(this, true)) {
                isActive = !inactive(getID());
                System.out.println("Il Server di Autenticazione è attivo? " + isActive);
            }

            System.gc();
            System.out.println("Authentication Server: chiusura completata.");

        } catch (NoSuchObjectException ex) {
            System.out.println(NO_SUCH_OBJECT_EXCEPTION);
        } catch (RemoteException ex) {
            System.out.println(REMOTE_EXCEPTION);
        } catch (NotBoundException ex) {
            System.out.println(NOT_BOUND_EXCEPTION);
        } catch (MalformedURLException ex) {
            System.out.println(MALFORMED_URL_EXCEPTION);
        } catch (UnknownObjectException ex) {
            System.out.println(UN_KNOWN_OBJECT_EXCEPTION);
        } catch (ActivationException ex) {
            System.out.println(ACTIVATION_EXCEPTION);
        }
    }

    private int ceckAccount(Account account) {
        String user = account.getUsername();
        String pass = account.getPassword();
        if (user.equals("admin") && pass.equals("admin")) {
            return ADMIN_USER;
        }
        if (user.equals("nicola") && pass.equals("nicola")) {
            return ADMIN_USER;
        }
        if (user.equals("davide") && pass.equals("davide")) {
            return NORMAL_USER;
        }
        if (user.equals("guest") && pass.equals("guest")) {
            return NORMAL_USER;
        }
        return LOGIN_FAILED;
    }
}
