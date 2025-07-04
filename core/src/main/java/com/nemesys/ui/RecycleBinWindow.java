package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.nemesys.fs.FileSystemSim;

import java.util.ArrayList; // Para listData
import java.util.List;      // Para listData

/**
 * Ventana de Papelera de Reciclaje.
 * Lista sólo archivos eliminados y ofrece botones Restaurar / Eliminar.
 */
public final class RecycleBinWindow extends BaseWindow {

    private final WindowManager manager;
    private final FileSystemSim binFs;
    private final java.util.List<String> listData = new ArrayList<>();
    private final com.badlogic.gdx.scenes.scene2d.ui.List<String> listView;

    public RecycleBinWindow(Skin skin, WindowManager mgr, FileSystemSim binFs) {
        super("Papelera de reciclaje", skin, WindowManager.AppType.RECYCLE_BIN, mgr);
        this.manager = mgr;
        this.binFs = binFs;

        defaults().pad(4f);

        // ── Barra de iconos Restaurar / Eliminar ───────────────────
        Table toolbar = new Table();
        ImageButton restoreBtn = new ImageButton(skin, "restore");
        ImageButton deleteBtn = new ImageButton(skin, "delete");

        restoreBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent e, Actor actor) {
                String sel = listView.getSelected();
                if (sel != null) {
                    manager.restoreFromRecycle(sel);
                    refreshList();
                }
            }
        });
        deleteBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent e, Actor actor) {
                String sel = listView.getSelected();
                if (sel != null) {
                    binFs.removeFile(sel);
                    refreshList();
                }
            }
        });

        toolbar.add(restoreBtn);
        toolbar.add(deleteBtn);
        add(toolbar).right().padTop(5).row();

        // ── Lista de archivos ───────────────────────────────────────
        listView = new com.badlogic.gdx.scenes.scene2d.ui.List<>(skin);
        listView.setAlignment(Align.left);
        ScrollPane scroll = new ScrollPane(listView, skin);
        scroll.setFadeScrollBars(false);
        add(scroll).prefWidth(400).prefHeight(260).grow();

        pack();
        setPosition(200, 150);

        refreshList();
    }

    private void refreshList() {
        listData.clear();
        for (String item : binFs.ls()) {
            if (!item.endsWith("/")) {
                listData.add(item);
            }
        }
        if (listData.isEmpty()) {
            listView.setItems(); // vacía
        } else {
            listView.setItems(listData.toArray(new String[0]));
        }
    }
}
