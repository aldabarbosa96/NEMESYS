package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nemesys.fs.FileSystemSim;

/**
 * Ventana de edición de texto estilo Bloc-de-Notas.
 * • “Save”     → guarda en la ruta actual (o abre diálogo si es nuevo).
 * • “Save As”  → siempre lanza el diálogo {@link SaveDialog}.
 */
public final class TextEditorWindow extends BaseWindow {

    private final FileSystemSim fs;
    private final Skin skin;

    /* estado documento ------------------------------------------------------ */
    private String currentPath;          // null → aún sin nombre
    private final TextArea area;
    private final Label titleLabel;

    public TextEditorWindow(Skin skin, WindowManager mgr, FileSystemSim fs, String path) {

        super("Editor", skin, WindowManager.AppType.TEXT_EDITOR, mgr);
        this.fs = fs;
        this.skin = skin;
        this.currentPath = path;

        /* ── layout básico ── */
        defaults().pad(4f);

        /* barra superior ---------------------------------------------------- */
        Table bar = new Table();
        titleLabel = new Label((currentPath == null) ? "Untitled.txt" : currentPath, skin);

        TextButton saveBtn = new TextButton("Save", skin);
        TextButton saveAsBtn = new TextButton("Save As", skin);

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

        Actor xBtn = getTitleTable().getChildren().peek();
        xBtn.clearListeners();                         // elimina listener heredado
        xBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent ev, float x, float y) {
                remove();                              // quita del Stage ⇒ GC
            }
        });

        /* ── acciones de los botones ── */
        saveBtn.addListener(e -> {
            if (!e.toString().equals("touchDown")) return false;
            if (currentPath == null) {
                openSaveDialog();
            } else {
                /* 1) navega al directorio del archivo  */
                int idx = Math.max(currentPath.lastIndexOf('\\'), currentPath.lastIndexOf('/'));
                if (idx >= 0) fs.cd("..");
                /* 2) guarda usando sólo el nombre */
                String name = idx >= 0 ? currentPath.substring(idx + 1) : currentPath;
                fs.overwrite(name, area.getText());
            }
            return true;
        });

        saveAsBtn.addListener(e -> {
            if (e.toString().equals("touchDown")) openSaveDialog();
            return true;
        });
    }

    /* carga inicial del archivo (si existe) */
    private String loadText() {
        if (currentPath == null) return "";
        String txt = fs.cat(currentPath);
        return txt == null ? "" : txt;
    }

    /* lanza el diálogo modal “Save As…” */
    private void openSaveDialog() {
        SaveDialog dlg = new SaveDialog(skin, fs, fileName -> {
            currentPath = fs.pwd() + "\\" + fileName;
            fs.overwrite(fileName, area.getText());
            titleLabel.setText(currentPath);
        });
        getStage().addActor(dlg);
    }

}
