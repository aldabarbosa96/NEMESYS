package com.nemesys.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.nemesys.fs.FileSystemSim;

import java.util.HashMap;
import java.util.Map;

public final class WindowManager {

    /*────────────────────────────── tipos ──────────────────────────────*/
    public enum AppType {TERMINAL, FILE_EXPLORER, TEXT_EDITOR}

    /*───────────────────────────── campos ──────────────────────────────*/
    private final Stage stage;
    private final Skin skin;
    private final Table taskbar;          // barra de botones abajo
    private final Map<AppType, BaseWindow> open = new HashMap<>();
    private final Map<AppType, TextButton> buttons = new HashMap<>();

    // una sola instancia del sistema de archivos (compartida)
    private final FileSystemSim fs = new FileSystemSim();

    /*────────────────────────── constructor ────────────────────────────*/
    public WindowManager(Stage stage, Skin skin, Table taskbar) {
        this.stage = stage;
        this.skin = skin;
        this.taskbar = taskbar;

        registerToggleStyle();            // ‹— estilo para los botones-ventana
    }

    /*──────────────────────── API pública ──────────────────────────────*/

    /**
     * Abre (o muestra) la ventana del tipo indicado.
     */
    public void open(AppType type) {
        open(type, null);
    }

    /**
     * Abre pasando un parámetro opcional (ruta archivo para el editor).
     */
    public void open(AppType type, String filePath) {

        // si ya existe y no es un editor, sólo la traemos al frente
        if (open.containsKey(type) && type != AppType.TEXT_EDITOR) {
            BaseWindow w = open.get(type);
            w.setVisible(true);
            w.toFront();
            stage.setKeyboardFocus(w);
            buttons.get(type).setChecked(false);
            return;
        }

        // crear nueva instancia
        BaseWindow w = create(type, filePath);
        stage.addActor(w);
        open.put(type, w);

        // botón en la task-bar
        TextButton b = new TextButton(w.getWindowTitle(), skin, "win95-toggle");
        b.addListener(e -> {
            if (!"touchDown".equals(e.toString())) return true;

            boolean willShow = b.isChecked();      // checked = ahora minimizado → mostrar
            w.setVisible(willShow);
            if (willShow) {
                w.toFront();
                stage.setKeyboardFocus(w);
            }
            b.setChecked(!willShow);               // coherencia visual
            return true;
        });

        taskbar.add(b).padRight(4f);
        buttons.put(type, b);
    }

    /**
     * Cierra y elimina la ventana (botón X o desde fuera).
     */
    public void close(AppType type) {
        BaseWindow w = open.remove(type);
        if (w != null) w.remove();

        TextButton b = buttons.remove(type);
        if (b != null) b.remove();
    }

    /**
     * Llamado por el botón “_” de la ventana.
     */
    public void minimize(AppType type, BaseWindow win) {
        win.setVisible(false);
        TextButton b = buttons.get(type);
        if (b != null) b.setChecked(true);         // checked == minimizado
        stage.setKeyboardFocus(null);
    }

    /*────────────────────────── helpers ───────────────────────────────*/

    private BaseWindow create(AppType type, String path) {
        switch (type) {
            case TERMINAL:
                return new TerminalWindow(skin, this, fs);

            case FILE_EXPLORER:
                return new FileExplorerWindow(skin, this, fs);

            case TEXT_EDITOR:
                String file = (path != null && !path.trim().isEmpty()) ? path : null;
                return new TextEditorWindow(skin, this, fs, file);

            default:
                throw new IllegalArgumentException("Tipo no soportado: " + type);
        }
    }


    /**
     * Estilo del botón-ventana (toggle) solo se registra una vez.
     */
    private void registerToggleStyle() {
        if (skin.has("win95-toggle", TextButtonStyle.class)) return;

        TextButtonStyle t = new TextButtonStyle(skin.getDrawable("face"),      // up
            skin.getDrawable("shadow"),    // down
            skin.getDrawable("shadow"),    // checked (minimizado)
            skin.getFont("font-win95"));
        t.fontColor = Color.BLACK;
        skin.add("win95-toggle", t);
    }

    /*──────── utilitario para comando “nano” en Terminal ────────*/
    public void openEditor(String filePath, FileSystemSim fsRef) {
        BaseWindow w = new TextEditorWindow(skin, this, fsRef, filePath);
        stage.addActor(w);
    }
}
