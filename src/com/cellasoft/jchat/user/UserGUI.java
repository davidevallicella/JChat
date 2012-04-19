package com.cellasoft.jchat.user;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.text.html.HTMLEditorKit;

import com.cellasoft.jchat.exceptions.ClientBannedException;
import com.cellasoft.jchat.exceptions.ClientConnectException;
import com.cellasoft.jchat.server.ImplUserMobileServer;
import com.cellasoft.jchat.server.MobileServer;
import com.cellasoft.jchat.utils.ChatArea;
import com.cellasoft.jchat.utils.ChatGUIInterface;
import com.cellasoft.jchat.utils.CloseTabButton;
import com.cellasoft.jchat.utils.ColorPopupMenu;
import com.cellasoft.jchat.utils.EmoticonPopupMenu;
import com.cellasoft.jchat.utils.JListClientItem;
import com.cellasoft.jchat.utils.JListImageRenderer;
import com.cellasoft.jchat.utils.MenuToggleButton;
import com.cellasoft.jchat.utils.Message;
import com.cellasoft.jchat.utils.Utils;

/**
 * Implementazione dell'interfaccia grafica dell'user che implementa
 * ChatGUIInterface.
 *
 * @author Davide Vallicella
 * @author Nicola Ald&agrave;
 *
 * @version 1.0
 */
public class UserGUI extends javax.swing.JFrame implements ChatGUIInterface {

	private static final long serialVersionUID = 1736473838283651235L;
	private static final String DISCONNECT_EXCEPTION = "Errore durante la disconnessione!";
    private ImplUserMobileServer user;
    private DefaultListModel model;
    private ArrayList<String> users;
    private HTMLEditorKit htmlKit;
    private String username;
    private JFrame login;

    /**
     *
     * @param ms
     * @param parent
     * @throws RemoteException
     */
    public UserGUI(ImplUserMobileServer ms, JFrame parent) throws RemoteException {
        this.user = ms;
        this.login = parent;
        initUser();
        initComponents();
        inputTF.grabFocus();
    }

    private void initUser() throws RemoteException {
        user.setGUI(this);
        username = user.getUsername();
        model = new DefaultListModel();
        users = new ArrayList<String>();
        htmlKit = new HTMLEditorKit();
        Utils.setCSSRule(htmlKit);
        setTitle(username);
    }

    public void updateList(String name, boolean connect) throws RemoteException {
        if (name != null) {
            if (connect && !users.contains(name)) {
                model.addElement(new JListClientItem(name, name, "chat/resources/person.png"));
                users.add(name);
            } else {
                model.removeElementAt(users.indexOf(name));
                users.remove(name);
            }
            return;
        }
        LinkedList<String> usersList = user.getClientsList();
        if (users != null) {
            for (Iterator<String> it = usersList.iterator(); it.hasNext();) {
                String nick = it.next();
                if (!username.equals(nick) && !users.contains(nick)) {
                    model.addElement(new JListClientItem(nick, nick, "chat/resources/person.png"));
                    users.add(nick);
                }
            }
        }
        userList.setModel(model);
    }

