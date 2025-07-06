package com.nemesys.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nemesys.NemesysGame;
import com.nemesys.ui.managers.*;
import com.nemesys.ui.windows.*;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DesktopScreen implements Screen {

    private final NemesysGame game;
    private final Stage stage;
    private final Skin skin;
    private final WindowsManager wm;
    private final StartMenu startMenu;
    private final Label clock;
    private final Table desktopIcons;
    private final Texture fileTexture;
    private float acc;

    // ——— NUEVO: gestión de posiciones y DnD ———
    private final List<String> iconOrder = new ArrayList<>();
    private final DragAndDrop dnd = new DragAndDrop();
    private boolean desktopDirty = true;
    private static final float CELL_WIDTH = 100f;
    private static final float CELL_HEIGHT = 120f;
    // ————————————————

    private static final float BAR_H = 46f;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

    public DesktopScreen(NemesysGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport(), game.batch);
        this.skin = UIStylesManager.create();

        // 1) Wallpaper
        Texture wall = new Texture(Gdx.files.internal("wallpaper3.png"));
        Image bg = new Image(wall);
        bg.setFillParent(true);
        bg.setScaling(Scaling.stretch);
        stage.addActor(bg);

        // 2) Reloj
        this.clock = new Label("", skin);

        // 3) Barra de tareas
        Table taskbarButtons = buildTaskbar();
        this.wm = new WindowsManager(stage, skin, taskbarButtons, this);

        // 4) Inicializamos FS y orden de iconos
        wm.getFs().toRoot();
        wm.getFs().cd("Desktop");
        iconOrder.clear();
        for (String item : wm.getFs().ls()) {
            if (!item.endsWith("/")) {
                iconOrder.add(item);
            }
        }

        // 5) Pre-cargamos textura genérica de archivo
        this.fileTexture = new Texture(Gdx.files.internal("icons/archivo.png"));

        // 6) Creamos tabla de iconos
        desktopIcons = new Table();
        desktopIcons.setFillParent(true);
        desktopIcons.top().left();
        stage.addActor(desktopIcons);

        // 7) Start menu
        this.startMenu = new StartMenu(skin, wm::open);
        startMenu.setVisible(false);
        stage.addActor(startMenu);

        Gdx.input.setInputProcessor(stage);
        updateClock();
    }

    /**
     * Marca el escritorio como “sucio” para refrescarlo
     */
    public void markDesktopDirty() {
        desktopDirty = true;
    }

    /**
     * Reconstruye la cuadrícula de iconos
     */
    private void refreshDesktop() {
        desktopIcons.clearChildren();

        // Sincronizamos iconOrder con los archivos actuales
        List<String> current = new ArrayList<>();
        for (String item : wm.getFs().ls()) {
            if (!item.endsWith("/")) current.add(item);
        }
        iconOrder.retainAll(current);
        for (String f : current) {
            if (!iconOrder.contains(f)) iconOrder.add(f);
        }

        // Papelera en la primera celda
        addIconCell("Papelera", skin.getDrawable("trash"), true);

        // Columnas según ancho de pantalla
        int cols = Math.max(1, (int) (Gdx.graphics.getWidth() / CELL_WIDTH));

        // Añadimos cada archivo según iconOrder
        for (int i = 0; i < iconOrder.size(); i++) {
            String name = iconOrder.get(i);
            addIconCell(name, new TextureRegionDrawable(new TextureRegion(fileTexture)), false);
            if ((i + 2) % cols == 0) {
                desktopIcons.row();
            }
        }
    }

    /**
     * Crea una celda con icono y etiqueta, y la registra para Drag & Drop.
     */
    private void addIconCell(final String name, final com.badlogic.gdx.scenes.scene2d.utils.Drawable iconDrw, final boolean isRecycle) {
        final Table cell = new Table();
        cell.defaults().center();

        ImageButton btn = new ImageButton(new ImageButton.ImageButtonStyle() {{
            imageUp = iconDrw;
            imageDown = iconDrw;
            imageOver = iconDrw;
        }});
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isRecycle) {
                    wm.open(WindowsManager.AppType.RECYCLE_BIN);
                } else {
                    wm.openEditor(name, wm.getFs());
                }
            }
        });

        Label lbl = new Label(name, skin, "desktop-icon-label");
        lbl.setWrap(true);
        lbl.setAlignment(Align.center);
        lbl.setWidth(CELL_WIDTH - 20);

        cell.add(btn).size(60, 60).padTop(5).row();
        cell.add(lbl).width(CELL_WIDTH - 20);

        // Fuente de Drag
        dnd.addSource(new Source(btn) {
            @Override
            public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                Payload payload = new Payload();
                payload.setObject(name);
                return payload;
            }
        });
        // Objetivo de Drop
        dnd.addTarget(new Target(cell) {
            @Override
            public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                return true;
            }

            @Override
            public void drop(Source source, Payload payload, float x, float y, int pointer) {
                String dragged = (String) payload.getObject();
                int from = iconOrder.indexOf(dragged);
                int to = iconOrder.indexOf(name);
                if (name.equals("Papelera")) return;
                Collections.swap(iconOrder, from, to);
                markDesktopDirty();
            }
        });

        desktopIcons.add(cell).width(CELL_WIDTH).height(CELL_HEIGHT).pad(8);
    }

    /**
     * Construye la barra de tareas con botón Inicio y reloj
     */
    private Table buildTaskbar() {
        Table root = new Table();
        root.setFillParent(true);
        root.align(Align.bottomLeft);
        stage.addActor(root);

        Table bar = new Table(skin);
        bar.setBackground("taskbar");
        bar.getBackground().setMinHeight(BAR_H);
        bar.align(Align.left);

        ImageTextButton startBtn = new ImageTextButton("Inicio", skin, "start-btn-img");
        startBtn.getImageCell().padRight(4f).padLeft(-5);
        startBtn.pad(2);
        bar.add(startBtn).width(90).padLeft(2).height(BAR_H - 7.5f);

        Table btnBar = new Table();
        btnBar.left();
        bar.add(btnBar).expandX().left();

        Container<Label> clockC = new Container<>(clock);
        clockC.background(skin.getDrawable("btn-checked"));
        clockC.pad(2, 8, 2, 8);
        bar.add(clockC).padRight(2).height(BAR_H - 7.5f);

        root.add(bar).growX().height(BAR_H);

        startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                boolean showing = startMenu.isVisible();
                if (!showing) {
                    Vector2 pos = new Vector2();
                    startBtn.localToStageCoordinates(pos);
                    startMenu.pack();
                    startMenu.setWidth(200f);
                    startMenu.setPosition(pos.x, pos.y + startBtn.getHeight());
                }
                startMenu.setVisible(!showing);
            }
        });

        return btnBar;
    }

    /**
     * Actualiza el reloj cada segundo
     */
    private void updateClock() {
        clock.setText(LocalTime.now().format(FMT));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (desktopDirty) {
            refreshDesktop();
            desktopDirty = false;
        }

        acc += delta;
        if (acc >= 1f) {
            acc = 0f;
            updateClock();
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        fileTexture.dispose();
    }
}
