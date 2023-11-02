package it.heron.hpet;

import org.bukkit.Bukkit;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class SecurityUtils {

    public static boolean enable(File file) {



        try {
            FileWriter writer = new FileWriter(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private static String getLocalIP() {
        try {
            return String.valueOf(InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }



}
