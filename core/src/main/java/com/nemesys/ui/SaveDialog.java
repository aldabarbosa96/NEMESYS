package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.nemesys.fs.FileSystemSim;

import java.util.function.Consumer;

public final class SaveDialog extends Window {

    private final FileSystemSim fs;
    private final Label pathLabel;
    private final List<String> list;
    private final TextField nameField;
    private final Consumer<String> onSave;

    public SaveDialog(Skin skin, FileSystemSim fs, Consumer<String> onSave) {
        super("Save As…", skin);
        this.fs = fs;
        this.onSave = onSave;

        /* ────── cabecera Win-95 ────── */
        getTitleLabel().setStyle(skin.get("title-label", Label.LabelStyle.class));
        getTitleTable().setBackground(skin.getDrawable("title"));

        /* ────── layout interno ────── */
        defaults().pad(4f).align(Align.left);

        /* nav: ↑ / home + ruta actual */
        ImageButton upBtn = new ImageButton(skin, "nav-back");
        ImageButton homeBtn = new ImageButton(skin, "nav-home");
        pathLabel = new Label(fs.pwd(), skin);

        Table nav = new Table();
        nav.add(upBtn).size(24).padRight(4);
        nav.add(homeBtn).size(24).padRight(8);
        nav.add(pathLabel).growX();
        add(nav).growX().row();

        /* lista de directorios */
        list = new List<>(skin);
        list.setItems(fs.ls().stream().filter(s -> s.endsWith("/")).toArray(String[]::new));
        ScrollPane scroll = new ScrollPane(list, skin);
        scroll.setFadeScrollBars(false);
        add(scroll).prefWidth(400).prefHeight(240).grow().row();

        /* nombre + botones */
        nameField = new TextField("", skin);
        TextButton save = new TextButton("Save", skin);
        TextButton cancel = new TextButton("Cancel", skin);

        Table bottom = new Table();
        bottom.add(new Label("File name:", skin)).padRight(4);
        bottom.add(nameField).growX().padRight(8);
        bottom.add(cancel).padRight(4);
        bottom.add(save);
        add(bottom).growX().row();

        pack();

        /* ────── listeners ────── */
        upBtn.addListener(e -> {
            if (e.toString().equals("touchDown") && fs.cd("..")) refresh();
            return true;
        });
        homeBtn.addListener(e -> {
            if (e.toString().equals("touchDown")) {
                fs.toRoot();
                refresh();
            }
            return true;
        });
        list.addListener(e -> {
            if (e.toString().equals("touchDown") && list.getSelected() != null) {
                String dir = list.getSelected();
                fs.cd(dir.substring(0, dir.length() - 1));
                refresh();
            }
            return true;
        });
        cancel.addListener(e -> {
            if (e.toString().equals("touchDown")) remove();
            return true;
        });
        save.addListener(e -> {
            if (!e.toString().equals("touchDown")) return false;
            String name = nameField.getText().trim();
            if (name.isEmpty()) return true;
            if (!name.contains(".")) name += ".txt";
            onSave.accept(name);
            remove();
            return true;
        });
    }

    /* refresca la ruta y la lista de carpetas */
    private void refresh() {
        pathLabel.setText(fs.pwd());
        list.setItems(fs.ls().stream().filter(s -> s.endsWith("/")).toArray(String[]::new));
    }

    /* centra la ventana al añadirse al Stage */
    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);
        if (stage != null) {
            setPosition((stage.getWidth() - getWidth()) / 2f, (stage.getHeight() - getHeight()) / 2f);
        }
    }
}
