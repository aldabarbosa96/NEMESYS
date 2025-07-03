package com.nemesys.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.nemesys.NemesysGame;

/**
 * Pantalla de arranque minimal.
 * Simula el “boot” inicial antes de mostrar el escritorio.
 */
public final class BootScreen implements Screen {

    private final NemesysGame game;
    private float timer;
    private static final float BOOT_TIME = 3.5f;

    public BootScreen(NemesysGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        timer += delta;

        // Limpieza de la pantalla
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibuja texto fijo
        game.batch.begin();
        game.font.draw(game.batch, "NEMESYS OS   -->   inicializando...", 40, Gdx.graphics.getHeight() / 2f);
        game.batch.end();

        if (timer >= BOOT_TIME) {
            game.setScreen(new DesktopScreen(game));
        }
    }

    @Override
    public void resize(int w, int h) {
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
    }
}
