package com.nemesys.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemesys.fs.FileSystemSim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WindowManager {

    public enum AppType {TERMINAL, FILE_EXPLORER, TEXT_EDITOR}

    private final Stage stage;
    private final Skin skin;
    private final Table taskbar;

    private final List<BaseWindow> openWindows = new ArrayList<>();
    private final Map<BaseWindow, TextButton> buttons = new HashMap<>();

    private final FileSystemSim fs = new FileSystemSim();

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
        if (type != AppType.TEXT_EDITOR) {
            for (BaseWindow w : openWindows) {
                boolean match = (type == AppType.TERMINAL && w instanceof TerminalWindow) || (type == AppType.FILE_EXPLORER && w instanceof FileExplorerWindow);
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

        BaseWindow w = create(type, filePath);
        openWindows.add(w);
        stage.addActor(w);

        TextButton b = new TextButton(w.getWindowTitle(), skin, "win95-toggle");
        // padding interno para que el texto respire
        b.pad(2f, 8f, 2f, 8f);
        b.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
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

        buttons.put(w, b);
        refreshTaskbar();
    }

    public void close(BaseWindow w) {
        if (openWindows.remove(w)) {
            w.remove();
        }
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

    private void refreshTaskbar() {
        taskbar.clearChildren();
        boolean first = true;
        for (BaseWindow w : openWindows) {
            TextButton b = buttons.get(w);
            if (first) {
                // mantenemos la separación izquierda del botón Inicio
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
            default:
                throw new IllegalArgumentException("Tipo no soportado: " + type);
        }
    }

    private void registerToggleStyle() {
        if (skin.has("win95-toggle", TextButtonStyle.class)) return;

        TextButtonStyle t = new TextButtonStyle(skin.getDrawable("face"),    // up
            skin.getDrawable("shadow"),  // down
            skin.getDrawable("shadow"),  // checked
            skin.getFont("font-win95"));
        t.fontColor = Color.BLACK;
        t.downFontColor = Color.BLACK;
        t.checkedFontColor = Color.BLACK;
        skin.add("win95-toggle", t);
    }

    public void openEditor(String filePath, FileSystemSim fsRef) {
        BaseWindow w = new TextEditorWindow(skin, this, fsRef, filePath);
        openWindows.add(w);
        stage.addActor(w);

        TextButton b = new TextButton(w.getWindowTitle(), skin, "win95-toggle");
        b.pad(2f, 8f, 2f, 8f);
        b.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
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

        buttons.put(w, b);
        refreshTaskbar();
    }
}
