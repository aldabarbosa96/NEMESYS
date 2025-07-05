package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.nemesys.fs.FileSystemSim;
import com.nemesys.fs.VirtualFile;
import com.nemesys.screens.DesktopScreen;

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
    private final DesktopScreen desktopScreen;   // ← REFERENCIA AL SCREEN

    private final List<BaseWindow> openWindows = new ArrayList<>();
    private final Map<BaseWindow, Button> buttons = new HashMap<>();

    /**
     * FS “real” del escritorio
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

    /**
     * Ahora recibimos también el DesktopScreen para notificarle cambios.
     */
    public WindowManager(Stage stage, Skin skin, Table taskbar, DesktopScreen desktopScreen) {
        this.stage = stage;
        this.skin = skin;
        this.taskbar = taskbar;
        this.desktopScreen = desktopScreen;
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
     * Mueve un archivo de un FS origen a la papelera,
     * y notifica al DesktopScreen para refrescar el escritorio.
     */
    public void moveToRecycle(FileSystemSim sourceFs, String name) {
        VirtualFile vf = sourceFs.removeFile(name);
        if (vf != null) {
            String originalPath = sourceFs.pwd();
            recycleMap.put(name, originalPath);
            recycleFs.toRoot();
            recycleFs.overwrite(name, vf.content);

            // ——— NOTIFICAMOS AL ESCRITORIO ———
            desktopScreen.markDesktopDirty();
        }
    }

    /**
     * Restaura un archivo a su ruta original,
     * y notifica al DesktopScreen para refrescar.
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

            // ——— NOTIFICAMOS AL ESCRITORIO ———
            desktopScreen.markDesktopDirty();
        }
    }

    /**
     * Abre un editor desde terminal (“nano”) o dobles clic,
     * sin afectar al escritorio.
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

    private Button getTaskBarButton(BaseWindow w) {
        TextButton.TextButtonStyle base = skin.get("win95-toggle", TextButton.TextButtonStyle.class);
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle(base);

        String iconName = iconNameFor(w);
        if (skin.has(iconName, Drawable.class)) {
            Drawable ic = skin.getDrawable(iconName);
            style.imageUp = ic;
            style.imageChecked = ic;
            style.imageDown = ic;
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
            case TERMINAL: {
                FileSystemSim termFs = new FileSystemSim();
                termFs.toRoot();
                termFs.cd("Desktop");
                return new TerminalWindow(skin, this, termFs);
            }
            case FILE_EXPLORER: {
                FileSystemSim expFs = new FileSystemSim();
                expFs.toRoot();
                expFs.cd("Desktop");
                return new FileExplorerWindow(skin, this, expFs);
            }
            case TEXT_EDITOR: {
                FileSystemSim editFs = new FileSystemSim();
                editFs.toRoot();
                editFs.cd("Desktop");
                return new TextEditorWindow(skin, this, editFs, (path != null && !path.isEmpty()) ? path : null);
            }
            case RECYCLE_BIN:
                return new RecycleBinWindow(skin, this, new FileSystemSim());
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
        toggle.fontColor = com.badlogic.gdx.graphics.Color.BLACK;
        toggle.downFontColor = com.badlogic.gdx.graphics.Color.BLACK;
        toggle.checkedFontColor = com.badlogic.gdx.graphics.Color.BLACK;
        skin.add("win95-toggle", toggle);
    }

    private String iconNameFor(BaseWindow w) {
        if (w instanceof TerminalWindow) return "icon-terminal";
        if (w instanceof FileExplorerWindow) return "icon-explorer";
        if (w instanceof TextEditorWindow) return "icon-editor";
        if (w instanceof RecycleBinWindow) return "trash-small";
        return "icon-logo";
    }

    public FileSystemSim getFs() {
        return fs;
    }
    public void requestDesktopRefresh() {
        desktopScreen.markDesktopDirty();
    }
}
