package com.nemesys.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;

/**
 * Skin Win-95 pixel-perfect para NEMESYS.
 * - estilo "default" para TextButton,
 * - "terminal-label" en verde,
 * - iconos estándar 24×24,
 * - icono "trash" sin fondo 48×48.
 */
public final class UIStyles {

    private static final Color FACE = Color.valueOf("C0C0C0");
    private static final Color HILITE = Color.valueOf("FFFFFF");
    private static final Color SHADOW = Color.valueOf("808080");
    private static final Color DARKSHDW = Color.valueOf("404040");
    private static final Color TITLE_BG = Color.valueOf("000080");
    private static final Color TEXT = Color.valueOf("000000");
    private static final Color GREEN = Color.valueOf("00FF00"); // para terminal

    private UIStyles() { /* utility */ }

    public static Skin create() {
        Skin sk = new Skin();

        // ── Fuentes IBM Plex Sans ─────────────────────────────────
        BitmapFont font14 = font(14);
        BitmapFont font18 = font(18);
        sk.add("font-win95", font14, BitmapFont.class);
        sk.add("font-win95-title", font18, BitmapFont.class);

        // ── Drawables planos ───────────────────────────────────────
        sk.add("white", flat(Color.WHITE), Drawable.class);
        sk.add("black", flat(Color.BLACK), Drawable.class);
        sk.add("face", flat(FACE), Drawable.class);
        sk.add("hilite", flat(HILITE), Drawable.class);
        sk.add("shadow", flat(SHADOW), Drawable.class);
        sk.add("darkshdw", flat(DARKSHDW), Drawable.class);
        sk.add("blue", flat(TITLE_BG), Drawable.class);

        // ── Cursores / fondos ─────────────────────────────────────
        sk.add("cursor", flat(Color.BLACK), Drawable.class);
        sk.add("taskbar", panel(Color.valueOf("808080")), Drawable.class);
        sk.add("menu-bg", panel(FACE), Drawable.class);
        sk.add("title", sk.getDrawable("blue"), Drawable.class);

        // ── Iconos 24px estándar ───────────────────────────────────
        sk.add("icon-back", icon("icons/back.png", 28, 28), Drawable.class);
        sk.add("icon-home", icon("icons/home.png", 28, 28), Drawable.class);
        sk.add("icon-save", icon("icons/save.png", 28, 28), Drawable.class);
        sk.add("icon-saveAs", icon("icons/saveAs.png", 28, 28), Drawable.class);
        sk.add("icon-restore", icon("icons/restore.png", 30, 28), Drawable.class);
        sk.add("icon-delete", icon("icons/delete.png", 30, 28), Drawable.class);
        sk.add("trash", icon("icons/papelera.png", 75, 75), Drawable.class);
        sk.add("icon-explorer", icon("icons/explorador.png", 25, 25), Drawable.class);
        sk.add("icon-terminal", icon("icons/terminal.png", 25, 25), Drawable.class);
        sk.add("icon-editor", icon("icons/editorTexto.png", 25, 25), Drawable.class);

        // ── Bisel 3D para botones ─────────────────────────────────
        sk.add("btn-up", bevel(FACE, HILITE, DARKSHDW), Drawable.class);
        sk.add("btn-down", bevel(FACE, DARKSHDW, HILITE), Drawable.class);

        // ── Label styles ───────────────────────────────────────────
        sk.add("win95-label-black", new Label.LabelStyle(font14, Color.BLACK));
        sk.add("win95-label-blue", new Label.LabelStyle(font14, TITLE_BG));
        sk.add("title-label", new Label.LabelStyle(font18, Color.WHITE));
        sk.add("default", new Label.LabelStyle(font14, TEXT));

        // ── estilo para terminal ───────────────────────────────────
        sk.add("terminal-label", new Label.LabelStyle(font14, GREEN));

        // ── Window style ──────────────────────────────────────────
        Window.WindowStyle winStyle = new Window.WindowStyle(font18, Color.WHITE, new NinePatchDrawable(makeFrame()));
        sk.add("win95-frame", winStyle);
        sk.add("default", winStyle); // para Window también

        // ── TextButton style Win95 por defecto ────────────────────
        NinePatchDrawable up = new NinePatchDrawable(makeBtnBg(false));
        NinePatchDrawable down = new NinePatchDrawable(makeBtnBg(true));
        TextButtonStyle btnStyle = new TextButtonStyle(up, down, null, sk.getFont("font-win95"));
        btnStyle.fontColor = TEXT;
        sk.add("win95", btnStyle);
        sk.add("start-btn", btnStyle);
        sk.add("win95-window", btnStyle);
        sk.add("default", btnStyle, TextButtonStyle.class);

        // ── ImageButton styles ────────────────────────────────────
        ImageButtonStyle imgBase = new ImageButtonStyle();
        imgBase.up = sk.getDrawable("face");
        imgBase.down = sk.getDrawable("shadow");

        ImageButtonStyle back = new ImageButtonStyle(imgBase);
        back.imageUp = sk.getDrawable("icon-back");
        sk.add("nav-back", back);

        ImageButtonStyle home = new ImageButtonStyle(imgBase);
        home.imageUp = sk.getDrawable("icon-home");
        sk.add("nav-home", home);

        ImageButtonStyle save = new ImageButtonStyle(imgBase);
        save.imageUp = sk.getDrawable("icon-save");
        sk.add("save", save);

        ImageButtonStyle saveAs = new ImageButtonStyle(imgBase);
        saveAs.imageUp = sk.getDrawable("icon-saveAs");
        sk.add("saveAs", saveAs);

        ImageButtonStyle restore = new ImageButtonStyle(imgBase);
        restore.imageUp = sk.getDrawable("icon-restore");
        sk.add("restore", restore);

        ImageButtonStyle delete = new ImageButtonStyle(imgBase);
        delete.imageUp = sk.getDrawable("icon-delete");
        sk.add("delete", delete);

        ImageButtonStyle trashBtn = new ImageButtonStyle();
        trashBtn.imageUp = sk.getDrawable("trash");
        trashBtn.imageDown = sk.getDrawable("trash");
        sk.add("trash", trashBtn);

        // ── List, ScrollPane, TextField defaults ─────────────────
        sk.add("default", new List.ListStyle(font14, Color.WHITE, TEXT, sk.getDrawable("blue")));
        sk.add("default", new ScrollPane.ScrollPaneStyle());
        TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle(font14, TEXT, sk.getDrawable("black"), null, sk.getDrawable("white"));
        sk.add("default", tfStyle);

        // ── NUEVO: Style para ítems del Start Menu ────────────────
        sk.add("menu-item-up", flat(FACE), Drawable.class);
        sk.add("menu-item-over", flat(TITLE_BG), Drawable.class);
        ImageTextButton.ImageTextButtonStyle baseMenuItem = new ImageTextButton.ImageTextButtonStyle();
        baseMenuItem.up = sk.getDrawable("menu-item-up");
        baseMenuItem.over = sk.getDrawable("menu-item-over");
        baseMenuItem.down = sk.getDrawable("menu-item-over");
        baseMenuItem.font = sk.getFont("font-win95");
        baseMenuItem.fontColor = Color.BLACK;
        baseMenuItem.overFontColor = Color.WHITE;
        baseMenuItem.downFontColor = Color.WHITE;
        sk.add("menu-item", baseMenuItem);

        // ── NUEVO: estilos con iconos ─────────────────────────────
        ImageTextButton.ImageTextButtonStyle explorerStyle = new ImageTextButton.ImageTextButtonStyle(baseMenuItem);
        explorerStyle.imageUp = sk.getDrawable("icon-explorer");
        explorerStyle.imageOver = sk.getDrawable("icon-explorer");
        explorerStyle.imageDown = sk.getDrawable("icon-explorer");
        sk.add("menu-item-explorer", explorerStyle);

        ImageTextButton.ImageTextButtonStyle terminalStyle = new ImageTextButton.ImageTextButtonStyle(baseMenuItem);
        terminalStyle.imageUp = sk.getDrawable("icon-terminal");
        terminalStyle.imageOver = sk.getDrawable("icon-terminal");
        terminalStyle.imageDown = sk.getDrawable("icon-terminal");
        sk.add("menu-item-terminal", terminalStyle);

        ImageTextButton.ImageTextButtonStyle editorStyle = new ImageTextButton.ImageTextButtonStyle(baseMenuItem);
        editorStyle.imageUp = sk.getDrawable("icon-editor");
        editorStyle.imageOver = sk.getDrawable("icon-editor");
        editorStyle.imageDown = sk.getDrawable("icon-editor");
        sk.add("menu-item-editor", editorStyle);

        return sk;
    }

