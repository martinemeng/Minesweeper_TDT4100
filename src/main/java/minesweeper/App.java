package minesweeper;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;


public class App extends Application{

    private Scene mainScene;
    private Scene gameScene;
    private mainController mainController;
    private gameController gameController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Laster inn brukergrensesnittet definert i fxml-filene
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("game.fxml"));

        //Representerer brukergrensesnittet 
        mainScene = new Scene(mainLoader.load());
        gameScene = new Scene(gameLoader.load());


        // Lagrer JavaFX controllers i felt
        mainController = mainLoader.getController();
        gameController = gameLoader.getController();

        // Setter eventhandler for knappene på start skjermen
        // KNAPP #1
        mainController.newGameButton.setOnAction(e -> {
            gameController.initGame(9, 9, 10); //lager nytt spill
            primaryStage.setScene(gameScene); //henter inn GUI for spillet

        });
        
        // KNAPP #2
        mainController.loadButton.setOnAction(e -> {
            try {
                // Henter det lagrede spillet fra txt-filen
                File saveFile = new File(getClass().getResource("/minesweeper/gameState.txt").getFile());

                //Kun hvis filen ikke er tom 
                if (saveFile != null) {
                    gameController.initGameFromFile(saveFile);
                    primaryStage.setScene(gameScene);
                }
            } catch (IOException e1) {
                Alert errorAlert = new Alert(AlertType.ERROR,
                        "Save file appears to be corrupt. Try loading another file.", ButtonType.OK);
                errorAlert.show();
            }
        }); 

        // Setter eventhandler for knappene på spill-skjermen

        //Går tilbake til hovedskjem hvis "new Game" blir trykket på
        gameController.newGameButton.setOnAction(e -> primaryStage.setScene(mainScene));

        // KNAPP #3
        //Lagrer spillet til fil hvis "save" blir trykket på
        gameController.saveButton.setOnAction(e -> {
            try {
                File saveFile = new File("src/main/resources/minesweeper/gameState.txt"); //fast fil tilstanden blir lagret i
                gameController.saveGameToFile(saveFile);
                
                // Viser bekreftelse på at spillet ble lagret
                Alert successAlert = new Alert(AlertType.CONFIRMATION, "Game was successfully saved to file.", ButtonType.OK);
                successAlert.show();
            } catch (IOException e1) {
                // Viser en feilmelding hvis det ikke gikk å lagre til fil
                Alert errorAlert = new Alert(AlertType.ERROR, "Failed to save the game to file.", ButtonType.OK);
                errorAlert.show();
            }
        });

        // KNAPP #4
        //Lager et nytt spill hvis "restart" blir trykke tpå
        gameController.restartButton.setOnAction(e -> {
            gameController.restart();
        });

        // Viser hovedskjermen til appen
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Minesweeper");
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }


}



