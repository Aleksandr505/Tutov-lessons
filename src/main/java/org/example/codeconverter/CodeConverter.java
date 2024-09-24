package org.example.codeconverter;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CodeConverter {

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
                    detectFileEncoding(filePath);
                } else if (option.equals("1")) {
                    convertFileEncoding(filePath, "CP866");
                } else if (option.equals("2")) {
                    convertFileEncoding(filePath, "windows-1251");
                } else if (option.equals("3")) {
                    convertFileEncoding(filePath, "ISO-8859-5");
                } else if (option.equals("4")) {
                    convertFileEncoding(filePath, "C310007");
                } else if (option.equals("5")) {
                    convertFileEncoding(filePath, "UTF-8");
                } else if (option.equals("6")) {
                    System.out.println("Please enter your file path for directory: " + baseFilePath + "\n");
                    readLine = reader.readLine();
                    filePath = baseFilePath + readLine;
                    writeOptions();
                } else if (option.equals("7")) {
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
        System.out.println("Please enter option: ");
        System.out.println("Get encoding type               : 0 ");
        System.out.println("Convert with CP866              : 1 ");
        System.out.println("Convert with windows-1251       : 2 ");
        System.out.println("Convert with ISO-8859-5         : 3 ");
        System.out.println("Convert with C310007            : 4 ");
        System.out.println("Convert with UTF-8              : 5 ");
        System.out.println("Change filepath                 : 6 ");
        System.out.println("Exit                            : 7 ");
    }

    // Метод для определения кодировки файла
    public static String detectFileEncoding(String filePath) throws IOException {
        byte[] buf = new byte[4096];
        try (FileInputStream fis = new FileInputStream(filePath)) {
            UniversalDetector detector = new UniversalDetector(null);

            int read;
            while ((read = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, read);
            }

            detector.dataEnd();
            String encoding = detector.getDetectedCharset();
            detector.reset();

            if (encoding != null) {
                System.out.println("Определена кодировка: " + encoding);
            } else {
                System.out.println("Кодировка не определена.");
                return null;
            }
            return encoding;
        }
    }

    // Метод для изменения кодировки файла
    public static void convertFileEncoding(String filePath, String targetEncoding) throws IOException {
        // Определяем исходную кодировку
        String sourceEncoding = detectFileEncoding(filePath);
        if (sourceEncoding == null) {
            System.out.println("sourceEncoding = null");
            return;
        }

        String convertedFilepath = filePath.replace(".txt", "_") + targetEncoding + ".txt";

        // Читаем содержимое файла в исходной кодировке
        byte[] encoded = Files.readAllBytes(Paths.get(filePath));
        String content = new String(encoded, Charset.forName(sourceEncoding));

        // Записываем содержимое файла в целевой кодировке
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(convertedFilepath), targetEncoding)) {
            writer.write(content);
            System.out.println("Файл перекодирован в " + targetEncoding + " и сохранён как " + convertedFilepath);
        } catch (Exception e) {
            System.out.println("Не удалось записать файл в кодировке " + targetEncoding);
        }
    }

}
