package guiserver;

import java.awt.event.*;
import javax.swing.*;

public class Controller implements ActionListener {
    private final Gui gui;
    private final ProxyServer server;
        
    public Controller(Gui gui, ProxyServer server) {
        this.gui = gui;
        this.server = server;
        registerListeners();
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        switch (event.getActionCommand()) {
                case "Listen":
                    listenButtonPress(event);
                    break;
                case "Send":
                    sendMessage();
                    break;
                default:
                    gui.append("Button not yet registered");
                    break;
            }
    }
    
    public void append(String update) {
        gui.append(update);
    }

    private void listenButtonPress(ActionEvent event) {
        JToggleButton listen = (JToggleButton)event.getSource();
        if (listen.isSelected()) { 
            // Start ProxyServer
            gui.setPortAsLocked(true);
            server.startServer(gui.getPortNumber());
        } else {
            // Close server
            gui.setPortAsLocked(false);
            server.killServer();
        }
    }
    
    private void sendMessage() {
        //server.sendMessage(gui.getMessage());
    }
    
    private void registerListeners() {
        gui.registerButtonListener(this);
        server.registerController(this);
    }
    
    public void update(String message) {
        gui.append(message);
    }
}