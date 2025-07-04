package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.Align;

import java.util.function.Consumer;

public final class StartMenu extends Table {
    private static final float ITEM_HEIGHT = 48f;
    private static final float PAD_H = 4f;
    private static final float TABLE_PAD_V = 2f;
    private static final float ICON_TEXT_PAD = 6f;
    private static final float ICON_SIZE = 32f;

    public StartMenu(Skin skin, Consumer<WindowManager.AppType> launcher) {
        super(skin);

        // ─── Fondo y padding de la tabla ──────────────────────────
        setBackground("menu-bg");
        pad(TABLE_PAD_V);

        // Ya no usamos padLeft global aquí, para controlar cada botón por separado:
        defaults().growX().height(ITEM_HEIGHT).align(Align.left);

        // ─── Explorador ───────────────────────────────────────────
        ImageTextButton explorer = new ImageTextButton("Explorador", skin, "menu-item-explorer");
        explorer.align(Align.left);
        Cell<?> imgCellE = explorer.getImageCell();
        imgCellE.padLeft(PAD_H).padRight(ICON_TEXT_PAD).size(ICON_SIZE, ICON_SIZE);
        explorer.getLabelCell().padLeft(0);

        explorer.addListener(e -> {
            if ("touchDown".equals(e.toString())) {
                launcher.accept(WindowManager.AppType.FILE_EXPLORER);
                setVisible(false);
            }
            return true;
        });

        // ─── Terminal ──────────────────────────────────────────────
        ImageTextButton terminal = new ImageTextButton("Terminal", skin, "menu-item-terminal");
        terminal.align(Align.left);
        Cell<?> imgCellT = terminal.getImageCell();
        imgCellT.padLeft(PAD_H).padRight(ICON_TEXT_PAD).size(ICON_SIZE, ICON_SIZE);
        terminal.getLabelCell().padLeft(0);

        terminal.addListener(e -> {
            if ("touchDown".equals(e.toString())) {
                launcher.accept(WindowManager.AppType.TERMINAL);
                setVisible(false);
            }
            return true;
        });

        // ─── Editor ────────────────────────────────────────────────
        ImageTextButton editor = new ImageTextButton("Editor", skin, "menu-item-editor");
        editor.align(Align.left);
        Cell<?> imgCellD = editor.getImageCell();
        imgCellD.padLeft(PAD_H).padRight(ICON_TEXT_PAD).size(ICON_SIZE, ICON_SIZE);
        editor.getLabelCell().padLeft(0);

        editor.addListener(e -> {
            if ("touchDown".equals(e.toString())) {
                launcher.accept(WindowManager.AppType.TEXT_EDITOR);
                setVisible(false);
            }
            return true;
        });

        // ─── Añadir botones al menú ────────────────────────────────
        add(explorer).row();
        add(terminal).row();
        add(editor).row();
    }
}
