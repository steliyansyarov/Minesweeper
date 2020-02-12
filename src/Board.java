import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.Timer;

public class Board extends JPanel implements MouseListener {

    private final int LINE_WIDTH;
    private final int LINE_HEIGHT;
    private final int NUM_MINES;
    private Cell[][] grid;
    private int numMinesCounter;
    private int timeCounter = 0;
    private int gameState; // -1 lost, 0 in game, 1 won
    private Timer timer;
    private final static int TILE_SIZE = 25;
    private final static int SCOREBOARD_SIZE = 30;
    private final static int SMILEY_SIZE = 26;
    private static Image im0, im1, im2, im3, im4, im5, im6, im7, im8, hidden_tile, mine, exploded_mine, flag, false_flag, win, loss, ingame;

    Board(int width, int height, int numMines) {
        this.LINE_WIDTH = width;
        this.LINE_HEIGHT = height;
        this.NUM_MINES = numMines;
        this.grid = new Cell[LINE_WIDTH][height];

       this.addMouseListener(this); // program listens to mouse clicks

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        im0 = toolkit.getImage(getClass().getResource("0.png"));
        im1 = toolkit.getImage(getClass().getResource("1.png"));
        im2 = toolkit.getImage(getClass().getResource("2.png"));
        im3 = toolkit.getImage(getClass().getResource("3.png"));
        im4 = toolkit.getImage(getClass().getResource("4.png"));
        im5 = toolkit.getImage(getClass().getResource("5.png"));
        im6 = toolkit.getImage(getClass().getResource("6.png"));
        im7 = toolkit.getImage(getClass().getResource("7.png"));
        im8 = toolkit.getImage(getClass().getResource("8.png"));
        hidden_tile = toolkit.getImage(getClass().getResource("hidden_tile.png"));
        mine = toolkit.getImage(getClass().getResource("mine.png"));
        exploded_mine = toolkit.getImage(getClass().getResource("exploded_mine.png"));
        flag = toolkit.getImage(getClass().getResource("flag.png"));
        false_flag = toolkit.getImage(getClass().getResource("false_flag.png"));
        win = toolkit.getImage(getClass().getResource("win.png"));
        loss = toolkit.getImage(getClass().getResource("loss.png"));
        ingame = toolkit.getImage(getClass().getResource("ingame.png"));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // Scoreboard
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0,0, LINE_WIDTH * TILE_SIZE, SCOREBOARD_SIZE);

        // Mine counter
        g2.setColor(Color.BLACK);
        g2.fillRect(3,3, 44, SCOREBOARD_SIZE - 6);
        drawDigit(g2, 5, 4, numMinesCounter / 100);
        drawDigit(g2, 5 + 14, 4, numMinesCounter / 10 % 10);
        drawDigit(g2, 5 + 28, 4, numMinesCounter % 10);

        // Smiley
        switch (gameState){
            case -1:
                g2.drawImage(loss, LINE_WIDTH * TILE_SIZE / 2 - SMILEY_SIZE / 2, (SCOREBOARD_SIZE - SMILEY_SIZE) / 2, SMILEY_SIZE, SMILEY_SIZE, this);
                break;
            case 0:
                g2.drawImage(ingame, LINE_WIDTH * TILE_SIZE / 2 - SMILEY_SIZE / 2, (SCOREBOARD_SIZE - SMILEY_SIZE) / 2, SMILEY_SIZE, SMILEY_SIZE, this);
                break;
            case 1:
                g2.drawImage(win, LINE_WIDTH * TILE_SIZE / 2 - SMILEY_SIZE / 2, (SCOREBOARD_SIZE - SMILEY_SIZE) / 2, SMILEY_SIZE, SMILEY_SIZE, this);
        }

        // Time counter
        g2.setColor(Color.BLACK);
        g2.fillRect(LINE_WIDTH * TILE_SIZE - 47, 3, 44, SCOREBOARD_SIZE - 6);
        drawDigit(g2, LINE_WIDTH * TILE_SIZE - 45, 4, timeCounter / 100);
        drawDigit(g2, LINE_WIDTH * TILE_SIZE - 45 + 14, 4, timeCounter / 10 % 10);
        drawDigit(g2, LINE_WIDTH * TILE_SIZE - 45 + 28, 4, timeCounter % 10);

