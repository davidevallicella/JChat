/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cellasoft.jchat.utils;

import java.awt.Color;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TimeZone;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author davide
 */
public class Utils {

    public static String colorToHex(Color color) {
        String colorstr = "#";
        // Red
        String str = Integer.toHexString(color.getRed());
        if (str.length() > 2) {
            str = str.substring(0, 2);
        } else if (str.length() < 2) {
            colorstr += "0" + str;
        } else {
            colorstr += str;
        }
        // Green
        str = Integer.toHexString(color.getGreen());
        if (str.length() > 2) {
            str = str.substring(0, 2);
        } else if (str.length() < 2) {
            colorstr += "0" + str;
        } else {
            colorstr += str;
        }
        // Blue
        str = Integer.toHexString(color.getBlue());
        if (str.length() > 2) {
            str = str.substring(0, 2);
        } else if (str.length() < 2) {
            colorstr += "0" + str;
        } else {
            colorstr += str;
        }
        return colorstr;
    }
    
    public static String getLocalPath(){
        return System.getProperty("user.dir");
    }
    
    public static String getIP() {
        String IP = null;
        try {

            for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                NetworkInterface iface = ifaces.nextElement();
                if (!iface.getDisplayName().equals("lo")) {
                    for (Enumeration<InetAddress> addresses = iface.getInetAddresses(); addresses.hasMoreElements();) {
                        InetAddress address = addresses.nextElement();
                        if (address instanceof Inet4Address) {
                            IP = address.getHostAddress();
                        }
                    }
                }
            }

        } catch (SocketException ex) {
            System.err.println("Utils.getIP(): Errore rilevameto ip.");
        }
        return IP;
    }

    public static HashMap<String, String> getEmoticonsMap() {
        String dir = "file://" + System.getProperty("user.dir") + "/chat/emoticon/";
        HashMap<String, String> smileMap = new HashMap<String, String>();

        smileMap.put(":)", dir + "smile.png");
        smileMap.put(":-)", dir + "smile.png");
        smileMap.put(":]", dir + "smile.png");
        smileMap.put("=)", dir + "smile.png");
        smileMap.put(":(", dir + "frown.png");
        smileMap.put(":-(", dir + "frown.png");
        smileMap.put(":[", dir + "frown.png");
        smileMap.put("=(", dir + "frown.png");
        smileMap.put(":-P", dir + "tongue.png");
        smileMap.put(":P", dir + "tongue.png");
        smileMap.put(":-p", dir + "tongue.png");
        smileMap.put(":p", dir + "tongue.png");
        smileMap.put("=P", dir + "tongue.png");
        smileMap.put("=p", dir + "tongue.png");
        smileMap.put(":-D", dir + "grin.png");
        smileMap.put(":D", dir + "grin.png");
        smileMap.put("=D", dir + "grin.png");
        smileMap.put(":-O", dir + "gasp.png");
        smileMap.put(":O", dir + "gasp.png");
        smileMap.put(":-o", dir + "gasp.png");
        smileMap.put(":o", dir + "gasp.png");
        smileMap.put(";-)", dir + "wink.png");
        smileMap.put(";)", dir + "wink.png");
        smileMap.put("8-)", dir + "glasses.png");
        smileMap.put("8)", dir + "glasses.png");
        smileMap.put("B-)", dir + "glasses.png");
        smileMap.put("B)", dir + "glasses.png");
        smileMap.put("8-|", dir + "sunglasses.png");
        smileMap.put("8|", dir + "sunglasses.png");
        smileMap.put("B-|", dir + "sunglasses.png");
        smileMap.put("B|", dir + "sunglasses.png");
        smileMap.put(">:(", dir + "grumpy.png");
        smileMap.put(">:-(", dir + "grumpy.png");
        smileMap.put(":\\", dir + "unsure.png");
        smileMap.put(":-/", dir + "unsure.png");
        smileMap.put(":'(", dir + "cry.png");
        smileMap.put("O:)", dir + "angel.png");
        smileMap.put("O:-)", dir + "angel.png");
        smileMap.put("3:)", dir + "devil.png");
        smileMap.put("3:-)", dir + "devil.png");
        smileMap.put(";*", dir + "kiss.png");
        smileMap.put(";-*", dir + "kiss.png");
        smileMap.put("<3", dir + "heart.png");
        smileMap.put("^_^", dir + "kiki.png");
        smileMap.put("o.0", dir + "confused.png");
        smileMap.put("0.o", dir + "confused.png");
        smileMap.put(":3", dir + "curlylips.png");
        smileMap.put(":v", dir + "pacman.png");
        smileMap.put("-_-", dir + "squint.png");

        return smileMap;
    }

    public static void setCSSRule(HTMLEditorKit htmlKit) {
        StyleSheet css = new StyleSheet();
        css.addRule("body {"
                + "text-align:center;"
                + "}");
        css.addRule("p {"
                + "font-family: Sans, Georgia, serif; "
                + "font-size: 11pt;"
                + "text-align:justify;"
                + "}");
        css.addRule("span { "
                + "font-size: 8px;"
                + "font-family: Sans; "
                + "color: #808080;"
                + "}");
        css.addRule("#nick {"
                + "font-family: Sans, Georgia, serif; "
                + "font-size: 11pt;"
                + "text-align:justify;"
                + "}");
        css.addRule("#connect { "
                + "display: block;"
                + "-moz-border-radius: 5px;"
                + "border-radius: 5px;"
                + "border-width: 1px;"
                + "border-style: solid;"
                + "border-color: #9bac55;"
                + "padding: 5px;"
                + "background-color: #e5f993;"
                + "margin-right: 3;"
                + "margin-left: 3;"
                + "margin-top: 3;"
                + "text-align:center;"
                + "}");
        css.addRule("#disconnect { "
                + "display: block;"
                + "border-width: 1px;"
                + "border-style: solid;"
                + "border-color: #90b1c7;"
                + "padding: 5px;"
                + "background-color: #fbe0e0;"
                + "margin-right: 3;"
                + "margin-left: 3;"
                + "margin-top: 3;"
                + "text-align:center;"
                + "}");
        css.addRule("#warning { "
                + "display: block;"
                + "border-width: 1px;"
                + "border-style: solid;"
                + "border-color: #c2beb1;"
                + "padding: 5px;"
                + "background-color: #fffcd3;"
                + "margin-right: 3;"
                + "margin-left: 3;"
                + "margin-top: 3;"
                + "text-align:center;"
                + "}");
        css.addRule("#success { "
                + "display: block;"
                + "border-width: 1px;"
                + "border-style: solid;"
                + "border-color: #ffffff;"
                + "padding: 5px;"
                + "background-color: #ffffff;"
                + "margin-right: 3;"
                + "margin-left: 3;"
                + "margin-top: 3;"
                + "text-align:center;"
                + "}");

        htmlKit.setStyleSheet(css);
    }

    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Calendar calendar = format.getCalendar();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String data = "" + hour;
        if (hour < 10) {
            data = "0" + data;
        }
        if (minute < 10) {
            data += ":0" + minute;
        } else {
            data += ":" + minute;
        }
        return data;
    }
}
