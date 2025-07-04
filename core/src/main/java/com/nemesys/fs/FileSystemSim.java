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
        return drive + rel.replace("/", "\\");  // / → \
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

    /**
     * Elimina un archivo del directorio actual y lo devuelve.
     */
    public VirtualFile removeFile(String fullName) {
        VirtualFile f = cwd.file(fullName);
        if (f == null) return null;
        cwd.removeFile(fullName);
        return f;
    }

    /**
     * Ejecuta comandos, incluido ahora "rm".
     */
    public String run(String cmd, String arg) {
        switch (cmd) {
            case "dir":
            case "ls":
                return String.join("  ", ls());

            case "cd":
                if (arg.isEmpty()) return "usage: cd <dir>";
                return cd(arg) ? null : "cd: no such dir: " + arg;

            case "pwd":
                return pwd();

            case "mkdir":
                if (arg.isEmpty()) return "usage: mkdir <dir>";
                return mkdir(arg) ? null : "mkdir: cannot create '" + arg + "': exists";

            case "touch":
                if (arg.isEmpty()) return "usage: touch <filename>";
                touch(arg);
                return null;

            case "cat":
                if (arg.isEmpty()) return "usage: cat <file>";
                String txt = cat(arg);
                return (txt == null) ? "cat: file not found: " + arg : txt;

            case "rm":
                if (arg.isEmpty()) return "usage: rm <file>";
                return removeFile(arg) != null ? null : "rm: cannot remove '" + arg + "'";

            case "du":
                return "24K .\\users\\david\\docs\n12K .\\users\\david\\pictures"; // stub

            case "df":
                return "Filesystem   Size  Used Avail Use% Mounted on\n" + "C:           512M  412M  100M  81% /";               // stub

            case "tree":
                return buildTree("");

            default:
                return null;
        }
    }

    private String buildTree(String indent) {
        StringBuilder sb = new StringBuilder();
        for (String item : ls()) {
            sb.append(indent).append(item).append('\n');
            if (item.endsWith("/")) {
                cd(item.substring(0, item.length() - 1));
                sb.append(buildTree(indent + "  "));
                cd("..");
            }
        }
        return sb.toString();
    }

    /* sobre-escribe o crea un archivo de texto */
    public void overwrite(String fullPath, String content) {
        int slash = Math.max(fullPath.lastIndexOf('/'), fullPath.lastIndexOf('\\'));
        String dir = (slash >= 0) ? fullPath.substring(0, slash) : "";
        String file = (slash >= 0) ? fullPath.substring(slash + 1) : fullPath;

        Directory saveCwd = cwd;          // recuerda dónde estabas
        if (!dir.isEmpty()) {             // navega a dir destino
            String[] parts = dir.split("[/\\\\]");
            for (String p : parts) {
                if (p.isEmpty()) continue;
                cwd = cwd.dir(p);          // crea subdir si falta
            }
        }

        /* nombre + ext */
        int dot = file.lastIndexOf('.');
        String n = (dot > 0) ? file.substring(0, dot) : file;
        String e = (dot > 0) ? file.substring(dot + 1) : "txt";
        cwd.file(n, e, content);

        cwd = saveCwd;     // vuelve a cwd original
    }

    /* helpers ----------------------------------------------------------------*/

    private static Directory findParent(Directory current, Directory target) {
        if (current.getDirs().contains(target)) return current;
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
