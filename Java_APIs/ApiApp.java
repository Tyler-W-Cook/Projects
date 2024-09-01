package cs1302.api;

import javafx.application.Application;
import javafx.application.Platform;
import cs1302.api.ZipAPIResponse;
import cs1302.api.HourlyResults;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ProgressBar;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import javafx.application.Application;
import java.lang.IllegalArgumentException;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.net.URLEncoder;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import java.net.URI;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Set;
import java.util.HashSet;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import com.google.gson.JsonSyntaxException;


/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {

    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)           // uses HTTP protocol version 2 where possible
        .followRedirects(HttpClient.Redirect.NORMAL)  // always redirects, except from HTTPS to HTTP
        .build();                                     // builds and returns a HttpClient object


    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()                          // enable nice output when printing
        .create();                                    // builds and returns a Gson object

    String sunny = "file:resources/sunny-weather-ed.svg";
    String def = "file:resources/th?id=OIP.1YC9HXlXs_P6eD1zC5GY6QHaFZ ";
    String rainy = "file:resources/url?sa=i";
    Stage stage;
    Label instructions = new Label("Please enter a zip code and the App"
        + "will generate the weather data for the zip code");
    Scene scene;
    VBox root;
    HBox top;
    ComboBox<String> drop;
    ComboBox<String> temp;
    TextField url;
    Button search;
    HBox displayHolder;
    ImageView [] display = new ImageView[7];
    Label [] min = new Label[7];
    Label [] max = new Label[7];
    int counter = 0;
    int hold = 0;

    HBox labels;
    String defu = "https://tse3.mm.bing.net/th?id=OIP.1YC9HXlXs_P6eD1zC5GY6QHaFZ&pid=Api&P=0";
    String minTemp;
    String maxTemp;
    String sunn = "https://thumbs.dreamstime.com/b/artistic-weather"
        + "-sunny-clip-art-sun-hand-drawn-watercolor-element-isolated-white-137581682.jpg";
    String rain = "https://clipartix.com/wp-content/uploads/2016/05/"
        + "Cold-weather-clip-art-free-clipart-images.jpg";

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        this.min[0] = new Label("  Min:      ");
        this.max[0] = new Label("  Max:        ");
        this.min[1] = new Label("  Min:        ");
        this.max[1] = new Label("  Max:         ");
        this.min[2] = new Label("  Min:        ");
        this.max[2] = new Label("  Max:         ");
        this.min[3] = new Label("  Min:        ");
        this.max[3] = new Label("  Max:        ");
        this.min[4] = new Label("  Min:         ");
        this.max[4] = new Label("  Max:        ");
        this.min[5] = new Label("  Min:         ");
        this.max[5] = new Label("  Max:        ");
        this.min[6] = new Label("  Min:         ");
        this.max[6] = new Label("  Max:        ");
        this.root = new VBox();
        this.top = new HBox(10);
        this.drop = new ComboBox();
        this.drop.getItems().addAll("US", "FI" , "FR", "IT", "MX", "SE");
        this.drop.getSelectionModel().selectFirst();
        this.temp = new ComboBox();
        this.temp.getItems().addAll("fahrenheit", "celsius");
        this.temp.getSelectionModel().selectFirst();
        this.search = new Button("Search");
        this.url = new TextField("");
        this.labels = new HBox(9);
        this.displayHolder = new HBox(10);
        for (int i = 0; i < 7; i++) {
            this.display[i] = new ImageView(this.defu);
            this.display[i].setFitHeight(150);
            this.display[i].setFitWidth(150);

        }




    } // ApiApp

    /** {@inheritDoc} */
    @Override

    public void init() {
        this.root.getChildren().addAll(this.top, this.displayHolder, this.labels);
        this.top.getChildren().addAll(this.url, this.drop
            , this.temp, this.search, this.instructions);
        this.displayHolder.getChildren().addAll(this.display[0],this.display[1],this.display[2]
            ,this.display[3],this.display[4],this.display[5],this.display[6]);
        this.labels.getChildren().addAll(this.min[0], this.max[0], this.min[1], this.max[1],
            this.min[2], this.max[2], this.min[3], this.max[3], this.min[4], this.max[4],
            this.min[5], this.max[5], this.min[6], this.max[6]);


        Runnable getLocation = () -> {
            locationSearch(url.getText());
        };

        this.search.setOnAction(event -> runInNewThread(getLocation, "Zip thread"));

    } //init

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        this.stage = stage;


        // demonstrate how to load local asset using "file:resources/"
        // Image bannerImage = new Image("file:resources/readme-banner.png");
        // ImageView banner = new ImageView(bannerImage);
        // banner.setPreserveRatio(true);
        // banner.setFitWidth(640);

        // some labels to display information
        //    Label notice = new Label("Modify the starter code to suit your needs.");

        // setup scene
        // root.getChildren().addAll(banner, notice);
        scene = new Scene(root);

        // setup stage
        stage.setTitle("WeatherApiApp!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

    } // start

    public void locationSearch (String userInput) {
        try {
            if (userInput.length() != 5) {
                throw new IllegalArgumentException("Bad zip code");
            }
            String term = URLEncoder.encode(userInput, StandardCharsets.UTF_8); // "zipcode"
            String country = URLEncoder.encode(this.drop.getValue().toString(),
                StandardCharsets.UTF_8); // "US"
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://api.zippopotam.us/" + country + "/" + term)).build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
            String jsonBody = response.body();
            System.out.println("********** RAW JSON STRING: **********");
            System.out.println(jsonBody.trim());
            ZipAPIResponse zipResponse = GSON.fromJson(jsonBody, ZipAPIResponse.class);
            if (zipResponse.country == null) {
                throw new IllegalArgumentException("Bad zip code");
            }
            System.out.println(zipResponse.places[0].latitude);
            String latitude = URLEncoder.encode(zipResponse.places[0].latitude
                , StandardCharsets.UTF_8); //lat
            String longitude =  URLEncoder.encode(zipResponse.places[0].longitude
                , StandardCharsets.UTF_8);//long
            System.out.println(zipResponse.places[0].place); //1st api done
            String temperature_unit = URLEncoder.encode(this.temp.getValue().toString(),
                StandardCharsets.UTF_8); // unit
            String query = String.format("?latitude=%s&longitude=%s&hourly=temperature_2m"
                + ",precipitation_probability&daily=temperature_2m_max"
                + ",temperature_2m_min&temperature_unit=%s&timezone=auto"
                , latitude, longitude, temperature_unit);
            HttpRequest weatherRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.open-meteo.com/v1/forecast" + query)).build();
            HttpResponse<String> weatherResponse = HTTP_CLIENT.send(weatherRequest
                , BodyHandlers.ofString());
            String weatherjsonBody = weatherResponse.body();
            System.out.println("********** RAW JSON STRING: **********");
            System.out.println(weatherjsonBody.trim());
            WeatherAPIResponse wResponse = GSON.fromJson(weatherjsonBody
                , WeatherAPIResponse.class);
            System.out.println(wResponse.hourly.temperature_2m[0]);

            this.minTemp =  wResponse.daily.temperature_2m_min[0];
            this.maxTemp =  wResponse.daily.temperature_2m_max[0];
            Platform.runLater(() -> this.max[0].setText(" Min:  " + this.maxTemp));
            Platform.runLater(() -> this.min[0].setText(" Max:  " + this.minTemp));
            this.minTemp =  wResponse.daily.temperature_2m_min[1];
            this.maxTemp =  wResponse.daily.temperature_2m_max[1];
            Platform.runLater(() -> this.max[1].setText(" Min:  " + this.maxTemp));
            Platform.runLater(() -> this.min[1].setText(" Max:  " + this.minTemp));
            this.minTemp =  wResponse.daily.temperature_2m_min[2];
            this.maxTemp =  wResponse.daily.temperature_2m_max[2];
            Platform.runLater(() -> this.max[2].setText(" Min:  " + this.maxTemp));
            Platform.runLater(() -> this.min[2].setText(" Max:  " + this.minTemp));
            this.minTemp =  wResponse.daily.temperature_2m_min[3];
            this.maxTemp =  wResponse.daily.temperature_2m_max[3];
            Platform.runLater(() -> this.max[3].setText(" Min:  " + this.maxTemp));
            Platform.runLater(() -> this.min[3].setText(" Max:  " + this.minTemp));
            this.minTemp =  wResponse.daily.temperature_2m_min[4];
            this.maxTemp =  wResponse.daily.temperature_2m_max[4];
            Platform.runLater(() -> this.max[4].setText(" Min:  " + this.maxTemp));
            Platform.runLater(() -> this.min[4].setText(" Max:  " + this.minTemp));
            this.minTemp =  wResponse.daily.temperature_2m_min[5];
            this.maxTemp =  wResponse.daily.temperature_2m_max[5];
            Platform.runLater(() -> this.max[5].setText(" Min:  " + this.maxTemp));
            Platform.runLater(() -> this.min[5].setText(" Max:  " + this.minTemp));
            this.minTemp =  wResponse.daily.temperature_2m_min[6];
            this.maxTemp =  wResponse.daily.temperature_2m_max[6];
            Platform.runLater(() -> this.max[6].setText(" Min:  " + this.maxTemp));
            Platform.runLater(() -> this.min[6].setText(" Max:  " + this.minTemp));

            this.counter = 0;
            for (int i = 0; i < 36; i += 6) {
                if (Double.parseDouble(wResponse.hourly.precipitation_probability[i]) < 1) {
                    Platform.runLater(() -> this.display[counter].setImage(new Image(this.sunn)));
                }
                if (Double.parseDouble(wResponse.hourly.precipitation_probability[i]) >= 30) {
                    Platform.runLater(() -> this.display[counter].setImage(new Image(this.rain)));
                }
                this.counter++;
            }
        } catch (InterruptedException | IllegalArgumentException |
            IOException | JsonSyntaxException | IllegalStateException e) {
            Platform.runLater(() ->  this.alertError(e, this.url.getText()));
        }
    }


    public static void alertError(Throwable cause, String uri) {
        TextArea text = new TextArea("Bad Zip Code for specified Country: " + uri
            + "\n \n" + "Exception: " + cause.toString());
        text.setEditable(false);
        Alert alert = new Alert(AlertType.ERROR);
        alert.getDialogPane().setContent(text);
        alert.setResizable(true);
        alert.showAndWait();
    } // alertError


    /**
     *Run in Thread method that makes a new Daemon thread.
     *@param task is a runnable that you want to run on a seperate thread.
     *@param name is the name of the newly created thread.
     */

    public static void runInNewThread(Runnable task, String name) {
        Thread t = new Thread (task, name);
        t.setDaemon(true);
        t.start();
    }


} // ApiApp