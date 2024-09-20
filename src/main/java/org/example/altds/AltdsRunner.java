package org.example.altds;

import java.io.*;

public class AltdsRunner {

    public static void main(String[] args) {
        try {
            String filePath = "D:\\repos\\lessons\\src\\main\\java\\org\\example\\altds\\file-with-altds.txt";
            String adsName = "secret";
            String data = "You can see this with ADS.";

            // Запись данных в альтернативный поток
            writeToADS(filePath, adsName, data);

            // Чтение данных из альтернативного потока
            readFromADS(filePath, adsName);
        } catch (Exception e) {
            e.printStackTrace();
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("\n /////////////////////////// \n");

            System.out.println("Successfully read ADS from " + filePath);
        } catch (IOException e) {
            System.out.println("Failed to read ADS from " + filePath);
            e.printStackTrace();
        }
    }

}
