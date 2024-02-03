package minesweeper;

public interface Tile { //grensesnitt som Cell implementerer
    
    public boolean isBomb();
    
    public boolean isOpen();

    public boolean isFlagged();

    public void setBomb();

    public void setFlag();

    public void open();
    
    public int getBombsSurrounding();

    public void setBombsSurrounding(int bombsAround);
}
