package com.nemesys.fs;

import java.util.*;

/**
 * Representa un directorio virtual, con subdirectorios y archivos.
 */
public final class Directory {
    public final String name;
    private final Map<String, Directory> subDirs = new LinkedHashMap<>();
    private final Map<String, VirtualFile> files = new LinkedHashMap<>();

    public Directory(String name) {
        this.name = name;
    }

    /* getters */
    public Collection<Directory> getDirs() {
        return subDirs.values();
    }

    public Collection<VirtualFile> getFiles() {
        return files.values();
    }

    /* builders */
    public Directory dir(String name) {
        return subDirs.computeIfAbsent(name, Directory::new);
    }

    public void file(String fname, String ext, String content) {
        files.put(fname + "." + ext, new VirtualFile(fname, ext, content));
    }

    public VirtualFile removeFile(String fullname) {
        return files.remove(fullname);
    }

    /* acceso */
    public Directory sub(String name) {
        return subDirs.get(name);
    }

    public VirtualFile file(String fullname) {
        return files.get(fullname);
    }
}
