package cs1302.gallery;

import java.lang.*;
import java.util.*;
import java.io.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.image.*;
import java.net.*;
import javafx.util.Duration;
import javafx.event.*;
import javafx.geometry.Insets;
import com.google.gson.*;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import javafx.scene.input.*;

/**
 * Represents an iTunes GalleryApp.
 */
public class GalleryApp extends Application {

    ToolBar toolBar = new ToolBar();
    HBox lowerPane = new HBox();
    HBox progBar = new HBox();
    VBox app = new VBox();
    TextField textField = new TextField();
    GridPane gridPane = new GridPane();
    Button searchBar = new Button("Play");
    boolean play = true;
    String searchText = "";
    ImageView[] imageView;
    String[] urlArray;
    URL url = null;
    InputStreamReader reader = null;
    int size = 0;
    boolean first = true;
    ProgressBar bar = new ProgressBar();
    double progress = 0.0;
    Timeline timeline;
    Separator space = new Separator();

    /**
     * {@inheritdoc}
     * Start method for the gallery.
     */
    @Override
    public void start(Stage stage) {
        HBox pane = new HBox();
        app.getChildren().add(file());
        Scene scene = new Scene(app);
        stage.setMaxWidth(500);
        stage.setMaxHeight(600);
        stage.setTitle("GalleryApp!");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        app.getChildren().addAll(pane);

        if (first) {
            search();
            query("pop");
            first = false;
        }
        progressBar();
    }

    /**
     * Method that creates the file tab and help tab along with functionality.
     * @return MenuBar
     */
    public MenuBar file() {
        Menu menu1 = new Menu("File");
        Menu menu2 = new Menu("Help");
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menu1, menu2);

        MenuItem menu12 = new MenuItem("Exit");
        MenuItem menu23 = new MenuItem("About");
        menu2.getItems().add(menu23);
        menu23.setOnAction( e -> {
            Stage dialog = new Stage();
            Image myPhoto = new Image("https://user-images.githubusercontent.com/76537506/"
                + "142921143-a3316318-3e0c-4ffd-ad4b-810add23c5ba.JPG");

            dialog.setTitle("About Shaam Bala");
            dialog.setMaxHeight(480);
            dialog.setMaxWidth(500);
            VBox info = new VBox();
            Scene dialogScene = new Scene(info);
            Text aboutMe = new Text();
            aboutMe.setText("About Shaam Bala \n sb0775@uga.edu \n Application v1.6.2");
            ImageView me = new ImageView(myPhoto);
            info.getChildren().addAll(aboutMe, me);
            dialog.setScene(dialogScene);
            dialog.sizeToScene();
            dialog.showAndWait();
        });

        menu12.setOnAction( e -> {
            Platform.exit();
            System.exit(0);
        });
        menu1.getItems().add(menu12);
        return menuBar;
    }

    /**
     * Sets up the searchbar button.
     */
    public void searchBar() {
        bar.setProgress(0.0);
        toolBar.getItems().add(searchBar);
        searchBar.setOnAction(event -> {
            if (play) {
                searchBar.setText("Pause");
                play = false;
                play();
            } else {
                searchBar.setText("Play");
                play = true;

            }
        });
    }

    /**
     * Sets up the search bar with the update images button.
     */
    public void search() {
        Button update = new Button("Update Images");
        Runnable r = () -> {
            searchBar();
            update.setOnAction( e -> {
                searchText = searchBar.getText();
                query(searchText);
            });
        };
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
        toolBar.getItems().addAll(space, new Text("Search Query: "), textField, update);
        app.getChildren().addAll(toolBar);
    }

    /**
     * Creates the progress bar.
     */
    public void progressBar() {
        BorderPane progress = new BorderPane();
        bar.setProgress(0.0);
        bar.setLayoutX(25.0);
        bar.setLayoutY(550.0);
        progBar.getChildren().addAll(bar, new Text("\t\t Images provided courtesy of iTunes"));
        progress.setBottom(progBar);
        app.getChildren().addAll(gridPane, progress);
    }

    /**
     * places the images along with the an exit screen if the number of images is insufficient.
     */
    public void grid() {
        if (this.size >= 20) {
            imageView = new ImageView[20];
            for (int i = 0; i < 20; i++) {
                imageView[i] = new ImageView(urlArray[i]);
                gridPane.setConstraints(imageView[i], i % 5, i / 5);
                gridPane.getChildren().addAll(imageView[i]);
            }
        } else {
            Stage grid = new Stage();
            Text exit = new Text("Your search does not have any results.");
            Button exitButton = new Button("Exit");
            HBox exitPane = new HBox();
            exitPane.getChildren().addAll(exit, new Text("\t\t"), exitButton);
            exitPane.setPadding(new Insets(12));
            Scene exitScene = new Scene(exitPane);
            grid.setTitle("Nope");
            grid.setScene(exitScene);
            grid.sizeToScene();
            grid.show();

            exitButton.setOnAction(event -> {
                Platform.exit();
                System.exit(0);
            });
        }
    }

    /**
     * Helper method to create 2 second delay between each new updated image.
     * @param handler
     */
    public void setTimeline(EventHandler<ActionEvent> handler) {
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), handler);
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    /**
     * Creates new image in a random part of the grid.
     */
    public void images() {
        ImageView[] images;
        Random random = new Random();
        bar.setProgress(0.0);
        if (this.size > 20) {
            images = new ImageView[this.size];
            for (int i = 21; i < this.size; i++) {
                images[i] = new ImageView(urlArray[i]);
            }
            for (int i = 0; i <= 5; i++) {
                int randomRow = random.nextInt(4);
                int randomCol = random.nextInt(5);
                int rdm = random.nextInt(this.size - 1) + 21;
                int img = 0;
                if (rdm < 50) {
                    img = rdm;
                }
                gridPane.setConstraints(images[img], randomCol, randomRow);
                gridPane.getChildren().addAll(images[img]);

            }
        }
    }

    /**
     * method that sets delay along with new images if the play/pause button is pressed.
     */
    public void play() {
        EventHandler<ActionEvent> handler = (e -> {
            images();
        });
        setTimeline(handler);
    }

    /**
     * Updates the progress bar.
     */
    public void incProgress() {
        progress += 0.05;
        bar.setProgress(progress);
    }


    /**
     * main method that reads the query.
     * @param query
     */
    public void query(String query) {
        String sURL = "";
        progress = 0.0;
        try {
            sURL = URLEncoder.encode(query, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            System.out.println(e);
        }

        try {
            url = new URL("https://itunes.apple.com/search?term=" + sURL);
        } catch (java.net.MalformedURLException e) {
            System.out.println(e);
        }

        try {
            reader = new InputStreamReader(url.openStream());
        } catch (IOException e) {
            System.out.println(e);
        }

        //JsonParser jp = new JsonParser();
        JsonElement je = JsonParser.parseReader(reader);
        JsonObject root = je.getAsJsonObject();
        JsonArray results = root.getAsJsonArray("results");

        int numResults = results.size();

        if (numResults < 20) {
            this.size = 0;
        } else {
            this.size = numResults;
        }

        urlArray = new String[numResults];

        for (int i = 0; i < numResults; i++) {
            JsonObject result = results.get(i).getAsJsonObject();
            JsonElement artworkUrl100 = result.get("artworkUrl100");

            if (artworkUrl100 != null) {
                String artUrl = artworkUrl100.getAsString();
                urlArray[i] = artUrl;
                Runnable r = () -> {
                    incProgress();
                };
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.start();
            }
        }
        grid();
    }

} // GalleryApp
