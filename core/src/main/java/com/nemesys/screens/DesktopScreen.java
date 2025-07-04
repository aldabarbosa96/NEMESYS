package com.nemesys.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nemesys.NemesysGame;
import com.nemesys.ui.StartMenu;
import com.nemesys.ui.UIStyles;
import com.nemesys.ui.WindowManager;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class DesktopScreen implements Screen {

    private final NemesysGame game;
    private final Stage stage;
    private final Skin skin;
    private final WindowManager wm;
    private final StartMenu startMenu;
    private final Label clock;
    private float acc;
    private static final float BAR_H = 48f;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

    public DesktopScreen(NemesysGame g) {
        game = g;
        stage = new Stage(new ScreenViewport(), g.batch);
        skin = UIStyles.create();

        Texture wall = new Texture(Gdx.files.internal("wallpaper1.png"));
        Image bg = new Image(wall);
        bg.setFillParent(true);
        bg.setScaling(Scaling.stretch);
        stage.addActor(bg);

        clock = new Label("", skin);
        Table taskButtons = buildTaskbar();
        wm = new WindowManager(stage, skin, taskButtons);

        startMenu = new StartMenu(skin, wm::open);
        startMenu.setVisible(false);
        stage.addActor(startMenu);
        startMenu.setPosition(0, BAR_H);

        Gdx.input.setInputProcessor(stage);
        updateClock();
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

        /* ───── Botón Inicio ───── */
        TextButton startBtn = new TextButton("Inicio", skin, "start-btn");
        startBtn.pad(2, 10, 2, 10);
        bar.add(startBtn).width(90).padLeft(8);

        /* ───── Botones de ventana ───── */
        Table btnBar = new Table();
        btnBar.left();
        bar.add(btnBar).expandX().left();

        /* ───── Reloj ───── */
        bar.add(clock).padRight(10);

        root.add(bar).growX().height(BAR_H);

        startBtn.addListener(e -> {
            if (e.toString().equals("touchDown")) startMenu.setVisible(!startMenu.isVisible());
            return true;
        });

        return btnBar;
    }


    private void updateClock() {
        clock.setText(LocalTime.now().format(FMT));
    }

    @Override
    public void render(float d) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        acc += d;
        if (acc >= 1f) {
            acc = 0;
            updateClock();
        }
        stage.act(d);
        stage.draw();
    }

    @Override
    public void resize(int w, int h) {
        stage.getViewport().update(w, h, true);
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
    }
}
