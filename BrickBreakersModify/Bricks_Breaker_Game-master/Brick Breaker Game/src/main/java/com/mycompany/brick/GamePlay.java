package com.mycompany.brick;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;

public class GamePlay extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0;

    private int totalbricks = 21;

    private Timer Timer;
    private int delay = 8;

    private int playerX = 310;

    private int ballposX = 120;
    private int ballposY = 350;
    private int ballXdir = -1;
    private int ballYdir = -2;

    private MapGenerator map;

    public GamePlay() {
        map = new MapGenerator(3, 7);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        Timer = new Timer(delay, this);
        Timer.start();
    }

    public void paint(Graphics g) {
        // Background
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        // Draw bricks
        map.draw((Graphics2D) g);

        // Borders
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        // Scores
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("" + score, 590, 30);

        // Paddle
        g.setColor(Color.yellow);
        g.fillRect(playerX, 550, 100, 8);

        // Ball
        g.setColor(Color.GREEN);
        g.fillOval(ballposX, ballposY, 20, 20);

        // Game Over
        if (ballposY > 570 || totalbricks == 0) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;

            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game Over! Score: " + score, 190, 300);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 340);
        }

        g.dispose();
    }

    public void actionPerformed(ActionEvent e) {
        if (play) {
            if (new Rectangle(ballposX, ballposY, 20, 20)
                    .intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballYdir = -ballYdir;
            }

            A:
            for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.bricksWidth + 80;
                        int brickY = i * map.bricksHeight + 50;
                        int brickWidth = map.bricksWidth;
                        int brickHeight = map.bricksHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);

                        if (ballRect.intersects(rect)) {
                            map.setBricksValue(0, i, j);
                            totalbricks--;
                            score += 5;

                            if (ballposX + 19 <= rect.x || ballposX + 1 >= rect.x + rect.width) {
                                ballXdir = -ballXdir;
                            } else {
                                ballYdir = -ballYdir;
                            }
                            break A;
                        }
                    }
                }
            }

            ballposX += ballXdir;
            ballposY += ballYdir;

            if (ballposX < 0) ballXdir = -ballXdir;
            if (ballposY < 0) ballYdir = -ballYdir;
            if (ballposX > 670) ballXdir = -ballXdir;
        }

        repaint();
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 600) playerX = 600;
            else moveRight();
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX <= 10) playerX = 10;
            else moveLeft();
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                String playerName = JOptionPane.showInputDialog(this, "Enter your name:");
                if (playerName != null && !playerName.trim().isEmpty()) {
                    DatabaseManager.saveScore(playerName, score);
                }

                List<String> topScores = DatabaseManager.getTopScores(5);
                StringBuilder message = new StringBuilder("🏆 Top Scorers:\n");
                for (String entry : topScores) {
                    message.append(entry).append("\n");
                }

                JOptionPane.showMessageDialog(this, message.toString(), "Leaderboard", JOptionPane.INFORMATION_MESSAGE);

                ballposX = 120;
                ballposY = 350;
                ballXdir = -1;
                ballYdir = -2;
                score = 0;
                playerX = 310;
                totalbricks = 21;
                map = new MapGenerator(3, 7);

                repaint();
                play = true;
            }
        }
    }

    public void moveRight() {
        play = true;
        playerX += 20;
    }

    public void moveLeft() {
        play = true;
        playerX -= 20;
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}
