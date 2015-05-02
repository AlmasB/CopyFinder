package com.almasb.cf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.application.HostServices;

public class Model {

    private final HostServices hostServices;

    public Model(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public enum SearchMode {
        TOP_LEVEL, RECURSIVE
    }

    public void openFileExplorer(Path file) {
        hostServices.showDocument(file.toAbsolutePath().toUri().toString());
    }

    public List<FileCopy> getCopies(Path dir, SearchMode mode) throws IOException {
        try (Stream<Path> paths = (
                mode == SearchMode.TOP_LEVEL
                ? Files.list(dir)
                : Files.walk(dir)
                )) {
            List<Path> files = paths
                    .filter(Model::nonZeroFile)
                    .collect(Collectors.toList());

            return getCopies(files);
        }
    }

    private List<FileCopy> getCopies(List<Path> files) throws IOException {
        List<FileCopy> copies = new ArrayList<>();

        boolean[] marked = new boolean[files.size()];

        for (int i = 0; i < files.size(); i++) {
            if (marked[i])
                continue;

            Path file1 = files.get(i);

            FileCopy copy = new FileCopy(file1.getFileName().toString(), Files.size(file1));
            copy.addCopy(file1);

            for (int j = i + 1; j < files.size(); j++) {
                if (marked[j])
                    continue;

                Path file2 = files.get(j);
                if (isSame(file1, file2)) {
                    copy.addCopy(file2);
                    marked[i] = true;
                    marked[j] = true;
                }
            }

            if (copy.getNumberOfCopies() > 1) {
                copies.add(copy);
            }
        }

        return copies;
    }

    private static boolean nonZeroFile(Path file) {
        try {
            return Files.isRegularFile(file) && Files.size(file) > 0;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static boolean isSame(Path file1, Path file2) throws IOException {
        if (Files.size(file1) != Files.size(file2))
            return false;

        return Arrays.equals(Files.readAllBytes(file1), Files.readAllBytes(file2));
    }
}
