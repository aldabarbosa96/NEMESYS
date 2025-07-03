package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public abstract class BaseWindow extends Window {

    private final String windowTitle;

    public BaseWindow(String title, Skin skin, WindowManager.AppType type, WindowManager manager) {
        super(title, skin);
        this.windowTitle = title;

        setMovable(true);
        setKeepWithinStage(true);
        getTitleLabel().setAlignment(Align.center);
        padTop(22f);

        TextButton closeBtn = new TextButton("X", skin);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent ev, float x, float y) {
                manager.close(type);
            }
        });
        getTitleTable().add().expandX();
        getTitleTable().add(closeBtn).padRight(4f);
        pack();
    }

    public String getWindowTitle() {
        return windowTitle;
    }
}
