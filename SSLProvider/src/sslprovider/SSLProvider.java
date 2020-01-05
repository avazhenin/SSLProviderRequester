/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sslprovider;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

/**
 *
 * @author lagi
 */
public class SSLProvider implements Runnable{

    int port = 12345;
    ServerSocketFactory ssocketFactory;
    ServerSocket ssocket;
    Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;
    String inmessage;
    String outmessage;

    public SSLProvider(int port) {
        this.port=port;
    }

    /**
     * @param args the command line arguments
     */

    public void run() {
        try {
            // TODO code application logic here
            //System.setProperty("javax.net.debug", "ssl");
            System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "miz2nda");
            System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
            System.setProperty("javax.net.ssl.trustStore", "keystore.jks");
            ssocketFactory = SSLServerSocketFactory.getDefault();
            ssocket = ssocketFactory.createServerSocket(port);
            System.out.println("Waiting for connection");
            socket = ssocket.accept();
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            // do stuff    
            getMessage();
            System.out.println("client>" + inmessage);
            sendMessage(prepareMessage());

        } catch (IOException ex) {
            Logger.getLogger(SSLProvider.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                socket.close();
                ssocket.close();
                in.close();
            } catch (Exception e) {
            }
        }

    }

    void sendMessage(String msg) {
        String getmsg = null;
        try {
            out.writeObject(msg);
            out.flush();
            System.out.println("server>" + msg);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    void getMessage() {
        try {
            inmessage = in.readObject().toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    String prepareMessage() {
        switch (inmessage.toLowerCase()) {
            case "hello":
                return outmessage = "Hi there";
            case "lagi":
                return outmessage = "Hi mate lagi";
            case "bye":
                return outmessage = "Bye";
            case "fuck u":
                return outmessage = "no fuck you!!!...";
        }
        return outmessage = "ok";
    }

    public static void main(String[] args) {
        SSLProvider sslprovider = new SSLProvider(Integer.valueOf(args[0]));
        while (true) {
            sslprovider.run();
        }
    }
}
