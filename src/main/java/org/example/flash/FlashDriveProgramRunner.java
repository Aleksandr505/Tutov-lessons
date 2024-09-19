package org.example.flash;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class FlashDriveProgramRunner {

    private static final String EXPECTED_SERIAL_NUMBER = "BC39-2465";

    private static final String FLASH_DRIVE_PATH = "F:";

    private static final Path programPath = Path.of("F:\\signature-1.0-jar-with-dependencies.jar");

    public static void main(String[] args) {
        try {
            String serialNumber = FlashDriveSerialChecker.getFlashDriveSerialNumber(FLASH_DRIVE_PATH);
            if (serialNumber != null) {
                System.out.println("Серийный номер флешки: " + serialNumber);
                if (serialNumber.equals(EXPECTED_SERIAL_NUMBER)) {
                    System.out.println("Флешка с правильным серийным номером подключена.");
                    runJavaProgramFromFlashDrive();
                } else {
                    System.out.println("Неправильный серийный номер флешки.");
                }
            } else {
                System.out.println("Не удалось получить серийный номер.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для запуска Java-программы с флешки
    private static void runJavaProgramFromFlashDrive() {
        try {
            String filename = programPath.getFileName().toString();
            if (Files.exists(programPath)) {
                System.out.println("Запускаем программу с флешки: " + programPath);

                if (filename.endsWith(".jar")) {
                    ProcessBuilder pb = new ProcessBuilder("java", "-jar", programPath.toAbsolutePath().toString());
                    pb.inheritIO();
                    Process process = pb.start();
                    process.waitFor();
                } else {
                    System.out.println("OOPS!");
                }
            } else {
                System.out.println("Программа не найдена по пути: " + programPath);
                checkDirectory();
            }
        } catch (IOException e) {
            System.err.println("Не удалось запустить программу с флешки: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkDirectory() {
        File flashDriveDirectory = new File(FLASH_DRIVE_PATH);
        String[] files = flashDriveDirectory.list();

        if (files != null) {
            for (String file : files) {
                System.out.println("Файл на флешке: " + file);
                Path path = Path.of("F:\\" + file);
                Path aaa = programPath;
                if (Files.exists(path)) {
                    System.out.println("File is exist " + path.toString());

                    String p1 = path.toString();
                    String p2 = aaa.toString();
                    if (p1.equals(p2)) {
                        System.out.println("NICE!!!");
                    } else {
                        System.out.println("WHAT!!!");
                    }
                } else {
                    System.out.println("File doesnt exist " + path.toString());
                }
            }
        } else {
            System.out.println("Не удалось прочитать содержимое директории.");
        }

    }
}

