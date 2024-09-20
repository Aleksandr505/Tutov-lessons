package org.example.altds;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class AltdsRunner {

    public static void main(String[] args) {
        try {
            String baseFilePath = "D:\\repos\\lessons\\src\\main\\java\\org\\example\\altds\\";
            String adsName = "secret";
            String data = "You can see this with ADS.";

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Please enter your file path for directory: " + baseFilePath + "\n");
            String readLine = reader.readLine();
            String filePath = baseFilePath + readLine;
            writeOptions();
            while (true) {
                String option = reader.readLine();
                if (option.equals("0")) {
                    readFromADS(filePath, adsName);
                } else if (option.equals("1")) {
                    createEmptyADS(filePath, adsName);
                } else if (option.equals("2")) {
                    writeToADS(filePath, adsName, data);
                } else if (option.equals("3")) {
                    clearADS(filePath, adsName);
                } else if (option.equals("4")) {
                    deleteFileWithADS(filePath);
                } else if (option.equals("5")) {
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
        System.out.println("Read from ADS - 0 ");
        System.out.println("Create ADS - 1 ");
        System.out.println("Add data to ADS - 2 ");
        System.out.println("Clear data from ADS - 3 ");
        System.out.println("Delete ADS - 4 ");
        System.out.println("Exit - 5 ");
    }

    // Метод для создания пустого альтернативного потока
    public static void createEmptyADS(String filePath, String adsName) {
        try (FileOutputStream fos = new FileOutputStream(filePath + ":" + adsName)) {
            System.out.println("Create empty ADS ");
        } catch (IOException e) {
            System.out.println("Failed create empty ADS");
            e.printStackTrace();
        }
    }

    public static void clearADS(String filePath, String adsName) {
        try (FileOutputStream fos = new FileOutputStream(filePath + ":" + adsName)) {
            // Ничего не записываем, просто очищаем поток
            System.out.println("ADS was cleared");
        } catch (IOException e) {
            System.out.println("ADS clear error");
            e.printStackTrace();
        }
    }

    public static void deleteFileWithADS(String filePath) {
        File file = new File(filePath);
        if (file.delete()) {
            System.out.println("ADS was deleted");
        } else {
            System.out.println("Failed to delete ADS");
        }
    }

    // Метод для записи данных в альтернативный поток
    public static void writeToADS(String filePath, String adsName, String data) throws Exception {
        String command = String.format("cmd /c echo %s > %s:%s", data, filePath, adsName);
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();

        if (process.exitValue() == 0) {
            System.out.println("Successfully written ADS to " + filePath);
        } else {
            System.out.println("Failed to write ADS to " + filePath);
        }
    }

    // Метод для чтения данных из альтернативного потока
    public static void readFromADS(String filePath, String adsName) throws Exception {
        try (FileInputStream fis = new FileInputStream(filePath + ":" + adsName)) {
            System.out.println("\n /////////////////////////// \n");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("\n /////////////////////////// \n");

            System.out.println("Successfully read ADS from " + filePath);
        } catch (IOException e) {
            System.out.println("Failed to read ADS from " + filePath);
        }
    }

}
