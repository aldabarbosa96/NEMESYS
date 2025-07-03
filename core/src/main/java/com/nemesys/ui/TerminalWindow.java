package com.nemesys.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class TerminalWindow extends BaseWindow {

    private final WindowManager manager;
    private final Skin skin;
    private final Table terminal;
    private final ScrollPane scroll;
    private final TextField input;
    private Table promptRow;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public TerminalWindow(Skin skin, WindowManager manager) {
        super("Terminal", skin, WindowManager.AppType.TERMINAL, manager);
        this.manager = manager;
        this.skin    = skin;
        ensureBlackDrawable();
        ensureStyles();

        /* estilo de la ventana: fondo negro sólido */
        WindowStyle plain = new WindowStyle(skin.get(WindowStyle.class));
        plain.background = skin.getDrawable("black");
        setStyle(plain);

        terminal = new Table();
        terminal.top().defaults().align(Align.left).padLeft(2f);
        terminal.setBackground(skin.getDrawable("black"));

        ScrollPane.ScrollPaneStyle spStyle = new ScrollPane.ScrollPaneStyle();
        spStyle.background = skin.getDrawable("black");
        scroll = new ScrollPane(terminal, spStyle);
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);

        input = new TextField("", skin, "terminal-field");
        input.setFocusTraversal(false);
        input.setTextFieldListener((tf, c) -> {
            if (c == '\n' || c == '\r') {
                String cmd = tf.getText().trim();
                tf.setText("");
                if (!cmd.isEmpty()) execute(cmd);
            }
        });

        defaults().pad(4f);
        add(scroll).prefWidth(480).prefHeight(240).grow();
        addPrompt();
        pack();
        setPosition(120, 140);
    }

    /* registra un drawable negro una sola vez en el skin */
    private void ensureBlackDrawable() {
        if (!skin.has("black", Drawable.class)) {
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(Color.BLACK);
            pm.fill();
            skin.add("black", new TextureRegionDrawable(new Texture(pm)), Drawable.class);
            pm.dispose();
        }
    }

    private void ensureStyles() {
        if (!skin.has("terminal-label", Label.LabelStyle.class)) {
            Label.LabelStyle ls = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
            ls.fontColor = Color.GREEN;
            skin.add("terminal-label", ls);
        }
        if (!skin.has("terminal-field", TextField.TextFieldStyle.class)) {
            TextField.TextFieldStyle ts = new TextField.TextFieldStyle(skin.get(TextField.TextFieldStyle.class));
            ts.fontColor = Color.GREEN;
            ts.cursor = skin.newDrawable("cursor", Color.GREEN);
            ts.background = null;
            ts.selection = skin.newDrawable("black", Color.BLACK);
            skin.add("terminal-field", ts);
        }
    }

    private void addPrompt() {
        promptRow = new Table();
        promptRow.add(new Label("$", skin, "terminal-label")).padRight(4f);
        promptRow.add(input).growX();
        terminal.add(promptRow).growX().row();
        scrollToEnd();
        if (getStage() != null) getStage().setKeyboardFocus(input);
    }

    private void writeLine(String txt) {
        terminal.add(new Label(txt, skin, "terminal-label")).growX().row();
        scrollToEnd();
    }

    private void scrollToEnd() {
        scroll.layout();
        scroll.setScrollPercentY(100);
    }

    private void execute(String line) {
        promptRow.clearChildren();
        promptRow.add(new Label("$ " + line, skin, "terminal-label")).growX();

        String[] parts = line.split(" ", 2);
        String cmd = parts[0];
        String arg = parts.length > 1 ? parts[1] : "";

        switch (cmd) {
            case "help":
                writeLine("Commands: help, echo, time, clear, exit");
                break;
            case "echo":
                writeLine(arg);
                break;
            case "time":
                writeLine(LocalTime.now().format(FMT));
                break;
            case "clear":
                terminal.clearChildren();
                break;
            case "exit":
                manager.close(WindowManager.AppType.TERMINAL);
                return;
            default:
                writeLine("Unknown command: " + cmd);
        }
        addPrompt();
    }
}
