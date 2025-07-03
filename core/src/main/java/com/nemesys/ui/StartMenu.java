package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.function.Consumer;

public final class StartMenu extends Table {

    public StartMenu(Skin skin, Consumer<WindowManager.AppType> launcher) {
        super(skin);
        setBackground("default-round");
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
        add(terminal).row();
        pack();
    }
}
