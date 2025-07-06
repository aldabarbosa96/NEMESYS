package com.nemesys.ui.windows;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.nemesys.fs.FileSystemSim;
import com.nemesys.ui.managers.WindowsManager;

import java.util.List;

public final class FileExplorerWindow extends BaseWindow {

    private final WindowsManager manager;
    private final FileSystemSim fs;
    private final Skin skin;
    private final com.badlogic.gdx.scenes.scene2d.ui.List<String> list;
    private final Label pathLabel;

    public FileExplorerWindow(Skin skin, WindowsManager manager, FileSystemSim fs) {
        super("File Explorer", skin, WindowsManager.AppType.FILE_EXPLORER, manager);
        this.manager = manager;
        this.fs = fs;
        this.skin = skin;

        pathLabel = new Label(fs.pwd(), skin);
        list = new com.badlogic.gdx.scenes.scene2d.ui.List<>(skin);
        list.setAlignment(Align.left);
        refreshList();

        // Doble clic: abrir carpeta o archivo
        list.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent ev, float x, float y) {
                if (getTapCount() == 2 && list.getSelected() != null) {
                    String sel = list.getSelected();
                    if (sel.endsWith("/")) {
                        fs.cd(sel.substring(0, sel.length() - 1));
                        refreshList();
                    } else {
                        manager.openEditor(sel, fs);
                    }
                }
            }
        });

        // Clic derecho: menú contextual (Abrir + Eliminar)
        list.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == Input.Buttons.RIGHT && list.getSelected() != null) {
                    showContextMenu(list.getSelected(), event.getStageX(), event.getStageY());
                    return true;
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        // Barra de navegación
        ImageButton upBtn = new ImageButton(skin, "nav-back");
        ImageButton homeBtn = new ImageButton(skin, "nav-home");
        upBtn.addListener(e -> {
            if (e.toString().equals("touchDown") && fs.cd("..")) {
                refreshList();
                return true;
            }
            return false;
        });
        homeBtn.addListener(e -> {
            if (e.toString().equals("touchDown")) {
                fs.toRoot();
                refreshList();
                return true;
            }
            return false;
        });

        Table nav = new Table();
        nav.add(upBtn).size(28).padRight(4f);
        nav.add(homeBtn).size(28).padRight(8f);
        nav.add(pathLabel).growX();

        ScrollPane scroll = new ScrollPane(list, skin);
        scroll.setFadeScrollBars(false);

        defaults().pad(4f);
        add(nav).growX().row();
        add(scroll).prefWidth(400).prefHeight(240).grow();

        pack();
        setPosition(160, 120);
    }

    private void refreshList() {
        pathLabel.setText(fs.pwd());
        List<String> items = fs.ls();
        list.setItems(items.toArray(new String[0]));
    }

    private void showContextMenu(String sel, float stageX, float stageY) {
        Window menu = new Window("", getSkin());
        menu.setMovable(false);
        menu.setResizable(false);
        menu.defaults().pad(4f);

        // "Abrir" solo para archivos
        if (!sel.endsWith("/")) {
            TextButton open = new TextButton("Abrir", getSkin());
            open.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent e, float x, float y) {
                    manager.openEditor(sel, fs);
                    menu.remove();
                }
            });
            menu.add(open).row();
        }

        // "Eliminar"
        TextButton delete = new TextButton("Eliminar", getSkin());
        delete.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                manager.moveToRecycle(fs, sel);
                refreshList();
                menu.remove();
            }
        });
        menu.add(delete).row();

        menu.pack();

        // Ajusta posición para que no salga fuera de la ventana
        float x = stageX;
        float y = stageY - menu.getHeight();
        Stage stage = getStage();
        if (x + menu.getWidth() > stage.getWidth()) {
            x = stage.getWidth() - menu.getWidth();
        }
        if (y < 0) {
            y = 0;
        }
        menu.setPosition(x, y);
        stage.addActor(menu);
    }
}
