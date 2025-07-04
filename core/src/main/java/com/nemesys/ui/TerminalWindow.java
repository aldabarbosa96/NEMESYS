package com.nemesys.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.nemesys.fs.FileSystemSim;

import java.time.format.DateTimeFormatter;

public final class TerminalWindow extends BaseWindow {

    private final WindowManager manager;
    private final FileSystemSim fs;
    private final Skin skin;

    private final Table terminal;
    private final ScrollPane scroll;
    private final TextField input;
    private Table promptRow;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public TerminalWindow(Skin skin, WindowManager manager, FileSystemSim fs) {
        super("Terminal", skin, WindowManager.AppType.TERMINAL, manager);
        this.manager = manager;
        this.fs = fs;
        this.skin = skin;
        ensureBlack();
        ensureStyles();

        WindowStyle ws = new WindowStyle(skin.get(WindowStyle.class));
        ws.background = skin.getDrawable("black");
        setStyle(ws);

        terminal = new Table();
        terminal.top().defaults().align(Align.left).padLeft(2f);
        terminal.setBackground(skin.getDrawable("black"));

        ScrollPane.ScrollPaneStyle sps = new ScrollPane.ScrollPaneStyle();
        sps.background = skin.getDrawable("black");
        scroll = new ScrollPane(terminal, sps);
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);

        input = new TextField("", skin, "terminal-field");
        input.setTextFieldListener((tf, c) -> {
            if (c == '\n' || c == '\r') {
                String cmd = tf.getText().trim();
                tf.setText("");
                if (!cmd.isEmpty()) exec(cmd);
            }
        });

        defaults().pad(4f);
        add(scroll).prefWidth(500).prefHeight(280).grow();
        newPrompt();
        pack();
        setPosition(100, 120);
    }

    private void ensureBlack() {
        if (!skin.has("black", Drawable.class)) {
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(Color.BLACK);
            pm.fill();
            skin.add("black", new TextureRegionDrawable(new Texture(pm)), Drawable.class);
            pm.dispose();
        }
    }

    private void ensureStyles() {
        if (!skin.has("terminal-field", TextField.TextFieldStyle.class)) {
            TextField.TextFieldStyle ts = new TextField.TextFieldStyle(skin.get(TextField.TextFieldStyle.class));
            ts.fontColor = Color.GREEN;
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(Color.GREEN);
            pm.fill();
            TextureRegionDrawable greenCursor = new TextureRegionDrawable(new Texture(pm));
            pm.dispose();
            ts.cursor = greenCursor;
            ts.background = null;
            ts.selection = skin.newDrawable("black", Color.BLACK);
            skin.add("terminal-field", ts);
        }
    }

    private void newPrompt() {
        promptRow = new Table();
        promptRow.add(new Label(fs.pwd() + ">", skin, "terminal-label")).padRight(2f);
        promptRow.add(input).growX();
        terminal.add(promptRow).growX().row();
        scrollToEnd();
        if (getStage() != null) getStage().setKeyboardFocus(input);
    }

    private void writeln(String txt) {
        terminal.add(new Label(txt, skin, "terminal-label")).growX().row();
        scrollToEnd();
    }

    private void scrollToEnd() {
        scroll.layout();
        scroll.setScrollPercentY(100);
    }

    private void exec(String line) {
        // ----- rm (mover a papelera) -----
        String[] parts = line.trim().split("\\s+", 2);
        String cmd = parts[0];
        String arg = parts.length > 1 ? parts[1] : "";

        if ("rm".equals(cmd)) {
            if (arg.isEmpty()) {
                writeln("usage: rm <file>");
            } else {
                manager.moveToRecycle(fs, arg);
            }
            newPrompt();
            return;
        }

        // ----- resto de comandos -----
        final String[] fsCmds = {"ls", "dir", "cd", "pwd", "mkdir", "touch", "cat", "tree", "du", "df"};
        if (java.util.Arrays.asList(fsCmds).contains(cmd)) {
            String result = fs.run(cmd, arg);
            if (result != null && !result.isEmpty()) {
                for (String ln : result.split("\n")) writeln(ln);
            }
            newPrompt();
            return;
        }

        switch (cmd) {
            case "help":
                writeln("Builtin commands:");
                writeln("  dir | ls        list directory");
                writeln("  cd <dir>        change directory");
                writeln("  pwd             print working dir");
                writeln("  tree            recursive listing");
                writeln("  mkdir <dir>     create directory");
                writeln("  touch <file>    create empty file");
                writeln("  cat <file>      show file contents");
                writeln("  du              disk usage summary");
                writeln("  df              filesystem usage");
                writeln("");
                writeln("System / info:");
                writeln("  whoami          current user");
                writeln("  hostname        machine name");
                writeln("  ver             OS version");
                writeln("  uname -a        kernel string");
                writeln("");
                writeln("Network (mock):");
                writeln("  ip -c a         show addresses");
                writeln("  ifconfig        legacy addr info");
                writeln("  ping <host>     fake ping");
                writeln("  tracert <host>  fake traceroute");
                writeln("");
                writeln("Utilities:");
                writeln("  time            current time");
                writeln("  clear | cls     clear screen");
                writeln("  exit            close terminal");
                writeln("  help            this message");
                break;

            case "time":
                writeln(java.time.LocalTime.now().format(FMT));
                break;

            case "clear":
            case "cls":
                terminal.clearChildren();
                break;

            case "exit":
                manager.close(this);
                return;

            case "whoami":
                writeln("david");
                break;

            case "hostname":
                writeln("NEMESYS-PC");
                break;

            case "ver":
                writeln("NEMESYS OS version 0.3 (1996)");
                break;

            case "uname":
                if ("-a".equals(arg)) writeln("NEMESYS 0.3 i586 (bogus)");
                else writeln("usage: uname -a");
                break;

            case "ip":
                writeln("eth0:  inet 192.168.0.42/24  brd 192.168.0.255  ...");
                break;

            case "ifconfig":
                writeln("eth0      Link encap:Ethernet  HWaddr 00:0A:E6:3E:FD:E1");
                writeln("          inet addr:192.168.0.42  Mask:255.255.255.0");
                break;

            case "ping":
                writeln("Pinging " + arg + " with 32 bytes of data:");
                writeln("Reply from " + arg + ": bytes=32 time=31ms TTL=57");
                writeln("Reply from " + arg + ": bytes=32 time=30ms TTL=57");
                break;

            case "tracert":
                writeln("Tracing route to " + arg + " over a maximum of 30 hops");
                writeln("  1  <1 ms  1 ms  1 ms  192.168.0.1");
                writeln("  2  20 ms 19 ms 20 ms  isp-gateway.net");
                writeln("  3  31 ms 30 ms 29 ms  " + arg);
                break;

            case "nano":
                if (arg.isEmpty()) {
                    writeln("usage: nano <file>");
                } else {
                    manager.openEditor(arg, fs);
                }
                break;

            default:
                writeln("Unknown command: " + cmd);
        }

        newPrompt();
    }

    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);
        if (stage != null) {
            stage.setKeyboardFocus(input);
        }
    }
}
