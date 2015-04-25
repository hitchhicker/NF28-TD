package control;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class mainApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception{
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/control/view/viewBuilder.fxml"));
		Pane root = (Pane) fxmlLoader.load();
		Scene scene = new Scene(root, 850, 620);
		primaryStage.setTitle("MyApp");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
