/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sslrequester;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 *
 * @author lagi
 */
public class SSLRequesterMain {

    /**
     * @param args the command line arguments
     */
    KeyStore tks;
    KeyStore iks;
    SSLContext ctx;
    TrustManagerFactory tmf;
    KeyManagerFactory kmf;
    SocketFactory factory;
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    String outmessage;
    static String inmessage;
//    int port = 12345;
//    String host = "192.168.101.94";
    char[] passphrase = "miz2nda".toCharArray();
    String sometext;

    void run(String host, int port, String line) {
        try {
            tks = KeyStore.getInstance(KeyStore.getDefaultType());
            tks.load(new FileInputStream("keystore.jks"), passphrase); /* Load the trust key store with root CAs. */
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(tks);
            iks = KeyStore.getInstance(KeyStore.getDefaultType());
            iks.load(new FileInputStream("keystore.jks"), passphrase); /* Load the identity key store with your key/cert. */
            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(iks, passphrase);
            ctx = SSLContext.getInstance("TLS");
            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            factory = ctx.getSocketFactory();
            socket = factory.createSocket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            try {
                sendMessage(line);
                getMessage();
                System.out.println("server>" + inmessage);
            } catch (Exception e) {
            }

        } catch (Exception e) {
            inmessage = e.getMessage();
            e.printStackTrace();
        } finally {
            try {
                out.close();
                socket.close();
            } catch (Exception e) {
            }

        }
    }

    void sendMessage(String msg) {
        String getmsg = null;
        try {
            out.writeObject(msg);
            out.flush();
            System.out.println("client>" + msg);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    void getMessage() {
        try {
            inmessage = in.readObject().toString();
        } catch (java.io.EOFException e) {
            inmessage = "No message from server";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public static void main(String args[]) {
        SSLRequesterMain client = new SSLRequesterMain();
        client.run("localhost", 12345, "hello");
    }
}
