package org.example.flash;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.Charset;

public class FlashDriveSerialChecker {

    private static final String EXPECTED_SERIAL_NUMBER = "BC39-2465";

    private static final String FLASH_DRIVE_PATH = "F:";

    public static void main(String[] args) {
        try {
            String serialNumber = getFlashDriveSerialNumber(FLASH_DRIVE_PATH);
            if (serialNumber != null) {
                System.out.println("Серийный номер флешки: " + serialNumber);
                if (serialNumber.equals(EXPECTED_SERIAL_NUMBER)) {
                    System.out.println("Флешка с правильным серийным номером подключена.");
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

    // Метод для получения серийного номера флешки
    public static String getFlashDriveSerialNumber(String driveLetter) throws IOException {
        Process process = new ProcessBuilder("cmd", "/c", "vol", driveLetter).start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(),  Charset.forName("CP866")));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("Серийный номер тома")) {
                return line.split(":")[1].trim();
            }
        }
        return null;
    }
}
