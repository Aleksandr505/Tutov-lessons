package org.example;

import javax.swing.*;

public class Client {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatGUI("Client"));
    }
}

