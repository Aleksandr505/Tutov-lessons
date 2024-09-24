package org.example.codeconverter.huffman;

import java.util.*;

class ShannonFanoNode {
    char character;
    double probability;
    String code;

    ShannonFanoNode(char character, double probability) {
        this.character = character;
        this.probability = probability;
        this.code = "";
    }
}

public class ShannonFanoCoding {
    public static void shannonFano(List<ShannonFanoNode> nodes, int start, int end) {
        if (start >= end) return;

        double totalSum = 0;
        for (int i = start; i <= end; i++) {
            totalSum += nodes.get(i).probability;
        }

        double partialSum = 0;
        int splitIndex = start;
        for (int i = start; i <= end; i++) {
            partialSum += nodes.get(i).probability;
            if (partialSum >= totalSum / 2) {
                splitIndex = i;
                break;
            }
        }

        for (int i = start; i <= splitIndex; i++) {
            nodes.get(i).code += "0";
        }
        for (int i = splitIndex + 1; i <= end; i++) {
            nodes.get(i).code += "1";
        }

        shannonFano(nodes, start, splitIndex);
        shannonFano(nodes, splitIndex + 1, end);
    }

    public static String encode(String text, Map<Character, String> shannonFanoCode) {
        StringBuilder encodedString = new StringBuilder();
        for (char character : text.toCharArray()) {
            encodedString.append(shannonFanoCode.get(character));
        }
        return encodedString.toString();
    }

    public static String decode(String encodedString, List<ShannonFanoNode> nodes) {
        StringBuilder decodedString = new StringBuilder();
        String temp = "";

        for (char bit : encodedString.toCharArray()) {
            temp += bit;
            for (ShannonFanoNode node : nodes) {
                if (node.code.equals(temp)) {
                    decodedString.append(node.character);
                    temp = "";
                    break;
                }
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

        // Подсчёт вероятностей
        List<ShannonFanoNode> nodes = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            nodes.add(new ShannonFanoNode(entry.getKey(), entry.getValue() / (double) text.length()));
        }

        // Сортировка по вероятности
        nodes.sort(Comparator.comparingDouble(a -> -a.probability));

        // Применение алгоритма Шеннона-Фано
        shannonFano(nodes, 0, nodes.size() - 1);

        // Генерация кодов
        Map<Character, String> shannonFanoCode = new HashMap<>();
        for (ShannonFanoNode node : nodes) {
            shannonFanoCode.put(node.character, node.code);
        }

        System.out.println("Коды Шеннона-Фано: " + shannonFanoCode);

        // Кодирование строки
        String encodedString = encode(text, shannonFanoCode);
        System.out.println("Закодированная строка: " + encodedString);

        // Декодирование строки
        String decodedString = decode(encodedString, nodes);
        System.out.println("Раскодированная строка: " + decodedString);
    }
}
