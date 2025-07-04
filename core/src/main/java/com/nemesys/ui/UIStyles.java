package com.nemesys.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Skin Win-95 pixel-perfect para NEMESYS.
 * — 100 % de la clase, sin omisiones —
 */
public final class UIStyles {

    /* ───────────────── PALETA CLÁSICA ───────────────── */
    private static final Color FACE = Color.valueOf("C0C0C0");  // gris interfaz
    private static final Color HILITE = Color.valueOf("FFFFFF");  // borde claro
    private static final Color SHADOW = Color.valueOf("808080");  // borde sombra
    private static final Color DARKSHDW = Color.valueOf("404040");  // borde sombra oscura
    private static final Color TITLE_BG = Color.valueOf("000080");  // azul cabecera
    private static final Color TEXT = Color.valueOf("000000");  // texto negro

    private UIStyles() { /* utility class */ }

    /* ───────────────── SKIN FACTORY ───────────────── */
    public static Skin create() {
        Skin sk = new Skin();

        /*──────── 1. Fuentes IBM Plex Sans ────────*/
        BitmapFont font14 = font(14);
        BitmapFont font18 = font(18);
        sk.add("font-win95", font14, BitmapFont.class);
        sk.add("font-win95-title", font18, BitmapFont.class);

        /*──────── 2. Drawables planos esenciales ────────*/
        sk.add("white", flat(Color.WHITE), Drawable.class);   // ← evita excepciones “white”
        sk.add("black", flat(Color.BLACK), Drawable.class);
        sk.add("face", flat(FACE), Drawable.class);
        sk.add("hilite", flat(HILITE), Drawable.class);
        sk.add("shadow", flat(SHADOW), Drawable.class);
        sk.add("blue", flat(TITLE_BG), Drawable.class);

        /*──────── 3. Cursores / fondos especiales ────────*/
        sk.add("cursor", flat(TEXT), Drawable.class);
        sk.add("taskbar", panel(SHADOW), Drawable.class);
        sk.add("menu-bg", panel(FACE), Drawable.class);
        sk.add("title", sk.getDrawable("blue"), Drawable.class);

        /*──────── 4. Iconos (24 px lógicos) ────────*/
        sk.add("icon-back", icon("icons/back.png"), Drawable.class);
        sk.add("icon-home", icon("icons/home.png"), Drawable.class);
        sk.add("icon-save", icon("icons/save.png"), Drawable.class);
        sk.add("icon-saveAs", icon("icons/saveAs.png"), Drawable.class);

        /*──────── 5. Botones bisel 3-D ────────*/
        sk.add("btn-up", bevel(FACE, HILITE, DARKSHDW), Drawable.class); // relieve
        sk.add("btn-down", bevel(FACE, DARKSHDW, HILITE), Drawable.class); // hundido

        /*──────── 6. Label styles ────────*/
        sk.add("win95-label-black", new Label.LabelStyle(font14, TEXT));
        sk.add("win95-label-blue", new Label.LabelStyle(font14, TITLE_BG));
        sk.add("title-label", new Label.LabelStyle(font18, Color.WHITE));
        sk.add("default",           new Label.LabelStyle(font14, TEXT));

        /*──────── 7. Ventanas ────────*/
        Window.WindowStyle win = new Window.WindowStyle(font18, Color.WHITE, new NinePatchDrawable(makeFrame()));
        sk.add("default", win);        // Window por defecto
        sk.add("win95-frame", win);        // alias explícito

        /*──────── 8. TextButton genérico plano ────────*/
        TextButton.TextButtonStyle flatBtn = new TextButton.TextButtonStyle(sk.getDrawable("face"), sk.getDrawable("shadow"), sk.getDrawable("face"), font14);
        flatBtn.fontColor = TEXT;
        sk.add("default", flatBtn);

        /*──────── 9. TextButton Win-95 (con bisel) ────────*/
        TextButton.TextButtonStyle winBtn = new TextButton.TextButtonStyle(sk.getDrawable("btn-up"), sk.getDrawable("btn-down"), null, font14);
        winBtn.fontColor = TEXT;
        sk.add("win95-window", winBtn);  // botón “X” de las ventanas
        sk.add("start-btn", winBtn);  // botón “Inicio”

        /*──────── 10. ImageButtons sin marco ────────*/
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

        /*──────── 11. List / ScrollPane / TextField ────────*/
        sk.add("default", new List.ListStyle(font14, Color.WHITE, TEXT, sk.getDrawable("blue")));
        sk.add("default", new ScrollPane.ScrollPaneStyle());

        TextField.TextFieldStyle tf = new TextField.TextFieldStyle(font14, TEXT, sk.getDrawable("black"), null, sk.getDrawable("white"));
        sk.add("default", tf);


        return sk;
    }

