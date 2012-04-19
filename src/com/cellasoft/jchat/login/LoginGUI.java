package com.cellasoft.jchat.login;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.MarshalledObject;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.AccessControlException;
import java.util.regex.Pattern;

import javax.security.auth.login.FailedLoginException;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import com.cellasoft.jchat.URLClientBootstrap;
import com.cellasoft.jchat.admin.AdminGUI;
import com.cellasoft.jchat.server.ImplAdminMobileServer;
import com.cellasoft.jchat.server.ImplUserMobileServer;
import com.cellasoft.jchat.server.LoginProxyInterface;
import com.cellasoft.jchat.user.UserGUI;
import com.cellasoft.jchat.utils.Account;
import com.cellasoft.jchat.utils.RegexFormatter;

/**
 * Classe caricata dal {@link URLClientBootstrap} per effettuare il login al server di autenticazione.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave
 *
 * @version 1.0
 */
public class LoginGUI extends javax.swing.JFrame {

	private static final long serialVersionUID = 8939757128562592829L;
	private static final String LOGIN_CAST_EXCEPTION =
            "L'interfaccia dello stub del server di autenticazione sconosciuta!";
    private static final String CLASS_CAST_EXCEPTION =
            "L'interfaccia del mobile server sconosciuta!";
    private static final String NOT_BOUND_EXCEPTION =
            "Referenza al server di autenticazione sul registro RMI non trovata!";
    private static final String MALFORMED_URL_EXCEPTION =
            "Indirizzo della lookup errato!";
    private static final String REMOTE_EXCEPTION =
            "Errore remoto.";
    private static final String FAILED_LOGIN_EXCEPTION =
            "Nome utente e/o password errati!!!";
    private static final String IO_EXCEPTION =
            "Errore nella Marshalled.";
    private static final String CLASS_NOT_FOUND_EXCEPTION =
            "Classe non trovata.";
    private static final String ACCESS_CONTROL_EXCEPTION =
            "Permesso negato!\nIndirizzo del server errato.";
    private Remote auth_stub;
    private String username;
    private String password;
    private String host;
    private String port;
    private Border border;
    private Thread log;

    /** 
     * Crea l'interfaccia grafica di login.
     *
     */
    public LoginGUI() {
        initComponents();
        border = userTF.getBorder();
        progresB.setVisible(false);
        progresB.setMaximum(100);
    }

    /**
     * Acquisisce i dati inseriti nel form grafico e fa la login sul server di autenticazione.
     *
     */
    public void login() {
        try {
            enableGUI(false);

            username = userTF.getText();
            if (username.equals("") || username == null) {
                userTF.setBorder(new LineBorder(new Color(254, 132, 132)));
                enableGUI(true);
                return;
            }
            statusMsg.setText("Aquisizione username...");
            progresB.setValue(5);
            password = String.valueOf(passTF.getPassword());
            if (password.equals("") || password == null) {
                passTF.setBorder(new LineBorder(new Color(254, 132, 132)));
                enableGUI(true);
                return;
            }
            statusMsg.setText("Aquisizione password...");
            progresB.setValue(10);
            host = hostTF.getText();
            if (host.equals("") || host == null) {
                hostTF.setBorder(new LineBorder(new Color(254, 132, 132)));
                enableGUI(true);
                return;
            }
            statusMsg.setText("Aquisizione server address...");
            progresB.setValue(15);
            port = portTF.getText();
            if (port.equals("") || port == null) {
                portTF.setBorder(new LineBorder(new Color(254, 132, 132)));
                enableGUI(true);
                return;
            }
            statusMsg.setText("Aquisizione port RMID...");
            progresB.setValue(20);

            statusMsg.setText("Lookup: //" + host + ":" + port + " ...");
            auth_stub = Naming.lookup("//" + host + ":" + port + "/AuthServer");

            statusMsg.setText("Lookup successful...");

            progresB.setValue(50);

            if (auth_stub instanceof LoginProxyInterface) {
                statusMsg.setText("Login...");
                progresB.setValue(55);
                MarshalledObject dataMO =
                        ((LoginProxyInterface) auth_stub).login(
                        new Account(username, password)); //mi ritorna un MS
                statusMsg.setText("Login successful...");
                progresB.setValue(70);
                Object msObj = dataMO.get();
                progresB.setValue(75);

                if (msObj instanceof ImplAdminMobileServer) {
                    statusMsg.setText("Init AdminGUI...");
                    progresB.setValue(80);
                    AdminGUI admin = new AdminGUI((ImplAdminMobileServer) msObj, this);
                    statusMsg.setText("Connect to JChat server...");
                    progresB.setValue(90);
                    if (admin.connect()) {
                        statusMsg.setText("Connect successful!");
                        progresB.setValue(100);
                        admin.show();
                        setVisible(false);
                    }
                } else if (msObj instanceof ImplUserMobileServer) {
                    statusMsg.setText("Init UserGUI...");
                    progresB.setValue(80);
                    UserGUI user = new UserGUI((ImplUserMobileServer) msObj, this);
                    statusMsg.setText("Connect to JChat server...");
                    progresB.setValue(90);
                    if (user.connect()) {
                        statusMsg.setText("Connect successful!");
                        progresB.setValue(100);
                        user.show();
                        setVisible(false);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, CLASS_CAST_EXCEPTION,
                            "Class Cast Exception",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, LOGIN_CAST_EXCEPTION,
                        "Class Cast Exception",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NotBoundException ex) {
            JOptionPane.showMessageDialog(this, NOT_BOUND_EXCEPTION
                    + "\n"
                    + ex.getMessage()
                    + "\n",
                    "Not Bound Exception",
                    JOptionPane.ERROR_MESSAGE);
        } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(this, MALFORMED_URL_EXCEPTION
                    + "\n"
                    + ex.getMessage()
                    + "\n",
                    "Malformed URL Exception",
                    JOptionPane.ERROR_MESSAGE);
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, REMOTE_EXCEPTION
                    + "\n"
                    + ex.getMessage()
                    + "\n",
                    "Remote Exception",
                    JOptionPane.ERROR_MESSAGE);
        } catch (FailedLoginException ex) {
            JOptionPane.showMessageDialog(this, FAILED_LOGIN_EXCEPTION,
                    "Failed Login Exception",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, IO_EXCEPTION
                    + "\n"
                    + ex.getMessage()
                    + "\n",
                    "IO Exception",
                    JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, CLASS_NOT_FOUND_EXCEPTION
                    + "\n"
                    + ex.getMessage()
                    + "\n",
                    "Class Not Found Exception",
                    JOptionPane.ERROR_MESSAGE);
        } catch (AccessControlException ex) {

            JOptionPane.showMessageDialog(this, ACCESS_CONTROL_EXCEPTION
                    + "\n"
                    + ex.getMessage()
                    + "\n",
                    "Access Control Exception",
                    JOptionPane.ERROR_MESSAGE);
        }
        enableGUI(true);
    }

    
    public void enableGUI(boolean enable) {
        progresB.setValue(0);
        progresB.setVisible(!enable);
        statusMsg.setText("");
        userTF.setEnabled(enable);
        passTF.setEnabled(enable);
        hostTF.setEnabled(enable);
        portTF.setEnabled(enable);
        okJB.setEnabled(enable);
        cancJB.setEnabled(enable);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        userTF = new javax.swing.JTextField();
        passTF = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        statusMsg = new javax.swing.JLabel();
        progresB = new javax.swing.JProgressBar();
        portTF = new javax.swing.JTextField();
        cancJB = new javax.swing.JButton();
        okJB = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        String _255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        Pattern p = Pattern.compile("^(?:" + _255 + "\\.){3}" + _255 + "$");
        RegexFormatter ipFormatter = new RegexFormatter(p);
        hostTF = new javax.swing.JFormattedTextField(ipFormatter);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JChat");
        setResizable(false);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon("chat/resources/logo_univr.gif"));

        jLabel6.setFont(new java.awt.Font("Verdana", 1, 13));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Università degli studi di Verona");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("<html>\n<head></head>\n<body>\n<font color=\"red\" size=\"8\">JChat</font>\n</body>\n</html>");

