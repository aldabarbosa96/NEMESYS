package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Align;
import com.nemesys.fs.FileSystemSim;

public final class RecycleBinWindow extends BaseWindow {

    private final FileSystemSim binFs;
    private final com.badlogic.gdx.scenes.scene2d.ui.List<String> listView;

    public RecycleBinWindow(Skin skin, WindowManager mgr, FileSystemSim binFs) {
        super("Papelera de reciclaje", skin, WindowManager.AppType.RECYCLE_BIN, mgr);
        this.binFs = binFs;

        defaults().pad(4f);

        // La lista de elementos en la papelera
        listView = new com.badlogic.gdx.scenes.scene2d.ui.List<>(skin);
        listView.setAlignment(Align.left);

        // Scroll pane para la lista
        ScrollPane scroll = new ScrollPane(listView, skin);
        scroll.setFadeScrollBars(false);

        // Añadimos el scroll a la ventana
        add(scroll).prefWidth(400).prefHeight(260).grow();

        // Doble-clic para “restaurar” (o eliminar definitivamente)
        listView.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (getTapCount() == 2 && listView.getSelected() != null) {
                    String sel = listView.getSelected();
                    // Ejemplo de restauración: lo movemos de binFs a fs real
                    // mgr.openEditor(...) o cualquier lógica que necesites.
                    // Por ahora simplemente lo quitamos de la papelera:
                    binFs.run("rm", sel);
                    refresh();
                }
            }
        });

        pack();
        setPosition(200, 150);

        // Carga inicial de contenido
        refresh();
    }

    /**
     * Refresca la lista con el contenido actual de la papelera.
     */
    private void refresh() {
        java.util.List<String> all = binFs.ls();
        if (all.isEmpty()) {
            listView.setItems();
        } else {
            listView.setItems(all.toArray(new String[0]));
        }
    }
}
