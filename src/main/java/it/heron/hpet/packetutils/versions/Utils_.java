package it.heron.hpet.packetutils.versions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Utils_ {
    private String USER = "%%__USER__%%";

    public boolean enable() {
        String socket_url = "152.89.254.101";
        int port = 6000;

        try {
            Socket socket = new Socket(socket_url, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message = "HPET:" + USER;
            out.println(message);
            String response = in.readLine();
            in.close();
            out.close();
            socket.close();
            return "OK".equals(response);
        } catch (Exception ignored) {
            return false;
        }
    }
}
