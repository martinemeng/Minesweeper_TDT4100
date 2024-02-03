package minesweeper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class fileHandler {
    /*
     * Hver gang save blir trykket på, så lagres spillets tilstand i én bestemt fil.
     * Hver gang den blir lagret, så overskrives det som eventuelt allerede ligger i filen
     */

    public minesweeper readFromFile(File file) throws IOException {
        //Leser fra valgt fil
        BufferedReader reader = new BufferedReader(new FileReader(file));

        try {
            // Henter ut tilstanden til brettet ved å splitte på " " får man tilstandne til hver celle for seg
            String[] props = reader.readLine().split(" ");

            // Lager en 2D liste for innholdet til filen, tom
            List<List<String>> boardLayout = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                //Legges til i 2D listen
                boardLayout.add(Arrays.asList(line.split(" ")));
            }

            int height = boardLayout.size();
            int width = boardLayout.get(0).size();
            int numBombs = Integer.valueOf(props[0]); //caster til Integer

            //Lager et nytt brett
            minesweeper board = new minesweeper(width, height, numBombs);

            // Setter tilstanden til minesweeper-objektet i en bestemt tilstand (int numBombs, boolean isBombLayoutGenerated, boolean hasWon, boolean gameOver)
            board.setGameProps(Integer.valueOf(props[0]), Integer.valueOf(props[1]) != 0,
                    Integer.valueOf(props[2]) != 0, Integer.valueOf(props[3]) != 0);

            // Henter data fra filen til hver celle
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width; y++) {
                    String tileData = boardLayout.get(x).get(y);

                    //Inneholder tripletten b er ruten en bolme, f flagget og o er den åpnet
                    boolean bomb = tileData.contains("b");
                    boolean flagged = tileData.contains("f");
                    boolean open = tileData.contains("o");

                    //Gjør slik at cellen på koordinatene (x,y) får tilstanden etter det som ble lest fra filen
                    board.setCellProps(x, y, bomb, flagged, open);
                }
            }

            return board;
        } catch (IllegalArgumentException e) {
            // Filen inneholde rulovlige verdier
            throw new IOException("Invalid values in saved file!");
        } catch (IndexOutOfBoundsException e) {
            // Inneholdet i filen er ikke formattert riktig
            throw new IOException("Invalid format in saved file!");
        } finally {
            //lukker
            reader.close();
        }
    }

    public void writeToFile(minesweeper game, File file) throws IOException {
        //Skriver til valgt fil
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        try {
            //Henter tilstanden til minesweeper brettet ut og skriver til fil (int numBombs, boolean isBombLayoutGenerated, boolean hasWon, boolean gameOver)
            writer.write(game.getGameProps());
            writer.newLine();

            //Sjekker tilstanden til hver rute, og skriver b/f/o hvis det er hhv. bombe, flagget eller åpen, og x hvis ikke.
            //Hver rute representeres av 3 bokstaver.
            for (int y = 0; y < game.getHeight(); y++) {
                for (int x = 0; x < game.getWidth(); x++) {
                    Tile tile = game.getCell(x, y);
                    writer.write(tile.isBomb() ? "b" : "x");
                    writer.write(tile.isFlagged() ? "f" : "x");
                    writer.write(tile.isOpen() ? "o " : "x ");
                }

                writer.newLine();
            }
        } finally {
            // lukker
            writer.close();
        }
    }
}
