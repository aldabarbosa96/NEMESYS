package com.nemesys;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nemesys.screens.BootScreen;

/**
 * Punto central de la app LibGDX.
 * Mantiene recursos globales y gestiona el flujo de Screens.
 */
public final class NemesysGame extends Game {
    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font  = new BitmapFont();
        setScreen(new BootScreen(this));
    }

    @Override
    public void dispose() {
        screen.dispose();
        batch.dispose();
        font.dispose();
    }
}
