package com.cellasoft.jchat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.MarshalledObject;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.ActivationGroupID;
import java.rmi.activation.UnknownGroupException;
import java.util.Properties;

import com.cellasoft.jchat.server.LoginProxyInterface;
import com.cellasoft.jchat.server.ServerInterface;
import com.cellasoft.jchat.utils.Utils;

/**
 * Classe che esegue il setup del sistema.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave
 *
 * @version 1.0
 */
public class Setup {

    public static ActivationGroupID SERVER_ACTIVATION_GROUP_ID;
    public static ActivationGroupID AUTH_ACTIVATION_GROUP_ID;
    private static Properties serverProp;
    private static Properties authProp;
    private static String server_centrale;
    private static String server_auth;
    private static String codebase;
    private static String implCodebase;
    private static LoginProxyInterface stub_auth;
    private static int port = 1098;

    public static void main(String args[]) {

        String local_dir = System.getProperty("user.dir");
        String home_dir = System.getProperty("user.home");

        String policySetup = "file://" + local_dir + "/chat/setup.policy";
        String policyGroup = "file://" + local_dir + "/chat/group.policy";
        String truststorePass = "123456";

        codebase = "http://" + Utils.getIP() + ":8000/common/";
        implCodebase = "file://" + home_dir + "/impl_server/";

        server_centrale = "chat.CentralServer";
        server_auth = "chat.AuthenticationServer";

        if (System.getSecurityManager() == null) {
            System.setProperty("java.rmi.server.codebase", codebase);
            System.setProperty("java.security.policy", policySetup);
            System.setSecurityManager(new RMISecurityManager());
        }

        serverProp = new Properties();
        serverProp.put("javax.net.ssl.trustStore", "truststore");
        serverProp.put("javax.net.ssl.trustStorePassword", truststorePass);
        serverProp.put("java.security.policy", policyGroup);
        serverProp.put("chat.impl.codebase", implCodebase);
        serverProp.put("java.class.path", "no_classpath");

        authProp = new Properties();
        authProp.put("java.security.policy", policyGroup);
        authProp.put("chat.impl.codebase", implCodebase);
        authProp.put("java.class.path", "no_classpath");
        authProp.put("java.rmi.dgc.leaseValue", "10000");

        ActivationGroupDesc dsServerGroup = new ActivationGroupDesc(serverProp, null);
        ActivationGroupDesc dsAuthGroup = new ActivationGroupDesc(authProp, null);

        try {
            SERVER_ACTIVATION_GROUP_ID = ActivationGroup.getSystem().registerGroup(dsServerGroup);
            System.out.println("Il gruppo del Server Centrale e' stato creato,  registrato col sistema d'attivazione, ed ha identificativo = " + SERVER_ACTIVATION_GROUP_ID.hashCode());
            AUTH_ACTIVATION_GROUP_ID = ActivationGroup.getSystem().registerGroup(dsAuthGroup);
            System.out.println("Il gruppo del Server di Autenticazione e' stato creato,  registrato col sistema d'attivazione, ed ha identificativo = " + AUTH_ACTIVATION_GROUP_ID.hashCode());

            ActivationDesc dsServer = new ActivationDesc(SERVER_ACTIVATION_GROUP_ID, server_centrale, implCodebase, null);
            ServerInterface stub_server = (ServerInterface) Activatable.register(dsServer);
            System.out.println("E' stato creato l'activation descriptor del Server Centrale che e' stato registrato col demone d'attivazione");



            ActivationDesc dsAuth = new ActivationDesc(AUTH_ACTIVATION_GROUP_ID, server_auth, implCodebase, new MarshalledObject(stub_server));
            stub_auth = (LoginProxyInterface) Activatable.register(dsAuth);
            System.out.println("E' stato creato l'activation descriptor del Server di Autenticazione che e' stato registrato col demone d'attivazione");
            System.out.println("Il server attivabile adesso puo' essere acceduto attraverso lo stub: " + stub_auth);

            System.out.println("Faccio il rebinding dello stub del Server di "
                    + "Autenticazione nel registro RMI alla porta 1098 dove gia'"
                    + " si trova registrato il sistema di attivazione ");
            Naming.rebind("//" + Utils.getIP() + ":" + port + "/AuthServer", stub_auth);
            System.out.println("Rebinding effettuata con successo!\nFine setup.");

        } catch (UnknownGroupException ex) {
            System.out.println("Gruppo di attivazione sconosciuto o invalido!");
        } catch (ActivationException ex) {
            System.out.println("Errore nell'attivazione!");
        } catch (MalformedURLException ex) {
            System.out.println("URL della rebind errato o non valido!");
        } catch (RemoteException ex) {
            System.out.println("Errore remoto nella rebind!");
        } catch (IOException ex) {
            System.out.println("Errore nella Marshalled durate l'activation Descriptor del Server di Autenticazione!");
        }

    }
}
