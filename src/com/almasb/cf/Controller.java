package com.almasb.cf;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

import com.almasb.cf.Model.SearchMode;

public class Controller {

    private Model model;

    public void setModel(Model model) {
        this.model = model;
    }

    @FXML
    private Label labelDirectory;
    @FXML
    private Label labelError;
    @FXML
    private TreeView<Node> tree;
    @FXML
    private ProgressIndicator progress;

    public void browse() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Open...");
        chooser.setInitialDirectory(new File("./"));

        File dir = chooser.showDialog(null);
        if (dir != null) {
            labelDirectory.setText(dir.getAbsolutePath());

            Task<List<FileCopy> > task = new GetCopiesTask(dir.toPath(), SearchMode.RECURSIVE);
            progress.progressProperty().bind(task.progressProperty());
            progress.setVisible(true);

            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
        }
    }

    private void updateTreeView(List<FileCopy> copies) {
        TreeItem<Node> root = new TreeItem<>(new Text("Found Copies"));

        for (FileCopy copy : copies) {
            String heading = String.format("[%s] [%d bytes] [%d files]", copy.getName(), copy.getSize(), copy.getNumberOfCopies());
            TreeItem<Node> node = new TreeItem<>(new Text(heading));
            copy.getCopies().forEach(path ->
                node.getChildren().add(new TreeItem<>(new Text(path.toAbsolutePath().toString()))));

            root.getChildren().add(node);
        }

        root.setExpanded(true);
        tree.setRoot(root);
    }

    private class GetCopiesTask extends Task<List<FileCopy> > {
        private final Path dir;
        private final SearchMode mode;

        public GetCopiesTask(Path dir, SearchMode mode) {
            this.dir = dir;
            this.mode = mode;
        }

        @Override
        protected List<FileCopy> call() throws Exception {
            return model.getCopies(dir, mode);
        }

        @Override
        protected void failed() {
            Throwable e = getException();
            labelError.setText(e == null ? "Unknown error" : e.getMessage());
            progress.setVisible(false);
        }

        @Override
        protected void succeeded() {
            updateTreeView(getValue());
            progress.setVisible(false);
        }
    }
}
