package com.nemesys.ui;

import com.badlogic.gdx.graphics.*;
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

    /* ---------- helpers gráficos ---------- */
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

    /* ---------- prompt & output ---------- */
    private String promptText() {
        return fs.pwd() + ">";
    }

    private void newPrompt() {
        promptRow = new Table();
        promptRow.add(new Label(promptText(), skin, "terminal-label")).padRight(2f);
        promptRow.add(input).growX();
        terminal.add(promptRow).growX().row();
        scrollToEnd();
        if (getStage() != null) getStage().setKeyboardFocus(input);
    }

    private void writeln(String txt) {
        terminal.add(new Label(txt, skin, "terminal-label")).growX().row();
        scrollToEnd();
    }

    private void listTree(FileSystemSim fsim, String indent) {
        for (String item : fsim.ls()) {
            writeln(indent + item);
            if (item.endsWith("/")) {
                fsim.cd(item.substring(0, item.length() - 1));
                listTree(fsim, indent + "  ");
                fsim.cd("..");
            }
        }
    }

    private void scrollToEnd() {
        scroll.layout();
        scroll.setScrollPercentY(100);
    }

    /* ---------- comandos ---------- */
    private void exec(String line) {
        /* pinta la línea que el usuario acaba de escribir */
        promptRow.clearChildren();
        promptRow.add(new Label(promptText() + " " + line, skin, "terminal-label")).growX();

        /* parseo simple: comando + resto como argumento único */
        String[] parts = line.trim().split("\\s+", 2);
        if (parts.length == 0) {
            newPrompt();
            return;
        }

        String cmd = parts[0];
        String arg = (parts.length > 1) ? parts[1] : "";

        /* ------------------------------------------------------------------ */
        /* 1 ─ Comandos que tocan el FS  →  delegados a FileSystemSim.run()   */
        /* ------------------------------------------------------------------ */
        final String[] fsCmds = {"ls", "dir", "cd", "pwd", "mkdir", "touch", "cat", "tree", "du", "df"};
        if (java.util.Arrays.asList(fsCmds).contains(cmd)) {
            String result = fs.run(cmd, arg);     // null = éxito sin salida
            if (result != null && !result.isEmpty()) {
                for (String ln : result.split("\n")) writeln(ln);
            }
            newPrompt();
            return;
        }

        /* ------------------------------------------------------------------ */
        /* 2 ─ Comandos “mock” que solo imprimen texto o afectan a la UI      */
        /* ------------------------------------------------------------------ */
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

            /* utilidades ---------------------------------------------------- */
            case "time":
                writeln(java.time.LocalTime.now().format(FMT));
                break;

            case "clear":
            case "cls":
                terminal.clearChildren();
                break;

            case "exit":
                manager.close(WindowManager.AppType.TERMINAL);
                return;

            /* información sistema ------------------------------------------- */
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

            /* network mocks -------------------------------------------------- */
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

}
