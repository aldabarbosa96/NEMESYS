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

        /* colores */
        Color gray = Color.valueOf("C0C0C0"), dGray = Color.valueOf("808080"), white = Color.WHITE, black = Color.BLACK, blue = Color.valueOf("000080");

        /* drawables 1×1 */
        skin.add("gray", flat(gray), Drawable.class);
        skin.add("dgray", flat(dGray), Drawable.class);
        skin.add("white", flat(white), Drawable.class);
        skin.add("black", flat(black), Drawable.class);
        skin.add("blue", flat(blue), Drawable.class);
        skin.add("cursor", skin.newDrawable("white", white), Drawable.class);
        skin.add("wallpaper", skin.newDrawable("gray"), Drawable.class);
        skin.add("taskbar", skin.newDrawable("dgray"), Drawable.class);
        skin.add("title", skin.getDrawable("blue"), Drawable.class);

        /* iconos navegación */
        skin.add("icon-back", new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/back.png")))), Drawable.class);
        skin.add("icon-home", new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/home.png")))), Drawable.class);

        /* bordes Win-95 para botones (24 px) */
        skin.add("btn-up", bordered(gray, black, 24), Drawable.class);
        skin.add("btn-down", bordered(dGray, black, 24), Drawable.class);

        /* fuentes IBM Plex Sans */
        BitmapFont font14 = font(14);
        BitmapFont font18 = font(18);

        /* estilo de título (18 px blanco) */
        skin.add("title-label", new Label.LabelStyle(font18, white));

        /* WindowStyle */
        Window.WindowStyle ws = new Window.WindowStyle(font18, white, skin.getDrawable("gray"));
        skin.add("default", ws);

        /* TextButton genérico */
        TextButton.TextButtonStyle btn = new TextButton.TextButtonStyle(skin.getDrawable("gray"), skin.getDrawable("dgray"), skin.getDrawable("white"), font14);
        btn.fontColor = black;
        skin.add("default", btn);
        skin.add("win95-window", btn);

        /* ImageButton nav-back / nav-home */
        ImageButton.ImageButtonStyle back = new ImageButton.ImageButtonStyle();
        back.up = skin.getDrawable("btn-up");
        back.down = skin.getDrawable("btn-down");
        back.imageUp = skin.getDrawable("icon-back");
        skin.add("nav-back", back);

        ImageButton.ImageButtonStyle home = new ImageButton.ImageButtonStyle(back);
        home.imageUp = skin.getDrawable("icon-home");
        skin.add("nav-home", home);

        /* Label y List */
        skin.add("default", new Label.LabelStyle(font14, black));
        List.ListStyle ls = new List.ListStyle(font14, white, black, skin.getDrawable("blue"));
        skin.add("default", ls);

        /* ScrollPane / TextField */
        skin.add("default", new ScrollPane.ScrollPaneStyle());
        TextField.TextFieldStyle tf = new TextField.TextFieldStyle(font14, black, skin.getDrawable("black"), null, skin.getDrawable("white"));
        skin.add("default", tf);

        return skin;
    }

    /* helper drawables */
    private static Drawable flat(Color c) {
        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(c);
        p.fill();
        TextureRegionDrawable d = new TextureRegionDrawable(new Texture(p));
        p.dispose();
        return d;
    }

    private static Drawable bordered(Color fill, Color border, int s) {
        Pixmap p = new Pixmap(s, s, Pixmap.Format.RGBA8888);
        p.setColor(fill);
        p.fill();
        p.setColor(border);
        p.drawRectangle(0, 0, s, s);
        TextureRegionDrawable d = new TextureRegionDrawable(new Texture(p));
        p.dispose();
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
