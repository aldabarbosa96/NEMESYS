// core/src/main/java/com/nemesys/ui/UIStyles.java
package com.nemesys.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public final class UIStyles {

    private UIStyles() {
    }

    public static Skin create() {
        Skin skin = new Skin();

        /* ────── Colores base ────── */
        Color gray = Color.valueOf("C0C0C0");
        Color dGray = Color.valueOf("808080");
        Color white = Color.WHITE;
        Color black = Color.BLACK;
        Color blue = Color.valueOf("000080");

        /* ────── Drawables planos 1×1 ────── */
        skin.add("gray", flat(gray), Drawable.class);
        skin.add("dgray", flat(dGray), Drawable.class);
        skin.add("white", flat(white), Drawable.class);
        skin.add("black", flat(black), Drawable.class);
        skin.add("blue", flat(blue), Drawable.class);
        skin.add("cursor", skin.newDrawable("white", white), Drawable.class);
        skin.add("wallpaper", skin.newDrawable("gray"), Drawable.class);
        skin.add("taskbar", skin.newDrawable("dgray"), Drawable.class);
        skin.add("title", skin.getDrawable("blue"), Drawable.class);

        /* ────── Iconos (24 px lógicos) ────── */
        skin.add("icon-back", icon("icons/back.png", 24), Drawable.class);
        skin.add("icon-home", icon("icons/home.png", 24), Drawable.class);
        skin.add("icon-save", icon("icons/save.png", 24), Drawable.class);
        skin.add("icon-saveAs", icon("icons/saveAs.png", 24), Drawable.class);

        /* ────── Bordes Win-95 para botones ────── */
        skin.add("btn-up", bordered(gray, black, 24), Drawable.class);
        skin.add("btn-down", bordered(dGray, black, 24), Drawable.class);

        /* ────── Fuentes IBM Plex Sans ────── */
        BitmapFont font14 = font(14);
        BitmapFont font18 = font(18);

        /* ────── Estilos de texto ────── */
        skin.add("title-label", new Label.LabelStyle(font18, white));
        skin.add("default", new Label.LabelStyle(font14, black));

        /* ────── WindowStyle ────── */
        Window.WindowStyle win95Frame = new Window.WindowStyle(font18, white, skin.getDrawable("gray"));
        skin.add("default", win95Frame);   // Window por defecto
        skin.add("win95-frame", win95Frame);   // Marco Win-95 (por si lo necesitas aparte)

        /* ────── TextButtonStyle genérico ────── */
        TextButton.TextButtonStyle btn = new TextButton.TextButtonStyle(skin.getDrawable("gray"), skin.getDrawable("dgray"), skin.getDrawable("white"), font14);
        btn.fontColor = black;
        skin.add("default", btn);
        skin.add("win95-window", btn);   // ⬅⬅ requerido por tu código

        /* ────── Base para ImageButton ────── */
        ImageButton.ImageButtonStyle imgBase = new ImageButton.ImageButtonStyle();
        imgBase.up = skin.getDrawable("btn-up");
        imgBase.down = skin.getDrawable("btn-down");

        /* ────── ImageButtons específicos ────── */
        ImageButton.ImageButtonStyle back = new ImageButton.ImageButtonStyle(imgBase);
        back.imageUp = skin.getDrawable("icon-back");
        skin.add("nav-back", back);

        ImageButton.ImageButtonStyle home = new ImageButton.ImageButtonStyle(imgBase);
        home.imageUp = skin.getDrawable("icon-home");
        skin.add("nav-home", home);

        ImageButton.ImageButtonStyle save = new ImageButton.ImageButtonStyle(imgBase);
        save.imageUp = skin.getDrawable("icon-save");
        skin.add("save", save);

        ImageButton.ImageButtonStyle saveAs = new ImageButton.ImageButtonStyle(imgBase);
        saveAs.imageUp = skin.getDrawable("icon-saveAs");
        skin.add("saveAs", saveAs);

        /* ────── ListStyle ────── */
        List.ListStyle listStyle = new List.ListStyle(font14, white, black, skin.getDrawable("blue"));
        skin.add("default", listStyle);

        /* ────── ScrollPaneStyle / TextFieldStyle ────── */
        skin.add("default", new ScrollPane.ScrollPaneStyle());

        TextField.TextFieldStyle tf = new TextField.TextFieldStyle(font14, black, skin.getDrawable("black"), null, skin.getDrawable("white"));
        skin.add("default", tf);

        return skin;
    }

    /* ─────────── Helpers ─────────── */

    private static Drawable flat(Color c) {
        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(c);
        p.fill();
        TextureRegionDrawable d = new TextureRegionDrawable(new Texture(p));
        p.dispose();
        return d;
    }

    private static Drawable bordered(Color fill, Color border, int size) {
        Pixmap p = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        p.setColor(fill);
        p.fill();
        p.setColor(border);
        p.drawRectangle(0, 0, size, size);
        TextureRegionDrawable d = new TextureRegionDrawable(new Texture(p));
        p.dispose();
        return d;
    }

    private static Drawable icon(String path, int logicalSize) {
        TextureRegionDrawable d = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(path))));
        d.setMinSize(logicalSize, logicalSize);  // tamaño lógico (Scene2D)
        return d;
    }

    private static BitmapFont font(int size) {
        return new com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator(Gdx.files.internal("IBMPlexSans-Regular.ttf")).generateFont(new com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter() {{
            this.size = size;
            this.minFilter = Texture.TextureFilter.Nearest;
            this.magFilter = Texture.TextureFilter.Nearest;
        }});
    }
}
