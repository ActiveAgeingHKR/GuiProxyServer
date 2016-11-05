package guiserver;

public class ProxyServer {
    
    private ProxyThread serverThread;
    private Controller controller;
    private int port = 12345;
    
    public static void main(String[] args) {
        Gui gui = new Gui();
        ProxyServer server = new ProxyServer();
        Controller controller = new Controller(gui, server);
    }        
    
    public void registerController(Controller controller) {
        this.controller = controller;
    }

    public void startServer(int port) {
        this.port = port;
        controller.update("Trying to start server");
        createNewServerThread();
    }
    
    private void createNewServerThread() {
        serverThread = new ProxyThread(0, port);
        serverThread.registerServer(this);
        serverThread.start();
    }
    
    public void killServer() {
        serverThread.kill();
    }
    
    public void updateController(int id, String message) {
        controller.update("Thread Id: " + id + "  -  " + message);
    }
}
