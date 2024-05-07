package org.example.lab4;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        long time = System.currentTimeMillis();
        String sourceDirectory = "/home/aleksandr/mnt/share/DISK-T";
        String directoryCopy = "copy";
        int threadsCount = 8;

        Stream<Path> paths = Files.walk(Paths.get(sourceDirectory));
        List<Path> pathsList = paths.collect(Collectors.toList());
        List<List<Path>> parts = partitionList(pathsList, threadsCount);

        parts.forEach(path -> {
            Thread thread = new Thread(new CopyTask(path, directoryCopy, sourceDirectory));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(System.currentTimeMillis() - time);
    }

    private static <T> List<List<T>> partitionList(List<T> list, int numPartitions) {
        int partitionSize = (int) Math.ceil((double) list.size() / numPartitions);
        List<List<T>> partitions = new ArrayList<>(numPartitions);
        for (int i = 0; i < list.size(); i += partitionSize) {
            partitions.add(list.subList(i, Math.min(i + partitionSize, list.size())));
        }
        return partitions;
    }
}
