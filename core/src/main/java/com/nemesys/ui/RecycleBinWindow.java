// core/src/main/java/com/nemesys/ui/RecycleBinWindow.java
package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Align;
import com.nemesys.fs.FileSystemSim;

/**
 * Ventana de Papelera de Reciclaje.
 * Sólo lista los archivos eliminados (sin mostrar rutas ni directorios),
 * y permite eliminación definitiva con doble-clic.
 */
public final class RecycleBinWindow extends BaseWindow {

    private final FileSystemSim binFs;
    private final com.badlogic.gdx.scenes.scene2d.ui.List<String> listView;

    public RecycleBinWindow(Skin skin, WindowManager mgr, FileSystemSim binFs) {
        super("Papelera de reciclaje", skin, WindowManager.AppType.RECYCLE_BIN, mgr);
        this.binFs = binFs;

        defaults().pad(4f);

        // Lista de archivos en la papelera
        listView = new com.badlogic.gdx.scenes.scene2d.ui.List<>(skin);
        listView.setAlignment(Align.left);

        // Scroll para la lista
        ScrollPane scroll = new ScrollPane(listView, skin);
        scroll.setFadeScrollBars(false);

        add(scroll).prefWidth(400).prefHeight(260).grow();

        // Doble-clic para eliminar definitivamente
        listView.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (getTapCount() == 2 && listView.getSelected() != null) {
                    binFs.removeFile(listView.getSelected());
                    refresh();
                }
            }
        });

        pack();
        setPosition(200, 150);

        // Carga inicial
        refresh();
    }

    /**
     * Refresca la lista, mostrando sólo archivos (sin directorios).
     */
    private void refresh() {
        java.util.List<String> all = binFs.ls();
        java.util.List<String> filesOnly = new java.util.ArrayList<>();
        for (String item : all) {
            if (!item.endsWith("/")) {
                filesOnly.add(item);
            }
        }
        if (filesOnly.isEmpty()) {
            listView.setItems();
        } else {
            listView.setItems(filesOnly.toArray(new String[0]));
        }
    }
}