    public void disconnect() {
        try {
            if (user.isConnect()) {
                user.disconnect();
            }
            this.statusJL.setText("<html>"
                    + "<head> </head>"
                    + "<body>"
                    + "<font face=\"Courier New, Courier, mono\">"
                    + "Offline"
                    + "</font>"
                    + "</body>"
                    + "</html>");
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, DISCONNECT_EXCEPTION
                    + "\n"
                    + ex.getMessage()
                    + "\n",
                    "Disconnect Exception",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean connect() {
        try {
            if (!user.isConnect()) {
                user.connect();

                this.ipJL.setText("<html> "
                        + "<head> </head> "
                        + "<body> "
                        + "<font face=\"Courier New, Courier, mono\">"
                        + Utils.getIP()
                        + "</font>"
                        + "</body>"
                        + "</html>");
                this.statusJL.setText("<html>"
                        + "<head> </head>"
                        + "<body>"
                        + "<font face=\"Courier New, Courier, mono\">"
                        + "Online"
                        + "</font>"
                        + "</body>"
                        + "</html>");
                updateList(null, true);
                return true;
            }
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this,
                    "Errore durante la connessione!\n" + ex.getMessage() + "\n",
                    "Remote Exception",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            login.setVisible(true);
        } catch (ClientConnectException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Connect Exception",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            login.setVisible(true);
        } catch (ClientBannedException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Banned Exception",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            login.setVisible(true);
        }
        return false;
    }

    private String inputUser(String prompt) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(new JLabel("Please enter user: "));
        p.add(Box.createHorizontalGlue());
        final JOptionPane op = new JOptionPane(p, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        op.setWantsInput(true);
        JDialog dlg = op.createDialog(this, prompt);
        dlg.show();
        dlg.dispose();
        Object value = op.getInputValue();
        if (value == JOptionPane.UNINITIALIZED_VALUE) {
            return null;
        }
        String str = (String) value;
        if (str != null && str.length() == 0) {
            str = null;
        }
        return str;
    }

    private void newMessageNotify(int i) {
        ((CloseTabButton) tabbedPane.getTabComponentAt(i)).colorTitle(Color.red);
    }

    /**
     *
     * @param client
     * @return
     * @throws RemoteException
     */
    public ChatArea getPrivateArea(String client) throws RemoteException {
        for (int i = 1; i < tabbedPane.getTabCount(); i++) {
            String title = tabbedPane.getTitleAt(i);
            if (title.equals(client)) {
                Component obj = tabbedPane.getComponentAt(i);
                if (obj instanceof JScrollPane) {
                    Component obj2 = ((JScrollPane) obj).getViewport().getView();
                    if (obj2 instanceof ChatArea) {
                        if (tabbedPane.getSelectedIndex() != i) {
                            newMessageNotify(i);
                        }
                        return (ChatArea) obj2;
                    }
                }
            }
        }
        Hashtable<String, MobileServer> privateUser = user.getPrivateList();
        if (privateUser.containsKey(client)) {
            return addPrivateArea(privateUser.get(client));
        }
        return null;
    }

    /**
     *
     * @param builder
     * @return
     * @throws RemoteException
     */
    public ChatArea addPrivateArea(MobileServer builder) throws RemoteException {
        String nick = builder.getUsername();
        ChatArea area = new ChatArea(htmlKit);
        area.setEditable(false);
        tabbedPane.add(nick, new JScrollPane(area));
        new CloseTabButton(tabbedPane, tabbedPane.getTabCount() - 1);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        inputTF.grabFocus();
        return area;
    }

    /**
     *
     * @param client
     * @throws RemoteException
     */
    public void removePrivateArea(String client) {
        for (int i = 1; i < tabbedPane.getTabCount(); i++) {
            String title = tabbedPane.getTitleAt(i);
            if (title.equals(client)) {
                tabbedPane.remove(i);
            }
        }
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    public ChatArea getPublicArea() {
        return ((ChatArea) chatArea);
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    public JFrame getFrame() {
        return this;
    }

    /**
     *
     * @throws RemoteException
     */
    public void close() {
        dispose();
    }

    private void privateChat(final String u) {
        Thread chatPrivate = new Thread() {

            @Override
            public void run() {
                String nick = u;
                if (nick == null) {
                    nick = inputUser("Private chat");
                    if (nick == null) {
                        stop();
                    }
                }
                try {

                    user.privateChat(nick);

                } catch (RemoteException ex) {
                    Logger.getLogger(UserGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        chatPrivate.start();

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
        splitPane = new javax.swing.JSplitPane();
        tabbedPane = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatArea = new ChatArea(htmlKit);
        jScrollPane2 = new javax.swing.JScrollPane();
        userList = new javax.swing.JList();
        jToolBar1 = new javax.swing.JToolBar();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        colorTB = new MenuToggleButton(new javax.swing.ImageIcon(System.getProperty("user.dir") + "/chat/resources/fore.gif"), true)
        ;
        emoticonTB = new MenuToggleButton(new javax.swing.ImageIcon(System.getProperty("user.dir") + "/chat/emoticon/smile.png"),false)
        ;
        inputTF = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        statusJL = new javax.swing.JLabel();
        ipJL = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileM = new javax.swing.JMenu();
        disconnectMI = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        exitMI = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        splitPane.setDividerLocation(390);
        splitPane.setDividerSize(5);

        tabbedPane.setMinimumSize(new java.awt.Dimension(380, 67));
        tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPaneStateChanged(evt);
            }
        });

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        chatArea.setBackground(new java.awt.Color(255, 255, 255));
        chatArea.setEditable(false);
        jScrollPane1.setViewportView(chatArea);

        tabbedPane.addTab("Principale", jScrollPane1);

        splitPane.setLeftComponent(tabbedPane);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(0, 130));

        userList.setFont(new java.awt.Font("Serif", 1, 12));
        userList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        userList.setCellRenderer(new JListImageRenderer());
        userList.setSelectionBackground(new java.awt.Color(0, 255, 150));
        userList.setSelectionForeground(new java.awt.Color(26, 26, 26));
        userList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(userList);

        splitPane.setRightComponent(jScrollPane2);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.add(filler1);

        ColorPopupMenu foregColorPop = new ColorPopupMenu(this,(MenuToggleButton)colorTB,true);
        ((MenuToggleButton)colorTB).setPopupMenu(foregColorPop);
        colorTB.setFocusable(false);
        colorTB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        colorTB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(colorTB);

        EmoticonPopupMenu emoticonPop = new EmoticonPopupMenu(inputTF);
        ((MenuToggleButton)emoticonTB).setPopupMenu(emoticonPop);
        emoticonTB.setFocusable(false);
        emoticonTB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        emoticonTB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(emoticonTB);

        inputTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputTFKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
            .addComponent(inputTF, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(splitPane, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(inputTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 128, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        statusJL.setText("<html>\n<head>\n</head>\n<body>\n<font face=\"Courier New, Courier, mono\">Online</font>\n</body>\n</html>");

        ipJL.setText("<html> <head> </head> <body> <font face=\"Courier New, Courier, mono\">---.---.---.---</font> </body> </html>");

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(286, 286, 286)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(84, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(statusJL, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ipJL)
                .addGap(321, 321, 321))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(statusJL)
                    .addComponent(ipJL, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 16, Short.MAX_VALUE))
                .addGap(68, 68, 68)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        fileM.setText("File");

        disconnectMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        disconnectMI.setText("Disconnect");
        disconnectMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnectMIActionPerformed(evt);
            }
        });
        fileM.add(disconnectMI);
        fileM.add(jSeparator1);

        exitMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        exitMI.setText("Exit");
        exitMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMIActionPerformed(evt);
            }
        });
        fileM.add(exitMI);

        jMenuBar1.add(fileM);

        jMenu1.setText("Options");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Private chat");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void inputTFKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputTFKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String msg = inputTF.getText();
            if (msg.equals("")) {
                return;
            }

            String data = Utils.getDate();

            inputTF.setText("");
            int index = tabbedPane.getSelectedIndex();
            String title = tabbedPane.getTitleAt(index);
            String color = Utils.colorToHex(((MenuToggleButton) colorTB).getColor());
            msg = "<p style=\"color:" + color + ";\">" + msg + "</p>";
            if (title.equals("Principale")) {
                try {
                    user.broadcastMessage(new Message(msg, username, data, Message.PUBLIC_MASSAGE));
                    ((ChatArea) chatArea).append(new Message(msg, username, data, Message.PUBLIC_MASSAGE));
                } catch (RemoteException ex) {
                    ((ChatArea) chatArea).append(new Message(msg, username, data, Message.WARNING_MESSAGE));
                }
            } else {

                String receiver = "";
                Hashtable<String, MobileServer> list = user.getPrivateList();
                Enumeration<String> it = list.keys();
                while (!receiver.equals(title) && it.hasMoreElements()) {
                    receiver = it.nextElement();
                }
                if (receiver.equals(title)) {
                    try {
                        list.get(receiver).sendMessage(new Message(msg, username, data, Message.PRIVATE_MASSAGE));
                        getPrivateArea(receiver).append(new Message(msg, username, data, Message.PRIVATE_MASSAGE));
                    } catch (RemoteException ex) {
                        ((ChatArea) chatArea).append(new Message(msg, username, data, Message.WARNING_MESSAGE));
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Non esiste la referenza per " + title, "Private chat", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_inputTFKeyPressed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        disconnect();
        login.setVisible(true);
    }//GEN-LAST:event_formWindowClosed

    private void disconnectMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnectMIActionPerformed
        dispose();
}//GEN-LAST:event_disconnectMIActionPerformed

    private void exitMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMIActionPerformed
        disconnect();
        System.exit(0);
}//GEN-LAST:event_exitMIActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        privateChat(null);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void tabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPaneStateChanged
        int i = tabbedPane.getSelectedIndex();
        if (i != 0) {
            ((CloseTabButton) tabbedPane.getTabComponentAt(i)).colorTitle(Color.BLACK);
        }
    }//GEN-LAST:event_tabbedPaneStateChanged

private void userListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userListMouseClicked

    if (evt.getClickCount() == 2) {
        int index = userList.locationToIndex(evt.getPoint());
        ListModel dlm = userList.getModel();
        Object objItem = dlm.getElementAt(index);
        if (objItem instanceof JListClientItem) {
            privateChat(((JListClientItem) objItem).getName());
        }
    }

}//GEN-LAST:event_userListMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane chatArea;
    private javax.swing.JToggleButton colorTB;
    private javax.swing.JMenuItem disconnectMI;
    private javax.swing.JToggleButton emoticonTB;
    private javax.swing.JMenuItem exitMI;
    private javax.swing.JMenu fileM;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JTextField inputTF;
    private javax.swing.JLabel ipJL;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JLabel statusJL;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JList userList;
    // End of variables declaration//GEN-END:variables
}
