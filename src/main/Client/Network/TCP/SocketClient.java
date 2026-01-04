package main.Client.Network.TCP;

import org.bson.Document;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketClient {

    private static SocketClient instance;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private volatile boolean connected = false;

    private SocketClient() {}

    public static synchronized SocketClient getInstance() {
        if (instance == null) instance = new SocketClient();
        return instance;
    }

    // CONNECT TCP 1 LẦN DUY NHẤT
    public synchronized void connect(String host, int port) throws IOException {
        if (connected) return;

        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

        connected = true;
        System.out.println("Core TCP connected");
    }

    public boolean isConnected() {
        return connected;
    }

    public void send(Document doc) {
        synchronized (out) {
            out.println(doc.toJson());
            out.flush();
        }
    }

    public BufferedReader getReader() {
        return in;
    }
}
