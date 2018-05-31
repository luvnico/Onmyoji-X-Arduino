package OnmyojiHelper.GUI;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;

public class FxPlay extends Application {

    protected static boolean isMac = true;
    public static void main(String[] args) {
        try {
            URL iconURL = FxPlay.class.getResource("imgs/daruma5.png");
            java.awt.Image image = new ImageIcon(iconURL).getImage();
            com.apple.eawt.Application.getApplication().setDockIconImage(image);
        } catch (Exception e) {
            // Won't work on Windows or Linux.
            isMac = false;
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("Prototype.fxml"));
            // set icon for windows / linux
            if(!isMac)
                primaryStage.getIcons().add(new Image("imgs/daruma5.png"));
            // set title
            primaryStage.setTitle("Onmyoji X Arduino");

            primaryStage.setScene(new Scene(root, 535, 330));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
