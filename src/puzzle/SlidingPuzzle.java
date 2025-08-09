package puzzle;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.awt.event.*;

public class SlidingPuzzle extends JFrame {
    private JPanel puzzlePanel;
    private JButton[][] buttons = new JButton[3][3];
    private JButton shuffleButton, solveButton;
    private JProgressBar progressBar;
    private int[][] puzzle = new int[3][3];
    private final Random random = new Random();
    private int emptyX = 2, emptyY = 2;
    private Timer shuffleTimer = null;
    private int shuffleSteps = 100; // Number of random moves to shuffle

    public SlidingPuzzle() {
        setTitle("Solvable Tough Sliding Puzzle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.DARK_GRAY);
        setLayout(new BorderLayout());

        puzzlePanel = new JPanel(new GridLayout(3, 3));
        puzzlePanel.setBackground(Color.LIGHT_GRAY);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 20));
                buttons[i][j].setFocusPainted(false);
                final int row = i, col = j;
                buttons[i][j].addActionListener(e -> handleButtonClick(row, col));
                puzzlePanel.add(buttons[i][j]);
            }
        }
        add(puzzlePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.setBackground(Color.DARK_GRAY);

        shuffleButton = new JButton("Shuffle");
        shuffleButton.setBackground(Color.BLUE);
        shuffleButton.setForeground(Color.WHITE);
        shuffleButton.addActionListener(e -> shufflePuzzle());
        controlPanel.add(shuffleButton);

        solveButton = new JButton("Solve (Hint)");
        solveButton.setBackground(Color.GREEN);
        solveButton.setForeground(Color.WHITE);
        solveButton.addActionListener(e -> showHint());
        controlPanel.add(solveButton);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setBackground(Color.DARK_GRAY);
        progressBar.setForeground(Color.YELLOW);
        controlPanel.add(progressBar);
        add(controlPanel, BorderLayout.SOUTH);

        shuffleTimer = new Timer(50, e -> {
            if (shuffleSteps > 0) {
                int dir = random.nextInt(4);
                int newX = emptyX, newY = emptyY;
                if (dir == 0 && emptyY > 0) newY--;
                else if (dir == 1 && emptyX < 2) newX++;
                else if (dir == 2 && emptyY < 2) newY++;
                else if (dir == 3 && emptyX > 0) newX--;

                if (newX != emptyX || newY != emptyY) {
                    swap(newY, newX);
                    updateButtons();
                    shuffleSteps--;
                }
            } else {
                shuffleTimer.stop();
            }
        });

        initializePuzzle();
        updateButtons();
        setVisible(true);
    }

    private void initializePuzzle() {
        int num = 1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                puzzle[i][j] = num < 9 ? num++ : 0;
            }
        }
        emptyX = 2;
        emptyY = 2;
    }

    private void updateButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (puzzle[i][j] == 0) {
                    buttons[i][j].setText("");
                    buttons[i][j].setBackground(Color.LIGHT_GRAY);
                } else {
                    buttons[i][j].setText(String.valueOf(puzzle[i][j]));
                    buttons[i][j].setBackground(new Color(150, 150, 250)); // Fixed color for a cleaner look
                }
                buttons[i][j].setForeground(Color.BLACK);
            }
        }
        updateProgressBar();
    }

    private void handleButtonClick(int row, int col) {
        if (isValidMove(row, col)) {
            swap(row, col);
            updateButtons();
            checkWin();
        }
    }

    private boolean isValidMove(int row, int col) {
        return (Math.abs(row - emptyY) + Math.abs(col - emptyX) == 1);
    }

    private void swap(int row, int col) {
        puzzle[emptyY][emptyX] = puzzle[row][col];
        puzzle[row][col] = 0;
        emptyX = col;
        emptyY = row;
    }

    private void checkWin() {
        if (getManhattanDistance() == 0) {
            JOptionPane.showMessageDialog(this, "Puzzle Solved!", "Victory", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void shufflePuzzle() {
        initializePuzzle(); // Start from a solved state
        shuffleSteps = 100;
        shuffleTimer.start();
    }

    private void showHint() {
        JOptionPane.showMessageDialog(this, "A true solver is complex. Try to get the tiles in the correct rows first!", "Hint", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateProgressBar() {
        int maxDistance = calculateMaxManhattanDistance();
        int currentDistance = getManhattanDistance();
        int progress = (int) (((double) (maxDistance - currentDistance) / maxDistance) * 100);
        progressBar.setValue(progress);
        progressBar.setString("Progress: " + progress + "%");
    }

    private int getManhattanDistance() {
        int distance = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int value = puzzle[i][j];
                if (value != 0) {
                    int targetRow = (value - 1) / 3;
                    int targetCol = (value - 1) % 3;
                    distance += Math.abs(i - targetRow) + Math.abs(j - targetCol);
                }
            }
        }
        return distance;
    }

    private int calculateMaxManhattanDistance() {
        // A rough estimate of the max possible distance for a 3x3 puzzle
        return 16;
    }
    private void updateButtons1() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (puzzle[i][j] == 0) {
                    // The empty button
                    buttons[i][j].setText("");
                    buttons[i][j].setBackground(Color.LIGHT_GRAY);
                } else {
                    // All other buttons
                    buttons[i][j].setText(String.valueOf(puzzle[i][j]));
                    // Set button color based on its original row
                    if (puzzle[i][j] <= 3) {
                        buttons[i][j].setBackground(Color.RED);
                    } else if (puzzle[i][j] <= 6) {
                        buttons[i][j].setBackground(Color.BLUE);
                    } else {
                        buttons[i][j].setBackground(Color.GREEN);
                    }
                }
                buttons[i][j].setForeground(Color.WHITE); // Use white text for better contrast
            }
        }
        updateProgressBar();
    }

   
}