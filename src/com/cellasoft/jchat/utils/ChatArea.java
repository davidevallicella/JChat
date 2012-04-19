package com.cellasoft.jchat.utils;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author davide
 */
public class ChatArea extends JTextPane {

	private static final long serialVersionUID = -1275938951804400884L;
	private HTMLEditorKit htmlKit;
    private HashMap<String, String> smileMap;

    public ChatArea(HTMLEditorKit kit) {
        this.htmlKit = kit;
        initPane();
    }

    private void initPane() {
        setContentType("text/html");
        setEditorKit(htmlKit);
        setDocument(htmlKit.createDefaultDocument());
        smileMap = Utils.getEmoticonsMap();
        setOpaque(false);
        setBorder(null);
    }

    public void append(Message msg) {
        String str = emoticonParser(msg);
        int length = getDocument().getLength();
        Element e = ((HTMLDocument) getDocument()).getParagraphElement(length);
        try {
            ((HTMLDocument) getDocument()).insertAfterEnd(e, str);
            setCaretPosition(getDocument().getLength());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Errore scrittura messaggio.",
                    "ChatArea.append()",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void setCaretPosition(int pos) {
        boolean ok = true;
        while (ok) {
            try {
                super.setCaretPosition(pos);
                ok = false;
            } catch (Exception ex) {
                pos -= 1;
            }
        }
    }

    private String emoticonParser(Message msg) {

        String str = msg.getMessage();
        StringBuffer bfr = new StringBuffer(str);

        for (Iterator<String> it = smileMap.keySet().iterator(); it.hasNext();) {
            String code = it.next();
            int index = str.indexOf(code);
            while (index != -1) {
                if (index > 0) {
                    if (str.charAt(index - 1) == '>' && (code.equals(":(") || code.equals(":-("))) {
                        index -= 1;
                        code = ">" + code;

                    } else if (str.charAt(index - 1) == '3' && (code.equals(":)") || code.equals(":-)"))) {
                        index -= 1;
                        code = "3" + code;

                    } else if (str.charAt(index - 1) == 'O' && (code.equals(":)") || code.equals(":-)"))) {
                        index -= 1;
                        code = "O" + code;

                    }
                }
                String replace = "<img src=\"" + smileMap.get(code) + "\">";
                bfr.replace(index, index + code.length(), replace);
                str = bfr.toString();
                index = str.indexOf(code);
            }
        }
        String table;


        if (msg.getType() == Message.CONNECT_MESSAGE) {
            table = "<div id=\"connect\"><table width=95% cellpadding=2>"
                    + "<tr>"
                    + "<td>"
                    + "<div id=\"nick\"><b>" + msg.getSender() + "</b> "
                    + msg.getMessage()
                    + "</div>"
                    + "</td>"
                    + "<td align=right><span>" + msg.getTime() + "</span></td>"
                    + "</tr>"
                    + "</table></div>";

        } else if (msg.getType() == Message.DISCONNECT_MESSAGE) {
            table = "<div id=\"disconnect\"><table width=95% cellpadding=2>"
                    + "<tr>"
                    + "<td>"
                    + "<div id=\"nick\"><b>" + msg.getSender() + "</b> "
                    + msg.getMessage()
                    + "</div>"
                    + "</td>"
                    + "<td align=right><span>" + msg.getTime() + "</span></td>"
                    + "</tr>"
                    + "</table></div>";
        } else if (msg.getType() == Message.WARNING_MESSAGE) {
            table = "<div id=\"warning\"><table width=95% cellpadding=2>"
                    + "<tr>"
                    + "<td>"
                    + "<div id=\"nick\"><b>" + msg.getSender() + "</b> "
                    + msg.getMessage()
                    + "</div>"
                    + "</td>"
                    + "<td align=right><span>" + msg.getTime() + "</span></td>"
                    + "</tr>"
                    + "</table></div>";
        } else {
            table = "<div id=\"success\"><table width=95% cellpadding=2>"
                    + "<tr>"
                    + "<td><div id=\"nick\"><b>" + msg.getSender() + "</b>:</div>"
                    + "</td>"
                    + "<td align=right><span>" + msg.getTime() + "</span></td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td>" + str + "</td>"
                    + "</tr>"
                    + "</table></div>";
        }

        return table;
    }
}
