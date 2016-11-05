package guiserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProxyThread implements Runnable {
    
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private ServerSocket serverSocket;
    public Socket socket = null;
    private ProxyServer server;
    private Thread thread;
    private final int id;
    private final int port;
    
    public ProxyThread(int id, int port) {
        this.id = id;
        this.port = port;
        socket = new Socket();
    }
    
    public void registerServer(ProxyServer server) {
        this.server = server;
        notifyServer("Thread created");
    }
    
    @Override
    public void run() {
        notifyServer("Thread starting");
        
        try {
            reservePort();
            waitForConnection();
            setUpStreams(); // TODO finish exception
          //  whileChatting(); // TODO finish exception
        } catch (IOException ex) {
            notifyServer(ex.getMessage());
        }        

        close();
        notifyServer("Thread Finished\n");
    }
    
    private void reservePort() throws IOException {
        if (serverSocket == null) {
            try {
                serverSocket = new ServerSocket(port, 0);
                notifyServer("Listening on port: " + port);
            } catch (IOException ex) {
                throw new IOException("Port " + port + " in use. Try a different one");
            }
        }
    }
    
    private void waitForConnection() throws IOException {       
        try {
            notifyServer("Waiting for connection...");
            socket = serverSocket.accept();
        } catch(IOException ex) {
            throw new IOException("Wait aborted...");
        }
    }

    private void setUpStreams() throws IOException {
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();
        
        input = new ObjectInputStream(socket.getInputStream());
        notifyServer("Now connected to " + socket.getInetAddress().getHostName());
    }
    
   /* private void whileChatting() throws IOException {
            try {
                notifyServer("CLIENT - " + (String)input.readObject());
            } catch (ClassNotFoundException classNotFoundException) {
                throw new IOException("whileChatting() - classNotFoundException");
            } catch (IOException ex) {
                throw new IOException("Client terminated connection...");
            }
    }*/
    
    private void close() {
        try {
            input.close();
        } catch(IOException ex) {
            notifyServer("close() - input.close() error");
        }
        
        
        try {
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
        } catch(IOException ex) {
            notifyServer("Error closing port: " + port + " hmm...");
        }
    }
    
    public void start() {
        if (thread == null) {
            thread = new Thread(this, Integer.toString(id));
            thread.start();
        } else {
            thread = null;
        }
    }
    
    public void kill() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(ProxyThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public boolean isRunning() {
        return thread.isAlive();
    }
    
    private void notifyServer(String message) {
        server.updateController(id, message);
    }
}
