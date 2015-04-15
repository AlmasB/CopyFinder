package com.almasb.cf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

public class Controller {

    @FXML
    private Label labelDirectory;
    @FXML
    private Label labelError;
    @FXML
    private TreeView<Node> tree;

    private List<Path> files;

    public void browse() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Open...");
        chooser.setInitialDirectory(new File("./"));
        File dir = chooser.showDialog(null);
        if (dir != null) {
            labelDirectory.setText(dir.getAbsolutePath());

            try (Stream<Path> allFiles = Files.walk(dir.toPath())) {
                files = allFiles
                        .filter(Controller::nonZeroFile)
                        .collect(Collectors.toList());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (files != null) {
            try {
                checkCopies();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkCopies() throws IOException {
        TreeItem<Node> root = new TreeItem<>(new Text("Found Copies"));

        boolean[] marked = new boolean[files.size()];

        for (int i = 0; i < files.size(); i++) {
            if (marked[i])
                continue;

            Path file = files.get(i);
            TreeItem<Node> node = new TreeItem<>();
            node.getChildren().add(new TreeItem<>(new Text(file.toAbsolutePath().toString())));

            for (int j = i + 1; j < files.size(); j++) {
                Path file2 = files.get(j);
                if (isSame(file, file2)) {
                    node.getChildren().add(new TreeItem<>(new Text(file2.toAbsolutePath().toString())));
                    marked[i] = true;
                    marked[j] = true;
                }
            }

            if (node.getChildren().size() > 1) {
                node.setValue(new Text(
                        file.getFileName().toString()
                        + " (" + Files.size(file) + " bytes) - ("
                        + node.getChildren().size() + " items)"));
                root.getChildren().add(node);
            }
        }

        root.setExpanded(true);
        tree.setRoot(root);
    }

    private boolean isSame(Path file1, Path file2) throws IOException {
        if (Files.size(file1) != Files.size(file2))
            return false;

        return Arrays.equals(Files.readAllBytes(file1), Files.readAllBytes(file2));
    }

    private static boolean nonZeroFile(Path file) {
        try {
            return Files.isRegularFile(file) && Files.size(file) > 0;
        }
        catch (Exception e) {
            return false;
        }
    }
}
