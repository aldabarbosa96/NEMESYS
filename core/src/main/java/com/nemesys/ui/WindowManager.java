package com.nemesys.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
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
    // Ahora guardamos Buttons para poder usar ImageTextButton
    private final Map<BaseWindow, Button> buttons = new HashMap<>();

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
        // Re-activar ventana si ya existe (excepto editor)
        if (type != AppType.TEXT_EDITOR) {
            for (BaseWindow w : openWindows) {
                boolean match = (type == AppType.TERMINAL && w instanceof TerminalWindow) || (type == AppType.FILE_EXPLORER && w instanceof FileExplorerWindow) || (type == AppType.RECYCLE_BIN && w instanceof RecycleBinWindow);
                if (match) {
                    w.setVisible(true);
                    w.toFront();
                    stage.setKeyboardFocus(w);
                    Button tb = buttons.get(w);
                    if (tb != null) tb.setChecked(false);
                    return;
                }
            }
        }

        // Crear nueva
        BaseWindow w = create(type, filePath);
        openWindows.add(w);
        stage.addActor(w);

        Button btn = getTaskBarButton(w);
        buttons.put(w, btn);
        refreshTaskbar();
    }

    public void close(BaseWindow w) {
        if (openWindows.remove(w)) w.remove();
        Button btn = buttons.remove(w);
        if (btn != null) btn.remove();
        refreshTaskbar();
    }

    public void minimize(BaseWindow w) {
        w.setVisible(false);
        Button btn = buttons.get(w);
        if (btn != null) btn.setChecked(true);
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
        Button btn = getTaskBarButton(w);
        buttons.put(w, btn);
        refreshTaskbar();
    }

    // ────────────────────────────────────────────────────────────

    /**
     * Ahora devolvemos Button para admitir ImageTextButton
     */
    private Button getTaskBarButton(BaseWindow w) {
        // Clonamos el estilo toggle existente
        TextButton.TextButtonStyle base = skin.get("win95-toggle", TextButton.TextButtonStyle.class);
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle(base);

        // Asignamos icono según tipo de ventana
        String iconName = iconNameFor(w);
        if (skin.has(iconName, Drawable.class)) {
            Drawable ic = skin.getDrawable(iconName);
            style.imageUp = ic;  // ventana activa
            style.imageChecked = ic;  // ventana minimizada
            style.imageDown = ic;  // clic momentáneo
        }

        ImageTextButton btn = new ImageTextButton(w.getWindowTitle(), style);
        btn.pad(2f, 8f, 2f, 8f);
        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (btn.isChecked()) {
                    w.setVisible(false);
                    stage.setKeyboardFocus(null);
                } else {
                    w.setVisible(true);
                    w.toFront();
                    stage.setKeyboardFocus(w);
                }
            }
        });
        return btn;
    }

    private void refreshTaskbar() {
        taskbar.clearChildren();
        boolean first = true;
        for (BaseWindow w : openWindows) {
            Actor btn = buttons.get(w);
            if (first) {
                taskbar.add(btn).padLeft(8f).padRight(4f);
                first = false;
            } else {
                taskbar.add(btn).padRight(4f);
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
                String f = (path != null && !path.trim().isEmpty()) ? path : null;
                return new TextEditorWindow(skin, this, fs, f);
            case RECYCLE_BIN:
                return new RecycleBinWindow(skin, this, recycleFs);
            default:
                throw new IllegalArgumentException("Tipo no soportado: " + type);
        }
    }

    private void registerToggleStyle() {
        if (skin.has("win95-toggle", TextButton.TextButtonStyle.class)) return;

        TextButton.TextButtonStyle toggle = new TextButton.TextButtonStyle();
        toggle.up = skin.getDrawable("btn-checked-light");
        toggle.down = skin.getDrawable("btn-down");
        toggle.checked = skin.getDrawable("btn-up");
        toggle.checkedOver = skin.getDrawable("btn-up");
        toggle.font = skin.getFont("font-win95");
        toggle.fontColor = Color.BLACK;
        toggle.downFontColor = Color.BLACK;
        toggle.checkedFontColor = Color.BLACK;
        skin.add("win95-toggle", toggle);
    }

    /**
     * Nombre del drawable según la clase real de la ventana
     */
    private String iconNameFor(BaseWindow w) {
        if (w instanceof TerminalWindow) return "icon-terminal";
        if (w instanceof FileExplorerWindow) return "icon-explorer";
        if (w instanceof TextEditorWindow) return "icon-editor";
        if (w instanceof RecycleBinWindow) return "trash-small";
        return "icon-logo";
    }
}
