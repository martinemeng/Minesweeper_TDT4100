package minesweeper;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class minesweeper{
    //Logikken

    /*Genererer rutenettet,
    plasserer miner på tilfeldige steder,
    beregner antall miner rundt hver celle, 
    håndterer hva som skjer når brukeren klikker på en celle. */


     //dimensjonen til brettet, kvadratisk
    private int numBombs; //antall bomber som legges ut er fast
    private Cell[][] board;
    private int height;
    private int width;
    protected boolean gameOver;
    protected boolean hasWon;
    private boolean bombLayoutGenerated;


    //KONSTRUKTØR
    public minesweeper(int width, int height, int numBombs){
        this.gameOver = false; //når spiller starter er verken spillet over eller vunnet
        this.hasWon = false;
        this.bombLayoutGenerated = false;

        if(width * height <= numBombs){
            throw new IllegalArgumentException("This many bombs is not allowed for this boarddimension");
        }

        if(width < 4 || height < 4 || numBombs < 1){
            throw new IllegalArgumentException("Board dimensions must be at least 4x4");
        }

        if(width != height){
            throw new IllegalArgumentException("The board must be square");
        }


        this.height = height;
        this.width = width;
        this.numBombs = numBombs;

        //initialiserer en tom 2D array av typen cell
        board = new Cell[height][width]; 
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                board[i][j] = new Cell(); //initialiserer hver celle med et Cell-objekt (hver rute i rutenettet)
            }
        }
    }

    


    //RETURNERER KOORDINATENE TIL CELLENE RUNDT VALGT POSISJON
    private ArrayList<int[]> getCoordinateForSurroundings(int x, int y){
        ArrayList<int[]> coordinates = new ArrayList<>();//oppretter en Arraylist for å ta vare på koordinatene til cellene rundt

        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {                    
                if (i == x && j == y) {
                    continue; //hvis det e plasseringen til den valgte cellen, så fortsetter den
                }
                if (i >= 0 && i < this.getHeight() && j >= 0 && j < this.getWidth()) {
                    coordinates.add(new int[]{i, j}); //legger til koordinatene hvis de oppfyller kravene
                }
            }
        }
            return coordinates;
    
    }

    //BRUKES TIL TESTING FOR Å LAGE ET BESTEMT BRETT
    public void preventAutoGeneratingBombLayout() {
        this.bombLayoutGenerated = true;
    }


    //LEGGER UT BOMBER
    public void generateBombLayout(int firstX, int firstY) {
        //Skal unngå bombe på første trekk og de rutene rundt
        ArrayList<int[]> illegalPlacements = getCoordinateForSurroundings(firstX, firstY);
        int[] firstCell = {firstX, firstY};
        illegalPlacements.add(firstCell);

        int i = 0;
        Random random = new Random();
        while (i < numBombs){
            int randRow = random.nextInt(this.height); //Tilfeldig heltall fra 0 opptil angitt høyde/bredde (men ikke til og med)
            int randCol = random.nextInt(this.width);

            int[] placeBomb = {randRow, randCol}; //tar vare på de tilfeldige koordinatene til å legge ut bombe

            //sjekker om koordinatene er en del av de ulovlige plasseringene
            Boolean illegalCell = false;
            for(int[] illegalPlacement : illegalPlacements){
                if(Arrays.equals(placeBomb, illegalPlacement)){
                    illegalCell = true;
                    break;
                }
            }

            //plasserer bombe hvis det ikke er en bombe der allerede og ikke er en ulovlig celle
            if(!illegalCell && !board[randRow][randCol].isBomb()){
                this.board[randRow][randCol].setBomb();
                i++;
            }
        }
        
        //Kaller på funksjonen som regner ut nummeret for hver celle etter bombene er lagt ut
        this.calculateCellNumbers();
    }


    //REGNER UT ANTALL BOMBER RUNDT HVER CELLE PÅ HELE BRETTET
    public void calculateCellNumbers() {
        //Går gjenn om hele brettet, celle for celle
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                //Koordinatene til cellene rundt den valgte cellen
                ArrayList<int[]> cellSAround = this.getCoordinateForSurroundings(i, j);

                int bombsAround = 0;

                //Hvis en av cellene rundt er en bombe, økes telleren for bomber rundt
                for(int[] bombs : cellSAround){
                    int x = bombs[0];
                    int y = bombs[1];
                    if(x >= 0 && x < this.getHeight() && y >= 0 && y < this.getWidth()){
                       Cell cellAround = this.getCell(bombs[0], bombs[1]);
                        if(cellAround.isBomb()){
                            bombsAround += 1;
                        } 
                    }
                    
                }

                //Setter verdien for bomber rundt for gjeldende celle
                Cell cell = this.getCell(i, j);
                cell.setBombsSurrounding(bombsAround);
            }
        }
    

    }



    //RETURNERER CELLEN PÅ ANGITTE KOORDINATER
    public Cell getCell(int x, int y) {
        if(x < 0 || y < 0 || x >= this.width || y >= this.height){
            throw new IllegalArgumentException("Can't choose cell outside the board");
        }

        return board[x][y];
    }


    //ÅPNER VALGT CELLE
    public void openCell(int x, int y){
        if(x < 0 || y < 0 || x >= this.width || y >= this.height){
            throw new IllegalArgumentException("Can't choose cell outside the board");
        }
        

        //Hvis ikke bombene allerede er lagt ut, så legges de ut
        if(!bombLayoutGenerated){
            generateBombLayout(x, y);
            bombLayoutGenerated = true;
        }


        Cell selectedCell = getCell(x, y);
        //Åpner celle hvis spillet fortsatt er i gang, den ikke allerede er åpnet eller flagget
        if(!(gameOver || hasWon) && !selectedCell.isOpen() && !selectedCell.isFlagged()){
            selectedCell.open();

            if(selectedCell.isBomb()){
                //Hvis valgt celle inneholder en bombe, spillet er slutt
                this.gameOver = true;
                this.gameCompleted();
            }
            else{
                //Åpner cellene rundt
                this.openSurroundingCells(x,y);
            }

            //Sjekker om spillet er vunnet, hvis ikke kan du spille videre
            if(this.checkVictory()){
                this.hasWon = true;
                this.gameCompleted();
            }
        }



    }



    //ÅPNER CELLENE RUNDT DEN VALGTE CELLEN
    public void openSurroundingCells(int x, int y){
        Cell selectedCell = getCell(x, y);
        selectedCell.open();
        
        //Hvis det ikke er noen bomber rundt, så åpnes celler rundt
        if(selectedCell.getBombsSurrounding() == 0){
            ArrayList<int[]> cellsAround = getCoordinateForSurroundings(x, y);
            
            for(int[] cells : cellsAround){
                Cell nextCell = getCell(cells[0], cells[1]);

                if(!nextCell.isOpen() && !nextCell.isFlagged()){
                    //hvis cellen ikke er åpnet eller flagget, fortsetter å åpne celler rundt
                    this.openSurroundingCells(cells[0], cells[1]);
                }
            }
        }
    }

    //SETTER FLAGG PÅ EN CELLE
    public void flagCell(int x, int y){
        if(x < 0 || y < 0 || x >= this.getWidth() || y >= this.getHeight()){
            throw new IllegalArgumentException("Can't choose cell outside the board");
        }

        Cell cell = this.getCell(x, y);
        //Hvis cellen ikke er åpen, så kan vi sette flagg
        if(!cell.isOpen()){
            cell.setFlag();
        }
    }

    //SJEKKER OM SPILLET ER VUNNET ELLER KAN FORTSETTE Å SPILLE
    public boolean checkVictory(){
        //sjekker gjennom hele brettet
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                Cell cell = this.getCell(i, j);

                //om det eksisterer en celle som ikke er åpnet og ikke en bombe, da er spillet enda ikke vunnet
                if(!cell.isOpen() && !cell.isBomb()){
                    return false;
                }
                
            }
        }
        //Hvis det ikke finnes noen uåpnede celler uten bomber, da er spillet vunnet
        return true;
    }


    //HÅNDTERER NÅR SPILLET ER FERDIG, ENTEN VUNNET ELLER TAPT
    private void gameCompleted(){
        //Går gjennom hele brettet
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                Cell cell = this.getCell(i, j);

                //Hvis tapt: Åpne alle celler med bomber
                if(this.gameOver){
                    if(cell.isBomb()){
                        cell.open();
                    }
                }

                //Hvis vunnet: Setter flagg på alle bombene
                else if(this.hasWon){
                    if(cell.isBomb() && !cell.isFlagged()){
                        cell.setFlag();
                    }
                }
                
            }
            
        }
    }


    public void gameOver(){
        this.gameOver = true;
    }

    public void hasWon(){
        this.hasWon = true;
    }


    //PLASSERER BOMBE PÅ ANGITT CELLE
    public void placeBombAtCell(int x, int y) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            throw new IllegalArgumentException("Can't place bomb outside of board");
        }
        
        this.board[x][y].setBomb();
        this.calculateCellNumbers();
    }
    

    //KAN MANUELT SETTE VERDIER TIL EN CELLE (brukes ved filbehandling)
    public void setCellProps(int x, int y, boolean bomb, boolean flagged, boolean open) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            throw new IllegalArgumentException("Can't open tile outside of board");
        }

        this.board[y][x] = new Cell(bomb, flagged, open);
        calculateCellNumbers();
    }


    //returnerer verdiene, true = 1, false = 0, for (int numBombs, boolean isBombLayoutGenerated, boolean hasWon, boolean gameOver)(for filbehandling)
    public String getGameProps(){
        return String.valueOf(this.numBombs) + " " + (this.bombLayoutGenerated ? "1" : "0") + " "
                + (this.hasWon ? "1" : "0") + " " + (this.gameOver ? "1" : "0" + " ");
    }

    public void setGameProps(int numBombs, boolean isBombLayoutGenerated, boolean hasWon, boolean gameOver){
        if (numBombs < 1) {
            throw new IllegalArgumentException("Number of bombs must be greater than 0");
        }

        this.numBombs = numBombs;
        this.bombLayoutGenerated = isBombLayoutGenerated;
        this.hasWon = hasWon;
        this.gameOver = gameOver;

    }

    public boolean isGameActive() {
        return !(this.hasWon || this.gameOver);
    }

    public int getHeight(){
        return this.height;
    }


    public int getWidth(){
        return this.width;
    }

    public int getNumBombs(){
        return this.numBombs;
    }


    //TOSTRING for å få et visuelt bilde av brettet
    @Override
    public String toString() {

        
        String boardString = "";
        for (Cell[] row : board) {
            for (Cell col : row) {
                if (col.isOpen() && !col.isBomb()) {
                    boardString += String.valueOf(col.getBombsSurrounding()) + " | ";
                } else if (col.isOpen() && col.isBomb()) {
                    boardString += "* | ";
                } else if (col.isFlagged()) {
                    boardString += "# | ";
                } else {
                    boardString += "X | ";
                }
            }
            boardString += "\n";
        }

        if (this.gameOver) {
            boardString += "Game Over!";
        } else if (this.hasWon) {
            boardString += "You Won!";
        }

        return boardString;

    } 
    
    //Main-metode, brukt for testing, før testene ble skrevet 
    public static void main(String[] args) {
        minesweeper testSpill = new minesweeper(9,9,10);
        testSpill.preventAutoGeneratingBombLayout();
        testSpill.placeBombAtCell(5, 4);
        testSpill.placeBombAtCell(4, 5);
        testSpill.placeBombAtCell(6, 5);
        testSpill.placeBombAtCell(5, 6);

        testSpill.openCell(3, 3);
        System.out.println(testSpill);
        testSpill.flagCell(5, 5);
        System.out.println(testSpill);

        /* 
        testSpill.openCell(5, 5);
        System.out.println(testSpill);
    
        for (int i = 0; i < testSpill.height; i++) {
            for (int j = 0; j < testSpill.width; j++) {
                testSpill.openCell(i, j);
                
            }
            
        }
        System.out.println(testSpill);*/

        
    }
}


