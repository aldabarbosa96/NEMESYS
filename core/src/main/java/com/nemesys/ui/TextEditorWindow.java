package com.nemesys.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.nemesys.fs.FileSystemSim;

public final class TextEditorWindow extends BaseWindow {
    private final FileSystemSim fs;
    private final WindowManager mgr;
    private String currentPath;
    private final TextArea area;
    private final Label titleLabel;

    public TextEditorWindow(Skin skin, WindowManager mgr, FileSystemSim fs, String path) {
        super("Editor", skin, WindowManager.AppType.TEXT_EDITOR, mgr);
        this.fs = fs;
        this.mgr = mgr;
        this.currentPath = (path != null && !path.trim().isEmpty()) ? path : null;

        defaults().pad(4f);

        // barra de iconos Save / Save As
        Table bar = new Table();
        titleLabel = new Label(currentPath == null ? "Untitled.txt" : currentPath, skin);
        ImageButton save = new ImageButton(skin, "save");
        ImageButton saveAs = new ImageButton(skin, "saveAs");

        bar.add(titleLabel).expandX().left();
        bar.add(save).padRight(4f);
        bar.add(saveAs);

        // área de texto
        area = new TextArea(loadText(), skin);
        ScrollPane scroll = new ScrollPane(area, skin);
        scroll.setFadeScrollBars(false);

        add(bar).growX().row();
        add(scroll).prefWidth(460).prefHeight(260).grow();

        pack();
        setPosition(180, 140);

        // listeners de guardar
        save.addListener(e -> {
            if (!e.toString().equals("touchDown")) return false;
            if (currentPath == null) openSaveDialog(false);
            else {
                int idx = Math.max(currentPath.lastIndexOf('\\'), currentPath.lastIndexOf('/'));
                if (idx >= 0) fs.cd("..");
                String name = idx >= 0 ? currentPath.substring(idx + 1) : currentPath;
                fs.overwrite(name, area.getText());
            }
            return true;
        });

        saveAs.addListener(e -> {
            if (!e.toString().equals("touchDown")) return false;
            openSaveDialog(true);
            return true;
        });
    }

    private String loadText() {
        if (currentPath == null) return "";
        String t = fs.cat(currentPath);
        return t == null ? "" : t;
    }

    private void openSaveDialog(boolean closeAfter) {
        SaveDialog dlg = new SaveDialog(getSkin(), fs, fileName -> {
            currentPath = fs.pwd() + "\\" + fileName;
            fs.overwrite(fileName, area.getText());
            titleLabel.setText(currentPath);
            if (closeAfter) mgr.close(WindowManager.AppType.TEXT_EDITOR);
        });
        getStage().addActor(dlg);
    }

    /**
     * Pide foco al área de texto para que salga el cursor parpadeante
     */
    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);
        if (stage != null) {
            stage.setKeyboardFocus(area);
        }
    }
}
