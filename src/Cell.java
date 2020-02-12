import java.awt.*;

public class Cell{

    private boolean mine;
    private boolean explodedMine;
    private Image image;
    private boolean revealed;
    private boolean flagged;

    Cell(Image image, boolean mine){
        this.image = image;
        this.mine = mine;
        this.revealed = false;
        this.flagged = false;
        this.explodedMine = false;
    }

    public Image getImage() {
        return image;
    }

    public boolean isMine() {
        return mine;
    }

    public boolean isExplodedMine() {
        return explodedMine;
    }

    public void setExplodedMine(boolean explodedMine) {
        this.explodedMine = explodedMine;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }
}
