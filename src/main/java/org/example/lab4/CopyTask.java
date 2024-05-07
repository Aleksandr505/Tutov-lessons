package org.example.lab4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class CopyTask implements Runnable {
    private final List<Path> files;
    private final String destinationDirectory;
    private final String sourceDirectory;

    public CopyTask(List<Path> files, String destinationDirectory, String sourceDirectory) {
        this.files = files;
        this.destinationDirectory = destinationDirectory;
        this.sourceDirectory = sourceDirectory;
    }

    @Override
    public void run() {
        for (int i = 0; i < files.size(); i++) {
            Path destination = Paths.get(destinationDirectory, files.get(i).toString()
                    .substring(sourceDirectory.length()));
            try {
                Files.copy(files.get(i), destination);
            } catch (IOException e) {
                i--;
            }
        }
    }
}