        // Grid
        for (int y = 0; y < LINE_HEIGHT; y++) {
            for (int x = 0; x < LINE_WIDTH; x++) {
                if(grid[x][y].isRevealed()) {
                    if(grid[x][y].isExplodedMine())
                        g2.drawImage(exploded_mine, x * TILE_SIZE, y * TILE_SIZE + SCOREBOARD_SIZE, TILE_SIZE, TILE_SIZE, this);
                    else if (grid[x][y].isFlagged()){
                        if (gameState == -1 && !grid[x][y].isMine()){
                            g2.drawImage(false_flag, x * TILE_SIZE, y * TILE_SIZE + SCOREBOARD_SIZE, TILE_SIZE, TILE_SIZE, this);
                        } else g2.drawImage(flag, x * TILE_SIZE, y * TILE_SIZE + SCOREBOARD_SIZE, TILE_SIZE, TILE_SIZE, this);
                    } else
                        g2.drawImage(grid[x][y].getImage(), x * TILE_SIZE, y * TILE_SIZE + SCOREBOARD_SIZE, TILE_SIZE, TILE_SIZE, this);
                } else {
                    if (gameState == -1 && grid[x][y].isMine())
                        g2.drawImage(grid[x][y].getImage(), x * TILE_SIZE, y * TILE_SIZE + SCOREBOARD_SIZE, TILE_SIZE, TILE_SIZE, this);
                    else
                        g2.drawImage(hidden_tile, x * TILE_SIZE, y * TILE_SIZE + SCOREBOARD_SIZE, TILE_SIZE, TILE_SIZE, this);
                }
            }
        }
    }

    private void drawDigit(Graphics2D g2, int x, int y, int digit){
        g2.setColor(Color.RED);
        var lineLength = 8;
        var lineThickness = 2;
        int[] digits = {0x7E, 0x30, 0x6D, 0x79, 0x33, 0x5B, 0x5F, 0x70, 0x7F, 0x7B};
        int digitSchema = digits[digit];

        if((digitSchema >> 6 & 1) == 1)
            g2.fillRect(x + lineThickness, y, lineLength, lineThickness); // A
        if((digitSchema >> 5 & 1) == 1)
            g2.fillRect(x + lineThickness + lineLength,y + lineThickness, lineThickness, lineLength); // B
        if((digitSchema >> 4 & 1) == 1)
            g2.fillRect(x + lineThickness + lineLength,y + 2 * lineThickness + lineLength, lineThickness, lineLength); // C
        if((digitSchema >> 3 & 1) == 1)
            g2.fillRect(x + lineThickness,y + 2 * lineThickness + 2 * lineLength, lineLength, lineThickness); // D
        if((digitSchema >> 2 & 1) == 1)
            g2.fillRect(x,y + 2 * lineThickness + lineLength, lineThickness, lineLength); // E
        if((digitSchema >> 1 & 1) == 1)
            g2.fillRect(x, y + lineThickness, lineThickness, lineLength); // F
        if((digitSchema & 1) == 1)
            g2.fillRect(x + lineThickness,y + lineThickness + lineLength, lineLength, lineThickness); // G
    }

    public void resetBoard() {
        this.updateUI();
        gameState = 0;
        timeCounter = 0;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeCounter++;
                repaint();
            }
        }, 0, 1000);
        numMinesCounter = NUM_MINES;

        //empty board
        for (int y = 0; y < LINE_HEIGHT; y++) {
            for (int x = 0; x < LINE_WIDTH; x++) {
                grid[x][y] = null;
            }
        }

        // scatter mines
        Random random = new Random();
        Set<Point> mines = new HashSet<>();
        while (mines.size() < NUM_MINES){
            Point p = new Point(random.nextInt(LINE_WIDTH), random.nextInt(LINE_HEIGHT));

            if(mines.contains(p))
                continue;

            grid[p.x][p.y] = new Cell(mine, true);
            mines.add(p);
        }

        // initialize the rest of the cells
        for (int y = 0; y < LINE_HEIGHT; y++) {
            for (int x = 0; x < LINE_WIDTH; x++) {
                if(grid[x][y] == null){
                    int weight = calculateWeight(x, y);
                    switch (weight){
                        case 0:
                            grid[x][y] = new Cell(im0, false);
                            break;
                        case 1:
                            grid[x][y] = new Cell(im1, false);
                            break;
                        case 2:
                            grid[x][y] = new Cell(im2, false);
                            break;
                        case 3:
                            grid[x][y] = new Cell(im3, false);
                            break;
                        case 4:
                            grid[x][y] = new Cell(im4, false);
                            break;
                        case 5:
                            grid[x][y] = new Cell(im5, false);
                            break;
                        case 6:
                            grid[x][y] = new Cell(im6, false);
                            break;
                        case 7:
                            grid[x][y] = new Cell(im7, false);
                            break;
                        case 8:
                            grid[x][y] = new Cell(im8, false);
                            break;
                    }
                }
            }
        }
    }

    private int calculateWeight(int x, int y) {
        int weight = 0;
        for( int yoff = -1; yoff <= 1; yoff++) {
            for( int xoff = -1; xoff <= 1; xoff++) {
                if (x + xoff >= 0 && x + xoff < LINE_WIDTH && y + yoff >= 0 && y + yoff < LINE_HEIGHT){
                    if(grid[x + xoff][y + yoff] != null && grid[x + xoff][y + yoff].isMine())
                        weight++;
                }
            }
        }
        return weight;
    }

    private void revealNeighbors(int x, int y){
        for( int yoff = -1; yoff <= 1; yoff++) {
            for( int xoff = -1; xoff <= 1; xoff++) {
                int currentX = x + xoff;
                int currentY = y + yoff;
                if (currentX >= 0 && currentX < LINE_WIDTH && currentY >= 0 && currentY < LINE_HEIGHT){
                    if(!grid[currentX][currentY].isRevealed()){
                        grid[currentX][currentY].setRevealed(true);
                        if(grid[currentX][currentY].getImage() == im0)
                            revealNeighbors(currentX, currentY);
                    }
                }
            }
        }
    }

    private boolean checkForWin(){
        for (int y = 0; y < LINE_HEIGHT; y++){
            for (int x = 0; x < LINE_WIDTH; x++){
                if(!grid[x][y].isRevealed()){
                    return false;
                }

            }
        }
        return true;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // clicking on the smiley
        if(e.getX() > LINE_WIDTH * TILE_SIZE / 2 - SMILEY_SIZE / 2 &&
                e.getX() < LINE_WIDTH * TILE_SIZE / 2 + SMILEY_SIZE / 2 &&
                e.getY() > (SCOREBOARD_SIZE - SMILEY_SIZE) / 2 &&
                e.getY() < (SCOREBOARD_SIZE + SMILEY_SIZE) / 2)
            this.resetBoard();

        // clicking on the grid
        if(gameState == 0){
            int x = e.getX() / TILE_SIZE;
            int y = (e.getY() - SCOREBOARD_SIZE) / TILE_SIZE;

            if (x < 0 || x >= LINE_WIDTH || y < 0 || y >= LINE_HEIGHT || e.getY() <= SCOREBOARD_SIZE)
                return;

            if (SwingUtilities.isLeftMouseButton(e)) {
                if (grid[x][y].isRevealed())
                    return;

                grid[x][y].setRevealed(true);

                if(grid[x][y].getImage() == im0){
                    revealNeighbors(x, y);
                }

                if (grid[x][y].isMine()) {
                    grid[x][y].setExplodedMine(true);
                    gameState = -1;
                    timer.cancel();
                    timer.purge();
                    repaint();
                    return;
                }
            } else if (SwingUtilities.isRightMouseButton(e)) {
                if (grid[x][y].isFlagged()) {
                    grid[x][y].setFlagged(false);
                    grid[x][y].setRevealed(false);
                    numMinesCounter++;
                } else if (!grid[x][y].isRevealed() && numMinesCounter > 0){
                    grid[x][y].setFlagged(true);
                    grid[x][y].setRevealed(true);
                    numMinesCounter--;
                }
            } else return;

            if(numMinesCounter == 0 && checkForWin()) {
                gameState = 1;
                timer.cancel();
                timer.purge();
            }

            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