    // ───────── Métodos auxiliares ─────────

    private static Drawable flat(Color c) {
        Pixmap p = new Pixmap(1, 1, Format.RGBA8888);
        p.setColor(c);
        p.fill();
        TextureRegionDrawable d = new TextureRegionDrawable(new Texture(p));
        p.dispose();
        return d;
    }

    private static Drawable panel(Color fill) {
        Pixmap p = new Pixmap(3, 3, Format.RGBA8888);
        p.setColor(Color.BLACK);
        p.drawRectangle(0, 0, 3, 3);
        p.setColor(fill);
        p.drawRectangle(1, 1, 1, 1);
        NinePatch np = new NinePatch(new Texture(p), 1, 1, 1, 1);
        p.dispose();
        return new NinePatchDrawable(np);
    }

    private static Drawable bevel(Color face, Color tl, Color br) {
        int S = 24;
        Pixmap p = new Pixmap(S, S, Format.RGBA8888);
        p.setColor(face);
        p.fill();
        p.setColor(tl);
        p.drawLine(0, 0, S - 2, 0);
        p.drawLine(0, 0, 0, S - 2);
        p.setColor(br);
        p.drawLine(S - 1, 1, S - 1, S - 1);
        p.drawLine(1, S - 1, S - 1, S - 1);
        TextureRegionDrawable d = new TextureRegionDrawable(new Texture(p));
        p.dispose();
        return d;
    }

