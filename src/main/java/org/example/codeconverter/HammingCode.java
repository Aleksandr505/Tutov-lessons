package org.example.codeconverter;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HammingCode {

    public static List<int[]> encodeText(String input) {
        List<int[]> encodedBlocks = new ArrayList<>();

        // Преобразуем каждый символ в 8-битное двоичное представление
        for (char ch : input.toCharArray()) {
            String binaryString = String.format("%8s", Integer.toBinaryString(ch)).replace(' ', '0');

            // Разбиваем на 4-битные блоки
            for (int i = 0; i < binaryString.length(); i += 4) {
                String block = binaryString.substring(i, Math.min(i + 4, binaryString.length()));
                int[] encodedBlock = encodeBlock(block);
                encodedBlocks.add(encodedBlock);
            }
        }
        return encodedBlocks;
    }

    private static int[] encodeBlock(String block) {
        int[] dataBits = new int[4];
        for (int i = 0; i < block.length(); i++) {
            dataBits[i] = Character.getNumericValue(block.charAt(i));
        }

        // Добавляем проверочные биты
        int p1 = dataBits[0] ^ dataBits[1] ^ dataBits[3];
        int p2 = dataBits[0] ^ dataBits[2] ^ dataBits[3];
        int p3 = dataBits[1] ^ dataBits[2] ^ dataBits[3];

        // Закодированный блок данных + проверочные биты
        return new int[]{p1, p2, dataBits[0], p3, dataBits[1], dataBits[2], dataBits[3]};
    }

    public static int[] correctErrors(int[] block) {
        // Проверочные биты
        int p1 = block[0];
        int p2 = block[1];
        int p3 = block[3];

        // Информационные биты
        int d0 = block[2];
        int d1 = block[4];
        int d2 = block[5];
        int d3 = block[6];

        // Рассчитываем синдром (вычисляем, есть ли ошибка)
        int s1 = p1 ^ d0 ^ d1 ^ d3;
        int s2 = p2 ^ d0 ^ d2 ^ d3;
        int s3 = p3 ^ d1 ^ d2 ^ d3;

        // Определяем позицию ошибки
        int errorPosition = (s3 << 2) | (s2 << 1) | s1;

        if (s1 == 0 && s2 == 0 && s3 == 0) {
            // Нет ошибок
            System.out.println("Ошибок нет для блока - " + mapBlockToString(block));
        } else if (errorPosition > 0 && errorPosition <= 7) {
            // Исправляем одну ошибку
            System.out.println("Обнаружена ошибка в позиции " + errorPosition + " для исходного блока - " + mapBlockToString(block));
            block[errorPosition - 1] ^= 1; // Инвертируем бит в найденной позиции
            System.out.print("    Блок после исправления - " + mapBlockToString(block) + "\n");
        } else {
            // Если синдромы не соответствуют положению одной ошибки, сообщаем о возможной двойной ошибке
            System.out.println("Обнаружено больше одной ошибки в блоке - " + mapBlockToString(block) + ". Исправление невозможно.");
        }

        return block;
    }

    public static void main(String[] args) {
        try {
            String baseFilePath = "D:\\repos\\lessons\\src\\main\\resources\\codeconverter\\";

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            writeOptions();
            while (true) {
                String option = reader.readLine();
                if (option.equals("1")) {
                    System.out.println("Please enter your string to encode: " + "\n");
                    String input = reader.readLine();
                    List<int[]> encodedText = encodeText(input);
                    writeEncodedBlocks(baseFilePath, encodedText);
                    writeOptions();
                } else if (option.equals("2")) {
                    List<int[]> encodedBlocks = readEncodedBlocks(baseFilePath);
                    List<int[]> correctedList = new ArrayList<>();
                    for (int[] block : encodedBlocks) {
                        int[] correctedBlock = correctErrors(block);
                        correctedList.add(correctedBlock);
                    }
                    writeCorrectedBlocks(baseFilePath, correctedList);
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

    private static String mapBlockToString(int[] block) {
        StringBuilder s = new StringBuilder();
        for (int j : block) {
            s.append(j);
        }
        return s.toString();
    }

    private static void writeOptions() {
        System.out.println("\n");
        System.out.println("Please enter option: ");
        System.out.println("Encode text                    : 1 ");
        System.out.println("Correct text                   : 2 ");
        System.out.println("Exit                           : 3 ");
        System.out.println("\n");
    }

    private static void writeEncodedBlocks(String filepath, List<int[]> codes) {
        filepath += "encode_text.txt";

        StringBuilder result = new StringBuilder();
        for (int[] block : codes) {
            for (int j : block) {
                result.append(j);
            }
            result.append(" ");
        }


        try (Writer writer = new OutputStreamWriter(new FileOutputStream(filepath))) {
            writer.write(result.toString());
            System.out.println("String encoded");
        } catch (Exception e) {
            System.out.println("Cannot encode string!");
        }
    }

    private static List<int[]> readEncodedBlocks(String filepath) {
        filepath += "encode_text.txt";
        List<int[]> resultList = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            String s = reader.readLine();
            String[] spl = s.split(" ");

            for (String block : spl) {
                int[] intBlock = new int[block.length()];
                for (int i = 0; i < block.length(); i++) {
                    String ch = String.valueOf(block.charAt(i));
                    intBlock[i] = Integer.parseInt(ch);
                }
                resultList.add(intBlock);
            }
        } catch (Exception e) {
            System.out.println("readEncodedBlocks error!");
        }

        return resultList;
    }

    private static void writeCorrectedBlocks(String filepath, List<int[]> codes) {
        filepath += "corrected_text.txt";

        StringBuilder result = new StringBuilder();
        for (int[] block : codes) {
            for (int j : block) {
                result.append(j);
            }
            result.append(" ");
        }


        try (Writer writer = new OutputStreamWriter(new FileOutputStream(filepath))) {
            writer.write(result.toString());
            System.out.println("Corrected string written");
        } catch (Exception e) {
            System.out.println("Cannot write corrected string!");
        }
    }

}
