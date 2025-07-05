package com.nemesys.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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

    public DesktopScreen(NemesysGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport(), game.batch);
        this.skin = UIStyles.create();

        // Wallpaper
        Texture wall = new Texture(Gdx.files.internal("wallpaper3.png"));
        Image bg = new Image(wall);
        bg.setFillParent(true);
        bg.setScaling(Scaling.stretch);
        stage.addActor(bg);

        // Desktop icons
        Table desktopIcons = new Table();
        desktopIcons.setFillParent(true);
        desktopIcons.top().left();
        desktopIcons.defaults().pad(15).padLeft(20).align(Align.center);
        ImageButton recycle = new ImageButton(skin, "trash");
        recycle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                wm.open(WindowManager.AppType.RECYCLE_BIN);
            }
        });
        desktopIcons.add(recycle).size(72, 72).row();
        desktopIcons.add(new Label("Papelera", skin)).padTop(-18).row();
        stage.addActor(desktopIcons);

        // Clock label (will be wrapped in container later)
        this.clock = new Label("", skin);

        // Build taskbar (and clock container)
        Table taskButtons = buildTaskbar();

        // Window manager
        this.wm = new WindowManager(stage, skin, taskButtons);

        // Start menu
        this.startMenu = new StartMenu(skin, wm::open);
        startMenu.setVisible(false);
        stage.addActor(startMenu);

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

        // Start button
        ImageTextButton startBtn = new ImageTextButton("Inicio", skin, "start-btn-img");
        startBtn.pad(2, 5, 2, 10);
        bar.add(startBtn).width(90).padLeft(8);

        // Window buttons container
        Table btnBar = new Table();
        btnBar.left();
        bar.add(btnBar).expandX().left();

        // Clock wrapped in bevel border
        Container<Label> clockC = new Container<>(clock);
        clockC.background(skin.getDrawable("btn-up"));
        clockC.pad(2, 8, 2, 8);
        bar.add(clockC).padRight(10);

        root.add(bar).growX().height(BAR_H);

        // Start button listener
        startBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean showing = startMenu.isVisible();
                if (!showing) {
                    startMenu.pack();
                    startMenu.setWidth(200f);
                    Vector2 pos = new Vector2(0, 0);
                    startBtn.localToStageCoordinates(pos);
                    float x = pos.x;
                    float y = pos.y + startBtn.getHeight();
                    startMenu.setPosition(x, y);
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
    }
}