    /**
     * 1 × 1 sólido
     */
    private static Drawable flat(Color c) {
        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(c);
        p.fill();
        TextureRegionDrawable d = new TextureRegionDrawable(new Texture(p));
        p.dispose();
        return d;
    }

    /**
     * Panel gris con borde negro 1 px (NinePatch)
     */
    private static Drawable panel(Color fill) {
        Pixmap p = new Pixmap(3, 3, Pixmap.Format.RGBA8888);
        p.setColor(Color.BLACK);
        p.drawRectangle(0, 0, 3, 3);
        p.setColor(fill);
        p.drawRectangle(1, 1, 1, 1);
        NinePatch np = new NinePatch(new Texture(p), 1, 1, 1, 1);
        p.dispose();
        return new NinePatchDrawable(np);
    }

    /** Marco de ventana Win-95 */
    private static NinePatch makeFrame() {
        int S = 32, B = 4;           // ‼  B antes era 6 → ahora 4
        Pixmap pm = new Pixmap(S, S, Pixmap.Format.RGBA8888);
        pm.setColor(FACE);    pm.fill();
        pm.setColor(HILITE);  pm.drawLine(0, 0, S - 1, 0);  pm.drawLine(0, 0, 0, S - 1);
        pm.setColor(DARKSHDW);pm.drawLine(0, S - 1, S - 1, S - 1);
        pm.drawLine(S - 1, 0, S - 1, S - 1);
        pm.setColor(SHADOW);  pm.drawRectangle(1, 1, S - 2, S - 2);
        NinePatch np = new NinePatch(new Texture(pm), B, B, B, B);
        pm.dispose();
        return np;
    }


    /**
     * Bisel 24 × 24 (up/down) con borde negro fino
     */
    private static Drawable bevel(Color face, Color tl, Color br) {
        int S = 24;
        Pixmap pm = new Pixmap(S, S, Pixmap.Format.RGBA8888);
        pm.setColor(face); pm.fill();
        pm.setColor(tl);                // highlight
        pm.drawLine(0, 0, S - 2, 0);
        pm.drawLine(0, 0, 0,     S - 2);
        pm.setColor(br);                // shadow
        pm.drawLine(S - 1, 1, S - 1, S - 1);
        pm.drawLine(1,     S - 1, S - 1, S - 1);
        Drawable d = new TextureRegionDrawable(new Texture(pm));
        pm.dispose();
        return d;
    }

    /**
     * Icono con tamaño lógico 24 × 24
     */
    private static Drawable icon(String path) {
        Texture tex = new Texture(Gdx.files.internal(path));
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        TextureRegionDrawable d = new TextureRegionDrawable(new TextureRegion(tex));
        d.setMinSize(24, 24);
        return d;
    }

    /**
     * Genera una BitmapFont de IBM Plex Sans con filtrado pixel-perfect.
     */
    private static BitmapFont font(int size) {
        com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator gen = new com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator(Gdx.files.internal("IBMPlexSans-Regular.ttf"));

        com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter p = new com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = size;
        p.minFilter = Texture.TextureFilter.Nearest;
        p.magFilter = Texture.TextureFilter.Nearest;

        BitmapFont bf = gen.generateFont(p);
        gen.dispose();
        return bf;
    }

}
