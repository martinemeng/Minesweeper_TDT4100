package minesweeper;

public class Cell implements Tile{

    private boolean bomb;
    private boolean flag;
    private boolean open;
    private int bombsAround;


    //KONSTRUKTØRER
    public Cell(boolean bomb, boolean flag, boolean open) {
        this.bomb = bomb;
        this.flag = flag;
        this.open = open;
    }

    public Cell(){
        
    }

    //Gettere for tilstand
    @Override
    public boolean isBomb() {
        return bomb;
    }

    @Override
    public boolean isOpen() {
       return open;
    }

    @Override
    public boolean isFlagged() {
        return flag;
    }

    public int getBombsSurrounding(){
            return this.bombsAround; //returnerer antall bomber rundt
        }

    //Settere for tilstanden
    @Override
    public void setBomb() { //setter bombe på angitt celle
        this.bomb = true;
    }

    @Override
    public void setFlag() { 
        this.flag = !this.flag; //setter flagg hvis det ikke er et, fjerner flagg hvis det er et
        
    }

    @Override
    public void open() {
        this.open = true; //avdekker celle
    }

    
    public void setBombsSurrounding(int bombsAround){ //setter antall bomber rundt
        if(bombsAround < 0){
            throw new IllegalArgumentException("Can't have negative bombs around");
        }

        this.bombsAround = bombsAround;
    }
    
}
