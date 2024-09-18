package org.example.lab3;

import javax.swing.*;
import java.awt.*;

public class SwingGUIExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Swing GUI Example");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Left container
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(3, 2));

        JLabel label1 = new JLabel("    From:");
        leftPanel.add(label1);
        JComboBox<String> comboBox1 = new JComboBox<>(new String[]{User.SENDER.name(), User.RECEIVER.name(), User.TRENT.name()});
        leftPanel.add(comboBox1);

        JLabel label2 = new JLabel("    To:");
        leftPanel.add(label2);
        JComboBox<String> comboBox2 = new JComboBox<>(new String[]{User.SENDER.name(), User.RECEIVER.name(), User.TRENT.name()});
        leftPanel.add(comboBox2);

        JLabel label3 = new JLabel("");
        leftPanel.add(label3);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(l -> {
            System.out.println("Hola!");
        });
        leftPanel.add(sendButton);

        // Right container
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JTextField textField1 = new JTextField(20);
        rightPanel.add(textField1);

        JTextField textField2 = new JTextField(20);
        rightPanel.add(textField2);

        // Add containers to frame
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new GridLayout(1, 2));
        contentPane.add(leftPanel);
        contentPane.add(rightPanel);

        frame.setVisible(true);
    }
}
