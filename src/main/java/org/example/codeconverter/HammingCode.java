package org.example.codeconverter;

import java.util.ArrayList;
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
            System.out.println("Ошибок нет.");
        } else if (errorPosition > 0 && errorPosition <= 7) {
            // Исправляем одну ошибку
            System.out.println("Обнаружена ошибка в позиции: " + errorPosition);
            block[errorPosition - 1] ^= 1; // Инвертируем бит в найденной позиции
            System.out.println("Ошибка исправлена.");
        } else {
            // Если синдромы не соответствуют положению одной ошибки, сообщаем о возможной двойной ошибке
            System.out.println("Обнаружено больше одной ошибки в блоке. Исправление невозможно.");
        }

        return block;
    }

    public static void main(String[] args) {
        String input = "Hello";
        List<int[]> encodedText = encodeText(input);

        // Выводим закодированные блоки
        for (int[] block : encodedText) {
            for (int bit : block) {
                System.out.print(bit);
            }
            System.out.println();
        }


        // Пример блока с одной ошибкой
        List<int[]> blocksWithErrors = new ArrayList<>();
        blocksWithErrors.add(new int[]{1, 1, 1, 1, 0, 0, 0}); // Одна ошибка
        blocksWithErrors.add(new int[]{0, 1, 1, 1, 1, 0, 0}); // Две ошибки

        for (int[] block : blocksWithErrors) {
            System.out.println("До исправления:");
            for (int bit : block) {
                System.out.print(bit);
            }
            System.out.println();

            int[] correctedBlock = correctErrors(block);

            System.out.println("После исправления:");
            for (int bit : correctedBlock) {
                System.out.print(bit);
            }
            System.out.println("\n");
        }
    }

}
