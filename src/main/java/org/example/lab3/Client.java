package org.example.lab3;

import javax.swing.*;

public class Client {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatGUI("Client"));
    }
}

