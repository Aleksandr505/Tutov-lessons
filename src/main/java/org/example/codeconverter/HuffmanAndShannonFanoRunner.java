package org.example.codeconverter;

import org.example.codeconverter.huffman.HuffmanCoding;
import org.example.codeconverter.huffman.HuffmanNode;
import org.example.codeconverter.shennon.ShannonFanoCoding;
import org.example.codeconverter.shennon.ShannonFanoNode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class HuffmanAndShannonFanoRunner {

    public static void main(String[] args) {
        try {
            String baseFilePath = "D:\\repos\\lessons\\src\\main\\resources\\codeconverter\\";

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Please enter your file path for directory: " + baseFilePath + "\n");
            String readLine = reader.readLine();
            String filePath = baseFilePath + readLine;
            writeOptions();
            while (true) {
                String option = reader.readLine();
                if (option.equals("0")) {
                    System.out.println("Please enter your file path for directory: " + baseFilePath + "\n");
                    readLine = reader.readLine();
                    filePath = baseFilePath + readLine;
                    writeOptions();
                } else if (option.equals("1")) {
                    huffmanMethod(filePath);
                    writeOptions();
                } else if (option.equals("2")) {
                    shannonFanoMethod(filePath);
                    writeOptions();
                } else if (option.equals("3")) {
                    return;
                } else {
                    System.out.println("Unknown option, try again: " + option);
                }
            }
        } catch (Exception e) {
            System.out.println("OOPS, something went wrong!");
            e.printStackTrace();
        }
    }

    private static void writeOptions() {
        System.out.println("\n");
        System.out.println("Please enter option: ");
        System.out.println("Change filepath                 : 0 ");
        System.out.println("Huffman method                  : 1 ");
        System.out.println("Shannon-Fano method             : 2 ");
        System.out.println("Exit                            : 3 ");
        System.out.println("\n");
    }

    private static void huffmanMethod(String filePath) throws IOException {
        String encodingFilepath = filePath.replace(".txt", "_") + "huffman_encoding" + ".txt";

        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        String text = new String(bytes);

        // Подсчёт частот символов
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char character : text.toCharArray()) {
            frequencyMap.put(character, frequencyMap.getOrDefault(character, 0) + 1);
        }

        // Построение дерева Хаффмана
        HuffmanNode root = HuffmanCoding.buildHuffmanTree(frequencyMap);

        // Генерация Хаффман-кодов
        Map<Character, String> huffmanCode = new HashMap<>();
        HuffmanCoding.generateCodes(root, "", huffmanCode);

        System.out.println("Хаффман-коды: " + huffmanCode);

        // Кодирование строки
        String encodedString = HuffmanCoding.encode(text, huffmanCode);
        System.out.println("Закодированная строка: " + encodedString);

        writeCompressedDataToFile(encodedString, encodingFilepath);

        /*try (Writer writer = new OutputStreamWriter(new FileOutputStream(encodingFilepath))) {
            writer.write(encodedString);
            System.out.println("Файл закодирован методом Хаффмана по пути:" + encodingFilepath);
        } catch (Exception e) {
            System.out.println("Не удалось записать файл, закодированный методом Хаффмана");
        }*/

        String decodingFilepath = filePath.replace(".txt", "_") + "huffman_decoding" + ".txt";
        String decodedString = HuffmanCoding.decode(encodedString, root);
        //System.out.println("Раскодированная строка: " + decodedString);

        //System.out.println("\n ///////////////////////////////////////////// \n");
        //System.out.println(HuffmanCoding.readCompressedDataFromFile(decodingFilepath));

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(decodingFilepath))) {
            writer.write(decodedString);
            System.out.println("Файл раскодирован методом Хаффмана по пути:" + decodingFilepath);
        } catch (Exception e) {
            System.out.println("Не удалось записать файл, раскодированный методом Хаффмана");
        }
    }

    private static void shannonFanoMethod(String filePath) throws IOException {
        String encodingFilepath = filePath.replace(".txt", "_") + "shannon_fano_encoding" + ".txt";

        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        String text = new String(bytes);

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
        ShannonFanoCoding.shannonFano(nodes, 0, nodes.size() - 1);

        // Генерация кодов
        Map<Character, String> shannonFanoCode = new HashMap<>();
        for (ShannonFanoNode node : nodes) {
            shannonFanoCode.put(node.character, node.code);
        }

        System.out.println("Коды Шеннона-Фано: " + shannonFanoCode);

        // Кодирование строки
        String encodedString = ShannonFanoCoding.encode(text, shannonFanoCode);
        System.out.println("Закодированная строка: " + encodedString);

        writeCompressedDataToFile(encodedString, encodingFilepath);

        /*try (Writer writer = new OutputStreamWriter(new FileOutputStream(encodingFilepath))) {
            writer.write(encodedString);
            System.out.println("Файл закодирован методом Шенона-Фано по пути:" + encodingFilepath);
        } catch (Exception e) {
            System.out.println("Не удалось записать файл, закодированный методом Шенона-Фано");
        }*/

        // Декодирование строки
        String decodingFilepath = filePath.replace(".txt", "_") + "shannon-fano_decoding" + ".txt";
        String decodedString = ShannonFanoCoding.decode(encodedString, nodes);
        //System.out.println("Раскодированная строка: " + decodedString);

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(decodingFilepath))) {
            writer.write(decodedString);
            System.out.println("Файл раскодирован методом Шенона-Фано по пути:" + decodingFilepath);
        } catch (Exception e) {
            System.out.println("Не удалось записать файл, раскодированный методом Шенона-Фано");
        }
    }

    // Преобразует строку с нулями и единицами в байты и записывает их в файл
    public static void writeCompressedDataToFile(String binaryString, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            // Добавляем нули в конец строки для кратности 8, если длина не кратна 8
            while (binaryString.length() % 8 != 0) {
                binaryString += "0";
            }

            // Преобразуем каждую группу из 8 символов (бит) в один байт
            for (int i = 0; i < binaryString.length(); i += 8) {
                String byteString = binaryString.substring(i, i + 8);
                int byteValue = Integer.parseInt(byteString, 2); // Преобразование в десятичное
                fos.write(byteValue);  // Запись в файл как байт
            }
            System.out.println("Данные успешно записаны в файл как байты.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readCompressedDataFromFile(String filePath) {
        StringBuilder binaryString = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            int byteValue;
            while ((byteValue = fis.read()) != -1) {
                // Преобразуем каждый байт обратно в бинарную строку длиной 8 символов
                String byteString = String.format("%8s", Integer.toBinaryString(byteValue)).replace(' ', '0');
                binaryString.append(byteString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return binaryString.toString();
    }

}
