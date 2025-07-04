package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.nemesys.fs.FileSystemSim;

import java.util.HashMap;
import java.util.Map;

public final class WindowManager {

    public enum AppType {TERMINAL, FILE_EXPLORER, TEXT_EDITOR}

    private final Stage stage;
    private final Skin skin;
    private final Table buttonBar;
    private final Map<AppType, BaseWindow> open = new HashMap<>();
    private final Map<AppType, TextButton> buttons = new HashMap<>();

    /* ——— instancia única del sistema de archivos ——— */
    private final FileSystemSim fs = new FileSystemSim();

    public WindowManager(Stage stage, Skin skin, Table buttonBar) {
        this.stage = stage;
        this.skin = skin;
        this.buttonBar = buttonBar;
    }

    public void open(AppType type, String filePath) {
        if (open.containsKey(type) && type != AppType.TEXT_EDITOR) {   // varios editores permitidos
            BaseWindow w = open.get(type);
            w.setVisible(true); w.toFront();
            return;
        }
        BaseWindow w = create(type, filePath);
        stage.addActor(w);
        open.put(type, w);

        TextButton b = new TextButton(w.getWindowTitle(), skin);
        b.addListener(e -> {
            if (e.toString().equals("touchDown")) {
                BaseWindow win = open.get(type);
                if (win.isVisible()) win.setVisible(false);
                else {
                    win.setVisible(true);
                    win.toFront();
                }
            }
            return true;
        });
        buttonBar.add(b).padRight(4f);
        buttons.put(type, b);
    }

    public void close(AppType type) {
        BaseWindow w = open.remove(type);
        if (w != null) w.remove();
        TextButton b = buttons.remove(type);
        if (b != null) b.remove();
    }

    /* fábrica --------------------------------------------------------------- */
    private BaseWindow create(AppType type, String... param) {
        switch (type) {
            case TERMINAL:
                return new TerminalWindow(skin, this, new FileSystemSim());

            case FILE_EXPLORER:
                return new FileExplorerWindow(skin, this, new FileSystemSim());

            /* 2️⃣  Editor ------------------------------------------------- */
            case TEXT_EDITOR:
                String file = (param.length > 0) ? param[0] : null;  // null → documento en blanco
                return new TextEditorWindow(skin, this, new FileSystemSim(), file);
        }
        throw new IllegalArgumentException("Tipo no soportado: " + type);
    }

    public void open(AppType type) { open(type, ""); }


    /* Helper específico para el comando nano ----------------------------- */
    public void openEditor(String filePath, FileSystemSim fsRef) {
        TextEditorWindow w = new TextEditorWindow(skin, this, fsRef, filePath);
        stage.addActor(w);
    }

}
