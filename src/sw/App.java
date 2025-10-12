package sw;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        // For now just show the splash screen.
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Image splashImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/splash.png")));
        ImageView imgView = new ImageView(splashImg);
        imgView.setPreserveRatio(true);
        imgView.setFitWidth(800);
        StackPane splashRoot = new StackPane(imgView);
        Scene splashScene = new Scene(splashRoot);
        primaryStage.setScene(splashScene);
        primaryStage.show();
    }
}
