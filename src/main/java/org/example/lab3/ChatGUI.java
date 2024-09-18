package org.example.lab3;

import org.bouncycastle.util.encoders.Hex;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static org.example.lab3.DigitalSignatureUtility.sign;
import static org.example.lab3.DigitalSignatureUtility.verify;

public class ChatGUI {

    private JComboBox<String> comboBoxFrom;
    private JComboBox<String> comboBoxTo;
    private JComboBox<String> comboBoxToSend;
    private JComboBox<String> comboBoxToCheck;
    private JButton sendButton;
    private JTextField informativeLabel;
    private JButton checkButton;

    private KeyPair pair;

    public ChatGUI(String name) {
        JFrame frame = new JFrame(name);
        frame.setSize(700, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Left container
        JPanel leftPanel = new JPanel();
        initLeftPanel(leftPanel);

        // Right container
        JPanel rightPanel = new JPanel();
        initRightPanel(rightPanel);

        // Add containers to frame
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new GridLayout(1, 2));
        contentPane.add(leftPanel);
        contentPane.add(rightPanel);

        frame.setVisible(true);
    }

    private void initLeftPanel(JPanel leftPanel) {
        leftPanel.setLayout(new GridLayout(3, 2));

        JLabel label1 = new JLabel("    From:");
        leftPanel.add(label1);
        comboBoxFrom = new JComboBox<>(new String[]{User.SENDER.name()});
        leftPanel.add(comboBoxFrom);

        JLabel label2 = new JLabel("    To:");
        leftPanel.add(label2);
        comboBoxTo = new JComboBox<>(new String[]{User.RECEIVER.name(), User.TRENT.name()});
        leftPanel.add(comboBoxTo);

        comboBoxToSend = new JComboBox<>(new String[]{"source_message", "signed_message"});
        leftPanel.add(comboBoxToSend);
        sendButton = new JButton("Send");
        sendButton.addActionListener(l -> {
            try {
                sendButtonOnClick();
            } catch (NoSuchAlgorithmException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        leftPanel.add(sendButton);
    }

    private void initRightPanel(JPanel rightPanel) {
        rightPanel.setLayout(new GridLayout(3, 3));
        //rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JLabel labelEmpty1 = new JLabel("");
        rightPanel.add(labelEmpty1);
        informativeLabel = new JTextField(5);
        informativeLabel.setText("Введите путь к файлу");
        informativeLabel.setEditable(false);
        rightPanel.add(informativeLabel);

        JLabel labelEmpty2 = new JLabel("");
        rightPanel.add(labelEmpty2);
        comboBoxToCheck = new JComboBox<>(new String[]{"signed_message", "incorrect_source_message"});
        rightPanel.add(comboBoxToCheck);

        JLabel labelEmpty3 = new JLabel("");
        rightPanel.add(labelEmpty3);
        checkButton = new JButton("Check");
        checkButton.addActionListener(l -> {
            try {
                checkButtonOnClick();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        rightPanel.add(checkButton);
    }

    private void sendButtonOnClick() throws NoSuchAlgorithmException, IOException {
        if (Objects.equals(comboBoxTo.getSelectedItem(), User.TRENT.name())
                && Objects.equals(comboBoxToSend.getSelectedItem(), "source_message")) {

            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            pair = generator.generateKeyPair();

            System.out.println("Публичный ключ: " + Hex.toHexString(pair.getPublic().getEncoded()));
            System.out.println("Приватный ключ: " + Hex.toHexString(pair.getPrivate().getEncoded()));

            System.out.println("\nХэш исходного сообщения: ");
            byte[] messageBytesFromFile = Files.readAllBytes(Paths.get("src/main/resources/sender/source_message.txt"));
            byte[] signedMessageFromFile = sign(messageBytesFromFile, "MD5", pair.getPrivate());
            Files.write(Paths.get("src/main/resources/sender/signed_message.txt"), signedMessageFromFile);

            System.out.println("Зашифрованное сообщение - " + Hex.toHexString(signedMessageFromFile));

            informativeLabel.setText("Sender получил подписанный файл от Trent");
        } else if (Objects.equals(comboBoxTo.getSelectedItem(), User.RECEIVER.name())
                && Objects.equals(comboBoxToSend.getSelectedItem(), "signed_message")) {

            byte[] messageBytesFromSourceFile = Files.readAllBytes(Paths.get("src/main/resources/sender/source_message.txt"));
            Files.write(Paths.get("src/main/resources/receiver/source_message.txt"), messageBytesFromSourceFile);

            byte[] messageBytesFromSignedFile = Files.readAllBytes(Paths.get("src/main/resources/sender/signed_message.txt"));
            Files.write(Paths.get("src/main/resources/receiver/signed_message.txt"), messageBytesFromSignedFile);

            messageBytesFromSourceFile[0] = (byte) (messageBytesFromSourceFile[0] + 8);
            Files.write(Paths.get("src/main/resources/receiver/incorrect_source_message.txt"), messageBytesFromSourceFile);

            informativeLabel.setText("Sender отправил исходный и подписанный файл для Receiver");
        } else {
            informativeLabel.setText("Неправильная комбинация!");
        }

    }

    private void checkButtonOnClick() throws IOException {
        boolean isCorrect;
        if (Objects.equals(comboBoxToCheck.getSelectedItem(), "signed_message")) {
            System.out.println("\nПроверочный хэш: ");
            byte[] messageBytesFromSourceFile = Files.readAllBytes(Paths.get("src/main/resources/receiver/source_message.txt"));
            byte[] messageBytesFromSignedFile = Files.readAllBytes(Paths.get("src/main/resources/receiver/signed_message.txt"));
            isCorrect = verify(messageBytesFromSourceFile, "MD5", pair.getPublic(), messageBytesFromSignedFile);
        } else {
            System.out.println("\nПроверочный хэш неверного сообщения: ");
            byte[] messageBytesFromIncorrectSourceFile = Files.readAllBytes(Paths.get("src/main/resources/receiver/incorrect_source_message.txt"));
            byte[] messageBytesFromSignedFile = Files.readAllBytes(Paths.get("src/main/resources/receiver/signed_message.txt"));

            //sign(messageBytesFromIncorrectSourceFile, "MD5", pair.getPrivate());

            isCorrect = verify(messageBytesFromIncorrectSourceFile, "MD5", pair.getPublic(), messageBytesFromSignedFile);
        }

        System.out.println("Верна ли подпись: " + isCorrect);

        if (isCorrect) {
            informativeLabel.setText("Подпись верна!");
        } else {
            informativeLabel.setText("Неверная подпись!");
        }
    }

}
