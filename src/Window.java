import javax.swing.*;
import java.awt.event.*;

public class Window extends JFrame implements ActionListener, KeyListener {

    private JMenuBar menuBar;
    private JMenu mGame, mDifficulty;
    private JMenuItem iHelp, iReset, iExit;
    private ButtonGroup buttonGroup;
    private JRadioButtonMenuItem rbEasy, rbMedium, rbHard;
    private Board board;
    private Difficulty difficulty;
    private int width, height;
    private final static int TILE_SIZE = 25;
    private final static int SCOREBOARD_SIZE = 30;

    Window(Difficulty difficulty){
        this.difficulty = difficulty;

        this.setTitle("Minesweeper");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        //this.setResizable(false);

        this.addKeyListener(this); // program listens for key presses

        menuBar = new JMenuBar();

        mGame = new JMenu("Game");
        menuBar.add(mGame);

        iHelp = new JMenuItem("Help");
        iHelp.setMnemonic(KeyEvent.VK_H);
        iHelp.addActionListener(this);
        menuBar.add(iHelp);

        mDifficulty = new JMenu("Set difficulty");
        mGame.add(mDifficulty);

        rbEasy = new JRadioButtonMenuItem("Easy");
        rbEasy.addActionListener(this);
        rbEasy.setSelected(true);

        rbMedium = new JRadioButtonMenuItem("Medium");
        rbMedium.addActionListener(this);

        rbHard = new JRadioButtonMenuItem("Hard");
        rbHard.addActionListener(this);

        buttonGroup = new ButtonGroup();
        buttonGroup.add(rbEasy);
        buttonGroup.add(rbMedium);
        buttonGroup.add(rbHard);

        mDifficulty.add(rbEasy);
        mDifficulty.add(rbMedium);
        mDifficulty.add(rbHard);

        iReset = new JMenuItem("Reset");
        iReset.addActionListener(this);
        mGame.add(iReset);

        mGame.addSeparator();

        iExit = new JMenuItem("Exit", KeyEvent.VK_X);
        iExit.addActionListener(this);
        mGame.add(iExit);

        this.setJMenuBar(menuBar);
        this.resetGame();
    }

    private void openHelp() {
        JOptionPane.showMessageDialog(null,
                "How to play:\n" +
                        "1. Left mouse click - opens a cell\n" +
                        "2. Right mouse click - marks the cell as a mine\n" +
                        "The goal is to discover all mines.\n" +
                        "Numbers represent the number of nearby mines.");
    }

    private void resetGame() {
        if(board != null)
            board.updateUI();

        switch (this.difficulty){
            case EASY:
                width = 9;
                height = 9;
                board = new Board(9, 9, 10);
                break;
            case MEDIUM:
                width = 20;
                height = 12;
                board = new Board(20, 12, 45);
                break;
            case HARD:
                width = 30;
                height = 16;
                board = new Board(30, 16, 99);
                break;
        }
        board.resetBoard();
        this.setContentPane(board);
        this.setSize(width * TILE_SIZE,SCOREBOARD_SIZE + (height + 3) * TILE_SIZE); // height counts the menubar and the frame of the window
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(iHelp)) {
            this.openHelp();
        } else if (e.getSource().equals(iReset)) {
            this.resetGame();
        } else if (e.getSource().equals(iExit)) {
            System.exit(0);
        } else if (e.getSource().equals(rbEasy)) {
            this.difficulty = Difficulty.EASY;
            this.resetGame();
        } else if (e.getSource().equals(rbMedium)) {
            this.difficulty = Difficulty.MEDIUM;
            this.resetGame();
        } else if (e.getSource().equals(rbHard)) {
            this.difficulty = Difficulty.HARD;
            this.resetGame();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'h'){
            this.openHelp();
        } else if (e.getKeyChar() == 'x'){
            System.exit(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public static void main(String[] args) {
        new Window(Difficulty.EASY);
    }
}
