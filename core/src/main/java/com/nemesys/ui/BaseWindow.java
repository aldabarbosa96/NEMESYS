package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

/**
 * Ventana base Win-95 con título a la izquierda y padding.
 */
public abstract class BaseWindow extends Window {

    private final String windowTitle;

    public BaseWindow(String title, Skin skin, WindowManager.AppType type, WindowManager manager) {
        super(title, skin);
        this.windowTitle = title;

        /* cabecera azul */
        getTitleTable().setBackground(skin.getDrawable("title"));
        getTitleTable().padLeft(6f).padRight(6f);

        /* reutiliza el Label interno con estilo 18 px blanco */
        Label titleLab = getTitleLabel();
        titleLab.setStyle(skin.get("title-label", Label.LabelStyle.class));
        titleLab.setAlignment(Align.left);

        setMovable(true);
        setKeepWithinStage(true);
        padTop(24f);

        /* botón X cuadrado */
        TextButton close = new TextButton("X", skin, "win95-window");
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                manager.close(type);
            }
        });

        /* reconstruimos la barra */
        getTitleTable().clearChildren();
        getTitleTable().add(titleLab).expandX().left();
        getTitleTable().add(close).size(18).pad(3f);

        pack();
    }

    public String getWindowTitle() {
        return windowTitle;
    }
}
