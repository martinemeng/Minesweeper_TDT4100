package minesweeper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class gameController {

    // Størrelsen på cellene og padding rundt
    private static final int CELL_SIZE = 40;
    private static final int PADDING = 20;

    private minesweeper gameBoard;
    private fileHandler fileHandler = new fileHandler();

    // FXML elementer
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private StackPane controlPane;

    @FXML
    private Pane boardPane;

    @FXML
    protected Button restartButton;

    @FXML
    protected Button newGameButton;

    @FXML
    protected Button saveButton;

    

    //Henter lagret spill fra fil
    public void initGameFromFile(File savedFile) throws IOException{
        this.gameBoard = this.fileHandler.readFromFile(savedFile);

        this.setFXMLStyle();
        this.createBoard();
        this.draw();

    }


    //Lager et nytt spill
    public void initGame(int width, int height, int numBombs){
        this.gameBoard = new minesweeper(width, height, numBombs);
        this.setFXMLStyle();
        this.createBoard();
    }

    //Fast layout for hvordan brettet skal se ut
    public void setFXMLStyle(){
        anchorPane.setPrefHeight(this.gameBoard.getHeight() * CELL_SIZE + PADDING + 50);
        anchorPane.setPrefWidth(this.gameBoard.getWidth() * CELL_SIZE + PADDING);
        gridPane.setPrefHeight(this.gameBoard.getHeight() * CELL_SIZE + 50 + PADDING);
        gridPane.setPrefWidth(this.gameBoard.getWidth() * CELL_SIZE + PADDING);
        controlPane.setPrefHeight(this.gameBoard.getWidth() * CELL_SIZE + PADDING);
        boardPane.setPrefHeight(this.gameBoard.getHeight() * CELL_SIZE + PADDING);
        boardPane.setPrefWidth(this.gameBoard.getWidth() * CELL_SIZE + PADDING);
    }

    //Lager nytt spill
    public void restart() {
        this.initGame(this.gameBoard.getWidth(), this.gameBoard.getHeight(), this.gameBoard.getNumBombs());
    }

    //Lagrer tilstanden til spillet til fil
    public void saveGameToFile(File file) throws IOException{
        this.fileHandler.writeToFile(gameBoard, file);
    }

    //Lager brettet i brukergrensesnittet
    private void createBoard(){
        //Fjerner alle noder som eventuelt henger igjen
        boardPane.getChildren().clear();
        //Henter høyden til boardpane i piksler
        boardPane.getHeight();

        for (int y = 0; y < gameBoard.getHeight(); y++) {
            for (int x = 0; x < gameBoard.getWidth(); x++) {
                //Oppretter en ny Pane for hver celle
                Pane cellPane = new Pane();


                cellPane.setTranslateX(x * CELL_SIZE + PADDING / 2);
                cellPane.setTranslateY(y * CELL_SIZE + PADDING / 2);

                //Egendefinert størrelse på cellene
                cellPane.setPrefWidth(CELL_SIZE);
                cellPane.setPrefHeight(CELL_SIZE);

                //Legger til x og y koordinatene til cellPane
                cellPane.getProperties().put("x", x);
                cellPane.getProperties().put("y", y);

                //Hvis cellene blir klikket på
                cellPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e){
                        //Spillet må være aktiv for at det er lovt å klikke
                        if(gameBoard.isGameActive()){
                            //Venstreklikk = flagg valgt celle
                            if(e.getButton() == MouseButton.SECONDARY){
                                gameBoard.flagCell((int) cellPane.getProperties().get("x"),
                                    (int) cellPane.getProperties().get("y"));
                            }
                            else if(e.getButton() == MouseButton.PRIMARY){
                                //Høyreklikk = åpne valgt celle
                                gameBoard.openCell((int) cellPane.getProperties().get("x"),
                                    (int) cellPane.getProperties().get("y"));
                            }

                            draw(); 
                        }
                    }
                });

                //Setter utseende til cellene
                cellPane.setStyle("-fx-border-color: grey; -fx-border-width: 2px; -fx-background-color: lightgray;");
                //Legger til cellen til brettet
                boardPane.getChildren().add(cellPane);
            }
        }
    }

    public void draw(){
        
        for (int y = 0; y < gameBoard.getHeight(); y++) {
            for (int x = 0; x < gameBoard.getWidth(); x++) {
                Cell cell = gameBoard.getCell(x, y);
                Pane cellPane = (Pane) boardPane.getChildren().get(y*gameBoard.getWidth() + x);
                cellPane.getChildren().clear();

                String filename = "";

                //Åpne celler
                if(cell.isOpen()){
                    //Endrer farge
                    cellPane.setStyle("-fx-border-color: #696969; -fx-border-width: 2px; -fx-background-color: #ffffff;");
                
                    if(cell.isBomb()){
                        filename = "bomb"; //Indikerer at det er en bombe (som skal brukes senere)
                        cellPane.setStyle( "-fx-border-color: #696969; -fx-border-width: 2px; -fx-background-color: #f08080;");
                    }

                    else if(cell.getBombsSurrounding() > 0){
                        //Får tallet til antall bomber rundt
                        filename = String.valueOf(cell.getBombsSurrounding());
                    }
                
                }
                else if(cell.isFlagged()){
                    //flagg
                    filename = "flag";
                }


                if(filename.length() != 0 ){
                    try{

                        //Hvis det finnes en fil med tilsvarende filnavn i minesweeper mappa, hent ut dette
                        InputStream imageStream = gameController.class
                                .getResourceAsStream("/minesweeper/" + filename + ".png");
                        //Selve bildet
                        Image tileImage = new Image(imageStream);
                        //Bildevisning
                        ImageView tileImageView = new ImageView(tileImage);
                        tileImageView.setFitWidth(CELL_SIZE * 0.6);
                        tileImageView.setFitHeight(CELL_SIZE * 0.6);
                        tileImageView.setX(CELL_SIZE * 0.2);
                        tileImageView.setY(CELL_SIZE * 0.2);
                        //Legger til bilde
                        cellPane.getChildren().add(tileImageView);
                        imageStream.close();


                    } catch (FileNotFoundException e) { //Finner ikke fil
                        Alert errorAlert = new Alert(AlertType.ERROR,
                                "Failed to load game resources. 1", ButtonType.OK);
                        errorAlert.showAndWait();
                        System.exit(0);
                    } catch (IOException e) { //Feiler med å lese fra fil
                        Alert errorAlert = new Alert(AlertType.ERROR,
                                "Failed to load game resources. 2", ButtonType.OK);
                        errorAlert.showAndWait();
                        System.exit(0);
                    }
                }
            }
    
        }

        //beskjed vises hvis du har vunnet
        if(gameBoard.checkVictory()){
            Alert winAlert = new Alert(AlertType.NONE, "You Won!", ButtonType.OK);
            winAlert.show();
        }

        //beskjed vises hvis du valgte en celle med bombe
        if(gameBoard.gameOver){
            Alert loseAlert = new Alert(AlertType.NONE, "You lost :(", ButtonType.OK);
            loseAlert.show();
        }
    }

    public minesweeper getGame() {
        return gameBoard;
    }

    
}
