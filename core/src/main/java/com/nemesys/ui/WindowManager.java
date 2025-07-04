// File: core/src/main/java/com/nemesys/ui/WindowManager.java
package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemesys.fs.FileSystemSim;
import com.nemesys.fs.VirtualFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WindowManager {

    /**
     * Tipos de “apps”
     */
    public enum AppType {TERMINAL, FILE_EXPLORER, TEXT_EDITOR, RECYCLE_BIN}

    private final Stage stage;
    private final Skin skin;
    private final Table taskbar;

    private final List<BaseWindow> openWindows = new ArrayList<>();
    private final Map<BaseWindow, TextButton> buttons = new HashMap<>();

    /**
     * FS “real”
     */
    private final FileSystemSim fs = new FileSystemSim();
    /**
     * FS de la papelera
     */
    private final FileSystemSim recycleFs = new FileSystemSim();
    /**
     * Mapa fichero → ruta original
     */
    private final Map<String, String> recycleMap = new HashMap<>();

    public WindowManager(Stage stage, Skin skin, Table taskbar) {
        this.stage = stage;
        this.skin = skin;
        this.taskbar = taskbar;
        registerToggleStyle();
    }

    public void open(AppType type) {
        open(type, null);
    }

    public void open(AppType type, String filePath) {
        // Si ya hay una ventana no-editor, la traemos al frente
        if (type != AppType.TEXT_EDITOR) {
            for (BaseWindow w : openWindows) {
                boolean match = (type == AppType.TERMINAL && w instanceof TerminalWindow) || (type == AppType.FILE_EXPLORER && w instanceof FileExplorerWindow) || (type == AppType.RECYCLE_BIN && w instanceof RecycleBinWindow);
                if (match) {
                    w.setVisible(true);
                    w.toFront();
                    stage.setKeyboardFocus(w);
                    TextButton tb = buttons.get(w);
                    if (tb != null) tb.setChecked(false);
                    return;
                }
            }
        }

        // Crear nueva
        BaseWindow w = create(type, filePath);
        openWindows.add(w);
        stage.addActor(w);

        TextButton b = getTaskBarButton(w);
        buttons.put(w, b);
        refreshTaskbar();
    }

    public void close(BaseWindow w) {
        if (openWindows.remove(w)) w.remove();
        TextButton b = buttons.remove(w);
        if (b != null) b.remove();
        refreshTaskbar();
    }

    public void minimize(BaseWindow w) {
        w.setVisible(false);
        TextButton b = buttons.get(w);
        if (b != null) b.setChecked(true);
        stage.setKeyboardFocus(null);
    }

    /**
     * Mueve un archivo de un FS origen a la papelera
     */
    public void moveToRecycle(FileSystemSim sourceFs, String name) {
        VirtualFile vf = sourceFs.removeFile(name);
        if (vf != null) {
            String originalPath = sourceFs.pwd();
            recycleMap.put(name, originalPath);
            // Lo colocamos en la papelera
            recycleFs.toRoot();
            recycleFs.overwrite(name, vf.content);
        }
    }

    /**
     * Restaura un archivo a su ruta original
     */
    public void restoreFromRecycle(String name) {
        VirtualFile vf = recycleFs.removeFile(name);
        if (vf != null) {
            // Recuperamos ruta original
            String originalPath = recycleMap.remove(name);
            if (originalPath != null) {
                fs.cdAbsolute(originalPath);
            } else {
                fs.toRoot();
            }
            fs.overwrite(name, vf.content);
        }
    }

    /**
     * Abre un editor desde terminal (“nano”)
     */
    public void openEditor(String filePath, FileSystemSim fsRef) {
        BaseWindow w = new TextEditorWindow(skin, this, fsRef, filePath);
        openWindows.add(w);
        stage.addActor(w);
        TextButton b = getTaskBarButton(w);
        buttons.put(w, b);
        refreshTaskbar();
    }

    /*────────────────────────────────────────────────────────────────*/

    private TextButton getTaskBarButton(BaseWindow w) {
        TextButton b = new TextButton(w.getWindowTitle(), skin, "win95-toggle");
        b.pad(2f, 8f, 2f, 8f);
        b.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (b.isChecked()) {
                    w.setVisible(false);
                    stage.setKeyboardFocus(null);
                } else {
                    w.setVisible(true);
                    w.toFront();
                    stage.setKeyboardFocus(w);
                }
            }
        });
        return b;
    }

    private void refreshTaskbar() {
        taskbar.clearChildren();
        boolean first = true;
        for (BaseWindow w : openWindows) {
            TextButton b = buttons.get(w);
            if (first) {
                taskbar.add(b).padLeft(8f).padRight(4f);
                first = false;
            } else {
                taskbar.add(b).padRight(4f);
            }
        }
    }

    private BaseWindow create(AppType type, String path) {
        switch (type) {
            case TERMINAL:
                return new TerminalWindow(skin, this, new FileSystemSim());
            case FILE_EXPLORER:
                return new FileExplorerWindow(skin, this, new FileSystemSim());
            case TEXT_EDITOR:
                String file = (path != null && !path.trim().isEmpty()) ? path : null;
                return new TextEditorWindow(skin, this, fs, file);
            case RECYCLE_BIN:
                return new RecycleBinWindow(skin, this, recycleFs);
            default:
                throw new IllegalArgumentException("Tipo no soportado: " + type);
        }
    }

    private void registerToggleStyle() {
        if (skin.has("win95-toggle", TextButtonStyle.class)) return;
        TextButtonStyle t = new TextButtonStyle(skin.getDrawable("face"),   // up
            skin.getDrawable("shadow"), // down
            skin.getDrawable("shadow"), // checked
            skin.getFont("font-win95"));
        t.fontColor = com.badlogic.gdx.graphics.Color.BLACK;
        t.downFontColor = com.badlogic.gdx.graphics.Color.BLACK;
        t.checkedFontColor = com.badlogic.gdx.graphics.Color.BLACK;
        skin.add("win95-toggle", t);
    }
}
