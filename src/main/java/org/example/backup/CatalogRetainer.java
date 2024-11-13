package org.example.backup;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class CatalogRetainer {

    public static void main(String[] args) {
        Path directory = Paths.get("/Users/aleksandr/Documents/repos/university/lessons/src/main/resources/backup");

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            registerAllDirectories(directory, watchService);

            while (true) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Отслеживание было прервано.");
                    break;
                }

                // Проходим по каждому событию, зафиксированному для каталога
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    Path fileName = (Path) event.context();
                    Path fullPath = ((Path) key.watchable()).resolve(fileName);

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        if (Files.isDirectory(fullPath)) {
                            registerNewDirectory(fullPath, watchService);
                        } else {
                            System.out.println("Файл создан: " + fullPath);
                        }
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        if (Files.isDirectory(fullPath)) {
                            System.out.println("Директория удалена: " + fullPath);
                        } else {
                            System.out.println("Файл удалён: " + fullPath);
                        }
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        if (!Files.isDirectory(fullPath)) {
                            System.out.println("Файл изменён: " + fullPath);
                        }
                    } else {
                        System.out.println("Не удалось классифицировать изменение: " + fullPath);
                    }
                }

                // Проверяем ключ, если он более не действителен, завершаем цикл
                boolean valid = key.reset();
                if (!valid) {
                    System.out.println("Отслеживаемая директория удалена");
                    key.cancel();
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при доступе к каталогу: " + e.getMessage());
        }
    }

    // Рекурсивная функция для регистрации всех поддиректорий
    private static void registerAllDirectories(Path start, WatchService watchService) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                if (dir.equals(start)) {
                    System.out.println("Отслеживание изменений в каталоге: " + dir);
                } else {
                    System.out.println("Отслеживание изменений в поддиректории: " + dir);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void registerNewDirectory(Path newDir, WatchService watchService) throws IOException {
        newDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

        System.out.println("Отслеживание изменений в новом каталоге: " + newDir);
    }

}
