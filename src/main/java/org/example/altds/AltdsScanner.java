package org.example.altds;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AltdsScanner {

    public static void main(String[] args) {
        try {
            String baseFilePath = "D:\\repos\\lessons\\src\\main\\resources\\altds";
            System.out.println("Your base directory is: " + baseFilePath);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            writeOptions();
            while (true) {
                String option = reader.readLine();
                if (option.equals("0")) {
                    System.out.println("Please enter new base directory path: \n");
                    baseFilePath = reader.readLine();
                } else if (option.equals("1")) {
                    List<String> allFilesWithADS = findFilesWithAltdsRecursively(baseFilePath);
                    if (allFilesWithADS.isEmpty()) {
                        System.out.println("Нет файлов с альтернативными потоками данных.");
                    } else {
                        System.out.println("Файлы с альтернативными потоками данных:");
                        for (String file : allFilesWithADS) {
                            System.out.println(file);
                        }
                    }
                } else if (option.equals("2")) {
                    return;
                } else {
                    System.out.println("Unknown option, try again: " + option);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeOptions() {
        System.out.println("Please enter option: ");
        System.out.println("Change base directory    : 0 ");
        System.out.println("Scan directory           : 1 ");
        System.out.println("Exit                     : 2 ");
    }

    public static List<String> findFilesWithAltdsInDirectory(String directoryPath) {
        List<String> filesWithADS = new ArrayList<>();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "dir /r \"" + directoryPath + "\"");
            processBuilder.directory(new File(directoryPath));
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(":$DATA")) {
                    filesWithADS.add(line.trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filesWithADS;
    }

    // Рекурсивный метод для обхода директорий и поиска файлов с альтернативными потоками
    public static List<String> findFilesWithAltdsRecursively(String directoryPath) {
        List<String> allFilesWithADS = new ArrayList<>();

        // Получение файлов и директорий в текущей директории
        File directory = new File(directoryPath);
        File[] filesAndDirs = directory.listFiles();

        if (filesAndDirs != null) {
            for (File file : filesAndDirs) {
                if (file.isDirectory()) {
                    // Рекурсивный вызов для поддиректорий
                    allFilesWithADS.addAll(findFilesWithAltdsRecursively(file.getAbsolutePath()));
                }
            }

            // Поиск файлов с Altds в текущей директории
            allFilesWithADS.addAll(findFilesWithAltdsInDirectory(directoryPath));
        }

        return allFilesWithADS;
    }

}

