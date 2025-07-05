package com.nemesys.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;

public final class UIStyles {

    private static final Color FACE = Color.valueOf("C0C0C0");
    private static final Color HILITE = Color.valueOf("FFFFFF");
    private static final Color SHADOW = Color.valueOf("808080");
    private static final Color DARKSHDW = Color.valueOf("404040");
    private static final Color TITLE_BG = Color.valueOf("000080");
    private static final Color TEXT = Color.valueOf("000000");
    private static final Color GREEN = Color.valueOf("00FF00"); // para terminal

    private UIStyles() { /* no instanciable */ }

    public static Skin create() {
        Skin sk = new Skin();

        // ── Fuentes IBM Plex Sans ─────────────────────────────────
        BitmapFont font14 = font("IBMPlexSans-Regular.ttf", 14);
        BitmapFont font18 = font("IBMPlexSans-Regular.ttf", 18);
        // Bold 14px con tracking negativo para negrita más compacta
        FreeTypeFontGenerator boldGen = new FreeTypeFontGenerator(Gdx.files.internal("IBMPlexSans-Bold.ttf"));
        FreeTypeFontParameter bp = new FreeTypeFontParameter();
        bp.size = 17;
        bp.spaceX = -1;
        bp.minFilter = TextureFilter.Nearest;
        bp.magFilter = TextureFilter.Nearest;
        BitmapFont font14Bold = boldGen.generateFont(bp);
        boldGen.dispose();

        sk.add("font-win95", font14, BitmapFont.class);
        sk.add("font-win95-title", font18, BitmapFont.class);
        sk.add("font-win95-bold", font14Bold, BitmapFont.class);

        // ── Drawables planos ───────────────────────────────────────
        sk.add("white", flat(Color.WHITE), Drawable.class);
        sk.add("black", flat(Color.BLACK), Drawable.class);
        sk.add("face", flat(FACE), Drawable.class);
        sk.add("hilite", flat(HILITE), Drawable.class);
        sk.add("shadow", flat(SHADOW), Drawable.class);
        sk.add("darkshdw", flat(DARKSHDW), Drawable.class);
        sk.add("blue", flat(TITLE_BG), Drawable.class);

        // ── Fondos / cursores ──────────────────────────────────────
        sk.add("cursor", flat(Color.BLACK), Drawable.class);
        sk.add("taskbar", panel(FACE), Drawable.class);
        sk.add("menu-bg", panel(FACE), Drawable.class);
        sk.add("title", sk.getDrawable("blue"), Drawable.class);

        // ── Iconos estandar ────────────────────────────────────────
        sk.add("icon-back", icon("icons/back.png", 28, 28), Drawable.class);
        sk.add("icon-home", icon("icons/home.png", 28, 28), Drawable.class);
        sk.add("icon-save", icon("icons/save.png", 28, 28), Drawable.class);
        sk.add("icon-saveAs", icon("icons/saveAs.png", 28, 28), Drawable.class);
        sk.add("icon-restore", icon("icons/restore.png", 30, 28), Drawable.class);
        sk.add("icon-delete", icon("icons/delete.png", 30, 28), Drawable.class);
        sk.add("trash", icon("icons/papelera.png", 60, 60), Drawable.class);
        sk.add("trash-small", icon("icons/papelera.png", 25, 25), Drawable.class);
        sk.add("icon-explorer", icon("icons/explorador.png", 25, 25), Drawable.class);
        sk.add("icon-terminal", icon("icons/terminal.png", 25, 25), Drawable.class);
        sk.add("icon-editor", icon("icons/editorTexto.png", 25, 25), Drawable.class);
        sk.add("icon-logo", icon("icons/logo.png", 26, 26), Drawable.class);

        // ── Bisel 3D botones ───────────────────────────────────────
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
        sk.add("desktop-icon-label", new Label.LabelStyle(font14, Color.WHITE));

        // ── Terminal label ─────────────────────────────────────────
        sk.add("terminal-label", new Label.LabelStyle(font14, GREEN));

        // ── Window style ──────────────────────────────────────────
        Window.WindowStyle winStyle = new Window.WindowStyle(font18, Color.WHITE, new NinePatchDrawable(makeFrame()));
        sk.add("win95-frame", winStyle);
        sk.add("default", winStyle);

        // ── TextButton Win95 por defecto ──────────────────────────
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle(sk.getDrawable("btn-up"), sk.getDrawable("btn-down"), null, sk.getFont("font-win95"));
        btnStyle.fontColor = TEXT;

        sk.add("win95", btnStyle);
        sk.add("start-btn", btnStyle);
        // necesario para Window minimizar/cerrar
        sk.add("win95-window", btnStyle, TextButton.TextButtonStyle.class);
        // default TextButton
        sk.add("default", btnStyle, TextButton.TextButtonStyle.class);

        // ── Start button (Inicio) ─────────────────────────────────
        ImageTextButton.ImageTextButtonStyle startImg = new ImageTextButton.ImageTextButtonStyle(btnStyle);
        startImg.imageUp = sk.getDrawable("icon-logo");
        startImg.imageDown = sk.getDrawable("icon-logo");
        startImg.imageOver = sk.getDrawable("icon-logo");
        startImg.imageDisabled = sk.getDrawable("icon-logo");
        startImg.font = sk.getFont("font-win95-bold");
        sk.add("start-btn-img", startImg);

        // ── Toggle style taskbar ──────────────────────────────────
        TextButton.TextButtonStyle toggle = new TextButton.TextButtonStyle();
        toggle.up = sk.getDrawable("btn-checked-light");
        toggle.down = sk.getDrawable("btn-down");
        toggle.checked = sk.getDrawable("btn-up");
        toggle.checkedOver = sk.getDrawable("btn-up");
        toggle.font = sk.getFont("font-win95-bold");
        toggle.fontColor = Color.BLACK;
        toggle.downFontColor = Color.BLACK;
        toggle.checkedFontColor = Color.BLACK;
        sk.add("win95-toggle", toggle);

        // ── ImageButton styles ────────────────────────────────────
        ImageButton.ImageButtonStyle imgBase = new ImageButton.ImageButtonStyle();
        imgBase.up = sk.getDrawable("face");
        imgBase.down = sk.getDrawable("shadow");

        ImageButton.ImageButtonStyle navBack = new ImageButton.ImageButtonStyle(imgBase);
        navBack.imageUp = sk.getDrawable("icon-back");
        sk.add("nav-back", navBack);

        ImageButton.ImageButtonStyle navHome = new ImageButton.ImageButtonStyle(imgBase);
        navHome.imageUp = sk.getDrawable("icon-home");
        sk.add("nav-home", navHome);

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

        // ── List, ScrollPane, TextField ───────────────────────────
        sk.add("default", new List.ListStyle(font14, Color.WHITE, TEXT, sk.getDrawable("blue")));
        sk.add("default", new ScrollPane.ScrollPaneStyle());

        TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle(font14, TEXT, sk.getDrawable("black"), null, sk.getDrawable("white"));
        sk.add("default", tfStyle);

        // ── StartMenu items ───────────────────────────────────────
        sk.add("menu-item-up", flat(FACE), Drawable.class);
        sk.add("menu-item-over", flat(TITLE_BG), Drawable.class);

        ImageTextButton.ImageTextButtonStyle baseMenu = new ImageTextButton.ImageTextButtonStyle();
        baseMenu.up = sk.getDrawable("menu-item-up");
        baseMenu.over = sk.getDrawable("menu-item-over");
        baseMenu.down = sk.getDrawable("menu-item-over");
        baseMenu.font = sk.getFont("font-win95");
        baseMenu.fontColor = Color.BLACK;
        baseMenu.overFontColor = Color.WHITE;
        baseMenu.downFontColor = Color.WHITE;
        sk.add("menu-item", baseMenu);

        ImageTextButton.ImageTextButtonStyle expl = new ImageTextButton.ImageTextButtonStyle(baseMenu);
        expl.imageUp = sk.getDrawable("icon-explorer");
        expl.imageOver = sk.getDrawable("icon-explorer");
        expl.imageDown = sk.getDrawable("icon-explorer");
        sk.add("menu-item-explorer", expl);

        ImageTextButton.ImageTextButtonStyle term = new ImageTextButton.ImageTextButtonStyle(baseMenu);
        term.imageUp = sk.getDrawable("icon-terminal");
        term.imageOver = sk.getDrawable("icon-terminal");
        term.imageDown = sk.getDrawable("icon-terminal");
        sk.add("menu-item-terminal", term);

        ImageTextButton.ImageTextButtonStyle edit = new ImageTextButton.ImageTextButtonStyle(baseMenu);
        edit.imageUp = sk.getDrawable("icon-editor");
        edit.imageOver = sk.getDrawable("icon-editor");
        edit.imageDown = sk.getDrawable("icon-editor");
        sk.add("menu-item-editor", edit);

        return sk;
    }

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
        int S = 32, B = 5;                     // ← cap inset a 5px en vez de 4
        Pixmap pm = new Pixmap(S, S, Format.RGBA8888);
        pm.setColor(FACE);
        pm.fill();

