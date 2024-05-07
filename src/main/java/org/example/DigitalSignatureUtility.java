package org.example;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.Arrays;

public class DigitalSignatureUtility {
    public static void main(String[] args) throws Exception {
        // генерируем пару ключей
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();

        // подписываем сообщение из консоли
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Input message: ");
        String input = reader.readLine();
        byte[] messageBytes = input.getBytes();
        byte[] signedMessage = sign(messageBytes, "MD5", pair.getPrivate());
        System.out.println("signedMessage - " + Hex.toHexString(signedMessage));
        boolean isCorrect = verify(messageBytes, "MD5", pair.getPublic(), signedMessage);
        System.out.println("Signature for input: " + isCorrect);

        System.out.println("\n /////////////////// \n");

        // подписываем файл
        byte[] messageBytesFromFile = Files.readAllBytes(Paths.get("src/main/resources/message.txt"));
        byte[] signedMessageFromFile = sign(messageBytesFromFile, "MD5", pair.getPrivate());
        System.out.println("signedMessage - " + Hex.toHexString(signedMessageFromFile));
        boolean isCorrectForFile = verify(messageBytesFromFile, "MD5", pair.getPublic(), signedMessageFromFile);
        System.out.println("Signature for file: " + isCorrectForFile);
    }

    public static byte[] sign(byte[] messageBytes, String hashingAlgorithm, PrivateKey privateKey) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashingAlgorithm);
            byte[] messageHash = md.digest(messageBytes);
            DigestAlgorithmIdentifierFinder hashAlgorithmFinder = new DefaultDigestAlgorithmIdentifierFinder();
            AlgorithmIdentifier hashingAlgorithmIdentifier = hashAlgorithmFinder.find(hashingAlgorithm);
            DigestInfo digestInfo = new DigestInfo(hashingAlgorithmIdentifier, messageHash);
            byte[] hashToEncrypt = digestInfo.getEncoded();

            System.out.println("hash - " + Hex.toHexString(messageHash));

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(hashToEncrypt);
        } catch (GeneralSecurityException | IOException exp) {
            throw new SecurityException("Error during signature generation", exp);
        }
    }

    public static boolean verify(byte[] messageBytes, String hashingAlgorithm, PublicKey publicKey, byte[] encryptedMessageHash) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashingAlgorithm);
            byte[] newMessageHash = md.digest(messageBytes);
            DigestAlgorithmIdentifierFinder hashAlgorithmFinder = new DefaultDigestAlgorithmIdentifierFinder();
            AlgorithmIdentifier hashingAlgorithmIdentifier = hashAlgorithmFinder.find(hashingAlgorithm);
            DigestInfo digestInfo = new DigestInfo(hashingAlgorithmIdentifier, newMessageHash);
            byte[] hashToEncrypt = digestInfo.getEncoded();

            System.out.println("hash - " + Hex.toHexString(hashToEncrypt));

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] decryptedMessageHash = cipher.doFinal(encryptedMessageHash);
            System.out.println("Расшифрованный хэш: " + Hex.toHexString(decryptedMessageHash));
            return Arrays.equals(decryptedMessageHash, hashToEncrypt);
        } catch (GeneralSecurityException | IOException exp) {
            throw new SecurityException("Error during verifying", exp);
        }
    }
}