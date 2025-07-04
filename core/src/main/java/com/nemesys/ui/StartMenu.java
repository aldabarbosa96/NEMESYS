package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.function.Consumer;

public final class StartMenu extends Table {

    public StartMenu(Skin skin, Consumer<WindowManager.AppType> launcher) {
        super(skin);
        setBackground("menu-bg");
        pad(6f);
        defaults().pad(2f).fillX();
        TextButton terminal = new TextButton("Terminal", skin);
        terminal.addListener(e -> {
            if (e.toString().equals("touchDown")) {
                launcher.accept(WindowManager.AppType.TERMINAL);
                setVisible(false);
            }
            return true;
        });

        TextButton explorerBtn = new TextButton("Explorador", skin);
        explorerBtn.addListener(e -> {
            if (e.toString().equals("touchDown")) {
                launcher.accept(WindowManager.AppType.FILE_EXPLORER);
                setVisible(false);
            }
            return true;
        });

        TextButton editorBtn = new TextButton("Editor", skin);
        editorBtn.addListener(e -> {
            if (e.toString().equals("touchDown")) {
                launcher.accept(WindowManager.AppType.TEXT_EDITOR);
                setVisible(false);
            }
            return true;
        });

        add(explorerBtn).row();
        add(terminal).row();
        add(editorBtn).row();
        pack();
    }
}
