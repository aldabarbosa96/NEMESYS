package com.nemesys.fs;

import java.util.List;
import java.util.stream.Collectors;

public final class FileSystemSim {

    private static final Directory ROOT = buildSampleTree();
    private Directory cwd = ROOT.sub("users");
    private final String drive = "C:";

    /* public API */
    public List<String> ls() {
        List<String> out = new java.util.ArrayList<>();

        // carpetas primero, con “/”
        out.addAll(cwd.getDirs().stream().map(d -> d.name + "/").collect(Collectors.toList()));

        // luego archivos
        out.addAll(cwd.getFiles().stream().map(VirtualFile::toString).collect(Collectors.toList()));

        java.util.Collections.sort(out);
        return out;
    }


    public boolean cd(String path) {
        if (path.equals("..") && cwd != ROOT) {       // subir
            cwd = findParent(ROOT, cwd);
            return true;
        }
        Directory d = cwd.sub(path);
        if (d != null) {
            cwd = d;
            return true;
        }
        return false;
    }

    public void toRoot() {
        cwd = ROOT;
    }

    public String cat(String fileName) {
        VirtualFile f = cwd.file(fileName);
        return f == null ? null : f.content;
    }

    public String pwd() {
        String rel = getPath(ROOT, cwd, "");
        if (rel.equals("/")) rel = "";      // raíz → cadena vacía
        return drive + rel.replace("/", "\\");  // convierte / en \
    }

    /* crea subdirectorio si no existe */
    public boolean mkdir(String name) {
        if (cwd.sub(name) != null) return false;   // ya existe
        cwd.dir(name);
        return true;
    }

    /* crea archivo de texto vacío o sobre-escribe */
    public void touch(String fullName) {
        String name = fullName;
        String ext = "txt";

        int dot = fullName.lastIndexOf('.');
        if (dot > 0 && dot < fullName.length() - 1) {   // hay extensión
            name = fullName.substring(0, dot);
            ext = fullName.substring(dot + 1);
        }
        cwd.file(name, ext, "");
    }


    /* helpers ----------------------------------------------------------------*/
    /* busca el padre real de target dentro del árbol ------------------------- */
    private static Directory findParent(Directory current, Directory target) {
        if (current.getDirs().contains(target)) return current;   // caso base
        for (Directory d : current.getDirs()) {
            Directory res = findParent(d, target);
            if (res != null) return res;
        }
        return null;
    }

    private static String getPath(Directory base, Directory dir, String acc) {
        if (dir == base) return "/" + acc;
        Directory parent = findParent(base, dir);
        return getPath(base, parent, dir.name + "/" + acc);
    }

    private static Directory buildSampleTree() {
        Directory root = new Directory("");
        Directory david = root.dir("users").dir("david");
        david.dir("docs").file("README", "txt", "Bienvenido a NEMESYS.");
        david.dir("pictures").file("wallpaper", "png", "<binary>");
        root.dir("system").dir("logs").file("boot", "log", "Boot OK.");
        return root;
    }
}
