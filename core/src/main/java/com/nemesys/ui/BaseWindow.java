package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

public abstract class BaseWindow extends Window {
    private static final float FRAME = 3f;
    private static final float BAR = 30f;

    private final WindowManager.AppType type;
    private final WindowManager mgr;
    private final String windowTitle;

    public BaseWindow(String title, Skin skin, WindowManager.AppType type, WindowManager mgr) {
        super(title, skin);
        this.windowTitle = title;
        this.type = type;
        this.mgr = mgr;

        pad(FRAME);
        padTop(FRAME + BAR);

        // limpiamos el titleTable original
        Table titleTable = getTitleTable();
        titleTable.clearChildren();
        titleTable.pad(0);
        titleTable.setBackground((Drawable) null);

        // franja de título custom
        Table bar = new Table();
        bar.setBackground(skin.getDrawable("title"));

        Label titleLbl = getTitleLabel();
        titleLbl.setStyle(skin.get("title-label", Label.LabelStyle.class));
        titleLbl.setAlignment(Align.left);

        // minimizar
        TextButton minimize = new TextButton("_", skin, "win95-window");
        minimize.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                setVisible(false);
                mgr.minimize(type, BaseWindow.this);
            }
        });

        // cerrar
        TextButton close = new TextButton("X", skin, "win95-window");
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                mgr.close(type);
            }
        });

        bar.add(titleLbl).expandX().left().padLeft(8f);
        bar.add(minimize).size(18).padRight(3f);
        bar.add(close).size(18).padRight(6f);

        titleTable.add(bar).expand().fillX().padTop(FRAME).height(BAR);

        setMovable(true);
        setResizable(true);
        setResizeBorder((int) FRAME);
        setKeepWithinStage(true);

        pack();
    }

    @Override
    public float getMinWidth() {
        return 120f;
    }

    @Override
    public float getMinHeight() {
        return 80f;
    }

    public String getWindowTitle() {
        return windowTitle;
    }
}
