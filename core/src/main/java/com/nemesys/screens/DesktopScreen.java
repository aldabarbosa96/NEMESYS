package com.nemesys.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nemesys.NemesysGame;
import com.nemesys.ui.StartMenu;
import com.nemesys.ui.UIStyles;
import com.nemesys.ui.WindowManager;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Pantalla principal que simula el escritorio de NEMESYS OS.
 * Dibuja fondo, iconos, taskbar y gestiona los clics sobre ellos.
 */
public final class DesktopScreen implements Screen {

    private final NemesysGame game;
    private final Stage stage;
    private final Skin skin;
    private final WindowManager wm;
    private final StartMenu startMenu;
    private final Label clock;
    private final Table desktopIcons;
    private final Texture fileTexture;
    private float acc;

    private static final float BAR_H = 46f;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

    public DesktopScreen(NemesysGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport(), game.batch);
        this.skin = UIStyles.create();

        // 1) Wallpaper
        Texture wall = new Texture(Gdx.files.internal("wallpaper3.png"));
        Image bg = new Image(wall);
        bg.setFillParent(true);
        bg.setScaling(Scaling.stretch);
        stage.addActor(bg);

        // 2) Reloj
        this.clock = new Label("", skin);

        // 3) Barra de tareas (y su Start‐menu)
        Table taskbarButtons = buildTaskbar();
        this.wm = new WindowManager(stage, skin, taskbarButtons);

        // 4) Situamos el FS “global” justo en C:\Desktop
        wm.getFs().toRoot();
        wm.getFs().cd("Desktop");

        // 5) Pre‐cargamos la textura genérica de archivo
        this.fileTexture = new Texture(Gdx.files.internal("icons/archivo.png"));

        // 6) Creamos la tabla de iconos una sola vez
        desktopIcons = new Table();
        desktopIcons.setFillParent(true);
        desktopIcons.top().left();
        desktopIcons.defaults().pad(15).padLeft(20).align(Align.center);
        stage.addActor(desktopIcons);

        // 7) Pintamos los iconos por primera vez
        refreshDesktop();

        // 8) Start menu
        this.startMenu = new StartMenu(skin, wm::open);
        startMenu.setVisible(false);
        stage.addActor(startMenu);

        Gdx.input.setInputProcessor(stage);
        updateClock();
    }

    /**
     * Reconstruye los iconos del escritorio SOLO cuando cambie el FS
     */
    private void refreshDesktop() {
        desktopIcons.clearChildren();

        // — Papelera de reciclaje —
        ImageButton recycle = new ImageButton(skin, "trash");
        recycle.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                wm.open(WindowManager.AppType.RECYCLE_BIN);
            }
        });
        desktopIcons.add(recycle).size(62, 62).row();
        desktopIcons.add(new Label("Papelera", skin, "desktop-icon-label")).padTop(-15).row();

        // — Archivos en C:\Desktop —
        TextureRegionDrawable fileDrw = new TextureRegionDrawable(fileTexture);
        for (String item : wm.getFs().ls()) {
            if (item.endsWith("/")) continue;
            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
            style.imageUp = fileDrw;
            style.imageDown = fileDrw;
            style.imageOver = fileDrw;
            ImageButton fileBtn = new ImageButton(style);
            fileBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    wm.openEditor(item, wm.getFs());
                }
            });
            desktopIcons.add(fileBtn).size(62, 62).row();
            desktopIcons.add(new Label(item, skin, "desktop-icon-label")).padTop(-15).row();
        }
    }

    private Table buildTaskbar() {
        Table root = new Table();
        root.setFillParent(true);
        root.align(Align.bottomLeft);
        stage.addActor(root);

        Table bar = new Table(skin);
        bar.setBackground("taskbar");
        bar.getBackground().setMinHeight(BAR_H);
        bar.align(Align.left);

        // — Start btn —
        ImageTextButton startBtn = new ImageTextButton("Inicio", skin, "start-btn-img");
        startBtn.getImageCell().padRight(4f).padLeft(-5);
        startBtn.pad(2);
        bar.add(startBtn).width(90).padLeft(2).height(BAR_H - 7.5f);

        // — Ventanas abiertas —
        Table btnBar = new Table();
        btnBar.left();
        bar.add(btnBar).expandX().left();

        // — Reloj —
        Container<Label> clockC = new Container<>(clock);
        clockC.background(skin.getDrawable("btn-checked"));
        clockC.pad(2, 8, 2, 8);
        bar.add(clockC).padRight(2).height(BAR_H - 7.5f);

        root.add(bar).growX().height(BAR_H);

        // Toggle Start menu
        startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                boolean showing = startMenu.isVisible();
                if (!showing) {
                    startMenu.pack();
                    startMenu.setWidth(200f);
                    Vector2 pos = new Vector2();
                    startBtn.localToStageCoordinates(pos);
                    startMenu.setPosition(pos.x, pos.y + startBtn.getHeight());
                }
                startMenu.setVisible(!showing);
            }
        });

        return btnBar;
    }

    private void updateClock() {
        clock.setText(LocalTime.now().format(FMT));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualizo iconos si cambió el FS
        refreshDesktop();

        // Reloj cada segundo
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
