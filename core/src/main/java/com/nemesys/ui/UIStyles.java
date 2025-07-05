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
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

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
        // Taskbar usa ahora exactamente el gris FACE (mismo que botón Inicio)
        sk.add("taskbar", flat(FACE), Drawable.class);
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
        // restauramos los 25×25 originales para Explorer/Terminal/Editor
        sk.add("icon-explorer", icon("icons/explorador.png", 25, 25), Drawable.class);
        sk.add("icon-terminal", icon("icons/terminal.png", 25, 25), Drawable.class);
        sk.add("icon-editor", icon("icons/editorTexto.png", 25, 25), Drawable.class);
        sk.add("icon-logo", icon("icons/logo.png", 25, 25), Drawable.class);

        // ── Bisel 3D para botones ─────────────────────────────────
        sk.add("btn-up", new NinePatchDrawable(makeBtnBg(false)), Drawable.class);
        sk.add("btn-down", new NinePatchDrawable(makeBtnBg(true)), Drawable.class);
        sk.add("btn-checked", new NinePatchDrawable(makeBtnChecked()), Drawable.class);
        sk.add("btn-light", new NinePatchDrawable(makeBtnLight()), Drawable.class);
        sk.add("btn-checked-light", new NinePatchDrawable(makeBtnCheckedLight()), Drawable.class);


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
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle(sk.getDrawable("btn-up"), sk.getDrawable("btn-down"), null, sk.getFont("font-win95"));
        btnStyle.fontColor = TEXT;
        sk.add("win95", btnStyle);
        sk.add("start-btn", btnStyle);

        ImageTextButton.ImageTextButtonStyle startImgStyle = new ImageTextButton.ImageTextButtonStyle(btnStyle);
        startImgStyle.imageUp = sk.getDrawable("icon-logo");
        startImgStyle.imageDown = sk.getDrawable("icon-logo");
        startImgStyle.imageOver = sk.getDrawable("icon-logo");
        startImgStyle.imageDisabled = sk.getDrawable("icon-logo");
        sk.add("start-btn-img", startImgStyle);

        sk.add("win95-window", btnStyle);
        sk.add("default", btnStyle, TextButton.TextButtonStyle.class);

        // ── ImageButton styles ────────────────────────────────────
        ImageButton.ImageButtonStyle imgBase = new ImageButton.ImageButtonStyle();
        imgBase.up = sk.getDrawable("face");
        imgBase.down = sk.getDrawable("shadow");

        ImageButton.ImageButtonStyle back = new ImageButton.ImageButtonStyle(imgBase);
        back.imageUp = sk.getDrawable("icon-back");
        sk.add("nav-back", back);

        ImageButton.ImageButtonStyle home = new ImageButton.ImageButtonStyle(imgBase);
        home.imageUp = sk.getDrawable("icon-home");
        sk.add("nav-home", home);

        ImageButton.ImageButtonStyle save = new ImageButton.ImageButtonStyle(imgBase);
        save.imageUp = sk.getDrawable("icon-save");
        sk.add("save", save);

        ImageButton.ImageButtonStyle saveAs = new ImageButton.ImageButtonStyle(imgBase);
        saveAs.imageUp = sk.getDrawable("icon-saveAs");
        sk.add("saveAs", saveAs);

        ImageButton.ImageButtonStyle restore = new ImageButton.ImageButtonStyle(imgBase);
        restore.imageUp = sk.getDrawable("icon-restore");
        sk.add("restore", restore);

        ImageButton.ImageButtonStyle delete = new ImageButton.ImageButtonStyle(imgBase);
        delete.imageUp = sk.getDrawable("icon-delete");
        sk.add("delete", delete);

        ImageButton.ImageButtonStyle trashBtn = new ImageButton.ImageButtonStyle();
        trashBtn.imageUp = sk.getDrawable("trash");
        trashBtn.imageDown = sk.getDrawable("trash");
        sk.add("trash", trashBtn);

        // ── List, ScrollPane, TextField defaults ─────────────────
        sk.add("default", new List.ListStyle(font14, Color.WHITE, TEXT, sk.getDrawable("blue")));
        sk.add("default", new ScrollPane.ScrollPaneStyle());
        TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle(font14, TEXT, sk.getDrawable("black"), null, sk.getDrawable("white"));
        sk.add("default", tfStyle);

        // ── Start Menu items & icon styles ────────────────────────
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

    // ───────── Métodos auxiliares ───────────────────────────────

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

    private static NinePatch makeBtnChecked() {
        int S = 32, B = 4;
        Pixmap pm = new Pixmap(S, S, Format.RGBA8888);
        // fondo plano FACE
        pm.setColor(Color.valueOf("C0C0C0"));
        pm.fill();
        // bisel hundido: top/left en SHADOW, bottom/right en HILITE
        pm.setColor(Color.valueOf("808080")); // SHADOW
        pm.drawLine(0, 0, S - 2, 0);
        pm.drawLine(0, 0, 0, S - 2);
        pm.setColor(Color.valueOf("FFFFFF")); // HILITE
        pm.drawLine(0, S - 1, S - 1, S - 1);
        pm.drawLine(S - 1, 0, S - 1, S - 1);
        NinePatch np = new NinePatch(new Texture(pm), B, B, B, B);
        pm.dispose();
        return np;
    }

    private static NinePatch makeBtnLight() {
        int S = 32, B = 4;
        Pixmap pm = new Pixmap(S, S, Format.RGBA8888);

        pm.setColor(Color.valueOf("D0D0D0")); // fondo gris claro
        pm.fill();

        // BIS**EL SALIENTE**: hilite arriba/izq, darkshdw abajo/der
        pm.setColor(Color.valueOf("FFFFFF")); // HILITE
        pm.drawLine(0, 0, S - 2, 0);
        pm.drawLine(0, 0, 0, S - 2);
        pm.setColor(Color.valueOf("404040")); // DARKSHDW
        pm.drawLine(0, S - 1, S - 1, S - 1);
        pm.drawLine(S - 1, 0, S - 1, S - 2);

        NinePatch np = new NinePatch(new Texture(pm), B, B, B, B);
        pm.dispose();
        return np;
    }

    private static NinePatch makeBtnCheckedLight() {
        int S = 32, B = 4;
        Pixmap pm = new Pixmap(S, S, Format.RGBA8888);
        // fondo más clarito que FACE
        pm.setColor(Color.valueOf("D0D0D0"));
        pm.fill();
        // bisel hundido: top/left en SHADOW, bottom/right en HILITE
        pm.setColor(SHADOW);
        pm.drawLine(0, 0, S - 2, 0);
        pm.drawLine(0, 0, 0, S - 2);
        pm.setColor(HILITE);
        pm.drawLine(0, S - 1, S - 1, S - 1);
        pm.drawLine(S - 1, 0, S - 1, S - 1);
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
