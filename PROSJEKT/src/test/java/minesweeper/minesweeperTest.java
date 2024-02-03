package minesweeper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class minesweeperTest {

    minesweeper spill;
    minesweeper testSpill;

    @BeforeEach
    public void setUp(){
        //Tilfeldig generert 50x50 brett med 40 bomber
        spill = new minesweeper(50,50,40);

        
        //Eget laget brett med egne bomber lagt ut
        // * = bombe, *tall* = åpnet med bombe rundt
        /*
          0 1 2 3 4 5 6 7 8
        0 0 0 0 0 0 0 0 0 0
        1 0 0 0 0 0 0 0 0 0
        2 0 0 0 0 0 0 0 0 0
        3 0 0 0 0 1 1 1 0 0
        4 0 0 0 1 2 * 2 1 0
        5 0 0 0 1 * 4 * 1 0
        6 0 0 0 1 2 * 2 1 0
        7 0 0 0 0 1 1 1 0 0
        8 0 0 0 0 0 0 0 0 0
         */
        testSpill = new minesweeper(9,9, 10);
        testSpill.preventAutoGeneratingBombLayout();
        testSpill.placeBombAtCell(5, 4);
        testSpill.placeBombAtCell(4, 5);
        testSpill.placeBombAtCell(6, 5);
        testSpill.placeBombAtCell(5, 6);

    }


    @Test
    @DisplayName("Testing constructor")
    public void testConstructor() {

        assertThrows(IllegalArgumentException.class, () -> {
            new minesweeper(10,10,100);
        }, "Should not be able to make a board with bombs in every cell");

        assertThrows(IllegalArgumentException.class, () -> {
            new minesweeper(10,10,110);
        }, "Should not be able to make a board with more bombs than cells");

        assertThrows(IllegalArgumentException.class, () -> {
            new minesweeper(-10,10,60);
        }, "Should not be able to make a board with negative dimensions");

        assertThrows(IllegalArgumentException.class, () -> {
            new minesweeper(10,-10,60);
        }, "Should not be able to make a board with negative dimensions");

        assertThrows(IllegalArgumentException.class, () -> {
            new minesweeper(3,3,2);
        }, "Should not be able to make a board with dimensions under 4x4");

        assertThrows(IllegalArgumentException.class, () -> {
            new minesweeper(9,9,0);
        }, "Should not be able to make a board with no bombs");

        assertThrows(IllegalArgumentException.class, () -> {
            new minesweeper(10,10,-10);
        }, "Should not be able to make a board with negative number of bombs");

        assertThrows(IllegalArgumentException.class, () -> {
            new minesweeper(7,10,5);
        }, "Should not be able to make a board that is not square");

    }


    @Test
    @DisplayName("Testing the bomb layout on the board")
    public void testBombLayout(){
        //Bombene blir lagt ut etter første trekk
        spill.openCell(25, 25);

        int bombs = 0;
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                if(spill.getCell(i, j).isBomb()){
                    bombs += 1;
                }
            }
            
        }

        assertEquals(40, bombs,"Incorrect number of bombs generated");

        assertFalse(spill.getCell(24,24).isBomb(),"Bomb should not be placed around opening tile");
        assertFalse(spill.getCell(24,25).isBomb(),"Bomb should not be placed around opening tile");
        assertFalse(spill.getCell(24,26).isBomb(),"Bomb should not be placed around opening tile");
        assertFalse(spill.getCell(25,24).isBomb(),"Bomb should not be placed around opening tile");
        assertFalse(spill.getCell(25,25).isBomb(),"Bomb should not be placed around opening tile");
        assertFalse(spill.getCell(25,26).isBomb(),"Bomb should not be placed around opening tile");
        assertFalse(spill.getCell(26,24).isBomb(),"Bomb should not be placed around opening tile");
        assertFalse(spill.getCell(26,25).isBomb(),"Bomb should not be placed around opening tile");
        assertFalse(spill.getCell(26,26).isBomb(),"Bomb should not be placed around opening tile");
    }


    @Test
    @DisplayName("Testing the calculations of the number of bombs")
    public void testCalculatingBombs(){

        //tester noen celler som skal være 0
        assertEquals(0, testSpill.getCell(0, 0).getBombsSurrounding());
        assertEquals(0, testSpill.getCell(0, 1).getBombsSurrounding());


        //tester celler som skal være 1
        assertEquals(1, testSpill.getCell(4, 3).getBombsSurrounding());
        assertEquals(1, testSpill.getCell(5, 3).getBombsSurrounding());
        assertEquals(1, testSpill.getCell(6, 3).getBombsSurrounding());

        assertEquals(1, testSpill.getCell(3, 4).getBombsSurrounding());
        assertEquals(1, testSpill.getCell(3, 5).getBombsSurrounding());
        assertEquals(1, testSpill.getCell(3, 6).getBombsSurrounding());

        assertEquals(1, testSpill.getCell(7, 4).getBombsSurrounding());
        assertEquals(1, testSpill.getCell(7, 5).getBombsSurrounding());
        assertEquals(1, testSpill.getCell(7, 6).getBombsSurrounding());
        
        assertEquals(1, testSpill.getCell(4, 7).getBombsSurrounding());
        assertEquals(1, testSpill.getCell(5, 7).getBombsSurrounding());
        assertEquals(1, testSpill.getCell(6, 7).getBombsSurrounding());

        //tester celler som skal være 2
        assertEquals(2, testSpill.getCell(4, 4).getBombsSurrounding());
        assertEquals(2, testSpill.getCell(4, 6).getBombsSurrounding());
        assertEquals(2, testSpill.getCell(6, 4).getBombsSurrounding());
        assertEquals(2, testSpill.getCell(6, 6).getBombsSurrounding());

        //tester celle som skal være 4
        assertEquals(4, testSpill.getCell(5, 5).getBombsSurrounding());

    }

    @Test
    @DisplayName("Testing logic that opens selected tile and around")
    public void testOpenCell(){
        testSpill.openCell(3,3);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Cell cell = testSpill.getCell(i, j);
                if(cell.isBomb()|| i == 5 && j == 5 ){
                    assertFalse(testSpill.getCell(i,j).isOpen(),"This tile should not be open");
                }
                else{
                    assertTrue(testSpill.getCell(i,j).isOpen(),"This tile should be open");
                }
            }
        }

        
    }


    @Test
    @DisplayName("Testing setting out flags on cells")
    public void testSetFlags(){
        int x = 5;
        int y = 5;

        assertFalse(spill.getCell(x, y).isFlagged(),"Tile should not be flagged");
        spill.flagCell(x, y);
        assertTrue(spill.getCell(x, y).isFlagged(), "This cell should be flagged");
        spill.openCell(x, y);
        assertFalse(spill.getCell(x, y).isOpen(), "This cell should not be able to be opened");
        spill.flagCell(x, y);
        assertFalse(spill.getCell(x, y).isFlagged(), "This cell should not be flagged anymore");


        assertThrows(IllegalArgumentException.class, () -> {
            spill.flagCell(-1, 5);
        }, "Should not be able to flag a cell outside of the board");

        assertThrows(IllegalArgumentException.class, () -> {
            spill.flagCell(55, 5);
        }, "Should not be able to flag a cell outside of the board");
        
        assertThrows(IllegalArgumentException.class, () -> {
            spill.flagCell(5, -1);
        }, "Should not be able to flag a cell outside of the board");
        
        assertThrows(IllegalArgumentException.class, () -> {
            spill.flagCell(5, 55);
        }, "Should not be able to flag a cell outside of the board");
        
        assertThrows(IllegalArgumentException.class, () -> {
            spill.flagCell(-1, -1);
        }, "Should not be able to flag a cell outside of the board");

        assertThrows(IllegalArgumentException.class, () -> {
            spill.flagCell(55, 55);
        }, "Should not be able to flag a cell outside of the board");

    }


    @Test
    @DisplayName("Testing recognition of when game is won")
    public void testVictory(){
        assertFalse(testSpill.checkVictory(),"The game is not finished, game should still be active");

        testSpill.openCell(4, 4);
        testSpill.openCell(0, 0);
        testSpill.openCell(3, 2);
        assertFalse(testSpill.checkVictory(),"The game is not finished, game should still be active");
        assertTrue(testSpill.isGameActive(),"Game is not won yet, game should be active");

        testSpill.flagCell(5,4);
        testSpill.flagCell(4,5);
        testSpill.flagCell(6,5);
        testSpill.flagCell(5,6);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                testSpill.openCell(i, j);
            }
        }

        assertTrue(testSpill.checkVictory(), "The game should have been won");
        assertFalse(testSpill.isGameActive(), "The game should not be active after is it won");
    }

 
    @Test
    @DisplayName("Testing recognition of when game is lost")
    public void testGameOver(){
        assertTrue(testSpill.isGameActive(),"The game is not finished, game should still be active");

        testSpill.openCell(4, 4);
        testSpill.openCell(0, 0);
        testSpill.openCell(3, 2);
        assertTrue(testSpill.isGameActive(),"Game is not won yet, game should be active");

        testSpill.openCell(4, 5);

        assertTrue(testSpill.getCell(4, 5).isOpen(), "Cell should be open");
        assertFalse(testSpill.isGameActive(), "The game should not be active after a cell with bomb is opened");
    }

}