        // highlight (arriba/izquierda) más grueso
        pm.setColor(pressed ? SHADOW : HILITE);
        pm.drawLine(0, 0, S - 2, 0);
        pm.drawLine(0, 1, S - 3, 1);
        pm.drawLine(0, 1, 0, S - 2);
        pm.drawLine(1, 1, 1, S - 3);

        // shadow (abajo/derecha) más grueso
        pm.setColor(DARKSHDW);
        pm.drawLine(0, S - 1, S - 1, S - 1);
        pm.drawLine(0, S - 2, S - 2, S - 2);
        pm.drawLine(S - 1, 0, S - 1, S - 1);
        pm.drawLine(S - 2, 1, S - 2, S - 2);

        NinePatch np = new NinePatch(new Texture(pm), B, B, B, B);
        pm.dispose();
        return np;
    }

    // 2) Botones “checked” (toggled)
    private static NinePatch makeBtnChecked() {
        int S = 32, B = 5;
        Pixmap pm = new Pixmap(S, S, Format.RGBA8888);
        pm.setColor(FACE);
        pm.fill();

        // shadow en vez de highlight
        pm.setColor(SHADOW);
        pm.drawLine(0, 0, S - 2, 0);
        pm.drawLine(0, 1, S - 3, 1);
        pm.drawLine(0, 0, 0, S - 2);
        pm.drawLine(1, 1, 1, S - 3);

        // highlight abajo/derecha
        pm.setColor(HILITE);
        pm.drawLine(0, S - 1, S - 1, S - 1);
        pm.drawLine(0, S - 2, S - 2, S - 2);
        pm.drawLine(S - 1, 0, S - 1, S - 1);
        pm.drawLine(S - 2, 1, S - 2, S - 2);

        NinePatch np = new NinePatch(new Texture(pm), B, B, B, B);
        pm.dispose();
        return np;
    }

    // 3) Botones “light” (fondo más claro)
    private static NinePatch makeBtnLight() {
        int S = 32, B = 5;
        Pixmap pm = new Pixmap(S, S, Format.RGBA8888);
        pm.setColor(Color.valueOf("D0D0D0"));
        pm.fill();

        // highlight
        pm.setColor(HILITE);
        pm.drawLine(0, 0, S - 2, 0);
        pm.drawLine(0, 1, S - 3, 1);
        pm.drawLine(0, 0, 0, S - 2);
        pm.drawLine(1, 1, 1, S - 3);

        // shadow
        pm.setColor(DARKSHDW);
        pm.drawLine(0, S - 1, S - 1, S - 1);
        pm.drawLine(0, S - 2, S - 2, S - 2);
        pm.drawLine(S - 1, 0, S - 1, S - 1);
        pm.drawLine(S - 2, 1, S - 2, S - 2);

        NinePatch np = new NinePatch(new Texture(pm), B, B, B, B);
        pm.dispose();
        return np;
    }

    // 4) Botones “checked light”
    private static NinePatch makeBtnCheckedLight() {
        int S = 32, B = 5;
        Pixmap pm = new Pixmap(S, S, Format.RGBA8888);
        pm.setColor(Color.valueOf("D0D0D0"));
        pm.fill();

        // shadow
        pm.setColor(SHADOW);
        pm.drawLine(0, 0, S - 2, 0);
        pm.drawLine(0, 1, S - 3, 1);
        pm.drawLine(0, 0, 0, S - 2);
        pm.drawLine(1, 1, 1, S - 3);

        // highlight
        pm.setColor(HILITE);
        pm.drawLine(0, S - 1, S - 1, S - 1);
        pm.drawLine(0, S - 2, S - 2, S - 2);
        pm.drawLine(S - 1, 0, S - 1, S - 1);
        pm.drawLine(S - 2, 1, S - 2, S - 2);

        NinePatch np = new NinePatch(new Texture(pm), B, B, B, B);
        pm.dispose();
        return np;
    }

    private static TextureRegionDrawable icon(String path, int w, int h) {
        Texture tex = new Texture(Gdx.files.internal(path));
        tex.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        TextureRegionDrawable d = new TextureRegionDrawable(new TextureRegion(tex));
        d.setMinWidth(w);
        d.setMinHeight(h);
        return d;
    }

    private static BitmapFont font(String ttfPath, int size) {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(ttfPath));
        FreeTypeFontParameter p = new FreeTypeFontParameter();
        p.size = size;
        p.minFilter = TextureFilter.Nearest;
        p.magFilter = TextureFilter.Nearest;
        BitmapFont bf = gen.generateFont(p);
        gen.dispose();
        return bf;
    }
}
