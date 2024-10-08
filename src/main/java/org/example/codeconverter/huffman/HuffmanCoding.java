package org.example.codeconverter.huffman;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

class HuffmanComparator implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode x, HuffmanNode y) {
        return x.frequency - y.frequency;
    }
}

public class HuffmanCoding {

    // Построение дерева Хаффмана
    public static HuffmanNode buildHuffmanTree(Map<Character, Integer> frequencyMap) {
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>(new HuffmanComparator());

        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            priorityQueue.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();

            HuffmanNode node = new HuffmanNode('-', left.frequency + right.frequency);
            node.left = left;
            node.right = right;

            priorityQueue.add(node);
        }

        return priorityQueue.poll();
    }

    // Генерация Хаффман-кодов
    public static void generateCodes(HuffmanNode root, String code, Map<Character, String> huffmanCode) {
        if (root == null) return;

        if (root.character != '-') {
            huffmanCode.put(root.character, code);
        }

        generateCodes(root.left, code + "0", huffmanCode);
        generateCodes(root.right, code + "1", huffmanCode);
    }

    // Кодирование строки
    public static String encode(String text, Map<Character, String> huffmanCode) {
        StringBuilder encodedString = new StringBuilder();
        for (char character : text.toCharArray()) {
            String code = huffmanCode.get(character);

            if (code != null) {
                encodedString.append(code);
            } else {
                //throw new IllegalArgumentException("Не найдено кодирование для символа: " + character);
            }
            //encodedString.append(huffmanCode.get(character));
        }
        return encodedString.toString();
    }

    // Декодирование строки
    public static String decode(String encodedString, HuffmanNode root) {
        StringBuilder decodedString = new StringBuilder();
        HuffmanNode currentNode = root;

        for (char bit : encodedString.toCharArray()) {
            currentNode = (bit == '0') ? currentNode.left : currentNode.right;

            if (currentNode.left == null && currentNode.right == null) {
                decodedString.append(currentNode.character);
                currentNode = root;
            }
        }
        return decodedString.toString();
    }

    public static void main(String[] args) {
        String text = "Пример текста для кодирования";

        // Подсчёт частот символов
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char character : text.toCharArray()) {
            frequencyMap.put(character, frequencyMap.getOrDefault(character, 0) + 1);
        }

        // Построение дерева Хаффмана
        HuffmanNode root = buildHuffmanTree(frequencyMap);

        // Генерация Хаффман-кодов
        Map<Character, String> huffmanCode = new HashMap<>();
        generateCodes(root, "", huffmanCode);

        System.out.println("Хаффман-коды: " + huffmanCode);

        // Кодирование строки
        String encodedString = encode(text, huffmanCode);
        System.out.println("Закодированная строка: " + encodedString);

        // Декодирование строки
        String decodedString = decode(encodedString, root);
        System.out.println("Раскодированная строка: " + decodedString);
    }
}
