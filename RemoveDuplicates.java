import java.io.File;
import java.io.FilenameFilter;

import java.util.Optional;
import java.util.List;

import javafx.application.Application;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.geometry.Pos;
import javafx.geometry.Insets;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * @author Sameer Kapoor
 * A JavaFx application that removes duplicate files stored in your system file manager.
*/
public class RemoveDuplicates extends Application {
    private static final int SCENE_WIDTH = 625;
    private static final int SCENE_HEIGHT = 700;

    private BorderPane root;

    private Label deleteLabel;

    private ListView listView;

    private Button searchByDirectoryBt;
    private Button deleteBt;

    private Separator separator;

    private DirectoryChooser dc;

    private File[] duplicateFiles;
    private String[] duplicateFilesStrings;

    private String formattedDuplicateFile;

    private Alert deleteAlert;

    private Optional<ButtonType> result;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();

        root.setTop(listViewHBox());
        root.setCenter(separatorVBox());
        root.setBottom(buttonsHBox());

        handleSearchByDirectoryBt();
        handleDeleteBt();

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        scene.setFill(Color.GRAY);

        primaryStage.setTitle("Remove Duplicates");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public VBox listViewHBox() {
        deleteLabel = new Label("Files to be deleted:");

        Font deleteLabelFont = Font.font("Roboto", FontWeight.BOLD, 16);

        deleteLabel.setFont(deleteLabelFont);

        listView = new ListView();

        listView.setPrefWidth(605);
        listView.setPrefHeight(525);

        VBox listViewHBox = new VBox(8);
        listViewHBox.getChildren().addAll(deleteLabel, listView);
        listViewHBox.setAlignment(Pos.CENTER);
        listViewHBox.setPadding(new Insets(7.5, 5, 5, 5));

        return listViewHBox;
    }

    public HBox buttonsHBox() {
        searchByDirectoryBt = new Button("Folder/Directory");
        searchByDirectoryBt.setStyle("-fx-background-color: #24a0ed; -fx-text-fill: white;");

        deleteBt = new Button("Delete");
        deleteBt.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        HBox buttonsHBox = new HBox(15);
        buttonsHBox.getChildren().addAll(searchByDirectoryBt, deleteBt);
        buttonsHBox.setPadding(new Insets(5, 5, 25, 5));
        buttonsHBox.setAlignment(Pos.CENTER);

        return buttonsHBox;
    }

    public VBox separatorVBox() {
        separator = new Separator();
        separator.setMaxWidth(585);

        VBox separatorVBox = new VBox();
        separatorVBox.getChildren().add(separator);
        separatorVBox.setPadding(new Insets(5, 5, 5, 5));
        separatorVBox.setAlignment(Pos.CENTER);

        return separatorVBox;
    }

    public void showDeleteAlert() {
        deleteAlert = new Alert(AlertType.WARNING, null, ButtonType.NO, ButtonType.YES);
        deleteAlert.setTitle("Warning");
        deleteAlert.setHeaderText("Are you sure you want to delete the " + listView.getItems().size() + " selected file(s)?");
        deleteAlert.setContentText("These items will be deleted immediately. You can't undo this action.");

        result = deleteAlert.showAndWait();
    }

    public void showNoFileSelectedAlert() {
        Alert noFileSelectedAlert = new Alert(AlertType.INFORMATION);
        noFileSelectedAlert.setTitle("Notice");
        noFileSelectedAlert.setHeaderText("There are no selected files to be deleted");
        noFileSelectedAlert.setContentText("Files must be selected in order to be deleted");
        noFileSelectedAlert.showAndWait();
    }

    public void delete(File[] files) {
        if (files == null || files.length == 0) {
            showNoFileSelectedAlert();
            return; 
        }

        for (File f : files) {
            f.delete();
        }
    }

    public void removeListViewItems(ListView lv) {
        lv.getItems().clear();
    }

    public String[] formatFiles(File[] files) {
        String[] formatted = new String[files.length];

        for (int i = 0; i < files.length; i++) {
            formatted[i] = String.format("%s", files[i].getName());
        }

        return formatted;
    }

    public void handleSearchByDirectoryBt() {
        searchByDirectoryBt.setOnAction((e) -> {
            dc = new DirectoryChooser();
            dc.setTitle("Select a folder/directory to remove duplicate files from");

            File selectedDirectory = dc.showDialog(null);

            if (selectedDirectory == null) {
                return;
            }

            duplicateFiles = selectedDirectory.listFiles((dir, name) -> {
                // Regex operator that matches files in the form "filename (i).ext", where i is any integer
                return name.matches(".*\\ \\(\\d*\\)\\.[A-za-z]*");
            });

            if (duplicateFiles == null || duplicateFiles.length == 0) {
                return;
            }

            duplicateFilesStrings = formatFiles(duplicateFiles);

            for (String dup : duplicateFilesStrings) {
                if (!listView.getItems().contains((dup))) {
                    listView.getItems().add(dup);
                }
            }
        });
    }

    public void handleDeleteBt() {
        deleteBt.setOnAction((e) -> {
            if (duplicateFiles == null || duplicateFiles.length == 0) {
                showNoFileSelectedAlert();
                return;
            }

            showDeleteAlert();

            if (!result.isPresent()) {
                return;
            } else if (result.get() == ButtonType.YES) {
                delete(duplicateFiles);
                removeListViewItems(listView);
                return;
            } else if (result.get() == ButtonType.NO) {
                return;
            }
        });
    }
}