        jLabel8.setFont(new java.awt.Font("Verdana", 1, 13));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Facoltà di scienze MM.FF.NN.");

        jLabel9.setFont(new java.awt.Font("DejaVu Sans", 0, 11));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Davide Vallicella - id072693");

        jLabel10.setFont(new java.awt.Font("DejaVu Sans", 0, 11));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Nicola Aldà - id073135");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)))
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addGap(0, 0, 0)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(jLabel9)
                .addGap(0, 0, 0)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel2.setMinimumSize(new java.awt.Dimension(268, 221));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Username: ");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Passoword: ");

        userTF.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                TFMousePressed(evt);
            }
        });
        userTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                portTFKeyPressed(evt);
            }
        });

        passTF.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                TFMousePressed(evt);
            }
        });
        passTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                portTFKeyPressed(evt);
            }
        });

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("IP: ");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("port: ");

        jPanel3.setMinimumSize(new java.awt.Dimension(244, 26));
        jPanel3.setPreferredSize(new java.awt.Dimension(244, 26));

        statusMsg.setFont(new java.awt.Font("SansSerif", 1, 11));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(progresB, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(statusMsg, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(progresB, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(statusMsg, javax.swing.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        portTF.setText("1098");
        portTF.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                TFMousePressed(evt);
            }
        });
        portTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                portTFKeyPressed(evt);
            }
        });

        cancJB.setText("Cancel");
        cancJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancJBActionPerformed(evt);
            }
        });

        okJB.setText("Login");
        okJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okJBActionPerformed(evt);
            }
        });

        hostTF.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                TFMousePressed(evt);
            }
        });
        hostTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                portTFKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(69, 69, 69)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(54, 54, 54)
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(passTF, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                            .addComponent(userTF, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                            .addComponent(hostTF, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                            .addComponent(portTF, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap(120, Short.MAX_VALUE)
                        .addComponent(okJB, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancJB, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(userTF, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(passTF, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hostTF, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(portTF, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
                .addGap(11, 11, 11)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancJB)
                    .addComponent(okJB))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okJBActionPerformed
        log = new Thread(new Runnable() {

            public void run() {
                login();
            }
        });

        log.start();
    }//GEN-LAST:event_okJBActionPerformed

    private void cancJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancJBActionPerformed
        System.exit(0);
    }//GEN-LAST:event_cancJBActionPerformed

    private void portTFKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_portTFKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            log = new Thread(new Runnable() {

                public void run() {
                    login();
                }
            });

            log.start();
        }
    }//GEN-LAST:event_portTFKeyPressed

private void TFMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TFMousePressed
    Component comp = evt.getComponent();
    if (comp instanceof JTextField) {
        ((JTextField) comp).setBorder(border);
    }
}//GEN-LAST:event_TFMousePressed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancJB;
    private javax.swing.JFormattedTextField hostTF;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton okJB;
    private javax.swing.JPasswordField passTF;
    private javax.swing.JTextField portTF;
    private javax.swing.JProgressBar progresB;
    private javax.swing.JLabel statusMsg;
    private javax.swing.JTextField userTF;
    // End of variables declaration//GEN-END:variables
}
