package org.example.backup;

import java.io.IOException;
import java.nio.file.*;

public class CatalogRetainer {

    public static void main(String[] args) {
        Path directory = Paths.get("D:\\repos\\lessons\\src\\main\\resources\\backup");

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            System.out.println("Отслеживание изменений в каталоге: " + directory);

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

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        System.out.println("Файл создан: " + fileName);
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        System.out.println("Файл удалён: " + fileName);
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        System.out.println("Файл изменён: " + fileName);
                    } else {
                        System.out.println("Не удалось класифицировать изменение: " + fileName);
                    }
                }

                // Проверяем ключ, если он более не действителен, завершаем цикл
                boolean valid = key.reset();
                if (!valid) {
                    System.out.println("Ключ недействителен, прекращаем отслеживание.");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при доступе к каталогу: " + e.getMessage());
        }
    }

}
