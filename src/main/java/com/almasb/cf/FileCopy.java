package com.almasb.cf;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileCopy {

    private final String name;
    private final long size;

    private List<Path> copies = new ArrayList<>();

    public FileCopy(String name, long size) {
        this.name = name;
        this.size = size;
    }

    public void addCopy(Path file) {
        copies.add(file);
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public List<Path> getCopies() {
        return new ArrayList<>(copies);
    }

    public int getNumberOfCopies() {
        return copies.size();
    }
}
