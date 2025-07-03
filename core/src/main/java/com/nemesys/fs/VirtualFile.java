package com.nemesys.fs;

public final class VirtualFile {
    public final String  name;
    public final String  extension;   // "txt", "png"...
    public final String  content;     // texto plano por ahora

    public VirtualFile(String name, String extension, String content) {
        this.name = name;
        this.extension = extension;
        this.content = content;
    }

    @Override public String toString() { return name + "." + extension; }
}
