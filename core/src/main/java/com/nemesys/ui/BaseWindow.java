package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

/**
 * Ventana base estilo Win-95 con:
 * • Marco 3-D, título a la izquierda.
 * • Botones minimizar “_” y cerrar “X”.
 * • Redimensionable desde bordes y esquinas.
 */
public abstract class BaseWindow extends Window {
    /**
     * Grosor visual del marco NinePatch (px)
     */
    private static final float FRAME = 3f;
    /**
     * Alto de la barra azul de título (px)
     */
    private static final float BAR = 30f;

    private final WindowManager.AppType type;
    private final WindowManager mgr;
    private final String windowTitle;

    public BaseWindow(String title, Skin skin, WindowManager.AppType type, WindowManager mgr) {
        super(title, skin);
        this.windowTitle = title;
        this.type = type;
        this.mgr = mgr;

        // ── Padding para el marco y la barra ──
        pad(FRAME);
        padTop(FRAME + BAR);

        // ── Limpiamos la tabla de título por defecto ──
        Table titleTable = getTitleTable();
        titleTable.clearChildren();
        titleTable.pad(0);
        titleTable.setBackground((Drawable) null);

        // ── Construcción de la franja azul ──
        Table bar = new Table();
        bar.setBackground(skin.getDrawable("title"));

        // Etiqueta del título
        Label titleLbl = getTitleLabel();
        titleLbl.setStyle(skin.get("title-label", Label.LabelStyle.class));
        titleLbl.setAlignment(Align.left);

        // Botón minimizar “_”
        TextButton minimize = new TextButton("_", skin, "win95-window");
        minimize.setName("minimizeButton");
        minimize.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                setVisible(false);
                mgr.minimize(type, BaseWindow.this);
            }
        });

        // Botón cerrar “X”
        TextButton close = new TextButton("X", skin, "win95-window");
        close.setName("closeButton");
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                mgr.close(type);
            }
        });

        // Añadimos componentes a la barra
        bar.add(titleLbl).expandX().left().padLeft(8f);
        bar.add(minimize).size(18).padRight(3f);
        bar.add(close).size(18).padRight(6f);

        // Insertamos la barra justo bajo el marco superior
        titleTable.add(bar).expand().fillX().padTop(FRAME).height(BAR);

        // ── Comportamientos adicionales ──
        setMovable(true);
        setResizable(true);
        setResizeBorder((int) FRAME);
        setKeepWithinStage(true);

        pack();
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    /**
     * Ancho mínimo para que no colapse
     */
    @Override
    public float getMinWidth() {
        return 120f;
    }

    /**
     * Alto mínimo para que no colapse
     */
    @Override
    public float getMinHeight() {
        return 80f;
    }
}