    private static NinePatch makeFrame() {
        int S = 32, B = 6;
        Pixmap pm = new Pixmap(S, S, Format.RGBA8888);
        pm.setColor(FACE);
        pm.fill();
        pm.setColor(HILITE);
        pm.drawLine(0, 0, S - 1, 0);
        pm.drawLine(0, 0, 0, S - 1);
        pm.setColor(DARKSHDW);
        pm.drawLine(0, S - 1, S - 1, S - 1);
        pm.drawLine(S - 1, 0, S - 1, S - 1);
        pm.setColor(SHADOW);
        pm.drawRectangle(1, 1, S - 2, S - 2);
        NinePatch np = new NinePatch(new Texture(pm), B, B, B, B);
        pm.dispose();
        return np;
    }

    private static NinePatch makeBtnBg(boolean pressed) {
        int S = 32, B = 4;
        Pixmap pm = new Pixmap(S, S, Format.RGBA8888);
        pm.setColor(FACE);
        pm.fill();
        pm.setColor(pressed ? SHADOW : HILITE);
        pm.drawLine(0, 0, S - 2, 0);
        pm.drawLine(0, 1, 0, S - 2);
        pm.setColor(DARKSHDW);
        pm.drawLine(0, S - 1, S - 1, S - 1);
        pm.drawLine(S - 1, 0, S - 1, S - 2);
        NinePatch np = new NinePatch(new Texture(pm), B, B, B, B);
        pm.dispose();
        return np;
    }

    private static TextureRegionDrawable icon(String path, int w, int h) {
        Texture tex = new Texture(Gdx.files.internal(path));
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        TextureRegionDrawable d = new TextureRegionDrawable(new TextureRegion(tex));
        d.setMinWidth(w);
        d.setMinHeight(h);
        return d;
    }

    private static BitmapFont font(int size) {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("IBMPlexSans-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = size;
        p.minFilter = Texture.TextureFilter.Nearest;
        p.magFilter = Texture.TextureFilter.Nearest;
        BitmapFont bf = gen.generateFont(p);
        gen.dispose();
        return bf;
    }
}
