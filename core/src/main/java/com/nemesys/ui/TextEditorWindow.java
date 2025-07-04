// File: core/src/main/java/com/nemesys/ui/TextEditorWindow.java
package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.nemesys.fs.FileSystemSim;

import java.io.File;

public final class TextEditorWindow extends BaseWindow {
    private final FileSystemSim fs;
    private final WindowManager mgr;
    private String currentPath;

    private final Table bar;
    private Label titleLabel;
    private TextField nameField;
    private final ImageButton saveBtn;
    private final ImageButton saveAsBtn;
    private final TextArea area;

    public TextEditorWindow(Skin skin, WindowManager mgr, FileSystemSim fs, String path) {
        super("Editor", skin, WindowManager.AppType.TEXT_EDITOR, mgr);
        this.fs = fs;
        this.mgr = mgr;
        this.currentPath = (path != null && !path.trim().isEmpty()) ? path : null;

        defaults().pad(4f);

        // ── Barra de nombre + botones ───────────────────────────────
        bar = new Table();
        if (currentPath != null) {
            titleLabel = new Label(currentPath, skin);
            nameField = null;
            bar.add(titleLabel).expandX().left();
        } else {
            titleLabel = null;
            nameField = new TextField("", skin);
            nameField.setMessageText("Untitled.txt");
            bar.add(nameField).expandX().left().padRight(8);
        }

        saveBtn = new ImageButton(skin, "save");
        saveAsBtn = new ImageButton(skin, "saveAs");
        bar.add(saveBtn).padRight(4).padTop(5);
        bar.add(saveAsBtn).padTop(5);

        add(bar).growX().row();

        // ── Área de texto ───────────────────────────────────────────
        area = new TextArea(loadText(), skin);
        ScrollPane scroll = new ScrollPane(area, skin);
        scroll.setFadeScrollBars(false);
        add(scroll).prefWidth(460).prefHeight(260).grow();

        pack();
        setPosition(180, 140);

        // ── Listeners ───────────────────────────────────────────────

        saveBtn.addListener(e -> {
            if (!e.toString().equals("touchDown")) return false;
            if (currentPath == null) {
                // Nuevo archivo
                String name = nameField.getText().trim();
                if (name.isEmpty()) return true;
                if (!name.contains(".")) name += ".txt";
                fs.overwrite(name, area.getText());
                currentPath = fs.pwd() + "\\" + name;
                replaceNameFieldWithLabel();
            } else {
                // Guardar existente
                int idx = Math.max(currentPath.lastIndexOf('\\'), currentPath.lastIndexOf('/'));
                if (idx >= 0) fs.cd("..");
                String base = (idx >= 0) ? currentPath.substring(idx + 1) : currentPath;
                fs.overwrite(base, area.getText());
            }
            return true;
        });

        saveAsBtn.addListener(e -> {
            if (!e.toString().equals("touchDown")) return false;
            openSaveDialog(true);
            return true;
        });
    }

    private String loadText() {
        if (currentPath == null) return "";
        String t = fs.cat(currentPath);
        return t == null ? "" : t;
    }

    /**
     * Tras el primer Save, reemplaza el TextField por un Label fijo.
     */
    private void replaceNameFieldWithLabel() {
        bar.clear();
        titleLabel = new Label(currentPath, getSkin());
        nameField = null;
        bar.add(titleLabel).expandX().left();
        bar.add(saveBtn).padRight(4).padTop(5);
        bar.add(saveAsBtn).padTop(5);
        bar.pack();
        pack();
    }

    /**
     * Abre el SaveDialog pasando el nombre actual como valor por defecto.
     */
    private void openSaveDialog(boolean closeAfter) {
        // Determina el nombre por defecto
        String defaultName;
        if (currentPath != null) {
            defaultName = new File(currentPath).getName();
        } else {
            defaultName = nameField.getText().trim();
            if (defaultName.isEmpty()) defaultName = "Untitled.txt";
        }

        SaveDialog dlg = new SaveDialog(getSkin(), fs, fileName -> {
            // Callback al pulsar Save en el diálogo
            currentPath = fs.pwd() + "\\" + fileName;
            fs.overwrite(fileName, area.getText());
            if (titleLabel != null) {
                titleLabel.setText(currentPath);
            } else {
                replaceNameFieldWithLabel();
            }
            if (closeAfter) mgr.close(this);
        }, defaultName);
        Stage stage = getStage();
        if (stage != null) stage.addActor(dlg);
    }

    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);
        if (stage != null) {
            stage.setKeyboardFocus(area);
        }
    }
}
