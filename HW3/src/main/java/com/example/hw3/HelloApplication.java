package com.example.hw3;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.Scene;


import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.scene.media.*;

import javafx.collections.*;
import javafx.geometry.Insets;
import java.io.File;
import java.util.List;

public class HelloApplication extends Application {
    private ObservableList<File> playlist = FXCollections.observableArrayList();
    private ListView<String> playlistView = new ListView<>();
    private MediaPlayer mediaPlayer;
    private int currentIndex = -1;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        playlistView.setPrefWidth(300);
        root.setLeft(playlistView);

        Button addBtn = new Button("Add Songs");
        Button playBtn = new Button("Play");
        Button pauseBtn = new Button("Pause");

        Button stopBtn = new Button("Stop");

        Button prevBtn = new Button("Previous");
        Button nextBtn = new Button("Next");


        HBox controls = new HBox(10, addBtn, playBtn, pauseBtn, stopBtn, prevBtn, nextBtn);
        controls.setPadding(new Insets(10));
        root.setBottom(controls);

        addBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select MP3 or WAV files");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav")
            );
            List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
            if (files != null) {
                playlist.addAll(files);
                updatePlaylistView();
            }
        });

        playBtn.setOnAction(e -> playSelectedOrCurrent());


        pauseBtn.setOnAction(e -> {
            if (mediaPlayer != null) mediaPlayer.pause();
        });

        stopBtn.setOnAction(e -> {
            if (mediaPlayer != null) mediaPlayer.stop();
        });


        nextBtn.setOnAction(e -> playNext());

        prevBtn.setOnAction(e -> playPrevious());


        Scene scene = new Scene(root, 600, 200);
        primaryStage.setTitle("JavaFX MediaPlayer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updatePlaylistView() {
        playlistView.getItems().clear();
        for (File file : playlist) {
            playlistView.getItems().add(file.getName());
        }
    }



    private void playSelectedOrCurrent() {
        int selected = playlistView.getSelectionModel().getSelectedIndex();
        if (selected != -1) {
            if (currentIndex == selected && mediaPlayer != null) {

                MediaPlayer.Status status = mediaPlayer.getStatus();
                if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.STOPPED) {
                    mediaPlayer.play();
                } else if (status != MediaPlayer.Status.PLAYING) {
                    playSong(selected);
                }
            } else {
                playSong(selected);
            }
        } else if (currentIndex != -1 && mediaPlayer != null) {
            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.STOPPED) {
                mediaPlayer.play();
            }
        } else if (!playlist.isEmpty()) {
            playSong(0);
        }
    }



    private void playSong(int index) {
        if (index < 0 || index >= playlist.size()) return;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        File file = playlist.get(index);
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(this::playNext);
        mediaPlayer.play();
        currentIndex = index;
        playlistView.getSelectionModel().select(index);
    }
    private void playNext() {
        if (playlist.isEmpty()) return;
        int nextIndex = (currentIndex + 1) % playlist.size();
        playSong(nextIndex);
    }


    private void playPrevious() {
        if (playlist.isEmpty()) return;
        int prevIndex = (currentIndex - 1 + playlist.size()) % playlist.size();
        playSong(prevIndex);
    }


    public static void main(String[] args) {
        launch(args);
    }
}