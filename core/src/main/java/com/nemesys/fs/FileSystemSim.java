package com.nemesys.fs;

import java.util.List;
import java.util.stream.Collectors;

public final class FileSystemSim {

    private final Directory root = buildSampleTree();
    private Directory cwd = root.sub("users");
    private final String drive = "C:";

    /* public API */
    public List<String> ls() {
        return cwd.getDirs().stream().map(d -> d.name + "/").collect(Collectors.toList()).stream().sorted().collect(Collectors.toList());
    }

    public boolean cd(String path) {
        if (path.equals("..") && cwd != root) {       // subir
            cwd = findParent(root, cwd);
            return true;
        }
        Directory d = cwd.sub(path);
        if (d != null) {
            cwd = d;
            return true;
        }
        return false;
    }

    public void toRoot() { cwd = root; }

    public String cat(String fileName) {
        VirtualFile f = cwd.file(fileName);
        return f == null ? null : f.content;
    }

    public String pwd() {
        String rel = getPath(root, cwd, "");
        if (rel.equals("/")) rel = "";      // raíz → cadena vacía
        return drive + rel.replace("/", "\\");  // convierte / en \
    }

    /* helpers ----------------------------------------------------------------*/
    private static Directory findParent(Directory current, Directory target) {
        if (current.getDirs().contains(target)) return current;
        for (Directory d : current.getDirs())
            if (findParent(d, target) != null) return d;
        return null;
    }

    private static String getPath(Directory base, Directory dir, String acc) {
        if (dir == base) return "/" + acc;
        Directory parent = findParent(base, dir);
        return getPath(base, parent, dir.name + "/" + acc);
    }

    private Directory buildSampleTree() {
        Directory root = new Directory("");
        Directory david = root.dir("users").dir("david");
        david.dir("docs").file("README", "txt", "Bienvenido a NEMESYS.");
        david.dir("pictures").file("wallpaper", "png", "<binary>");
        root.dir("system").dir("logs").file("boot", "log", "Boot OK.");
        return root;
    }
}
