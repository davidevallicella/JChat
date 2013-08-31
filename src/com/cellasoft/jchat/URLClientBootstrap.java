package com.cellasoft.jchat;

import java.net.MalformedURLException;
import java.rmi.RMISecurityManager;
import java.rmi.server.RMIClassLoader;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * Classe che esegue il bootstrap del client minimale.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave
 *
 * @version 1.0
 */
public class URLClientBootstrap {

    private static String codebase;  //Codebase da cui caricare la classe Client
    private static final String clientClass = "chat.LoginGUI";    
    private static final String truststorePass = "123456";

    /**
     * main del Bootstrap.
     *
     * @param args il vettore degli argomenti passati all'avvio
     */
    public static void main(String[] args) {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        String local_dir = System.getProperty("user.dir");
        String policy = "file://" + local_dir + "/chat/policy";
        
        if (System.getSecurityManager() == null) {
            System.setProperty("java.security.policy", policy);
            System.setProperty("javax.net.ssl.trustStore", "truststore");
            System.setProperty("javax.net.ssl.trustStorePassword", truststorePass);

            System.setSecurityManager(new RMISecurityManager());
        }       

        if (!args[0].isEmpty()) {
            codebase = "http://" + args[0];
        }
        if (!args[1].isEmpty()) {
            codebase += ":" + args[1] + "/common/";
        }
        
        try {

            Class log = RMIClassLoader.loadClass(codebase, clientClass);
            JFrame login = (JFrame) log.newInstance();
            login.show();

        } catch (MalformedURLException ex) {
            System.out.println(codebase + " non è un URL valido.");
        } catch (ClassNotFoundException ex) {
            System.out.println("Classe non trovata: " + ex.getMessage());
        }
    }
}
