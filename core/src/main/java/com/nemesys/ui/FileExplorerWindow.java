// core/src/main/java/com/nemesys/ui/FileExplorerWindow.java
package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.nemesys.fs.FileSystemSim;

public final class FileExplorerWindow extends BaseWindow {

    private final FileSystemSim fs;
    private final Skin skin;
    private final List<String> list;
    private final Label pathLabel;

    public FileExplorerWindow(Skin skin, WindowManager manager, FileSystemSim fs) {
        super("File Explorer", skin, WindowManager.AppType.FILE_EXPLORER, manager);
        this.skin = skin;
        this.fs = fs;

        pathLabel = new Label(fs.pwd(), skin);
        list = new List<>(skin);
        list.setAlignment(Align.left);
        refreshList();

        /* ── barra navegación con iconos ── */
        ImageButton upBtn = new ImageButton(skin, "nav-back");
        ImageButton homeBtn = new ImageButton(skin, "nav-home");

        upBtn.addListener(e -> {
            if (e.toString().equals("touchDown")) {
                if (fs.cd("..")) refreshList();
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
        nav.add(upBtn).size(24).padRight(4f);
        nav.add(homeBtn).size(24).padRight(8f);
        nav.add(pathLabel).growX();

        ScrollPane scroll = new ScrollPane(list, skin);
        scroll.setFadeScrollBars(false);

        list.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent ev, float x, float y) {
                if (getTapCount() == 2 && list.getSelected() != null) {
                    String sel = list.getSelected();
                    if (sel.endsWith("/")) {
                        fs.cd(sel.substring(0, sel.length() - 1));
                        refreshList();
                    }
                }
            }
        });

        defaults().pad(4f);
        add(nav).growX().row();
        add(scroll).prefWidth(400).prefHeight(240).grow();
        pack();
        setPosition(160, 120);
    }

    private void refreshList() {
        pathLabel.setText(fs.pwd());
        list.setItems(fs.ls().toArray(new String[0]));
    }
}

