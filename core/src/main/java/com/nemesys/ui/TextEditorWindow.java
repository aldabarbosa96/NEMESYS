package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nemesys.fs.FileSystemSim;

/**
 * Ventana de edición de texto estilo Bloc-de-Notas.
 * • “Save”     → icono, guarda en la ruta actual (o abre diálogo si es nuevo) y permanece abierta.
 * • “Save As”  → icono, guarda siempre en nueva ruta y cierra el editor.
 */
public final class TextEditorWindow extends BaseWindow {

    private final FileSystemSim fs;
    private final Skin skin;
    private final WindowManager manager;

    /* estado documento ------------------------------------------------------ */
    private String currentPath;          // null → aún sin nombre
    private final TextArea area;
    private final Label titleLabel;

    public TextEditorWindow(Skin skin, WindowManager mgr, FileSystemSim fs, String path) {
        super("Editor", skin, WindowManager.AppType.TEXT_EDITOR, mgr);
        this.fs = fs;
        this.skin = skin;
        this.manager = mgr;
        this.currentPath = path;

        /* ── layout básico ── */
        defaults().pad(4f);

        /* barra superior ---------------------------------------------------- */
        Table bar = new Table();
        titleLabel = new Label((currentPath == null) ? "Untitled.txt" : currentPath, skin);

        ImageButton saveBtn = new ImageButton(skin, "save");
        ImageButton saveAsBtn = new ImageButton(skin, "saveAs");

        bar.add(titleLabel).expandX().left();
        bar.add(saveBtn).padRight(4);
        bar.add(saveAsBtn);

        /* área de texto ----------------------------------------------------- */
        area = new TextArea(loadText(), skin);
        ScrollPane scroll = new ScrollPane(area, skin);
        scroll.setFadeScrollBars(false);

        add(bar).growX().row();
        add(scroll).prefWidth(460).prefHeight(260).grow();
        pack();
        setPosition(180, 140);

        // Botón X: cerrar totalmente (remueve de ventana y taskbar)
        Actor xBtn = getTitleTable().getChildren().peek();
        xBtn.clearListeners();
        xBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent ev, float x, float y) {
                manager.close(WindowManager.AppType.TEXT_EDITOR);
            }
        });

        /* ── acciones de los botones ── */
        saveBtn.addListener(e -> {
            if (!e.toString().equals("touchDown")) return false;
            if (currentPath == null) {
                openSaveDialog(false);
            } else {
                int idx = Math.max(currentPath.lastIndexOf('\\'), currentPath.lastIndexOf('/'));
                if (idx >= 0) fs.cd("..");
                String name = idx >= 0 ? currentPath.substring(idx + 1) : currentPath;
                fs.overwrite(name, area.getText());
            }
            return true;
        });

        saveAsBtn.addListener(e -> {
            if (!e.toString().equals("touchDown")) return false;
            openSaveDialog(true);
            return true;
        });
    }

    /* carga inicial del archivo (si existe) */
    private String loadText() {
        if (currentPath == null) return "";
        String txt = fs.cat(currentPath);
        return txt == null ? "" : txt;
    }

    /**
     * @param closeAfterSave si es true, cierra la ventana tras guardar (Save As).
     */
    private void openSaveDialog(boolean closeAfterSave) {
        SaveDialog dlg = new SaveDialog(skin, fs, fileName -> {
            currentPath = fs.pwd() + "\\" + fileName;
            fs.overwrite(fileName, area.getText());
            titleLabel.setText(currentPath);
            if (closeAfterSave) {
                manager.close(WindowManager.AppType.TEXT_EDITOR);
            }
        });
        getStage().addActor(dlg);
    }
}
