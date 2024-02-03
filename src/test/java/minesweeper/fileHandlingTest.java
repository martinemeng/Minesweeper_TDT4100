package minesweeper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class fileHandlingTest {

    fileHandler fileHandler;
    minesweeper game1;
    minesweeper game2;

    
    @BeforeEach
    public void setup(){
        
        fileHandler = new fileHandler();

        game1 = new minesweeper(9, 9, 10);
        game1.flagCell(6,4);
        game1.openCell(4, 6);

        game2 = new minesweeper(6, 6, 5);
        game2.flagCell(1,1);
        game2.openCell(2,2);

    }


    @Test
    @DisplayName("Testing saving og loading game state to and from file")
    public void testSaveLoadtoFile() throws IOException{
        fileHandler.writeToFile(game1, new File("./testState1.txt"));
        fileHandler.writeToFile(game2, new File("./testState2.txt"));

        minesweeper savedGame1 = fileHandler.readFromFile(new File("./testState1.txt"));
        minesweeper savedGame2 = fileHandler.readFromFile(new File("./testState2.txt"));

        assertEquals(savedGame1.toString(), game1.toString(), "Saved board should be equal to the actual board");
        assertEquals(savedGame2.toString(), game2.toString(), "Saved board should be equal to the actual board");
    }


     // Cleans up leftover save files after test is completed
     @AfterAll
     public static void cleanup() throws IOException {
         File game1File = new File("./testState1.txt");
         //File game2File = new File("./TestBoard2.txt");
 
         if (!game1File.delete()) {
             throw new IOException("Failed to delete file at " + game1File.getPath());
         }
        /*  if (!game2File.delete()) {
             throw new IOException("Failed to delete file at " + game2File.getPath());
         }*/
     }
 }

